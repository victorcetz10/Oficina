package com.softcredito.app.modelo;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.google.gson.Gson;
import com.softcredito.app.provider.Contract.Codigos;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Elemento que controla la transformación de JSON a POJO y viceversa
 */
public class ProcesadorLocalCodigos {

    private static final String TAG = ProcesadorLocalCodigos.class.getSimpleName();

    private interface Consulta{

        // Proyección para la consulta
        String[] PROYECCION = {
                Codigos.ID,
                Codigos.ID_ESTADO,
                Codigos.ID_MUNICIPIO,
                Codigos.CP,
                Codigos.ASENTAMIENTO,
                Codigos.TIPO,
                Codigos.VERSION,
                Codigos.MODIFICADO
        };

        // Indices de columnas
        int ID = 0;
        int ID_ESTADO = 1;
        int ID_MUNICIPIO = 2;
        int CP = 3;
        int ASENTAMIENTO = 4;
        int TIPO = 5;
        int VERSION = 6;
        int MODIFICADO = 7;

    }

    // Mapa para filtrar solo los elementos a sincronizar
    private HashMap<String, Codigo> remotos = new HashMap<>();

    // Conversor JSON
    private Gson gson = new Gson();

    public ProcesadorLocalCodigos() {
    }

    public void procesar(JSONArray arrayJson) {
        // Añadir elementos convertidos al mapa de los remotos
        for (Codigo item : gson
                .fromJson(arrayJson.toString(), Codigo[].class)) {
            item.aplicarSanidad();
            remotos.put(item.id, item);//Se agregan al mapa para poder procesarlos despues
        }
    }

    public void procesarOperaciones(ArrayList<ContentProviderOperation> ops, ContentResolver resolver) {
        // Consultar datos locales
        Cursor c = resolver.query(Codigos.URI_CONTENIDO,
                Consulta.PROYECCION,
                Codigos.INSERTADO + "=?",
                new String[]{"0"}, null);

        if (c != null) {

            while (c.moveToNext()) {

                // Convertir fila del cursor en objeto
                Codigo filaActual = deCursorACodigo(c);

                // Buscar si el dato local se encuentra en el mapa de remotos
                Codigo match = remotos.get(filaActual.id);

                if (match != null) {
                    // Esta entrada existe, por lo que se remueve del mapa de remotos
                    remotos.remove(filaActual.id);

                    // Crear uri del item
                    Uri updateUri = Codigos.construirUri(filaActual.id);

                    /*
                    Aquí se aplica la resolución de conflictos de modificaciones de un mismo recurso
                    tanto en el servidro como en la app. Quién tenga la versión más actual, será tomado
                    como preponderante
                     */
                    if (match.compararCon(filaActual)) {
                        int flag = match.esMasReciente(filaActual);
                        if (flag > 0) {
                            Log.d(TAG, "Programar actualización  del codigo " + updateUri);

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
                    Uri deleteUri = Codigos.construirUri(filaActual.id);
                    Log.i(TAG, "Programar Eliminación del codigo postal " + deleteUri);
                    ops.add(ContentProviderOperation.newDelete(deleteUri).build());
                }
            }
            c.close();
        }

        // Insertar los items resultantes ya que se asume que no existen en local
        for (Codigo codigo : remotos.values()) {
            Log.d(TAG, "Programar Inserción de un nuevo codigo con ID = " + codigo.id);
            ops.add(construirOperacionInsert(codigo));
        }
    }

    private ContentProviderOperation construirOperacionInsert(Codigo nuevo) {
        return ContentProviderOperation.newInsert(Codigos.URI_CONTENIDO)
                .withValue(Codigos.ID, nuevo.id)
                .withValue(Codigos.ID_ESTADO, nuevo.id_estado)
                .withValue(Codigos.ID_MUNICIPIO, nuevo.id_municipio)
                .withValue(Codigos.CP, nuevo.cp)
                .withValue(Codigos.ASENTAMIENTO, nuevo.asentamiento)
                .withValue(Codigos.TIPO, nuevo.tipo)
                .withValue(Codigos.VERSION, nuevo.version)
                .withValue(Codigos.INSERTADO, 0)
                .build();
    }

    private ContentProviderOperation construirOperacionUpdate(Codigo match, Uri updateUri) {
        return ContentProviderOperation.newUpdate(updateUri)
                .withValue(Codigos.ID, match.id)
                .withValue(Codigos.ID_ESTADO, match.id_estado)
                .withValue(Codigos.ID_MUNICIPIO, match.id_municipio)
                .withValue(Codigos.CP, match.cp)
                .withValue(Codigos.ASENTAMIENTO, match.asentamiento)
                .withValue(Codigos.TIPO, match.tipo)
                .withValue(Codigos.VERSION, match.version)
                .withValue(Codigos.MODIFICADO, match.modificado)
                .build();
    }

    /**
     * Convierte una fila de un Cursor en un nuevo Cliente
     *
     * @param c cursor
     * @return objeto cliente
     */
    private Codigo deCursorACodigo(Cursor c) {
        return new Codigo(
                c.getString(Consulta.ID),
                c.getInt(Consulta.ID_ESTADO),
                c.getInt(Consulta.ID_MUNICIPIO),
                c.getString(Consulta.CP),
                c.getString(Consulta.ASENTAMIENTO),
                c.getString(Consulta.TIPO),
                c.getString(Consulta.VERSION),
                c.getInt(Consulta.MODIFICADO)
        );
    }
}
