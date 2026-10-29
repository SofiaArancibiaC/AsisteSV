package com.js.smartvolt.asiste.model.data.local

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.TypeConverter
import java.time.LocalDate
import java.time.LocalTime

class Converters {
    @TypeConverter
    fun fromLocalDate(value: LocalDate?): String? = value?.toString()

    @RequiresApi(Build.VERSION_CODES.O)
    @TypeConverter
    fun toLocalDate(value: String?): LocalDate? = value?.let { LocalDate.parse(it) }

    @TypeConverter
    fun fromLocalTime(value: LocalTime?): String? = value?.toString()

    @RequiresApi(Build.VERSION_CODES.O)
    @TypeConverter
    fun toLocalTime(value: String?): LocalTime? = value?.let { LocalTime.parse(it) }
}