package com.js.smartvolt.asiste.viewmodel

import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.js.smartvolt.asiste.model.Asistencia
import com.js.smartvolt.asiste.ui.view.ResultadoMarca
import com.js.smartvolt.asiste.model.EstadoAsistencia
import com.js.smartvolt.asiste.model.admin.Faena
import com.js.smartvolt.asiste.model.data.local.AsistenciaDAO
import com.js.smartvolt.asiste.data.location.ResultadoUbicacion
import com.js.smartvolt.asiste.data.location.UbicacionProvider

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.YearMonth
import java.time.format.DateTimeFormatter

private const val TOLERANCIA_MINUTOS = 10L
private val FORMATO_HORA: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")

data class PresenciaUi(
    val cargando: Boolean = false,
    val latitud: Double? = null,
    val longitud: Double? = null,
    val precisionMetros: Float? = null,
    val distanciaMetros: Float? = null,
    val dentroFaena: Boolean = false,
    val simulada: Boolean = false,
    val aviso: String? = null
)

@OptIn(ExperimentalCoroutinesApi::class)
class AsistenciaViewModel(
    private val dao: AsistenciaDAO,
    private val ubicacion: UbicacionProvider,
    private val alertaDao: com.js.smartvolt.asiste.model.data.local.AlertaDao? = null
) : ViewModel() {

    // ---------- Calendario ----------
    private val _month = MutableStateFlow(YearMonth.now())
    val month: StateFlow<YearMonth> = _month.asStateFlow()

    private val _attendances = MutableStateFlow<Map<LocalDate, Asistencia>>(emptyMap())
    val attendances: StateFlow<Map<LocalDate, Asistencia>> = _attendances.asStateFlow()

    private val _selected = MutableStateFlow<Asistencia?>(null)
    val selected: StateFlow<Asistencia?> = _selected.asStateFlow()

    // ---------- Marca con GPS ----------
    private val _presencia = MutableStateFlow(PresenciaUi())
    val presencia: StateFlow<PresenciaUi> = _presencia.asStateFlow()

    private val _resultado = MutableStateFlow<ResultadoMarca?>(null)
    val resultado: StateFlow<ResultadoMarca?> = _resultado.asStateFlow()

    private val _asistenciaHoy = MutableStateFlow<Asistencia?>(null)
    val asistenciaHoy: StateFlow<Asistencia?> = _asistenciaHoy.asStateFlow()

    private var marcando = false

    init {
        viewModelScope.launch {
            _month.flatMapLatest { m ->
                dao.getByRange(m.atDay(1), m.atEndOfMonth()).map { list ->
                    list.associateBy { it.fecha }
                }
            }.collect { _attendances.value = it }
        }
        viewModelScope.launch {
            _asistenciaHoy.value = dao.getByFecha(LocalDate.now()).lastOrNull()
        }
    }

    fun setMonth(m: YearMonth) { _month.value = m }
    fun selectDate(date: LocalDate) { _selected.value = _attendances.value[date] }
    fun clearSelection() { _selected.value = null }
    fun limpiarResultado() { _resultado.value = null }

    fun actualizarPresencia(faena: Faena) {
        viewModelScope.launch {
            _presencia.value = _presencia.value.copy(cargando = true, aviso = null)
            _presencia.value = leerPresencia(faena)
        }
    }

    fun marcarEntrada(
        faena: Faena,
        fecha: LocalDate,
        inicioTurno: LocalTime,
        finTurno: LocalTime,
        sinConexion: Boolean = false,
        codigoOperario: String = "OP-014"
    ) {
        if (marcando) return
        marcando = true
        viewModelScope.launch {
            try {
                val yaMarcada = dao.getByFecha(fecha).firstOrNull {
                    it.estado == EstadoAsistencia.CUMPLIDA || it.estado == EstadoAsistencia.ATRASADA
                }
                if (yaMarcada != null) {
                    _resultado.value = ResultadoMarca.aprobada(
                        hora = yaMarcada.registro?.format(FORMATO_HORA) ?: "--:--",
                        sinConexion = sinConexion
                    )
                    return@launch
                }

                _presencia.value = _presencia.value.copy(cargando = true, aviso = null)
                val lectura = leerPresencia(faena)
                _presencia.value = lectura

                val lat = lectura.latitud
                val lng = lectura.longitud
                val registro = LocalTime.now().withSecond(0).withNano(0)

                when {
                    lat == null || lng == null ->
                        _resultado.value = ResultadoMarca.sinUbicacion(
                            lectura.aviso ?: "No se pudo leer tu ubicación."
                        )

                    lectura.simulada -> {
                        dao.upsert(nuevaAsistencia(fecha, EstadoAsistencia.RECHAZADA, inicioTurno, finTurno, registro, lat, lng))
                        alertaDao?.upsert(com.js.smartvolt.asiste.model.admin.Alerta(
                            codigoOperario = codigoOperario,
                            tipoMarca = com.js.smartvolt.asiste.model.admin.TipoMarca.ENTRADA,
                            hora = registro,
                            faenaId = faena.id,
                            motivo = "Ubicación simulada",
                            estado = "REVISION"
                        ))
                        _resultado.value = ResultadoMarca.rechazadaPorFakeGps(lectura.dentroFaena)
                    }

                    !lectura.dentroFaena -> {
                        dao.upsert(nuevaAsistencia(fecha, EstadoAsistencia.RECHAZADA, inicioTurno, finTurno, registro, lat, lng))
                        alertaDao?.upsert(com.js.smartvolt.asiste.model.admin.Alerta(
                            codigoOperario = codigoOperario,
                            tipoMarca = com.js.smartvolt.asiste.model.admin.TipoMarca.ENTRADA,
                            hora = registro,
                            faenaId = faena.id,
                            motivo = "Fuera de faena",
                            estado = "REVISION"
                        ))
                        _resultado.value = ResultadoMarca.rechazadaFueraDeFaena(
                            (lectura.distanciaMetros ?: 0f).toInt()
                        )
                    }

                    else -> {
                        val atrasada = registro.isAfter(inicioTurno.plusMinutes(TOLERANCIA_MINUTOS))
                        dao.upsert(
                            nuevaAsistencia(
                                fecha,
                                if (atrasada) EstadoAsistencia.ATRASADA else EstadoAsistencia.CUMPLIDA,
                                inicioTurno, finTurno, registro, lat, lng
                            )
                        )
                        _resultado.value = ResultadoMarca.aprobada(
                            hora = registro.format(FORMATO_HORA),
                            sinConexion = sinConexion,
                            atrasada = atrasada
                        )
                    }
                }
                refrescarHoy(fecha)
            } finally {
                marcando = false
            }
        }
    }

    fun marcarSalida(faena: Faena, fecha: LocalDate, sinConexion: Boolean = false) {
        if (marcando) return
        marcando = true
        viewModelScope.launch {
            try {
                val hoy = dao.getByFecha(fecha).lastOrNull {
                    it.estado == EstadoAsistencia.CUMPLIDA || it.estado == EstadoAsistencia.ATRASADA
                }
                if (hoy == null) {
                    _resultado.value = ResultadoMarca.sinEntrada()
                    return@launch
                }
                if (hoy.registroSalida != null) {
                    _resultado.value = ResultadoMarca.salidaRegistrada(hoy.registroSalida.format(FORMATO_HORA))
                    return@launch
                }

                _presencia.value = _presencia.value.copy(cargando = true, aviso = null)
                val lectura = leerPresencia(faena)
                _presencia.value = lectura

                if (lectura.latitud == null || lectura.longitud == null) {
                    _resultado.value = ResultadoMarca.sinUbicacion(lectura.aviso ?: "No se pudo leer tu ubicación.")
                    return@launch
                }
                if (lectura.simulada) {
                    _resultado.value = ResultadoMarca.rechazadaPorFakeGps(lectura.dentroFaena)
                    return@launch
                }
                if (!lectura.dentroFaena) {
                    _resultado.value = ResultadoMarca.rechazadaFueraDeFaena((lectura.distanciaMetros ?: 0f).toInt())
                    return@launch
                }

                val salida = LocalTime.now().withSecond(0).withNano(0)
                dao.upsert(hoy.copy(registroSalida = salida))
                _resultado.value = ResultadoMarca.salidaRegistrada(salida.format(FORMATO_HORA))
                refrescarHoy(fecha)
            } finally {
                marcando = false
            }
        }
    }

    private suspend fun refrescarHoy(fecha: LocalDate) {
        _asistenciaHoy.value = dao.getByFecha(fecha).lastOrNull()
    }

    private fun nuevaAsistencia(
        fecha: LocalDate,
        estado: EstadoAsistencia,
        inicioTurno: LocalTime,
        finTurno: LocalTime,
        registro: LocalTime,
        lat: Double,
        lng: Double
    ) = Asistencia(
        fecha = fecha,
        estado = estado,
        inicioTurno = inicioTurno,
        finTurno = finTurno,
        registro = registro,
        latitud = lat,
        longitud = lng
    )

    private suspend fun leerPresencia(faena: Faena): PresenciaUi =
        when (val r = ubicacion.obtener()) {
            is ResultadoUbicacion.Ok -> {
                val distancia = FloatArray(1)
                Location.distanceBetween(
                    r.lectura.latitud, r.lectura.longitud,
                    faena.latitud, faena.longitud,
                    distancia
                )
                val dentro = distancia[0] <= faena.radioMetros
                val aviso = if (dentro && r.lectura.precisionMetros > faena.radioMetros) {
                    "Tu GPS tiene baja precisión (±${r.lectura.precisionMetros.toInt()} m). Actívalo en modo preciso."
                } else null

                PresenciaUi(
                    latitud = r.lectura.latitud,
                    longitud = r.lectura.longitud,
                    precisionMetros = r.lectura.precisionMetros,
                    distanciaMetros = distancia[0],
                    dentroFaena = dentro,
                    simulada = r.lectura.simulada,
                    aviso = aviso
                )
            }
            ResultadoUbicacion.SinPermiso ->
                PresenciaUi(aviso = "Necesitas el permiso de ubicación precisa para marcar.")
            ResultadoUbicacion.GpsApagado ->
                PresenciaUi(aviso = "Activa el GPS del teléfono e intenta de nuevo.")
            is ResultadoUbicacion.Error ->
                PresenciaUi(aviso = r.mensaje)
        }
}

class AsistenciaViewModelFactory(
    private val dao: AsistenciaDAO,
    private val ubicacion: UbicacionProvider,
    private val alertaDao: com.js.smartvolt.asiste.model.data.local.AlertaDao? = null
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AsistenciaViewModel(dao, ubicacion, alertaDao) as T
    }
}
