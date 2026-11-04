package com.js.smartvolt.asiste.model.data.local

import android.content.Context
import androidx.room.Room

object DatabaseProvider {
    private var db: AppDatabase? = null

    private fun database(context: Context): AppDatabase {
        if (db == null) {
            db = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "asiste.db"
            )
                .fallbackToDestructiveMigration(true)
                .fallbackToDestructiveMigrationOnDowngrade(true)
                .build()
        }
        return db!!
    }

    fun getAsistenciaDao(context: Context): AsistenciaDAO =
        database(context).asistenciaDao()

    fun getUsuarioDao(context: Context): UsuarioDAO =
        database(context).usuarioDao()

    // Compatibilidad: por defecto devuelve el DAO de asistencia
    fun getDao(context: Context): AsistenciaDAO = getAsistenciaDao(context)
}