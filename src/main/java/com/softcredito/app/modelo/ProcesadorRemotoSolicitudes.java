package com.softcredito.app.modelo;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import com.google.gson.Gson;
import com.softcredito.app.provider.Contract.Solicitudes;
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
public class ProcesadorRemotoSolicitudes {
    private static final String TAG = ProcesadorRemotoSolicitudes.class.getSimpleName();

    // Campos JSON
    private static final String INSERCIONES = "inserciones";
    private static final String MODIFICACIONES = "modificaciones";
    private static final String ELIMINACIONES = "eliminaciones";

    private Gson gson = new Gson();

    private interface Consulta {

        // Proyección para la consulta
        String[] PROYECCION = {
                Solicitudes.ID,
                Solicitudes.CLAVE,
                Solicitudes.CONTRATO,
                Solicitudes.ID_CLIENTE,
                Solicitudes.ID_GRUPO,
                Solicitudes.FECHA_SOLICITUD,
                Solicitudes.ID_PRODUCTO,
                Solicitudes.ID_BANCO,
                Solicitudes.MONTO_SOLICITADO,
                Solicitudes.PLAZO_SOLICITADO,
                Solicitudes.ID_TASA_REFERENCIA,
                Solicitudes.SOBRETASA,
                Solicitudes.TASA_MORATORIA,
                Solicitudes.ID_TIPO_PAGO,
                Solicitudes.ID_TIPO_AMORTIZACION,
                //Solicitudes.MONTO_PAGAR,//Este campo no se sincroniza
                //Solicitudes.FECHA_VENCIMIENTO,//Este campo no se sincroniza
                Solicitudes.VERSION,//Se envia versión para que se empareje con el remoto
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
        Cursor c = cr.query(Solicitudes.URI_CONTENIDO,
                Consulta.PROYECCION,
                Solicitudes.INSERTADO + "=?",
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
        Cursor c = cr.query(Solicitudes.URI_CONTENIDO,
                Consulta.PROYECCION,
                Solicitudes.MODIFICADO + "=?",
                new String[]{"1"}, null);

        // Comprobar si hay trabajo que realizar
        if (c != null && c.getCount() > 0) {

            Log.d(TAG, "Existen " + c.getCount() + " modificaciones de solicitudes");

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
        Cursor c = cr.query(Solicitudes.URI_CONTENIDO,
                Consulta.PROYECCION,
                Solicitudes.ELIMINADO + "=?",
                new String[]{"1"}, null);

        // Comprobar si hay trabajo que realizar
        if (c != null && c.getCount() > 0) {

            Log.d(TAG, "Existen " + c.getCount() + " eliminaciones de solicitudes");

            // Procesar operaciones
            while (c.moveToNext()) {
                ops.add(UConsultas.obtenerString(c, Solicitudes.ID));
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
        valores.put(Solicitudes.INSERTADO, 0);
        valores.put(Solicitudes.MODIFICADO, 0);

        String seleccion = Solicitudes.INSERTADO + " = ? OR " +
                Solicitudes.MODIFICADO + "= ?";
        String[] argumentos = {"1", "1"};

        // Modificar banderas de insertados y modificados
        cr.update(Solicitudes.URI_CONTENIDO, valores, seleccion , argumentos);

        seleccion = Solicitudes.ELIMINADO + "=?";
        // Eliminar definitivamente
        cr.delete(Solicitudes.URI_CONTENIDO, seleccion, new String[]{"1"});

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
                    valores.put(Solicitudes.ID, conversiones.get(key).toString());

                    String seleccion = Solicitudes.ID + " = ?";
                    String[] argumentos = {key};//Key es el ID Local

                    // Modificar banderas de insertados y modificados
                    cr.update(Solicitudes.URI_CONTENIDO, valores, seleccion, argumentos);
                }
            }
        }catch (JSONException e){

        }
    }

    private Map<String, Object> mapearInsercion(Cursor c) {
        // Nuevo mapa para reflejarlo en JSON
        Map<String, Object> mapa = new HashMap<String, Object>();

        // Añadir valores de columnas como atributos
        UDatos.agregarStringAMapa(mapa, Solicitudes.ID, c);
        UDatos.agregarStringAMapa(mapa, Solicitudes.CLAVE, c);
        UDatos.agregarStringAMapa(mapa, Solicitudes.CONTRATO, c);
        UDatos.agregarStringAMapa(mapa, Solicitudes.ID_CLIENTE, c);
        UDatos.agregarStringAMapa(mapa, Solicitudes.ID_GRUPO, c);
        UDatos.agregarStringAMapa(mapa, Solicitudes.FECHA_SOLICITUD, c);
        UDatos.agregarStringAMapa(mapa, Solicitudes.ID_PRODUCTO, c);
        UDatos.agregarStringAMapa(mapa, Solicitudes.ID_BANCO, c);
        UDatos.agregarStringAMapa(mapa, Solicitudes.MONTO_SOLICITADO, c);
        UDatos.agregarStringAMapa(mapa, Solicitudes.PLAZO_SOLICITADO, c);
        UDatos.agregarStringAMapa(mapa, Solicitudes.ID_TASA_REFERENCIA, c);
        UDatos.agregarStringAMapa(mapa, Solicitudes.SOBRETASA, c);
        UDatos.agregarStringAMapa(mapa, Solicitudes.TASA_MORATORIA, c);
        UDatos.agregarStringAMapa(mapa, Solicitudes.ID_TIPO_PAGO, c);
        UDatos.agregarStringAMapa(mapa, Solicitudes.ID_TIPO_AMORTIZACION, c);
        //UDatos.agregarStringAMapa(mapa, Solicitudes.MONTO_PAGAR, c);//Esta columna no se sincroniza
        //UDatos.agregarStringAMapa(mapa, Solicitudes.FECHA_VENCIMIENTO, c);//Esta columna no se sincroniza
        UDatos.agregarStringAMapa(mapa, Solicitudes.VERSION, c);

        return mapa;
    }

    private Map<String, Object> mapearActualizacion(Cursor c) {
        // Nuevo mapa para reflejarlo en JSON
        Map<String, Object> mapa = new HashMap<String, Object>();

        // Añadir valores de columnas como atributos
        UDatos.agregarStringAMapa(mapa, Solicitudes.ID, c);
        UDatos.agregarStringAMapa(mapa, Solicitudes.CLAVE, c);
        UDatos.agregarStringAMapa(mapa, Solicitudes.CONTRATO, c);
        UDatos.agregarStringAMapa(mapa, Solicitudes.ID_CLIENTE, c);
        UDatos.agregarStringAMapa(mapa, Solicitudes.ID_GRUPO, c);
        UDatos.agregarStringAMapa(mapa, Solicitudes.FECHA_SOLICITUD, c);
        UDatos.agregarStringAMapa(mapa, Solicitudes.ID_PRODUCTO, c);
        UDatos.agregarStringAMapa(mapa, Solicitudes.ID_BANCO, c);
        UDatos.agregarStringAMapa(mapa, Solicitudes.MONTO_SOLICITADO, c);
        UDatos.agregarStringAMapa(mapa, Solicitudes.PLAZO_SOLICITADO, c);
        UDatos.agregarStringAMapa(mapa, Solicitudes.ID_TASA_REFERENCIA, c);
        UDatos.agregarStringAMapa(mapa, Solicitudes.SOBRETASA, c);
        UDatos.agregarStringAMapa(mapa, Solicitudes.TASA_MORATORIA, c);
        UDatos.agregarStringAMapa(mapa, Solicitudes.ID_TIPO_PAGO, c);
        UDatos.agregarStringAMapa(mapa, Solicitudes.ID_TIPO_AMORTIZACION, c);
        //UDatos.agregarStringAMapa(mapa, Solicitudes.MONTO_PAGAR, c);//Esta columna no se sincroniza
        //UDatos.agregarStringAMapa(mapa, Solicitudes.FECHA_VENCIMIENTO, c);//Esta columna no se sincroniza
        UDatos.agregarStringAMapa(mapa, Solicitudes.VERSION, c);

        return mapa;
    }
}
