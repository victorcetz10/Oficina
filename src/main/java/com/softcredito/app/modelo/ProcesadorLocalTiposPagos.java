package com.softcredito.app.modelo;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.google.gson.Gson;

import com.softcredito.app.provider.Contract.TiposPagos;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Elemento que controla la transformación de JSON a POJO y viceversa
 */
public class ProcesadorLocalTiposPagos {

    private static final String TAG = ProcesadorLocalTiposPagos.class.getSimpleName();

    private interface Consulta{

        // Proyección para la consulta
        String[] PROYECCION = {
                TiposPagos.ID,
                TiposPagos.NOMBRE,
                TiposPagos.DESCRIPCION,
                TiposPagos.VERSION,
                TiposPagos.MODIFICADO
        };

        // Indices de columnas
        int ID = 0;
        int NOMBRE = 1;
        int DESCRIPCION = 2;
        int VERSION = 3;
        int MODIFICADO = 4;

    }

    // Mapa para filtrar solo los elementos a sincronizar
    private HashMap<String, TipoPago> remotos = new HashMap<>();

    // Conversor JSON
    private Gson gson = new Gson();

    public ProcesadorLocalTiposPagos() {
    }

    public void procesar(JSONArray arrayJson) {
        // Añadir elementos convertidos al mapa de los remotos
        for (TipoPago item : gson
                .fromJson(arrayJson.toString(), TipoPago[].class)) {
            item.aplicarSanidad();
            remotos.put(item.id, item);//Se agregan al mapa para poder procesarlos despues
        }
    }

    public void procesarOperaciones(ArrayList<ContentProviderOperation> ops, ContentResolver resolver) {
        // Consultar datos locales
        Cursor c = resolver.query(TiposPagos.URI_CONTENIDO,
                Consulta.PROYECCION,
                TiposPagos.INSERTADO + "=?",
                new String[]{"0"}, null);

        if (c != null) {

            while (c.moveToNext()) {

                // Convertir fila del cursor en objeto
                TipoPago filaActual = deCursorATipoPago(c);

                // Buscar si el dato local se encuentra en el mapa de remotos
                TipoPago match = remotos.get(filaActual.id);

                if (match != null) {
                    // Esta entrada existe, por lo que se remueve del mapa de remotos
                    remotos.remove(filaActual.id);

                    // Crear uri del item
                    Uri updateUri = TiposPagos.construirUri(filaActual.id);

                    /*
                    Aquí se aplica la resolución de conflictos de modificaciones de un mismo recurso
                    tanto en el servidro como en la app. Quién tenga la versión más actual, será tomado
                    como preponderante
                     */
                    if (match.compararCon(filaActual)) {
                        int flag = match.esMasReciente(filaActual);
                        if (flag > 0) {
                            Log.d(TAG, "Programar actualización  del tipo de pago " + updateUri);

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
                    Uri deleteUri = TiposPagos.construirUri(filaActual.id);
                    Log.i(TAG, "Programar Eliminación del tipo de pago " + deleteUri);
                    ops.add(ContentProviderOperation.newDelete(deleteUri).build());
                }
            }
            c.close();
        }

        // Insertar los items resultantes ya que se asume que no existen en local
        for (TipoPago tipoPago : remotos.values()) {
            Log.d(TAG, "Programar Inserción de un nuevo tipo de pago con ID = " + tipoPago.id);
            ops.add(construirOperacionInsert(tipoPago));
        }
    }

    private ContentProviderOperation construirOperacionInsert(TipoPago nuevo) {
        return ContentProviderOperation.newInsert(TiposPagos.URI_CONTENIDO)
                .withValue(TiposPagos.ID, nuevo.id)
                .withValue(TiposPagos.NOMBRE, nuevo.nombre)
                .withValue(TiposPagos.DESCRIPCION, nuevo.descripcion)
                .withValue(TiposPagos.VERSION, nuevo.version)
                .withValue(TiposPagos.INSERTADO, 0)
                .build();
    }

    private ContentProviderOperation construirOperacionUpdate(TipoPago match, Uri updateUri) {
        return ContentProviderOperation.newUpdate(updateUri)
                .withValue(TiposPagos.ID, match.id)
                .withValue(TiposPagos.NOMBRE, match.nombre)
                .withValue(TiposPagos.DESCRIPCION, match.descripcion)
                .withValue(TiposPagos.VERSION, match.version)
                .withValue(TiposPagos.MODIFICADO, match.modificado)
                .build();
    }

    /**
     * Convierte una fila de un Cursor en un nuevo TipoPago
     *
     * @param c cursor
     * @return objeto tipopago
     */
    private TipoPago deCursorATipoPago(Cursor c) {
        return new TipoPago(
                c.getString(Consulta.ID),
                c.getString(Consulta.NOMBRE),
                c.getString(Consulta.DESCRIPCION),
                c.getString(Consulta.VERSION),
                c.getInt(Consulta.MODIFICADO)
        );
    }
}
