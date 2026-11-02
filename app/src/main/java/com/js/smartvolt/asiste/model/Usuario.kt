package com.js.smartvolt.asiste.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "usuarios")
data class Usuario(
    @PrimaryKey val correo: String,
    val nombre : String,
    val passwordHash : String
)
