package com.example.did_holder_app.data.model.VC


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class VC2(
    @Json(name = "code")
    val code: Int,
    @Json(name = "data")
    val `data`: Data,
    @Json(name = "msg")
    val msg: String
)