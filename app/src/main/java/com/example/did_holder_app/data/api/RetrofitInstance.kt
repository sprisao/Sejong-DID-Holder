package com.example.did_holder_app.data.api

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager

object RetrofitInstance {

    class TrustAllCerts : javax.net.ssl.X509TrustManager {
        override fun checkClientTrusted(
            chain: Array<java.security.cert.X509Certificate>,
            authType: String
        ) {
        }

        override fun checkServerTrusted(
            chain: Array<java.security.cert.X509Certificate>,
            authType: String
        ) {
        }

        override fun getAcceptedIssuers(): Array<java.security.cert.X509Certificate> {
            return arrayOf()
        }
    }

    private val trustAllCerts = TrustAllCerts()
    private val trustAllCertsArray = arrayOf<TrustManager>(trustAllCerts)
    private val sslContext: SSLContext = SSLContext.getInstance("SSL").apply {
        init(null, trustAllCertsArray, java.security.SecureRandom())
    }
    private val trustAllHosts = HostnameVerifier { _, _ -> true }


    private const val BASE_URL = "http://192.168.4.85:8080"

    val moshi: Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()


    private val retrofit = Retrofit.Builder().baseUrl("https://14.63.215.106:8081/v1/holder/")
        .addConverterFactory(MoshiConverterFactory.create(moshi)).client(
            OkHttpClient.Builder().sslSocketFactory(
                sslContext.socketFactory,
                trustAllCerts
            ).hostnameVerifier(trustAllHosts).build()
        ).build()

    val blockchainApi: BlockChainHolderApi = retrofit.create(BlockChainHolderApi::class.java)
}
