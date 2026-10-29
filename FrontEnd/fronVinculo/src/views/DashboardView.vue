<script setup lang="ts">
import { computed } from 'vue';
import KpiCard, { type Kpi } from '@/components/dashboard/KpiCard.vue';
import AttentionCard from '@/components/dashboard/AttentionCard.vue';
import { contactoPorId, store } from '@/api/store';
import {
  estadoProximoPaso,
  formatoFecha,
  nombreCompleto,
  parseFecha,
  tiposNota,
} from '@/api/format';

defineEmits<{ abrir: [id: number] }>();

const hoy = new Date();
const fechaHoy = hoy.toLocaleDateString('es-ES', { weekday: 'long', day: 'numeric', month: 'long' });
const saludo = hoy.getHours() < 14 ? 'Buenos días' : hoy.getHours() < 21 ? 'Buenas tardes' : 'Buenas noches';
const nombrePerfil = computed(() => store.perfil?.nombre.split(' ')[0] ?? '');

const total = computed(() => store.contactos.length);
const pct = (n: number) => (total.value ? Math.round((n / total.value) * 100) : 0);

const conPaso = computed(() => store.contactos.filter((c) => estadoProximoPaso(c) !== 'sin-fecha'));
const vencidos = computed(() => store.contactos.filter((c) => estadoProximoPaso(c) === 'vencido'));
const atencion = computed(() => store.contactos.filter((c) => ['vencido', 'pronto'].includes(estadoProximoPaso(c))));

const kpis = computed<Kpi[]>(() => {
  const conCirculo = store.contactos.filter((c) => c.circuloID > 0).length;
  const circulosUsados = new Set(store.contactos.map((c) => c.circuloID).filter((id) => id > 0)).size;
  const esteMes = store.contactos.filter((c) => {
    const d = parseFecha(c.creadoEn);
    return d && d.getMonth() === hoy.getMonth() && d.getFullYear() === hoy.getFullYear();
  }).length;
  const prioritarios = store.contactos.filter((c) => c.prioritario).length;
  const cadencias = store.contactos.map((c) => c.cadenciaDias).filter((d) => d > 0);
  const media = cadencias.length ? Math.round(cadencias.reduce((a, b) => a + b, 0) / cadencias.length) : 0;
  const meta = store.perfil?.cadenciaDefectoDias || 30;

  return [
    {
      label: 'Contactos Totales',
      value: String(total.value),
      detail: `en ${circulosUsados} círculos`,
      icon: 'contacts',
      iconClass: 'bg-surface-container text-on-surface-variant',
      badge: `+${esteMes} este mes`,
      badgeClass: 'px-2 py-0.5 rounded-full bg-secondary/10 text-secondary',
      progress: pct(conCirculo),
      progressClass: 'bg-primary-container',
    },
    {
      label: 'Contactos Prioritarios',
      value: String(prioritarios),
      valueClass: 'text-secondary',
      detail: 'marcados como clave',
      icon: 'favorite',
      iconClass: 'bg-secondary/15 text-secondary',
      badge: `${pct(prioritarios)}% de tu red`,
      badgeClass: 'text-secondary font-medium',
      progress: pct(prioritarios),
      progressClass: 'bg-secondary',
    },
    {
      label: 'Próximos Pasos',
      value: String(conPaso.value.length),
      detail: `${vencidos.value.length} vencidos`,
      detailClass: vencidos.value.length ? 'text-tertiary' : undefined,
      icon: 'schedule',
      iconClass: 'bg-tertiary-fixed text-tertiary-container',
      badge: vencidos.value.length ? 'Urgente' : 'Al día',
      badgeClass: vencidos.value.length
        ? 'px-2 py-0.5 rounded-full bg-error-container text-on-error-container'
        : 'px-2 py-0.5 rounded-full bg-secondary/10 text-secondary',
      progress: pct(conPaso.value.length),
      progressClass: 'bg-tertiary-container',
    },
    {
      label: 'Cadencia Media',
      value: media ? String(media) : '—',
      detail: 'días entre contactos',
      icon: 'avg_pace',
      iconClass: 'bg-surface-container text-on-surface-variant',
      badge: `Meta: ≤${meta} d`,
      badgeClass: 'text-on-surface-variant',
      progress: media ? Math.min(100, Math.round((meta / media) * 100)) : 0,
      progressClass: 'bg-primary',
    },
  ];
});

