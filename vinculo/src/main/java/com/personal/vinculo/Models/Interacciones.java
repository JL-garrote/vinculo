package com.personal.vinculo.Models;

public class Interacciones {

    private long id;
    private long contactoID;
    private long contactoRelacionadoID;
    private String tipo;
    private String titulo;
    private String fecha;
    private String lugar;
    private String resumen;
    private String temas;
    private int borrador;
    private String origen;
    private String idExterno;
    private String creadoEn;

    public Interacciones() {
    }

    public Interacciones(long id, long contactoID, long contactoRelacionadoID, String tipo, String titulo, String fecha, String lugar, String resumen, String temas, int borrador, String origen, String idExterno, String creadoEn) {
        this.id = id;
        this.contactoID = contactoID;
        this.contactoRelacionadoID = contactoRelacionadoID;
        this.tipo = tipo;
        this.titulo = titulo;
        this.fecha = fecha;
        this.lugar = lugar;
        this.resumen = resumen;
        this.temas = temas;
        this.borrador = borrador;
        this.origen = origen;
        this.idExterno = idExterno;
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

    public long getContactoRelacionadoID() {
        return contactoRelacionadoID;
    }

    public void setContactoRelacionadoID(long contactoRelacionadoID) {
        this.contactoRelacionadoID = contactoRelacionadoID;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getLugar() {
        return lugar;
    }

    public void setLugar(String lugar) {
        this.lugar = lugar;
    }

    public String getResumen() {
        return resumen;
    }

    public void setResumen(String resumen) {
        this.resumen = resumen;
    }

    public String getTemas() {
        return temas;
    }

    public void setTemas(String temas) {
        this.temas = temas;
    }

    public int getBorrador() {
        return borrador;
    }

    public void setBorrador(int borrador) {
        this.borrador = borrador;
    }

    public String getOrigen() {
        return origen;
    }

    public void setOrigen(String origen) {
        this.origen = origen;
    }

    public String getIdExterno() {
        return idExterno;
    }

    public void setIdExterno(String idExterno) {
        this.idExterno = idExterno;
    }

    public String getCreadoEn() {
        return creadoEn;
    }

    public void setCreadoEn(String creadoEn) {
        this.creadoEn = creadoEn;
    }
}