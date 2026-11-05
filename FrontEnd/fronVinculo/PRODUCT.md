# Product

<!-- impeccable:product-schema 1 -->

## Platform

web

## Users

Profesionales con una red de contactos grande (LinkedIn, clientes, mentores, antiguos compañeros, amigos) que quieren cuidarla con intención en lugar de dejar que se enfríe. El primer usuario es el propio autor; el producto se diseña para él y para gente como él.

- **Monousuario:** cada persona instala Vínculo y lo usa con sus propios datos, en su ordenador. No hay equipos, roles ni datos compartidos.
- **Situación de uso:** sesiones cortas y recurrentes ("¿con quién tengo que hablar esta semana?") y momentos puntuales justo antes o después de un encuentro (repasar la ficha, apuntar qué se habló, fijar el próximo paso).
- **Escala real:** redes de cientos a más de 10.000 contactos tras importar LinkedIn o Google.
- **Idioma:** español (es-ES) en interfaz, formatos de fecha y números.

## Product Purpose

Las relaciones se enfrían si no se cuidan. Vínculo es un PRM (*Personal Relationship Manager*): hace con las personas que te importan lo que un CRM hace con los clientes de una empresa. Te recuerda con quién hace tiempo que no hablas, cuál es el próximo paso con cada persona, qué hablasteis la última vez y qué detalles personales no deberías olvidar.

Éxito: el usuario abre la app, ve en segundos qué relaciones requieren atención, actúa sobre ellas y registra lo ocurrido sin fricción, de forma que ninguna relación importante se pierda por descuido.

## Positioning

Lo que ni un CRM de ventas ni la libreta de contactos del móvil pueden decir con verdad, todo a la vez:

1. **Cadencia por círculos.** Cada círculo (Familia, Mentores, Clientes…) fija cada cuántos días conviene hablar; cada contacto puede sobrescribirla. La app calcula quién se está enfriando y lo prioriza.
2. **Memoria de la relación.** Notas, interacciones, cumpleaños, familia, aficiones y quién te presentó a quién: el contexto humano, no un embudo de ventas.
3. **Pipeline relacional.** Un tablero *Identificado → Primer contacto → Reunión → Seguimiento → Aliado* para las relaciones que quieres hacer crecer de forma deliberada.
4. **Datos locales y privados.** Todo vive en el ordenador del usuario; los archivos importados se leen en el navegador y no salen de ahí.

## Operating Context

- Se ejecuta en local: backend Spring Boot + MariaDB (XAMPP o Docker) y frontend Vue servido por Vite o nginx. Instalable como PWA de escritorio desde Edge o Chrome.
- Fuentes de datos de entrada: exportaciones de LinkedIn (`Connections.csv`), Google Contacts (Google CSV) y hojas propias en CSV/Excel (`;` o `,`).
- Rituales: revisión semanal de relaciones que requieren atención; registro de una interacción tras un café, llamada, reunión o email; preparación antes de un encuentro desde la ficha del contacto.

## Capabilities and Constraints

**Confirmado (v1.3.0):**
- Dashboard con relaciones que requieren atención, notas recientes, recordatorios y reparto por círculos.
- Directorio de contactos con búsqueda, filtros (círculo, estado del próximo paso, ciudad), ordenación y paginación para miles de contactos.
- Ficha de contacto: datos, contexto personal, presentaciones, interacciones, notas (general, oportunidad, preferencia, recurso) y recordatorios con recurrencia.
- Crear, editar y borrar contactos, círculos, notas y recordatorios. Borrar un círculo deja a sus contactos sin círculo; borrar un contacto borra todo su historial. Siempre se confirma antes de borrar.
- Importación de contactos con revisión previa, detección de duplicados y reimportación segura (origen + identificador externo).
- Modo claro y oscuro (sigue al sistema y recuerda la elección); diseño adaptable de móvil a escritorio.

**Restricciones:**
- Sin login: solo para uso local, nunca expuesto a internet. El inicio de sesión llegará como cambio mayor (v2.0.0).
- La API devuelve `200` aunque una edición o borrado falle; la interfaz vuelve a leer el registro para mostrar lo que quedó guardado.
- La importación solo admite `.csv` y no trae fotos.

**Terminología:** contacto, círculo, cadencia (días), próximo paso, interacción, nota, recordatorio, pipeline, prioritario.

**Pendiente / sin decidir:**
- Edición de interacciones; borrado de interacciones y recordatorios.
- Buscador global, ajustes y perfil, notificaciones, sincronización con calendario: hoy existen como controles visibles sin función y no deben presentarse como si funcionaran.
- Navegación sin rutas (no hay URL por vista).

## Brand Commitments

- Nombre: **Vínculo**, con tilde. Descriptor: "PRM personal".
- Logotipo en `public/logo.svg` y `public/logo-oscuro.svg`.
- Voz: español cercano y directo, en segunda persona ("tu red", "con quién hace tiempo que no hablas"); sin jerga de ventas.

## Evidence on Hand

- Datos de prueba: CSV de 7, 100, 2.000 y 10.000 contactos ficticios en el Escritorio del autor; la base local tiene unos 12.000 contactos de prueba.
- `README.md` del repositorio con funcionalidades, novedades por versión, hoja de ruta y limitaciones.
- No hay usuarios externos, testimonios, métricas de uso ni capturas de prensa. No inventarlos.

## Product Principles

1. **La atención primero.** Cada pantalla responde antes que nada a "¿a quién debo cuidar ahora?"; lo urgente se ve sin buscarlo.
2. **Personas, no leads.** El lenguaje y las funciones sirven a relaciones humanas; nada de embudos de conversión ni métricas de venta.
3. **Tus datos son tuyos.** Nada sale del ordenador del usuario; cualquier función nueva respeta el uso local y monousuario.
4. **Rápido a escala.** Debe sentirse inmediato con miles de contactos importados.
5. **Honestidad en la interfaz.** No se muestra un control que no funciona ni un aviso que no es real; lo destructivo siempre se confirma.

## Accessibility & Inclusion

- Objetivo: **WCAG 2.2 AA** en toda la app, en modo claro y oscuro.
- Contraste mínimo 4,5:1 en texto y 3:1 en componentes; los colores de círculo que elige el usuario se ajustan para mantenerlo.
- Uso completo con teclado y foco visible; nombres accesibles en todos los controles (los iconos de Material Symbols se ocultan al lector de pantalla).
- Zonas táctiles de al menos 24×24 px, con 44 px como objetivo en móvil; campos de 16 px en móvil para evitar el zoom de iOS.
