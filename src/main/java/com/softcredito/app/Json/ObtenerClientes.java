package com.softcredito.app.Json;

public class ObtenerClientes {

    private Integer id;
    private String rfc;
    private String ine;

    public ObtenerClientes(Integer id, String rfc, String ine, String curp) {
        this.id = id;
        this.rfc = rfc;
        this.ine = ine;
        this.curp = curp;
    }

    public ObtenerClientes(){

    }

    private String curp;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRfc() {
        return rfc;
    }

    public void setRfc(String rfc) {
        this.rfc = rfc;
    }

    public String getIne() {
        return ine;
    }

    public void setIne(String ine) {
        this.ine = ine;
    }

    public String getCurp() {
        return curp;
    }

    public void setCurp(String curp) {
        this.curp = curp;
    }


}
