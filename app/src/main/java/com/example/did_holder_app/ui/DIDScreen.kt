package com.example.did_holder_app.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import timber.log.Timber
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.PrivateKey
import java.security.PublicKey
import java.security.Signature

@Composable
fun DIDScreen() {
    val didDoc = DidDocument()

    val publicKey = didDoc.publicKey
    val privateKey = didDoc.privateKey

    Timber.d("Public Key: $publicKey")
    Timber.d("Private Key: $privateKey")

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


class DidDocument {
    /* 1. Generate Asymmetric Key */
    val keyPair: KeyPair = KeyPairGenerator.getInstance("RSA").apply {
        initialize(2048)
    }.generateKeyPair()

    /*public key*/

    val publicKey: PublicKey = keyPair.public

    val privateKey: PrivateKey = keyPair.private

    fun signMessage(privateKey: PrivateKey, message: String): String {
        val signature = Signature.getInstance("SHA256withRSA")
        signature.initSign(privateKey)
        signature.update(message.toByteArray())
        return signature.sign().toString()
    }

    fun verifySignature(
        publicKey: PublicKey,
        message: String,
        signature: ByteArray
    ): Boolean {
        val sig = Signature.getInstance("SHA256withRSA")
        sig.initVerify(publicKey)
        sig.update(message.toByteArray())
        return sig.verify(signature)
    }

}
