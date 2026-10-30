<script setup lang="ts">
import { computed } from 'vue';
import type { Contacto } from '@/api/types';
import { circuloDe } from '@/api/store';
import {
  canales,
  conAlfa,
  diasHasta,
  estadoProximoPaso,
  estiloEstado,
  formatoFecha,
  lista,
  nombreCompleto,
  parseFecha,
  proximoCumple,
  textoDias,
} from '@/api/format';
import AvatarContacto from '../AvatarContacto.vue';

const props = defineProps<{ contacto: Contacto; striped?: boolean }>();
defineEmits<{ abrir: [id: number] }>();
const selected = defineModel<boolean>('selected', { default: false });

const nombre = computed(() => nombreCompleto(props.contacto));
const intereses = computed(() => lista(props.contacto.intereses).slice(0, 3));
const circulo = computed(() => circuloDe(props.contacto));
const cadencia = computed(() => props.contacto.cadenciaDias || circulo.value?.defectoDias || 0);
const canal = computed(() => (props.contacto.canalPreferido ? canales[props.contacto.canalPreferido] : null));
const estado = computed(() => estadoProximoPaso(props.contacto));
const fechaPaso = computed(() => parseFecha(props.contacto.proximoPasoFecha));
const cumpleEsteMes = computed(() => {
  const cumple = proximoCumple(props.contacto.date);
  return cumple !== null && cumple.dias <= 30;
});

const telefono = computed(() => props.contacto.telefono?.replace(/[^\d+]/g, '') ?? '');
</script>

