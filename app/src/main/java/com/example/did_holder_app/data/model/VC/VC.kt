package com.example.did_holder_app.data.model.VC


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class VcResponse(
    @Json(name = "code")
    val code: Int?,
    @Json(name = "data")
    val vcResponseData: VcResponseData?,
    @Json(name = "msg")
    val msg: String
)

@JsonClass(generateAdapter = true)
data class SignInRequest(
    @Json(name = "userid") val userId: String,
    @Json(name = "userpass") val userPass: String,
)

@JsonClass(generateAdapter = true)
data class SignUpRequest(
    @Json(name = "userid") val userId: String,
    @Json(name = "userpass") val userPass: String,
    @Json(name = "username") val userName: String,
    @Json(name = "userphoneno") val userPhoneNo: String,
    @Json(name = "jobposition") val jobPosition: String
)

@JsonClass(generateAdapter = true)
data class SignInResponse(
    @Json(name = "data")
    val data: SignInData?,
    @Json(name = "code")
    val code: Int?,
    @Json(name = "msg")
    val msg: String?
)

@JsonClass(generateAdapter = true)
data class SignUpResponse(
    @Json(name = "data")
    val data: SignUpData?,
    @Json(name = "code")
    val code: Int?,
    @Json(name = "msg")
    val msg: String?
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
data class SignInData(
    @Json(name = "userpass")
    val userPassword: String?,
    @Json(name = "regdate")
    val registrationDate: String?,
    @Json(name = "jobposition")
    val jopPosition: String?,
    @Json(name = "userid")
    val userId: String?,
    @Json(name = "userphoneno")
    val userPhoneNo: String?,
    @Json(name = "userseq")
    val userSequence: Int?,
    @Json(name = "upddate")
    val updateDate: String?,
    @Json(name = "status")
    val userStatus: String?
)


@JsonClass(generateAdapter = true)
data class VCRequest(
    @Json(name = "userseq")
    val userseq: Int?,
    @Json(name = "holderdid")
    val holderdid: String?,
)