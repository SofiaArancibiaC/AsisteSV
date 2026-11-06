package com.js.smartvolt.asiste.ui.view.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.js.smartvolt.asiste.model.admin.Rol
import com.js.smartvolt.asiste.model.admin.Usuario
import com.js.smartvolt.asiste.model.data.local.DatabaseProvider
import com.js.smartvolt.asiste.model.data.local.sha256
import com.js.smartvolt.asiste.ui.components.*
import com.js.smartvolt.asiste.ui.theme.Medidas
import com.js.smartvolt.asiste.viewmodel.FaenaViewModel
import com.js.smartvolt.asiste.viewmodel.FaenaViewModelFactory
import com.js.smartvolt.asiste.viewmodel.UsuarioViewModel
import com.js.smartvolt.asiste.viewmodel.UsuarioViewModelFactory
import kotlinx.coroutines.launch

// ---------------------------------------------------------------
// Lista de usuarios
// ---------------------------------------------------------------
@Composable
fun AdminUsuariosView(onNew: () -> Unit, onOpenPerfil: (String) -> Unit, allowNew: Boolean = true) {
    val context = LocalContext.current
    val vm: UsuarioViewModel = viewModel(factory = UsuarioViewModelFactory(DatabaseProvider.getUsuarioDao(context)))
    val usuarios by vm.usuarios.collectAsState()
    val visibles = usuarios.filter { !it.correo.contains("admin", ignoreCase = true) }

    PantallaAdmin(titulo = "USUARIOS", scroll = false, espacioHeader = Medidas.EspacioS) {
        BadgeEstado("Total: ${visibles.size} usuarios", MaterialTheme.colorScheme.primary)

        Spacer(Modifier.height(Medidas.EspacioM))

        if (usuarios.isEmpty()) {
            Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text(
                    text = "Aún no hay usuarios registrados",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(visibles) { u ->
                    UsuarioItem(u, onClick = { onOpenPerfil(u.codigo ?: u.correo) })
                }
            }
        }

        Spacer(Modifier.height(Medidas.EspacioM))

        if (allowNew) BotonPrincipal("NUEVO USUARIO", onClick = onNew)
    }
}

@Composable
private fun UsuarioItem(u: Usuario, onClick: () -> Unit) {
    val titulo = u.nombre.ifBlank { u.correo }

    Panel(onClick = onClick) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Avatar(titulo)
            Spacer(Modifier.width(Medidas.EspacioM))
            Column(Modifier.weight(1f)) {
                Text(
                    text = titulo,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = u.correo,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Icon(
                imageVector = Icons.Filled.KeyboardArrowRight,
                contentDescription = "Ver perfil",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// ---------------------------------------------------------------
// Perfil de usuario
// ---------------------------------------------------------------
@Composable
fun AdminPerfilUsuarioView(
    codigo: String,
    onBack: () -> Unit,
    onTurnos: () -> Unit,
    onHorasExtra: () -> Unit,
    allowDangerZone: Boolean = true
) {
    val context = LocalContext.current
    val dao = DatabaseProvider.getUsuarioDao(context)
    val scope = rememberCoroutineScope()

    PantallaAdmin("PERFIL USUARIO", codigo, onBack) {
        Panel {
            Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Avatar(codigo, Medidas.AvatarL)
                Spacer(Modifier.height(Medidas.EspacioM))
                Text(
                    text = codigo,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        Spacer(Modifier.height(Medidas.EspacioL))

        AccionCard("Asignar turnos", "Define días y horarios del operario", Icons.Filled.List, onTurnos)
        Spacer(Modifier.height(10.dp))
        AccionCard("Horas extra", "Registra y revisa horas extra", Icons.Filled.Add, onHorasExtra)

        if (allowDangerZone) {
            Spacer(Modifier.height(Medidas.EspacioL))
            Text(
                text = "ZONA DE PELIGRO",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(Modifier.height(Medidas.EspacioM))
            BotonPrincipal("DESACTIVAR USUARIO") {
                scope.launch { dao.setActivo(codigo, false) }
                onBack()
            }
            Spacer(Modifier.height(10.dp))
            BotonPrincipal("ELIMINAR USUARIO") {
                scope.launch { dao.getByCodigo(codigo)?.let { dao.delete(it) } }
                onBack()
            }
        }
    }
}

// ---------------------------------------------------------------
// Nuevo usuario
// ---------------------------------------------------------------
@Composable
fun AdminNuevoUsuarioView(onBack: () -> Unit) {
    val context = LocalContext.current
    val vm: UsuarioViewModel = viewModel(factory = UsuarioViewModelFactory(DatabaseProvider.getUsuarioDao(context)))
    val faenaVm: FaenaViewModel = viewModel(factory = FaenaViewModelFactory(DatabaseProvider.getFaenaDao(context)))
    val faenas by faenaVm.faenas.collectAsState()

    var correo by remember { mutableStateOf("") }
    var nombre by remember { mutableStateOf("") }
    var faenaId by remember { mutableStateOf(0L) }
    var rol by remember { mutableStateOf(Rol.OPERARIO) }

    LaunchedEffect(faenas) {
        if (faenaId == 0L && faenas.isNotEmpty()) faenaId = faenas.first().id
    }

    val correoValido = android.util.Patterns.EMAIL_ADDRESS.matcher(correo.trim()).matches()
    val formularioValido = correoValido && nombre.isNotBlank() && faenaId != 0L
    val faenaNombre = faenas.firstOrNull { it.id == faenaId }?.nombre ?: "Seleccionar"

    PantallaAdmin("NUEVO USUARIO", onBack = onBack) {
        Panel {
            CampoTexto(
                etiqueta = "Correo",
                valor = correo,
                onCambio = { correo = it },
                error = if (correo.isNotBlank() && !correoValido) "Correo inválido" else null
            )
            Spacer(Modifier.height(Medidas.EspacioM))
            CampoTexto("Nombre", nombre, { nombre = it })
            Spacer(Modifier.height(Medidas.EspacioM))
            CampoDropdown(
                etiqueta = "Faena asignada",
                valorActual = faenaNombre,
                opciones = faenas,
                textoOpcion = { it.nombre },
                onSeleccion = { faenaId = it.id }
            )
        }

        Spacer(Modifier.height(Medidas.EspacioM))

        CampoDropdown(
            etiqueta = "Rol",
            valorActual = rol.name,
            opciones = Rol.values().toList(),
            textoOpcion = { it.name },
            onSeleccion = { rol = it }
        )

        Spacer(Modifier.height(Medidas.EspacioM))

        Text(
            text = "La contraseña inicial será \"123456\".",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(Medidas.EspacioL))

        BotonPrincipal("GUARDAR", enabled = formularioValido) {
            vm.agregar(
                Usuario(
                    correo = correo.trim().lowercase(),
                    nombre = nombre,
                    passwordHash = sha256("123456"),
                    codigo = "OP-${System.currentTimeMillis().toString().takeLast(3)}",
                    rol = rol,
                    faenaId = faenaId,
                    activo = true
                )
            )
            onBack()
        }
    }
}

// ---------------------------------------------------------------
// Detalle operario (pendiente de implementar)
// ---------------------------------------------------------------
@Composable
fun AdminDetalleOperarioView(codigo: String, onBack: () -> Unit) {
    PantallaAdmin("DETALLE OPERARIO", codigo, onBack, scroll = false) {
        PlaceholderCard("Día a día (placeholder)")
    }
}