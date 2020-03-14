package com.softcredito.app.modelo;

import com.softcredito.app.utilidades.UTiempo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * POJO de los clientes
 */
public class Estado {

    public String id;
    public String estado;
    public int id_pais;
    public String clave;
    public int riesgo;
    public String clave_buro;
    public String version;
    public int modificado;

    public Estado(String id, String estado, int id_pais, String clave, int riesgo, String clave_buro, String version, int modificado) {
        this.id = id;
        this.estado = estado;
        this.id_pais = id_pais;
        this.clave = clave;
        this.riesgo = riesgo;
        this.clave_buro = clave_buro;
        this.version = version;
        this.modificado = modificado;
    }

    public void aplicarSanidad() {
        id = id == null ? "" : id;
        estado = estado == null ? "" : estado;
        clave = clave == null ? "" : clave;
        clave_buro = clave_buro == null ? "" : clave_buro;
        version = version == null ? UTiempo.obtenerTiempo() : version;
        modificado = 0;
    }

    public int esMasReciente(Estado match) {
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

    public boolean compararCon(Estado otro) {
        return id.equals(otro.id) &&
                estado.equals(otro.estado);
    }
}
