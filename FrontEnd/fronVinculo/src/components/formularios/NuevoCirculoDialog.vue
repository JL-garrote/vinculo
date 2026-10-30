<script setup lang="ts">
import { ref } from 'vue';
import ModalBase from '../ModalBase.vue';
import { api, ApiError, mensajeError } from '@/api/vinculo';
import { store } from '@/api/store';
import type { Circulo } from '@/api/types';

const emit = defineEmits<{ creado: [circulo: Circulo]; cerrar: [] }>();

const nombre = ref('');
const color = ref('#4338CA');
const cadencia = ref<number | null>(30);
const guardando = ref(false);
const error = ref<string | null>(null);

async function guardar() {
  if (!nombre.value.trim() || guardando.value) return;
  guardando.value = true;
  error.value = null;
  try {
    const circulo = await api.crearCirculo({
      nombre: nombre.value.trim(),
      color: color.value,
      cadenciaDefectoDias: cadencia.value || null,
    });
    store.circulos.push(circulo);
    store.circulos.sort((a, b) => a.orden - b.orden);
    emit('creado', circulo);
  } catch (e) {
    error.value = e instanceof ApiError && e.status === 409 ? 'Ya existe un círculo con ese nombre.' : mensajeError(e);
  } finally {
    guardando.value = false;
  }
}

const campo =
  'w-full bg-surface-container-low text-on-surface font-body-sm text-body-sm py-2.5 px-3 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary/20';
const etiqueta = 'font-label-sm text-label-sm uppercase tracking-wider text-on-surface-variant';
</script>

<template>
  <ModalBase titulo="Nuevo círculo" ancho="max-w-md" @cerrar="emit('cerrar')">
    <form class="flex flex-col gap-4" @submit.prevent="guardar">
      <label class="flex flex-col gap-1.5">
        <span :class="etiqueta">Nombre *</span>
        <input v-model="nombre" maxlength="100" required autofocus placeholder="Ej. Antiguos compañeros" :class="campo" />
      </label>
      <div class="grid grid-cols-2 gap-3">
        <label class="flex flex-col gap-1.5">
          <span :class="etiqueta">Color</span>
          <div class="flex items-center gap-2">
            <input v-model="color" type="color" class="w-11 h-10 rounded-lg cursor-pointer bg-transparent" />
            <span class="font-label-md text-label-md text-on-surface-variant">{{ color.toUpperCase() }}</span>
          </div>
        </label>
        <label class="flex flex-col gap-1.5">
          <span :class="etiqueta">Cadencia por defecto (días)</span>
          <input v-model.number="cadencia" type="number" min="1" max="3650" :class="campo" />
        </label>
      </div>

      <div class="flex items-center gap-2 p-3 rounded-lg bg-surface-container-low">
        <span class="font-label-sm text-label-sm text-on-surface-variant">Vista previa:</span>
        <span
          class="inline-flex items-center gap-1.5 px-2.5 py-1 rounded-full font-label-sm text-label-sm font-semibold"
          :style="{ backgroundColor: `${color}26`, color }"
        >
          <span class="w-1.5 h-1.5 rounded-full" :style="{ backgroundColor: color }"></span>
          {{ nombre || 'Nuevo círculo' }}
        </span>
      </div>

      <p v-if="error" class="p-3 rounded-lg bg-error-container text-on-error-container font-body-sm text-body-sm">{{ error }}</p>

      <div class="flex items-center justify-end gap-2">
        <button
          type="button"
          class="px-4 py-2 rounded-lg bg-surface-container text-on-surface-variant hover:text-on-surface font-label-md text-label-md"
          @click="emit('cerrar')"
        >
          Cancelar
        </button>
        <button
          type="submit"
          class="px-4 py-2 rounded-lg bg-primary-container hover:bg-primary text-on-primary font-label-md text-label-md shadow-sm disabled:opacity-50"
          :disabled="!nombre.trim() || guardando"
        >
          {{ guardando ? 'Guardando…' : 'Crear círculo' }}
        </button>
      </div>
    </form>
  </ModalBase>
</template>
