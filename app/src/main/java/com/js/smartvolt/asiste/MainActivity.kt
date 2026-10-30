package com.js.smartvolt.asiste

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.js.smartvolt.asiste.ui.view.CalendarioScreen
import com.js.smartvolt.asiste.ui.theme.AsisteSVTheme
import com.js.smartvolt.asiste.ui.view.InicioView
import com.js.smartvolt.asiste.ui.view.LoginView
import com.js.smartvolt.asiste.ui.view.ValidacionPresencia
import java.time.LocalDate
import java.time.LocalTime

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AsisteSVTheme {
                    InicioView(
                        codigoOperario = "OP-014",
                        nombreFaena = "Faena Ruta 5 Norte",
                        fecha = LocalDate.now(),
                        inicioTurno = LocalTime.of(8, 0),
                        finTurno = LocalTime.of(18, 0),
                        frente = "Frente 2",
                        validacion = ValidacionPresencia(true, true, -62, false),
                        sinConexion = false,
                        onMarcarEntrada = { /* validar y guardar la marca */ }
                    )
                    //CalendarioScreen()
                //Surface(modifier = Modifier.fillMaxSize()) {
                    //LoginView()
                //}

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
