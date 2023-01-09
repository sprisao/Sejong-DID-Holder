package com.example.did_holder_app.util

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DidDataStore(private val context: Context){
    private object PreferencesKeys {
        val DID = stringPreferencesKey("did")
        val PUBLIC_KEY = stringPreferencesKey("public_key")
    }
    companion object {
        const val DATASTORE_NAME = "did_datastore"
        private val Context.dataStore by preferencesDataStore(Constants.DATASTORE_NAME)
    }

    val getDid: Flow<String?> = context.dataStore.data.map{ preferences ->
        preferences[PreferencesKeys.DID] ?: ""
    }

    suspend fun saveDid(did: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.DID] = did
        }
    }
}
