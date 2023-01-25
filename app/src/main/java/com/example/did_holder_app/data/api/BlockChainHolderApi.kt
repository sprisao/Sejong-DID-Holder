package com.example.did_holder_app.data.api

import com.example.did_holder_app.data.model.Blockchain.BlockchainHolder
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface BlockChainHolderApi {
    @POST("/v1/holder")
    suspend fun postDidDocument(@Body blockchainHolder: BlockchainHolder): Response<Unit>
}