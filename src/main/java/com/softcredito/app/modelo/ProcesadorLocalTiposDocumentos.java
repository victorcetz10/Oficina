package com.softcredito.app.modelo;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.google.gson.Gson;
import com.softcredito.app.provider.Contract.TiposDocumentos;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Elemento que controla la transformación de JSON a POJO y viceversa
 */
public class ProcesadorLocalTiposDocumentos {

    private static final String TAG = ProcesadorLocalTiposDocumentos.class.getSimpleName();

    private interface Consulta{

        // Proyección para la consulta
        String[] PROYECCION = {
                TiposDocumentos.ID,
                TiposDocumentos.NOMBRE,
                TiposDocumentos.DESCRIPCION,
                TiposDocumentos.VERSION,
                TiposDocumentos.MODIFICADO
        };

        // Indices de columnas
        int ID = 0;
        int NOMBRE = 1;
        int DESCRIPCION = 2;
        int VERSION = 3;
        int MODIFICADO = 4;

    }

    // Mapa para filtrar solo los elementos a sincronizar
    private HashMap<String, TipoDocumento> remotos = new HashMap<>();

    // Conversor JSON
    private Gson gson = new Gson();

    public ProcesadorLocalTiposDocumentos() {
    }

    public void procesar(JSONArray arrayJson) {
        // Añadir elementos convertidos al mapa de los remotos
        for (TipoDocumento item : gson
                .fromJson(arrayJson.toString(), TipoDocumento[].class)) {
            item.aplicarSanidad();
            remotos.put(item.id, item);//Se agregan al mapa para poder procesarlos despues
        }
    }

    public void procesarOperaciones(ArrayList<ContentProviderOperation> ops, ContentResolver resolver) {
        // Consultar datos locales
        Cursor c = resolver.query(TiposDocumentos.URI_CONTENIDO,
                Consulta.PROYECCION,
                TiposDocumentos.INSERTADO + "=?",
                new String[]{"0"}, null);

        if (c != null) {

            while (c.moveToNext()) {

                // Convertir fila del cursor en objeto
                TipoDocumento filaActual = deCursorATipoDocumento(c);

                // Buscar si el dato local se encuentra en el mapa de remotos
                TipoDocumento match = remotos.get(filaActual.id);

                if (match != null) {
                    // Esta entrada existe, por lo que se remueve del mapa de remotos
                    remotos.remove(filaActual.id);

                    // Crear uri del item
                    Uri updateUri = TiposDocumentos.construirUri(filaActual.id);

                    /*
                    Aquí se aplica la resolución de conflictos de modificaciones de un mismo recurso
                    tanto en el servidro como en la app. Quién tenga la versión más actual, será tomado
                    como preponderante
                     */
                    if (match.compararCon(filaActual)) {
                        int flag = match.esMasReciente(filaActual);
                        if (flag > 0) {
                            Log.d(TAG, "Programar actualización  del tipo de documento " + updateUri);

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
                    Uri deleteUri = TiposDocumentos.construirUri(filaActual.id);
                    Log.i(TAG, "Programar Eliminación del tipo de documento " + deleteUri);
                    ops.add(ContentProviderOperation.newDelete(deleteUri).build());
                }
            }
            c.close();
        }

        // Insertar los items resultantes ya que se asume que no existen en local
        for (TipoDocumento tipoDocumento : remotos.values()) {
            Log.d(TAG, "Programar Inserción de un nuevo tipo de documento con ID = " + tipoDocumento.id);
            ops.add(construirOperacionInsert(tipoDocumento));
        }
    }

    private ContentProviderOperation construirOperacionInsert(TipoDocumento nuevo) {
        return ContentProviderOperation.newInsert(TiposDocumentos.URI_CONTENIDO)
                .withValue(TiposDocumentos.ID, nuevo.id)
                .withValue(TiposDocumentos.NOMBRE, nuevo.nombre)
                .withValue(TiposDocumentos.DESCRIPCION, nuevo.descripcion)
                .withValue(TiposDocumentos.VERSION, nuevo.version)
                .withValue(TiposDocumentos.INSERTADO, 0)
                .build();
    }

    private ContentProviderOperation construirOperacionUpdate(TipoDocumento match, Uri updateUri) {
        return ContentProviderOperation.newUpdate(updateUri)
                .withValue(TiposDocumentos.ID, match.id)
                .withValue(TiposDocumentos.NOMBRE, match.nombre)
                .withValue(TiposDocumentos.DESCRIPCION, match.descripcion)
                .withValue(TiposDocumentos.VERSION, match.version)
                .withValue(TiposDocumentos.MODIFICADO, match.modificado)
                .build();
    }

    /**
     * Convierte una fila de un Cursor en un nuevo tipo de documento
     *
     * @param c cursor
     * @return objeto tipo de documento
     */
    private TipoDocumento deCursorATipoDocumento(Cursor c) {
        return new TipoDocumento(
                c.getString(Consulta.ID),
                c.getString(Consulta.NOMBRE),
                c.getString(Consulta.DESCRIPCION),
                c.getString(Consulta.VERSION),
                c.getInt(Consulta.MODIFICADO)
        );
    }
}
