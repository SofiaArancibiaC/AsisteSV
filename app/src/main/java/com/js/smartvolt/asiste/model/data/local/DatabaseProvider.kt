package com.js.smartvolt.asiste.model.data.local

import android.content.Context
import androidx.room.Room

object DatabaseProvider {
    @Volatile
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

    fun getAsistenciaDao(context: Context): AsistenciaDAO = database(context).asistenciaDao()

    fun getUsuarioDao(context: Context): UsuarioDAO =
        database(context).usuarioDao()

    fun getFaenaDao(context: Context): FaenaDao =
        database(context).faenaDao()

    fun getHoraExtraDao(context: Context): HoraExtraDao =
        database(context).horaExtraDao()

    fun getTurnoDao(context: Context): TurnoDAO =
        database(context).turnoDao()
    fun getAlertaDao(context: Context): AlertaDao =
        database(context).alertaDao()
    // Compatibilidad: por defecto devuelve el DAO de asistencia
    fun getDao(context: Context): AsistenciaDAO = getAsistenciaDao(context)
}
