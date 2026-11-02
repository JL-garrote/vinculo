<script setup lang="ts">
import { computed, ref, watch } from 'vue';
import InteractionLogger, {
  type InteractionType,
  type NewInteraction,
} from '@/components/ficha/InteractionLogger.vue';
import AvatarContacto from '@/components/AvatarContacto.vue';
import NuevaNotaForm from '@/components/formularios/NuevaNotaForm.vue';
import RecordatorioDialog from '@/components/formularios/RecordatorioDialog.vue';
import ConfirmarBorradoDialog from '@/components/ConfirmarBorradoDialog.vue';
import {
  borrarContacto,
  borrarNota,
  circuloDe,
  contactoPorId,
  notasDe,
  recargarRecordatorios,
  store,
} from '@/api/store';
import { api, mensajeError } from '@/api/vinculo';
import {
  canales,
  conAlfa,
  diasHasta,
  estadoProximoPaso,
  estiloEstado,
  fechaBackend,
  formatoFecha,
  lista,
  nombreCompleto,
  parseFecha,
  proximoCumple,
  textoDias,
  tiposInteraccion,
  tiposNota,
} from '@/api/format';
import type { Contacto, Interaccion, Nota, Recordatorio, TipoInteraccion } from '@/api/types';

const contactoId = defineModel<number | null>('contactoId', { default: null });

const contactosOrdenados = computed(() =>
  [...store.contactos].sort((a, b) => nombreCompleto(a, false).localeCompare(nombreCompleto(b, false), 'es')),
);

// Contacto mostrado: el elegido o, si no hay ninguno, el primero del directorio
const contacto = computed<Contacto | undefined>(
  () => (contactoId.value !== null ? contactoPorId(contactoId.value) : undefined) ?? contactosOrdenados.value[0],
);

const idSeleccionado = computed({
  get: () => contacto.value?.id ?? null,
  set: (id: number | null) => (contactoId.value = id),
});

const nombre = computed(() => (contacto.value ? nombreCompleto(contacto.value) : ''));
const circulo = computed(() => (contacto.value ? circuloDe(contacto.value) : undefined));
const canal = computed(() => (contacto.value?.canalPreferido ? canales[contacto.value.canalPreferido] : null));
const cadencia = computed(() => contacto.value?.cadenciaDias || circulo.value?.defectoDias || 0);
const ubicacion = computed(() => [contacto.value?.ciudad, contacto.value?.pais].filter(Boolean).join(', '));
const puesto = computed(() =>
  [contacto.value?.cargo, contacto.value?.empresa].filter(Boolean).join(' @ '),
);

const fechaPaso = computed(() => parseFecha(contacto.value?.proximoPasoFecha ?? null));
const estado = computed(() => (contacto.value ? estiloEstado[estadoProximoPaso(contacto.value)] : estiloEstado['sin-fecha']));

const contextoPersonal = computed(() => {
  const c = contacto.value;
  if (!c) return [];
  const cumple = proximoCumple(c.date);
  const items = [
    { icon: 'diversity_1', label: 'Familia & Vínculos', texto: c.familia, badge: undefined as string | undefined },
    { icon: 'interests', label: 'Aficiones & Pasiones', texto: c.aficiones, badge: undefined },
    {
      icon: 'cake',
      label: 'Cumpleaños',
      texto: cumple ? cumple.fecha.toLocaleDateString('es-ES', { day: 'numeric', month: 'long' }) : null,
      badge: cumple ? `${textoDias(cumple.dias)} · cumple ${cumple.edad}` : undefined,
    },
    { icon: 'workspace_premium', label: 'Hito Profesional', texto: c.hitoProfesional, badge: undefined },
    { icon: 'handshake', label: 'Cómo nos conocimos', texto: c.comoConocidos, badge: undefined },
  ];
  return items.filter((i) => i.texto);
});

