package com.js.smartvolt.asiste.model.admin

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

enum class Rol { OPERARIO, CAPATAZ, ADMIN }

@Entity(
    tableName = "usuario",
    foreignKeys = [
        ForeignKey(
            entity = Faena::class,
            parentColumns = ["id"],
            childColumns = ["faenaId"],
            onDelete = ForeignKey.CASCADE,   // si se borra la faena, se borran sus usuarios
            onUpdate = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["faenaId"])]
)
data class Usuario(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val correo: String,
    val nombre: String,
    val passwordHash: String,
    val codigo: String,
    val rol: Rol,
    val faenaId: Long,
    val activo: Boolean = true
)
