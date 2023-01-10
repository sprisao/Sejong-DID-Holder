package com.example.did_holder_app

import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.collectAsState
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.example.did_holder_app.data.DIDRepositoryImpl
import com.example.did_holder_app.ui.theme.DID_Holder_AppTheme
import com.example.did_holder_app.ui.viewmodel.DIDViewModel
import com.example.did_holder_app.ui.viewmodel.DIDViewModelProviderFactory
import com.example.did_holder_app.util.Constants
import com.example.did_holder_app.util.Constants.DATASTORE_NAME
import com.example.did_holder_app.util.DidDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber

class MainActivity : ComponentActivity() {

//    private lateinit var didViewModel: DIDViewModel

//    private object PreferencesKeys {
//        val DID = stringPreferencesKey("did")
//        val PUBLIC_KEY = stringPreferencesKey("public_key")
//    }
//
//    companion object {
//        const val DATASTORE_NAME = "did_datastore"
//        private val Context.dataStore by preferencesDataStore(Constants.DATASTORE_NAME)
//    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /*init timber*/
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        setContent {
            DID_Holder_AppTheme {
                DidApp()
            }
        }
    }


}


