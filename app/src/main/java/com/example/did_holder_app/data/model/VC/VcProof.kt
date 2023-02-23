package com.example.did_holder_app.data.model.VC


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class VcProof(
    @Json(name = "type")
    val type: String,
    @Json(name = "creator")
    val creator: String,
    @Json(name = "created")
    val created: String,
    @Json(name = "proofPurpose")
    val proofPurpose: String,
    @Json(name = "proofValue")
    val proofValue: String,
){
    constructor() : this(
        "",
        "",
        "",
        "",
        ""
    )
}