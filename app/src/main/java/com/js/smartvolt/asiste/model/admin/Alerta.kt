package com.js.smartvolt.asiste.model.admin

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalTime


enum class TipoMarca { ENTRADA, SALIDA}

@Entity(
    tableName = "alerta",
    foreignKeys = [
        ForeignKey(
            entity = Faena::class,
            parentColumns = ["id"],
            childColumns = ["faenaId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["faenaId"])]
)
data class Alerta(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val codigoOperario: String,
    val tipoMarca: TipoMarca,
    val hora: LocalTime,
    val faenaId: Long,
    val motivo: String,           // "ubicación simulada", "GPS sin baliza", etc.
    val estado: String            // "RECHAZADA" o "REVISION"
)
