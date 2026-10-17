package com.personal.vinculo.Service;

import com.personal.vinculo.Models.Notas;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Service
public class NotasService {

    private Notas note;
    private JdbcTemplate jdbc;

    public NotasService() {
        note = new Notas();
        jdbc = new JdbcTemplate();
    }

    public List<Notas> sacarTodos() {
        String sql = "SELECT * FROM notas";
        List<Notas> lista = jdbc.query(sql,this::rowNote);
        return lista;
    }

    public int contadorDeNotas(){
        List<Notas> lista = sacarTodos();
        return lista.size();
    }

    public Optional<Notas> buscarPorID(int id) {
        String sql = "SELECT * FROM notas WHERE id = ?";
        List<Notas> lista = jdbc.query(sql,this::rowNote,id);
        return lista.stream().findFirst();
    }

    private Notas rowNote(ResultSet rs, int rowNum) throws SQLException {
        note.setId(rs.getInt("id"));
        note.setContactoID(rs.getInt("contacto_id"));
        note.setTipo(rs.getString("tipo"));
        note.setTitulo(rs.getString("titulo"));
        note.setContenido(rs.getString("contenido"));
        note.setFijada(rs.getInt("fijada"));

        Timestamp created = rs.getTimestamp("created_at");
        note.setCreadoEn(created != null ? created.toString() : null);

        return note;
    }
}
