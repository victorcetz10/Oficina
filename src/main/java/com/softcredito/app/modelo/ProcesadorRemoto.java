package com.softcredito.app.modelo;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import com.google.gson.Gson;
import com.softcredito.app.provider.Contract.Clientes;
import com.softcredito.app.provider.Contract.Solicitudes;
import com.softcredito.app.provider.Contract.Cotizadores;
import com.softcredito.app.provider.Contract.DocumentosEntregados;
import com.softcredito.app.utilidades.UConsultas;
import com.softcredito.app.utilidades.UDatos;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Actua como un transformador desde SQLite a JSON para enviar clientes al servidor
 */
public class ProcesadorRemoto {
    private static final String TAG = ProcesadorRemoto.class.getSimpleName();

    // Campos JSON
    private static final String INSERCIONES = "inserciones";
    private static final String MODIFICACIONES = "modificaciones";
    private static final String ELIMINACIONES = "eliminaciones";

    private Gson gson = new Gson();

    private interface Consulta {

        // Proyección para consulta de clientes
        String[] PROYECCION = {
                Clientes.ID,
                Clientes.ES_CLIENTE,
                Clientes.TIPO_PERSONA,
                Clientes.RAZON_SOCIAL,
                Clientes.PRIMER_NOMBRE,
                Clientes.SEGUNDO_NOMBRE,
                Clientes.PRIMER_APELLIDO,
                Clientes.SEGUNDO_APELLIDO,
                Clientes.CURP,
                Clientes.RFC,
                Clientes.INE,
                Clientes.FECHA_NACIMIENTO,
                Clientes.ESTADO_CIVIL,
                Clientes.OCUPACION,
                Clientes.ID_CATEGORIA_ACTIVIDAD_ECONOMICA,
                Clientes.ID_ACTIVIDAD_ECONOMICA,
                Clientes.CONTACTO,
                Clientes.RELACION_CONTACTO,
                Clientes.TELEFONO,
                Clientes.CELULAR,
                Clientes.CORREO,
                Clientes.PAIS,
                Clientes.ESTADO,
                Clientes.MUNICIPIO,
                Clientes.LOCALIDAD,
                Clientes.CODIGO_POSTAL,
                Clientes.NOTAS,
                Clientes.VERSION
        };
    }


    public Map<String, Object> crearPayload(ContentResolver cr) {
        HashMap<String, Object> payload = new HashMap<>();

        List<Map<String, Object>> inserciones = obtenerInserciones(cr);
        List<Map<String, Object>> modificaciones = obtenerModificaciones(cr);
        List<String> eliminaciones = obtenerEliminaciones(cr);

        // Verificación: ¿Hay cambios locales?
        if (inserciones == null && modificaciones == null && eliminaciones == null) {
            return null;
        }

        payload.put(INSERCIONES, inserciones);
        payload.put(MODIFICACIONES, modificaciones);
        payload.put(ELIMINACIONES, eliminaciones);

        //return gson.toJson(payload);
        return payload;
    }

    public List<Map<String, Object>> obtenerInserciones(ContentResolver cr) {
        List<Map<String, Object>> ops = new ArrayList<>();

        // Obtener CLIENTES donde 'insertado' = 1
        Cursor c = cr.query(Clientes.URI_CONTENIDO,
                Consulta.PROYECCION,
                Clientes.INSERTADO + "=?",
                new String[]{"1"}, null);

        // Comprobar si hay trabajo que realizar
        if (c != null && c.getCount() > 0) {

            Log.d(TAG, "Inserciones remotas: " + c.getCount());

            // Procesar inserciones
            while (c.moveToNext()) {
                ops.add(mapearInsercion(c));
            }

            return ops;

        } else {
            return null;
        }

    }

    public List<Map<String, Object>> obtenerModificaciones(ContentResolver cr) {

        List<Map<String, Object>> ops = new ArrayList<>();

        // Obtener clientes donde 'modificado' = 1
        Cursor c = cr.query(Clientes.URI_CONTENIDO,
                Consulta.PROYECCION,
                Clientes.MODIFICADO + "=?",
                new String[]{"1"}, null);

        // Comprobar si hay trabajo que realizar
        if (c != null && c.getCount() > 0) {

            Log.d(TAG, "Existen " + c.getCount() + " modificaciones de clientes");

            // Procesar operaciones
            while (c.moveToNext()) {
                ops.add(mapearActualizacion(c));
            }

            return ops;

        } else {
            return null;
        }

    }

    public List<String> obtenerEliminaciones(ContentResolver cr) {

        List<String> ops = new ArrayList<>();

        // Obtener clientes donde 'eliminado' = 1
        Cursor c = cr.query(Clientes.URI_CONTENIDO,
                Consulta.PROYECCION,
                Clientes.ELIMINADO + "=?",
                new String[]{"1"}, null);

        // Comprobar si hay trabajo que realizar
        if (c != null && c.getCount() > 0) {

            Log.d(TAG, "Existen " + c.getCount() + " eliminaciones de clientes");

            // Procesar operaciones
            while (c.moveToNext()) {
                ops.add(UConsultas.obtenerString(c, Clientes.ID));
            }

            return ops;

        } else {
            return null;
        }

    }


