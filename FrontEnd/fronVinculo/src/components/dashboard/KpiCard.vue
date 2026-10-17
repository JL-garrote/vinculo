<script setup lang="ts">
export interface Kpi {
  label: string;
  value: string;
  valueClass?: string;
  detail: string;
  detailClass?: string;
  icon: string;
  iconClass: string;
  badge: string;
  badgeIcon?: string;
  badgeClass: string;
  progress: number;
  progressClass: string;
}

defineProps<{ kpi: Kpi }>();
</script>

<template>
  <div
    class="bg-surface-container-lowest p-5 rounded-xl shadow-sm flex flex-col justify-between gap-4 hover:-translate-y-0.5 transition-transform"
  >
    <div class="flex items-start justify-between">
      <div class="p-2 rounded-lg" :class="kpi.iconClass">
        <span class="material-symbols-outlined text-xl">{{ kpi.icon }}</span>
      </div>
      <span class="inline-flex items-center gap-1 text-label-sm font-label-sm" :class="kpi.badgeClass">
        <span v-if="kpi.badgeIcon" class="material-symbols-outlined text-sm">{{ kpi.badgeIcon }}</span>
        {{ kpi.badge }}
      </span>
    </div>

    <div>
      <span class="text-label-md font-label-md text-on-surface-variant uppercase tracking-wider block">
        {{ kpi.label }}
      </span>
      <div class="flex items-baseline gap-2 mt-0.5">
        <span class="text-headline-md font-headline-md" :class="kpi.valueClass ?? 'text-on-surface'">
          {{ kpi.value }}
        </span>
        <span class="text-body-sm font-body-sm" :class="kpi.detailClass ?? 'text-on-surface-variant'">
          {{ kpi.detail }}
        </span>
      </div>
    </div>

    <div class="w-full bg-surface-container h-1 rounded-full overflow-hidden">
      <div class="h-full rounded-full" :class="kpi.progressClass" :style="{ width: `${kpi.progress}%` }"></div>
    </div>
  </div>
</template>
