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
import com.example.did_holder_app.did.DidInit
import com.example.did_holder_app.ui.viewmodel.DIDViewModel
import com.example.did_holder_app.util.DidDataStore
import kotlinx.coroutines.launch

@Composable
fun DIDScreen(didViewModel: DIDViewModel) {
    val context = LocalContext.current
    val didInit = DidInit(context, didViewModel)

    val scope = rememberCoroutineScope()
    val dataStore = DidDataStore(context)


    val myDid = dataStore.getDid.collectAsState(initial = "")


    /*show did from datastore*/

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        if (myDid.value == "") {
            scope.launch {
                val did = didInit.generateDID(didInit.publicKey)
                dataStore.saveDid(did)
            }
            GenerateDIDButton(modifier = Modifier.padding(16.dp))
        }
        Text(text = myDid.value!!)
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

