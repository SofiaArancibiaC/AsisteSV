package com.js.smartvolt.asiste.ui.view

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.js.smartvolt.asiste.model.EstadoAsistencia
import com.js.smartvolt.asiste.model.admin.Faena
import com.js.smartvolt.asiste.ui.components.OsmMapView
import com.js.smartvolt.asiste.viewmodel.AsistenciaViewModel
import java.time.LocalDate
import java.time.LocalTime

private val PERMISOS_UBICACION = arrayOf(
    Manifest.permission.ACCESS_FINE_LOCATION,
    Manifest.permission.ACCESS_COARSE_LOCATION
)

private fun tienePermisoPreciso(context: Context): Boolean =
    ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

private fun Context.findActivity(): Activity? {
    var ctx: Context? = this
    while (ctx is ContextWrapper) {
        if (ctx is Activity) return ctx
        ctx = ctx.baseContext
    }
    return null
}

@Composable
fun InicioRoute(
    viewModel: AsistenciaViewModel,
    faena: Faena,
    codigoOperario: String,
    fecha: LocalDate,
    inicioTurno: LocalTime,
    finTurno: LocalTime,
    frente: String,
    sinConexion: Boolean,
    onResultado: (ResultadoMarca) -> Unit,
    onVerCalendario: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val presencia by viewModel.presencia.collectAsState()
    val asistenciaHoy by viewModel.asistenciaHoy.collectAsState()

    var permisoConcedido by remember { mutableStateOf(tienePermisoPreciso(context)) }
    var yaPregunto by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { respuesta ->
        permisoConcedido = respuesta[Manifest.permission.ACCESS_FINE_LOCATION] == true
        if (permisoConcedido) viewModel.actualizarPresencia(faena)
    }

    val pedirPermiso: () -> Unit = {
        val activity = context.findActivity()
        val rationale = activity != null && ActivityCompat.shouldShowRequestPermissionRationale(
            activity,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        if (yaPregunto && !rationale) {
            // Denegado para siempre: mandar a Ajustes de la app
            context.startActivity(
                Intent(
                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.fromParts("package", context.packageName, null)
                )
            )
        } else {
            yaPregunto = true
            val activity = context.findActivity()
            if (activity != null) {
                ActivityCompat.requestPermissions(activity, PERMISOS_UBICACION, 1001)
            }
        }
    }

    // Al entrar: lee el GPS o pide el permiso
    LaunchedEffect(faena.id) {
        if (tienePermisoPreciso(context)) {
            permisoConcedido = true
            viewModel.actualizarPresencia(faena)
        } else {
            permisoConcedido = false
        }
    }

    // Al volver de Ajustes, re-verifica
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                val ok = tienePermisoPreciso(context)
                if (ok && !permisoConcedido) {
                    permisoConcedido = true
                    viewModel.actualizarPresencia(faena)
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    // Entrega cada resultado una sola vez
    LaunchedEffect(Unit) {
        viewModel.resultado.collect { r ->
            if (r != null) {
                onResultado(r)
                viewModel.limpiarResultado()
            }
        }
    }

    val entradaHecha = asistenciaHoy?.estado == EstadoAsistencia.CUMPLIDA ||
            asistenciaHoy?.estado == EstadoAsistencia.ATRASADA
    val salidaHecha = asistenciaHoy?.registroSalida != null

    val textoBoton = when {
        salidaHecha -> "JORNADA CERRADA"
        entradaHecha -> "MARCAR SALIDA"
        else -> "MARCAR ENTRADA"
    }

    InicioView(
        codigoOperario = codigoOperario,
        nombreFaena = faena.nombre,
        fecha = fecha,
        inicioTurno = inicioTurno,
        finTurno = finTurno,
        frente = frente,
        validacion = ValidacionPresencia(
            gpsDentroFaena = presencia.dentroFaena,
            balizaDetectada = false,
            rssiDbm = null
        ),
        sinConexion = sinConexion,
        textoBoton = textoBoton,
        aviso = presencia.aviso,
        cargando = presencia.cargando || salidaHecha,
        onMarcarEntrada = {
            when {
                !permisoConcedido -> pedirPermiso()
                salidaHecha -> Unit
                entradaHecha -> viewModel.marcarSalida(faena, fecha, sinConexion)
                else -> viewModel.marcarEntrada(faena, fecha, inicioTurno, finTurno, sinConexion, codigoOperario)
            }
        },
        onVerCalendario = onVerCalendario,
        mapa = {
            OsmMapView(
                faenaLat = faena.latitud,
                faenaLng = faena.longitud,
                radioMetros = faena.radioMetros,
                usuarioLat = presencia.latitud,
                usuarioLng = presencia.longitud
            )
        },
        modifier = modifier
    )
}