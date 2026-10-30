<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue';
import PipelineCard from '@/components/pipeline/PipelineCard.vue';
import PipelineEntryDialog from '@/components/pipeline/PipelineEntryDialog.vue';
import { api, ApiError } from '@/api/vinculo';
import { contactoPorId, store } from '@/api/store';
import { diasHasta, etapas, nombreCompleto, parseFecha, prioridades } from '@/api/format';
import type {
  EtapaPipeline,
  PeticionPipelineContacto,
  Pipeline,
  PipelineContacto,
  PrioridadPipeline,
} from '@/api/types';

defineEmits<{ abrir: [contactoId: number] }>();

const pipelines = ref<Pipeline[]>([]);
const pipelineId = ref<number | null>(null);
const entradas = ref<PipelineContacto[]>([]);
const cargando = ref(true);
const error = ref<string | null>(null);
const aviso = ref<string | null>(null);

const pipeline = computed(() => pipelines.value.find((p) => p.id === pipelineId.value));

async function cargarPipelines() {
  cargando.value = true;
  error.value = null;
  try {
    pipelines.value = await api.pipelines();
    pipelineId.value = (pipelines.value.find((p) => p.activo) ?? pipelines.value[0])?.id ?? null;
    if (pipelineId.value === null) cargando.value = false;
  } catch (e) {
    error.value = e instanceof Error ? e.message : 'Error desconocido';
    cargando.value = false;
  }
}

async function cargarEntradas() {
  if (pipelineId.value === null) return;
  cargando.value = true;
  error.value = null;
  try {
    entradas.value = await api.pipelineContactos(pipelineId.value);
  } catch (e) {
    error.value = e instanceof Error ? e.message : 'Error desconocido';
  } finally {
    cargando.value = false;
  }
}

onMounted(cargarPipelines);
watch(pipelineId, cargarEntradas);

function mostrarAviso(texto: string) {
  aviso.value = texto;
  setTimeout(() => (aviso.value = null), 4000);
}

// ---------- Métricas ----------

const accionVencida = (e: PipelineContacto) => {
  const fecha = parseFecha(e.siguienteAccionFecha);
  return fecha !== null && diasHasta(fecha) < 0;
};

const metrics = computed(() => {
  const enDialogo = entradas.value.filter((e) => ['primer_contacto', 'reunion', 'seguimiento'].includes(e.etapa)).length;
  const aliados = entradas.value.filter((e) => e.etapa === 'aliado').length;
  const altas = entradas.value.filter((e) => e.prioridad === 'alta').length;
  return [
    {
      label: 'Total en Pipeline',
      value: entradas.value.length,
      detail: `${altas} de prioridad alta`,
      detailIcon: 'priority_high',
      detailClass: 'text-on-surface-variant',
      icon: 'diversity_3',
      iconClass: 'bg-surface-container-highest text-primary',
    },
    {
      label: 'Conexiones Activas',
      value: enDialogo,
      detail: 'En diálogo',
      detailClass: 'text-on-surface-variant',
      icon: 'forum',
      iconClass: 'bg-secondary-container/40 text-secondary',
    },
    {
      label: 'Alianzas',
      value: aliados,
      detail: 'Relación establecida',
      detailIcon: 'verified',
      detailClass: 'text-secondary',
      icon: 'handshake',
      iconClass: 'bg-surface-container-highest text-primary',
    },
  ];
});

const vencidas = computed(() => entradas.value.filter(accionVencida));
const progreso = computed(() =>
  entradas.value.length
    ? Math.round((entradas.value.filter((e) => e.etapa === 'aliado').length / entradas.value.length) * 100)
    : 0,
);

// ---------- Filtros ----------

const search = ref('');
const circuloFiltro = ref(0);
const prioridad = ref<PrioridadPipeline | 'todas'>('todas');
const soloVencidas = ref(false);

function textoBuscable(e: PipelineContacto) {
  const c = contactoPorId(e.contactoID);
  return [c ? nombreCompleto(c) : '', c?.empresa, c?.cargo, e.siguienteAccion]
    .filter(Boolean)
    .join(' ')
    .toLowerCase();
}

