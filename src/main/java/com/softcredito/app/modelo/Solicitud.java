package com.softcredito.app.modelo;

import com.softcredito.app.utilidades.UTiempo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * POJO de los solicitudes
 */
public class Solicitud {

    public String id;
    public String clave;
    public String contrato;
    public String id_cliente;
    public String id_grupo;
    public String fecha_solicitud;
    public String id_producto;
    public String id_banco;//En este caso es string porque el id del banco puede ser generado desde android
    public String monto_solicitado;
    public int plazo_solicitado;
    public int id_tasa_referencia;
    public String sobretasa;
    public String tasa_moratoria;
    public int id_tipo_pago;
    public int id_tipo_amortizacion;
    public String monto_pagar;
    public String monto_vencido;
    public String fecha_vencimiento;
    public String status;
    public String version;
    public int modificado;

    public Solicitud(String id, String clave, String contrato, String id_grupo, String id_cliente, String fecha_solicitud, String id_producto,
                     String id_banco, String monto_solicitado, int plazo_solicitado, int id_tasa_referencia, String sobretasa,
                     String tasa_moratoria, int id_tipo_pago, int id_tipo_amortizacion, String monto_pagar, String monto_vencido, String fecha_vencimiento, String status,
                     String version, int modificado) {

        this.id = id;
        this.clave = clave;
        this.contrato = contrato;
        this.id_grupo = id_grupo;
        this.id_cliente = id_cliente;
        this.fecha_solicitud = fecha_solicitud;
        this.id_producto = id_producto;
        this.id_banco = id_banco;
        this.monto_solicitado = monto_solicitado;
        this.plazo_solicitado = plazo_solicitado;
        this.id_tasa_referencia = id_tasa_referencia;
        this.sobretasa = sobretasa;
        this.tasa_moratoria = tasa_moratoria;
        this.id_tipo_pago = id_tipo_pago;
        this.id_tipo_amortizacion = id_tipo_amortizacion;
        this.monto_pagar = monto_pagar;
        this.monto_vencido = monto_vencido;
        this.fecha_vencimiento = fecha_vencimiento;
        this.status = status;
        this.version = version;
        this.modificado = modificado;
    }

    public void aplicarSanidad() {
        id = id == null ? "" : id;
        version = version == null ? UTiempo.obtenerTiempo() : version;
        modificado = 0;
    }

    public int esMasReciente(Solicitud match) {
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

    public boolean compararCon(Solicitud otro) {
        return id.equals(otro.id);
    }
}
