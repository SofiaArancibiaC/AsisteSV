package com.js.smartvolt.asiste.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.js.smartvolt.asiste.model.Asistencia
import com.js.smartvolt.asiste.model.EstadoAsistencia
import com.js.smartvolt.asiste.model.data.local.DatabaseProvider
import com.js.smartvolt.asiste.viewmodel.AsistenciaViewModel
import com.js.smartvolt.asiste.viewmodel.AsistenciaViewModelFactory
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.daysOfWeek
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

private val LOCALE_ES = Locale("es", "CL")

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalendarioScreen() {
    val context = LocalContext.current
    val dao = remember { DatabaseProvider.getDao(context) }
    val vm: AsistenciaViewModel = viewModel(factory = AsistenciaViewModelFactory(dao))

    val month by vm.month.collectAsState()
    val attendances by vm.attendances.collectAsState()
    val selected by vm.selected.collectAsState()

    val scope = rememberCoroutineScope()
    val mesInicial = remember { YearMonth.now() }
    val diasSemana = remember { daysOfWeek(firstDayOfWeek = DayOfWeek.MONDAY) }

    val state = rememberCalendarState(
        startMonth = mesInicial.minusMonths(6),
        endMonth = mesInicial.plusMonths(6),
        firstVisibleMonth = mesInicial,
        firstDayOfWeek = DayOfWeek.MONDAY
    )

    // Sincroniza el mes visible del calendario con el ViewModel
    LaunchedEffect(state) {
        snapshotFlow { state.firstVisibleMonth.yearMonth }.collect { visible ->
            vm.setMonth(visible)
            vm.clearSelection()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Título
        Text("MI CALENDARIO")
        Text("Turnos y asistencia")

        // Navegación de mes
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = {
                scope.launch { state.animateScrollToMonth(month.minusMonths(1)) }
            }) { Text("<") }

            Text(
                month.month.getDisplayName(TextStyle.FULL, LOCALE_ES)
                    .replaceFirstChar { it.uppercase() } + " " + month.year
            )

            TextButton(onClick = {
                scope.launch { state.animateScrollToMonth(month.plusMonths(1)) }
            }) { Text(">") }
        }

        // Encabezado de días de la semana (L M M J V S D)
        Row(Modifier.fillMaxWidth()) {
            diasSemana.forEach { dow ->
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        dow.getDisplayName(TextStyle.SHORT, LOCALE_ES)
                            .take(1)
                            .uppercase()
                    )
                }
            }
        }

        // Cuadrícula del mes
        HorizontalCalendar(
            state = state,
            dayContent = { day ->
                DiaCalendario(
                    dia = day,
                    asistencia = attendances[day.date],
                    onClick = { vm.selectDate(day.date) }
                )
            }
        )

        // Leyenda (texto simple en lugar de colores)
        Text(
            text = "✓ Cumplida   ! Atrasada   ✕ Falta",
            modifier = Modifier.padding(vertical = 8.dp)
        )

        HorizontalDivider()

        // Detalle del día seleccionado
        selected?.let { att ->
            DetalleDiaSeleccionado(att = att, onCerrar = { vm.clearSelection() })
        } ?: Text(
            text = "Toca un día con turno para ver el detalle",
            modifier = Modifier.padding(top = 12.dp)
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun DiaCalendario(
    dia: CalendarDay,
    asistencia: Asistencia?,
    onClick: () -> Unit
) {
    // Días que no pertenecen al mes: celda vacía para mantener la grilla
    if (dia.position != DayPosition.MonthDate) {
        Box(Modifier.aspectRatio(1f))
        return
    }

    val marca = when (asistencia?.estado) {
        EstadoAsistencia.CUMPLIDA -> "✓"
        EstadoAsistencia.ATRASADA -> "!"
        EstadoAsistencia.FALTA -> "✕"
        null -> ""
    }

    Column(
        modifier = Modifier
            .aspectRatio(1f)
            .clickable(enabled = asistencia != null) { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(dia.date.dayOfMonth.toString())
        Text(marca)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun DetalleDiaSeleccionado(att: Asistencia, onCerrar: () -> Unit) {
    val fechaTexto = att.fecha
        .format(DateTimeFormatter.ofPattern("EEEE d 'de' MMMM", LOCALE_ES))
        .replaceFirstChar { it.uppercase() }

    val estadoTexto = when (att.estado) {
        EstadoAsistencia.CUMPLIDA -> "Cumplida"
        EstadoAsistencia.ATRASADA -> "Atrasada"
        EstadoAsistencia.FALTA -> "Falta"
    }

    Column(modifier = Modifier.padding(top = 12.dp)) {
        Text(fechaTexto)
        FilaDetalle("Turno", att.inicioTurno.toString())
        FilaDetalle("Entrada", att.registro?.toString() ?: "Sin marca")
        FilaDetalle("Estado", estadoTexto)
        TextButton(onClick = onCerrar) { Text("Cerrar") }
    }
}

@Composable
private fun FilaDetalle(etiqueta: String, valor: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(etiqueta)
        Text(valor)
    }
}