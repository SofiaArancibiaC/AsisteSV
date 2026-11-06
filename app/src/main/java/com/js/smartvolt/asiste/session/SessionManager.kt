package com.js.smartvolt.asiste.session

import com.js.smartvolt.asiste.model.admin.Rol
import com.js.smartvolt.asiste.model.admin.Usuario

object SessionManager {
    var usuario: Usuario? = null
    val codigo: String? get() = usuario?.codigo
    val rol: Rol? get() = usuario?.rol
    fun set(u: Usuario) { usuario = u }
    fun clear() { usuario = null }
}