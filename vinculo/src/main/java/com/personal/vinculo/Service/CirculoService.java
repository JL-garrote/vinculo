package com.personal.vinculo.Service;

import com.personal.vinculo.Models.Circulos;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Service
public class CirculoService {

    private Circulos cr;
    private JdbcTemplate jdbcTemplate;

    public CirculoService() {
        cr = new Circulos();
        jdbcTemplate = new JdbcTemplate();
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
        cr.setId(rs.getLong("id"));
        cr.setNombre(rs.getString("nombre"));
        cr.setColor(rs.getString("color"));
        cr.setDefectoDias(rs.getInt("cadencia_defecto_dias"));
        cr.setOrden(rs.getInt("orden"));

        return cr;
    }
}
