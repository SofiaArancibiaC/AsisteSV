package com.js.smartvolt.asiste.data.repository

import com.js.smartvolt.asiste.model.admin.Alerta
import com.js.smartvolt.asiste.model.data.local.AlertaDao
import kotlinx.coroutines.flow.Flow

class AlertaRepository(private val dao: AlertaDao) {
    fun getAll(): Flow<List<Alerta>> = dao.getAll()
    fun getByEstado(estado: String): Flow<List<Alerta>> = dao.getByEstado(estado)
    fun getByFaena(faenaId: Long): Flow<List<Alerta>> = dao.getByFaena(faenaId)
    suspend fun upsert(alerta: Alerta): Long = dao.upsert(alerta)
    suspend fun delete(alerta: Alerta) = dao.delete(alerta)
}