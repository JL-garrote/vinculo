package com.personal.vinculo.Service;

import com.personal.vinculo.Models.Interacciones;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Service
public class InteraccionService {

    public static final Set<String> TIPOS = Set.of("cafe", "comida", "reunion", "llamada", "videollamada",
            "mensaje", "email", "evento", "introduccion", "otro");

    private JdbcTemplate jdbc;
    private InsertHelper insert;

    public InteraccionService(JdbcTemplate jdbc, InsertHelper insert) {
        this.jdbc = jdbc;
        this.insert = insert;
    }

    public List<Interacciones> sacarTodas() {
        String sql = "SELECT * FROM interacciones ORDER BY fecha DESC, id DESC";
        return jdbc.query(sql, this::rowInteraccion);
    }

    public List<Interacciones> deContacto(long contactoID) {
        String sql = "SELECT * FROM interacciones WHERE contacto_id = ? OR contacto_relacionado_id = ? "
                + "ORDER BY fecha DESC, id DESC";
        return jdbc.query(sql, this::rowInteraccion, contactoID, contactoID);
    }

    public Optional<Interacciones> buscarPorID(long id) {
        String sql = "SELECT * FROM interacciones WHERE id = ?";
        return jdbc.query(sql, this::rowInteraccion, id).stream().findFirst();
    }

    /**
     * Inserta la interacción. Si no es borrador, el trigger de la base de datos
     * marca como completados los recordatorios pendientes del contacto.
     */
    public Interacciones crear(Interacciones i) {
        Map<String, Object> valores = new HashMap<>();
        valores.put("contacto_id", i.getContactoID());
        valores.put("contacto_relacionado_id", InsertHelper.idONull(i.getContactoRelacionadoID()));
        valores.put("tipo", i.getTipo());
        valores.put("titulo", i.getTitulo());
        valores.put("fecha", i.getFecha());
        valores.put("lugar", i.getLugar());
        valores.put("resumen", i.getResumen());
        valores.put("temas", i.getTemas());
        valores.put("es_borrador", i.getBorrador());

        long id = insert.insertar("interacciones", valores);
        return buscarPorID(id).orElseThrow();
    }

    private Interacciones rowInteraccion(ResultSet rs, int rowNum) throws SQLException {
        Interacciones i = new Interacciones();
        i.setId(rs.getLong("id"));
        i.setContactoID(rs.getLong("contacto_id"));
        i.setContactoRelacionadoID(rs.getLong("contacto_relacionado_id"));
        i.setTipo(rs.getString("tipo"));
        i.setTitulo(rs.getString("titulo"));
        i.setFecha(rs.getString("fecha"));
        i.setLugar(rs.getString("lugar"));
        i.setResumen(rs.getString("resumen"));
        i.setTemas(rs.getString("temas"));
        i.setBorrador(rs.getInt("es_borrador"));
        i.setOrigen(rs.getString("origen"));
        i.setIdExterno(rs.getString("id_externo"));
        i.setCreadoEn(rs.getString("creado_en"));
        return i;
    }
}
