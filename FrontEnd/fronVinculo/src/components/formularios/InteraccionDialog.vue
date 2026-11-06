<script setup lang="ts">
import { ref, watch } from 'vue';
import ModalBase from '../ModalBase.vue';
import { api, mensajeEdicion } from '@/api/vinculo';
import { aInputFecha, desdeInputFecha, lista, tiposInteraccion } from '@/api/format';
import type { Interaccion, TipoInteraccion } from '@/api/types';

/** Edita una interacción del historial (las nuevas se registran desde la ficha) */
const props = defineProps<{ interaccion: Interaccion }>();
const emit = defineEmits<{ guardado: [interaccion: Interaccion]; cerrar: [] }>();

const original = props.interaccion;
const tipo = ref<TipoInteraccion>(original.tipo in tiposInteraccion ? original.tipo : 'otro');
const titulo = ref(original.titulo ?? '');
const fecha = ref(aInputFecha(original.fecha));
const lugar = ref(original.lugar ?? '');
const resumen = ref(original.resumen ?? '');
const temas = ref(lista(original.temas).join(', '));
const borrador = ref(!!original.borrador);
const guardando = ref(false);
const error = ref<string | null>(null);

// El título por defecto es el nombre del tipo: si no se ha cambiado, sigue al tipo elegido
watch(tipo, (nuevo, anterior) => {
  if (!titulo.value.trim() || titulo.value === tiposInteraccion[anterior].label) titulo.value = tiposInteraccion[nuevo].label;
});

async function guardar() {
  if (!fecha.value || guardando.value) return;
  guardando.value = true;
  error.value = null;
  try {
    const temasLimpios = temas.value
      .split(',')
      .map((t) => t.trim())
      .filter(Boolean);
    const interaccion = await api.actualizarInteraccion({
      ...original,
      tipo: tipo.value,
      titulo: titulo.value.trim() || tiposInteraccion[tipo.value].label,
      fecha: desdeInputFecha(fecha.value)!,
      lugar: lugar.value.trim() || null,
      resumen: resumen.value.trim() || null,
      temas: temasLimpios.length ? temasLimpios.join(',') : null,
      borrador: borrador.value ? 1 : 0,
    });
    emit('guardado', interaccion);
  } catch (e) {
    error.value = mensajeEdicion(e);
  } finally {
    guardando.value = false;
  }
}

const campo =
  'w-full bg-surface-container-low text-on-surface font-body-sm text-base sm:text-body-sm py-2.5 px-3 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary/20';
const etiqueta = 'font-label-sm text-label-sm uppercase tracking-wider text-on-surface-variant';
</script>

<template>
  <ModalBase titulo="Editar interacción" @cerrar="emit('cerrar')">
    <form class="flex flex-col gap-4" @submit.prevent="guardar">
      <div class="grid grid-cols-1 sm:grid-cols-2 gap-3">
        <label class="flex flex-col gap-1.5">
          <span :class="etiqueta">Tipo</span>
          <select v-model="tipo" :class="campo">
            <option v-for="(info, clave) in tiposInteraccion" :key="clave" :value="clave">{{ info.label }}</option>
          </select>
        </label>
        <label class="flex flex-col gap-1.5">
          <span :class="etiqueta">Fecha *</span>
          <input v-model="fecha" type="datetime-local" required :class="campo" />
        </label>
      </div>
      <label class="flex flex-col gap-1.5">
        <span :class="etiqueta">Título</span>
        <input v-model="titulo" maxlength="200" autofocus :placeholder="tiposInteraccion[tipo].label" :class="campo" />
      </label>
      <label class="flex flex-col gap-1.5">
        <span :class="etiqueta">Lugar</span>
        <input v-model="lugar" maxlength="200" placeholder="Ej. Café Comercial, Madrid" :class="campo" />
      </label>
      <label class="flex flex-col gap-1.5">
        <span :class="etiqueta">Resumen</span>
        <textarea v-model="resumen" rows="4" class="resize-y" :class="campo" placeholder="¿De qué hablasteis?"></textarea>
      </label>
      <label class="flex flex-col gap-1.5">
        <span :class="etiqueta">Temas</span>
        <input v-model="temas" placeholder="Separados por comas: Estrategia, Familia" :class="campo" />
      </label>
      <label class="flex items-center gap-2.5 cursor-pointer">
        <input v-model="borrador" type="checkbox" class="w-4 h-4 accent-primary cursor-pointer" />
        <span class="font-body-sm text-body-sm text-on-surface">Es un borrador (aún no cuenta como contacto realizado)</span>
      </label>

      <p v-if="error" role="alert" class="p-3 rounded-lg bg-error-container text-on-error-container font-body-sm text-body-sm">{{ error }}</p>

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
          class="px-4 py-2 rounded-lg bg-primary-container hover:bg-primary dark:hover:bg-primary-fixed-dim text-on-primary font-label-md text-label-md shadow-sm disabled:opacity-50"
          :disabled="!fecha || guardando"
        >
          {{ guardando ? 'Guardando…' : 'Guardar cambios' }}
        </button>
      </div>
    </form>
  </ModalBase>
</template>
