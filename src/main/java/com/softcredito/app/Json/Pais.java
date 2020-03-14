package com.softcredito.app.Json;

public class Pais {

    public Pais(Integer id, String pais, String iso) {
        this.id = id;
        this.pais = pais;
        this.iso = iso;
    }

    public Pais(){

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public String getIso() {
        return iso;
    }

    public void setIso(String iso) {
        this.iso = iso;
    }
    private Integer id;
    private String pais;
    private String iso;
}