    /**
     * Desmarca los clientes locales que ya han sido sincronizados
     *
     * @param cr content resolver
     */
    public void desmarcarClientes(ContentResolver cr) {
        // Establecer valores de la actualización
        ContentValues valores = new ContentValues();
        valores.put(Clientes.INSERTADO, 0);
        valores.put(Clientes.MODIFICADO, 0);

        String seleccion = Clientes.INSERTADO + " = ? OR " +
                Clientes.MODIFICADO + "= ?";
        String[] argumentos = {"1", "1"};

        // Modificar banderas de insertados y modificados
        cr.update(Clientes.URI_CONTENIDO, valores, seleccion, argumentos);

        seleccion = Clientes.ELIMINADO + "=?";
        // Eliminar definitivamente
        cr.delete(Clientes.URI_CONTENIDO, seleccion, new String[]{"1"});

    }
    /**
     * Convierte los Ids locales a los remotoe
     *
     * @param response json ids
     * @param cr content resolver
     */
    public void convertirClientes(JSONObject response, ContentResolver cr) {
        try {
            JSONObject conversiones=response.getJSONObject("conversiones");
            Iterator<?> keys = conversiones.keys();
            while( keys.hasNext() ) {
                String key = (String)keys.next();
                if (Long.parseLong(conversiones.get(key).toString())!=0) {
                    // Establecer valores de la actualización
                    ContentValues valores = new ContentValues();
                    valores.put(Clientes.ID, conversiones.get(key).toString());

                    String seleccion = Clientes.ID + " = ?";
                    String[] argumentos = {key};//Key es el ID Local

                    // Modificar banderas de insertados y modificados
                    cr.update(Clientes.URI_CONTENIDO, valores, seleccion, argumentos);

                    //Actualizar las solicituds con el nuevo ID
                    ContentValues valoresSolicitudes = new ContentValues();
                    valoresSolicitudes.put(Solicitudes.ID_CLIENTE, conversiones.get(key).toString());
                    String seleccionSolicitudes = Solicitudes.ID_CLIENTE + " = ?";
                    String[] argumentosSolicitudes = {key};//Key es el ID Local
                    cr.update(Solicitudes.URI_CONTENIDO, valoresSolicitudes, seleccionSolicitudes, argumentosSolicitudes);

                    //Actualizar las cotizaciones con el nuevo ID
                    ContentValues valoresCotizadores = new ContentValues();
                    valoresCotizadores.put(Cotizadores.ID_CLIENTE, conversiones.get(key).toString());
                    String seleccionCotizadores = Cotizadores.ID_CLIENTE + " = ?";
                    String[] argumentosCotizadores = {key};//Key es el ID Local
                    cr.update(Cotizadores.URI_CONTENIDO, valoresCotizadores, seleccionCotizadores, argumentosCotizadores);

                    //Actualizar los documentos entregados con el nuevo ID
                    ContentValues valoresDocumentosEntregados = new ContentValues();
                    valoresDocumentosEntregados.put(DocumentosEntregados.ID_CLIENTE, conversiones.get(key).toString());
                    String seleccionDocumentosEntregados = DocumentosEntregados.ID_CLIENTE + " = ?";
                    String[] argumentosDocumentosEntregados = {key};//Key es el ID Local
                    cr.update(DocumentosEntregados.URI_CONTENIDO, valoresDocumentosEntregados, seleccionDocumentosEntregados, argumentosDocumentosEntregados);
                }
            }
        }catch (JSONException e){

        }
    }

