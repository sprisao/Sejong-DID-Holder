package com.example.did_holder_app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.did_holder_app.data.DIDRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

class DIDViewModel(private val didRepository: DIDRepository) : ViewModel() {

    fun saveDID(did: String) = viewModelScope.launch(Dispatchers.IO) {
        Timber.d("saveDID: $did")
        didRepository.saveDID(did)
    }

    fun getDID() = viewModelScope.launch(Dispatchers.IO) {
        didRepository.getDID()
    }

    fun savePublicKey(publicKey: String) = viewModelScope.launch(Dispatchers.IO) {
        didRepository.savePublicKey(publicKey)
    }

    fun getPublicKey() = viewModelScope.launch(Dispatchers.IO) {
        didRepository.getPublicKey()
    }
}


