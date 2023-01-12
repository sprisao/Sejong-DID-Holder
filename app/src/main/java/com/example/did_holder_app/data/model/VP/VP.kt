package com.example.did_holder_app.data.model.VP


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class VP(
    @Json(name = "@context")
    val context: List<String>,
    @Json(name = "id")
    val id: String,
    @Json(name = "proof")
    val proof: Proof,
    @Json(name = "type")
    val type: List<String>,
    @Json(name = "verifiableCredential")
    val verifiableCredential: List<VerifiableCredential>
)

//{
//    "@context": [
//    "https://www.w3.org/2018/credentials/v1",
//    "https://www.w3.org/2018/credentials/examples/v1"
//    ],
//    "id": "http://sejongCredential/presentations/1234",
//    "type": [
//    "VerifiablePresentation",
//    "SejongAccessPresentation"
//    ],
//    "verifiableCredential": [
//    {
//        "@context": [
//        "https://www.w3.org/2018/credentials/v1",
//        "https://www.w3.org/2018/credentials/examples/v1"
//        ],
//        "id": "http://sejongCredential/credentials/58473",
//        "type": [
//        "VerifiableCredential",
//        "SejongAccessCredential"
//        ],
//        "issuer": "did:sejong:mainnet:ebfeb1f712ebc6f1c276e12ec22(Issuer-DID)",
//        "issuanceDate": "2023-01-01T00:00:00Z",
//        "expirationDate": "2024-01-01T00:00:00Z",
//        "credentialSubject": {
//        "id": "did:sejong:mainnet:ebfeb1f712ebc6f1c276e12ec21(Holder-DID)",
//        "employee": {
//        "type": "BlockChainDevelopment",
//        "psotion": "JM",
//        "name": "JiSeungGu",
//        "phoneno": "01011111111",
//        "status": "access"
//    }
//    },
//        "proof": {
//        "type": "Ed25519Signature2020",
//        "created": "2023-01-01T00:00:00Z",
//        "verificationMethod": "https://sejong/issuers/565049#key-1",
//        "proofPurpose": "SejongAccessMethod",
//        "proofValue": "z3BXsFfx1qJ5NsTkKqREjQ3AGh6RAmCwvgu1HcDSzK3P5QEg2TAw8ufktJBw8QkAQRciMGyBf5T2AHyRg2w13Uvhp"
//    }
//    }
//    ],
//    "proof": {
//    "type": "RsaSignature2018",
//    "created": "2023-01-02T00:00:00Z",
//    "proofPurpose": "authentication",
//    "verificationMethod": "https://sejong/holder/565049#key-1",
//    "challenge": "99612b24-63d9-11ea-b99f-4f66f3e4f81a",
//    "domain": "sejong.service.com",
//    "proofValue": "DgYdYMUYHURJLD7xdnWRinqWCEY5u5fK...j915Lt3hMzLHoPiPQ9sSVfRrs1D"
//}
//}
