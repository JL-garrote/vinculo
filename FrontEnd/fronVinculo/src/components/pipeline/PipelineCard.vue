<script setup lang="ts">
export type Priority = 'Alta' | 'Media' | 'Normal';

export interface Tag {
  label: string;
  class: string;
}

// Bloque central de la tarjeta: cambia según la etapa en la que nació
export type CardDetail =
  | {
      kind: 'intro';
      icon: string;
      label: string;
      value: string;
      affinity: number;
      affinityClass: string;
      whenIcon: string;
      when: string;
    }
  | {
      kind: 'contacto';
      channelIcon: string;
      channel: string;
      ago: string;
      agoClass: string;
      quote: string;
      tag: string;
      actionIcon: string;
      action: string;
      actionClass: string;
    }
  | {
      kind: 'reunion';
      highlight?: boolean;
      whenIcon: string;
      when: string;
      place: string;
      topic: string;
      prep?: string;
      tag: Tag;
      statusIcon: string;
      status: string;
      statusClass: string;
    }
  | {
      kind: 'seguimiento';
      cadenceIcon: string;
      cadence: string;
      cadenceClass: string;
      touch: string;
      touchClass: string;
      milestone: string;
      tags: Tag[];
      overdue?: boolean;
    }
  | {
      kind: 'establecida';
      badgeIcon: string;
      badge: string;
      title: string;
      cadence: string;
      text: string;
      footIcon: string;
      foot: string;
      nextIcon: string;
      next: string;
    };

export interface PipelineContact {
  id: number;
  name: string;
  role: string;
  avatar: string;
  statusClass: string;
  statusTitle?: string;
  priority: Priority;
  industry: string;
  accentClass?: string;
  detail: CardDetail;
}

defineProps<{ card: PipelineContact }>();

const priorityClass: Record<Priority, string> = {
  Alta: 'text-error bg-error-container',
  Media: 'text-on-tertiary-fixed-variant bg-tertiary-fixed',
  Normal: 'text-outline bg-surface-container-high',
};
</script>

