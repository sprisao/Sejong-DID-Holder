package com.example.did_holder_app.data.model.DIDDocument


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DidDocument(
    @Json(name = "@context")
    val context: String,
    @Json(name = "id")
    val id: String,
    @Json(name = "publicKey")
    val publicKey: List<PublicKey>,
    @Json(name = "authentication")
    val authentication: List<Authentication>,
//    @Json(name = "service")
//    val service: List<Service>
) {
    constructor() : this(
        "",
        "",
        listOf(),
        listOf(),)
}

//{
//    "@context": "https://www.w3.org/ns/did/v1",
//    "id": "did:example:123456789abcdefghi",
//    "publicKey": [{
//    "id": "did:example:123456789abcdefghi#keys-1",
//    "type": "Ed25519VerificationKey2018",
//    "controller": "did:example:123456789abcdefghi",
//    "publicKeyBase58": "H3C2AVvLMv6gmMNam3uVAjZpfkcJCwDwnZn6z3wXmqPV"
//}],
//    "authentication": [{
//    "type": "Ed25519SignatureAuthentication2018",
//    "publicKey": "did:example:123456789abcdefghi#keys-1"
//}],
//    "service": [{
//    "id": "did:example:123456789abcdefghi;indx",
//    "type": "IndxService",
//    "serviceEndpoint": "https://example.com/indx"
//}]
//}
