package com.personal.vinculo.Service;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Inserta una fila con solo las columnas que traen valor y devuelve el id generado.
 * Las columnas que se omiten toman el valor por defecto de la tabla (origen, prioritario, creado_en...).
 */
@Component
public class InsertHelper {

    private JdbcTemplate jdbc;

    public InsertHelper(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public long insertar(String tabla, Map<String, Object> valores) {
        Map<String, Object> conValor = new LinkedHashMap<>();
        valores.forEach((columna, valor) -> {
            if (valor != null && !(valor instanceof String texto && texto.isBlank())) {
                conValor.put(columna, valor);
            }
        });

        SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbc)
                .withTableName(tabla)
                .usingColumns(conValor.keySet().toArray(String[]::new))
                .usingGeneratedKeyColumns("id");
        return insert.executeAndReturnKey(conValor).longValue();
    }

    /** Los ids que el modelo guarda como long usan 0 para "sin valor" */
    public static Long idONull(long id) {
        return id > 0 ? id : null;
    }

    public static Integer positivoONull(int valor) {
        return valor > 0 ? valor : null;
    }
}
