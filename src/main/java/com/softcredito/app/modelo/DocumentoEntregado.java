package com.softcredito.app.modelo;

import com.softcredito.app.utilidades.UTiempo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * POJO de los documentos entregados
 */
public class DocumentoEntregado {

    public String id;
    public String id_cliente;//String porque que puede ser generado por la App o por Softcredito
    public String id_documento_requerido;//String porque que puede ser generado por la App o por Softcredito
    public String nombre_documento;
    public String descripcion;
    public int status;
    public String tipo_documento;//String porque que puede ser generado por la App o por Softcredito
    public String version;
    public int modificado;

    public DocumentoEntregado(String id, String id_cliente, String id_documento_requerido, String nombre_documento, String descripcion, int status, String tipo_documento, String version, int modificado) {
        this.id = id;
        this.id_cliente = id_cliente;
        this.id_documento_requerido = id_documento_requerido;
        this.nombre_documento = nombre_documento;
        this.descripcion = descripcion;
        this.status = status;
        this.tipo_documento = tipo_documento;
        this.version = version;
        this.modificado = modificado;
    }

    public void aplicarSanidad() {
        id = id == null ? "" : id;
        nombre_documento = nombre_documento == null ? "" : nombre_documento;
        descripcion = descripcion == null ? "" : descripcion;
        version = version == null ? UTiempo.obtenerTiempo() : version;
        modificado = 0;
    }

    public int esMasReciente(DocumentoEntregado match) {
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

    public boolean compararCon(DocumentoEntregado otro) {
        return id.equals(otro.id);
    }
}
