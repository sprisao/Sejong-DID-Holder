package com.example.did_holder_app.data

import android.content.Context
import com.example.did_holder_app.data.model.Blockchain.BlockchainResponse
import com.example.did_holder_app.data.model.DIDDocument.DidDocument
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

interface DIDRepository {
    suspend fun generateDidDocument()
    suspend fun saveToBlockChain(
        didDocument: DidDocument,
        callback: DIDRepositoryImpl.SaveToBlockChainCallback
    )
}


