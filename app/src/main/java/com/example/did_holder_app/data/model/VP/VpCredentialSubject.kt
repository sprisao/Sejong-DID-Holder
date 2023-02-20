package com.example.did_holder_app.data.model.VP


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class VpCredentialSubject(
    @Json(name = "employee")
    val employee: Employee,
    @Json(name = "id")
    val id: String
)
