package com.personal.vinculo.Service;

import com.personal.vinculo.Models.Perfil;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

@Service
public class PerfilService {

    private JdbcTemplate jdbc;

    public PerfilService(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<Perfil> sacarUsuario() {
        List<Perfil> lista = null;
        try {
            String sql = "SELECT * FROM perfil";
            lista = jdbc.query(sql, this::rowPerfil);
        } catch (DataAccessException e) {
            System.out.println(e.getMessage());
        }
        return lista;
    }

    private Perfil rowPerfil(ResultSet rs, int rowNum) throws SQLException {
        Perfil usuario = new Perfil();
        usuario.setId(rs.getLong("id"));
        usuario.setNombre(rs.getString("nombre"));
        usuario.setEmail(rs.getString("email"));
        usuario.setAvatarURI(rs.getString("avatar_url"));
        usuario.setZonaHoraria(rs.getString("zona_horaria"));
        usuario.setObjetivoInteraccionesSemana(rs.getInt("objetivo_interacciones_semana"));
        usuario.setUmbralRelacionFuerte(rs.getInt("umbral_relacion_fuerte"));
        usuario.setCadenciaDefectoDias(rs.getInt("cadencia_por_defecto_dias"));
        usuario.setPreferencias(rs.getString("preferencias"));
        usuario.setIntegraciones(rs.getString("integraciones"));

        Timestamp created = rs.getTimestamp("creado_en");
        usuario.setCreadoEn(created != null ? created.toString() : null);

        Timestamp updated = rs.getTimestamp("actualizado_en");
        usuario.setActualizadoEn(updated != null ? updated.toString() : null);

        return usuario;
    }
}
