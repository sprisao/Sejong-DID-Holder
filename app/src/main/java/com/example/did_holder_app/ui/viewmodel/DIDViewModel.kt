package com.example.did_holder_app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.did_holder_app.data.repository.DIDRepositoryImpl
import com.example.did_holder_app.data.datastore.DidDataStore
import com.example.did_holder_app.data.model.Blockchain.BlockchainResponse
import com.example.did_holder_app.data.model.DIDDocument.DidDocument
import com.example.did_holder_app.data.model.VC.*
import kotlinx.coroutines.Dispatchers
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
    val vc: Flow<VcResponse?> = dataStore.vcFlow
    val userSeq: Flow<Int?> = dataStore.userSeqFlow

    // DID Document 생성
    fun generateDidDocument() = viewModelScope.launch(Dispatchers.IO) {
        try {
            didRepository.generateDidDocument()
        } catch (e: Exception) {
            Timber.e(e)
        }
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
            didRepository.requestVC(request, result)
        }
    }


    // VC삭제
    fun clearVc() {
        viewModelScope.launch {
            dataStore.clearVc()
        }
    }



}


