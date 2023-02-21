package com.example.did_holder_app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.did_holder_app.data.DIDRepositoryImpl
import com.example.did_holder_app.data.model.DIDDocument.DidDocument
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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
        didDocument: DidDocument
    ) {
        viewModelScope.launch {
            didRepository.saveToBlockChain(didDocument, object : DIDRepositoryImpl.SaveToBlockChainCallback {
                override fun onSuccess() {
                    // Handle success
                    Timber.d("Success")
                }
                override fun onError(error: String) {
                    // Handle error
                    Timber.d("Error: $error")
                }
            })
        }
    }

}


