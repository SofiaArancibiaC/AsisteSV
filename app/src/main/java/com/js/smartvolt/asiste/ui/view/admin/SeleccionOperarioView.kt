package com.js.smartvolt.asiste.ui.view.admin

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.js.smartvolt.asiste.model.admin.Rol
import com.js.smartvolt.asiste.model.admin.Usuario
import com.js.smartvolt.asiste.model.data.local.DatabaseProvider

/** Operario seleccionado en pantallas de turnos y horas extra. */
class SeleccionOperario(codigoInicial: String) {
    var id by mutableStateOf(0L)
    var codigo by mutableStateOf(codigoInicial)
    var operarios by mutableStateOf<List<Usuario>>(emptyList())

    fun elegir(u: Usuario) {
        id = u.id
        codigo = u.codigo
    }
}

/** Carga los operarios y deja seleccionado el que viene por parámetro (o el primero). */
@Composable
fun rememberSeleccionOperario(codigoInicial: String): SeleccionOperario {
    val context = LocalContext.current
    val seleccion = remember { SeleccionOperario(codigoInicial) }
    val flujo = remember { DatabaseProvider.getUsuarioDao(context).getByRol(Rol.OPERARIO) }
    val operarios by flujo.collectAsState(initial = emptyList())

    LaunchedEffect(operarios) {
        seleccion.operarios = operarios
        if (seleccion.id == 0L && operarios.isNotEmpty()) {
            seleccion.elegir(operarios.firstOrNull { it.codigo == codigoInicial } ?: operarios.first())
        }
    }
    return seleccion
}