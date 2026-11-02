package com.js.smartvolt.asiste.ui.theme

import android.app.Activity
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat

// Esquema único oscuro: la identidad de la app es la paleta navy + amarillo neón
private val AppColors = darkColorScheme(
    primary = Amarillo,
    onPrimary = FondoArriba,
    primaryContainer = Color(0xFF2E3300),
    onPrimaryContainer = Amarillo,
    secondary = Naranja,
    onSecondary = FondoArriba,
    secondaryContainer = Color(0xFF4A2312),
    onSecondaryContainer = Color(0xFFFFD9C9),
    tertiary = Verde,
    onTertiary = FondoArriba,
    background = FondoArriba,
    onBackground = TextoClaro,
    surface = CampoFondo,
    onSurface = TextoClaro,
    surfaceVariant = CampoFondo,
    onSurfaceVariant = TextoGris,
    outline = CampoBorde,
    outlineVariant = CampoBorde,
    error = Rojo,
    onError = FondoArriba,
    // Si tu versión de material3 es anterior a 1.2 y no compila, borra estas 5 líneas
    surfaceContainerLowest = FondoArriba,
    surfaceContainerLow = Color(0xFF0E1526),
    surfaceContainer = CampoFondo,
    surfaceContainerHigh = Color(0xFF16213A),
    surfaceContainerHighest = Color(0xFF1C2845)
)

private val AppShapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(24.dp)
)

@Composable
fun AsisteSVTheme(
    // Se mantiene por compatibilidad con llamadas existentes; el tema siempre es oscuro
    @Suppress("UNUSED_PARAMETER") darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    // Barra de estado transparente con íconos claros (el degradado se ve detrás)
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Color.Transparent.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    CompositionLocalProvider(LocalEstadoColors provides EstadoColorsFaena) {
        MaterialTheme(
            colorScheme = AppColors,
            typography = Typography,
            shapes = AppShapes,
            content = content
        )
    }
}