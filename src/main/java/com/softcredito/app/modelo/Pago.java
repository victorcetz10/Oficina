package com.softcredito.app.modelo;

import com.softcredito.app.utilidades.UTiempo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * POJO de los pagos
 */
public class Pago {

    public String id;
    public int id_solicitud;//Solamente se liga a la solicitud, esto es porque solo se puede pagar grupal o individual, no se puede grupal-individual.
    public String fecha;
    public String monto;
    public int id_canal_cobranza;
    public int id_instrumento_monetario;
    public String version;
    public int modificado;

    public Pago(String id, int id_solicitud, String fecha, String monto, int id_canal_cobranza, int id_instrumento_monetario, String version, int modificado) {
        this.id = id;
        this.id_solicitud = id_solicitud;
        this.fecha = fecha;
        this.monto = monto;
        this.id_canal_cobranza = id_canal_cobranza;
        this.id_instrumento_monetario = id_instrumento_monetario;
        this.version = version;
        this.modificado = modificado;
    }

    public void aplicarSanidad() {
        id = id == null ? "" : id;
        fecha = fecha == null ? "" : fecha;
        monto = monto == null ? "" : monto;
        version = version == null ? UTiempo.obtenerTiempo() : version;
        modificado = 0;
    }

    public int esMasReciente(Pago match) {
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

    public boolean compararCon(Pago otro) {
        return id.equals(otro.id);
    }
}
