package com.js.smartvolt.asiste.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp


// ---------------------------------------------------------------
// Paleta oscura (original)
// ---------------------------------------------------------------
val FondoArriba = Color(0xFF0A0F1C)
val FondoAbajo = Color(0xFF16120F)
val Amarillo = Color(0xFFFFCE01)

val AmarilloSuave = Color(0xFFFFD62B)
val Naranja = Color(0xFFFF6D00)
val CampoFondo = Color(0xFF111A2E)
val CampoBorde = Color(0xFF26324D)
val TextoClaro = Color(0xFFE5E7EB)
val TextoGris = Color(0xFF8B94A7)
val Verde = Color(0xFF2DD4A0)
val Rojo = Color(0xFFFF4D5E)

val Gris = Color(0xFFA8A8A8)

// ---------------------------------------------------------------
// Paleta clara
// ---------------------------------------------------------------
val FondoClaroArriba = Color(0xFFF4F6FA)
val FondoClaroAbajo = Color(0xFFE9EDF5)
val SuperficieClara = Color(0xFFFFFFFF)
val SuperficieClaraVariante = Color(0xFFE9EDF5)
val BordeClaro = Color(0xFFD3D9E5)
val TextoOscuro = Color(0xFF0A0F1C)
val TextoOscuroGris = Color(0xFF5B6478)

val NaranjaClaro = Color(0xFFE0510F)
val VerdeClaro = Color(0xFF0F9D75)

@Composable
fun colorCampos() = OutlinedTextFieldDefaults.colors(
    focusedContainerColor = if (isSystemInDarkTheme()) CampoFondo else SuperficieClara,
    unfocusedContainerColor = if (isSystemInDarkTheme()) CampoFondo else SuperficieClara,
    focusedBorderColor = if (isSystemInDarkTheme()) Amarillo else AmbarClaro,
    unfocusedBorderColor = if (isSystemInDarkTheme()) CampoBorde else BordeClaro,
    focusedTextColor = if (isSystemInDarkTheme()) TextoClaro else TextoOscuro,
    unfocusedTextColor = if (isSystemInDarkTheme()) TextoClaro else TextoOscuro,
    cursorColor = if (isSystemInDarkTheme()) Amarillo else AmbarClaro,
    focusedLeadingIconColor = if (isSystemInDarkTheme()) Amarillo else AmbarClaro,
    unfocusedLeadingIconColor = if (isSystemInDarkTheme()) TextoGris else TextoOscuroGris,
    focusedTrailingIconColor = if (isSystemInDarkTheme()) Amarillo else AmbarClaro,
    unfocusedTrailingIconColor = if (isSystemInDarkTheme()) TextoGris else TextoOscuroGris,
    focusedLabelColor = if (isSystemInDarkTheme()) Amarillo else AmbarClaro,
    unfocusedLabelColor = if (isSystemInDarkTheme()) TextoGris else TextoOscuroGris
)
val AmbarClaro = Color(0xFFFFCE01)
val ColorHoy = AmbarClaro.tenue()

val Beige = Color(0xFFFFE791)
val RojoClaro = Color(0xFFD32F2F)
val GrisClaro = Color(0xFF6B7280)

// ---------------------------------------------------------------
// Degradados
// ---------------------------------------------------------------
val DegradadoFondo: Brush = Brush.verticalGradient(listOf(FondoArriba, FondoAbajo))
val DegradadoFondoClaro: Brush = Brush.verticalGradient(listOf(FondoClaroArriba, FondoClaroAbajo))
val DegradadoMarca: Brush = Brush.horizontalGradient(listOf(Beige, AmbarClaro))

/** Degradado de acción (botón principal). Igual en ambos temas. */
val DegradadoAccion: Brush = Brush.horizontalGradient(listOf(Beige, AmbarClaro))

val LocalDegradadoFondo = staticCompositionLocalOf { DegradadoFondo }

/** Fondo de pantalla según el tema: MaterialTheme.degradadoFondo */
val MaterialTheme.degradadoFondo: Brush
    @Composable
    @ReadOnlyComposable
    get() = LocalDegradadoFondo.current

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

/** Estados para tema oscuro (original). */
val EstadoColorsFaena = EstadoColors(
    cumplida = Verde,
    atrasada = AmarilloSuave,
    falta = Naranja,
    enRevision = Gris,
    rechazada = Rojo.tenue(0.5f),
    sobreEstado = FondoArriba
)

/** Estados para tema claro: más oscuros para tener contraste sobre blanco. */
val EstadoColorsClaro = EstadoColors(
    cumplida = VerdeClaro,
    atrasada = AmbarClaro,
    falta = NaranjaClaro,
    enRevision = GrisClaro,
    rechazada = RojoClaro.tenue(0.5f),
    sobreEstado = Color.White
)

val LocalEstadoColors = staticCompositionLocalOf { EstadoColorsFaena }

/** Acceso corto: MaterialTheme.estados.cumplida (compatibilidad con pantallas existentes). */
val MaterialTheme.estados: EstadoColors
    @Composable
    @ReadOnlyComposable
    get() = LocalEstadoColors.current

// ---------------------------------------------------------------
// Colores de estado para alertas de marcas
// VALIDADA -> verde, RECHAZADA -> rojo, FALTA -> naranja, resto -> gris
// ---------------------------------------------------------------
fun EstadoColors.deAlerta(estado: String): Color = when {
    estado.equals("VALIDADA", ignoreCase = true) -> cumplida
    estado.equals("RECHAZADA", ignoreCase = true) -> rechazada
    estado.contains("FALTA", ignoreCase = true) -> falta
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
