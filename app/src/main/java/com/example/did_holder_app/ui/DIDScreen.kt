package com.example.did_holder_app.ui

import android.widget.Toast
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
import com.example.did_holder_app.data.model.DIDDocument.DidDocument
import com.example.did_holder_app.ui.viewmodel.DIDViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import timber.log.Timber

@Composable
fun DIDScreen(viewModel: DIDViewModel) {
    val scope = rememberCoroutineScope()
    val didDocumentState = viewModel.didDocument.collectAsState(initial = DidDocument())
    val state = if (didDocumentState.value?.id != null) {
        DIDState.Existing(didDocument = didDocumentState.value!!)
    } else {
        DIDState.None
    }

    DIDScreenState(viewModel, scope, state)
}

@Composable
fun DIDScreenState(
    viewModel: DIDViewModel,
    scope: CoroutineScope,
    state: DIDState
) {
    when (state) {
        is DIDState.None -> EmptyDidScreen(viewModel)
        is DIDState.Existing -> WithDidScreen(viewModel, scope, state.didDocument)
    }
}

sealed class DIDState {
    object None : DIDState()
    data class Existing(val didDocument: DidDocument) : DIDState()
}

@Composable
fun EmptyDidScreen(viewModel: DIDViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(
            onClick = {
                viewModel.generateDidDocument()
            },
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = "DID 생성")
        }
    }
}


@Composable
fun WithDidScreen(
    viewModel: DIDViewModel,
    scope: CoroutineScope,
    didDocument: DidDocument
) {
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
                    viewModel.clearDidDocument()
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
            viewModel.saveDidDocumentToBlockchain(didDocument) {
                if (it.isSuccessful) {
                    if (it.body()?.code == 0) {
                        Toast.makeText(context, "블록체인에 저장되었습니다.", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "실패 : ${it.body()?.msg}", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, "실패 : ${it.body()?.msg}", Toast.LENGTH_SHORT).show()
                }
            }
        }) {
            Text(text = "블록체인에 저장")
        }
    }
}