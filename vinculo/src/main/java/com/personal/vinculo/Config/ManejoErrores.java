package com.personal.vinculo.Config;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

/**
 * Convierte los errores de datos en respuestas con un mensaje legible,
 * en lugar de un 500 genérico.
 */
@RestControllerAdvice
public class ManejoErrores {

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> estado(ResponseStatusException e) {
        return respuesta(HttpStatus.valueOf(e.getStatusCode().value()), e.getReason());
    }

    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity<Map<String, Object>> duplicado(DuplicateKeyException e) {
        return respuesta(HttpStatus.CONFLICT, "Ya existe un registro con esos datos");
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> integridad(DataIntegrityViolationException e) {
        return respuesta(HttpStatus.BAD_REQUEST, "Datos no válidos o referencia a un registro que no existe");
    }

    private ResponseEntity<Map<String, Object>> respuesta(HttpStatus estado, String mensaje) {
        return ResponseEntity.status(estado).body(Map.of(
                "status", estado.value(),
                "error", estado.getReasonPhrase(),
                "mensaje", mensaje != null ? mensaje : estado.getReasonPhrase()));
    }
}
