package com.js.smartvolt.asiste.model.admin

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.LocalTime


@Entity(tableName = "turno",
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
data class Turno(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val usuarioId: Long,
    val dia: LocalDate,
    val activo: Boolean,
    val horario: LocalTime

)