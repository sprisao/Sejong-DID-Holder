package com.example.did_holder_app.util

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.did_holder_app.data.model.DidDocument
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


}
