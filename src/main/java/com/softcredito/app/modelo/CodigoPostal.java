package com.softcredito.app.modelo;

import com.softcredito.app.utilidades.UTiempo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * POJO de los clientes
 */
public class CodigoPostal {

    public String id;
    public String codigo_postal;
    public int id_municipio;
    public String version;
    public int modificado;

    public CodigoPostal(String id, String codigo_postal, int id_municipio, String version, int modificado) {
        this.id = id;
        this.codigo_postal = codigo_postal;
        this.id_municipio = id_municipio;
        this.version = version;
        this.modificado = modificado;
    }

    public void aplicarSanidad() {
        id = id == null ? "" : id;
        codigo_postal = codigo_postal == null ? "" : codigo_postal;
        version = version == null ? UTiempo.obtenerTiempo() : version;
        modificado = 0;
    }

    public int esMasReciente(CodigoPostal match) {
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

    public boolean compararCon(CodigoPostal otro) {
        return id.equals(otro.id) &&
                codigo_postal.equals(otro.codigo_postal);
    }
}
