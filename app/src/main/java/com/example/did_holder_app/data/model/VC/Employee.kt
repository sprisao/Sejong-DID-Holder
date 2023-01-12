package com.example.did_holder_app.data.model.VC


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Employee(
    @Json(name = "name")
    val name: String,
    @Json(name = "phoneno")
    val phoneno: String,
    @Json(name = "psotion")
    val psotion: String,
    @Json(name = "status")
    val status: String,
    @Json(name = "type")
    val type: String
)
