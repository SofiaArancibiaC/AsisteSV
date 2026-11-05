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
- AdminUsuariosView ahora excluye al usuario admin de la lista (solo muestra operarios/capataces).
- AdminUsuariosView abre perfil por código de operario (u.codigo) en lugar de correo, así AdminTurnosView selecciona el operario correcto en lugar del primero.
- AdminNuevoUsuarioView ahora inserta usuarios en Room (correo, nombre) y carga faenas desde Room; la contraseña se fija automáticamente a "123456" (no se ingresa por el admin). Validación de correo con android.util.Patterns.EMAIL_ADDRESS en ambos login (LoginViewModel) y nuevo usuario (mensaje de error en UI).
- AdminNuevoUsuarioView ahora permite seleccionar el rol (OPERARIO/CAPATAZ/ADMIN) mediante dropdown; se guarda en Usuario.rol.
- UsuarioDAO ahora tiene insertarUsuario; Nuevo usuario permite seleccionar faena (dropdown) y crea Usuario vinculado.
- Modelo Usuario ya incluye faenaId (FK a Faena); falta método insert en UsuarioDAO para asignar trabajadores a faenas.
- Panel admin ahora se conecta a Room: muestra conteo real de asistencias y usuarios.
- FaenaViewModel creado y DatabaseProvider/AppDatabase extendidos con FaenaDao; panel muestra conteo de faenas.
- Panel admin ahora lista faenas dinámicamente desde Room; AsisteApp siembra 3 faenas de prueba si no existen.
- Panel admin incluye botón "SIMULAR MARCAS" que inserta Asistencias CUMPLIDA para todos los usuarios del día seleccionado, permitiendo probar métricas en vivo.
- Panel admin incluye botón "LIMPIAR MARCAS SIMULADAS" que elimina las asistencias simuladas (fecha del día + registro 08:05).
- Vista de admin-alertas implementada: AdminAlertasView (filtros por faena/fecha/estado y lista de alertas) y AdminDetalleAlertaView (detalle y acciones validar/rechazar). Se expuso AlertaDao en AppDatabase y DatabaseProvider (getAlertaDao).
- AdminDetalleAlertaView: botones VALIDAR/RECHAZAR ahora actualizan el estado de la alerta en Room (upsert) con scope coroutine; la UI refleja el nuevo estado.
- Reporte de horas ahora permite seleccionar faena (dropdown) y período mensual (dropdown últimos 6 meses); filtra usuarios por faena y asistencias/horas extra por mes.
- Marca en MainView ahora registra Asistencia en Room y genera Alerta (REVISION) si la marca es fuera del turno. Estado: CUMPLIDA dentro de tolerancia, ATRASADA si >10 min, RECHAZADA si fuera del rango.
- MainView ahora carga el turno activo del día desde Room y lo muestra en InicioView (inicio/fin), en lugar de horario fijo.
- Corregido error de compilación en Asistencia (coma faltante) y llamada inválida a InicioRoute (onMarcarEntrada no existe). Build debug exitoso.
- Corregido crash al login de usuario normal: InicioRoute ya no lanza permisos automáticamente en LaunchedEffect (evita requestCode inválido); se muestra botón para pedir permiso.
- Corregido segundo crash al tocar "Marcar": se reemplazó launcher.launch por ActivityCompat.requestPermissions con requestCode válido (1001).
- AsistenciaViewModel ahora crea Alerta (REVISION) cuando la marca es rechazada por ubicación simulada o fuera de faena; se inyecta AlertaDao y codigoOperario.
- Navbars movidas a componentes reutilizables en `ui/components/BottomBars.kt` (AdminBottomBar, UserBottomBar); usadas en AdminMainView y MainView.
- AdminTurnosView ahora ofrece solo dos turnos: AM 08:00-14:00 y PM 15:00-21:00 (dropdown), guardando horario de inicio.
- AdminTurnosView: el operario ya no es seleccionable; se muestra como campo de solo lectura con el usuario seleccionado desde la vista de usuarios.
- Corregido off-by-one en DatePicker de turnos/horas extra: se convierte millis a LocalDate usando ZoneId UTC (antes systemDefault restaba un día en zonas horarias negativas).
- Eliminación de turnos en admin ahora usa coroutine (scope.launch) para que el Flow de turnos se actualice en calendario.
- Datos de prueba movidos de MainActivity a AsisteApp (Application), que siembra usuarios por defecto en Room. MainActivity ya no inyecta datos a mano.
- Componente de botón con degradado creado en `ui/components/Buttons.kt` (GradientButton) listo para usar en vistas de usuario.
- Menú hamburguesa (3 barras) movido a componente reutilizable `ui/components/DrawerMenu.kt` (HamburgerScaffold) usado en MainView; vistas ConfiguracionView y PerfilView scrolleables. BackHandler evita salir de la app y vuelve a MainView desde vistas extra. Padding aplicado a CalendarioView/HistorialView para evitar solapamiento con TopAppBar. Opción "Cerrar sesión" agregada al menú. PerfilView muestra datos del usuario (nombre, correo, código, faena, rol, activo) sin contraseña. ConfiguracionView incluye permisos y toggle de modo oscuro. CommonButton disponible en `ui/components/Buttons.kt` y usado en ConfiguracionView.
- AdminHorasExtraView implementado: selector de operario, DatePicker para fecha, campo de horas, motivo, estado (PENDIENTE/APROBADO/RECHAZADO), nota; guarda HoraExtra en Room vía HoraExtraDao y lista/elimina horas extra por operario. Aprobación/rechazo en línea para items PENDIENTE (botones Aprobar/Rechazar que actualizan estado a APROBADO/RECHAZADO).
- CalendarioView ahora muestra horas extra del usuario como punto en el día (color secondary) y usa borde punteado para marcar días con turno próximo (tieneTurno && fecha>=hoy). Parámetro horasExtra: Set<LocalDate> añadido a CalendarioContenido y DiaCalendario.
- Leyenda de CalendarioView ahora incluye "Rechazada" (color estados.rechazada).
- LoginView y OlvideContrasenaView ahora adaptan su paleta al tema del sistema (isSystemInDarkTheme): colores claros en modo claro y oscuros en modo oscuro.
- Corregido amarillo neón en tema claro: LoginView/OlvideContrasenaView usan ámbar oscuro (0xFFB38600) en claro en lugar de 0xFFE6FF0A; Theme.kt ColoresClaros usa AmbarClaro para primaryContainer y blanco para onPrimary.