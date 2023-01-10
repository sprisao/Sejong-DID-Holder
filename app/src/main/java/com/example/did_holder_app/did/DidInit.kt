package com.example.did_holder_app.did

import android.content.Context
import android.util.Base64
import com.example.did_holder_app.util.AndroidKeyStoreUtil
import com.example.did_holder_app.util.DidDataStore
import java.security.*

class DidInit(context: Context) {

    private val didDataStore = DidDataStore(context)

    suspend fun generateDID(): String {

        /* Generate Asymmetric Keypair*/
        val keyPair: KeyPair = KeyPairGenerator.getInstance("RSA").apply {
            initialize(2048)
        }.generateKeyPair()

        val publicKey: PublicKey = keyPair.public
        val privateKey: PrivateKey = keyPair.private

        /* save original public Key in datastore*/
        didDataStore.savePublicKey(publicKey.toString())

        /* encrypt and save private Key in Keystore*/
        val encryptedPrivateKey: ByteArray =
            AndroidKeyStoreUtil.generateAndSaveKey(privateKey.toString())

//        val decryptedPrivateKey: String =
//            AndroidKeyStoreUtil.loadAndDecryptKey(encryptedPrivateKey)

        val message = publicKey.toString()
        val md = MessageDigest.getInstance("SHA-256")
        val encryptedPubKey = md.digest(message.toByteArray())

        // DID Url 부분
        val encodedPubKey = Base64.encodeToString(encryptedPubKey, Base64.NO_WRAP)

        return "did:sjbr:${encodedPubKey}"
    }

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
}