// Contactos con próximo paso, del más urgente al más lejano
const proximos = computed(() =>
  [...conPaso.value]
    .sort((a, b) => (parseFecha(a.proximoPasoFecha)?.getTime() ?? 0) - (parseFecha(b.proximoPasoFecha)?.getTime() ?? 0))
    .slice(0, 3),
);

// Últimas notas registradas
const actividad = computed(() =>
  [...store.notas]
    .sort((a, b) => (parseFecha(b.creadoEn)?.getTime() ?? 0) - (parseFecha(a.creadoEn)?.getTime() ?? 0))
    .slice(0, 5)
    .map((nota) => {
      const contacto = contactoPorId(nota.contactoID);
      const fecha = parseFecha(nota.creadoEn);
      return {
        nota,
        tipo: tiposNota[nota.tipo] ?? tiposNota.general,
        contacto,
        nombre: contacto ? nombreCompleto(contacto) : `Contacto #${nota.contactoID}`,
        fecha: fecha ? formatoFecha(fecha, true) : '',
      };
    }),
);

const circulos = computed(() =>
  store.circulos.map((circulo) => {
    const count = store.contactos.filter((c) => c.circuloID === circulo.id).length;
    return { ...circulo, count, percent: pct(count) };
  }),
);

const conCirculo = computed(() => pct(store.contactos.filter((c) => c.circuloID > 0).length));
</script>

