package com.js.smartvolt.asiste.ui.view.admin

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.js.smartvolt.asiste.model.EstadoAsistencia
import com.js.smartvolt.asiste.model.admin.Rol
import com.js.smartvolt.asiste.model.data.local.DatabaseProvider
import com.js.smartvolt.asiste.ui.components.*
import com.js.smartvolt.asiste.ui.theme.Medidas
import com.js.smartvolt.asiste.ui.theme.estados
import com.js.smartvolt.asiste.ui.theme.tenue

// ---------------------------------------------------------------
// Modelo de la fila del reporte
// ---------------------------------------------------------------
private data class ResumenOperario(
    val codigo: String,
    val horas: Int,
    val extras: Double,
    val atrasos: Int,
    val faltas: Int,
    val registros: Int
)

private const val HORAS_POR_JORNADA = 8

private fun Double.formatoHoras(): String =
    if (this % 1.0 == 0.0) toInt().toString() else "%.1f".format(this)

// ---------------------------------------------------------------
// Pantalla
// ---------------------------------------------------------------
@Composable
fun AdminReportesView(onOpenOperario: (String) -> Unit) {
    val context = LocalContext.current
    val usuarioDao = DatabaseProvider.getUsuarioDao(context)
    val asistenciaDao = DatabaseProvider.getAsistenciaDao(context)
    val horaExtraDao = DatabaseProvider.getHoraExtraDao(context)
    val faenaDao = DatabaseProvider.getFaenaDao(context)
    val faenas by faenaDao.getAll().collectAsState(initial = emptyList())
    val usuarios by usuarioDao.getByRol(Rol.OPERARIO).collectAsState(initial = emptyList())

    var faenaSeleccionada by remember { mutableStateOf<Long?>(null) } // null = todas
    var mesSeleccionado by remember { mutableStateOf(java.time.YearMonth.now()) }
    var expandFaena by remember { mutableStateOf(false) }
    var expandMes by remember { mutableStateOf(false) }

    val usuariosFiltrados = if (faenaSeleccionada == null) usuarios else usuarios.filter { it.faenaId == faenaSeleccionada }

    val resumenes = usuariosFiltrados.map { u ->
        key(u.id) {
            val asistencias by asistenciaDao.getByUsuario(u.id).collectAsState(initial = emptyList())
            val horasExtra by horaExtraDao.getByUsuario(u.id).collectAsState(initial = emptyList())

            val asistMes = asistencias.filter { java.time.YearMonth.from(it.fecha) == mesSeleccionado }
            val extrasMes = horasExtra.filter { java.time.YearMonth.from(it.fecha) == mesSeleccionado && it.estado.name == "APROBADO" }

            val cumplidas = asistMes.count { it.estado == EstadoAsistencia.CUMPLIDA }
            val atrasos = asistMes.count { it.estado == EstadoAsistencia.ATRASADA }
            val faltas = asistMes.count { it.estado == EstadoAsistencia.FALTA }
            val extras = extrasMes.sumOf { it.horas.toDouble() }

            ResumenOperario(
                codigo = u.codigo,
                horas = cumplidas * HORAS_POR_JORNADA,
                extras = extras,
                atrasos = atrasos,
                faltas = faltas,
                registros = cumplidas + atrasos + faltas
            )
        }
    }
    // Un resumen por operario (cada uno lee sus propios datos)
    val totalHoras = resumenes.sumOf { it.horas }
    val totalExtras = resumenes.sumOf { it.extras }
    val totalAtrasos = resumenes.sumOf { it.atrasos }
    val totalFaltas = resumenes.sumOf { it.faltas }
    val totalRegistros = resumenes.sumOf { it.registros }
    val ausentismo = if (totalRegistros == 0) 0f else totalFaltas.toFloat() / totalRegistros

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = Medidas.PaddingPantalla, vertical = Medidas.EspacioM)
    ) {
        AdminHeader("REPORTE DE HORAS", "Remuneraciones y ausentismo")

        Spacer(Modifier.height(Medidas.EspacioM))

        Row(horizontalArrangement = Arrangement.spacedBy(Medidas.EspacioS)) {
            Box {
                SelectorChip(if (faenaSeleccionada == null) "Todas las faenas" else faenas.firstOrNull { it.id == faenaSeleccionada }?.nombre ?: "Faena", onClick = { expandFaena = true })
                DropdownMenu(expanded = expandFaena, onDismissRequest = { expandFaena = false }) {
                    DropdownMenuItem(text = { Text("Todas las faenas") }, onClick = { faenaSeleccionada = null; expandFaena = false })
                    faenas.forEach { f ->
                        DropdownMenuItem(text = { Text(f.nombre) }, onClick = { faenaSeleccionada = f.id; expandFaena = false })
                    }
                }
                // click area
            }
            Box {
                SelectorChip(mesSeleccionado.toString(), onClick = { expandMes = true })
                DropdownMenu(expanded = expandMes, onDismissRequest = { expandMes = false }) {
                    (0..5).map { mesSeleccionado.minusMonths(it.toLong()) }.forEach { m ->
                        DropdownMenuItem(text = { Text(m.toString()) }, onClick = { mesSeleccionado = m; expandMes = false })
                    }
                }
            }
        }

        Spacer(Modifier.height(Medidas.EspacioL))

        // Resumen general
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Medidas.EspacioS)
        ) {
            StatTile("HORAS", totalHoras.toString(), MaterialTheme.estados.cumplida, Modifier.weight(1f))
            StatTile("EXTRA", totalExtras.formatoHoras(), MaterialTheme.colorScheme.primary, Modifier.weight(1f))
            StatTile("ATRASOS", totalAtrasos.toString(), MaterialTheme.estados.atrasada, Modifier.weight(1f))
            StatTile("FALTAS", totalFaltas.toString(), MaterialTheme.estados.falta, Modifier.weight(1f))
        }

        Spacer(Modifier.height(Medidas.EspacioL))

        // Tabla por operario
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.large,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(Medidas.BordeCard, MaterialTheme.colorScheme.outline)
        ) {
            Column(Modifier.padding(vertical = Medidas.EspacioS)) {
                FilaEncabezado()
                HorizontalDivider(color = MaterialTheme.colorScheme.outline)

                if (resumenes.isEmpty()) {
                    Text(
                        text = "No hay operarios registrados",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(Medidas.PaddingCard),
                        textAlign = TextAlign.Center
                    )
                } else {
                    resumenes.forEachIndexed { index, r ->
                        FilaOperario(r, onClick = { onOpenOperario(r.codigo) })
                        if (index < resumenes.lastIndex) {
                            HorizontalDivider(
                                modifier = Modifier.padding(horizontal = Medidas.PaddingCard),
                                color = MaterialTheme.colorScheme.outline.tenue(0.5f)
                            )
                        }
                    }
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                FilaTotal(totalHoras, totalExtras, totalAtrasos, totalFaltas)
            }
        }

        Spacer(Modifier.height(Medidas.EspacioL))

        // Ausentismo
        AusentismoCard(ausentismo)

        Spacer(Modifier.height(Medidas.EspacioL))

        Button(
            onClick = { },
            modifier = Modifier
                .fillMaxWidth()
                .height(Medidas.AlturaBoton),
            shape = MaterialTheme.shapes.medium
        ) {
            Text("EXPORTAR  CSV · PDF", style = MaterialTheme.typography.labelLarge)
        }
    }
}

