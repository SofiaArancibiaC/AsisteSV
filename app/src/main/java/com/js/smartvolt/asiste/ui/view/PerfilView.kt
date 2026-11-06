package com.js.smartvolt.asiste.ui.view

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.js.smartvolt.asiste.model.admin.Usuario
import com.js.smartvolt.asiste.data.repository.FaenaRepository
import com.js.smartvolt.asiste.data.repository.UsuarioRepository
import com.js.smartvolt.asiste.model.data.local.DatabaseProvider
import com.js.smartvolt.asiste.viewmodel.PerfilViewModel
import com.js.smartvolt.asiste.viewmodel.PerfilViewModelFactory
import com.js.smartvolt.asiste.ui.theme.Medidas
import com.js.smartvolt.asiste.ui.theme.estados
import com.js.smartvolt.asiste.ui.theme.inicial
import com.js.smartvolt.asiste.ui.theme.tenue
import kotlinx.coroutines.flow.first

@Composable
fun PerfilView(codigoOperario: String = "OP-014") {
    val context = LocalContext.current
    val vm: PerfilViewModel = viewModel(factory = PerfilViewModelFactory(
        UsuarioRepository(DatabaseProvider.getUsuarioDao(context)),
        FaenaRepository(DatabaseProvider.getFaenaDao(context))
    ))
    val usuario by vm.usuario.collectAsState()
    val faenaNombre by vm.faenaNombre.collectAsState()

    LaunchedEffect(codigoOperario) { vm.cargar(codigoOperario) }

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = Medidas.PaddingPantalla, vertical = Medidas.EspacioM)
    ) {
        Text(
            text = "Perfil",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(Modifier.height(Medidas.EspacioL))

        val u = usuario
        if (u == null) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .padding(Medidas.EspacioL * 2),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        } else {
            val nombre = usuario?.nombre?.takeIf { it.isNotBlank() } ?: u.codigo
            val colorActivo = if (u.activo) MaterialTheme.estados.cumplida
            else MaterialTheme.estados.enRevision

            // Cabecera
            PerfilCard {
                Column(
                    Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(Medidas.AvatarL)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = nombre.inicial(),
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            style = MaterialTheme.typography.headlineMedium
                        )
                    }

                    Spacer(Modifier.height(Medidas.EspacioM))

                    Text(
                        text = nombre,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = u.codigo,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(Modifier.height(Medidas.EspacioM))

                    BadgeEstado(if (u.activo) "Activo" else "Inactivo", colorActivo)
                }
            }

            Spacer(Modifier.height(Medidas.EspacioM))

            // Datos
            PerfilCard {
                Text(
                    text = "DATOS DEL USUARIO",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(Medidas.EspacioS))

                InfoRow("Nombre", usuario?.nombre ?: "-")
                DivisorSuave()
                InfoRow("Correo", usuario?.correo ?: "-")
                DivisorSuave()
                InfoRow("Código operario", u.codigo)
                DivisorSuave()
                InfoRow("Faena", faenaNombre ?: u.faenaId.toString())
                DivisorSuave()
                InfoRow("Rol", u.rol.name)
            }
        }

        Spacer(Modifier.height(Medidas.EspacioL))
    }
}

// ---------------------------------------------------------------
// Componentes
// ---------------------------------------------------------------
@Composable
private fun PerfilCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(Medidas.BordeCard, MaterialTheme.colorScheme.outline)
    ) {
        Column(Modifier.padding(Medidas.PaddingCard), content = content)
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = label.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.height(Medidas.EspacioXS))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun DivisorSuave() {
    HorizontalDivider(color = MaterialTheme.colorScheme.outline.tenue(0.5f))
}

@Composable
private fun BadgeEstado(texto: String, color: Color) {
    Box(
        modifier = Modifier
            .clip(CircleShape)
            .background(color.tenue())
            .padding(horizontal = 12.dp, vertical = 5.dp)
    ) {
        Text(
            text = texto,
            color = color,
            style = MaterialTheme.typography.labelMedium
        )
    }
}
