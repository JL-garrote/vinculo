<script setup lang="ts">
interface NavItem {
  path: string;
  label: string;
  icon: string;
}

const mainNav: NavItem[] = [
  { path: 'dashboard', label: 'Dashboard', icon: 'dashboard' },
  { path: 'contactos', label: 'Contactos', icon: 'group' },
  { path: 'ficha-contacto', label: 'Ficha Contacto', icon: 'account_circle' },
  { path: 'pipeline-relacional', label: 'Pipeline Relacional', icon: 'view_kanban' },
];

// Sección activa, controlada desde App.vue con v-model
const active = defineModel<string>({ default: 'dashboard' });
</script>

<template>
  <aside
    class="fixed left-0 top-0 h-full w-64 bg-surface-container-lowest shadow-[0_1px_8px_rgba(0,0,0,0.04)] z-50 flex flex-col justify-between py-6 px-4"
  >
    <div class="flex flex-col gap-8">
      <div class="flex items-center gap-3 px-2">
        <img
          alt="Vínculo CRM Logo"
          class="h-8 w-auto object-contain"
          src="https://lh3.googleusercontent.com/aida/AEtjO1U8hZvNywSZrh6x26qIc6sMeizN968yMF-AS9u-3GWwSIDCcTVu_h7BoNKA81VF5GvERF_anVhqD40AD5wE4gtYEaAa-e-H-KJEhkfkHgq7Ob-As6pnnBfVxwYUCXcBShe6ly5PSH7sVodJV9ASPRn5o_2xWIRycHt3O0XEaog3hu6TrgP7ylybFkms-jylW-QCK7ObBuyTf9hqlAcFYeL-0LjnldZGSEKV-D7-LlnDZcw8YU54pfJGpz8"
        />
        <div class="flex flex-col">
          <span class="font-headline-sm text-headline-sm text-on-surface leading-none">Vínculo</span>
          <span class="font-label-sm text-label-sm text-on-surface-variant tracking-wider uppercase mt-0.5">
            PRM Personal
          </span>
        </div>
      </div>

      <nav class="flex flex-col gap-1">
        <a
          v-for="item in mainNav"
          :key="item.path"
          href="#"
          :aria-current="active === item.path ? 'page' : undefined"
          class="flex items-center gap-3 px-3 py-2.5 rounded-lg font-label-lg text-label-lg transition-colors"
          :class="
            active === item.path
              ? 'bg-primary-container text-on-primary shadow-sm'
              : 'text-on-surface-variant hover:bg-surface-container-high hover:text-on-surface'
          "
          @click.prevent="active = item.path"
        >
          <span class="material-symbols-outlined text-lg">{{ item.icon }}</span>
          <span>{{ item.label }}</span>
        </a>
      </nav>
    </div>

    <div class="flex flex-col gap-1 border-t border-surface-container-high pt-4">
      <a
        href="#"
        class="flex items-center gap-3 px-3 py-2.5 rounded-lg font-label-lg text-label-lg text-on-surface-variant hover:bg-surface-container-high hover:text-on-surface transition-colors"
      >
        <span class="material-symbols-outlined text-lg">settings</span>
        <span>Ajustes</span>
      </a>
      <a
        href="#"
        class="flex items-center gap-3 px-3 py-2.5 rounded-lg font-label-lg text-label-lg text-error hover:bg-error-container hover:text-on-error-container transition-colors"
      >
        <span class="material-symbols-outlined text-lg">logout</span>
        <span>Cerrar sesión</span>
      </a>
    </div>
  </aside>
</template>
