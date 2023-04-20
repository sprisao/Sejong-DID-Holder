package com.example.did_holder_app.data.model.DIDDocument


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DIDPublicKey(
    @Json(name = "id")
    val id: String,
    @Json(name = "type")
    val type: String,
    @Json(name = "controller")
    val controller: String,
    @Json(name = "publicKeyBase58")
    val publicKeyBase58: String,
)
