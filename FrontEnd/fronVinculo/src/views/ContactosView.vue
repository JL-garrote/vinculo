<script setup lang="ts">
import { computed, onBeforeUnmount, ref, watch } from 'vue';
import ContactRow from '@/components/contactos/ContactRow.vue';
import NuevoCirculoDialog from '@/components/formularios/NuevoCirculoDialog.vue';
import ConfirmarBorradoDialog from '@/components/ConfirmarBorradoDialog.vue';
import ImportarContactosDialog from '@/components/formularios/ImportarContactosDialog.vue';
import { borrarCirculo, store } from '@/api/store';
import { estadoProximoPaso, estiloEstado, lista, nombreCompleto, parseFecha, type EstadoProximoPaso } from '@/api/format';

defineEmits<{ abrir: [id: number]; nuevoContacto: [] }>();

const creandoCirculo = ref(false);
const borrandoCirculo = ref(false);
const editandoCirculo = ref(false);
const importando = ref(false);

const total = computed(() => store.contactos.length);
const numero = (n: number) => n.toLocaleString('es-ES');
const plural = (n: number, uno: string, varios: string) => `${numero(n)} ${n === 1 ? uno : varios}`;
const contarEstado = (estado: EstadoProximoPaso) =>
  store.contactos.filter((c) => estadoProximoPaso(c) === estado).length;

// Resumen de la red en una línea, en vez de tarjetas que repetían el Dashboard
const resumen = computed(() => {
  const conCirculo = store.contactos.filter((c) => c.circuloID > 0).length;
  const prioritarios = store.contactos.filter((c) => c.prioritario).length;
  return [
    plural(total.value, 'contacto', 'contactos'),
    `${numero(conCirculo)} en círculos`,
    plural(prioritarios, 'prioritario', 'prioritarios'),
  ].join(' · ');
});

// Atajos a los estados que piden acción: aplican el mismo filtro que el selector "Próximo paso"
const atajos = computed(() => [
  { estado: 'vencido' as const, icon: 'event_busy', texto: plural(contarEstado('vencido'), 'vencido', 'vencidos'), n: contarEstado('vencido') },
  { estado: 'pronto' as const, icon: 'event_upcoming', texto: `${numero(contarEstado('pronto'))} esta semana`, n: contarEstado('pronto') },
]);

function alternarAtajo(estado: EstadoProximoPaso) {
  estadoFilter.value = estadoFilter.value === estado ? 'all' : estado;
}

// Píldoras de círculo: "Todos", uno por círculo y "Sin círculo" si hace falta
const circleFilters = computed(() => {
  const filtros = [
    { key: 0, label: 'Todos', count: total.value },
    ...store.circulos.map((c) => ({
      key: c.id,
      label: c.nombre,
      count: store.contactos.filter((x) => x.circuloID === c.id).length,
    })),
  ];
  const sinCirculo = store.contactos.filter((c) => c.circuloID === 0).length;
  if (sinCirculo) filtros.push({ key: -1, label: 'Sin círculo', count: sinCirculo });
  return filtros;
});

const ciudades = computed(() =>
  [...new Set(store.contactos.map((c) => c.ciudad).filter((c): c is string => !!c))].sort((a, b) =>
    a.localeCompare(b, 'es'),
  ),
);

// Filtros
const activeCircle = ref(0);
const circuloActivo = computed(() => store.circulos.find((c) => c.id === activeCircle.value));
const contactosDelCirculoActivo = computed(
  () => store.contactos.filter((c) => c.circuloID === activeCircle.value).length,
);

function circuloBorrado() {
  borrandoCirculo.value = false;
  activeCircle.value = 0;
}
const search = ref('');

// Con miles de contactos, filtrar y ordenar en cada tecla se nota: se busca cuando dejas de escribir
const textoBusqueda = ref('');
let esperaBusqueda: ReturnType<typeof setTimeout> | undefined;
watch(textoBusqueda, (texto) => {
  clearTimeout(esperaBusqueda);
  esperaBusqueda = setTimeout(() => (search.value = texto), 200);
});
onBeforeUnmount(() => clearTimeout(esperaBusqueda));
const estadoFilter = ref<'all' | EstadoProximoPaso>('all');
const ciudad = ref('all');
const sortBy = ref<'urgent' | 'alpha' | 'created'>('urgent');
const rowsPerPage = ref(10);
const page = ref(1);

const hayFiltros = computed(
  () => activeCircle.value !== 0 || !!textoBusqueda.value.trim() || estadoFilter.value !== 'all' || ciudad.value !== 'all',
);

