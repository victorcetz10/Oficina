package com.softcredito.app.modelo;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.google.gson.Gson;
import com.softcredito.app.provider.Contract.ActividadesEconomicas;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Elemento que controla la transformación de JSON a POJO y viceversa
 */
public class ProcesadorLocalActividadesEconomicas {

    private static final String TAG = ProcesadorLocalActividadesEconomicas.class.getSimpleName();

    private interface Consulta{

        // Proyección para la consulta
        String[] PROYECCION = {
                ActividadesEconomicas.ID,
                ActividadesEconomicas.ID_CATEGORIA,
                ActividadesEconomicas.CLAVE,
                ActividadesEconomicas.DESCRIPCION,
                ActividadesEconomicas.RIESGO,
                ActividadesEconomicas.VERSION,
                ActividadesEconomicas.MODIFICADO
        };

        // Indices de columnas
        int ID = 0;
        int ID_CATEGORIA = 1;
        int CLAVE = 2;
        int DESCRIPCION = 3;
        int RIESGO = 4;
        int VERSION = 5;
        int MODIFICADO = 6;

    }

    // Mapa para filtrar solo los elementos a sincronizar
    private HashMap<String, ActividadEconomica> remotos = new HashMap<>();

    // Conversor JSON
    private Gson gson = new Gson();

    public ProcesadorLocalActividadesEconomicas() {
    }

    public void procesar(JSONArray arrayJson) {
        // Añadir elementos convertidos al mapa de los remotos
        for (ActividadEconomica item : gson
                .fromJson(arrayJson.toString(), ActividadEconomica[].class)) {
            item.aplicarSanidad();
            remotos.put(item.id, item);//Se agregan al mapa para poder procesarlos despues
        }
    }

    public void procesarOperaciones(ArrayList<ContentProviderOperation> ops, ContentResolver resolver) {
        // Consultar datos locales
        Cursor c = resolver.query(ActividadesEconomicas.URI_CONTENIDO,
                Consulta.PROYECCION,
                ActividadesEconomicas.INSERTADO + "=?",
                new String[]{"0"}, null);

        if (c != null) {

            while (c.moveToNext()) {

                // Convertir fila del cursor en objeto
                ActividadEconomica filaActual = deCursorAActividadEconomica(c);

                // Buscar si el dato local se encuentra en el mapa de remotos
                ActividadEconomica match = remotos.get(filaActual.id);

                if (match != null) {
                    // Esta entrada existe, por lo que se remueve del mapa de remotos
                    remotos.remove(filaActual.id);

                    // Crear uri del item
                    Uri updateUri = ActividadesEconomicas.construirUri(filaActual.id);

                    /*
                    Aquí se aplica la resolución de conflictos de modificaciones de un mismo recurso
                    tanto en el servidro como en la app. Quién tenga la versión más actual, será tomado
                    como preponderante
                     */
                    if (match.compararCon(filaActual)) {
                        int flag = match.esMasReciente(filaActual);
                        if (flag > 0) {
                            Log.d(TAG, "Programar actualización  de la actividad economica " + updateUri);

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
                    Uri deleteUri = ActividadesEconomicas.construirUri(filaActual.id);
                    Log.i(TAG, "Programar Eliminación de la actividad economica " + deleteUri);
                    ops.add(ContentProviderOperation.newDelete(deleteUri).build());
                }
            }
            c.close();
        }

        // Insertar los items resultantes ya que se asume que no existen en local
        for (ActividadEconomica estado_civil : remotos.values()) {
            Log.d(TAG, "Programar Inserción de una nueva actividad economica con ID = " + estado_civil.id);
            ops.add(construirOperacionInsert(estado_civil));
        }
    }

    private ContentProviderOperation construirOperacionInsert(ActividadEconomica nuevo) {
        return ContentProviderOperation.newInsert(ActividadesEconomicas.URI_CONTENIDO)
                .withValue(ActividadesEconomicas.ID, nuevo.id)
                .withValue(ActividadesEconomicas.ID_CATEGORIA, nuevo.id_categoria)
                .withValue(ActividadesEconomicas.CLAVE, nuevo.clave)
                .withValue(ActividadesEconomicas.DESCRIPCION, nuevo.descripcion)
                .withValue(ActividadesEconomicas.RIESGO, nuevo.riesgo)
                .withValue(ActividadesEconomicas.VERSION, nuevo.version)
                .withValue(ActividadesEconomicas.INSERTADO, 0)
                .build();
    }

    private ContentProviderOperation construirOperacionUpdate(ActividadEconomica match, Uri updateUri) {
        return ContentProviderOperation.newUpdate(updateUri)
                .withValue(ActividadesEconomicas.ID, match.id)
                .withValue(ActividadesEconomicas.ID_CATEGORIA, match.id_categoria)
                .withValue(ActividadesEconomicas.CLAVE, match.clave)
                .withValue(ActividadesEconomicas.DESCRIPCION, match.descripcion)
                .withValue(ActividadesEconomicas.RIESGO, match.riesgo)
                .withValue(ActividadesEconomicas.VERSION, match.version)
                .withValue(ActividadesEconomicas.MODIFICADO, match.modificado)
                .build();
    }

    /**
     * Convierte una fila de un Cursor en un nuevo Cliente
     *
     * @param c cursor
     * @return objeto cliente
     */
    private ActividadEconomica deCursorAActividadEconomica(Cursor c) {
        return new ActividadEconomica(
                c.getString(Consulta.ID),
                c.getInt(Consulta.ID_CATEGORIA),
                c.getString(Consulta.CLAVE),
                c.getString(Consulta.DESCRIPCION),
                c.getInt(Consulta.RIESGO),
                c.getString(Consulta.VERSION),
                c.getInt(Consulta.MODIFICADO)
        );
    }
}
