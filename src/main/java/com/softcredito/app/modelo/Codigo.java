package com.softcredito.app.modelo;

import com.softcredito.app.utilidades.UTiempo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * POJO de los clientes
 */
public class Codigo	 {

    public String id;
    public int id_estado;
    public int id_municipio;
    public String cp;
    public String asentamiento;
    public String tipo;
    public String version;
    public int modificado;

    public Codigo(String id, int id_estado, int id_municipio, String cp, String asentamiento, String tipo, String version, int modificado) {
        this.id = id;
        this.id_estado = id_estado;
        this.id_municipio = id_municipio;
        this.cp = cp;
        this.asentamiento = asentamiento;
        this.tipo = tipo;
        this.version = version;
        this.modificado = modificado;
    }

    public void aplicarSanidad() {
        id = id == null ? "" : id;
        cp = cp == null ? "" : cp;
        asentamiento = asentamiento == null ? "" : asentamiento;
        tipo = tipo == null ? "" : tipo;
        version = version == null ? UTiempo.obtenerTiempo() : version;
        modificado = 0;
    }

    public int esMasReciente(Codigo match) {
        SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        try {
            Date fechaA = formato.parse(version);
            Date fechaB = formato.parse(match.version);

            return fechaA.compareTo(fechaB);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public boolean compararCon(Codigo otro) {
        return id.equals(otro.id) &&
                asentamiento.equals(otro.asentamiento);
    }
}
