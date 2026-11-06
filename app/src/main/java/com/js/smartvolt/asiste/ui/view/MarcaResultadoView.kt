package com.js.smartvolt.asiste.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.graphicsLayer
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.js.smartvolt.asiste.ui.theme.AsisteSVTheme
import com.js.smartvolt.asiste.ui.theme.DegradadoFondo
import com.js.smartvolt.asiste.ui.theme.DegradadoMarca
import com.js.smartvolt.asiste.ui.theme.LocalEstadoColors
import com.js.smartvolt.asiste.ui.theme.degradadoFondo

/** Una fila de la validación de presencia (GPS, baliza BLE, ubicación simulada). */
data class CheckPresencia(
    val etiqueta: String,
    val valor: String,
    val ok: Boolean
)


/** Datos que necesita la pantalla para mostrar una marca aprobada o rechazada. */
data class ResultadoMarca(
    val aprobada: Boolean,
    val titulo: String,
    val subtitulo: String,
    val checks: List<CheckPresencia>,
    val mensaje: String,
    val nota: String? = null,
    val textoBoton: String
) {
    companion object {
        /** Marca válida. [rssi] es opcional: si no hay baliza BLE, no se muestra esa fila. */
        fun aprobada(
            hora: String,
            rssi: Int? = null,
            sinConexion: Boolean = false,
            atrasada: Boolean = false
        ): ResultadoMarca {
            val checks = buildList {
                add(CheckPresencia("GPS dentro de la faena", "SÍ", true))
                if (rssi != null) add(CheckPresencia("Baliza BLE de la faena", "$rssi dBm", true))
                add(CheckPresencia("Ubicación simulada", "NO", true))
            }
            return ResultadoMarca(
                aprobada = true,
                titulo = "MARCA REGISTRADA",
                subtitulo = "Entrada registrada a las $hora",
                checks = checks,
                mensaje = if (atrasada) "Tu asistencia quedó guardada con atraso."
                else "Tu asistencia quedó guardada correctamente.",
                nota = when {
                    sinConexion -> "Sin conexión · se guardará en el teléfono"
                    atrasada -> "Marca fuera de la tolerancia del turno"
                    else -> null
                },
                textoBoton = "VOLVER AL INICIO"
            )
        }

        /** Marca rechazada por ubicación simulada (Fake GPS). */
        fun rechazadaPorFakeGps(gpsEnFaena: Boolean = true) = ResultadoMarca(
            aprobada = false,
            titulo = "MARCA RECHAZADA",
            subtitulo = "Se detectó una ubicación simulada",
            checks = listOf(
                CheckPresencia("GPS en faena", if (gpsEnFaena) "SÍ" else "NO", gpsEnFaena),
                CheckPresencia("Baliza BLE", "NO DETECTADA", false),
                CheckPresencia("Mock Location", "ACTIVO", false)
            ),
            mensaje = "Desactiva las ubicaciones simuladas y el modo desarrollador. " +
                    "Luego acércate a la baliza de tu frente.",
            nota = "El intento quedó registrado.",
            textoBoton = "REINTENTAR"
        )

        /** Marca rechazada porque el operario está fuera del radio de la faena. */
        fun rechazadaFueraDeFaena(distanciaMetros: Int) = ResultadoMarca(
            aprobada = false,
            titulo = "MARCA RECHAZADA",
            subtitulo = "Estás fuera de la faena",
            checks = listOf(
                CheckPresencia("GPS dentro de la faena", "NO · A $distanciaMetros m", false),
                CheckPresencia("Ubicación simulada", "NO", true)
            ),
            mensaje = "Acércate a la faena y vuelve a intentar.",
            nota = "El intento quedó registrado.",
            textoBoton = "REINTENTAR"
        )

        /** No se pudo leer el GPS (sin permiso, GPS apagado o error). No se guarda nada. */
        fun sinUbicacion(motivo: String) = ResultadoMarca(
            aprobada = false,
            titulo = "SIN UBICACIÓN",
            subtitulo = "No se pudo leer tu ubicación",
            checks = listOf(
                CheckPresencia("GPS dentro de la faena", "SIN DATOS", false)
            ),
            mensaje = motivo,
            nota = null,
            textoBoton = "REINTENTAR"
        )
        /** Intenta marcar salida sin haber marcado entrada. */
        fun sinEntrada() = ResultadoMarca(
            aprobada = false,
            titulo = "SIN ENTRADA",
            subtitulo = "No registraste tu entrada hoy",
            checks = listOf(
                CheckPresencia("Entrada del día", "NO REGISTRADA", false)
            ),
            mensaje = "Primero marca tu entrada y luego la salida.",
            nota = null,
            textoBoton = "ENTENDIDO"
        )

        /** Salida registrada correctamente. */
        fun salidaRegistrada(hora: String) = ResultadoMarca(
            aprobada = true,
            titulo = "SALIDA REGISTRADA",
            subtitulo = "Salida registrada a las $hora",
            checks = listOf(
                CheckPresencia("GPS dentro de la faena", "SÍ", true),
                CheckPresencia("Ubicación simulada", "NO", true)
            ),
            mensaje = "Tu jornada quedó cerrada correctamente.",
            nota = null,
            textoBoton = "VOLVER AL INICIO"
        )
    }
}

