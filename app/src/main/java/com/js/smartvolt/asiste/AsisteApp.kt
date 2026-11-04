package com.js.smartvolt.asiste

import android.app.Application
import com.js.smartvolt.asiste.model.data.local.DatabaseProvider
import com.js.smartvolt.asiste.model.data.local.sha256
import com.js.smartvolt.asiste.model.RecuperarUsuario
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class AsisteApp : Application() {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        scope.launch {
            val dao = DatabaseProvider.getUsuarioDao(this@AsisteApp)
            try {
                if (dao.existeCorreo("tecnico@serviciosvolt.cl") == 0) {
                    dao.insertar(RecuperarUsuario(correo = "tecnico@serviciosvolt.cl", nombre = "Tecnico Demo", passwordHash = sha256("123456")))
                }
                if (dao.existeCorreo("admin@serviciosvolt.cl") == 0) {
                    dao.insertar(RecuperarUsuario(correo = "admin@serviciosvolt.cl", nombre = "Admin Demo", passwordHash = sha256("admin123")))
                }
            } catch (_: Exception) { }
        }
    }
}