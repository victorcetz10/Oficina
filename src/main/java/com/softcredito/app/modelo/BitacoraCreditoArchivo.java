package com.softcredito.app.modelo;

import com.softcredito.app.utilidades.UTiempo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * POJO de los bitacoras de credito activos
 */
public class BitacoraCreditoArchivo {

    public String id;
    public String id_bitacora_credito;//String porque que puede ser generado por la App o por Softcredito
    public String fecha;
    public String nombre;
    public String tipo;
    public String ruta;
    public String body;
    public String version;
    public int modificado;

    public BitacoraCreditoArchivo(String id, String id_bitacora_credito, String fecha, String nombre, String tipo, String ruta, String body, String version, int modificado) {
        this.id = id;
        this.id_bitacora_credito = id_bitacora_credito;
        this.fecha = fecha;
        this.nombre = nombre;
        this.tipo = tipo;
        this.ruta = ruta;
        this.body = body;
        this.version = version;
        this.modificado = modificado;
    }

    public void aplicarSanidad() {
        id = id == null ? "" : id;
        nombre = nombre == null ? "" : nombre;
        tipo = tipo == null ? "" : tipo;
        ruta = ruta == null ? "" : ruta;
        body = body == null ? "" : body;
        version = version == null ? UTiempo.obtenerTiempo() : version;
        modificado = 0;
    }

    public int esMasReciente(BitacoraCreditoArchivo match) {
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

    public boolean compararCon(BitacoraCreditoArchivo otro) {
        return id.equals(otro.id);
    }
}
