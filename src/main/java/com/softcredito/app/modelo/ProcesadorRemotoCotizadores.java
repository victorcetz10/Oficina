package com.softcredito.app.modelo;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import com.google.gson.Gson;
import com.softcredito.app.provider.Contract.Cotizadores;
import com.softcredito.app.utilidades.UConsultas;
import com.softcredito.app.utilidades.UDatos;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Actua como un transformador desde SQLite a JSON para enviar clientes al servidor
 */
public class ProcesadorRemotoCotizadores {
    private static final String TAG = ProcesadorRemotoCotizadores.class.getSimpleName();
    private final String ruta_cotizadores = Environment.getExternalStorageDirectory().getAbsolutePath() +"/softcredito/cotizadores/";

    // Campos JSON
    private static final String INSERCIONES = "inserciones";
    private static final String MODIFICACIONES = "modificaciones";
    private static final String ELIMINACIONES = "eliminaciones";

    private Gson gson = new Gson();

    private interface Consulta {

        // Proyección para consulta de clientes
        String[] PROYECCION = {
                Cotizadores.ID,
                Cotizadores.ID_CLIENTE,
                Cotizadores.FECHA_COTIZACION,
                Cotizadores.VALIDEZ,
                Cotizadores.FECHA_DISPOSICION,
                Cotizadores.FECHA_INICIO_AMORTIZACIONES,
                Cotizadores.ID_PRODUCTO,
                Cotizadores.MONTO_AUTORIZADO,
                Cotizadores.PLAZO_AUTORIZADO,
                Cotizadores.ID_TASA_REFERENCIA,
                Cotizadores.SOBRETASA,
                Cotizadores.TASA_MORATORIA,
                Cotizadores.ID_TIPO_PAGO,
                Cotizadores.ID_TIPO_AMORTIZACION,
                Cotizadores.NOTAS,
                Cotizadores.VERSION
        };
    }


