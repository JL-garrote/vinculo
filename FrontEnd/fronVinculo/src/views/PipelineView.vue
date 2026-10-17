<script setup lang="ts">
import { computed, ref } from 'vue';
import PipelineCard, { type PipelineContact, type Priority } from '@/components/pipeline/PipelineCard.vue';

type StageKey = 'identificados' | 'primer-contacto' | 'reunion' | 'seguimiento' | 'establecida';

interface Stage {
  key: StageKey;
  title: string;
  dotClass: string;
  headerClass: string;
  countClass: string;
  // Contactos de la etapa que no se muestran en el tablero (solo suman al contador)
  hidden: number;
}

interface Metric {
  label: string;
  value: string;
  detail: string;
  detailIcon?: string;
  detailClass: string;
  icon: string;
  iconClass: string;
}

const VC = 'Venture Capital / Finanzas';
const DEEP = 'Deep Tech & AI';
const CLIMATE = 'Climate Tech';
const SAAS = 'B2B SaaS';
const industries = [VC, DEEP, CLIMATE, SAAS];

const metrics: Metric[] = [
  {
    label: 'Total en Pipeline',
    value: '42',
    detail: '+5 este mes',
    detailIcon: 'trending_up',
    detailClass: 'text-secondary',
    icon: 'diversity_3',
    iconClass: 'bg-surface-container-highest text-primary',
  },
  {
    label: 'Conexiones Activas',
    value: '18',
    detail: 'En diálogo constante',
    detailClass: 'text-on-surface-variant',
    icon: 'forum',
    iconClass: 'bg-secondary-container/40 text-secondary',
  },
  {
    label: 'Alianzas / Acuerdos',
    value: '6',
    detail: '85.7% retención',
    detailIcon: 'verified',
    detailClass: 'text-secondary',
    icon: 'handshake',
    iconClass: 'bg-surface-container-highest text-primary',
  },
];

const networkHealth = 92;

const stages: Stage[] = [
  {
    key: 'identificados',
    title: 'Identificados',
    dotClass: 'bg-outline',
    headerClass: 'bg-surface-container-low',
    countClass: 'bg-surface-container-highest text-on-surface-variant',
    hidden: 5,
  },
  {
    key: 'primer-contacto',
    title: 'Primer Contacto',
    dotClass: 'bg-on-tertiary-container',
    headerClass: 'bg-surface-container-low',
    countClass: 'bg-surface-container-highest text-on-surface-variant',
    hidden: 4,
  },
  {
    key: 'reunion',
    title: 'Reunión / Café',
    dotClass: 'bg-primary',
    headerClass: 'bg-surface-container-low',
    countClass: 'bg-surface-container-highest text-on-surface-variant',
    hidden: 3,
  },
  {
    key: 'seguimiento',
    title: 'Seguimiento Activo',
    dotClass: 'bg-secondary',
    headerClass: 'bg-surface-container-low',
    countClass: 'bg-surface-container-highest text-on-surface-variant',
    hidden: 10,
  },
  {
    key: 'establecida',
    title: 'Relación Establecida',
    dotClass: 'bg-secondary',
    headerClass: 'bg-secondary-fixed/40',
    countClass: 'bg-secondary text-on-secondary',
    hidden: 9,
  },
];

