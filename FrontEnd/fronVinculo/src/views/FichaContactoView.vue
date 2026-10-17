<script setup lang="ts">
import { ref } from 'vue';
import InteractionLogger, {
  type InteractionType,
  type NewInteraction,
} from '@/components/ficha/InteractionLogger.vue';

interface ContextItem {
  icon: string;
  label: string;
  html: string;
  badge?: string;
}

interface Connection {
  initials: string;
  initialsClass: string;
  name: string;
  detail: string;
}

interface TimelineEntry {
  id: number;
  title: string;
  badge: string;
  badgeClass: string;
  icon: string;
  iconClass: string;
  date: string;
  text: string;
  small?: boolean;
  recommendation?: string;
  mood?: string;
  tags?: string[];
}

const contact = {
  name: 'Dr. Alejandro Morales',
  avatar:
    'https://lh3.googleusercontent.com/aida-public/AB6AXuB-h-ktzreKKTGXsXFYTIwUAu0HU3Km6_lFnZP7GztY_1vKdlx2Zhlm5Py6eh0fbfyXk8bU-C4SYqHKfYjKsyvj_GQdIWCTlnjwklzCr0Up24R39SkRbqvuzFzarqmzZ5yc1O1RawngDpy3xMq6SbWhcH4AYVvRHwirW2gQu4zDT5u_FThx1El0psGrk5EnDenEyUKXgxtC-uVbrtZnyNRDNx9lHNqIAcBFNThGV_iIx5xiFbf4oRr4',
  role: 'Managing Partner @ Horizon Capital',
  location: 'Madrid, España',
  email: 'alejandro.morales@horizoncap.es',
  phone: '+34 914 223 901',
  health: 95,
  cadence: 'Cada 30 días',
  nextTouch: 'en 12 días',
};

const circles = [
  { label: 'Mentor', class: 'bg-primary-fixed text-on-primary-fixed' },
  { label: 'Círculo de Confianza', class: 'bg-secondary-container text-on-secondary-container' },
];

const personalContext: ContextItem[] = [
  {
    icon: 'diversity_1',
    label: 'Familia & Vínculos',
    html: 'Casado con <span class="font-semibold">Laura</span>. Dos hijos: <span class="font-medium text-primary">Lucas</span> (11 años) y <span class="font-medium text-primary">Mia</span> (8 años).',
  },
  {
    icon: 'interests',
    label: 'Aficiones & Pasiones',
    html: 'Pádel (fines de semana), coleccionista de libros antiguos de filosofía, café de especialidad filtrado (V60).',
  },
  { icon: 'cake', label: 'Cumpleaños', html: '14 de Mayo', badge: 'En 2 meses' },
  { icon: 'workspace_premium', label: 'Hito Profesional', html: 'Fundó Horizon Capital en Octubre de 2017' },
];

const connections: Connection[] = [
  {
    initials: 'CS',
    initialsClass: 'bg-primary-fixed text-on-primary-fixed',
    name: 'Clara Soto',
    detail: 'Co-inversora en ronda Angel',
  },
  {
    initials: 'DR',
    initialsClass: 'bg-secondary-fixed text-on-secondary-fixed',
    name: 'David Rivas',
    detail: 'CTO LexAI (Presentación pendiente)',
  },
];

const hashtags = ['#HorizonPortfolio', '#VentureCapital', '#ClubHarvardEspaña'];

