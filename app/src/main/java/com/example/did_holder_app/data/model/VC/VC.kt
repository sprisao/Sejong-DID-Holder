package com.example.did_holder_app.data.model.VC


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class VCResponse(
    @Json(name = "code")
    val code: Int?,
    @Json(name = "data")
    val data: Data?,
    @Json(name = "msg")
    val msg: String
) {
    constructor() : this(0, Data(), "")
}

@JsonClass(generateAdapter = true)
data class SignUpRequest(
    @Json(name = "userid") val userId: String,
    @Json(name = "userpass") val userPass: String,
    @Json(name = "username") val userName: String,
    @Json(name = "userphoneno") val userPhoneNo: String,
    @Json(name = "jobposition") val jobPosition: String
)

@JsonClass(generateAdapter = true)
data class SignUpResponse(
    @Json(name = "data")
    val data: SignUpData?,
    @Json(name = "code")
    val code: Int?,
    @Json(name = "msg")
    val msg: String
)

@JsonClass(generateAdapter = true)
data class SignUpData(
    @Json(name = "upddate")
    val upddate: String?,
    @Json(name = "userpass")
    val userpass: String?,
    @Json(name = "regdate")
    val regdate: String?,
    @Json(name = "jobposition")
    val jobposition: String?,
    @Json(name = "userid")
    val userid: String?,
    @Json(name = "userphoneno")
    val userphoneno: String?,
    @Json(name = "userseq")
    val userseq: Int?,
)


@JsonClass(generateAdapter = true)
data class VCRequest(
    @Json(name = "userseq")
    val userseq: Int,
    @Json(name = "holderdid")
    val holderdid: String,
)