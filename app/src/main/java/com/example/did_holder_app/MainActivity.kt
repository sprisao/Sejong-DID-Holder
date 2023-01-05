package com.example.did_holder_app

import android.annotation.SuppressLint
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
import com.example.did_holder_app.ui.theme.DID_Holder_AppTheme
import com.example.did_holder_app.util.AndroidKeyStoreUtil.generateAndSaveKey
import com.example.did_holder_app.util.AndroidKeyStoreUtil.loadAndDecryptKey
import timber.log.Timber

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /*init timber*/
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

//        val dummyKey = "This is My Dummy Key"
//        val encryptedKey: ByteArray = generateAndSaveKey(dummyKey)
//        Timber.d("Encrypted Key: $encryptedKey")
//        val decryptedKey: String = loadAndDecryptKey(encryptedKey)
//        Timber.d("Decrypted Key: $decryptedKey")


        setContent {
            DID_Holder_AppTheme {

            }
        }
    }


}


