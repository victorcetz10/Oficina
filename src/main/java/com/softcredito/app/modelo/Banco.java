package com.softcredito.app.modelo;

import com.softcredito.app.utilidades.UTiempo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * POJO de los clientes
 */
public class Banco {

    public String id;
    public String institucion;
    public String sucursal;
    public String clabe;
    public String numero_cuenta;
    public String version;
    public int modificado;

    public Banco(String id, String institucion, String sucursal, String clabe, String numero_cuenta, String version, int modificado) {
        this.id = id;
        this.institucion = institucion;
        this.sucursal = sucursal;
        this.clabe = clabe;
        this.numero_cuenta = numero_cuenta;
        this.version = version;
        this.modificado = modificado;
    }

    public void aplicarSanidad() {
        id = id == null ? "" : id;
        institucion = institucion == null ? "" : institucion;
        sucursal = sucursal == null ? "" : sucursal;
        clabe = clabe == null ? "" : clabe;
        numero_cuenta = numero_cuenta == null ? "" : numero_cuenta;
        version = version == null ? UTiempo.obtenerTiempo() : version;
        modificado = 0;
    }

    public int esMasReciente(Banco match) {
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

    public boolean compararCon(Banco otro) {
        return id.equals(otro.id);
    }
}
