import type { Circulo, Contacto, Nota, Perfil } from './types';

// En desarrollo Vite reenvía /api al backend (ver server.proxy en vite.config.ts)
const BASE = '/api/controller';

async function get<T>(path: string): Promise<T> {
  const res = await fetch(`${BASE}${path}`);
  if (!res.ok) throw new Error(`El backend respondió ${res.status} en ${path}`);
  // Un Optional vacío llega como "null" y algunos errores del backend como cuerpo vacío
  const text = await res.text();
  return (text ? JSON.parse(text) : null) as T;
}

export const api = {
  contactos: async () => (await get<Contacto[] | null>('/contacto/todos')) ?? [],
  contacto: (id: number) => get<Contacto | null>(`/contacto/porID/${id}`),
  contadorContactos: () => get<number>('/contacto/contador'),

  circulos: async () => (await get<Circulo[] | null>('/circulo/todos')) ?? [],
  circulo: (id: number) => get<Circulo | null>(`/circulo/porID/${id}`),

  notas: async () => (await get<Nota[] | null>('/notas/todas')) ?? [],
  nota: (id: number) => get<Nota | null>(`/notas/id/${id}`),
  contadorNotas: () => get<number>('/notas/contadorNotas'),

  perfil: () => get<Perfil | null>('/perfil'),
};