const notas = computed(() =>
  contacto.value
    ? [...notasDe(contacto.value.id)].sort(
        (a, b) => b.fijada - a.fijada || (parseFecha(b.creadoEn)?.getTime() ?? 0) - (parseFecha(a.creadoEn)?.getTime() ?? 0),
      )
    : [],
);

const presentadoPor = computed(() =>
  contacto.value && contacto.value.presentadoPorID > 0 ? contactoPorId(contacto.value.presentadoPorID) : undefined,
);
const haPresentado = computed(() =>
  contacto.value ? store.contactos.filter((c) => c.presentadoPorID === contacto.value!.id) : [],
);
const intereses = computed(() => lista(contacto.value?.intereses ?? null));

// ---------- Interacciones y recordatorios del contacto (desde el backend) ----------

const interacciones = ref<Interaccion[]>([]);
const recordatorios = ref<Recordatorio[]>([]);
const cargandoHistorial = ref(false);
const errorHistorial = ref<string | null>(null);

async function cargarHistorial(id: number) {
  cargandoHistorial.value = true;
  errorHistorial.value = null;
  try {
    const [inter, rec] = await Promise.all([api.interaccionesDe(id), api.recordatoriosDe(id)]);
    // Si mientras tanto se cambió de contacto, se descarta la respuesta
    if (contacto.value?.id !== id) return;
    interacciones.value = inter;
    recordatorios.value = rec;
  } catch (e) {
    errorHistorial.value = mensajeError(e);
  } finally {
    cargandoHistorial.value = false;
  }
}

watch(
  () => contacto.value?.id,
  (id) => {
    interacciones.value = [];
    recordatorios.value = [];
    if (id !== undefined) cargarHistorial(id);
  },
  { immediate: true },
);

const timeline = computed(() =>
  interacciones.value.map((i) => {
    const tipo = tiposInteraccion[i.tipo] ?? tiposInteraccion.otro;
    const fecha = parseFecha(i.fecha);
    return {
      ...i,
      tipoInfo: tipo,
      fechaTexto: fecha ? `${formatoFecha(fecha, true)} • ${textoDias(diasHasta(fecha))}` : '',
      tags: lista(i.temas).map((t) => `#${t}`),
    };
  }),
);

// Pestañas del registro → tipos de interacción del backend
const tipoBackend: Record<InteractionType, TipoInteraccion> = {
  cafe: 'cafe',
  llamada: 'llamada',
  mensaje: 'mensaje',
  nota: 'otro',
};

async function guardarInteraccion({ type, text, topics, borrador }: NewInteraction) {
  if (!contacto.value) return;
  const tipo = tipoBackend[type];
  const nueva = await api.crearInteraccion({
    contactoID: contacto.value.id,
    tipo,
    titulo: tiposInteraccion[tipo].label,
    fecha: fechaBackend(),
    resumen: text,
    temas: topics.length ? topics.join(',') : null,
    borrador,
  });
  interacciones.value = [nueva, ...interacciones.value];
  // Una interacción puede completar recordatorios pendientes (trigger de la base de datos)
  if (!borrador) {
    const [rec] = await Promise.all([api.recordatoriosDe(contacto.value.id), recargarRecordatorios()]);
    recordatorios.value = rec;
  }
}

const recordatoriosPendientes = computed(() => recordatorios.value.filter((r) => r.estado === 'pendiente'));

// ---------- Altas desde la ficha ----------

const creandoNota = ref(false);
const programando = ref(false);

function recordatorioCreado(r: Recordatorio) {
  programando.value = false;
  if (r.contactoID === contacto.value?.id) recordatorios.value = [...recordatorios.value, r];
}

const logger = ref<InstanceType<typeof InteractionLogger> | null>(null);

// ---------- Borrados ----------

const borrandoContacto = ref(false);
const notaABorrar = ref<Nota | null>(null);

function contactoBorrado() {
  borrandoContacto.value = false;
  // Sin contacto elegido se muestra el primero del directorio
  contactoId.value = null;
}
</script>