const board = ref<Record<StageKey, PipelineContact[]>>({
  identificados: [
    {
      id: 1,
      name: 'Rodrigo Morales',
      role: 'Partner • Andes VC',
      avatar:
        'https://lh3.googleusercontent.com/aida-public/AB6AXuByshVnSQtJzNpZC4uOTmUyfVWeonfUQsTSZrQNEiFWhWsohePcIvV_E5_-THQOn06Y_dZmmSvm0IUDksIpFtYbfP-9tBiz4_2nA4jNd5ggZJWDeK382ObOwM07VUL9hbhDu5BlIiqj_f0Nb_xwQ4A_pjdzg5uM8CkwUkMBsxiKWFmEQU9sZNwomzKYBaccLDSkMTcOldgCZtUJTm9rB3deaH7E9zXuljJA8t_7hcDx7Sy5japNfd1w',
      statusClass: 'bg-secondary',
      statusTitle: 'Ritmo al día',
      priority: 'Alta',
      industry: VC,
      detail: {
        kind: 'intro',
        icon: 'link',
        label: 'Vía Introducción:',
        value: 'Carla Gómez (Ex-colega)',
        affinity: 94,
        affinityClass: 'bg-primary-fixed text-on-primary-fixed-variant',
        whenIcon: 'schedule',
        when: 'En 2 días',
      },
    },
    {
      id: 2,
      name: 'Beatriz Varela',
      role: 'Angel • ScaleLab',
      avatar:
        'https://lh3.googleusercontent.com/aida-public/AB6AXuCcQ7oTBN4Uv-7EjCMO2kAySVUNwkBMsoo9ysTbIJji321sYNuneQuYFB7y6hVCH_MJwRWvqHiq1dceyy80cfQ1Zo0e1Bbj_8JGZ4JE69PcoElP0SkcMVlr2Gdab9IN58b3PHR8J8PQMKouX721vWe2WBszqoMI1kc6sU8LhJ4HmAaU3YiveOz6WCNG9Mw3ys3C1jqsFuK0Pp-iYZS7OINOT_MDiREFRsTPoZOUTEnLYaZTtb-bEfMC',
      statusClass: 'bg-outline',
      statusTitle: 'Neutral',
      priority: 'Media',
      industry: VC,
      detail: {
        kind: 'intro',
        icon: 'campaign',
        label: 'Conexión en común:',
        value: 'Ponente en SouthSummit',
        affinity: 82,
        affinityClass: 'bg-surface-container-high text-on-surface',
        whenIcon: 'calendar_today',
        when: '10 Mayo',
      },
    },
    {
      id: 3,
      name: 'Santiago Herrera',
      role: 'Director • Horizon Corp',
      avatar:
        'https://lh3.googleusercontent.com/aida-public/AB6AXuCeHarEk0pSSgVYzxmkBN5M-LK7nAJU_VilBORJCbhFemVis0XChEEtnjLFtI9wTqkV-Hn68_guz0BRV2xduhgO-J7cILKYoziahSJV9dM1jEzNc_T-KST1bYsHFpPFUAGH-do2fZuOymbbS-o7we9_9Oul8V-yQ8fSJzTF3CWAgit4xbmN3Okmr3wfDLK7v9Y5O2cS9WpenxiX8LyhSs3aS8TIHaVF6TdzVY4oJq7U5L-TuVzg-0pn',
      statusClass: 'bg-outline',
      priority: 'Normal',
      industry: CLIMATE,
      detail: {
        kind: 'intro',
        icon: 'link',
        label: 'Warm Intro:',
        value: 'David Soler (Inversor Seed)',
        affinity: 78,
        affinityClass: 'bg-surface-container-high text-on-surface',
        whenIcon: 'schedule',
        when: 'Sin agendar',
      },
    },
  ],
  'primer-contacto': [
    {
      id: 4,
      name: 'Elena Rostova',
      role: 'Principal • Dawn Ventures',
      avatar:
        'https://lh3.googleusercontent.com/aida-public/AB6AXuAjSsFmlR45MSpkWbpZn5XJK0Abbah9myIncafSCLtoIuhMAPNQdn0K0XoD_WKmL_WlK97bvoeWU2Gs17vrcaF27aJJo-NCvH0sI7Gla7ZT1TvyeK-9f5M-mTZODLMS_0uVU4VuyVUKpK6K6AtGanUxS9RZd2jG6cCPdwKbWp6bFEDmoSriBt2m0nzv9EUU-AKIGEY6c3H08OsPKXeOBoIOAv3282igi-x3bDgSq0lhMEiEMLnqZDiz',
      statusClass: 'bg-tertiary-fixed-dim',
      statusTitle: 'Requiere atención',
      priority: 'Alta',
      industry: SAAS,
      detail: {
        kind: 'contacto',
        channelIcon: 'mail',
        channel: 'Email Enviado',
        ago: 'Hace 4 días',
        agoClass: 'text-tertiary-container',
        quote: 'Adjunté One-Pager + mención de Carla...',
        tag: 'SaaS B2B',
        actionIcon: 'notification_important',
        action: 'Reenviar hoy',
        actionClass: 'text-error font-semibold',
      },
    },
    {
      id: 5,
      name: 'Marc Benítez',
      role: 'Head of Partnerships • FinNext',
      avatar:
        'https://lh3.googleusercontent.com/aida-public/AB6AXuBckGlp2eo7oglWz7GIrt1xO4FlocxytgGQ28V1zGG3S4nj0l7WM8qhCZuO7V8cqcJm-XPhgPD5h4uJcwPKbE0DF7RtH1Lh41DOknNwqeKpSVasz6gsdFpwXHSpuBZMBE_PAuV0Bs_l_EV9FQFEKvvidAb_5xGJc-ZEEnxdFbsUA3Q4ZOBhE-UynSbogxyG5rnvUnbSG4NIHhF6Ob5av4eque8pPfUcmaHGet5cHgMxJBv0XeJArWTu',
      statusClass: 'bg-secondary',
      priority: 'Media',
      industry: VC,
      detail: {
        kind: 'contacto',
        channelIcon: 'send',
        channel: 'LinkedIn InMail',
        ago: 'Hace 1 día',
        agoClass: 'text-secondary',
        quote: 'Respondió interesado en ver métricas...',
        tag: 'Fintech',
        actionIcon: 'reply',
        action: 'Responder',
        actionClass: 'text-on-surface',
      },
    },
  ],
  reunion: [
    {
      id: 6,
      name: 'Valeria Montero',
      role: 'Managing Director • Kibo Growth',
      avatar:
        'https://lh3.googleusercontent.com/aida-public/AB6AXuAW2VXWihQ5o01BJlVSTMvzC-E2sfnN7OPyqFR9k0AKLjNpwg-ikA6ZvVjnnyUQczOWKk8arMFUObYBG-bH-zNpc2wclVlhsmRnwEcOlAs-wplGRb2YMDwd7Nh3bWyJkPXDuqFEUZlRse6VAT4KSgF9Mon237GOwsMdIOuqJP7Kl2DlL2wlVXkpHZq0o6K1sMdmeVO6dTxhVpyHK-XdP5e1v7i-mtzQ9DuBgvGf7YpsIdj8sHbwP6L8',
      statusClass: 'bg-secondary',
      priority: 'Alta',
      industry: VC,
      accentClass: 'border-l-4 border-primary',
      detail: {
        kind: 'reunion',
        highlight: true,
        whenIcon: 'coffee',
        when: 'Jueves, 11:30 AM',
        place: 'Presencial',
        topic: 'Proyecciones Q3 + Modelo de retención comunitaria',
        prep: '3 notas preparadas • Pitch v4.pdf',
        tag: { label: 'Inversores 2025', class: 'bg-secondary-fixed text-on-secondary-fixed-variant' },
        statusIcon: 'check_circle',
        status: 'Confirmado',
        statusClass: 'text-secondary',
      },
    },
    {
      id: 7,
      name: 'Julián Casares',
      role: 'VP Product • CloudMatrix',
      avatar:
        'https://lh3.googleusercontent.com/aida-public/AB6AXuDQFqMrwXfk5TFd7-QFM52-8ALZyBVwHK5ZQGecnPgtc7RFd_XodN2Qas52ze0R5btHlLbF5hwM5N5_CAvObMQxJBJl8CVCR8owBKmTL09Me0lPtD8ekbDlNdOZqjmhMe4f4MPvaA4BmmFovRfc8P8Hrjqnl_d3uFkLOu2M4OuNDQdT-vtkDJsbyg1tHwsrvs8Cbzvsia2_fvZ4eGs9CBTpEEazBXBtyOQrA6wVMhxuyOFKt71VGn6i',
      statusClass: 'bg-secondary',
      priority: 'Media',
      industry: SAAS,
      detail: {
        kind: 'reunion',
        whenIcon: 'video_call',
        when: 'Viernes, 16:00',
        place: 'Google Meet',
        topic: 'Explorar sinergia con APIs de Vínculo',
        tag: { label: 'Tech • Alianzas', class: 'bg-surface-container text-on-surface' },
        statusIcon: 'alarm',
        status: 'En 3 días',
        statusClass: '',
      },
    },
  ],
  seguimiento: [
    {
      id: 8,
      name: 'Lucía Méndez',
      role: 'Founder • Bloom Network',
      avatar:
        'https://lh3.googleusercontent.com/aida-public/AB6AXuBgq0nmGQOY0yblyIoaF_EDhWLhC_UfbwTu6WXtvHEeOc2bb2LjIiHalCM03c8FQ5ebWQm1_FERd6TZBjn5JcXDhVqGtJ8QreFo-k8hMUr51a8KdHKa8fGHQwWkbuW2w6M7HQt60J9SpLhn1fm4rVsCEotl-wMKa3ljMgIIpkxw6e3HJZVbpXLR4WXKT53oFPcyDIU0oTmnGrHpgNpbCRrH3RTTmASszd5GQgyiONqz4inhe7QF6N-B',
      statusClass: 'bg-secondary',
      priority: 'Alta',
      industry: CLIMATE,
      detail: {
        kind: 'seguimiento',
        cadenceIcon: 'autorenew',
        cadence: 'Cadencia: Cada 14d',
        cadenceClass: 'text-secondary',
        touch: 'Toque: en 3 días',
        touchClass: 'text-on-surface-variant',
        milestone: 'Envió feedback positivo sobre el prototipo UI.',
        tags: [
          { label: 'Mentores', class: 'bg-surface-container text-on-surface' },
          { label: 'Diseño', class: 'bg-primary-fixed text-on-primary-fixed-variant' },
        ],
      },
    },
    {
      id: 9,
      name: 'Álvaro Duprat',
      role: 'Socio • Kepler Angels',
      avatar:
        'https://lh3.googleusercontent.com/aida-public/AB6AXuDsMg88-yBp02_1HMm7ufaeTorvAkX-S9oMuz8Q55O-TL5rS2iivHCD8JErg5moknyopVSVo8latM4T4TwmSf9aHdLUdHuU_sPKdesN_ulgYxF1swgR7l2l41xB0ASI0M_qSdMOkhqKSQN6y3sJrJTe3X-0mv1zhrblgdVuEVuPiPFy039webQuk_6kzNucAH2remRy0txqSxUlp38168uuVlULFJL4JcfuKnUxqZnwpCgL641RIx3J',
      statusClass: 'bg-tertiary-fixed-dim',
      statusTitle: 'Ritmo por vencer',
      priority: 'Media',
      industry: DEEP,
      detail: {
        kind: 'seguimiento',
        cadenceIcon: 'hourglass_top',
        cadence: 'Cadencia: Mensual',
        cadenceClass: 'text-on-tertiary-container',
        touch: 'Caducó hace 2d',
        touchClass: 'text-error font-medium',
        milestone: 'Café presencial en Madrid (22 Mar).',
        tags: [{ label: 'Inversores', class: 'bg-surface-container text-on-surface' }],
        overdue: true,
      },
    },
  ],
  establecida: [
    {
      id: 10,
      name: 'Inés Arismendi',
      role: 'Directora • Nexus Advisory',
      avatar:
        'https://lh3.googleusercontent.com/aida-public/AB6AXuDn2W3f3wIgv_FZssa5BSRmczTx8qx_IOEuJLOgB2aqPdnyE_uUiKzaOQ9kv1-DsL8GeXm9NgICV8b_fGZQ-WhWu8_PG_WxyUi0MJGRgiBC_FTVVAKi0eVdfViVpeCeZ5lxHarB0X2DhN6BI0rta_MjEYFQwuSai8-5EVSc8ft1SR5N3ZxpArr47t13zz0vYVBhLWsRMsMxHcg29VpZJLPtDpjbXSICU1dbFHDj9By8PbJvw626t9wa',
      statusClass: 'bg-secondary',
      priority: 'Normal',
      industry: SAAS,
      accentClass: 'border-l-4 border-secondary',
      detail: {
        kind: 'establecida',
        badgeIcon: 'verified',
        badge: 'Aliada',
        title: 'Acuerdo Activo',
        cadence: 'Check-in mensual',
        text: 'Referral cruzado de leads calificados y co-mentoría bimestral.',
        footIcon: 'check_circle',
        foot: 'Relación Sólida',
        nextIcon: 'event',
        next: 'Próx: 28 May',
      },
    },
    {
      id: 11,
      name: 'Tomás De La Vega',
      role: 'Ex-CPO • GlobalRetail',
      avatar:
        'https://lh3.googleusercontent.com/aida-public/AB6AXuBEq_yNtR3klVDTSc9VZXweoxBNay5zxapQUUJckkOCEPTIkjIjayk8kaHObd6vbM_rCme98BeoWjsrTXLlxgBTeyTDpn_gPuSTYAQADJ9TO2_QS6iFUKJP7RowBksb7zoHUxmpAohAuac7kvpAGjdcCl4wtw8_xvc81l5bkSQE1r2wY9VgXqP3w8o4grPPXC7AEwpJlWdhh33TKHpzHb7ODw16wWvxlOhkrWzwvvNUb1AcIW6TvKNf',
      statusClass: 'bg-secondary',
      priority: 'Normal',
      industry: VC,
      accentClass: 'border-l-4 border-secondary',
      detail: {
        kind: 'establecida',
        badgeIcon: 'star',
        badge: 'Key Angel',
        title: 'Compromiso',
        cadence: 'Ritmo: Cada 21d',
        text: 'Lead Investor en Ronda Semilla y facilitador de presentaciones a 4 fondos.',
        footIcon: 'shield',
        foot: 'Confianza Total',
        nextIcon: 'call',
        next: 'Al día',
      },
    },
  ],
});

