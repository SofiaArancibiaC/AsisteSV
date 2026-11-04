package com.js.smartvolt.asiste.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.js.smartvolt.asiste.model.RecuperarUsuario
import com.js.smartvolt.asiste.model.data.local.UsuarioDAO
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class UsuarioViewModel(private val dao: UsuarioDAO) : ViewModel() {
    val usuarios: StateFlow<List<RecuperarUsuario>> =
        dao.getAll().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun agregar(usuario: RecuperarUsuario) {
        viewModelScope.launch { dao.insertar(usuario) }
    }
}

class UsuarioViewModelFactory(private val dao: UsuarioDAO) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return UsuarioViewModel(dao) as T
    }
}