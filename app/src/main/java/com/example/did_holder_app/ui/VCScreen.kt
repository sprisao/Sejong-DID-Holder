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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.did_holder_app.data.model.VC.VC
import com.example.did_holder_app.util.DidDataStore
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

val jsonAdapter : JsonAdapter<VC> = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()
    .adapter(VC::class.java)

@Composable
fun VCScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    /*set vcText state*/

    val dataStore = DidDataStore(context)
    val myVC = dataStore.getVC.collectAsState(initial = VC())
    val myVCString = jsonAdapter.toJson(myVC.value)

//    val myVCString = remember { mutableStateOf(VC(data = null, code = null, msg = "")) }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (myVC.value?.data != null) {
            for (i in myVC.value!!.data!!.credentialSubject) {

                Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.Start) {
                    Text(text = "이름: ${i.name}", )
                    Text(text = "직급: ${i.position}")
                    Text(text = "부서: ${i.type}")
                }
            }
        } else {
            Button(onClick = { scope.launch { getVC(dataStore, scope) } }) {
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

private fun getVC(dataStore: DidDataStore, scope: CoroutineScope) {
    val call = api.getVC("tibob44", "한현수")
    call.enqueue(object : retrofit2.Callback<VC> {
        override fun onResponse(call: Call<VC>, response: Response<VC>) {
            if (response.isSuccessful) {
                /*set vcText state*/
                val vcJson = jsonAdapter.toJson(response.body())
                scope.launch { dataStore.saveVc(vcJson) }
            } else {
                println("Error")
            }
        }

        override fun onFailure(call: Call<VC>, t: Throwable) {
            println(t.message)
        }
    })
}