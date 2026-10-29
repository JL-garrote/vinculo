import type { CanalPreferido, Contacto, TipoNota } from './types';

const DIA_MS = 24 * 60 * 60 * 1000;

export function nombreCompleto(c: Contacto, conTratamiento = true): string {
  return [conTratamiento ? c.tratamiento : null, c.nombre, c.apellidos].filter(Boolean).join(' ');
}

export function iniciales(nombre: string): string {
  return nombre
    .split(/\s+/)
    .filter((p) => p && !p.endsWith('.'))
    .slice(0, 2)
    .map((p) => p[0]?.toUpperCase() ?? '')
    .join('');
}

export function lista(texto: string | null): string[] {
  return (texto ?? '')
    .split(/[,;]/)
    .map((t) => t.trim())
    .filter(Boolean);
}

/** Convierte "YYYY-MM-DD" o "YYYY-MM-DD HH:mm:ss(.S)" del backend en Date */
export function parseFecha(texto: string | null): Date | null {
  if (!texto) return null;
  const d = new Date(texto.replace(' ', 'T'));
  return Number.isNaN(d.getTime()) ? null : d;
}

function inicioDelDia(d: Date): number {
  return new Date(d.getFullYear(), d.getMonth(), d.getDate()).getTime();
}

/** Días desde hoy hasta la fecha (negativo si ya pasó) */
export function diasHasta(fecha: Date): number {
  return Math.round((inicioDelDia(fecha) - inicioDelDia(new Date())) / DIA_MS);
}

export function textoDias(dias: number): string {
  if (dias === 0) return 'Hoy';
  if (dias === 1) return 'Mañana';
  if (dias === -1) return 'Ayer';
  return dias > 0 ? `En ${dias} días` : `Hace ${-dias} días`;
}

export function formatoFecha(fecha: Date, conHora = false): string {
  return fecha.toLocaleDateString('es-ES', {
    day: 'numeric',
    month: 'short',
    year: 'numeric',
    ...(conHora ? { hour: '2-digit', minute: '2-digit' } : {}),
  });
}

/** Próximo cumpleaños a partir de la fecha de nacimiento */
export function proximoCumple(nacimiento: string | null): { fecha: Date; dias: number; edad: number } | null {
  const d = parseFecha(nacimiento);
  if (!d) return null;
  const hoy = new Date();
  let fecha = new Date(hoy.getFullYear(), d.getMonth(), d.getDate());
  if (diasHasta(fecha) < 0) fecha = new Date(hoy.getFullYear() + 1, d.getMonth(), d.getDate());
  return { fecha, dias: diasHasta(fecha), edad: fecha.getFullYear() - d.getFullYear() };
}

export type EstadoProximoPaso = 'vencido' | 'pronto' | 'programado' | 'sin-fecha';

/** Estado del próximo paso: vencido, en los próximos 7 días, más adelante o sin fecha */
export function estadoProximoPaso(c: Contacto): EstadoProximoPaso {
  const fecha = parseFecha(c.proximoPasoFecha);
  if (!fecha) return 'sin-fecha';
  const dias = diasHasta(fecha);
  if (dias < 0) return 'vencido';
  if (dias <= 7) return 'pronto';
  return 'programado';
}

export const estiloEstado: Record<EstadoProximoPaso, { dot: string; texto: string; titulo: string }> = {
  vencido: { dot: 'bg-error', texto: 'text-error', titulo: 'Próximo paso vencido' },
  pronto: { dot: 'bg-tertiary-container', texto: 'text-tertiary-container', titulo: 'Próximo paso esta semana' },
  programado: { dot: 'bg-secondary', texto: 'text-secondary', titulo: 'Próximo paso programado' },
  'sin-fecha': { dot: 'bg-outline', texto: 'text-on-surface-variant', titulo: 'Sin próximo paso' },
};

export const canales: Record<CanalPreferido, { label: string; icon: string }> = {
  whatsapp: { label: 'WhatsApp', icon: 'chat' },
  llamada: { label: 'Llamada', icon: 'call' },
  email: { label: 'Email', icon: 'mail' },
  presencial: { label: 'Presencial', icon: 'local_cafe' },
  videollamada: { label: 'Videollamada', icon: 'video_call' },
};

export const tiposNota: Record<TipoNota, { label: string; icon: string; clase: string }> = {
  general: { label: 'General', icon: 'sticky_note_2', clase: 'bg-surface-container text-on-surface-variant' },
  oportunidad: { label: 'Oportunidad', icon: 'lightbulb', clase: 'bg-tertiary-fixed text-tertiary-container' },
  preferencia: { label: 'Preferencia', icon: 'tune', clase: 'bg-primary-fixed text-primary-container' },
  recurso: { label: 'Recurso', icon: 'auto_stories', clase: 'bg-secondary/10 text-secondary' },
};

/** Color hexadecimal con transparencia (#RRGGBB + alfa en hex) */
export function conAlfa(hex: string, alfa: number): string {
  const a = Math.round(alfa * 255)
    .toString(16)
    .padStart(2, '0');
  return /^#[0-9a-f]{6}$/i.test(hex) ? `${hex}${a}` : hex;
}
