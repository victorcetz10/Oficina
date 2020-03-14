package com.softcredito.app.Json;

public class ActividadEconomica {
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    private Integer id;
    private String descripcion;
    private String clave;

    public ActividadEconomica(Integer id, String clave, String descripcion) {
        this.id = id;
        this.clave = clave;
        this.descripcion = descripcion;
    }

    public ActividadEconomica(){

    }





}
