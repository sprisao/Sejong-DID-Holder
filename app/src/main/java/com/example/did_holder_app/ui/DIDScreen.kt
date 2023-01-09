package com.example.did_holder_app.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.did_holder_app.did.DidInit
import com.example.did_holder_app.ui.viewmodel.DIDViewModel
import timber.log.Timber

@Composable
fun DIDScreen(didViewModel: DIDViewModel) {
    val didInit = DidInit(didViewModel)

    val publicKey = didInit.publicKey
    val privateKey = didInit.privateKey

    Timber.d("publicKey: $publicKey")
    Timber.d("privateKey: $privateKey")

    val scope = rememberCoroutineScope()

    didViewModel.getDID()

    /*show did from datastore*/
    val did = didViewModel.getDID()
    val didText = remember { mutableStateOf(did) }

    Timber.d("didText: $didText")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        GenerateDIDButton(modifier = Modifier.padding(16.dp))
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

