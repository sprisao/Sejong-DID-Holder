package com.example.did_holder_app.data.model.VP


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class VpProof(
    @Json(name = "challenge")
    val challenge: String,
    @Json(name = "created")
    val created: String,
    @Json(name = "domain")
    val domain: String,
    @Json(name = "proofPurpose")
    val proofPurpose: String,
    @Json(name = "proofValue")
    val proofValue: String,
    @Json(name = "type")
    val type: String,
)
