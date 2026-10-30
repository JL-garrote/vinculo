import type {
  CanalPreferido,
  Contacto,
  EtapaPipeline,
  PrioridadPipeline,
  TipoInteraccion,
  TipoNota,
} from './types';

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
  preferencia: { label: 'Preferencia', icon: 'tune', clase: 'bg-primary-fixed text-on-primary-fixed-variant' },
  recurso: { label: 'Recurso', icon: 'auto_stories', clase: 'bg-secondary/10 text-secondary' },
};

export const etapas: { key: EtapaPipeline; titulo: string; dot: string; header: string; count: string }[] = [
  {
    key: 'identificado',
    titulo: 'Identificados',
    dot: 'bg-outline',
    header: 'bg-surface-container-low',
    count: 'bg-surface-container-highest text-on-surface-variant',
  },
  {
    key: 'primer_contacto',
    titulo: 'Primer Contacto',
    dot: 'bg-on-tertiary-container',
    header: 'bg-surface-container-low',
    count: 'bg-surface-container-highest text-on-surface-variant',
  },
  {
    key: 'reunion',
    titulo: 'Reunión / Café',
    dot: 'bg-primary',
    header: 'bg-surface-container-low',
    count: 'bg-surface-container-highest text-on-surface-variant',
  },
  {
    key: 'seguimiento',
    titulo: 'Seguimiento Activo',
    dot: 'bg-secondary',
    header: 'bg-surface-container-low',
    count: 'bg-surface-container-highest text-on-surface-variant',
  },
  {
    key: 'aliado',
    titulo: 'Relación Establecida',
    dot: 'bg-secondary',
    header: 'bg-secondary-fixed/40',
    count: 'bg-secondary text-on-secondary',
  },
];

export const prioridades: Record<PrioridadPipeline, { label: string; badge: string; dot: string }> = {
  alta: { label: 'Alta', badge: 'text-error bg-error-container', dot: 'bg-error' },
  media: { label: 'Media', badge: 'text-on-tertiary-fixed-variant bg-tertiary-fixed', dot: 'bg-on-tertiary-container' },
  baja: { label: 'Baja', badge: 'text-outline bg-surface-container-high', dot: 'bg-outline' },
};

export const tiposInteraccion: Record<TipoInteraccion, { label: string; icon: string; clase: string }> = {
  cafe: { label: 'Café', icon: 'local_cafe', clase: 'bg-secondary-container text-on-secondary-container' },
  comida: { label: 'Comida', icon: 'restaurant', clase: 'bg-secondary-container text-on-secondary-container' },
  reunion: { label: 'Reunión', icon: 'groups', clase: 'bg-secondary-container text-on-secondary-container' },
  llamada: { label: 'Llamada', icon: 'phone_in_talk', clase: 'bg-primary-fixed text-on-primary-fixed' },
  videollamada: { label: 'Videollamada', icon: 'video_call', clase: 'bg-primary-fixed text-on-primary-fixed' },
  mensaje: { label: 'Mensaje', icon: 'chat', clase: 'bg-tertiary-fixed text-on-tertiary-fixed' },
  email: { label: 'Email', icon: 'mail', clase: 'bg-tertiary-fixed text-on-tertiary-fixed' },
  evento: { label: 'Evento', icon: 'celebration', clase: 'bg-surface-container-high text-on-surface-variant' },
  introduccion: { label: 'Presentación', icon: 'handshake', clase: 'bg-surface-container-high text-on-surface-variant' },
  otro: { label: 'Nota libre', icon: 'edit_note', clase: 'bg-surface-container-high text-on-surface-variant' },
};

/** Fecha y hora local en el formato del backend: YYYY-MM-DD HH:mm:ss */
export function fechaBackend(fecha = new Date()): string {
  const p = (n: number) => String(n).padStart(2, '0');
  return `${fecha.getFullYear()}-${p(fecha.getMonth() + 1)}-${p(fecha.getDate())} ${p(fecha.getHours())}:${p(fecha.getMinutes())}:${p(fecha.getSeconds())}`;
}

/** "YYYY-MM-DD HH:mm:ss" del backend → valor de <input type="datetime-local"> */
export function aInputFecha(texto: string | null): string {
  return texto ? texto.slice(0, 16).replace(' ', 'T') : '';
}

/** Valor de <input type="datetime-local"> → "YYYY-MM-DD HH:mm:ss" para el backend */
export function desdeInputFecha(valor: string): string | null {
  return valor ? `${valor.replace('T', ' ')}:00` : null;
}

/** Color hexadecimal con transparencia (#RRGGBB + alfa en hex) */
export function conAlfa(hex: string, alfa: number): string {
  const a = Math.round(alfa * 255)
    .toString(16)
    .padStart(2, '0');
  return /^#[0-9a-f]{6}$/i.test(hex) ? `${hex}${a}` : hex;
}
