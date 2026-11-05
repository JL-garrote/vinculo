<script setup lang="ts">
import { computed, reactive, ref } from 'vue';
import ModalBase from '../ModalBase.vue';
import { api, mensajeEdicion, mensajeError } from '@/api/vinculo';
import { reemplazar, store } from '@/api/store';
import { aInputFecha, canales, desdeInputFecha, nombreCompleto } from '@/api/format';
import type { CanalPreferido, Contacto, PeticionContacto } from '@/api/types';

/** Con un contacto, el diálogo sirve para editarlo; sin él, para crear uno nuevo */
const props = defineProps<{ contacto?: Contacto }>();
const emit = defineEmits<{ creado: [contacto: Contacto]; guardado: [contacto: Contacto]; cerrar: [] }>();

const original = props.contacto;
const form = reactive({
  tratamiento: original?.tratamiento ?? '',
  nombre: original?.nombre ?? '',
  apellidos: original?.apellidos ?? '',
  empresa: original?.empresa ?? '',
  cargo: original?.cargo ?? '',
  email: original?.email ?? '',
  telefono: original?.telefono ?? '',
  ciudad: original?.ciudad ?? '',
  pais: original?.pais ?? '',
  linkedinURI: original?.linkedinURI ?? '',
  canalPreferido: (original?.canalPreferido ?? '') as CanalPreferido | '',
  circuloID: original?.circuloID ?? 0,
  rolRelacion: original?.rolRelacion ?? '',
  cadenciaDias: (original?.cadenciaDias || null) as number | null,
  prioritario: !!original?.prioritario,
  intereses: original?.intereses ?? '',
  cumpleanos: original?.date?.slice(0, 10) ?? '',
  comoConocidos: original?.comoConocidos ?? '',
  presentadoPorID: original?.presentadoPorID ?? 0,
  proximoPaso: original?.proximoPaso ?? '',
  proximoPasoFecha: aInputFecha(original?.proximoPasoFecha ?? null),
});

const guardando = ref(false);
const error = ref<string | null>(null);
const masDatos = ref(false);

// Al editar, el propio contacto no puede habérselo presentado a sí mismo
const contactosOrdenados = computed(() =>
  store.contactos
    .filter((c) => c.id !== original?.id)
    .sort((a, b) => nombreCompleto(a, false).localeCompare(nombreCompleto(b, false), 'es')),
);

const texto = (v: string) => v.trim() || null;

async function guardar() {
  if (!form.nombre.trim() || guardando.value) return;
  guardando.value = true;
  error.value = null;
  try {
    const datos: PeticionContacto = {
      tratamiento: texto(form.tratamiento),
      nombre: form.nombre.trim(),
      apellidos: texto(form.apellidos),
      empresa: texto(form.empresa),
      cargo: texto(form.cargo),
      email: texto(form.email),
      telefono: texto(form.telefono),
      ciudad: texto(form.ciudad),
      pais: texto(form.pais),
      linkedinURI: texto(form.linkedinURI),
      canalPreferido: form.canalPreferido || null,
      circuloID: form.circuloID || null,
      rolRelacion: texto(form.rolRelacion),
      cadenciaDias: form.cadenciaDias || null,
      prioritario: form.prioritario,
      intereses: texto(form.intereses),
      cumpleanos: form.cumpleanos || null,
      comoConocidos: texto(form.comoConocidos),
      presentadoPorID: form.presentadoPorID || null,
      proximoPaso: texto(form.proximoPaso),
      proximoPasoFecha: desdeInputFecha(form.proximoPasoFecha),
    };
    if (original) {
      // Se envía el contacto completo en el formato del GET: los campos que este formulario no muestra
      // (familia, foto, origen…) van como estaban, para que el UPDATE no los vacíe
      const contacto = await api.actualizarContacto({
        ...original,
        tratamiento: datos.tratamiento ?? null,
        nombre: datos.nombre,
        apellidos: datos.apellidos ?? null,
        empresa: datos.empresa ?? null,
        cargo: datos.cargo ?? null,
        email: datos.email ?? null,
        telefono: datos.telefono ?? null,
        ciudad: datos.ciudad ?? null,
        pais: datos.pais ?? null,
        linkedinURI: datos.linkedinURI ?? null,
        canalPreferido: datos.canalPreferido ?? null,
        circuloID: datos.circuloID ?? 0,
        rolRelacion: datos.rolRelacion ?? null,
        cadenciaDias: datos.cadenciaDias ?? 0,
        prioritario: datos.prioritario ? 1 : 0,
        intereses: datos.intereses ?? null,
        date: datos.cumpleanos ?? null,
        comoConocidos: datos.comoConocidos ?? null,
        presentadoPorID: datos.presentadoPorID ?? 0,
        proximoPaso: datos.proximoPaso ?? null,
        proximoPasoFecha: datos.proximoPasoFecha ?? null,
      });
      reemplazar(store.contactos, contacto);
      emit('guardado', contacto);
    } else {
      const contacto = await api.crearContacto(datos);
      store.contactos.push(contacto);
      emit('creado', contacto);
    }
  } catch (e) {
    error.value = original ? mensajeEdicion(e) : mensajeError(e);
  } finally {
    guardando.value = false;
  }
}

