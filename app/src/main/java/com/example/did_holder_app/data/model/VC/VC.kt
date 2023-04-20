package com.example.did_holder_app.data.model.VC


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class VcResponse(
    @Json(name = "code")
    val code: Int,
    @Json(name = "data")
    val vcData: VcData,
    @Json(name = "msg")
    val msg: String
)

@JsonClass(generateAdapter = true)
data class VcData(
    @Json(name = "vc")
    val verifiableCredential: VerifiableCredential?,
    @Json(name = "additionalInfo")
    val additionalInfo: AdditionalInfo?
) {
    constructor() : this(
        VerifiableCredential(
            listOf(),
            "",
            listOf(),
            listOf(),
            "",
            "",
            "",
            "",
            Proof(
                "",
                "",
                "",
                "",
                "",
            )

        ), AdditionalInfo(
            listOf(),
            listOf(),
        )
    )
}

@JsonClass(generateAdapter = true)
data class VerifiableCredential(
    @Json(name = "@context")
    val context: List<String>?,
    @Json(name = "id")
    val id: String?,
    @Json(name = "type")
    val type: List<String>?,
    @Json(name = "credentialSubject")
    val credentialSubject: List<CredentialSubject>?,
    @Json(name = "issuer")
    val issuer: String?,
    @Json(name = "issuanceDate")
    val issuanceDate: String?,
    @Json(name = "validFrom")
    val validFrom: String?,
    @Json(name = "validUntil")
    val validUntil: String?,
    @Json(name = "proof")
    val proof: Proof?
)

@JsonClass(generateAdapter = true)
data class CredentialSubject(
    @Json(name = "type")
    val type: String?,
    @Json(name = "id")
    val id: String?,
    @Json(name = "position")
    val position: String?,
    @Json(name = "name")
    val name: String?,
    @Json(name = "status")
    val status: String?
)

@JsonClass(generateAdapter = true)
data class Proof(
    @Json(name = "type")
    val type: String?,
    @Json(name = "creator")
    val creator: String?,
    @Json(name = "created")
    val created: String?,
    @Json(name = "proofPurpose")
    val proofPurpose: String?,
    @Json(name = "proofValue")
    val proofValue: String?
)

@JsonClass(generateAdapter = true)
data class AdditionalInfo(
    @Json(name = "credentialText")
    val credentialText: List<CredentialText>?,
    @Json(name = "credentialSalt")
    val credentialSalt: List<CredentialSalt>?
)

@JsonClass(generateAdapter = true)
data class CredentialText(
    @Json(name = "name")
    val name: String?,
    @Json(name = "id")
    val id: String?,
    @Json(name = "position")
    val position: String?,
    @Json(name = "type")
    val type: String?,
    @Json(name = "status")
    val status: String?
)

@JsonClass(generateAdapter = true)
data class CredentialSalt(
    @Json(name = "name")
    val name: String?,
    @Json(name = "id")
    val id: String?,
    @Json(name = "position")
    val position: String?,
    @Json(name = "type")
    val type: String?,
    @Json(name = "status")
    val status: String?
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