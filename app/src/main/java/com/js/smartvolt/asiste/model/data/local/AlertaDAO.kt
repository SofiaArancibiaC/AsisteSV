package com.js.smartvolt.asiste.model.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.js.smartvolt.asiste.model.admin.Alerta
import com.js.smartvolt.asiste.model.admin.AlertaConFaena
import kotlinx.coroutines.flow.Flow

@Dao
interface AlertaDao {
    @Query("SELECT * FROM alerta ORDER BY id DESC")
    fun getAll(): Flow<List<Alerta>>

    @Query("SELECT * FROM alerta WHERE estado = :estado")
    fun getByEstado(estado: String): Flow<List<Alerta>>

    @Query("SELECT * FROM alerta WHERE faenaId = :faenaId")
    fun getByFaena(faenaId: Long): Flow<List<Alerta>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: Alerta): Long

    @Delete
    suspend fun delete(entity: Alerta)

    @Transaction
    @Query("SELECT * FROM alerta ORDER BY id DESC")
    fun getAlertasConFaena(): Flow<List<AlertaConFaena>>
}
