<script setup lang="ts">
import { computed, ref, watch } from 'vue';
import ContactRow from '@/components/contactos/ContactRow.vue';
import { store } from '@/api/store';
import { estadoProximoPaso, lista, nombreCompleto, parseFecha, type EstadoProximoPaso } from '@/api/format';

defineEmits<{ abrir: [id: number] }>();

interface Metric {
  label: string;
  value: number;
  valueClass: string;
  detail: string;
  detailIcon?: string;
  detailClass: string;
  icon: string;
  iconClass: string;
}

const total = computed(() => store.contactos.length);
const pct = (n: number) => (total.value ? Math.round((n / total.value) * 100) : 0);
const contarEstado = (estado: EstadoProximoPaso) =>
  store.contactos.filter((c) => estadoProximoPaso(c) === estado).length;
const conCadencia = computed(() => pct(store.contactos.filter((c) => c.cadenciaDias > 0).length));

const metrics = computed<Metric[]>(() => {
  const conCirculo = store.contactos.filter((c) => c.circuloID > 0).length;
  const prioritarios = store.contactos.filter((c) => c.prioritario).length;
  return [
    {
      label: 'Organizados',
      value: conCirculo,
      valueClass: 'text-on-surface',
      detail: `${pct(conCirculo)}% con círculo`,
      detailIcon: 'trending_up',
      detailClass: 'text-secondary',
      icon: 'sentiment_satisfied',
      iconClass: 'bg-secondary-container/40 text-secondary',
    },
    {
      label: 'Pasos Vencidos',
      value: contarEstado('vencido'),
      valueClass: 'text-tertiary',
      detail: 'Requieren toque',
      detailIcon: 'schedule',
      detailClass: 'text-tertiary-container',
      icon: 'priority_high',
      iconClass: 'bg-tertiary-fixed text-tertiary',
    },
    {
      label: 'Esta Semana',
      value: contarEstado('pronto'),
      valueClass: 'text-primary',
      detail: 'próximos pasos en 7 días',
      detailClass: 'text-on-surface-variant',
      icon: 'event_upcoming',
      iconClass: 'bg-primary-fixed text-primary-container',
    },
    {
      label: 'Prioritarios',
      value: prioritarios,
      valueClass: 'text-on-surface',
      detail: `${pct(prioritarios)}% de tu red`,
      detailClass: 'text-secondary',
      icon: 'diversity_1',
      iconClass: 'bg-surface-container-high text-on-surface',
    },
  ];
});

// Píldoras de círculo: "Todos", uno por círculo y "Sin círculo" si hace falta
const circleFilters = computed(() => {
  const filtros = [
    { key: 0, label: 'Todos', count: total.value },
    ...store.circulos.map((c) => ({
      key: c.id,
      label: c.nombre,
      count: store.contactos.filter((x) => x.circuloID === c.id).length,
    })),
  ];
  const sinCirculo = store.contactos.filter((c) => c.circuloID === 0).length;
  if (sinCirculo) filtros.push({ key: -1, label: 'Sin círculo', count: sinCirculo });
  return filtros;
});

const ciudades = computed(() =>
  [...new Set(store.contactos.map((c) => c.ciudad).filter((c): c is string => !!c))].sort((a, b) =>
    a.localeCompare(b, 'es'),
  ),
);

// Filtros
const activeCircle = ref(0);
const search = ref('');
const estadoFilter = ref<'all' | EstadoProximoPaso>('all');
const ciudad = ref('all');
const sortBy = ref<'urgent' | 'alpha' | 'created'>('urgent');
const rowsPerPage = ref(10);
const page = ref(1);

const fechaPaso = (fecha: string | null) => parseFecha(fecha)?.getTime() ?? Number.POSITIVE_INFINITY;

const filteredContacts = computed(() => {
  const q = search.value.trim().toLowerCase();

  const resultado = store.contactos.filter((c) => {
    if (activeCircle.value === -1 && c.circuloID !== 0) return false;
    if (activeCircle.value > 0 && c.circuloID !== activeCircle.value) return false;
    if (estadoFilter.value !== 'all' && estadoProximoPaso(c) !== estadoFilter.value) return false;
    if (ciudad.value !== 'all' && c.ciudad !== ciudad.value) return false;
    if (!q) return true;
    return [nombreCompleto(c), c.empresa, c.cargo, c.ciudad, c.email, ...lista(c.intereses)]
      .filter(Boolean)
      .some((campo) => campo!.toLowerCase().includes(q));
  });

  if (sortBy.value === 'urgent') return resultado.sort((a, b) => fechaPaso(a.proximoPasoFecha) - fechaPaso(b.proximoPasoFecha));
  if (sortBy.value === 'alpha') return resultado.sort((a, b) => nombreCompleto(a, false).localeCompare(nombreCompleto(b, false), 'es'));
  return resultado.sort((a, b) => (parseFecha(b.creadoEn)?.getTime() ?? 0) - (parseFecha(a.creadoEn)?.getTime() ?? 0));
});

