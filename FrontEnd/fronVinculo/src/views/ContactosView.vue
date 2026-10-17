<script setup lang="ts">
import { computed, ref } from 'vue';
import ContactRow, { type DirectoryContact } from '@/components/contactos/ContactRow.vue';

interface Metric {
  label: string;
  value: string;
  valueClass: string;
  detail: string;
  detailIcon?: string;
  detailClass: string;
  icon: string;
  iconClass: string;
}

interface CircleFilter {
  key: string;
  label: string;
  count: number;
}

const metrics: Metric[] = [
  {
    label: 'Al día',
    value: '182',
    valueClass: 'text-on-surface',
    detail: '73% del directorio',
    detailIcon: 'trending_up',
    detailClass: 'text-secondary',
    icon: 'sentiment_satisfied',
    iconClass: 'bg-secondary-container/40 text-secondary',
  },
  {
    label: 'En Riesgo (>30d)',
    value: '28',
    valueClass: 'text-tertiary',
    detail: 'Requieren toque',
    detailIcon: 'schedule',
    detailClass: 'text-tertiary-container',
    icon: 'priority_high',
    iconClass: 'bg-tertiary-fixed text-tertiary',
  },
  {
    label: 'Acciones Hoy',
    value: '7',
    valueClass: 'text-primary',
    detail: '3 llamadas, 4 cafés',
    detailClass: 'text-on-surface-variant',
    icon: 'event_upcoming',
    iconClass: 'bg-primary-fixed text-primary-container',
  },
  {
    label: 'Círculo Íntimo',
    value: '15',
    valueClass: 'text-on-surface',
    detail: 'Cadencia semanal activa',
    detailClass: 'text-secondary',
    icon: 'diversity_1',
    iconClass: 'bg-surface-container-high text-on-surface',
  },
];

const circleFilters: CircleFilter[] = [
  { key: 'todos', label: 'Todos', count: 248 },
  { key: 'intimo', label: 'Círculo Íntimo', count: 15 },
  { key: 'mentores', label: 'Mentores', count: 8 },
  { key: 'clientes', label: 'Clientes & Leads', count: 42 },
  { key: 'networking', label: 'Networking', count: 120 },
  { key: 'inversores', label: 'Inversores', count: 14 },
];

