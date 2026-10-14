package com.personal.vinculo.Models;

public class Notas {

    private long id;
    private long contactoID;
    private String tipo;
    private String titulo;
    private String contenido;
    private long fijada;
    private String creadoEn;

    public Notas() {
    }

    public Notas(long id, long contactoID, String tipo, String titulo, String contenido, long fijada, String creadoEn) {
        this.id = id;
        this.contactoID = contactoID;
        this.tipo = tipo;
        this.titulo = titulo;
        this.contenido = contenido;
        this.fijada = fijada;
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

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public long getFijada() {
        return fijada;
    }

    public void setFijada(long fijada) {
        this.fijada = fijada;
    }

    public String getCreadoEn() {
        return creadoEn;
    }

    public void setCreadoEn(String creadoEn) {
        this.creadoEn = creadoEn;
    }
}
