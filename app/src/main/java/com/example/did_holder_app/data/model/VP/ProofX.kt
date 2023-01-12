package com.example.did_holder_app.data.model.VP


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ProofX(
    @Json(name = "created")
    val created: String,
    @Json(name = "proofPurpose")
    val proofPurpose: String,
    @Json(name = "proofValue")
    val proofValue: String,
    @Json(name = "type")
    val type: String,
    @Json(name = "verificationMethod")
    val verificationMethod: String
)
