package com.js.smartvolt.asiste

import android.app.Application
import com.js.smartvolt.asiste.model.data.local.DatabaseProvider
import com.js.smartvolt.asiste.model.data.local.sha256
import com.js.smartvolt.asiste.model.RecuperarUsuario
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.first

class AsisteApp : Application() {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
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
                    dao.insertar(RecuperarUsuario(correo = "tecnico@serviciosvolt.cl", nombre = "Tecnico Demo", passwordHash = sha256("123456"), codigoUsuario = "OP-014"))
                    dao.insertarUsuario(com.js.smartvolt.asiste.model.admin.Usuario(codigo = "OP-014", rol = com.js.smartvolt.asiste.model.admin.Rol.OPERARIO, faenaId = 1L, activo = true))
                }
                if (dao.existeCorreo("admin@serviciosvolt.cl") == 0) {
                    dao.insertar(RecuperarUsuario(correo = "admin@serviciosvolt.cl", nombre = "Admin Demo", passwordHash = sha256("admin123")))
                }
            } catch (_: Exception) { }
        }
    }
}