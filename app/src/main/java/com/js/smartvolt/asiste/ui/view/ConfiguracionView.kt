package com.js.smartvolt.asiste.ui.view

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.js.smartvolt.asiste.ui.components.CommonButton
import com.js.smartvolt.asiste.ui.theme.Medidas

@Composable
fun ConfiguracionView() {
    var darkMode by remember { mutableStateOf(false) }

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = Medidas.PaddingPantalla, vertical = Medidas.EspacioM)
    ) {
        Text(
            text = "Configuración",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(Modifier.height(Medidas.EspacioL))

        SeccionConfig("PERMISOS") {
            PermissionRow("GPS / Ubicación") {
                // Solicitar permiso de ubicación
            }
            HorizontalDivider(
                modifier = Modifier.padding(vertical = Medidas.EspacioS),
                color = MaterialTheme.colorScheme.outline
            )
            PermissionRow("Datos biométricos") {
                // Solicitar permiso biométrico
            }
        }

        Spacer(Modifier.height(Medidas.EspacioL))

        SeccionConfig("APARIENCIA") {
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Modo oscuro",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )
                Switch(checked = darkMode, onCheckedChange = { darkMode = it })
            }
        }
    }
}

@Composable
private fun SeccionConfig(titulo: String, content: @Composable ColumnScope.() -> Unit) {
    Text(
        text = titulo,
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
    Spacer(Modifier.height(Medidas.EspacioS))
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(Medidas.BordeCard, MaterialTheme.colorScheme.outline)
    ) {
        Column(Modifier.padding(Medidas.PaddingCard), content = content)
    }
}

@Composable
private fun PermissionRow(label: String, onClick: () -> Unit) {
    Row(
        Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )
        Spacer(Modifier.width(Medidas.EspacioM))
        CommonButton(
            text = "Permitir",
            onClick = onClick,
            modifier = Modifier.width(120.dp)
        )
    }
}
