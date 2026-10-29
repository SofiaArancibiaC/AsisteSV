package com.js.smartvolt.asiste.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.js.smartvolt.asiste.model.Asistencia
import com.js.smartvolt.asiste.model.data.local.AsistenciaDAO
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth

@OptIn(ExperimentalCoroutinesApi::class)
@RequiresApi(Build.VERSION_CODES.O)
class AsistenciaViewModel(private val dao: AsistenciaDAO) : ViewModel() {

    // Mes que se está mostrando
    private val _month = MutableStateFlow(YearMonth.now())
    val month: StateFlow<YearMonth> = _month.asStateFlow()

    // Attendances del mes actual (mapa fecha -> asistencia)
    private val _attendances = MutableStateFlow<Map<LocalDate, Asistencia>>(emptyMap())
    val attendances: StateFlow<Map<LocalDate, Asistencia>> = _attendances.asStateFlow()

    // Detalle seleccionado al tocar un día
    private val _selected = MutableStateFlow<Asistencia?>(null)
    val selected: StateFlow<Asistencia?> = _selected.asStateFlow()

    init {
        // Carga inicial y recarga cuando cambia el mes
        viewModelScope.launch {
            _month.flatMapLatest { m ->
                val start = m.atDay(1)
                val end = m.atEndOfMonth()
                dao.getByRange(start, end).map { list ->
                    list.associateBy { it.fecha }
                }
            }.collect { map ->
                _attendances.value = map
            }
        }
    }

    fun setMonth(m: YearMonth) {
        _month.value = m
    }

    fun selectDate(date: LocalDate) {
        _selected.value = _attendances.value[date]
    }

    fun clearSelection() {
        _selected.value = null
    }
}

class AsistenciaViewModelFactory(private val dao: AsistenciaDAO) : ViewModelProvider.Factory {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AsistenciaViewModel(dao) as T
    }
}