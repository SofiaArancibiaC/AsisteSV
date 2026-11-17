# 🎀 Sanrucho

Tienda en línea de productos **Sanrio**, desarrollada como proyecto académico en **Duoc UC** (Analista Programador).

El repositorio tiene dos partes:

- **Frontend (React + TypeScript):** el sitio web completo de la tienda, con catálogo, carrito, sesión de usuarios, boletas y panel de administración.
- **Backend (Java + Spring Cloud):** arquitectura de microservicios en desarrollo, pensada para reemplazar los datos de prueba del frontend.

---

## Tabla de contenidos

1. [Estado del proyecto](#estado-del-proyecto)
2. [Tecnologías](#tecnologías)
3. [Estructura del repositorio](#estructura-del-repositorio)
4. [Frontend](#frontend)
5. [Backend](#backend)
6. [Pruebas](#pruebas)
7. [Próximos pasos](#próximos-pasos)
8. [Autora](#autora)

---

## Estado del proyecto

| Parte | Estado |
| --- | --- |
| Frontend React | Funcional. Guarda los datos en `localStorage`, por lo que **no necesita el backend** para funcionar. |
| Backend de microservicios | En desarrollo. Servidor Eureka y servicios de usuarios, catálogo, pedidos y pagos configurados con PostgreSQL. |
| Integración frontend ↔ backend | Pendiente. |

---

## Tecnologías

**Frontend**

- React 18, TypeScript y Vite
- React Router 6
- Bootstrap 5, React-Bootstrap y Bootstrap Icons
- SweetAlert2 (alertas)
- jsPDF y html2canvas (generación de documentos PDF)
- Karma y Jasmine (pruebas unitarias)

**Backend**

- Java 21 y Maven (proyecto multi-módulo)
- Spring Boot 4.1.0 y Spring Cloud 2025.0.0
- Spring Data JPA y PostgreSQL
- Netflix Eureka (descubrimiento de servicios)
- OpenFeign (comunicación entre microservicios)
- MapStruct y Lombok
- Docker (base de datos local)

---

## Estructura del repositorio

```
Sanrucho/
├─ Backend/
│  ├─ eureka/            # Servidor de descubrimiento (puerto 8761)
│  ├─ ms-usuarios/       # Microservicio de usuarios   (puerto 9001)
│  ├─ ms-catalogo/       # Microservicio de catálogo   (puerto 9002)
│  ├─ ms-pedidos/        # Microservicio de pedidos    (puerto 9003)
│  ├─ ms-pagos/          # Microservicio de pagos      (puerto 9004)
│  ├─ init-multi-db/     # Scripts SQL y .bat para la base de datos con Docker
│  ├─ pom.xml            # POM padre (cl.sanrucho:tienda)
│  └─ *.bat              # Scripts para compilar y levantar servicios (Windows)
└─ Frontend-React/
   ├─ public/            # Imágenes y estilos estáticos
   └─ src/
      ├─ components/     # Layout y componentes reutilizables
      ├─ context/        # AuthContext (sesión) y CartContext (carrito)
      ├─ hooks/          # useProductos, useUsuarios, usePedidos, etc.
      ├─ pages/          # Páginas públicas y panel de administración
      ├─ storage/        # Acceso reactivo a localStorage
      ├─ data/           # Datos de prueba (seed)
      ├─ types/          # Tipos del dominio
      └─ utils/          # Validaciones, formatos, alertas y lógica de pedidos
```

---

## Frontend

### Funcionalidades

- Catálogo de productos con filtros y detalle de producto
- Reseñas de productos
- Carrito de compras con cálculo de totales y envío
- Registro, inicio de sesión y perfil de usuario
- Blog, página "Nosotros" y formulario de contacto
- Panel de administración protegido por roles: gestión de productos, usuarios, boletas e historial de compras
- Validaciones de formularios y alertas con SweetAlert2

### Requisitos

- Node.js 18 o superior (recomendado 20+)

### Cómo ejecutarlo

```bash
cd Frontend-React
npm install
npm run dev
```

La aplicación se abre en <http://localhost:5173>.

### Comandos disponibles

| Comando | Descripción |
| --- | --- |
| `npm run dev` | Servidor de desarrollo con recarga automática |
| `npm run build` | Verifica tipos y genera la carpeta `dist` |
| `npm run preview` | Sirve la versión compilada |
| `npm test` | Ejecuta las pruebas unitarias una vez |
| `npm run test:watch` | Ejecuta las pruebas y vuelve a correrlas al guardar |

### Usuarios de prueba

Los datos se cargan automáticamente en `localStorage` la primera vez que se abre la aplicación.

| Rol | Correo | Contraseña | Acceso a `/admin` |
| --- | --- | --- | --- |
| Dueño | `dueno@gmail.com` | `dueno123` | Sí |
| Admin | `admin@gmail.com` | `admin123` | Sí |
| Cliente | `camila@gmail.com` | `camila123` | No |

Para reiniciar los datos de prueba, borra las claves `sanrucho_*` en DevTools → Application → Local Storage y recarga la página.

### Rutas principales

- **Públicas:** `/`, `/productos`, `/producto-detalle`, `/carrito`, `/login`, `/registro`, `/nosotros`, `/blog`, `/contacto`
- **Administración (rol Dueño o Admin):** `/admin`, `/admin/productos`, `/admin/boletas`, `/admin/usuarios`

---

## Backend

### Arquitectura

Cada microservicio es un módulo Maven independiente, con su propia base de datos PostgreSQL, y se registra en el servidor Eureka.

| Servicio | Puerto | Base de datos |
| --- | --- | --- |
| `eureka` | 8761 | — |
| `ms-usuarios` | 9001 | `usuarios` |
| `ms-catalogo` | 9002 | `catalogo` |
| `ms-pedidos` | 9003 | `pedidos` |
| `ms-pagos` | 9004 | `pagos` |

### Requisitos

- JDK 21
- Maven 3.9 o superior
- Docker

### Cómo ejecutarlo

**1. Levantar PostgreSQL con Docker**

```bash
docker run --name postgres-db -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=123 -p 5433:5432 -d postgres
```

**2. Crear las bases de datos**

```bash
docker exec -it postgres-db psql -U postgres -c "CREATE DATABASE usuarios;"
docker exec -it postgres-db psql -U postgres -c "CREATE DATABASE catalogo;"
docker exec -it postgres-db psql -U postgres -c "CREATE DATABASE pedidos;"
docker exec -it postgres-db psql -U postgres -c "CREATE DATABASE pagos;"
```

Hibernate crea las tablas automáticamente (`ddl-auto: update`).

**3. Iniciar el servidor Eureka**

```bash
cd Backend
mvn -f eureka spring-boot:run
```

**4. Iniciar los microservicios** (cada uno en su propia terminal)

```bash
mvn -f ms-usuarios spring-boot:run
mvn -f ms-catalogo spring-boot:run
mvn -f ms-pedidos spring-boot:run
mvn -f ms-pagos spring-boot:run
```

En Windows también puedes usar `run-all.bat` para abrir una ventana por servicio.

Panel de Eureka: <http://localhost:8761>

> Las credenciales de la base de datos (`postgres` / `123`) son solo para desarrollo local.

---

## Pruebas

**Frontend:** pruebas unitarias con Karma y Jasmine sobre validaciones, formatos y lógica de pedidos.

```bash
cd Frontend-React
npm test
```

**Backend:**

```bash
cd Backend
mvn test
```

---

## Próximos pasos

- Completar la lógica de negocio de los microservicios (endpoints REST, DTOs y validaciones)
- Agregar un API Gateway
- Conectar el frontend con los microservicios en vez de `localStorage`
- Incorporar autenticación con tokens
- Ampliar la cobertura de pruebas

---

## Autora

**Sofía Arancibia Cortez**
Estudiante de Analista Programador, Duoc UC

[LinkedIn](https://www.linkedin.com/in/sofía-arancibia-cortez-963933319) · [GitHub](https://github.com/SofiaArancibiaC)
