import { ref } from 'vue';

// Enrutado mínimo por hash (#/contactos, #/contactos/12…): cada vista tiene dirección propia,
// así recargar, el botón Atrás y los enlaces funcionan sin añadir vue-router.

export type Seccion = 'dashboard' | 'contactos' | 'ficha-contacto' | 'pipeline-relacional';

export const RUTAS: Record<Seccion, { ruta: string; titulo: string }> = {
  dashboard: { ruta: '/', titulo: 'Dashboard' },
  contactos: { ruta: '/contactos', titulo: 'Contactos' },
  'ficha-contacto': { ruta: '/ficha', titulo: 'Ficha de contacto' },
  'pipeline-relacional': { ruta: '/pipeline', titulo: 'Pipeline relacional' },
};

export const hrefDe = (seccion: Seccion) => `#${RUTAS[seccion].ruta}`;
export const hrefFicha = (id: number) => `#/contactos/${id}`;

export const seccion = ref<Seccion>('dashboard');
export const contactoActual = ref<number | null>(null);

function leer() {
  const ruta = decodeURI(location.hash.replace(/^#/, '')) || '/';
  const ficha = ruta.match(/^\/contactos\/(\d+)\/?$/);
  if (ficha) {
    seccion.value = 'ficha-contacto';
    contactoActual.value = Number(ficha[1]);
    return;
  }
  const encontrada = (Object.keys(RUTAS) as Seccion[]).find((s) => RUTAS[s].ruta === ruta.replace(/(.)\/$/, '$1'));
  seccion.value = encontrada ?? 'dashboard';
  if (seccion.value !== 'ficha-contacto') contactoActual.value = null;
}

/** Hash que corresponde al estado actual */
function hashActual() {
  if (seccion.value === 'ficha-contacto' && contactoActual.value !== null) return hrefFicha(contactoActual.value);
  return hrefDe(seccion.value);
}

/** Lleva la dirección del navegador al estado actual (añade una entrada al historial) */
export function sincronizarHash() {
  const h = hashActual();
  if (location.hash !== h && !(h === '#/' && location.hash === '')) location.hash = h;
}

export function iniciarRutas() {
  leer();
  window.addEventListener('hashchange', leer);
}
