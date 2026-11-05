<script setup lang="ts">
import { computed, onBeforeUnmount, onMounted, ref, useId } from 'vue';
import { store } from '@/api/store';
import { alternarTema, tema } from '@/api/tema';
import { nombreCompleto } from '@/api/format';
import AvatarContacto from './AvatarContacto.vue';

const emit = defineEmits<{ nuevoContacto: []; abrirMenu: []; abrirContacto: [id: number] }>();

const nombre = computed(() => store.perfil?.nombre ?? 'Mi perfil');
const subtitulo = computed(() => store.perfil?.email ?? store.perfil?.zonaHoraria ?? '');

// Buscador global: salta a la ficha de un contacto sin pasar por el directorio
const MAX_RESULTADOS = 6;
const idLista = useId();
const campo = ref<HTMLInputElement | null>(null);
const texto = ref('');
const abierto = ref(false);
const activo = ref(0);
const esMac = /Mac|iPhone|iPad/.test(navigator.platform);

const normalizar = (s: string) => s.normalize('NFD').replace(/[̀-ͯ]/g, '').toLowerCase();

const resultados = computed(() => {
  const q = normalizar(texto.value.trim());
  if (!q) return [];
  const encontrados = [];
  for (const c of store.contactos) {
    const campos = [nombreCompleto(c, false), c.empresa, c.email];
    if (campos.some((v) => v && normalizar(v).includes(q))) {
      encontrados.push(c);
      if (encontrados.length === MAX_RESULTADOS) break;
    }
  }
  return encontrados;
});

const mostrarLista = computed(() => abierto.value && texto.value.trim() !== '');

function escribir() {
  abierto.value = true;
  activo.value = 0;
}

function mover(paso: number) {
  if (!resultados.value.length) return;
  abierto.value = true;
  activo.value = (activo.value + paso + resultados.value.length) % resultados.value.length;
}

function elegir(id: number) {
  emit('abrirContacto', id);
  texto.value = '';
  abierto.value = false;
  campo.value?.blur();
}

function confirmar() {
  const elegido = resultados.value[activo.value];
  if (mostrarLista.value && elegido) elegir(elegido.id);
}

function cerrar() {
  if (abierto.value && texto.value) abierto.value = false;
  else {
    texto.value = '';
    campo.value?.blur();
  }
}

// Ctrl+K (⌘K en Mac) lleva el foco al buscador desde cualquier pantalla
function atajo(e: KeyboardEvent) {
  if (e.key.toLowerCase() === 'k' && (esMac ? e.metaKey : e.ctrlKey) && campo.value?.offsetParent) {
    e.preventDefault();
    campo.value.focus();
    campo.value.select();
  }
}
onMounted(() => window.addEventListener('keydown', atajo));
onBeforeUnmount(() => window.removeEventListener('keydown', atajo));
</script>

