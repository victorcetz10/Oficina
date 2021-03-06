package com.softcredito.app.modelo;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.google.gson.Gson;
import com.softcredito.app.provider.Contract.EstadosCiviles;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Elemento que controla la transformación de JSON a POJO y viceversa
 */
public class ProcesadorLocalEstadosCiviles {

    private static final String TAG = ProcesadorLocalEstadosCiviles.class.getSimpleName();

    private interface Consulta{

        // Proyección para la consulta
        String[] PROYECCION = {
                EstadosCiviles.ID,
                EstadosCiviles.ESTADO_CIVIL,
                EstadosCiviles.VERSION,
                EstadosCiviles.MODIFICADO
        };

        // Indices de columnas
        int ID = 0;
        int ESTADO_CIVIL = 1;
        int VERSION = 2;
        int MODIFICADO = 3;

    }

    // Mapa para filtrar solo los elementos a sincronizar
    private HashMap<String, EstadoCivil> remotos = new HashMap<>();

    // Conversor JSON
    private Gson gson = new Gson();

    public ProcesadorLocalEstadosCiviles() {
    }

    public void procesar(JSONArray arrayJson) {
        // Añadir elementos convertidos al mapa de los remotos
        for (EstadoCivil item : gson
                .fromJson(arrayJson.toString(), EstadoCivil[].class)) {
            item.aplicarSanidad();
            remotos.put(item.id, item);//Se agregan al mapa para poder procesarlos despues
        }
    }

    public void procesarOperaciones(ArrayList<ContentProviderOperation> ops, ContentResolver resolver) {
        // Consultar datos locales
        Cursor c = resolver.query(EstadosCiviles.URI_CONTENIDO,
                Consulta.PROYECCION,
                EstadosCiviles.INSERTADO + "=?",
                new String[]{"0"}, null);

        if (c != null) {

            while (c.moveToNext()) {

                // Convertir fila del cursor en objeto
                EstadoCivil filaActual = deCursorAEstadoCivil(c);

                // Buscar si el dato local se encuentra en el mapa de remotos
                EstadoCivil match = remotos.get(filaActual.id);

                if (match != null) {
                    // Esta entrada existe, por lo que se remueve del mapa de remotos
                    remotos.remove(filaActual.id);

                    // Crear uri del item
                    Uri updateUri = EstadosCiviles.construirUri(filaActual.id);

                    /*
                    Aquí se aplica la resolución de conflictos de modificaciones de un mismo recurso
                    tanto en el servidro como en la app. Quién tenga la versión más actual, será tomado
                    como preponderante
                     */
                    if (match.compararCon(filaActual)) {
                        int flag = match.esMasReciente(filaActual);
                        if (flag > 0) {
                            Log.d(TAG, "Programar actualización  del estado civil " + updateUri);

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
                    Uri deleteUri = EstadosCiviles.construirUri(filaActual.id);
                    Log.i(TAG, "Programar Eliminación del estado civil " + deleteUri);
                    ops.add(ContentProviderOperation.newDelete(deleteUri).build());
                }
            }
            c.close();
        }

        // Insertar los items resultantes ya que se asume que no existen en local
        for (EstadoCivil estado_civil : remotos.values()) {
            Log.d(TAG, "Programar Inserción de un nuevo estado civil con ID = " + estado_civil.id);
            ops.add(construirOperacionInsert(estado_civil));
        }
    }

    private ContentProviderOperation construirOperacionInsert(EstadoCivil nuevo) {
        return ContentProviderOperation.newInsert(EstadosCiviles.URI_CONTENIDO)
                .withValue(EstadosCiviles.ID, nuevo.id)
                .withValue(EstadosCiviles.ESTADO_CIVIL, nuevo.estado_civil)
                .withValue(EstadosCiviles.VERSION, nuevo.version)
                .withValue(EstadosCiviles.INSERTADO, 0)
                .build();
    }

    private ContentProviderOperation construirOperacionUpdate(EstadoCivil match, Uri updateUri) {
        return ContentProviderOperation.newUpdate(updateUri)
                .withValue(EstadosCiviles.ID, match.id)
                .withValue(EstadosCiviles.ESTADO_CIVIL, match.estado_civil)
                .withValue(EstadosCiviles.VERSION, match.version)
                .withValue(EstadosCiviles.MODIFICADO, match.modificado)
                .build();
    }

    /**
     * Convierte una fila de un Cursor en un nuevo Cliente
     *
     * @param c cursor
     * @return objeto cliente
     */
    private EstadoCivil deCursorAEstadoCivil(Cursor c) {
        return new EstadoCivil(
                c.getString(Consulta.ID),
                c.getString(Consulta.ESTADO_CIVIL),
                c.getString(Consulta.VERSION),
                c.getInt(Consulta.MODIFICADO)
        );
    }
}
