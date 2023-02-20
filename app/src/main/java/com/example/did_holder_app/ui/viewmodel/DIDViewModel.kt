package com.example.did_holder_app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.did_holder_app.data.DIDRepository
import com.example.did_holder_app.data.DIDRepositoryImpl
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

}


