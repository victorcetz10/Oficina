package com.softcredito.app.modelo;

import com.softcredito.app.utilidades.UTiempo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * POJO de los clientes
 */
public class EstadoCivil {

    public String id;
    public String estado_civil;
    public String version;
    public int modificado;

    public EstadoCivil(String id, String estado_civil, String version, int modificado) {
        this.id = id;
        this.estado_civil = estado_civil;
        this.version = version;
        this.modificado = modificado;
    }

    public void aplicarSanidad() {
        id = id == null ? "" : id;
        estado_civil = estado_civil == null ? "" : estado_civil;
        version = version == null ? UTiempo.obtenerTiempo() : version;
        modificado = 0;
    }

    public int esMasReciente(EstadoCivil match) {
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

    public boolean compararCon(EstadoCivil otro) {
        return id.equals(otro.id) &&
                estado_civil.equals(otro.estado_civil);
    }
}
