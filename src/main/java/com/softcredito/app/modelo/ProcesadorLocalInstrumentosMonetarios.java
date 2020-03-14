package com.softcredito.app.modelo;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.google.gson.Gson;
import com.softcredito.app.provider.Contract.InstrumentosMonetarios;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Elemento que controla la transformación de JSON a POJO y viceversa
 */
public class ProcesadorLocalInstrumentosMonetarios {

    private static final String TAG = ProcesadorLocalInstrumentosMonetarios.class.getSimpleName();

    private interface Consulta{

        // Proyección para la consulta
        String[] PROYECCION = {
                InstrumentosMonetarios.ID,
                InstrumentosMonetarios.CLAVE,
                InstrumentosMonetarios.DESCRIPCION,
                InstrumentosMonetarios.VERSION,
                InstrumentosMonetarios.MODIFICADO
        };

        // Indices de columnas
        int ID = 0;
        int CLAVE = 1;
        int DESCRIPCION = 2;
        int VERSION = 3;
        int MODIFICADO = 4;

    }

    // Mapa para filtrar solo los elementos a sincronizar
    private HashMap<String, InstrumentoMonetario> remotos = new HashMap<>();

    // Conversor JSON
    private Gson gson = new Gson();

    public ProcesadorLocalInstrumentosMonetarios() {
    }

    public void procesar(JSONArray arrayJson) {
        // Añadir elementos convertidos al mapa de los remotos
        for (InstrumentoMonetario item : gson
                .fromJson(arrayJson.toString(), InstrumentoMonetario[].class)) {
            item.aplicarSanidad();
            remotos.put(item.id, item);//Se agregan al mapa para poder procesarlos despues
        }
    }

    public void procesarOperaciones(ArrayList<ContentProviderOperation> ops, ContentResolver resolver) {
        // Consultar datos locales
        Cursor c = resolver.query(InstrumentosMonetarios.URI_CONTENIDO,
                Consulta.PROYECCION,
                InstrumentosMonetarios.INSERTADO + "=?",
                new String[]{"0"}, null);

        if (c != null) {

            while (c.moveToNext()) {

                // Convertir fila del cursor en objeto
                InstrumentoMonetario filaActual = deCursorAInstrumentoMonetario(c);

                // Buscar si el dato local se encuentra en el mapa de remotos
                InstrumentoMonetario match = remotos.get(filaActual.id);

                if (match != null) {
                    // Esta entrada existe, por lo que se remueve del mapa de remotos
                    remotos.remove(filaActual.id);

                    // Crear uri del item
                    Uri updateUri = InstrumentosMonetarios.construirUri(filaActual.id);

                    /*
                    Aquí se aplica la resolución de conflictos de modificaciones de un mismo recurso
                    tanto en el servidro como en la app. Quién tenga la versión más actual, será tomado
                    como preponderante
                     */
                    if (match.compararCon(filaActual)) {
                        int flag = match.esMasReciente(filaActual);
                        if (flag > 0) {
                            Log.d(TAG, "Programar actualización  del instrumento monetario " + updateUri);

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
                    Uri deleteUri = InstrumentosMonetarios.construirUri(filaActual.id);
                    Log.i(TAG, "Programar Eliminación del instrumento monetario " + deleteUri);
                    ops.add(ContentProviderOperation.newDelete(deleteUri).build());
                }
            }
            c.close();
        }

        // Insertar los items resultantes ya que se asume que no existen en local
        for (InstrumentoMonetario instrumentoMonetario : remotos.values()) {
            Log.d(TAG, "Programar Inserción de un nuevo instrumento monetario con ID = " + instrumentoMonetario.id);
            ops.add(construirOperacionInsert(instrumentoMonetario));
        }
    }

    private ContentProviderOperation construirOperacionInsert(InstrumentoMonetario nuevo) {
        return ContentProviderOperation.newInsert(InstrumentosMonetarios.URI_CONTENIDO)
                .withValue(InstrumentosMonetarios.ID, nuevo.id)
                .withValue(InstrumentosMonetarios.CLAVE, nuevo.clave)
                .withValue(InstrumentosMonetarios.DESCRIPCION, nuevo.descripcion)
                .withValue(InstrumentosMonetarios.VERSION, nuevo.version)
                .withValue(InstrumentosMonetarios.INSERTADO, 0)
                .build();
    }

    private ContentProviderOperation construirOperacionUpdate(InstrumentoMonetario match, Uri updateUri) {
        return ContentProviderOperation.newUpdate(updateUri)
                .withValue(InstrumentosMonetarios.ID, match.id)
                .withValue(InstrumentosMonetarios.CLAVE, match.clave)
                .withValue(InstrumentosMonetarios.DESCRIPCION, match.descripcion)
                .withValue(InstrumentosMonetarios.VERSION, match.version)
                .withValue(InstrumentosMonetarios.MODIFICADO, match.modificado)
                .build();
    }

    /**
     * Convierte una fila de un Cursor en un nuevo InstrumentoMonetario
     *
     * @param c cursor
     * @return objeto instrumentomonetario
     */
    private InstrumentoMonetario deCursorAInstrumentoMonetario(Cursor c) {
        return new InstrumentoMonetario(
                c.getString(Consulta.ID),
                c.getString(Consulta.CLAVE),
                c.getString(Consulta.DESCRIPCION),
                c.getString(Consulta.VERSION),
                c.getInt(Consulta.MODIFICADO)
        );
    }
}
