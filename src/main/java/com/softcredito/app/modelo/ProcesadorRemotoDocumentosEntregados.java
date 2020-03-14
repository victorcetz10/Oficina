package com.softcredito.app.modelo;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import com.google.gson.Gson;
import com.softcredito.app.provider.Contract;
import com.softcredito.app.provider.Contract.DocumentosEntregados;
import com.softcredito.app.provider.Contract.ArchivosDocumentosEntregados;
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
 * Actua como un transformador desde SQLite a JSON para enviar los registros al servidor
 */
public class ProcesadorRemotoDocumentosEntregados {
    private static final String TAG = ProcesadorRemotoDocumentosEntregados.class.getSimpleName();

    // Campos JSON
    private static final String INSERCIONES = "inserciones";
    private static final String MODIFICACIONES = "modificaciones";
    private static final String ELIMINACIONES = "eliminaciones";

    private Gson gson = new Gson();

    private interface Consulta {

        // Proyección para la consulta
        String[] PROYECCION = {
                DocumentosEntregados.ID,
                DocumentosEntregados.ID_CLIENTE,
                DocumentosEntregados.ID_DOCUMENTO_REQUERIDO,
                DocumentosEntregados.NOMBRE_DOCUMENTO,
                DocumentosEntregados.DESCRIPCION,
                DocumentosEntregados.STATUS,
                DocumentosEntregados.TIPO_DOCUMENTO,
                DocumentosEntregados.VERSION,//Se envia versión para que se empareje con el remoto
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

        // Obtener REGISTROS donde 'insertado' = 1
        Cursor c = cr.query(DocumentosEntregados.URI_CONTENIDO,
                Consulta.PROYECCION,
                DocumentosEntregados.INSERTADO + "=?",
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

        // Obtener REGISTROS donde 'modificado' = 1
        Cursor c = cr.query(DocumentosEntregados.URI_CONTENIDO,
                Consulta.PROYECCION,
                DocumentosEntregados.MODIFICADO + "=?",
                new String[]{"1"}, null);

        // Comprobar si hay trabajo que realizar
        if (c != null && c.getCount() > 0) {

            Log.d(TAG, "Existen " + c.getCount() + " modificaciones de documentos entregados");

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

        // Obtener REGISTROS donde 'eliminado' = 1
        Cursor c = cr.query(DocumentosEntregados.URI_CONTENIDO,
                Consulta.PROYECCION,
                DocumentosEntregados.ELIMINADO + "=?",
                new String[]{"1"}, null);

        // Comprobar si hay trabajo que realizar
        if (c != null && c.getCount() > 0) {

            Log.d(TAG, "Existen " + c.getCount() + " eliminaciones de documentos entregados");

            // Procesar operaciones
            while (c.moveToNext()) {
                ops.add(UConsultas.obtenerString(c, DocumentosEntregados.ID));
            }

            return ops;

        } else {
            return null;
        }

    }


    /**
     * Desmarca los REGISTROS locales que ya han sido sincronizados
     *
     * @param cr content resolver
     */
    public void desmarcar(ContentResolver cr) {
        // Establecer valores de la actualización
        ContentValues valores = new ContentValues();
        valores.put(DocumentosEntregados.INSERTADO, 0);
        valores.put(DocumentosEntregados.MODIFICADO, 0);

        String seleccion = DocumentosEntregados.INSERTADO + " = ? OR " +
                DocumentosEntregados.MODIFICADO + "= ?";
        String[] argumentos = {"1", "1"};

        // Modificar banderas de insertados y modificados
        cr.update(DocumentosEntregados.URI_CONTENIDO, valores, seleccion , argumentos);

        seleccion = DocumentosEntregados.ELIMINADO + "=?";
        // Eliminar definitivamente
        cr.delete(DocumentosEntregados.URI_CONTENIDO, seleccion, new String[]{"1"});

    }
    /**
     * Convierte los Ids locales a los remotos
     *
     * @param response json ids
     * @param cr content resolver
     */
    public void convertir(JSONObject response, ContentResolver cr) {
        try {
            JSONObject conversiones=response.getJSONObject("conversiones");
            Iterator<?> keys = conversiones.keys();
            while( keys.hasNext() ) {
                String key = (String)keys.next();
                if (Long.parseLong(conversiones.get(key).toString())!=0) {
                    // Establecer valores de la actualización
                    ContentValues valores = new ContentValues();
                    valores.put(DocumentosEntregados.ID, conversiones.get(key).toString());

                    String seleccion = DocumentosEntregados.ID + " = ?";
                    String[] argumentos = {key};//Key es el ID Local

                    // Modificar banderas de insertados y modificados
                    cr.update(DocumentosEntregados.URI_CONTENIDO, valores, seleccion, argumentos);

                    //Actualizar los Archivos de las bitacoras con el nuevo ID
                    ContentValues valoresArchivos = new ContentValues();
                    valoresArchivos.put(ArchivosDocumentosEntregados.ID_DOCUMENTO_ENTREGADO, conversiones.get(key).toString());
                    String seleccionArchivos = ArchivosDocumentosEntregados.ID_DOCUMENTO_ENTREGADO + " = ?";
                    String[] argumentosArchivos = {key};//Key es el ID Local
                    cr.update(ArchivosDocumentosEntregados.URI_CONTENIDO, valoresArchivos, seleccionArchivos, argumentosArchivos);
                }
            }
        }catch (JSONException e){

        }
    }

    private Map<String, Object> mapearInsercion(Cursor c) {
        // Nuevo mapa para reflejarlo en JSON
        Map<String, Object> mapa = new HashMap<String, Object>();

        // Añadir valores de columnas como atributos
        UDatos.agregarStringAMapa(mapa, DocumentosEntregados.ID, c);
        UDatos.agregarStringAMapa(mapa, DocumentosEntregados.ID_CLIENTE, c);
        UDatos.agregarStringAMapa(mapa, DocumentosEntregados.ID_DOCUMENTO_REQUERIDO, c);
        UDatos.agregarStringAMapa(mapa, DocumentosEntregados.NOMBRE_DOCUMENTO, c);
        UDatos.agregarStringAMapa(mapa, DocumentosEntregados.DESCRIPCION, c);
        UDatos.agregarStringAMapa(mapa, DocumentosEntregados.STATUS, c);
        UDatos.agregarStringAMapa(mapa, DocumentosEntregados.TIPO_DOCUMENTO, c);
        UDatos.agregarStringAMapa(mapa, DocumentosEntregados.VERSION, c);

        return mapa;
    }

    private Map<String, Object> mapearActualizacion(Cursor c) {
        // Nuevo mapa para reflejarlo en JSON
        Map<String, Object> mapa = new HashMap<String, Object>();

        // Añadir valores de columnas como atributos
        UDatos.agregarStringAMapa(mapa, DocumentosEntregados.ID, c);
        UDatos.agregarStringAMapa(mapa, DocumentosEntregados.ID_CLIENTE, c);
        UDatos.agregarStringAMapa(mapa, DocumentosEntregados.ID_DOCUMENTO_REQUERIDO, c);
        UDatos.agregarStringAMapa(mapa, DocumentosEntregados.NOMBRE_DOCUMENTO, c);
        UDatos.agregarStringAMapa(mapa, DocumentosEntregados.DESCRIPCION, c);
        UDatos.agregarStringAMapa(mapa, DocumentosEntregados.STATUS, c);
        UDatos.agregarStringAMapa(mapa, DocumentosEntregados.TIPO_DOCUMENTO, c);
        UDatos.agregarStringAMapa(mapa, DocumentosEntregados.VERSION, c);

        return mapa;
    }
}
