package com.js.smartvolt.asiste.model.data.local

import android.content.Context
import androidx.room.Room

object DatabaseProvider {
    private var db: AppDatabase? = null

    private fun getDb(context: Context): AppDatabase {
        if (db == null) {
            db = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "asistencia.db"
            )
                .fallbackToDestructiveMigration(dropAllTables = true)
                .build()
                        context.applicationContext,
                        AppDatabase::class.java,
                        "asistencia.db"
                    ).fallbackToDestructiveMigration(true).fallbackToDestructiveMigrationOnDowngrade(true).build()
        }
        return db!!
    }
    fun getDao(context: Context): AsistenciaDAO= getDb(context).asistenciaDao()

    fun getUsuarioDao(context: Context): UsuarioDAO= getDb(context).usuarioDao()
}