<script setup lang="ts">
import { ref } from 'vue';

export type InteractionType = 'cafe' | 'llamada' | 'mensaje' | 'nota';

export interface NewInteraction {
  type: InteractionType;
  text: string;
  topics: string[];
}

const emit = defineEmits<{ save: [interaction: NewInteraction] }>();

const tabs: { type: InteractionType; label: string; icon: string }[] = [
  { type: 'cafe', label: 'Café / Presencial', icon: 'local_cafe' },
  { type: 'llamada', label: 'Llamada', icon: 'phone' },
  { type: 'mensaje', label: 'Mensaje', icon: 'chat' },
  { type: 'nota', label: 'Nota Libre', icon: 'edit_note' },
];

const suggestedTopics = ['Estrategia', 'Familia', 'LegalTech'];

const today = new Date().toLocaleDateString('es-ES', { day: 'numeric', month: 'short', year: 'numeric' });

const activeType = ref<InteractionType>('cafe');
const text = ref('');
const topics = ref<string[]>([]);
const saved = ref(false);
const placeholder = ref('¿De qué hablaron? Menciona temas tratados, recomendaciones o tareas acordadas...');
const textarea = ref<HTMLTextAreaElement | null>(null);

function toggleTopic(topic: string) {
  topics.value = topics.value.includes(topic)
    ? topics.value.filter((t) => t !== topic)
    : [...topics.value, topic];
}

function save() {
  if (!text.value.trim()) {
    placeholder.value = 'Por favor escribe un detalle antes de registrar...';
    textarea.value?.focus();
    return;
  }
  emit('save', { type: activeType.value, text: text.value.trim(), topics: topics.value });
  saved.value = true;
  setTimeout(() => {
    text.value = '';
    topics.value = [];
    saved.value = false;
  }, 1500);
}

// Permite al padre llevar el foco al cuadro de texto ("Registrar Interacción")
function focus() {
  textarea.value?.focus();
  textarea.value?.scrollIntoView({ behavior: 'smooth', block: 'center' });
}

defineExpose({ focus });
</script>

<template>
  <div class="bg-surface-container-lowest rounded-xl p-6 shadow-sm flex flex-col gap-4">
    <div class="flex items-center justify-between">
      <h2 class="font-headline-sm text-headline-sm text-on-surface flex items-center gap-2">
        <span class="material-symbols-outlined text-primary">add_comment</span>
        <span>Registrar Nueva Interacción</span>
      </h2>
      <span class="font-label-sm text-label-sm text-on-surface-variant">Hoy, {{ today }}</span>
    </div>

    <!-- Canal -->
    <div class="flex items-center gap-1.5 p-1 bg-surface-container-low rounded-lg">
      <button
        v-for="tab in tabs"
        :key="tab.type"
        type="button"
        class="flex-1 py-1.5 px-3 rounded-md font-label-sm text-label-sm flex items-center justify-center gap-1.5 transition-all"
        :class="
          activeType === tab.type
            ? 'bg-surface-container-lowest text-on-surface shadow-sm'
            : 'text-on-surface-variant hover:text-on-surface'
        "
        @click="activeType = tab.type"
      >
        <span class="material-symbols-outlined text-sm" :class="{ 'text-secondary': activeType === tab.type }">
          {{ tab.icon }}
        </span>
        <span>{{ tab.label }}</span>
      </button>
    </div>

    <div class="flex flex-col gap-3">
      <textarea
        ref="textarea"
        v-model="text"
        rows="3"
        :placeholder="placeholder"
        class="w-full p-3.5 bg-surface rounded-lg font-body-sm text-body-sm text-on-surface placeholder:text-outline-variant focus:outline-none focus:ring-2 focus:ring-primary/20 resize-none"
      ></textarea>

      <div class="flex flex-wrap items-center justify-between gap-3">
        <div class="flex flex-wrap items-center gap-1.5">
          <span class="font-label-sm text-label-sm text-on-surface-variant mr-1">Temas:</span>
          <button
            v-for="topic in suggestedTopics"
            :key="topic"
            type="button"
            class="px-2 py-0.5 rounded font-label-sm text-label-sm transition-colors"
            :class="
              topics.includes(topic)
                ? 'bg-primary-fixed text-on-primary-fixed'
                : 'bg-surface-container hover:bg-surface-container-high text-on-surface'
            "
            @click="toggleTopic(topic)"
          >
            {{ topics.includes(topic) ? '✓' : '+' }} {{ topic }}
          </button>
        </div>

        <div class="flex items-center gap-2">
          <button
            type="button"
            class="px-3 py-1.5 bg-surface-container text-on-surface-variant hover:text-on-surface font-label-md text-label-md rounded-lg transition-colors"
          >
            Guardar Borrador
          </button>
          <button
            type="button"
            class="px-4 py-1.5 text-on-primary font-label-md text-label-md rounded-lg transition-colors shadow-sm flex items-center gap-1"
            :class="saved ? 'bg-secondary' : 'bg-primary-container hover:bg-primary'"
            @click="save"
          >
            <template v-if="saved">
              <span class="material-symbols-outlined text-sm">done_all</span>
              <span>Guardado</span>
            </template>
            <template v-else>
              <span>Guardar Registro</span>
              <span class="material-symbols-outlined text-sm">check</span>
            </template>
          </button>
        </div>
      </div>
    </div>
  </div>
</template>
