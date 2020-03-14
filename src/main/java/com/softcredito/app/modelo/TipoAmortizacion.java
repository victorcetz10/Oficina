package com.softcredito.app.modelo;

import com.softcredito.app.utilidades.UTiempo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * POJO de los clientes
 */
public class TipoAmortizacion {

    public String id;
    public String nombre;
    public String pago;
    public String plazo;
    public String version;
    public int modificado;

    public TipoAmortizacion(String id, String nombre, String pago, String plazo, String version, int modificado) {
        this.id = id;
        this.nombre = nombre;
        this.pago = pago;
        this.plazo = plazo;
        this.version = version;
        this.modificado = modificado;
    }

    public void aplicarSanidad() {
        id = id == null ? "" : id;
        nombre = nombre == null ? "" : nombre;
        version = version == null ? UTiempo.obtenerTiempo() : version;
        modificado = 0;
    }

    public int esMasReciente(TipoAmortizacion match) {
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

    public boolean compararCon(TipoAmortizacion otro) {
        return id.equals(otro.id);
    }
}
