package com.softcredito.app.modelo;

import com.softcredito.app.utilidades.UTiempo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * POJO de los clientes
 */
public class Municipio {

    public String id;
    public String municipio;
    public int id_estado;
    public String version;
    public int modificado;

    public Municipio(String id, String municipio, int id_estado, String version, int modificado) {
        this.id = id;
        this.municipio = municipio;
        this.id_estado = id_estado;
        this.version = version;
        this.modificado = modificado;
    }

    public void aplicarSanidad() {
        id = id == null ? "" : id;
        municipio = municipio == null ? "" : municipio;
        version = version == null ? UTiempo.obtenerTiempo() : version;
        modificado = 0;
    }

    public int esMasReciente(Municipio match) {
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

    public boolean compararCon(Municipio otro) {
        return id.equals(otro.id) &&
                municipio.equals(otro.municipio);
    }
}
