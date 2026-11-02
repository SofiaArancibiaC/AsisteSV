package com.js.smartvolt.asiste.model.data.local

import android.content.Context
import androidx.room.Room

// Singleton: solo existe una instancia de la base de datos en toda la app.
// Se encarga de crearla la primera vez y reutilizarla después.
object DatabaseProvider {
    // Guarda la BD en memoria. Es null hasta que se crea por primera vez.
    private var db: AsisteDatabase? = null

    // Devuelve el DAO de Asistencia. Si la BD no existe aún, la crea.
    fun getDao(context: Context): AsistenciaDAO {
        if (db == null) {
            // Crea la BD con Room:
            // - context.applicationContext: contexto de la app (evita fugas de memoria)
            // - AppDatabase::class.java: clase que define la BD
            // - "asistencia.db": nombre del archivo de la BD en el dispositivo
            db = Room.databaseBuilder(
                        context.applicationContext,
                        AsisteDatabase::class.java,
                        "asistencia.db"
                    )
                    // Si cambia la versión de la BD, borra los datos y recrea (útil en desarrollo)
                    .fallbackToDestructiveMigration(true)
                    // Igual pero cuando la versión baja (downgrade)
                    .fallbackToDestructiveMigrationOnDowngrade(true)
                    .build()
        }
        // Devuelve el DAO que permite hacer consultas (insertar, leer, borrar asistencias)
        return db!!.asistenciaDao()
    }
}