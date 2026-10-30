package com.personal.vinculo.Service;

import com.personal.vinculo.Models.Circulos;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class CirculoService {

    private JdbcTemplate jdbcTemplate;
    private InsertHelper insert;

    public CirculoService(JdbcTemplate jdbcTemplate, InsertHelper insert) {
        this.jdbcTemplate = jdbcTemplate;
        this.insert = insert;
    }

    /** Inserta el círculo (al final del orden si no se indica) y lo devuelve */
    public Circulos crear(Circulos circulo) {
        int orden = circulo.getOrden() > 0
                ? circulo.getOrden()
                : jdbcTemplate.queryForObject("SELECT COALESCE(MAX(orden) + 1, 1) FROM circulos", Integer.class);

        Map<String, Object> valores = new HashMap<>();
        valores.put("nombre", circulo.getNombre());
        valores.put("color", circulo.getColor());
        valores.put("cadencia_defecto_dias", InsertHelper.positivoONull(circulo.getDefectoDias()));
        valores.put("orden", orden);

        long id = insert.insertar("circulos", valores);
        return buscarPorID((int) id).orElseThrow();
    }

    public List<Circulos> sacarTodos() {
        String sql = "SELECT * FROM circulos";
        List<Circulos> lista = jdbcTemplate.query(sql,this::rowCirculos);
        return lista;
    }

    public Optional<Circulos> buscarPorID(int id) {
        String sql = "SELECT * FROM circulos WHERE id = ?";
        List<Circulos> lista = jdbcTemplate.query(sql,this::rowCirculos,id);
        return lista.stream().findFirst();
    }

    private Circulos rowCirculos(ResultSet rs, int rowNum) throws SQLException {
        Circulos cr = new Circulos();
        cr.setId(rs.getLong("id"));
        cr.setNombre(rs.getString("nombre"));
        cr.setColor(rs.getString("color"));
        cr.setDefectoDias(rs.getInt("cadencia_defecto_dias"));
        cr.setOrden(rs.getInt("orden"));

        return cr;
    }
}