@Composable
fun MarcaResultadoScreen(
    resultado: ResultadoMarca,
    onAccion: () -> Unit
) {
    val cs = MaterialTheme.colorScheme
    val estados = LocalEstadoColors.current
    val colorPrincipal = if (resultado.aprobada) estados.cumplida else estados.falta

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.degradadoFondo)
            .navigationBarsPadding()
    ) {
        // Encabezado (respeta la barra de estado)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Text(
                text = if (resultado.aprobada) "MARCA VÁLIDA" else "MARCA NO VÁLIDA",
                color = cs.primary,
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = if (resultado.aprobada) "Entrada registrada" else "Entrada rechazada",
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
                .padding(16.dp)
        ) {
            // Contenido que puede hacer scroll en pantallas pequeñas
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(8.dp))

                // Círculo animado con ✓ o ✕ dibujado trazo a trazo
                IconoResultadoAnimado(
                    aprobada = resultado.aprobada,
                    color = colorPrincipal,
                    colorSimbolo = estados.sobreEstado,
                    clave= resultado.subtitulo,
                    modifier = Modifier.size(120.dp)
                )

                Spacer(Modifier.height(14.dp))

                Text(
                    text = resultado.titulo,
                    color = colorPrincipal,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = resultado.subtitulo,
                    color = cs.onSurface,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(16.dp))

                // Tarjeta con las validaciones
                val forma = RoundedCornerShape(12.dp)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(forma)
                        .background(cs.surface)
                        .border(1.5.dp, cs.outline, forma)
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    resultado.checks.forEachIndexed { index, check ->
                        FilaCheck(check)
                        if (index < resultado.checks.lastIndex) {
                            HorizontalDivider(color = cs.outline)
                        }
                    }
                }

                Spacer(Modifier.height(14.dp))

                Text(
                    text = resultado.mensaje,
                    color = cs.onSurfaceVariant,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )

                resultado.nota?.let { nota ->
                    Spacer(Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(cs.outline)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = nota,
                            color = cs.onSurface,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            // Botón principal (REINTENTAR / VOLVER AL INICIO)
            val formaBoton = RoundedCornerShape(14.dp)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .clip(formaBoton)
                    .background(DegradadoMarca)
                    .clickable { onAccion() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = resultado.textoBoton,
                    color = cs.onPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }
    }
}

