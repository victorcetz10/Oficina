package com.softcredito.app.modelo;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.google.gson.Gson;
import com.softcredito.app.provider.Contract.Paises;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Elemento que controla la transformación de JSON a POJO y viceversa
 */
public class ProcesadorLocalPaises {

    private static final String TAG = ProcesadorLocalPaises.class.getSimpleName();

    private interface Consulta{

        // Proyección para la consulta
        String[] PROYECCION = {
                Paises.ID,
                Paises.PAIS,
                Paises.ISO,
                Paises.RIESGO,
                Paises.PREDETERMINADO,
                Paises.VERSION,
                Paises.MODIFICADO
        };

        // Indices de columnas
        int ID = 0;
        int PAIS = 1;
        int ISO = 2;
        int RIESGO = 3;
        int PREDETERMINADO = 4;
        int VERSION = 5;
        int MODIFICADO = 6;

    }

    // Mapa para filtrar solo los elementos a sincronizar
    private HashMap<String, Pais> remotos = new HashMap<>();

    // Conversor JSON
    private Gson gson = new Gson();

    public ProcesadorLocalPaises() {
    }

    public void procesar(JSONArray arrayJson) {
        // Añadir elementos convertidos al mapa de los remotos
        for (Pais item : gson
                .fromJson(arrayJson.toString(), Pais[].class)) {
            item.aplicarSanidad();
            remotos.put(item.id, item);//Se agregan al mapa para poder procesarlos despues
        }
    }

    public void procesarOperaciones(ArrayList<ContentProviderOperation> ops, ContentResolver resolver) {
        // Consultar datos locales
        Cursor c = resolver.query(Paises.URI_CONTENIDO,
                Consulta.PROYECCION,
                Paises.INSERTADO + "=?",
                new String[]{"0"}, null);

        if (c != null) {

            while (c.moveToNext()) {

                // Convertir fila del cursor en objeto
                Pais filaActual = deCursorAPais(c);

                // Buscar si el dato local se encuentra en el mapa de remotos
                Pais match = remotos.get(filaActual.id);

                if (match != null) {
                    // Esta entrada existe, por lo que se remueve del mapa de remotos
                    remotos.remove(filaActual.id);

                    // Crear uri del item
                    Uri updateUri = Paises.construirUri(filaActual.id);

                    /*
                    Aquí se aplica la resolución de conflictos de modificaciones de un mismo recurso
                    tanto en el servidro como en la app. Quién tenga la versión más actual, será tomado
                    como preponderante
                     */
                    if (match.compararCon(filaActual)) {
                        int flag = match.esMasReciente(filaActual);
                        if (flag > 0) {
                            Log.d(TAG, "Programar actualización  del pais " + updateUri);

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
                    Uri deleteUri = Paises.construirUri(filaActual.id);
                    Log.i(TAG, "Programar Eliminación del pais " + deleteUri);
                    ops.add(ContentProviderOperation.newDelete(deleteUri).build());
                }
            }
            c.close();
        }

        // Insertar los items resultantes ya que se asume que no existen en local
        for (Pais pais : remotos.values()) {
            Log.d(TAG, "Programar Inserción de un nuevo pais con ID = " + pais.id);
            ops.add(construirOperacionInsert(pais));
        }
    }

    private ContentProviderOperation construirOperacionInsert(Pais nuevo) {
        return ContentProviderOperation.newInsert(Paises.URI_CONTENIDO)
                .withValue(Paises.ID, nuevo.id)
                .withValue(Paises.PAIS, nuevo.pais)
                .withValue(Paises.ISO, nuevo.iso)
                .withValue(Paises.RIESGO, nuevo.riesgo)
                .withValue(Paises.PREDETERMINADO, nuevo.predeterminado)
                .withValue(Paises.VERSION, nuevo.version)
                .withValue(Paises.INSERTADO, 0)
                .build();
    }

    private ContentProviderOperation construirOperacionUpdate(Pais match, Uri updateUri) {
        return ContentProviderOperation.newUpdate(updateUri)
                .withValue(Paises.ID, match.id)
                .withValue(Paises.PAIS, match.pais)
                .withValue(Paises.ISO, match.iso)
                .withValue(Paises.RIESGO, match.riesgo)
                .withValue(Paises.PREDETERMINADO, match.predeterminado)
                .withValue(Paises.VERSION, match.version)
                .withValue(Paises.MODIFICADO, match.modificado)
                .build();
    }

    /**
     * Convierte una fila de un Cursor en un nuevo Cliente
     *
     * @param c cursor
     * @return objeto cliente
     */
    private Pais deCursorAPais(Cursor c) {
        return new Pais(
                c.getString(Consulta.ID),
                c.getString(Consulta.PAIS),
                c.getString(Consulta.ISO),
                c.getString(Consulta.RIESGO),
                c.getInt(Consulta.PREDETERMINADO),
                c.getString(Consulta.VERSION),
                c.getInt(Consulta.MODIFICADO)
        );
    }
}
