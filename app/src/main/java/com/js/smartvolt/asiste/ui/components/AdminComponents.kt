package com.js.smartvolt.asiste.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.js.smartvolt.asiste.ui.theme.Medidas
import com.js.smartvolt.asiste.ui.theme.colorsCampo
import com.js.smartvolt.asiste.ui.theme.inicial
import com.js.smartvolt.asiste.ui.theme.tenue
import java.time.Instant
import java.time.ZoneId
import androidx.compose.runtime.*

// ---------------------------------------------------------------
// Componentes originales
// ---------------------------------------------------------------

@Composable
fun AdminHeader(title: String, subtitle: String? = null, onBack: (() -> Unit)? = null) {
    Column(Modifier.fillMaxWidth().padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (onBack != null) {
                IconButton(onClick = onBack) { Text("←") }
            }
            Text(title, style = MaterialTheme.typography.titleLarge)
        }
        if (subtitle != null) Text(subtitle, style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
fun KpiCard(value: String, label: String, modifier: Modifier, onClick: () -> Unit) {
    Card(modifier = modifier, onClick = onClick) {
        Column(Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(value, style = MaterialTheme.typography.headlineMedium)
            Text(label, style = MaterialTheme.typography.labelSmall)
        }
    }
}

@Composable
fun FaenaRow(nombre: String, conteo: String, pct: Float) {
    Row(Modifier.fillMaxWidth().padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(nombre, Modifier.weight(1f))
        Text(conteo, Modifier.padding(horizontal = 8.dp))
        Text("${(pct * 100).toInt()}%", color = MaterialTheme.colorScheme.primary)
    }
}

// ---------------------------------------------------------------
// Componentes compartidos (movidos desde AdminViews)
// ---------------------------------------------------------------

@Composable
fun Avatar(texto: String, size: Dp = Medidas.AvatarS) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primaryContainer),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = texto.inicial(),
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            style = if (size > Medidas.AvatarS) MaterialTheme.typography.headlineMedium
            else MaterialTheme.typography.titleMedium
        )
    }
}

@Composable
fun Panel(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (onClick != null) Modifier
                    .clip(MaterialTheme.shapes.large)
                    .clickable(onClick = onClick)
                else Modifier
            ),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(Medidas.BordeCard, MaterialTheme.colorScheme.outline)
    ) {
        Column(Modifier.padding(Medidas.PaddingCard), content = content)
    }
}

@Composable
fun EtiquetaCampo(texto: String) {
    Text(
        text = texto,
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
    Spacer(Modifier.height(Medidas.EspacioXS))
}

@Composable
fun BadgeEstado(texto: String, color: Color) {
    Box(
        modifier = Modifier
            .clip(CircleShape)
            .background(color.tenue())
            .padding(horizontal = 10.dp, vertical = 5.dp)
    ) {
        Text(
            text = texto,
            color = color,
            style = MaterialTheme.typography.labelMedium
        )
    }
}

@Composable
fun BotonPrincipal(texto: String, enabled: Boolean = true, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier
            .fillMaxWidth()
            .height(Medidas.AlturaBoton),
        shape = MaterialTheme.shapes.medium
    ) {
        Text(texto, style = MaterialTheme.typography.labelLarge)
    }
}

/** Campo que se ve como input pero abre un diálogo (fecha, hora). */
@Composable
fun CampoSelector(etiqueta: String, valor: String, onClick: () -> Unit) {
    Column {
        EtiquetaCampo(etiqueta)
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.medium)
                .clickable(onClick = onClick),
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(Medidas.BordeCard, MaterialTheme.colorScheme.outline)
        ) {
            Text(
                text = valor,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)
            )
        }
    }
}

@Composable
fun AccionCard(
    titulo: String,
    descripcion: String,
    icono: ImageVector,
    onClick: () -> Unit
) {
    Panel(onClick = onClick) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(Medidas.AvatarS)
                    .clip(MaterialTheme.shapes.medium)
                    .background(MaterialTheme.colorScheme.primary.tenue()),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icono,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(Modifier.width(Medidas.EspacioM))
            Column(Modifier.weight(1f)) {
                Text(
                    text = titulo,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = descripcion,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                imageVector = Icons.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun PlaceholderCard(texto: String) {
    Panel {
        Text(
            text = texto,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
    }
}


/** Campo de texto estándar. Si `error` no es null, se muestra en rojo. */
@Composable
fun CampoTexto(
    etiqueta: String,
    valor: String,
    onCambio: (String) -> Unit = {},
    modifier: Modifier = Modifier,
    error: String? = null,
    singleLine: Boolean = true,
    readOnly: Boolean = false
) {
    OutlinedTextField(
        value = valor,
        onValueChange = onCambio,
        label = { Text(etiqueta) },
        isError = error != null,
        supportingText = error?.let { { Text(it) } },
        singleLine = singleLine,
        readOnly = readOnly,
        shape = MaterialTheme.shapes.medium,
        colors = colorsCampo(),
        modifier = modifier.fillMaxWidth()
    )
}

/** Dropdown genérico: sirve para faenas, roles, estados, operarios, etc. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> CampoDropdown(
    etiqueta: String,
    valorActual: String,
    opciones: List<T>,
    textoOpcion: (T) -> String,
    onSeleccion: (T) -> Unit
) {
    var expandido by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(expanded = expandido, onExpandedChange = { expandido = !expandido }) {
        OutlinedTextField(
            value = valorActual,
            onValueChange = {},
            readOnly = true,
            label = { Text(etiqueta) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expandido) },
            shape = MaterialTheme.shapes.medium,
            colors = colorsCampo(),
            modifier = Modifier.menuAnchor().fillMaxWidth()
        )
        ExposedDropdownMenu(expanded = expandido, onDismissRequest = { expandido = false }) {
            opciones.forEach { opcion ->
                DropdownMenuItem(
                    text = { Text(textoOpcion(opcion)) },
                    onClick = {
                        onSeleccion(opcion)
                        expandido = false
                    }
                )
            }
        }
    }
}

/** Diálogo de fecha. Devuelve la fecha elegida como "yyyy-MM-dd". */
@Composable
fun DialogoFecha(onDismiss: () -> Unit, onFechaElegida: (String) -> Unit) {
    val state = rememberDatePickerState()

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                state.selectedDateMillis?.let {
                    onFechaElegida(
                        Instant.ofEpochMilli(it)
                            .atZone(ZoneId.of("UTC"))
                            .toLocalDate()
                            .toString()
                    )
                }
                onDismiss()
            }) { Text("OK") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    ) { DatePicker(state = state) }
}