<script setup lang="ts">
import { computed } from 'vue';
import type { Contacto } from '@/api/types';
import { circuloDe } from '@/api/store';
import {
  canales,
  estiloCirculo,
  textoCirculo,
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
import { hrefFicha } from '@/api/rutas';
import AvatarContacto from '../AvatarContacto.vue';

const props = defineProps<{ contacto: Contacto; striped?: boolean }>();
defineEmits<{ abrir: [id: number] }>();

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
  <tr class="hover:bg-surface-container-low transition-colors group max-sm:relative" :class="{ 'bg-surface/30': striped }">
    <!-- En móvil toda la fila abre la ficha: el enlace del nombre se estira sobre ella -->
    <!-- Nombre & Intereses -->
    <td class="py-4 px-4 sm:px-6">
      <div class="flex items-center gap-3.5">
        <div class="relative shrink-0">
          <AvatarContacto :nombre="nombre" :src="contacto.fotoURI" />
          <span
            v-if="contacto.prioritario"
            class="absolute -bottom-0.5 -right-0.5 w-4 h-4 rounded-full bg-secondary text-on-secondary flex items-center justify-center ring-2 ring-surface-container-lowest"
            role="img"
            aria-label="Contacto prioritario"
            title="Prioritario"
          >
            <span aria-hidden="true" class="material-symbols-outlined text-[10px]">star</span>
          </span>
        </div>
        <div class="flex flex-col min-w-0">
          <a
            :href="hrefFicha(contacto.id)"
            class="font-label-lg text-label-lg text-on-surface group-hover:text-primary transition-colors flex items-center gap-1.5 text-left break-words py-0.5 rounded max-sm:after:absolute max-sm:after:inset-0"
          >
            {{ nombre }}
            <span v-if="cumpleEsteMes" aria-hidden="true" class="material-symbols-outlined text-sm text-tertiary-container" title="Cumpleaños próximo">
              cake
            </span>
            <span v-if="cumpleEsteMes" class="sr-only">(cumpleaños próximo)</span>
          </a>
          <div v-if="intereses.length" class="flex items-center gap-1.5 mt-1 flex-wrap">
            <span
              v-for="interes in intereses"
              :key="interes"
              :title="interes"
              class="max-w-40 truncate px-2 py-0.5 rounded-full bg-surface-container font-label-sm text-label-sm text-on-surface-variant"
            >
              {{ interes }}
            </span>
          </div>
          <!-- En pantallas estrechas las columnas de círculo y próximo paso se ocultan: resumen aquí -->
          <div v-if="circulo || fechaPaso" class="md:hidden flex items-center gap-2 mt-1 flex-wrap font-label-sm text-label-sm">
            <span v-if="circulo" class="flex items-center gap-1" :style="{ color: textoCirculo(circulo.color) }">
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
        class="inline-flex items-center gap-1.5 px-2.5 py-1 rounded-full font-label-sm text-label-sm font-semibold whitespace-nowrap"
        :style="estiloCirculo(circulo.color)"
      >
        <span class="w-1.5 h-1.5 rounded-full" :style="{ backgroundColor: circulo.color }"></span>
        {{ circulo.nombre }}
      </span>
      <span v-else class="font-label-sm text-label-sm text-outline">Sin círculo</span>
    </td>

    <!-- Empresa & Rol -->
    <td class="py-4 px-4 hidden xl:table-cell">
      <div class="flex flex-col">
        <span class="font-label-md text-label-md text-on-surface font-semibold">{{ contacto.cargo ?? '—' }}</span>
        <span class="font-body-sm text-body-sm text-on-surface-variant">{{ contacto.empresa ?? '' }}</span>
      </div>
    </td>

    <!-- Cadencia -->
    <td class="py-4 px-4 hidden min-[1800px]:table-cell">
      <span class="font-body-sm text-body-sm text-on-surface-variant bg-surface-container px-2.5 py-1 rounded-md">
        {{ cadencia ? `Cada ${cadencia} días` : 'Sin definir' }}
      </span>
    </td>

    <!-- Canal preferido -->
    <td class="py-4 px-4 hidden 2xl:table-cell">
      <div class="flex flex-col">
        <span class="font-label-md text-label-md text-on-surface font-medium flex items-center gap-1.5">
          <span aria-hidden="true" class="material-symbols-outlined text-base text-primary">{{ canal?.icon ?? 'help' }}</span>
          {{ canal?.label ?? 'Sin preferencia' }}
        </span>
        <span class="font-body-sm text-body-sm text-on-surface-variant truncate max-w-[200px]">
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
          aria-hidden="true"
        ></div>
        <div class="flex flex-col">
          <!-- El punto de color solo se ve: su significado también se lee -->
          <span class="sr-only">{{ estiloEstado[estado].titulo }}:</span>
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
    <td class="py-4 px-4 sm:px-6 text-right max-sm:relative max-sm:z-10">
      <div class="flex items-center justify-end gap-1 text-on-surface-variant">
        <a
          v-if="telefono"
          :href="`tel:${telefono}`"
          title="Llamar"
          :aria-label="`Llamar a ${nombre}`"
          class="hidden xl:block p-1.5 hover:text-primary hover:bg-surface-container rounded-lg transition-colors"
        >
          <span aria-hidden="true" class="material-symbols-outlined text-lg">call</span>
        </a>
        <a
          v-if="telefono"
          :href="`https://wa.me/${telefono.replace('+', '')}`"
          target="_blank"
          rel="noopener"
          title="WhatsApp"
          :aria-label="`Escribir por WhatsApp a ${nombre} (se abre en otra pestaña)`"
          class="hidden xl:block p-1.5 hover:text-secondary hover:bg-surface-container rounded-lg transition-colors"
        >
          <span aria-hidden="true" class="material-symbols-outlined text-lg">chat</span>
        </a>
        <a
          v-if="contacto.email"
          :href="`mailto:${contacto.email}`"
          title="Email"
          :aria-label="`Enviar un email a ${nombre}`"
          class="hidden xl:block p-1.5 hover:text-primary hover:bg-surface-container rounded-lg transition-colors"
        >
          <span aria-hidden="true" class="material-symbols-outlined text-lg">mail</span>
        </a>
        <button
          type="button"
          title="Ver ficha"
          :aria-label="`Ver la ficha de ${nombre}`"
          class="p-1.5 hover:text-on-surface hover:bg-surface-container rounded-lg transition-colors"
          @click="$emit('abrir', contacto.id)"
        >
          <span aria-hidden="true" class="material-symbols-outlined text-lg">open_in_new</span>
        </button>
      </div>
    </td>
  </tr>
</template>
