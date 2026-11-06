package com.js.smartvolt.asiste.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.js.smartvolt.asiste.data.repository.FaenaRepository
import com.js.smartvolt.asiste.data.repository.UsuarioRepository
import com.js.smartvolt.asiste.model.admin.Usuario
import com.js.smartvolt.asiste.model.admin.Faena
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PerfilViewModel(
    private val usuarioRepo: UsuarioRepository,
    private val faenaRepo: FaenaRepository
) : ViewModel() {
    private val _usuario = MutableStateFlow<Usuario?>(null)
    val usuario: StateFlow<Usuario?> = _usuario.asStateFlow()
    private val _faenaNombre = MutableStateFlow<String?>(null)
    val faenaNombre: StateFlow<String?> = _faenaNombre.asStateFlow()

    fun cargar(codigo: String) {
        viewModelScope.launch {
            val u = usuarioRepo.getByCodigo(codigo)
            _usuario.value = u
            if (u != null) {
                faenaRepo.getById(u.faenaId).collect { f ->
                    _faenaNombre.value = f?.nombre
                }
            }
        }
    }
}

class PerfilViewModelFactory(
    private val usuarioRepo: UsuarioRepository,
    private val faenaRepo: FaenaRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return PerfilViewModel(usuarioRepo, faenaRepo) as T
    }
}