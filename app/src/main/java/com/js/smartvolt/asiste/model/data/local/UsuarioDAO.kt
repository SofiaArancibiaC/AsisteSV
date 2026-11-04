package com.js.smartvolt.asiste.model.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.js.smartvolt.asiste.model.Usuario

@Dao
interface UsuarioDAO{
    @Query("SELECT * FROM usuarios WHERE correo = :correo AND passwordHash = :hash LIMIT 1")
    suspend fun login(correo: String, hash: String) : Usuario?

    @Query("SELECT COUNT(*) FROM usuarios WHERE correo = :correo")
    suspend fun existeCorreo(correo: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(usuario: Usuario)
}