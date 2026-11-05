<script setup lang="ts">
import { onMounted, ref, watch } from 'vue';
import SideVar from './components/SideVar.vue';
import TopBar from './components/TopBar.vue';
import DashboardView from './views/DashboardView.vue';
import ContactosView from './views/ContactosView.vue';
import FichaContactoView from './views/FichaContactoView.vue';
import PipelineView from './views/PipelineView.vue';
import NuevoContactoDialog from './components/formularios/NuevoContactoDialog.vue';
import { cargarDatos, store } from './api/store';
import { nombreCompleto } from './api/format';
import { contactoActual, iniciarRutas, RUTAS, seccion, sincronizarHash } from './api/rutas';
import type { Contacto } from './api/types';

iniciarRutas();

const creandoContacto = ref(false);
// Menú lateral en pantallas pequeñas (en escritorio siempre está visible)
const menuAbierto = ref(false);
const principal = ref<HTMLElement | null>(null);

function abrirFicha(id: number) {
  contactoActual.value = id;
  seccion.value = 'ficha-contacto';
}

function contactoCreado(contacto: Contacto) {
  creandoContacto.value = false;
  abrirFicha(contacto.id);
}

// Cada cambio de vista: dirección del navegador, título de la pestaña, scroll arriba
// y el foco al contenido, para que el lector de pantalla anuncie la nueva vista
watch([seccion, contactoActual], ([nueva], [anterior]) => {
  sincronizarHash();
  window.scrollTo({ top: 0 });
  if (nueva !== anterior) principal.value?.focus({ preventScroll: true });
});

watch(
  [seccion, contactoActual, () => store.contactos.length],
  () => {
    const contacto = seccion.value === 'ficha-contacto' ? store.contactos.find((c) => c.id === contactoActual.value) : undefined;
    document.title = `${contacto ? nombreCompleto(contacto) : RUTAS[seccion.value].titulo} · Vínculo`;
  },
  { immediate: true },
);

onMounted(cargarDatos);
</script>

<template>
  <a
    href="#contenido"
    class="sr-only focus:not-sr-only focus:fixed focus:top-3 focus:left-3 focus:z-[60] focus:px-4 focus:py-2 focus:rounded-lg focus:bg-primary-container focus:text-on-primary font-label-lg text-label-lg"
    @click.prevent="principal?.focus()"
  >
    Saltar al contenido
  </a>
  <SideVar :activa="seccion" :abierto="menuAbierto" @cerrar="menuAbierto = false" />
  <div class="lg:pl-64">
    <TopBar @nuevo-contacto="creandoContacto = true" @abrir-menu="menuAbierto = true" @abrir-contacto="abrirFicha" />
    <main id="contenido" ref="principal" tabindex="-1" class="w-full pt-16 bg-surface min-h-screen focus:outline-none">
      <div
        v-if="store.error"
        role="alert"
        class="mx-4 sm:mx-6 lg:mx-8 mt-6 p-4 rounded-xl bg-error-container text-on-error-container flex flex-col sm:flex-row sm:items-center justify-between gap-4"
      >
        <div class="flex items-center gap-3">
          <span aria-hidden="true" class="material-symbols-outlined">cloud_off</span>
          <div class="flex flex-col">
            <span class="font-label-lg text-label-lg">No se pudo conectar con el backend</span>
            <span class="font-body-sm text-body-sm">{{ store.error }}. Comprueba que Spring Boot está arrancado en el puerto 8080.</span>
          </div>
        </div>
        <button
          type="button"
          class="self-start sm:self-auto px-3 py-1.5 rounded-lg bg-error text-on-error font-label-md text-label-md shrink-0"
          @click="cargarDatos"
        >
          Reintentar
        </button>
      </div>

      <div v-if="store.cargando" role="status" class="px-4 sm:px-8 py-16 flex items-center justify-center gap-3 text-on-surface-variant">
        <span aria-hidden="true" class="material-symbols-outlined animate-spin motion-reduce:animate-none">progress_activity</span>
        <span class="font-body-md text-body-md">Cargando datos…</span>
      </div>

      <template v-else>
        <DashboardView v-if="seccion === 'dashboard'" @abrir="abrirFicha" />
        <ContactosView v-else-if="seccion === 'contactos'" @abrir="abrirFicha" @nuevo-contacto="creandoContacto = true" />
        <FichaContactoView v-else-if="seccion === 'ficha-contacto'" v-model:contacto-id="contactoActual" />
        <PipelineView v-else-if="seccion === 'pipeline-relacional'" @abrir="abrirFicha" />
      </template>
    </main>
  </div>

  <NuevoContactoDialog v-if="creandoContacto" @creado="contactoCreado" @cerrar="creandoContacto = false" />
</template>
