package com.example.did_holder_app.data.repository

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.did_holder_app.data.api.RetrofitInstance.blockchainApi
import com.example.did_holder_app.data.api.RetrofitInstance.vcServerApi
import com.example.did_holder_app.data.datastore.DidDataStore
import com.example.did_holder_app.data.keystore.AndroidKeyStoreUtil.generateAndStoreEd25519KeyPair
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
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.bitcoinj.core.Base58
import retrofit2.Response
import retrofit2.awaitResponse
import java.security.MessageDigest

class DIDRepositoryImpl(private val dataStore: DidDataStore) : DIDRepository {

    val moshi: Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val didDocJsonAdapter: JsonAdapter<DidDocument> = moshi.adapter(DidDocument::class.java)
    private val vcResponseDataJsonAdapter: JsonAdapter<VcResponseData> = moshi.adapter(VcResponseData::class.java)

    private fun hashKey(key: ByteArray): ByteArray {
        return MessageDigest.getInstance("SHA-256").digest(key)
    }

    // DID Document 생성
    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun generateDidDocument() {


        val keyPair = generateAndStoreEd25519KeyPair()

        val publicKeyByte = keyPair.second.encoded
        val hashedPubKey = hashKey(publicKeyByte)

        val publicKeyBase58 = Base58.encode(publicKeyByte)

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
                val didDocumentJson = didDocJsonAdapter.toJson(didDocument)
                dataStore.saveDidDocument(didDocumentJson)
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
                    try {
                        response.body().let {
                            val vcResponseData = vcResponseDataJsonAdapter.toJson(it?.vcResponseData)
                            vcResponseData?.let { data ->
                                dataStore.saveVc(data)
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
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
