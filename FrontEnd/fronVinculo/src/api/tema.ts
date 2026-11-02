import { ref, watch } from 'vue';

export type Tema = 'claro' | 'oscuro';

const CLAVE = 'vinculo-tema';

// Lo que eligió el usuario; si nunca eligió, sigue al sistema operativo.
function temaInicial(): Tema {
  try {
    const guardado = localStorage.getItem(CLAVE);
    if (guardado === 'claro' || guardado === 'oscuro') return guardado;
  } catch {
    // localStorage puede no estar disponible (modo privado)
  }
  return window.matchMedia('(prefers-color-scheme: dark)').matches ? 'oscuro' : 'claro';
}

export const tema = ref<Tema>(temaInicial());

watch(
  tema,
  (valor) => document.documentElement.classList.toggle('dark', valor === 'oscuro'),
  { immediate: true },
);

export function alternarTema() {
  tema.value = tema.value === 'oscuro' ? 'claro' : 'oscuro';
  try {
    localStorage.setItem(CLAVE, tema.value);
  } catch {
    // sin persistencia, el cambio vale para esta sesión
  }
}
