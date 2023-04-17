package com.example.did_holder_app.data.model.VP


import com.example.did_holder_app.data.model.VC.CredentialSalt
import com.example.did_holder_app.data.model.VC.CredentialText
import com.example.did_holder_app.data.model.VC.VcData
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class VP(
    @Json(name = "@context")
    val context: String,
    @Json(name = "id")
    val id: String,
    @Json(name = "type")
    val type: List<String>,
    @Json(name = "verifiableCredential")
    val verifiableCredential: List<com.example.did_holder_app.data.model.VC.VerifiableCredential?>,
    @Json(name = "proof")
    val vpProof: VpProof,
    @Json(name = "credentialText")
    val credentialText: List<CredentialText>?,
    @Json(name = "credentialSalt")
    val credentialSalt: List<CredentialSalt>?
)