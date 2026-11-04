package com.js.smartvolt.asiste.model.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.js.smartvolt.asiste.model.Asistencia
import com.js.smartvolt.asiste.model.Usuario

@Database(
    entities = [Asistencia::class, Usuario::class],
    version = 3,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun asistenciaDao(): AsistenciaDAO
    abstract fun usuarioDao(): UsuarioDAO
}