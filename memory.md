# Memoria del proyecto AsisteSV

## Descripción
App Android (Kotlin + Jetpack Compose) de asistencia/turnos. Package `com.js.smartvolt.asiste`. Gestiona asistencias, usuarios, alertas, turnos, horas extra, faenas.

## Estructura
- Android Gradle project (Kotlin). `app` module.
- Room para persistencia (KSP).
- Compose para UI.
- ViewModels con factories.

## Base de datos
- `AppDatabase` (RoomDatabase) entities: Asistencia, RecuperarUsuario, Usuario, Alerta, Turno, HoraExtra, Faena. v3. `asistenciaDao()`, `usuarioDao()`.
- `AsisteDatabase` legacy solo Asistencia (v2).
- `DatabaseProvider` singleton usa `AppDatabase` ("asiste.db"), expone `getAsistenciaDao`, `getUsuarioDao`, y `getDao` (compat -> asistencia).

## DAOs
- `AsistenciaDAO`: getByRange, upsert, delete, getAll.
- `UsuarioDAO`: login, getAll, getById, existeCorreo, getByEstado, getByRol, insertar(RecuperarUsuario), delete(Usuario), getUsuariosConFaena.

## Modelos clave
- `Asistencia`, `EstadoAsistencia`, `RecuperarUsuario`, admin: Alerta, Faena, HoraExtra, Turno, Usuario, AlertaConFaena, UsuarioConFaena.

## MainActivity
- Siembra asistencias de ejemplo si vacío.
- Crea usuario de prueba `tecnico@serviciosvolt.cl` / 123456.
- Navegación: login -> inicio/olvide.

## Errores corregidos
- `DatabaseProvider.getDao()` devolvía `AsistenciaDAO` pero se usaba como `UsuarioDAO` en MainActivity (insertar, login). Solución: DatabaseProvider usa AppDatabase y expone ambos DAOs; MainActivity usa getAsistenciaDao/getUsuarioDao.

## Notas
- JDK 25 no soportado por Kotlin; fallback JVM_24.
- Gradle offline build funciona.
- Al actualizar código, actualizar este archivo.

## Acceso admin
- Credenciales demo: admin@serviciosvolt.cl / admin123 (siembra automática).
- Login detecta correo con "admin" y abre AdminMainView en lugar de MainView.

## Admin UI (mockup)
- `AdminMainView.kt` ahora contiene navegación entre 9 pantallas admin (Panel, Reportes, Alertas, Usuarios, Detalle alerta, Perfil usuario, Nuevo usuario, Turnos, Horas extra, Detalle operario) con barra inferior de 4 pestañas y estado activo.
- Panel implementado con selector de día, faena, KPIs tocables, "Requiere atención" y asistencia por faena.
- Reportes calcula métricas por operario desde Room: horas trabajadas (CUMPLIDA*8), horas extra aprobadas (HoraExtraDao), atrasos y faltas (Asistencia por usuarioId). Se agregó usuarioId a Asistencia, se expusieron HoraExtraDao/TurnoDAO en AppDatabase y DatabaseProvider, y AsistenciaDAO.getByUsuario. Versión DB bumped a 4 con fallbackToDestructiveMigration.
- AdminTurnosView implementado: selector de operario, DatePicker para día y TimePicker para horario (botones para abrir los diálogos), guarda Turno en Room vía TurnoDAO, y listado/eliminación de turnos por operario. Se agregó UsuarioDAO.getByCodigo.
- CalendarioView ahora recibe codigoOperario y muestra los turnos asignados al usuario (consulta TurnoDAO por usuario; si no hay usuario, muestra todos). Los días con turno se marcan con un punto en el calendario. MainView pasa codigoOperario a CalendarioView. RecuperarUsuario ahora incluye codigoUsuario; AdminNuevoUsuarioView guarda el código generado en RecuperarUsuario; LoginViewModel expone el código en LoginEstado.Exito; MainActivity lo pasa a MainView. AsisteApp siembra Usuario OP-014 para tecnico demo. TurnoDAO.getAll añadido. Flujo login -> usuario -> turnos funcionando. DB v5.
- Componentes reutilizables movidos a `ui/components/AdminComponents.kt` (AdminHeader, KpiCard, FaenaRow, AdminPrimaryButton).
- UsuarioViewModel creado para sincronizar lista de usuarios desde Room; AdminUsuariosView ahora muestra usuarios reales.
- AdminNuevoUsuarioView ahora inserta usuarios en Room (correo, nombre) y carga faenas desde Room; la contraseña se fija automáticamente a "123456" (no se ingresa por el admin). Validación de correo con android.util.Patterns.EMAIL_ADDRESS en ambos login (LoginViewModel) y nuevo usuario (mensaje de error en UI).
- UsuarioDAO ahora tiene insertarUsuario; Nuevo usuario permite seleccionar faena (dropdown) y crea Usuario vinculado.
- Modelo Usuario ya incluye faenaId (FK a Faena); falta método insert en UsuarioDAO para asignar trabajadores a faenas.
- Panel admin ahora se conecta a Room: muestra conteo real de asistencias y usuarios.
- FaenaViewModel creado y DatabaseProvider/AppDatabase extendidos con FaenaDao; panel muestra conteo de faenas.
- Panel admin ahora lista faenas dinámicamente desde Room; AsisteApp siembra 3 faenas de prueba si no existen.
- Vista de admin-alertas implementada: AdminAlertasView (filtros por faena/fecha/estado y lista de alertas) y AdminDetalleAlertaView (detalle y acciones validar/rechazar). Se expuso AlertaDao en AppDatabase y DatabaseProvider (getAlertaDao).
- Reporte de horas ahora permite seleccionar faena (dropdown) y período mensual (dropdown últimos 6 meses); filtra usuarios por faena y asistencias/horas extra por mes.
- Marca en MainView ahora registra Asistencia en Room y genera Alerta (REVISION) si la marca es fuera del turno. Estado: CUMPLIDA dentro de tolerancia, ATRASADA si >10 min, RECHAZADA si fuera del rango.
- MainView ahora carga el turno activo del día desde Room y lo muestra en InicioView (inicio/fin), en lugar de horario fijo.
- AdminTurnosView ahora ofrece solo dos turnos: AM 08:00-14:00 y PM 15:00-21:00 (dropdown), guardando horario de inicio.
- Eliminación de turnos en admin ahora usa coroutine (scope.launch) para que el Flow de turnos se actualice en calendario.
- Datos de prueba movidos de MainActivity a AsisteApp (Application), que siembra usuarios por defecto en Room. MainActivity ya no inyecta datos a mano.
- Componente de botón con degradado creado en `ui/components/Buttons.kt` (GradientButton) listo para usar en vistas de usuario.