package com.softcredito.app.modelo;

import com.softcredito.app.utilidades.UTiempo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * POJO de los cotizaciones
 */
public class Cotizador {

    public String id;
    public String id_cliente;
    public String fecha_cotizacion;
    public int validez;
    public String fecha_disposicion;
    public String fecha_inicio_amortizaciones;
    public String id_producto;
    public String monto_autorizado;
    public int plazo_autorizado;
    public int id_tasa_referencia;
    public String sobretasa;
    public String tasa_moratoria;
    public int id_tipo_pago;
    public int id_tipo_amortizacion;
    public String notas;
    public String version;
    public int modificado;

    public Cotizador(String id, String id_cliente, String fecha_cotizacion, int validez, String fecha_disposicion,
        String fecha_inicio_amortizaciones, String id_producto, String monto_autorizado, int plazo_autorizado, int id_tasa_referencia,
        String sobretasa, String tasa_moratoria, int id_tipo_pago, int id_tipo_amortizacion, String notas,
        String version, int modificado) {

        this.id = id;
        this.id_cliente = id_cliente;
        this.fecha_cotizacion = fecha_cotizacion;
        this.validez = validez;
        this.fecha_disposicion = fecha_disposicion;
        this.fecha_inicio_amortizaciones = fecha_inicio_amortizaciones;
        this.id_producto = id_producto;
        this.monto_autorizado = monto_autorizado;
        this.plazo_autorizado = plazo_autorizado;
        this.id_tasa_referencia = id_tasa_referencia;
        this.sobretasa = sobretasa;
        this.tasa_moratoria = tasa_moratoria;
        this.id_tipo_pago = id_tipo_pago;
        this.id_tipo_amortizacion = id_tipo_amortizacion;
        this.notas = notas;
        this.version = version;
        this.modificado = modificado;
    }

    public void aplicarSanidad() {
        id = id == null ? "" : id;
        version = version == null ? UTiempo.obtenerTiempo() : version;
        modificado = 0;
    }

    public int esMasReciente(Cotizador match) {
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

    public boolean compararCon(Cotizador otro) {
        return id.equals(otro.id);
    }
}