// ---------------------------------------------------------------
// Componentes
// ---------------------------------------------------------------
@Composable
private fun SelectorChip(texto: String, onClick: () -> Unit = {}) {
    AssistChip(
        onClick = onClick,        label = { Text(texto) },
        trailingIcon = {
            Icon(
                imageVector = Icons.Filled.ArrowDropDown,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
        },
        colors = AssistChipDefaults.assistChipColors(
            containerColor = MaterialTheme.colorScheme.surface,
            labelColor = MaterialTheme.colorScheme.onSurface,
            trailingIconContentColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        border = AssistChipDefaults.assistChipBorder(
            enabled = true,
            borderColor = MaterialTheme.colorScheme.outline
        )
    )
}

@Composable
private fun StatTile(
    etiqueta: String,
    valor: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = color.tenue()),
        border = BorderStroke(Medidas.BordeCard, color.tenue(0.4f))
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(vertical = Medidas.EspacioM, horizontal = Medidas.EspacioS),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = valor,
                style = MaterialTheme.typography.titleLarge,
                color = color,
                maxLines = 1
            )
            Spacer(Modifier.height(Medidas.EspacioXS))
            Text(
                text = etiqueta,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun FilaEncabezado() {
    val estilo = MaterialTheme.typography.labelSmall
    val color = MaterialTheme.colorScheme.onSurfaceVariant
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = Medidas.PaddingCard, vertical = Medidas.EspacioS)
    ) {
        Text("OPERARIO", Modifier.weight(1.4f), style = estilo, color = color)
        Text("HORAS", Modifier.weight(1f), style = estilo, color = color, textAlign = TextAlign.End)
        Text("EXTRA", Modifier.weight(1f), style = estilo, color = color, textAlign = TextAlign.End)
        Text("ATRASOS", Modifier.weight(1f), style = estilo, color = color, textAlign = TextAlign.End)
        Text("FALTAS", Modifier.weight(1f), style = estilo, color = color, textAlign = TextAlign.End)
    }
}

