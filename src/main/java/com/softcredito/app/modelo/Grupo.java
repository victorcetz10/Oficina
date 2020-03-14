package com.softcredito.app.modelo;

import com.softcredito.app.utilidades.UTiempo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * POJO de los grupos (este no tiene procesador local porque el caso de los pagos solo se suben, no se descargan)
 */
public class Grupo {

    public String id;
    public String clave;
    public String nombre;
    public String descripcion;
    public String dia_reunion;
    public String hora_reunion;
    public String version;
    public int modificado;

    public Grupo(String id, String clave, String nomnbre, String descripcion, String dia_reunion, String hora_reunion, String version, int modificado) {
        this.id = id;
        this.clave = clave;
        this.nombre = nomnbre;
        this.descripcion = descripcion;
        this.dia_reunion = dia_reunion;
        this.hora_reunion = hora_reunion;
        this.version = version;
        this.modificado = modificado;
    }

    public void aplicarSanidad() {
        id = id == null ? "" : id;
        clave = clave == null ? "" : clave;
        nombre = nombre == null ? "" : nombre;
        descripcion = descripcion == null ? "" : descripcion;
        dia_reunion = dia_reunion == null ? "" : dia_reunion;
        hora_reunion = hora_reunion == null ? "" : hora_reunion;
        version = version == null ? UTiempo.obtenerTiempo() : version;
        modificado = 0;
    }

    public int esMasReciente(Grupo match) {
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

    public boolean compararCon(Grupo otro) {
        return id.equals(otro.id) &&
                clave.equals(otro.clave);
    }
}
