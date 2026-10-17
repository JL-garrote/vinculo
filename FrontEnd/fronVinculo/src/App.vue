<script setup lang="ts">
import { ref } from 'vue';
import SideVar from './components/SideVar.vue';
import TopBar from './components/TopBar.vue';
import DashboardView from './views/DashboardView.vue';
import ContactosView from './views/ContactosView.vue';
import FichaContactoView from './views/FichaContactoView.vue';
import PipelineView from './views/PipelineView.vue';

const section = ref('dashboard');
</script>

<template>
  <SideVar v-model="section" />
  <div class="pl-64">
    <TopBar />
    <main class="w-full pt-16 bg-surface min-h-screen">
      <DashboardView v-if="section === 'dashboard'" />
      <ContactosView v-else-if="section === 'contactos'" />
      <FichaContactoView v-else-if="section === 'ficha-contacto'" />
      <PipelineView v-else-if="section === 'pipeline-relacional'" />
      <div v-else class="px-8 py-16 text-center font-body-md text-body-md text-on-surface-variant">
        Sección en construcción.
      </div>
    </main>
  </div>
</template>
