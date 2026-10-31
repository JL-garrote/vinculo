<script setup lang="ts">
import { computed } from 'vue';
import type { PipelineContacto } from '@/api/types';
import { circuloDe, contactoPorId } from '@/api/store';
import { conAlfa, diasHasta, formatoFecha, nombreCompleto, parseFecha, prioridades, textoDias } from '@/api/format';
import AvatarContacto from '../AvatarContacto.vue';

const props = defineProps<{ entrada: PipelineContacto }>();
defineEmits<{ editar: [entrada: PipelineContacto]; abrir: [contactoId: number] }>();

const contacto = computed(() => contactoPorId(props.entrada.contactoID));
const nombre = computed(() =>
  contacto.value ? nombreCompleto(contacto.value) : `Contacto #${props.entrada.contactoID}`,
);
const rol = computed(() => [contacto.value?.cargo, contacto.value?.empresa].filter(Boolean).join(' • '));
const circulo = computed(() => (contacto.value ? circuloDe(contacto.value) : undefined));
const prioridad = computed(() => prioridades[props.entrada.prioridad] ?? prioridades.media);

const fechaAccion = computed(() => parseFecha(props.entrada.siguienteAccionFecha));
const diasAccion = computed(() => (fechaAccion.value ? diasHasta(fechaAccion.value) : null));
const claseAccion = computed(() => {
  if (diasAccion.value === null) return 'text-on-surface-variant';
  if (diasAccion.value < 0) return 'text-error font-semibold';
  if (diasAccion.value <= 7) return 'text-tertiary-container font-medium';
  return 'text-secondary font-medium';
});

const diasEnEtapa = computed(() => {
  const desde = parseFecha(props.entrada.etapaDesdeFecha);
  return desde ? -diasHasta(desde) : 0;
});
</script>

<template>
  <div
    class="p-4 rounded-xl bg-surface-container-lowest shadow-sm hover:shadow-md transition-all cursor-grab active:cursor-grabbing group flex flex-col gap-3"
    :class="{ 'border-l-4 border-secondary': entrada.etapa === 'aliado' }"
    @click="$emit('editar', entrada)"
  >
    <!-- Cabecera -->
    <div class="flex items-start justify-between gap-3">
      <AvatarContacto :nombre="nombre" :src="contacto?.fotoURI" />
      <div class="flex flex-col flex-1 min-w-0">
        <div class="flex items-center justify-between gap-2">
          <h4 class="font-label-lg text-label-lg text-on-surface truncate group-hover:text-primary transition-colors">
            {{ nombre }}
          </h4>
          <span class="px-1.5 py-0.5 rounded font-label-sm text-label-sm shrink-0" :class="prioridad.badge">
            {{ prioridad.label }}
          </span>
        </div>
        <span class="font-body-sm text-body-sm text-on-surface-variant truncate">{{ rol || 'Sin cargo registrado' }}</span>
      </div>
    </div>

    <!-- Siguiente acción -->
    <div class="p-2.5 rounded-lg bg-surface-container-low flex flex-col gap-1.5">
      <div class="flex flex-wrap items-center justify-between gap-x-2 gap-y-0.5 font-label-sm text-label-sm">
        <span class="text-on-surface-variant flex items-center gap-1">
          <span class="material-symbols-outlined text-xs text-primary">flag</span> Siguiente acción
        </span>
        <span
          v-if="fechaAccion && diasAccion !== null"
          class="whitespace-nowrap"
          :class="claseAccion"
          :title="formatoFecha(fechaAccion, true)"
        >
          {{ textoDias(diasAccion) }}
        </span>
      </div>
      <p class="font-body-sm text-body-sm" :class="entrada.siguienteAccion ? 'text-on-surface' : 'text-outline italic'">
        {{ entrada.siguienteAccion ?? 'Sin definir' }}
      </p>
    </div>

    <!-- Pie -->
    <div class="flex flex-wrap items-center justify-between gap-2 text-on-surface-variant pt-1">
      <span
        v-if="circulo"
        class="px-2 py-0.5 rounded-full font-label-sm text-label-sm"
        :style="{ backgroundColor: conAlfa(circulo.color, 0.15), color: circulo.color }"
      >
        {{ circulo.nombre }}
      </span>
      <span v-else></span>
      <span class="font-label-sm text-label-sm flex items-center gap-1" title="Tiempo en esta etapa">
        <span class="material-symbols-outlined text-xs">hourglass_top</span>
        {{ diasEnEtapa === 0 ? 'Hoy' : `${diasEnEtapa} d en etapa` }}
      </span>
    </div>
  </div>
</template>
