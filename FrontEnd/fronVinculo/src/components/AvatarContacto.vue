<script setup lang="ts">
import { computed } from 'vue';
import { iniciales } from '@/api/format';

const props = defineProps<{ nombre: string; src?: string | null; size?: string }>();

const texto = computed(() => iniciales(props.nombre));
</script>

<template>
  <img
    v-if="src"
    :src="src"
    :alt="nombre"
    class="rounded-full object-cover shrink-0"
    :class="size ?? 'w-11 h-11'"
    draggable="false"
  />
  <div
    v-else
    class="rounded-full shrink-0 bg-primary-fixed text-on-primary-fixed flex items-center justify-center font-label-lg text-label-lg"
    :class="size ?? 'w-11 h-11'"
    :title="nombre"
  >
    {{ texto }}
  </div>
</template>
