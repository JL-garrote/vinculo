// Tipos que reflejan el JSON que devuelve el backend (modelos de com.personal.vinculo.Models).
// Los ids que en la base de datos son NULL llegan como 0, porque el modelo Java usa `long`.

export type CanalPreferido = 'whatsapp' | 'llamada' | 'email' | 'presencial' | 'videollamada';
export type OrigenContacto = 'manual' | 'google' | 'linkedin' | 'csv';
export type TipoNota = 'general' | 'oportunidad' | 'preferencia' | 'recurso';

export interface Contacto {
  id: number;
  tratamiento: string | null;
  nombre: string;
  apellidos: string | null;
  fotoURI: string | null;
  empresa: string | null;
  cargo: string | null;
  ciudad: string | null;
  pais: string | null;
  email: string | null;
  telefono: string | null;
  linkedinURI: string | null;
  canalPreferido: CanalPreferido | null;
  circuloID: number;
  rolRelacion: string | null;
  cadenciaDias: number;
  prioritario: number;
  intereses: string | null;
  /** Cumpleaños (columna `cumpleanos`), formato YYYY-MM-DD */
  date: string | null;
  familia: string | null;
  aficiones: string | null;
  hitoProfesional: string | null;
  comoConocidos: string | null;
  presentadoPorID: number;
  proximoPaso: string | null;
  /** Formato YYYY-MM-DD HH:mm:ss */
  proximoPasoFecha: string | null;
  origen: OrigenContacto;
  idExterno: string | null;
  archivado: number;
  creadoEn: string;
  actualizadoEn: string;
}

export interface Circulo {
  id: number;
  nombre: string;
  /** Color en hexadecimal, p. ej. #E11D48 */
  color: string;
  defectoDias: number;
  orden: number;
}

export interface Nota {
  id: number;
  contactoID: number;
  tipo: TipoNota;
  titulo: string | null;
  contenido: string;
  fijada: number;
  creadoEn: string;
}

export type TipoInteraccion =
  | 'cafe'
  | 'comida'
  | 'reunion'
  | 'llamada'
  | 'videollamada'
  | 'mensaje'
  | 'email'
  | 'evento'
  | 'introduccion'
  | 'otro';

export interface Interaccion {
  id: number;
  contactoID: number;
  contactoRelacionadoID: number;
  tipo: TipoInteraccion;
  titulo: string | null;
  /** Formato YYYY-MM-DD HH:mm:ss */
  fecha: string;
  lugar: string | null;
  resumen: string | null;
  /** Temas separados por comas */
  temas: string | null;
  borrador: number;
  origen: string;
  idExterno: string | null;
  creadoEn: string;
}

export interface Recordatorio {
  id: number;
  contactoID: number;
  titulo: string;
  descripcion: string | null;
  venceEnFecha: string;
  recurrenciaDias: number;
  origen: 'manual' | 'asistente';
  estado: 'pendiente' | 'completado' | 'descartado';
  completadoEnFecha: string | null;
  creadoEn: string;
}

// ---------- Cuerpos de las peticiones POST ----------

export interface PeticionContacto {
  tratamiento?: string | null;
  nombre: string;
  apellidos?: string | null;
  empresa?: string | null;
  cargo?: string | null;
  ciudad?: string | null;
  pais?: string | null;
  email?: string | null;
  telefono?: string | null;
  linkedinURI?: string | null;
  canalPreferido?: CanalPreferido | null;
  circuloID?: number | null;
  rolRelacion?: string | null;
  cadenciaDias?: number | null;
  prioritario?: boolean;
  intereses?: string | null;
  /** YYYY-MM-DD */
  cumpleanos?: string | null;
  comoConocidos?: string | null;
  presentadoPorID?: number | null;
  proximoPaso?: string | null;
  proximoPasoFecha?: string | null;
}

export interface PeticionNota {
  contactoID: number;
  tipo: TipoNota;
  titulo?: string | null;
  contenido: string;
  fijada: boolean;
}

export interface PeticionCirculo {
  nombre: string;
  color: string;
  cadenciaDefectoDias?: number | null;
}

export interface PeticionInteraccion {
  contactoID: number;
  tipo: TipoInteraccion;
  titulo?: string | null;
  fecha: string;
  resumen?: string | null;
  temas?: string | null;
  borrador?: boolean;
}

export interface PeticionRecordatorio {
  contactoID?: number | null;
  titulo: string;
  descripcion?: string | null;
  venceEn: string;
  recurrenciaDias?: number | null;
}

export type EtapaPipeline = 'identificado' | 'primer_contacto' | 'reunion' | 'seguimiento' | 'aliado';
export type PrioridadPipeline = 'alta' | 'media' | 'baja';

export interface Pipeline {
  id: number;
  nombre: string;
  objetivo: string | null;
  activo: boolean;
  creadoEn: string;
}

/** Un contacto dentro de un pipeline (tabla pipeline_contactos) */
export interface PipelineContacto {
  id: number;
  pipelineID: number;
  contactoID: number;
  etapa: EtapaPipeline;
  prioridad: PrioridadPipeline;
  siguienteAccion: string | null;
  /** Formato YYYY-MM-DD HH:mm:ss */
  siguienteAccionFecha: string | null;
  posicion: number;
  /** Desde cuándo está en la etapa actual */
  etapaDesdeFecha: string;
  creadoEn: string;
}

/** Campos que se envían al crear o actualizar una entrada del pipeline */
export interface PeticionPipelineContacto {
  contactoID?: number;
  etapa: EtapaPipeline;
  prioridad: PrioridadPipeline;
  siguienteAccion: string | null;
  siguienteAccionFecha: string | null;
}

export interface Perfil {
  id: number;
  nombre: string;
  email: string | null;
  avatarURI: string | null;
  zonaHoraria: string | null;
  objetivoInteraccionesSemana: number;
  umbralRelacionFuerte: number;
  cadenciaDefectoDias: number;
  preferencias: string | null;
  integraciones: string | null;
  creadoEn: string;
  actualizadoEn: string;
}