const timeline = ref<TimelineEntry[]>([
  {
    id: 4,
    title: 'Café en Gran Vía, Madrid',
    badge: 'Presencial',
    badgeClass: 'bg-secondary-container/50 text-on-secondary-container',
    icon: 'local_cafe',
    iconClass: 'bg-secondary-container text-on-secondary-container',
    date: '12 Feb 2025 • Hace 12 días',
    text: 'Discutimos la estructuración de la ronda seed y la estrategia de contrataciones clave. Mencionó que Horizon está viendo gran tracción en B2B. Muy generoso con su tiempo.',
    recommendation: '"The Hard Thing About Hard Things" (Ben Horowitz)',
    tags: ['#RondaSeed', '#Lecturas', '#Consejo'],
  },
  {
    id: 3,
    title: 'Llamada telefónica (25 min)',
    badge: 'Llamada',
    badgeClass: 'bg-primary-fixed text-on-primary-fixed',
    icon: 'phone_in_talk',
    iconClass: 'bg-primary-fixed text-on-primary-fixed',
    date: '18 Ene 2025 • Hace 1 mes',
    text: 'Revisión rápida de métricas trimestrales. Alejandro aprobó la presentación para el comité y sugirió simplificar la diapositiva de adquisición de clientes.',
    mood: 'Tono muy entusiasta con los avances.',
  },
  {
    id: 2,
    title: 'Mensaje navideño enviado',
    badge: 'Mensaje',
    badgeClass: 'bg-surface-container text-on-surface-variant',
    icon: 'mark_email_read',
    iconClass: 'bg-tertiary-fixed text-on-tertiary-fixed',
    date: '20 Dic 2024',
    text: 'Felicitación personalizada recordando el viaje que hizo en verano a Santander con su familia. Respondió con un audio agradecido desde los Pirineos.',
    small: true,
  },
  {
    id: 1,
    title: 'Almuerzo de trabajo en Club Financiero',
    badge: 'Reunión',
    badgeClass: 'bg-secondary-container/50 text-on-secondary-container',
    icon: 'restaurant',
    iconClass: 'bg-secondary-container text-on-secondary-container',
    date: '15 Nov 2024',
    text: 'Sesión de mentoría profunda sobre gobierno corporativo y balance vida-trabajo. Compartió aprendizajes de sus primeros 5 años de gestión en Horizon.',
  },
]);

// Cómo se pinta en el historial cada tipo de interacción nueva
const typeStyles: Record<InteractionType, Omit<TimelineEntry, 'id' | 'date' | 'text' | 'tags'>> = {
  cafe: {
    title: 'Café / encuentro presencial',
    badge: 'Presencial',
    badgeClass: 'bg-secondary-container/50 text-on-secondary-container',
    icon: 'local_cafe',
    iconClass: 'bg-secondary-container text-on-secondary-container',
  },
  llamada: {
    title: 'Llamada',
    badge: 'Llamada',
    badgeClass: 'bg-primary-fixed text-on-primary-fixed',
    icon: 'phone_in_talk',
    iconClass: 'bg-primary-fixed text-on-primary-fixed',
  },
  mensaje: {
    title: 'Mensaje',
    badge: 'Mensaje',
    badgeClass: 'bg-surface-container text-on-surface-variant',
    icon: 'chat',
    iconClass: 'bg-tertiary-fixed text-on-tertiary-fixed',
  },
  nota: {
    title: 'Nota libre',
    badge: 'Nota',
    badgeClass: 'bg-surface-container text-on-surface-variant',
    icon: 'edit_note',
    iconClass: 'bg-surface-container-high text-on-surface-variant',
  },
};

function addInteraction({ type, text, topics }: NewInteraction) {
  timeline.value.unshift({
    id: Date.now(),
    ...typeStyles[type],
    date: 'Hoy',
    text,
    tags: topics.length ? topics.map((t) => `#${t}`) : undefined,
  });
}

const logger = ref<InstanceType<typeof InteractionLogger> | null>(null);
</script>