const campo =
  'w-full bg-surface-container-low text-on-surface font-body-sm text-body-sm py-2.5 px-3 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary/20';
const etiqueta = 'font-label-sm text-label-sm uppercase tracking-wider text-on-surface-variant';
</script>

<template>
  <ModalBase :titulo="original ? 'Editar contacto' : 'Nuevo contacto'" ancho="max-w-2xl" @cerrar="emit('cerrar')">
    <form class="flex flex-col gap-4" @submit.prevent="guardar">
      <div class="grid grid-cols-1 sm:grid-cols-[110px_1fr_1fr] gap-3">
        <label class="flex flex-col gap-1.5">
          <span :class="etiqueta">Tratamiento</span>
          <input v-model="form.tratamiento" maxlength="20" placeholder="Dr., Sra…" :class="campo" />
        </label>
        <label class="flex flex-col gap-1.5">
          <span :class="etiqueta">Nombre *</span>
          <input v-model="form.nombre" maxlength="100" required autofocus :class="campo" />
        </label>
        <label class="flex flex-col gap-1.5">
          <span :class="etiqueta">Apellidos</span>
          <input v-model="form.apellidos" maxlength="150" :class="campo" />
        </label>
      </div>

      <div class="grid grid-cols-1 sm:grid-cols-2 gap-3">
        <label class="flex flex-col gap-1.5">
          <span :class="etiqueta">Empresa</span>
          <input v-model="form.empresa" maxlength="200" :class="campo" />
        </label>
        <label class="flex flex-col gap-1.5">
          <span :class="etiqueta">Cargo</span>
          <input v-model="form.cargo" maxlength="150" :class="campo" />
        </label>
        <label class="flex flex-col gap-1.5">
          <span :class="etiqueta">Email</span>
          <input v-model="form.email" type="email" maxlength="255" :class="campo" />
        </label>
        <label class="flex flex-col gap-1.5">
          <span :class="etiqueta">Teléfono</span>
          <input v-model="form.telefono" type="tel" maxlength="30" :class="campo" />
        </label>
        <label class="flex flex-col gap-1.5">
          <span :class="etiqueta">Círculo</span>
          <select v-model.number="form.circuloID" :class="campo">
            <option :value="0">Sin círculo</option>
            <option v-for="c in store.circulos" :key="c.id" :value="c.id">{{ c.nombre }}</option>
          </select>
        </label>
        <label class="flex flex-col gap-1.5">
          <span :class="etiqueta">Canal preferido</span>
          <select v-model="form.canalPreferido" :class="campo">
            <option value="">Sin preferencia</option>
            <option v-for="(c, key) in canales" :key="key" :value="key">{{ c.label }}</option>
          </select>
        </label>
        <label class="flex flex-col gap-1.5">
          <span :class="etiqueta">Cadencia (días)</span>
          <input v-model.number="form.cadenciaDias" type="number" min="1" max="3650" placeholder="La del círculo" :class="campo" />
        </label>
        <label class="flex items-center gap-2 self-end pb-2.5 cursor-pointer">
          <input v-model="form.prioritario" type="checkbox" class="w-4 h-4 accent-primary-container" />
          <span class="font-label-md text-label-md text-on-surface">Contacto prioritario</span>
        </label>
      </div>

      <div class="grid grid-cols-1 sm:grid-cols-2 gap-3">
        <label class="flex flex-col gap-1.5">
          <span :class="etiqueta">Próximo paso</span>
          <input v-model="form.proximoPaso" maxlength="255" placeholder="Ej. Invitar a un café" :class="campo" />
        </label>
        <label class="flex flex-col gap-1.5">
          <span :class="etiqueta">Fecha del próximo paso</span>
          <input v-model="form.proximoPasoFecha" type="datetime-local" :class="campo" />
        </label>
      </div>

      <button
        type="button"
        class="self-start flex items-center gap-1 font-label-md text-label-md text-primary hover:underline"
        @click="masDatos = !masDatos"
      >
        <span aria-hidden="true" class="material-symbols-outlined text-base">{{ masDatos ? 'expand_less' : 'expand_more' }}</span>
        {{ masDatos ? 'Menos datos' : 'Más datos (ubicación, intereses, cumpleaños…)' }}
      </button>

      <div v-if="masDatos" class="grid grid-cols-1 sm:grid-cols-2 gap-3">
        <label class="flex flex-col gap-1.5">
          <span :class="etiqueta">Ciudad</span>
          <input v-model="form.ciudad" maxlength="100" :class="campo" />
        </label>
        <label class="flex flex-col gap-1.5">
          <span :class="etiqueta">País</span>
          <input v-model="form.pais" maxlength="100" :class="campo" />
        </label>
        <label class="flex flex-col gap-1.5">
          <span :class="etiqueta">Rol en tu red</span>
          <input v-model="form.rolRelacion" maxlength="50" placeholder="Mentor, cliente…" :class="campo" />
        </label>
        <label class="flex flex-col gap-1.5">
          <span :class="etiqueta">Cumpleaños</span>
          <input v-model="form.cumpleanos" type="date" :class="campo" />
        </label>
        <label class="flex flex-col gap-1.5 sm:col-span-2">
          <span :class="etiqueta">Intereses (separados por comas)</span>
          <input v-model="form.intereses" maxlength="500" placeholder="Pádel, IA, vino" :class="campo" />
        </label>
        <label class="flex flex-col gap-1.5">
          <span :class="etiqueta">LinkedIn</span>
          <input v-model="form.linkedinURI" type="url" maxlength="500" :class="campo" />
        </label>
        <label class="flex flex-col gap-1.5">
          <span :class="etiqueta">Te lo presentó</span>
          <select v-model.number="form.presentadoPorID" :class="campo">
            <option :value="0">Nadie</option>
            <option v-for="c in contactosOrdenados" :key="c.id" :value="c.id">{{ nombreCompleto(c) }}</option>
          </select>
        </label>
        <label class="flex flex-col gap-1.5 sm:col-span-2">
          <span :class="etiqueta">Cómo os conocisteis</span>
          <input v-model="form.comoConocidos" :class="campo" />
        </label>
      </div>

      <p v-if="error" class="p-3 rounded-lg bg-error-container text-on-error-container font-body-sm text-body-sm">{{ error }}</p>

      <div class="flex items-center justify-end gap-2 pt-1">
        <button
          type="button"
          class="px-4 py-2 rounded-lg bg-surface-container text-on-surface-variant hover:text-on-surface font-label-md text-label-md"
          @click="emit('cerrar')"
        >
          Cancelar
        </button>
        <button
          type="submit"
          class="px-4 py-2 rounded-lg bg-primary-container hover:bg-primary dark:hover:bg-primary-fixed-dim text-on-primary font-label-md text-label-md shadow-sm disabled:opacity-50"
          :disabled="!form.nombre.trim() || guardando"
        >
          {{ guardando ? 'Guardando…' : original ? 'Guardar cambios' : 'Crear contacto' }}
        </button>
      </div>
    </form>
  </ModalBase>
</template>
