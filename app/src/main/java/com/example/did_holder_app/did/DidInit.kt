package com.example.did_holder_app.did

import android.content.Context
import android.util.Base64
import com.example.did_holder_app.ui.viewmodel.DIDViewModel
import com.example.did_holder_app.util.AndroidKeyStoreUtil
import java.security.*

class DidInit(context: Context) {


    /* 1. 비대칭 키 쌍 생성 */
    val publicKey: PublicKey = generateKeyPair().public
    val privateKey: PrivateKey = generateKeyPair().private

    /* 2. PrivateKey 암호화 및 Keystore에 저장*/
    val encryptedPrivateKey: ByteArray =
        AndroidKeyStoreUtil.generateAndSaveKey(privateKey.toString())
    val decryptedPrivateKey: String =
        AndroidKeyStoreUtil.loadAndDecryptKey(encryptedPrivateKey)

    /* 3. 생성한 공개키로 DID 생성 */

    /* 4. 생성한 DID와 PublicKey를 Room에 저장*/

    private fun generateKeyPair(): KeyPair {
        val keyPair: KeyPair = KeyPairGenerator.getInstance("RSA").apply {
            initialize(2048)
        }.generateKeyPair()
        return keyPair
    }

    fun generateDID(pubKey: PublicKey): String {
        val generatedDid: String

        val message = pubKey.toString()
        val md = MessageDigest.getInstance("SHA-256")
        val genDid = md.digest(message.toByteArray())

        generatedDid = "did:sjbr:${Base64.encodeToString(genDid, Base64.NO_WRAP)}"
//        didViewModel.saveDID(generatedDid)
        /* - publicKey를 Base64로 encoding하여 생성*/

        return generatedDid
    }


    // ------ DID Auth 부분 ------
    /* 개인키를 활용하여 Message에 사인*/
    fun signMessage(privateKey: PrivateKey, message: String): String {

        val signature = Signature.getInstance("SHA256withRSA")
        signature.initSign(privateKey)
        signature.update(message.toByteArray())
        return signature.sign().toString()
    }

    /* 개인키로 사인받은 Message를 공개키로 검증*/
    fun verifySignature(
        publicKey: PublicKey,
        message: String,
        signature: ByteArray
    ): Boolean {
        val sig = Signature.getInstance("SHA256withRSA")
        sig.initVerify(publicKey)
        sig.update(message.toByteArray())
        return sig.verify(signature)
    }
}
