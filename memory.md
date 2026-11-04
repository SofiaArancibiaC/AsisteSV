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