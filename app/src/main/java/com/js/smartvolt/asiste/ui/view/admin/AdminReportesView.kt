package com.js.smartvolt.asiste.ui.view.admin

import com.js.smartvolt.asiste.ui.components.*

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll

@Composable
fun AdminReportesView(onOpenOperario: (String) -> Unit) {
    Column(Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState())) {
        AdminHeader("REPORTE DE HORAS", "Remuneraciones y ausentismo")

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Faena", Modifier.weight(1f))
            Text("Período", Modifier.weight(1f))
        }
        Spacer(Modifier.height(12.dp))

        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(8.dp)) {
                Row(Modifier.fillMaxWidth()) {
                    Text("OPERARIO", Modifier.weight(1f), style = MaterialTheme.typography.labelSmall)
                    Text("HORAS", Modifier.weight(1f), style = MaterialTheme.typography.labelSmall)
                    Text("EXTRA", Modifier.weight(1f), style = MaterialTheme.typography.labelSmall)
                    Text("ATRASOS", Modifier.weight(1f), style = MaterialTheme.typography.labelSmall)
                    Text("FALTAS", Modifier.weight(1f), style = MaterialTheme.typography.labelSmall)
                }
                HorizontalDivider(Modifier.padding(vertical = 4.dp))
                val rows = listOf(
                    Triple("OP-014", "88", "2"),
                    Triple("OP-021", "80", "0"),
                    Triple("OP-033", "76", "1"),
                )
                rows.forEach { (op, horas, extra) ->
                    Row(Modifier.fillMaxWidth().clickable { onOpenOperario(op) }) {
                        Text(op, Modifier.weight(1f))
                        Text(horas, Modifier.weight(1f))
                        Text(extra, Modifier.weight(1f))
                        Text("1", Modifier.weight(1f))
                        Text("0", Modifier.weight(1f))
                    }
                    Spacer(Modifier.height(4.dp))
                }
                HorizontalDivider(Modifier.padding(vertical = 4.dp))
                Row(Modifier.fillMaxWidth()) {
                    Text("TOTAL", Modifier.weight(1f), style = MaterialTheme.typography.labelMedium)
                    Text("392", Modifier.weight(1f), style = MaterialTheme.typography.labelMedium)
                    Text("5", Modifier.weight(1f), style = MaterialTheme.typography.labelMedium)
                    Text("6", Modifier.weight(1f), style = MaterialTheme.typography.labelMedium)
                    Text("3", Modifier.weight(1f), style = MaterialTheme.typography.labelMedium)
                }
            }
        }
        Spacer(Modifier.height(8.dp))
        Text("Ausentismo del período: ")
        Spacer(Modifier.height(12.dp))
        Button(onClick = { }, Modifier.fillMaxWidth()) { Text("EXPORTAR  CSV · PDF") }
    }
}
