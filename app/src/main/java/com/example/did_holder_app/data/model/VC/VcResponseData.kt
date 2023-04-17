package com.example.did_holder_app.data.model.VC


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
//
//@JsonClass(generateAdapter = true)
//data class VcResponseData(
//    @Json(name = "@context")
//    val context: List<String>,
//    @Json(name = "id")
//    val id: String,
//    @Json(name = "type")
//    val type: List<String>,
//    @Json(name = "credentialSubject")
//    val vcCredentialSubject: List<VcCredentialSubject>,
//    @Json(name = "issuer")
//    val issuer: String,
//    @Json(name = "issuanceDate")
//    val issuanceDate: String,
////    @Json(name = "expirationDate")
////    val expirationDate: String?,
//    @Json(name = "validFrom")
//    val validFrom: String?,
//    @Json(name = "validUntil")
//    val validUntil: String?,
//    @Json(name = "proof")
//    val vcProof: VcProof,
//) {
//
//    constructor() : this(
//        listOf(),
//        "",
//        listOf(),
//        listOf(),
//        "",
//        "",
//        "",
//        "",
//        VcProof())
//}