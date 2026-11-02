package com.js.smartvolt.asiste.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.js.smartvolt.asiste.model.data.local.DatabaseProvider
import com.js.smartvolt.asiste.ui.theme.AsisteSVTheme
import com.js.smartvolt.asiste.ui.theme.DegradadoFondo
import com.js.smartvolt.asiste.ui.theme.LocalEstadoColors
import com.js.smartvolt.asiste.viewmodel.AsistenciaViewModel
import com.js.smartvolt.asiste.viewmodel.AsistenciaViewModelFactory
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import androidx.compose.ui.platform.LocalContext

@Composable
fun HistorialView() {
    val context = LocalContext.current
    val dao = remember { DatabaseProvider.getDao(context) }
    val attendances by dao.getAll().collectAsState(initial = emptyList())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DegradadoFondo)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Text(
            text = "Historial de marcas",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(16.dp)
        )
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(attendances) { a ->
                val estados = LocalEstadoColors.current
                val color = when (a.estado) {
                    com.js.smartvolt.asiste.model.EstadoAsistencia.CUMPLIDA -> estados.cumplida
                    com.js.smartvolt.asiste.model.EstadoAsistencia.ATRASADA -> androidx.compose.ui.graphics.Color(0xFFFFE70A)
                    com.js.smartvolt.asiste.model.EstadoAsistencia.FALTA -> androidx.compose.ui.graphics.Color(0xFFFF6A2B)
                    com.js.smartvolt.asiste.model.EstadoAsistencia.EN_REVISION -> androidx.compose.ui.graphics.Color(0xFF757575)
                    com.js.smartvolt.asiste.model.EstadoAsistencia.RECHAZADA -> androidx.compose.ui.graphics.Color(0xFFFF4D5E)
                    else -> estados.falta
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = a.fecha.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                        Text(text = "Marca: ${a.registro?.toString() ?: "--:--"}", style = MaterialTheme.typography.bodySmall)
                    }
                    Surface(color = color, contentColor = estados.sobreEstado, shape = MaterialTheme.shapes.small) {
                        Text(
                            text = a.estado.name.replace("_", " "),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }
            }
        }
    }
}