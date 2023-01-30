package com.example.did_holder_app.ui

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.did_holder_app.data.api.RetrofitInstance
import com.example.did_holder_app.data.model.Blockchain.BlockchainHolder
import com.example.did_holder_app.data.model.DIDDocument.DidDocument
import com.example.did_holder_app.did.DidInit
import com.example.did_holder_app.util.DidDataStore
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.launch
import timber.log.Timber

@Composable
fun DIDScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val dataStore = DidDataStore(context)
    val moshi = com.squareup.moshi.Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()
    val jsonAdapter: JsonAdapter<DidDocument> = moshi.adapter(DidDocument::class.java)


    val didInit = DidInit(dataStore)

    val myDidDocument = dataStore.getDidDocument.collectAsState(initial = DidDocument())

    val myDidDocumentString = jsonAdapter.toJson(myDidDocument.value)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        if (myDidDocument.value?.id != null) {
            Text(
                myDidDocumentString,
                style = MaterialTheme.typography.bodySmall,
            )
        } else {
            Button(
                onClick = {
                    scope.launch {
                        didInit.generateDidDocument()
                    }
                },
                modifier = Modifier.padding(16.dp)
            ) {
                Text(text = "DID 생성")
            }
        }


        Button(
            onClick = {
                scope.launch {
                    dataStore.deleteDidDocument()
                }
            },
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = "DID 삭제")
        }
        Button(onClick = {
            scope.launch {
                val blockchainHolder = BlockchainHolder( myDidDocument.value!!.id ,myDidDocumentString.toString())
                val api = RetrofitInstance.blockchainApi
                try {
                    val response = api.postDidDocument(blockchainHolder)
                    if (response.isSuccessful) {
                        Timber.d("Success")
                    } else {
                        Timber.e(response.toString())
                        Timber.d("Fail")
                    }
                } catch (e: Exception) {
                    Timber.e(e)
                }
            }
            Toast.makeText(context, myDidDocument.value!!.id, Toast.LENGTH_SHORT).show()
        }) {
            Text(text = "블록체인에 저장")
        }
    }

}

