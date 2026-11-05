package com.js.smartvolt.asiste.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity

//Paleta de diseño
private val fondoArriba = Color(0xFF0A0F1C)
private val fondoAbajo = Color(0xFF16120F)
private val Amarillo = Color(0xFFE6FF0A)
private val Naranja = Color(0xFFFF6A2B)
private val campoFondo = Color(0xFF111A2E)
private val campoBorde = Color(0xFF26324D)
private val textoClaro = Color(0xFFE5E7EB)
private val textoGris = Color(0xFF8B94A7)
private val Verde = Color(0xFF2DD4A0)
//
private val DegradadoMarca = Brush.horizontalGradient(listOf(Amarillo, Amarillo))


@Composable
fun LoginView(
    onLogin: (correo: String, pass: String) -> Unit = { _, _ -> },
    onOlvideClave: () -> Unit={},
    error: String? = null,
    cargando: Boolean = false
) {
    val dark = isSystemInDarkTheme()
    val fondoArriba = if (dark) Color(0xFF0A0F1C) else Color(0xFFF5F7FA)
    val fondoAbajo = if (dark) Color(0xFF16120F) else Color(0xFFFFFFFF)
    val campoFondo = if (dark) Color(0xFF111A2E) else Color(0xFFFFFFFF)
    val campoBorde = if (dark) Color(0xFF26324D) else Color(0xFFD1D5DB)
    val textoClaro = if (dark) Color(0xFFE5E7EB) else Color(0xFF1F2937)
    val textoGris = if (dark) Color(0xFF8B94A7) else Color(0xFF6B7280)

    //Estados
    var correo by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }
    var passVisible by remember { mutableStateOf(false) }
    var recordar by remember { mutableStateOf(true) }

    val colorCampos= OutlinedTextFieldDefaults.colors(
        focusedContainerColor = campoFondo,
        unfocusedContainerColor = campoFondo,
        focusedBorderColor = Amarillo,
        unfocusedBorderColor = campoBorde,
        focusedTextColor = textoClaro,
        unfocusedTextColor = textoClaro,
        cursorColor = Amarillo,
        focusedLeadingIconColor = Amarillo,
        unfocusedLeadingIconColor = textoGris,
        focusedTrailingIconColor = Amarillo,
        unfocusedTrailingIconColor = textoGris,
        focusedLabelColor = Amarillo,
        unfocusedLabelColor = textoGris
    )

    Column(
        //Cambio color de fondo
        modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(fondoArriba, fondoAbajo)))
            .verticalScroll(rememberScrollState()).padding(horizontal = 24.dp, vertical = 32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        //Logo
        Box(modifier = Modifier.size(68.dp).clip(RoundedCornerShape(18.dp)).background(
            Color(0xFF1A1A14)).border(2.dp, Brush.linearGradient(listOf(Amarillo,Naranja)), RoundedCornerShape(18.dp)),
            contentAlignment = Alignment.Center
        ){
            Icon(imageVector = Icons.Default.Bolt, contentDescription = "Logo", tint = Amarillo, modifier = Modifier.size(36.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))
        //Titulo VOLT + SERVICIOS
        Row {
            Text(text = "VOLT", color = textoClaro, fontSize = 30.sp, fontWeight = FontWeight.ExtraBold)
            Text(text = "SERVICIOS", style = TextStyle(brush = DegradadoMarca, fontSize = 30.sp, fontWeight = FontWeight.ExtraBold))
        }

        Text(text = "INGENIERÍA & SOLUCIONES ELÉCTRICAS", color = textoGris, fontSize = 11.sp, fontWeight = FontWeight.Medium, letterSpacing = 1.sp, textAlign = TextAlign.Center)

        Spacer(modifier = Modifier.height(48.dp))

        //Correo
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
            placeholder = {Text("tecnico@serviciosvolt.cl", color = textoGris)},
            leadingIcon = {Icon(Icons.Default.Email, contentDescription = "Correo")},
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            shape = RoundedCornerShape(14.dp),
            colors = colorCampos,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Contraseña",
                color = textoClaro,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "¿Olvidaste tu clave?",
                color = Naranja,
                fontSize = 12.sp,
                modifier = Modifier.clickable { onOlvideClave()}
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = pass,
            onValueChange = { pass = it },
            singleLine = true,
            placeholder = { Text("••••••••", color = textoGris) },
            leadingIcon = {
                Icon(Icons.Default.Lock, contentDescription = "Contraseña")
            },
            trailingIcon = {
                IconButton (onClick = { passVisible = !passVisible }) {
                    Icon(
                        imageVector = if (passVisible) Icons.Default.VisibilityOff
                        else Icons.Default.Visibility,
                        contentDescription = if (passVisible) "Ocultar contraseña"
                        else "Mostrar contraseña"
                    )
                }
            },
            visualTransformation = if (passVisible) VisualTransformation.None
            else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            shape = RoundedCornerShape(14.dp),
            colors = colorCampos,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Recordar usuario + Servidor seguro
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = recordar,
                    onCheckedChange = { recordar = it },
                    colors = CheckboxDefaults.colors(
                        checkedColor = Naranja,
                        uncheckedColor = textoGris,
                        checkmarkColor = textoClaro
                    )
                )
                Text(
                    text = "Recordar usuario",
                    color = textoGris,
                    fontSize = 13.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        error?.let {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = it,
                color = Naranja,
                fontSize = 12.sp,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Botón con degradado
        Button(
            onClick = { onLogin(correo, pass) },
            enabled = !cargando,
            shape = RoundedCornerShape(16.dp),
            contentPadding = PaddingValues(0.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .background(DegradadoMarca, RoundedCornerShape(16.dp))
        ) {
            Text(
                text = "INICIAR SESIÓN",
                color = Color(0xFF111111),
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = Color(0xFF111111),
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

    }
}

@Preview(showBackground = true)
@Composable
fun LoginViewPreview() {
    LoginView()
}