package com.example.did_holder_app.data.model.VC


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Data(
    @Json(name = "@context")
    val context: List<String>,
    @Json(name = "credentialSubject")
    val credentialSubject: List<CredentialSubject>,
    @Json(name = "expirationDate")
    val expirationDate: String,
    @Json(name = "id")
    val id: String,
    @Json(name = "issuanceDate")
    val issuanceDate: String,
    @Json(name = "issuer")
    val issuer: String,
    @Json(name = "proof")
    val proof: Proof,
    @Json(name = "type")
    val type: List<String>
)