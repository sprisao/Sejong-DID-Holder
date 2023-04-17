package com.example.did_holder_app.ui

import android.content.Context
import android.view.ViewGroup
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.example.did_holder_app.R
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
        ScanQRCode(navController)
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
fun ScanQRCode(navController: NavController) {

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
    navController: NavController,
    qrResult: String,
    context: Context,
) {
    val vpData = listOf("나의 DID", "직위", "이름", "사번")

    val selectedCredential : List<String> = listOf("id", "name", "position", "type", "status")

    val onItemChecked = { index: Int, isChecked: Boolean ->
        Timber.d("index: $index, isChecked: $isChecked")
    }
    var showDialog by remember { mutableStateOf(false) }
    var dialogMessage by remember { mutableStateOf("") }
    var isSuccess by remember { mutableStateOf(false) }

    var isLoading by remember { mutableStateOf(false) }


    if (isLoading) {
        LoadingScreen(message = "VP를 생성하여 전송 중 입니다.")

    } else if (showDialog) {
        ConfirmationDialog(
            onConfirm = {
                navController.popBackStack()
            },
            isSucess = isSuccess,
            message = dialogMessage,
        )
    } else {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
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
                        "'세종텔레콤 출입시스템'에서 \n 다음과 같은 정보를 요청합니다 :",
                        style = TextStyle(fontSize = 18.sp, letterSpacing = 0.sp),
                        maxLines = 2,
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
                                Box(
                                    modifier = Modifier
                                        .padding(vertical = 0.dp)
                                        .width(40.dp)
                                        .height(40.dp),
                                ) {
                                    Checkbox(
                                        modifier = Modifier.fillMaxSize(),
                                        checked = true,
                                        onCheckedChange = {
                                            onItemChecked(index, it)
                                        })
                                }
                                Text(vpData[index], style = TextStyle(fontSize = 18.sp))
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(15.dp))
                    Text(
                        "위 정보를 제공하시겠습니까?",
                        style = TextStyle(fontSize = 18.sp, letterSpacing = 0.sp),
                        maxLines = 1,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(15.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Button(
                            shape =
                            RoundedCornerShape(20),
                            onClick = {
                                navController.popBackStack()
                            },
                            modifier = Modifier.width(125.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
                        ) {
                            Text("취소")
                        }
                        Button(
                            shape =
                            RoundedCornerShape(20),
                            colors = ButtonDefaults.buttonColors(Color.Red), onClick = {
                                isLoading = true
                                viewModel.generateVP(qrResult, selectedCredential)
                                viewModel.verifyVP {
                                    if (it.isSuccessful) {
                                        Timber.d("VP 검증 성공")
                                        if (it.body()?.code == 0) {
                                            if (it.body()!!.data.verifyResult) {
                                                isSuccess = true
                                                isLoading = false
                                                showDialog = true
                                            } else if (!it.body()!!.data.idResult) {
                                                isSuccess = false
                                                dialogMessage = "DID가 일치하지 않습니다."
                                                isLoading = false
                                                showDialog = true
                                            } else if (!it.body()!!.data.vcResult) {
                                                isSuccess = false
                                                dialogMessage = "인증서가 일치하지 않습니다."
                                                isLoading = false
                                                showDialog = true
                                            } else if (!it.body()!!.data.vpResult) {
                                                isSuccess = false
                                                dialogMessage = "VP가 일치하지 않습니다."
                                                isLoading = false
                                                showDialog = true
                                            } else if (!it.body()!!.data.authdateResult) {
                                                isSuccess = false
                                                dialogMessage = "최신 인증서가 아닙니다."
                                                isLoading = false
                                                showDialog = true
                                            } else if (!it.body()!!.data.challengeResult) {
                                                isSuccess = false
                                                dialogMessage = "상호 검증에 문제가 있습니다."
                                                isLoading = false
                                                showDialog = true
                                            } else if (!it.body()!!.data.vcStatusResult) {
                                                isSuccess = false
                                                dialogMessage = "인증서의 상태가 유효하지 않습니다."
                                                isLoading = false
                                                showDialog = true
                                            }
                                        } else {
                                            isSuccess = false
                                            isLoading = false
                                            showDialog = true
                                        }
                                    } else {
                                        isSuccess = false
                                        isLoading = false
                                        showDialog = true
                                    }
                                }
                            }, modifier = Modifier.width(125.dp)
                        ) {
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
    isSucess: Boolean,
    onConfirm: () -> Unit,
    message: String
) {
    if (isSucess) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .fillMaxHeight(0.6f)
                    .padding(bottom = 0.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
            ) {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 60.dp)
                        .fillMaxWidth(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.baseline_verified_user_24),
                        contentDescription = null,
                        modifier = Modifier
                            .size(50.dp)
                            .align(Alignment.CenterHorizontally)
                    )
                    Spacer(modifier = Modifier.height(15.dp))
                    Text(
                        "검증에 성공하였습니다.",
                        style = TextStyle(fontSize = 22.sp),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(45.dp))
                    Button(
                        modifier = Modifier
                            .fillMaxWidth(0.6f)
                            .height(40.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                        onClick = {
                            onConfirm()
                        }
                    ) {
                        Text("확인")
                    }
                }
            }
        }
    } else {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .fillMaxHeight(0.6f)
                    .padding(bottom = 0.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
            ) {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 60.dp)
                        .fillMaxWidth(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.baseline_warning_24),
                        contentDescription = null,
                        modifier = Modifier
                            .size(50.dp)
                            .align(Alignment.CenterHorizontally)
                    )
                    Spacer(modifier = Modifier.height(15.dp))
                    Text(
                        "검증에 실패하였습니다.",
                        style = TextStyle(fontSize = 22.sp),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        message,
                        style = TextStyle(fontSize = 18.sp),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(45.dp))
                    Button(
                        modifier = Modifier
                            .fillMaxWidth(0.6f)
                            .height(40.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                        onClick = {
                            onConfirm()
                        }
                    ) {
                        Text("확인")
                    }
                }
            }
        }
    }
}
