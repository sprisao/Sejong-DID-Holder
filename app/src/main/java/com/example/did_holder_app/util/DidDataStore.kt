package com.example.did_holder_app.util

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.did_holder_app.data.model.DIDDocument.DidDocument
import com.example.did_holder_app.data.model.VC.VC
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber

class DidDataStore(private val context: Context) {

    val moshi: Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val jsonAdapter: JsonAdapter<DidDocument> =
        moshi.adapter(DidDocument::class.java)

    private object PreferencesKeys {
        val DID_DOCUMENT = stringPreferencesKey("did_document")
        val VC = stringPreferencesKey("vc")
    }

    companion object {
        private val Context.dataStore by preferencesDataStore(Constants.DATASTORE_NAME)
    }

    val getDidDocument: Flow<DidDocument?> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.DID_DOCUMENT]?.let {
            Timber.d("getDidDocument: $it")
            jsonAdapter.fromJson(it)
        }
    }

    suspend fun saveDidDocument(didDocument: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.DID_DOCUMENT] = didDocument
        }
    }

    suspend fun deleteDidDocument() {
        context.dataStore.edit { preferences ->
            preferences.remove(PreferencesKeys.DID_DOCUMENT)
        }
    }


    val getVC: Flow<VC?> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.VC]?.let {
            Timber.d("getVC: $it")
            moshi.adapter(VC::class.java).fromJson(it)
        }
    }


     suspend fun saveVc(vc: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.VC] = vc
        }
    }

    suspend fun deleteVc() {
        context.dataStore.edit { preferences ->
            preferences.remove(PreferencesKeys.VC)
        }
    }
}
