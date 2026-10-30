<script setup lang="ts">
import { computed, ref } from 'vue';
import type {
  Contacto,
  EtapaPipeline,
  PeticionPipelineContacto,
  PipelineContacto,
  PrioridadPipeline,
} from '@/api/types';
import { contactoPorId } from '@/api/store';
import { aInputFecha, desdeInputFecha, etapas, nombreCompleto, prioridades } from '@/api/format';

const props = defineProps<{
  /** Entrada a editar; si no hay, el formulario es para añadir */
  entrada?: PipelineContacto | null;
  etapaInicial?: EtapaPipeline;
  contactosDisponibles: Contacto[];
  guardando: boolean;
  error: string | null;
}>();

const emit = defineEmits<{
  guardar: [datos: PeticionPipelineContacto];
  quitar: [entrada: PipelineContacto];
  abrir: [contactoId: number];
  cerrar: [];
}>();

const editando = computed(() => !!props.entrada);
const contactoEditado = computed(() => (props.entrada ? contactoPorId(props.entrada.contactoID) : undefined));

const contactoID = ref<number | null>(props.entrada?.contactoID ?? props.contactosDisponibles[0]?.id ?? null);
const etapa = ref<EtapaPipeline>(props.entrada?.etapa ?? props.etapaInicial ?? 'identificado');
const prioridad = ref<PrioridadPipeline>(props.entrada?.prioridad ?? 'media');
const siguienteAccion = ref(props.entrada?.siguienteAccion ?? '');
const fecha = ref(aInputFecha(props.entrada?.siguienteAccionFecha ?? null));
const confirmarQuitar = ref(false);

const puedeGuardar = computed(() => !props.guardando && (editando.value || contactoID.value !== null));

function guardar() {
  if (!puedeGuardar.value) return;
  emit('guardar', {
    ...(editando.value ? {} : { contactoID: contactoID.value ?? undefined }),
    etapa: etapa.value,
    prioridad: prioridad.value,
    siguienteAccion: siguienteAccion.value.trim() || null,
    siguienteAccionFecha: desdeInputFecha(fecha.value),
  });
}

const campo =
  'w-full bg-surface-container-low text-on-surface font-body-sm text-body-sm py-2.5 px-3 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary/20';
const etiqueta = 'font-label-sm text-label-sm uppercase tracking-wider text-on-surface-variant';
</script>

<template>
  <div class="fixed inset-0 z-[60] flex items-center justify-center p-4 bg-inverse-surface/40" @click.self="emit('cerrar')">
    <form
      class="w-full max-w-lg bg-surface-container-lowest rounded-xl shadow-xl p-6 flex flex-col gap-5"
      @submit.prevent="guardar"
      @keydown.esc="emit('cerrar')"
    >
      <div class="flex items-center justify-between">
        <h2 class="font-headline-sm text-headline-sm text-on-surface">
          {{ editando ? 'Editar en el pipeline' : 'Añadir al pipeline' }}
        </h2>
        <button type="button" class="p-1 text-on-surface-variant hover:text-on-surface" title="Cerrar" @click="emit('cerrar')">
          <span class="material-symbols-outlined">close</span>
        </button>
      </div>

      <!-- Contacto -->
      <div class="flex flex-col gap-1.5">
        <span :class="etiqueta">Contacto</span>
        <div v-if="editando" class="flex items-center justify-between gap-2">
          <span class="font-label-lg text-label-lg text-on-surface">
            {{ contactoEditado ? nombreCompleto(contactoEditado) : `Contacto #${entrada?.contactoID}` }}
          </span>
          <button
            v-if="contactoEditado"
            type="button"
            class="font-label-sm text-label-sm text-primary hover:underline"
            @click="emit('abrir', contactoEditado.id)"
          >
            Ver ficha
          </button>
        </div>
        <template v-else>
          <select v-if="contactosDisponibles.length" v-model="contactoID" :class="campo" required>
            <option v-for="c in contactosDisponibles" :key="c.id" :value="c.id">
              {{ nombreCompleto(c) }}{{ c.empresa ? ` · ${c.empresa}` : '' }}
            </option>
          </select>
          <p v-else class="font-body-sm text-body-sm text-on-surface-variant">
            Todos tus contactos ya están en este pipeline.
          </p>
        </template>
      </div>

      <!-- Etapa y prioridad -->
      <div class="grid grid-cols-2 gap-4">
        <label class="flex flex-col gap-1.5">
          <span :class="etiqueta">Etapa</span>
          <select v-model="etapa" :class="campo">
            <option v-for="e in etapas" :key="e.key" :value="e.key">{{ e.titulo }}</option>
          </select>
        </label>
        <div class="flex flex-col gap-1.5">
          <span :class="etiqueta">Prioridad</span>
          <div class="flex items-center gap-1 bg-surface-container-low rounded-lg p-1">
            <button
              v-for="(p, key) in prioridades"
              :key="key"
              type="button"
              class="flex-1 py-1.5 rounded-md font-label-sm text-label-sm flex items-center justify-center gap-1 transition-colors"
              :class="prioridad === key ? 'bg-surface-container-lowest text-on-surface shadow-sm' : 'text-on-surface-variant'"
              @click="prioridad = key"
            >
              <span class="w-1.5 h-1.5 rounded-full" :class="p.dot"></span> {{ p.label }}
            </button>
          </div>
        </div>
      </div>

      <!-- Siguiente acción -->
      <label class="flex flex-col gap-1.5">
        <span :class="etiqueta">Siguiente acción</span>
        <input v-model="siguienteAccion" type="text" maxlength="255" placeholder="Ej. Enviar el one-pager" :class="campo" />
      </label>
      <label class="flex flex-col gap-1.5">
        <span :class="etiqueta">Fecha de la acción</span>
        <input v-model="fecha" type="datetime-local" :class="campo" />
      </label>

      <p v-if="error" class="p-3 rounded-lg bg-error-container text-on-error-container font-body-sm text-body-sm">
        {{ error }}
      </p>

      <!-- Acciones -->
      <div class="flex items-center justify-between gap-3 pt-1">
        <div>
          <template v-if="editando && entrada">
            <button
              v-if="!confirmarQuitar"
              type="button"
              class="px-3 py-2 rounded-lg text-error hover:bg-error-container font-label-md text-label-md transition-colors"
              @click="confirmarQuitar = true"
            >
              Quitar del pipeline
            </button>
            <button
              v-else
              type="button"
              class="px-3 py-2 rounded-lg bg-error text-on-error font-label-md text-label-md"
              :disabled="guardando"
              @click="emit('quitar', entrada)"
            >
              ¿Seguro? Quitar
            </button>
          </template>
        </div>
        <div class="flex items-center gap-2">
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
            :disabled="!puedeGuardar"
          >
            {{ guardando ? 'Guardando…' : editando ? 'Guardar cambios' : 'Añadir' }}
          </button>
        </div>
      </div>
    </form>
  </div>
</template>
