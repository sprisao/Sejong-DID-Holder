package com.example.did_holder_app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.did_holder_app.data.DIDRepositoryImpl
import com.example.did_holder_app.data.model.DIDDocument.DidDocument
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

class DIDViewModel(private val didRepository: DIDRepositoryImpl) : ViewModel() {

        fun generateDidDocument() = viewModelScope.launch(Dispatchers.IO) {
            try {
                didRepository.generateDidDocument()
            } catch (e: Exception) {
                Timber.e(e)
            }
        }
    fun saveToBlockChain(didDocument: DidDocument) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                didRepository.saveToBlockChain(didDocument)
            } catch (e: Exception) {
                Timber.e(e)
            }
        }
    }

}


