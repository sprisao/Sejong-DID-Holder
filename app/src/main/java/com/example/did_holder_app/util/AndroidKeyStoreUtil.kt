package com.example.did_holder_app.util

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.security.KeyStore
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec


object AndroidKeyStoreUtil {
    private const val KEY_LENGTH_BIT = 128
    private val keyGenerator =
        KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")

    private val keyStore = KeyStore.getInstance("AndroidKeyStore")

    private val IV = ByteArray(12)

    private val CIPHER_WITH_ALGORITHM = Cipher.getInstance("AES/GCM/NoPadding")
    fun generateAndSaveKey(plaintext: String): ByteArray {

        val keyGenParameterSpec = KeyGenParameterSpec.Builder(
            "key1",
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setUserAuthenticationRequired(false)
            .setRandomizedEncryptionRequired(false)
            .build()

        keyGenerator.init(keyGenParameterSpec)
        val secretKey: SecretKey = keyGenerator.generateKey()

        SecureRandom().nextBytes(IV)

        // Encrypt the plaintext
        CIPHER_WITH_ALGORITHM.init(
            Cipher.ENCRYPT_MODE, secretKey, GCMParameterSpec(
                KEY_LENGTH_BIT, IV
            )
        )
        val ciphertext = CIPHER_WITH_ALGORITHM.doFinal(plaintext.toByteArray())

        keyStore.load(null)

        keyStore.setEntry(
            "key1",
            KeyStore.SecretKeyEntry(secretKey),
            null
        )

        return ciphertext

    }

    fun loadAndDecryptKey(ciphertext: ByteArray): String {

        // Load the key from the Android Keystore
        keyStore.load(null)

        val secretKey = keyStore.getKey("key1", null) as SecretKey

        // Decrypt the ciphertext
        CIPHER_WITH_ALGORITHM.init(
            Cipher.DECRYPT_MODE, secretKey, GCMParameterSpec(
                KEY_LENGTH_BIT, IV
            )
        )

        return String(CIPHER_WITH_ALGORITHM.doFinal(ciphertext))
    }
}
