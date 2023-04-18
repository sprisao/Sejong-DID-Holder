package com.example.did_holder_app.util

object Constants {
    // Screen Names
    const val DID_SCREEN_NAME = "DID"
    const val VC_SCREEN_NAME = "VC"
    const val QR_SCREEN_NAME = "QR"
    const val QR_RESULT_SCREEN_NAME = "QR_RESULT"
    const val SIGN_UP_SCREEN_NAME = "SIGN_UP"

    // DataStore
    const val DATASTORE_NAME = "DID_DATASTORE"

    // KeyPair
    const val KEYPAIR_ALGORITHM = "RSA"

    // DID Document
    const val DID_DOCUMENT_CONTEXT = "https://www.w3.org/ns/did/v1"
    const val DID_DOCUMENT_METHODE = "SEJONG_MAINNET"
    const val DID_DOCUMENT_PUBLIC_KEY_TYPE = "RSAVerificationkey2018"
    const val DID_DOCUMENT_AUTHENTICATION_TYPE = "RsaSignatureAuthentication2023"
    const val DID_DOCUMENT_SERVICE_TYPE = "IndxService"
    const val DID_DOCUMENT_SERVICE_ENDPOINT = "https://example.com/indx"


    // VP
    const val VP_PROOF_TYPE = "RSASignature2018"
    const val VP_PROOF_PURPOSE = "authentication"

}
