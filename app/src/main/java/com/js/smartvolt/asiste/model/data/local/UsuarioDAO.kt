package com.js.smartvolt.asiste.model.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.js.smartvolt.asiste.model.admin.Usuario
import com.js.smartvolt.asiste.model.admin.Rol
import com.js.smartvolt.asiste.model.admin.UsuarioConFaena
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface UsuarioDAO {
    @Query("SELECT * FROM usuario WHERE correo = :correo AND passwordHash = :hash LIMIT 1")
    suspend fun login(correo: String, hash: String): Usuario?

    @Query("SELECT * FROM usuario")
    fun getAll(): Flow<List<Usuario>>

    @Query("SELECT * FROM usuario WHERE id = :id")
    fun getById(id: Long): Flow<Usuario?>

    @Query("SELECT * FROM usuario WHERE codigo = :codigo LIMIT 1")
    suspend fun getByCodigo(codigo: String): Usuario?

    @Query("SELECT COUNT(*) FROM usuario WHERE correo = :correo")
    suspend fun existeCorreo(correo: String): Int

    @Query("SELECT * FROM usuario WHERE activo = :activo")
    fun getByEstado(activo: Boolean): Flow<List<Usuario>>

    @Query("SELECT * FROM usuario WHERE rol = :rol")
    fun getByRol(rol: Rol): Flow<List<Usuario>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(usuario: Usuario)

    @Delete
    suspend fun delete(entity: Usuario)

    @Transaction
    @Query("SELECT * FROM usuario")
    fun getUsuariosConFaena(): Flow<List<UsuarioConFaena>>
}
