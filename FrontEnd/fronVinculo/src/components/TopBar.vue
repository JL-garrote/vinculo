<script setup lang="ts">
import { computed } from 'vue';
import { store } from '@/api/store';
import { alternarTema, tema } from '@/api/tema';
import AvatarContacto from './AvatarContacto.vue';

defineEmits<{ nuevoContacto: []; abrirMenu: [] }>();

const nombre = computed(() => store.perfil?.nombre ?? 'Mi perfil');
const subtitulo = computed(() => store.perfil?.email ?? store.perfil?.zonaHoraria ?? '');
</script>

<template>
  <header
    class="fixed top-0 left-0 lg:left-64 right-0 h-16 bg-surface-container-lowest/90 backdrop-blur-xl shadow-[0_1px_8px_rgba(0,0,0,0.04)] z-40 flex items-center justify-between gap-3 px-4 sm:px-6 lg:px-8"
  >
    <div class="flex items-center gap-2 sm:gap-4 flex-1 min-w-0 max-w-96">
      <button
        type="button"
        title="Menú"
        class="p-2 -ml-2 rounded-lg text-on-surface-variant hover:bg-surface-container-high lg:hidden"
        @click="$emit('abrirMenu')"
      >
        <span class="material-symbols-outlined text-xl">menu</span>
      </button>
      <img alt="Vínculo" class="h-6 w-auto sm:hidden dark:hidden" src="/logo.svg" />
      <img alt="Vínculo" class="h-6 w-auto hidden max-sm:dark:block" src="/logo-oscuro.svg" />
      <div class="relative w-full hidden sm:flex items-center">
        <span class="material-symbols-outlined absolute left-3 text-on-surface-variant text-lg">search</span>
        <input
          type="text"
          placeholder="Buscar contactos, notas, círculos..."
          class="w-full pl-9 pr-12 py-2 bg-surface rounded-lg font-body-sm text-body-sm text-on-surface placeholder:text-outline-variant focus:outline-none focus:ring-2 focus:ring-primary/20"
        />
        <kbd
          class="absolute right-2.5 px-1.5 py-0.5 rounded bg-surface-container text-on-surface-variant font-label-sm text-label-sm hidden md:block"
        >
          ⌘K
        </kbd>
      </div>
    </div>

    <div class="flex items-center gap-1 sm:gap-3 shrink-0">
      <button
        type="button"
        title="Nuevo contacto"
        class="flex items-center gap-2 p-2 sm:px-3.5 bg-primary-container text-on-primary rounded-lg font-label-lg text-label-lg hover:bg-primary transition-colors shadow-sm"
        @click="$emit('nuevoContacto')"
      >
        <span class="material-symbols-outlined text-base">add</span>
        <span class="hidden sm:inline">Nuevo Contacto</span>
      </button>

      <button
        type="button"
        :title="tema === 'oscuro' ? 'Cambiar a modo claro' : 'Cambiar a modo oscuro'"
        class="p-2 text-on-surface-variant hover:text-on-surface hover:bg-surface-container-high rounded-lg transition-colors"
        @click="alternarTema"
      >
        <span class="material-symbols-outlined text-xl">{{ tema === 'oscuro' ? 'light_mode' : 'dark_mode' }}</span>
      </button>

      <button
        type="button"
        class="p-2 text-on-surface-variant hover:text-on-surface hover:bg-surface-container-high rounded-lg transition-colors relative"
      >
        <span class="material-symbols-outlined text-xl">notifications</span>
        <span class="absolute top-2 right-2 w-2 h-2 rounded-full bg-secondary"></span>
      </button>

      <div class="hidden sm:flex items-center gap-3 pl-2 border-l border-surface-container-high">
        <AvatarContacto :nombre="nombre" :src="store.perfil?.avatarURI" size="w-8 h-8" />
        <div class="hidden lg:flex flex-col text-left">
          <span class="font-label-md text-label-md text-on-surface leading-tight">{{ nombre }}</span>
          <span class="font-label-sm text-label-sm text-on-surface-variant">{{ subtitulo }}</span>
        </div>
      </div>
    </div>
  </header>
</template>