<template>
  <tr class="hover:bg-surface-container-low transition-colors group" :class="{ 'bg-surface/30': striped }">
    <td class="py-4 px-6 text-center hidden sm:table-cell">
      <input v-model="selected" type="checkbox" class="w-4 h-4 rounded cursor-pointer accent-primary-container" />
    </td>

    <!-- Nombre & Intereses -->
    <td class="py-4 px-4">
      <div class="flex items-center gap-3.5">
        <div class="relative shrink-0">
          <AvatarContacto :nombre="nombre" :src="contacto.fotoURI" />
          <span
            v-if="contacto.prioritario"
            class="absolute -bottom-0.5 -right-0.5 w-4 h-4 rounded-full bg-secondary text-on-secondary flex items-center justify-center ring-2 ring-surface-container-lowest"
            title="Prioritario"
          >
            <span class="material-symbols-outlined text-[10px]">star</span>
          </span>
        </div>
        <div class="flex flex-col min-w-0">
          <button
            type="button"
            class="font-label-lg text-label-lg text-on-surface group-hover:text-primary transition-colors flex items-center gap-1.5 text-left"
            @click="$emit('abrir', contacto.id)"
          >
            {{ nombre }}
            <span v-if="cumpleEsteMes" class="material-symbols-outlined text-sm text-tertiary-container" title="Cumpleaños próximo">
              cake
            </span>
          </button>
          <div v-if="intereses.length" class="flex items-center gap-1.5 mt-1 flex-wrap">
            <span
              v-for="interes in intereses"
              :key="interes"
              class="px-2 py-0.5 rounded-full bg-surface-container font-label-sm text-label-sm text-on-surface-variant"
            >
              {{ interes }}
            </span>
          </div>
          <!-- En pantallas estrechas las columnas de círculo y próximo paso se ocultan: resumen aquí -->
          <div v-if="circulo || fechaPaso" class="md:hidden flex items-center gap-2 mt-1 flex-wrap font-label-sm text-label-sm">
            <span v-if="circulo" class="flex items-center gap-1" :style="{ color: circulo.color }">
              <span class="w-1.5 h-1.5 rounded-full" :style="{ backgroundColor: circulo.color }"></span>
              {{ circulo.nombre }}
            </span>
            <span v-if="fechaPaso" class="sm:hidden" :class="estiloEstado[estado].texto">
              {{ textoDias(diasHasta(fechaPaso)) }}
            </span>
          </div>
        </div>
      </div>
    </td>

    <!-- Círculo -->
    <td class="py-4 px-4 hidden md:table-cell">
      <span
        v-if="circulo"
        class="inline-flex items-center gap-1.5 px-2.5 py-1 rounded-full font-label-sm text-label-sm font-semibold"
        :style="{ backgroundColor: conAlfa(circulo.color, 0.15), color: circulo.color }"
      >
        <span class="w-1.5 h-1.5 rounded-full" :style="{ backgroundColor: circulo.color }"></span>
        {{ circulo.nombre }}
      </span>
      <span v-else class="font-label-sm text-label-sm text-outline">Sin círculo</span>
    </td>

    <!-- Empresa & Rol -->
    <td class="py-4 px-4 hidden lg:table-cell">
      <div class="flex flex-col">
        <span class="font-label-md text-label-md text-on-surface font-semibold">{{ contacto.cargo ?? '—' }}</span>
        <span class="font-body-sm text-body-sm text-on-surface-variant">{{ contacto.empresa ?? '' }}</span>
      </div>
    </td>

    <!-- Cadencia -->
    <td class="py-4 px-4 hidden 2xl:table-cell">
      <span class="font-body-sm text-body-sm text-on-surface-variant bg-surface-container px-2.5 py-1 rounded-md">
        {{ cadencia ? `Cada ${cadencia} días` : 'Sin definir' }}
      </span>
    </td>

    <!-- Canal preferido -->
    <td class="py-4 px-4 hidden xl:table-cell">
      <div class="flex flex-col">
        <span class="font-label-md text-label-md text-on-surface font-medium flex items-center gap-1.5">
          <span class="material-symbols-outlined text-base text-primary">{{ canal?.icon ?? 'help' }}</span>
          {{ canal?.label ?? 'Sin preferencia' }}
        </span>
        <span class="font-body-sm text-body-sm text-on-surface-variant truncate max-w-[220px]">
          {{ contacto.email ?? contacto.telefono ?? '' }}
        </span>
      </div>
    </td>

    <!-- Próximo paso -->
    <td class="py-4 px-4 hidden sm:table-cell">
      <div v-if="contacto.proximoPaso || fechaPaso" class="flex items-start gap-2.5">
        <div
          class="w-2 h-2 rounded-full shrink-0 mt-1.5"
          :class="estiloEstado[estado].dot"
          :title="estiloEstado[estado].titulo"
        ></div>
        <div class="flex flex-col">
          <span class="font-label-md text-label-md text-on-surface font-medium">{{ contacto.proximoPaso ?? 'Próximo toque' }}</span>
          <span
            v-if="fechaPaso"
            class="font-body-sm text-body-sm"
            :class="estiloEstado[estado].texto"
            :title="formatoFecha(fechaPaso, true)"
          >
            {{ textoDias(diasHasta(fechaPaso)) }}
          </span>
        </div>
      </div>
      <span v-else class="font-label-sm text-label-sm text-outline">Sin próximo paso</span>
    </td>

    <!-- Acciones -->
    <td class="py-4 px-4 sm:px-6 text-right">
      <div class="flex items-center justify-end gap-1 text-on-surface-variant">
        <a
          v-if="telefono"
          :href="`tel:${telefono}`"
          title="Llamar"
          class="hidden sm:block p-1.5 hover:text-primary hover:bg-surface-container rounded-lg transition-colors"
        >
          <span class="material-symbols-outlined text-lg">call</span>
        </a>
        <a
          v-if="telefono"
          :href="`https://wa.me/${telefono.replace('+', '')}`"
          target="_blank"
          rel="noopener"
          title="WhatsApp"
          class="hidden sm:block p-1.5 hover:text-secondary hover:bg-surface-container rounded-lg transition-colors"
        >
          <span class="material-symbols-outlined text-lg">chat</span>
        </a>
        <a
          v-if="contacto.email"
          :href="`mailto:${contacto.email}`"
          title="Email"
          class="hidden sm:block p-1.5 hover:text-primary hover:bg-surface-container rounded-lg transition-colors"
        >
          <span class="material-symbols-outlined text-lg">mail</span>
        </a>
        <button
          type="button"
          title="Ver ficha"
          class="p-1.5 hover:text-on-surface hover:bg-surface-container rounded-lg transition-colors"
          @click="$emit('abrir', contacto.id)"
        >
          <span class="material-symbols-outlined text-lg">open_in_new</span>
        </button>
      </div>
    </td>
  </tr>
</template>
