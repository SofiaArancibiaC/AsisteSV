package com.js.smartvolt.asiste.ui.view.admin

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.js.smartvolt.asiste.model.admin.EstadoHorasExtras
import com.js.smartvolt.asiste.model.admin.HoraExtra
import com.js.smartvolt.asiste.model.data.local.DatabaseProvider
import com.js.smartvolt.asiste.ui.components.*
import com.js.smartvolt.asiste.ui.theme.Medidas
import com.js.smartvolt.asiste.ui.theme.estados
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import java.time.LocalDate

@Composable
fun AdminHorasExtraView(codigo: String, onBack: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val horaExtraDao = DatabaseProvider.getHoraExtraDao(context)
    val operario = rememberSeleccionOperario(codigo)

    var fecha by remember { mutableStateOf(LocalDate.now().toString()) }
    var horas by remember { mutableStateOf("1.0") }
    var motivo by remember { mutableStateOf("") }
    var nota by remember { mutableStateOf("") }
    var estado by remember { mutableStateOf(EstadoHorasExtras.PENDIENTE) }
    var mostrarFecha by remember { mutableStateOf(false) }

    val horasExtra by (if (operario.id != 0L) horaExtraDao.getByUsuario(operario.id) else flowOf(emptyList()))
        .collectAsState(initial = emptyList())

    PantallaAdmin("HORAS EXTRA", operario.codigo, onBack) {
        Panel {
            CampoDropdown(
                etiqueta = "Operario",
                valorActual = operario.codigo,
                opciones = operario.operarios,
                textoOpcion = { it.codigo },
                onSeleccion = { operario.elegir(it) }
            )

            Spacer(Modifier.height(Medidas.EspacioM))

            Row(horizontalArrangement = Arrangement.spacedBy(Medidas.EspacioM)) {
                Box(Modifier.weight(1f)) {
                    CampoSelector("FECHA", fecha.ifBlank { "Seleccionar fecha" }) { mostrarFecha = true }
                }
                Box(Modifier.weight(1f)) {
                    CampoTexto("HORAS", horas, { horas = it })
                }
            }

            Spacer(Modifier.height(Medidas.EspacioM))
            CampoTexto("Motivo", motivo, { motivo = it })

            Spacer(Modifier.height(Medidas.EspacioM))
            CampoDropdown(
                etiqueta = "ESTADO",
                valorActual = estado.name,
                opciones = EstadoHorasExtras.values().toList(),
                textoOpcion = { it.name },
                onSeleccion = { estado = it }
            )

            Spacer(Modifier.height(Medidas.EspacioM))
            CampoTexto("Nota", nota, { nota = it }, singleLine = false)
        }

        Spacer(Modifier.height(Medidas.EspacioL))

        BotonPrincipal(
            texto = "GUARDAR HORA EXTRA",
            enabled = operario.id != 0L && horas.toDoubleOrNull() != null
        ) {
            scope.launch {
                horaExtraDao.upsert(
                    HoraExtra(
                        usuarioId = operario.id,
                        fecha = LocalDate.parse(fecha),
                        horas = horas.toDoubleOrNull() ?: 0.0,
                        motivo = motivo,
                        estado = estado,
                        nota = nota
                    )
                )
            }
            motivo = ""
            nota = ""
        }

        Spacer(Modifier.height(Medidas.EspacioL))

        TituloSeccion("HORAS EXTRA REGISTRADAS")

        if (horasExtra.isEmpty()) {
            TextoVacio("Este operario aún no tiene horas extra")
        } else {
            horasExtra.sortedByDescending { it.fecha }.forEach { h ->
                HoraExtraItem(
                    h = h,
                    onCambiarEstado = { nuevo -> scope.launch { horaExtraDao.upsert(h.copy(estado = nuevo)) } },
                    onEliminar = { scope.launch { horaExtraDao.delete(h) } }
                )
            }
        }
    }

    if (mostrarFecha) {
        DialogoFecha(onDismiss = { mostrarFecha = false }, onFechaElegida = { fecha = it })
    }
}

@Composable
private fun HoraExtraItem(
    h: HoraExtra,
    onCambiarEstado: (EstadoHorasExtras) -> Unit,
    onEliminar: () -> Unit
) {
    val color = when (h.estado) {
        EstadoHorasExtras.APROBADO -> MaterialTheme.estados.cumplida
        EstadoHorasExtras.RECHAZADO -> MaterialTheme.estados.rechazada
        else -> MaterialTheme.estados.enRevision
    }

    Panel(Modifier.padding(bottom = 10.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(
                    text = "${h.fecha} · ${h.horas} h",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = h.motivo.ifBlank { "Sin motivo" },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (h.nota.isNotBlank()) {
                    Text(
                        text = h.nota,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            if (h.estado == EstadoHorasExtras.PENDIENTE) {
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    TextButton(onClick = { onCambiarEstado(EstadoHorasExtras.APROBADO) }) { Text("Aprobar") }
                    TextButton(onClick = { onCambiarEstado(EstadoHorasExtras.RECHAZADO) }) { Text("Rechazar") }
                }
            } else {
                BadgeEstado(h.estado.name, color)
            }
            IconButton(onClick = onEliminar) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "Eliminar hora extra",
                    tint = MaterialTheme.estados.rechazada
                )
            }
        }
    }
}