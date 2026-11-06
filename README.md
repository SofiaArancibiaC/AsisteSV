# AsisteSV — Asistencia en Faena

Aplicación móvil Android para el **registro de asistencia de trabajadores en faena**, con validación de ubicación por GPS, detección de ubicación simulada (fake GPS) y paneles diferenciados por rol (Operario, Capataz y Administrador).

> Proyecto académico — asignatura **DSY1105** (Duoc UC).
> Paquete: `com.js.smartvolt.asiste`

---

## Tabla de contenidos

- [Descripción](#descripción)
- [Características](#características)
- [Roles y funcionalidades](#roles-y-funcionalidades)
- [Cómo funciona la marca de asistencia](#cómo-funciona-la-marca-de-asistencia)
- [Tecnologías](#tecnologías)
- [Arquitectura](#arquitectura)
- [Estructura del proyecto](#estructura-del-proyecto)
- [Modelo de datos](#modelo-de-datos)
- [Requisitos](#requisitos)
- [Instalación y ejecución](#instalación-y-ejecución)
- [Usuarios de demostración](#usuarios-de-demostración)
- [Permisos de Android](#permisos-de-android)
- [Notas y limitaciones](#notas-y-limitaciones)
- [Autoría](#autoría)

---

## Descripción

**AsisteSV** permite que los operarios registren su entrada y salida directamente desde el teléfono, y que solo se acepte la marca si se encuentran **físicamente dentro del radio de la faena**. Cuando se detecta un intento sospechoso (ubicación simulada o marca fuera de la faena), la marca queda **rechazada** y se genera una **alerta** para que capataz o administrador la revisen.

La app funciona **de forma local** (base de datos Room en el dispositivo), por lo que no requiere un backend para ejecutarse y demostrarse.

## Características

- Inicio de sesión con correo y contraseña (hash **SHA-256**) y flujo de "Olvidé mi contraseña".
- Marca de **entrada** y **salida** validada por GPS (radio configurable por faena).
- **Detección de ubicación simulada** (mock location).
- Estados de asistencia: `CUMPLIDA`, `ATRASADA`, `FALTA`, `EN_REVISION`, `RECHAZADA`.
- Tolerancia de atraso de **10 minutos** sobre el inicio del turno.
- **Calendario mensual** de asistencias y **historial** con resumen (cumplidas, atrasos, faltas).
- **Mapa** de la faena con OpenStreetMap (osmdroid).
- **Alertas** automáticas ante marcas rechazadas (ubicación simulada / fuera de faena).
- Gestión de **usuarios, turnos y horas extra** desde el panel de administración.
- **Reportes** de asistencia por faena y por día.
- Navegación con menú lateral, barras inferiores por rol y tema Material 3.
- Carga automática de **datos demo** (faenas y usuarios) en el primer arranque.

## Roles y funcionalidades

| Rol | Funcionalidades principales |
| --- | --- |
| **Operario** | Marcar entrada/salida, ver estado de presencia (GPS), calendario, historial, perfil y configuración. |
| **Capataz** | Panel de su faena, reportes, revisión de alertas, listado de operarios, turnos y horas extra por operario. |
| **Administrador** | Panel general con filtros por día y faena, reportes, alertas con detalle, alta/edición de usuarios, turnos y aprobación de horas extra. |

## Cómo funciona la marca de asistencia

1. El operario presiona **MARCAR ENTRADA**.
2. `UbicacionProvider` obtiene la ubicación con *Fused Location Provider* (alta precisión, timeout de 12 s; si no llega una lectura nueva usa la última conocida).
3. Se calcula la distancia entre el teléfono y el centro de la faena y se compara con el radio permitido (por defecto **100 m**).
4. Se resuelve el resultado:

| Situación | Resultado | Efecto |
| --- | --- | --- |
| Sin permiso / GPS apagado / sin lectura | Marca no realizada | Se informa al usuario cómo corregirlo |
| Ubicación simulada detectada | `RECHAZADA` | Se crea una **alerta** (`REVISION`) |
| Fuera del radio de la faena | `RECHAZADA` | Se crea una **alerta** (`REVISION`) |
| Dentro de la faena y a tiempo | `CUMPLIDA` | Entrada registrada |
| Dentro de la faena pasada la tolerancia | `ATRASADA` | Entrada registrada con atraso |

5. La **salida** exige una entrada previa válida y repite las mismas validaciones de ubicación.
6. Si ya existe una entrada válida para el día, no se duplica la marca.

> La interfaz contempla también una validación por baliza BLE en la pantalla de inicio, pero la detección real de balizas no está implementada en esta versión.

## Tecnologías

| Área | Tecnología |
| --- | --- |
| Lenguaje | Kotlin |
| UI | Jetpack Compose + Material 3 |
| Persistencia | Room `2.7.0` (SQLite local) con KSP |
| Asincronía | Kotlin Coroutines + Flow / StateFlow |
| Ubicación | Google Play Services — Fused Location Provider |
| Mapas | osmdroid `6.1.20` (OpenStreetMap) |
| Calendario | [kizitonwose/Calendar](https://github.com/kizitonwose/Calendar) (Compose) |
| Biometría | `androidx.biometric` `1.1.0` (dependencia y permiso declarados) |
| Build | Gradle `9.6.0`, JDK 25 |
| SDK Android | `minSdk 26` · `targetSdk 37` |

## Arquitectura

El proyecto sigue un patrón **MVVM** con una capa de repositorios:

```
UI (Compose Views)
      │  observa StateFlow
      ▼
ViewModel  (AsistenciaViewModel, LoginViewModel, UsuarioViewModel, ...)
      │
      ▼
Repository (AuthRepository, AsistenciaRepository, ...)
      │
      ▼
DAO (Room)  ──►  AppDatabase  (asiste.db)
```

- `DatabaseProvider` entrega una instancia única (singleton) de la base de datos.
- `SessionManager` mantiene al usuario autenticado en memoria.
- `MainActivity` decide el flujo de navegación según el rol del usuario tras el login.

## Estructura del proyecto

```
app/src/main/java/com/js/smartvolt/asiste/
├── AsisteApp.kt                 # Application: init de osmdroid + datos demo
├── MainActivity.kt              # Navegación login → olvidé clave → vista por rol
├── data/
│   ├── location/                # UbicacionProvider (GPS + detección de mock)
│   └── repository/              # Auth, Asistencia, Alerta, Faena, HoraExtra, Turno, Usuario
├── model/
│   ├── Asistencia.kt
│   ├── RecuperarUsuario.kt
│   ├── admin/                   # Alerta, Faena, HoraExtra, Turno, Usuario (+ relaciones)
│   └── data/local/              # AppDatabase, DAOs, Converters, Hash (SHA-256)
├── session/                     # SessionManager
├── ui/
│   ├── components/              # Barras inferiores, menú lateral, botones, mapa OSM, etc.
│   ├── theme/                   # Color, Theme, Type
│   └── view/
│       ├── (Login, Inicio, Calendario, Historial, Perfil, Configuración, ...)
│       ├── admin/               # Panel, usuarios, turnos, horas extra, alertas, reportes
│       └── capataz/             # Panel y vistas del capataz
└── viewmodel/                   # Asistencia, Login, OlvideClave, Faena, Perfil, Usuario
```

## Modelo de datos

Base de datos Room `asiste.db` (versión 6), con estas entidades:

| Entidad | Descripción | Relaciones |
| --- | --- | --- |
| `Faena` | Lugar de trabajo: nombre, presentes/total, latitud, longitud y radio en metros. | — |
| `Usuario` | Correo, nombre, hash de contraseña, código, rol (`OPERARIO`, `CAPATAZ`, `ADMIN`) y estado activo. | `faenaId` → `Faena` |
| `Asistencia` | Fecha, estado, horario de turno, hora de entrada/salida y coordenadas. | — |
| `Turno` | Día, horario y si está activo. | `usuarioId` → `Usuario` |
| `HoraExtra` | Fecha, horas, motivo, nota y estado (`PENDIENTE`, `APROBADO`, `RECHAZADO`). | `usuarioId` → `Usuario` |
| `Alerta` | Código de operario, tipo de marca (`ENTRADA`/`SALIDA`), hora, motivo y estado. | `faenaId` → `Faena` |

## Requisitos

- **Android Studio** reciente (con soporte para AGP/Gradle 9.x).
- **JDK 25** (se puede usar el JBR incluido en Android Studio).
- **Android SDK** con API 37 instalada. 
- Dispositivo o emulador con **Android 8.0 (API 26)** o superior.
- Para probar la marca de asistencia: un dispositivo con **GPS** (en emulador se puede fijar la ubicación manualmente).

## Instalación y ejecución

1. Clona el repositorio:

   ```bash
   git clone <URL-DEL-REPOSITORIO>
   cd AsisteSV
   ```

2. Abre el proyecto en **Android Studio** y espera a que termine la sincronización de Gradle.

3. Verifica que el archivo `local.properties` apunte a tu Android SDK (Android Studio lo genera automáticamente):

   ```properties
   sdk.dir=RUTA/A/TU/Android/Sdk
   ```

   > `local.properties` es específico de cada equipo y **no debe subirse** al repositorio.

4. Selecciona un dispositivo o emulador y ejecuta la configuración **app** (▶).


5. Al abrir la app por primera vez, concede el permiso de **ubicación precisa** y activa el GPS.

### Probar la marca de asistencia

La faena demo está centrada en **Santiago** (`-33.4489, -70.6693`) con un radio de **100 m**. Para probar:

- **Marca válida:** en un emulador, fija la ubicación en esas coordenadas (Extended controls → Location).
- **Marca rechazada por distancia:** fija una ubicación lejana a la faena.
- **Marca rechazada por fake GPS:** usa una app de ubicación simulada; la marca se rechaza y se genera una alerta visible para capataz/administrador.

## Usuarios de demostración

Se crean automáticamente en el primer arranque (datos solo para pruebas académicas):

| Rol | Correo | Contraseña | Código |
| --- | --- | --- | --- |
| Operario | `tecnico@serviciosvolt.cl` | `123456` | `OP-014` |
| Capataz | `capataz@serviciosvolt.cl` | `capataz123` | `CAP-001` |
| Administrador | `admin@serviciosvolt.cl` | `admin123` | `ADMIN-001` |

También se precargan tres faenas de ejemplo: **Ruta 5 Norte**, **Puente Maipo** y **Planta Industrial**.

## Permisos de Android

| Permiso | Uso |
| --- | --- |
| `ACCESS_FINE_LOCATION` | Validar que el operario esté dentro de la faena (se exige ubicación precisa). |
| `ACCESS_COARSE_LOCATION` | Complemento de ubicación. |
| `INTERNET` / `ACCESS_NETWORK_STATE` | Descarga de teselas del mapa (OpenStreetMap) y estado de red. |
| `USE_BIOMETRIC` | Declarado para autenticación biométrica. |

## Notas y limitaciones

- Es una **versión académica**: todos los datos viven en el dispositivo (Room); no hay backend.
- Las contraseñas se almacenan como hash **SHA-256 sin sal**. Para un entorno real se recomienda usar un algoritmo con sal y costo (bcrypt, Argon2) y autenticación en servidor.
- La recuperación de contraseña es una **simulación**: valida el formato del correo y responde con un mensaje genérico (evita la enumeración de usuarios), pero no envía correos.
- La base de datos usa `fallbackToDestructiveMigration`: al cambiar el esquema, los datos locales se reinician.
- La sesión (`SessionManager`) se guarda solo en memoria; al cerrar la app hay que iniciar sesión nuevamente.
- La validación por baliza BLE está contemplada en la interfaz pero aún no implementada.

## Autoría

Desarrollado por **Jhaimar Altuve** y **Sofía Arancibia** como proyecto de la asignatura **Desarrollo de Aplicaciones Móviles**, Duoc UC.

---

*Servicios Volt — AsisteSV*
