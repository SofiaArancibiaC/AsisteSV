package com.js.smartvolt.asiste

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.lifecycleScope
import com.js.smartvolt.asiste.model.Asistencia
import com.js.smartvolt.asiste.model.EstadoAsistencia
import com.js.smartvolt.asiste.model.data.local.DatabaseProvider
import com.js.smartvolt.asiste.ui.theme.AsisteSVTheme
import com.js.smartvolt.asiste.ui.view.LoginView
import com.js.smartvolt.asiste.ui.view.MainView
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime

class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Datos de prueba: siembra asistencias de ejemplo una sola vez
        val dao = DatabaseProvider.getDao(this)
        lifecycleScope.launch {
            val existing = dao.getByRange(LocalDate.now().minusMonths(2), LocalDate.now().plusMonths(2))
            // Flow: collect first emission
            val list = existing.first()
            if (list.isEmpty()) {
                val hoy = LocalDate.now()
                val samples = listOf(
                    Asistencia(fecha = hoy.minusDays(1), estado = EstadoAsistencia.CUMPLIDA, inicioTurno = LocalTime.of(8,0), registro = LocalTime.of(7,55)),
                    Asistencia(fecha = hoy.minusDays(2), estado = EstadoAsistencia.ATRASADA, inicioTurno = LocalTime.of(8,0), registro = LocalTime.of(8,20)),
                    Asistencia(fecha = hoy.minusDays(3), estado = EstadoAsistencia.FALTA, inicioTurno = LocalTime.of(8,0), registro = null),
                    Asistencia(fecha = hoy.plusDays(1), estado = EstadoAsistencia.CUMPLIDA, inicioTurno = LocalTime.of(8,0), registro = LocalTime.of(7,58)),
                    Asistencia(fecha = hoy.plusDays(2), estado = EstadoAsistencia.EN_REVISION, inicioTurno = LocalTime.of(8,0), registro = LocalTime.of(8,5)),
                    Asistencia(fecha = hoy.plusDays(3), estado = EstadoAsistencia.RECHAZADA, inicioTurno = LocalTime.of(8,0), registro = null)
                )
                samples.forEach { dao.upsert(it) }
            }
        }

        setContent {
            AsisteSVTheme {
                var loggedIn by remember { mutableStateOf(false) }
                if (loggedIn) {
                    MainView()
                } else {
                    LoginView(onLogin = { loggedIn = true })
                }
            }
        }
    }


@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AsisteSVTheme {
        Greeting("Android")
    }
}



