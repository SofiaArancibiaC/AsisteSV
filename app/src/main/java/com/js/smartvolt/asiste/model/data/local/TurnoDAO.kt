package com.js.smartvolt.asiste.model.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.js.smartvolt.asiste.model.admin.Turno
import kotlinx.coroutines.flow.Flow

@Dao
interface TurnoDAO {
    @Query("SELECT * FROM turno WHERE usuarioId = :usuarioId")
    fun getByUsuario(usuarioId: Long): Flow<List<Turno>>

    @Query("SELECT * FROM turno")
    fun getAll(): Flow<List<Turno>>

    @Query("SELECT * FROM turno WHERE usuarioId = :usuarioId AND dia = :dia")
    fun getByUsuarioYDia(usuarioId: Long, dia: String): Flow<Turno?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: Turno): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(entities: List<Turno>)

    @Delete
    suspend fun delete(entity: Turno)

    @Query("DELETE FROM turno WHERE usuarioId = :usuarioId")
    suspend fun deleteByUsuario(usuarioId: Long)
}
