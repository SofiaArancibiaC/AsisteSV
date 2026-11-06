package com.js.smartvolt.asiste.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HamburgerScaffold(
    title: String = "AsisteSV",
    onConfiguracion: () -> Unit,
    onPerfil: () -> Unit,
    onCerrarSesion: () -> Unit = {},
    bottomBar: @Composable () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text("Menú", modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.titleLarge)
                NavigationDrawerItem(label = { Text("Configuración") }, selected = false, onClick = { onConfiguracion(); scope.launch { drawerState.close() } }, icon = { Icon(Icons.Default.Settings, null) })
                NavigationDrawerItem(label = { Text("Perfil") }, selected = false, onClick = { onPerfil(); scope.launch { drawerState.close() } }, icon = { Icon(Icons.Default.Person, null) })
                Spacer(Modifier.height(500.dp))
                NavigationDrawerItem(label = { Text("Cerrar sesión") }, selected = false, onClick = { onCerrarSesion(); scope.launch { drawerState.close() } })
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(title = { Text(title) }, navigationIcon = { IconButton(onClick = { scope.launch { drawerState.open() } }) { Icon(Icons.Default.Menu, null) } })
            },
            bottomBar = bottomBar
        ) { padding -> content(padding) }
    }
}