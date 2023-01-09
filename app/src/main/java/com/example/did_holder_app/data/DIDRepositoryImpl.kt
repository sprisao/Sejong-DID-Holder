package com.example.did_holder_app.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

class DIDRepositoryImpl(private val dataStore: DataStore<Preferences>) : DIDRepository {

    private object PreferencesKeys {
        val DID = stringPreferencesKey("did")
        val PUBLIC_KEY = stringPreferencesKey("public_key")
    }

    override suspend fun saveDID(did: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.DID] = did
        }
    }

    override suspend fun getDID(): Flow<String> {
        val preferences = dataStore.data
        return preferences.catch { exception ->
            if (exception is Exception) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }.map { preferences ->
            preferences[PreferencesKeys.DID] ?: ""
        }
    }

    override suspend fun savePublicKey(publicKey: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.PUBLIC_KEY] = publicKey
        }
    }

    override suspend fun getPublicKey(): Flow<String> {
        return dataStore.data.catch { exception ->
            if (exception is Exception) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }.map { preferences ->
            preferences[PreferencesKeys.PUBLIC_KEY] ?: ""
        }
    }

}


//private object PreferencesKeys {
//    val SORT_MODE = stringPreferencesKey("sort_mode")
//}
//
//override suspend fun saveSortMode(mode: String) {
//    dataStore.edit { preferences ->
//        preferences[PreferencesKeys.SORT_MODE] = mode
//    }
//}
//
//override suspend fun getSortMode(): Flow<String> {
//    return dataStore.data
//        .catch { exception ->
//            if (exception is IOException) {
//                emit(emptyPreferences())
//                exception.printStackTrace()
//            } else {
//                throw exception
//            }
//        }
//        .map { preferences ->
//            val sortMode = preferences[PreferencesKeys.SORT_MODE] ?: Sort.ACCURACY.value
//            sortMode
//        }
//}
//}
