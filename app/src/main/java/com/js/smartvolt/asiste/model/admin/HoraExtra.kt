package com.js.smartvolt.asiste.model.admin

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDate


enum class EstadoHorasExtras{ PENDIENTE, APROBADO, RECHAZADO }
@Entity(tableName = "hora_extra",
        foreignKeys = [
        ForeignKey(
            entity = Usuario::class,
            parentColumns = ["id"],
            childColumns = ["usuarioId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["usuarioId"])]
)
data class HoraExtra(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val usuarioId: Long,
    val fecha: LocalDate,
    val horas: Double,
    val motivo: String,
    val estado: EstadoHorasExtras,
    val nota: String

)