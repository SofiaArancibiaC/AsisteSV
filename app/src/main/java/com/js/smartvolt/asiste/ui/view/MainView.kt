package com.js.smartvolt.asiste.ui.view

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import java.time.LocalDate
import java.time.LocalTime

@Composable
fun MainView() {
    var selectedTab by remember { mutableStateOf(0) }
    var resultadoMarca by remember { mutableStateOf<ResultadoMarca?>(null) }

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
            0 -> InicioView(
                codigoOperario = "OP-014",
                nombreFaena = "Faena Ruta 5 Norte",
                fecha = LocalDate.now(),
                inicioTurno = LocalTime.of(8, 0),
                finTurno = LocalTime.of(18, 0),
                frente = "Frente 2",
                validacion = ValidacionPresencia(true, true, -62, false),
                sinConexion = false,
                onMarcarEntrada = { resultadoMarca = ResultadoMarca.aprobada("07:56", -62) },
                modifier = Modifier.padding(padding)
            )
            1 -> CalendarioView()
            2 -> HistorialView()
        }
    }
}