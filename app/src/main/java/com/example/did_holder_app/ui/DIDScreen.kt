package com.example.did_holder_app.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun DIDScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        GenerateDIDButton(modifier = Modifier.padding(16.dp))
        Text(text = "DID 생성", style = MaterialTheme.typography.bodyLarge)
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