const contacts: DirectoryContact[] = [
  {
    name: 'Sofía Valdés',
    avatar:
      'https://lh3.googleusercontent.com/aida-public/AB6AXuCwbNTjw4DrYvGNGD3gzyMj3ilMTmKVp-wSb4JI9F84rAekg24_dOx2mpHBEKkxEcD7GBEwBYOlW3IGkR5nFXKzueK7ID5QmOy32AWeuNtgJYrLSJtG3sqx4WbU8-q2th08awJBuv3j9MwvsWjtZIfggF4pKOzVb3IPg6qhDh_Zkj3vUHRQw3ognHYTml_WpqBVfnPZbCh9hZszfrt_DGsJvgVQ5wpZ6pHu78spxSsWKHM2vyHO2FNc',
    birthday: true,
    statusClass: 'bg-error',
    statusTitle: 'Cadencia vencida',
    interests: ['Diseño UI', 'Vino', 'Maratones'],
    group: 'clientes',
    circle: 'Cliente VIP',
    circleClass: 'bg-primary-fixed text-on-primary-fixed',
    circleDotClass: 'bg-primary',
    role: 'VP of Product',
    company: 'Novatech LatAm',
    cadence: 'Cada 2 semanas',
    daysSince: 38,
    health: 'risk',
    lastWhen: 'Hace 38 días',
    lastWhenClass: 'text-error',
    lastWhat: 'Llamada breve de alineación',
    healthDotClass: 'bg-error',
    healthTitle: 'En riesgo (>30 días)',
    nextIcon: 'restaurant',
    nextIconClass: 'text-primary',
    nextTitle: 'Almuerzo 24 Mar',
    nextDetail: 'Confirmar restaurante',
  },
  {
    name: 'Mateo Arismendi',
    avatar:
      'https://lh3.googleusercontent.com/aida-public/AB6AXuA2V2a32MRhtejzJFcJ26VUJUJH-xBXNdsWSlnZE08rPsv6cPtVRlc_2XJX03SzU8T4U0k0PQ4QTkjiWwCVtySCBOhzqoAEBq8eJniu60DKHHoLFyNGXSKZ20lBg20eHKD9sEVnHgYcjO_YpwrqE9dctZNsk6XqGKTpv-fOM6Jv6Xq7oQIuvs_A9X2maMvv18hPVeKYLP7GDPpH-lTpeL-G7itZzFki9oNyPm3YmT--N6MGC6K0rLsQ',
    statusClass: 'bg-secondary',
    statusTitle: 'Relación activa',
    interests: ['Angel Investing', 'SaaS B2B'],
    group: 'mentores',
    circle: 'Mentor',
    circleClass: 'bg-tertiary-fixed text-on-tertiary-fixed',
    circleDotClass: 'bg-tertiary',
    role: 'Board Advisor',
    company: 'Kite Ventures',
    cadence: 'Mensual',
    daysSince: 4,
    health: 'good',
    lastWhen: 'Hace 4 días',
    lastWhenClass: 'text-on-surface',
    lastWhat: 'Café presencial (Revisión Q1)',
    healthDotClass: 'bg-secondary',
    healthTitle: 'Al día',
    nextIcon: 'send',
    nextIconClass: 'text-secondary',
    nextTitle: 'Enviar informe',
    nextDetail: 'Métricas de retención',
  },
  {
    name: 'Lucía Morales',
    avatar:
      'https://lh3.googleusercontent.com/aida-public/AB6AXuD4kt-DzZ9YUDSy02gnOpa8rS_NI306NNUJ8An2bmpD_4q2Rl1zuQdgHhO4K35escPoHsrwGl3tFnF_cORHsMnfVSvFRZSaHTHby1-rGCFXUGTF_hbQ9FQUKZxIisxJSTT7vz2Y7aCeRem4hyg_BspPlrYba6mVpGO9D3Vu4DyzEhUXzuDSRi3ICFcW94U3qoi6JGzZ6JRh6n4STh-qdRph5BR_RhTDMCdqM77OkfKLZE5WyX3uHe1I',
    statusClass: 'bg-secondary',
    statusTitle: 'Relación activa',
    interests: ['Arquitectura', 'Fotografía', 'Senderismo'],
    group: 'intimo',
    circle: 'Amigo Íntimo',
    circleClass: 'bg-secondary-fixed text-on-secondary-fixed',
    circleDotClass: 'bg-secondary',
    role: 'Lead Architect',
    company: 'Estudio Diagonal',
    cadence: 'Semanal',
    daysSince: 1,
    health: 'good',
    lastWhen: 'Ayer',
    lastWhenClass: 'text-on-surface',
    lastWhat: 'Audio de WhatsApp (Paseo perro)',
    healthDotClass: 'bg-secondary',
    healthTitle: 'Al día',
    nextIcon: 'directions_walk',
    nextIconClass: 'text-primary',
    nextTitle: 'Escapada Sierra',
    nextDetail: 'Sábado 29',
  },
  {
    name: 'Carlos Mendizábal',
    avatar:
      'https://lh3.googleusercontent.com/aida-public/AB6AXuATh3DTagvdqFto85fr8Ztr0ANHa6kw5u_J9HTRrwuaWpYfQkYic277SKJycacdd6tlX-74tTWSp6Nv7C1UXGtP4OF8MEW8aF-S757DKn_Tr76EsdvxRUP_3QjHGrqwyDxIkUu1LLVsBuMyViWjgauYfWjdgiRiFRAQ2cKlWaxFYaVe61yrDk9iQv0QSMhwAKbhVSAN37uWyovrbhUHL_7T5HHGayCI6KJh7EwmrihhH4Dz1rLABH_p',
    statusClass: 'bg-tertiary-container',
    statusTitle: 'Por vencer',
    interests: ['Fintech', 'Series A'],
    group: 'inversores',
    circle: 'Inversor',
    circleClass: 'bg-surface-container-high text-on-surface',
    circleDotClass: 'bg-on-surface-variant',
    role: 'Managing Partner',
    company: 'Iberia Seed Capital',
    cadence: 'Bimestral',
    daysSince: 26,
    health: 'warning',
    lastWhen: 'Hace 26 días',
    lastWhenClass: 'text-tertiary-container',
    lastWhat: 'Actualización trimestral (Email)',
    healthDotClass: 'bg-tertiary-container',
    healthTitle: 'Atención requerida pronto',
    nextIcon: 'celebration',
    nextIconClass: 'text-primary-container',
    nextTitle: 'Felicitar por ascenso',
    nextDetail: 'Nuevo fondo lanzado',
  },
  {
    name: 'Elena Chen',
    avatar:
      'https://lh3.googleusercontent.com/aida-public/AB6AXuC7U4lTJWm4ahEr9SY-X6r1Kem-nrHuZvL9RswowsqLcrzLVZogXgV5Gu9W4uWGCGr6UzFVKi4wDRBsMxWPKB5nh_gcKvZ0jfimmFcuU_EUczVn9pySptlpvx7A192r_Jug1B71F8wpAg4PbkOBiGQJLsua5GsVgK-Nj_gwiT2K5-A8SNRcGzLpup0SYVr3lcPTJv4tdB26jENCWjfBTVPx_rkoS36UCew-QZNSY6Er1XO5LEOdiIsX',
    statusClass: 'bg-secondary',
    statusTitle: 'Relación activa',
    interests: ['Inteligencia Artificial', 'Podcast'],
    group: 'networking',
    circle: 'Networking',
    circleClass: 'bg-primary-fixed-dim/40 text-on-primary-fixed-variant',
    circleDotClass: 'bg-primary-container',
    role: 'Founder & CEO',
    company: 'Synthetix Labs',
    cadence: 'Trimestral',
    daysSince: 12,
    health: 'good',
    lastWhen: 'Hace 12 días',
    lastWhenClass: 'text-on-surface',
    lastWhat: 'Mensaje en LinkedIn (Conferencia)',
    healthDotClass: 'bg-secondary',
    healthTitle: 'Al día',
    nextIcon: 'mic',
    nextIconClass: 'text-on-surface-variant',
    nextTitle: 'Invitación a podcast',
    nextDetail: 'Episodio de Mayo',
  },
  {
    name: 'Dr. Javier Roca',
    avatar:
      'https://lh3.googleusercontent.com/aida-public/AB6AXuCYQQ0B7zcLDpKB8CukdkK0PXppS2Omb_I3JLbkbCnrPszb27_0VyEr5cng39Qoj6807AMdiz5rqk-QLaJtPuQKyjOQNozemlAJJntyMmbS19C80-XdPCOlLvCyKbf54PrRGQvO_hABZrjhBnXbL9gUGl4ubAZVMDUwYnUwRRaaMm6lWNAXjr0UbMDlte88hLpDuZwN32RqFqRF49cI0o_IcWr2Gmn5DKyEXsimSN0Y1nX7lKaV_Ihe',
    statusClass: 'bg-error',
    statusTitle: 'Cadencia muy retrasada',
    interests: ['Filosofía', 'Bioética'],
    group: 'mentores',
    circle: 'Mentor',
    circleClass: 'bg-tertiary-fixed text-on-tertiary-fixed',
    circleDotClass: 'bg-tertiary',
    role: 'Catedrático Emérito',
    company: 'Universidad Complutense',
    cadence: 'Cada 6 semanas',
    daysSince: 60,
    health: 'risk',
    lastWhen: 'Hace 2 meses',
    lastWhenClass: 'text-error',
    lastWhat: 'Email sobre su último libro',
    healthDotClass: 'bg-error',
    healthTitle: 'En riesgo',
    nextIcon: 'call',
    nextIconClass: 'text-error',
    nextTitle: 'Llamada de puesta al día',
    nextDetail: 'Preguntar por su salud',
  },
];

