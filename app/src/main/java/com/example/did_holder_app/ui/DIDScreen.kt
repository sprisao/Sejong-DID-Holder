package com.example.did_holder_app.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.did_holder_app.data.model.DidDocument
import com.example.did_holder_app.did.DidInit
import com.example.did_holder_app.util.DidDataStore
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.launch
import timber.log.Timber

@Composable
fun DIDScreen() {
    val context = LocalContext.current
    val moshi = com.squareup.moshi.Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()
    val jsonAdapter: JsonAdapter<DidDocument> = moshi.adapter(DidDocument::class.java)

    val didInit = DidInit()
    val scope = rememberCoroutineScope()
    val dataStore = DidDataStore(context)

    val myDidDocument = dataStore.getDidDocument.collectAsState(initial = DidDocument())
    val myDidDocumentString = jsonAdapter.toJson(myDidDocument.value)

    Timber.d(myDidDocumentString)
    /*show did from datastore*/

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            myDidDocumentString,
            style = MaterialTheme.typography.bodySmall,
        )

        Button(
            onClick = {
                scope.launch {
                    val newDidDocument = didInit.generateDidDocument()
                    val didDocumentJson = jsonAdapter.toJson(newDidDocument)
                    dataStore.saveDidDocument(didDocumentJson)
                }
            },
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = "DID 생성")
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
    }

}

@Composable
fun GenerateDIDButton(modifier: Modifier) {
    Button(
        onClick = { /*TODO*/ },
        modifier = Modifier
            .width(120.dp)
            .height(50.dp)
    ) {
        Text(text = "DID 생성", style = MaterialTheme.typography.bodyLarge)
    }
}

