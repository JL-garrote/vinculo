import type {
  CanalPreferido,
  Contacto,
  EtapaPipeline,
  PrioridadPipeline,
  TipoInteraccion,
  TipoNota,
} from './types';
import { tema, type Tema } from './tema';

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

// ---------- Color de los círculos ----------
// Cada círculo tiene el color que eligió el usuario y ese color lo identifica (punto, barra, fondo de la pastilla).
// Como texto no siempre se lee: un ámbar sobre blanco no llega a 2:1. Para el texto se usa el mismo tono con la
// luminosidad ajustada en OKLCH hasta 4,5:1 sobre la pastilla, en cada tema.

type Rgb = [number, number, number];

// Superficies sobre las que se pintan pastillas (--color-surface-container-lowest / -low de main.css)
const SUPERFICIES: Record<'tarjeta' | 'hundida', Record<Tema, string>> = {
  tarjeta: { claro: '#ffffff', oscuro: '#181b2d' },
  hundida: { claro: '#eff4ff', oscuro: '#1e2236' },
};
const CONTRASTE_TEXTO = 4.5;

const esHex = (c: string | null | undefined): c is string => !!c && /^#[0-9a-f]{6}$/i.test(c);
const aRgb = (hex: string): Rgb => [1, 3, 5].map((i) => parseInt(hex.slice(i, i + 2), 16)) as Rgb;
const aHex = (rgb: Rgb) => `#${rgb.map((v) => Math.round(v).toString(16).padStart(2, '0')).join('')}`;
const lineal = (v: number) => ((v /= 255) <= 0.04045 ? v / 12.92 : ((v + 0.055) / 1.055) ** 2.4);
const gamma = (v: number) => 255 * (v <= 0.0031308 ? 12.92 * v : 1.055 * v ** (1 / 2.4) - 0.055);
const luminancia = (rgb: Rgb) => {
  const [r, g, b] = rgb.map(lineal) as Rgb;
  return 0.2126 * r + 0.7152 * g + 0.0722 * b;
};
const contraste = (a: Rgb, b: Rgb) => {
  const [x, y] = [luminancia(a), luminancia(b)].sort((p, q) => q - p) as [number, number];
  return (x + 0.05) / (y + 0.05);
};
const mezclar = (a: Rgb, b: Rgb, peso: number): Rgb => a.map((v, i) => v * peso + b[i]! * (1 - peso)) as Rgb;

// sRGB ↔ OKLCH (Björn Ottosson). Devuelve null si el color cae fuera de sRGB.
function aOklch(rgb: Rgb): [number, number, number] {
  const [r, g, b] = rgb.map(lineal) as Rgb;
  const l = Math.cbrt(0.4122214708 * r + 0.5363325363 * g + 0.0514459929 * b);
  const m = Math.cbrt(0.2119034982 * r + 0.6806995451 * g + 0.1073969566 * b);
  const s = Math.cbrt(0.0883024619 * r + 0.2817188376 * g + 0.6299787005 * b);
  const L = 0.2104542553 * l + 0.793617785 * m - 0.0040720468 * s;
  const A = 1.9779984951 * l - 2.428592205 * m + 0.4505937099 * s;
  const B = 0.0259040371 * l + 0.7827717662 * m - 0.808675766 * s;
  return [L, Math.hypot(A, B), Math.atan2(B, A)];
}
function desdeOklch(L: number, C: number, h: number): Rgb | null {
  const A = C * Math.cos(h);
  const B = C * Math.sin(h);
  const l = (L + 0.3963377774 * A + 0.2158037573 * B) ** 3;
  const m = (L - 0.1055613458 * A - 0.0638541728 * B) ** 3;
  const s = (L - 0.0894841775 * A - 1.291485548 * B) ** 3;
  const lin = [
    4.0767416621 * l - 3.3077115913 * m + 0.2309699292 * s,
    -1.2684380046 * l + 2.6097574011 * m - 0.3413193965 * s,
    -0.0041960863 * l - 0.7034186147 * m + 1.707614701 * s,
  ];
  return lin.every((v) => v >= -1e-4 && v <= 1 + 1e-4) ? (lin.map((v) => gamma(Math.min(1, Math.max(0, v)))) as Rgb) : null;
}

const cacheTexto = new Map<string, string>();

/** El tono de `color`, con la luminosidad mínima necesaria para leerse (4,5:1) sobre `fondo` */
function textoLegible(color: Rgb, fondo: Rgb): string {
  const clave = `${aHex(color)}|${aHex(fondo)}`;
  const guardado = cacheTexto.get(clave);
  if (guardado) return guardado;

  let resultado = aHex(color);
  if (contraste(color, fondo) < CONTRASTE_TEXTO) {
    const [L0, C0, h] = aOklch(color);
    const paso = luminancia(fondo) > 0.18 ? -0.01 : 0.01; // fondo claro → oscurecer; oscuro → aclarar
    resultado = luminancia(fondo) > 0.18 ? '#000000' : '#ffffff';
    for (let L = L0 + paso; L > 0 && L < 1; L += paso) {
      // Cerca del negro o del blanco no cabe tanta saturación: se reduce hasta que el color existe en sRGB
      let C = C0;
      let rgb = desdeOklch(L, C, h);
      while (!rgb && C > 0.005) rgb = desdeOklch(L, (C *= 0.92), h);
      if (rgb && contraste(rgb, fondo) >= CONTRASTE_TEXTO) {
        resultado = aHex(rgb);
        break;
      }
    }
  }
  cacheTexto.set(clave, resultado);
  return resultado;
}

/**
 * Estilo de la pastilla de un círculo en el tema actual: fondo sólido con un 15 % de su color y texto legible
 * del mismo tono. `sobre` es la superficie en la que va la pastilla.
 */
export function estiloCirculo(color: string | null, sobre: keyof typeof SUPERFICIES = 'tarjeta') {
  if (!esHex(color)) return {};
  const fondo = mezclar(aRgb(color), aRgb(SUPERFICIES[sobre][tema.value]), 0.15);
  return { backgroundColor: aHex(fondo), color: textoLegible(aRgb(color), fondo) };
}

/** Color de texto con el tono del círculo, legible directamente sobre la superficie (sin pastilla) */
export function textoCirculo(color: string | null, sobre: keyof typeof SUPERFICIES = 'tarjeta') {
  return esHex(color) ? textoLegible(aRgb(color), aRgb(SUPERFICIES[sobre][tema.value])) : undefined;
}
