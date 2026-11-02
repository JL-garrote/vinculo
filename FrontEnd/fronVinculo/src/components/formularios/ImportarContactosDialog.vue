<script setup lang="ts">
import { computed, ref } from 'vue';
import ModalBase from '../ModalBase.vue';
import { api, ApiError, mensajeError } from '@/api/vinculo';
import { store } from '@/api/store';
import { leerContactos } from '@/api/importar';
import type { OrigenContacto, PeticionContacto } from '@/api/types';

const emit = defineEmits<{ cerrar: [] }>();

type Fuente = 'linkedin' | 'google' | 'csv';

const fuentes: Record<Fuente, { label: string; icon: string; pasos: string[] }> = {
  linkedin: {
    label: 'LinkedIn',
    icon: 'work',
    pasos: [
      'En LinkedIn, abre Yo → Configuración y privacidad → Privacidad de datos.',
      'Pulsa "Obtener una copia de tus datos", marca solo "Contactos" y solicita el archivo.',
      'LinkedIn te avisa por email en unos minutos. Descarga el ZIP, descomprímelo y sube aquí "Connections.csv".',
      'LinkedIn solo incluye el email de quienes lo tienen visible.',
    ],
  },
  google: {
    label: 'Google',
    icon: 'contacts',
    pasos: [
      'Abre contacts.google.com y pulsa "Exportar" (icono de descarga arriba a la derecha).',
      'Elige "Google CSV" y descarga el archivo.',
      'Sube aquí el fichero "contacts.csv".',
    ],
  },
  csv: {
    label: 'CSV / Excel',
    icon: 'table',
    pasos: [
      'Usa una hoja de cálculo con una fila de cabecera. Solo es obligatoria la columna "Nombre".',
      'Columnas reconocidas: Nombre, Apellidos, Email, Teléfono, Empresa, Cargo, Ciudad, País, LinkedIn y Cumpleaños (AAAA-MM-DD).',
      'En Excel, guárdala como "CSV UTF-8 (delimitado por comas)" y súbela aquí.',
    ],
  },
};

const etiquetaOrigen: Record<OrigenContacto, string> = {
  linkedin: 'exportación de LinkedIn',
  google: 'exportación de Google Contacts',
  csv: 'CSV genérico',
  manual: 'manual',
};

interface Fila {
  datos: PeticionContacto;
  duplicado: boolean;
  marcada: boolean;
  estado: 'pendiente' | 'creado' | 'existe' | 'error';
  error?: string;
}

const fuente = ref<Fuente>('linkedin');
const paso = ref<'elegir' | 'revisar' | 'importando' | 'hecho'>('elegir');
const archivo = ref('');
const origen = ref<OrigenContacto>('csv');
const filas = ref<Fila[]>([]);
const descartadas = ref(0);
const circuloID = ref(0);
const error = ref<string | null>(null);
const arrastrando = ref(false);

const marcadas = computed(() => filas.value.filter((f) => f.marcada));
const duplicados = computed(() => filas.value.filter((f) => f.duplicado).length);
const todasMarcadas = computed({
  get: () => filas.value.length > 0 && filas.value.every((f) => f.marcada),
  set: (v: boolean) => filas.value.forEach((f) => (f.marcada = v)),
});
const progreso = computed(() => filas.value.filter((f) => f.marcada && f.estado !== 'pendiente').length);
const resumen = computed(() => ({
  creados: filas.value.filter((f) => f.estado === 'creado').length,
  existian: filas.value.filter((f) => f.estado === 'existe').length,
  errores: filas.value.filter((f) => f.estado === 'error'),
}));

async function leer(fichero: File | undefined) {
  if (!fichero) return;
  error.value = null;
  if (!/\.csv$/i.test(fichero.name)) {
    error.value = 'El archivo tiene que ser un .csv.';
    return;
  }
  try {
    const resultado = leerContactos(await fichero.text(), store.contactos);
    if (!resultado.contactos.length) {
      error.value = 'El archivo no tiene ningún contacto con nombre.';
      return;
    }
    archivo.value = fichero.name;
    origen.value = resultado.origen;
    descartadas.value = resultado.descartadas;
    // Los posibles duplicados se dejan sin marcar: decides tú si importarlos
    filas.value = resultado.contactos.map((c) => ({ ...c, marcada: !c.duplicado, estado: 'pendiente' }));
    paso.value = 'revisar';
  } catch (e) {
    error.value = mensajeError(e);
  }
}

function soltar(e: DragEvent) {
  arrastrando.value = false;
  leer(e.dataTransfer?.files[0]);
}

