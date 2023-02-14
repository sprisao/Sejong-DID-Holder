package com.example.did_holder_app.util

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.did_holder_app.data.model.DIDDocument.DidDocument
import com.example.did_holder_app.data.model.VC.VC
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DidDataStore(context: Context) {
    companion object {
        private val Context.dataStore by preferencesDataStore(Constants.DATASTORE_NAME)
    }

    private val moshi: Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    // Adapters
    private val didDocumentAdapter = moshi.adapter(DidDocument::class.java)
    private val vcAdapter = moshi.adapter(VC::class.java)

    private val dataStore = context.dataStore

    object Key {
        val DID_DOCUMENT = stringPreferencesKey("did_document")
        val VC = stringPreferencesKey("vc")
        val USERSEQ = stringPreferencesKey("userseq")
    }


    // Did Document Flow

    val didDocumentFlow: Flow<DidDocument?> = dataStore.data.map {
        val didDocumentString = it[Key.DID_DOCUMENT]
        if (didDocumentString != null) {
            didDocumentAdapter.fromJson(didDocumentString)
        } else {
            null
        }
    }

    suspend fun saveDidDocument(didDocument: String) {
        dataStore.edit {
            it[Key.DID_DOCUMENT] = didDocument
        }
    }

    suspend fun clearDidDocument() {
        dataStore.edit {
            it.remove(Key.DID_DOCUMENT)
        }
    }


    // VC Flow

    val vcFlow: Flow<VC?> = context.dataStore.data.map {
        val vcString = it[Key.VC]
        if (vcString != null) {
            vcAdapter.fromJson(vcString)
        } else {
            null
        }
    }


    suspend fun saveVc(vc: String) {
        dataStore.edit {
            it[Key.VC] = vc
        }
    }

    suspend fun clearVc() {
        dataStore.edit {
            it.remove(Key.VC)
        }
    }


    val userseqFlow: Flow<Int?> = context.dataStore.data.map {
        val userseqString = it[Key.USERSEQ]
        if (userseqString != null) {
            userseqString.toInt()
        } else {
            null
        }
    }

    suspend fun saveUserseq(userseq: Int) {
        dataStore.edit {
            it[Key.USERSEQ] = userseq.toString()
        }
    }

    suspend fun clearUserseq() {
        dataStore.edit {
            it.remove(Key.USERSEQ)
        }
    }


}
