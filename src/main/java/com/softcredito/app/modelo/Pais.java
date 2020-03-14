package com.softcredito.app.modelo;

import com.softcredito.app.utilidades.UTiempo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * POJO de los clientes
 */
public class Pais {

    public String id;
    public String pais;
    public String iso;
    public String riesgo;
    public int predeterminado;
    public String version;
    public int modificado;

    public Pais(String id, String pais, String iso, String riesgo, int predeterminado, String version, int modificado) {
        this.id = id;
        this.pais = pais;
        this.iso = iso;
        this.riesgo = riesgo;
        this.predeterminado = predeterminado;
        this.version = version;
        this.modificado = modificado;
    }

    public void aplicarSanidad() {
        id = id == null ? "" : id;
        pais = pais == null ? "" : pais;
        iso = iso == null ? "" : iso;
        riesgo = riesgo == null ? "" : riesgo;
        version = version == null ? UTiempo.obtenerTiempo() : version;
        modificado = 0;
    }

    public int esMasReciente(Pais match) {
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

    public boolean compararCon(Pais otro) {
        return id.equals(otro.id) &&
                iso.equals(otro.iso);
    }
}
