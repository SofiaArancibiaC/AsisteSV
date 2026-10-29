package com.js.smartvolt.asiste.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// ---------- Paleta base (tomada del mockup) ----------
val AmarilloFaena = Color(0xFFFFC400)
val NegroFaena = Color(0xFF1A1A1A)
val CremaFondo = Color(0xFFF2F1EA)
val CremaOscuro = Color(0xFFE4E2D8)
val GrisTexto = Color(0xFF4A4A44)
val GrisBorde = Color(0xFFC9C7BC)

val VerdeCumplida = Color(0xFF2E9E4F)
val NaranjaAtrasada = Color(0xFFF26A0F)
val RojoFalta = Color(0xFFC62828)

// ---------- Colores de estado de asistencia ----------
// Material 3 no tiene roles para estos, así que se exponen aparte.
@Immutable
data class EstadoColors(
    val cumplida: Color,
    val atrasada: Color,
    val falta: Color,
    val revision: Color,
    val onEstado: Color,    // texto sobre cumplida / atrasada / falta
    val onRevision: Color   // texto sobre revision (amarillo)
)

val EstadoColorsLight = EstadoColors(
    cumplida = VerdeCumplida,
    atrasada = NaranjaAtrasada,
    falta = RojoFalta,
    revision = AmarilloFaena,
    onEstado = Color.White,
    onRevision = NegroFaena
)

val EstadoColorsDark = EstadoColors(
    cumplida = Color(0xFF4CAF50),
    atrasada = Color(0xFFFF9800),
    falta = Color(0xFFEF5350),
    revision = AmarilloFaena,
    onEstado = NegroFaena,
    onRevision = NegroFaena
)

val LocalEstadoColors = staticCompositionLocalOf { EstadoColorsLight }

/** Uso: MaterialTheme.estados.cumplida */
val MaterialTheme.estados: EstadoColors
    @Composable
    @ReadOnlyComposable
    get() = LocalEstadoColors.current