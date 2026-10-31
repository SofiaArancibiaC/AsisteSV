package com.js.smartvolt.asiste.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.js.smartvolt.asiste.model.Asistencia
import com.js.smartvolt.asiste.model.EstadoAsistencia
import com.js.smartvolt.asiste.model.data.local.DatabaseProvider
import com.js.smartvolt.asiste.ui.theme.AsisteSVTheme
import com.js.smartvolt.asiste.ui.theme.DegradadoFondo
import com.js.smartvolt.asiste.ui.theme.DegradadoMarca
import com.js.smartvolt.asiste.ui.theme.EstadoColors
import com.js.smartvolt.asiste.ui.theme.LocalEstadoColors
import com.js.smartvolt.asiste.viewmodel.AsistenciaViewModel
import com.js.smartvolt.asiste.viewmodel.AsistenciaViewModelFactory
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.daysOfWeek
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

private val LOCALE_ES = Locale("es", "CL")

/** Datos de un día ya formateados para mostrar (así la pantalla se puede previsualizar sin base de datos). */
data class DiaAsistenciaUi(
    val fecha: LocalDate,
    val estado: EstadoAsistencia,
    val turno: String,
    val entrada: String
)

private fun Asistencia.aUi(formato: DateTimeFormatter) = DiaAsistenciaUi(
    fecha = fecha,
    estado = estado,
    turno = inicioTurno.format(formato),
    entrada = registro?.format(formato) ?: "Sin marca"
)

private fun EstadoColors.de(estado: EstadoAsistencia): Color = when (estado) {
    EstadoAsistencia.CUMPLIDA -> cumplida
    EstadoAsistencia.ATRASADA -> atrasada
    EstadoAsistencia.FALTA -> falta
}

// ============================================================
// Versión con ViewModel (la que usas en la navegación)
// ============================================================
@Composable
fun CalendarioView() {
    val context = LocalContext.current
    val dao = remember { DatabaseProvider.getDao(context) }
    val vm: AsistenciaViewModel = viewModel(factory = AsistenciaViewModelFactory(dao))

    val attendances by vm.attendances.collectAsState()
    val selected by vm.selected.collectAsState()

    val formatoHora = remember { DateTimeFormatter.ofPattern("HH:mm") }
    val dias = remember(attendances) {
        attendances.mapValues { (_, asistencia) -> asistencia.aUi(formatoHora) }
    }
    val seleccionado = remember(selected) { selected?.aUi(formatoHora) }

    CalendarioContenido(
        dias = dias,
        seleccionado = seleccionado,
        onMesVisible = { vm.setMonth(it) },
        onSeleccionar = { vm.selectDate(it) },
        onLimpiar = { vm.clearSelection() }
    )
}

