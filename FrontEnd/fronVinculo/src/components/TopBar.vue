<template>
  <header
    class="fixed top-0 left-64 right-0 h-16 bg-surface-container-lowest/90 backdrop-blur-xl shadow-[0_1px_8px_rgba(0,0,0,0.04)] z-40 flex items-center justify-between px-8"
  >
    <div class="flex items-center gap-4 w-96">
      <div class="relative w-full flex items-center">
        <span class="material-symbols-outlined absolute left-3 text-on-surface-variant text-lg">search</span>
        <input
          type="text"
          placeholder="Buscar contactos, notas, círculos..."
          class="w-full pl-9 pr-12 py-2 bg-surface rounded-lg font-body-sm text-body-sm text-on-surface placeholder:text-outline-variant focus:outline-none focus:ring-2 focus:ring-primary/20"
        />
        <kbd
          class="absolute right-2.5 px-1.5 py-0.5 rounded bg-surface-container text-on-surface-variant font-label-sm text-label-sm"
        >
          ⌘K
        </kbd>
      </div>
    </div>

    <div class="flex items-center gap-4">
      <button
        type="button"
        class="flex items-center gap-2 px-3.5 py-2 bg-primary-container text-on-primary rounded-lg font-label-lg text-label-lg hover:bg-primary transition-colors shadow-sm"
      >
        <span class="material-symbols-outlined text-base">add</span>
        <span>Nuevo Contacto</span>
      </button>

      <button
        type="button"
        class="p-2 text-on-surface-variant hover:text-on-surface hover:bg-surface-container-high rounded-lg transition-colors relative"
      >
        <span class="material-symbols-outlined text-xl">notifications</span>
        <span class="absolute top-2 right-2 w-2 h-2 rounded-full bg-secondary"></span>
      </button>

      <div class="flex items-center gap-3 pl-2 border-l border-surface-container-high">
        <img
          alt="Profile"
          class="w-8 h-8 rounded-full object-cover"
          src="https://lh3.googleusercontent.com/aida-public/AB6AXuBxyfgTz2IKWtvPRsOaO4L2zLZNHHBCuq53dzmQNwnRyiy6vj3xunSuXy-i9ReGIJH_Ujrx9L57jRDkQ0ZZCoYtpMLNEoXvVd9io8qWeq3J3rs9jv0A0ptBaQ9VrflovM3hCZ_YA-xkQz5bs_SAlHKgZ7nYvupvQ_PoXSTU1Ij9kdMoeWdWRgLvMDPv6BaK5PA4IYUPvvCQ8Df_ki0C_CiroIcpdTXAMUiCa1XOLG0V7VlA6CPfYDQb"
        />
        <div class="hidden lg:flex flex-col text-left">
          <span class="font-label-md text-label-md text-on-surface leading-tight">Martín Silva</span>
          <span class="font-label-sm text-label-sm text-on-surface-variant">Relaciones Pro</span>
        </div>
      </div>
    </div>
  </header>
</template>
