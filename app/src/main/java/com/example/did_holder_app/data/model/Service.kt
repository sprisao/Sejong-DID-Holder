package com.example.did_holder_app.data.model


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Service(
    @Json(name = "id")
    val id: String,
    @Json(name = "serviceEndpoint")
    val serviceEndpoint: String,
    @Json(name = "type")
    val type: String
)
