<script setup lang="ts">
import { nextTick, onBeforeUnmount, onMounted, ref, useId } from 'vue';

defineProps<{ titulo: string; ancho?: string }>();
const emit = defineEmits<{ cerrar: [] }>();

// Diálogo modal accesible: se anuncia como diálogo con su título, el foco entra al abrir,
// no puede salir con Tab mientras está abierto y vuelve al botón que lo abrió al cerrar.
const idTitulo = useId();
const panel = ref<HTMLElement | null>(null);
const origen = document.activeElement as HTMLElement | null;

const ENFOCABLES = 'a[href], button:not([disabled]), input:not([disabled]), select:not([disabled]), textarea:not([disabled]), [tabindex]:not([tabindex="-1"])';
const enfocables = () =>
  [...(panel.value?.querySelectorAll<HTMLElement>(ENFOCABLES) ?? [])].filter((el) => el.offsetParent !== null);

function atraparTab(e: KeyboardEvent) {
  const lista = enfocables();
  if (!lista.length) return;
  const primero = lista[0]!;
  const ultimo = lista[lista.length - 1]!;
  if (e.shiftKey && document.activeElement === primero) {
    e.preventDefault();
    ultimo.focus();
  } else if (!e.shiftKey && document.activeElement === ultimo) {
    e.preventDefault();
    primero.focus();
  }
}

onMounted(async () => {
  await nextTick();
  // Respeta el autofocus del formulario; si no hay, el primer campo (sin contar el botón de cerrar)
  const destino =
    panel.value?.querySelector<HTMLElement>('[autofocus]') ??
    enfocables().find((el) => !el.dataset.cerrar) ??
    panel.value;
  destino?.focus();
});

onBeforeUnmount(() => {
  if (origen?.isConnected) origen.focus();
});
</script>

<template>
  <div
    class="fixed inset-0 z-[60] flex items-center justify-center p-4 bg-scrim"
    @click.self="emit('cerrar')"
  >
    <div
      ref="panel"
      role="dialog"
      aria-modal="true"
      :aria-labelledby="idTitulo"
      tabindex="-1"
      class="w-full max-h-[90vh] overflow-y-auto bg-surface-container-lowest rounded-xl shadow-xl p-6 flex flex-col gap-5 focus:outline-none"
      :class="ancho ?? 'max-w-lg'"
      @keydown.esc.stop="emit('cerrar')"
      @keydown.tab="atraparTab"
    >
      <div class="flex items-center justify-between gap-3">
        <h2 :id="idTitulo" class="font-headline-sm text-headline-sm text-on-surface">{{ titulo }}</h2>
        <button
          type="button"
          data-cerrar="true"
          aria-label="Cerrar"
          class="p-1.5 -mr-1.5 rounded-lg text-on-surface-variant hover:text-on-surface hover:bg-surface-container"
          @click="emit('cerrar')"
        >
          <span aria-hidden="true" class="material-symbols-outlined">close</span>
        </button>
      </div>
      <slot />
    </div>
  </div>
</template>
