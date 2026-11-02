package com.js.smartvolt.asiste.model.admin

import androidx.room.Embedded
import androidx.room.Relation

data class UsuarioConFaena(
    @Embedded val usuario: Usuario,
    @Relation(
        parentColumn = "faenaId",   // columna de UsuarioEntity
        entityColumn = "id"         // columna de FaenaEntity
    )
    val faena: Faena
)