@Composable
private fun FilaOperario(r: ResumenOperario, onClick: () -> Unit) {
    val apagado = MaterialTheme.colorScheme.onSurfaceVariant
    val estilo = MaterialTheme.typography.bodyMedium

    Row(
        Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = Medidas.PaddingCard, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = r.codigo,
            modifier = Modifier.weight(1.4f),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = r.horas.toString(),
            modifier = Modifier.weight(1f),
            style = estilo,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.End
        )
        Text(
            text = r.extras.formatoHoras(),
            modifier = Modifier.weight(1f),
            style = estilo,
            color = if (r.extras > 0) MaterialTheme.colorScheme.primary else apagado,
            textAlign = TextAlign.End
        )
        Text(
            text = r.atrasos.toString(),
            modifier = Modifier.weight(1f),
            style = estilo,
            fontWeight = if (r.atrasos > 0) FontWeight.Bold else FontWeight.Normal,
            color = if (r.atrasos > 0) MaterialTheme.estados.atrasada else apagado,
            textAlign = TextAlign.End
        )
        Text(
            text = r.faltas.toString(),
            modifier = Modifier.weight(1f),
            style = estilo,
            fontWeight = if (r.faltas > 0) FontWeight.Bold else FontWeight.Normal,
            color = if (r.faltas > 0) MaterialTheme.estados.falta else apagado,
            textAlign = TextAlign.End
        )
    }
}

@Composable
private fun FilaTotal(horas: Int, extras: Double, atrasos: Int, faltas: Int) {
    val estilo = MaterialTheme.typography.labelLarge
    val color = MaterialTheme.colorScheme.primary
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = Medidas.PaddingCard, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("TOTAL", Modifier.weight(1.4f), style = estilo, color = color)
        Text(horas.toString(), Modifier.weight(1f), style = estilo, color = color, textAlign = TextAlign.End)
        Text(extras.formatoHoras(), Modifier.weight(1f), style = estilo, color = color, textAlign = TextAlign.End)
        Text(atrasos.toString(), Modifier.weight(1f), style = estilo, color = color, textAlign = TextAlign.End)
        Text(faltas.toString(), Modifier.weight(1f), style = estilo, color = color, textAlign = TextAlign.End)
    }
}

@Composable
private fun AusentismoCard(ausentismo: Float) {
    val color = MaterialTheme.estados.falta

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(Medidas.BordeCard, MaterialTheme.colorScheme.outline)
    ) {
        Column(Modifier.padding(Medidas.PaddingCard)) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "AUSENTISMO DEL PERÍODO",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "%.1f%%".format(ausentismo * 100),
                    style = MaterialTheme.typography.titleLarge,
                    color = color
                )
            }
            Spacer(Modifier.height(Medidas.EspacioM))
            LinearProgressIndicator(
                progress = { ausentismo },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),
                color = color,
                trackColor = MaterialTheme.colorScheme.outline,
                strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
            )
        }
    }
}
