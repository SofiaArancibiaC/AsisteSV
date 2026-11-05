package com.js.smartvolt.asiste.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.LocalTime

enum class EstadoAsistencia{ CUMPLIDA, ATRASADA, FALTA, EN_REVISION, RECHAZADA}

@Entity(tableName = "asistencia")
data class Asistencia(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val fecha: LocalDate,
    val estado: EstadoAsistencia,
    val inicioTurno: LocalTime,
    val finTurno: LocalTime? = null,
    val registroSalida: LocalTime? = null,
    val latitud: Double? = null,
    val longitud: Double? = null,
    val registro: LocalTime? = null,
    val usuarioId: Long? = null
)