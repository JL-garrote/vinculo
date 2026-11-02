import { reactive } from 'vue';
import { api } from './vinculo';
import type { Circulo, Contacto, Nota, Perfil, Recordatorio } from './types';

// Datos compartidos por todas las vistas; se cargan una vez al arrancar la app
export const store = reactive({
  perfil: null as Perfil | null,
  contactos: [] as Contacto[],
  circulos: [] as Circulo[],
  notas: [] as Nota[],
  /** Recordatorios pendientes de todos los contactos */
  recordatorios: [] as Recordatorio[],
  cargando: false,
  error: null as string | null,
});

export async function cargarDatos() {
  store.cargando = true;
  store.error = null;
  try {
    const [perfil, contactos, circulos, notas, recordatorios] = await Promise.all([
      api.perfil(),
      api.contactos(),
      api.circulos(),
      api.notas(),
      api.recordatoriosPendientes(),
    ]);
    store.perfil = perfil;
    store.contactos = contactos.filter((c) => !c.archivado);
    store.circulos = [...circulos].sort((a, b) => a.orden - b.orden);
    store.notas = notas;
    store.recordatorios = recordatorios;
  } catch (e) {
    store.error = e instanceof Error ? e.message : 'Error desconocido';
  } finally {
    store.cargando = false;
  }
}

/** Recarga los recordatorios pendientes (p. ej. tras registrar una interacción, que puede completarlos) */
export async function recargarRecordatorios() {
  store.recordatorios = await api.recordatoriosPendientes();
}

// ---------- Borrados: se llama al backend y se refleja en el store lo que la base de datos hace sola ----------

/** Borra el contacto junto con sus notas y recordatorios (ON DELETE CASCADE) */
export async function borrarContacto(id: number) {
  await api.borrarContacto(id);
  store.contactos = store.contactos.filter((c) => c.id !== id);
  for (const c of store.contactos) if (c.presentadoPorID === id) c.presentadoPorID = 0;
  store.notas = store.notas.filter((n) => n.contactoID !== id);
  store.recordatorios = store.recordatorios.filter((r) => r.contactoID !== id);
}

/** Borra el círculo; sus contactos se quedan sin círculo (ON DELETE SET NULL) */
export async function borrarCirculo(id: number) {
  await api.borrarCirculo(id);
  store.circulos = store.circulos.filter((c) => c.id !== id);
  for (const c of store.contactos) if (c.circuloID === id) c.circuloID = 0;
}

export async function borrarNota(id: number) {
  await api.borrarNota(id);
  store.notas = store.notas.filter((n) => n.id !== id);
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
