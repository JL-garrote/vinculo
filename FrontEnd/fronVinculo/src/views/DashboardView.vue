<script setup lang="ts">
import KpiCard, { type Kpi } from '@/components/dashboard/KpiCard.vue';
import AttentionCard, { type AttentionContact } from '@/components/dashboard/AttentionCard.vue';

interface Activity {
  title: string;
  circle: string;
  date: string;
  icon: string;
  iconClass: string;
  text: string;
  mood?: string;
  next?: string;
  tags?: string[];
}

interface Circle {
  name: string;
  count: number;
  percent: number;
  dotClass: string;
  percentClass: string;
  barClass: string;
}

const kpis: Kpi[] = [
  {
    label: 'Contactos Totales',
    value: '248',
    detail: 'en 8 círculos',
    icon: 'contacts',
    iconClass: 'bg-surface-container text-on-surface-variant',
    badge: '+12 este mes',
    badgeClass: 'px-2 py-0.5 rounded-full bg-secondary/10 text-secondary',
    progress: 74,
    progressClass: 'bg-primary-container',
  },
  {
    label: 'Relaciones Fuertes',
    value: '86%',
    valueClass: 'text-secondary',
    detail: 'salud relacional alta',
    icon: 'favorite',
    iconClass: 'bg-secondary/15 text-secondary',
    badge: '+3% vs feb',
    badgeIcon: 'trending_up',
    badgeClass: 'text-secondary font-medium',
    progress: 86,
    progressClass: 'bg-secondary',
  },
  {
    label: 'Seguimientos Pendientes',
    value: '5',
    detail: 'prioritarios hoy',
    detailClass: 'text-tertiary',
    icon: 'schedule',
    iconClass: 'bg-tertiary-fixed text-tertiary-container',
    badge: 'Urgente',
    badgeClass: 'px-2 py-0.5 rounded-full bg-error-container text-on-error-container',
    progress: 45,
    progressClass: 'bg-tertiary-container',
  },
  {
    label: 'Frecuencia Media',
    value: '18',
    detail: 'días de cadencia',
    icon: 'avg_pace',
    iconClass: 'bg-surface-container text-on-surface-variant',
    badge: 'Meta: ≤20 d',
    badgeClass: 'text-on-surface-variant',
    progress: 90,
    progressClass: 'bg-primary',
  },
];

