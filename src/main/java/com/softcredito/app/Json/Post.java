package com.softcredito.app.Json;


import com.google.gson.annotations.SerializedName;

public class Post {


    public Post(Integer id, String institucion, String sucursal) {
        this.id = id;
        this.institucion = institucion;
        this.sucursal = sucursal;
    }

    public Post(){

    }
    public Integer getId() {

        return id;
    }

    public void setId(Integer id) {

        this.id = id;
    }

    public String getInstitucion() {
        return institucion;
    }

    public void setInstitucion(String institucion) {

        this.institucion = institucion;

    }

    public String getSucursal() {
        return sucursal;
    }

    public void setSucursal(String sucursal) {

        this.sucursal = sucursal;
    }

    private Integer id;
    private String institucion;
    private String sucursal;
}


