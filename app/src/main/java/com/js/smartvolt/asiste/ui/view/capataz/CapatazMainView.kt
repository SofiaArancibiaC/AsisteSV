package com.js.smartvolt.asiste.ui.view.capataz

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.js.smartvolt.asiste.ui.components.AdminBottomBar
import com.js.smartvolt.asiste.ui.components.HamburgerScaffold
import com.js.smartvolt.asiste.ui.view.admin.AdminAlertasView
import com.js.smartvolt.asiste.ui.view.admin.AdminDetalleAlertaView
import com.js.smartvolt.asiste.ui.view.admin.AdminDetalleOperarioView
import com.js.smartvolt.asiste.ui.view.admin.AdminHorasExtraView
import com.js.smartvolt.asiste.ui.view.admin.AdminPerfilUsuarioView
import com.js.smartvolt.asiste.ui.view.admin.AdminReportesView
import com.js.smartvolt.asiste.ui.view.admin.AdminTurnosView
import com.js.smartvolt.asiste.ui.view.admin.AdminUsuariosView

sealed class CapatazScreen {
    object Panel : CapatazScreen()
    object Reportes : CapatazScreen()
    object Alertas : CapatazScreen()
    object Usuarios : CapatazScreen()
    data class DetalleAlerta(val id: String) : CapatazScreen()
    data class PerfilUsuario(val codigo: String) : CapatazScreen()
    data class Turnos(val codigo: String) : CapatazScreen()
    data class HorasExtra(val codigo: String) : CapatazScreen()
    data class DetalleOperario(val codigo: String) : CapatazScreen()
}

@Composable
fun CapatazMainView(onCerrarSesion: () -> Unit = {}) {
    var current by remember { mutableStateOf<CapatazScreen>(CapatazScreen.Panel) }
    val tabFor: (CapatazScreen) -> Int = { s ->
        when (s) {
            is CapatazScreen.Panel, is CapatazScreen.DetalleAlerta -> 0
            is CapatazScreen.Reportes, is CapatazScreen.DetalleOperario -> 1
            is CapatazScreen.Alertas -> 2
            else -> 3
        }
    }
    var selectedTab by remember { mutableStateOf(tabFor(current)) }
    LaunchedEffect(current) { selectedTab = tabFor(current) }

    BackHandler(enabled = true) {
        when (current) {
            is CapatazScreen.DetalleAlerta -> current = CapatazScreen.Alertas
            is CapatazScreen.PerfilUsuario -> current = CapatazScreen.Panel
            is CapatazScreen.Turnos -> current = CapatazScreen.PerfilUsuario((current as CapatazScreen.Turnos).codigo)
            is CapatazScreen.HorasExtra -> current = CapatazScreen.PerfilUsuario((current as CapatazScreen.HorasExtra).codigo)
            is CapatazScreen.DetalleOperario -> current = CapatazScreen.Reportes
            else -> if (selectedTab != 0) { selectedTab = 0; current = CapatazScreen.Panel }
        }
    }

    HamburgerScaffold(
        title = "AsisteSV Capataz",
        onConfiguracion = { },
        onPerfil = { },
        onCerrarSesion = onCerrarSesion,
        bottomBar = {
            AdminBottomBar(selectedTab = selectedTab, onTabSelected = { t ->
                current = when (t) {
                    0 -> CapatazScreen.Panel
                    1 -> CapatazScreen.Reportes
                    2 -> CapatazScreen.Alertas
                    else -> CapatazScreen.Usuarios
                }
            })
        },
        content = { padding ->
            Box(Modifier.padding(padding)) {
                when (val s = current) {
                    CapatazScreen.Panel -> CapatazPanelView(onOpenAlertas = { current = CapatazScreen.Alertas }, onOpenOperarios = { current = CapatazScreen.Usuarios }, onOpenReporte = { current = CapatazScreen.Reportes })
                    CapatazScreen.Usuarios -> AdminUsuariosView(
                        onNew = { },
                        onOpenPerfil = { current = CapatazScreen.PerfilUsuario(it) },
                        allowNew = false
                    )
                    CapatazScreen.Reportes -> AdminReportesView(onOpenOperario = {
                        current = CapatazScreen.DetalleOperario(it)
                    })
                    CapatazScreen.Alertas -> AdminAlertasView(onOpenDetalle = {
                        current = CapatazScreen.DetalleAlerta(it)
                    })
                    is CapatazScreen.DetalleAlerta -> AdminDetalleAlertaView(
                        id = s.id,
                        onBack = { current = CapatazScreen.Alertas })
                    is CapatazScreen.PerfilUsuario -> AdminPerfilUsuarioView(
                        codigo = s.codigo,
                        onBack = { current = CapatazScreen.Panel },
                        onTurnos = { current = CapatazScreen.Turnos(s.codigo) },
                        onHorasExtra = { current = CapatazScreen.HorasExtra(s.codigo) },
                        allowDangerZone = false
                    )
                    is CapatazScreen.Turnos -> AdminTurnosView(
                        codigo = s.codigo,
                        onBack = { current = CapatazScreen.PerfilUsuario(s.codigo) })
                    is CapatazScreen.HorasExtra -> AdminHorasExtraView(
                        codigo = s.codigo,
                        onBack = { current = CapatazScreen.PerfilUsuario(s.codigo) })
                    is CapatazScreen.DetalleOperario -> AdminDetalleOperarioView(
                        codigo = s.codigo,
                        onBack = { current = CapatazScreen.Reportes })
                }
            }
        }
    )
}