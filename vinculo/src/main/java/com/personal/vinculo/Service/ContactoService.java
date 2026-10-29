package com.personal.vinculo.Service;

import com.personal.vinculo.Models.Contactos;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ContactoService {

    private JdbcTemplate jdbc;

    public ContactoService(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
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