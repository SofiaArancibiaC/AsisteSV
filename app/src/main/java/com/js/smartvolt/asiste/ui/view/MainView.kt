package com.js.smartvolt.asiste.ui.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.js.smartvolt.asiste.ui.components.HamburgerScaffold
import com.js.smartvolt.asiste.ui.components.UserBottomBar
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.first
import androidx.lifecycle.viewmodel.compose.viewModel
import com.js.smartvolt.asiste.model.admin.Faena
import com.js.smartvolt.asiste.model.data.local.DatabaseProvider
import com.js.smartvolt.asiste.model.data.local.UbicacionProvider
import com.js.smartvolt.asiste.viewmodel.AsistenciaViewModel
import com.js.smartvolt.asiste.viewmodel.AsistenciaViewModelFactory
import java.time.LocalDate

@Composable
fun MainView(codigoOperario: String = "OP-014", onCerrarSesion: () -> Unit = {}) {
    val context = LocalContext.current
    val dao = remember { DatabaseProvider.getAsistenciaDao(context) }
    val ubicacion = remember { UbicacionProvider(context) }

    val alertaDao = DatabaseProvider.getAlertaDao(context)
    val vm: AsistenciaViewModel = viewModel(
        factory = AsistenciaViewModelFactory(dao, ubicacion, alertaDao)
    )

    val faena = remember {
        Faena(
            id = 1,
            nombre = "Faena Ruta 5 Norte",
            presentes = 42,
            total = 48,
            latitud = -33.4489,
            longitud = -70.6693,
            radioMetros = 100.0
        )
    }
    val asistenciaDao = DatabaseProvider.getAsistenciaDao(context)
    val turnoDao = DatabaseProvider.getTurnoDao(context)
    val usuarioDao = DatabaseProvider.getUsuarioDao(context)
    var selectedTab by remember { mutableStateOf(0) }
    var resultadoMarca by remember { mutableStateOf<ResultadoMarca?>(null) }
    var inicioTurno by remember { mutableStateOf(java.time.LocalTime.of(8,0)) }
    var finTurno by remember { mutableStateOf(java.time.LocalTime.of(14,0)) }
    var vistaExtra by remember { mutableStateOf<String?>(null) }
    LaunchedEffect(codigoOperario) {
        val u = usuarioDao.getByCodigo(codigoOperario)
        if (u != null) {
            val turnos = turnoDao.getByUsuario(u.id).first()
            val hoy = java.time.LocalDate.now()
            val t = turnos.firstOrNull { it.dia == hoy && it.activo }
            if (t != null) {
                inicioTurno = t.horario
                finTurno = t.horario.plusHours(6)
            }
        }
    }

    BackHandler(enabled = true) {
        if (vistaExtra != null) vistaExtra = null
        else if (selectedTab != 0) selectedTab = 0
    }

    if (resultadoMarca != null) {
        MarcaResultadoScreen(
            resultado = resultadoMarca!!,
            onAccion = { resultadoMarca = null }
        )
        return
    }

    if (vistaExtra != null) {
        HamburgerScaffold(
            title = if (vistaExtra == "config") "Configuración" else "Perfil",
            onConfiguracion = { vistaExtra = "config" },
            onPerfil = { vistaExtra = "perfil" },
            onCerrarSesion = onCerrarSesion,
            content = { padding ->
                Box(Modifier.padding(padding)) {
                    if (vistaExtra == "config") ConfiguracionView() else PerfilView(codigoOperario = codigoOperario)
                }
            }
        )
        return
    }

    HamburgerScaffold(
        title = "AsisteSV",
        onConfiguracion = { vistaExtra = "config" },
        onPerfil = { vistaExtra = "perfil" },
        onCerrarSesion = onCerrarSesion,
        bottomBar = {
            UserBottomBar(selectedTab = selectedTab, onTabSelected = { selectedTab = it })
        },
        content = { padding ->
            when (selectedTab) {
                0 -> InicioRoute(
                    viewModel = vm,
                    faena = faena,
                    codigoOperario = "OP-014",
                    fecha = LocalDate.now(),
                    inicioTurno = inicioTurno,
                    finTurno = finTurno,
                    frente = "Frente 2",
                    sinConexion = false,
                    onResultado = { resultadoMarca = it },
                    onVerCalendario = { selectedTab = 1 },
                    modifier = Modifier.padding(padding)
                )
                1 -> Box(Modifier.padding(padding)) { CalendarioView(codigoOperario = codigoOperario) }
                2 -> Box(Modifier.padding(padding)) { HistorialView() }
            }
        }
    )
}
