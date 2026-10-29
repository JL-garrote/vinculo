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
