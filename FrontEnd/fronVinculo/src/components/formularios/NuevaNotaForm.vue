<script setup lang="ts">
import { ref } from 'vue';
import { api, mensajeError } from '@/api/vinculo';
import { store } from '@/api/store';
import { tiposNota } from '@/api/format';
import type { TipoNota } from '@/api/types';

const props = defineProps<{ contactoId: number }>();
const emit = defineEmits<{ creada: []; cancelar: [] }>();

const tipo = ref<TipoNota>('general');
const titulo = ref('');
const contenido = ref('');
const fijada = ref(false);
const guardando = ref(false);
const error = ref<string | null>(null);

async function guardar() {
  if (!contenido.value.trim() || guardando.value) return;
  guardando.value = true;
  error.value = null;
  try {
    const nota = await api.crearNota({
      contactoID: props.contactoId,
      tipo: tipo.value,
      titulo: titulo.value.trim() || null,
      contenido: contenido.value.trim(),
      fijada: fijada.value,
    });
    store.notas.push(nota);
    emit('creada');
  } catch (e) {
    error.value = mensajeError(e);
  } finally {
    guardando.value = false;
  }
}

const campo =
  'w-full bg-surface text-on-surface font-body-sm text-body-sm py-2 px-3 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary/20';
</script>

<template>
  <form class="p-4 rounded-lg bg-surface-container-low flex flex-col gap-3" @submit.prevent="guardar">
    <div class="flex items-center gap-2">
      <select v-model="tipo" class="w-auto" :class="campo">
        <option v-for="(t, key) in tiposNota" :key="key" :value="key">{{ t.label }}</option>
      </select>
      <input v-model="titulo" maxlength="200" placeholder="Título (opcional)" :class="campo" />
    </div>
    <textarea
      v-model="contenido"
      rows="3"
      required
      autofocus
      placeholder="¿Qué quieres recordar de este contacto?"
      class="resize-none"
      :class="campo"
    ></textarea>
    <p v-if="error" class="font-body-sm text-body-sm text-error">{{ error }}</p>
    <div class="flex items-center justify-between gap-2">
      <label class="flex items-center gap-2 cursor-pointer font-label-sm text-label-sm text-on-surface-variant">
        <input v-model="fijada" type="checkbox" class="w-4 h-4 accent-primary-container" /> Fijar nota
      </label>
      <div class="flex items-center gap-2">
        <button
          type="button"
          class="px-3 py-1.5 rounded-lg bg-surface-container text-on-surface-variant hover:text-on-surface font-label-md text-label-md"
          @click="emit('cancelar')"
        >
          Cancelar
        </button>
        <button
          type="submit"
          class="px-3 py-1.5 rounded-lg bg-primary-container hover:bg-primary text-on-primary font-label-md text-label-md disabled:opacity-50"
          :disabled="!contenido.trim() || guardando"
        >
          {{ guardando ? 'Guardando…' : 'Guardar nota' }}
        </button>
      </div>
    </div>
  </form>
</template>
