package com.softcredito.app.modelo;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import com.google.gson.Gson;
import com.softcredito.app.provider.Contract.Pagos;
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
public class ProcesadorRemotoPagos {
    private static final String TAG = ProcesadorRemotoPagos.class.getSimpleName();

    // Campos JSON
    private static final String INSERCIONES = "inserciones";
    private static final String MODIFICACIONES = "modificaciones";
    private static final String ELIMINACIONES = "eliminaciones";

    private Gson gson = new Gson();

    private interface Consulta {

        // Proyección para la consulta
        String[] PROYECCION = {
                Pagos.ID,
                Pagos.ID_SOLICITUD,
                Pagos.FECHA,
                Pagos.MONTO,
                Pagos.ID_CANAL_COBRANZA,
                Pagos.ID_INSTRUMENTO_MONETARIO,
                Pagos.VERSION,//Se envia versión para que se empareje con el remoto
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
        Cursor c = cr.query(Pagos.URI_CONTENIDO,
                Consulta.PROYECCION,
                Pagos.INSERTADO + "=?",
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
        Cursor c = cr.query(Pagos.URI_CONTENIDO,
                Consulta.PROYECCION,
                Pagos.MODIFICADO + "=?",
                new String[]{"1"}, null);

        // Comprobar si hay trabajo que realizar
        if (c != null && c.getCount() > 0) {

            Log.d(TAG, "Existen " + c.getCount() + " modificaciones de pagos");

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
        Cursor c = cr.query(Pagos.URI_CONTENIDO,
                Consulta.PROYECCION,
                Pagos.ELIMINADO + "=?",
                new String[]{"1"}, null);

        // Comprobar si hay trabajo que realizar
        if (c != null && c.getCount() > 0) {

            Log.d(TAG, "Existen " + c.getCount() + " eliminaciones de pagos");

            // Procesar operaciones
            while (c.moveToNext()) {
                ops.add(UConsultas.obtenerString(c, Pagos.ID));
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
        valores.put(Pagos.INSERTADO, 0);
        valores.put(Pagos.MODIFICADO, 0);

        String seleccion = Pagos.INSERTADO + " = ? OR " +
                Pagos.MODIFICADO + "= ?";
        String[] argumentos = {"1", "1"};

        // Modificar banderas de insertados y modificados
        cr.update(Pagos.URI_CONTENIDO, valores, seleccion , argumentos);

        seleccion = Pagos.ELIMINADO + "=?";
        // Eliminar definitivamente
        cr.delete(Pagos.URI_CONTENIDO, seleccion, new String[]{"1"});

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
                    valores.put(Pagos.ID, conversiones.get(key).toString());

                    String seleccion = Pagos.ID + " = ?";
                    String[] argumentos = {key};//Key es el ID Local

                    // Modificar banderas de insertados y modificados
                    cr.update(Pagos.URI_CONTENIDO, valores, seleccion, argumentos);
                }
            }
        }catch (JSONException e){

        }
    }

    private Map<String, Object> mapearInsercion(Cursor c) {
        // Nuevo mapa para reflejarlo en JSON
        Map<String, Object> mapa = new HashMap<String, Object>();

        // Añadir valores de columnas como atributos
        UDatos.agregarStringAMapa(mapa, Pagos.ID, c);
        UDatos.agregarStringAMapa(mapa, Pagos.ID_SOLICITUD, c);
        UDatos.agregarStringAMapa(mapa, Pagos.FECHA, c);
        UDatos.agregarStringAMapa(mapa, Pagos.MONTO, c);
        UDatos.agregarStringAMapa(mapa, Pagos.ID_CANAL_COBRANZA, c);
        UDatos.agregarStringAMapa(mapa, Pagos.ID_INSTRUMENTO_MONETARIO, c);
        UDatos.agregarStringAMapa(mapa, Pagos.VERSION, c);

        return mapa;
    }

    private Map<String, Object> mapearActualizacion(Cursor c) {
        // Nuevo mapa para reflejarlo en JSON
        Map<String, Object> mapa = new HashMap<String, Object>();

        // Añadir valores de columnas como atributos
        UDatos.agregarStringAMapa(mapa, Pagos.ID, c);
        UDatos.agregarStringAMapa(mapa, Pagos.ID_SOLICITUD, c);
        UDatos.agregarStringAMapa(mapa, Pagos.FECHA, c);
        UDatos.agregarStringAMapa(mapa, Pagos.MONTO, c);
        UDatos.agregarStringAMapa(mapa, Pagos.ID_CANAL_COBRANZA, c);
        UDatos.agregarStringAMapa(mapa, Pagos.ID_INSTRUMENTO_MONETARIO, c);
        UDatos.agregarStringAMapa(mapa, Pagos.VERSION, c);

        return mapa;
    }
}