<template>
  <header
    class="fixed top-0 left-0 lg:left-64 right-0 h-16 bg-surface-container-lowest/90 backdrop-blur-xl shadow-[0_1px_8px_rgba(0,0,0,0.04)] z-40 flex items-center justify-between gap-3 px-4 sm:px-6 lg:px-8"
  >
    <div class="flex items-center gap-2 sm:gap-4 flex-1 min-w-0 max-w-96">
      <button
        type="button"
        aria-label="Abrir el menú"
        class="p-2.5 -ml-2.5 rounded-lg text-on-surface-variant hover:bg-surface-container-high lg:hidden"
        @click="$emit('abrirMenu')"
      >
        <span aria-hidden="true" class="material-symbols-outlined text-xl">menu</span>
      </button>
      <img alt="Vínculo" class="h-6 w-auto sm:hidden dark:hidden" src="/logo.svg" />
      <img alt="Vínculo" class="h-6 w-auto hidden max-sm:dark:block" src="/logo-oscuro.svg" />
      <div class="relative w-full hidden sm:flex items-center">
        <span aria-hidden="true" class="material-symbols-outlined absolute left-3 text-on-surface-variant text-lg pointer-events-none">search</span>
        <input
          ref="campo"
          v-model="texto"
          type="search"
          role="combobox"
          aria-label="Buscar un contacto"
          aria-autocomplete="list"
          :aria-expanded="mostrarLista"
          :aria-controls="idLista"
          :aria-activedescendant="mostrarLista && resultados.length ? `${idLista}-${activo}` : undefined"
          aria-keyshortcuts="Control+K Meta+K"
          autocomplete="off"
          placeholder="Buscar contactos…"
          class="w-full pl-9 pr-16 py-2 bg-surface rounded-lg font-body-sm text-body-sm text-on-surface placeholder:text-outline focus:outline-none focus:ring-2 focus:ring-primary/40 [&::-webkit-search-cancel-button]:hidden"
          @input="escribir"
          @focus="abierto = true"
          @blur="abierto = false"
          @keydown.down.prevent="mover(1)"
          @keydown.up.prevent="mover(-1)"
          @keydown.enter.prevent="confirmar"
          @keydown.esc.prevent="cerrar"
        />
        <kbd
          aria-hidden="true"
          class="absolute right-2.5 px-1.5 py-0.5 rounded bg-surface-container text-on-surface-variant font-label-sm text-label-sm hidden md:block pointer-events-none"
        >
          {{ esMac ? '⌘K' : 'Ctrl K' }}
        </kbd>

        <ul
          v-show="mostrarLista"
          :id="idLista"
          role="listbox"
          aria-label="Contactos encontrados"
          class="absolute top-full left-0 right-0 mt-2 p-1.5 rounded-xl bg-surface-container-lowest shadow-[0_8px_24px_rgba(11,28,48,0.14)] ring-1 ring-outline-variant/60 flex flex-col"
        >
          <li
            v-for="(c, i) in resultados"
            :id="`${idLista}-${i}`"
            :key="c.id"
            role="option"
            :aria-selected="i === activo"
            class="flex items-center gap-3 px-2.5 py-2 rounded-lg cursor-pointer"
            :class="i === activo ? 'bg-surface-container' : ''"
            @mousedown.prevent="elegir(c.id)"
            @mousemove="activo = i"
          >
            <AvatarContacto :nombre="nombreCompleto(c)" :src="c.fotoURI" size="w-8 h-8" />
            <span class="flex flex-col min-w-0">
              <span class="font-label-lg text-label-lg text-on-surface truncate">{{ nombreCompleto(c) }}</span>
              <span v-if="c.empresa || c.email" class="font-body-sm text-body-sm text-on-surface-variant truncate">
                {{ [c.cargo, c.empresa].filter(Boolean).join(' · ') || c.email }}
              </span>
            </span>
          </li>
          <li v-if="!resultados.length" role="presentation" class="px-2.5 py-3 font-body-sm text-body-sm text-on-surface-variant">
            Ningún contacto coincide con «{{ texto.trim() }}».
          </li>
        </ul>
      </div>
    </div>

    <div class="flex items-center gap-1 sm:gap-3 shrink-0">
      <button
        type="button"
        aria-label="Nuevo contacto"
        class="flex items-center gap-2 p-2.5 sm:px-3.5 sm:py-2 bg-primary-container text-on-primary rounded-lg font-label-lg text-label-lg hover:bg-primary dark:hover:bg-primary-fixed-dim transition-colors shadow-sm"
        @click="$emit('nuevoContacto')"
      >
        <span aria-hidden="true" class="material-symbols-outlined text-base">add</span>
        <span aria-hidden="true" class="hidden sm:inline">Nuevo contacto</span>
      </button>

      <button
        type="button"
        :aria-label="tema === 'oscuro' ? 'Cambiar a modo claro' : 'Cambiar a modo oscuro'"
        :title="tema === 'oscuro' ? 'Cambiar a modo claro' : 'Cambiar a modo oscuro'"
        class="p-2.5 text-on-surface-variant hover:text-on-surface hover:bg-surface-container-high rounded-lg transition-colors"
        @click="alternarTema"
      >
        <span aria-hidden="true" class="material-symbols-outlined text-xl">{{ tema === 'oscuro' ? 'light_mode' : 'dark_mode' }}</span>
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
