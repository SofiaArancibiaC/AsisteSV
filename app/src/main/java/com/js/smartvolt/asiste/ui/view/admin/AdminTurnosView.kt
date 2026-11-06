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
import com.js.smartvolt.asiste.model.admin.Turno
import com.js.smartvolt.asiste.model.data.local.DatabaseProvider
import com.js.smartvolt.asiste.ui.components.*
import com.js.smartvolt.asiste.ui.theme.Medidas
import com.js.smartvolt.asiste.ui.theme.estados
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime

/** Hora de inicio → texto que se muestra en el dropdown. */
private val HORARIOS = listOf(
    "08:00" to "AM 08:00-14:00",
    "15:00" to "PM 15:00-21:00"
)

@Composable
fun AdminTurnosView(codigo: String, onBack: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val turnoDao = DatabaseProvider.getTurnoDao(context)
    val operario = rememberSeleccionOperario(codigo)

    var dia by remember { mutableStateOf(LocalDate.now().toString()) }
    var horario by remember { mutableStateOf(HORARIOS.first().first) }
    var activo by remember { mutableStateOf(true) }
    var mostrarFecha by remember { mutableStateOf(false) }

    val turnos by (if (operario.id != 0L) turnoDao.getByUsuario(operario.id) else flowOf(emptyList()))
        .collectAsState(initial = emptyList())

    PantallaAdmin("ASIGNAR TURNOS", operario.codigo, onBack) {
        Panel {
            CampoTexto("Operario", operario.codigo, readOnly = true)

            Spacer(Modifier.height(Medidas.EspacioM))

            Row(horizontalArrangement = Arrangement.spacedBy(Medidas.EspacioM)) {
                Box(Modifier.weight(1f)) {
                    CampoSelector("DÍA", dia.ifBlank { "Seleccionar día" }) { mostrarFecha = true }
                }
                Box(Modifier.weight(1f)) {
                    CampoDropdown(
                        etiqueta = "TURNO",
                        valorActual = HORARIOS.firstOrNull { it.first == horario }?.second ?: horario,
                        opciones = HORARIOS,
                        textoOpcion = { it.second },
                        onSeleccion = { horario = it.first }
                    )
                }
            }

            Spacer(Modifier.height(Medidas.EspacioM))

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Turno activo",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Switch(checked = activo, onCheckedChange = { activo = it })
            }
        }

        Spacer(Modifier.height(Medidas.EspacioL))

        BotonPrincipal("GUARDAR TURNO", enabled = operario.id != 0L) {
            scope.launch {
                turnoDao.upsert(
                    Turno(
                        usuarioId = operario.id,
                        dia = LocalDate.parse(dia),
                        activo = activo,
                        horario = LocalTime.parse(horario)
                    )
                )
            }
        }

        Spacer(Modifier.height(Medidas.EspacioL))

        TituloSeccion("TURNOS ASIGNADOS")

        if (turnos.isEmpty()) {
            TextoVacio("Este operario aún no tiene turnos")
        } else {
            turnos.sortedBy { it.dia }.forEach { t ->
                TurnoItem(t, onEliminar = { scope.launch { turnoDao.delete(t) } })
            }
        }
    }

    if (mostrarFecha) {
        DialogoFecha(onDismiss = { mostrarFecha = false }, onFechaElegida = { dia = it })
    }
}

@Composable
private fun TurnoItem(t: Turno, onEliminar: () -> Unit) {
    val color = if (t.activo) MaterialTheme.estados.cumplida else MaterialTheme.estados.enRevision

    Panel(Modifier.padding(bottom = 10.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(
                    text = "${t.dia}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${t.horario}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            BadgeEstado(if (t.activo) "Activo" else "Inactivo", color)
            IconButton(onClick = onEliminar) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "Eliminar turno",
                    tint = MaterialTheme.estados.rechazada
                )
            }
        }
    }
}