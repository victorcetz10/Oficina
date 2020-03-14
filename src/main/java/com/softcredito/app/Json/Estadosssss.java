package com.softcredito.app.Json;

public class Estadosssss {

    private String clave;
    private Integer id;
    private String estado;

    public Estadosssss(Integer id, String estado, String clave) {
        this.id = id;
        this.estado = estado;
        this.clave = clave;
    }

    public Estadosssss(){

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

}
