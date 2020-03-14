package com.softcredito.app.modelo;

import com.softcredito.app.utilidades.UTiempo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * POJO de los productos
 */
public class Producto {

    public String id;
    public String prefijo;
    public String clave;
    public String nombre;
    public int id_tipo_amortizacion;
    public String sobretasa;
    public String tasa_moratoria;
    public int plazo_maximo;
    public String monto_maximo;
    public int id_tipo_pago;
    public String version;
    public int modificado;

    public Producto(String id, String prefijo, String clave, String nombre, int id_tipo_amortizacion, String sobretasa, String tasa_moratoria, int plazo_maximo, String monto_maximo, int id_tipo_pago, String version, int modificado) {
        this.id = id;
        this.prefijo = prefijo;
        this.clave = clave;
        this.nombre = nombre;
        this.id_tipo_amortizacion = id_tipo_amortizacion;
        this.sobretasa = sobretasa;
        this.tasa_moratoria = tasa_moratoria;
        this.plazo_maximo = plazo_maximo;
        this.monto_maximo = monto_maximo;
        this.id_tipo_pago = id_tipo_pago;
        this.version = version;
        this.modificado = modificado;
    }

    public void aplicarSanidad() {
        id = id == null ? "" : id;
        prefijo = prefijo == null ? "" : prefijo;
        clave = clave == null ? "" : clave;
        nombre = nombre == null ? "" : nombre;
        version = version == null ? UTiempo.obtenerTiempo() : version;
        modificado = 0;
    }

    public int esMasReciente(Producto match) {
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

    public boolean compararCon(Producto otro) {
        return id.equals(otro.id);
    }
}
