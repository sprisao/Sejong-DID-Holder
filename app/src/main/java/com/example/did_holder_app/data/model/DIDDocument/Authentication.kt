package com.example.did_holder_app.data.model.DIDDocument


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Authentication(
    @Json(name = "publicKey")
    val publicKey: String,
    @Json(name = "type")
    val type: String
)
