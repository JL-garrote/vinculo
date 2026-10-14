package com.personal.vinculo.Models;

public class Recordatorios {

    private long id;
    private long contactoID;
    private String titulo;
    private String descripcion;
    private String venceEnFecha;
    private int recurrenciaDias;
    private String origen;
    private String estado;
    private String completadoEnFecha;
    private String creadoEn;

    public Recordatorios() {
    }

    public Recordatorios(long id, long contactoID, String titulo, String descripcion, String venceEnFecha, int recurrenciaDias, String origen, String estado, String completadoEnFecha, String creadoEn) {
        this.id = id;
        this.contactoID = contactoID;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.venceEnFecha = venceEnFecha;
        this.recurrenciaDias = recurrenciaDias;
        this.origen = origen;
        this.estado = estado;
        this.completadoEnFecha = completadoEnFecha;
        this.creadoEn = creadoEn;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getContactoID() {
        return contactoID;
    }

    public void setContactoID(long contactoID) {
        this.contactoID = contactoID;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getVenceEnFecha() {
        return venceEnFecha;
    }

    public void setVenceEnFecha(String venceEnFecha) {
        this.venceEnFecha = venceEnFecha;
    }

    public int getRecurrenciaDias() {
        return recurrenciaDias;
    }

    public void setRecurrenciaDias(int recurrenciaDias) {
        this.recurrenciaDias = recurrenciaDias;
    }

    public String getOrigen() {
        return origen;
    }

    public void setOrigen(String origen) {
        this.origen = origen;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getCompletadoEnFecha() {
        return completadoEnFecha;
    }

    public void setCompletadoEnFecha(String completadoEnFecha) {
        this.completadoEnFecha = completadoEnFecha;
    }

    public String getCreadoEn() {
        return creadoEn;
    }

    public void setCreadoEn(String creadoEn) {
        this.creadoEn = creadoEn;
    }
}