<template>
  <div
    class="p-4 rounded-xl bg-surface-container-lowest shadow-sm hover:shadow-md transition-all cursor-grab active:cursor-grabbing group flex flex-col gap-3"
    :class="card.accentClass"
  >
    <!-- Cabecera -->
    <div class="flex items-start justify-between gap-3">
      <div class="relative shrink-0">
        <img class="w-11 h-11 rounded-full object-cover" :src="card.avatar" :alt="card.name" draggable="false" />
        <span
          class="absolute -bottom-0.5 -right-0.5 w-3.5 h-3.5 rounded-full ring-2 ring-surface-container-lowest"
          :class="card.statusClass"
          :title="card.statusTitle"
        ></span>
      </div>
      <div class="flex flex-col flex-1 min-w-0">
        <div class="flex items-center justify-between gap-2">
          <h4 class="font-label-lg text-label-lg text-on-surface truncate group-hover:text-primary transition-colors">
            {{ card.name }}
          </h4>
          <span
            v-if="card.detail.kind === 'establecida'"
            class="px-2 py-0.5 rounded-full bg-secondary text-on-secondary font-label-sm text-label-sm flex items-center gap-1 shrink-0"
          >
            <span class="material-symbols-outlined text-xs">{{ card.detail.badgeIcon }}</span> {{ card.detail.badge }}
          </span>
          <span
            v-else
            class="px-1.5 py-0.5 rounded font-label-sm text-label-sm shrink-0"
            :class="priorityClass[card.priority]"
          >
            {{ card.priority }}
          </span>
        </div>
        <span class="font-body-sm text-body-sm text-on-surface-variant truncate">{{ card.role }}</span>
      </div>
    </div>

    <!-- Identificados: introducción -->
    <template v-if="card.detail.kind === 'intro'">
      <div class="p-2.5 rounded-lg bg-surface-container-low flex flex-col gap-1">
        <span class="font-label-sm text-label-sm text-on-surface-variant flex items-center gap-1">
          <span class="material-symbols-outlined text-xs text-primary">{{ card.detail.icon }}</span>
          {{ card.detail.label }}
        </span>
        <span class="font-body-sm text-body-sm text-on-surface font-medium truncate">{{ card.detail.value }}</span>
      </div>
      <div class="flex items-center justify-between text-on-surface-variant pt-1">
        <span class="px-2 py-0.5 rounded-full font-label-sm text-label-sm" :class="card.detail.affinityClass">
          Afinidad: {{ card.detail.affinity }}%
        </span>
        <span class="font-label-sm text-label-sm flex items-center gap-1">
          <span class="material-symbols-outlined text-xs">{{ card.detail.whenIcon }}</span> {{ card.detail.when }}
        </span>
      </div>
    </template>

    <!-- Primer contacto: canal y tiempo -->
    <template v-else-if="card.detail.kind === 'contacto'">
      <div class="p-2.5 rounded-lg bg-surface-container-low flex flex-col gap-1.5">
        <div class="flex items-center justify-between font-label-sm text-label-sm">
          <span class="text-on-surface-variant flex items-center gap-1">
            <span class="material-symbols-outlined text-xs text-primary">{{ card.detail.channelIcon }}</span>
            {{ card.detail.channel }}
          </span>
          <span class="font-medium" :class="card.detail.agoClass">{{ card.detail.ago }}</span>
        </div>
        <p class="font-body-sm text-body-sm text-on-surface line-clamp-1 italic">"{{ card.detail.quote }}"</p>
      </div>
      <div class="flex items-center justify-between text-on-surface-variant pt-1">
        <span class="px-2 py-0.5 rounded bg-surface-container text-on-surface font-label-sm text-label-sm">
          {{ card.detail.tag }}
        </span>
        <span class="font-label-sm text-label-sm flex items-center gap-1" :class="card.detail.actionClass">
          <span class="material-symbols-outlined text-xs">{{ card.detail.actionIcon }}</span> {{ card.detail.action }}
        </span>
      </div>
    </template>

    <!-- Reunión / café: agenda y preparación -->
    <template v-else-if="card.detail.kind === 'reunion'">
      <div
        class="p-2.5 rounded-lg flex flex-col gap-2"
        :class="card.detail.highlight ? 'bg-surface-container' : 'bg-surface-container-low'"
      >
        <div class="flex items-center justify-between">
          <span
            class="font-label-sm text-label-sm font-semibold flex items-center gap-1"
            :class="card.detail.highlight ? 'text-primary' : 'text-on-surface'"
          >
            <span class="material-symbols-outlined text-sm">{{ card.detail.whenIcon }}</span> {{ card.detail.when }}
          </span>
          <span class="font-label-sm text-label-sm text-on-surface-variant">{{ card.detail.place }}</span>
        </div>
        <div class="font-body-sm text-body-sm text-on-surface">
          <span class="font-medium text-on-surface block">Tema central:</span>
          <span>{{ card.detail.topic }}</span>
        </div>
        <div v-if="card.detail.prep" class="flex items-center gap-1 text-on-surface-variant font-label-sm text-label-sm">
          <span class="material-symbols-outlined text-xs">notes</span>
          <span>{{ card.detail.prep }}</span>
        </div>
      </div>
      <div class="flex items-center justify-between text-on-surface-variant pt-1">
        <span class="px-2 py-0.5 rounded font-label-sm text-label-sm" :class="card.detail.tag.class">
          {{ card.detail.tag.label }}
        </span>
        <span class="font-label-sm text-label-sm flex items-center gap-1" :class="card.detail.statusClass">
          <span class="material-symbols-outlined text-xs">{{ card.detail.statusIcon }}</span> {{ card.detail.status }}
        </span>
      </div>
    </template>

    <!-- Seguimiento activo: cadencia e hito -->
    <template v-else-if="card.detail.kind === 'seguimiento'">
      <div class="p-2.5 rounded-lg bg-surface-container-low flex flex-col gap-1.5">
        <div class="flex items-center justify-between font-label-sm text-label-sm">
          <span class="font-semibold flex items-center gap-1" :class="card.detail.cadenceClass">
            <span class="material-symbols-outlined text-xs">{{ card.detail.cadenceIcon }}</span> {{ card.detail.cadence }}
          </span>
          <span :class="card.detail.touchClass">{{ card.detail.touch }}</span>
        </div>
        <p class="font-body-sm text-body-sm text-on-surface">
          <span class="font-medium">Último hito:</span> {{ card.detail.milestone }}
        </p>
      </div>
      <div class="flex items-center justify-between text-on-surface-variant pt-1">
        <div class="flex gap-1">
          <span
            v-for="tag in card.detail.tags"
            :key="tag.label"
            class="px-2 py-0.5 rounded-full font-label-sm text-label-sm"
            :class="tag.class"
          >
            {{ tag.label }}
          </span>
        </div>
        <button
          v-if="card.detail.overdue"
          type="button"
          class="px-2.5 py-1 rounded bg-primary text-on-primary font-label-sm text-label-sm hover:bg-primary-container transition-colors"
        >
          Contactar
        </button>
        <span v-else class="material-symbols-outlined text-secondary text-base" title="Ritmo saludable">favorite</span>
      </div>
    </template>

    <!-- Relación establecida: acuerdo -->
    <template v-else-if="card.detail.kind === 'establecida'">
      <div class="p-2.5 rounded-lg bg-surface-container-low flex flex-col gap-1.5">
        <div class="flex items-center justify-between font-label-sm text-label-sm">
          <span class="text-secondary font-medium">{{ card.detail.title }}</span>
          <span class="text-on-surface-variant">{{ card.detail.cadence }}</span>
        </div>
        <p class="font-body-sm text-body-sm text-on-surface">{{ card.detail.text }}</p>
      </div>
      <div class="flex items-center justify-between text-on-surface-variant pt-1">
        <span class="font-label-sm text-label-sm text-secondary flex items-center gap-1 font-semibold">
          <span class="material-symbols-outlined text-xs">{{ card.detail.footIcon }}</span> {{ card.detail.foot }}
        </span>
        <span class="font-label-sm text-label-sm flex items-center gap-1">
          <span class="material-symbols-outlined text-xs">{{ card.detail.nextIcon }}</span> {{ card.detail.next }}
        </span>
      </div>
    </template>
  </div>
</template>
