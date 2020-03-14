package com.softcredito.app.modelo;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.google.gson.Gson;

import com.softcredito.app.provider.Contract.Bancos;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Elemento que controla la transformación de JSON a POJO y viceversa
 */
public class ProcesadorLocalBancos {

    private static final String TAG = ProcesadorLocalBancos.class.getSimpleName();

    private interface Consulta{

        // Proyección para la consulta
        String[] PROYECCION = {
                Bancos.ID,
                Bancos.INSTITUCION,
                Bancos.SUCURSAL,
                Bancos.CLABE,
                Bancos.NUMERO_CUENTA,
                Bancos.VERSION,
                Bancos.MODIFICADO
        };

        // Indices de columnas
        int ID = 0;
        int INSTITUCION = 1;
        int SUCURSAL = 2;
        int CLABE = 3;
        int NUMERO_CUENTA = 4;
        int VERSION = 5;
        int MODIFICADO = 6;

    }

    // Mapa para filtrar solo los elementos a sincronizar
    private HashMap<String, Banco> remotos = new HashMap<>();

    // Conversor JSON
    private Gson gson = new Gson();

    public ProcesadorLocalBancos() {
    }

    public void procesar(JSONArray arrayJson) {
        // Añadir elementos convertidos al mapa de los remotos
        for (Banco item : gson
                .fromJson(arrayJson.toString(), Banco[].class)) {
            item.aplicarSanidad();
            remotos.put(item.id, item);//Se agregan al mapa para poder procesarlos despues
        }
    }

    public void procesarOperaciones(ArrayList<ContentProviderOperation> ops, ContentResolver resolver) {
        // Consultar datos locales
        Cursor c = resolver.query(Bancos.URI_CONTENIDO,
                Consulta.PROYECCION,
                Bancos.INSERTADO + "=?",
                new String[]{"0"}, null);

        if (c != null) {

            while (c.moveToNext()) {

                // Convertir fila del cursor en objeto
                Banco filaActual = deCursorABanco(c);

                // Buscar si el dato local se encuentra en el mapa de remotos
                Banco match = remotos.get(filaActual.id);

                if (match != null) {
                    // Esta entrada existe, por lo que se remueve del mapa de remotos
                    remotos.remove(filaActual.id);

                    // Crear uri del item
                    Uri updateUri = Bancos.construirUri(filaActual.id);

                    /*
                    Aquí se aplica la resolución de conflictos de modificaciones de un mismo recurso
                    tanto en el servidro como en la app. Quién tenga la versión más actual, será tomado
                    como preponderante
                     */
                    if (match.compararCon(filaActual)) {
                        int flag = match.esMasReciente(filaActual);
                        if (flag > 0) {
                            Log.d(TAG, "Programar actualización  del banco " + updateUri);

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
                    Uri deleteUri = Bancos.construirUri(filaActual.id);
                    Log.i(TAG, "Programar Eliminación del banco " + deleteUri);
                    ops.add(ContentProviderOperation.newDelete(deleteUri).build());
                }
            }
            c.close();
        }

        // Insertar los items resultantes ya que se asume que no existen en local
        for (Banco banco : remotos.values()) {
            Log.d(TAG, "Programar Inserción de un nuevo banco con ID = " + banco.id);
            ops.add(construirOperacionInsert(banco));
        }
    }

    private ContentProviderOperation construirOperacionInsert(Banco nuevo) {
        return ContentProviderOperation.newInsert(Bancos.URI_CONTENIDO)
                .withValue(Bancos.ID, nuevo.id)
                .withValue(Bancos.INSTITUCION, nuevo.institucion)
                .withValue(Bancos.SUCURSAL, nuevo.sucursal)
                .withValue(Bancos.CLABE, nuevo.clabe)
                .withValue(Bancos.NUMERO_CUENTA, nuevo.numero_cuenta)
                .withValue(Bancos.VERSION, nuevo.version)
                .withValue(Bancos.INSERTADO, 0)
                .build();
    }

    private ContentProviderOperation construirOperacionUpdate(Banco match, Uri updateUri) {
        return ContentProviderOperation.newUpdate(updateUri)
                .withValue(Bancos.ID, match.id)
                .withValue(Bancos.INSTITUCION, match.institucion)
                .withValue(Bancos.SUCURSAL, match.sucursal)
                .withValue(Bancos.CLABE, match.clabe)
                .withValue(Bancos.NUMERO_CUENTA, match.numero_cuenta)
                .withValue(Bancos.VERSION, match.version)
                .withValue(Bancos.MODIFICADO, match.modificado)
                .build();
    }

    /**
     * Convierte una fila de un Cursor en un nuevo Banco
     *
     * @param c cursor
     * @return objeto banco
     */
    private Banco deCursorABanco(Cursor c) {
        return new Banco(
                c.getString(Consulta.ID),
                c.getString(Consulta.INSTITUCION),
                c.getString(Consulta.SUCURSAL),
                c.getString(Consulta.CLABE),
                c.getString(Consulta.NUMERO_CUENTA),
                c.getString(Consulta.VERSION),
                c.getInt(Consulta.MODIFICADO)
        );
    }
}
