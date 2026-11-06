<p align="center">
  <img src="FrontEnd/fronVinculo/public/logo.svg" alt="Vínculo" width="96" />
</p>

<h1 align="center">Vínculo · PRM Personal</h1>

<p align="center">
  Un gestor de relaciones personales para cuidar tu red de contactos con intención.
</p>

> **🚀 Versión 1.5.0: tu historial, completo.**
> Vínculo funciona de punta a punta: backend, frontend, base de datos y despliegue con Docker. Esta versión completa la gestión del historial: ya se pueden editar y borrar interacciones y borrar recordatorios desde la ficha del contacto. Todos los diálogos de la app son ahora accesibles con teclado y lector de pantalla. El proyecto sigue en desarrollo activo y **vendrán más actualizaciones con nuevas funcionalidades**. Consulta las [novedades](#-novedades) y la [hoja de ruta](#-hoja-de-ruta).

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
- Botón **Contactar** en cada tarjeta de atención: abre WhatsApp, la llamada o el email según el canal preferido del contacto.

### Contactos
- Directorio con búsqueda y filtros por círculo, estado del próximo paso y ciudad.
- Ordenación y paginación compacta, pensada para redes de miles de contactos.
- Alta de nuevos contactos y creación de **círculos** (grupos con color y cadencia propia).
- Edición y borrado de círculos: al borrar uno, sus contactos no se pierden, se quedan sin círculo.
- **Importación de contactos** desde LinkedIn, Google Contacts o un CSV/Excel propio (ver [cómo importar](#-importar-contactos)).

### Ficha de contacto
- Vista 360° de cada persona: datos de contacto, contexto personal y presentaciones (quién te lo presentó y a quién ha presentado él).
- Registro de **interacciones** (café, llamada, reunión, email…), con borradores.
- **Notas** (generales, oportunidades, preferencias y recursos) y **recordatorios** con recurrencia.
- Historial completo de la relación.
- **Edición** del contacto, de sus notas, de sus recordatorios pendientes y de cada **interacción** del historial (tipo, fecha, lugar, resumen, temas y si es borrador).
- **Borrado** de notas, interacciones, recordatorios y del propio contacto (con todo su historial). Siempre se pide confirmación antes de borrar.

### Pipeline relacional
- Tablero kanban con cinco etapas: *Identificado → Primer contacto → Reunión → Seguimiento → Aliado*.
- Arrastrar y soltar para mover contactos entre etapas.
- Prioridades, siguiente acción con fecha y aviso de acciones vencidas.

### Experiencia
- 🔎 **Buscador global** en la barra superior: escribe un nombre, empresa o email y salta directamente a la ficha. Atajo **Ctrl+K** (⌘K en Mac).
- 🔗 **Cada vista tiene su dirección** (`#/contactos`, `#/contactos/12`, `#/pipeline`): puedes recargar, usar el botón *Atrás* del navegador o abrir una ficha en otra pestaña.
- ♿ **Accesible**: se puede usar entera con teclado, con foco visible y un enlace para saltar al contenido; los lectores de pantalla anuncian cada control por su nombre; el texto cumple el contraste WCAG AA en modo claro y oscuro.
- 🌙 **Modo oscuro**, que sigue al sistema por defecto y recuerda tu elección.
- 📱 **Diseño adaptable** a móvil, tablet y escritorio.
- 💻 **Instalable como app de escritorio** desde Edge o Chrome (PWA), con su propio icono.

## 📥 Importar contactos

En **Contactos → Importar contactos** puedes traer tu red de golpe a partir de un archivo `.csv`:

| Fuente | Cómo conseguir el archivo |
|---|---|
| **LinkedIn** | *Yo → Configuración y privacidad → Privacidad de datos → Obtener una copia de tus datos*, marca solo **Contactos**. Del ZIP que te llega por email, sube `Connections.csv`. |
| **Google** | En [contacts.google.com](https://contacts.google.com) pulsa **Exportar → Google CSV**. |
| **CSV / Excel** | Una hoja con cabecera. Solo es obligatoria la columna `Nombre`. También se reconocen `Apellidos`, `Email`, `Teléfono`, `Empresa`, `Cargo`, `Ciudad`, `País`, `LinkedIn` y `Cumpleaños` (AAAA-MM-DD). Guárdala como *CSV UTF-8*. |

- El archivo se lee **en tu navegador**: no se guarda nada hasta que confirmas.
- Antes de importar ves la lista completa, puedes desmarcar contactos y asignarlos todos a un círculo.
- Los contactos que parecen estar ya en Vínculo (mismo email, perfil de LinkedIn o nombre) aparecen como **"Ya existe"** y sin marcar.
- Repetir una importación es seguro: cada contacto se guarda con su origen y un identificador (perfil de LinkedIn o email), y los repetidos se saltan.

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
| | `PUT` | `/contacto/actualizar/{id}` | Editar contacto |
| | `GET` | `/contacto/borrar/{id}` | Borrar contacto (y en cascada sus notas, interacciones, recordatorios y entradas del pipeline) |
| Círculos | `GET` | `/circulo/todos` | Lista de círculos |
| | `GET` | `/circulo/porID/{id}` | Un círculo |
| | `POST` | `/circulo` | Crear círculo |
| | `PUT` | `/circulo/actualizar/{id}` | Editar círculo |
| | `GET` | `/circulo/borrar/{id}` | Borrar círculo (sus contactos se quedan sin círculo) |
| Notas | `GET` | `/notas/todas` | Lista de notas |
| | `GET` | `/notas/id/{id}` | Una nota |
| | `GET` | `/notas/contadorNotas` | Número de notas |
| | `POST` | `/notas` | Crear nota |
| | `PUT` | `/notas/actualizar/{id}` | Editar nota |
| | `GET` | `/notas/borrar/{id}` | Borrar nota |
| Interacciones | `GET` | `/interaccion/todas` | Todas las interacciones |
| | `GET` | `/interaccion/contacto/{id}` | Historial de un contacto |
| | `POST` | `/interaccion` | Registrar interacción |
| | `PUT` | `/interaccion/actualizar/{id}` | Editar interacción |
| | `GET` | `/interaccion/borrar/{id}` | Borrar interacción |
| Recordatorios | `GET` | `/recordatorio/pendientes` | Recordatorios pendientes |
| | `GET` | `/recordatorio/contacto/{id}` | Recordatorios de un contacto |
| | `POST` | `/recordatorio` | Crear recordatorio |
| | `PUT` | `/recordatorio/actualizar/{id}` | Editar recordatorio |
| | `GET` | `/recordatorio/borrar/{id}` | Borrar recordatorio |
| Pipeline | `GET` | `/pipeline/todos` | Lista de pipelines |
| | `GET` | `/pipeline/porID/{id}` | Un pipeline |
| | `GET` | `/pipeline/{id}/contactos` | Contactos de un pipeline |
| | `POST` | `/pipeline/{id}/contactos` | Añadir contacto al pipeline |
| | `PUT` | `/pipeline/contactos/{entradaID}` | Mover o editar una entrada |
| | `DELETE` | `/pipeline/contactos/{entradaID}` | Quitar del pipeline |
| Perfil | `GET` | `/perfil` | Perfil del usuario |

Las rutas `PUT …/actualizar/{id}` reciben el registro **completo**, con el mismo formato JSON que devuelve su `GET`, y no devuelven cuerpo. Los campos que no se envíen se guardan vacíos.

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

## 🆕 Novedades

### v1.5.0
- **Editar interacciones** desde el historial de la ficha: tipo, fecha, título, lugar, resumen, temas y si es un borrador. Si cambias la fecha, el historial se reordena solo.
- **Borrar interacciones y recordatorios**, con confirmación. Al borrar un recordatorio desaparece también de los pendientes del dashboard.
- Nuevos endpoints de borrado en la API: `GET /interaccion/borrar/{id}` y `GET /recordatorio/borrar/{id}`.
- **Diálogos accesibles** en toda la app: el lector de pantalla los anuncia con su título, el foco entra al abrirlos y no se escapa con Tab, *Esc* los cierra y al cerrar el foco vuelve al botón que los abrió.
- Los botones de editar y borrar dicen a qué elemento afectan (por ejemplo, «Borrar la interacción Café del 1 nov 2026, 10:00»).
- Si el backend todavía no tiene una ruta de borrado, la app lo explica en lugar de mostrar un error genérico.

### v1.4.0
- **Buscador global de contactos** en la barra superior, con navegación por teclado y atajo Ctrl+K / ⌘K.
- **Direcciones propias para cada vista**: recargar, el botón *Atrás* y los enlaces a una ficha concreta ya funcionan.
- **Accesibilidad (WCAG 2.2 AA)** en toda la app: nombres para todos los controles, iconos que el lector de pantalla ya no lee como texto, foco visible, menú móvil que se maneja con teclado y contraste corregido en ambos temas.
- **Contactos** más claro: resumen de la red en una línea, atajos a los pasos vencidos y de esta semana, botón para quitar filtros cuando no hay resultados y columnas que aparecen solo cuando caben, sin scroll lateral.
- Los colores de los círculos se ajustan solos para que el texto siempre se lea, también en modo oscuro.
- En móvil, toda la fila de un contacto abre su ficha.
- El botón **Contactar** del dashboard abre el canal preferido del contacto, y las tarjetas del pipeline se pueden abrir con el teclado para editarlas o cambiarlas de etapa.
- Fuera los controles que aún no hacían nada (notificaciones, ajustes, cerrar sesión y sincronizar calendario): volverán cuando tengan funcionalidad.

### v1.3.0
- **Edición** de contactos, círculos, notas y recordatorios desde la app, reutilizando los mismos formularios que para crearlos.
- Al editar un contacto se conservan los datos que el formulario no muestra (familia, foto, origen de la importación…).
- Nuevos endpoints `PUT …/actualizar/{id}` en la API, también para interacciones.
- Paginación compacta en Contactos, para que siga siendo cómoda con miles de contactos.

### v1.2.0
- **Importación de contactos** desde LinkedIn (`Connections.csv`), Google Contacts (Google CSV) o un CSV/Excel propio.
- Vista previa antes de importar, detección de duplicados, asignación a un círculo y resumen final con los errores.
- Desde LinkedIn se guarda la fecha en que conectasteis; desde Google, el teléfono y el cumpleaños.
- Se hace todo desde el frontend, usando la API existente: no hay cambios en el backend ni en la base de datos.

### v1.1.0
- Borrado de **contactos**, **círculos** y **notas** desde la app, con confirmación antes de borrar.
- Al borrar un contacto se eliminan también sus notas, interacciones, recordatorios y entradas del pipeline. Al borrar un círculo, sus contactos se conservan sin círculo.
- Nuevos endpoints de borrado en la API.

### v1.0.0
- Primera versión estable: dashboard, contactos, ficha 360°, pipeline relacional, modo oscuro, diseño adaptable y despliegue con Docker.

## 🗺️ Hoja de ruta

Esta es la **versión 1.5.0**. Lo siguiente que está previsto para las próximas versiones:

- [x] Borrar contactos, círculos y notas desde la app *(v1.1.0)*.
- [x] Importar contactos desde LinkedIn, Google y CSV *(v1.2.0)*.
- [x] Editar contactos, círculos, notas y recordatorios desde la app *(v1.3.0)*.
- [x] Buscador global de contactos *(v1.4.0)*.
- [x] Accesibilidad WCAG AA y direcciones propias para cada vista *(v1.4.0)*.
- [x] Editar interacciones y borrar interacciones y recordatorios *(v1.5.0)*.
- [ ] Ampliar el buscador global a notas y círculos.
- [ ] Pantalla de ajustes y edición del perfil.
- [ ] Inicio de sesión para proteger los datos.
- [ ] Sincronización con el calendario.
- [ ] Notificaciones de recordatorios y cumpleaños.
- [ ] Tests automáticos en backend y frontend.

¿Tienes alguna idea? Abre un *issue*.

## ⚠️ Limitaciones conocidas de la v1.5.0

- Los endpoints de borrado y edición responden `200` aunque no hayan cambiado nada. Al editar, la app vuelve a leer el registro y muestra lo que de verdad quedó guardado, pero no puede mostrar el motivo del fallo.
- La seguridad está abierta (sin login): está pensada para usarse **solo en local**. No la expongas a internet.
- La importación solo admite archivos `.csv` (no `.vcf` ni `.xlsx`) y no trae fotos de perfil.
- El buscador global solo busca contactos (por nombre, empresa o email), todavía no notas ni círculos.

## 👤 Autor

**Juan Luis Garrote** · [@JL-garrote](https://github.com/JL-garrote)

Proyecto personal en desarrollo. ¡Gracias por pasarte!
