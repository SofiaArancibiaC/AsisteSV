package com.js.smartvolt.asiste.ui.view.admin

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

sealed class AdminScreen {
    object Panel : AdminScreen()
    object Reportes : AdminScreen()
    object Alertas : AdminScreen()
    object Usuarios : AdminScreen()
    data class DetalleAlerta(val id: String) : AdminScreen()
    data class PerfilUsuario(val codigo: String) : AdminScreen()
    object NuevoUsuario : AdminScreen()
    data class Turnos(val codigo: String) : AdminScreen()
    data class HorasExtra(val codigo: String) : AdminScreen()
    data class DetalleOperario(val codigo: String) : AdminScreen()
}

@Composable
fun AdminMainView() {
    var current by remember { mutableStateOf<AdminScreen>(AdminScreen.Panel) }
    val tabFor: (AdminScreen) -> Int = { s ->
        when (s) {
            is AdminScreen.Panel, is AdminScreen.DetalleAlerta -> 0
            is AdminScreen.Reportes, is AdminScreen.DetalleOperario -> 1
            is AdminScreen.Alertas -> 2
            is AdminScreen.Usuarios, is AdminScreen.PerfilUsuario, is AdminScreen.NuevoUsuario,
            is AdminScreen.Turnos, is AdminScreen.HorasExtra -> 3
        }
    }
    var selectedTab by remember { mutableStateOf(tabFor(current)) }
    LaunchedEffect(current) { selectedTab = tabFor(current) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(selected = selectedTab == 0, onClick = { current = AdminScreen.Panel }, icon = { Icon(Icons.Default.Home, null) }, label = { Text("Inicio") })
                NavigationBarItem(selected = selectedTab == 1, onClick = { current = AdminScreen.Reportes }, icon = { Icon(Icons.Default.AlignVerticalBottom, null) }, label = { Text("Reportes") })
                NavigationBarItem(selected = selectedTab == 2, onClick = { current = AdminScreen.Alertas }, icon = { Icon(Icons.Default.Notifications, null) }, label = { Text("Alertas") })
                NavigationBarItem(selected = selectedTab == 3, onClick = { current = AdminScreen.Usuarios }, icon = { Icon(Icons.Default.Person, null) }, label = { Text("Usuarios") })
            }
        }
    ) { padding ->
        Box(Modifier.padding(padding)) {
            when (val s = current) {
                AdminScreen.Panel -> AdminPanelView(onOpenAlertas = { current = AdminScreen.Alertas }, onOpenUsuarios = { current = AdminScreen.Usuarios }, onOpenReporte = { current = AdminScreen.Reportes })
                AdminScreen.Reportes -> AdminReportesView(onOpenOperario = { current = AdminScreen.DetalleOperario(it) })
                AdminScreen.Alertas -> AdminAlertasView(onOpenDetalle = { current = AdminScreen.DetalleAlerta(it) })
                AdminScreen.Usuarios -> AdminUsuariosView(onNew = { current = AdminScreen.NuevoUsuario }, onOpenPerfil = { current = AdminScreen.PerfilUsuario(it) })
                is AdminScreen.DetalleAlerta -> AdminDetalleAlertaView(id = s.id, onBack = { current = AdminScreen.Alertas })
                is AdminScreen.PerfilUsuario -> AdminPerfilUsuarioView(codigo = s.codigo, onBack = { current = AdminScreen.Usuarios }, onTurnos = { current = AdminScreen.Turnos(s.codigo) }, onHorasExtra = { current = AdminScreen.HorasExtra(s.codigo) })
                AdminScreen.NuevoUsuario -> AdminNuevoUsuarioView(onBack = { current = AdminScreen.Usuarios })
                is AdminScreen.Turnos -> AdminTurnosView(codigo = s.codigo, onBack = { current = AdminScreen.PerfilUsuario(s.codigo) })
                is AdminScreen.HorasExtra -> AdminHorasExtraView(codigo = s.codigo, onBack = { current = AdminScreen.PerfilUsuario(s.codigo) })
                is AdminScreen.DetalleOperario -> AdminDetalleOperarioView(codigo = s.codigo, onBack = { current = AdminScreen.Reportes })
            }
        }
    }
}


