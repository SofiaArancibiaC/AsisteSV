package com.js.smartvolt.asiste.model.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.js.smartvolt.asiste.model.Asistencia
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface AsistenciaDAO {
    @Query("SELECT * FROM asistencia WHERE fecha BETWEEN :start AND :end")
    fun getByRange(start: LocalDate, end: LocalDate): Flow<List<Asistencia>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: Asistencia)

    @Query("DELETE FROM asistencia WHERE id = :id")
    suspend fun delete(id: Long)
}