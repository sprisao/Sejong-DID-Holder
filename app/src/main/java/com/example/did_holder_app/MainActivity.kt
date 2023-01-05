package com.example.did_holder_app

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
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

//        val keystore = AndroidKeyStoreUtil()
        val dummyKey = "This is My Dummy Key"
        /*Encrypt this dummykey*/

        val encryptedKey: ByteArray = generateAndSaveKey(dummyKey)
        Timber.d("Encrypted Key: $encryptedKey")

        val decryptedKey: String = loadAndDecryptKey(encryptedKey)
//
        Timber.d("Decrypted Key: $decryptedKey")



        setContent {
            DID_Holder_AppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting("Android")
//                    usingKeystore()
                }
            }
        }
    }


}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    DID_Holder_AppTheme {
        Greeting("Android")
    }
}
