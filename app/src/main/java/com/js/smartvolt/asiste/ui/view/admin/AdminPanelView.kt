package com.js.smartvolt.asiste.ui.view.admin

import com.js.smartvolt.asiste.ui.components.*

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll

@Composable
fun AdminPanelView(onOpenAlertas: () -> Unit, onOpenUsuarios: () -> Unit, onOpenReporte: () -> Unit) {
    var dia by remember { mutableStateOf(0) }
    val dias = listOf("Mar 20 oct", "Mié 21 oct", "Jue 22 oct", "Vie 23 oct")
    var faena by remember { mutableStateOf("Todas las faenas") }

    Column(Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState())) {
        AdminHeader("PANEL ADMINISTRADOR", "ADM-001 · Actualizado 08:15")

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { if (dia > 0) dia-- }) { Text("<") }
            Text(dias[dia], style = MaterialTheme.typography.titleMedium)
            IconButton(onClick = { if (dia < dias.lastIndex) dia++ }) { Text(">") }
        }
        Spacer(Modifier.height(8.dp))
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text("Faena: ", style = MaterialTheme.typography.bodyMedium)
            Text(faena, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
        }
        Spacer(Modifier.height(12.dp))

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            KpiCard("128", "PRESENTES", Modifier.weight(1f)) { }
            KpiCard("9", "ATRASADOS", Modifier.weight(1f)) { }
        }
        Spacer(Modifier.height(8.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            KpiCard("6", "AUSENTES", Modifier.weight(1f)) { onOpenUsuarios() }
            KpiCard("4", "SOSPECHOSOS", Modifier.weight(1f)) { onOpenAlertas() }
        }

        Spacer(Modifier.height(16.dp))
        Text("REQUIERE ATENCIÓN", style = MaterialTheme.typography.titleSmall)
        Spacer(Modifier.height(4.dp))
        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(12.dp)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("4 alertas sin revisar")
                    Text("›", color = MaterialTheme.colorScheme.primary)
                }
                Spacer(Modifier.height(4.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("2 horas extra por aprobar")
                    Text("›", color = MaterialTheme.colorScheme.primary)
                }
            }
        }

        Spacer(Modifier.height(16.dp))
        Text("ASISTENCIA POR FAENA", style = MaterialTheme.typography.titleSmall)
        Spacer(Modifier.height(4.dp))
        FaenaRow("Ruta 5 Norte", "42/48", 0.87f)
        FaenaRow("Puente Maipo", "36/40", 0.90f)
        FaenaRow("Planta Industrial", "50/55", 0.91f)

        Spacer(Modifier.height(16.dp))
        GradientButton(text = "VER REPORTE DE HORAS", onClick = onOpenReporte)
    }
}

@Composable
private fun KpiCard(value: String, label: String, modifier: Modifier, onClick: () -> Unit) {
    Card(modifier = modifier, onClick = onClick) {
        Column(Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(value, style = MaterialTheme.typography.headlineMedium)
            Text(label, style = MaterialTheme.typography.labelSmall)
        }
    }
}

@Composable
private fun FaenaRow(nombre: String, conteo: String, pct: Float) {
    Row(Modifier.fillMaxWidth().padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(nombre, Modifier.weight(1f))
        Text(conteo, Modifier.padding(horizontal = 8.dp))
        Text("${(pct * 100).toInt()}%", color = MaterialTheme.colorScheme.primary)
    }
}

