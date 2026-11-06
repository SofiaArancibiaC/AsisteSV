package com.js.smartvolt.asiste.ui.view.admin

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.js.smartvolt.asiste.model.RecuperarUsuario
import com.js.smartvolt.asiste.model.admin.Rol
import com.js.smartvolt.asiste.model.admin.HoraExtra
import com.js.smartvolt.asiste.model.admin.EstadoHorasExtras
import com.js.smartvolt.asiste.model.admin.Turno
import com.js.smartvolt.asiste.model.admin.Usuario
import com.js.smartvolt.asiste.model.data.local.DatabaseProvider
import com.js.smartvolt.asiste.model.data.local.sha256
import com.js.smartvolt.asiste.ui.components.*
import com.js.smartvolt.asiste.ui.theme.Medidas
import com.js.smartvolt.asiste.ui.theme.colorsCampo
import com.js.smartvolt.asiste.ui.theme.estados
import com.js.smartvolt.asiste.ui.theme.inicial
import com.js.smartvolt.asiste.ui.theme.tenue
import com.js.smartvolt.asiste.viewmodel.FaenaViewModel
import com.js.smartvolt.asiste.viewmodel.FaenaViewModelFactory
import com.js.smartvolt.asiste.viewmodel.UsuarioViewModel
import com.js.smartvolt.asiste.viewmodel.UsuarioViewModelFactory
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.launch

// ---------------------------------------------------------------
// Componentes compartidos
// ---------------------------------------------------------------
@Composable
private fun Avatar(texto: String, size: androidx.compose.ui.unit.Dp = Medidas.AvatarS) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primaryContainer),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = texto.inicial(),
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            style = if (size > Medidas.AvatarS) MaterialTheme.typography.headlineMedium
            else MaterialTheme.typography.titleMedium
        )
    }
}

@Composable
private fun Panel(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clip(MaterialTheme.shapes.large).clickable(onClick = onClick) else Modifier),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(Medidas.BordeCard, MaterialTheme.colorScheme.outline)
    ) {
        Column(Modifier.padding(Medidas.PaddingCard), content = content)
    }
}

@Composable
private fun EtiquetaCampo(texto: String) {
    Text(
        text = texto,
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
    Spacer(Modifier.height(Medidas.EspacioXS))
}

@Composable
private fun BadgeEstado(texto: String, color: Color) {
    Box(
        modifier = Modifier
            .clip(CircleShape)
            .background(color.tenue())
            .padding(horizontal = 10.dp, vertical = 5.dp)
    ) {
        Text(
            text = texto,
            color = color,
            style = MaterialTheme.typography.labelMedium
        )
    }
}

@Composable
private fun BotonPrincipal(texto: String, enabled: Boolean = true, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier
            .fillMaxWidth()
            .height(Medidas.AlturaBoton),
        shape = MaterialTheme.shapes.medium
    ) {
        Text(texto, style = MaterialTheme.typography.labelLarge)
    }
}

/** Campo que se ve como input pero abre un diálogo (fecha, hora). */
@Composable
private fun CampoSelector(etiqueta: String, valor: String, onClick: () -> Unit) {
    Column {
        EtiquetaCampo(etiqueta)
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.medium)
                .clickable(onClick = onClick),
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(Medidas.BordeCard, MaterialTheme.colorScheme.outline)
        ) {
            Text(
                text = valor,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)
            )
        }
    }
}

