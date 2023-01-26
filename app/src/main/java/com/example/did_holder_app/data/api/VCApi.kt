package com.example.did_holder_app.data.api

import com.example.did_holder_app.data.model.VC.VC
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface VCApi {

    //http://localhost:8080/v1/test?userid=tibob44&username=%ED%95%9C%ED%98%84%EC%88%98

    @GET("/v1/test")
    fun getVC(
        @Query("userid") userid: String,
        @Query("username") username: String
    ): Call<VC>
}