const attention: AttentionContact[] = [
  {
    name: 'Carlos Mendoza',
    role: 'Mentor / Inversor Semilla',
    avatar:
      'https://lh3.googleusercontent.com/aida-public/AB6AXuB10p9YJ0h4KsBRjouHulzw9GJ0-FsVkHuXKnGghepsP8Y0ZJw0eEyxo9_9TukJI676UYj81toDBw-Db8A5N2hoeGkewRH1y1edot-oQa-2yB2D_DsltoNqzRjL7GVRSrffuxEk-4Au4ceXCv2IGa3wHj0V6YiYIMlq_A8neFjV84YG0wjpO5xK9G8mDjFXKWF0Ee8PPqd8Wb3clsR9246U_SnjSR4cHlHtnAb5uNL2GEKhhHhihOxv',
    statusClass: 'bg-tertiary',
    statusTitle: 'Cadencia excedida',
    circle: 'Mentores',
    circleClass: 'bg-primary-fixed text-on-primary-fixed',
    alert: 'Último contacto: hace 42 días',
    alertIcon: 'warning',
    alertClass: 'text-tertiary',
    meta: 'Cadencia: mensual',
    note: 'Hablaron sobre la ronda Seed en cafeteria Federal. Prometiste enviar métricas de retención de Q1.',
    actions: [
      { label: 'WhatsApp', icon: 'chat', iconClass: 'text-secondary' },
      { label: 'Llamada', icon: 'call', primary: true },
    ],
    secondaryAction: 'Posponer 7 días',
  },
  {
    name: 'Elena Ramos',
    role: 'Ex-colega & Amiga',
    avatar:
      'https://lh3.googleusercontent.com/aida-public/AB6AXuDO2W-PDZjd1jbSnt_Zr0t22PPxPULtXDUxlh2NV89JvpK6YQM2WmSmZYR-ptOjuqzlTFMTYHSZyf30Egrlhv9kKgyLj9Ia7HsS4QE8c-KLDusozBhJ79-eDFw1Q42FQTflABuP4xZjfsuvh0VHlJXJ2sw6lTKVLE1i8OYqdZZsqwXGAiXuqwcflv3MU03GkKHz9Bu3etBBQdjZuGepAxaGVUP6jHo8-n1ZICKpmq2okqRM24FGBGqF',
    statusClass: 'bg-secondary',
    statusTitle: 'Activa',
    circle: 'Amigos',
    circleClass: 'bg-error-container text-on-error-container',
    alert: 'Cumpleaños este sábado (18 Mar)',
    alertIcon: 'cake',
    alertClass: 'text-primary-container font-semibold',
    meta: '34 años',
    note: 'Le fascinan las plantas exóticas (Monsteras) y los libros de diseño editorial japonés.',
    actions: [
      { label: 'Mensaje', icon: 'edit_note', primary: true },
      { label: 'Regalos', icon: 'featured_seasonal_and_gifts', iconClass: 'text-primary-container' },
    ],
    secondaryAction: 'Añadir a lista de compras',
  },
  {
    name: 'Mateo Silva',
    role: 'Cliente Clave • Tech Lead',
    avatar:
      'https://lh3.googleusercontent.com/aida-public/AB6AXuDmNpbQ0ellXDa-AMTBI6slGW85M1apzfvXOYSv7ivqGeFrsIP2dwppdW00mLAQqP0wcqSYhiHOrUTDFLTaveQoSXNuym94T-KQDhfsbERN5WX34OnmjGNZrfzMFGVwsI2jL2CmBNvthEJ5ZzeUmrqItkAWUYlEu59RYm-OuOB1mNWSIaOGpHjwDVoTjbm0zF0oc4mP8OoboAG2RXDu_uwQyLHaNA4jflL0WuzZeju2S2gs1MutxhY2',
    statusClass: 'bg-primary-container',
    statusTitle: 'Compromiso para hoy',
    circle: 'Clientes',
    circleClass: 'bg-secondary-fixed text-on-secondary-fixed-variant',
    alert: 'Planificado para hoy (16:30 hs)',
    alertIcon: 'event',
    alertClass: 'text-secondary font-semibold',
    meta: 'Zoom',
    note: 'Hablar sobre feedback del MVP y definir hoja de ruta para la integración con Stripe.',
    actions: [
      { label: 'Ver Notas', icon: 'description', iconClass: 'text-primary-container' },
      { label: 'Abrir Sala', icon: 'video_call', primary: true },
    ],
    secondaryAction: 'Reagendar para mañana',
  },
];

const activities: Activity[] = [
  {
    title: 'Café con Sofía Valenzuela',
    circle: 'Networking tech',
    date: 'Ayer, 18:20',
    icon: 'coffee',
    iconClass: 'bg-secondary/10 text-secondary',
    text: 'Reunión en Palermo. Charlamos sobre su reciente cambio de puesto a VP de Producto. Le compartí el contacto del equipo de Growth. Le encantó la recomendación del libro de arquitectura de sistemas.',
    mood: 'Energía muy positiva',
    next: 'Próximo contacto: en 3 semanas',
  },
  {
    title: 'Llamada breve con Diego Aranda',
    circle: 'Mentores',
    date: 'Lunes, 11:15',
    icon: 'phone_in_talk',
    iconClass: 'bg-primary-fixed text-primary-container',
    text: 'Puesta al día de 15 minutos mientras viajaba al aeropuerto. Acordamos revisar el deck de inversores una vez que tengamos tracción en el piloto B2B. Su hija ingresó a la universidad.',
    tags: ['#inversores', '#deck-q2'],
  },
  {
    title: 'Introducción de contacto a Camila Ríos',
    circle: 'Clientes',
    date: 'Viernes pasado',
    icon: 'mail',
    iconClass: 'bg-tertiary-fixed text-tertiary-container',
    text: 'Conecté por correo electrónico con Javier (diseñador UX senior). Ambos estaban buscando sinergias en el sector de salud digital. Pendiente confirmar si concertaron reunión.',
  },
];

