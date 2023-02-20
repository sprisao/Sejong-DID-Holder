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
import com.example.did_holder_app.data.DIDRepositoryImpl
import com.example.did_holder_app.data.model.DIDDocument.DidDocument
import com.example.did_holder_app.data.datastore.DidDataStore
import com.example.did_holder_app.ui.viewmodel.DIDViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import timber.log.Timber

@Composable
fun DIDScreen() {

    val context = LocalContext.current
    val dataStore = DidDataStore(context)
    val viewModel = remember { DIDViewModel(DIDRepositoryImpl(dataStore)) }

    val scope = rememberCoroutineScope()
    val myDidDocument = dataStore.didDocumentFlow.collectAsState(initial = DidDocument())

    val state = if (myDidDocument.value?.id != null) {
        DIDState.Existing(didDocument = myDidDocument.value!!)
    } else {
        DIDState.None
    }

    DIDScreenState(viewModel, scope, dataStore, state)
}

@Composable
fun DIDScreenState(
    viewModel: DIDViewModel,
    scope: CoroutineScope,
    dataStore: DidDataStore,
    state: DIDState
) {
    when (state) {
        is DIDState.None -> EmptyDidScreen(viewModel, scope, dataStore)
        is DIDState.Existing -> WithDidScreen(viewModel, scope, dataStore, state.didDocument)
    }
}

sealed class DIDState {
    object None : DIDState()
    data class Existing(val didDocument: DidDocument) : DIDState()
}

@Composable
fun EmptyDidScreen(viewModel: DIDViewModel, scope: CoroutineScope, dataStore: DidDataStore) {
//    val didInit = DidInit(dataStore)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(
            onClick = {
//                scope.launch {
//                    didInit.generateDidDocument()
//                }
                viewModel.generateDidDocument()
            },
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = "DID 생성")
        }
    }
}


@Composable
fun WithDidScreen(viewModel:DIDViewModel, scope: CoroutineScope, dataStore: DidDataStore, didDocument: DidDocument) {
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
        Button(
            onClick = {
                scope.launch {
                    Timber.d(didDocument.toString())
                }
            },
        ) {
            Text(text = "Did Doc 생성")
        }
        Button(onClick = {
            viewModel.saveToBlockChain(didDocument)
        }) {
            Text(text = "블록체인에 저장")
        }
    }
}

