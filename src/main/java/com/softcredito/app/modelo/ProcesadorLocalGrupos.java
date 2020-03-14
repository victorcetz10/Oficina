package com.softcredito.app.modelo;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.google.gson.Gson;
import com.softcredito.app.provider.Contract.Grupos;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Elemento que controla la transformación de JSON a POJO y viceversa
 */
public class ProcesadorLocalGrupos {

    private static final String TAG = ProcesadorLocalGrupos.class.getSimpleName();

    private interface Consulta{

        // Proyección para la consulta
        String[] PROYECCION = {
                Grupos.ID,
                Grupos.CLAVE,
                Grupos.NOMBRE,
                Grupos.DESCRIPCION,
                Grupos.DIA_REUNION,
                Grupos.HORA_REUNION,
                Grupos.VERSION,
                Grupos.MODIFICADO
        };

        // Indices de columnas
        int ID = 0;
        int CLAVE = 1;
        int NOMBRE = 2;
        int DESCRIPCION = 3;
        int DIA_REUNION = 4;
        int HORA_REUNION = 5;
        int VERSION = 6;
        int MODIFICADO = 7;

    }

    // Mapa para filtrar solo los elementos a sincronizar
    private HashMap<String, Grupo> remotos = new HashMap<>();

    // Conversor JSON
    private Gson gson = new Gson();

    public ProcesadorLocalGrupos() {
    }

    public void procesar(JSONArray arrayJson) {
        // Añadir elementos convertidos al mapa de los remotos
        for (Grupo item : gson
                .fromJson(arrayJson.toString(), Grupo[].class)) {
            item.aplicarSanidad();
            remotos.put(item.id, item);//Se agregan al mapa para poder procesarlos despues
        }
    }

    public void procesarOperaciones(ArrayList<ContentProviderOperation> ops, ContentResolver resolver) {
        // Consultar datos locales
        Cursor c = resolver.query(Grupos.URI_CONTENIDO,
                Consulta.PROYECCION,
                Grupos.INSERTADO + "=?",
                new String[]{"0"}, null);

        if (c != null) {

            while (c.moveToNext()) {

                // Convertir fila del cursor en objeto
                Grupo filaActual = deCursorAGrupo(c);

                // Buscar si el dato local se encuentra en el mapa de remotos
                Grupo match = remotos.get(filaActual.id);

                if (match != null) {
                    // Esta entrada existe, por lo que se remueve del mapa de remotos
                    remotos.remove(filaActual.id);

                    // Crear uri del item
                    Uri updateUri = Grupos.construirUri(filaActual.id);

                    /*
                    Aquí se aplica la resolución de conflictos de modificaciones de un mismo recurso
                    tanto en el servidro como en la app. Quién tenga la versión más actual, será tomado
                    como preponderante
                     */
                    if (match.compararCon(filaActual)) {
                        int flag = match.esMasReciente(filaActual);
                        if (flag > 0) {
                            Log.d(TAG, "Programar actualización  del grupo " + updateUri);

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
                    Uri deleteUri = Grupos.construirUri(filaActual.id);
                    Log.i(TAG, "Programar Eliminación del grupo " + deleteUri);
                    ops.add(ContentProviderOperation.newDelete(deleteUri).build());
                }
            }
            c.close();
        }

        // Insertar los items resultantes ya que se asume que no existen en local
        for (Grupo grupo : remotos.values()) {
            Log.d(TAG, "Programar Inserción de un nuevo grupo con ID = " + grupo.id);
            ops.add(construirOperacionInsert(grupo));
        }
    }

    private ContentProviderOperation construirOperacionInsert(Grupo nuevo) {
        return ContentProviderOperation.newInsert(Grupos.URI_CONTENIDO)
                .withValue(Grupos.ID, nuevo.id)
                .withValue(Grupos.CLAVE, nuevo.clave)
                .withValue(Grupos.NOMBRE, nuevo.nombre)
                .withValue(Grupos.DESCRIPCION, nuevo.descripcion)
                .withValue(Grupos.DIA_REUNION, nuevo.dia_reunion)
                .withValue(Grupos.HORA_REUNION, nuevo.hora_reunion)
                .withValue(Grupos.VERSION, nuevo.version)
                .withValue(Grupos.INSERTADO, 0)
                .build();
    }

    private ContentProviderOperation construirOperacionUpdate(Grupo match, Uri updateUri) {
        return ContentProviderOperation.newUpdate(updateUri)
                .withValue(Grupos.ID, match.id)
                .withValue(Grupos.CLAVE, match.clave)
                .withValue(Grupos.NOMBRE, match.nombre)
                .withValue(Grupos.DESCRIPCION, match.descripcion)
                .withValue(Grupos.DIA_REUNION, match.dia_reunion)
                .withValue(Grupos.HORA_REUNION, match.hora_reunion)
                .withValue(Grupos.VERSION, match.version)
                .withValue(Grupos.MODIFICADO, match.modificado)
                .build();
    }

    /**
     * Convierte una fila de un Cursor en un nuevo Grupo
     *
     * @param c cursor
     * @return objeto grupo
     */
    private Grupo deCursorAGrupo(Cursor c) {
        return new Grupo(
                c.getString(Consulta.ID),
                c.getString(Consulta.CLAVE),
                c.getString(Consulta.NOMBRE),
                c.getString(Consulta.DESCRIPCION),
                c.getString(Consulta.DIA_REUNION),
                c.getString(Consulta.HORA_REUNION),
                c.getString(Consulta.VERSION),
                c.getInt(Consulta.MODIFICADO)
        );
    }
}