const circles: Circle[] = [
  {
    name: 'Amigos cercanos',
    count: 28,
    percent: 92,
    dotClass: 'bg-error',
    percentClass: 'text-secondary',
    barClass: 'bg-secondary',
  },
  {
    name: 'Mentores & Consejeros',
    count: 12,
    percent: 68,
    dotClass: 'bg-primary-container',
    percentClass: 'text-tertiary-container',
    barClass: 'bg-tertiary-container',
  },
  {
    name: 'Networking Tech & Fundadores',
    count: 144,
    percent: 88,
    dotClass: 'bg-primary',
    percentClass: 'text-secondary',
    barClass: 'bg-primary-container',
  },
  {
    name: 'Clientes Clave & Partners',
    count: 64,
    percent: 84,
    dotClass: 'bg-secondary',
    percentClass: 'text-secondary',
    barClass: 'bg-secondary',
  },
];

const networkHealth = 86;
</script>

<template>
  <div class="px-8 py-8 flex flex-col gap-8 max-w-[1600px] mx-auto w-full">
    <!-- Bienvenida -->
    <div class="flex flex-col md:flex-row md:items-end justify-between gap-4">
      <div class="flex flex-col gap-1">
        <div class="flex items-center gap-2">
          <span
            class="inline-flex items-center px-2.5 py-0.5 rounded-full text-label-sm font-label-sm bg-secondary/10 text-secondary"
          >
            <span class="w-1.5 h-1.5 rounded-full bg-secondary mr-1.5 animate-pulse"></span>
            Ritmo Relacional Óptimo
          </span>
          <span class="text-on-surface-variant font-label-sm text-label-sm">Miércoles, 15 de Marzo</span>
        </div>
        <h1 class="text-headline-lg font-headline-lg text-on-surface tracking-tight">¡Buenos días, Lucía!</h1>
        <p class="text-body-md font-body-md text-on-surface-variant">
          Tienes <span class="text-primary-container font-semibold">4 relaciones</span> que requieren atención hoy y
          <span class="text-on-surface font-semibold">2 eventos próximos</span> en tu radar.
        </p>
      </div>
      <div class="flex items-center gap-3">
        <button
          type="button"
          class="flex items-center gap-2 px-3.5 py-2 bg-surface-container-high hover:bg-surface-container-highest text-on-surface rounded-lg text-label-lg font-label-lg transition-all shadow-sm"
        >
          <span class="material-symbols-outlined text-base text-primary-container">sync</span>
          <span>Sincronizar Calendario</span>
        </button>
        <button
          type="button"
          class="flex items-center gap-2 px-4 py-2 bg-primary-container hover:bg-primary text-on-primary rounded-lg text-label-lg font-label-lg transition-all shadow-sm"
        >
          <span class="material-symbols-outlined text-base">alarm_add</span>
          <span>Programar Recordatorio</span>
        </button>
      </div>
    </div>

    <!-- KPIs -->
    <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
      <KpiCard v-for="kpi in kpis" :key="kpi.label" :kpi="kpi" />
    </div>

    <!-- Requieren tu atención -->
    <div class="flex flex-col gap-4">
      <div class="flex items-center justify-between">
        <div class="flex items-center gap-3">
          <div class="w-2.5 h-2.5 rounded-full bg-tertiary-container"></div>
          <h2 class="text-headline-sm font-headline-sm text-on-surface">Requieren tu atención</h2>
          <span class="text-body-sm font-body-sm text-on-surface-variant hidden sm:inline">
            • Mantén el vínculo antes de que se enfríe
          </span>
        </div>
        <button
          type="button"
          class="text-label-lg font-label-lg text-primary-container hover:text-primary flex items-center gap-1 transition-colors"
        >
          <span>Ver todos los 14 recordatorios</span>
          <span class="material-symbols-outlined text-sm">arrow_forward</span>
        </button>
      </div>
      <div class="grid grid-cols-1 md:grid-cols-3 gap-5">
        <AttentionCard v-for="contact in attention" :key="contact.name" :contact="contact" />
      </div>
    </div>

    <!-- Contenido principal (60% / 40%) -->
    <div class="grid grid-cols-1 lg:grid-cols-12 gap-8 items-start">
      <!-- Actividad Relacional Reciente -->
      <div class="lg:col-span-7 flex flex-col gap-5">
        <div class="flex items-center justify-between">
          <div class="flex items-center gap-2">
            <span class="material-symbols-outlined text-primary-container text-xl">history_edu</span>
            <h2 class="text-headline-sm font-headline-sm text-on-surface">Actividad Relacional Reciente</h2>
          </div>
          <div class="flex items-center gap-2">
            <button
              type="button"
              class="p-1.5 rounded-lg bg-surface-container text-on-surface-variant hover:text-on-surface transition-colors"
              title="Filtrar notas"
            >
              <span class="material-symbols-outlined text-lg">filter_list</span>
            </button>
            <button
              type="button"
              class="flex items-center gap-1.5 px-3 py-1.5 rounded-lg bg-surface-container-high text-on-surface hover:bg-surface-container-highest text-label-md font-label-md transition-colors"
            >
              <span class="material-symbols-outlined text-sm">add_comment</span>
              <span>Registrar Nota</span>
            </button>
          </div>
        </div>

        <div class="bg-surface-container-lowest rounded-xl p-6 shadow-sm flex flex-col gap-6">
          <div v-for="(item, i) in activities" :key="item.title" class="flex gap-4 relative">
            <div class="flex flex-col items-center">
              <div class="w-10 h-10 rounded-full flex items-center justify-center z-10" :class="item.iconClass">
                <span class="material-symbols-outlined text-lg">{{ item.icon }}</span>
              </div>
              <div v-if="i < activities.length - 1" class="w-0.5 bg-surface-container-high h-full my-1"></div>
            </div>
            <div class="flex flex-col gap-1.5 flex-1" :class="{ 'pb-6': i < activities.length - 1 }">
              <div class="flex items-center justify-between flex-wrap gap-2">
                <div class="flex items-center gap-2">
                  <span class="font-label-lg text-label-lg text-on-surface">{{ item.title }}</span>
                  <span class="px-2 py-0.5 rounded text-label-sm font-label-sm bg-surface-container text-on-surface-variant">
                    {{ item.circle }}
                  </span>
                </div>
                <span class="text-label-sm font-label-sm text-on-surface-variant">{{ item.date }}</span>
              </div>
              <p class="text-body-sm font-body-sm text-on-surface-variant bg-surface-container-low p-3.5 rounded-lg">
                {{ item.text }}
              </p>
              <div v-if="item.mood" class="flex items-center gap-3 text-label-sm font-label-sm text-on-surface-variant pt-1">
                <span class="flex items-center gap-1 text-secondary">
                  <span class="material-symbols-outlined text-xs">sentiment_very_satisfied</span>
                  {{ item.mood }}
                </span>
                <span>•</span>
                <span>{{ item.next }}</span>
              </div>
              <div v-if="item.tags" class="flex items-center gap-2 flex-wrap pt-1">
                <span
                  v-for="tag in item.tags"
                  :key="tag"
                  class="inline-flex items-center px-2 py-0.5 rounded bg-surface-container text-on-surface-variant text-label-sm font-label-sm"
                >
                  {{ tag }}
                </span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Círculos y Salud de Red -->
      <div class="lg:col-span-5 flex flex-col gap-5">
        <div class="flex items-center justify-between">
          <div class="flex items-center gap-2">
            <span class="material-symbols-outlined text-secondary text-xl">hub</span>
            <h2 class="text-headline-sm font-headline-sm text-on-surface">Círculos y Salud de Red</h2>
          </div>
          <button
            type="button"
            class="p-1.5 rounded-lg hover:bg-surface-container text-on-surface-variant transition-colors"
            title="Gestionar Círculos"
          >
            <span class="material-symbols-outlined text-lg">tune</span>
          </button>
        </div>

        <div class="bg-surface-container-lowest rounded-xl p-6 shadow-sm flex flex-col gap-6">
          <!-- Estado global -->
          <div class="flex items-center justify-between p-4 rounded-xl bg-surface-container-low gap-4">
            <div class="flex flex-col">
              <span class="text-label-sm font-label-sm text-on-surface-variant uppercase tracking-wider">
                Estado Global de Red
              </span>
              <span class="text-headline-md font-headline-md text-on-surface mt-0.5">{{ networkHealth }}% al día</span>
              <span class="text-body-sm font-body-sm text-secondary flex items-center gap-1 mt-1">
                <span class="material-symbols-outlined text-sm">verified</span>
                214 relaciones en cadencia activa
              </span>
            </div>
            <div class="relative w-20 h-20 flex items-center justify-center flex-shrink-0">
              <svg class="w-full h-full -rotate-90" viewBox="0 0 36 36">
                <path
                  class="text-surface-container-high"
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
              <span class="absolute font-label-lg text-label-lg text-on-surface">{{ networkHealth }}%</span>
            </div>
          </div>

          <!-- Desglose por círculo -->
          <div class="flex flex-col gap-4">
            <span class="text-label-md font-label-md text-on-surface-variant uppercase tracking-wider">
              Desglose por Círculo
            </span>
            <div v-for="circle in circles" :key="circle.name" class="flex flex-col gap-1.5">
              <div class="flex items-center justify-between text-body-sm font-body-sm">
                <div class="flex items-center gap-2">
                  <span class="w-2.5 h-2.5 rounded-full" :class="circle.dotClass"></span>
                  <span class="font-medium text-on-surface">{{ circle.name }}</span>
                  <span class="text-on-surface-variant text-label-sm font-label-sm">({{ circle.count }})</span>
                </div>
                <div class="flex items-center gap-2 font-label-sm text-label-sm">
                  <span class="font-semibold" :class="circle.percentClass">{{ circle.percent }}%</span>
                  <span class="text-on-surface-variant">al día</span>
                </div>
              </div>
              <div class="w-full bg-surface-container h-2 rounded-full overflow-hidden">
                <div class="h-full rounded-full" :class="circle.barClass" :style="{ width: `${circle.percent}%` }"></div>
              </div>
            </div>
          </div>

          <!-- CTA -->
          <div class="p-4 rounded-xl bg-surface-container-high/60 flex flex-col gap-3">
            <div class="flex items-start gap-3">
              <span class="material-symbols-outlined text-primary-container text-xl mt-0.5">update</span>
              <div class="flex flex-col">
                <span class="font-label-lg text-label-lg text-on-surface">Establecer Ritmo de Contacto</span>
                <span class="font-body-sm text-body-sm text-on-surface-variant">
                  Automatiza recordatorios según círculos para que ninguna relación clave se diluya en el tiempo.
                </span>
              </div>
            </div>
            <button
              type="button"
              class="w-full py-2.5 px-4 bg-surface-container-lowest hover:bg-surface-container text-on-surface font-label-lg text-label-lg rounded-lg shadow-sm transition-all flex items-center justify-center gap-2"
            >
              <span class="material-symbols-outlined text-base text-primary-container">add_circle</span>
              <span>Nuevo Recordatorio Recurrente</span>
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
