package com.js.smartvolt.asiste.model.admin

import androidx.room.Embedded
import androidx.room.Relation

data class AlertaConFaena(
    @Embedded val alerta: Alerta,
    @Relation(parentColumn = "faenaId", entityColumn = "id")
    val faena: Faena
)
