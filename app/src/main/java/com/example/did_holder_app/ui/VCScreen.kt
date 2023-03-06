package com.example.did_holder_app.ui

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.OutlinedTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.did_holder_app.data.model.DIDDocument.DidDocument
import com.example.did_holder_app.data.model.VC.SignInRequest
import com.example.did_holder_app.data.model.VC.VCRequest
import com.example.did_holder_app.data.model.VC.VcResponseData
import com.example.did_holder_app.ui.viewmodel.DIDViewModel
import com.example.did_holder_app.util.Constants
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.launch

@Composable
fun VCScreen(navController: NavController, viewModel: DIDViewModel) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val savedVC = viewModel.vc.collectAsState(initial = VcResponseData())
    val savedDidDocument = viewModel.didDocument.collectAsState(initial = DidDocument())
//    val savedUserSeq = viewModel.userSeq.collectAsState(initial = 0)


    var isLoading by remember { mutableStateOf(false) }

    val moshi: Moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    val jsonAdapter: JsonAdapter<VcResponseData> = moshi.adapter(VcResponseData::class.java)
    val vcResponseData = jsonAdapter.toJson(savedVC.value)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        when {
//            (savedUserSeq.value != 0 && savedUserSeq.value != null) && savedVC.value == null -> {
            savedVC.value == null -> {
                if (isLoading) {
                    CircularProgressIndicator()
                    Box(modifier = Modifier.padding(8.dp))
                    Text(text = "생성중...", style = MaterialTheme.typography.labelSmall)
                } else {
                    Button(
                        modifier = Modifier.fillMaxWidth(1f),
                        colors = ButtonDefaults.buttonColors(
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(10.dp),
                        onClick = {
                            isLoading = true
                            viewModel.requestVC(
                                VCRequest(
                                    //todo login 화면 없으므로 userSeq 하드코딩
                                    39,
                                    savedDidDocument.value?.id
                                )
                            ) {
                                isLoading = false
                                if (it.isSuccessful) {
                                    if (it.body()?.code == 0) {
                                        Toast.makeText(context, "VC 요청완료", Toast.LENGTH_SHORT)
                                            .show()
                                    } else {
                                        Toast.makeText(
                                            context,
                                            "실패 : ${it.body()?.msg}",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                } else {
                                    Toast.makeText(
                                        context,
                                        "실패 : ${it.message()}",
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()
                                }
                            }
                        },
                        enabled = !isLoading
                    )
                    {
                        Text(text = "VC요청", style = MaterialTheme.typography.labelSmall)
                    }
                }
//                Button(onClick = {
//                    scope.launch {
//                        viewModel.clearUserSeq()
//                    }
//                }) {
//                    Text(text = "로그아웃", style = MaterialTheme.typography.labelSmall)
//                }
            }
            savedVC.value != null -> {
                Text(vcResponseData.toString(), style = MaterialTheme.typography.labelSmall)
                Button(onClick = {
                    scope.launch {
                        viewModel.clearVc()
                    }
                }) {
                    Text(text = "VC삭제", style = MaterialTheme.typography.labelSmall)
                }
            }

            else -> {
                var userId by remember { mutableStateOf("androidTest") }
                var userPassword by remember { mutableStateOf("androidTest1") }
                OutlinedTextField(
                    value = userId,
                    onValueChange = { userId = it },
                    label = { Text("User ID") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = userPassword,
                    onValueChange = { userPassword = it },
                    label = { Text("Password") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation()
                )
                Button(onClick = {
                    val signInRequest = SignInRequest(userId, userPassword)
                    viewModel.signInUser(signInRequest) {
                        if (it.isSuccessful) {
                            if (it.body()?.code == 0) {
                                Toast.makeText(context, "로그인 성공", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(
                                    context,
                                    "실패 : ${it.body()?.msg}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            Toast.makeText(context, "실패 : ${it.message()}", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                }) {
                    Text(text = "로그인", style = MaterialTheme.typography.labelSmall)
                }
                Text(text = "또는", style = MaterialTheme.typography.labelSmall)
                Button(onClick = {
                    navController.navigate(Constants.SIGN_UP_SCREEN_NAME)
                }) {
                    Text(text = "회원가입", style = MaterialTheme.typography.labelSmall)
                }
            }
        }
    }
}
