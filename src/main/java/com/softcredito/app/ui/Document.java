package com.softcredito.app.ui;

public class Document {

    private  int id;
    private String nombre;

    public Document() {
    }


    public Document(int id, String nombre, String tipo, String ruta) {
        this.id = id;
        this.nombre = nombre;
        this.tipo = tipo;
        this.ruta = ruta;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getRuta() {
        return ruta;
    }

    public void setRuta(String ruta) {
        this.ruta = ruta;
    }

    private String tipo;
    private String ruta;
}
