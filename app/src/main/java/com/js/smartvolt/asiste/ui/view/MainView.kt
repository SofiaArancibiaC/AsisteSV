package com.js.smartvolt.asiste.ui.view

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.first
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.js.smartvolt.asiste.model.admin.Faena
import com.js.smartvolt.asiste.model.data.local.DatabaseProvider
import com.js.smartvolt.asiste.model.data.local.UbicacionProvider
import com.js.smartvolt.asiste.viewmodel.AsistenciaViewModel
import com.js.smartvolt.asiste.viewmodel.AsistenciaViewModelFactory
import java.time.LocalDate
import java.time.LocalTime

@Composable
fun MainView(codigoOperario: String = "OP-014") {
    val context = LocalContext.current
    val dao = remember { DatabaseProvider.getAsistenciaDao(context) }
    val ubicacion = remember { UbicacionProvider(context) }

    val vm: AsistenciaViewModel = viewModel(
        factory = AsistenciaViewModelFactory(dao, ubicacion)
    )

    // Demo. Cambia latitud/longitud por las de tu faena real.
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
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val asistenciaDao = com.js.smartvolt.asiste.model.data.local.DatabaseProvider.getAsistenciaDao(context)
    val turnoDao = com.js.smartvolt.asiste.model.data.local.DatabaseProvider.getTurnoDao(context)
    val alertaDao = com.js.smartvolt.asiste.model.data.local.DatabaseProvider.getAlertaDao(context)
    val usuarioDao = com.js.smartvolt.asiste.model.data.local.DatabaseProvider.getUsuarioDao(context)
    var selectedTab by remember { mutableStateOf(0) }
    var resultadoMarca by remember { mutableStateOf<ResultadoMarca?>(null) }
    var inicioTurno by remember { mutableStateOf(java.time.LocalTime.of(8,0)) }
    var finTurno by remember { mutableStateOf(java.time.LocalTime.of(14,0)) }
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



    if (resultadoMarca != null) {
        MarcaResultadoScreen(
            resultado = resultadoMarca!!,
            onAccion = { resultadoMarca = null }
        )
        return
    }

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Inicio") },
                    label = { Text("Inicio") }
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Icon(Icons.Default.CalendarMonth, contentDescription = "Calendario") },
                    label = { Text("Calendario") }
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = { Icon(Icons.Default.History, contentDescription = "Historial") },
                    label = { Text("Historial") }
                )
            }
        }
    ) { padding ->
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
                onMarcarEntrada = {
                    scope.launch {
                        val ahora = java.time.LocalTime.now() //editar para probar distintas marcas
                        val fecha = java.time.LocalDate.now()
                        val usuarioDao = com.js.smartvolt.asiste.model.data.local.DatabaseProvider.getUsuarioDao(context)
                        val usuario = usuarioDao.getByCodigo(codigoOperario)
                        val turnos = turnoDao.getByUsuario(usuario?.id ?: 0L).first()
                        val turnoHoy = turnos.firstOrNull { it.dia == fecha }
                        val inicioTurno = turnoHoy?.horario ?: java.time.LocalTime.of(8,0)
                        val finTurno = inicioTurno.plusHours(6)
                        val estado = when {
                            turnoHoy == null -> com.js.smartvolt.asiste.model.EstadoAsistencia.RECHAZADA
                            ahora.isBefore(inicioTurno) || ahora.isAfter(finTurno) -> com.js.smartvolt.asiste.model.EstadoAsistencia.RECHAZADA
                            ahora.isAfter(inicioTurno.plusMinutes(10)) -> com.js.smartvolt.asiste.model.EstadoAsistencia.ATRASADA
                            else -> com.js.smartvolt.asiste.model.EstadoAsistencia.CUMPLIDA
                        }
                        val asistencia = com.js.smartvolt.asiste.model.Asistencia(
                            fecha = fecha,
                            estado = estado,
                            inicioTurno = inicioTurno,
                            registro = ahora,
                            usuarioId = usuario?.id
                        )
                        asistenciaDao.upsert(asistencia)
                        if (estado == com.js.smartvolt.asiste.model.EstadoAsistencia.RECHAZADA) {
                            alertaDao.upsert(com.js.smartvolt.asiste.model.admin.Alerta(
                                codigoOperario = codigoOperario,
                                tipoMarca = com.js.smartvolt.asiste.model.admin.TipoMarca.ENTRADA,
                                hora = ahora,
                                faenaId = usuario?.faenaId ?: 0L,
                                motivo = "Marca fuera de turno",
                                estado = "REVISION"
                            ))
                        }
                    }
                    resultadoMarca = ResultadoMarca.aprobada("07:56", -62)
                },
                modifier = Modifier.padding(padding)
            )
            1 -> CalendarioView(codigoOperario = codigoOperario)
            2 -> HistorialView()
        }
    }
}