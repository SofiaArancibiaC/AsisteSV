package com.js.smartvolt.asiste.ui.view

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.js.smartvolt.asiste.ui.theme.AsisteSVTheme
import com.js.smartvolt.asiste.ui.theme.DegradadoFondo
import com.js.smartvolt.asiste.ui.theme.DegradadoMarca
import com.js.smartvolt.asiste.ui.theme.LocalEstadoColors
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

private val LOCALE_ES: Locale = Locale.forLanguageTag("es-CL")

/** Resultado de las validaciones de presencia que se muestran en la tarjeta. */
data class ValidacionPresencia(
    val gpsDentroFaena: Boolean,
    val balizaDetectada: Boolean,
    val rssiDbm: Int? = null,
    val ubicacionSimulada: Boolean
)

@Composable
fun InicioView(
    codigoOperario: String,
    nombreFaena: String,
    fecha: LocalDate,
    inicioTurno: LocalTime,
    finTurno: LocalTime,
    frente: String,
    validacion: ValidacionPresencia,
    sinConexion: Boolean,
    onMarcarEntrada: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cs = MaterialTheme.colorScheme

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DegradadoFondo)
    ) {
        // ---------- Cabecera ----------
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Text(
                text = "ASISTENCIA OBRA",
                style = MaterialTheme.typography.titleLarge,
                color = cs.primary
            )
            Text(
                text = "$codigoOperario · $nombreFaena",
                style = MaterialTheme.typography.bodyMedium,
                color = cs.onSurfaceVariant
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

        // ---------- Contenido ----------
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            TarjetaTurno(
                fecha = fecha,
                inicio = inicioTurno,
                fin = finTurno,
                frente = frente
            )

            TarjetaValidacion(validacion)

            if (sinConexion) {
                Etiqueta(
                    texto = "Sin conexión · se guardará en el teléfono",
                    fondo = cs.outline,
                    contenido = cs.onSurface
                )
            }

            Spacer(Modifier.weight(1f))

            // Botón principal con el degradado de marca
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(72.dp)
                    .clip(MaterialTheme.shapes.large)
                    .background(DegradadoMarca)
                    .clickable { onMarcarEntrada() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "MARCAR ENTRADA",
                    style = MaterialTheme.typography.titleLarge,
                    color = cs.onPrimary
                )
            }
        }
    }
}

// ============================================================
// Componentes privados (solo los usa esta pantalla)
// ============================================================

@Composable
private fun TarjetaTurno(
    fecha: LocalDate,
    inicio: LocalTime,
    fin: LocalTime,
    frente: String
) {
    val cs = MaterialTheme.colorScheme
    val hora = DateTimeFormatter.ofPattern("HH:mm")
    val prefijo = if (fecha == LocalDate.now()) "HOY · " else ""
    val fechaTexto = prefijo + fecha
        .format(DateTimeFormatter.ofPattern("EEEE d 'de' MMMM", LOCALE_ES))
        .uppercase(LOCALE_ES)

    TarjetaConBorde {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = fechaTexto,
                style = MaterialTheme.typography.labelMedium,
                color = cs.onSurfaceVariant
            )
            Text(
                text = "${inicio.format(hora)} – ${fin.format(hora)}",
                style = MaterialTheme.typography.headlineMedium,
                color = cs.onSurface
            )
            Etiqueta(
                texto = "TURNO PROGRAMADO · $frente",
                fondo = cs.primary,
                contenido = cs.onPrimary
            )
        }
    }
}

@Composable
private fun TarjetaValidacion(v: ValidacionPresencia) {
    val cs = MaterialTheme.colorScheme
    val baliza = if (v.balizaDetectada) {
        "✓ ${v.rssiDbm ?: "--"} dBm" to true
    } else {
        "✕ NO DETECTADA" to false
    }

    TarjetaConBorde {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "VALIDACIÓN DE PRESENCIA",
                style = MaterialTheme.typography.titleMedium,
                color = cs.onSurface
            )

            FilaValidacion(
                etiqueta = "GPS dentro de la faena",
                resultado = if (v.gpsDentroFaena) "✓ SÍ" else "✕ NO",
                correcto = v.gpsDentroFaena
            )
            HorizontalDivider(color = cs.outline)
            FilaValidacion(
                etiqueta = "Baliza BLE de la faena",
                resultado = baliza.first,
                correcto = baliza.second
            )
            HorizontalDivider(color = cs.outline)
            // Aquí lo correcto es que NO haya ubicación simulada
            FilaValidacion(
                etiqueta = "Ubicación simulada",
                resultado = if (v.ubicacionSimulada) "✕ SÍ" else "✓ NO",
                correcto = !v.ubicacionSimulada
            )
        }
    }
}

@Composable
private fun FilaValidacion(etiqueta: String, resultado: String, correcto: Boolean) {
    val estados = LocalEstadoColors.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = etiqueta,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        Etiqueta(
            texto = resultado,
            fondo = if (correcto) estados.cumplida else estados.falta,
            contenido = estados.sobreEstado
        )
    }
}

/** Tarjeta oscura con borde, como los campos del diseño. */
@Composable
private fun TarjetaConBorde(contenido: @Composable () -> Unit) {
    val cs = MaterialTheme.colorScheme
    OutlinedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        border = BorderStroke(1.5.dp, cs.outline),
        colors = CardDefaults.outlinedCardColors(
            containerColor = cs.surface,
            contentColor = cs.onSurface
        )
    ) {
        contenido()
    }
}

/** Píldora de texto (estado, turno, aviso). */
@Composable
private fun Etiqueta(texto: String, fondo: Color, contenido: Color) {
    Surface(
        color = fondo,
        contentColor = contenido,
        shape = MaterialTheme.shapes.small
    ) {
        Text(
            text = texto,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelMedium
        )
    }
}

// ============================================================
// Preview con datos ficticios
// ============================================================

@Preview(showBackground = true, heightDp = 780)
@Composable
private fun InicioScreenPreview() {
    AsisteSVTheme {
        InicioView(
            codigoOperario = "OP-014",
            nombreFaena = "Faena Ruta 5 Norte",
            fecha = LocalDate.now(),
            inicioTurno = LocalTime.of(8, 0),
            finTurno = LocalTime.of(18, 0),
            frente = "Frente 2",
            validacion = ValidacionPresencia(
                gpsDentroFaena = true,
                balizaDetectada = true,
                rssiDbm = -62,
                ubicacionSimulada = false
            ),
            sinConexion = true,
            onMarcarEntrada = {}
        )
    }
}