package com.personal.vinculo.Service;

import com.personal.vinculo.Models.Contactos;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ContactoService {

    private JdbcTemplate jdbc;
    private InsertHelper insert;

    public ContactoService(JdbcTemplate jdbc, InsertHelper insert) {
        this.jdbc = jdbc;
        this.insert = insert;
    }

    /** Inserta el contacto y lo devuelve tal como queda en la base de datos */
    public Contactos crear(Contactos c) {
        Map<String, Object> valores = new HashMap<>();
        valores.put("tratamiento", c.getTratamiento());
        valores.put("nombre", c.getNombre());
        valores.put("apellidos", c.getApellidos());
        valores.put("foto_url", c.getFotoURI());
        valores.put("empresa", c.getEmpresa());
        valores.put("cargo", c.getCargo());
        valores.put("ciudad", c.getCiudad());
        valores.put("pais", c.getPais());
        valores.put("email", c.getEmail());
        valores.put("telefono", c.getTelefono());
        valores.put("linkedin_url", c.getLinkedinURI());
        valores.put("canal_preferido", c.getCanalPreferido());
        valores.put("circulo_id", InsertHelper.idONull(c.getCirculoID()));
        valores.put("rol_relacion", c.getRolRelacion());
        valores.put("cadencia_dias", InsertHelper.positivoONull(c.getCadenciaDias()));
        valores.put("prioritario", c.getPrioritario());
        valores.put("intereses", c.getIntereses());
        valores.put("cumpleanos", c.getDate());
        valores.put("familia", c.getFamilia());
        valores.put("aficiones", c.getAficiones());
        valores.put("hito_profesional", c.getHitoProfesional());
        valores.put("como_nos_conocimos", c.getComoConocidos());
        valores.put("presentado_por_id", InsertHelper.idONull(c.getPresentadoPorID()));
        valores.put("proximo_paso", c.getProximoPaso());
        valores.put("proximo_paso_fecha", c.getProximoPasoFecha());
        valores.put("origen", c.getOrigen());
        valores.put("id_externo", c.getIdExterno());

        long id = insert.insertar("contactos", valores);
        return buscarPorID(id).orElseThrow();
    }

    public List<Contactos> sacarTodos() {
        List<Contactos> contactosList = null;
        try {
            String sql = "SELECT * FROM contactos";
            contactosList= jdbc.query(sql, this::rowContact);
        }catch (Exception e) {
            System.out.println("[Error en el metodo sacar todos] " + e.getMessage());
        }
        return contactosList;
    }

    public int contadorDeContactos() {
        List<Contactos> lista = sacarTodos();
        return lista.size();
    }

    public Optional<Contactos> buscarPorID(long id) {
        List<Contactos> lista = null;
        try {
            String sql = "SELECT * FROM contactos WHERE id = ?";
            lista = jdbc.query(sql, this::rowContact, id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return lista.stream().findFirst();
    }

    public Optional<Contactos> buscarPorEmpresa(String empresa){
        List<Contactos> lista = null;
        try {
            String sql = "SELECT * FROM contactos WHERE empresa = ?";
            lista = jdbc.query(sql, this::rowContact, empresa);
        }catch (Exception e) {
            System.out.println("");
        }
        return lista.stream().findFirst();
    }

    private Contactos rowContact(ResultSet rs, int rowNum) throws SQLException {
        Contactos contact = new Contactos();
        contact.setId(rs.getLong("id"));
        contact.setTratamiento(rs.getString("tratamiento"));
        contact.setNombre(rs.getString("nombre"));
        contact.setApellidos(rs.getString("apellidos"));
        contact.setFotoURI(rs.getString("foto_url"));
        contact.setEmpresa(rs.getString("empresa"));
        contact.setCargo(rs.getString("cargo"));
        contact.setCiudad(rs.getString("ciudad"));
        contact.setPais(rs.getString("pais"));
        contact.setEmail(rs.getString("email"));
        contact.setTelefono(rs.getString("telefono"));
        contact.setLinkedinURI(rs.getString("linkedin_url"));
        contact.setCanalPreferido(rs.getString("canal_preferido"));
        contact.setCirculoID(rs.getLong("circulo_id"));
        contact.setRolRelacion(rs.getString("rol_relacion"));
        contact.setCadenciaDias(rs.getInt("cadencia_dias"));
        contact.setPrioritario(rs.getInt("prioritario"));
        contact.setIntereses(rs.getString("intereses"));
        contact.setDate(rs.getString("cumpleanos"));
        contact.setFamilia(rs.getString("familia"));
        contact.setAficiones(rs.getString("aficiones"));
        contact.setHitoProfesional(rs.getString("hito_profesional"));
        contact.setComoConocidos(rs.getString("como_nos_conocimos"));
        contact.setPresentadoPorID(rs.getLong("presentado_por_id"));
        contact.setProximoPaso(rs.getString("proximo_paso"));
        contact.setProximoPasoFecha(rs.getString("proximo_paso_fecha"));
        contact.setOrigen(rs.getString("origen"));
        contact.setIdExterno(rs.getString("id_externo"));
        contact.setArchivado(rs.getInt("archivado"));

        Timestamp created = rs.getTimestamp("creado_en");
        contact.setCreadoEn(created != null ? created.toString() : null);

        Timestamp updated = rs.getTimestamp("actualizado_en");
        contact.setActualizadoEn(updated != null ? updated.toString() : null);

        return contact;
    }
}