package com.js.smartvolt.asiste.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat

// Esquema oscuro: paleta navy + amarillo neón
private val ColoresOscuros = darkColorScheme(
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

// Esquema claro: el navy pasa a ser el color principal y el amarillo queda de contenedor
private val ColoresClaros = lightColorScheme(
    primary = FondoArriba,
    onPrimary = Color.White,
    primaryContainer = AmbarClaro,
    onPrimaryContainer = TextoOscuro,
    secondary = NaranjaClaro,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFE0D1),
    onSecondaryContainer = Color(0xFF4A2312),
    tertiary = VerdeClaro,
    onTertiary = Color.White,
    background = FondoClaroArriba,
    onBackground = TextoOscuro,
    surface = SuperficieClara,
    onSurface = TextoOscuro,
    surfaceVariant = SuperficieClaraVariante,
    onSurfaceVariant = TextoOscuroGris,
    outline = BordeClaro,
    outlineVariant = BordeClaro,
    error = RojoClaro,
    onError = Color.White,
    // Si tu versión de material3 es anterior a 1.2 y no compila, borra estas 5 líneas
    surfaceContainerLowest = Color.White,
    surfaceContainerLow = Color(0xFFF8F9FC),
    surfaceContainer = Color(0xFFF1F3F9),
    surfaceContainerHigh = Color(0xFFE9EDF5),
    surfaceContainerHighest = Color(0xFFE1E6F0)
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
    // true = oscuro, false = claro. Por defecto sigue el modo del sistema.
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // Barra de estado transparente; los íconos se adaptan al tema
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Color.Transparent.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    CompositionLocalProvider(
        LocalEstadoColors provides if (darkTheme) EstadoColorsFaena else EstadoColorsClaro,
        LocalDegradadoFondo provides if (darkTheme) DegradadoFondo else DegradadoFondoClaro
    ) {
        MaterialTheme(
            colorScheme = if (darkTheme) ColoresOscuros else ColoresClaros,
            typography = Typography,
            shapes = AppShapes,
            content = content
        )
    }
}