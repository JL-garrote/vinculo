<script setup lang="ts">
export interface DirectoryContact {
  name: string;
  avatar: string;
  birthday?: boolean;
  statusClass: string;
  statusTitle: string;
  interests: string[];
  group: string;
  circle: string;
  circleClass: string;
  circleDotClass: string;
  role: string;
  company: string;
  cadence: string;
  daysSince: number;
  health: 'risk' | 'warning' | 'good';
  lastWhen: string;
  lastWhenClass: string;
  lastWhat: string;
  healthDotClass: string;
  healthTitle: string;
  nextIcon: string;
  nextIconClass: string;
  nextTitle: string;
  nextDetail: string;
}

defineProps<{ contact: DirectoryContact; striped?: boolean }>();
const selected = defineModel<boolean>('selected', { default: false });

const quickActions = [
  { title: 'Llamar', icon: 'call', hover: 'hover:text-primary' },
  { title: 'WhatsApp', icon: 'chat', hover: 'hover:text-secondary' },
  { title: 'Email', icon: 'mail', hover: 'hover:text-primary' },
  { title: 'Más opciones', icon: 'more_vert', hover: 'hover:text-on-surface' },
];
</script>

<template>
  <tr class="hover:bg-surface-container-low transition-colors group" :class="{ 'bg-surface/30': striped }">
    <td class="py-4 px-6 text-center">
      <input
        v-model="selected"
        type="checkbox"
        class="w-4 h-4 rounded cursor-pointer accent-primary-container"
      />
    </td>

    <!-- Nombre & Intereses -->
    <td class="py-4 px-4">
      <div class="flex items-center gap-3.5">
        <div class="relative shrink-0">
          <img class="w-11 h-11 rounded-full object-cover" :src="contact.avatar" :alt="contact.name" />
          <span
            class="absolute bottom-0 right-0 w-3 h-3 rounded-full ring-2 ring-surface-container-lowest"
            :class="contact.statusClass"
            :title="contact.statusTitle"
          ></span>
        </div>
        <div class="flex flex-col min-w-0">
          <span
            class="font-label-lg text-label-lg text-on-surface group-hover:text-primary transition-colors flex items-center gap-1.5 cursor-pointer"
          >
            {{ contact.name }}
            <span
              v-if="contact.birthday"
              class="material-symbols-outlined text-sm text-tertiary-container"
              title="Cumpleaños este mes"
            >
              cake
            </span>
          </span>
          <div class="flex items-center gap-1.5 mt-1 flex-wrap">
            <span
              v-for="interest in contact.interests"
              :key="interest"
              class="px-2 py-0.5 rounded-full bg-surface-container font-label-sm text-label-sm text-on-surface-variant"
            >
              {{ interest }}
            </span>
          </div>
        </div>
      </div>
    </td>

    <!-- Círculo -->
    <td class="py-4 px-4">
      <span
        class="inline-flex items-center gap-1.5 px-2.5 py-1 rounded-full font-label-sm text-label-sm font-semibold"
        :class="contact.circleClass"
      >
        <span class="w-1.5 h-1.5 rounded-full" :class="contact.circleDotClass"></span>
        {{ contact.circle }}
      </span>
    </td>

    <!-- Empresa & Rol -->
    <td class="py-4 px-4">
      <div class="flex flex-col">
        <span class="font-label-md text-label-md text-on-surface font-semibold">{{ contact.role }}</span>
        <span class="font-body-sm text-body-sm text-on-surface-variant">{{ contact.company }}</span>
      </div>
    </td>

    <!-- Cadencia -->
    <td class="py-4 px-4">
      <span class="font-body-sm text-body-sm text-on-surface-variant bg-surface-container px-2.5 py-1 rounded-md">
        {{ contact.cadence }}
      </span>
    </td>

    <!-- Última interacción -->
    <td class="py-4 px-4">
      <div class="flex items-start gap-2.5">
        <div class="w-2 h-2 rounded-full shrink-0 mt-1.5" :class="contact.healthDotClass" :title="contact.healthTitle"></div>
        <div class="flex flex-col">
          <span class="font-label-md text-label-md font-medium" :class="contact.lastWhenClass">{{ contact.lastWhen }}</span>
          <span class="font-body-sm text-body-sm text-on-surface-variant">{{ contact.lastWhat }}</span>
        </div>
      </div>
    </td>

    <!-- Próxima acción -->
    <td class="py-4 px-4">
      <div class="flex items-center gap-2 p-2 rounded-lg bg-surface-container-high/60">
        <span class="material-symbols-outlined text-base" :class="contact.nextIconClass">{{ contact.nextIcon }}</span>
        <div class="flex flex-col">
          <span class="font-label-sm text-label-sm text-on-surface font-medium leading-tight">{{ contact.nextTitle }}</span>
          <span class="font-label-sm text-label-sm text-on-surface-variant">{{ contact.nextDetail }}</span>
        </div>
      </div>
    </td>

    <!-- Acciones -->
    <td class="py-4 px-6 text-right">
      <div class="flex items-center justify-end gap-1 text-on-surface-variant">
        <button
          v-for="action in quickActions"
          :key="action.title"
          type="button"
          class="p-1.5 hover:bg-surface-container rounded-lg transition-colors"
          :class="action.hover"
          :title="action.title"
        >
          <span class="material-symbols-outlined text-lg">{{ action.icon }}</span>
        </button>
      </div>
    </td>
  </tr>
</template>
