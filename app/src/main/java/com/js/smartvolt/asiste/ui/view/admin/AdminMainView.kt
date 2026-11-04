package com.js.smartvolt.asiste.ui.view.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
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
                AdminScreen.Panel -> AdminPanelScreen(onOpenAlertas = { current = AdminScreen.Alertas }, onOpenUsuarios = { current = AdminScreen.Usuarios }, onOpenReporte = { current = AdminScreen.Reportes })
                AdminScreen.Reportes -> AdminReportesScreen(onOpenOperario = { current = AdminScreen.DetalleOperario(it) })
                AdminScreen.Alertas -> AdminAlertasScreen(onOpenDetalle = { current = AdminScreen.DetalleAlerta(it) })
                AdminScreen.Usuarios -> AdminUsuariosScreen(onNew = { current = AdminScreen.NuevoUsuario }, onOpenPerfil = { current = AdminScreen.PerfilUsuario(it) })
                is AdminScreen.DetalleAlerta -> AdminDetalleAlertaScreen(id = s.id, onBack = { current = AdminScreen.Alertas })
                is AdminScreen.PerfilUsuario -> AdminPerfilUsuarioScreen(codigo = s.codigo, onBack = { current = AdminScreen.Usuarios }, onTurnos = { current = AdminScreen.Turnos(s.codigo) }, onHorasExtra = { current = AdminScreen.HorasExtra(s.codigo) })
                AdminScreen.NuevoUsuario -> AdminNuevoUsuarioScreen(onBack = { current = AdminScreen.Usuarios })
                is AdminScreen.Turnos -> AdminTurnosScreen(codigo = s.codigo, onBack = { current = AdminScreen.PerfilUsuario(s.codigo) })
                is AdminScreen.HorasExtra -> AdminHorasExtraScreen(codigo = s.codigo, onBack = { current = AdminScreen.PerfilUsuario(s.codigo) })
                is AdminScreen.DetalleOperario -> AdminDetalleOperarioScreen(codigo = s.codigo, onBack = { current = AdminScreen.Reportes })
            }
        }
    }
}

@Composable
fun AdminHeader(title: String, subtitle: String? = null, onBack: (() -> Unit)? = null) {
    Column(Modifier.fillMaxWidth().padding(16.dp)) {
        Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
            if (onBack != null) {
                IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Atrás") }
            }
            Text(title, style = MaterialTheme.typography.titleLarge)
        }
        if (subtitle != null) Text(subtitle, style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
fun AdminPanelScreen(onOpenAlertas: () -> Unit, onOpenUsuarios: () -> Unit, onOpenReporte: () -> Unit) {
    var dia by remember { mutableStateOf(0) }
    val dias = listOf("Mar 20 oct", "Mié 21 oct", "Jue 22 oct", "Vie 23 oct")
    var faena by remember { mutableStateOf("Todas las faenas") }

    Column(Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState())) {
        AdminHeader("PANEL ADMINISTRADOR", "ADM-001 · Actualizado 08:15")

        // Selector de día
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { if (dia > 0) dia-- }) { Icon(Icons.Default.ChevronLeft, null) }
            Text(dias[dia], style = MaterialTheme.typography.titleMedium)
            IconButton(onClick = { if (dia < dias.lastIndex) dia++ }) { Icon(Icons.Default.ChevronRight, null) }
        }
        Spacer(Modifier.height(8.dp))
        // Faena
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text("Faena: ", style = MaterialTheme.typography.bodyMedium)
            Text(faena, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
        }
        Spacer(Modifier.height(12.dp))

        // KPIs 2x2
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            KpiCard("128", "PRESENTES", Modifier.weight(1f)) { }
            KpiCard("9", "ATRASADOS", Modifier.weight(1f)) { }
        }
        Spacer(Modifier.height(8.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            KpiCard("6", "AUSENTES", Modifier.weight(1f)) { onOpenUsuarios() }
            KpiCard("4", "SOSPECHOSOS", Modifier.weight(1f)) { onOpenAlertas() }
        }

        Spacer(Modifier.height(16.dp))
        Text("REQUIERE ATENCIÓN", style = MaterialTheme.typography.titleSmall)
        Spacer(Modifier.height(4.dp))
        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(12.dp)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("4 alertas sin revisar")
                    Text("›", color = MaterialTheme.colorScheme.primary)
                }
                Spacer(Modifier.height(4.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("2 horas extra por aprobar")
                    Text("›", color = MaterialTheme.colorScheme.primary)
                }
            }
        }

        Spacer(Modifier.height(16.dp))
        Text("ASISTENCIA POR FAENA", style = MaterialTheme.typography.titleSmall)
        Spacer(Modifier.height(4.dp))
        FaenaRow("Ruta 5 Norte", "42/48", 0.87f)
        FaenaRow("Puente Maipo", "36/40", 0.90f)
        FaenaRow("Planta Industrial", "50/55", 0.91f)

        Spacer(Modifier.height(16.dp))
        Button(onClick = onOpenReporte, Modifier.fillMaxWidth()) { Text("VER REPORTE DE HORAS") }
    }
}

