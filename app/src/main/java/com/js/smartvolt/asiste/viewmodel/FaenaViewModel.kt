package com.js.smartvolt.asiste.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.js.smartvolt.asiste.model.admin.Faena
import com.js.smartvolt.asiste.model.data.local.FaenaDao
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FaenaViewModel(private val dao: FaenaDao) : ViewModel() {
    val faenas: StateFlow<List<Faena>> =
        dao.getAll().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun agregar(faena: Faena) {
        viewModelScope.launch { dao.upsert(faena) }
    }
}

class FaenaViewModelFactory(private val dao: FaenaDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return FaenaViewModel(dao) as T
    }
}