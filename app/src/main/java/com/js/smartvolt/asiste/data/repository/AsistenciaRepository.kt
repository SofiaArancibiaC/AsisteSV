package com.js.smartvolt.asiste.data.repository

import com.js.smartvolt.asiste.model.Asistencia
import com.js.smartvolt.asiste.model.data.local.AsistenciaDAO
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

class AsistenciaRepository(private val dao: AsistenciaDAO) {
    fun getByRange(start: LocalDate, end: LocalDate): Flow<List<Asistencia>> = dao.getByRange(start, end)
    suspend fun getByFecha(fecha: LocalDate): List<Asistencia> = dao.getByFecha(fecha)
    fun getByUsuario(usuarioId: Long): Flow<List<Asistencia>> = dao.getByUsuario(usuarioId)
    suspend fun upsert(asistencia: Asistencia) = dao.upsert(asistencia)
    suspend fun delete(id: Long) = dao.delete(id)
    fun getAll(): Flow<List<Asistencia>> = dao.getAll()
}
