package com.js.smartvolt.asiste.data.repository

import com.js.smartvolt.asiste.model.admin.HoraExtra
import com.js.smartvolt.asiste.model.data.local.HoraExtraDao
import kotlinx.coroutines.flow.Flow

class HoraExtraRepository(private val dao: HoraExtraDao) {
    fun getByUsuario(usuarioId: Long): Flow<List<HoraExtra>> = dao.getByUsuario(usuarioId)
    fun getByUsuarioYFecha(usuarioId: Long, fecha: String): Flow<HoraExtra?> = dao.getByUsuarioYFecha(usuarioId, fecha)
    fun getByEstado(estado: String): Flow<List<HoraExtra>> = dao.getByEstado(estado)
    suspend fun upsert(horaExtra: HoraExtra): Long = dao.upsert(horaExtra)
    suspend fun delete(horaExtra: HoraExtra) = dao.delete(horaExtra)
}
