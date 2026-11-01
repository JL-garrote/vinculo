import type { Contacto, OrigenContacto, PeticionContacto } from './types';

// Importación de contactos desde ficheros CSV exportados de LinkedIn, Google Contacts o una hoja de cálculo.
// Todo se lee en el navegador; luego cada contacto se crea con el POST /contacto de siempre.

export interface ContactoImportado {
  datos: PeticionContacto;
  /** Coincide con un contacto que ya tienes o con otro anterior del mismo fichero */
  duplicado: boolean;
}

export interface ResultadoLectura {
  origen: OrigenContacto;
  contactos: ContactoImportado[];
  /** Filas sin nombre ni email, que no se pueden importar */
  descartadas: number;
}

/** Lee un CSV (RFC 4180): comillas, comillas dobles escapadas y saltos de línea dentro de un campo */
export function parsearCSV(texto: string): string[][] {
  texto = texto.replace(/^﻿/, '');
  // Excel en español separa con ";"; LinkedIn y Google, con ","
  const inicio = texto.split(/\r?\n/, 5).join('\n');
  const separador = (inicio.match(/;/g)?.length ?? 0) > (inicio.match(/,/g)?.length ?? 0) ? ';' : ',';

  const filas: string[][] = [];
  let fila: string[] = [];
  let campo = '';
  let entreComillas = false;

  for (let i = 0; i < texto.length; i++) {
    const c = texto[i];
    if (entreComillas) {
      if (c === '"' && texto[i + 1] === '"') {
        campo += '"';
        i++;
      } else if (c === '"') {
        entreComillas = false;
      } else {
        campo += c;
      }
    } else if (c === '"') {
      entreComillas = true;
    } else if (c === separador) {
      fila.push(campo);
      campo = '';
    } else if (c === '\n' || c === '\r') {
      if (c === '\r' && texto[i + 1] === '\n') i++;
      fila.push(campo);
      filas.push(fila);
      fila = [];
      campo = '';
    } else {
      campo += c;
    }
  }
  if (campo || fila.length) {
    fila.push(campo);
    filas.push(fila);
  }
  return filas.filter((f) => f.some((v) => v.trim()));
}

const normalizar = (s: string) =>
  s
    .normalize('NFD')
    .replace(/[̀-ͯ]/g, '')
    .toLowerCase()
    .trim();

// Nombres de columna que se reconocen para cada campo (ya normalizados), por orden de preferencia
const columnas = {
  nombre: ['first name', 'given name', 'nombre', 'nombre de pila'],
  segundoNombre: ['middle name', 'additional name', 'segundo nombre'],
  apellidos: ['last name', 'family name', 'apellidos', 'apellido', 'surname'],
  nombreCompleto: ['name', 'full name', 'nombre completo', 'display name'],
  email: ['email address', 'e-mail address', 'e-mail 1 - value', 'email', 'e-mail', 'correo', 'correo electronico'],
  telefono: ['phone 1 - value', 'mobile phone', 'phone', 'phone number', 'telefono', 'movil'],
  empresa: ['company', 'organization name', 'organization 1 - name', 'empresa', 'organizacion'],
  cargo: ['position', 'organization title', 'organization 1 - title', 'job title', 'title', 'cargo', 'puesto'],
  ciudad: ['address 1 - city', 'city', 'ciudad', 'localidad'],
  pais: ['address 1 - country', 'country', 'pais'],
  linkedin: ['url', 'linkedin', 'linkedin url', 'perfil de linkedin'],
  cumpleanos: ['birthday', 'cumpleanos', 'fecha de nacimiento'],
  conectado: ['connected on'],
} satisfies Record<string, string[]>;

type Campo = keyof typeof columnas;

/** Recorta al tamaño de la columna en la base de datos; vacío → null */
const recortar = (v: string, max: number) => v.slice(0, max) || null;

function detectarOrigen(cabecera: string[]): OrigenContacto {
  if (cabecera.includes('connected on')) return 'linkedin';
  if (cabecera.some((c) => c.includes(' - value') || c === 'given name' || c === 'organization name')) return 'google';
  return 'csv';
}

