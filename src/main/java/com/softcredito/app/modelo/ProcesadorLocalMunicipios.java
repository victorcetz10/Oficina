package com.softcredito.app.modelo;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.google.gson.Gson;
import com.softcredito.app.provider.Contract.Municipios;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Elemento que controla la transformación de JSON a POJO y viceversa
 */
public class ProcesadorLocalMunicipios {

    private static final String TAG = ProcesadorLocalMunicipios.class.getSimpleName();

    private interface Consulta{

        // Proyección para la consulta
        String[] PROYECCION = {
                Municipios.ID,
                Municipios.MUNICIPIO,
                Municipios.ID_ESTADO,
                Municipios.VERSION,
                Municipios.MODIFICADO
        };

        // Indices de columnas
        int ID = 0;
        int MUNICIPIO = 1;
        int ID_ESTADO = 2;
        int VERSION = 3;
        int MODIFICADO = 4;

    }

    // Mapa para filtrar solo los elementos a sincronizar
    private HashMap<String, Municipio> remotos = new HashMap<>();

    // Conversor JSON
    private Gson gson = new Gson();

    public ProcesadorLocalMunicipios() {
    }

    public void procesar(JSONArray arrayJson) {
        // Añadir elementos convertidos al mapa de los remotos
        for (Municipio item : gson
                .fromJson(arrayJson.toString(), Municipio[].class)) {
            item.aplicarSanidad();
            remotos.put(item.id, item);//Se agregan al mapa para poder procesarlos despues
        }
    }

    public void procesarOperaciones(ArrayList<ContentProviderOperation> ops, ContentResolver resolver) {
        // Consultar datos locales
        Cursor c = resolver.query(Municipios.URI_CONTENIDO,
                Consulta.PROYECCION,
                Municipios.INSERTADO + "=?",
                new String[]{"0"}, null);

        if (c != null) {

            while (c.moveToNext()) {

                // Convertir fila del cursor en objeto
                Municipio filaActual = deCursorAMunicipio(c);

                // Buscar si el dato local se encuentra en el mapa de remotos
                Municipio match = remotos.get(filaActual.id);

                if (match != null) {
                    // Esta entrada existe, por lo que se remueve del mapa de remotos
                    remotos.remove(filaActual.id);

                    // Crear uri del item
                    Uri updateUri = Municipios.construirUri(filaActual.id);

                    /*
                    Aquí se aplica la resolución de conflictos de modificaciones de un mismo recurso
                    tanto en el servidro como en la app. Quién tenga la versión más actual, será tomado
                    como preponderante
                     */
                    if (match.compararCon(filaActual)) {
                        int flag = match.esMasReciente(filaActual);
                        if (flag > 0) {
                            Log.d(TAG, "Programar actualización  del municipio " + updateUri);

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
                    Uri deleteUri = Municipios.construirUri(filaActual.id);
                    Log.i(TAG, "Programar Eliminación del municipio " + deleteUri);
                    ops.add(ContentProviderOperation.newDelete(deleteUri).build());
                }
            }
            c.close();
        }

        // Insertar los items resultantes ya que se asume que no existen en local
        for (Municipio municipio : remotos.values()) {
            Log.d(TAG, "Programar Inserción de un nuevo estado con ID = " + municipio.id);
            ops.add(construirOperacionInsert(municipio));
        }
    }

    private ContentProviderOperation construirOperacionInsert(Municipio nuevo) {
        return ContentProviderOperation.newInsert(Municipios.URI_CONTENIDO)
                .withValue(Municipios.ID, nuevo.id)
                .withValue(Municipios.MUNICIPIO, nuevo.municipio)
                .withValue(Municipios.ID_ESTADO, nuevo.id_estado)
                .withValue(Municipios.VERSION, nuevo.version)
                .withValue(Municipios.INSERTADO, 0)
                .build();
    }

    private ContentProviderOperation construirOperacionUpdate(Municipio match, Uri updateUri) {
        return ContentProviderOperation.newUpdate(updateUri)
                .withValue(Municipios.ID, match.id)
                .withValue(Municipios.MUNICIPIO, match.municipio)
                .withValue(Municipios.ID_ESTADO, match.id_estado)
                .withValue(Municipios.VERSION, match.version)
                .withValue(Municipios.MODIFICADO, match.modificado)
                .build();
    }

    /**
     * Convierte una fila de un Cursor en un nuevo Cliente
     *
     * @param c cursor
     * @return objeto cliente
     */
    private Municipio deCursorAMunicipio(Cursor c) {
        return new Municipio(
                c.getString(Consulta.ID),
                c.getString(Consulta.MUNICIPIO),
                c.getInt(Consulta.ID_ESTADO),
                c.getString(Consulta.VERSION),
                c.getInt(Consulta.MODIFICADO)
        );
    }
}
