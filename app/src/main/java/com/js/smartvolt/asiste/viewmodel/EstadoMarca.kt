package com.js.smartvolt.asiste.viewmodel

data class CheckPresencia(
    val etiqueta: String,
    val valor: String,
    val ok: Boolean
)

data class ResultadoMarca(
    val aprobada: Boolean,
    val titulo: String,
    val subtitulo: String,
    val checks: List<CheckPresencia>,
    val mensaje: String,
    val nota: String? = null,
    val textoBoton: String
) {
    companion object {
        fun aprobada(
            hora: String,
            rssi: Int? = null,
            sinConexion: Boolean = false,
            atrasada: Boolean = false
        ): ResultadoMarca {
            val checks = buildList {
                add(CheckPresencia("GPS dentro de la faena", "Sí", true))
                if (rssi != null) add(CheckPresencia("Baliza BLE de la faena", "$rssi dBm", true))
                add(CheckPresencia("Ubicación simulada", "NO", true))
            }
            return ResultadoMarca(
                aprobada = true,
                titulo = "MARCA REGISTRADA",
                subtitulo = "Entrada registrada a las $hora",
                checks = checks,
                mensaje = if (atrasada) "Tu asistencia quedó guardada con atraso."
                else "Tu asistencia quedó guardada correctamente.",
                nota = when {
                    sinConexion -> "Sin conexión — se guardará en el teléfono"
                    atrasada -> "Marca fuera de la tolerancia del turno"
                    else -> null
                },
                textoBoton = "VOLVER AL INICIO"
            )
        }

        fun rechazadaPorFakeGps(dentroFaena: Boolean): ResultadoMarca = ResultadoMarca(
            aprobada = false,
            titulo = "MARCA RECHAZADA",
            subtitulo = "Se detectó ubicación simulada",
            checks = listOf(
                CheckPresencia("GPS dentro de la faena", if (dentroFaena) "Sí" else "NO", dentroFaena),
                CheckPresencia("Ubicación simulada", "SÍ", false)
            ),
            mensaje = "Tu marca no fue registrada porque el dispositivo reportó ubicación simulada (mock location).",
            nota = "Si no estás usando una app de ubicación falsa, reinicia el GPS y vuelve a intentar.",
            textoBoton = "VOLVER E INTENTAR DE NUEVO"
        )

        fun rechazadaFueraDeFaena(distanciaMetros: Int): ResultadoMarca = ResultadoMarca(
            aprobada = false,
            titulo = "MARCA RECHAZADA",
            subtitulo = "Estás fuera de la faena",
            checks = listOf(
                CheckPresencia("GPS dentro de la faena", "NO", false),
                CheckPresencia("Distancia a la faena", "$distanciaMetros m", false)
            ),
            mensaje = "Tu marca no fue registrada porque estás fuera del radio de la faena.",
            nota = null,
            textoBoton = "VOLVER E INTENTAR DE NUEVO"
        )

        fun sinUbicacion(aviso: String): ResultadoMarca = ResultadoMarca(
            aprobada = false,
            titulo = "MARCA RECHAZADA",
            subtitulo = "No se pudo obtener tu ubicación",
            checks = emptyList(),
            mensaje = aviso,
            nota = null,
            textoBoton = "VOLVER E INTENTAR DE NUEVO"
        )

        fun sinEntrada(): ResultadoMarca = ResultadoMarca(
            aprobada = false,
            titulo = "SALIDA NO REGISTRADA",
            subtitulo = "No tienes entrada registrada hoy",
            checks = emptyList(),
            mensaje = "Debes marcar entrada antes de marcar salida.",
            nota = null,
            textoBoton = "VOLVER"
        )

        fun salidaRegistrada(hora: String): ResultadoMarca = ResultadoMarca(
            aprobada = true,
            titulo = "SALIDA REGISTRADA",
            subtitulo = "Salida registrada a las $hora",
            checks = emptyList(),
            mensaje = "Tu salida fue guardada correctamente.",
            nota = null,
            textoBoton = "VOLVER AL INICIO"
        )
    }
}
