package com.softcredito.app.modelo;

import com.softcredito.app.utilidades.UTiempo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * POJO de los clientes
 */
public class CanalCobranza {

    public String id;
    public String clave;
    public String nombre;
    public String referencia;
    public String version;
    public int modificado;

    public CanalCobranza(String id, String clave, String nombre, String referencia, String version, int modificado) {
        this.id = id;
        this.clave = clave;
        this.nombre = nombre;
        this.referencia = referencia;
        this.version = version;
        this.modificado = modificado;
    }

    public void aplicarSanidad() {
        id = id == null ? "" : id;
        clave = clave == null ? "" : clave;
        nombre = nombre == null ? "" : nombre;
        referencia = referencia == null ? "" : referencia;
        version = version == null ? UTiempo.obtenerTiempo() : version;
        modificado = 0;
    }

    public int esMasReciente(CanalCobranza match) {
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

    public boolean compararCon(CanalCobranza otro) {
        return id.equals(otro.id);
    }
}