    public Map<String, Object> crearPayload(ContentResolver cr, String id) {
        HashMap<String, Object> payload = new HashMap<>();

        List<Map<String, Object>> inserciones = obtenerInserciones(cr, id);
        List<Map<String, Object>> modificaciones = obtenerModificaciones(cr, id);
        List<String> eliminaciones = obtenerEliminaciones(cr, id);

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

    public List<Map<String, Object>> obtenerInserciones(ContentResolver cr, String id) {
        Uri uri;
        List<Map<String, Object>> ops = new ArrayList<>();

        if(TextUtils.isEmpty(id)){
            uri = Cotizadores.URI_CONTENIDO;
        }else{
            uri = Cotizadores.construirUri(id);
        }
        // Obtener REGISTROS donde 'insertado' = 1
        Cursor c = cr.query(uri,
                Consulta.PROYECCION,
                Cotizadores.INSERTADO + "=?",
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

    public List<Map<String, Object>> obtenerModificaciones(ContentResolver cr, String id) {
        Uri uri;
        List<Map<String, Object>> ops = new ArrayList<>();

        if(TextUtils.isEmpty(id)){
            uri = Cotizadores.URI_CONTENIDO;
        }else{
            uri = Cotizadores.construirUri(id);
        }
        // Obtener clientes donde 'modificado' = 1
        Cursor c = cr.query(uri,
                Consulta.PROYECCION,
                Cotizadores.MODIFICADO + "=? OR (" + Cotizadores.INSERTADO + "<>?)",//Los que se modificaron o los que se se desde el servidor
                new String[]{"1","1"}, null);

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

    public List<String> obtenerEliminaciones(ContentResolver cr, String id) {
        Uri uri;
        List<String> ops = new ArrayList<>();

        if(TextUtils.isEmpty(id)){
            uri = Cotizadores.URI_CONTENIDO;
        }else{
            uri = Cotizadores.construirUri(id);
        }
        // Obtener clientes donde 'eliminado' = 1
        Cursor c = cr.query(uri,
                Consulta.PROYECCION,
                Cotizadores.ELIMINADO + "=?",
                new String[]{"1"}, null);

        // Comprobar si hay trabajo que realizar
        if (c != null && c.getCount() > 0) {

            Log.d(TAG, "Existen " + c.getCount() + " eliminaciones de clientes");

            // Procesar operaciones
            while (c.moveToNext()) {
                ops.add(UConsultas.obtenerString(c, Cotizadores.ID));
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
    public void desmarcarCotizadores(ContentResolver cr) {
        // Establecer valores de la actualización
        ContentValues valores = new ContentValues();
        valores.put(Cotizadores.INSERTADO, 0);
        valores.put(Cotizadores.MODIFICADO, 0);

        String seleccion = Cotizadores.INSERTADO + " = ? OR " +
                Cotizadores.MODIFICADO + "= ?";
        String[] argumentos = {"1", "1"};

        // Modificar banderas de insertados y modificados
        cr.update(Cotizadores.URI_CONTENIDO, valores, seleccion, argumentos);

        seleccion = Cotizadores.ELIMINADO + "=?";
        // Eliminar definitivamente
        cr.delete(Cotizadores.URI_CONTENIDO, seleccion, new String[]{"1"});

    }
    /**
     * Convierte los Ids locales a los remotoe
     *
     * @param response json ids
     * @param cr content resolver
     */
    public void convertirCotizadores(JSONObject response, ContentResolver cr) {
        try {
            JSONObject conversiones=response.getJSONObject("conversiones");
            Iterator<?> keys = conversiones.keys();
            while( keys.hasNext() ) {
                String key = (String)keys.next();
                if (Long.parseLong(conversiones.get(key).toString())!=0) {
                    // Establecer valores de la actualización
                    ContentValues valores = new ContentValues();
                    valores.put(Cotizadores.ID, conversiones.get(key).toString());

                    String seleccion = Cotizadores.ID + " = ?";
                    String[] argumentos = {key};//Key es el ID Local

                    // Modificar banderas de insertados y modificados
                    cr.update(Cotizadores.URI_CONTENIDO, valores, seleccion, argumentos);
                }
            }
        }catch (JSONException e){

        }
    }
    /**
     * Actualiza los registros locales a los remotoe
     *
     * @param response json cotizadores
     * @param cr content resolver
     */
    public void actualizarCotizadores(JSONObject response, ContentResolver cr) {
        try {
            JSONObject conversiones=response.getJSONObject("actuaizaciones");
            Iterator<?> keys = conversiones.keys();
            while( keys.hasNext() ) {
                String key = (String)keys.next();
                if (!conversiones.isNull(key)) {
                    JSONObject row=response.getJSONObject(key);
                    // Establecer valores de la actualización
                    ContentValues valores = new ContentValues();
                    valores.put(Cotizadores.ID, row.get(Cotizadores.ID).toString());
                    valores.put(Cotizadores.FECHA_COTIZACION, row.get(Cotizadores.FECHA_COTIZACION).toString());
                    valores.put(Cotizadores.VALIDEZ, row.get(Cotizadores.VALIDEZ).toString());
                    valores.put(Cotizadores.FECHA_DISPOSICION, row.get(Cotizadores.FECHA_DISPOSICION).toString());
                    valores.put(Cotizadores.FECHA_INICIO_AMORTIZACIONES, row.get(Cotizadores.FECHA_INICIO_AMORTIZACIONES).toString());
                    valores.put(Cotizadores.ID_PRODUCTO, row.get(Cotizadores.ID_PRODUCTO).toString());
                    valores.put(Cotizadores.MONTO_AUTORIZADO, row.get(Cotizadores.MONTO_AUTORIZADO).toString());
                    valores.put(Cotizadores.PLAZO_AUTORIZADO, row.get(Cotizadores.PLAZO_AUTORIZADO).toString());
                    valores.put(Cotizadores.ID_TASA_REFERENCIA, row.get(Cotizadores.ID_TASA_REFERENCIA).toString());
                    valores.put(Cotizadores.SOBRETASA, row.get(Cotizadores.SOBRETASA).toString());
                    valores.put(Cotizadores.TASA_MORATORIA, row.get(Cotizadores.TASA_MORATORIA).toString());
                    valores.put(Cotizadores.ID_TIPO_PAGO, row.get(Cotizadores.ID_TIPO_PAGO).toString());
                    valores.put(Cotizadores.ID_TIPO_AMORTIZACION, row.get(Cotizadores.ID_TIPO_AMORTIZACION).toString());
                    valores.put(Cotizadores.NOTAS, row.get(Cotizadores.NOTAS).toString());

                    String seleccion = Cotizadores.ID + " = ?";
                    String[] argumentos = {key};//Key es el ID Local

                    // Modificar banderas de insertados y modificados
                    cr.update(Cotizadores.URI_CONTENIDO, valores, seleccion, argumentos);
                }
            }
        }catch (JSONException e){

        }
    }

    /**
     * Genera los pdf de registros
     *
     * @param response json cotizadores
     * @param cr content resolver
     */
    public void generarPDFCotizadores(JSONObject response, ContentResolver cr) {
        try {
            JSONObject pdfs=response.getJSONObject("pdfs");
            Iterator<?> keys = pdfs.keys();
            while( keys.hasNext() ) {
                String key = (String)keys.next();
                if (!TextUtils.isEmpty(pdfs.get(key).toString())) {
                    String idCotizador = key;
                    byte[] contenido = Base64.decode(pdfs.get(key).toString(), 0);


                    File carpeta = new File(ruta_cotizadores);
                    carpeta.mkdirs();
                    String ruta_archivo = ruta_cotizadores + idCotizador + ".pdf";//Los archivos se guardan con el ID del cotizador
                    File archivo = new File( ruta_archivo );
                    try {
                        FileOutputStream output = new FileOutputStream(archivo);
                        output.write(contenido);
                        output.flush();
                        output.close();
                    } catch (IOException ex) {
                        Log.e("ERROR ", "Error:" + ex);
                    }
                }
            }
        }catch (JSONException e){

        }
    }

    private Map<String, Object> mapearInsercion(Cursor c) {
        // Nuevo mapa para reflejarlo en JSON
        Map<String, Object> mapaCotizador = new HashMap<String, Object>();

        // Añadir valores de columnas como atributos
        UDatos.agregarStringAMapa(mapaCotizador, Cotizadores.ID, c);
        UDatos.agregarStringAMapa(mapaCotizador, Cotizadores.ID_CLIENTE, c);
        UDatos.agregarStringAMapa(mapaCotizador, Cotizadores.FECHA_COTIZACION, c);
        UDatos.agregarStringAMapa(mapaCotizador, Cotizadores.VALIDEZ, c);
        UDatos.agregarStringAMapa(mapaCotizador, Cotizadores.FECHA_DISPOSICION, c);
        UDatos.agregarStringAMapa(mapaCotizador, Cotizadores.FECHA_INICIO_AMORTIZACIONES, c);
        UDatos.agregarStringAMapa(mapaCotizador, Cotizadores.ID_PRODUCTO, c);
        UDatos.agregarStringAMapa(mapaCotizador, Cotizadores.MONTO_AUTORIZADO, c);
        UDatos.agregarStringAMapa(mapaCotizador, Cotizadores.PLAZO_AUTORIZADO, c);
        UDatos.agregarStringAMapa(mapaCotizador, Cotizadores.ID_TASA_REFERENCIA, c);
        UDatos.agregarStringAMapa(mapaCotizador, Cotizadores.SOBRETASA, c);
        UDatos.agregarStringAMapa(mapaCotizador, Cotizadores.TASA_MORATORIA, c);
        UDatos.agregarStringAMapa(mapaCotizador, Cotizadores.ID_TIPO_PAGO, c);
        UDatos.agregarStringAMapa(mapaCotizador, Cotizadores.ID_TIPO_AMORTIZACION, c);
        UDatos.agregarStringAMapa(mapaCotizador, Cotizadores.NOTAS, c);
        UDatos.agregarStringAMapa(mapaCotizador, Cotizadores.VERSION, c);

        return mapaCotizador;
    }

    private Map<String, Object> mapearActualizacion(Cursor c) {
        // Nuevo mapa para reflejarlo en JSON
        Map<String, Object> mapaCotizador = new HashMap<String, Object>();

        // Añadir valores de columnas como atributos
        UDatos.agregarStringAMapa(mapaCotizador, Cotizadores.ID, c);
        UDatos.agregarStringAMapa(mapaCotizador, Cotizadores.ID_CLIENTE, c);
        UDatos.agregarStringAMapa(mapaCotizador, Cotizadores.FECHA_COTIZACION, c);
        UDatos.agregarStringAMapa(mapaCotizador, Cotizadores.VALIDEZ, c);
        UDatos.agregarStringAMapa(mapaCotizador, Cotizadores.FECHA_DISPOSICION, c);
        UDatos.agregarStringAMapa(mapaCotizador, Cotizadores.FECHA_INICIO_AMORTIZACIONES, c);
        UDatos.agregarStringAMapa(mapaCotizador, Cotizadores.ID_PRODUCTO, c);
        UDatos.agregarStringAMapa(mapaCotizador, Cotizadores.MONTO_AUTORIZADO, c);
        UDatos.agregarStringAMapa(mapaCotizador, Cotizadores.PLAZO_AUTORIZADO, c);
        UDatos.agregarStringAMapa(mapaCotizador, Cotizadores.ID_TASA_REFERENCIA, c);
        UDatos.agregarStringAMapa(mapaCotizador, Cotizadores.SOBRETASA, c);
        UDatos.agregarStringAMapa(mapaCotizador, Cotizadores.TASA_MORATORIA, c);
        UDatos.agregarStringAMapa(mapaCotizador, Cotizadores.ID_TIPO_PAGO, c);
        UDatos.agregarStringAMapa(mapaCotizador, Cotizadores.ID_TIPO_AMORTIZACION, c);
        UDatos.agregarStringAMapa(mapaCotizador, Cotizadores.NOTAS, c);
        UDatos.agregarStringAMapa(mapaCotizador, Cotizadores.VERSION, c);

        return mapaCotizador;
    }
}
