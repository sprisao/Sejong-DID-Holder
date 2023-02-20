package com.example.did_holder_app.data.api

import com.example.did_holder_app.data.model.Blockchain.BlockChainRequest
import com.example.did_holder_app.data.model.Blockchain.BlockchainResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface BlockchainApi {
    @POST("/v1/holder")
    fun save(@Body request: BlockChainRequest): Call<BlockchainResponse>
}