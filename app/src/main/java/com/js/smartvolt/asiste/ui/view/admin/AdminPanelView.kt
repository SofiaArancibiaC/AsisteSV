package com.js.smartvolt.asiste.ui.view.admin

import com.js.smartvolt.asiste.ui.components.*

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.js.smartvolt.asiste.model.data.local.DatabaseProvider
import com.js.smartvolt.asiste.viewmodel.FaenaViewModel
import com.js.smartvolt.asiste.viewmodel.FaenaViewModelFactory
import com.js.smartvolt.asiste.viewmodel.UsuarioViewModelFactory
import kotlinx.coroutines.launch
import java.time.Instant

import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun AdminPanelView(onOpenAlertas: () -> Unit, onOpenUsuarios: () -> Unit, onOpenReporte: () -> Unit) {
    val context = LocalContext.current
    val usuarioDao = DatabaseProvider.getUsuarioDao(context)
    val asistenciaDao = DatabaseProvider.getAsistenciaDao(context)
    val faenaDao = DatabaseProvider.getFaenaDao(context)
    val faenaVm: FaenaViewModel = viewModel(factory = FaenaViewModelFactory(faenaDao))
    val faenas by faenaVm.faenas.collectAsState()
    val vm: com.js.smartvolt.asiste.viewmodel.UsuarioViewModel = viewModel(factory = UsuarioViewModelFactory(usuarioDao))
    val usuarios by vm.usuarios.collectAsState()
    val asistencias by asistenciaDao.getAll().collectAsState(initial = emptyList())
    val usuariosConFaena by usuarioDao.getUsuariosConFaena().collectAsState(initial = emptyList())
    val scope = rememberCoroutineScope()

    var dia by remember { mutableStateOf(LocalDate.now()) }
    var faena by remember { mutableStateOf("Todas las faenas") }
    var mostrarSelector by remember { mutableStateOf(false) }

    val formato = remember {
        DateTimeFormatter.ofPattern("EEEE d 'de' MMMM yyyy", Locale.forLanguageTag("es-CL"))
    }
    val textoDia = dia.format(formato).replaceFirstChar { it.uppercase() }

    if (mostrarSelector) {
        // El DatePicker trabaja con milisegundos en UTC
        val pickerState = rememberDatePickerState(
            initialSelectedDateMillis = dia
                .atStartOfDay(ZoneOffset.UTC)
                .toInstant()
                .toEpochMilli()
        )

        DatePickerDialog(
            onDismissRequest = { mostrarSelector = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        pickerState.selectedDateMillis?.let { millis ->
                            dia = Instant.ofEpochMilli(millis)
                                .atZone(ZoneOffset.UTC)
                                .toLocalDate()
                        }
                        mostrarSelector = false
                    }
                ) { Text("Aceptar") }
            },
            dismissButton = {
                TextButton(onClick = { mostrarSelector = false }) { Text("Cancelar") }
            }
        ) {
            DatePicker(state = pickerState)
        }
    }
    Column(Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState())) {
        AdminHeader("PANEL ADMINISTRADOR", "ADM-001 · Actualizado 08:15")

        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = { mostrarSelector = true }) {
                Icon(Icons.Default.DateRange, contentDescription = "Filtrar por fecha")
                Spacer(Modifier.width(8.dp))
                Text("Filtrar por fecha")
            }
            TextButton(
                onClick = { dia = LocalDate.now() },
                enabled = dia != LocalDate.now()
            ) { Text("Hoy") }
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { dia = dia.minusDays(1) }) { Text("<") }
            Text(textoDia, style = MaterialTheme.typography.titleMedium)
            IconButton(onClick = { dia = dia.plusDays(1) }) { Text(">") }
        }

        Spacer(Modifier.height(12.dp))
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text("Faena: ", style = MaterialTheme.typography.bodyMedium)
            Text("$faena (${faenas.size} faenas)", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
        }
        Spacer(Modifier.height(12.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            KpiCard("${asistencias.size}", "ASISTENCIAS", Modifier.weight(1f)) { }
            KpiCard("${usuarios.size}", "USUARIOS", Modifier.weight(1f)) { }
        }
        Spacer(Modifier.height(8.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            KpiCard("0", "AUSENTES", Modifier.weight(1f)) { onOpenUsuarios() }
            KpiCard("0", "ALERTAS", Modifier.weight(1f)) { onOpenAlertas() }
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
        if (faenas.isEmpty()) {
            Text("No hay faenas registradas")
        } else {
            faenas.forEach { f ->
                FaenaRow(f.nombre, "0/0"  , 0f)
            }
        }

        Spacer(Modifier.height(16.dp))
        CommonButton(text = "SIMULAR MARCAS", onClick = {
            scope.launch {
                usuariosConFaena.forEach { uf ->
                    asistenciaDao.upsert(
                        com.js.smartvolt.asiste.model.Asistencia(
                            fecha = dia,
                            estado = com.js.smartvolt.asiste.model.EstadoAsistencia.CUMPLIDA,
                            inicioTurno = java.time.LocalTime.of(8, 0),
                            finTurno = java.time.LocalTime.of(14, 0),
                            registro = java.time.LocalTime.of(8, 5),
                            usuarioId = uf.usuario.id
                        )
                    )
                }
            }
        }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        CommonButton(text = "LIMPIAR MARCAS SIMULADAS", onClick = {
            scope.launch {
                asistencias.filter { it.fecha == dia && it.registro == java.time.LocalTime.of(8, 5) }.forEach { a ->
                    asistenciaDao.delete(a.id)
                }
            }
        }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        CommonButton(text = "VER REPORTE DE HORAS", onClick = onOpenReporte, modifier = Modifier.fillMaxWidth())
    }
}

