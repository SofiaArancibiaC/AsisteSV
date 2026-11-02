package com.js.smartvolt.asiste.model.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.js.smartvolt.asiste.model.Asistencia


// Clase principal de Room: define qué tablas (entidades) tiene la BD y su versión.
// Hereda de RoomDatabase, que es la clase base de Room para bases de datos.
@Database(
    entities = [Asistencia::class], // tablas de la base de datos (aquí solo Asistencia)
    version = 2, // versión de la BD; si cambias estructura, súbela y migra
    exportSchema = false // no exporta el esquema a archivos (simplifica el proyecto)
)
@TypeConverters(Converters::class) // convierte tipos no soportados por SQLite (ej. fechas)
abstract class AsisteDatabase : RoomDatabase() {

    // Room implementa automáticamente este metodo: devuelve el DAO para acceder a la tabla Asistencia
    abstract fun asistenciaDao(): AsistenciaDAO
}