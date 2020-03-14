package com.softcredito.app.Json;

public class Paisessss {

    public Paisessss(Integer id, String pais, String iso) {
        this.id = id;
        this.pais = pais;
        this.iso = iso;
    }

    public Paisessss(){

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
