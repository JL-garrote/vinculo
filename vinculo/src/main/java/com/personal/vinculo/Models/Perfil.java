package com.personal.vinculo.Models;

public class Perfil {

    private long id;
    private String nombre;
    private String email;
    private String avatarURI;
    private String zonaHoraria;
    private int objetivoInteraccionesSemana;
    private int umbralRelacionFuerte;
    private int cadenciaDefectoDias;
    private String preferencias;
    private String integraciones;
    private String creadoEn;
    private String actualizadoEn;

    public Perfil() {
    }

    public Perfil(long id, String nombre, String email, String avatarURI, String zonaHoraria, int objetivoInteraccionesSemana, int umbralRelacionFuerte, int cadenciaDefectoDias, String preferencias, String integraciones, String creadoEn, String actualizadoEn) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.avatarURI = avatarURI;
        this.zonaHoraria = zonaHoraria;
        this.objetivoInteraccionesSemana = objetivoInteraccionesSemana;
        this.umbralRelacionFuerte = umbralRelacionFuerte;
        this.cadenciaDefectoDias = cadenciaDefectoDias;
        this.preferencias = preferencias;
        this.integraciones = integraciones;
        this.creadoEn = creadoEn;
        this.actualizadoEn = actualizadoEn;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAvatarURI() {
        return avatarURI;
    }

    public void setAvatarURI(String avatarURI) {
        this.avatarURI = avatarURI;
    }

    public String getZonaHoraria() {
        return zonaHoraria;
    }

    public void setZonaHoraria(String zonaHoraria) {
        this.zonaHoraria = zonaHoraria;
    }

    public int getObjetivoInteraccionesSemana() {
        return objetivoInteraccionesSemana;
    }

    public void setObjetivoInteraccionesSemana(int objetivoInteraccionesSemana) {
        this.objetivoInteraccionesSemana = objetivoInteraccionesSemana;
    }

    public int getUmbralRelacionFuerte() {
        return umbralRelacionFuerte;
    }

    public void setUmbralRelacionFuerte(int umbralRelacionFuerte) {
        this.umbralRelacionFuerte = umbralRelacionFuerte;
    }

    public int getCadenciaDefectoDias() {
        return cadenciaDefectoDias;
    }

    public void setCadenciaDefectoDias(int cadenciaDefectoDias) {
        this.cadenciaDefectoDias = cadenciaDefectoDias;
    }

    public String getPreferencias() {
        return preferencias;
    }

    public void setPreferencias(String preferencias) {
        this.preferencias = preferencias;
    }

    public String getIntegraciones() {
        return integraciones;
    }

    public void setIntegraciones(String integraciones) {
        this.integraciones = integraciones;
    }

    public String getCreadoEn() {
        return creadoEn;
    }

    public void setCreadoEn(String creadoEn) {
        this.creadoEn = creadoEn;
    }

    public String getActualizadoEn() {
        return actualizadoEn;
    }

    public void setActualizadoEn(String actualizadoEn) {
        this.actualizadoEn = actualizadoEn;
    }
}
