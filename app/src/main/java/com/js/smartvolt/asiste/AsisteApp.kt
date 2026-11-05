package com.js.smartvolt.asiste

import android.app.Application
import android.content.Context
import com.js.smartvolt.asiste.model.RecuperarUsuario
import com.js.smartvolt.asiste.model.data.local.DatabaseProvider
import com.js.smartvolt.asiste.model.data.local.sha256
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
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

        // Seed de usuarios demo (igual que antes)
        scope.launch {
            val dao = DatabaseProvider.getUsuarioDao(this@AsisteApp)
            try {
                if (dao.existeCorreo("tecnico@serviciosvolt.cl") == 0) {
                    dao.insertar(
                        RecuperarUsuario(
                            correo = "tecnico@serviciosvolt.cl",
                            nombre = "Tecnico Demo",
                            passwordHash = sha256("123456")
                        )
                    )
                }
                if (dao.existeCorreo("admin@serviciosvolt.cl") == 0) {
                    dao.insertar(
                        RecuperarUsuario(
                            correo = "admin@serviciosvolt.cl",
                            nombre = "Admin Demo",
                            passwordHash = sha256("admin123")
                        )
                    )
                }
            } catch (_: Exception) {
            }
        }
    }
}
