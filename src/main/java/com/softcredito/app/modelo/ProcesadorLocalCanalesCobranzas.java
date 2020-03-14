package com.softcredito.app.modelo;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.google.gson.Gson;
import com.softcredito.app.provider.Contract.CanalesCobranzas;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Elemento que controla la transformación de JSON a POJO y viceversa
 */
public class ProcesadorLocalCanalesCobranzas {

    private static final String TAG = ProcesadorLocalCanalesCobranzas.class.getSimpleName();

    private interface Consulta{

        // Proyección para la consulta
        String[] PROYECCION = {
                CanalesCobranzas.ID,
                CanalesCobranzas.CLAVE,
                CanalesCobranzas.NOMBRE,
                CanalesCobranzas.REFERENCIA,
                CanalesCobranzas.VERSION,
                CanalesCobranzas.MODIFICADO
        };

        // Indices de columnas
        int ID = 0;
        int CLAVE = 1;
        int NOMBRE = 2;
        int REFEENCIA = 3;
        int VERSION = 4;
        int MODIFICADO = 5;

    }

    // Mapa para filtrar solo los elementos a sincronizar
    private HashMap<String, CanalCobranza> remotos = new HashMap<>();

    // Conversor JSON
    private Gson gson = new Gson();

    public ProcesadorLocalCanalesCobranzas() {
    }

    public void procesar(JSONArray arrayJson) {
        // Añadir elementos convertidos al mapa de los remotos
        for (CanalCobranza item : gson
                .fromJson(arrayJson.toString(), CanalCobranza[].class)) {
            item.aplicarSanidad();
            remotos.put(item.id, item);//Se agregan al mapa para poder procesarlos despues
        }
    }

    public void procesarOperaciones(ArrayList<ContentProviderOperation> ops, ContentResolver resolver) {
        // Consultar datos locales
        Cursor c = resolver.query(CanalesCobranzas.URI_CONTENIDO,
                Consulta.PROYECCION,
                CanalesCobranzas.INSERTADO + "=?",
                new String[]{"0"}, null);

        if (c != null) {

            while (c.moveToNext()) {

                // Convertir fila del cursor en objeto
                CanalCobranza filaActual = deCursorACanalCobranza(c);

                // Buscar si el dato local se encuentra en el mapa de remotos
                CanalCobranza match = remotos.get(filaActual.id);

                if (match != null) {
                    // Esta entrada existe, por lo que se remueve del mapa de remotos
                    remotos.remove(filaActual.id);

                    // Crear uri del item
                    Uri updateUri = CanalesCobranzas.construirUri(filaActual.id);

                    /*
                    Aquí se aplica la resolución de conflictos de modificaciones de un mismo recurso
                    tanto en el servidro como en la app. Quién tenga la versión más actual, será tomado
                    como preponderante
                     */
                    if (match.compararCon(filaActual)) {
                        int flag = match.esMasReciente(filaActual);
                        if (flag > 0) {
                            Log.d(TAG, "Programar actualización  del canal de cobranza " + updateUri);

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
                    Uri deleteUri = CanalesCobranzas.construirUri(filaActual.id);
                    Log.i(TAG, "Programar Eliminación del canal de cobranza " + deleteUri);
                    ops.add(ContentProviderOperation.newDelete(deleteUri).build());
                }
            }
            c.close();
        }

        // Insertar los items resultantes ya que se asume que no existen en local
        for (CanalCobranza canalCobranza : remotos.values()) {
            Log.d(TAG, "Programar Inserción de un nuevo canal de cobranza con ID = " + canalCobranza.id);
            ops.add(construirOperacionInsert(canalCobranza));
        }
    }

    private ContentProviderOperation construirOperacionInsert(CanalCobranza nuevo) {
        return ContentProviderOperation.newInsert(CanalesCobranzas.URI_CONTENIDO)
                .withValue(CanalesCobranzas.ID, nuevo.id)
                .withValue(CanalesCobranzas.CLAVE, nuevo.clave)
                .withValue(CanalesCobranzas.NOMBRE, nuevo.nombre)
                .withValue(CanalesCobranzas.REFERENCIA, nuevo.referencia)
                .withValue(CanalesCobranzas.VERSION, nuevo.version)
                .withValue(CanalesCobranzas.INSERTADO, 0)
                .build();
    }

    private ContentProviderOperation construirOperacionUpdate(CanalCobranza match, Uri updateUri) {
        return ContentProviderOperation.newUpdate(updateUri)
                .withValue(CanalesCobranzas.ID, match.id)
                .withValue(CanalesCobranzas.CLAVE, match.clave)
                .withValue(CanalesCobranzas.NOMBRE, match.nombre)
                .withValue(CanalesCobranzas.REFERENCIA, match.referencia)
                .withValue(CanalesCobranzas.VERSION, match.version)
                .withValue(CanalesCobranzas.MODIFICADO, match.modificado)
                .build();
    }

    /**
     * Convierte una fila de un Cursor en un nuevo CanalCobranza
     *
     * @param c cursor
     * @return objeto canalcobranza
     */
    private CanalCobranza deCursorACanalCobranza(Cursor c) {
        return new CanalCobranza(
                c.getString(Consulta.ID),
                c.getString(Consulta.CLAVE),
                c.getString(Consulta.NOMBRE),
                c.getString(Consulta.REFEENCIA),
                c.getString(Consulta.VERSION),
                c.getInt(Consulta.MODIFICADO)
        );
    }
}
