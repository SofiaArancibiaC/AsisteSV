package com.js.smartvolt.asiste

import android.os.Bundle
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.js.smartvolt.asiste.model.Asistencia
import com.js.smartvolt.asiste.model.EstadoAsistencia
import com.js.smartvolt.asiste.model.Usuario
import com.js.smartvolt.asiste.model.data.local.DatabaseProvider
import com.js.smartvolt.asiste.model.data.local.sha256
import com.js.smartvolt.asiste.ui.theme.AsisteSVTheme
import com.js.smartvolt.asiste.ui.view.LoginEstado
import com.js.smartvolt.asiste.ui.view.LoginView
import com.js.smartvolt.asiste.ui.view.LoginViewModel
import com.js.smartvolt.asiste.ui.view.LoginViewModelFactory
import com.js.smartvolt.asiste.ui.view.MainView
import com.js.smartvolt.asiste.ui.view.OlvideClaveViewModel
import com.js.smartvolt.asiste.ui.view.OlvideClaveViewModelFactory
import com.js.smartvolt.asiste.ui.view.OlvideContrasenaView
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime

class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Datos de prueba: siembra asistencias de ejemplo una sola vez
        val dao = DatabaseProvider.getDao(this)
        lifecycleScope.launch {
            val list = dao.getByRange(
                LocalDate.now().minusMonths(2),
                LocalDate.now().plusMonths(2)
            ).first()
            if (list.isEmpty()) {
                val hoy = LocalDate.now()
                val samples = listOf(
                    Asistencia(fecha = hoy.minusDays(1), estado = EstadoAsistencia.CUMPLIDA, inicioTurno = LocalTime.of(8, 0), registro = LocalTime.of(7, 55)),
                    Asistencia(fecha = hoy.minusDays(2), estado = EstadoAsistencia.ATRASADA, inicioTurno = LocalTime.of(8, 0), registro = LocalTime.of(8, 20)),
                    Asistencia(fecha = hoy.minusDays(3), estado = EstadoAsistencia.FALTA, inicioTurno = LocalTime.of(8, 0), registro = null),
                    Asistencia(fecha = hoy.plusDays(1), estado = EstadoAsistencia.CUMPLIDA, inicioTurno = LocalTime.of(8, 0), registro = LocalTime.of(7, 58)),
                    Asistencia(fecha = hoy.plusDays(2), estado = EstadoAsistencia.EN_REVISION, inicioTurno = LocalTime.of(8, 0), registro = LocalTime.of(8, 5)),
                    Asistencia(fecha = hoy.plusDays(3), estado = EstadoAsistencia.RECHAZADA, inicioTurno = LocalTime.of(8, 0), registro = null)
                )
                samples.forEach { dao.upsert(it) }
            }
        }

        setContent {
            AsisteSVTheme {
                // ---- Navegación: "login" | "olvide" | "inicio" ----
                var pantalla by remember { mutableStateOf("login") }

                val context = LocalContext.current
                val usuarioDao = remember { DatabaseProvider.getUsuarioDao(context) }
                val vm: LoginViewModel = viewModel(factory = LoginViewModelFactory(usuarioDao))
                val estado by vm.estado.collectAsState()

                // Usuario de prueba (solo desarrollo)
                LaunchedEffect(Unit) {
                    usuarioDao.insertar(
                        Usuario(
                            correo = "tecnico@serviciosvolt.cl",
                            nombre = "Técnico Demo",
                            passwordHash = sha256("123456")
                        )
                    )
                }

                // Si el login fue exitoso -> pantalla de inicio
                LaunchedEffect(estado) {
                    if (estado is LoginEstado.Exito) pantalla = "inicio"
                }

                // Botón atrás: desde cualquier pantalla vuelve al login
                BackHandler(enabled = pantalla != "login") {
                    pantalla = "login"
                }

                when (pantalla) {
                    "login" -> Surface(modifier = Modifier.fillMaxSize()) {
                        LoginView(
                            onLogin = { correo, pass -> vm.login(correo, pass) },
                            onOlvideClave = { pantalla = "olvide" },
                            cargando = estado is LoginEstado.Cargando,
                            error = (estado as? LoginEstado.Error)?.mensaje
                        )
                    }

                    "olvide" -> {
                        val vmOlvide: OlvideClaveViewModel = viewModel(
                            factory = OlvideClaveViewModelFactory(usuarioDao)
                        )
                        val estadoOlvide by vmOlvide.estado.collectAsState()

                        // Al entrar a la pantalla se limpia el estado anterior
                        LaunchedEffect(Unit) { vmOlvide.reset() }

                        OlvideContrasenaView(
                            onVolver = {
                                vmOlvide.reset()
                                pantalla = "login"
                            },
                            estado = estadoOlvide,
                            onEnviar = { vmOlvide.enviar(it) }
                        )
                    }

                    "inicio" -> MainView()
                }
            }
        }
    }
}