function quitarFiltros() {
  activeCircle.value = 0;
  textoBusqueda.value = '';
  search.value = '';
  estadoFilter.value = 'all';
  ciudad.value = 'all';
}

const fechaPaso = (fecha: string | null) => parseFecha(fecha)?.getTime() ?? Number.POSITIVE_INFINITY;

const filteredContacts = computed(() => {
  const q = search.value.trim().toLowerCase();

  const resultado = store.contactos.filter((c) => {
    if (activeCircle.value === -1 && c.circuloID !== 0) return false;
    if (activeCircle.value > 0 && c.circuloID !== activeCircle.value) return false;
    if (estadoFilter.value !== 'all' && estadoProximoPaso(c) !== estadoFilter.value) return false;
    if (ciudad.value !== 'all' && c.ciudad !== ciudad.value) return false;
    if (!q) return true;
    return [nombreCompleto(c), c.empresa, c.cargo, c.ciudad, c.email, ...lista(c.intereses)]
      .filter(Boolean)
      .some((campo) => campo!.toLowerCase().includes(q));
  });

  if (sortBy.value === 'urgent') return resultado.sort((a, b) => fechaPaso(a.proximoPasoFecha) - fechaPaso(b.proximoPasoFecha));
  if (sortBy.value === 'alpha') return resultado.sort((a, b) => nombreCompleto(a, false).localeCompare(nombreCompleto(b, false), 'es'));
  return resultado.sort((a, b) => (parseFecha(b.creadoEn)?.getTime() ?? 0) - (parseFecha(a.creadoEn)?.getTime() ?? 0));
});

// Paginación
const totalPages = computed(() => Math.max(1, Math.ceil(filteredContacts.value.length / rowsPerPage.value)));
const pageContacts = computed(() =>
  filteredContacts.value.slice((page.value - 1) * rowsPerPage.value, page.value * rowsPerPage.value),
);
const rangeStart = computed(() => (filteredContacts.value.length ? (page.value - 1) * rowsPerPage.value + 1 : 0));
const rangeEnd = computed(() => Math.min(page.value * rowsPerPage.value, filteredContacts.value.length));

// Números de página a mostrar: la primera, la última y las vecinas de la actual; null = hueco "…"
const paginasVisibles = computed<(number | null)[]>(() => {
  const total = totalPages.value;
  const actual = page.value;
  const numeros = [...new Set([1, actual - 1, actual, actual + 1, total])]
    .filter((n) => n >= 1 && n <= total)
    .sort((a, b) => a - b);
  const resultado: (number | null)[] = [];
  numeros.forEach((n, i) => {
    const anterior = numeros[i - 1];
    // Si solo falta una página entre dos números, se muestra en vez de "…"
    if (anterior !== undefined && n - anterior === 2) resultado.push(anterior + 1);
    else if (anterior !== undefined && n - anterior > 2) resultado.push(null);
    resultado.push(n);
  });
  return resultado;
});

watch([activeCircle, search, estadoFilter, ciudad, sortBy, rowsPerPage], () => (page.value = 1));

const selectClass =
  'appearance-none bg-surface-container-low hover:bg-surface-container text-on-surface font-label-md text-base sm:text-label-md py-2.5 pl-3.5 pr-9 rounded-lg transition-colors cursor-pointer';
</script>

