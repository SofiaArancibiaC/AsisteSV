package com.js.smartvolt.asiste.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.js.smartvolt.asiste.model.data.local.UsuarioDAO
import com.js.smartvolt.asiste.model.data.local.sha256
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface LoginEstado{
    data object Idle : LoginEstado
    data object Cargando : LoginEstado
    data object Exito : LoginEstado
    data class Error(val mensaje: String) : LoginEstado
}

class LoginViewModel(private val dao: UsuarioDAO): ViewModel(){
    private val _estado= MutableStateFlow<LoginEstado>(LoginEstado.Idle)

    val estado: StateFlow<LoginEstado> = _estado.asStateFlow()

    fun login(correo: String, pass: String){
        if (correo.isBlank() || pass.isBlank()){
            _estado.value= LoginEstado.Error("Completa correo y contraseña")
            return
        }
        viewModelScope.launch {
            _estado.value = LoginEstado.Cargando
            val usuario = dao.login(correo.trim().lowercase(), sha256(pass))
            _estado.value = if (usuario != null) {
                LoginEstado.Exito
            } else {
                LoginEstado.Error("Correo o contraseña incorrectos")
            }
        }

        }
    }

class LoginViewModelFactory(private val dao: UsuarioDAO) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return LoginViewModel(dao) as T
    }
}
