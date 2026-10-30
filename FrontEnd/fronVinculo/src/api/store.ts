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

export function circuloDe(contacto: Contacto): Circulo | undefined {
  return store.circulos.find((c) => c.id === contacto.circuloID);
}

export function contactoPorId(id: number): Contacto | undefined {
  return store.contactos.find((c) => c.id === id);
}

export function notasDe(contactoId: number): Nota[] {
  return store.notas.filter((n) => n.contactoID === contactoId);
}
