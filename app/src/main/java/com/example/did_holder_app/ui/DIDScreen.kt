package com.example.did_holder_app.ui

import android.util.Base64
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.did_holder_app.util.AndroidKeyStoreUtil.generateAndSaveKey
import com.example.did_holder_app.util.AndroidKeyStoreUtil.loadAndDecryptKey
import timber.log.Timber
import java.security.*

@Composable
fun DIDScreen() {
    val didDoc = DidDocument()

    val publicKey = didDoc.publicKey.toString()
    val privateKey = didDoc.privateKey
    val encPrivateKey = didDoc.encryptedPrivateKey
    val decPrivateKey = didDoc.decryptedPrivateKey

    val did = didDoc.did

    Timber.d("Public Key: $publicKey")
    Timber.d("Private Key: $privateKey")
    Timber.d("Encrypted Private Key: $encPrivateKey")
    Timber.d("Decrypted Private Key: $decPrivateKey")
    Timber.d("DID: $did")

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

    /* Generated Asymmetric Keypair*/
    val publicKey: PublicKey = keyPair.public
    val privateKey: PrivateKey = keyPair.private

    /* Todo: 여기서 Keypair를 한번 더 Encode 하는 것이 필요한가?*/

    /* PrivateKey 암호화 및 Keystore에 저장*/
    val encryptedPrivateKey: ByteArray = generateAndSaveKey(privateKey.toString())

    /* 암호화하여 저장된 PrivateKey를 Keystore에서 가져옴*/
    val decryptedPrivateKey: String = loadAndDecryptKey(encryptedPrivateKey)


    // ------ DID Auth 부분 ------

    /* 개인키를 활용하여 Message에 사인*/
    fun signMessage(privateKey: PrivateKey, message: String): String {

        val signature = Signature.getInstance("SHA256withRSA")
        signature.initSign(privateKey)
        signature.update(message.toByteArray())
        return signature.sign().toString()
    }

    /* 개인키로 사인받은 Message를 공개키로 검증*/
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

    // ------ DID Auth 부분 ------


    /* 2. 생성한 공개키를 활용하여 DID 생성 */
    /* - publicKey를 Base64로 encoding하여 생성*/
    val did: String = generateDID(publicKey)


    /* pubKey를 Encoding 한다면 ByteArray로 받아야 하지만 그냥 전달하므로 PublicKey 객체를 인자로 받는다.*/
    private fun generateDID(pubKey: PublicKey): String {

        val message = pubKey.toString()
        val md = MessageDigest.getInstance("SHA-256")
        val genDid = md.digest(message.toByteArray())

        return "did:sjbr:${Base64.encodeToString(genDid, Base64.NO_WRAP)}"
    }

}
