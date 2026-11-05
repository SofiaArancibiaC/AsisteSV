package com.js.smartvolt.asiste.model.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.js.smartvolt.asiste.model.Asistencia
import com.js.smartvolt.asiste.model.RecuperarUsuario
import com.js.smartvolt.asiste.model.admin.Alerta
import com.js.smartvolt.asiste.model.admin.Faena
import com.js.smartvolt.asiste.model.admin.HoraExtra
import com.js.smartvolt.asiste.model.admin.Turno
import com.js.smartvolt.asiste.model.admin.Usuario

@Database(
    entities = [
        Asistencia::class,
        RecuperarUsuario::class,
        Usuario::class,
        Alerta::class,
        Turno::class,
        HoraExtra::class,
        Faena::class
    ],
    version = 4,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun asistenciaDao(): AsistenciaDAO
    abstract fun usuarioDao(): UsuarioDAO
    abstract fun faenaDao(): FaenaDao
}