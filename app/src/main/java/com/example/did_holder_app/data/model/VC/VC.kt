package com.example.did_holder_app.data.model.VC


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class VC(
    @Json(name = "@context")
    val context: List<String>,
    @Json(name = "credentialSubject")
    val credentialSubject: CredentialSubject,
    @Json(name = "expirationDate")
    val expirationDate: String,
    @Json(name = "id")
    val id: String,
    @Json(name = "issuanceDate")
    val issuanceDate: String,
    @Json(name = "issuer")
    val issuer: String,
    @Json(name = "proof")
    val proof: Proof,
    @Json(name = "type")
    val type: List<String>
)

//{
//    "@context": [
//    "https://www.w3.org/2018/credentials/v1",
//    "https://www.w3.org/2018/credentials/examples/v1"
//    ],
//    "id": "http://sejongCredential/credentials/58473",
//    "type": ["VerifiableCredential", "SejongAccessCredential"],
//    "issuer": "did:sejong:mainnet:ebfeb1f712ebc6f1c276e12ec22(Issuer-DID)",
//    "issuanceDate": "2023-01-01T00:00:00Z",
//    "expirationDate": "2024-01-01T00:00:00Z",
//    "credentialSubject": {
//    "id": "did:sejong:mainnet:ebfeb1f712ebc6f1c276e12ec21(Holder-DID)",
//    "employee": {
//        "type": "BlockChainDevelopment",
//        "psotion" :"JM",
//        "name": "JiSeungGu",
//        "phoneno" :"01011111111",
//        "status" : "access"
//    }
//},
//    "proof": {
//    "type": "Ed25519Signature2020",
//    "created": "2023-01-01T00:00:00Z",
//    "verificationMethod": "https://sejong/issuers/565049#key-1",
//    "proofPurpose": "SejongAccessMethod",
//    "proofValue": "z3BXsFfx1qJ5NsTkKqREjQ3AGh6RAmCwvgu1HcDSzK3P5QEg2TAw8ufktJBw8QkAQRciMGyBf5T2AHyRg2w13Uvhp"
//}
//}
