package com.personal.vinculo.Models;

public class PipelinesContactos {

    private long id;
    private long pipelineID;
    private long contactoID;
    private String etapa;
    private String prioridad;
    private String siguienteAccion;
    private String siguienteAccionFecha;
    private int posicion;
    private String etapaDesdeFecha;
    private String creadoEn;

    public PipelinesContactos() {
    }

    public PipelinesContactos(long id, long pipelineID, long contactoID, String etapa, String prioridad, String siguienteAccion, String siguienteAccionFecha, int posicion, String etapaDesdeFecha, String creadoEn) {
        this.id = id;
        this.pipelineID = pipelineID;
        this.contactoID = contactoID;
        this.etapa = etapa;
        this.prioridad = prioridad;
        this.siguienteAccion = siguienteAccion;
        this.siguienteAccionFecha = siguienteAccionFecha;
        this.posicion = posicion;
        this.etapaDesdeFecha = etapaDesdeFecha;
        this.creadoEn = creadoEn;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getPipelineID() {
        return pipelineID;
    }

    public void setPipelineID(long pipelineID) {
        this.pipelineID = pipelineID;
    }

    public long getContactoID() {
        return contactoID;
    }

    public void setContactoID(long contactoID) {
        this.contactoID = contactoID;
    }

    public String getEtapa() {
        return etapa;
    }

    public void setEtapa(String etapa) {
        this.etapa = etapa;
    }

    public String getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(String prioridad) {
        this.prioridad = prioridad;
    }

    public String getSiguienteAccion() {
        return siguienteAccion;
    }

    public void setSiguienteAccion(String siguienteAccion) {
        this.siguienteAccion = siguienteAccion;
    }

    public String getSiguienteAccionFecha() {
        return siguienteAccionFecha;
    }

    public void setSiguienteAccionFecha(String siguienteAccionFecha) {
        this.siguienteAccionFecha = siguienteAccionFecha;
    }

    public int getPosicion() {
        return posicion;
    }

    public void setPosicion(int posicion) {
        this.posicion = posicion;
    }

    public String getEtapaDesdeFecha() {
        return etapaDesdeFecha;
    }

    public void setEtapaDesdeFecha(String etapaDesdeFecha) {
        this.etapaDesdeFecha = etapaDesdeFecha;
    }

    public String getCreadoEn() {
        return creadoEn;
    }

    public void setCreadoEn(String creadoEn) {
        this.creadoEn = creadoEn;
    }
}