// Paginación
const totalPages = computed(() => Math.max(1, Math.ceil(filteredContacts.value.length / rowsPerPage.value)));
const pageContacts = computed(() =>
  filteredContacts.value.slice((page.value - 1) * rowsPerPage.value, page.value * rowsPerPage.value),
);
const rangeStart = computed(() => (filteredContacts.value.length ? (page.value - 1) * rowsPerPage.value + 1 : 0));
const rangeEnd = computed(() => Math.min(page.value * rowsPerPage.value, filteredContacts.value.length));

watch([activeCircle, search, estadoFilter, ciudad, sortBy, rowsPerPage], () => (page.value = 1));

// Selección
const selected = ref<Record<number, boolean>>({});

const allSelected = computed({
  get: () => pageContacts.value.length > 0 && pageContacts.value.every((c) => selected.value[c.id]),
  set: (value: boolean) => {
    for (const c of pageContacts.value) selected.value[c.id] = value;
  },
});

const selectClass =
  'appearance-none bg-surface-container-low hover:bg-surface-container text-on-surface font-label-md text-label-md py-2.5 pl-3.5 pr-9 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary/20 transition-all cursor-pointer';
</script>

<template>
  <div class="px-8 py-8 flex flex-col gap-8 max-w-[1600px] mx-auto w-full">
    <!-- Cabecera -->
    <section class="flex flex-col md:flex-row md:items-end justify-between gap-6 pb-2">
      <div class="flex flex-col gap-2">
        <div class="flex items-center gap-3">
          <span
            class="px-2.5 py-0.5 rounded-full bg-primary-fixed text-on-primary-fixed font-label-sm text-label-sm uppercase tracking-wider"
          >
            Gestión Activa
          </span>
          <span class="flex items-center gap-1.5 text-on-surface-variant font-body-sm text-body-sm">
            <span class="w-2 h-2 rounded-full bg-secondary"></span> {{ conCadencia }}% con cadencia definida
          </span>
        </div>
        <div class="flex items-baseline gap-4">
          <h1 class="font-headline-lg text-headline-lg text-on-surface tracking-tight">Directorio de Relaciones</h1>
          <span class="font-label-lg text-label-lg px-3 py-1 rounded-full bg-surface-container-high text-on-surface-variant">
            {{ total }} contactos registrados
          </span>
        </div>
        <p class="font-body-md text-body-md text-on-surface-variant max-w-2xl">
          Cultiva conexiones humanas a largo plazo con intención, ritmos periódicos y contexto enriquecido.
        </p>
      </div>
      <div class="flex items-center gap-3 self-start md:self-auto">
        <button
          type="button"
          class="flex items-center gap-2 px-4 py-2.5 bg-surface-container-lowest text-on-surface rounded-lg font-label-lg text-label-lg hover:bg-surface-container transition-all shadow-sm"
        >
          <span class="material-symbols-outlined text-lg text-primary">cloud_download</span>
          <span>Importar (Google / LinkedIn)</span>
        </button>
        <button
          type="button"
          class="flex items-center gap-2 px-5 py-2.5 bg-primary-container text-on-primary rounded-lg font-label-lg text-label-lg hover:bg-primary transition-all shadow-sm"
        >
          <span class="material-symbols-outlined text-lg">person_add</span>
          <span>Añadir Contacto</span>
        </button>
      </div>
    </section>

    <!-- Métricas -->
    <section class="grid grid-cols-1 sm:grid-cols-2 xl:grid-cols-4 gap-4">
      <div
        v-for="metric in metrics"
        :key="metric.label"
        class="p-5 rounded-xl bg-surface-container-lowest shadow-sm flex items-center justify-between"
      >
        <div class="flex flex-col">
          <span class="font-label-sm text-label-sm text-on-surface-variant uppercase tracking-wider">{{ metric.label }}</span>
          <span class="font-headline-md text-headline-md mt-1" :class="metric.valueClass">{{ metric.value }}</span>
          <span class="font-body-sm text-body-sm flex items-center gap-1 mt-1" :class="metric.detailClass">
            <span v-if="metric.detailIcon" class="material-symbols-outlined text-sm">{{ metric.detailIcon }}</span>
            {{ metric.detail }}
          </span>
        </div>
        <div class="w-12 h-12 rounded-full flex items-center justify-center" :class="metric.iconClass">
          <span class="material-symbols-outlined text-2xl">{{ metric.icon }}</span>
        </div>
      </div>
    </section>

    <!-- Filtros -->
    <section class="flex flex-col gap-4">
      <div class="flex items-center gap-2 overflow-x-auto pb-1">
        <button
          v-for="filter in circleFilters"
          :key="filter.key"
          type="button"
          class="px-4 py-2 rounded-full font-label-lg text-label-lg flex items-center gap-2 shrink-0 shadow-sm transition-all"
          :class="
            activeCircle === filter.key
              ? 'bg-primary-container text-on-primary'
              : 'bg-surface-container-lowest text-on-surface-variant hover:text-on-surface hover:bg-surface-container-high'
          "
          @click="activeCircle = filter.key"
        >
          <span>{{ filter.label }}</span>
          <span
            class="px-1.5 rounded-full font-label-sm text-label-sm"
            :class="activeCircle === filter.key ? 'bg-on-primary/20 text-on-primary' : 'bg-surface-container text-on-surface-variant'"
          >
            {{ filter.count }}
          </span>
        </button>
      </div>

      <div
        class="flex flex-col lg:flex-row items-stretch lg:items-center justify-between gap-3 bg-surface-container-lowest p-3 rounded-xl shadow-sm"
      >
        <div class="relative flex-1">
          <span class="material-symbols-outlined absolute left-3.5 top-1/2 -translate-y-1/2 text-on-surface-variant text-xl">
            search
          </span>
          <input
            v-model="search"
            type="text"
            placeholder="Buscar por nombre, empresa, interés o ciudad..."
            class="w-full pl-11 pr-4 py-2.5 bg-surface-container-low rounded-lg font-body-sm text-body-sm text-on-surface placeholder:text-outline focus:outline-none focus:ring-2 focus:ring-primary/20 transition-all"
          />
        </div>

        <div class="flex items-center flex-wrap sm:flex-nowrap gap-2.5">
          <div class="relative">
            <select v-model="estadoFilter" :class="selectClass">
              <option value="all">Próximo paso: Todos</option>
              <option value="vencido">⚠️ Vencido</option>
              <option value="pronto">⚡ En los próximos 7 días</option>
              <option value="programado">🟢 Programado más adelante</option>
              <option value="sin-fecha">Sin próximo paso</option>
            </select>
            <span
              class="material-symbols-outlined absolute right-2.5 top-1/2 -translate-y-1/2 text-on-surface-variant pointer-events-none text-base"
            >
              expand_more
            </span>
          </div>
          <div class="relative">
            <select v-model="ciudad" :class="selectClass">
              <option value="all">Ubicación: Todas</option>
              <option v-for="nombre in ciudades" :key="nombre" :value="nombre">{{ nombre }}</option>
            </select>
            <span
              class="material-symbols-outlined absolute right-2.5 top-1/2 -translate-y-1/2 text-on-surface-variant pointer-events-none text-base"
            >
              expand_more
            </span>
          </div>
          <div class="relative">
            <select v-model="sortBy" :class="selectClass">
              <option value="urgent">Ordenar: Próximo paso</option>
              <option value="alpha">Nombre (A-Z)</option>
              <option value="created">Fecha de adición</option>
            </select>
            <span
              class="material-symbols-outlined absolute right-2.5 top-1/2 -translate-y-1/2 text-on-surface-variant pointer-events-none text-base"
            >
              sort
            </span>
          </div>
        </div>
      </div>
    </section>

    <!-- Tabla de contactos -->
    <div class="w-full bg-surface-container-lowest rounded-xl shadow-sm overflow-hidden flex flex-col">
      <div class="overflow-x-auto w-full">
        <table class="w-full text-left border-collapse">
          <thead>
            <tr class="bg-surface-container-low/70 text-on-surface-variant font-label-md text-label-md uppercase tracking-wider">
              <th class="py-3.5 px-6 font-semibold w-12 text-center">
                <input v-model="allSelected" type="checkbox" class="w-4 h-4 rounded cursor-pointer accent-primary-container" />
              </th>
              <th class="py-3.5 px-4 font-semibold min-w-[280px]">Nombre &amp; Intereses</th>
              <th class="py-3.5 px-4 font-semibold min-w-[150px]">Círculo</th>
              <th class="py-3.5 px-4 font-semibold min-w-[200px]">Empresa &amp; Rol</th>
              <th class="py-3.5 px-4 font-semibold min-w-[140px]">Cadencia</th>
              <th class="py-3.5 px-4 font-semibold min-w-[200px]">Canal Preferido</th>
              <th class="py-3.5 px-4 font-semibold min-w-[190px]">Próximo Paso</th>
              <th class="py-3.5 px-6 font-semibold text-right min-w-[140px]">Acciones</th>
            </tr>
          </thead>
          <tbody class="text-on-surface font-body-sm text-body-sm">
            <ContactRow
              v-for="(contacto, i) in pageContacts"
              :key="contacto.id"
              v-model:selected="selected[contacto.id]"
              :contacto="contacto"
              :striped="i % 2 === 1"
              @abrir="$emit('abrir', $event)"
            />
            <tr v-if="pageContacts.length === 0">
              <td colspan="8" class="py-10 text-center font-body-sm text-body-sm text-on-surface-variant">
                {{ total ? 'No hay contactos que coincidan con los filtros.' : 'Todavía no hay contactos.' }}
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- Paginación -->
      <div class="p-4 bg-surface-container-lowest flex flex-col sm:flex-row items-center justify-between gap-4">
        <div class="flex items-center gap-3">
          <span class="font-body-sm text-body-sm text-on-surface-variant">
            Mostrando <span class="font-semibold text-on-surface">{{ rangeStart }} - {{ rangeEnd }}</span> de
            <span class="font-semibold text-on-surface">{{ filteredContacts.length }}</span> contactos
          </span>
          <div class="h-4 w-px bg-surface-container-high hidden sm:block"></div>
          <div class="flex items-center gap-2">
            <span class="font-label-sm text-label-sm text-on-surface-variant">Filas por página:</span>
            <select
              v-model.number="rowsPerPage"
              class="bg-surface-container-low text-on-surface font-label-sm text-label-sm py-1 px-2 rounded focus:outline-none cursor-pointer"
            >
              <option :value="10">10</option>
              <option :value="25">25</option>
              <option :value="50">50</option>
            </select>
          </div>
        </div>

        <div class="flex items-center gap-1.5">
          <button
            type="button"
            :disabled="page === 1"
            class="p-2 rounded-lg text-on-surface hover:bg-surface-container disabled:opacity-40 disabled:hover:bg-transparent transition-colors"
            @click="page = 1"
          >
            <span class="material-symbols-outlined text-lg">first_page</span>
          </button>
          <button
            type="button"
            :disabled="page === 1"
            class="p-2 rounded-lg text-on-surface hover:bg-surface-container disabled:opacity-40 disabled:hover:bg-transparent transition-colors"
            @click="page--"
          >
            <span class="material-symbols-outlined text-lg">chevron_left</span>
          </button>
          <div class="flex items-center gap-1">
            <button
              v-for="n in totalPages"
              :key="n"
              type="button"
              class="w-8 h-8 rounded-lg font-label-md text-label-md flex items-center justify-center transition-colors"
              :class="
                n === page
                  ? 'bg-primary-container text-on-primary font-semibold'
                  : 'text-on-surface-variant hover:bg-surface-container hover:text-on-surface'
              "
              @click="page = n"
            >
              {{ n }}
            </button>
          </div>
          <button
            type="button"
            :disabled="page === totalPages"
            class="p-2 rounded-lg text-on-surface hover:bg-surface-container disabled:opacity-40 disabled:hover:bg-transparent transition-colors"
            @click="page++"
          >
            <span class="material-symbols-outlined text-lg">chevron_right</span>
          </button>
          <button
            type="button"
            :disabled="page === totalPages"
            class="p-2 rounded-lg text-on-surface hover:bg-surface-container disabled:opacity-40 disabled:hover:bg-transparent transition-colors"
            @click="page = totalPages"
          >
            <span class="material-symbols-outlined text-lg">last_page</span>
          </button>
        </div>
      </div>
    </div>
  </div>
</template>