<template>
  <div v-if="!contacto" class="px-4 sm:px-8 py-16 text-center font-body-md text-body-md text-on-surface-variant">
    Todavía no hay contactos. Añade uno para ver su ficha.
  </div>

  <div v-else class="relative w-full px-4 sm:px-6 lg:px-8 pt-6 sm:pt-8 pb-10 overflow-x-clip">
    <!-- Acentos de fondo -->
    <div class="absolute -top-12 -left-12 w-96 h-96 bg-primary/5 rounded-full blur-3xl pointer-events-none"></div>
    <div class="absolute top-20 right-1/4 w-80 h-80 bg-secondary-container/20 rounded-full blur-3xl pointer-events-none"></div>

    <!-- Selector de contacto -->
    <div class="relative flex flex-wrap items-center justify-end gap-2 mb-4">
      <span class="font-label-sm text-label-sm text-on-surface-variant">Ver ficha de:</span>
      <select
        v-model="idSeleccionado"
        class="max-w-full min-w-0 bg-surface-container-lowest text-on-surface font-label-md text-label-md py-1.5 px-3 rounded-lg shadow-sm focus:outline-none focus:ring-2 focus:ring-primary/20 cursor-pointer"
      >
        <option v-for="c in contactosOrdenados" :key="c.id" :value="c.id">{{ nombreCompleto(c) }}</option>
      </select>
    </div>

    <!-- Cabecera del contacto -->
    <div class="relative bg-surface-container-lowest rounded-xl shadow-sm p-5 sm:p-8 flex flex-col gap-6">
      <div class="flex flex-col lg:flex-row lg:items-start justify-between gap-6">
        <div class="flex flex-col sm:flex-row items-start sm:items-center gap-6">
          <div class="relative">
            <AvatarContacto :nombre="nombre" :src="contacto.fotoURI" size="w-20 h-20 sm:w-28 sm:h-28 text-headline-md shadow-md" />
            <span
              v-if="contacto.prioritario"
              class="absolute bottom-1 right-1 w-6 h-6 rounded-full bg-secondary text-on-secondary flex items-center justify-center shadow-sm"
              title="Contacto prioritario"
            >
              <span class="material-symbols-outlined text-sm" style="font-variation-settings: 'FILL' 1">star</span>
            </span>
          </div>

          <div class="flex flex-col gap-2 min-w-0">
            <div class="flex flex-wrap items-center gap-2">
              <h1 class="font-headline-md text-headline-md text-on-surface">{{ nombre }}</h1>
              <span
                v-if="contacto.rolRelacion"
                class="px-2.5 py-0.5 rounded-full font-label-sm text-label-sm bg-primary-fixed text-on-primary-fixed"
              >
                {{ contacto.rolRelacion }}
              </span>
              <span
                v-if="circulo"
                class="px-2.5 py-0.5 rounded-full font-label-sm text-label-sm"
                :style="{ backgroundColor: conAlfa(circulo.color, 0.15), color: circulo.color }"
              >
                {{ circulo.nombre }}
              </span>
              <span
                v-if="contacto.prioritario"
                class="px-2.5 py-0.5 rounded-full font-label-sm text-label-sm bg-surface-container-high text-on-surface-variant flex items-center gap-1"
              >
                <span class="material-symbols-outlined text-xs">grade</span> Prioritario
              </span>
            </div>
            <p
              v-if="puesto || ubicacion"
              class="font-body-md text-body-md text-on-surface-variant flex flex-wrap items-center gap-2"
            >
              <span v-if="puesto">{{ puesto }}</span>
              <span v-if="puesto && ubicacion" class="w-1.5 h-1.5 rounded-full bg-outline-variant"></span>
              <span v-if="ubicacion" class="flex items-center gap-1">
                <span class="material-symbols-outlined text-sm">location_on</span> {{ ubicacion }}
              </span>
            </p>
            <div class="flex flex-wrap items-center gap-3 mt-1">
              <a
                v-if="contacto.email"
                :href="`mailto:${contacto.email}`"
                class="flex items-center gap-1.5 px-2.5 py-1 rounded bg-surface-container-low text-on-surface hover:bg-surface-container transition-colors font-label-sm text-label-sm"
              >
                <span class="material-symbols-outlined text-sm text-primary">mail</span>
                <span class="truncate">{{ contacto.email }}</span>
              </a>
              <a
                v-if="contacto.telefono"
                :href="`tel:${contacto.telefono.replace(/[^\d+]/g, '')}`"
                class="flex items-center gap-1.5 px-2.5 py-1 rounded bg-surface-container-low text-on-surface hover:bg-surface-container transition-colors font-label-sm text-label-sm"
              >
                <span class="material-symbols-outlined text-sm text-secondary">call</span>
                <span>{{ contacto.telefono }}</span>
              </a>
              <a
                v-if="contacto.linkedinURI"
                :href="contacto.linkedinURI"
                target="_blank"
                rel="noopener"
                title="LinkedIn"
                class="flex items-center gap-1.5 px-2.5 py-1 rounded bg-surface-container-low text-on-surface hover:bg-surface-container transition-colors font-label-sm text-label-sm"
              >
                <span class="material-symbols-outlined text-sm">link</span>
                <span>LinkedIn</span>
              </a>
              <span
                v-if="canal"
                class="flex items-center gap-1.5 px-2.5 py-1 rounded bg-surface-container-low text-on-surface-variant font-label-sm text-label-sm"
              >
                <span class="material-symbols-outlined text-sm">{{ canal.icon }}</span>
                Prefiere: {{ canal.label }}
              </span>
            </div>
          </div>
        </div>

        <div class="flex flex-wrap items-center gap-2.5 self-start lg:self-center">
          <button
            type="button"
            class="flex items-center gap-2 px-4 py-2.5 bg-primary-container text-on-primary rounded-lg font-label-lg text-label-lg hover:bg-primary transition-all shadow-sm"
            @click="logger?.focus()"
          >
            <span class="material-symbols-outlined text-base">rate_review</span>
            <span>Registrar Interacción</span>
          </button>
          <button
            type="button"
            class="flex items-center gap-2 px-3.5 py-2.5 bg-surface-container text-on-surface rounded-lg font-label-lg text-label-lg hover:bg-surface-container-high transition-colors"
            @click="programando = true"
          >
            <span class="material-symbols-outlined text-base">alarm</span>
            <span>Programar Toque</span>
          </button>
          <button
            type="button"
            class="p-2.5 rounded-lg text-error hover:bg-error-container transition-colors"
            title="Eliminar contacto"
            @click="borrandoContacto = true"
          >
            <span class="material-symbols-outlined text-base">delete</span>
          </button>
        </div>
      </div>

      <!-- Ritmo de la relación -->
      <div class="w-full bg-surface-container-low rounded-lg p-4 flex flex-col md:flex-row items-center justify-between gap-4 mt-2">
        <div class="flex items-center gap-4 w-full md:w-auto">
          <div class="w-12 h-12 rounded-full bg-surface-container-lowest flex items-center justify-center shrink-0">
            <span class="material-symbols-outlined" :class="estado.texto">event</span>
          </div>
          <div>
            <div class="flex items-center gap-2">
              <span class="font-label-lg text-label-lg text-on-surface">
                {{ contacto.proximoPaso ?? 'Sin próximo paso definido' }}
              </span>
              <span class="w-2 h-2 rounded-full" :class="estado.dot"></span>
            </div>
            <p class="font-body-sm text-body-sm text-on-surface-variant">{{ estado.titulo }}</p>
          </div>
        </div>
        <div class="flex flex-wrap items-center gap-4 sm:gap-8 w-full md:w-auto justify-between md:justify-end">
          <div class="flex flex-col text-left md:text-right">
            <span class="font-label-sm text-label-sm uppercase tracking-wider text-on-surface-variant">Cadencia Deseada</span>
            <span class="font-label-lg text-label-lg text-on-surface mt-0.5 flex items-center gap-1.5 md:justify-end">
              <span class="material-symbols-outlined text-sm text-primary">autorenew</span>
              {{ cadencia ? `Cada ${cadencia} días` : 'Sin definir' }}
            </span>
          </div>
          <div class="flex flex-col text-left md:text-right">
            <span class="font-label-sm text-label-sm uppercase tracking-wider text-on-surface-variant">Fecha del Paso</span>
            <span
              class="font-label-lg text-label-lg mt-0.5 flex items-center gap-1.5 md:justify-end"
              :class="estado.texto"
              :title="fechaPaso ? formatoFecha(fechaPaso, true) : ''"
            >
              <span class="material-symbols-outlined text-sm">schedule</span>
              {{ fechaPaso ? textoDias(diasHasta(fechaPaso)) : 'Sin fecha' }}
            </span>
          </div>
        </div>
      </div>
    </div>

    <!-- Vista 360 -->
    <div class="relative grid grid-cols-1 lg:grid-cols-12 gap-6 lg:gap-8 mt-6 sm:mt-8">
      <!-- Columna izquierda -->
      <div class="lg:col-span-5 flex flex-col gap-6">
        <!-- Contexto personal -->
        <div class="bg-surface-container-lowest rounded-xl p-4 sm:p-6 shadow-sm flex flex-col gap-5">
          <div class="flex items-center gap-2.5">
            <span class="p-2 rounded-lg bg-surface-container text-primary material-symbols-outlined text-lg">
              sentiment_satisfied
            </span>
            <h2 class="font-headline-sm text-headline-sm text-on-surface">Contexto Personal</h2>
          </div>
          <p v-if="!contextoPersonal.length" class="font-body-sm text-body-sm text-on-surface-variant">
            Aún no hay contexto personal para este contacto.
          </p>
          <div v-else class="flex flex-col gap-4">
            <div v-for="item in contextoPersonal" :key="item.label" class="flex items-start gap-3">
              <span class="material-symbols-outlined text-base text-on-surface-variant mt-0.5">{{ item.icon }}</span>
              <div class="flex-1 flex justify-between items-center gap-3">
                <div>
                  <span class="font-label-sm text-label-sm uppercase tracking-wider text-on-surface-variant block">
                    {{ item.label }}
                  </span>
                  <p class="font-body-md text-body-md text-on-surface mt-0.5">{{ item.texto }}</p>
                </div>
                <span
                  v-if="item.badge"
                  class="px-2 py-0.5 rounded bg-surface-container text-on-surface-variant font-label-sm text-label-sm whitespace-nowrap"
                >
                  {{ item.badge }}
                </span>
              </div>
            </div>
          </div>
        </div>

        <!-- Notas -->
        <div class="bg-surface-container-lowest rounded-xl p-4 sm:p-6 shadow-sm flex flex-col gap-4">
          <div class="flex items-center justify-between">
            <div class="flex items-center gap-2">
              <span
                class="p-1.5 rounded-lg bg-tertiary-fixed text-tertiary material-symbols-outlined text-lg"
                style="font-variation-settings: 'FILL' 1"
              >
                push_pin
              </span>
              <h2 class="font-headline-sm text-headline-sm text-on-surface">Notas</h2>
              <span class="px-2 py-0.5 rounded-full bg-surface-container font-label-sm text-label-sm text-on-surface-variant">
                {{ notas.length }}
              </span>
            </div>
            <button
              v-if="!creandoNota"
              type="button"
              class="font-label-sm text-label-sm text-primary hover:underline"
              @click="creandoNota = true"
            >
              + Añadir
            </button>
          </div>
          <NuevaNotaForm
            v-if="creandoNota"
            :key="contacto.id"
            :contacto-id="contacto.id"
            @creada="creandoNota = false"
            @cancelar="creandoNota = false"
          />
          <p v-if="!notas.length && !creandoNota" class="font-body-sm text-body-sm text-on-surface-variant">
            Este contacto todavía no tiene notas.
          </p>
          <div
            v-for="nota in notas"
            :key="nota.id"
            class="p-4 rounded-lg flex flex-col gap-2"
            :class="nota.fijada ? 'bg-tertiary-fixed/30' : 'bg-surface-container-low'"
          >
            <div class="flex items-center justify-between gap-2">
              <span class="flex items-center gap-1.5 font-label-sm text-label-sm uppercase tracking-wider text-on-surface-variant">
                <span v-if="nota.fijada" class="material-symbols-outlined text-xs text-tertiary-container">push_pin</span>
                {{ nota.titulo ?? (tiposNota[nota.tipo] ?? tiposNota.general).label }}
              </span>
              <div class="flex items-center gap-1">
                <span
                  class="px-2 py-0.5 rounded font-label-sm text-label-sm"
                  :class="(tiposNota[nota.tipo] ?? tiposNota.general).clase"
                >
                  {{ (tiposNota[nota.tipo] ?? tiposNota.general).label }}
                </span>
                <button
                  type="button"
                  class="p-1 rounded text-on-surface-variant hover:text-error hover:bg-error-container transition-colors"
                  title="Borrar nota"
                  @click="notaABorrar = nota"
                >
                  <span class="material-symbols-outlined text-base">delete</span>
                </button>
              </div>
            </div>
            <p class="font-body-sm text-body-sm text-on-surface">{{ nota.contenido }}</p>
            <span class="font-label-sm text-label-sm text-outline">
              {{ parseFecha(nota.creadoEn) ? formatoFecha(parseFecha(nota.creadoEn)!) : '' }}
            </span>
          </div>
        </div>

        <!-- Recordatorios -->
        <div class="bg-surface-container-lowest rounded-xl p-4 sm:p-6 shadow-sm flex flex-col gap-4">
          <div class="flex items-center justify-between">
            <div class="flex items-center gap-2">
              <span class="p-1.5 rounded-lg bg-primary-fixed text-on-primary-fixed-variant material-symbols-outlined text-lg">alarm</span>
              <h2 class="font-headline-sm text-headline-sm text-on-surface">Recordatorios</h2>
              <span class="px-2 py-0.5 rounded-full bg-surface-container font-label-sm text-label-sm text-on-surface-variant">
                {{ recordatoriosPendientes.length }} pendientes
              </span>
            </div>
            <button type="button" class="font-label-sm text-label-sm text-primary hover:underline" @click="programando = true">
              + Programar
            </button>
          </div>
          <p v-if="!recordatorios.length" class="font-body-sm text-body-sm text-on-surface-variant">
            Sin recordatorios para este contacto.
          </p>
          <div
            v-for="r in recordatorios"
            :key="r.id"
            class="flex items-start gap-3 p-3 rounded-lg"
            :class="r.estado === 'pendiente' ? 'bg-surface-container-low' : 'bg-surface-container-low/50 opacity-70'"
          >
            <span
              class="material-symbols-outlined text-base mt-0.5"
              :class="r.estado === 'pendiente' ? 'text-primary' : 'text-secondary'"
            >
              {{ r.estado === 'pendiente' ? 'schedule' : r.estado === 'completado' ? 'task_alt' : 'block' }}
            </span>
            <div class="flex-1 flex flex-col gap-0.5">
              <span class="font-label-lg text-label-lg text-on-surface" :class="{ 'line-through': r.estado !== 'pendiente' }">
                {{ r.titulo }}
              </span>
              <span v-if="r.descripcion" class="font-body-sm text-body-sm text-on-surface-variant">{{ r.descripcion }}</span>
              <span class="font-label-sm text-label-sm text-on-surface-variant">
                <template v-if="parseFecha(r.venceEnFecha)">
                  {{ formatoFecha(parseFecha(r.venceEnFecha)!, true) }} · {{ textoDias(diasHasta(parseFecha(r.venceEnFecha)!)) }}
                </template>
                <template v-if="r.recurrenciaDias"> · cada {{ r.recurrenciaDias }} días</template>
                <template v-if="r.estado !== 'pendiente'"> · {{ r.estado }}</template>
              </span>
            </div>
          </div>
        </div>

        <!-- Conexiones -->
        <div class="bg-surface-container-lowest rounded-xl p-4 sm:p-6 shadow-sm flex flex-col gap-4">
          <div class="flex items-center gap-2">
            <span class="p-1.5 rounded-lg bg-surface-container text-secondary material-symbols-outlined text-lg">hub</span>
            <h2 class="font-headline-sm text-headline-sm text-on-surface">Conexiones &amp; Intereses</h2>
          </div>
          <div class="flex flex-col gap-3">
            <button
              v-if="presentadoPor"
              type="button"
              class="flex items-center justify-between p-2.5 rounded-lg bg-surface-container-low hover:bg-surface-container transition-colors text-left"
              @click="contactoId = presentadoPor.id"
            >
              <div class="flex items-center gap-3">
                <AvatarContacto :nombre="nombreCompleto(presentadoPor)" :src="presentadoPor.fotoURI" size="w-8 h-8" />
                <div>
                  <span class="font-label-lg text-label-lg text-on-surface block leading-tight">
                    {{ nombreCompleto(presentadoPor) }}
                  </span>
                  <span class="font-label-sm text-label-sm text-on-surface-variant">Te lo presentó</span>
                </div>
              </div>
              <span class="material-symbols-outlined text-on-surface-variant text-base">arrow_forward</span>
            </button>
            <button
              v-for="persona in haPresentado"
              :key="persona.id"
              type="button"
              class="flex items-center justify-between p-2.5 rounded-lg bg-surface-container-low hover:bg-surface-container transition-colors text-left"
              @click="contactoId = persona.id"
            >
              <div class="flex items-center gap-3">
                <AvatarContacto :nombre="nombreCompleto(persona)" :src="persona.fotoURI" size="w-8 h-8" />
                <div>
                  <span class="font-label-lg text-label-lg text-on-surface block leading-tight">
                    {{ nombreCompleto(persona) }}
                  </span>
                  <span class="font-label-sm text-label-sm text-on-surface-variant">Presentado por este contacto</span>
                </div>
              </div>
              <span class="material-symbols-outlined text-on-surface-variant text-base">arrow_forward</span>
            </button>
            <p v-if="!presentadoPor && !haPresentado.length" class="font-body-sm text-body-sm text-on-surface-variant">
              Sin presentaciones registradas.
            </p>
            <div v-if="intereses.length" class="mt-2 flex flex-wrap gap-1.5">
              <span
                v-for="interes in intereses"
                :key="interes"
                class="px-2 py-1 rounded bg-surface-container text-on-surface font-label-sm text-label-sm"
              >
                #{{ interes }}
              </span>
            </div>
          </div>
        </div>
      </div>

      <!-- Columna derecha -->
      <div class="lg:col-span-7 flex flex-col gap-6">
        <InteractionLogger ref="logger" :guardar="guardarInteraccion" />

        <!-- Historial -->
        <div class="bg-surface-container-lowest rounded-xl p-4 sm:p-6 shadow-sm flex flex-col gap-6">
          <div class="flex items-center gap-2.5">
            <span class="p-2 rounded-lg bg-surface-container text-primary material-symbols-outlined text-lg">
              history_toggle_off
            </span>
            <div>
              <h2 class="font-headline-sm text-headline-sm text-on-surface">Memoria Relacional &amp; Historial</h2>
              <span class="font-label-sm text-label-sm text-on-surface-variant">
                {{ timeline.length }} interacciones registradas
              </span>
            </div>
          </div>

          <p v-if="errorHistorial" class="p-3 rounded-lg bg-error-container text-on-error-container font-body-sm text-body-sm">
            No se pudo cargar el historial: {{ errorHistorial }}
          </p>
          <p v-else-if="cargandoHistorial" class="font-body-sm text-body-sm text-on-surface-variant">Cargando historial…</p>
          <p v-else-if="!timeline.length" class="font-body-sm text-body-sm text-on-surface-variant">
            Aún no hay interacciones con este contacto. Registra la primera arriba.
          </p>

          <div
            v-else
            class="relative pl-6 flex flex-col gap-8 before:content-[''] before:absolute before:left-2.5 before:top-3 before:bottom-3 before:w-0.5 before:bg-surface-container-high"
          >
            <div v-for="entry in timeline" :key="entry.id" class="relative flex flex-col gap-2">
              <div
                class="absolute -left-6 top-1 w-5 h-5 rounded-full flex items-center justify-center ring-4 ring-surface-container-lowest"
                :class="entry.tipoInfo.clase"
              >
                <span class="material-symbols-outlined text-xs">{{ entry.tipoInfo.icon }}</span>
              </div>
              <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-1">
                <div class="flex items-center gap-2">
                  <span class="font-label-lg text-label-lg text-on-surface">{{ entry.titulo ?? entry.tipoInfo.label }}</span>
                  <span class="px-2 rounded-full font-label-sm text-label-sm" :class="entry.tipoInfo.clase">
                    {{ entry.tipoInfo.label }}
                  </span>
                  <span
                    v-if="entry.borrador"
                    class="px-2 rounded-full font-label-sm text-label-sm bg-surface-container-high text-on-surface-variant"
                  >
                    Borrador
                  </span>
                </div>
                <span class="font-label-sm text-label-sm text-on-surface-variant">{{ entry.fechaTexto }}</span>
              </div>
              <div class="bg-surface-container-low rounded-lg flex flex-col gap-2 p-4">
                <p v-if="entry.lugar" class="flex items-center gap-1 font-label-sm text-label-sm text-on-surface-variant">
                  <span class="material-symbols-outlined text-xs">location_on</span> {{ entry.lugar }}
                </p>
                <p class="font-body-md text-body-md text-on-surface">{{ entry.resumen ?? 'Sin resumen.' }}</p>
                <div v-if="entry.tags.length" class="flex flex-wrap items-center gap-1.5 pt-1">
                  <span
                    v-for="tag in entry.tags"
                    :key="tag"
                    class="px-2 py-0.5 rounded bg-surface-container text-on-surface-variant font-label-sm text-label-sm"
                  >
                    {{ tag }}
                  </span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <RecordatorioDialog
      v-if="programando"
      :contacto-id-inicial="contacto.id"
      @creado="recordatorioCreado"
      @cerrar="programando = false"
    />

    <ConfirmarBorradoDialog
      v-if="borrandoContacto"
      titulo="Eliminar contacto"
      :mensaje="`Se borrará a ${nombre} junto con sus notas, interacciones, recordatorios y su lugar en el pipeline.`"
      :borrar="() => borrarContacto(contacto!.id)"
      @borrado="contactoBorrado"
      @cerrar="borrandoContacto = false"
    />
    <ConfirmarBorradoDialog
      v-if="notaABorrar"
      titulo="Borrar nota"
      :mensaje="`Se borrará la nota «${notaABorrar.titulo ?? notaABorrar.contenido.slice(0, 60)}».`"
      :borrar="() => borrarNota(notaABorrar!.id)"
      @borrado="notaABorrar = null"
      @cerrar="notaABorrar = null"
    />
  </div>
</template>
