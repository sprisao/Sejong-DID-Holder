package com.example.did_holder_app.data.model


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DidDocument(
    @Json(name = "authentication")
    val authentication: List<Authentication>,
    @Json(name = "@context")
    val context: String,
    @Json(name = "id")
    val id: String,
    @Json(name = "publicKey")
    val publicKey: List<PublicKey>,
    @Json(name = "service")
    val service: List<Service>
)
