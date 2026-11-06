package com.js.smartvolt.asiste.data.repository

import com.js.smartvolt.asiste.model.admin.Usuario
import com.js.smartvolt.asiste.model.data.local.UsuarioDAO
import com.js.smartvolt.asiste.model.data.local.sha256

class AuthRepository(private val dao: UsuarioDAO) {
    suspend fun login(correo: String, pass: String): Usuario? =
        dao.login(correo.trim().lowercase(), sha256(pass))
}
