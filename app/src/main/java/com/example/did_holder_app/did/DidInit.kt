package com.example.did_holder_app.did

import android.util.Base64
import androidx.datastore.preferences.core.Preferences
import com.example.did_holder_app.data.model.DIDDocument.Authentication
import com.example.did_holder_app.data.model.DIDDocument.DidDocument
import com.example.did_holder_app.data.model.DIDDocument.PublicKey
import com.example.did_holder_app.data.model.DIDDocument.Service
import com.example.did_holder_app.util.AndroidKeyStoreUtil
import com.example.did_holder_app.util.DidDataStore
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.MessageDigest

class DidInit(private val dataStore: DidDataStore<Preferences>) {

    val moshi: Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val jsonAdapter: JsonAdapter<DidDocument> = moshi.adapter(DidDocument::class.java)

    companion object {
        private const val DID_METHOD = "sjbr"
        private const val ALGORITHM = "SHA256withRSA"
    }

    private val rsaKeyPair: KeyPair by lazy {
        KeyPairGenerator.getInstance("RSA").apply {
            initialize(2048)
        }.generateKeyPair()
    }

    suspend fun generateDidDocument() {
        val publicKey = rsaKeyPair.public
        val privateKey = rsaKeyPair.private
        val message = publicKey.toString().toByteArray()

        val hashedPubKey = hashKey(message)
        val encodedPubKey = Base64.encodeToString(hashedPubKey, Base64.NO_WRAP)

        val didId = "did:$DID_METHOD:$encodedPubKey"

        val didDocument = DidDocument(
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

        coroutineScope {
            launch {
                val didDocumentJson = jsonAdapter.toJson(didDocument)
                dataStore.saveDidDocument(didDocumentJson)
                AndroidKeyStoreUtil.generateAndSaveKey(privateKey.toString())
            }
        }
    }


    private fun hashKey(key: ByteArray): ByteArray {
        return MessageDigest.getInstance("SHA-256").digest(key)
    }

}
