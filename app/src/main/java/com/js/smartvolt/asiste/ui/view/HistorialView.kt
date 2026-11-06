package com.js.smartvolt.asiste.ui.view

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.js.smartvolt.asiste.data.repository.AsistenciaRepository
import com.js.smartvolt.asiste.model.EstadoAsistencia
import com.js.smartvolt.asiste.model.data.local.DatabaseProvider
import com.js.smartvolt.asiste.ui.theme.DegradadoFondo
import com.js.smartvolt.asiste.ui.theme.EstadoColors
import com.js.smartvolt.asiste.ui.theme.Medidas
import com.js.smartvolt.asiste.ui.theme.degradadoFondo
import com.js.smartvolt.asiste.ui.theme.estados
import com.js.smartvolt.asiste.ui.theme.tenue
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

// ---------------------------------------------------------------
// Helpers de estado
// ---------------------------------------------------------------
private fun EstadoColors.deAsistencia(estado: EstadoAsistencia): Color = when (estado) {
    EstadoAsistencia.CUMPLIDA -> cumplida
    EstadoAsistencia.ATRASADA -> atrasada
    EstadoAsistencia.FALTA -> falta
    EstadoAsistencia.EN_REVISION -> enRevision
    EstadoAsistencia.RECHAZADA -> rechazada
    else -> falta
}

private fun EstadoAsistencia.etiqueta(): String =
    name.replace("_", " ").lowercase().replaceFirstChar { it.uppercase() }

private val formatoFecha: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

// ---------------------------------------------------------------
// Pantalla
// ---------------------------------------------------------------
@Composable
fun HistorialView() {
    val context = LocalContext.current
    val repo = remember { AsistenciaRepository(DatabaseProvider.getAsistenciaDao(context)) }
    val attendances by repo.getAll().collectAsState(initial = emptyList())

    val ordenadas = remember(attendances) { attendances.sortedByDescending { it.fecha } }
    val cumplidas = attendances.count { it.estado == EstadoAsistencia.CUMPLIDA }
    val atrasos = attendances.count { it.estado == EstadoAsistencia.ATRASADA }
    val faltas = attendances.count { it.estado == EstadoAsistencia.FALTA }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.degradadoFondo)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Text(
            text = "HISTORIAL DE MARCAS",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(
                start = Medidas.PaddingPantalla,
                end = Medidas.PaddingPantalla,
                top = Medidas.EspacioL,
                bottom = Medidas.EspacioM
            )
        )

        // Resumen
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = Medidas.PaddingPantalla),
            horizontalArrangement = Arrangement.spacedBy(Medidas.EspacioS)
        ) {
            ResumenTile("CUMPLIDAS", cumplidas.toString(), MaterialTheme.estados.cumplida, Modifier.weight(1f))
            ResumenTile("ATRASOS", atrasos.toString(), MaterialTheme.estados.atrasada, Modifier.weight(1f))
            ResumenTile("FALTAS", faltas.toString(), MaterialTheme.estados.falta, Modifier.weight(1f))
        }

        Spacer(Modifier.height(Medidas.EspacioM))

        if (ordenadas.isEmpty()) {
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Aún no hay marcas registradas",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = Medidas.PaddingPantalla,
                    end = Medidas.PaddingPantalla,
                    bottom = Medidas.EspacioL
                ),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(ordenadas) { a ->
                    val color = MaterialTheme.estados.deAsistencia(a.estado)
                    val dia = a.fecha.dayOfWeek
                        .getDisplayName(TextStyle.FULL, Locale("es", "CL"))
                        .replaceFirstChar { it.uppercase() }

                    MarcaCard(
                        dia = dia,
                        fecha = a.fecha.format(formatoFecha),
                        marca = a.registro?.toString() ?: "--:--",
                        estado = a.estado.etiqueta(),
                        color = color
                    )
                }
            }
        }
    }
}

// Componentes
@Composable
private fun ResumenTile(
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
private fun MarcaCard(
    dia: String,
    fecha: String,
    marca: String,
    estado: String,
    color: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(Medidas.BordeCard, MaterialTheme.colorScheme.outline)
    ) {
        Row(Modifier.height(IntrinsicSize.Min)) {
            // Franja lateral con el color del estado
            Box(
                Modifier
                    .fillMaxHeight()
                    .width(Medidas.FranjaEstado)
                    .background(color)
            )

            Row(
                Modifier
                    .weight(1f)
                    .padding(Medidas.PaddingCard),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(Modifier.weight(1f)) {
                    Text(
                        text = dia,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = fecha,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(Medidas.EspacioXS))
                    Text(
                        text = "Marca: $marca",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                BadgeEstado(estado, color)
            }
        }
    }
}

@Composable
private fun BadgeEstado(texto: String, color: Color) {
    Box(
        modifier = Modifier
            .clip(CircleShape)
            .background(color.tenue())
            .padding(horizontal = 12.dp, vertical = 5.dp)
    ) {
        Text(
            text = texto,
            color = color,
            style = MaterialTheme.typography.labelMedium
        )
    }
}
