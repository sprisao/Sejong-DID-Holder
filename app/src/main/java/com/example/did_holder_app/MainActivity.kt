package com.example.did_holder_app

import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.example.did_holder_app.data.DIDRepositoryImpl
import com.example.did_holder_app.ui.theme.DID_Holder_AppTheme
import com.example.did_holder_app.ui.viewmodel.DIDViewModel
import com.example.did_holder_app.ui.viewmodel.DIDViewModelProviderFactory
import com.example.did_holder_app.util.Constants.DATASTORE_NAME
import timber.log.Timber

class MainActivity : ComponentActivity() {

    private val Context.dataStore by preferencesDataStore(DATASTORE_NAME)
    private lateinit var didViewModel: DIDViewModel

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /*init timber*/
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        val didRepository = DIDRepositoryImpl(dataStore)
        val factory = DIDViewModelProviderFactory(didRepository, this)

        didViewModel = ViewModelProvider(this, factory)[DIDViewModel::class.java]

        setContent {
            DID_Holder_AppTheme {
                DidApp(didViewModel)
            }
        }
    }


}


