package com.softcredito.app.modelo;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.util.Base64;
import android.util.Log;

import com.google.gson.Gson;
import com.softcredito.app.provider.Contract;
import com.softcredito.app.provider.Contract.BitacorasCreditoArchivos;
import com.softcredito.app.utilidades.UConsultas;
import com.softcredito.app.utilidades.UDatos;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Actua como un transformador desde SQLite a JSON para enviar los registros al servidor
 */
public class ProcesadorRemotoBitacorasCreditoArchivos {
    private static final String TAG = ProcesadorRemotoBitacorasCreditoArchivos.class.getSimpleName();

    // Campos JSON
    private static final String INSERCIONES = "inserciones";
    private static final String MODIFICACIONES = "modificaciones";
    private static final String ELIMINACIONES = "eliminaciones";

    private Gson gson = new Gson();

    private interface Consulta {

        // Proyección para la consulta
        String[] PROYECCION = {
                BitacorasCreditoArchivos.ID,
                BitacorasCreditoArchivos.ID_BITACORA_CREDITO,
                BitacorasCreditoArchivos.FECHA,
                BitacorasCreditoArchivos.NOMBRE,
                BitacorasCreditoArchivos.TIPO,
                BitacorasCreditoArchivos.RUTA,
                BitacorasCreditoArchivos.VERSION,//Se envia versión para que se empareje con el remoto
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
        Cursor c = cr.query(BitacorasCreditoArchivos.URI_CONTENIDO,
                Consulta.PROYECCION,
                BitacorasCreditoArchivos.INSERTADO + "=?",
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
        Cursor c = cr.query(BitacorasCreditoArchivos.URI_CONTENIDO,
                Consulta.PROYECCION,
                BitacorasCreditoArchivos.MODIFICADO + "=?",
                new String[]{"1"}, null);

        // Comprobar si hay trabajo que realizar
        if (c != null && c.getCount() > 0) {

            Log.d(TAG, "Existen " + c.getCount() + " modificaciones de archivos documentos entregados");

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
        Cursor c = cr.query(BitacorasCreditoArchivos.URI_CONTENIDO,
                Consulta.PROYECCION,
                BitacorasCreditoArchivos.ELIMINADO + "=?",
                new String[]{"1"}, null);

        // Comprobar si hay trabajo que realizar
        if (c != null && c.getCount() > 0) {

            Log.d(TAG, "Existen " + c.getCount() + " eliminaciones de archivos documentos entregados");

            // Procesar operaciones
            while (c.moveToNext()) {
                ops.add(UConsultas.obtenerString(c, BitacorasCreditoArchivos.ID));
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
        valores.put(BitacorasCreditoArchivos.INSERTADO, 0);
        valores.put(BitacorasCreditoArchivos.MODIFICADO, 0);

        String seleccion = BitacorasCreditoArchivos.INSERTADO + " = ? OR " +
                BitacorasCreditoArchivos.MODIFICADO + "= ?";
        String[] argumentos = {"1", "1"};

        // Modificar banderas de insertados y modificados
        cr.update(BitacorasCreditoArchivos.URI_CONTENIDO, valores, seleccion , argumentos);

        seleccion = BitacorasCreditoArchivos.ELIMINADO + "=?";
        // Eliminar definitivamente
        cr.delete(BitacorasCreditoArchivos.URI_CONTENIDO, seleccion, new String[]{"1"});

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
                    valores.put(BitacorasCreditoArchivos.ID, conversiones.get(key).toString());

                    String seleccion = BitacorasCreditoArchivos.ID + " = ?";
                    String[] argumentos = {key};//Key es el ID Local

                    // Modificar banderas de insertados y modificados
                    cr.update(BitacorasCreditoArchivos.URI_CONTENIDO, valores, seleccion, argumentos);
                }
            }
        }catch (JSONException e){

        }
    }

    private Map<String, Object> mapearInsercion(Cursor c) {
        // Nuevo mapa para reflejarlo en JSON
        Map<String, Object> mapa = new HashMap<String, Object>();

        // Añadir valores de columnas como atributos
        UDatos.agregarStringAMapa(mapa, BitacorasCreditoArchivos.ID, c);
        UDatos.agregarStringAMapa(mapa, BitacorasCreditoArchivos.ID_BITACORA_CREDITO, c);
        UDatos.agregarStringAMapa(mapa, BitacorasCreditoArchivos.FECHA, c);
        UDatos.agregarStringAMapa(mapa, BitacorasCreditoArchivos.NOMBRE, c);
        UDatos.agregarStringAMapa(mapa, BitacorasCreditoArchivos.TIPO, c);
        //La ruta se envía como referencia, no se guarda pero se necesita para saber que archivos se van a subir
        UDatos.agregarStringAMapa(mapa, BitacorasCreditoArchivos.RUTA, c);
        UDatos.agregarStringAMapa(mapa, BitacorasCreditoArchivos.VERSION, c);

        /*
        try {
            String body = getStringFromFile(UConsultas.obtenerString(c, BitacorasCreditoArchivos.RUTA));
            byte[] bBody64 = Base64.encode(body.getBytes("CP1252"),Base64.DEFAULT);
            String body64 = new String(bBody64,"CP1252");
            mapa.put(Contract.BitacorasCreditoArchivos.BODY, body64);
        }catch (Exception ex){

        }
        */

        return mapa;
    }

    private Map<String, Object> mapearActualizacion(Cursor c) {
        // Nuevo mapa para reflejarlo en JSON
        Map<String, Object> mapa = new HashMap<String, Object>();

        // Añadir valores de columnas como atributos
        UDatos.agregarStringAMapa(mapa, BitacorasCreditoArchivos.ID, c);
        UDatos.agregarStringAMapa(mapa, BitacorasCreditoArchivos.ID_BITACORA_CREDITO, c);
        UDatos.agregarStringAMapa(mapa, BitacorasCreditoArchivos.FECHA, c);
        UDatos.agregarStringAMapa(mapa, BitacorasCreditoArchivos.NOMBRE, c);
        UDatos.agregarStringAMapa(mapa, BitacorasCreditoArchivos.TIPO, c);
        //UDatos.agregarStringAMapa(mapa, BitacorasCreditoArchivos.RUTA, c);
        UDatos.agregarStringAMapa(mapa, BitacorasCreditoArchivos.VERSION, c);

        return mapa;
    }

    public static String convertStreamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        reader.close();
        return sb.toString();
    }

    public static String getStringFromFile (String filePath) throws Exception {
        File fl = new File(filePath);
        int length = (int) fl.length();

        byte[] bytes = new byte[length];

        FileInputStream in = new FileInputStream(fl);
        try {
            in.read(bytes);
        } finally {
            in.close();
        }

        String ret = new String(bytes,"CP1252");
        return ret;
    }
}
