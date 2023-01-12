package com.example.did_holder_app.data.model.VPRequest


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AcceptedMethod(
    @Json(name = "method")
    val method: String
)
