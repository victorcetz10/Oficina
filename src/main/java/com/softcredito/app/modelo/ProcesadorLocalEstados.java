package com.softcredito.app.modelo;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.google.gson.Gson;
import com.softcredito.app.provider.Contract.Estados;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Elemento que controla la transformación de JSON a POJO y viceversa
 */
public class ProcesadorLocalEstados {

    private static final String TAG = ProcesadorLocalEstados.class.getSimpleName();

    private interface Consulta{

        // Proyección para la consulta
        String[] PROYECCION = {
                Estados.ID,
                Estados.ESTADO,
                Estados.ID_PAIS,
                Estados.CLAVE,
                Estados.RIESGO,
                Estados.CLAVE_BURO,
                Estados.VERSION,
                Estados.MODIFICADO
        };

        // Indices de columnas
        int ID = 0;
        int ESTADO = 1;
        int ID_PAIS = 2;
        int CLAVE = 3;
        int RIESGO = 4;
        int CLAVE_BURO = 5;
        int VERSION = 6;
        int MODIFICADO = 7;

    }

    // Mapa para filtrar solo los elementos a sincronizar
    private HashMap<String, Estado> remotos = new HashMap<>();

    // Conversor JSON
    private Gson gson = new Gson();

    public ProcesadorLocalEstados() {
    }

    public void procesar(JSONArray arrayJson) {
        // Añadir elementos convertidos al mapa de los remotos
        for (Estado item : gson
                .fromJson(arrayJson.toString(), Estado[].class)) {
            item.aplicarSanidad();
            remotos.put(item.id, item);//Se agregan al mapa para poder procesarlos despues
        }
    }

    public void procesarOperaciones(ArrayList<ContentProviderOperation> ops, ContentResolver resolver) {
        // Consultar datos locales
        Cursor c = resolver.query(Estados.URI_CONTENIDO,
                Consulta.PROYECCION,
                Estados.INSERTADO + "=?",
                new String[]{"0"}, null);

        if (c != null) {

            while (c.moveToNext()) {

                // Convertir fila del cursor en objeto
                Estado filaActual = deCursorAEstado(c);

                // Buscar si el dato local se encuentra en el mapa de remotos
                Estado match = remotos.get(filaActual.id);

                if (match != null) {
                    // Esta entrada existe, por lo que se remueve del mapa de remotos
                    remotos.remove(filaActual.id);

                    // Crear uri del item
                    Uri updateUri = Estados.construirUri(filaActual.id);

                    /*
                    Aquí se aplica la resolución de conflictos de modificaciones de un mismo recurso
                    tanto en el servidro como en la app. Quién tenga la versión más actual, será tomado
                    como preponderante
                     */
                    if (match.compararCon(filaActual)) {
                        int flag = match.esMasReciente(filaActual);
                        if (flag > 0) {
                            Log.d(TAG, "Programar actualización  del estado " + updateUri);

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
                    Uri deleteUri = Estados.construirUri(filaActual.id);
                    Log.i(TAG, "Programar Eliminación del estado " + deleteUri);
                    ops.add(ContentProviderOperation.newDelete(deleteUri).build());
                }
            }
            c.close();
        }

        // Insertar los items resultantes ya que se asume que no existen en local
        for (Estado estado : remotos.values()) {
            Log.d(TAG, "Programar Inserción de un nuevo estado con ID = " + estado.id);
            ops.add(construirOperacionInsert(estado));
        }
    }

    private ContentProviderOperation construirOperacionInsert(Estado nuevo) {
        return ContentProviderOperation.newInsert(Estados.URI_CONTENIDO)
                .withValue(Estados.ID, nuevo.id)
                .withValue(Estados.ESTADO, nuevo.estado)
                .withValue(Estados.ID_PAIS, nuevo.id_pais)
                .withValue(Estados.CLAVE, nuevo.clave)
                .withValue(Estados.RIESGO, nuevo.riesgo)
                .withValue(Estados.CLAVE_BURO, nuevo.clave_buro)
                .withValue(Estados.VERSION, nuevo.version)
                .withValue(Estados.INSERTADO, 0)
                .build();
    }

    private ContentProviderOperation construirOperacionUpdate(Estado match, Uri updateUri) {
        return ContentProviderOperation.newUpdate(updateUri)
                .withValue(Estados.ID, match.id)
                .withValue(Estados.ESTADO, match.estado)
                .withValue(Estados.ID_PAIS, match.id_pais)
                .withValue(Estados.CLAVE, match.clave)
                .withValue(Estados.RIESGO, match.riesgo)
                .withValue(Estados.CLAVE_BURO, match.clave_buro)
                .withValue(Estados.VERSION, match.version)
                .withValue(Estados.MODIFICADO, match.modificado)
                .build();
    }

    /**
     * Convierte una fila de un Cursor en un nuevo Cliente
     *
     * @param c cursor
     * @return objeto cliente
     */
    private Estado deCursorAEstado(Cursor c) {
        return new Estado(
                c.getString(Consulta.ID),
                c.getString(Consulta.ESTADO),
                c.getInt(Consulta.ID_PAIS),
                c.getString(Consulta.CLAVE),
                c.getInt(Consulta.RIESGO),
                c.getString(Consulta.CLAVE_BURO),
                c.getString(Consulta.VERSION),
                c.getInt(Consulta.MODIFICADO)
        );
    }
}