// Filtros
const search = ref('');
const industry = ref('all');
const priority = ref<Priority | 'Todas'>('Todas');
const viewMode = ref<'kanban' | 'tabla'>('kanban');

const priorityFilters: { value: Priority | 'Todas'; dotClass?: string }[] = [
  { value: 'Todas' },
  { value: 'Alta', dotClass: 'bg-error' },
  { value: 'Media', dotClass: 'bg-on-tertiary-container' },
  { value: 'Normal', dotClass: 'bg-outline' },
];

// Texto en el que busca el filtro: nombre, rol y los textos visibles de la tarjeta
function searchableText(card: PipelineContact) {
  const detailTexts = Object.entries(card.detail)
    .filter(([key, value]) => typeof value === 'string' && key !== 'kind' && !/(Class|Icon)$/.test(key))
    .map(([, value]) => value as string);
  return [card.name, card.role, ...detailTexts].join(' ').toLowerCase();
}

function isVisible(card: PipelineContact) {
  const q = search.value.trim().toLowerCase();
  if (q && !searchableText(card).includes(q)) return false;
  if (industry.value !== 'all' && card.industry !== industry.value) return false;
  if (priority.value !== 'Todas' && card.priority !== priority.value) return false;
  return true;
}

const visibleBoard = computed(() => {
  const result = {} as Record<StageKey, PipelineContact[]>;
  for (const stage of stages) result[stage.key] = board.value[stage.key].filter(isVisible);
  return result;
});

