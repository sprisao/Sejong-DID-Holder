package com.example.did_holder_app.data.model.VC


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class VcCredentialSubject(
    @Json(name = "type")
    val type: String,
    @Json(name = "id")
    val did: String,
    @Json(name = "position")
    val position: String,
    @Json(name = "name")
    val name: String,
//    @Json(name = "phoneno")
//    val phoneno: String,
    @Json(name = "status")
    val status: String,
//    @Json(name = "password")
//    val password: String
)