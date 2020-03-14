package com.softcredito.app.modelo;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.google.gson.Gson;
import com.softcredito.app.provider.Contract;
import com.softcredito.app.provider.Contract.BitacorasCreditoArchivos;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Elemento que controla la transformación de JSON a POJO y viceversa
 */
public class ProcesadorLocalBitacorasCreditoArchivos {

    private static final String TAG = ProcesadorLocalBitacorasCreditoArchivos.class.getSimpleName();

    private interface Consulta{

        // Proyección para la consulta
        String[] PROYECCION = {
                BitacorasCreditoArchivos.ID,
                BitacorasCreditoArchivos.ID_BITACORA_CREDITO,
                BitacorasCreditoArchivos.FECHA,
                BitacorasCreditoArchivos.NOMBRE,
                BitacorasCreditoArchivos.TIPO,
                BitacorasCreditoArchivos.RUTA,
                BitacorasCreditoArchivos.BODY,
                BitacorasCreditoArchivos.VERSION,
                BitacorasCreditoArchivos.MODIFICADO
        };

        // Indices de columnas
        int ID = 0;
        int ID_BITACORA_CREDITO = 1;
        int FECHA = 2;
        int NOMBRE = 3;
        int TIPO = 4;
        int RUTA = 5;
        int BODY = 6;
        int VERSION = 7;
        int MODIFICADO = 8;

    }

    // Mapa para filtrar solo los elementos a sincronizar
    private HashMap<String, BitacoraCreditoArchivo> remotos = new HashMap<>();

    // Conversor JSON
    private Gson gson = new Gson();

    public ProcesadorLocalBitacorasCreditoArchivos() {
    }

    public void procesar(JSONArray arrayJson) {
        // Añadir elementos convertidos al mapa de los remotos
        for (BitacoraCreditoArchivo item : gson
                .fromJson(arrayJson.toString(), BitacoraCreditoArchivo[].class)) {
            item.aplicarSanidad();
            remotos.put(item.id, item);//Se agregan al mapa para poder procesarlos despues
        }
    }

    public HashMap<String, String[]> procesarOperaciones(ArrayList<ContentProviderOperation> ops, ContentResolver resolver) {
        // Consultar datos locales
        Cursor c = resolver.query(BitacorasCreditoArchivos.URI_CONTENIDO,
                Consulta.PROYECCION,
                BitacorasCreditoArchivos.INSERTADO + "=?",
                new String[]{"0"}, null);

        if (c != null) {

            while (c.moveToNext()) {

                // Convertir fila del cursor en objeto
                BitacoraCreditoArchivo filaActual = deCursorABitacoraCreditoArchivo(c);

                // Buscar si el dato local se encuentra en el mapa de remotos
                BitacoraCreditoArchivo match = remotos.get(filaActual.id);

                if (match != null) {
                    // Esta entrada existe, por lo que se remueve del mapa de remotos
                    remotos.remove(filaActual.id);

                    // Crear uri del item
                    Uri updateUri = BitacorasCreditoArchivos.construirUri(filaActual.id);

                    /*
                    Aquí se aplica la resolución de conflictos de modificaciones de un mismo recurso
                    tanto en el servidro como en la app. Quién tenga la versión más actual, será tomado
                    como preponderante
                     */
                    if (match.compararCon(filaActual)) {
                        int flag = match.esMasReciente(filaActual);
                        if (flag > 0) {
                            Log.d(TAG, "Programar actualización  del documento entregado " + updateUri);

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
                    Uri deleteUri = BitacorasCreditoArchivos.construirUri(filaActual.id);
                    Log.i(TAG, "Programar Eliminación del archivo de bitacora de crédito " + deleteUri);
                    ops.add(ContentProviderOperation.newDelete(deleteUri).build());
                }
            }
            c.close();
        }

        // Insertar los items resultantes ya que se asume que no existen en local
        HashMap<String, String[]> nuevos = new HashMap<>();
        for (BitacoraCreditoArchivo bitacoraCreditoArchivo : remotos.values()) {
            nuevos.put(bitacoraCreditoArchivo.id,new String[]{bitacoraCreditoArchivo.id,bitacoraCreditoArchivo.nombre});
            Log.d(TAG, "Programar Inserción de un nuevo archivo de bitacora de crédito con ID = " + bitacoraCreditoArchivo.id);
            ops.add(construirOperacionInsert(bitacoraCreditoArchivo));
        }
        return nuevos;
    }

    private ContentProviderOperation construirOperacionInsert(BitacoraCreditoArchivo nuevo) {
        return ContentProviderOperation.newInsert(BitacorasCreditoArchivos.URI_CONTENIDO)
                .withValue(BitacorasCreditoArchivos.ID, nuevo.id)
                .withValue(BitacorasCreditoArchivos.ID_BITACORA_CREDITO, nuevo.id_bitacora_credito)
                .withValue(BitacorasCreditoArchivos.FECHA, nuevo.fecha)
                .withValue(BitacorasCreditoArchivos.NOMBRE, nuevo.nombre)
                .withValue(BitacorasCreditoArchivos.TIPO, nuevo.tipo)
                //.withValue(BitacorasCreditoArchivos.RUTA, nuevo.ruta)//La ruta no se guarda, porque es relativa al dispositivo
                .withValue(BitacorasCreditoArchivos.BODY, nuevo.body)
                .withValue(BitacorasCreditoArchivos.VERSION, nuevo.version)
                .withValue(BitacorasCreditoArchivos.INSERTADO, 0)
                .build();
    }

    private ContentProviderOperation construirOperacionUpdate(BitacoraCreditoArchivo match, Uri updateUri) {
        return ContentProviderOperation.newUpdate(updateUri)
                .withValue(BitacorasCreditoArchivos.ID, match.id)
                .withValue(BitacorasCreditoArchivos.ID_BITACORA_CREDITO, match.id_bitacora_credito)
                .withValue(BitacorasCreditoArchivos.FECHA, match.fecha)
                .withValue(BitacorasCreditoArchivos.NOMBRE, match.nombre)
                .withValue(BitacorasCreditoArchivos.TIPO, match.tipo)
                //.withValue(BitacorasCreditoArchivos.RUTA, match.ruta)//La ruta no se sinconiza
                .withValue(BitacorasCreditoArchivos.VERSION, match.version)
                .withValue(BitacorasCreditoArchivos.MODIFICADO, match.modificado)
                .build();
    }

    /**
     * Convierte una fila de un Cursor en un nuevo Archivo de Bitacora de Crédito
     *
     * @param c cursor
     * @return objeto archivo bitacora de crédito
     */
    private BitacoraCreditoArchivo deCursorABitacoraCreditoArchivo(Cursor c) {
        return new BitacoraCreditoArchivo(
                c.getString(Consulta.ID),
                c.getString(Consulta.ID_BITACORA_CREDITO),
                c.getString(Consulta.FECHA),
                c.getString(Consulta.NOMBRE),
                c.getString(Consulta.TIPO),
                c.getString(Consulta.RUTA),
                c.getString(Consulta.BODY),
                c.getString(Consulta.VERSION),
                c.getInt(Consulta.MODIFICADO)
        );
    }
}
