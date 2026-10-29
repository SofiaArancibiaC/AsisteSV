package com.js.smartvolt.asiste.model.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.js.smartvolt.asiste.model.Asistencia

@Database(
    entities = [Asistencia::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun asistenciaDao(): AsistenciaDAO
}