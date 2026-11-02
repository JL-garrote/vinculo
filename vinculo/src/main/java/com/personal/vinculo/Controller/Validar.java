package com.personal.vinculo.Controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Set;

/** Comprobaciones comunes de las peticiones; lanzan 400 con un mensaje claro */
final class Validar {

    private Validar() {
    }

    static void obligatorio(Object valor, String campo) {
        if (valor == null || (valor instanceof String texto && texto.isBlank())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Falta el campo " + campo);
        }
    }

    static void enumerado(String valor, Set<String> permitidos, String campo) {
        if (valor != null && !permitidos.contains(valor)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Valor no válido para " + campo + ": " + valor + ". Permitidos: " + permitidos);
        }
    }
}
