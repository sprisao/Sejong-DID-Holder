package com.example.did_holder_app.ui

import android.content.Context
import android.view.ViewGroup
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
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

@Composable
fun QRResultScreen(
    viewModel: DIDViewModel,
    navController: NavController, qrResult: String,
    context: Context,
) {
    val vpData = listOf("DID", "직위", "이름", "사번")
    val onItemChecked = { index: Int, isChecked: Boolean ->
        Timber.d("index: $index, isChecked: $isChecked")
    }
    var showDialog by remember { mutableStateOf(false) }

    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    if (isLoading) {
        LoadingScreen(message = "VP를 생성하고 전송중입니다.")
    }
    else if (showDialog) {
        ConfirmationDialog(
            onConfirm = {
                navController.popBackStack()
            },
            onDismiss = {
                showDialog = false
            }
        )
    }
    else {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier.padding(16.dp),
                elevation = CardDefaults.elevatedCardElevation(8.dp),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "VP를 생성하여 전송하시겠습니까?",
                        style = TextStyle(fontSize = 18.sp, letterSpacing = 0.sp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(15.dp))
                    LazyColumn(
                        modifier = Modifier.width(300.dp),
                        horizontalAlignment = Alignment.Start,
                        verticalArrangement = Arrangement.Center
                    ) {
                        items(vpData.size) { index ->
                            Row(
                                horizontalArrangement = Arrangement.Start,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(checked = true, onCheckedChange = {
                                    onItemChecked(index, it)
                                })
                                Text(vpData[index], style = TextStyle(fontSize = 18.sp))
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(15.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Button(
                            onClick = {
                                navController.popBackStack()
                            },
                            modifier = Modifier.width(120.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
                        ) {
                            Text("취소")
                        }
                        Button(onClick = {
                            isLoading = true
                            viewModel.generateVP(qrResult)
                            viewModel.verifyVP {
                                if (it.isSuccessful) {
                                    Timber.d("VP 검증 성공")
                                    if (it.body()?.code == 0) {
                                        isLoading = false
                                        showDialog = true
//                                        Toast.makeText(context, "VP 검증 성공", Toast.LENGTH_SHORT)
//                                            .show()
                                    } else {
                                        Toast.makeText(context, "VP 검증 실패", Toast.LENGTH_SHORT)
                                            .show()
                                    }
                                } else {
                                    Timber.d("VP 검증 실패")
                                    Toast.makeText(context, "VP 검증 실패", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }, modifier = Modifier.width(120.dp)) {
                            Text("확인")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ConfirmationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    var visible by remember { mutableStateOf(true) }

    if (visible) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .fillMaxHeight(0.3f)
                    .padding(bottom = 16.dp)
                    .animateContentSize(
                        animationSpec = tween(
                            durationMillis = 300,
                            easing = LinearOutSlowInEasing
                        )
                    ),
            ) {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .fillMaxWidth(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Spacer(modifier = Modifier.height(15.dp))
                    Text(
                        "✅",
                        style= TextStyle(fontSize = 20.sp),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        "VP 검증 성공",
                        style= TextStyle(fontSize = 22.sp),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(15.dp))
                    Button(
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .height(50.dp),
                        shape = RoundedCornerShape(10.dp),
                        onClick = {
                            onConfirm()
                            visible = false
                        }
                    ) {
                        Text("확인")
                    }
                }
            }
        }
    } else {
        onDismiss()
    }
}
