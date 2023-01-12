package com.example.did_holder_app.data.model.DIDDocument


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PublicKey(
    @Json(name = "controller")
    val controller: String,
    @Json(name = "id")
    val id: String,
    @Json(name = "publicKeyBase64")
    val publicKeyBase64: String,
    @Json(name = "type")
    val type: String
)
