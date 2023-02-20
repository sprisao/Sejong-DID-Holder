package com.example.did_holder_app.data.model.VP


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class VerifiableCredential(
    @Json(name = "@context")
    val context: List<String>,
    @Json(name = "credentialSubject")
    val vpCredentialSubject: VpCredentialSubject,
    @Json(name = "expirationDate")
    val expirationDate: String,
    @Json(name = "id")
    val id: String,
    @Json(name = "issuanceDate")
    val issuanceDate: String,
    @Json(name = "issuer")
    val issuer: String,
    @Json(name = "proof")
    val vpProof: VpProof,
    @Json(name = "type")
    val type: List<String>
)
