package com.personal.vinculo.Models;

public class Contactos {

    private long id;
    private String tratamiento;
    private String nombre;
    private String apellidos;
    private String fotoURI;
    private String empresa;
    private String cargo;
    private String ciudad;
    private String pais;
    private String email;
    private String telefono;
    private String linkedinURI;
    private String canalPreferido;
    private long circuloID;
    private String rolRelacion;
    private int cadenciaDias;
    private int prioritario;
    private String intereses;
    private String date;
    private String familia;
    private String aficiones;
    private String hitoProfesional;
    private String comoConocidos;
    private long presentadoPorID;
    private String proximoPaso;
    private String proximoPasoFecha;
    private String origen;
    private String idExterno;
    private int archivado;
    private String creadoEn;
    private String actualizadoEn;

    public Contactos() {
    }

    public Contactos(long id, String tratamiento, String nombre, String apellidos, String fotoURI, String empresa, String cargo, String ciudad, String pais, String email, String telefono, String linkedinURI, String canalPreferido, long circuloID, String rolRelacion, int cadenciaDias, int prioritario, String intereses, String date, String familia, String aficiones, String hitoProfesional, String comoConocidos, long presentadoPorID, String proximoPaso, String proximoPasoFecha, String origen, String idExterno, int archivado, String creadoEn, String actualizadoEn) {
        this.id = id;
        this.tratamiento = tratamiento;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.fotoURI = fotoURI;
        this.empresa = empresa;
        this.cargo = cargo;
        this.ciudad = ciudad;
        this.pais = pais;
        this.email = email;
        this.telefono = telefono;
        this.linkedinURI = linkedinURI;
        this.canalPreferido = canalPreferido;
        this.circuloID = circuloID;
        this.rolRelacion = rolRelacion;
        this.cadenciaDias = cadenciaDias;
        this.prioritario = prioritario;
        this.intereses = intereses;
        this.date = date;
        this.familia = familia;
        this.aficiones = aficiones;
        this.hitoProfesional = hitoProfesional;
        this.comoConocidos = comoConocidos;
        this.presentadoPorID = presentadoPorID;
        this.proximoPaso = proximoPaso;
        this.proximoPasoFecha = proximoPasoFecha;
        this.origen = origen;
        this.idExterno = idExterno;
        this.archivado = archivado;
        this.creadoEn = creadoEn;
        this.actualizadoEn = actualizadoEn;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTratamiento() {
        return tratamiento;
    }

    public void setTratamiento(String tratamiento) {
        this.tratamiento = tratamiento;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getFotoURI() {
        return fotoURI;
    }

    public void setFotoURI(String fotoURI) {
        this.fotoURI = fotoURI;
    }

    public String getEmpresa() {
        return empresa;
    }

    public void setEmpresa(String empresa) {
        this.empresa = empresa;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getLinkedinURI() {
        return linkedinURI;
    }

    public void setLinkedinURI(String linkedinURI) {
        this.linkedinURI = linkedinURI;
    }

    public String getCanalPreferido() {
        return canalPreferido;
    }

    public void setCanalPreferido(String canalPreferido) {
        this.canalPreferido = canalPreferido;
    }

    public long getCirculoID() {
        return circuloID;
    }

    public void setCirculoID(long circuloID) {
        this.circuloID = circuloID;
    }

    public String getRolRelacion() {
        return rolRelacion;
    }

    public void setRolRelacion(String rolRelacion) {
        this.rolRelacion = rolRelacion;
    }

    public int getCadenciaDias() {
        return cadenciaDias;
    }

    public void setCadenciaDias(int cadenciaDias) {
        this.cadenciaDias = cadenciaDias;
    }

    public int getPrioritario() {
        return prioritario;
    }

    public void setPrioritario(int prioritario) {
        this.prioritario = prioritario;
    }

    public String getIntereses() {
        return intereses;
    }

    public void setIntereses(String intereses) {
        this.intereses = intereses;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getFamilia() {
        return familia;
    }

    public void setFamilia(String familia) {
        this.familia = familia;
    }

    public String getAficiones() {
        return aficiones;
    }

    public void setAficiones(String aficiones) {
        this.aficiones = aficiones;
    }

    public String getHitoProfesional() {
        return hitoProfesional;
    }

    public void setHitoProfesional(String hitoProfesional) {
        this.hitoProfesional = hitoProfesional;
    }

    public String getComoConocidos() {
        return comoConocidos;
    }

    public void setComoConocidos(String comoConocidos) {
        this.comoConocidos = comoConocidos;
    }

    public long getPresentadoPorID() {
        return presentadoPorID;
    }

    public void setPresentadoPorID(long presentadoPorID) {
        this.presentadoPorID = presentadoPorID;
    }

    public String getProximoPaso() {
        return proximoPaso;
    }

    public void setProximoPaso(String proximoPaso) {
        this.proximoPaso = proximoPaso;
    }

    public String getProximoPasoFecha() {
        return proximoPasoFecha;
    }

    public void setProximoPasoFecha(String proximoPasoFecha) {
        this.proximoPasoFecha = proximoPasoFecha;
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

    public int getArchivado() {
        return archivado;
    }

    public void setArchivado(int archivado) {
        this.archivado = archivado;
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
