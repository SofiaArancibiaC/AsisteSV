package com.js.smartvolt.asiste.model

import java.time.LocalDate
import java.time.LocalTime
enum class EstadoAsistencia{ CUMPLIDA, ATRASADA, FALTA}

data class Asistencia(
    val fecha: LocalDate,
    val estado: EstadoAsistencia,
    val inicioTurno: LocalTime,
    val registro: LocalTime? = null   // null si falta
)