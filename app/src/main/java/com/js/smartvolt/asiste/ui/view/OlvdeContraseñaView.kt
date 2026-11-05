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

// Paleta de diseño (misma del login)
private val fondoArriba = Color(0xFF0A0F1C)
private val fondoAbajo = Color(0xFF16120F)
private val Amarillo = Color(0xFFE6FF0A)
private val Naranja = Color(0xFFFF6A2B)
private val campoFondo = Color(0xFF111A2E)
private val campoBorde = Color(0xFF26324D)
private val textoClaro = Color(0xFFE5E7EB)
private val textoGris = Color(0xFF8B94A7)
private val Verde = Color(0xFF2DD4A0)

@Composable
fun OlvideContrasenaView(
    onVolver: () -> Unit = {},
    estado: OlvideEstado = OlvideEstado.Idle,
    onEnviar: (String) -> Unit = {}
) {
    val dark = isSystemInDarkTheme()
    val fondoArriba = if (dark) Color(0xFF0A0F1C) else Color(0xFFF5F7FA)
    val fondoAbajo = if (dark) Color(0xFF16120F) else Color(0xFFFFFFFF)
    val campoFondo = if (dark) Color(0xFF111A2E) else Color(0xFFFFFFFF)
    val campoBorde = if (dark) Color(0xFF26324D) else Color(0xFFD1D5DB)
    val textoClaro = if (dark) Color(0xFFE5E7EB) else Color(0xFF1F2937)
    val textoGris = if (dark) Color(0xFF8B94A7) else Color(0xFF6B7280)

    var correo by remember { mutableStateOf("") }

    val colorCampos = OutlinedTextFieldDefaults.colors(
        focusedContainerColor = campoFondo,
        unfocusedContainerColor = campoFondo,
        focusedBorderColor = Amarillo,
        unfocusedBorderColor = campoBorde,
        focusedTextColor = textoClaro,
        unfocusedTextColor = textoClaro,
        cursorColor = Amarillo,
        focusedLeadingIconColor = Amarillo,
        unfocusedLeadingIconColor = textoGris
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(fondoArriba, fondoAbajo)))
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
                    tint = textoClaro
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Recuperar contraseña",
            color = textoClaro,
            fontSize = 24.sp,
            fontWeight = FontWeight.ExtraBold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Ingresa tu correo y te enviaremos las instrucciones para restablecer tu clave.",
            color = textoGris,
            fontSize = 13.sp,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Correo Electrónico",
            color = textoClaro,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = correo,
            onValueChange = { correo = it },
            singleLine = true,
            placeholder = { Text("tecnico@serviciosvolt.cl", color = textoGris) },
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = "Correo") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            shape = RoundedCornerShape(14.dp),
            colors = colorCampos,
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
                disabledContainerColor = campoBorde,
                disabledContentColor = textoGris
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
                contentColor = textoGris
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