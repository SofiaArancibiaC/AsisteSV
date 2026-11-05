package com.js.smartvolt.asiste.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

// ---------------------------------------------------------------
// Paleta de diseño
// ---------------------------------------------------------------
val FondoArriba = Color(0xFF0A0F1C)
val FondoAbajo = Color(0xFF16120F)
val Amarillo = Color(0xFFE6FF0A)

val AmarilloSuave = Color(0xFFFFE70A)
val Naranja = Color(0xFFFF6A2B)
val CampoFondo = Color(0xFF111A2E)
val CampoBorde = Color(0xFF26324D)
val TextoClaro = Color(0xFFE5E7EB)
val TextoGris = Color(0xFF8B94A7)
val Verde = Color(0xFF2DD4A0)
val Rojo = Color(0xFFFF4D5E)

val Gris = Color(0xFFA8A8A8)

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
    val enRevision: Color,
    val rechazada: Color,
    /** Color del texto/íconos que van encima de un relleno de estado. */
    val sobreEstado: Color
) {
    /** Alias de [sobreEstado] para el código que ya usa `onEstado`. */
    val onEstado: Color get() = sobreEstado
}

val EstadoColorsFaena = EstadoColors(
    cumplida = Verde,
    atrasada = AmarilloSuave,
    falta = Naranja,
    enRevision = Gris,
    rechazada = Rojo,
    sobreEstado = FondoArriba
)

val LocalEstadoColors = staticCompositionLocalOf { EstadoColorsFaena }

/** Acceso corto: MaterialTheme.estados.cumplida (compatibilidad con pantallas existentes). */
val MaterialTheme.estados: EstadoColors
    @Composable
    @ReadOnlyComposable
    get() = LocalEstadoColors.current

// ---------------------------------------------------------------
// Colores de estado para alertas de marcas
// VALIDADA -> verde, RECHAZADA -> rojo, SOSPECHOSA -> naranja, resto -> gris
// ---------------------------------------------------------------
fun EstadoColors.deAlerta(estado: String): Color = when {
    estado.equals("VALIDADA", ignoreCase = true) -> cumplida
    estado.equals("RECHAZADA", ignoreCase = true) -> rechazada
    estado.contains("SOSPECH", ignoreCase = true) -> falta
    else -> enRevision
}

/** Versión translúcida de un color, útil para fondos de badges y chips. */
fun Color.tenue(alpha: Float = 0.16f): Color = copy(alpha = alpha)

/** Primera letra en mayúscula, para avatares. */
fun String.inicial(): String = trim().take(1).uppercase()

// ---------------------------------------------------------------
// Medidas compartidas entre pantallas
// ---------------------------------------------------------------
object Medidas {
    val PaddingPantalla = 16.dp
    val EspacioXS = 4.dp
    val EspacioS = 8.dp
    val EspacioM = 12.dp
    val EspacioL = 16.dp
    val PaddingCard = 14.dp
    val BordeCard = 1.dp
    val FranjaEstado = 6.dp
    val AlturaBoton = 52.dp
    val AvatarS = 40.dp
    val AvatarL = 72.dp
}

// ---------------------------------------------------------------
// Colores de campos de texto (formularios)
// ---------------------------------------------------------------
@Composable
fun colorsCampo() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = MaterialTheme.colorScheme.primary,
    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
    focusedLabelColor = MaterialTheme.colorScheme.primary,
    unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
    cursorColor = MaterialTheme.colorScheme.primary,
    focusedContainerColor = MaterialTheme.colorScheme.surface,
    unfocusedContainerColor = MaterialTheme.colorScheme.surface
)

// ---------------------------------------------------------------
// Alias de compatibilidad con nombres anteriores
// ---------------------------------------------------------------
val AmarilloFaena = Amarillo
val NegroFaena = FondoArriba