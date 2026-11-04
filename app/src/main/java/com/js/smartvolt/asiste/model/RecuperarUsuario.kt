package com.js.smartvolt.asiste.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recupera_usuario")
data class RecuperarUsuario(
    @PrimaryKey val correo: String,
    val nombre : String,
    val passwordHash : String
)
