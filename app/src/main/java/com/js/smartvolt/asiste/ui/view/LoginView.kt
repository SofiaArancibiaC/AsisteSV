package com.js.smartvolt.asiste.ui.view

import android.R
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

//Paleta de diseño
private val FondoArriba = Color(0xFF0A0F1C)
private val FondoAbajo = Color(0xFF16120F)
private val Amarillo = Color(0xFFE6FF0A)
private val Naranja = Color(0xFFFF6A2B)
private val CampoFondo = Color(0xFF111A2E)
private val CampoBorde = Color(0xFF26324D)
private val TextoClaro = Color(0xFFE5E7EB)
private val TextoGris = Color(0xFF8B94A7)
private val Verde = Color(0xFF2DD4A0)
//
private val DegradadoMarca = Brush.horizontalGradient(listOf(Amarillo, Amarillo))


@Composable
fun LoginView() {
    //Estados
    var correo by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }
    var passVisible by remember { mutableStateOf(false) }
    var recordar by remember { mutableStateOf(true) }

    val colorCampos= OutlinedTextFieldDefaults.colors(
        focusedContainerColor = CampoFondo,
        unfocusedContainerColor = CampoFondo,
        focusedBorderColor = Amarillo,
        unfocusedBorderColor = CampoBorde,
        focusedTextColor = TextoClaro,
        unfocusedTextColor = TextoClaro,
        cursorColor = Amarillo,
        focusedLeadingIconColor = Amarillo,
        unfocusedLeadingIconColor = TextoGris,
        focusedTrailingIconColor = Amarillo,
        unfocusedTrailingIconColor = TextoGris,
        focusedLabelColor = Amarillo,
        unfocusedLabelColor = TextoGris
    )

    Column(
        //Cambio color de fondo
        modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(FondoArriba, FondoAbajo)))
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
            Text(text = "VOLT", color = Color.White, fontSize = 30.sp, fontWeight = FontWeight.ExtraBold)
            Text(text = "SERVICIOS", style = TextStyle(brush = DegradadoMarca, fontSize = 30.sp, fontWeight = FontWeight.ExtraBold))
        }

        Text(text = "INGENIERÍA & SOLUCIONES ELÉCTRICAS", color = TextoGris, fontSize = 11.sp, fontWeight = FontWeight.Medium, letterSpacing = 1.sp, textAlign = TextAlign.Center)

        Spacer(modifier = Modifier.height(48.dp))

        //Correo
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
            placeholder = {Text("tecnico@serviciosvolt.cl", color = TextoGris)},
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
                color = TextoClaro,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "¿Olvidaste tu clave?",
                color = Naranja,
                fontSize = 12.sp,
                modifier = Modifier.clickable { /* TODO: recuperar clave */ }
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = pass,
            onValueChange = { pass = it },
            singleLine = true,
            placeholder = { Text("••••••••", color = TextoGris) },
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
                        uncheckedColor = TextoGris,
                        checkmarkColor = Color.White
                    )
                )
                Text(
                    text = "Recordar usuario",
                    color = TextoGris,
                    fontSize = 13.sp
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(Verde)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Servidor Seguro",
                    color = Verde,
                    fontSize = 11.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Botón con degradado
        Button(
            onClick = { /* TODO: validar login */ },
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
    }
}

@Preview(showBackground = true)
@Composable
fun LoginViewPreview() {
    LoginView()
}