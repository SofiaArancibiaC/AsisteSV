package com.js.smartvolt.asiste.ui.view.admin

import com.js.smartvolt.asiste.ui.components.*

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AdminAlertasView(onOpenDetalle: (String) -> Unit) {
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        AdminHeader("ALERTAS DE MARCAS", "4 pendientes")
        Button(onClick = { onOpenDetalle("A-001") }) { Text("Ver detalle") }
    }
}

@Composable
fun AdminUsuariosView(onNew: () -> Unit, onOpenPerfil: (String) -> Unit) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val dao = com.js.smartvolt.asiste.model.data.local.DatabaseProvider.getUsuarioDao(context)
    val vm: com.js.smartvolt.asiste.viewmodel.UsuarioViewModel = androidx.lifecycle.viewmodel.compose.viewModel(factory = com.js.smartvolt.asiste.viewmodel.UsuarioViewModelFactory(dao))
    val usuarios by vm.usuarios.collectAsState()

    Column(Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState())) {
        AdminHeader("USUARIOS")
        Text("Total: ${usuarios.size} usuarios")
        Spacer(Modifier.height(8.dp))
        usuarios.forEach { u ->
            Row(Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(u.correo)
                TextButton(onClick = { onOpenPerfil(u.correo) }) { Text("Ver") }
            }
        }
        Spacer(Modifier.height(8.dp))
        Button(onClick = onNew) { Text("Nuevo usuario") }
    }
}

@Composable
fun AdminDetalleAlertaView(id: String, onBack: () -> Unit) {
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        AdminHeader("DETALLE DE ALERTA", id, onBack)
        Text("Datos de la marca (placeholder)")
    }
}

@Composable
fun AdminPerfilUsuarioView(codigo: String, onBack: () -> Unit, onTurnos: () -> Unit, onHorasExtra: () -> Unit) {
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        AdminHeader("PERFIL USUARIO", codigo, onBack)
        Button(onClick = onTurnos) { Text("Asignar turnos") }
        Button(onClick = onHorasExtra) { Text("Horas extra") }
    }
}

@Composable
fun AdminNuevoUsuarioView(onBack: () -> Unit) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val dao = com.js.smartvolt.asiste.model.data.local.DatabaseProvider.getUsuarioDao(context)
    val vm: com.js.smartvolt.asiste.viewmodel.UsuarioViewModel = androidx.lifecycle.viewmodel.compose.viewModel(factory = com.js.smartvolt.asiste.viewmodel.UsuarioViewModelFactory(dao))

    var correo by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf("") }
    var nombre by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf("") }
    var pass by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf("") }

    Column(Modifier.fillMaxSize().padding(16.dp).verticalScroll(androidx.compose.foundation.rememberScrollState())) {
        AdminHeader("NUEVO USUARIO", onBack = onBack)
        OutlinedTextField(value = correo, onValueChange = { correo = it }, label = { Text("Correo") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value = pass, onValueChange = { pass = it }, label = { Text("Contraseña") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(12.dp))
        Button(onClick = {
            if (correo.isNotBlank() && nombre.isNotBlank() && pass.isNotBlank()) {
                vm.agregar(com.js.smartvolt.asiste.model.RecuperarUsuario(correo = correo.trim().lowercase(), nombre = nombre, passwordHash = com.js.smartvolt.asiste.model.data.local.sha256(pass)))
                onBack()
            }
        }, Modifier.fillMaxWidth()) { Text("GUARDAR") }
    }
}

@Composable
fun AdminTurnosView(codigo: String, onBack: () -> Unit) {
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        AdminHeader("ASIGNAR TURNOS", codigo, onBack)
        Text("Horario por día (placeholder)")
    }
}

@Composable
fun AdminHorasExtraView(codigo: String, onBack: () -> Unit) {
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        AdminHeader("HORAS EXTRA", codigo, onBack)
        Text("Formulario horas extra (placeholder)")
    }
}

@Composable
fun AdminDetalleOperarioView(codigo: String, onBack: () -> Unit) {
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        AdminHeader("DETALLE OPERARIO", codigo, onBack)
        Text("Día a día (placeholder)")
    }
}
