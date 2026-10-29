<script setup lang="ts">
import { onMounted, ref } from 'vue';
import SideVar from './components/SideVar.vue';
import TopBar from './components/TopBar.vue';
import DashboardView from './views/DashboardView.vue';
import ContactosView from './views/ContactosView.vue';
import FichaContactoView from './views/FichaContactoView.vue';
import PipelineView from './views/PipelineView.vue';
import { cargarDatos, store } from './api/store';

const section = ref('dashboard');
const contactoSeleccionado = ref<number | null>(null);

function abrirFicha(id: number) {
  contactoSeleccionado.value = id;
  section.value = 'ficha-contacto';
}

onMounted(cargarDatos);
</script>

<template>
  <SideVar v-model="section" />
  <div class="pl-64">
    <TopBar />
    <main class="w-full pt-16 bg-surface min-h-screen">
      <div
        v-if="store.error"
        class="mx-8 mt-6 p-4 rounded-xl bg-error-container text-on-error-container flex items-center justify-between gap-4"
      >
        <div class="flex items-center gap-3">
          <span class="material-symbols-outlined">cloud_off</span>
          <div class="flex flex-col">
            <span class="font-label-lg text-label-lg">No se pudo conectar con el backend</span>
            <span class="font-body-sm text-body-sm">{{ store.error }}. Comprueba que Spring Boot está arrancado en el puerto 8080.</span>
          </div>
        </div>
        <button
          type="button"
          class="px-3 py-1.5 rounded-lg bg-error text-on-error font-label-md text-label-md shrink-0"
          @click="cargarDatos"
        >
          Reintentar
        </button>
      </div>

      <div v-if="store.cargando" class="px-8 py-16 flex items-center justify-center gap-3 text-on-surface-variant">
        <span class="material-symbols-outlined animate-spin">progress_activity</span>
        <span class="font-body-md text-body-md">Cargando datos…</span>
      </div>

      <template v-else>
        <DashboardView v-if="section === 'dashboard'" @abrir="abrirFicha" />
        <ContactosView v-else-if="section === 'contactos'" @abrir="abrirFicha" />
        <FichaContactoView v-else-if="section === 'ficha-contacto'" v-model:contacto-id="contactoSeleccionado" />
        <PipelineView v-else-if="section === 'pipeline-relacional'" />
        <div v-else class="px-8 py-16 text-center font-body-md text-body-md text-on-surface-variant">
          Sección en construcción.
        </div>
      </template>
    </main>
  </div>
</template>
