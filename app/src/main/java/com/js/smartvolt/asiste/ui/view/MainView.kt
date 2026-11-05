package com.js.smartvolt.asiste.ui.view

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
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
fun MainView() {
    val context = LocalContext.current
    val dao = remember { DatabaseProvider.getAsistenciaDao(context) }
    val ubicacion = remember { UbicacionProvider(context) }

    val vm: AsistenciaViewModel = viewModel(
        factory = AsistenciaViewModelFactory(dao, ubicacion)
    )

    var selectedTab by remember { mutableStateOf(0) }
    var resultadoMarca by remember { mutableStateOf<ResultadoMarca?>(null) }

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
                inicioTurno = LocalTime.of(8, 0),
                finTurno = LocalTime.of(18, 0),
                frente = "Frente 2",
                sinConexion = false,
                onResultado = { resultadoMarca = it },
                onVerCalendario = { selectedTab = 1 },
                modifier = Modifier.padding(padding)
            )
            1 -> CalendarioView()
            2 -> HistorialView()
        }
    }
}