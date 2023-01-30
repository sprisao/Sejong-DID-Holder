package com.example.did_holder_app.did

import android.util.Base64
import com.example.did_holder_app.data.model.DIDDocument.Authentication
import com.example.did_holder_app.data.model.DIDDocument.DidDocument
import com.example.did_holder_app.data.model.DIDDocument.PublicKey
import com.example.did_holder_app.data.model.DIDDocument.Service
import com.example.did_holder_app.util.AndroidKeyStoreUtil
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.MessageDigest
import java.security.Signature

class DidInit {
    companion object {
        private const val DID_METHOD = "sjbr"
        private const val ALGORITHM = "SHA256withRSA"
    }

    private val rsaKeyPair: KeyPair by lazy {
        KeyPairGenerator.getInstance("RSA").apply {
            initialize(2048)
        }.generateKeyPair()
    }

    private val encryptedPrivateKey: ByteArray by lazy {
        AndroidKeyStoreUtil.generateAndSaveKey(rsaKeyPair.private.toString())
    }

    fun generateDidDocument(): DidDocument {
        val publicKey = rsaKeyPair.public
        val message = publicKey.toString().toByteArray()

        val hashedPubKey = hashKey(message)
        val encodedPubKey = Base64.encodeToString(hashedPubKey, Base64.NO_WRAP)

        val didId = "did:$DID_METHOD:$encodedPubKey"

        return DidDocument(
            context = "https://www.w3.org/ns/did/v1",
            id = didId,
            publicKey = listOf(
                PublicKey(
                    controller = didId,
                    id = "$didId#keys-1",
                    publicKeyBase64 = encodedPubKey,
                    type = "RSAVerificationKey2023"
                )
            ),
            authentication = listOf(
                Authentication(
                    type = "RSASignatureAuthentication2023",
                    publicKey = "$didId#keys-1"
                )
            ),
            service = listOf(
                Service(
                    id = "$didId;indx",
                    type = "IndxService",
                    serviceEndpoint = "https://example.com/indx"
                )
            )
        )
    }

    fun signMessage(message: String): String {
        val signature = Signature.getInstance(ALGORITHM)
        signature.initSign(rsaKeyPair.private)
        signature.update(message.toByteArray())
        return Base64.encodeToString(signature.sign(), Base64.DEFAULT)
    }

    fun verifySignature(message: String, signature: String): Boolean {
        val sig = Signature.getInstance(ALGORITHM)
        sig.initVerify(rsaKeyPair.public)
        sig.update(message.toByteArray())
        return sig.verify(Base64.decode(signature, Base64.DEFAULT))
    }

    private fun hashKey(key: ByteArray): ByteArray {
        return MessageDigest.getInstance("SHA-256").digest(key)
    }

}