// Filtros
const activeCircle = ref('todos');
const search = ref('');
const healthFilter = ref('all');
const location = ref('all');
const sortBy = ref('urgent');
const viewMode = ref<'tabla' | 'cuadricula'>('tabla');
const rowsPerPage = ref(10);

const filteredContacts = computed(() => {
  const q = search.value.trim().toLowerCase();

  const list = contacts.filter((c) => {
    if (activeCircle.value !== 'todos' && c.group !== activeCircle.value) return false;
    if (healthFilter.value === 'risk' && c.health !== 'risk') return false;
    if (healthFilter.value === 'good' && c.health !== 'good') return false;
    if (healthFilter.value === 'recent' && c.daysSince >= 7) return false;
    if (!q) return true;
    return [c.name, c.company, c.role, c.circle, ...c.interests].some((field) => field.toLowerCase().includes(q));
  });

  if (sortBy.value === 'urgent') return [...list].sort((a, b) => b.daysSince - a.daysSince);
  if (sortBy.value === 'alpha') return [...list].sort((a, b) => a.name.localeCompare(b.name, 'es'));
  if (sortBy.value === 'recent') return [...list].sort((a, b) => a.daysSince - b.daysSince);
  return list;
});

// Selección
const selected = ref<Record<string, boolean>>({});

