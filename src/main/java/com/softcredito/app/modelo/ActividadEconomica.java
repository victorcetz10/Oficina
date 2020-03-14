package com.softcredito.app.modelo;

import com.softcredito.app.utilidades.UTiempo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * POJO de los clientes
 */
public class ActividadEconomica {

    public String id;
    public int id_categoria;
    public String clave;
    public String descripcion;
    public int riesgo;
    public String version;
    public int modificado;

    public ActividadEconomica(String id, int id_categoria, String clave, String descripcion, int riesgo, String version, int modificado) {
        this.id = id;
        this.id_categoria = id_categoria;
        this.clave = clave;
        this.descripcion = descripcion;
        this.riesgo = riesgo;
        this.version = version;
        this.modificado = modificado;
    }

    public void aplicarSanidad() {
        id = id == null ? "" : id;
        clave = clave == null ? "" : clave;
        descripcion = descripcion == null ? "" : descripcion;
        version = version == null ? UTiempo.obtenerTiempo() : version;
        modificado = 0;
    }

    public int esMasReciente(ActividadEconomica match) {
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

    public boolean compararCon(ActividadEconomica otro) {
        return id.equals(otro.id) &&
                clave.equals(otro.clave);
    }
}