async function importar() {
  paso.value = 'importando';
  // De uno en uno: así el progreso es fiel y no se satura el backend con exportaciones grandes
  for (const fila of marcadas.value) {
    try {
      const contacto = await api.crearContacto({ ...fila.datos, circuloID: circuloID.value || null });
      store.contactos.push(contacto);
      fila.estado = 'creado';
    } catch (e) {
      // 409: ya hay un contacto con el mismo origen e id externo (perfil de LinkedIn o email)
      if (e instanceof ApiError && e.status === 409) {
        fila.estado = 'existe';
      } else {
        fila.estado = 'error';
        fila.error = mensajeError(e);
      }
    }
  }
  paso.value = 'hecho';
}

const nombreDe = (d: PeticionContacto) => [d.nombre, d.apellidos].filter(Boolean).join(' ');
const etiqueta = 'font-label-sm text-label-sm uppercase tracking-wider text-on-surface-variant';
</script>

<template>
  <ModalBase titulo="Importar contactos" ancho="max-w-3xl" @cerrar="paso !== 'importando' && emit('cerrar')">
    <!-- 1. Elegir fuente y archivo -->
    <template v-if="paso === 'elegir'">
      <div class="grid grid-cols-3 gap-2">
        <button
          v-for="(f, key) in fuentes"
          :key="key"
          type="button"
          class="flex flex-col sm:flex-row items-center justify-center gap-1.5 sm:gap-2 px-3 py-3 rounded-lg font-label-lg text-label-lg transition-colors"
          :class="
            fuente === key
              ? 'bg-primary-container text-on-primary shadow-sm'
              : 'bg-surface-container-low text-on-surface-variant hover:bg-surface-container hover:text-on-surface'
          "
          @click="fuente = key"
        >
          <span class="material-symbols-outlined text-lg">{{ f.icon }}</span>
          {{ f.label }}
        </button>
      </div>

      <ol class="flex flex-col gap-2 p-4 rounded-lg bg-surface-container-low list-decimal list-inside">
        <li v-for="texto in fuentes[fuente].pasos" :key="texto" class="font-body-sm text-body-sm text-on-surface">
          {{ texto }}
        </li>
      </ol>

      <label
        class="flex flex-col items-center justify-center gap-2 p-8 rounded-xl border-2 border-dashed cursor-pointer transition-colors text-center"
        :class="arrastrando ? 'border-primary bg-primary/5' : 'border-outline-variant hover:border-primary'"
        @dragover.prevent="arrastrando = true"
        @dragleave="arrastrando = false"
        @drop.prevent="soltar"
      >
        <span class="material-symbols-outlined text-4xl text-primary">upload_file</span>
        <span class="font-label-lg text-label-lg text-on-surface">Arrastra aquí el archivo .csv o haz clic para elegirlo</span>
        <span class="font-body-sm text-body-sm text-on-surface-variant">
          Se lee en tu navegador; no se guarda nada hasta que confirmes.
        </span>
        <input
          type="file"
          accept=".csv,text/csv"
          class="sr-only"
          @change="leer(($event.target as HTMLInputElement).files?.[0])"
        />
      </label>

      <p v-if="error" class="p-3 rounded-lg bg-error-container text-on-error-container font-body-sm text-body-sm">
        {{ error }}
      </p>
    </template>

    <!-- 2. Revisar antes de importar -->
    <template v-else-if="paso === 'revisar'">
      <div class="flex flex-col gap-1">
        <p class="font-body-md text-body-md text-on-surface">
          <strong>{{ archivo }}</strong> · {{ etiquetaOrigen[origen] }} · {{ filas.length }} contactos
        </p>
        <p v-if="duplicados || descartadas" class="font-body-sm text-body-sm text-on-surface-variant">
          <template v-if="duplicados">{{ duplicados }} parecen estar ya en Vínculo y se han desmarcado. </template>
          <template v-if="descartadas">{{ descartadas }} filas sin nombre se han ignorado.</template>
        </p>
      </div>

      <div class="max-h-[45vh] overflow-auto rounded-lg bg-surface-container-low">
        <table class="w-full text-left">
          <thead class="sticky top-0 bg-surface-container">
            <tr>
              <th class="p-3 w-10"><input v-model="todasMarcadas" type="checkbox" class="w-4 h-4 accent-primary-container" title="Marcar todos" /></th>
              <th class="p-3" :class="etiqueta">Nombre</th>
              <th class="p-3 hidden sm:table-cell" :class="etiqueta">Empresa · Cargo</th>
              <th class="p-3 hidden md:table-cell" :class="etiqueta">Email</th>
            </tr>
          </thead>
          <tbody>
            <tr
              v-for="(fila, i) in filas"
              :key="i"
              class="border-t border-surface-container cursor-pointer hover:bg-surface-container"
              @click="fila.marcada = !fila.marcada"
            >
              <td class="p-3"><input v-model="fila.marcada" type="checkbox" class="w-4 h-4 accent-primary-container" @click.stop /></td>
              <td class="p-3 font-body-sm text-body-sm text-on-surface">
                <span class="flex flex-wrap items-center gap-2">
                  {{ nombreDe(fila.datos) }}
                  <span
                    v-if="fila.duplicado"
                    class="px-1.5 py-0.5 rounded bg-tertiary-fixed text-on-surface font-label-sm text-label-sm"
                  >
                    Ya existe
                  </span>
                </span>
              </td>
              <td class="p-3 hidden sm:table-cell font-body-sm text-body-sm text-on-surface-variant">
                {{ [fila.datos.empresa, fila.datos.cargo].filter(Boolean).join(' · ') || '—' }}
              </td>
              <td class="p-3 hidden md:table-cell font-body-sm text-body-sm text-on-surface-variant break-all">
                {{ fila.datos.email ?? '—' }}
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <label class="flex flex-col sm:flex-row sm:items-center gap-2">
        <span :class="etiqueta">Añadir al círculo</span>
        <select
          v-model.number="circuloID"
          class="bg-surface-container-low text-on-surface font-body-sm text-body-sm py-2 px-3 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary/20"
        >
          <option :value="0">Sin círculo</option>
          <option v-for="c in store.circulos" :key="c.id" :value="c.id">{{ c.nombre }}</option>
        </select>
      </label>

      <div class="flex flex-wrap items-center justify-between gap-2">
        <button
          type="button"
          class="px-3 py-2 rounded-lg text-on-surface-variant hover:text-on-surface font-label-md text-label-md"
          @click="paso = 'elegir'"
        >
          ← Otro archivo
        </button>
        <div class="flex items-center gap-2">
          <button
            type="button"
            class="px-4 py-2 rounded-lg bg-surface-container text-on-surface-variant hover:text-on-surface font-label-md text-label-md"
            @click="emit('cerrar')"
          >
            Cancelar
          </button>
          <button
            type="button"
            class="px-4 py-2 rounded-lg bg-primary-container hover:bg-primary text-on-primary font-label-md text-label-md shadow-sm disabled:opacity-50"
            :disabled="!marcadas.length"
            @click="importar"
          >
            Importar {{ marcadas.length }} {{ marcadas.length === 1 ? 'contacto' : 'contactos' }}
          </button>
        </div>
      </div>
    </template>

    <!-- 3. Progreso -->
    <div v-else-if="paso === 'importando'" class="flex flex-col gap-3 py-4">
      <p class="font-body-md text-body-md text-on-surface">Importando {{ progreso }} de {{ marcadas.length }}…</p>
      <div class="h-2 rounded-full bg-surface-container overflow-hidden">
        <div
          class="h-full bg-primary-container transition-all"
          :style="{ width: `${marcadas.length ? (progreso / marcadas.length) * 100 : 0}%` }"
        ></div>
      </div>
      <p class="font-body-sm text-body-sm text-on-surface-variant">No cierres esta ventana hasta que termine.</p>
    </div>

    <!-- 4. Resultado -->
    <template v-else>
      <div class="flex items-start gap-3">
        <span class="p-2 rounded-full bg-secondary-container text-secondary material-symbols-outlined">task_alt</span>
        <div class="flex flex-col gap-1 font-body-md text-body-md text-on-surface">
          <p><strong>{{ resumen.creados }}</strong> contactos importados.</p>
          <p v-if="resumen.existian" class="text-on-surface-variant">
            {{ resumen.existian }} ya estaban importados de antes y se han saltado.
          </p>
          <p v-if="resumen.errores.length" class="text-error">{{ resumen.errores.length }} no se han podido importar:</p>
        </div>
      </div>
      <ul
        v-if="resumen.errores.length"
        class="max-h-40 overflow-auto p-3 rounded-lg bg-error-container text-on-error-container font-body-sm text-body-sm flex flex-col gap-1"
      >
        <li v-for="(f, i) in resumen.errores" :key="i">
          <strong>{{ nombreDe(f.datos) }}</strong>: {{ f.error }}
        </li>
      </ul>
      <div class="flex justify-end">
        <button
          type="button"
          class="px-4 py-2 rounded-lg bg-primary-container hover:bg-primary text-on-primary font-label-md text-label-md shadow-sm"
          @click="emit('cerrar')"
        >
          Listo
        </button>
      </div>
    </template>
  </ModalBase>
</template>