<template>
  <div class="relative w-full px-8 pt-8 pb-10">
    <!-- Acentos de fondo -->
    <div class="absolute -top-12 -left-12 w-96 h-96 bg-primary/5 rounded-full blur-3xl pointer-events-none"></div>
    <div class="absolute top-20 right-1/4 w-80 h-80 bg-secondary-container/20 rounded-full blur-3xl pointer-events-none"></div>

    <!-- Cabecera del contacto -->
    <div class="relative bg-surface-container-lowest rounded-xl shadow-sm p-8 flex flex-col gap-6">
      <div class="flex flex-col lg:flex-row lg:items-start justify-between gap-6">
        <div class="flex flex-col sm:flex-row items-start sm:items-center gap-6">
          <div class="relative">
            <div class="w-28 h-28 rounded-full overflow-hidden shadow-md">
              <img class="w-full h-full object-cover" :src="contact.avatar" :alt="contact.name" />
            </div>
            <span
              class="absolute bottom-1 right-1 w-6 h-6 rounded-full bg-secondary text-on-secondary flex items-center justify-center shadow-sm"
              title="Vínculo Activo & Saludable"
            >
              <span class="material-symbols-outlined text-sm" style="font-variation-settings: 'FILL' 1">check</span>
            </span>
          </div>

          <div class="flex flex-col gap-2">
            <div class="flex flex-wrap items-center gap-2">
              <h1 class="font-headline-md text-headline-md text-on-surface">{{ contact.name }}</h1>
              <span
                v-for="circle in circles"
                :key="circle.label"
                class="px-2.5 py-0.5 rounded-full font-label-sm text-label-sm"
                :class="circle.class"
              >
                {{ circle.label }}
              </span>
              <span
                class="px-2.5 py-0.5 rounded-full font-label-sm text-label-sm bg-surface-container-high text-on-surface-variant flex items-center gap-1"
              >
                <span class="material-symbols-outlined text-xs">grade</span> Tier 1
              </span>
            </div>
            <p class="font-body-md text-body-md text-on-surface-variant flex items-center gap-2">
              <span>{{ contact.role }}</span>
              <span class="w-1.5 h-1.5 rounded-full bg-outline-variant"></span>
              <span class="flex items-center gap-1">
                <span class="material-symbols-outlined text-sm">location_on</span> {{ contact.location }}
              </span>
            </p>
            <div class="flex flex-wrap items-center gap-3 mt-1">
              <a
                :href="`mailto:${contact.email}`"
                class="flex items-center gap-1.5 px-2.5 py-1 rounded bg-surface-container-low text-on-surface hover:bg-surface-container transition-colors font-label-sm text-label-sm"
              >
                <span class="material-symbols-outlined text-sm text-primary">mail</span>
                <span>{{ contact.email }}</span>
              </a>
              <a
                :href="`tel:${contact.phone.replaceAll(' ', '')}`"
                class="flex items-center gap-1.5 px-2.5 py-1 rounded bg-surface-container-low text-on-surface hover:bg-surface-container transition-colors font-label-sm text-label-sm"
              >
                <span class="material-symbols-outlined text-sm text-secondary">call</span>
                <span>{{ contact.phone }}</span>
              </a>
              <div class="flex items-center gap-1 pl-1">
                <a
                  href="#"
                  title="LinkedIn"
                  class="p-1.5 rounded bg-surface-container-low hover:bg-surface-container text-on-surface-variant hover:text-on-surface transition-colors"
                >
                  <span class="material-symbols-outlined text-sm">link</span>
                </a>
                <a
                  href="#"
                  title="Perfil Profesional"
                  class="p-1.5 rounded bg-surface-container-low hover:bg-surface-container text-on-surface-variant hover:text-on-surface transition-colors"
                >
                  <span class="material-symbols-outlined text-sm">public</span>
                </a>
              </div>
            </div>
          </div>
        </div>

        <div class="flex items-center gap-2.5 self-start lg:self-center">
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
          >
            <span class="material-symbols-outlined text-base">alarm</span>
            <span>Programar Toque</span>
          </button>
          <button
            type="button"
            title="Editar"
            class="p-2.5 rounded-lg bg-surface-container text-on-surface-variant hover:text-on-surface hover:bg-surface-container-high transition-colors"
          >
            <span class="material-symbols-outlined text-lg">edit</span>
          </button>
        </div>
      </div>

      <!-- Salud relacional -->
      <div class="w-full bg-surface-container-low rounded-lg p-4 flex flex-col md:flex-row items-center justify-between gap-4 mt-2">
        <div class="flex items-center gap-4 w-full md:w-auto">
          <div class="relative w-12 h-12 flex-shrink-0 flex items-center justify-center">
            <svg class="w-12 h-12 -rotate-90" viewBox="0 0 36 36">
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
                :stroke-dasharray="`${contact.health}, 100`"
                stroke-linecap="round"
                stroke-width="3.5"
              />
            </svg>
            <span class="absolute font-label-md text-label-md font-bold text-on-surface">{{ contact.health }}%</span>
          </div>
          <div>
            <div class="flex items-center gap-2">
              <span class="font-label-lg text-label-lg text-on-surface">Salud Relacional: Excelente</span>
              <span class="w-2 h-2 rounded-full bg-secondary"></span>
            </div>
            <p class="font-body-sm text-body-sm text-on-surface-variant">
              Conexión sólida, reciprocidad alta y compromisos al día.
            </p>
          </div>
        </div>
        <div class="flex items-center gap-8 w-full md:w-auto justify-between md:justify-end">
          <div class="flex flex-col text-left md:text-right">
            <span class="font-label-sm text-label-sm uppercase tracking-wider text-on-surface-variant">Cadencia Deseada</span>
            <span class="font-label-lg text-label-lg text-on-surface mt-0.5 flex items-center gap-1.5 md:justify-end">
              <span class="material-symbols-outlined text-sm text-primary">autorenew</span> {{ contact.cadence }}
            </span>
          </div>
          <div class="flex flex-col text-left md:text-right">
            <span class="font-label-sm text-label-sm uppercase tracking-wider text-on-surface-variant">
              Próximo Toque Sugerido
            </span>
            <span class="font-label-lg text-label-lg text-secondary mt-0.5 flex items-center gap-1.5 md:justify-end">
              <span class="material-symbols-outlined text-sm">schedule</span> {{ contact.nextTouch }}
            </span>
          </div>
        </div>
      </div>
    </div>

    <!-- Vista 360 -->
    <div class="relative grid grid-cols-1 lg:grid-cols-12 gap-8 mt-8">
      <!-- Columna izquierda -->
      <div class="lg:col-span-5 flex flex-col gap-6">
        <!-- Contexto personal -->
        <div class="bg-surface-container-lowest rounded-xl p-6 shadow-sm flex flex-col gap-5">
          <div class="flex items-center justify-between">
            <div class="flex items-center gap-2.5">
              <span class="p-2 rounded-lg bg-surface-container text-primary material-symbols-outlined text-lg">
                sentiment_satisfied
              </span>
              <h2 class="font-headline-sm text-headline-sm text-on-surface">Contexto Personal</h2>
            </div>
            <button type="button" class="text-on-surface-variant hover:text-on-surface">
              <span class="material-symbols-outlined text-lg">more_horiz</span>
            </button>
          </div>
          <div class="flex flex-col gap-4">
            <div v-for="item in personalContext" :key="item.label" class="flex items-start gap-3">
              <span class="material-symbols-outlined text-base text-on-surface-variant mt-0.5">{{ item.icon }}</span>
              <div class="flex-1 flex justify-between items-center gap-3">
                <div>
                  <span class="font-label-sm text-label-sm uppercase tracking-wider text-on-surface-variant block">
                    {{ item.label }}
                  </span>
                  <p class="font-body-md text-body-md text-on-surface mt-0.5" v-html="item.html"></p>
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

        <!-- Notas fijadas -->
        <div class="bg-surface-container-lowest rounded-xl p-6 shadow-sm flex flex-col gap-4">
          <div class="flex items-center justify-between">
            <div class="flex items-center gap-2">
              <span
                class="p-1.5 rounded-lg bg-tertiary-fixed text-tertiary material-symbols-outlined text-lg"
                style="font-variation-settings: 'FILL' 1"
              >
                push_pin
              </span>
              <h2 class="font-headline-sm text-headline-sm text-on-surface">Notas de Valor Fijadas</h2>
            </div>
            <button type="button" class="font-label-sm text-label-sm text-primary hover:underline">+ Añadir</button>
          </div>
          <div class="p-4 bg-tertiary-fixed/30 rounded-lg flex flex-col gap-2">
            <div class="flex items-center justify-between">
              <span class="font-label-sm text-label-sm text-tertiary-container uppercase tracking-wider">
                Oportunidad Estratégica
              </span>
              <span class="font-label-sm text-label-sm text-outline">Fijada el 12 Feb</span>
            </div>
            <p class="font-body-sm text-body-sm text-on-surface">
              Le interesa muchísimo el avance en
              <strong class="text-on-surface font-semibold">IA generativa aplicada a legaltech</strong>. Tiene tesis de
              inversión abierta para el Q2.
            </p>
            <div class="mt-1 pt-2 flex items-center justify-between">
              <span class="flex items-center gap-1 font-label-sm text-label-sm text-primary">
                <span class="material-symbols-outlined text-sm">outgoing_mail</span> Presentarle a David en abril
              </span>
              <span class="w-2 h-2 rounded-full bg-secondary"></span>
            </div>
          </div>
          <div class="p-4 bg-surface-container-low rounded-lg flex flex-col gap-1.5">
            <div class="flex items-center justify-between">
              <span class="font-label-sm text-label-sm text-on-surface-variant uppercase tracking-wider">
                Preferencia de Reuniones
              </span>
              <span class="font-label-sm text-label-sm text-outline">Permanente</span>
            </div>
            <p class="font-body-sm text-body-sm text-on-surface">
              Siempre prefiere mañanas temprano (08:30) para café presencial o llamadas cortas de 20 min vía audio antes de
              su comité de socios.
            </p>
          </div>
        </div>

        <!-- Proyectos & conexiones -->
        <div class="bg-surface-container-lowest rounded-xl p-6 shadow-sm flex flex-col gap-4">
          <div class="flex items-center justify-between">
            <div class="flex items-center gap-2">
              <span class="p-1.5 rounded-lg bg-surface-container text-secondary material-symbols-outlined text-lg">hub</span>
              <h2 class="font-headline-sm text-headline-sm text-on-surface">Proyectos &amp; Conexiones</h2>
            </div>
            <span class="px-2 py-0.5 rounded-full bg-surface-container font-label-sm text-label-sm text-on-surface-variant">
              4 Mutuos
            </span>
          </div>
          <div class="flex flex-col gap-3">
            <div
              v-for="person in connections"
              :key="person.name"
              class="flex items-center justify-between p-2.5 rounded-lg bg-surface-container-low"
            >
              <div class="flex items-center gap-3">
                <div
                  class="w-8 h-8 rounded-full flex items-center justify-center font-label-md text-label-md"
                  :class="person.initialsClass"
                >
                  {{ person.initials }}
                </div>
                <div>
                  <span class="font-label-lg text-label-lg text-on-surface block leading-tight">{{ person.name }}</span>
                  <span class="font-label-sm text-label-sm text-on-surface-variant">{{ person.detail }}</span>
                </div>
              </div>
              <span class="material-symbols-outlined text-on-surface-variant text-base">arrow_forward</span>
            </div>
            <div class="mt-2 flex flex-wrap gap-1.5">
              <span
                v-for="tag in hashtags"
                :key="tag"
                class="px-2 py-1 rounded bg-surface-container text-on-surface font-label-sm text-label-sm"
              >
                {{ tag }}
              </span>
            </div>
          </div>
        </div>
      </div>

      <!-- Columna derecha -->
      <div class="lg:col-span-7 flex flex-col gap-6">
        <InteractionLogger ref="logger" @save="addInteraction" />

        <!-- Historial -->
        <div class="bg-surface-container-lowest rounded-xl p-6 shadow-sm flex flex-col gap-6">
          <div class="flex items-center justify-between">
            <div class="flex items-center gap-2.5">
              <span class="p-2 rounded-lg bg-surface-container text-primary material-symbols-outlined text-lg">
                history_toggle_off
              </span>
              <div>
                <h2 class="font-headline-sm text-headline-sm text-on-surface">Memoria Relacional &amp; Historial</h2>
                <span class="font-label-sm text-label-sm text-on-surface-variant">
                  {{ 38 + timeline.length }} interacciones registradas en los últimos 3 años
                </span>
              </div>
            </div>
            <div class="flex items-center gap-2">
              <button
                type="button"
                title="Filtrar por canal"
                class="p-1.5 rounded-lg bg-surface-container-low hover:bg-surface-container text-on-surface-variant transition-colors"
              >
                <span class="material-symbols-outlined text-base">filter_list</span>
              </button>
              <button
                type="button"
                title="Exportar historial"
                class="p-1.5 rounded-lg bg-surface-container-low hover:bg-surface-container text-on-surface-variant transition-colors"
              >
                <span class="material-symbols-outlined text-base">file_download</span>
              </button>
            </div>
          </div>

          <div
            class="relative pl-6 flex flex-col gap-8 before:content-[''] before:absolute before:left-2.5 before:top-3 before:bottom-3 before:w-0.5 before:bg-surface-container-high"
          >
            <div v-for="entry in timeline" :key="entry.id" class="relative flex flex-col gap-2">
              <div
                class="absolute -left-6 top-1 w-5 h-5 rounded-full flex items-center justify-center ring-4 ring-surface-container-lowest"
                :class="entry.iconClass"
              >
                <span class="material-symbols-outlined text-xs">{{ entry.icon }}</span>
              </div>
              <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-1">
                <div class="flex items-center gap-2">
                  <span class="font-label-lg text-label-lg text-on-surface">{{ entry.title }}</span>
                  <span class="px-2 rounded-full font-label-sm text-label-sm" :class="entry.badgeClass">{{ entry.badge }}</span>
                </div>
                <span class="font-label-sm text-label-sm text-on-surface-variant">{{ entry.date }}</span>
              </div>
              <div class="bg-surface-container-low rounded-lg flex flex-col gap-2" :class="entry.small ? 'p-3.5' : 'p-4'">
                <p class="text-on-surface" :class="entry.small ? 'font-body-sm text-body-sm' : 'font-body-md text-body-md'">
                  {{ entry.text }}
                </p>
                <div v-if="entry.recommendation" class="flex items-center gap-2 pt-1">
                  <span class="material-symbols-outlined text-base text-primary">auto_stories</span>
                  <span class="font-body-sm text-body-sm text-on-surface-variant">
                    Recomendó leer: <em class="text-on-surface font-medium">{{ entry.recommendation }}</em>.
                  </span>
                </div>
                <div v-if="entry.mood" class="flex items-center gap-1 text-on-surface-variant font-label-sm text-label-sm">
                  <span class="material-symbols-outlined text-xs">sentiment_very_satisfied</span> {{ entry.mood }}
                </div>
                <div v-if="entry.tags" class="flex flex-wrap items-center gap-1.5 pt-1">
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

          <button
            type="button"
            class="w-full py-2.5 rounded-lg bg-surface-container hover:bg-surface-container-high text-on-surface-variant hover:text-on-surface font-label-md text-label-md transition-colors text-center"
          >
            Ver todas las interacciones anteriores (38 más)
          </button>
        </div>
      </div>
    </div>
  </div>
</template>
