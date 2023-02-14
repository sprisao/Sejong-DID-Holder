package com.example.did_holder_app.data.api

import com.example.did_holder_app.data.model.VC.SignUpRequest
import com.example.did_holder_app.data.model.VC.SignUpResponse
import com.example.did_holder_app.data.model.VC.VCRequest
import com.example.did_holder_app.data.model.VC.VCResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface VCApi {

    //http://localhost:8080/v1/test?userid=tibob44&username=%ED%95%9C%ED%98%84%EC%88%98

    @POST("/v1/member/vc")
    fun getVC(@Body request: VCRequest): Call<VCResponse>

    @POST("/v1/member")
    fun createUser(@Body request: SignUpRequest): Call<SignUpResponse>

}