// ---------------------------------------------------------------
// Lista de usuarios
// ---------------------------------------------------------------
@Composable
fun AdminUsuariosView(onNew: () -> Unit, onOpenPerfil: (String) -> Unit) {
    val context = LocalContext.current
    val dao = DatabaseProvider.getUsuarioDao(context)
    val vm: UsuarioViewModel = viewModel(factory = UsuarioViewModelFactory(dao))
    val usuarios by vm.usuarios.collectAsState()
    val usuariosVisibles = usuarios.filter { !it.correo.contains("admin", ignoreCase = true) }

    Column(
        Modifier
            .fillMaxSize()
            .padding(horizontal = Medidas.PaddingPantalla, vertical = Medidas.EspacioM)
    ) {
        AdminHeader("USUARIOS")

        Spacer(Modifier.height(Medidas.EspacioS))

        BadgeEstado("Total: ${usuariosVisibles.size} usuarios", MaterialTheme.colorScheme.primary)

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
                items(usuariosVisibles) { u ->
                    Panel(onClick = { onOpenPerfil(u.codigo ?: u.correo) }) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Avatar(u.nombre.ifBlank { u.correo })
                            Spacer(Modifier.width(Medidas.EspacioM))
                            Column(Modifier.weight(1f)) {
                                Text(
                                    text = u.nombre.ifBlank { u.correo },
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
            }
        }

        Spacer(Modifier.height(Medidas.EspacioM))

        BotonPrincipal("NUEVO USUARIO", onClick = onNew)
    }
}

// ---------------------------------------------------------------
// Perfil de usuario
// ---------------------------------------------------------------
@Composable
private fun AccionCard(
    titulo: String,
    descripcion: String,
    icono: ImageVector,
    onClick: () -> Unit
) {
    Panel(onClick = onClick) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(Medidas.AvatarS)
                    .clip(MaterialTheme.shapes.medium)
                    .background(MaterialTheme.colorScheme.primary.tenue()),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icono,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(Modifier.width(Medidas.EspacioM))
            Column(Modifier.weight(1f)) {
                Text(
                    text = titulo,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = descripcion,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                imageVector = Icons.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun AdminPerfilUsuarioView(codigo: String, onBack: () -> Unit, onTurnos: () -> Unit, onHorasExtra: () -> Unit) {
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = Medidas.PaddingPantalla, vertical = Medidas.EspacioM)
    ) {
        AdminHeader("PERFIL USUARIO", codigo, onBack)

        Spacer(Modifier.height(Medidas.EspacioL))

        Panel {
            Column(
                Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
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

        AccionCard(
            titulo = "Asignar turnos",
            descripcion = "Define días y horarios del operario",
            icono = Icons.Filled.List,
            onClick = onTurnos
        )
        Spacer(Modifier.height(10.dp))
        AccionCard(
            titulo = "Horas extra",
            descripcion = "Registra y revisa horas extra",
            icono = Icons.Filled.Add,
            onClick = onHorasExtra
        )
    }
}

// ---------------------------------------------------------------
// Nuevo usuario
// ---------------------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminNuevoUsuarioView(onBack: () -> Unit) {
    val context = LocalContext.current
    val dao = DatabaseProvider.getUsuarioDao(context)
    val faenaDao = DatabaseProvider.getFaenaDao(context)
    val vm: UsuarioViewModel = viewModel(factory = UsuarioViewModelFactory(dao))
    val faenaVm: FaenaViewModel = viewModel(factory = FaenaViewModelFactory(faenaDao))
    val faenas by faenaVm.faenas.collectAsState()

    var correo by remember { mutableStateOf("") }
    var nombre by remember { mutableStateOf("") }
    var selectedFaenaId by remember { mutableStateOf(0L) }
    var expanded by remember { mutableStateOf(false) }
    var selectedRol by remember { mutableStateOf(Rol.OPERARIO) }
    var expandRol by remember { mutableStateOf(false) }

    LaunchedEffect(faenas) {
        if (selectedFaenaId == 0L && faenas.isNotEmpty()) selectedFaenaId = faenas.first().id
    }

    val correoValido = android.util.Patterns.EMAIL_ADDRESS.matcher(correo.trim()).matches()
    val correoInvalido = correo.isNotBlank() && !correoValido
    val formularioValido = correoValido && nombre.isNotBlank() && selectedFaenaId != 0L
    val selectedName = faenas.firstOrNull { it.id == selectedFaenaId }?.nombre ?: "Seleccionar"

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = Medidas.PaddingPantalla, vertical = Medidas.EspacioM)
    ) {
        AdminHeader("NUEVO USUARIO", onBack = onBack)

        Spacer(Modifier.height(Medidas.EspacioL))

        Panel {
            OutlinedTextField(
                value = correo,
                onValueChange = { correo = it },
                label = { Text("Correo") },
                isError = correoInvalido,
                supportingText = if (correoInvalido) {
                    { Text("Correo inválido") }
                } else null,
                singleLine = true,
                shape = MaterialTheme.shapes.medium,
                colors = colorsCampo(),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(Medidas.EspacioM))

            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre") },
                singleLine = true,
                shape = MaterialTheme.shapes.medium,
                colors = colorsCampo(),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(Medidas.EspacioM))

            ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
                OutlinedTextField(
                    value = selectedName,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Faena asignada") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    shape = MaterialTheme.shapes.medium,
                    colors = colorsCampo(),
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    faenas.forEach { f ->
                        DropdownMenuItem(
                            text = { Text(f.nombre) },
                            onClick = { selectedFaenaId = f.id; expanded = false }
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(Medidas.EspacioM))

        ExposedDropdownMenuBox(expanded = expandRol, onExpandedChange = { expandRol = !expandRol }) {
            OutlinedTextField(
                value = selectedRol.name,
                onValueChange = {},
                readOnly = true,
                label = { Text("Rol") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expandRol) },
                shape = MaterialTheme.shapes.medium,
                colors = colorsCampo(),
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(expanded = expandRol, onDismissRequest = { expandRol = false }) {
                Rol.values().forEach { r ->
                    DropdownMenuItem(
                        text = { Text(r.name) },
                        onClick = { selectedRol = r; expandRol = false }
                    )
                }
            }
        }

        Spacer(Modifier.height(Medidas.EspacioM))

        Text(
            text = "La contraseña inicial será \"123456\".",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(Medidas.EspacioL))

        BotonPrincipal("GUARDAR", enabled = formularioValido) {
            if (formularioValido) {
                val codigo = "OP-${System.currentTimeMillis().toString().takeLast(3)}"
                vm.agregar(
                    Usuario(
                        correo = correo.trim().lowercase(),
                        nombre = nombre,
                        passwordHash = sha256("123456"),
                        codigo = codigo,
                        rol = selectedRol,
                        faenaId = selectedFaenaId,
                        activo = true
                    )
                )
                runBlocking {
                    dao.insertar(
                        Usuario(
                            correo = correo,
                            nombre = nombre,
                            passwordHash = sha256("123456"),
                            codigo = codigo,
                            rol = selectedRol,
                            faenaId = selectedFaenaId,
                            activo = true
                        )
                    )
                }
                onBack()
            }
        }
    }
}

// ---------------------------------------------------------------
// Asignar turnos
// ---------------------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminTurnosView(codigo: String, onBack: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val usuarioDao = DatabaseProvider.getUsuarioDao(context)
    val turnoDao = DatabaseProvider.getTurnoDao(context)
    val operarios by usuarioDao.getByRol(Rol.OPERARIO).collectAsState(initial = emptyList())

    var selectedUsuarioId by remember { mutableStateOf(0L) }
    var selectedUsuarioCodigo by remember { mutableStateOf(codigo) }
    var dia by remember { mutableStateOf(java.time.LocalDate.now().toString()) }
    var horario by remember { mutableStateOf("08:00") }
    var expandHorario by remember { mutableStateOf(false) }
    var activo by remember { mutableStateOf(true) }
    var expanded by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    val datePickerState = rememberDatePickerState()
    val timePickerState = rememberTimePickerState()

    LaunchedEffect(operarios) {
        if (selectedUsuarioId == 0L && operarios.isNotEmpty()) {
            val match = operarios.firstOrNull { it.codigo == codigo } ?: operarios.first()
            selectedUsuarioId = match.id
            selectedUsuarioCodigo = match.codigo
        }
    }

    val turnos by (if (selectedUsuarioId != 0L) turnoDao.getByUsuario(selectedUsuarioId) else flowOf(emptyList()))
        .collectAsState(initial = emptyList())

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = Medidas.PaddingPantalla, vertical = Medidas.EspacioM)
    ) {
        AdminHeader("ASIGNAR TURNOS", selectedUsuarioCodigo, onBack)

        Spacer(Modifier.height(Medidas.EspacioL))

        Panel {
            OutlinedTextField(
                value = selectedUsuarioCodigo,
                onValueChange = {},
                readOnly = true,
                label = { Text("Operario") },
                shape = MaterialTheme.shapes.medium,
                colors = colorsCampo(),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(Medidas.EspacioM))

            Row(horizontalArrangement = Arrangement.spacedBy(Medidas.EspacioM)) {
                Box(Modifier.weight(1f)) {
                    CampoSelector(
                        etiqueta = "DÍA",
                        valor = if (dia.isBlank()) "Seleccionar día" else dia,
                        onClick = { showDatePicker = true }
                    )
                }
                Box(Modifier.weight(1f)) {
                    ExposedDropdownMenuBox(expanded = expandHorario, onExpandedChange = { expandHorario = !expandHorario }) {
                        OutlinedTextField(
                            value = horario,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("TURNO") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expandHorario) },
                            shape = MaterialTheme.shapes.medium,
                            colors = colorsCampo(),
                            modifier = Modifier.menuAnchor().fillMaxWidth()
                        )
                        ExposedDropdownMenu(expanded = expandHorario, onDismissRequest = { expandHorario = false }) {
                            DropdownMenuItem(text = { Text("AM 08:00-14:00") }, onClick = { horario = "08:00"; expandHorario = false })
                            DropdownMenuItem(text = { Text("PM 15:00-21:00") }, onClick = { horario = "15:00"; expandHorario = false })
                        }
                    }
                }
            }

            Spacer(Modifier.height(Medidas.EspacioM))

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Turno activo",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Switch(checked = activo, onCheckedChange = { activo = it })
            }
        }

        Spacer(Modifier.height(Medidas.EspacioL))

        BotonPrincipal("GUARDAR TURNO", enabled = selectedUsuarioId != 0L) {
            if (selectedUsuarioId != 0L) {
                runBlocking {
                    turnoDao.upsert(
                        Turno(
                            usuarioId = selectedUsuarioId,
                            dia = java.time.LocalDate.parse(dia),
                            activo = activo,
                            horario = java.time.LocalTime.parse(horario)
                        )
                    )
                }
            }
        }

        Spacer(Modifier.height(Medidas.EspacioL))

        Text(
            text = "TURNOS ASIGNADOS",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(Medidas.EspacioS))

        if (turnos.isEmpty()) {
            Text(
                text = "Este operario aún no tiene turnos",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth().padding(vertical = Medidas.EspacioM),
                textAlign = TextAlign.Center
            )
        } else {
            turnos.sortedBy { it.dia }.forEach { t ->
                val colorTurno = if (t.activo) MaterialTheme.estados.cumplida else MaterialTheme.estados.enRevision
                Panel(Modifier.padding(bottom = 10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text(
                                text = "${t.dia}",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "${t.horario}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        BadgeEstado(if (t.activo) "Activo" else "Inactivo", colorTurno)
                        IconButton(onClick = { scope.launch { turnoDao.delete(t) } }) {
                            Icon(
                                imageVector = Icons.Filled.Delete,
                                contentDescription = "Eliminar turno",
                                tint = MaterialTheme.estados.rechazada
                            )
                        }
                    }
                }
            }
        }
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        dia = java.time.Instant.ofEpochMilli(it)
                            .atZone(java.time.ZoneId.of("UTC"))
                            .toLocalDate()
                            .toString()
                    }
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancelar") }
            }
        ) { DatePicker(state = datePickerState) }
    }

    if (showTimePicker) {
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    horario = "%02d:%02d".format(timePickerState.hour, timePickerState.minute)
                    showTimePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) { Text("Cancelar") }
            },
            text = { TimePicker(state = timePickerState) }
        )
    }
}

