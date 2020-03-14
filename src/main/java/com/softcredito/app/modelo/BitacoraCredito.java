package com.softcredito.app.modelo;

import com.softcredito.app.utilidades.UTiempo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * POJO de los bitacoras_credito
 */
public class BitacoraCredito {

    public String id;
    public String id_solicitud;
    public String asunto;
    public String fecha;
    public String hora;
    public String numero_amortizacion;
    public String detalles_pago;
    public String descripcion;
    public String valor_garantia;
    public String descripcion_garantia;
    public String version;
    public int modificado;

    public BitacoraCredito(String id, String id_solicitud, String asunto, String fecha, String hora,
        String numero_amortizacion, String detalles_pago, String descripcion, String valor_garantia, String descripcion_garantia,
        String version, int modificado) {

        this.id = id;
        this.id_solicitud = id_solicitud;
        this.asunto = asunto;
        this.fecha = fecha;
        this.hora = hora;
        this.numero_amortizacion = numero_amortizacion;
        this.detalles_pago = detalles_pago;
        this.descripcion = descripcion;
        this.valor_garantia = valor_garantia;
        this.descripcion_garantia = descripcion_garantia;
        this.version = version;
        this.modificado = modificado;
    }

    public void aplicarSanidad() {
        id = id == null ? "" : id;
        version = version == null ? UTiempo.obtenerTiempo() : version;
        modificado = 0;
    }

    public int esMasReciente(BitacoraCredito match) {
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

    public boolean compararCon(BitacoraCredito otro) {
        return id.equals(otro.id);
    }
}
