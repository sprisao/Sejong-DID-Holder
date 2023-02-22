package com.example.did_holder_app.data.repository

import com.example.did_holder_app.data.api.RetrofitInstance.blockchainApi
import com.example.did_holder_app.data.api.RetrofitInstance.vcServerApi
import com.example.did_holder_app.data.datastore.DidDataStore
import com.example.did_holder_app.data.keystore.AndroidKeyStoreUtil
import com.example.did_holder_app.data.model.Blockchain.BlockChainRequest
import com.example.did_holder_app.data.model.Blockchain.BlockchainResponse
import com.example.did_holder_app.data.model.DIDDocument.Authentication
import com.example.did_holder_app.data.model.DIDDocument.DidDocument
import com.example.did_holder_app.data.model.DIDDocument.PublicKey
import com.example.did_holder_app.data.model.DIDDocument.Service
import com.example.did_holder_app.data.model.VC.*
import com.example.did_holder_app.util.Constants.DID_DOCUMENT_AUTHENTICATION_TYPE
import com.example.did_holder_app.util.Constants.DID_DOCUMENT_CONTEXT
import com.example.did_holder_app.util.Constants.DID_DOCUMENT_METHODE
import com.example.did_holder_app.util.Constants.DID_DOCUMENT_PUBLIC_KEY_TYPE
import com.example.did_holder_app.util.Constants.DID_DOCUMENT_SERVICE_ENDPOINT
import com.example.did_holder_app.util.Constants.DID_DOCUMENT_SERVICE_TYPE
import com.example.did_holder_app.util.Constants.KEYPAIR_ALGORITHM
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.bitcoinj.core.Base58
import org.bouncycastle.crypto.generators.Ed25519KeyPairGenerator
import org.bouncycastle.crypto.params.Ed25519KeyGenerationParameters
import org.bouncycastle.crypto.params.Ed25519PrivateKeyParameters
import org.bouncycastle.crypto.params.Ed25519PublicKeyParameters
import retrofit2.Response
import retrofit2.awaitResponse
import timber.log.Timber
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.MessageDigest
import java.security.SecureRandom

class DIDRepositoryImpl(private val dataStore: DidDataStore) : DIDRepository {

    val moshi: Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val jsonAdapter: JsonAdapter<DidDocument> = moshi.adapter(DidDocument::class.java)

//    private val rsaKeyPair: KeyPair by lazy {
//        KeyPairGenerator.getInstance(KEYPAIR_ALGORITHM).apply {
//            initialize(2048)
//        }.generateKeyPair()
//    }

    private fun hashKey(key: ByteArray): ByteArray {
        return MessageDigest.getInstance("SHA-256").digest(key)
    }

    // DID Document 생성
    override suspend fun generateDidDocument() {
//        val publicKey = rsaKeyPair.public
//        val privateKey = rsaKeyPair.private

        val random = SecureRandom()

        val keyPairGenerator = Ed25519KeyPairGenerator()
        keyPairGenerator.init(Ed25519KeyGenerationParameters(random))
        val keyPair = keyPairGenerator.generateKeyPair()
        val publicKeyParams: Ed25519PublicKeyParameters =
            keyPair.public as Ed25519PublicKeyParameters
        val privateKeyParams: Ed25519PrivateKeyParameters =
            keyPair.private as Ed25519PrivateKeyParameters

        val publicKeyByte = publicKeyParams.encoded
        val privateKeyByte = privateKeyParams.encoded

        Timber.d(publicKeyByte.toString())
        Timber.d(privateKeyByte.toString())
        val publicKeyBase58 = Base58.encode(publicKeyByte)

        val hashedPubKey = hashKey(publicKeyByte)

        val did = Base58.encode(hashedPubKey)

        val didId = "did:$DID_DOCUMENT_METHODE:$did"

        val didDocument = DidDocument(
            context = DID_DOCUMENT_CONTEXT,
            id = didId,
            publicKey = listOf(
                PublicKey(
                    controller = didId,
                    id = "$didId#keys-1",
                    publicKeyBase58 = publicKeyBase58,
                    type = DID_DOCUMENT_PUBLIC_KEY_TYPE
                )
            ),
            authentication = listOf(
                Authentication(
                    type = DID_DOCUMENT_AUTHENTICATION_TYPE,
                    publicKey = "$didId#keys-1"
                )
            ),
            service = listOf(
                Service(
                    id = "$didId;indx",
                    type = DID_DOCUMENT_SERVICE_TYPE,
                    serviceEndpoint = DID_DOCUMENT_SERVICE_ENDPOINT,
                )
            )
        )

        coroutineScope {
            launch {
                val didDocumentJson = jsonAdapter.toJson(didDocument)
                dataStore.saveDidDocument(didDocumentJson)
                AndroidKeyStoreUtil.generateAndSaveKey(privateKeyByte.toString())
            }
        }
    }


    // 블록체인에 저장
    override suspend fun saveToBlockChain(
        didDocument: DidDocument,
        result: (Response<BlockchainResponse>) -> Unit,
    ) {
        try {
            val call = blockchainApi.save(
                BlockChainRequest(
                    didDocument.id,
                    didDocument.toString()
                )
            )
            val response = call.awaitResponse()
            if (response.isSuccessful) {
                result(response)
            } else {
                result(response)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // 회원가입
    override suspend fun signUpUser(
        request: SignUpRequest,
        result: (Response<SignUpResponse>) -> Unit
    ) {
        try {
            val call = vcServerApi.signUp(request)
            val response = call.awaitResponse()
            if (response.isSuccessful) {
                if (response.body()?.code == 0) {
                    // DataStore에 userseq 저장
                    response.body()?.data?.userseq?.let { dataStore.saveUserSeq(it) }
                }
                result(response)
            } else {
                result(response)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // VC요청
    override suspend fun requestVC(request: VCRequest, result: (Response<VcResponse>) -> Unit) {
        try {
            val call = vcServerApi.requestVC(request)
            val response = call.awaitResponse()
            if (response.isSuccessful) {
                if (response.body()?.code == 0) {
                    // DataStore에 VC 저장
                    response.body()?.vcResponseData.let { dataStore.saveVc(it.toString()) }
                }
                result(response)
            } else {
                result(response)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    // 로그인
    override suspend fun signInUser(
        request: SignInRequest,
        result: (Response<SignInResponse>) -> Unit
    ) {
        try {
            val call = vcServerApi.signIn(request)
            val response = call.awaitResponse()
            if (response.isSuccessful) {
                if (response.body()?.code == 0) {
                    // DataStore에 userseq 저장
                    response.body()?.data?.userSequence.let {
                        if (it != null) {
                            dataStore.saveUserSeq(it)
                        }
                    }
                }
                result(response)
            } else {
                result(response)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


}
