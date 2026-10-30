<p align="center">
  <img src="FrontEnd/fronVinculo/public/logo.svg" alt="Vínculo" width="96" />
</p>

<h1 align="center">Vínculo · PRM Personal</h1>

<p align="center">
  Un gestor de relaciones personales para cuidar tu red de contactos con intención.
</p>

> **🚧 Versión 0.1: primera versión.**
> Esta es la primera versión funcional de Vínculo. La base ya está hecha (backend, frontend, base de datos y despliegue con Docker), pero el proyecto sigue en desarrollo activo y **vendrán más actualizaciones con nuevas funcionalidades**. Consulta la [hoja de ruta](#-hoja-de-ruta) para ver lo que viene.

---

## ¿Qué es Vínculo?

Un **CRM** sirve a las empresas para gestionar a sus clientes. Un **PRM** (*Personal Relationship Manager*) hace lo mismo con las personas que te importan: amigos, mentores, clientes o contactos profesionales.

La idea es sencilla: las relaciones se enfrían si no se cuidan. Vínculo te ayuda a recordar:

- con quién hace tiempo que no hablas,
- cuál es el próximo paso con cada persona,
- qué hablasteis la última vez,
- y qué detalles personales no deberías olvidar (cumpleaños, familia, aficiones…).

Es una aplicación **monousuario**: está pensada para que la uses tú, con tus propios datos, en tu ordenador.

## ✨ Funcionalidades de esta versión

### Dashboard
- Saludo personalizado y resumen del día.
- Indicadores clave: contactos totales, prioritarios, próximos pasos y cadencia media.
- Tarjetas con las relaciones que **requieren tu atención**, ordenadas por urgencia.
- Notas recientes, recordatorios pendientes y reparto de tu red por círculos.

### Contactos
- Directorio con búsqueda y filtros por círculo, estado del próximo paso y ciudad.
- Ordenación y paginación.
- Alta de nuevos contactos y creación de **círculos** (grupos con color y cadencia propia).

### Ficha de contacto
- Vista 360° de cada persona: datos de contacto, contexto personal y presentaciones (quién te lo presentó y a quién ha presentado él).
- Registro de **interacciones** (café, llamada, reunión, email…), con borradores.
- **Notas** (generales, oportunidades, preferencias y recursos) y **recordatorios** con recurrencia.
- Historial completo de la relación.

### Pipeline relacional
- Tablero kanban con cinco etapas: *Identificado → Primer contacto → Reunión → Seguimiento → Aliado*.
- Arrastrar y soltar para mover contactos entre etapas.
- Prioridades, siguiente acción con fecha y aviso de acciones vencidas.

### Experiencia
- 🌙 **Modo oscuro**, que sigue al sistema por defecto y recuerda tu elección.
- 📱 **Diseño adaptable** a móvil, tablet y escritorio.
- 💻 **Instalable como app de escritorio** desde Edge o Chrome (PWA), con su propio icono.

## 🛠️ Tecnologías

| Parte | Tecnología |
|---|---|
| **Backend** | Java 25 · Spring Boot 4 · Spring JDBC (`JdbcTemplate`) · Spring Security |
| **Frontend** | Vue 3 · TypeScript · Vite · Tailwind CSS 4 · Material Symbols |
| **Base de datos** | MySQL / MariaDB (vistas, triggers y procedimientos almacenados) |
| **Despliegue** | Docker · Docker Compose · nginx |

## 🏗️ Arquitectura

```
┌──────────────┐     /api      ┌──────────────┐     JDBC     ┌──────────────┐
│   Frontend   │ ───────────▶  │   Backend    │ ──────────▶  │   MariaDB    │
│ Vue + nginx  │               │ Spring Boot  │              │              │
│  :3000       │ ◀───────────  │  :8080       │ ◀──────────  │  :3306       │
└──────────────┘     JSON      └──────────────┘              └──────────────┘
```

El navegador solo habla con el frontend. nginx (o Vite, en desarrollo) reenvía las peticiones `/api` al backend, así que no hacen falta configuraciones de CORS.

## 🚀 Puesta en marcha con Docker (recomendado)

Solo necesitas [Docker Desktop](https://www.docker.com/products/docker-desktop/).

```bash
git clone https://github.com/JL-garrote/vinculo.git
cd vinculo
docker compose up -d --build
```

Abre **http://localhost:3000** y listo.

- La primera vez tarda unos minutos, porque descarga las dependencias de Maven y npm.
- La base de datos se crea sola con el esquema y unos datos iniciales (círculos y un pipeline).
- Los datos se guardan en un volumen de Docker, así que no se pierden al apagar los contenedores.

| Servicio | Puerto en tu equipo |
|---|---|
| Aplicación web | `3000` |
| Base de datos | `3307` (solo accesible desde tu propio equipo) |

**Contraseña de la base de datos (opcional):** por defecto no tiene. Si quieres ponérsela, copia `.env.example` como `.env` y rellena `DB_PASSWORD` **antes** del primer arranque.

**Comandos útiles:**

```bash
docker compose down        # apagar
docker compose up -d       # volver a encender
docker compose down -v     # apagar y BORRAR la base de datos
```

## 🧑‍💻 Desarrollo en local (sin Docker)

**Requisitos:** Java 25, Node.js 22.18+ (o 24.12+) y MySQL/MariaDB (por ejemplo, XAMPP).

1. **Base de datos.** Crea una base de datos `vinculo` y ejecuta los scripts de [`docker/mysql/`](docker/mysql/) en orden.
2. **Backend.** Revisa la conexión en [`vinculo/src/main/resources/application.properties`](vinculo/src/main/resources/application.properties) y arranca:
   ```bash
   cd vinculo
   ./mvnw spring-boot:run
   ```
3. **Frontend.**
   ```bash
   cd FrontEnd/fronVinculo
   npm install
   npm run dev
   ```
   Abre la URL que muestre Vite (normalmente http://localhost:5173). Si el backend no está en `localhost:8080`, indícalo con la variable `VITE_API_TARGET`.

## 📡 API REST

Todas las rutas empiezan por `/api/controller`.

| Recurso | Método | Ruta | Descripción |
|---|---|---|---|
| Contactos | `GET` | `/contacto/todos` | Lista de contactos |
| | `GET` | `/contacto/porID/{id}` | Un contacto |
| | `GET` | `/contacto/contador` | Número de contactos |
| | `POST` | `/contacto` | Crear contacto |
| Círculos | `GET` | `/circulo/todos` | Lista de círculos |
| | `GET` | `/circulo/porID/{id}` | Un círculo |
| | `POST` | `/circulo` | Crear círculo |
| Notas | `GET` | `/notas/todas` | Lista de notas |
| | `GET` | `/notas/id/{id}` | Una nota |
| | `GET` | `/notas/contadorNotas` | Número de notas |
| | `POST` | `/notas` | Crear nota |
| Interacciones | `GET` | `/interaccion/todas` | Todas las interacciones |
| | `GET` | `/interaccion/contacto/{id}` | Historial de un contacto |
| | `POST` | `/interaccion` | Registrar interacción |
| Recordatorios | `GET` | `/recordatorio/pendientes` | Recordatorios pendientes |
| | `GET` | `/recordatorio/contacto/{id}` | Recordatorios de un contacto |
| | `POST` | `/recordatorio` | Crear recordatorio |
| Pipeline | `GET` | `/pipeline/todos` | Lista de pipelines |
| | `GET` | `/pipeline/porID/{id}` | Un pipeline |
| | `GET` | `/pipeline/{id}/contactos` | Contactos de un pipeline |
| | `POST` | `/pipeline/{id}/contactos` | Añadir contacto al pipeline |
| | `PUT` | `/pipeline/contactos/{entradaID}` | Mover o editar una entrada |
| | `DELETE` | `/pipeline/contactos/{entradaID}` | Quitar del pipeline |
| Perfil | `GET` | `/perfil` | Perfil del usuario |

Los errores devuelven un JSON con el formato `{ status, error, mensaje }`:
- `400` si los datos no son válidos,
- `404` si el recurso no existe,
- `409` si el registro está duplicado.

## 📁 Estructura del proyecto

```
vinculo/
├── docker-compose.yml        # Levanta base de datos, backend y frontend
├── docker/mysql/             # Esquema y datos iniciales de la base de datos
├── vinculo/                  # Backend (Spring Boot)
│   └── src/main/java/com/personal/vinculo/
│       ├── Controller/       # Endpoints REST
│       ├── Service/          # Acceso a datos con JdbcTemplate
│       ├── Models/           # Entidades
│       └── Config/           # Seguridad y manejo de errores
└── FrontEnd/fronVinculo/     # Frontend (Vue 3)
    └── src/
        ├── api/              # Cliente HTTP, store, tipos, formatos y tema
        ├── components/       # Componentes reutilizables
        └── views/            # Dashboard, Contactos, Ficha y Pipeline
```

## 🗺️ Hoja de ruta

Esta es la **versión 0.1**. Lo siguiente que está previsto:

- [ ] Editar y borrar contactos, notas, círculos, interacciones y recordatorios desde la app.
- [ ] Buscador global funcional (contactos, notas y círculos).
- [ ] Pantalla de ajustes y edición del perfil.
- [ ] Inicio de sesión para proteger los datos.
- [ ] Sincronización con el calendario.
- [ ] Importar contactos desde Google, LinkedIn y CSV.
- [ ] Notificaciones de recordatorios y cumpleaños.
- [ ] Tests automáticos en backend y frontend.

¿Tienes alguna idea? Abre un *issue*.

## ⚠️ Limitaciones conocidas de la v0.1

- Por ahora solo se pueden **crear** registros. Editar y borrar solo está disponible en el pipeline.
- La seguridad está abierta (sin login): está pensada para usarse **solo en local**. No la expongas a internet.
- Algunos botones del diseño (sincronizar calendario, importar, ajustes) todavía no tienen funcionalidad.

## 👤 Autor

**Juan Luis Garrote** · [@JL-garrote](https://github.com/JL-garrote)

Proyecto personal en desarrollo. ¡Gracias por pasarte!
