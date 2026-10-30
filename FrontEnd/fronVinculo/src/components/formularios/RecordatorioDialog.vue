<script setup lang="ts">
import { computed, ref } from 'vue';
import ModalBase from '../ModalBase.vue';
import { api, mensajeError } from '@/api/vinculo';
import { store } from '@/api/store';
import { aInputFecha, desdeInputFecha, fechaBackend, nombreCompleto } from '@/api/format';
import type { Recordatorio } from '@/api/types';

const props = defineProps<{ contactoIdInicial?: number | null }>();
const emit = defineEmits<{ creado: [recordatorio: Recordatorio]; cerrar: [] }>();

// Por defecto, mañana a las 9:00
const manana = new Date();
manana.setDate(manana.getDate() + 1);
manana.setHours(9, 0, 0, 0);

const contactoID = ref<number>(props.contactoIdInicial ?? 0);
const titulo = ref('');
const descripcion = ref('');
const venceEn = ref(aInputFecha(fechaBackend(manana)));
const recurrencia = ref<number | null>(null);
const guardando = ref(false);
const error = ref<string | null>(null);

const contactosOrdenados = computed(() =>
  [...store.contactos].sort((a, b) => nombreCompleto(a, false).localeCompare(nombreCompleto(b, false), 'es')),
);

async function guardar() {
  if (!titulo.value.trim() || !venceEn.value || guardando.value) return;
  guardando.value = true;
  error.value = null;
  try {
    const recordatorio = await api.crearRecordatorio({
      contactoID: contactoID.value || null,
      titulo: titulo.value.trim(),
      descripcion: descripcion.value.trim() || null,
      venceEn: desdeInputFecha(venceEn.value)!,
      recurrenciaDias: recurrencia.value || null,
    });
    store.recordatorios.push(recordatorio);
    store.recordatorios.sort((a, b) => a.venceEnFecha.localeCompare(b.venceEnFecha));
    emit('creado', recordatorio);
  } catch (e) {
    error.value = mensajeError(e);
  } finally {
    guardando.value = false;
  }
}

const campo =
  'w-full bg-surface-container-low text-on-surface font-body-sm text-body-sm py-2.5 px-3 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary/20';
const etiqueta = 'font-label-sm text-label-sm uppercase tracking-wider text-on-surface-variant';
</script>

<template>
  <ModalBase titulo="Programar recordatorio" @cerrar="emit('cerrar')">
    <form class="flex flex-col gap-4" @submit.prevent="guardar">
      <label class="flex flex-col gap-1.5">
        <span :class="etiqueta">Título *</span>
        <input v-model="titulo" maxlength="200" required autofocus placeholder="Ej. Escribirle por su cumpleaños" :class="campo" />
      </label>
      <label class="flex flex-col gap-1.5">
        <span :class="etiqueta">Contacto</span>
        <select v-model.number="contactoID" :class="campo">
          <option :value="0">Ninguno (recordatorio general)</option>
          <option v-for="c in contactosOrdenados" :key="c.id" :value="c.id">{{ nombreCompleto(c) }}</option>
        </select>
      </label>
      <div class="grid grid-cols-2 gap-3">
        <label class="flex flex-col gap-1.5">
          <span :class="etiqueta">Vence *</span>
          <input v-model="venceEn" type="datetime-local" required :class="campo" />
        </label>
        <label class="flex flex-col gap-1.5">
          <span :class="etiqueta">Repetir cada (días)</span>
          <input v-model.number="recurrencia" type="number" min="1" max="3650" placeholder="No se repite" :class="campo" />
        </label>
      </div>
      <label class="flex flex-col gap-1.5">
        <span :class="etiqueta">Descripción</span>
        <textarea v-model="descripcion" rows="2" class="resize-none" :class="campo"></textarea>
      </label>

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
          :disabled="!titulo.trim() || !venceEn || guardando"
        >
          {{ guardando ? 'Guardando…' : 'Programar' }}
        </button>
      </div>
    </form>
  </ModalBase>
</template>
