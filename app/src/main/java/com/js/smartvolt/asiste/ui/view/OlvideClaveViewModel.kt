package com.js.smartvolt.asiste.ui.view

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.js.smartvolt.asiste.model.data.local.UsuarioDAO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface OlvideEstado {
    data object Idle : OlvideEstado
    data object Cargando : OlvideEstado
    data object Enviado : OlvideEstado
    data class Error(val mensaje: String) : OlvideEstado
}

class OlvideClaveViewModel(private val dao: UsuarioDAO) : ViewModel() {

    private val _estado = MutableStateFlow<OlvideEstado>(OlvideEstado.Idle)
    val estado: StateFlow<OlvideEstado> = _estado.asStateFlow()

    fun enviar(correo: String) {
        val limpio = correo.trim().lowercase()

        if (limpio.isBlank()) {
            _estado.value = OlvideEstado.Error("Ingresa tu correo")
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(limpio).matches()) {
            _estado.value = OlvideEstado.Error("Correo inválido")
            return
        }

        viewModelScope.launch {
            _estado.value = OlvideEstado.Cargando

            // En una app real aquí se llama al backend (POST /auth/forgot-password).
            // Con Room solo comprobamos si el correo está registrado.
            dao.existeCorreo(limpio)

            // Mensaje genérico a propósito: no revelamos si el correo existe
            // (evita la enumeración de usuarios).
            _estado.value = OlvideEstado.Enviado
        }
    }

    fun reset() {
        _estado.value = OlvideEstado.Idle
    }
}

class OlvideClaveViewModelFactory(private val dao: UsuarioDAO) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return OlvideClaveViewModel(dao) as T
    }
}
