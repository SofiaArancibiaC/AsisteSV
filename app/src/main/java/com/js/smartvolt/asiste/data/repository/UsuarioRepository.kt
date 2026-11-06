package com.js.smartvolt.asiste.data.repository

import com.js.smartvolt.asiste.model.admin.Usuario
import com.js.smartvolt.asiste.model.data.local.UsuarioDAO
import kotlinx.coroutines.flow.Flow

class UsuarioRepository(private val dao: UsuarioDAO) {
    fun getAll(): Flow<List<Usuario>> = dao.getAll()
    suspend fun getByCodigo(codigo: String): Usuario? = dao.getByCodigo(codigo)
    fun getByRol(rol: com.js.smartvolt.asiste.model.admin.Rol): Flow<List<Usuario>> = dao.getByRol(rol)
    suspend fun existeCorreo(correo: String): Int = dao.existeCorreo(correo)
    suspend fun insertar(usuario: Usuario) = dao.insertar(usuario)
    suspend fun delete(usuario: Usuario) = dao.delete(usuario)
}