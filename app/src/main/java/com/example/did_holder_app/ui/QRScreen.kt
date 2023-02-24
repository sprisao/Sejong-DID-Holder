package com.example.did_holder_app.ui

import android.content.Context
import android.os.Build
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.example.did_holder_app.data.BarCodeAnalyser
import com.example.did_holder_app.ui.viewmodel.DIDViewModel
import com.example.did_holder_app.util.Constants.QR_RESULT_SCREEN_NAME
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.common.util.concurrent.ListenableFuture
import timber.log.Timber
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun QRScreen(viewModel: DIDViewModel, navController: NavController) {
    val cameraPermissionState =
        rememberPermissionState(android.Manifest.permission.CAMERA)

    if (!cameraPermissionState.status.isGranted) {
        CheckCameraPermission(cameraPermissionState)
    } else {
        ScanQRCode(viewModel, navController)
    }

}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CheckCameraPermission(cameraPermissionState: PermissionState) {
    Column(
        /*align items center of screen*/
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("QR코드 스캔을 위해 카메라 권한이 필요합니다.")
        Button(onClick = { cameraPermissionState.launchPermissionRequest() }) {
            Text("권한 설정")
        }
    }
}

@Composable
fun ScanQRCode(viewModel: ViewModel, navController: NavController) {

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var preview by remember { mutableStateOf<Preview?>(null) }
    val barCodeVal = remember { mutableStateOf("") }

    AndroidView(
        factory = { AndroidViewContext ->
            PreviewView(AndroidViewContext).apply {
                this.scaleType = PreviewView.ScaleType.FILL_CENTER
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                )
                implementationMode = PreviewView.ImplementationMode.COMPATIBLE
            }
        },
        modifier = Modifier
            .fillMaxSize(),
        update = { previewView ->
            val cameraSelector: CameraSelector = CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build()
            val cameraExecutor: ExecutorService = Executors.newSingleThreadExecutor()
            val cameraProviderFuture: ListenableFuture<ProcessCameraProvider> =
                ProcessCameraProvider.getInstance(context)

            cameraProviderFuture.addListener({
                preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }
                val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()


                val barcodeAnalyser = BarCodeAnalyser { barcodes ->
                    barcodes.forEach { barcode ->
                        barcode.rawValue?.let { barcodeValue ->
                            barCodeVal.value = barcodeValue
                            /*Navigate to qrresultscreen with barcodevalue*/
                            navController.navigate("$QR_RESULT_SCREEN_NAME/$barcodeValue")
                        }
                    }
                }
                val imageAnalysis: ImageAnalysis = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()
                    .also {
                        it.setAnalyzer(cameraExecutor, barcodeAnalyser)
                    }

                try {
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        cameraSelector,
                        preview,
                        imageAnalysis
                    )
                } catch (e: Exception) {
                    Timber.d("CameraPreview: ${e.localizedMessage}")
                }
            }, ContextCompat.getMainExecutor(context))
        }
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun QRResultScreen(
    viewModel: DIDViewModel,
    navController: NavController, qrResult: String,
    context: Context
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("QR코드 스캔 결과:\n$qrResult", modifier = Modifier.align(Alignment.CenterHorizontally))
        Button(onClick = {
            viewModel.generateVP(qrResult)
        }) {
            Text(text = "VP 생성")
        }
        Button(onClick = {
            viewModel.verifyVP {
                if (it.isSuccessful) {
                    Timber.d("VP 검증 성공")
                    if (it.body()?.code == 0) {
                        Toast.makeText(context, "VP 검증 성공", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "VP 검증 실패", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Timber.d("VP 검증 실패")
                    Toast.makeText(context, "VP 검증 실패", Toast.LENGTH_SHORT).show()
                }
            }
        }) {
            Text("신원증명(VP제출)")
        }
    }
}
