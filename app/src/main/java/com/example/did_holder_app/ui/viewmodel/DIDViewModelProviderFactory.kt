package com.example.did_holder_app.ui.viewmodel

import android.os.Bundle
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import com.example.did_holder_app.data.DIDRepository

@Suppress("UNCHECKED_CAST")
class DIDViewModelProviderFactory(
    private val didRepository: DIDRepository,
    owner: SavedStateRegistryOwner,
    defaultArgs: Bundle? = null
) : AbstractSavedStateViewModelFactory(owner, defaultArgs) {
    override fun <T : ViewModel> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
    ): T {
        if (modelClass.isAssignableFrom(DIDViewModel::class.java)) {
            return DIDViewModel(didRepository) as T
        }
        throw IllegalArgumentException("ViewModel class not found")
    }


}
