package com.js.smartvolt.asiste.ui.view.admin

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.js.smartvolt.asiste.model.admin.Alerta
import com.js.smartvolt.asiste.model.data.local.DatabaseProvider
import com.js.smartvolt.asiste.ui.components.*
import com.js.smartvolt.asiste.ui.theme.Medidas
import com.js.smartvolt.asiste.ui.theme.colorsCampo
import com.js.smartvolt.asiste.ui.theme.deAlerta
import com.js.smartvolt.asiste.ui.theme.estados
import com.js.smartvolt.asiste.ui.theme.inicial
import com.js.smartvolt.asiste.ui.theme.tenue
import kotlinx.coroutines.launch

// ---------------------------------------------------------------
// Helpers de estado (mismos textos de la BD que en AdminAlertasView)
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
fun AdminDetalleAlertaView(id: String, onBack: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val alertaDao = DatabaseProvider.getAlertaDao(context)
    val alertas by alertaDao.getAll().collectAsState(initial = emptyList())
    val alerta = alertas.firstOrNull { it.id.toString() == id }

    var nota by remember { mutableStateOf("") }

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = Medidas.PaddingPantalla, vertical = Medidas.EspacioM)
    ) {
        if (alerta == null) {
            AdminHeader("DETALLE DE ALERTA", onBack = onBack)
            Spacer(Modifier.height(Medidas.EspacioL))
            Text(
                text = "Alerta no encontrada",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Medidas.EspacioL),
                textAlign = TextAlign.Center
            )
            return@Column
        }

        val colorEstado = MaterialTheme.estados.deAlerta(alerta.estado)
        val resuelta = alerta.esValidada() || alerta.esRechazada()
        val alertasOperario = alertas.count { it.codigoOperario == alerta.codigoOperario }

        AdminHeader("DETALLE DE ALERTA", alerta.codigoOperario.toString(), onBack)

        Spacer(Modifier.height(Medidas.EspacioL))

        // Resumen de la alerta
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.large,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(Medidas.BordeCard, MaterialTheme.colorScheme.outline)
        ) {
            Row(Modifier.height(IntrinsicSize.Min)) {
                Box(
                    Modifier
                        .fillMaxHeight()
                        .width(Medidas.FranjaEstado)
                        .background(colorEstado)
                )
                Row(
                    Modifier
                        .weight(1f)
                        .padding(Medidas.PaddingCard),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(Medidas.AvatarS)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = alerta.codigoOperario.toString().inicial(),
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }

                    Spacer(Modifier.width(Medidas.EspacioM))

                    Column(Modifier.weight(1f)) {
                        Text(
                            text = alerta.codigoOperario.toString(),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
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
            }
        }

        Spacer(Modifier.height(Medidas.EspacioM))

        // Motivo
        SeccionCard("MOTIVO DE LA ALERTA") {
            Text(
                text = alerta.motivo,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Spacer(Modifier.height(Medidas.EspacioM))

        // Datos de la marca
        SeccionCard("DATOS DE LA MARCA") {
            FilaDato("Resultado del sistema", alerta.etiqueta(), colorEstado)
            DivisorSuave()
            FilaDato("Tipo de marca", alerta.tipoMarca.toString())
            DivisorSuave()
            FilaDato("Hora", alerta.hora.toString())
            DivisorSuave()
            FilaDato("Faena ID", alerta.faenaId.toString())
        }

        Spacer(Modifier.height(Medidas.EspacioM))

        // Historial
        SeccionCard("HISTORIAL DEL OPERARIO") {
            FilaDato(
                etiqueta = "Alertas registradas",
                valor = alertasOperario.toString(),
                colorValor = if (alertasOperario > 1) MaterialTheme.estados.falta else null
            )
        }

        Spacer(Modifier.height(Medidas.EspacioM))

        // Nota del admin
        OutlinedTextField(
            value = nota,
            onValueChange = { nota = it },
            label = { Text("Nota del admin (opcional)") },
            minLines = 3,
            shape = MaterialTheme.shapes.medium,
            colors = colorsCampo(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(Medidas.EspacioL))

        // Acciones
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Medidas.EspacioM)
        ) {
            OutlinedButton(
                onClick = {
                    alerta?.let { a ->
                        scope.launch { alertaDao.upsert(a.copy(estado = "RECHAZADA")) }
                    }
                },
                enabled = !resuelta,
                modifier = Modifier
                    .weight(1f)
                    .height(Medidas.AlturaBoton),
                shape = MaterialTheme.shapes.medium,
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.estados.rechazada
                ),
                border = BorderStroke(
                    Medidas.BordeCard,
                    if (resuelta) MaterialTheme.colorScheme.outline
                    else MaterialTheme.estados.rechazada
                )
            ) {
                Text("RECHAZAR", style = MaterialTheme.typography.labelLarge)
            }

            Button(
                onClick = {
                    alerta?.let { a ->
                        scope.launch { alertaDao.upsert(a.copy(estado = "VALIDADA")) }
                    }
                },
                enabled = !resuelta,
                modifier = Modifier
                    .weight(1f)
                    .height(Medidas.AlturaBoton),
                shape = MaterialTheme.shapes.medium,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.estados.cumplida,
                    contentColor = MaterialTheme.estados.onEstado
                )
            ) {
                Text("VALIDAR", style = MaterialTheme.typography.labelLarge)
            }
        }

        if (resuelta) {
            Spacer(Modifier.height(Medidas.EspacioS))
            Text(
                text = "Esta alerta ya fue resuelta.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }

        Spacer(Modifier.height(Medidas.EspacioL))
    }
}

// ---------------------------------------------------------------
// Componentes
// ---------------------------------------------------------------
@Composable
private fun SeccionCard(titulo: String, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(Medidas.BordeCard, MaterialTheme.colorScheme.outline)
    ) {
        Column(Modifier.padding(Medidas.PaddingCard)) {
            Text(
                text = titulo,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(Medidas.EspacioS))
            content()
        }
    }
}

@Composable
private fun FilaDato(etiqueta: String, valor: String, colorValor: Color? = null) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = etiqueta,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = valor,
            style = MaterialTheme.typography.titleMedium,
            color = colorValor ?: MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun DivisorSuave() {
    HorizontalDivider(color = MaterialTheme.colorScheme.outline.tenue(0.5f))
}

@Composable
private fun EstadoBadge(texto: String, icono: ImageVector, color: Color) {
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
            style = MaterialTheme.typography.labelMedium
        )
    }
}
