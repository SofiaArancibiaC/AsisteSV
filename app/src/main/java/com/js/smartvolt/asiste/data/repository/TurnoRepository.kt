package com.js.smartvolt.asiste.data.repository

import com.js.smartvolt.asiste.model.admin.Turno
import com.js.smartvolt.asiste.model.data.local.TurnoDAO
import kotlinx.coroutines.flow.Flow

class TurnoRepository(private val dao: TurnoDAO) {
    fun getAll(): Flow<List<Turno>> = dao.getAll()
    fun getByUsuario(usuarioId: Long): Flow<List<Turno>> = dao.getByUsuario(usuarioId)
    fun getByUsuarioYDia(usuarioId: Long, dia: String): Flow<Turno?> = dao.getByUsuarioYDia(usuarioId, dia)
    suspend fun upsert(turno: Turno): Long = dao.upsert(turno)
    suspend fun upsertAll(turnos: List<Turno>) = dao.upsertAll(turnos)
    suspend fun delete(turno: Turno) = dao.delete(turno)
    suspend fun deleteByUsuario(usuarioId: Long) = dao.deleteByUsuario(usuarioId)
}
