package com.example.did_holder_app.ui

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.OutlinedTextField
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.did_holder_app.data.api.RetrofitInstance.vcServerApi
import com.example.did_holder_app.data.model.DIDDocument.DidDocument
import com.example.did_holder_app.data.model.VC.SignInRequest
import com.example.did_holder_app.data.model.VC.SignInResponse
import com.example.did_holder_app.data.model.VC.VCRequest
import com.example.did_holder_app.data.model.VC.VCResponse
import com.example.did_holder_app.util.Constants
import com.example.did_holder_app.util.DidDataStore
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Response
import timber.log.Timber

val jsonAdapter: JsonAdapter<VCResponse> = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()
    .adapter(VCResponse::class.java)

@Composable
fun VCScreen(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    /*set vcText state*/

    val dataStore = DidDataStore(context)
    val myVCResponse = dataStore.vcResponseFlow.collectAsState(initial = VCResponse())

    var showVCString by remember {
        mutableStateOf(false)
    }
    val myVCString = jsonAdapter.toJson(myVCResponse.value)
    val myDidDocument = dataStore.didDocumentFlow.collectAsState(initial = DidDocument())

//    val myVCString = remember { mutableStateOf(VC(data = null, code = null, msg = "")) }

    /*get userSeqFlow*/
    val userSeq = dataStore.userseqFlow.collectAsState(0)
    Timber.d("userSeq: ${userSeq.value.toString()}")

    var userId: String by remember { mutableStateOf("androidTest") }
    var userPassword: String by remember { mutableStateOf("androidTest1") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        if ((userSeq.value != 0 && userSeq.value != null) && myVCResponse.value?.data == null) {
            /*회원가입=true, VC발급=false*/

            Button(onClick = {
                scope.launch {
                    getVC(dataStore, scope, userSeq.value!!, myDidDocument.value!!)
                }
            }) {
                Text(text = "VC생성", style = MaterialTheme.typography.labelSmall)
            }
            Button(
                onClick = {
                    scope.launch {
                        dataStore.clearUserseq()
                    }
                },
            ) {
                Text(text = "로그아웃", style = MaterialTheme.typography.labelSmall)
            }
        } else if (myVCResponse.value?.data != null) {
            /*회원가입=true, VC발급=true*/

            Button(onClick = {

            }) {
                Text(text = "VC삭제", style = MaterialTheme.typography.labelSmall)
            }
        } else {
            /*회원가입=false, VC발급=false*/

            /*TextField with us*/

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
                signInUser(signInRequest, dataStore, scope, context)
            }) {
                Text(text = "로그인", style = MaterialTheme.typography.labelSmall)
            }

            Text(text = "또는", style = MaterialTheme.typography.labelSmall)
            Button(onClick = {
                navController.navigate(Constants.SIGN_UP) {}
            }) {
                Text(text = "회원가입", style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}

fun getVC(dataStore: DidDataStore, scope: CoroutineScope, userSeq: Int, didDocument: DidDocument) {
    val vcRequest = VCRequest(
        userseq = userSeq,
        holderdid = didDocument.id,
    )

    val call = vcRequest.let { vcServerApi.getVC(it) }
    call.enqueue(object : retrofit2.Callback<VCResponse> {
        override fun onResponse(call: Call<VCResponse>, response: Response<VCResponse>) {
            if (response.isSuccessful) {
                /*set vcText state*/
                val vcJson = jsonAdapter.toJson(response.body())
                scope.launch { dataStore.saveVc(vcJson) }
                /*show modal*/
                Timber.d(response.body().toString())
                Timber.d("VC발급 성공")

            } else {
                println("Error")
            }
        }

        override fun onFailure(call: Call<VCResponse>, t: Throwable) {
            println(t.message)
        }
    })
}

private fun signInUser(
    request: SignInRequest,
    dataStore: DidDataStore,
    scope: CoroutineScope,
    context: Context
) {
    val call = vcServerApi.login(request)
    call.enqueue(object : retrofit2.Callback<SignInResponse> {
        override fun onResponse(
            call: Call<SignInResponse>, response: Response<SignInResponse>
        ) {
            if (response.isSuccessful) {
                Timber.d("Success")
                Timber.d(response.body().toString())
                /*get userseq from SignUpRequest */
                if (response.body()!!.code == 0) {
                    val userseq = response.body()?.data?.userSequence
                    scope.launch {
                        dataStore.saveUserseq(userseq!!)
                    }
                } else if (response.body()!!.code != 0) {
                    /*Show toast*/
                    Toast.makeText(context, response.body()!!.msg, Toast.LENGTH_SHORT).show()
                }
            } else {
                println("Error")
            }
        }

        override fun onFailure(call: Call<SignInResponse>, t: Throwable) {
            println(t.message)
        }

    })
}