function esVisible(e: PipelineContacto) {
  const q = search.value.trim().toLowerCase();
  if (q && !textoBuscable(e).includes(q)) return false;
  if (prioridad.value !== 'todas' && e.prioridad !== prioridad.value) return false;
  if (circuloFiltro.value && contactoPorId(e.contactoID)?.circuloID !== circuloFiltro.value) return false;
  if (soloVencidas.value && !accionVencida(e)) return false;
  return true;
}

const columnas = computed(() =>
  etapas.map((etapa) => {
    const todas = entradas.value.filter((e) => e.etapa === etapa.key);
    return {
      ...etapa,
      total: todas.length,
      visibles: todas.filter(esVisible).sort((a, b) => a.posicion - b.posicion || a.id - b.id),
    };
  }),
);

// ---------- Arrastrar y soltar ----------

const dragging = ref<PipelineContacto | null>(null);
const dropTarget = ref<EtapaPipeline | null>(null);

async function onDrop(destino: EtapaPipeline) {
  const entrada = dragging.value;
  dropTarget.value = null;
  dragging.value = null;
  if (!entrada || entrada.etapa === destino) return;

  // Actualización optimista: se mueve ya y se deshace si el backend falla
  const original = { ...entrada };
  entrada.etapa = destino;
  try {
    const actualizada = await api.actualizarEnPipeline(entrada.id, {
      etapa: destino,
      prioridad: entrada.prioridad,
      siguienteAccion: entrada.siguienteAccion,
      siguienteAccionFecha: entrada.siguienteAccionFecha,
    });
    reemplazar(actualizada);
  } catch {
    Object.assign(entrada, original);
    mostrarAviso('No se pudo mover la tarjeta. Comprueba la conexión con el backend.');
  }
}

function reemplazar(actualizada: PipelineContacto) {
  const i = entradas.value.findIndex((e) => e.id === actualizada.id);
  if (i === -1) entradas.value.push(actualizada);
  else entradas.value[i] = actualizada;
}

// ---------- Añadir / editar / quitar ----------

const dialogo = ref<{ entrada: PipelineContacto | null; etapa: EtapaPipeline } | null>(null);
const guardando = ref(false);
const errorDialogo = ref<string | null>(null);

const disponibles = computed(() => {
  const enPipeline = new Set(entradas.value.map((e) => e.contactoID));
  return store.contactos
    .filter((c) => !enPipeline.has(c.id))
    .sort((a, b) => nombreCompleto(a, false).localeCompare(nombreCompleto(b, false), 'es'));
});

function abrirDialogo(entrada: PipelineContacto | null, etapa: EtapaPipeline = 'identificado') {
  errorDialogo.value = null;
  dialogo.value = { entrada, etapa };
}

async function guardar(datos: PeticionPipelineContacto) {
  if (!dialogo.value || pipelineId.value === null) return;
  guardando.value = true;
  errorDialogo.value = null;
  try {
    const resultado = dialogo.value.entrada
      ? await api.actualizarEnPipeline(dialogo.value.entrada.id, datos)
      : await api.anadirAlPipeline(pipelineId.value, datos);
    reemplazar(resultado);
    dialogo.value = null;
  } catch (e) {
    errorDialogo.value =
      e instanceof ApiError && e.status === 409
        ? 'Ese contacto ya está en este pipeline.'
        : e instanceof ApiError && e.status === 400
          ? 'Datos no válidos: revisa el contacto, la etapa y la prioridad.'
          : 'No se pudo guardar. Comprueba la conexión con el backend.';
  } finally {
    guardando.value = false;
  }
}

async function quitar(entrada: PipelineContacto) {
  guardando.value = true;
  errorDialogo.value = null;
  try {
    await api.quitarDelPipeline(entrada.id);
    entradas.value = entradas.value.filter((e) => e.id !== entrada.id);
    dialogo.value = null;
  } catch (e) {
    if (e instanceof ApiError && e.status === 404) {
      entradas.value = entradas.value.filter((x) => x.id !== entrada.id);
      dialogo.value = null;
    } else {
      errorDialogo.value = 'No se pudo quitar. Comprueba la conexión con el backend.';
    }
  } finally {
    guardando.value = false;
  }
}
</script>

