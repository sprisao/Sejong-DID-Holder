package com.example.did_holder_app.data.model.Blockchain

import com.example.did_holder_app.data.model.VC.SignUpData
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

data class BlockChainRequest(val HolderDid: String, val Document: String)

@JsonClass(generateAdapter = true)
data class BlockchainResponse(
    @Json(name = "data")
    val data: SignUpData?,
    @Json(name = "code")
    val code: Int?,
    @Json(name = "msg")
    val msg: String?
)