/** Google separa varios valores de un campo con " ::: "; nos quedamos con el primero */
const primerValor = (v: string) => v.split(' ::: ')[0]!.trim();

/** Clave para detectar duplicados: email, perfil de LinkedIn o nombre completo */
function claves(c: { nombre: string; apellidos?: string | null; email?: string | null; linkedinURI?: string | null }) {
  const lista: string[] = [];
  if (c.email) lista.push(`e:${normalizar(c.email)}`);
  if (c.linkedinURI) lista.push(`l:${normalizar(c.linkedinURI).replace(/\/+$/, '')}`);
  lista.push(`n:${normalizar(`${c.nombre} ${c.apellidos ?? ''}`).replace(/\s+/g, ' ')}`);
  return lista;
}

export function leerContactos(texto: string, existentes: Contacto[]): ResultadoLectura {
  const filas = parsearCSV(texto);
  // LinkedIn pone unas líneas de aviso antes de la cabecera: la cabecera es la primera fila con una columna de nombre
  const indiceCabecera = filas.findIndex((f) => {
    const cab = f.map(normalizar);
    return [...columnas.nombre, ...columnas.nombreCompleto].some((n) => cab.includes(n));
  });
  if (indiceCabecera === -1) {
    throw new Error('No se encuentra una columna de nombre ("First Name", "Nombre"...). ¿Es el fichero correcto?');
  }

  const cabecera = filas[indiceCabecera]!.map(normalizar);
  const origen = detectarOrigen(cabecera);
  const indices = Object.fromEntries(
    Object.entries(columnas).map(([campo, nombres]) => [
      campo,
      nombres.map((n) => cabecera.indexOf(n)).find((i) => i !== -1) ?? -1,
    ]),
  ) as Record<Campo, number>;

  const vistas = new Set(existentes.flatMap((c) => claves(c)));
  const contactos: ContactoImportado[] = [];
  let descartadas = 0;

  for (const fila of filas.slice(indiceCabecera + 1)) {
    const valor = (campo: Campo) => (indices[campo] === -1 ? '' : primerValor(fila[indices[campo]] ?? ''));

    let nombre = [valor('nombre'), valor('segundoNombre')].filter(Boolean).join(' ');
    let apellidos = valor('apellidos');
    if (!nombre && valor('nombreCompleto')) {
      const [primero, ...resto] = valor('nombreCompleto').split(/\s+/);
      nombre = primero ?? '';
      apellidos ||= resto.join(' ');
    }
    const email = valor('email');
    // Sin nombre, se usa la parte del email antes de la @
    if (!nombre && email) nombre = email.split('@')[0]!;
    if (!nombre) {
      descartadas++;
      continue;
    }

    const linkedin = valor('linkedin');
    const cumple = valor('cumpleanos');
    const conectado = valor('conectado');
    const datos: PeticionContacto = {
      nombre: nombre.slice(0, 100),
      apellidos: recortar(apellidos, 150),
      email: recortar(email, 255),
      telefono: recortar(valor('telefono'), 30),
      empresa: recortar(valor('empresa'), 200),
      cargo: recortar(valor('cargo'), 150),
      ciudad: recortar(valor('ciudad'), 100),
      pais: recortar(valor('pais'), 100),
      linkedinURI: /^https?:\/\//i.test(linkedin) ? recortar(linkedin, 500) : null,
      // Solo fechas completas: Google exporta "--05-12" cuando no sabe el año
      cumpleanos: /^\d{4}-\d{2}-\d{2}$/.test(cumple) ? cumple : null,
      comoConocidos: conectado ? `Conectamos en LinkedIn el ${conectado}` : null,
      origen,
    };
    datos.idExterno = origen === 'linkedin' ? datos.linkedinURI : (datos.email ?? null);

    const suyas = claves(datos);
    const duplicado = suyas.some((k) => vistas.has(k));
    suyas.forEach((k) => vistas.add(k));
    contactos.push({ datos, duplicado });
  }

  return { origen, contactos, descartadas };
}