<template>
  <div class="px-4 sm:px-6 lg:px-8 py-6 sm:py-8 flex flex-col gap-6 sm:gap-8 max-w-[1600px] mx-auto w-full">
    <!-- Cabecera -->
    <section class="flex flex-col md:flex-row md:items-end justify-between gap-5">
      <div class="flex flex-col gap-2 min-w-0">
        <h1 class="font-headline-lg-mobile text-headline-lg-mobile md:font-headline-lg md:text-headline-lg text-on-surface tracking-tight">
          Directorio de Relaciones
        </h1>
        <p class="font-body-md text-body-md text-on-surface-variant tabular-nums">{{ resumen }}</p>
        <!-- Lo que pide acción, a un clic: filtra la tabla (y se refleja en el selector "Próximo paso") -->
        <div class="flex flex-wrap items-center gap-2 mt-1">
          <button
            v-for="atajo in atajos"
            :key="atajo.estado"
            type="button"
            class="flex items-center gap-1.5 px-3 py-1.5 rounded-lg font-label-lg text-label-lg tabular-nums transition-colors disabled:opacity-50 disabled:cursor-default"
            :class="
              estadoFilter === atajo.estado
                ? 'bg-primary-container text-on-primary'
                : 'bg-surface-container-lowest text-on-surface shadow-sm enabled:hover:bg-surface-container-high'
            "
            :aria-pressed="estadoFilter === atajo.estado"
            :disabled="!atajo.n && estadoFilter !== atajo.estado"
            @click="alternarAtajo(atajo.estado)"
          >
            <span
              aria-hidden="true"
              class="material-symbols-outlined text-base"
              :class="estadoFilter === atajo.estado ? '' : estiloEstado[atajo.estado].texto"
            >
              {{ atajo.icon }}
            </span>
            {{ atajo.texto }}
          </button>
        </div>
      </div>
      <div class="flex flex-wrap items-center gap-3 self-start md:self-auto">
        <button
          type="button"
          class="flex items-center gap-2 px-4 py-2.5 bg-surface-container-lowest text-on-surface rounded-lg font-label-lg text-label-lg hover:bg-surface-container transition-all shadow-sm"
          @click="importando = true"
        >
          <span aria-hidden="true" class="material-symbols-outlined text-lg text-primary">cloud_download</span>
          <span>Importar contactos</span>
        </button>
        <button
          type="button"
          class="flex items-center gap-2 px-5 py-2.5 bg-primary-container text-on-primary rounded-lg font-label-lg text-label-lg hover:bg-primary dark:hover:bg-primary-fixed-dim transition-all shadow-sm"
          @click="$emit('nuevoContacto')"
        >
          <span aria-hidden="true" class="material-symbols-outlined text-lg">person_add</span>
          <span>Añadir contacto</span>
        </button>
      </div>
    </section>

    <!-- Filtros -->
    <section class="flex flex-col gap-4">
      <div class="flex items-center gap-2 overflow-x-auto pb-1">
        <button
          v-for="filter in circleFilters"
          :key="filter.key"
          type="button"
          class="px-4 py-2 rounded-full font-label-lg text-label-lg flex items-center gap-2 shrink-0 shadow-sm transition-colors"
          :class="
            activeCircle === filter.key
              ? 'bg-primary-container text-on-primary'
              : 'bg-surface-container-lowest text-on-surface-variant hover:text-on-surface hover:bg-surface-container-high'
          "
          :aria-pressed="activeCircle === filter.key"
          :title="filter.label"
          @click="activeCircle = filter.key"
        >
          <span class="max-w-48 truncate">{{ filter.label }}</span>
          <span
            class="px-1.5 rounded-full font-label-sm text-label-sm"
            :class="activeCircle === filter.key ? 'bg-on-primary/20 text-on-primary' : 'bg-surface-container text-on-surface-variant'"
          >
            {{ numero(filter.count) }}
          </span>
        </button>
        <button
          type="button"
          class="px-4 py-2 rounded-full font-label-lg text-label-lg flex items-center gap-1 shrink-0 border border-dashed border-outline-variant text-on-surface-variant hover:text-primary hover:border-primary transition-colors"
          @click="creandoCirculo = true"
        >
          <span aria-hidden="true" class="material-symbols-outlined text-base">add</span> Nuevo círculo
        </button>
        <button
          v-if="circuloActivo"
          type="button"
          class="px-4 py-2 rounded-full font-label-lg text-label-lg flex items-center gap-1 shrink-0 text-on-surface-variant hover:text-on-surface hover:bg-surface-container-high transition-colors"
          @click="editandoCirculo = true"
        >
          <span aria-hidden="true" class="material-symbols-outlined text-base">edit</span> Editar círculo
        </button>
        <button
          v-if="circuloActivo"
          type="button"
          class="px-4 py-2 rounded-full font-label-lg text-label-lg flex items-center gap-1 shrink-0 text-error hover:bg-error-container transition-colors"
          @click="borrandoCirculo = true"
        >
          <span aria-hidden="true" class="material-symbols-outlined text-base">delete</span> Eliminar círculo
        </button>
      </div>

      <div
        class="flex flex-col lg:flex-row items-stretch lg:items-center justify-between gap-3 bg-surface-container-lowest p-3 rounded-xl shadow-sm"
      >
        <div class="relative flex-1">
          <span aria-hidden="true" class="material-symbols-outlined absolute left-3.5 top-1/2 -translate-y-1/2 text-on-surface-variant text-xl">
            search
          </span>
          <input
            v-model="textoBusqueda"
            type="search"
            aria-label="Buscar contactos"
            placeholder="Buscar por nombre, empresa, interés o ciudad..."
            class="w-full pl-11 pr-4 py-2.5 bg-surface-container-low rounded-lg font-body-sm text-base sm:text-body-sm text-on-surface placeholder:text-outline transition-colors"
          />
        </div>

        <div class="flex items-center flex-wrap sm:flex-nowrap gap-2.5">
          <div class="relative">
            <select v-model="estadoFilter" aria-label="Filtrar por próximo paso" :class="selectClass">
              <option value="all">Próximo paso: Todos</option>
              <option value="vencido">Vencido</option>
              <option value="pronto">En los próximos 7 días</option>
              <option value="programado">Programado más adelante</option>
              <option value="sin-fecha">Sin próximo paso</option>
            </select>
            <span
              aria-hidden="true"
              class="material-symbols-outlined absolute right-2.5 top-1/2 -translate-y-1/2 text-on-surface-variant pointer-events-none text-base"
            >
              expand_more
            </span>
          </div>
          <div class="relative">
            <select v-model="ciudad" aria-label="Filtrar por ubicación" :class="selectClass">
              <option value="all">Ubicación: Todas</option>
              <option v-for="nombre in ciudades" :key="nombre" :value="nombre">{{ nombre }}</option>
            </select>
            <span
              aria-hidden="true"
              class="material-symbols-outlined absolute right-2.5 top-1/2 -translate-y-1/2 text-on-surface-variant pointer-events-none text-base"
            >
              expand_more
            </span>
          </div>
          <div class="relative">
            <select v-model="sortBy" aria-label="Ordenar contactos" :class="selectClass">
              <option value="urgent">Ordenar: Próximo paso</option>
              <option value="alpha">Nombre (A-Z)</option>
              <option value="created">Fecha de adición</option>
            </select>
            <span
              aria-hidden="true"
              class="material-symbols-outlined absolute right-2.5 top-1/2 -translate-y-1/2 text-on-surface-variant pointer-events-none text-base"
            >
              sort
            </span>
          </div>
        </div>
      </div>
    </section>

    <!-- Tabla de contactos -->
    <div class="w-full bg-surface-container-lowest rounded-xl shadow-sm overflow-hidden flex flex-col">
      <div class="overflow-x-auto w-full">
        <table class="w-full text-left border-collapse">
          <caption class="sr-only">Contactos</caption>
          <thead>
            <tr class="bg-surface-container-low/70 text-on-surface-variant font-label-md text-label-md uppercase tracking-wider">
              <th scope="col" class="py-3.5 px-4 sm:px-6 font-semibold min-w-[180px]">Nombre &amp; Intereses</th>
              <th scope="col" class="py-3.5 px-4 font-semibold hidden md:table-cell">Círculo</th>
              <th scope="col" class="py-3.5 px-4 font-semibold min-w-[160px] hidden xl:table-cell">Empresa &amp; Rol</th>
              <th scope="col" class="py-3.5 px-4 font-semibold min-w-[140px] hidden min-[1800px]:table-cell">Cadencia</th>
              <th scope="col" class="py-3.5 px-4 font-semibold min-w-[180px] hidden 2xl:table-cell">Canal Preferido</th>
              <th scope="col" class="py-3.5 px-4 font-semibold min-w-[150px] hidden sm:table-cell">Próximo Paso</th>
              <th scope="col" class="py-3.5 px-4 sm:px-6 font-semibold text-right">Acciones</th>
            </tr>
          </thead>
          <tbody class="text-on-surface font-body-sm text-body-sm">
            <ContactRow
              v-for="(contacto, i) in pageContacts"
              :key="contacto.id"
              :contacto="contacto"
              :striped="i % 2 === 1"
              @abrir="$emit('abrir', $event)"
            />
            <tr v-if="pageContacts.length === 0">
              <td colspan="7" class="py-10 text-center font-body-sm text-body-sm text-on-surface-variant">
                <p>{{ total ? 'No hay contactos que coincidan con los filtros.' : 'Todavía no hay contactos.' }}</p>
                <button
                  v-if="total && hayFiltros"
                  type="button"
                  class="mt-3 px-3 py-1.5 rounded-lg font-label-lg text-label-lg text-primary hover:bg-surface-container transition-colors"
                  @click="quitarFiltros"
                >
                  Quitar filtros
                </button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- Paginación -->
      <div class="p-4 bg-surface-container-lowest flex flex-col sm:flex-row items-center justify-between gap-4">
        <div class="flex flex-wrap items-center justify-center gap-3">
          <!-- Se anuncia al cambiar filtros o búsqueda, para quien no ve cómo cambia la tabla -->
          <span class="font-body-sm text-body-sm text-on-surface-variant tabular-nums" aria-live="polite">
            <template v-if="filteredContacts.length">
              Mostrando <span class="font-semibold text-on-surface">{{ numero(rangeStart) }}–{{ numero(rangeEnd) }}</span> de
              <span class="font-semibold text-on-surface">{{ numero(filteredContacts.length) }}</span>
              {{ filteredContacts.length === 1 ? 'contacto' : 'contactos' }}
            </template>
            <template v-else>Ningún contacto coincide</template>
          </span>
          <div class="h-4 w-px bg-surface-container-high hidden sm:block"></div>
          <label class="flex items-center gap-2">
            <span class="font-label-sm text-label-sm text-on-surface-variant">Filas por página:</span>
            <select
              v-model.number="rowsPerPage"
              class="bg-surface-container-low text-on-surface font-label-sm text-base sm:text-label-sm py-1.5 px-2.5 rounded-lg cursor-pointer"
            >
              <option :value="10">10</option>
              <option :value="25">25</option>
              <option :value="50">50</option>
              <option :value="100">100</option>
            </select>
          </label>
        </div>

        <nav v-if="totalPages > 1" aria-label="Paginación de contactos" class="flex flex-wrap items-center justify-center gap-1.5">
          <button
            type="button"
            :disabled="page === 1"
            aria-label="Primera página"
            title="Primera página"
            class="p-2 rounded-lg text-on-surface hover:bg-surface-container disabled:opacity-40 disabled:hover:bg-transparent transition-colors"
            @click="page = 1"
          >
            <span aria-hidden="true" class="material-symbols-outlined text-lg">first_page</span>
          </button>
          <button
            type="button"
            :disabled="page === 1"
            aria-label="Página anterior"
            title="Página anterior"
            class="p-2 rounded-lg text-on-surface hover:bg-surface-container disabled:opacity-40 disabled:hover:bg-transparent transition-colors"
            @click="page--"
          >
            <span aria-hidden="true" class="material-symbols-outlined text-lg">chevron_left</span>
          </button>
          <div class="flex items-center gap-1">
            <template v-for="(n, i) in paginasVisibles" :key="n ?? `hueco-${i}`">
              <span v-if="n === null" aria-hidden="true" class="w-6 text-center text-on-surface-variant select-none">…</span>
              <button
                v-else
                type="button"
                class="min-w-8 h-8 px-1.5 rounded-lg font-label-md text-label-md tabular-nums flex items-center justify-center transition-colors"
                :class="
                  n === page
                    ? 'bg-primary-container text-on-primary font-semibold'
                    : 'text-on-surface-variant hover:bg-surface-container hover:text-on-surface'
                "
                :aria-label="`Página ${n}`"
                :aria-current="n === page ? 'page' : undefined"
                @click="page = n"
              >
                {{ n }}
              </button>
            </template>
          </div>
          <button
            type="button"
            :disabled="page === totalPages"
            aria-label="Página siguiente"
            title="Página siguiente"
            class="p-2 rounded-lg text-on-surface hover:bg-surface-container disabled:opacity-40 disabled:hover:bg-transparent transition-colors"
            @click="page++"
          >
            <span aria-hidden="true" class="material-symbols-outlined text-lg">chevron_right</span>
          </button>
          <button
            type="button"
            :disabled="page === totalPages"
            aria-label="Última página"
            title="Última página"
            class="p-2 rounded-lg text-on-surface hover:bg-surface-container disabled:opacity-40 disabled:hover:bg-transparent transition-colors"
            @click="page = totalPages"
          >
            <span aria-hidden="true" class="material-symbols-outlined text-lg">last_page</span>
          </button>
        </nav>
      </div>
    </div>

    <NuevoCirculoDialog
      v-if="creandoCirculo"
      @creado="(c) => { creandoCirculo = false; activeCircle = c.id; }"
      @cerrar="creandoCirculo = false"
    />
    <NuevoCirculoDialog
      v-if="editandoCirculo && circuloActivo"
      :circulo="circuloActivo"
      @guardado="editandoCirculo = false"
      @cerrar="editandoCirculo = false"
    />
    <ConfirmarBorradoDialog
      v-if="borrandoCirculo && circuloActivo"
      titulo="Eliminar círculo"
      :mensaje="`Se borrará el círculo «${circuloActivo.nombre}». ${
        contactosDelCirculoActivo
          ? `Sus ${contactosDelCirculoActivo} contactos no se borran: se quedan sin círculo.`
          : 'No tiene contactos.'
      }`"
      :borrar="() => borrarCirculo(circuloActivo!.id)"
      @borrado="circuloBorrado"
      @cerrar="borrandoCirculo = false"
    />
    <ImportarContactosDialog v-if="importando" @cerrar="importando = false" />
  </div>
</template>
