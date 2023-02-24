package com.example.did_holder_app.data.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface VPApi {
    @POST("/v1/verify")
    fun sendVP(@Body request: VpRequest): Call<VpResponse>

}

@JsonClass(generateAdapter = true)
data class VpRequest(
    @Json(name = "holderdid")
    val holderdid: String,
    @Json(name = "vp")
    val vp: String
)


@JsonClass(generateAdapter = true)
data class VpResponseData(
    @Json(name = "challengeresult")
    var challengeResult: Boolean,
    @Json(name = "vpresult")
    var vpResult: Boolean,
    @Json(name = "vcResult")
    var vcResult: Boolean,
    @Json(name = "vcstatusresult")
    var vcStatusResult: Boolean,
    @Json(name = "verifyresult")
    var verifyResult: Boolean
)


@JsonClass(generateAdapter = true)
data class VpResponse(
    @Json(name = "code")
    var code: Int,
    @Json(name = "msg")
    var msg: String,
    @Json(name = "data")
    var data: VpResponseData
)