const allSelected = computed({
  get: () => filteredContacts.value.length > 0 && filteredContacts.value.every((c) => selected.value[c.name]),
  set: (value: boolean) => {
    for (const c of filteredContacts.value) selected.value[c.name] = value;
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
            <span class="w-2 h-2 rounded-full bg-secondary"></span> 98% con cadencia definida
          </span>
        </div>
        <div class="flex items-baseline gap-4">
          <h1 class="font-headline-lg text-headline-lg text-on-surface tracking-tight">Directorio de Relaciones</h1>
          <span class="font-label-lg text-label-lg px-3 py-1 rounded-full bg-surface-container-high text-on-surface-variant">
            248 contactos registrados
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
      <div class="flex items-center justify-between overflow-x-auto pb-1 gap-2">
        <div class="flex items-center gap-2">
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
              :class="
                activeCircle === filter.key
                  ? 'bg-on-primary/20 text-on-primary'
                  : 'bg-surface-container text-on-surface-variant'
              "
            >
              {{ filter.count }}
            </span>
          </button>
        </div>

        <div class="hidden lg:flex items-center gap-1 bg-surface-container-lowest p-1 rounded-lg shadow-sm shrink-0">
          <button
            type="button"
            title="Vista Tabla"
            class="px-3 py-1.5 rounded-md flex items-center gap-1.5 font-label-sm text-label-sm transition-all"
            :class="
              viewMode === 'tabla'
                ? 'bg-primary-container text-on-primary shadow-sm'
                : 'text-on-surface-variant hover:text-on-surface hover:bg-surface-container'
            "
            @click="viewMode = 'tabla'"
          >
            <span class="material-symbols-outlined text-base">table_rows</span>
            <span>Tabla</span>
          </button>
          <button
            type="button"
            title="Vista Cuadrícula"
            class="px-3 py-1.5 rounded-md flex items-center gap-1.5 font-label-sm text-label-sm transition-all"
            :class="
              viewMode === 'cuadricula'
                ? 'bg-primary-container text-on-primary shadow-sm'
                : 'text-on-surface-variant hover:text-on-surface hover:bg-surface-container'
            "
            @click="viewMode = 'cuadricula'"
          >
            <span class="material-symbols-outlined text-base">grid_view</span>
            <span>Cuadrícula</span>
          </button>
        </div>
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
            placeholder="Buscar por nombre, empresa, etiqueta o ciudad..."
            class="w-full pl-11 pr-24 py-2.5 bg-surface-container-low rounded-lg font-body-sm text-body-sm text-on-surface placeholder:text-outline focus:outline-none focus:ring-2 focus:ring-primary/20 transition-all"
          />
          <div
            class="absolute right-3 top-1/2 -translate-y-1/2 flex items-center gap-1 text-on-surface-variant font-label-sm text-label-sm bg-surface-container-highest px-2 py-0.5 rounded"
          >
            <span>Filtro rápido</span>
          </div>
        </div>

        <div class="flex items-center flex-wrap sm:flex-nowrap gap-2.5">
          <div class="relative">
            <select v-model="healthFilter" :class="selectClass">
              <option value="all">Salud de relación: Todas</option>
              <option value="risk">⚠️ En riesgo (&gt;30 días)</option>
              <option value="good">🟢 Al día con cadencia</option>
              <option value="recent">⚡ Reciente (&lt;7 días)</option>
            </select>
            <span
              class="material-symbols-outlined absolute right-2.5 top-1/2 -translate-y-1/2 text-on-surface-variant pointer-events-none text-base"
            >
              expand_more
            </span>
          </div>
          <div class="relative">
            <select v-model="location" :class="selectClass">
              <option value="all">Ubicación: Todas</option>
              <option value="madrid">Madrid</option>
              <option value="barcelona">Barcelona</option>
              <option value="cdmx">Ciudad de México</option>
              <option value="remoto">Remoto / Global</option>
            </select>
            <span
              class="material-symbols-outlined absolute right-2.5 top-1/2 -translate-y-1/2 text-on-surface-variant pointer-events-none text-base"
            >
              expand_more
            </span>
          </div>
          <div class="relative">
            <select v-model="sortBy" :class="selectClass">
              <option value="urgent">Ordenar: Más urgente</option>
              <option value="alpha">Nombre (A-Z)</option>
              <option value="recent">Último contacto</option>
              <option value="created">Fecha de adición</option>
            </select>
            <span
              class="material-symbols-outlined absolute right-2.5 top-1/2 -translate-y-1/2 text-on-surface-variant pointer-events-none text-base"
            >
              sort
            </span>
          </div>
          <button
            type="button"
            title="Más filtros"
            class="p-2.5 bg-surface-container-low hover:bg-surface-container text-on-surface-variant hover:text-on-surface rounded-lg transition-all flex items-center justify-center"
          >
            <span class="material-symbols-outlined text-lg">tune</span>
          </button>
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
              <th class="py-3.5 px-4 font-semibold min-w-[220px]">Última Interacción</th>
              <th class="py-3.5 px-4 font-semibold min-w-[190px]">Próxima Acción</th>
              <th class="py-3.5 px-6 font-semibold text-right min-w-[140px]">Acciones</th>
            </tr>
          </thead>
          <tbody class="text-on-surface font-body-sm text-body-sm">
            <ContactRow
              v-for="(contact, i) in filteredContacts"
              :key="contact.name"
              v-model:selected="selected[contact.name]"
              :contact="contact"
              :striped="i % 2 === 1"
            />
            <tr v-if="filteredContacts.length === 0">
              <td colspan="8" class="py-10 text-center font-body-sm text-body-sm text-on-surface-variant">
                No hay contactos que coincidan con los filtros.
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- Paginación -->
      <div class="p-4 bg-surface-container-lowest flex flex-col sm:flex-row items-center justify-between gap-4">
        <div class="flex items-center gap-3">
          <span class="font-body-sm text-body-sm text-on-surface-variant">
            Mostrando
            <span class="font-semibold text-on-surface">
              {{ filteredContacts.length ? 1 : 0 }} - {{ filteredContacts.length }}
            </span>
            de <span class="font-semibold text-on-surface">248</span> contactos
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
            v-for="icon in ['first_page', 'chevron_left']"
            :key="icon"
            type="button"
            disabled
            class="p-2 rounded-lg text-outline-variant hover:bg-surface-container disabled:opacity-40 disabled:hover:bg-transparent transition-colors"
          >
            <span class="material-symbols-outlined text-lg">{{ icon }}</span>
          </button>
          <div class="flex items-center gap-1">
            <button
              type="button"
              class="w-8 h-8 rounded-lg font-label-md text-label-md bg-primary-container text-on-primary flex items-center justify-center font-semibold"
            >
              1
            </button>
            <button
              v-for="page in [2, 3]"
              :key="page"
              type="button"
              class="w-8 h-8 rounded-lg font-label-md text-label-md text-on-surface-variant hover:bg-surface-container hover:text-on-surface flex items-center justify-center transition-colors"
            >
              {{ page }}
            </button>
            <span class="px-1 text-on-surface-variant text-sm">...</span>
            <button
              type="button"
              class="w-8 h-8 rounded-lg font-label-md text-label-md text-on-surface-variant hover:bg-surface-container hover:text-on-surface flex items-center justify-center transition-colors"
            >
              25
            </button>
          </div>
          <button
            v-for="icon in ['chevron_right', 'last_page']"
            :key="icon"
            type="button"
            class="p-2 rounded-lg text-on-surface hover:bg-surface-container transition-colors"
          >
            <span class="material-symbols-outlined text-lg">{{ icon }}</span>
          </button>
        </div>
      </div>
    </div>

    <!-- TODO: sección "Salud del Ecosistema" (Quick Insights): el HTML llegó cortado -->
  </div>
</template>
