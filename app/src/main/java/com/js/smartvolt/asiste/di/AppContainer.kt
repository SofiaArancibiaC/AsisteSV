package com.js.smartvolt.asiste.di

import android.content.Context
import com.js.smartvolt.asiste.model.data.local.DatabaseProvider
import com.js.smartvolt.asiste.data.repository.*

class AppContainer(context: Context) {
    private val dbCtx = context.applicationContext
    val usuarioDao = DatabaseProvider.getUsuarioDao(dbCtx)
    val asistenciaDao = DatabaseProvider.getAsistenciaDao(dbCtx)
    val faenaDao = DatabaseProvider.getFaenaDao(dbCtx)
    val alertaDao = DatabaseProvider.getAlertaDao(dbCtx)
    val turnoDao = DatabaseProvider.getTurnoDao(dbCtx)
    val horaExtraDao = DatabaseProvider.getHoraExtraDao(dbCtx)

    val authRepository = AuthRepository(usuarioDao)
    val usuarioRepository = UsuarioRepository(usuarioDao)
    val faenaRepository = FaenaRepository(faenaDao)
    val asistenciaRepository = AsistenciaRepository(asistenciaDao)
    val alertaRepository = AlertaRepository(alertaDao)
    val turnoRepository = TurnoRepository(turnoDao)
    val horaExtraRepository = HoraExtraRepository(horaExtraDao)
}