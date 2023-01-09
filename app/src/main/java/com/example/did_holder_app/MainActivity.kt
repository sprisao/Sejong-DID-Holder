package com.example.did_holder_app

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.did_holder_app.data.DIDRepositoryImpl
import com.example.did_holder_app.ui.theme.DID_Holder_AppTheme
import com.example.did_holder_app.ui.viewmodel.DIDViewModel
import com.example.did_holder_app.util.AndroidKeyStoreUtil.generateAndSaveKey
import com.example.did_holder_app.util.AndroidKeyStoreUtil.loadAndDecryptKey
import com.example.did_holder_app.util.Constants.DATASTORE_NAME
import kotlinx.coroutines.launch
import timber.log.Timber

class MainActivity : ComponentActivity() {

    private val Context.dataStore by preferencesDataStore(DATASTORE_NAME)
    lateinit var didViewModel: DIDViewModel

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /*init timber*/
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        val DIDRepository = DIDRepositoryImpl(dataStore)
        didViewModel = DIDViewModel(DIDRepository)

        lifecycleScope.launch{
            didViewModel.getDID()
            Timber.d("DID: ${didViewModel.getDID()}")
        }


        try {
            if (didViewModel.getDID() == null) {
            } else {
                Timber.d("DID is not null")
            }
        } catch (e: Exception) {
            Timber.e(e)
        }


        setContent {
            DID_Holder_AppTheme {
                DidApp(didViewModel)
            }
        }
    }


}


