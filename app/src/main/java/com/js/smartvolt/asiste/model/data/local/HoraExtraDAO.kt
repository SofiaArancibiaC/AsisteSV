package com.js.smartvolt.asiste.model.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.js.smartvolt.asiste.model.admin.HoraExtra
import kotlinx.coroutines.flow.Flow

@Dao
interface HoraExtraDao {
    @Query("SELECT * FROM hora_extra WHERE usuarioId = :usuarioId ORDER BY fecha DESC")
    fun getByUsuario(usuarioId: Long): Flow<List<HoraExtra>>

    @Query("SELECT * FROM hora_extra WHERE usuarioId = :usuarioId AND fecha = :fecha")
    fun getByUsuarioYFecha(usuarioId: Long, fecha: String): Flow<HoraExtra?>

    @Query("SELECT * FROM hora_extra WHERE estado = :estado")
    fun getByEstado(estado: String): Flow<List<HoraExtra>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: HoraExtra): Long

    @Delete
    suspend fun delete(entity: HoraExtra)
}
