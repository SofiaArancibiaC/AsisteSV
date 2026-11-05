package com.js.smartvolt.asiste.ui.view.admin

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.js.smartvolt.asiste.model.data.local.DatabaseProvider

@Composable
fun AdminDetalleAlertaView(id: String, onBack: () -> Unit) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val alertaDao = DatabaseProvider.getAlertaDao(context)
    val alertas by alertaDao.getAll().collectAsState(initial = emptyList())
    val alerta = alertas.firstOrNull { it.id.toString() == id }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
            TextButton(onClick = onBack) { Text("◄") }
            Text("DETALLE DE ALERTA", style = MaterialTheme.typography.titleLarge)
        }
        Spacer(Modifier.height(8.dp))
        if (alerta == null) {
            Text("Alerta no encontrada")
            return
        }
        Text("${alerta.codigoOperario} · ${alerta.tipoMarca} ${alerta.hora}")
        Text("Resultado del sistema: ${alerta.estado}")
        Text("Motivo: ${alerta.motivo}")
        Spacer(Modifier.height(8.dp))
        Text("Datos de la marca")
        Text("Faena ID: ${alerta.faenaId}")
        Spacer(Modifier.height(8.dp))
        Text("Historial del operario")
        Text("Alertas últimos 30 días: ${alertas.count { it.codigoOperario == alerta.codigoOperario }}")
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value = "", onValueChange = {}, label = { Text("Nota del admin (opcional)") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(12.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = {}, modifier = Modifier.weight(1f)) { Text("RECHAZAR MARCA") }
            Button(onClick = {}, modifier = Modifier.weight(1f)) { Text("VALIDAR MARCA") }
        }
    }
}