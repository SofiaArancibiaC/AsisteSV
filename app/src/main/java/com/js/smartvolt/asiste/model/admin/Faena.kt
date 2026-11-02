package com.js.smartvolt.asiste.model.admin

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "faena")
data class Faena(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nombre: String,
    val presentes: Int,
    val total: Int
    )