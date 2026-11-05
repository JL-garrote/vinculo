<script setup lang="ts">
import { nextTick, ref, watch } from 'vue';
import { hrefDe, RUTAS, type Seccion } from '@/api/rutas';

const mainNav: { seccion: Seccion; label: string; icon: string }[] = [
  { seccion: 'dashboard', label: 'Dashboard', icon: 'dashboard' },
  { seccion: 'contactos', label: 'Contactos', icon: 'group' },
  { seccion: 'ficha-contacto', label: RUTAS['ficha-contacto'].titulo, icon: 'account_circle' },
  { seccion: 'pipeline-relacional', label: RUTAS['pipeline-relacional'].titulo, icon: 'view_kanban' },
];

// abierto: solo afecta a pantallas pequeñas, donde el menú se despliega encima del contenido
const props = defineProps<{ activa: Seccion; abierto?: boolean }>();
const emit = defineEmits<{ cerrar: [] }>();

// Al abrir el menú en móvil el foco entra en él; al cerrarlo vuelve al botón que lo abrió
const panel = ref<HTMLElement | null>(null);
let origen: HTMLElement | null = null;
watch(
  () => props.abierto,
  async (abierto) => {
    if (abierto) {
      origen = document.activeElement as HTMLElement | null;
      await nextTick();
      panel.value?.querySelector<HTMLElement>('button, a')?.focus();
    } else origen?.focus();
  },
);
</script>

<template>
  <div v-if="abierto" class="fixed inset-0 z-50 bg-scrim lg:hidden" aria-hidden="true" @click="emit('cerrar')"></div>
  <aside
    ref="panel"
    aria-label="Menú principal"
    class="fixed left-0 top-0 h-full w-64 bg-surface-container-lowest shadow-[0_1px_8px_rgba(0,0,0,0.04)] z-50 flex flex-col py-6 px-4 overflow-y-auto duration-200 lg:translate-x-0 lg:visible motion-reduce:transition-none"
    :class="
      /* Al abrir se hace visible al instante (y puede recibir el foco); al cerrar se oculta al acabar de deslizarse */
      abierto ? 'translate-x-0 visible transition-transform' : '-translate-x-full invisible transition-[transform,visibility]'
    "
    @keydown.esc="emit('cerrar')"
  >
    <div class="flex flex-col gap-8">
      <div class="flex items-center gap-3 px-2">
        <img alt="" class="h-7 w-auto dark:hidden" src="/logo.svg" />
        <img alt="" class="h-7 w-auto hidden dark:block" src="/logo-oscuro.svg" />
        <div class="flex flex-col flex-1">
          <span class="font-headline-sm text-headline-sm text-on-surface leading-none">Vínculo</span>
          <span class="font-label-sm text-label-sm text-on-surface-variant tracking-wider uppercase mt-0.5">
            PRM Personal
          </span>
        </div>
        <button
          type="button"
          aria-label="Cerrar el menú"
          class="p-2.5 -mr-2 rounded-lg text-on-surface-variant hover:bg-surface-container-high lg:hidden"
          @click="emit('cerrar')"
        >
          <span aria-hidden="true" class="material-symbols-outlined text-xl">close</span>
        </button>
      </div>

      <nav aria-label="Secciones" class="flex flex-col gap-1">
        <a
          v-for="item in mainNav"
          :key="item.seccion"
          :href="hrefDe(item.seccion)"
          :aria-current="activa === item.seccion ? 'page' : undefined"
          class="flex items-center gap-3 px-3 py-2.5 rounded-lg font-label-lg text-label-lg transition-colors"
          :class="
            activa === item.seccion
              ? 'bg-primary-container text-on-primary shadow-sm'
              : 'text-on-surface-variant hover:bg-surface-container-high hover:text-on-surface'
          "
          @click="emit('cerrar')"
        >
          <span aria-hidden="true" class="material-symbols-outlined text-lg">{{ item.icon }}</span>
          <span>{{ item.label }}</span>
        </a>
      </nav>
    </div>
  </aside>
</template>
