package com.personal.vinculo.Service;

import com.personal.vinculo.Models.Recordatorios;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class RecordatorioService {

    private JdbcTemplate jdbc;
    private InsertHelper insert;

    public RecordatorioService(JdbcTemplate jdbc, InsertHelper insert) {
        this.jdbc = jdbc;
        this.insert = insert;
    }

    public List<Recordatorios> pendientes() {
        String sql = "SELECT * FROM recordatorios WHERE estado = 'pendiente' ORDER BY vence_en, id";
        return jdbc.query(sql, this::rowRecordatorio);
    }

    public List<Recordatorios> deContacto(long contactoID) {
        String sql = "SELECT * FROM recordatorios WHERE contacto_id = ? ORDER BY estado = 'pendiente' DESC, vence_en, id";
        return jdbc.query(sql, this::rowRecordatorio, contactoID);
    }

    public Optional<Recordatorios> buscarPorID(long id) {
        String sql = "SELECT * FROM recordatorios WHERE id = ?";
        return jdbc.query(sql, this::rowRecordatorio, id).stream().findFirst();
    }

    public Recordatorios crear(Recordatorios r) {
        Map<String, Object> valores = new HashMap<>();
        valores.put("contacto_id", InsertHelper.idONull(r.getContactoID()));
        valores.put("titulo", r.getTitulo());
        valores.put("descripcion", r.getDescripcion());
        valores.put("vence_en", r.getVenceEnFecha());
        valores.put("recurrencia_dias", InsertHelper.positivoONull(r.getRecurrenciaDias()));

        long id = insert.insertar("recordatorios", valores);
        return buscarPorID(id).orElseThrow();
    }

    private Recordatorios rowRecordatorio(ResultSet rs, int rowNum) throws SQLException {
        Recordatorios r = new Recordatorios();
        r.setId(rs.getLong("id"));
        r.setContactoID(rs.getLong("contacto_id"));
        r.setTitulo(rs.getString("titulo"));
        r.setDescripcion(rs.getString("descripcion"));
        r.setVenceEnFecha(rs.getString("vence_en"));
        r.setRecurrenciaDias(rs.getInt("recurrencia_dias"));
        r.setOrigen(rs.getString("origen"));
        r.setEstado(rs.getString("estado"));
        r.setCompletadoEnFecha(rs.getString("completado_en"));
        r.setCreadoEn(rs.getString("creado_en"));
        return r;
    }
}
