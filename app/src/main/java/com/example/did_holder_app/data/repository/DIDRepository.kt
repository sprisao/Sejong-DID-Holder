package com.example.did_holder_app.data.repository

import com.example.did_holder_app.data.model.Blockchain.BlockchainResponse
import com.example.did_holder_app.data.model.DIDDocument.DidDocument
import com.example.did_holder_app.data.model.VC.*
import org.bouncycastle.asn1.cmp.Challenge
import retrofit2.Response

interface DIDRepository {
    suspend fun generateDidDocument()
    suspend fun saveToBlockChain(
        didDocument: DidDocument,
        result: (Response<BlockchainResponse>) -> Unit,
    )
    suspend fun signUpUser(
        request: SignUpRequest,
        result: (Response<SignUpResponse>) -> Unit,
    )
    suspend fun signInUser(
        request: SignInRequest,
        result: (Response<SignInResponse>) -> Unit,
    )
    suspend fun requestVC(
        request: VCRequest,
        result: (Response<VcResponse>) -> Unit,
    )

    suspend fun generateVP(
        challenge: String,
    )
}


