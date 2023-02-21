package com.example.did_holder_app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.did_holder_app.data.DIDRepositoryImpl
import com.example.did_holder_app.data.model.Blockchain.BlockchainResponse
import com.example.did_holder_app.data.model.DIDDocument.DidDocument
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response
import timber.log.Timber

class DIDViewModel(private val didRepository: DIDRepositoryImpl,) : ViewModel() {

    fun generateDidDocument() = viewModelScope.launch(Dispatchers.IO) {
        try {
            didRepository.generateDidDocument()
        } catch (e: Exception) {
            Timber.e(e)
        }
    }

    fun saveDidDocumentToBlockchain(
        didDocument: DidDocument,
        result: (Response<BlockchainResponse>) -> Unit,
    ) {
        viewModelScope.launch {
            didRepository.saveToBlockChain(didDocument, result)
        }
    }

}


