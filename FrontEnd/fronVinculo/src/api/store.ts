import { reactive } from 'vue';
import { api } from './vinculo';
import type { Circulo, Contacto, Nota, Perfil } from './types';

// Datos compartidos por todas las vistas; se cargan una vez al arrancar la app
export const store = reactive({
  perfil: null as Perfil | null,
  contactos: [] as Contacto[],
  circulos: [] as Circulo[],
  notas: [] as Nota[],
  cargando: false,
  error: null as string | null,
});

export async function cargarDatos() {
  store.cargando = true;
  store.error = null;
  try {
    const [perfil, contactos, circulos, notas] = await Promise.all([
      api.perfil(),
      api.contactos(),
      api.circulos(),
      api.notas(),
    ]);
    store.perfil = perfil;
    store.contactos = contactos.filter((c) => !c.archivado);
    store.circulos = [...circulos].sort((a, b) => a.orden - b.orden);
    store.notas = notas;
  } catch (e) {
    store.error = e instanceof Error ? e.message : 'Error desconocido';
  } finally {
    store.cargando = false;
  }
}

export function circuloDe(contacto: Contacto): Circulo | undefined {
  return store.circulos.find((c) => c.id === contacto.circuloID);
}

export function contactoPorId(id: number): Contacto | undefined {
  return store.contactos.find((c) => c.id === id);
}

export function notasDe(contactoId: number): Nota[] {
  return store.notas.filter((n) => n.contactoID === contactoId);
}
