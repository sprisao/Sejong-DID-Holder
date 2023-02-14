package com.example.did_holder_app.data.api

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager

object RetrofitInstance {

    val moshi: Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    // Blockchain Server에 접근하기위해 SSL 인증서를 무시하는 OkHttpClient 생성
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

    // Urls
    private const val VC_SERVER_URL = "https://14.63.215.106:8080"
    private const val VC_HOLDER_URL = "https://14.63.215.106:8081"
    private const val RELAY_SERVER_URL = "https://14.63.215.106:8082"
    private const val BLOCKCHAIN_HOLDER_URL = "https://14.63.215.106:8081/v1/holder/"

    val httpLoggingInterceptor = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)

    // BlockChain에 DidDocument 저장
    private val retrofit_blockchain = Retrofit.Builder().baseUrl(BLOCKCHAIN_HOLDER_URL)
        .addConverterFactory(MoshiConverterFactory.create(moshi)).client(
            OkHttpClient.Builder().addInterceptor(httpLoggingInterceptor).sslSocketFactory(
                sslContext.socketFactory,
                trustAllCerts
            ).hostnameVerifier(trustAllHosts).build()
        ).build()


    // Issuer와 통신하여 VC 발급
    private val retrofit_vc = Retrofit.Builder()
        .baseUrl(VC_SERVER_URL)
        .addConverterFactory(MoshiConverterFactory.create(moshi)).client(
            OkHttpClient.Builder().addInterceptor(httpLoggingInterceptor).sslSocketFactory(
                sslContext.socketFactory,
                trustAllCerts
            ).hostnameVerifier(trustAllHosts).build()
        ).build()

    //Api
    val blockchainApi: BlockchainApi =
        retrofit_blockchain.create(BlockchainApi::class.java)

    val vcServerApi: VCApi = retrofit_vc.create(VCApi::class.java)
}
