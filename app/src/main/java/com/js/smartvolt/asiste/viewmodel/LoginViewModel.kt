package com.js.smartvolt.asiste.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.js.smartvolt.asiste.data.repository.AuthRepository
import com.js.smartvolt.asiste.session.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface LoginEstado {
    data object Idle : LoginEstado
    data object Cargando : LoginEstado
    data class Exito(val codigo: String?) : LoginEstado
    data class Error(val mensaje: String) : LoginEstado
}

class LoginViewModel(private val auth: AuthRepository) : ViewModel() {

    private val _estado = MutableStateFlow<LoginEstado>(LoginEstado.Idle)
    val estado: StateFlow<LoginEstado> = _estado.asStateFlow()

    fun login(correo: String, pass: String) {
        val correoLimpio = correo.trim()
        if (correoLimpio.isBlank() || pass.isBlank()) {
            _estado.value = LoginEstado.Error("Completa correo y contraseña")
            return
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(correoLimpio).matches()) {
            _estado.value = LoginEstado.Error("Correo inválido")
            return
        }
        viewModelScope.launch {
            _estado.value = LoginEstado.Cargando
            try {
                val usuario = auth.login(correo.trim().lowercase(), pass)
                if (usuario != null) {
                    SessionManager.set(usuario)
                    _estado.value = LoginEstado.Exito(usuario.codigo)
                } else {
                    _estado.value = LoginEstado.Error("Correo o contraseña incorrectos")
                }
            } catch (e: Exception) {
                _estado.value = LoginEstado.Error("Error interno: ${e.message}")
            }
        }
    }

    fun limpiarEstado() {
        _estado.value = LoginEstado.Idle
    }
}

class LoginViewModelFactory(private val auth: AuthRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return LoginViewModel(auth) as T
    }
}
