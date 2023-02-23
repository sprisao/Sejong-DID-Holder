package com.example.did_holder_app.data.keystore

import android.os.Build
import android.security.keystore.KeyProperties
import android.security.keystore.KeyProtection
import androidx.annotation.RequiresApi
import org.bouncycastle.crypto.generators.Ed25519KeyPairGenerator
import org.bouncycastle.crypto.params.Ed25519KeyGenerationParameters
import org.bouncycastle.crypto.params.Ed25519PrivateKeyParameters
import org.bouncycastle.crypto.params.Ed25519PublicKeyParameters
import java.security.KeyStore
import java.security.SecureRandom
import javax.crypto.spec.SecretKeySpec


object AndroidKeyStoreUtil {

    @RequiresApi(Build.VERSION_CODES.O)
    fun generateAndStoreEd25519KeyPair(): Pair<Ed25519PrivateKeyParameters, Ed25519PublicKeyParameters> {

        val keyAlias = "did_key_alias"

        // KeyStore 불러옴
        val keyStore = KeyStore.getInstance("AndroidKeyStore")
        keyStore.load(null)

        // KeyStore에 이미 키가 존재하는지 확인
//        if (keyStore.containsAlias(keyAlias)) {
//            throw Exception("KeyStore already contains key with alias $keyAlias")
//        }

        val keyPairGenerator = Ed25519KeyPairGenerator()

        val random = SecureRandom()

        keyPairGenerator.init(Ed25519KeyGenerationParameters(random))

        // 대칭키쌍 생성
        val keyPair = keyPairGenerator.generateKeyPair()

        // 개인키
        val privateKey = keyPair.private as Ed25519PrivateKeyParameters

        // 개인키를 byte로 변환
        val privateKeyEncoded = privateKey.encoded

        // KeyStore Entry 생성
        val keyEntry = KeyStore.SecretKeyEntry(
            SecretKeySpec(privateKeyEncoded, KeyProperties.KEY_ALGORITHM_AES),
        )


        // KeyStore에 저장
        keyStore.setEntry(
            keyAlias,
            keyEntry,
            KeyProtection.Builder(KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setRandomizedEncryptionRequired(false)
                .build()
        )

        // 공개키
        val publicKey = keyPair.public as Ed25519PublicKeyParameters

        return Pair(privateKey, publicKey)
    }

}
