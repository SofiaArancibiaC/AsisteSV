package com.js.smartvolt.asiste.ui.view.capataz

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.js.smartvolt.asiste.model.data.local.DatabaseProvider
import com.js.smartvolt.asiste.ui.theme.Medidas
import com.js.smartvolt.asiste.ui.theme.degradadoFondo
import com.js.smartvolt.asiste.ui.theme.estados
import com.js.smartvolt.asiste.ui.theme.inicial
import com.js.smartvolt.asiste.ui.theme.tenue

@Composable
fun CapatazPanelView(
    onOpenAlertas: () -> Unit,
    onOpenOperarios: () -> Unit,
    onOpenReporte: () -> Unit
) {
    val context = LocalContext.current
    val faenaDao = DatabaseProvider.getFaenaDao(context)
    val faenas by faenaDao.getAll().collectAsState(initial = emptyList())
    val faena = faenas.firstOrNull()

    Box(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.degradadoFondo)
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Medidas.PaddingPantalla, vertical = Medidas.EspacioL)
        ) {
            // ---------- Encabezado ----------
            Text(
                "Panel Capataz",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                "Resumen de la faena de hoy",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(Medidas.EspacioL))

            // ---------- Tarjeta de faena ----------
            if (faena != null) {
                ResumenFaenaCard(
                    nombre = faena.nombre,
                    presentes = faena.presentes,
                    total = faena.total
                )
            } else {
                SinFaenaCard()
            }

            Spacer(Modifier.height(20.dp))

            // ---------- Accesos ----------
            Text(
                "Gestión",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(Modifier.height(Medidas.EspacioM))

            AccionCard(
                titulo = "Operarios",
                subtitulo = "Revisa y administra a tu equipo",
                icono = Icons.Filled.Person,
                acento = MaterialTheme.estados.cumplida,
                onClick = onOpenOperarios
            )
            Spacer(Modifier.height(Medidas.EspacioM))
            AccionCard(
                titulo = "Alertas",
                subtitulo = "Marcas pendientes, faltas y rechazos",
                icono = Icons.Filled.Notifications,
                acento = MaterialTheme.estados.falta,
                onClick = onOpenAlertas
            )
            Spacer(Modifier.height(Medidas.EspacioM))
            AccionCard(
                titulo = "Reportes",
                subtitulo = "Asistencia por periodo",
                icono = Icons.Filled.DateRange,
                acento = MaterialTheme.colorScheme.secondary,
                onClick = onOpenReporte
            )

            Spacer(Modifier.height(Medidas.EspacioL))
        }
    }
}

// =====================================================================
// Componentes
// =====================================================================

@Composable
private fun ResumenFaenaCard(
    nombre: String,
    presentes: Int,
    total: Int
) {
    val ausentes = (total - presentes).coerceAtLeast(0)
    val progreso = if (total > 0) (presentes.toFloat() / total).coerceIn(0f, 1f) else 0f
    val porcentaje = (progreso * 100).toInt()

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(Medidas.BordeCard, MaterialTheme.colorScheme.outlineVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(Modifier.padding(Medidas.PaddingCard + 2.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(Medidas.AvatarS)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        nombre.inicial(),
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                Spacer(Modifier.width(Medidas.EspacioM))
                Column(Modifier.weight(1f)) {
                    Text(
                        nombre,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        "Faena activa",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    "$porcentaje%",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.estados.cumplida
                )
            }

            Spacer(Modifier.height(Medidas.EspacioL))

            LinearProgressIndicator(
                progress = { progreso },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(50)),
                color = MaterialTheme.estados.cumplida,
                trackColor = MaterialTheme.estados.cumplida.tenue(0.18f)
            )

            Spacer(Modifier.height(Medidas.EspacioL))

            Row(horizontalArrangement = Arrangement.spacedBy(Medidas.EspacioM)) {
                StatChip(
                    modifier = Modifier.weight(1f),
                    etiqueta = "Presentes",
                    valor = presentes.toString(),
                    color = MaterialTheme.estados.cumplida
                )
                StatChip(
                    modifier = Modifier.weight(1f),
                    etiqueta = "Ausentes",
                    valor = ausentes.toString(),
                    color = MaterialTheme.estados.falta
                )
                StatChip(
                    modifier = Modifier.weight(1f),
                    etiqueta = "Total",
                    valor = total.toString(),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun StatChip(
    modifier: Modifier = Modifier,
    etiqueta: String,
    valor: String,
    color: Color
) {
    Column(
        modifier = modifier
            .clip(MaterialTheme.shapes.medium)
            .background(color.tenue(0.12f))
            .padding(vertical = Medidas.EspacioM, horizontal = Medidas.EspacioS),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            valor,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            etiqueta,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun SinFaenaCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(Medidas.BordeCard, MaterialTheme.colorScheme.outlineVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(Modifier.padding(Medidas.PaddingCard + 2.dp)) {
            Text(
                "Sin faena asignada",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(Modifier.height(Medidas.EspacioXS))
            Text(
                "Cuando tengas una faena activa, verás aquí la asistencia del día.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun AccionCard(
    titulo: String,
    subtitulo: String,
    icono: ImageVector,
    acento: Color,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(Medidas.BordeCard, MaterialTheme.colorScheme.outlineVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Medidas.PaddingCard),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .background(acento.tenue(0.16f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icono,
                    contentDescription = null,
                    tint = acento
                )
            }

            Spacer(Modifier.width(Medidas.EspacioL))

            Column(Modifier.weight(1f)) {
                Text(
                    titulo,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    subtitulo,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Icon(
                imageVector = Icons.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}