<template>
  <div class="px-8 py-8 flex flex-col gap-8 max-w-[1600px] mx-auto w-full">
    <!-- Bienvenida -->
    <div class="flex flex-col md:flex-row md:items-end justify-between gap-4">
      <div class="flex flex-col gap-1">
        <div class="flex items-center gap-2">
          <span
            class="inline-flex items-center px-2.5 py-0.5 rounded-full text-label-sm font-label-sm"
            :class="vencidos.length ? 'bg-error-container text-on-error-container' : 'bg-secondary/10 text-secondary'"
          >
            <span
              class="w-1.5 h-1.5 rounded-full mr-1.5 animate-pulse"
              :class="vencidos.length ? 'bg-error' : 'bg-secondary'"
            ></span>
            {{ vencidos.length ? `${vencidos.length} pasos vencidos` : 'Ritmo Relacional al día' }}
          </span>
          <span class="text-on-surface-variant font-label-sm text-label-sm first-letter:uppercase">{{ fechaHoy }}</span>
        </div>
        <h1 class="text-headline-lg font-headline-lg text-on-surface tracking-tight">
          ¡{{ saludo }}{{ nombrePerfil ? `, ${nombrePerfil}` : '' }}!
        </h1>
        <p class="text-body-md font-body-md text-on-surface-variant">
          Tienes <span class="text-primary-container font-semibold">{{ atencion.length }} relaciones</span> que requieren
          atención esta semana y <span class="text-on-surface font-semibold">{{ conPaso.length }} próximos pasos</span> en tu
          radar.
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

    <!-- Próximos pasos -->
    <div class="flex flex-col gap-4">
      <div class="flex items-center justify-between">
        <div class="flex items-center gap-3">
          <div class="w-2.5 h-2.5 rounded-full bg-tertiary-container"></div>
          <h2 class="text-headline-sm font-headline-sm text-on-surface">Requieren tu atención</h2>
          <span class="text-body-sm font-body-sm text-on-surface-variant hidden sm:inline">
            • Mantén el vínculo antes de que se enfríe
          </span>
        </div>
      </div>
      <div v-if="proximos.length" class="grid grid-cols-1 md:grid-cols-3 gap-5">
        <AttentionCard v-for="contacto in proximos" :key="contacto.id" :contacto="contacto" @abrir="$emit('abrir', $event)" />
      </div>
      <p
        v-else
        class="bg-surface-container-lowest rounded-xl p-6 shadow-sm text-center font-body-sm text-body-sm text-on-surface-variant"
      >
        Ningún contacto tiene un próximo paso con fecha.
      </p>
    </div>

    <!-- Contenido principal (60% / 40%) -->
    <div class="grid grid-cols-1 lg:grid-cols-12 gap-8 items-start">
      <!-- Notas recientes -->
      <div class="lg:col-span-7 flex flex-col gap-5">
        <div class="flex items-center justify-between">
          <div class="flex items-center gap-2">
            <span class="material-symbols-outlined text-primary-container text-xl">history_edu</span>
            <h2 class="text-headline-sm font-headline-sm text-on-surface">Notas Recientes</h2>
          </div>
          <span class="font-label-sm text-label-sm text-on-surface-variant">{{ store.notas.length }} en total</span>
        </div>

        <div class="bg-surface-container-lowest rounded-xl p-6 shadow-sm flex flex-col gap-6">
          <p v-if="!actividad.length" class="text-center font-body-sm text-body-sm text-on-surface-variant">
            Todavía no hay notas registradas.
          </p>
          <div v-for="(item, i) in actividad" :key="item.nota.id" class="flex gap-4 relative">
            <div class="flex flex-col items-center">
              <div class="w-10 h-10 rounded-full flex items-center justify-center z-10" :class="item.tipo.clase">
                <span class="material-symbols-outlined text-lg">{{ item.tipo.icon }}</span>
              </div>
              <div v-if="i < actividad.length - 1" class="w-0.5 bg-surface-container-high h-full my-1"></div>
            </div>
            <div class="flex flex-col gap-1.5 flex-1" :class="{ 'pb-6': i < actividad.length - 1 }">
              <div class="flex items-center justify-between flex-wrap gap-2">
                <div class="flex items-center gap-2">
                  <span class="font-label-lg text-label-lg text-on-surface">{{ item.nota.titulo ?? item.tipo.label }}</span>
                  <button
                    v-if="item.contacto"
                    type="button"
                    class="px-2 py-0.5 rounded text-label-sm font-label-sm bg-surface-container text-on-surface-variant hover:text-primary"
                    @click="$emit('abrir', item.contacto.id)"
                  >
                    {{ item.nombre }}
                  </button>
                </div>
                <span class="text-label-sm font-label-sm text-on-surface-variant">{{ item.fecha }}</span>
              </div>
              <p class="text-body-sm font-body-sm text-on-surface-variant bg-surface-container-low p-3.5 rounded-lg">
                {{ item.nota.contenido }}
              </p>
              <div v-if="item.nota.fijada" class="flex items-center gap-1 text-label-sm font-label-sm text-tertiary-container">
                <span class="material-symbols-outlined text-xs">push_pin</span> Fijada
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Círculos -->
      <div class="lg:col-span-5 flex flex-col gap-5">
        <div class="flex items-center justify-between">
          <div class="flex items-center gap-2">
            <span class="material-symbols-outlined text-secondary text-xl">hub</span>
            <h2 class="text-headline-sm font-headline-sm text-on-surface">Círculos de tu Red</h2>
          </div>
        </div>

        <div class="bg-surface-container-lowest rounded-xl p-6 shadow-sm flex flex-col gap-6">
          <div class="flex items-center justify-between p-4 rounded-xl bg-surface-container-low gap-4">
            <div class="flex flex-col">
              <span class="text-label-sm font-label-sm text-on-surface-variant uppercase tracking-wider">
                Red Organizada
              </span>
              <span class="text-headline-md font-headline-md text-on-surface mt-0.5">{{ conCirculo }}% en círculos</span>
              <span class="text-body-sm font-body-sm text-secondary flex items-center gap-1 mt-1">
                <span class="material-symbols-outlined text-sm">verified</span>
                {{ total }} contactos en {{ store.circulos.length }} círculos
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
                  :stroke-dasharray="`${conCirculo}, 100`"
                  stroke-linecap="round"
                  stroke-width="3.5"
                />
              </svg>
              <span class="absolute font-label-lg text-label-lg text-on-surface">{{ conCirculo }}%</span>
            </div>
          </div>

          <div class="flex flex-col gap-4">
            <span class="text-label-md font-label-md text-on-surface-variant uppercase tracking-wider">
              Desglose por Círculo
            </span>
            <div v-for="circulo in circulos" :key="circulo.id" class="flex flex-col gap-1.5">
              <div class="flex items-center justify-between text-body-sm font-body-sm">
                <div class="flex items-center gap-2">
                  <span class="w-2.5 h-2.5 rounded-full" :style="{ backgroundColor: circulo.color }"></span>
                  <span class="font-medium text-on-surface">{{ circulo.nombre }}</span>
                  <span class="text-on-surface-variant text-label-sm font-label-sm">({{ circulo.count }})</span>
                </div>
                <div class="flex items-center gap-2 font-label-sm text-label-sm">
                  <span class="font-semibold" :style="{ color: circulo.color }">{{ circulo.percent }}%</span>
                  <span class="text-on-surface-variant">· cada {{ circulo.defectoDias }} d</span>
                </div>
              </div>
              <div class="w-full bg-surface-container h-2 rounded-full overflow-hidden">
                <div
                  class="h-full rounded-full"
                  :style="{ width: `${circulo.percent}%`, backgroundColor: circulo.color }"
                ></div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
