<script setup lang="ts">
import { computed } from 'vue';
import type { Contacto } from '@/api/types';
import { circuloDe, notasDe } from '@/api/store';
import {
  canales,
  conAlfa,
  estadoProximoPaso,
  estiloEstado,
  formatoFecha,
  lista,
  nombreCompleto,
  parseFecha,
  textoDias,
  diasHasta,
} from '@/api/format';
import AvatarContacto from '../AvatarContacto.vue';

const props = defineProps<{ contacto: Contacto }>();
defineEmits<{ abrir: [id: number] }>();

const nombre = computed(() => nombreCompleto(props.contacto));
const rol = computed(() => [props.contacto.cargo, props.contacto.empresa].filter(Boolean).join(' • '));
const circulo = computed(() => circuloDe(props.contacto));
const estado = computed(() => estiloEstado[estadoProximoPaso(props.contacto)]);
const fecha = computed(() => parseFecha(props.contacto.proximoPasoFecha));
const canal = computed(() => (props.contacto.canalPreferido ? canales[props.contacto.canalPreferido] : null));

// Contexto bajo el próximo paso: la nota fijada o, si no hay, sus intereses
const contexto = computed(() => {
  const notas = notasDe(props.contacto.id);
  const nota = notas.find((n) => n.fijada) ?? notas[0];
  if (nota) return nota.contenido;
  const intereses = lista(props.contacto.intereses);
  return intereses.length ? `Intereses: ${intereses.join(', ')}` : 'Sin notas todavía.';
});
</script>

<template>
  <div class="bg-surface-container-lowest rounded-xl p-5 shadow-sm flex flex-col justify-between gap-5 transition-all hover:shadow-md">
    <div class="flex flex-col gap-3">
      <div class="flex items-start justify-between gap-3">
        <div class="flex items-center gap-3 min-w-0">
          <div class="relative">
            <AvatarContacto :nombre="nombre" :src="contacto.fotoURI" size="w-12 h-12" />
            <span
              class="absolute -bottom-0.5 -right-0.5 w-3.5 h-3.5 rounded-full border-2 border-surface-container-lowest"
              :class="estado.dot"
              :title="estado.titulo"
            ></span>
          </div>
          <div class="flex flex-col min-w-0">
            <span class="font-label-lg text-label-lg text-on-surface truncate">{{ nombre }}</span>
            <span class="font-body-sm text-body-sm text-on-surface-variant truncate">{{ rol || 'Sin cargo registrado' }}</span>
          </div>
        </div>
        <span
          v-if="circulo"
          class="inline-flex items-center px-2 py-0.5 rounded-md text-label-sm font-label-sm whitespace-nowrap"
          :style="{ backgroundColor: conAlfa(circulo.color, 0.15), color: circulo.color }"
        >
          {{ circulo.nombre }}
        </span>
      </div>

      <div class="bg-surface-container-low rounded-lg p-3 flex flex-col gap-1.5">
        <div class="flex items-center justify-between gap-2 text-label-sm font-label-sm">
          <span class="flex items-center gap-1 font-semibold" :class="estado.texto">
            <span class="material-symbols-outlined text-sm">event</span>
            {{ contacto.proximoPaso }}
          </span>
          <span v-if="fecha" class="text-on-surface-variant whitespace-nowrap" :title="formatoFecha(fecha, true)">
            {{ textoDias(diasHasta(fecha)) }}
          </span>
        </div>
        <p class="font-body-sm text-body-sm text-on-surface-variant line-clamp-2">{{ contexto }}</p>
      </div>
    </div>

    <div class="flex flex-col gap-2 pt-2">
      <div class="grid grid-cols-2 gap-2">
        <button
          type="button"
          class="flex items-center justify-center gap-1.5 py-2 px-3 rounded-lg bg-surface-container-high hover:bg-surface-container-highest text-on-surface text-label-md font-label-md transition-colors"
        >
          <span class="material-symbols-outlined text-base text-secondary">{{ canal?.icon ?? 'chat' }}</span>
          {{ canal?.label ?? 'Contactar' }}
        </button>
        <button
          type="button"
          class="flex items-center justify-center gap-1.5 py-2 px-3 rounded-lg bg-primary-container hover:bg-primary text-on-primary text-label-md font-label-md transition-colors shadow-sm"
          @click="$emit('abrir', contacto.id)"
        >
          <span class="material-symbols-outlined text-base">account_circle</span>
          Ver ficha
        </button>
      </div>
    </div>
  </div>
</template>
