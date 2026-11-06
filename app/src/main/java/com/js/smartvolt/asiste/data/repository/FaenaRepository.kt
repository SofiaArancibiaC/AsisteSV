package com.js.smartvolt.asiste.data.repository

import com.js.smartvolt.asiste.model.admin.Faena
import com.js.smartvolt.asiste.model.data.local.FaenaDao
import kotlinx.coroutines.flow.Flow

class FaenaRepository(private val dao: FaenaDao) {
    fun getAll(): Flow<List<Faena>> = dao.getAll()
    fun getById(id: Long): Flow<Faena?> = dao.getById(id)
    suspend fun upsert(faena: Faena): Long = dao.upsert(faena)
    suspend fun delete(faena: Faena) = dao.delete(faena)
}
