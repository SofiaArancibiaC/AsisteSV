package com.js.smartvolt.asiste.ui.view.admin

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessAlarm
import androidx.compose.material.icons.filled.AlignVerticalBottom
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*

@Composable
fun AdminMainView(){
    var selectedTab by remember { mutableStateOf(0) }

    Scaffold(
        bottomBar = {
            NavigationBar{
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = {selectedTab = 0},
                    icon = { Icon(Icons.Default.Home, contentDescription = "Inicio") },
                    label = { Text("Inicio") }
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick =  {selectedTab = 1},
                    icon = {Icon(Icons.Default.AlignVerticalBottom, contentDescription = "Reportes")},
                    label = {Text("Reportes")}
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick =  {selectedTab = 2},
                    icon = {Icon(Icons.Default.Notifications, contentDescription = "Alertas")},
                    label = {Text("Alertas")}
                )
                NavigationBarItem(
                    selected = selectedTab == 3,
                    onClick =  {selectedTab = 3},
                    icon = {Icon(Icons.Rounded.Person, contentDescription = "Usuarios")},
                    label = {Text("Usuarios")}
                )
            }
        }
    ) { padding -> when (selectedTab){
        0 ->null
        1 -> null
        2 -> null
        3 -> null
    } }


}