<script setup lang="ts">
defineProps<{ titulo: string; ancho?: string }>();
const emit = defineEmits<{ cerrar: [] }>();
</script>

<template>
  <div
    class="fixed inset-0 z-[60] flex items-center justify-center p-4 bg-scrim"
    @click.self="emit('cerrar')"
    @keydown.esc="emit('cerrar')"
  >
    <div
      class="w-full max-h-[90vh] overflow-y-auto bg-surface-container-lowest rounded-xl shadow-xl p-6 flex flex-col gap-5"
      :class="ancho ?? 'max-w-lg'"
    >
      <div class="flex items-center justify-between">
        <h2 class="font-headline-sm text-headline-sm text-on-surface">{{ titulo }}</h2>
        <button type="button" class="p-1 text-on-surface-variant hover:text-on-surface" title="Cerrar" @click="emit('cerrar')">
          <span class="material-symbols-outlined">close</span>
        </button>
      </div>
      <slot />
    </div>
  </div>
</template>