<template>
  <div class="flex flex-col w-full">
    <!-- Cabecera y KPIs -->
    <div class="px-8 py-6 flex flex-col gap-6 bg-surface-container-lowest shadow-sm">
      <div class="flex flex-col lg:flex-row lg:items-center justify-between gap-4">
        <div class="flex flex-col gap-1">
          <div class="flex items-center gap-2 text-on-surface-variant font-label-sm text-label-sm tracking-wider uppercase">
            <span class="inline-block w-2 h-2 rounded-full" :class="pipeline?.activo ? 'bg-secondary' : 'bg-outline'"></span>
            <span>{{ pipeline?.activo ? 'Pipeline activo' : 'Pipeline inactivo' }}</span>
          </div>
          <div class="flex flex-wrap items-center gap-3">
            <h1 class="font-headline-md text-headline-md text-on-surface">{{ pipeline?.nombre ?? 'Pipeline Relacional' }}</h1>
            <select
              v-if="pipelines.length > 1"
              v-model="pipelineId"
              class="px-3 py-1.5 rounded-full bg-surface-container-high text-on-surface font-label-md text-label-md focus:outline-none cursor-pointer"
            >
              <option v-for="p in pipelines" :key="p.id" :value="p.id">{{ p.nombre }}</option>
            </select>
            <span
              v-if="pipeline?.objetivo"
              class="flex items-center gap-2 px-3 py-1.5 rounded-full bg-surface-container-high text-on-surface font-label-md text-label-md"
            >
              <span class="material-symbols-outlined text-base text-primary">target</span>
              <span>Objetivo: {{ pipeline.objetivo }}</span>
            </span>
          </div>
        </div>
        <button
          type="button"
          class="flex items-center gap-2 px-4 py-2 rounded-lg bg-primary text-on-primary font-label-lg text-label-lg hover:bg-primary-container transition-all shadow-md disabled:opacity-50 self-start lg:self-auto"
          :disabled="!pipeline"
          @click="abrirDialogo(null)"
        >
          <span class="material-symbols-outlined text-base">person_add</span>
          <span>Añadir al Pipeline</span>
        </button>
      </div>

      <div class="grid grid-cols-1 md:grid-cols-4 gap-4 pt-2">
        <div
          v-for="metric in metrics"
          :key="metric.label"
          class="flex items-center justify-between p-4 rounded-xl bg-surface-container-low transition-all hover:bg-surface-container"
        >
          <div class="flex flex-col">
            <span class="font-label-sm text-label-sm text-on-surface-variant uppercase tracking-wider">{{ metric.label }}</span>
            <span class="font-headline-lg text-headline-lg text-on-surface mt-1 leading-none">{{ metric.value }}</span>
            <span class="font-label-sm text-label-sm flex items-center gap-1 mt-1" :class="metric.detailClass">
              <span v-if="metric.detailIcon" class="material-symbols-outlined text-xs">{{ metric.detailIcon }}</span>
              {{ metric.detail }}
            </span>
          </div>
          <div class="w-12 h-12 rounded-xl flex items-center justify-center" :class="metric.iconClass">
            <span class="material-symbols-outlined text-2xl">{{ metric.icon }}</span>
          </div>
        </div>

        <!-- Progreso hacia alianza -->
        <div class="flex items-center justify-between p-4 rounded-xl bg-surface-container-low transition-all hover:bg-surface-container">
          <div class="flex flex-col justify-between h-full">
            <div>
              <span class="font-label-sm text-label-sm text-on-surface-variant uppercase tracking-wider">Conversión</span>
              <div class="flex items-baseline gap-2 mt-1">
                <span class="font-headline-md text-headline-md text-on-surface leading-none">{{ progreso }}%</span>
                <span class="font-label-sm text-label-sm text-secondary">a alianza</span>
              </div>
            </div>
            <span class="font-label-sm text-label-sm" :class="vencidas.length ? 'text-error' : 'text-on-surface-variant'">
              {{ vencidas.length ? `${vencidas.length} acciones vencidas` : 'Sin acciones vencidas' }}
            </span>
          </div>
          <div class="w-14 h-14 flex items-center justify-center relative">
            <svg class="w-14 h-14 -rotate-90" viewBox="0 0 36 36">
              <path
                class="text-surface-container-highest"
                d="M18 2.0845 a 15.9155 15.9155 0 0 1 0 31.831 a 15.9155 15.9155 0 0 1 0 -31.831"
                fill="none"
                stroke="currentColor"
                stroke-width="3.5"
              />
              <path
                class="text-secondary"
                d="M18 2.0845 a 15.9155 15.9155 0 0 1 0 31.831 a 15.9155 15.9155 0 0 1 0 -31.831"
                fill="none"
                stroke="currentColor"
                :stroke-dasharray="`${progreso}, 100`"
                stroke-linecap="round"
                stroke-width="3.5"
              />
            </svg>
            <span class="material-symbols-outlined absolute text-secondary text-sm">handshake</span>
          </div>
        </div>
      </div>
    </div>

    <!-- Barra de filtros -->
    <div class="px-8 py-3 bg-surface flex flex-wrap items-center justify-between gap-4">
      <div class="flex flex-wrap items-center gap-3">
        <div class="relative w-64">
          <span class="material-symbols-outlined absolute left-3 top-1/2 -translate-y-1/2 text-on-surface-variant text-base">
            search
          </span>
          <input
            v-model="search"
            type="text"
            placeholder="Filtrar por nombre, empresa o acción..."
            class="w-full pl-9 pr-3 py-1.5 bg-surface-container-lowest rounded-lg font-body-sm text-body-sm text-on-surface placeholder:text-outline focus:outline-none focus:ring-2 focus:ring-primary/20 shadow-sm"
          />
        </div>

        <div class="flex items-center bg-surface-container-lowest rounded-lg px-3 py-1.5 shadow-sm">
          <span class="material-symbols-outlined text-base text-on-surface-variant mr-2">hub</span>
          <span class="font-label-sm text-label-sm text-on-surface-variant mr-1">Círculo:</span>
          <select
            v-model.number="circuloFiltro"
            class="bg-transparent font-label-md text-label-md text-on-surface focus:outline-none cursor-pointer pr-2"
          >
            <option :value="0">Todos</option>
            <option v-for="c in store.circulos" :key="c.id" :value="c.id">{{ c.nombre }}</option>
          </select>
        </div>

        <div class="flex items-center bg-surface-container-lowest rounded-lg p-1 shadow-sm gap-1">
          <button
            type="button"
            class="px-2.5 py-1 rounded font-label-sm text-label-sm transition-colors"
            :class="prioridad === 'todas' ? 'bg-primary-container text-on-primary' : 'text-on-surface-variant hover:text-on-surface hover:bg-surface-container-high'"
            @click="prioridad = 'todas'"
          >
            Todas
          </button>
          <button
            v-for="(p, key) in prioridades"
            :key="key"
            type="button"
            class="px-2.5 py-1 rounded font-label-sm text-label-sm flex items-center gap-1 transition-colors"
            :class="prioridad === key ? 'bg-primary-container text-on-primary' : 'text-on-surface-variant hover:text-on-surface hover:bg-surface-container-high'"
            @click="prioridad = key"
          >
            <span class="w-1.5 h-1.5 rounded-full" :class="p.dot"></span> {{ p.label }}
          </button>
        </div>

        <button
          type="button"
          class="flex items-center gap-1 px-3 py-1.5 rounded-lg font-label-sm text-label-sm shadow-sm transition-colors"
          :class="soloVencidas ? 'bg-error text-on-error' : 'bg-surface-container-lowest text-on-surface-variant hover:text-on-surface'"
          @click="soloVencidas = !soloVencidas"
        >
          <span class="material-symbols-outlined text-base">notification_important</span>
          Solo vencidas
        </button>
      </div>
    </div>

    <!-- Estados -->
    <div v-if="error" class="mx-8 mt-2 p-4 rounded-xl bg-error-container text-on-error-container flex items-center justify-between gap-4">
      <span class="font-body-sm text-body-sm">No se pudo cargar el pipeline: {{ error }}</span>
      <button type="button" class="px-3 py-1.5 rounded-lg bg-error text-on-error font-label-md text-label-md" @click="cargarPipelines">
        Reintentar
      </button>
    </div>
    <div v-else-if="cargando" class="px-8 py-16 flex items-center justify-center gap-3 text-on-surface-variant">
      <span class="material-symbols-outlined animate-spin">progress_activity</span>
      <span class="font-body-md text-body-md">Cargando pipeline…</span>
    </div>
    <p v-else-if="!pipeline" class="px-8 py-16 text-center font-body-md text-body-md text-on-surface-variant">
      No hay ningún pipeline creado en la base de datos.
    </p>

    <!-- Tablero kanban -->
    <div v-else class="px-8 py-4 overflow-x-auto pb-10">
      <div class="flex gap-5 min-w-[1440px] items-start">
        <div
          v-for="col in columnas"
          :key="col.key"
          class="w-80 flex-shrink-0 flex flex-col gap-3 rounded-xl transition-colors"
          :class="{ 'bg-primary-fixed/40 outline-2 outline-dashed outline-primary/30': dropTarget === col.key }"
          @dragover.prevent="dropTarget = col.key"
          @dragleave.self="dropTarget = null"
          @drop.prevent="onDrop(col.key)"
        >
          <div class="flex items-center justify-between px-3 py-2.5 rounded-xl" :class="col.header">
            <div class="flex items-center gap-2">
              <span class="w-2.5 h-2.5 rounded-full" :class="col.dot"></span>
              <span class="font-label-lg text-label-lg text-on-surface">{{ col.titulo }}</span>
              <span class="px-2 py-0.5 rounded-full font-label-sm text-label-sm" :class="col.count">{{ col.total }}</span>
            </div>
            <button
              type="button"
              title="Añadir a esta etapa"
              class="p-1 text-on-surface-variant hover:text-on-surface rounded-lg"
              @click="abrirDialogo(null, col.key)"
            >
              <span class="material-symbols-outlined text-base">add</span>
            </button>
          </div>

          <div class="flex flex-col gap-3 min-h-24">
            <PipelineCard
              v-for="entrada in col.visibles"
              :key="entrada.id"
              :entrada="entrada"
              draggable="true"
              :class="{ 'opacity-40': dragging?.id === entrada.id }"
              @dragstart="dragging = entrada"
              @dragend="dragging = null; dropTarget = null"
              @editar="abrirDialogo($event)"
            />
            <p v-if="col.visibles.length === 0" class="py-6 text-center font-body-sm text-body-sm text-on-surface-variant">
              {{ col.total ? 'Ningún contacto coincide' : 'Sin contactos' }}
            </p>
          </div>
        </div>
      </div>
    </div>

    <!-- Sugerencia -->
    <div
      v-if="pipeline && vencidas.length"
      class="mx-8 mb-8 p-5 rounded-2xl bg-surface-container-lowest shadow-sm flex flex-col md:flex-row items-center justify-between gap-4"
    >
      <div class="flex items-center gap-4">
        <div class="w-10 h-10 shrink-0 rounded-xl bg-primary-fixed flex items-center justify-center text-primary">
          <span class="material-symbols-outlined text-xl">psychology</span>
        </div>
        <div class="flex flex-col">
          <span class="font-label-lg text-label-lg text-on-surface">Sugerencia del Asistente Relacional</span>
          <span class="font-body-sm text-body-sm text-on-surface-variant">
            Tienes {{ vencidas.length }} {{ vencidas.length === 1 ? 'acción vencida' : 'acciones vencidas' }} en este
            pipeline. Un mensaje breve reactivará la calidez del vínculo.
          </span>
        </div>
      </div>
      <button
        type="button"
        class="px-4 py-2 rounded-lg bg-primary text-on-primary font-label-md text-label-md hover:bg-primary-container transition-colors shadow-sm whitespace-nowrap"
        @click="soloVencidas = true"
      >
        Ver acciones vencidas
      </button>
    </div>

    <!-- Aviso flotante -->
    <div
      v-if="aviso"
      class="fixed bottom-6 right-6 z-[70] px-4 py-3 rounded-xl bg-inverse-surface text-inverse-on-surface font-body-sm text-body-sm shadow-lg"
    >
      {{ aviso }}
    </div>

    <PipelineEntryDialog
      v-if="dialogo"
      :entrada="dialogo.entrada"
      :etapa-inicial="dialogo.etapa"
      :contactos-disponibles="disponibles"
      :guardando="guardando"
      :error="errorDialogo"
      @guardar="guardar"
      @quitar="quitar"
      @abrir="(id) => { dialogo = null; $emit('abrir', id); }"
      @cerrar="dialogo = null"
    />
  </div>
</template>