// ---------------------------------------------------------------
// Placeholders (pendientes de implementar)
// ---------------------------------------------------------------
@Composable
private fun PlaceholderCard(texto: String) {
    Panel {
        Text(
            text = texto,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminHorasExtraView(codigo: String, onBack: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val usuarioDao = DatabaseProvider.getUsuarioDao(context)
    val horaExtraDao = DatabaseProvider.getHoraExtraDao(context)
    val operarios by usuarioDao.getByRol(Rol.OPERARIO).collectAsState(initial = emptyList())

    var selectedUsuarioId by remember { mutableStateOf(0L) }
    var selectedUsuarioCodigo by remember { mutableStateOf(codigo) }
    var fecha by remember { mutableStateOf(java.time.LocalDate.now().toString()) }
    var horas by remember { mutableStateOf("1.0") }
    var motivo by remember { mutableStateOf("") }
    var nota by remember { mutableStateOf("") }
    var estado by remember { mutableStateOf(EstadoHorasExtras.PENDIENTE) }
    var expandEstado by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    LaunchedEffect(operarios) {
        if (selectedUsuarioId == 0L && operarios.isNotEmpty()) {
            val match = operarios.firstOrNull { it.codigo == codigo } ?: operarios.first()
            selectedUsuarioId = match.id
            selectedUsuarioCodigo = match.codigo
        }
    }

    val horasExtra by (if (selectedUsuarioId != 0L) horaExtraDao.getByUsuario(selectedUsuarioId) else flowOf(emptyList()))
        .collectAsState(initial = emptyList())

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = Medidas.PaddingPantalla, vertical = Medidas.EspacioM)
    ) {
        AdminHeader("HORAS EXTRA", selectedUsuarioCodigo, onBack)

        Spacer(Modifier.height(Medidas.EspacioL))

        Panel {
            ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
                OutlinedTextField(
                    value = selectedUsuarioCodigo,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Operario") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                    shape = MaterialTheme.shapes.medium,
                    colors = colorsCampo(),
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    operarios.forEach { u ->
                        DropdownMenuItem(
                            text = { Text(u.codigo) },
                            onClick = {
                                selectedUsuarioId = u.id
                                selectedUsuarioCodigo = u.codigo
                                expanded = false
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(Medidas.EspacioM))

            Row(horizontalArrangement = Arrangement.spacedBy(Medidas.EspacioM)) {
                Box(Modifier.weight(1f)) {
                    CampoSelector(
                        etiqueta = "FECHA",
                        valor = if (fecha.isBlank()) "Seleccionar fecha" else fecha,
                        onClick = { showDatePicker = true }
                    )
                }
                Box(Modifier.weight(1f)) {
                    OutlinedTextField(
                        value = horas,
                        onValueChange = { horas = it },
                        label = { Text("HORAS") },
                        singleLine = true,
                        shape = MaterialTheme.shapes.medium,
                        colors = colorsCampo(),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(Modifier.height(Medidas.EspacioM))

            OutlinedTextField(
                value = motivo,
                onValueChange = { motivo = it },
                label = { Text("Motivo") },
                singleLine = true,
                shape = MaterialTheme.shapes.medium,
                colors = colorsCampo(),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(Medidas.EspacioM))

            ExposedDropdownMenuBox(expanded = expandEstado, onExpandedChange = { expandEstado = !expandEstado }) {
                OutlinedTextField(
                    value = estado.name,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("ESTADO") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expandEstado) },
                    shape = MaterialTheme.shapes.medium,
                    colors = colorsCampo(),
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(expanded = expandEstado, onDismissRequest = { expandEstado = false }) {
                    EstadoHorasExtras.values().forEach { e ->
                        DropdownMenuItem(
                            text = { Text(e.name) },
                            onClick = { estado = e; expandEstado = false }
                        )
                    }
                }
            }

            Spacer(Modifier.height(Medidas.EspacioM))

            OutlinedTextField(
                value = nota,
                onValueChange = { nota = it },
                label = { Text("Nota") },
                shape = MaterialTheme.shapes.medium,
                colors = colorsCampo(),
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(Modifier.height(Medidas.EspacioL))

        BotonPrincipal("GUARDAR HORA EXTRA", enabled = selectedUsuarioId != 0L && horas.toDoubleOrNull() != null) {
            if (selectedUsuarioId != 0L) {
                runBlocking {
                    horaExtraDao.upsert(
                        HoraExtra(
                            usuarioId = selectedUsuarioId,
                            fecha = java.time.LocalDate.parse(fecha),
                            horas = horas.toDoubleOrNull() ?: 0.0,
                            motivo = motivo,
                            estado = estado,
                            nota = nota
                        )
                    )
                }
                motivo = ""
                nota = ""
            }
        }

        Spacer(Modifier.height(Medidas.EspacioL))

        Text(
            text = "HORAS EXTRA REGISTRADAS",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(Medidas.EspacioS))

        if (horasExtra.isEmpty()) {
            Text(
                text = "Este operario aún no tiene horas extra",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth().padding(vertical = Medidas.EspacioM),
                textAlign = TextAlign.Center
            )
        } else {
            horasExtra.sortedByDescending { it.fecha }.forEach { h ->
                val colorEstado = when (h.estado) {
                    EstadoHorasExtras.APROBADO -> MaterialTheme.estados.cumplida
                    EstadoHorasExtras.RECHAZADO -> MaterialTheme.estados.rechazada
                    else -> MaterialTheme.estados.enRevision
                }
                Panel(Modifier.padding(bottom = 10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text(
                                text = "${h.fecha} · ${h.horas} h",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = h.motivo.ifBlank { "Sin motivo" },
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            if (h.nota.isNotBlank()) {
                                Text(
                                    text = h.nota,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        if (h.estado == EstadoHorasExtras.PENDIENTE) {
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                TextButton(onClick = {
                                    scope.launch {
                                        horaExtraDao.upsert(h.copy(estado = EstadoHorasExtras.APROBADO))
                                    }
                                }) { Text("Aprobar") }
                                TextButton(onClick = {
                                    scope.launch {
                                        horaExtraDao.upsert(h.copy(estado = EstadoHorasExtras.RECHAZADO))
                                    }
                                }) { Text("Rechazar") }
                            }
                        } else {
                            BadgeEstado(h.estado.name, colorEstado)
                        }
                        IconButton(onClick = { scope.launch { horaExtraDao.delete(h) } }) {
                            Icon(
                                imageVector = Icons.Filled.Delete,
                                contentDescription = "Eliminar hora extra",
                                tint = MaterialTheme.estados.rechazada
                            )
                        }
                    }
                }
            }
        }
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        fecha = java.time.Instant.ofEpochMilli(it)
                            .atZone(java.time.ZoneId.of("UTC"))
                            .toLocalDate()
                            .toString()
                    }
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancelar") }
            }
        ) { DatePicker(state = datePickerState) }
    }
}

@Composable
fun AdminDetalleOperarioView(codigo: String, onBack: () -> Unit) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(horizontal = Medidas.PaddingPantalla, vertical = Medidas.EspacioM)
    ) {
        AdminHeader("DETALLE OPERARIO", codigo, onBack)
        Spacer(Modifier.height(Medidas.EspacioL))
        PlaceholderCard("Día a día (placeholder)")
    }
}
