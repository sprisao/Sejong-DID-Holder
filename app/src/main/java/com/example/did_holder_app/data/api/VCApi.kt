package com.example.did_holder_app.data.api

import com.example.did_holder_app.data.model.VC.*
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface VCApi {

    //http://localhost:8080/v1/test?userid=tibob44&username=%ED%95%9C%ED%98%84%EC%88%98

    @POST("/v1/member/vc")
    fun requestVC(@Body request: VCRequest): Call<VcResponse>

    @POST("/v1/member")

    fun signUp(@Body request: SignUpRequest): Call<SignUpResponse>

    @POST("/v1/member/login")
    fun signIn(@Body request: SignInRequest): Call<SignInResponse>

}