@Composable
private fun FilaCheck(check: CheckPresencia) {
    val cs = MaterialTheme.colorScheme
    val estados = LocalEstadoColors.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = check.etiqueta, color = cs.onSurface, fontSize = 14.sp)

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(if (check.ok) estados.cumplida else estados.falta)
                .padding(horizontal = 8.dp, vertical = 3.dp)
        ) {
            Text(
                text = (if (check.ok) "✓ " else "✕ ") + check.valor,
                color = estados.sobreEstado,
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}

@Preview(showBackground = true, name = "Rechazada")
@Composable
private fun PreviewRechazada() {
    AsisteSVTheme {
        MarcaResultadoScreen(
            resultado = ResultadoMarca.rechazadaPorFakeGps(),
            onAccion = {}
        )
    }
}

@Preview(showBackground = true, name = "Aprobada")
@Composable
private fun PreviewAprobada() {
    AsisteSVTheme {
        MarcaResultadoScreen(
            resultado = ResultadoMarca.aprobada(hora = "07:56", rssi = -62),
            onAccion = {}
        )
    }
}
@Composable
private fun IconoResultadoAnimado(
    aprobada: Boolean,
    color: Color,
    colorSimbolo: Color,
    clave: Any? = null,
    modifier: Modifier = Modifier
) {
    // Círculo principal
    val escala = remember { Animatable(0f) }
    val alfaCirculo = remember { Animatable(0f) }

    // Halo que se expande y desaparece
    val escalaHalo = remember { Animatable(0.6f) }
    val alfaHalo = remember { Animatable(0f) }

    // Progreso del trazo del símbolo (0 = nada, 1 = completo)
    val progreso = remember { Animatable(0f) }

    LaunchedEffect(aprobada, clave) {
        // Reinicio por si el resultado cambia
        escala.snapTo(0f)
        alfaCirculo.snapTo(0f)
        escalaHalo.snapTo(0.6f)
        alfaHalo.snapTo(0f)
        progreso.snapTo(0f)

        // 1) El círculo aparece con rebote
        launch {
            alfaCirculo.animateTo(1f, tween(durationMillis = 160))
            escala.animateTo(
                targetValue = 1f,
                animationSpec = spring(
                    dampingRatio = 0.45f,
                    stiffness = Spring.StiffnessMediumLow
                )
            )
        }

        // 2) El halo se expande y se desvanece
        launch {
            alfaHalo.animateTo(0.45f, tween(durationMillis = 140))
            escalaHalo.animateTo(
                targetValue = 1.18f,
                animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing)
            )
            alfaHalo.animateTo(0f, tween(durationMillis = 320))
        }

        // 3) El trazo arranca cuando el círculo ya se ve
        delay(180)
        progreso.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 480, easing = FastOutSlowInEasing)
        )
    }

    Box(modifier = modifier, contentAlignment = Alignment.Center) {

        // Halo
        Box(
            Modifier
                .fillMaxSize()
                .graphicsLayer {
                    scaleX = escalaHalo.value
                    scaleY = escalaHalo.value
                    alpha = alfaHalo.value
                }
                .clip(CircleShape)
                .background(color)
        )

        // Círculo sólido (92 de 120 = 0.7667)
        Box(
            Modifier
                .fillMaxSize(0.7667f)
                .graphicsLayer {
                    scaleX = escala.value
                    scaleY = escala.value
                    alpha = alfaCirculo.value
                }
                .clip(CircleShape)
                .background(color)
        )

        // Símbolo dibujado
        Canvas(
            Modifier
                .fillMaxSize(0.5f)
                .graphicsLayer { alpha = alfaCirculo.value }
        ) {
            dibujarSimbolo(
                aprobada = aprobada,
                progreso = progreso.value,
                color = colorSimbolo,
                grosor = size.width * 0.13f
            )
        }
    }
}

/** Dibuja un ✓ o un ✕ de forma progresiva. */
private fun DrawScope.dibujarSimbolo(
    aprobada: Boolean,
    progreso: Float,
    color: Color,
    grosor: Float
) {
    val w = size.width
    val h = size.height

    // Dibuja el tramo [inicio, fin] del progreso total
    fun tramo(desde: Offset, hasta: Offset, inicio: Float, fin: Float) {
        if (progreso <= inicio) return
        val t = ((progreso - inicio) / (fin - inicio)).coerceIn(0f, 1f)
        val finParcial = Offset(
            desde.x + (hasta.x - desde.x) * t,
            desde.y + (hasta.y - desde.y) * t
        )
        drawLine(
            color = color,
            start = desde,
            end = finParcial,
            strokeWidth = grosor,
            cap = StrokeCap.Round
        )
    }

    if (aprobada) {
        // ✓ : primero la bajada corta, luego la subida larga
        val p1 = Offset(w * 0.12f, h * 0.54f)
        val p2 = Offset(w * 0.40f, h * 0.82f)
        val p3 = Offset(w * 0.90f, h * 0.18f)
        tramo(p1, p2, 0f, 0.45f)
        tramo(p2, p3, 0.45f, 1f)
    } else {
        // ✕ : primero la diagonal \
        val a = Offset(w * 0.16f, h * 0.16f)
        val b = Offset(w * 0.84f, h * 0.84f)
        val c = Offset(w * 0.84f, h * 0.16f)
        val d = Offset(w * 0.16f, h * 0.84f)
        tramo(a, b, 0f, 0.5f)
        tramo(c, d, 0.5f, 1f)
    }
}
