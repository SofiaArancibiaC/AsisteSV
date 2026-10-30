package com.js.smartvolt.asiste.ui.view

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.js.smartvolt.asiste.ui.theme.AmarilloFaena
import com.js.smartvolt.asiste.ui.theme.AsisteSVTheme
import com.js.smartvolt.asiste.ui.theme.NegroFaena
import com.js.smartvolt.asiste.ui.theme.estados
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
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // ---------- Cabecera negra ----------
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(NegroFaena)
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Text(
                text = "ASISTENCIA OBRA",
                style = MaterialTheme.typography.titleLarge,
                color = AmarilloFaena
            )
            Text(
                text = "$codigoOperario · $nombreFaena",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White
            )
        }
        FranjaPrecaucion()

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
                    fondo = MaterialTheme.colorScheme.surfaceVariant,
                    contenido = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(Modifier.weight(1f))

            Button(
                onClick = onMarcarEntrada,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(72.dp),
                shape = MaterialTheme.shapes.large,
                border = BorderStroke(2.dp, NegroFaena)
            ) {
                Text(
                    text = "MARCAR ENTRADA",
                    style = MaterialTheme.typography.titleLarge
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
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "${inicio.format(hora)} – ${fin.format(hora)}",
                style = MaterialTheme.typography.headlineMedium
            )
            Etiqueta(
                texto = "TURNO PROGRAMADO · $frente",
                fondo = MaterialTheme.colorScheme.primary,
                contenido = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

@Composable
private fun TarjetaValidacion(v: ValidacionPresencia) {
    val baliza = if (v.balizaDetectada) {
        "✓ ${v.rssiDbm ?: "--"} dBm" to true
    } else {
        "✕ NO DETECTADA" to false
    }

    TarjetaConBorde {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "VALIDACIÓN DE PRESENCIA",
                style = MaterialTheme.typography.titleMedium
            )

            FilaValidacion(
                etiqueta = "GPS dentro de la faena",
                resultado = if (v.gpsDentroFaena) "✓ SÍ" else "✕ NO",
                correcto = v.gpsDentroFaena
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            FilaValidacion(
                etiqueta = "Baliza BLE de la faena",
                resultado = baliza.first,
                correcto = baliza.second
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
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
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = etiqueta, style = MaterialTheme.typography.bodyLarge)
        Etiqueta(
            texto = resultado,
            fondo = if (correcto) MaterialTheme.estados.cumplida else MaterialTheme.estados.falta,
            contenido = MaterialTheme.estados.onEstado
        )
    }
}

/** Tarjeta blanca con borde grueso, como en el mockup. */
@Composable
private fun TarjetaConBorde(contenido: @Composable () -> Unit) {
    OutlinedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        border = BorderStroke(2.dp, MaterialTheme.colorScheme.outline),
        colors = CardDefaults.outlinedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
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

/** Franja diagonal amarilla y negra bajo la cabecera. */
@Composable
private fun FranjaPrecaucion() {
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(10.dp)
            .clipToBounds()
    ) {
        drawRect(AmarilloFaena)
        val paso = 14.dp.toPx()
        var x = -size.height
        while (x < size.width) {
            val franja = Path().apply {
                moveTo(x, size.height)
                lineTo(x + paso / 2, size.height)
                lineTo(x + paso / 2 + size.height, 0f)
                lineTo(x + size.height, 0f)
                close()
            }
            drawPath(franja, NegroFaena)
            x += paso
        }
    }
}

// ============================================================
// Preview con datos ficticios
// ============================================================

@Preview(showBackground = true, heightDp = 780)
@Composable
private fun InicioScreenPreview() {
    AsisteSVTheme() {
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