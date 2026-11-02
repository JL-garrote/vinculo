package com.personal.vinculo.Service;

import com.personal.vinculo.Models.Pipelines;
import com.personal.vinculo.Models.PipelinesContactos;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class PipelineService {

    public static final Set<String> ETAPAS = Set.of("identificado", "primer_contacto", "reunion", "seguimiento", "aliado");
    public static final Set<String> PRIORIDADES = Set.of("alta", "media", "baja");

    private JdbcTemplate jdbc;

    public PipelineService(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    // ---------- Pipelines ----------

    public List<Pipelines> sacarTodos() {
        String sql = "SELECT * FROM pipelines ORDER BY id";
        return jdbc.query(sql, this::rowPipeline);
    }

    public Optional<Pipelines> buscarPorID(long id) {
        String sql = "SELECT * FROM pipelines WHERE id = ?";
        List<Pipelines> lista = jdbc.query(sql, this::rowPipeline, id);
        return lista.stream().findFirst();
    }

    // ---------- Contactos dentro de un pipeline ----------

    public List<PipelinesContactos> contactosDelPipeline(long pipelineID) {
        String sql = "SELECT * FROM pipeline_contactos WHERE pipeline_id = ? ORDER BY etapa, posicion, id";
        return jdbc.query(sql, this::rowPipelineContacto, pipelineID);
    }

    public Optional<PipelinesContactos> buscarEntradaPorID(long id) {
        String sql = "SELECT * FROM pipeline_contactos WHERE id = ?";
        List<PipelinesContactos> lista = jdbc.query(sql, this::rowPipelineContacto, id);
        return lista.stream().findFirst();
    }

    /** Añade un contacto al pipeline al final de su etapa y devuelve la fila creada */
    public PipelinesContactos anadirContacto(long pipelineID, PipelinesContactos entrada) {
        String etapa = entrada.getEtapa() != null ? entrada.getEtapa() : "identificado";
        String prioridad = entrada.getPrioridad() != null ? entrada.getPrioridad() : "media";
        int posicion = siguientePosicion(pipelineID, etapa);

        String sql = "INSERT INTO pipeline_contactos "
                + "(pipeline_id, contacto_id, etapa, prioridad, siguiente_accion, siguiente_accion_fecha, posicion) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, pipelineID);
            ps.setLong(2, entrada.getContactoID());
            ps.setString(3, etapa);
            ps.setString(4, prioridad);
            ps.setString(5, vacioANull(entrada.getSiguienteAccion()));
            ps.setString(6, vacioANull(entrada.getSiguienteAccionFecha()));
            ps.setInt(7, posicion);
            return ps;
        }, keyHolder);

        long id = keyHolder.getKey().longValue();
        return buscarEntradaPorID(id).orElseThrow();
    }

    /**
     * Actualiza etapa, prioridad, siguiente acción y posición.
     * Si cambia de etapa, se reinicia la fecha "en etapa desde" y pasa al final de la nueva columna.
     */
    public Optional<PipelinesContactos> actualizarEntrada(long id, PipelinesContactos cambios) {
        Optional<PipelinesContactos> actual = buscarEntradaPorID(id);
        if (actual.isEmpty()) {
            return Optional.empty();
        }
        PipelinesContactos e = actual.get();

        String etapa = cambios.getEtapa() != null ? cambios.getEtapa() : e.getEtapa();
        String prioridad = cambios.getPrioridad() != null ? cambios.getPrioridad() : e.getPrioridad();
        boolean cambiaDeEtapa = !etapa.equals(e.getEtapa());
        int posicion = cambiaDeEtapa ? siguientePosicion(e.getPipelineID(), etapa) : e.getPosicion();

        String sql = "UPDATE pipeline_contactos SET etapa = ?, prioridad = ?, siguiente_accion = ?, "
                + "siguiente_accion_fecha = ?, posicion = ?"
                + (cambiaDeEtapa ? ", en_etapa_desde = CURRENT_TIMESTAMP" : "")
                + " WHERE id = ?";
        jdbc.update(sql, etapa, prioridad, vacioANull(cambios.getSiguienteAccion()),
                vacioANull(cambios.getSiguienteAccionFecha()), posicion, id);

        return buscarEntradaPorID(id);
    }

    public boolean quitarEntrada(long id) {
        String sql = "DELETE FROM pipeline_contactos WHERE id = ?";
        return jdbc.update(sql, id) > 0;
    }

    // ---------- Utilidades ----------

    private int siguientePosicion(long pipelineID, String etapa) {
        String sql = "SELECT COALESCE(MAX(posicion) + 1, 0) FROM pipeline_contactos WHERE pipeline_id = ? AND etapa = ?";
        Integer posicion = jdbc.queryForObject(sql, Integer.class, pipelineID, etapa);
        return posicion != null ? posicion : 0;
    }

    private String vacioANull(String texto) {
        return texto == null || texto.isBlank() ? null : texto;
    }

    private Pipelines rowPipeline(ResultSet rs, int rowNum) throws SQLException {
        Pipelines pipeline = new Pipelines();
        pipeline.setId(rs.getLong("id"));
        pipeline.setNombre(rs.getString("nombre"));
        pipeline.setObjetivo(rs.getString("objetivo"));
        pipeline.setActivo(rs.getBoolean("activo"));
        pipeline.setCreadoEn(rs.getString("creado_en"));
        return pipeline;
    }

    private PipelinesContactos rowPipelineContacto(ResultSet rs, int rowNum) throws SQLException {
        PipelinesContactos entrada = new PipelinesContactos();
        entrada.setId(rs.getLong("id"));
        entrada.setPipelineID(rs.getLong("pipeline_id"));
        entrada.setContactoID(rs.getLong("contacto_id"));
        entrada.setEtapa(rs.getString("etapa"));
        entrada.setPrioridad(rs.getString("prioridad"));
        entrada.setSiguienteAccion(rs.getString("siguiente_accion"));
        entrada.setSiguienteAccionFecha(rs.getString("siguiente_accion_fecha"));
        entrada.setPosicion(rs.getInt("posicion"));
        entrada.setEtapaDesdeFecha(rs.getString("en_etapa_desde"));
        entrada.setCreadoEn(rs.getString("creado_en"));
        return entrada;
    }
}
