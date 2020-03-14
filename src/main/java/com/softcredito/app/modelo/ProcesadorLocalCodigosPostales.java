package com.softcredito.app.modelo;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.google.gson.Gson;
import com.softcredito.app.provider.Contract.CodigosPostales;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Elemento que controla la transformación de JSON a POJO y viceversa
 */
public class ProcesadorLocalCodigosPostales {

    private static final String TAG = ProcesadorLocalCodigosPostales.class.getSimpleName();

    private interface Consulta{

        // Proyección para la consulta
        String[] PROYECCION = {
                CodigosPostales.ID,
                CodigosPostales.CODIGO_POSTAL,
                CodigosPostales.ID_MUNICIPIO,
                CodigosPostales.VERSION,
                CodigosPostales.MODIFICADO
        };

        // Indices de columnas
        int ID = 0;
        int CODIGO_POSTAL = 1;
        int ID_MUNICIPIO = 2;
        int VERSION = 3;
        int MODIFICADO = 4;

    }

    // Mapa para filtrar solo los elementos a sincronizar
    private HashMap<String, CodigoPostal> remotos = new HashMap<>();

    // Conversor JSON
    private Gson gson = new Gson();

    public ProcesadorLocalCodigosPostales() {
    }

    public void procesar(JSONArray arrayJson) {
        // Añadir elementos convertidos al mapa de los remotos
        for (CodigoPostal item : gson
                .fromJson(arrayJson.toString(), CodigoPostal[].class)) {
            item.aplicarSanidad();
            remotos.put(item.id, item);//Se agregan al mapa para poder procesarlos despues
        }
    }

    public void procesarOperaciones(ArrayList<ContentProviderOperation> ops, ContentResolver resolver) {
        // Consultar datos locales
        Cursor c = resolver.query(CodigosPostales.URI_CONTENIDO,
                Consulta.PROYECCION,
                CodigosPostales.INSERTADO + "=?",
                new String[]{"0"}, null);

        if (c != null) {

            while (c.moveToNext()) {

                // Convertir fila del cursor en objeto
                CodigoPostal filaActual = deCursorACodigoPostal(c);

                // Buscar si el dato local se encuentra en el mapa de remotos
                CodigoPostal match = remotos.get(filaActual.id);

                if (match != null) {
                    // Esta entrada existe, por lo que se remueve del mapa de remotos
                    remotos.remove(filaActual.id);

                    // Crear uri del item
                    Uri updateUri = CodigosPostales.construirUri(filaActual.id);

                    /*
                    Aquí se aplica la resolución de conflictos de modificaciones de un mismo recurso
                    tanto en el servidro como en la app. Quién tenga la versión más actual, será tomado
                    como preponderante
                     */
                    if (match.compararCon(filaActual)) {
                        int flag = match.esMasReciente(filaActual);
                        if (flag > 0) {
                            Log.d(TAG, "Programar actualización  del codigo postal " + updateUri);

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
                    Uri deleteUri = CodigosPostales.construirUri(filaActual.id);
                    Log.i(TAG, "Programar Eliminación del codigo postal " + deleteUri);
                    ops.add(ContentProviderOperation.newDelete(deleteUri).build());
                }
            }
            c.close();
        }

        // Insertar los items resultantes ya que se asume que no existen en local
        for (CodigoPostal codigoPostal : remotos.values()) {
            Log.d(TAG, "Programar Inserción de un nuevo codigo postal con ID = " + codigoPostal.id);
            ops.add(construirOperacionInsert(codigoPostal));
        }
    }

    private ContentProviderOperation construirOperacionInsert(CodigoPostal nuevo) {
        return ContentProviderOperation.newInsert(CodigosPostales.URI_CONTENIDO)
                .withValue(CodigosPostales.ID, nuevo.id)
                .withValue(CodigosPostales.CODIGO_POSTAL, nuevo.codigo_postal)
                .withValue(CodigosPostales.ID_MUNICIPIO, nuevo.id_municipio)
                .withValue(CodigosPostales.VERSION, nuevo.version)
                .withValue(CodigosPostales.INSERTADO, 0)
                .build();
    }

    private ContentProviderOperation construirOperacionUpdate(CodigoPostal match, Uri updateUri) {
        return ContentProviderOperation.newUpdate(updateUri)
                .withValue(CodigosPostales.ID, match.id)
                .withValue(CodigosPostales.CODIGO_POSTAL, match.codigo_postal)
                .withValue(CodigosPostales.ID_MUNICIPIO, match.id_municipio)
                .withValue(CodigosPostales.VERSION, match.version)
                .withValue(CodigosPostales.MODIFICADO, match.modificado)
                .build();
    }

    /**
     * Convierte una fila de un Cursor en un nuevo Cliente
     *
     * @param c cursor
     * @return objeto cliente
     */
    private CodigoPostal deCursorACodigoPostal(Cursor c) {
        return new CodigoPostal(
                c.getString(Consulta.ID),
                c.getString(Consulta.CODIGO_POSTAL),
                c.getInt(Consulta.ID_MUNICIPIO),
                c.getString(Consulta.VERSION),
                c.getInt(Consulta.MODIFICADO)
        );
    }
}
