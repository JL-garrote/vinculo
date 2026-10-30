import type {
  Circulo,
  Contacto,
  Interaccion,
  Nota,
  Perfil,
  PeticionCirculo,
  PeticionContacto,
  PeticionInteraccion,
  PeticionNota,
  PeticionPipelineContacto,
  PeticionRecordatorio,
  Pipeline,
  PipelineContacto,
  Recordatorio,
} from './types';

// En desarrollo Vite reenvía /api al backend (ver server.proxy en vite.config.ts)
const BASE = '/api/controller';

/** Error de la API con el código HTTP y, si lo hay, el mensaje del backend */
export class ApiError extends Error {
  constructor(
    message: string,
    public status: number,
  ) {
    super(message);
  }
}

async function request<T>(path: string, init?: RequestInit): Promise<T> {
  let res: Response;
  try {
    res = await fetch(`${BASE}${path}`, {
      ...init,
      headers: init?.body ? { 'Content-Type': 'application/json' } : undefined,
    });
  } catch {
    throw new ApiError('No se pudo conectar con el backend', 0);
  }
  const text = await res.text();
  if (!res.ok) {
    let mensaje = `El backend respondió ${res.status} en ${path}`;
    try {
      mensaje = JSON.parse(text).mensaje ?? mensaje;
    } catch {
      // cuerpo vacío o no JSON: se queda el mensaje genérico
    }
    throw new ApiError(mensaje, res.status);
  }
  // Un Optional vacío llega como "null"; un 204 o algunos errores, como cuerpo vacío
  return (text ? JSON.parse(text) : null) as T;
}

const get = <T>(path: string) => request<T>(path);
const send = <T>(method: 'POST' | 'PUT' | 'DELETE', path: string, body?: unknown) =>
  request<T>(path, { method, body: body === undefined ? undefined : JSON.stringify(body) });

/** Mensaje para mostrar al usuario a partir de cualquier error */
export function mensajeError(e: unknown): string {
  return e instanceof Error ? e.message : 'Error desconocido';
}

export const api = {
  contactos: async () => (await get<Contacto[] | null>('/contacto/todos')) ?? [],
  contacto: (id: number) => get<Contacto | null>(`/contacto/porID/${id}`),
  contadorContactos: () => get<number>('/contacto/contador'),
  crearContacto: (datos: PeticionContacto) => send<Contacto>('POST', '/contacto', datos),

  circulos: async () => (await get<Circulo[] | null>('/circulo/todos')) ?? [],
  circulo: (id: number) => get<Circulo | null>(`/circulo/porID/${id}`),
  crearCirculo: (datos: PeticionCirculo) => send<Circulo>('POST', '/circulo', datos),

  notas: async () => (await get<Nota[] | null>('/notas/todas')) ?? [],
  nota: (id: number) => get<Nota | null>(`/notas/id/${id}`),
  contadorNotas: () => get<number>('/notas/contadorNotas'),
  crearNota: (datos: PeticionNota) => send<Nota>('POST', '/notas', datos),

  interaccionesDe: async (contactoId: number) =>
    (await get<Interaccion[] | null>(`/interaccion/contacto/${contactoId}`)) ?? [],
  crearInteraccion: (datos: PeticionInteraccion) => send<Interaccion>('POST', '/interaccion', datos),

  recordatoriosPendientes: async () => (await get<Recordatorio[] | null>('/recordatorio/pendientes')) ?? [],
  recordatoriosDe: async (contactoId: number) =>
    (await get<Recordatorio[] | null>(`/recordatorio/contacto/${contactoId}`)) ?? [],
  crearRecordatorio: (datos: PeticionRecordatorio) => send<Recordatorio>('POST', '/recordatorio', datos),

  perfil: () => get<Perfil | null>('/perfil'),

  pipelines: async () => (await get<Pipeline[] | null>('/pipeline/todos')) ?? [],
  pipelineContactos: async (pipelineId: number) =>
    (await get<PipelineContacto[] | null>(`/pipeline/${pipelineId}/contactos`)) ?? [],
  anadirAlPipeline: (pipelineId: number, datos: PeticionPipelineContacto) =>
    send<PipelineContacto>('POST', `/pipeline/${pipelineId}/contactos`, datos),
  actualizarEnPipeline: (entradaId: number, datos: PeticionPipelineContacto) =>
    send<PipelineContacto>('PUT', `/pipeline/contactos/${entradaId}`, datos),
  quitarDelPipeline: (entradaId: number) => send<null>('DELETE', `/pipeline/contactos/${entradaId}`),
};