// ============================================================
// Contenido sin ViewModel (se puede previsualizar)
// ============================================================
@Composable
fun CalendarioContenido(
    dias: Map<LocalDate, DiaAsistenciaUi>,
    seleccionado: DiaAsistenciaUi?,
    onMesVisible: (YearMonth) -> Unit,
    onSeleccionar: (LocalDate) -> Unit,
    onLimpiar: () -> Unit,
    modifier: Modifier = Modifier,
    hoy: LocalDate = LocalDate.now()
) {
    val cs = MaterialTheme.colorScheme
    val estados = LocalEstadoColors.current
    val scope = rememberCoroutineScope()

    val mesInicial = remember(hoy) { YearMonth.from(hoy) }
    val diasSemana = remember { daysOfWeek(firstDayOfWeek = DayOfWeek.MONDAY) }

    val state = rememberCalendarState(
        startMonth = mesInicial.minusMonths(6),
        endMonth = mesInicial.plusMonths(6),
        firstVisibleMonth = mesInicial,
        firstDayOfWeek = DayOfWeek.MONDAY
    )
    val mesVisible = state.firstVisibleMonth.yearMonth

    // Avisa al ViewModel cuando cambia el mes visible
    LaunchedEffect(state) {
        snapshotFlow { state.firstVisibleMonth.yearMonth }.collect { visible ->
            onMesVisible(visible)
            onLimpiar()
        }
    }

    val mesTexto = mesVisible.month.getDisplayName(TextStyle.FULL, LOCALE_ES)
        .replaceFirstChar { it.uppercase() } + " " + mesVisible.year

    val delMes = dias.values.filter { YearMonth.from(it.fecha) == mesVisible }
    val nCumplidas = delMes.count { it.estado == EstadoAsistencia.CUMPLIDA }
    val nAtrasadas = delMes.count { it.estado == EstadoAsistencia.ATRASADA }
    val nFaltas = delMes.count { it.estado == EstadoAsistencia.FALTA }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DegradadoFondo)
    ) {
        // Encabezado (respeta la barra de estado)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Text(
                text = "MI CALENDARIO",
                color = cs.primary,
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = "Turnos y asistencia",
                color = cs.onSurfaceVariant,
                fontSize = 12.sp
            )
        }

        // Línea de marca
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .height(3.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(DegradadoMarca)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // ---------- Resumen del mes ----------
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ResumenMes("Cumplidas", nCumplidas, estados.cumplida, Modifier.weight(1f))
                ResumenMes("Atrasadas", nAtrasadas, estados.atrasada, Modifier.weight(1f))
                ResumenMes("Faltas", nFaltas, estados.falta, Modifier.weight(1f))
            }

            // ---------- Tarjeta del calendario ----------
            val formaTarjeta = RoundedCornerShape(18.dp)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(formaTarjeta)
                    .background(cs.surface)
                    .border(1.5.dp, cs.outline, formaTarjeta)
                    .padding(12.dp)
            ) {
                // Navegación de mes
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BotonMes("‹") {
                        scope.launch { state.animateScrollToMonth(mesVisible.minusMonths(1)) }
                    }
                    Text(
                        text = mesTexto,
                        color = cs.onSurface,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        textAlign = TextAlign.Center
                    )
                    BotonMes("›") {
                        scope.launch { state.animateScrollToMonth(mesVisible.plusMonths(1)) }
                    }
                }

                Spacer(Modifier.height(10.dp))

                // Encabezado de días de la semana (L M M J V S D)
                Row(Modifier.fillMaxWidth()) {
                    diasSemana.forEach { dow ->
                        Box(
                            modifier = Modifier.weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = dow.getDisplayName(TextStyle.SHORT, LOCALE_ES)
                                    .take(1)
                                    .uppercase(),
                                color = cs.primary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }
                }

                Spacer(Modifier.height(4.dp))

                // Cuadrícula del mes
                HorizontalCalendar(
                    state = state,
                    dayContent = { day ->
                        DiaCalendario(
                            dia = day,
                            hoy = hoy,
                            registro = dias[day.date],
                            seleccionado = seleccionado?.fecha == day.date,
                            onClick = {
                                if (seleccionado?.fecha == day.date) onLimpiar()
                                else onSeleccionar(day.date)
                            }
                        )
                    }
                )

                Spacer(Modifier.height(8.dp))
                HorizontalDivider(color = cs.outline)
                Spacer(Modifier.height(10.dp))

                Leyenda()
            }

            // ---------- Detalle del día seleccionado ----------
            if (seleccionado != null) {
                DetalleDia(dia = seleccionado)
            } else {
                Text(
                    text = "Toca un día con turno para ver el detalle",
                    color = cs.onSurfaceVariant,
                    fontSize = 13.sp,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun ResumenMes(etiqueta: String, cantidad: Int, color: Color, modifier: Modifier = Modifier) {
    val cs = MaterialTheme.colorScheme
    val forma = RoundedCornerShape(14.dp)
    Column(
        modifier = modifier
            .clip(forma)
            .background(cs.surface)
            .border(1.5.dp, cs.outline, forma)
            .padding(vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = cantidad.toString(),
            color = color,
            fontSize = 26.sp,
            fontWeight = FontWeight.ExtraBold
        )
        Text(
            text = etiqueta,
            color = cs.onSurfaceVariant,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun BotonMes(simbolo: String, onClick: () -> Unit) {
    val cs = MaterialTheme.colorScheme
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(cs.outline.copy(alpha = 0.6f))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = simbolo,
            color = cs.primary,
            fontSize = 24.sp,
            fontWeight = FontWeight.ExtraBold
        )
    }
}

@Composable
private fun DiaCalendario(
    dia: CalendarDay,
    hoy: LocalDate,
    registro: DiaAsistenciaUi?,
    seleccionado: Boolean,
    onClick: () -> Unit
) {
    // Días que no pertenecen al mes: celda vacía para mantener la grilla
    if (dia.position != DayPosition.MonthDate) {
        Box(Modifier.aspectRatio(1f))
        return
    }

    val cs = MaterialTheme.colorScheme
    val estados = LocalEstadoColors.current

    val forma = RoundedCornerShape(10.dp)
    val esHoy = dia.date == hoy
    val esFinDeSemana = dia.date.dayOfWeek == DayOfWeek.SATURDAY ||
            dia.date.dayOfWeek == DayOfWeek.SUNDAY

    // Turno próximo: día hábil futuro (o de hoy) sin marca registrada.
    // Si tu app ya conoce los turnos programados, reemplaza esta condición.
    val esProximo = registro == null && !esFinDeSemana && !dia.date.isBefore(hoy)

    val relleno = when {
        registro != null -> estados.de(registro.estado)
        esHoy -> cs.primary
        esProximo -> cs.outline.copy(alpha = 0.35f)
        else -> Color.Transparent
    }
    val colorTexto = when {
        registro != null -> estados.sobreEstado
        esHoy -> cs.onPrimary
        esProximo -> cs.onSurface
        else -> cs.onSurfaceVariant.copy(alpha = 0.45f)
    }

    Box(
        modifier = Modifier
            .padding(2.5.dp)
            .aspectRatio(1f)
            .clip(forma)
            .background(relleno)
            .then(
                if (esProximo && !esHoy) Modifier.bordeDiscontinuo(cs.onSurfaceVariant) else Modifier
            )
            // Hoy: aro neón. Seleccionado: aro blanco por encima.
            .then(if (esHoy) Modifier.border(3.dp, cs.primary, forma) else Modifier)
            .then(if (seleccionado) Modifier.border(3.dp, cs.onSurface, forma) else Modifier)
            .clickable(enabled = registro != null) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = dia.date.dayOfMonth.toString(),
            color = colorTexto,
            fontSize = 15.sp,
            fontWeight = if (registro != null || esHoy || esProximo) FontWeight.ExtraBold
            else FontWeight.Normal
        )
    }
}

/** Borde punteado para los turnos próximos. */
private fun Modifier.bordeDiscontinuo(
    color: Color,
    radio: Dp = 10.dp
): Modifier = this.drawBehind {
    val grosor = 1.5.dp.toPx()
    drawRoundRect(
        color = color,
        topLeft = Offset(grosor / 2, grosor / 2),
        size = Size(size.width - grosor, size.height - grosor),
        cornerRadius = CornerRadius(radio.toPx(), radio.toPx()),
        style = Stroke(
            width = grosor,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 8f), 0f)
        )
    )
}

