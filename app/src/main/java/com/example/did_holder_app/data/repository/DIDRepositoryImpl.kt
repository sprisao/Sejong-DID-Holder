package com.example.did_holder_app.data.repository

import android.os.Build
import android.util.Base64
import androidx.annotation.RequiresApi
import com.example.did_holder_app.data.api.RetrofitInstance.blockchainApi
import com.example.did_holder_app.data.api.RetrofitInstance.vcServerApi
import com.example.did_holder_app.data.api.RetrofitInstance.vpServerApi
import com.example.did_holder_app.data.api.VpRequest
import com.example.did_holder_app.data.api.VpResponse
import com.example.did_holder_app.data.datastore.DidDataStore
import com.example.did_holder_app.data.keystore.AndroidKeyStoreUtil.generateAndStoreEd25519KeyPair
import com.example.did_holder_app.data.model.Blockchain.BlockChainRequest
import com.example.did_holder_app.data.model.Blockchain.BlockchainResponse
import com.example.did_holder_app.data.model.DIDDocument.Authentication
import com.example.did_holder_app.data.model.DIDDocument.DidDocument
import com.example.did_holder_app.data.model.DIDDocument.PublicKey
import com.example.did_holder_app.data.model.VC.*
import com.example.did_holder_app.data.model.VP.VP
import com.example.did_holder_app.data.model.VP.VpProof
import com.example.did_holder_app.util.Constants.DID_DOCUMENT_CONTEXT
import com.example.did_holder_app.util.Constants.DID_DOCUMENT_METHODE
import com.example.did_holder_app.util.Constants.DID_DOCUMENT_PUBLIC_KEY_TYPE
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import org.bitcoinj.core.Base58
import org.bouncycastle.crypto.digests.SHA512Digest
import org.bouncycastle.crypto.params.Ed25519PrivateKeyParameters
import org.bouncycastle.crypto.params.Ed25519PublicKeyParameters
import org.bouncycastle.crypto.signers.Ed25519Signer
import org.bouncycastle.jcajce.interfaces.EdDSAPrivateKey
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.math.ec.rfc8032.Ed25519
import org.bouncycastle.util.encoders.Hex
import retrofit2.Response
import retrofit2.awaitResponse
import timber.log.Timber
import java.security.MessageDigest
import java.security.Security
import java.security.Signature
import java.security.interfaces.EdECKey
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class DIDRepositoryImpl(private val dataStore: DidDataStore) : DIDRepository {

    val moshi: Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val didDocJsonAdapter: JsonAdapter<DidDocument> = moshi.adapter(DidDocument::class.java)
    private val vcResponseDataJsonAdapter: JsonAdapter<VcResponseData> =
        moshi.adapter(VcResponseData::class.java)
    private val vpJsonAdapter: JsonAdapter<VP> = moshi.adapter(VP::class.java)

    val vc: Flow<VcResponseData?> = dataStore.vcFlow

    private fun hashKey(key: ByteArray): ByteArray {
        return MessageDigest.getInstance("SHA-256").digest(key)
    }

    // DID Document 생성
    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun generateDidDocument() {


        val keyPair = generateAndStoreEd25519KeyPair()

        val privateKeyByte = keyPair.first.encoded
        val privateKeyBase64 = Base64.encodeToString(privateKeyByte, Base64.DEFAULT)

        Timber.d(privateKeyBase64)

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
                    id = didId,
                    type = DID_DOCUMENT_PUBLIC_KEY_TYPE,
                    controller = didId,
                    publicKeyBase58 = publicKeyBase58,
                )
            ),
            authentication = listOf(
                Authentication(
//                    type = DID_DOCUMENT_AUTHENTICATION_TYPE,
                    publicKey = didId
                )
            ),
