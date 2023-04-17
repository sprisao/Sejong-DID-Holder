package com.example.did_holder_app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.did_holder_app.data.api.VpResponse
import com.example.did_holder_app.data.datastore.DidDataStore
import com.example.did_holder_app.data.model.Blockchain.BlockchainResponse
import com.example.did_holder_app.data.model.DIDDocument.DidDocument
import com.example.did_holder_app.data.model.VC.*
import com.example.did_holder_app.data.repository.DIDRepositoryImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import retrofit2.Response
import timber.log.Timber

class DIDViewModel(
    private val didRepository: DIDRepositoryImpl,
    private val dataStore: DidDataStore
) :
    ViewModel() {
    val didDocument: Flow<DidDocument?> = dataStore.didDocumentFlow
    val vc: Flow<VcData?> = dataStore.vcFlow
    val userSeq: Flow<Int?> = dataStore.userSeqFlow
    val isDidSaved: Flow<Boolean?> = dataStore.isDidSavedFlow

    // DID Document 생성
    fun generateDidDocument(onComplete: () -> Unit) = viewModelScope.launch(Dispatchers.IO) {
            delay(3500)
        try {
            didRepository.generateDidDocument()
        } catch (e: Exception) {
            Timber.e(e)
        }
        onComplete()
    }

    // Blockchain에 DID Document를 저장
    fun saveDidDocumentToBlockchain(
        didDocument: DidDocument,
        result: (Response<BlockchainResponse>) -> Unit,
    ) {
        viewModelScope.launch {
            didRepository.saveToBlockChain(didDocument, result)
        }
    }

    // DataStore 내 DID Document 삭제
    fun clearDidDocument() {
        viewModelScope.launch {
            dataStore.clearDidDocument()
        }
    }

    // SignUp 회원가입
    fun signUpUser(
        request: SignUpRequest,
        result: (Response<SignUpResponse>) -> Unit,
    ) {
        viewModelScope.launch {
            didRepository.signUpUser(request, result)
        }
    }

    // SignIn 로그임
    fun signInUser(
        request: SignInRequest,
        result: (Response<SignInResponse>) -> Unit,
    ) {
        viewModelScope.launch {
            didRepository.signInUser(request, result)
        }
    }

    // Delete UserSeq 로그아웃
    fun clearUserSeq() {
        viewModelScope.launch {
            dataStore.clearUserSeq()
        }
    }

    // VC 발급 요청
    fun requestVC(request: VCRequest, result: (Response<VcResponse>) -> Unit) {
        viewModelScope.launch {
            delay(3000)
            didRepository.requestVC(request, result)
        }
    }


    // VC삭제
    fun clearVc() {
        viewModelScope.launch {
            dataStore.clearVc()
        }
    }



    fun generateVP(challenge: String) {
        viewModelScope.launch {
            didRepository.generateVP(challenge)
            delay(1000)
        }
    }

    fun verifyVP(result: (Response<VpResponse>) -> Unit) {
        viewModelScope.launch {
            delay(2000)
            didRepository.verifyVP(result)
        }
    }

    fun saveIsDidSaved(isDidSaved: Boolean) {
        viewModelScope.launch {
            dataStore.saveIsDidSaved(isDidSaved)
        }
    }

    fun clearIsDidSaved() {
        viewModelScope.launch {
            dataStore.clearIsDidSaved()
        }
    }

}


