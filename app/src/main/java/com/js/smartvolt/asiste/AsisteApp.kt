package com.js.smartvolt.asiste

import android.app.Application
import android.content.Context
import com.js.smartvolt.asiste.model.data.local.DatabaseProvider
import com.js.smartvolt.asiste.model.data.local.sha256
import com.js.smartvolt.asiste.model.RecuperarUsuario
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.first
import org.osmdroid.config.Configuration

class AsisteApp : Application() {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()

        // osmdroid: se inicializa UNA sola vez, antes de dibujar el mapa
        Configuration.getInstance().load(
            this,
            getSharedPreferences("osmdroid", Context.MODE_PRIVATE)
        )
        Configuration.getInstance().userAgentValue =
            "$packageName (AsisteSV; contacto@serviciosvolt.cl)"

        // Seed de usuarios demo
        scope.launch {
            val dao = DatabaseProvider.getUsuarioDao(this@AsisteApp)
            try {
                val faenaDao = DatabaseProvider.getFaenaDao(this@AsisteApp)
                if (faenaDao.getAll().first().isEmpty()) {
                    faenaDao.upsert(com.js.smartvolt.asiste.model.admin.Faena(nombre = "Ruta 5 Norte", presentes = 42, total = 48))
                    faenaDao.upsert(com.js.smartvolt.asiste.model.admin.Faena(nombre = "Puente Maipo", presentes = 36, total = 40))
                    faenaDao.upsert(com.js.smartvolt.asiste.model.admin.Faena(nombre = "Planta Industrial", presentes = 50, total = 55))
                }
                if (dao.existeCorreo("tecnico@serviciosvolt.cl") == 0) {
                    dao.insertar(com.js.smartvolt.asiste.model.admin.Usuario(
                        correo = "tecnico@serviciosvolt.cl",
                        nombre = "Tecnico Demo",
                        passwordHash = sha256("123456"),
                        codigo = "OP-014",
                        rol = com.js.smartvolt.asiste.model.admin.Rol.OPERARIO,
                        faenaId = 1L,
                        activo = true
                    ))
                }
                if (dao.existeCorreo("admin@serviciosvolt.cl") == 0) {
                    dao.insertar(com.js.smartvolt.asiste.model.admin.Usuario(
                        correo = "admin@serviciosvolt.cl",
                        nombre = "Admin Demo",
                        passwordHash = sha256("admin123"),
                        codigo = "ADMIN-001",
                        rol = com.js.smartvolt.asiste.model.admin.Rol.ADMIN,
                        faenaId = 1L,
                        activo = true
                    ))
                }
            } catch (_: Exception) { }
        }
    }
}
