package com.softcredito.app.modelo;

import com.softcredito.app.utilidades.UTiempo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * POJO de los archivos documentos entregados
 */
public class ArchivoDocumentoEntregado {

    public String id;
    public String id_documento_entregado;//String porque que puede ser generado por la App o por Softcredito
    public String fecha;
    public String nombre;
    public String tipo;
    public String ruta;
    public String descripcion;
    public String body;//Este campo no se guarda, es solo para intercambiar archivos
    public String version;
    public int modificado;

    public ArchivoDocumentoEntregado(String id, String id_documento_entregado, String fecha, String nombre, String tipo, String ruta, String descripcion, String body, String version, int modificado) {
        this.id = id;
        this.id_documento_entregado = id_documento_entregado;
        this.fecha = fecha;
        this.nombre = nombre;
        this.tipo = tipo;
        this.ruta = ruta;
        this.descripcion = descripcion;
        this.body = body;
        this.version = version;
        this.modificado = modificado;
    }

    public void aplicarSanidad() {
        id = id == null ? "" : id;
        nombre = nombre == null ? "" : nombre;
        tipo = tipo == null ? "" : tipo;
        ruta = ruta == null ? "" : ruta;
        descripcion = descripcion == null ? "" : descripcion;
        body = body == null ? "" : body;
        version = version == null ? UTiempo.obtenerTiempo() : version;
        modificado = 0;
    }

    public int esMasReciente(ArchivoDocumentoEntregado match) {
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

    public boolean compararCon(ArchivoDocumentoEntregado otro) {
        return id.equals(otro.id);
    }
}
