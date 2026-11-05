package com.js.smartvolt.asiste.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun AdminBottomBar(selectedTab: Int, onTabSelected: (Int) -> Unit, alertasPendientes: Int = 0) {
    NavigationBar {
        NavigationBarItem(selected = selectedTab == 0, onClick = { onTabSelected(0) }, icon = { Icon(Icons.Default.Home, null) }, label = { Text("Inicio") })
        NavigationBarItem(selected = selectedTab == 1, onClick = { onTabSelected(1) }, icon = { Icon(Icons.Default.AlignVerticalBottom, null) }, label = { Text("Reportes") })
        NavigationBarItem(selected = selectedTab == 2, onClick = { onTabSelected(2) }, icon = { BadgedBox(badge = { if (alertasPendientes > 0) Badge { Text(alertasPendientes.toString()) } }) { Icon(Icons.Default.Notifications, null) } }, label = { Text("Alertas") })
        NavigationBarItem(selected = selectedTab == 3, onClick = { onTabSelected(3) }, icon = { Icon(Icons.Default.Person, null) }, label = { Text("Usuarios") })
    }
}

@Composable
fun UserBottomBar(selectedTab: Int, onTabSelected: (Int) -> Unit) {
    NavigationBar {
        NavigationBarItem(selected = selectedTab == 0, onClick = { onTabSelected(0) }, icon = { Icon(Icons.Default.Home, null) }, label = { Text("Inicio") })
        NavigationBarItem(selected = selectedTab == 1, onClick = { onTabSelected(1) }, icon = { Icon(Icons.Default.CalendarMonth, null) }, label = { Text("Calendario") })
        NavigationBarItem(selected = selectedTab == 2, onClick = { onTabSelected(2) }, icon = { Icon(Icons.Default.History, null) }, label = { Text("Historial") })
    }
}