//            service = listOf(
//                Service(
//                    id = "$didId;indx",
//                    type = DID_DOCUMENT_SERVICE_TYPE,
//                    serviceEndpoint = DID_DOCUMENT_SERVICE_ENDPOINT,
//                )
//            )
        )

        coroutineScope {
            launch {
                val didDocumentJson = didDocJsonAdapter.toJson(didDocument)
                dataStore.saveDidDocument(didDocumentJson)
                dataStore.savePrivateKey(privateKeyBase64)
//                dataStore.savePublicKey(publicKeyBase58)
            }
        }
    }


    // 블록체인에 저장
    override suspend fun saveToBlockChain(
        didDocument: DidDocument,
        result: (Response<BlockchainResponse>) -> Unit,
    ) {
        val didDocumentJson = didDocJsonAdapter.toJson(didDocument)
        try {
            val call = blockchainApi.save(
                BlockChainRequest(
                    didDocument.id,
                    didDocumentJson.toString()
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
                            val vcResponseData =
                                vcResponseDataJsonAdapter.toJson(it?.vcResponseData)
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


    // todo 서명하기 위한 vp model 생성
    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun generateVP(challenge: String) {
        val vc = dataStore.vcFlow.first()
        val didDocument = dataStore.didDocumentFlow.first()
        val did = didDocument!!.id
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")
        val now = Instant.now().atZone(ZoneId.of("UTC")).format(formatter)

        // proofvalue가 없는 vp 생성
        var myVP = VP(
            context = "https://www.w3.org/2018/credentials/v1",
            id = did,
            type = listOf("VerifiablePresentation", "SejongAccessPresentation"),
            verifiableCredential = listOf(vc),
            vpProof = VpProof(
                type = "Ed25519Signature2018",
                creator = did.toString(),
                created = now.toString(),
                proofPurpose = "authentication",
                challenge = challenge,
                proofValue = null
            )
        )

        // ProofValue를 제외한 VP를 Json으로 변환
        val vpWithoutProofValue = vpJsonAdapter.toJson(myVP)

        // PrivateKey를 가져옴
        val privateKey = dataStore.privateKeyFlow.first()
        Timber.d("privateKey : ${privateKey}")

        // PrivateKey를 Base64로 디코딩
        val privateKeyByte = Base64.decode(privateKey, Base64.DEFAULT)
        Timber.d("privateKeyByte : ${Hex.toHexString(privateKeyByte)}")

        // PrivateKey를 Ed25519PrivateKeyParameters로 변환
        val acturalPrivateKey = Ed25519PrivateKeyParameters(privateKeyByte, 0)

        // PrivateKey로 데이터 서명
        val signer = Ed25519Signer()
        signer.init(true, acturalPrivateKey)

        // Json데이터를 String으로 변환후 ByteArray로 변환 -> Hashing
        val hashedVpWithoutProofValue = hashKey(vpWithoutProofValue.toString().toByteArray())

        signer.update(
            hashedVpWithoutProofValue,
            0,
            /* Size : String의 ByteArray값의 사이즈로 책정*/
            hashedVpWithoutProofValue.size
        )

        // 서명
        val signature = signer.generateSignature()

        // 서명을 Base64로 인코딩
        val signatureBase64 = Base64.encodeToString(signature, Base64.NO_WRAP or Base64.NO_PADDING)

        // todo: 서명 된 vp 생성
        myVP.vpProof.proofValue = signatureBase64

        val myVPtoJSon2 = vpJsonAdapter.toJson(myVP)
        Timber.d(myVPtoJSon2)
        dataStore.saveVp(myVPtoJSon2.toString())
    }

    override suspend fun verifyVP(result: (Response<VpResponse>) -> Unit) {

        val thisVp = dataStore.vpFlow.first()
        /*convert thisVP to Json*/
        val didDocument = dataStore.didDocumentFlow.first()

        val did = didDocument?.id

        try {
            val call = vpServerApi.sendVP(
                VpRequest(
                    did.toString(),
                    thisVp.toString()
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
            result (Response.error(500, ResponseBody.create(null, "error")))
        }
    }

}
