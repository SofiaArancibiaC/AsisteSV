package com.js.smartvolt.asiste.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.material.icons.Icons
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

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
