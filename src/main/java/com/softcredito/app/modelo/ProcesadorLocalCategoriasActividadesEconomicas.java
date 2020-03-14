package com.softcredito.app.modelo;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.google.gson.Gson;
import com.softcredito.app.provider.Contract.CategoriasActividadesEconomicas;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Elemento que controla la transformación de JSON a POJO y viceversa
 */
public class ProcesadorLocalCategoriasActividadesEconomicas {

    private static final String TAG = ProcesadorLocalCategoriasActividadesEconomicas.class.getSimpleName();

    private interface Consulta{

        // Proyección para la consulta
        String[] PROYECCION = {
                CategoriasActividadesEconomicas.ID,
                CategoriasActividadesEconomicas.NOMBRE,
                CategoriasActividadesEconomicas.DESCRIPCION,
                CategoriasActividadesEconomicas.VERSION,
                CategoriasActividadesEconomicas.MODIFICADO
        };

        // Indices de columnas
        int ID = 0;
        int NOMBRE = 1;
        int DESCRIPCION = 2;
        int VERSION = 3;
        int MODIFICADO = 4;

    }

    // Mapa para filtrar solo los elementos a sincronizar
    private HashMap<String, CategoriaActividadEconomica> remotos = new HashMap<>();

    // Conversor JSON
    private Gson gson = new Gson();

    public ProcesadorLocalCategoriasActividadesEconomicas() {
    }

    public void procesar(JSONArray arrayJson) {
        // Añadir elementos convertidos al mapa de los remotos
        for (CategoriaActividadEconomica item : gson
                .fromJson(arrayJson.toString(), CategoriaActividadEconomica[].class)) {
            item.aplicarSanidad();
            remotos.put(item.id, item);//Se agregan al mapa para poder procesarlos despues
        }
    }

    public void procesarOperaciones(ArrayList<ContentProviderOperation> ops, ContentResolver resolver) {
        // Consultar datos locales
        Cursor c = resolver.query(CategoriasActividadesEconomicas.URI_CONTENIDO,
                Consulta.PROYECCION,
                CategoriasActividadesEconomicas.INSERTADO + "=?",
                new String[]{"0"}, null);

        if (c != null) {

            while (c.moveToNext()) {

                // Convertir fila del cursor en objeto
                CategoriaActividadEconomica filaActual = deCursorACategoriaActividadEconomica(c);

                // Buscar si el dato local se encuentra en el mapa de remotos
                CategoriaActividadEconomica match = remotos.get(filaActual.id);

                if (match != null) {
                    // Esta entrada existe, por lo que se remueve del mapa de remotos
                    remotos.remove(filaActual.id);

                    // Crear uri del item
                    Uri updateUri = CategoriasActividadesEconomicas.construirUri(filaActual.id);

                    /*
                    Aquí se aplica la resolución de conflictos de modificaciones de un mismo recurso
                    tanto en el servidro como en la app. Quién tenga la versión más actual, será tomado
                    como preponderante
                     */
                    if (match.compararCon(filaActual)) {
                        int flag = match.esMasReciente(filaActual);
                        if (flag > 0) {
                            Log.d(TAG, "Programar actualización  de la categoria de actividad economica " + updateUri);

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
                    Uri deleteUri = CategoriasActividadesEconomicas.construirUri(filaActual.id);
                    Log.i(TAG, "Programar Eliminación de la categoria de actividad economica " + deleteUri);
                    ops.add(ContentProviderOperation.newDelete(deleteUri).build());
                }
            }
            c.close();
        }

        // Insertar los items resultantes ya que se asume que no existen en local
        for (CategoriaActividadEconomica estado_civil : remotos.values()) {
            Log.d(TAG, "Programar Inserción de una nueva categoria de actividad economica con ID = " + estado_civil.id);
            ops.add(construirOperacionInsert(estado_civil));
        }
    }

    private ContentProviderOperation construirOperacionInsert(CategoriaActividadEconomica nuevo) {
        return ContentProviderOperation.newInsert(CategoriasActividadesEconomicas.URI_CONTENIDO)
                .withValue(CategoriasActividadesEconomicas.ID, nuevo.id)
                .withValue(CategoriasActividadesEconomicas.NOMBRE, nuevo.nombre)
                .withValue(CategoriasActividadesEconomicas.DESCRIPCION, nuevo.descripcion)
                .withValue(CategoriasActividadesEconomicas.VERSION, nuevo.version)
                .withValue(CategoriasActividadesEconomicas.INSERTADO, 0)
                .build();
    }

    private ContentProviderOperation construirOperacionUpdate(CategoriaActividadEconomica match, Uri updateUri) {
        return ContentProviderOperation.newUpdate(updateUri)
                .withValue(CategoriasActividadesEconomicas.ID, match.id)
                .withValue(CategoriasActividadesEconomicas.NOMBRE, match.nombre)
                .withValue(CategoriasActividadesEconomicas.DESCRIPCION, match.descripcion)
                .withValue(CategoriasActividadesEconomicas.VERSION, match.version)
                .withValue(CategoriasActividadesEconomicas.MODIFICADO, match.modificado)
                .build();
    }

    /**
     * Convierte una fila de un Cursor en un nuevo Cliente
     *
     * @param c cursor
     * @return objeto cliente
     */
    private CategoriaActividadEconomica deCursorACategoriaActividadEconomica(Cursor c) {
        return new CategoriaActividadEconomica(
                c.getString(Consulta.ID),
                c.getString(Consulta.NOMBRE),
                c.getString(Consulta.DESCRIPCION),
                c.getString(Consulta.VERSION),
                c.getInt(Consulta.MODIFICADO)
        );
    }
}
