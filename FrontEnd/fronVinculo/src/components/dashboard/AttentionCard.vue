<script setup lang="ts">
export interface CardAction {
  label: string;
  icon: string;
  primary?: boolean;
  iconClass?: string;
}

export interface AttentionContact {
  name: string;
  role: string;
  avatar: string;
  statusClass: string;
  statusTitle: string;
  circle: string;
  circleClass: string;
  alert: string;
  alertIcon: string;
  alertClass: string;
  meta: string;
  note: string;
  actions: [CardAction, CardAction];
  secondaryAction: string;
}

defineProps<{ contact: AttentionContact }>();
</script>

<template>
  <div class="bg-surface-container-lowest rounded-xl p-5 shadow-sm flex flex-col justify-between gap-5 transition-all hover:shadow-md">
    <div class="flex flex-col gap-3">
      <div class="flex items-start justify-between gap-3">
        <div class="flex items-center gap-3">
          <div class="relative">
            <img class="w-12 h-12 rounded-full object-cover shadow-sm" :src="contact.avatar" :alt="contact.name" />
            <span
              class="absolute -bottom-0.5 -right-0.5 w-3.5 h-3.5 rounded-full border-2 border-surface-container-lowest"
              :class="contact.statusClass"
              :title="contact.statusTitle"
            ></span>
          </div>
          <div class="flex flex-col min-w-0">
            <span class="font-label-lg text-label-lg text-on-surface truncate">{{ contact.name }}</span>
            <span class="font-body-sm text-body-sm text-on-surface-variant truncate">{{ contact.role }}</span>
          </div>
        </div>
        <span
          class="inline-flex items-center px-2 py-0.5 rounded-md text-label-sm font-label-sm whitespace-nowrap"
          :class="contact.circleClass"
        >
          {{ contact.circle }}
        </span>
      </div>

      <div class="bg-surface-container-low rounded-lg p-3 flex flex-col gap-1.5">
        <div class="flex items-center justify-between text-label-sm font-label-sm">
          <span class="flex items-center gap-1" :class="contact.alertClass">
            <span class="material-symbols-outlined text-sm">{{ contact.alertIcon }}</span>
            {{ contact.alert }}
          </span>
          <span class="text-on-surface-variant">{{ contact.meta }}</span>
        </div>
        <p class="font-body-sm text-body-sm text-on-surface-variant line-clamp-2">{{ contact.note }}</p>
      </div>
    </div>

    <div class="flex flex-col gap-2 pt-2">
      <div class="grid grid-cols-2 gap-2">
        <button
          v-for="action in contact.actions"
          :key="action.label"
          type="button"
          class="flex items-center justify-center gap-1.5 py-2 px-3 rounded-lg text-label-md font-label-md transition-colors"
          :class="
            action.primary
              ? 'bg-primary-container hover:bg-primary text-on-primary shadow-sm'
              : 'bg-surface-container-high hover:bg-surface-container-highest text-on-surface'
          "
        >
          <span class="material-symbols-outlined text-base" :class="action.iconClass">{{ action.icon }}</span>
          {{ action.label }}
        </button>
      </div>
      <button
        type="button"
        class="w-full text-center py-1.5 text-label-sm font-label-sm text-on-surface-variant hover:text-on-surface transition-colors"
      >
        {{ contact.secondaryAction }}
      </button>
    </div>
  </div>
</template>
