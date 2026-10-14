package com.personal.vinculo.Models;

public class Pipelines {

    private long id;
    private String nombre;
    private String objetivo;
    private boolean activo;
    private String creadoEn;

    public Pipelines() {
    }

    public Pipelines(long id, String nombre, String objetivo, boolean activo, String creadoEn) {
        this.id = id;
        this.nombre = nombre;
        this.objetivo = objetivo;
        this.activo = activo;
        this.creadoEn = creadoEn;
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

    public String getObjetivo() {
        return objetivo;
    }

    public void setObjetivo(String objetivo) {
        this.objetivo = objetivo;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public String getCreadoEn() {
        return creadoEn;
    }

    public void setCreadoEn(String creadoEn) {
        this.creadoEn = creadoEn;
    }
}
