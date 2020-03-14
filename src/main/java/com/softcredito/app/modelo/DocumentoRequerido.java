package com.softcredito.app.modelo;

import com.softcredito.app.utilidades.UTiempo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * POJO de los clientes
 */
public class DocumentoRequerido {

    public String id;
    public String nombre;
    public int id_tipo_documento;
    public String descripcion;
    public int opcional;
    public String version;
    public int modificado;

    public DocumentoRequerido(String id, String nombre, int id_tipo_documento, String descripcion, int opcional, String version, int modificado) {
        this.id = id;
        this.nombre = nombre;
        this.id_tipo_documento = id_tipo_documento;
        this.descripcion = descripcion;
        this.opcional = opcional;
        this.version = version;
        this.modificado = modificado;
    }

    public void aplicarSanidad() {
        id = id == null ? "" : id;
        nombre = nombre == null ? "" : nombre;
        descripcion = descripcion == null ? "" : descripcion;
        version = version == null ? UTiempo.obtenerTiempo() : version;
        modificado = 0;
    }

    public int esMasReciente(DocumentoRequerido match) {
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

    public boolean compararCon(DocumentoRequerido otro) {
        return id.equals(otro.id);
    }
}
