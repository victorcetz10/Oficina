package com.softcredito.app.modelo;

import com.softcredito.app.utilidades.UTiempo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * POJO de los clientes
 */
public class TipoPersona {

    public String id;
    public String clave;
    public String nombre;
    public String descripcion;
    public String version;
    public int modificado;

    public TipoPersona(String id, String clave, String nombre, String descripcion, String version, int modificado) {
        this.id = id;
        this.clave = clave;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.version = version;
        this.modificado = modificado;
    }

    public void aplicarSanidad() {
        id = id == null ? "" : id;
        clave = clave == null ? "" : clave;
        nombre = nombre == null ? "" : nombre;
        descripcion = descripcion == null ? "" : descripcion;
        version = version == null ? UTiempo.obtenerTiempo() : version;
        modificado = 0;
    }

    public int esMasReciente(TipoPersona match) {
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

    public boolean compararCon(TipoPersona otro) {
        return id.equals(otro.id) &&
                clave.equals(otro.clave) &&
                nombre.equals(otro.nombre);
    }
}
