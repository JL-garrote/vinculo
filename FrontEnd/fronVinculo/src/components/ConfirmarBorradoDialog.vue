<script setup lang="ts">
import { ref } from 'vue';
import ModalBase from './ModalBase.vue';
import { mensajeError } from '@/api/vinculo';

const props = defineProps<{
  titulo: string;
  /** Qué se va a perder al borrar */
  mensaje: string;
  /** Llama al backend; si falla, el error se muestra en el diálogo */
  borrar: () => Promise<unknown>;
}>();
const emit = defineEmits<{ borrado: []; cerrar: [] }>();

const borrando = ref(false);
const error = ref<string | null>(null);

async function confirmar() {
  borrando.value = true;
  error.value = null;
  try {
    await props.borrar();
    emit('borrado');
  } catch (e) {
    error.value = mensajeError(e);
  } finally {
    borrando.value = false;
  }
}
</script>

<template>
  <ModalBase :titulo="titulo" ancho="max-w-md" @cerrar="emit('cerrar')">
    <div class="flex items-start gap-3">
      <span class="p-2 rounded-full bg-error-container text-on-error-container material-symbols-outlined">delete</span>
      <p class="font-body-md text-body-md text-on-surface-variant">{{ mensaje }} Esta acción no se puede deshacer.</p>
    </div>
    <p v-if="error" class="p-3 rounded-lg bg-error-container text-on-error-container font-body-sm text-body-sm">
      {{ error }}
    </p>
    <div class="flex items-center justify-end gap-2">
      <button
        type="button"
        class="px-4 py-2 rounded-lg bg-surface-container text-on-surface-variant hover:text-on-surface font-label-md text-label-md"
        @click="emit('cerrar')"
      >
        Cancelar
      </button>
      <button
        type="button"
        class="px-4 py-2 rounded-lg bg-error text-on-error font-label-md text-label-md shadow-sm disabled:opacity-50"
        :disabled="borrando"
        @click="confirmar"
      >
        {{ borrando ? 'Borrando…' : 'Borrar' }}
      </button>
    </div>
  </ModalBase>
</template>
