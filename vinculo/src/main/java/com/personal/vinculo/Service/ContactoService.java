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

    private Contactos contact;
    private JdbcTemplate jdbc;

    public ContactoService() {
        contact = new Contactos();
        jdbc = new JdbcTemplate();
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
        contact.setId(rs.getLong("id"));
        contact.setTratamiento(rs.getString("tratamiento"));
        contact.setNombre(rs.getString("nombre"));
        contact.setApellidos(rs.getString("apellidos"));
        contact.setFotoURI(rs.getString("fotoURI"));
        contact.setEmpresa(rs.getString("empresa"));
        contact.setCargo(rs.getString("cargo"));
        contact.setCiudad(rs.getString("ciudad"));
        contact.setPais(rs.getString("pais"));
        contact.setEmail(rs.getString("email"));
        contact.setTelefono(rs.getString("telefono"));
        contact.setLinkedinURI(rs.getString("linkedinURI"));
        contact.setCanalPreferido(rs.getString("canalPreferido"));
        contact.setCirculoID(rs.getLong("circuloID"));
        contact.setRolRelacion(rs.getString("rolRelacion"));
        contact.setCadenciaDias(rs.getInt("cadenciaDias"));
        contact.setPrioritario(rs.getInt("prioritario"));
        contact.setIntereses(rs.getString("intereses"));
        contact.setDate(rs.getString("date"));
        contact.setFamilia(rs.getString("familia"));
        contact.setAficiones(rs.getString("aficiones"));
        contact.setHitoProfesional(rs.getString("hitoProfesional"));
        contact.setComoConocidos(rs.getString("comoConocidos"));
        contact.setPresentadoPorID(rs.getLong("presentadoPorID"));
        contact.setProximoPaso(rs.getString("proximoPaso"));
        contact.setProximoPasoFecha(rs.getString("proximoPasoFecha"));
        contact.setOrigen(rs.getString("origen"));
        contact.setIdExterno(rs.getString("idExterno"));
        contact.setArchivado(rs.getInt("archivado"));
        contact.setCreadoEn(rs.getString("creadoEn"));
        contact.setActualizadoEn(rs.getString("actualizadoEn"));


        Timestamp created = rs.getTimestamp("created_at");
        contact.setCreadoEn(created != null ? created.toString() : null);

        Timestamp updated = rs.getTimestamp("updated_at");
        contact.setActualizadoEn(updated != null ? updated.toString() : null);

        return contact;
    }
}