package com.js.smartvolt.asiste.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import com.js.smartvolt.asiste.ui.theme.Medidas

/** Contenedor estándar: padding + header + (opcional) scroll. */
@Composable
fun PantallaAdmin(
    titulo: String,
    subtitulo: String? = null,
    onBack: (() -> Unit)? = null,
    scroll: Boolean = true,
    espacioHeader: Dp = Medidas.EspacioL,
    content: @Composable ColumnScope.() -> Unit
) {
    val scrollState = rememberScrollState()
    Column(
        Modifier
            .fillMaxSize()
            .then(if (scroll) Modifier.verticalScroll(scrollState) else Modifier)
            .padding(horizontal = Medidas.PaddingPantalla, vertical = Medidas.EspacioM)
    ) {
        AdminHeader(titulo, subtitulo, onBack)
        Spacer(Modifier.height(espacioHeader))
        content()
    }
}

@Composable
fun TituloSeccion(texto: String) {
    Text(
        text = texto,
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
    Spacer(Modifier.height(Medidas.EspacioS))
}

@Composable
fun TextoVacio(texto: String) {
    Text(
        text = texto,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth().padding(vertical = Medidas.EspacioM)
    )
}