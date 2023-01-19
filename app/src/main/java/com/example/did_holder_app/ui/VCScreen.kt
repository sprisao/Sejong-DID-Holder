package com.example.did_holder_app.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.did_holder_app.data.model.VC.VC
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query


@Composable
fun VCScreen() {
    val scope = rememberCoroutineScope()
    /*set vcText state*/

    val vc = remember { mutableStateOf(VC(data = null, code = null, msg = "")) }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (vc.value.data != null) {
            for (credentialSubject in vc.value.data!!.credentialSubject) {

                Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.Start) {
                    Text(text = "이름: ${credentialSubject.name}", )
                    Text(text = "직급: ${credentialSubject.position}")
                    Text(text = "부서: ${credentialSubject.type}")
                }
            }
        } else {
            Button(onClick = { scope.launch { getVC(vc) } }) {
                Text(text = "사원증 발급 요청(VC)", style = MaterialTheme.typography.labelSmall)
            }
        }

    }
}

// Define the API Endpoint
val API_ENDPOINT = "http://192.168.4.85:8080"

// Create a Moshi instance for parsing JSON
val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

// Create a OkHttpClient instance for handling HTTP requests
val okHttpClient = OkHttpClient.Builder().build()

// Create a Retrofit instance for the API Endpoint
val retrofit = Retrofit.Builder()
    .baseUrl(API_ENDPOINT)
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .client(okHttpClient)
    .build()

// Define the interface for the API
interface ApiService {
    @GET("/v1/test")
    fun getVC(
        @Query("userid") userid: String,
        @Query("username") username: String
    ): Call<VC>
}

// Create an instance of the API inteface
val api = retrofit.create(ApiService::class.java)

private fun getVC(vcText: MutableState<VC>) {
    val call = api.getVC("tibob44", "한현수")
    call.enqueue(object : retrofit2.Callback<VC> {
        override fun onResponse(call: Call<VC>, response: Response<VC>) {
            if (response.isSuccessful) {
                val vc = response.body()
                /*set vcText state*/
                vcText.value = vc!!
                println(vcText.toString())
            } else {
                println("Error")
            }
        }

        override fun onFailure(call: Call<VC>, t: Throwable) {
            println(t.message)
        }
    })
}