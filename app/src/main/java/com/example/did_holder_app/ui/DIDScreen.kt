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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import timber.log.Timber

@Composable
fun DIDScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val dataStore = DidDataStore(context)
    val myDidDocument = dataStore.didDocumentFlow.collectAsState(initial = DidDocument())

    val state = if (myDidDocument.value?.id != null) {
        DIDState.Existing(didDocument = myDidDocument.value!!)
    } else {
        DIDState.None
    }

    DIDScreenState(scope, dataStore, state)
}

@Composable
fun DIDScreenState(scope: CoroutineScope, dataStore: DidDataStore, state: DIDState) {
    when (state) {
        is DIDState.None -> EmptyDidScreen(scope, dataStore)
        is DIDState.Existing -> WithDidScreen(scope, dataStore, state.didDocument)
    }
}

sealed class DIDState {
    object None : DIDState()
    data class Existing(val didDocument: DidDocument) : DIDState()
}

@Composable
fun EmptyDidScreen(scope: CoroutineScope, dataStore: DidDataStore) {
    val didInit = DidInit(dataStore)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
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
}


@Composable
fun WithDidScreen(scope: CoroutineScope, dataStore: DidDataStore, didDocument: DidDocument) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            didDocument.id,
            style = MaterialTheme.typography.bodySmall,
        )

        Button(
            onClick = {
                scope.launch {
                    dataStore.clearDidDocument()
                }
            },
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = "DID 삭제")
        }
        Button(onClick = {
            scope.launch {
                val blockchainHolder =
                    BlockchainHolder(didDocument.id, didDocument.toString())

                val api = RetrofitInstance.blockchainApi
                try {
                    val response = api.postDidDocument(blockchainHolder)
                    if (response.isSuccessful) {
                        Timber.d("Saved to blockchain")
                        Toast.makeText(context, "저장 성공", Toast.LENGTH_SHORT).show()
                    } else {
                        Timber.e(response.toString())
                        Toast.makeText(context, "저장 실패", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show()
                }
            }
        }) {
            Text(text = "블록체인에 저장")
        }
    }
}
