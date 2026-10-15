package com.personal.vinculo.Service;

import com.personal.vinculo.Models.Circulos;
import com.personal.vinculo.Models.Perfil;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

@Service
public class PerfilService {

    private Perfil usuario;
    private JdbcTemplate jdbc;

    public PerfilService(Perfil usuario, JdbcTemplate jdbc) {
        this.usuario = usuario;
        this.jdbc = jdbc;
    }

    public List<Perfil> sacarUsuario() {
        String sql = "SELECT * FROM perfil";
        List<Perfil> lista = jdbc.query(sql,this::rowPerfil);
        return lista;
    }

    private Perfil rowPerfil(ResultSet rs, int rowNum) throws SQLException {
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

        Timestamp created = rs.getTimestamp("created_at");
        usuario.setCreadoEn(created != null ? created.toString() : null);

        Timestamp updated = rs.getTimestamp("updated_at");
        usuario.setActualizadoEn(updated != null ? updated.toString() : null);

        return usuario;
    }
}
