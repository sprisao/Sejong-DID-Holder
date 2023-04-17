package com.example.did_holder_app.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.did_holder_app.data.model.DIDDocument.DidDocument
import com.example.did_holder_app.data.model.VC.VcData
import com.example.did_holder_app.util.Constants
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
    private val vcResponseAdapter = moshi.adapter(VcData::class.java)

    private val dataStore = context.dataStore

    object Key {
        val DID_DOCUMENT = stringPreferencesKey("did_document")
        val VC = stringPreferencesKey("vc")
        val USERSEQ = stringPreferencesKey("userseq")
        val PRIVATE_KEY = stringPreferencesKey("private_key")
        val VP = stringPreferencesKey("vp")

        val IS_DID_SAVED = stringPreferencesKey("is_did_saved")
    }

    val isDidSavedFlow: Flow<Boolean> = context.dataStore.data.map {
        it[Key.IS_DID_SAVED].toBoolean()
    }

    suspend fun saveIsDidSaved(isDidSaved: Boolean) {
        dataStore.edit {
            it[Key.IS_DID_SAVED] = isDidSaved.toString()
        }
    }

    suspend fun clearIsDidSaved() {
        dataStore.edit {
            it.remove(Key.IS_DID_SAVED)
        }
    }

    val vpFlow: Flow<String?> = context.dataStore.data.map {
        it[Key.VP]
    }

    suspend fun saveVp(vp: String) {
        dataStore.edit {
            it[Key.VP] = vp
        }
    }

    suspend fun clearVp() {
        dataStore.edit {
            it.remove(Key.VP)
        }
    }


    val privateKeyFlow: Flow<String?> = context.dataStore.data.map {
        it[Key.PRIVATE_KEY]
    }

    suspend fun savePrivateKey(privateKey: String) {
        dataStore.edit {
            it[Key.PRIVATE_KEY] = privateKey
        }
    }

    suspend fun clearPrivateKey() {
        dataStore.edit {
            it.remove(Key.PRIVATE_KEY)
        }
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

    // VC Flow
    val vcFlow: Flow<VcData?> = context.dataStore.data.map {
        val vcString = it[Key.VC]
        if (vcString != null) {
            vcResponseAdapter.fromJson(vcString)
        } else {
            null
        }
    }

    val userSeqFlow: Flow<Int?> = context.dataStore.data.map {
        it[Key.USERSEQ]?.toInt()
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

    suspend fun saveUserSeq(userSeq: Int) {
        dataStore.edit {
            it[Key.USERSEQ] = userSeq.toString()
        }
    }

    suspend fun clearUserSeq() {
        dataStore.edit {
            it.remove(Key.USERSEQ)
        }
    }


}
