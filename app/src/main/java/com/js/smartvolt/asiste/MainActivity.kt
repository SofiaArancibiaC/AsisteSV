package com.js.smartvolt.asiste

import android.os.Bundle
import android.util.Log
import androidx.compose.material3.Surface
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.js.smartvolt.asiste.model.Asistencia
import com.js.smartvolt.asiste.model.data.local.DatabaseProvider
import com.js.smartvolt.asiste.model.data.local.sha256
import com.js.smartvolt.asiste.ui.theme.AsisteSVTheme
import com.js.smartvolt.asiste.ui.view.LoginView
import com.js.smartvolt.asiste.ui.view.MainView
import com.js.smartvolt.asiste.ui.view.admin.AdminMainView
import com.js.smartvolt.asiste.ui.view.OlvideClaveViewModel
import com.js.smartvolt.asiste.ui.view.OlvideClaveViewModelFactory
import com.js.smartvolt.asiste.ui.view.OlvideContrasenaView
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import java.time.LocalTime
import com.js.smartvolt.asiste.model.EstadoAsistencia
import com.js.smartvolt.asiste.model.RecuperarUsuario
import com.js.smartvolt.asiste.viewmodel.LoginEstado
import com.js.smartvolt.asiste.viewmodel.LoginViewModel
import com.js.smartvolt.asiste.viewmodel.LoginViewModelFactory
import com.js.smartvolt.asiste.data.repository.AuthRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate


class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            AsisteSVTheme() {
                // ---- Navegación: "login" | "olvide" | "inicio" ----
                var pantalla by remember { mutableStateOf("login") }
                var esAdmin by remember { mutableStateOf(false) }
                var codigoOperario by remember { mutableStateOf<String?>(null) }

                val context = LocalContext.current
                val usuarioDao = remember { DatabaseProvider.getUsuarioDao(context) }
                val vm: LoginViewModel = viewModel(factory = LoginViewModelFactory(AuthRepository(usuarioDao)))
                val estado by vm.estado.collectAsState()


                // Si el login fue exitoso -> pantalla de inicio
                LaunchedEffect(estado) {
                    if (estado is LoginEstado.Exito) {
                        codigoOperario = (estado as LoginEstado.Exito).codigo
                        pantalla = if (esAdmin) "admin" else "inicio"
                    }
                }

                // Botón atrás: desde cualquier pantalla (excepto admin, que gestiona su propio back) vuelve al login
                BackHandler (enabled = pantalla != "login" && pantalla != "admin") {
                    vm.limpiarEstado()
                    pantalla = "login"
                }

                when (pantalla) {
                    "login" -> Surface(modifier = Modifier.fillMaxSize()) {
                        LoginView(
                            onLogin = { correo, pass -> esAdmin = correo.trim().lowercase().contains("admin"); vm.login(correo, pass) },
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

                        // OJO: aquí va el composable de la pantalla, no el ViewModel
                        OlvideContrasenaView(
                            onVolver = {
                                vmOlvide.reset()
                                vm.limpiarEstado()
                                pantalla = "login"
                            },
                            estado = estadoOlvide,
                            onEnviar = { vmOlvide.enviar(it) }
                        )
                    }

                    "inicio" -> MainView(codigoOperario = codigoOperario ?: "OP-014", onCerrarSesion = { vm.limpiarEstado(); pantalla = "login" })
                    "admin" -> AdminMainView(onCerrarSesion = { vm.limpiarEstado(); pantalla = "login" })
                }
            }
        }
    }
}
