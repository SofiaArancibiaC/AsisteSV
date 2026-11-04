package com.js.smartvolt.asiste.model.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.js.smartvolt.asiste.model.admin.Faena
import kotlinx.coroutines.flow.Flow

@Dao
interface FaenaDao {
    @Query("SELECT * FROM faena")
    fun getAll(): Flow<List<Faena>>

    @Query("SELECT * FROM faena WHERE id = :id")
    fun getById(id: Long): Flow<Faena?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: Faena): Long

    @Delete
    suspend fun delete(entity: Faena)
}