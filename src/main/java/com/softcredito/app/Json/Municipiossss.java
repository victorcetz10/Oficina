package com.softcredito.app.Json;

public class Municipiossss {


    public Municipiossss(Integer id, String municipio, String id_estado) {
        this.id = id;
        this.municipio = municipio;
        this.id_estado = id_estado;
    }

    public Municipiossss(){

    }
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getMunicipio() {
        return municipio;
    }

    public void setMunicipio(String municipio) {
        this.municipio = municipio;
    }

    public String getId_estado() {
        return id_estado;
    }

    public void setId_estado(String id_estado) {
        this.id_estado = id_estado;
    }

    private Integer id;
    private String municipio;
    private String id_estado;

}
