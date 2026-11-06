package com.js.smartvolt.asiste.ui.view.admin

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.js.smartvolt.asiste.ui.components.AdminBottomBar
import com.js.smartvolt.asiste.ui.components.HamburgerScaffold
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
fun AdminMainView(onCerrarSesion: () -> Unit = {}) {
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

    BackHandler(enabled = true) {
        when (current) {
            is AdminScreen.DetalleAlerta -> current = AdminScreen.Alertas
            is AdminScreen.PerfilUsuario -> current = AdminScreen.Usuarios
            is AdminScreen.NuevoUsuario -> current = AdminScreen.Usuarios
            is AdminScreen.Turnos -> current = AdminScreen.PerfilUsuario((current as AdminScreen.Turnos).codigo)
            is AdminScreen.HorasExtra -> current = AdminScreen.PerfilUsuario((current as AdminScreen.HorasExtra).codigo)
            is AdminScreen.DetalleOperario -> current = AdminScreen.Reportes
            else -> if (selectedTab != 0) { selectedTab = 0; current = AdminScreen.Panel }
        }
    }

    HamburgerScaffold(
        title = "AsisteSV Admin",
        onConfiguracion = { },
        onPerfil = { },
        onCerrarSesion = onCerrarSesion,
        bottomBar = {
            AdminBottomBar(selectedTab = selectedTab, onTabSelected = { t ->
                current = when (t) {
                    0 -> AdminScreen.Panel
                    1 -> AdminScreen.Reportes
                    2 -> AdminScreen.Alertas
                    else -> AdminScreen.Usuarios
                }
            })
        },
        content = { padding ->
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
    )
}


