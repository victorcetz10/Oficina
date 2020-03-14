package com.softcredito.app.modelo;

import com.softcredito.app.utilidades.UTiempo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * POJO de los clientes
 */
public class Cliente {

    public String id;
    public String tipo_persona;
    public String razon_social;
    public String nombre1;
    public String nombre2;
    public String apellido_paterno;
    public String apellido_materno;
    public String contacto;
    public String relacion_contacto;
    public String telefono;
    public String correo;
    public String latitud;
    public String longitud;
    public String curp;
    public String rfc;
    public String ine;
    public String codigo_postal;
    public String pais;
    public String estado;
    public String municipio;
    public String localidad;
    public String colonia;
    public String calle;
    public String numero_exterior;
    public String numero_interior;
    public String referencia;
    public String fecha_nacimiento;
    public String ocupacion;
    public String estado_civil;
    public int id_categoria_actividad_economica;
    public int id_actividad_economica;
    public String celular;
    public int es_cliente;
    public String notas;
    public String version;
    public int modificado;

    public Cliente(String id, String tipo_persona, String razon_social, String nombre1, String nombre2, String apellido_paterno,
                    String apellido_materno, String contacto, String relacion_contacto, String telefono, String correo,
                   String latitud, String longitud,
                   String curp, String rfc, String ine, String codigo_postal, String pais, String estado, String municipio, String localidad,
                   String colonia, String calle, String numero_exterior, String numero_interior, String referencia,
                   String fecha_nacimiento, String ocupacion, String estado_civil, int id_categoria_actividad_economica, int id_actividad_economica, String celular, int es_cliente, String notas,
                   String version, int modificado) {
        this.id = id;
        this.tipo_persona = tipo_persona;
        this.razon_social = razon_social;
        this.nombre1 = nombre1;
        this.nombre2 = nombre2;
        this.apellido_paterno = apellido_paterno;
        this.apellido_materno = apellido_materno;
        this.contacto = contacto;
        this.relacion_contacto = relacion_contacto;
        this.telefono = telefono;
        this.correo = correo;
        this.latitud = latitud;
        this.longitud = longitud;
        this.curp = curp;
        this.rfc = rfc;
        this.ine = ine;
        this.codigo_postal = codigo_postal;
        this.pais = pais;
        this.estado = estado;
        this.municipio = municipio;
        this.localidad = localidad;
        this.colonia= colonia;
        this.calle = calle;
        this.numero_exterior = numero_exterior;
        this.numero_interior = numero_interior;
        this.referencia = referencia;
        this.fecha_nacimiento = fecha_nacimiento;
        this.ocupacion = ocupacion;
        this.estado_civil = estado_civil;
        this.id_categoria_actividad_economica = id_categoria_actividad_economica;
        this.id_actividad_economica = id_actividad_economica;
        this.celular = celular;
        this.es_cliente = es_cliente;
        this.notas = notas;
        this.version = version;
        this.modificado = modificado;
    }

    public void aplicarSanidad() {
        id = id == null ? "" : id;
        tipo_persona = tipo_persona == null ? "" : tipo_persona;
        razon_social = razon_social == null ? "" : razon_social;
        nombre1 = nombre1 == null ? "" : nombre1;
        nombre2 = nombre2 == null ? "" : nombre2;
        apellido_paterno = apellido_paterno == null ? "" : apellido_paterno;
        apellido_materno = apellido_materno == null ? "" : apellido_materno;
        contacto = contacto == null ? "" : contacto;
        relacion_contacto = relacion_contacto == null ? "" : relacion_contacto;
        telefono = telefono == null ? "" : telefono;
        correo = correo == null ? "" : correo;
        //
        latitud = latitud == null ? "" : latitud;
        longitud = longitud == null ? "" : longitud;
        //
        version = version == null ? UTiempo.obtenerTiempo() : version;
        modificado = 0;
    }

    public int esMasReciente(Cliente match) {
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

    public boolean compararCon(Cliente otro) {
        return id.equals(otro.id);
    }
}
