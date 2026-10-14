package com.personal.vinculo.Models;

public class Circulos {

    private long id;
    private String nombre;
    private String color;
    private int defectoDias;
    private int orden;

    public Circulos() {
    }

    public Circulos(long id, String nombre, String color, int defectoDias, int orden) {
        this.id = id;
        this.nombre = nombre;
        this.color = color;
        this.defectoDias = defectoDias;
        this.orden = orden;
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

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getDefectoDias() {
        return defectoDias;
    }

    public void setDefectoDias(int defectoDias) {
        this.defectoDias = defectoDias;
    }

    public int getOrden() {
        return orden;
    }

    public void setOrden(int orden) {
        this.orden = orden;
    }
}
