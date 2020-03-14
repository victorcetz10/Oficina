package com.softcredito.app.provider;

import android.net.Uri;
import android.provider.BaseColumns;

import java.util.UUID;

/**
 * Contrato con la estructura de la base de datos y forma de las URIs
 */
public class Promocion {

    interface ColumnasSincronizacion {
        String MODIFICADO = "modificado";
        String ELIMINADO = "eliminado";
        String INSERTADO = "insertado";
    }

    interface ColumnasCliente {
        String ID = "id"; // Pk
        String TIPO_PERSONA = "tipo_persona";
        String RAZON_SOCIAL = "razon_social";
        String PRIMER_NOMBRE = "nombre1";
        String SEGUNDO_NOMBRE = "nombre2";
        String PRIMER_APELLIDO = "apellido_paterno";
        String SEGUNDO_APELLIDO = "apellido_materno";
        String CONTACTO = "contacto";
        String RELACION_CONTACTO = "relacion_contacto";
        String TELEFONO = "telefono";
        String CORREO = "correo";
        String VERSION = "version";
    }


    // Autoridad del Content Provider
    public final static String AUTORIDAD = "com.softcredito.app";

    // Uri base
    public final static Uri URI_CONTENIDO_BASE = Uri.parse("content://" + AUTORIDAD);


    /**
     * Controlador de la tabla "cliente"
     */
    public static class Clientes
            implements BaseColumns, ColumnasCliente, ColumnasSincronizacion {

        public static final Uri URI_CONTENIDO =
                URI_CONTENIDO_BASE.buildUpon().appendPath(RECURSO_CLIENTE).build();

        public final static String MIME_RECURSO =
                "vnd.android.cursor.item/vnd." + AUTORIDAD + "/" + RECURSO_CLIENTE;

        public final static String MIME_COLECCION =
                "vnd.android.cursor.dir/vnd." + AUTORIDAD + "/" + RECURSO_CLIENTE;


        /**
         * Construye una {@link Uri} para el {@link #ID} solicitado.
         */
        public static Uri construirUriCliente(String id) {
            return URI_CONTENIDO.buildUpon().appendPath(id).build();
        }

        public static String generarId() {
            return "C-" + UUID.randomUUID();
        }

        public static String obtenerId(Uri uri) {
            return uri.getLastPathSegment();
        }
    }

    // Recursos
    public final static String RECURSO_CLIENTE = "clientes";

}
