package com.example.did_holder_app.data.model.VPRequest


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class VPRequest(
    @Json(name = "challenge")
    val challenge: String,
    @Json(name = "domain")
    val domain: String,
    @Json(name = "query")
    val query: List<Query>
)

//{
//    "query": [
//    {
//        "type": "DIDAuthentication",
//        "acceptedMethods": [
//        {
//            "method": "key"
//        }
//        ]
//    }],
//    "challenge": "99612b24-63d9-11ea-b99f-4f66f3e4f81a",
//    "domain": "sejong.service.com"
//}
