package com.js.smartvolt.asiste.ui.view.admin

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.js.smartvolt.asiste.model.admin.Alerta
import com.js.smartvolt.asiste.model.data.local.DatabaseProvider
import com.js.smartvolt.asiste.ui.components.*
import com.js.smartvolt.asiste.ui.theme.Medidas
import com.js.smartvolt.asiste.ui.theme.deAlerta
import com.js.smartvolt.asiste.ui.theme.estados
import com.js.smartvolt.asiste.ui.theme.tenue

private enum class FiltroAlerta(val label: String) {
    TODAS("Todas"),
    SOSPECHOSAS("Sospechosas"),
    RECHAZADAS("Rechazadas")
}

// ---------------------------------------------------------------
// Helpers de estado (un solo lugar para los textos de la BD)
// ---------------------------------------------------------------
private fun Alerta.esValidada() = estado.equals("VALIDADA", ignoreCase = true)
private fun Alerta.esRechazada() = estado.equals("RECHAZADA", ignoreCase = true)
private fun Alerta.esSospechosa() = estado.contains("SOSPECH", ignoreCase = true)

private fun Alerta.icono(): ImageVector = when {
    esValidada() -> Icons.Filled.Check
    esRechazada() -> Icons.Filled.Close
    esSospechosa() -> Icons.Filled.Warning
    else -> Icons.Filled.Info
}

private fun Alerta.etiqueta(): String = when {
    esValidada() -> "Validada"
    esRechazada() -> "Rechazada"
    esSospechosa() -> "Sospechosa"
    else -> estado.lowercase().replaceFirstChar { it.uppercase() }
}

// ---------------------------------------------------------------
// Pantalla
// ---------------------------------------------------------------
@Composable
fun AdminAlertasView(onOpenDetalle: (String) -> Unit) {
    val context = LocalContext.current
    val alertaDao = DatabaseProvider.getAlertaDao(context)
    val alertas by alertaDao.getAll().collectAsState(initial = emptyList())

    var filtro by remember { mutableStateOf(FiltroAlerta.TODAS) }

    val pendientes = alertas.count { !it.esValidada() && !it.esRechazada() }

    val visibles = remember(alertas, filtro) {
        alertas
            .filter { a ->
                when (filtro) {
                    FiltroAlerta.TODAS -> true
                    FiltroAlerta.SOSPECHOSAS -> a.esSospechosa()
                    FiltroAlerta.RECHAZADAS -> a.esRechazada()
                }
            }
            .sortedBy { it.hora }
    }

    Column(
        Modifier
            .fillMaxSize()
            .padding(horizontal = Medidas.PaddingPantalla, vertical = Medidas.EspacioM)
    ) {
        AdminHeader("ALERTAS DE MARCAS", "($pendientes)")

        Spacer(Modifier.height(Medidas.EspacioM))

        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Medidas.EspacioS)
        ) {
            FiltroAlerta.values().forEach { f ->
                FilterChip(
                    selected = filtro == f,
                    onClick = { filtro = f },
                    label = { Text(f.label) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            }
        }

        Spacer(Modifier.height(Medidas.EspacioM))

        if (visibles.isEmpty()) {
            EmptyAlertas(
                mensaje = if (alertas.isEmpty()) "No hay alertas pendientes"
                else "No hay alertas con este filtro"
            )
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = Medidas.EspacioL)
            ) {
                items(visibles, key = { it.id }) { alerta ->
                    AlertaCard(
                        alerta = alerta,
                        onVerDetalle = { onOpenDetalle(alerta.id.toString()) }
                    )
                }
            }
        }
    }
}

@Composable
private fun AlertaCard(alerta: Alerta, onVerDetalle: () -> Unit) {
    val colorEstado = MaterialTheme.estados.deAlerta(alerta.estado)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(Medidas.BordeCard, MaterialTheme.colorScheme.outline)
    ) {
        Row(Modifier.height(IntrinsicSize.Min)) {
            // Franja lateral con el color del estado
            Box(
                Modifier
                    .fillMaxHeight()
                    .width(Medidas.FranjaEstado)
                    .background(colorEstado)
            )

            Column(
                Modifier
                    .weight(1f)
                    .padding(14.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Avatar con inicial del operario
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = alerta.codigoOperario.toString().take(1).uppercase(),
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }

                    Spacer(Modifier.width(Medidas.EspacioM))

                    Column(Modifier.weight(1f)) {
                        Text(
                            text = alerta.codigoOperario.toString(),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "${alerta.tipoMarca} · ${alerta.hora}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    EstadoBadge(
                        texto = alerta.etiqueta(),
                        icono = alerta.icono(),
                        color = colorEstado
                    )
                }

                Spacer(Modifier.height(10.dp))

                Text(
                    text = alerta.motivo,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(Modifier.height(4.dp))

                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onVerDetalle) {
                        Text("VER DETALLE", style = MaterialTheme.typography.labelLarge)
                    }
                }
            }
        }
    }
}

@Composable
private fun EstadoBadge(
    texto: String,
    icono: ImageVector,
    color: androidx.compose.ui.graphics.Color
) {
    Row(
        modifier = Modifier
            .clip(CircleShape)
            .background(color.tenue())
            .padding(horizontal = 10.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icono,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(14.dp)
        )
        Spacer(Modifier.width(4.dp))
        Text(
            text = texto,
            color = color,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun EmptyAlertas(mensaje: String) {
    Box(
        Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(36.dp)
                )
            }
            Spacer(Modifier.height(Medidas.EspacioM))
            Text(
                text = mensaje,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}