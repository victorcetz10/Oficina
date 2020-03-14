package com.softcredito.app.modelo;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.google.gson.Gson;
import com.softcredito.app.provider.Contract.TiposContactos;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Elemento que controla la transformación de JSON a POJO y viceversa
 */
public class ProcesadorLocalTiposContactos {

    private static final String TAG = ProcesadorLocalTiposContactos.class.getSimpleName();

    private interface Consulta{

        // Proyección para la consulta
        String[] PROYECCION = {
                TiposContactos.ID,
                TiposContactos.NOMBRE,
                TiposContactos.DESCRIPCION,
                TiposContactos.VERSION,
                TiposContactos.MODIFICADO
        };

        // Indices de columnas
        int ID = 0;
        int NOMBRE = 1;
        int DESCRIPCION = 2;
        int VERSION = 3;
        int MODIFICADO = 4;

    }

    // Mapa para filtrar solo los elementos a sincronizar
    private HashMap<String, TipoContacto> remotos = new HashMap<>();

    // Conversor JSON
    private Gson gson = new Gson();

    public ProcesadorLocalTiposContactos() {
    }

    public void procesar(JSONArray arrayJson) {
        // Añadir elementos convertidos al mapa de los remotos
        for (TipoContacto item : gson
                .fromJson(arrayJson.toString(), TipoContacto[].class)) {
            item.aplicarSanidad();
            remotos.put(item.id, item);//Se agregan al mapa para poder procesarlos despues
        }
    }

    public void procesarOperaciones(ArrayList<ContentProviderOperation> ops, ContentResolver resolver) {
        // Consultar datos locales
        Cursor c = resolver.query(TiposContactos.URI_CONTENIDO,
                Consulta.PROYECCION,
                TiposContactos.INSERTADO + "=?",
                new String[]{"0"}, null);

        if (c != null) {

            while (c.moveToNext()) {

                // Convertir fila del cursor en objeto
                TipoContacto filaActual = deCursorATipoContacto(c);

                // Buscar si el dato local se encuentra en el mapa de remotos
                TipoContacto match = remotos.get(filaActual.id);

                if (match != null) {
                    // Esta entrada existe, por lo que se remueve del mapa de remotos
                    remotos.remove(filaActual.id);

                    // Crear uri del item
                    Uri updateUri = TiposContactos.construirUri(filaActual.id);

                    /*
                    Aquí se aplica la resolución de conflictos de modificaciones de un mismo recurso
                    tanto en el servidro como en la app. Quién tenga la versión más actual, será tomado
                    como preponderante
                     */
                    if (match.compararCon(filaActual)) {
                        int flag = match.esMasReciente(filaActual);
                        if (flag > 0) {
                            Log.d(TAG, "Programar actualización  del tipo de contacto " + updateUri);

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
                    Uri deleteUri = TiposContactos.construirUri(filaActual.id);
                    Log.i(TAG, "Programar Eliminación del tipo de contacto " + deleteUri);
                    ops.add(ContentProviderOperation.newDelete(deleteUri).build());
                }
            }
            c.close();
        }

        // Insertar los items resultantes ya que se asume que no existen en local
        for (TipoContacto tipoContacto : remotos.values()) {
            Log.d(TAG, "Programar Inserción de un nuevo tipo de contacto con ID = " + tipoContacto.id);
            ops.add(construirOperacionInsert(tipoContacto));
        }
    }

    private ContentProviderOperation construirOperacionInsert(TipoContacto nuevo) {
        return ContentProviderOperation.newInsert(TiposContactos.URI_CONTENIDO)
                .withValue(TiposContactos.ID, nuevo.id)
                .withValue(TiposContactos.NOMBRE, nuevo.nombre)
                .withValue(TiposContactos.DESCRIPCION, nuevo.descripcion)
                .withValue(TiposContactos.VERSION, nuevo.version)
                .withValue(TiposContactos.INSERTADO, 0)
                .build();
    }

    private ContentProviderOperation construirOperacionUpdate(TipoContacto match, Uri updateUri) {
        return ContentProviderOperation.newUpdate(updateUri)
                .withValue(TiposContactos.ID, match.id)
                .withValue(TiposContactos.NOMBRE, match.nombre)
                .withValue(TiposContactos.DESCRIPCION, match.descripcion)
                .withValue(TiposContactos.VERSION, match.version)
                .withValue(TiposContactos.MODIFICADO, match.modificado)
                .build();
    }

    /**
     * Convierte una fila de un Cursor en un nuevo Cliente
     *
     * @param c cursor
     * @return objeto cliente
     */
    private TipoContacto deCursorATipoContacto(Cursor c) {
        return new TipoContacto(
                c.getString(Consulta.ID),
                c.getString(Consulta.NOMBRE),
                c.getString(Consulta.DESCRIPCION),
                c.getString(Consulta.VERSION),
                c.getInt(Consulta.MODIFICADO)
        );
    }
}
