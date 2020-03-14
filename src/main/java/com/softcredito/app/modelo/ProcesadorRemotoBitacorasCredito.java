package com.softcredito.app.modelo;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import com.google.gson.Gson;
import com.softcredito.app.provider.Contract;
import com.softcredito.app.provider.Contract.BitacorasCredito;
import com.softcredito.app.provider.Contract.BitacorasCreditoArchivos;
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
public class ProcesadorRemotoBitacorasCredito {
    private static final String TAG = ProcesadorRemotoBitacorasCredito.class.getSimpleName();

    // Campos JSON
    private static final String INSERCIONES = "inserciones";
    private static final String MODIFICACIONES = "modificaciones";
    private static final String ELIMINACIONES = "eliminaciones";

    private Gson gson = new Gson();

    private interface Consulta {

        // Proyección para la consulta
        String[] PROYECCION = {
                BitacorasCredito.ID,
                BitacorasCredito.ID_SOLICITUD,
                BitacorasCredito.ASUNTO,
                BitacorasCredito.FECHA,
                BitacorasCredito.HORA,
                BitacorasCredito.NUMERO_AMORTIZACION,
                BitacorasCredito.DETALLES_PAGO,
                BitacorasCredito.DESCRIPCION,
                BitacorasCredito.VALOR_GARANTIA,
                BitacorasCredito.DESCRIPCION_GARANTIA,
                BitacorasCredito.VERSION,//Se envia versión para que se empareje con el remoto
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
        Cursor c = cr.query(BitacorasCredito.URI_CONTENIDO,
                Consulta.PROYECCION,
                BitacorasCredito.INSERTADO + "=?",
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
        Cursor c = cr.query(BitacorasCredito.URI_CONTENIDO,
                Consulta.PROYECCION,
                BitacorasCredito.MODIFICADO + "=?",
                new String[]{"1"}, null);

        // Comprobar si hay trabajo que realizar
        if (c != null && c.getCount() > 0) {

            Log.d(TAG, "Existen " + c.getCount() + " modificaciones de bitacoras de crédito");

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
        Cursor c = cr.query(BitacorasCredito.URI_CONTENIDO,
                Consulta.PROYECCION,
                BitacorasCredito.ELIMINADO + "=?",
                new String[]{"1"}, null);

        // Comprobar si hay trabajo que realizar
        if (c != null && c.getCount() > 0) {

            Log.d(TAG, "Existen " + c.getCount() + " eliminaciones de bitacoras de crédito");

            // Procesar operaciones
            while (c.moveToNext()) {
                ops.add(UConsultas.obtenerString(c, BitacorasCredito.ID));
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
        valores.put(BitacorasCredito.INSERTADO, 0);
        valores.put(BitacorasCredito.MODIFICADO, 0);

        String seleccion = BitacorasCredito.INSERTADO + " = ? OR " +
                BitacorasCredito.MODIFICADO + "= ?";
        String[] argumentos = {"1", "1"};

        // Modificar banderas de insertados y modificados
        cr.update(BitacorasCredito.URI_CONTENIDO, valores, seleccion , argumentos);

        seleccion = BitacorasCredito.ELIMINADO + "=?";
        // Eliminar definitivamente
        cr.delete(BitacorasCredito.URI_CONTENIDO, seleccion, new String[]{"1"});

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
                    valores.put(BitacorasCredito.ID, conversiones.get(key).toString());

                    String seleccion = BitacorasCredito.ID + " = ?";
                    String[] argumentos = {key};//Key es el ID Local

                    // Modificar banderas de insertados y modificados
                    cr.update(BitacorasCredito.URI_CONTENIDO, valores, seleccion, argumentos);

                    //Actualizar los Archivos de las bitacoras con el nuevo ID
                    ContentValues valoresArchivos = new ContentValues();
                    valoresArchivos.put(BitacorasCreditoArchivos.ID_BITACORA_CREDITO, conversiones.get(key).toString());
                    String seleccionArchivos = BitacorasCreditoArchivos.ID_BITACORA_CREDITO + " = ?";
                    String[] argumentosArchivos = {key};//Key es el ID Local
                    cr.update(BitacorasCreditoArchivos.URI_CONTENIDO, valoresArchivos, seleccionArchivos, argumentosArchivos);
                }
            }
        }catch (JSONException e){

        }
    }

    private Map<String, Object> mapearInsercion(Cursor c) {
        // Nuevo mapa para reflejarlo en JSON
        Map<String, Object> mapa = new HashMap<String, Object>();

        // Añadir valores de columnas como atributos
        UDatos.agregarStringAMapa(mapa, BitacorasCredito.ID, c);
        UDatos.agregarStringAMapa(mapa, BitacorasCredito.ID_SOLICITUD, c);
        UDatos.agregarStringAMapa(mapa, BitacorasCredito.ASUNTO, c);
        UDatos.agregarStringAMapa(mapa, BitacorasCredito.FECHA, c);
        UDatos.agregarStringAMapa(mapa, BitacorasCredito.HORA, c);
        UDatos.agregarStringAMapa(mapa, BitacorasCredito.NUMERO_AMORTIZACION, c);
        UDatos.agregarStringAMapa(mapa, BitacorasCredito.DETALLES_PAGO, c);
        UDatos.agregarStringAMapa(mapa, BitacorasCredito.DESCRIPCION, c);
        UDatos.agregarStringAMapa(mapa, BitacorasCredito.VALOR_GARANTIA, c);
        UDatos.agregarStringAMapa(mapa, BitacorasCredito.DESCRIPCION_GARANTIA, c);
        UDatos.agregarStringAMapa(mapa, BitacorasCredito.VERSION, c);

        return mapa;
    }

    private Map<String, Object> mapearActualizacion(Cursor c) {
        // Nuevo mapa para reflejarlo en JSON
        Map<String, Object> mapa = new HashMap<String, Object>();

        // Añadir valores de columnas como atributos
        UDatos.agregarStringAMapa(mapa, BitacorasCredito.ID, c);
        UDatos.agregarStringAMapa(mapa, BitacorasCredito.ID_SOLICITUD, c);
        UDatos.agregarStringAMapa(mapa, BitacorasCredito.ASUNTO, c);
        UDatos.agregarStringAMapa(mapa, BitacorasCredito.FECHA, c);
        UDatos.agregarStringAMapa(mapa, BitacorasCredito.HORA, c);
        UDatos.agregarStringAMapa(mapa, BitacorasCredito.NUMERO_AMORTIZACION, c);
        UDatos.agregarStringAMapa(mapa, BitacorasCredito.DETALLES_PAGO, c);
        UDatos.agregarStringAMapa(mapa, BitacorasCredito.DESCRIPCION, c);
        UDatos.agregarStringAMapa(mapa, BitacorasCredito.VALOR_GARANTIA, c);
        UDatos.agregarStringAMapa(mapa, BitacorasCredito.DESCRIPCION_GARANTIA, c);
        UDatos.agregarStringAMapa(mapa, BitacorasCredito.VERSION, c);

        return mapa;
    }
}
