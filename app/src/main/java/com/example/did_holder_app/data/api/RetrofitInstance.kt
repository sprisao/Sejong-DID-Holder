package com.example.did_holder_app.data.api

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitInstance {
    private val okHttpClient: OkHttpClient by lazy {

        /* http 통신간 이루어지는 bodylevel의 log들을 가로챈다. */
        val httpLoggingInterceptor =
            HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)

        /* Intercepter를 포함하여 OkHttpClient를 빌드한다. */
        OkHttpClient.Builder().addInterceptor(httpLoggingInterceptor)
            .connectTimeout(100, TimeUnit.SECONDS).readTimeout(100, TimeUnit.SECONDS)
            .writeTimeout(100, TimeUnit.SECONDS).build()
    }

    private const val BASE_URL = "http://192.168.4.85:8080"
//    192.168.4.85

    val moshi: Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder().addConverterFactory(MoshiConverterFactory.create(moshi)).baseUrl(BASE_URL)
            .client(okHttpClient).build()
    }

    val didApi: DIDApi by lazy {
        retrofit.create(DIDApi::class.java)
    }

}