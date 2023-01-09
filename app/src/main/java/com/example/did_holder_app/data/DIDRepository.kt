package com.example.did_holder_app.data

import kotlinx.coroutines.flow.Flow

interface DIDRepository {

    suspend fun saveDID(did: String)
    suspend fun getDID(): Flow<String>

    suspend fun savePublicKey(publicKey: String)
    suspend fun getPublicKey(): Flow<String>

}