// Arrastrar y soltar tarjetas entre columnas
const dragging = ref<{ id: number; from: StageKey } | null>(null);
const dropTarget = ref<StageKey | null>(null);

function onDragStart(id: number, from: StageKey) {
  dragging.value = { id, from };
}

function onDrop(to: StageKey) {
  const drag = dragging.value;
  dropTarget.value = null;
  dragging.value = null;
  if (!drag || drag.from === to) return;
  const source = board.value[drag.from];
  const index = source.findIndex((c) => c.id === drag.id);
  if (index === -1) return;
  const [card] = source.splice(index, 1);
  if (card) board.value[to].unshift(card);
}
</script>

<template>
  <div class="flex flex-col w-full">
    <!-- Cabecera y KPIs -->
    <div class="px-8 py-6 flex flex-col gap-6 bg-surface-container-lowest shadow-sm">
      <div class="flex flex-col lg:flex-row lg:items-center justify-between gap-4">
        <div class="flex flex-col gap-1">
          <div class="flex items-center gap-2 text-on-surface-variant font-label-sm text-label-sm tracking-wider uppercase">
            <span class="inline-block w-2 h-2 rounded-full bg-secondary"></span>
            <span>Red Estratégica • Modo Campaña</span>
          </div>
          <div class="flex flex-wrap items-center gap-3">
            <h1 class="font-headline-md text-headline-md text-on-surface">Pipeline Relacional</h1>
            <button
              type="button"
              class="flex items-center gap-2 px-3 py-1.5 rounded-full bg-surface-container-high text-on-surface font-label-md text-label-md hover:bg-surface-container transition-colors"
            >
              <span class="material-symbols-outlined text-base text-primary">target</span>
              <span>Objetivo: Ronda Semilla &amp; Networking Estratégico 2025</span>
              <span class="material-symbols-outlined text-sm">expand_more</span>
            </button>
          </div>
        </div>
        <div class="flex flex-wrap items-center gap-3">
          <button
            type="button"
            class="flex items-center gap-2 px-3.5 py-2 rounded-lg bg-surface-container-high text-on-surface font-label-lg text-label-lg hover:bg-surface-variant transition-colors shadow-sm"
          >
            <span class="material-symbols-outlined text-base">tune</span>
            <span>Ajustar Ritmos</span>
          </button>
          <button
            type="button"
            class="flex items-center gap-2 px-4 py-2 rounded-lg bg-primary text-on-primary font-label-lg text-label-lg hover:bg-primary-container transition-all shadow-md"
          >
            <span class="material-symbols-outlined text-base">person_add</span>
            <span>Añadir al Pipeline</span>
          </button>
        </div>
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

        <!-- Salud de red -->
        <div class="flex items-center justify-between p-4 rounded-xl bg-surface-container-low transition-all hover:bg-surface-container">
          <div class="flex flex-col justify-between h-full">
            <div>
              <span class="font-label-sm text-label-sm text-on-surface-variant uppercase tracking-wider">Salud de Red</span>
              <div class="flex items-baseline gap-2 mt-1">
                <span class="font-headline-md text-headline-md text-on-surface leading-none">{{ networkHealth }}%</span>
                <span class="font-label-sm text-label-sm text-secondary">Óptima</span>
              </div>
            </div>
            <span class="font-label-sm text-label-sm text-on-surface-variant">Solo 3 contactos fríos</span>
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
                :stroke-dasharray="`${networkHealth}, 100`"
                stroke-linecap="round"
                stroke-width="3.5"
              />
            </svg>
            <span class="material-symbols-outlined absolute text-secondary text-sm">favorite</span>
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
            placeholder="Filtrar por nombre, empresa o warm intro..."
            class="w-full pl-9 pr-3 py-1.5 bg-surface-container-lowest rounded-lg font-body-sm text-body-sm text-on-surface placeholder:text-outline focus:outline-none focus:ring-2 focus:ring-primary/20 shadow-sm"
          />
        </div>

        <div class="flex items-center bg-surface-container-lowest rounded-lg px-3 py-1.5 shadow-sm">
          <span class="material-symbols-outlined text-base text-on-surface-variant mr-2">apartment</span>
          <span class="font-label-sm text-label-sm text-on-surface-variant mr-1">Industria:</span>
          <select
            v-model="industry"
            class="bg-transparent font-label-md text-label-md text-on-surface focus:outline-none cursor-pointer pr-2"
          >
            <option value="all">Todas las industrias</option>
            <option v-for="name in industries" :key="name" :value="name">{{ name }}</option>
          </select>
        </div>

        <div class="flex items-center bg-surface-container-lowest rounded-lg p-1 shadow-sm gap-1">
          <button
            v-for="filter in priorityFilters"
            :key="filter.value"
            type="button"
            class="px-2.5 py-1 rounded font-label-sm text-label-sm flex items-center gap-1 transition-colors"
            :class="
              priority === filter.value
                ? 'bg-primary-container text-on-primary'
                : 'text-on-surface-variant hover:text-on-surface hover:bg-surface-container-high'
            "
            @click="priority = filter.value"
          >
            <span v-if="filter.dotClass" class="w-1.5 h-1.5 rounded-full" :class="filter.dotClass"></span>
            {{ filter.value }}
          </button>
        </div>
      </div>

      <div class="flex items-center gap-2">
        <div class="flex items-center bg-surface-container-lowest rounded-lg p-1 shadow-sm">
          <button
            type="button"
            title="Vista Kanban"
            class="p-1.5 rounded"
            :class="viewMode === 'kanban' ? 'bg-surface-container-high text-primary' : 'text-on-surface-variant hover:text-on-surface'"
            @click="viewMode = 'kanban'"
          >
            <span class="material-symbols-outlined text-base">view_kanban</span>
          </button>
          <button
            type="button"
            title="Vista Tabla"
            class="p-1.5 rounded"
            :class="viewMode === 'tabla' ? 'bg-surface-container-high text-primary' : 'text-on-surface-variant hover:text-on-surface'"
            @click="viewMode = 'tabla'"
          >
            <span class="material-symbols-outlined text-base">table_rows</span>
          </button>
        </div>
        <div class="h-4 w-px bg-surface-container-highest"></div>
        <button
          type="button"
          class="flex items-center gap-1 px-3 py-1.5 rounded-lg bg-surface-container-lowest text-on-surface-variant hover:text-on-surface font-label-sm text-label-sm shadow-sm"
        >
          <span class="material-symbols-outlined text-base">swap_vert</span>
          <span>Próximo Toque</span>
        </button>
      </div>
    </div>

    <!-- Tablero kanban -->
    <div class="px-8 py-4 overflow-x-auto pb-10">
      <div class="flex gap-5 min-w-[1440px] items-start">
        <div
          v-for="stage in stages"
          :key="stage.key"
          class="w-80 flex-shrink-0 flex flex-col gap-3 rounded-xl transition-colors"
          :class="{ 'bg-primary-fixed/40 outline-2 outline-dashed outline-primary/30': dropTarget === stage.key }"
          @dragover.prevent="dropTarget = stage.key"
          @dragleave.self="dropTarget = null"
          @drop.prevent="onDrop(stage.key)"
        >
          <div class="flex items-center justify-between px-3 py-2.5 rounded-xl" :class="stage.headerClass">
            <div class="flex items-center gap-2">
              <span class="w-2.5 h-2.5 rounded-full" :class="stage.dotClass"></span>
              <span class="font-label-lg text-label-lg text-on-surface">{{ stage.title }}</span>
              <span class="px-2 py-0.5 rounded-full font-label-sm text-label-sm" :class="stage.countClass">
                {{ board[stage.key].length + stage.hidden }}
              </span>
            </div>
            <button type="button" title="Añadir" class="p-1 text-on-surface-variant hover:text-on-surface rounded-lg">
              <span class="material-symbols-outlined text-base">add</span>
            </button>
          </div>

          <div class="flex flex-col gap-3 min-h-24">
            <PipelineCard
              v-for="card in visibleBoard[stage.key]"
              :key="card.id"
              :card="card"
              draggable="true"
              :class="{ 'opacity-40': dragging?.id === card.id }"
              @dragstart="onDragStart(card.id, stage.key)"
              @dragend="dragging = null; dropTarget = null"
            />
            <p
              v-if="visibleBoard[stage.key].length === 0"
              class="py-6 text-center font-body-sm text-body-sm text-on-surface-variant"
            >
              Sin contactos
            </p>
          </div>
        </div>
      </div>
    </div>

    <!-- Sugerencia del asistente -->
    <div
      class="mx-8 mb-8 p-5 rounded-2xl bg-surface-container-lowest shadow-sm flex flex-col md:flex-row items-center justify-between gap-4"
    >
      <div class="flex items-center gap-4">
        <div class="w-10 h-10 shrink-0 rounded-xl bg-primary-fixed flex items-center justify-center text-primary">
          <span class="material-symbols-outlined text-xl">psychology</span>
        </div>
        <div class="flex flex-col">
          <span class="font-label-lg text-label-lg text-on-surface">Sugerencia del Asistente Relacional</span>
          <span class="font-body-sm text-body-sm text-on-surface-variant">
            Tienes 3 personas que no contactas hace más de 30 días en tu columna de 'Seguimiento Activo'. Un mensaje breve
            reactivará la calidez del vínculo.
          </span>
        </div>
      </div>
      <div class="flex items-center gap-3 w-full md:w-auto justify-end">
        <button
          type="button"
          class="px-4 py-2 rounded-lg bg-surface-container-high text-on-surface font-label-md text-label-md hover:bg-surface-variant transition-colors whitespace-nowrap"
        >
          Ver Contactos Pendientes
        </button>
        <button
          type="button"
          class="px-4 py-2 rounded-lg bg-primary text-on-primary font-label-md text-label-md hover:bg-primary-container transition-colors shadow-sm whitespace-nowrap"
        >
          Enviar Saludos Programados
        </button>
      </div>
    </div>
  </div>
</template>