@Composable
private fun KpiCard(value: String, label: String, modifier: Modifier, onClick: () -> Unit) {
    Card(modifier = modifier, onClick = onClick) {
        Column(Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(value, style = MaterialTheme.typography.headlineMedium)
            Text(label, style = MaterialTheme.typography.labelSmall)
        }
    }
}

@Composable
private fun FaenaRow(nombre: String, conteo: String, pct: Float) {
    Row(Modifier.fillMaxWidth().padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(nombre, Modifier.weight(1f))
        Text(conteo, Modifier.padding(horizontal = 8.dp))
        Text("${(pct * 100).toInt()}%", color = MaterialTheme.colorScheme.primary)
    }
}

@Composable
fun AdminReportesScreen(onOpenOperario: (String) -> Unit) {
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        AdminHeader("REPORTE DE HORAS", "Remuneraciones y ausentismo")
        Button(onClick = { onOpenOperario("OP-014") }) { Text("Ver OP-014") }
    }
}

@Composable
fun AdminAlertasScreen(onOpenDetalle: (String) -> Unit) {
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        AdminHeader("ALERTAS DE MARCAS", "4 pendientes")
        Button(onClick = { onOpenDetalle("A-001") }) { Text("Ver detalle") }
    }
}

@Composable
fun AdminUsuariosScreen(onNew: () -> Unit, onOpenPerfil: (String) -> Unit) {
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        AdminHeader("USUARIOS")
        Button(onClick = { onOpenPerfil("OP-014") }) { Text("Perfil OP-014") }
        Button(onClick = onNew) { Text("Nuevo usuario") }
    }
}

@Composable
fun AdminDetalleAlertaScreen(id: String, onBack: () -> Unit) {
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        AdminHeader("DETALLE DE ALERTA", id, onBack)
        Text("Datos de la marca (placeholder)")
    }
}

@Composable
fun AdminPerfilUsuarioScreen(codigo: String, onBack: () -> Unit, onTurnos: () -> Unit, onHorasExtra: () -> Unit) {
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        AdminHeader("PERFIL USUARIO", codigo, onBack)
        Button(onClick = onTurnos) { Text("Asignar turnos") }
        Button(onClick = onHorasExtra) { Text("Horas extra") }
    }
}

@Composable
fun AdminNuevoUsuarioScreen(onBack: () -> Unit) {
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        AdminHeader("NUEVO USUARIO", onBack = onBack)
        Text("Formulario (placeholder)")
    }
}

@Composable
fun AdminTurnosScreen(codigo: String, onBack: () -> Unit) {
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        AdminHeader("ASIGNAR TURNOS", codigo, onBack)
        Text("Horario por día (placeholder)")
    }
}

@Composable
fun AdminHorasExtraScreen(codigo: String, onBack: () -> Unit) {
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        AdminHeader("HORAS EXTRA", codigo, onBack)
        Text("Formulario horas extra (placeholder)")
    }
}

@Composable
fun AdminDetalleOperarioScreen(codigo: String, onBack: () -> Unit) {
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        AdminHeader("DETALLE OPERARIO", codigo, onBack)
        Text("Día a día (placeholder)")
    }
}