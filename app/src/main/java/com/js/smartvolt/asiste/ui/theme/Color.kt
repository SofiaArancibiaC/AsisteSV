package com.js.smartvolt.asiste.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// ---------------------------------------------------------------
// Paleta de diseño
// ---------------------------------------------------------------
val FondoArriba = Color(0xFF0A0F1C)
val FondoAbajo = Color(0xFF16120F)
val Amarillo = Color(0xFFE6FF0A)
val Naranja = Color(0xFFFF6A2B)
val CampoFondo = Color(0xFF111A2E)
val CampoBorde = Color(0xFF26324D)
val TextoClaro = Color(0xFFE5E7EB)
val TextoGris = Color(0xFF8B94A7)
val Verde = Color(0xFF2DD4A0)
val Rojo = Color(0xFFFF4D5E)

// ---------------------------------------------------------------
// Degradados
// ---------------------------------------------------------------
val DegradadoFondo: Brush = Brush.verticalGradient(listOf(FondoArriba, FondoAbajo))
val DegradadoMarca: Brush = Brush.horizontalGradient(listOf(Amarillo, Amarillo))

// ---------------------------------------------------------------
// Colores de estado de asistencia (calendario, historial, marcas)
// ---------------------------------------------------------------
@Immutable
data class EstadoColors(
    val cumplida: Color,
    val atrasada: Color,
    val falta: Color,
    /** Color del texto/íconos que van encima de un relleno de estado. */
    val sobreEstado: Color
) {
    /** Alias de [sobreEstado] para el código que ya usa `onEstado`. */
    val onEstado: Color get() = sobreEstado
}

val EstadoColorsFaena = EstadoColors(
    cumplida = Verde,
    atrasada = Naranja,
    falta = Rojo,
    sobreEstado = FondoArriba
)

val LocalEstadoColors = staticCompositionLocalOf { EstadoColorsFaena }

/** Acceso corto: MaterialTheme.estados.cumplida (compatibilidad con pantallas existentes). */
val MaterialTheme.estados: EstadoColors
    @Composable
    @ReadOnlyComposable
    get() = LocalEstadoColors.current

// ---------------------------------------------------------------
// Alias de compatibilidad con nombres anteriores
// ---------------------------------------------------------------
val AmarilloFaena = Amarillo
val NegroFaena = FondoArriba