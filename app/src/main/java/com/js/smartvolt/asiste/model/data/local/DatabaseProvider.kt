package com.js.smartvolt.asiste.model.data.local

import android.content.Context
import androidx.room.Room

object DatabaseProvider {
    private var db: AppDatabase? = null

    fun getDao(context: Context): AsistenciaDAO {
        if (db == null) {
            db = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "asistencia.db"
            ).build()
        }
        return db!!.asistenciaDao()
    }
}