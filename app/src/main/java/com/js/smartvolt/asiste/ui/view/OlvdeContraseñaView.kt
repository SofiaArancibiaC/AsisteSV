package com.js.smartvolt.asiste.ui.view



import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.js.smartvolt.asiste.ui.theme.Amarillo
import com.js.smartvolt.asiste.ui.theme.CampoBorde
import com.js.smartvolt.asiste.ui.theme.FondoAbajo
import com.js.smartvolt.asiste.ui.theme.FondoArriba
import com.js.smartvolt.asiste.ui.theme.FondoClaroArriba
import com.js.smartvolt.asiste.ui.theme.FondoClaroAbajo
import com.js.smartvolt.asiste.ui.theme.CampoFondo
import com.js.smartvolt.asiste.ui.theme.SuperficieClara
import com.js.smartvolt.asiste.ui.theme.CampoBorde
import com.js.smartvolt.asiste.ui.theme.BordeClaro
import com.js.smartvolt.asiste.ui.theme.TextoClaro
import com.js.smartvolt.asiste.ui.theme.TextoOscuro
import com.js.smartvolt.asiste.ui.theme.TextoGris
import com.js.smartvolt.asiste.ui.theme.TextoOscuroGris
import com.js.smartvolt.asiste.ui.theme.AmbarClaro
import com.js.smartvolt.asiste.ui.theme.NaranjaClaro
import androidx.compose.foundation.isSystemInDarkTheme
import com.js.smartvolt.asiste.ui.theme.Naranja
import com.js.smartvolt.asiste.ui.theme.TextoClaro
import com.js.smartvolt.asiste.ui.theme.TextoGris
import com.js.smartvolt.asiste.ui.theme.Verde
import com.js.smartvolt.asiste.ui.theme.colorCampos


@Composable
fun OlvideContrasenaView(
    onVolver: () -> Unit = {},
    estado: OlvideEstado = OlvideEstado.Idle,
    onEnviar: (String) -> Unit = {}
) {

    var correo by remember { mutableStateOf("") }

    val dark = isSystemInDarkTheme()
    val FondoArriba = if (dark) FondoArriba else FondoClaroArriba
    val FondoAbajo = if (dark) FondoAbajo else FondoClaroAbajo
    val CampoFondo = if (dark) CampoFondo else SuperficieClara
    val CampoBorde = if (dark) CampoBorde else BordeClaro
    val TextoClaro = if (dark) TextoClaro else TextoOscuro
    val TextoGris = if (dark) TextoGris else TextoOscuroGris
    val Amarillo = if (dark) Amarillo else AmbarClaro
    val Naranja = if (dark) Naranja else NaranjaClaro

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(FondoArriba, FondoAbajo)))
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Botón volver
        Row(modifier = Modifier.fillMaxWidth()) {
            IconButton(onClick = onVolver) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver",
                    tint = TextoClaro
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Recuperar contraseña",
            color = TextoClaro,
            fontSize = 24.sp,
            fontWeight = FontWeight.ExtraBold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Ingresa tu correo y te enviaremos las instrucciones para restablecer tu clave.",
            color = TextoGris,
            fontSize = 13.sp,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Correo Electrónico",
            color = TextoClaro,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = correo,
            onValueChange = { correo = it },
            singleLine = true,
            placeholder = { Text("tecnico@serviciosvolt.cl", color = TextoGris) },
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = "Correo") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            shape = RoundedCornerShape(14.dp),
            colors = colorCampos(),
            modifier = Modifier.fillMaxWidth()
        )

        // Mensajes según el estado
        when (estado) {
            is OlvideEstado.Error -> {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = estado.mensaje,
                    color = Naranja,
                    fontSize = 12.sp,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            is OlvideEstado.Enviado -> {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Si el correo está registrado, recibirás las instrucciones en unos minutos.",
                    color = Verde,
                    fontSize = 12.sp,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            else -> Unit
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { onEnviar(correo) },
            enabled = estado !is OlvideEstado.Cargando,
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Amarillo,
                contentColor = Color(0xFF111111),
                disabledContainerColor = CampoBorde,
                disabledContentColor = TextoGris
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
        ) {
            Text(
                text = if (estado is OlvideEstado.Cargando) "ENVIANDO..." else "ENVIAR INSTRUCCIONES",
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = onVolver,
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
                contentColor = TextoGris
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Text("Volver al inicio de sesión")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OlvideContrasenaViewPreview() {
    OlvideContrasenaView()
}