    private Map<String, Object> mapearInsercion(Cursor c) {
        // Nuevo mapa para reflejarlo en JSON
        Map<String, Object> mapaCliente = new HashMap<String, Object>();

        // Añadir valores de columnas como atributos
        UDatos.agregarStringAMapa(mapaCliente, Clientes.ID, c);
        UDatos.agregarStringAMapa(mapaCliente, Clientes.ES_CLIENTE, c);
        UDatos.agregarStringAMapa(mapaCliente, Clientes.TIPO_PERSONA, c);
        UDatos.agregarStringAMapa(mapaCliente, Clientes.RAZON_SOCIAL, c);
        UDatos.agregarStringAMapa(mapaCliente, Clientes.PRIMER_NOMBRE, c);
        UDatos.agregarStringAMapa(mapaCliente, Clientes.SEGUNDO_NOMBRE, c);
        UDatos.agregarStringAMapa(mapaCliente, Clientes.PRIMER_APELLIDO, c);
        UDatos.agregarStringAMapa(mapaCliente, Clientes.SEGUNDO_APELLIDO, c);
        UDatos.agregarStringAMapa(mapaCliente, Clientes.CURP, c);
        UDatos.agregarStringAMapa(mapaCliente, Clientes.RFC, c);
        UDatos.agregarStringAMapa(mapaCliente, Clientes.INE, c);
        UDatos.agregarStringAMapa(mapaCliente, Clientes.FECHA_NACIMIENTO, c);
        UDatos.agregarStringAMapa(mapaCliente, Clientes.ESTADO_CIVIL, c);
        UDatos.agregarStringAMapa(mapaCliente, Clientes.OCUPACION, c);
        UDatos.agregarStringAMapa(mapaCliente, Clientes.ID_CATEGORIA_ACTIVIDAD_ECONOMICA, c);
        UDatos.agregarStringAMapa(mapaCliente, Clientes.ID_ACTIVIDAD_ECONOMICA, c);
        UDatos.agregarStringAMapa(mapaCliente, Clientes.CONTACTO, c);
        UDatos.agregarStringAMapa(mapaCliente, Clientes.RELACION_CONTACTO, c);
        UDatos.agregarStringAMapa(mapaCliente, Clientes.TELEFONO, c);
        UDatos.agregarStringAMapa(mapaCliente, Clientes.CELULAR, c);
        UDatos.agregarStringAMapa(mapaCliente, Clientes.CORREO, c);
        UDatos.agregarStringAMapa(mapaCliente, Clientes.PAIS, c);
        UDatos.agregarStringAMapa(mapaCliente, Clientes.ESTADO, c);
        UDatos.agregarStringAMapa(mapaCliente, Clientes.MUNICIPIO, c);
        UDatos.agregarStringAMapa(mapaCliente, Clientes.LOCALIDAD, c);
        UDatos.agregarStringAMapa(mapaCliente, Clientes.CODIGO_POSTAL, c);
        UDatos.agregarStringAMapa(mapaCliente, Clientes.NOTAS, c);
        UDatos.agregarStringAMapa(mapaCliente, Clientes.VERSION, c);

        return mapaCliente;
    }

    private Map<String, Object> mapearActualizacion(Cursor c) {
        // Nuevo mapa para reflejarlo en JSON
        Map<String, Object> mapaCliente = new HashMap<String, Object>();

        // Añadir valores de columnas como atributos
        UDatos.agregarStringAMapa(mapaCliente, Clientes.ID, c);
        UDatos.agregarStringAMapa(mapaCliente, Clientes.ES_CLIENTE, c);
        UDatos.agregarStringAMapa(mapaCliente, Clientes.TIPO_PERSONA, c);
        UDatos.agregarStringAMapa(mapaCliente, Clientes.RAZON_SOCIAL, c);
        UDatos.agregarStringAMapa(mapaCliente, Clientes.PRIMER_NOMBRE, c);
        UDatos.agregarStringAMapa(mapaCliente, Clientes.SEGUNDO_NOMBRE, c);
        UDatos.agregarStringAMapa(mapaCliente, Clientes.PRIMER_APELLIDO, c);
        UDatos.agregarStringAMapa(mapaCliente, Clientes.SEGUNDO_APELLIDO, c);
        UDatos.agregarStringAMapa(mapaCliente, Clientes.CURP, c);
        UDatos.agregarStringAMapa(mapaCliente, Clientes.RFC, c);
        UDatos.agregarStringAMapa(mapaCliente, Clientes.INE, c);
        UDatos.agregarStringAMapa(mapaCliente, Clientes.FECHA_NACIMIENTO, c);
        UDatos.agregarStringAMapa(mapaCliente, Clientes.ESTADO_CIVIL, c);
        UDatos.agregarStringAMapa(mapaCliente, Clientes.OCUPACION, c);
        UDatos.agregarStringAMapa(mapaCliente, Clientes.ID_CATEGORIA_ACTIVIDAD_ECONOMICA, c);
        UDatos.agregarStringAMapa(mapaCliente, Clientes.ID_ACTIVIDAD_ECONOMICA, c);
        UDatos.agregarStringAMapa(mapaCliente, Clientes.CONTACTO, c);
        UDatos.agregarStringAMapa(mapaCliente, Clientes.RELACION_CONTACTO, c);
        UDatos.agregarStringAMapa(mapaCliente, Clientes.TELEFONO, c);
        UDatos.agregarStringAMapa(mapaCliente, Clientes.CELULAR, c);
        UDatos.agregarStringAMapa(mapaCliente, Clientes.CORREO, c);
        UDatos.agregarStringAMapa(mapaCliente, Clientes.PAIS, c);
        UDatos.agregarStringAMapa(mapaCliente, Clientes.ESTADO, c);
        UDatos.agregarStringAMapa(mapaCliente, Clientes.MUNICIPIO, c);
        UDatos.agregarStringAMapa(mapaCliente, Clientes.LOCALIDAD, c);
        UDatos.agregarStringAMapa(mapaCliente, Clientes.CODIGO_POSTAL, c);
        UDatos.agregarStringAMapa(mapaCliente, Clientes.VERSION, c);

        return mapaCliente;
    }
}
