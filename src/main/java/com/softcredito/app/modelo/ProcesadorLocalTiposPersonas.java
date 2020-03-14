package com.softcredito.app.modelo;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.google.gson.Gson;

import com.softcredito.app.provider.Contract.TiposPersonas;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Elemento que controla la transformación de JSON a POJO y viceversa
 */
public class ProcesadorLocalTiposPersonas {

    private static final String TAG = ProcesadorLocalTiposPersonas.class.getSimpleName();

    private interface Consulta{

        // Proyección para la consulta
        String[] PROYECCION = {
                TiposPersonas.ID,
                TiposPersonas.CLAVE,
                TiposPersonas.NOMBRE,
                TiposPersonas.DESCRIPCION,
                TiposPersonas.VERSION,
                TiposPersonas.MODIFICADO
        };

        // Indices de columnas
        int ID = 0;
        int CLAVE = 1;
        int NOMBRE = 2;
        int DESCRIPCION = 3;
        int VERSION = 4;
        int MODIFICADO = 5;

    }

    // Mapa para filtrar solo los elementos a sincronizar
    private HashMap<String, TipoPersona> remotos = new HashMap<>();

    // Conversor JSON
    private Gson gson = new Gson();

    public ProcesadorLocalTiposPersonas() {
    }

    public void procesar(JSONArray arrayJson) {
        // Añadir elementos convertidos al mapa de los remotos
        for (TipoPersona item : gson
                .fromJson(arrayJson.toString(), TipoPersona[].class)) {
            item.aplicarSanidad();
            remotos.put(item.id, item);//Se agregan al mapa para poder procesarlos despues
        }
    }

    public void procesarOperaciones(ArrayList<ContentProviderOperation> ops, ContentResolver resolver) {
        // Consultar datos locales
        Cursor c = resolver.query(TiposPersonas.URI_CONTENIDO,
                Consulta.PROYECCION,
                TiposPersonas.INSERTADO + "=?",
                new String[]{"0"}, null);

        if (c != null) {

            while (c.moveToNext()) {

                // Convertir fila del cursor en objeto
                TipoPersona filaActual = deCursorATipoPersona(c);

                // Buscar si el dato local se encuentra en el mapa de remotos
                TipoPersona match = remotos.get(filaActual.id);

                if (match != null) {
                    // Esta entrada existe, por lo que se remueve del mapa de remotos
                    remotos.remove(filaActual.id);

                    // Crear uri del item
                    Uri updateUri = TiposPersonas.construirUri(filaActual.id);

                    /*
                    Aquí se aplica la resolución de conflictos de modificaciones de un mismo recurso
                    tanto en el servidro como en la app. Quién tenga la versión más actual, será tomado
                    como preponderante
                     */
                    if (match.compararCon(filaActual)) {
                        int flag = match.esMasReciente(filaActual);
                        if (flag > 0) {
                            Log.d(TAG, "Programar actualización  del tipo de persona " + updateUri);

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
                    Uri deleteUri = TiposPersonas.construirUri(filaActual.id);
                    Log.i(TAG, "Programar Eliminación del tipo de persona " + deleteUri);
                    ops.add(ContentProviderOperation.newDelete(deleteUri).build());
                }
            }
            c.close();
        }

        // Insertar los items resultantes ya que se asume que no existen en local
        for (TipoPersona tipoPersona : remotos.values()) {
            Log.d(TAG, "Programar Inserción de un nuevo tipo de persona con ID = " + tipoPersona.id);
            ops.add(construirOperacionInsert(tipoPersona));
        }
    }

    private ContentProviderOperation construirOperacionInsert(TipoPersona nuevo) {
        return ContentProviderOperation.newInsert(TiposPersonas.URI_CONTENIDO)
                .withValue(TiposPersonas.ID, nuevo.id)
                .withValue(TiposPersonas.CLAVE, nuevo.clave)
                .withValue(TiposPersonas.NOMBRE, nuevo.nombre)
                .withValue(TiposPersonas.DESCRIPCION, nuevo.descripcion)
                .withValue(TiposPersonas.VERSION, nuevo.version)
                .withValue(TiposPersonas.INSERTADO, 0)
                .build();
    }

    private ContentProviderOperation construirOperacionUpdate(TipoPersona match, Uri updateUri) {
        return ContentProviderOperation.newUpdate(updateUri)
                .withValue(TiposPersonas.ID, match.id)
                .withValue(TiposPersonas.CLAVE, match.clave)
                .withValue(TiposPersonas.NOMBRE, match.nombre)
                .withValue(TiposPersonas.DESCRIPCION, match.descripcion)
                .withValue(TiposPersonas.VERSION, match.version)
                .withValue(TiposPersonas.MODIFICADO, match.modificado)
                .build();
    }

    /**
     * Convierte una fila de un Cursor en un nuevo Cliente
     *
     * @param c cursor
     * @return objeto cliente
     */
    private TipoPersona deCursorATipoPersona(Cursor c) {
        return new TipoPersona(
                c.getString(Consulta.ID),
                c.getString(Consulta.CLAVE),
                c.getString(Consulta.NOMBRE),
                c.getString(Consulta.DESCRIPCION),
                c.getString(Consulta.VERSION),
                c.getInt(Consulta.MODIFICADO)
        );
    }
}