@Composable
private fun Leyenda() {
    val estados = LocalEstadoColors.current
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        ItemLeyenda("Cumplida", estados.cumplida)
        ItemLeyenda("Atrasada", estados.atrasada)
        ItemLeyenda("Falta", estados.falta)
        ItemLeyenda("Próximo", null)
    }
}

@Composable
private fun ItemLeyenda(texto: String, color: Color?) {
    val cs = MaterialTheme.colorScheme
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(RoundedCornerShape(3.dp))
                .then(
                    if (color != null) Modifier.background(color)
                    else Modifier.bordeDiscontinuo(cs.onSurfaceVariant, radio = 3.dp)
                )
        )
        Spacer(Modifier.width(5.dp))
        Text(
            text = texto,
            color = cs.onSurface,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun DetalleDia(dia: DiaAsistenciaUi) {
    val cs = MaterialTheme.colorScheme
    val estados = LocalEstadoColors.current
    val colorEstado = estados.de(dia.estado)

    val fechaTexto = dia.fecha
        .format(DateTimeFormatter.ofPattern("EEEE d 'de' MMMM", LOCALE_ES))
        .replaceFirstChar { it.uppercase() }

    val estadoTexto = when (dia.estado) {
        EstadoAsistencia.CUMPLIDA -> "CUMPLIDA"
        EstadoAsistencia.ATRASADA -> "ATRASADA"
        EstadoAsistencia.FALTA -> "FALTA"
    }

    val forma = RoundedCornerShape(16.dp)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .clip(forma)
            .background(cs.surface)
            .border(1.5.dp, cs.outline, forma)
    ) {
        // Barra lateral con el color del estado
        Box(
            modifier = Modifier
                .width(8.dp)
                .fillMaxHeight()
                .background(colorEstado)
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 14.dp, vertical = 12.dp)
        ) {
            Text(
                text = fechaTexto,
                color = cs.onSurface,
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Spacer(Modifier.height(4.dp))

            FilaDetalle("Turno") {
                Text(
                    text = dia.turno,
                    color = cs.onSurface,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            HorizontalDivider(color = cs.outline)
            FilaDetalle("Entrada") {
                Text(
                    text = dia.entrada,
                    color = cs.onSurface,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            HorizontalDivider(color = cs.outline)
            FilaDetalle("Estado") {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(colorEstado)
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = estadoTexto,
                        color = estados.sobreEstado,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }
    }
}

@Composable
private fun FilaDetalle(etiqueta: String, valor: @Composable () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = etiqueta,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 14.sp
        )
        valor()
    }
}

// ============================================================
// Preview con datos ficticios (no necesita base de datos)
// ============================================================
@Preview(showBackground = true, heightDp = 860)
@Composable
fun CalendarioPreview() {
    val hoy = LocalDate.of(2026, 10, 7)
    val dias = listOf(
        DiaAsistenciaUi(LocalDate.of(2026, 10, 1), EstadoAsistencia.CUMPLIDA, "08:00", "07:58"),
        DiaAsistenciaUi(LocalDate.of(2026, 10, 2), EstadoAsistencia.ATRASADA, "08:00", "08:14"),
        DiaAsistenciaUi(LocalDate.of(2026, 10, 5), EstadoAsistencia.CUMPLIDA, "08:00", "07:59"),
        DiaAsistenciaUi(LocalDate.of(2026, 10, 6), EstadoAsistencia.FALTA, "08:00", "Sin marca")
    ).associateBy { it.fecha }

    AsisteSVTheme {
        CalendarioContenido(
            dias = dias,
            seleccionado = dias[LocalDate.of(2026, 10, 2)],
            onMesVisible = {},
            onSeleccionar = {},
            onLimpiar = {},
            hoy = hoy
        )
    }
}