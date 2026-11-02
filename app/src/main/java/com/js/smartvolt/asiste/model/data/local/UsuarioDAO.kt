package com.js.smartvolt.asiste.model.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.js.smartvolt.asiste.model.admin.Usuario
import com.js.smartvolt.asiste.model.admin.UsuarioConFaena
import kotlinx.coroutines.flow.Flow

@Dao
interface UsuarioDao {
    @Query("SELECT * FROM usuario")
    fun getAll(): Flow<List<Usuario>>

    @Query("SELECT * FROM usuario WHERE id = :id")
    fun getById(id: Long): Flow<Usuario?>

    @Query("SELECT * FROM usuario WHERE activo = :activo")
    fun getByEstado(activo: Boolean): Flow<List<Usuario>>

    @Query("SELECT * FROM usuario WHERE rol = :rol")
    fun getByRol(rol: String): Flow<List<Usuario>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: Usuario): Long

    @Delete
    suspend fun delete(entity: Usuario)

    @Transaction
    @Query("SELECT * FROM usuario")
    fun getUsuariosConFaena(): Flow<List<UsuarioConFaena>>
}