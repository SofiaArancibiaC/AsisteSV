package com.js.smartvolt.asiste.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat

private val LightColors = lightColorScheme(
    primary = AmarilloFaena,
    onPrimary = NegroFaena,
    primaryContainer = Color(0xFFFFE082),
    onPrimaryContainer = NegroFaena,
    secondary = NegroFaena,
    onSecondary = AmarilloFaena,
    secondaryContainer = CremaOscuro,
    onSecondaryContainer = NegroFaena,
    tertiary = VerdeCumplida,
    onTertiary = Color.White,
    background = CremaFondo,
    onBackground = NegroFaena,
    surface = CremaFondo,
    onSurface = NegroFaena,
    surfaceVariant = CremaOscuro,
    onSurfaceVariant = GrisTexto,
    outline = NegroFaena,
    outlineVariant = GrisBorde,
    error = RojoFalta,
    onError = Color.White,
    // Si tu versión de material3 es anterior a 1.2 y no compila, borra estas 5 líneas
    surfaceContainerLowest = Color.White,
    surfaceContainerLow = CremaFondo,
    surfaceContainer = CremaFondo,
    surfaceContainerHigh = CremaOscuro,
    surfaceContainerHighest = CremaOscuro
)

private val DarkColors = darkColorScheme(
    primary = AmarilloFaena,
    onPrimary = NegroFaena,
    primaryContainer = Color(0xFF5C4700),
    onPrimaryContainer = Color(0xFFFFE082),
    secondary = CremaOscuro,
    onSecondary = NegroFaena,
    secondaryContainer = Color(0xFF2C2C28),
    onSecondaryContainer = CremaFondo,
    tertiary = Color(0xFF4CAF50),
    onTertiary = NegroFaena,
    background = Color(0xFF141412),
    onBackground = CremaFondo,
    surface = Color(0xFF141412),
    onSurface = CremaFondo,
    surfaceVariant = Color(0xFF2A2A26),
    onSurfaceVariant = GrisBorde,
    outline = Color(0xFF8F8D83),
    outlineVariant = Color(0xFF3A3A35),
    error = Color(0xFFEF5350),
    onError = NegroFaena,
    surfaceContainerLowest = Color(0xFF0F0F0D),
    surfaceContainerLow = Color(0xFF181816),
    surfaceContainer = Color(0xFF1C1C19),
    surfaceContainerHigh = Color(0xFF232320),
    surfaceContainerHighest = Color(0xFF2C2C28)
)

// Esquinas redondeadas como las tarjetas y botones del mockup
private val AppShapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(6.dp),
    medium = RoundedCornerShape(10.dp),
    large = RoundedCornerShape(14.dp),
    extraLarge = RoundedCornerShape(20.dp)
)

@Composable
fun AsisteSVTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // Sin dynamic color a propósito: queremos la identidad del mockup
    val colorScheme = if (darkTheme) DarkColors else LightColors
    val estadoColors = if (darkTheme) EstadoColorsDark else EstadoColorsLight

    // Barra de estado negra, como la cabecera del mockup
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = NegroFaena.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    CompositionLocalProvider(LocalEstadoColors provides estadoColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            shapes = AppShapes,
            content = content
        )
    }
}