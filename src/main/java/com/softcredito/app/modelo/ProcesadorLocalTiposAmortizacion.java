package com.softcredito.app.modelo;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.google.gson.Gson;

import com.softcredito.app.provider.Contract.TiposAmortizacion;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Elemento que controla la transformación de JSON a POJO y viceversa
 */
public class ProcesadorLocalTiposAmortizacion {

    private static final String TAG = ProcesadorLocalTiposAmortizacion.class.getSimpleName();

    private interface Consulta{

        // Proyección para la consulta
        String[] PROYECCION = {
                TiposAmortizacion.ID,
                TiposAmortizacion.NOMBRE,
                TiposAmortizacion.PAGO,
                TiposAmortizacion.PLAZO,
                TiposAmortizacion.VERSION,
                TiposAmortizacion.MODIFICADO
        };

        // Indices de columnas
        int ID = 0;
        int NOMBRE = 1;
        int PAGO = 2;
        int PLAZO = 3;
        int VERSION = 4;
        int MODIFICADO = 5;

    }

    // Mapa para filtrar solo los elementos a sincronizar
    private HashMap<String, TipoAmortizacion> remotos = new HashMap<>();

    // Conversor JSON
    private Gson gson = new Gson();

    public ProcesadorLocalTiposAmortizacion() {
    }

    public void procesar(JSONArray arrayJson) {
        // Añadir elementos convertidos al mapa de los remotos
        for (TipoAmortizacion item : gson
                .fromJson(arrayJson.toString(), TipoAmortizacion[].class)) {
            item.aplicarSanidad();
            remotos.put(item.id, item);//Se agregan al mapa para poder procesarlos despues
        }
    }

    public void procesarOperaciones(ArrayList<ContentProviderOperation> ops, ContentResolver resolver) {
        // Consultar datos locales
        Cursor c = resolver.query(TiposAmortizacion.URI_CONTENIDO,
                Consulta.PROYECCION,
                TiposAmortizacion.INSERTADO + "=?",
                new String[]{"0"}, null);

        if (c != null) {

            while (c.moveToNext()) {

                // Convertir fila del cursor en objeto
                TipoAmortizacion filaActual = deCursorATipoAmortizacion(c);

                // Buscar si el dato local se encuentra en el mapa de remotos
                TipoAmortizacion match = remotos.get(filaActual.id);

                if (match != null) {
                    // Esta entrada existe, por lo que se remueve del mapa de remotos
                    remotos.remove(filaActual.id);

                    // Crear uri del item
                    Uri updateUri = TiposAmortizacion.construirUri(filaActual.id);

                    /*
                    Aquí se aplica la resolución de conflictos de modificaciones de un mismo recurso
                    tanto en el servidro como en la app. Quién tenga la versión más actual, será tomado
                    como preponderante
                     */
                    if (match.compararCon(filaActual)) {
                        int flag = match.esMasReciente(filaActual);
                        if (flag > 0) {
                            Log.d(TAG, "Programar actualización  del tipo de amortización " + updateUri);

                            // Verificación: ¿Existe conflicto de modificación?
                            if (filaActual.modificado == 1) {
                                match.modificado = 0;
                            }
                            ops.add(construirOperacionUpdate(match, updateUri));

                        }

                    }

                } else {
                    /*
                    Se deduce que aquellos elementos que no coincidieron, ya no existen en el servidor,
                    por lo que se eliminarán
                     */
                    Uri deleteUri = TiposAmortizacion.construirUri(filaActual.id);
                    Log.i(TAG, "Programar Eliminación del tipo de amortización " + deleteUri);
                    ops.add(ContentProviderOperation.newDelete(deleteUri).build());
                }
            }
            c.close();
        }

        // Insertar los items resultantes ya que se asume que no existen en local
        for (TipoAmortizacion tipoAmortizacion : remotos.values()) {
            Log.d(TAG, "Programar Inserción de un nuevo tipo de amortización con ID = " + tipoAmortizacion.id);
            ops.add(construirOperacionInsert(tipoAmortizacion));
        }
    }

    private ContentProviderOperation construirOperacionInsert(TipoAmortizacion nuevo) {
        return ContentProviderOperation.newInsert(TiposAmortizacion.URI_CONTENIDO)
                .withValue(TiposAmortizacion.ID, nuevo.id)
                .withValue(TiposAmortizacion.NOMBRE, nuevo.nombre)
                .withValue(TiposAmortizacion.PAGO, nuevo.pago)
                .withValue(TiposAmortizacion.PLAZO, nuevo.plazo)
                .withValue(TiposAmortizacion.VERSION, nuevo.version)
                .withValue(TiposAmortizacion.INSERTADO, 0)
                .build();
    }

    private ContentProviderOperation construirOperacionUpdate(TipoAmortizacion match, Uri updateUri) {
        return ContentProviderOperation.newUpdate(updateUri)
                .withValue(TiposAmortizacion.ID, match.id)
                .withValue(TiposAmortizacion.NOMBRE, match.nombre)
                .withValue(TiposAmortizacion.PAGO, match.pago)
                .withValue(TiposAmortizacion.PLAZO, match.plazo)
                .withValue(TiposAmortizacion.VERSION, match.version)
                .withValue(TiposAmortizacion.MODIFICADO, match.modificado)
                .build();
    }

    /**
     * Convierte una fila de un Cursor en un nuevo TipoAmortizacion
     *
     * @param c cursor
     * @return objeto tipoAmortizacion
     */
    private TipoAmortizacion deCursorATipoAmortizacion(Cursor c) {
        return new TipoAmortizacion(
                c.getString(Consulta.ID),
                c.getString(Consulta.NOMBRE),
                c.getString(Consulta.PAGO),
                c.getString(Consulta.PLAZO),
                c.getString(Consulta.VERSION),
                c.getInt(Consulta.MODIFICADO)
        );
    }
}
