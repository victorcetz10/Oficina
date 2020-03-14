package com.softcredito.app.modelo;

import com.softcredito.app.utilidades.UTiempo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * POJO de los clientes
 */
public class InstrumentoMonetario {

    public String id;
    public String clave;
    public String descripcion;
    public String version;
    public int modificado;

    public InstrumentoMonetario(String id, String clave, String descripcion, String version, int modificado) {
        this.id = id;
        this.clave = clave;
        this.descripcion = descripcion;
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

    public int esMasReciente(InstrumentoMonetario match) {
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

    public boolean compararCon(InstrumentoMonetario otro) {
        return id.equals(otro.id);
    }
}
