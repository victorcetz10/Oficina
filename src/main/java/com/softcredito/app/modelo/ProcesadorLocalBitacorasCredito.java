package com.softcredito.app.modelo;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.google.gson.Gson;

import com.softcredito.app.provider.Contract.BitacorasCredito;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Elemento que controla la transformación de JSON a POJO y viceversa
 */
public class ProcesadorLocalBitacorasCredito {

    private static final String TAG = ProcesadorLocalBitacorasCredito.class.getSimpleName();

    private interface Consulta{

        // Proyección para la consulta
        String[] PROYECCION = {
                BitacorasCredito.ID,
                BitacorasCredito.ID_SOLICITUD,
                BitacorasCredito.ASUNTO,
                BitacorasCredito.FECHA,
                BitacorasCredito.HORA,
                BitacorasCredito.NUMERO_AMORTIZACION,
                BitacorasCredito.DETALLES_PAGO,
                BitacorasCredito.DESCRIPCION,
                BitacorasCredito.VALOR_GARANTIA,
                BitacorasCredito.DESCRIPCION_GARANTIA,
                BitacorasCredito.VERSION,
                BitacorasCredito.MODIFICADO
        };

        // Indices de columnas
        int ID = 0;
        int ID_SOLICITUD = 1;
        int ASUNTO = 2;
        int FECHA = 3;
        int HORA = 4;
        int NUMERO_AMORTIZACION = 5;
        int DETALLES_PAGO = 6;
        int DESCRIPCION = 7;
        int VALOR_GARANTIA = 8;
        int DESCRIPCION_GARANTIA = 9;
        int VERSION = 10;
        int MODIFICADO = 11;

    }

    // Mapa para filtrar solo los elementos a sincronizar
    private HashMap<String, BitacoraCredito> remotos = new HashMap<>();

    // Conversor JSON
    private Gson gson = new Gson();

    public ProcesadorLocalBitacorasCredito() {
    }

    public void procesar(JSONArray arrayJson) {
        // Añadir elementos convertidos al mapa de los remotos
        for (BitacoraCredito item : gson
                .fromJson(arrayJson.toString(), BitacoraCredito[].class)) {
            item.aplicarSanidad();
            remotos.put(item.id, item);//Se agregan al mapa para poder procesarlos despues
        }
    }

    public void procesarOperaciones(ArrayList<ContentProviderOperation> ops, ContentResolver resolver) {
        // Consultar datos locales
        Cursor c = resolver.query(BitacorasCredito.URI_CONTENIDO,
                Consulta.PROYECCION,
                BitacorasCredito.INSERTADO + "=?",
                new String[]{"0"}, null);

        if (c != null) {

            while (c.moveToNext()) {

                // Convertir fila del cursor en objeto
                BitacoraCredito filaActual = deCursorABitacoraCredito(c);

                // Buscar si el dato local se encuentra en el mapa de remotos
                BitacoraCredito match = remotos.get(filaActual.id);

                if (match != null) {
                    // Esta entrada existe, por lo que se remueve del mapa de remotos
                    remotos.remove(filaActual.id);

                    // Crear uri del item
                    Uri updateUri = BitacorasCredito.construirUri(filaActual.id);

                    /*
                    Aquí se aplica la resolución de conflictos de modificaciones de un mismo recurso
                    tanto en el servidro como en la app. Quién tenga la versión más actual, será tomado
                    como preponderante
                     */
                    if (match.compararCon(filaActual)) {
                        int flag = match.esMasReciente(filaActual);
                        if (flag > 0) {
                            Log.d(TAG, "Programar actualización  de la bitacora de crédito " + updateUri);

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
                    Uri deleteUri = BitacorasCredito.construirUri(filaActual.id);
                    Log.i(TAG, "Programar Eliminación de la bitacora de crédito " + deleteUri);
                    ops.add(ContentProviderOperation.newDelete(deleteUri).build());
                }
            }
            c.close();
        }

        // Insertar los items resultantes ya que se asume que no existen en local
        for (BitacoraCredito bitacoraCredito : remotos.values()) {
            Log.d(TAG, "Programar Inserción de una nueva bitacora de crédito con ID = " + bitacoraCredito.id);
            ops.add(construirOperacionInsert(bitacoraCredito));
        }
    }

    private ContentProviderOperation construirOperacionInsert(BitacoraCredito nuevo) {
        return ContentProviderOperation.newInsert(BitacorasCredito.URI_CONTENIDO)
                .withValue(BitacorasCredito.ID, nuevo.id)
                .withValue(BitacorasCredito.ID_SOLICITUD, nuevo.id_solicitud)
                .withValue(BitacorasCredito.ASUNTO, nuevo.asunto)
                .withValue(BitacorasCredito.FECHA, nuevo.fecha)
                .withValue(BitacorasCredito.HORA, nuevo.hora)
                .withValue(BitacorasCredito.NUMERO_AMORTIZACION, nuevo.numero_amortizacion)
                .withValue(BitacorasCredito.DETALLES_PAGO, nuevo.detalles_pago)
                .withValue(BitacorasCredito.DESCRIPCION, nuevo.descripcion)
                .withValue(BitacorasCredito.VALOR_GARANTIA, nuevo.valor_garantia)
                .withValue(BitacorasCredito.DESCRIPCION_GARANTIA, nuevo.descripcion_garantia)
                .withValue(BitacorasCredito.VERSION, nuevo.version)
                .withValue(BitacorasCredito.INSERTADO, 0)
                .build();
    }

    private ContentProviderOperation construirOperacionUpdate(BitacoraCredito match, Uri updateUri) {
        return ContentProviderOperation.newUpdate(updateUri)
                .withValue(BitacorasCredito.ID, match.id)
                .withValue(BitacorasCredito.ID_SOLICITUD, match.id_solicitud)
                .withValue(BitacorasCredito.ASUNTO, match.asunto)
                .withValue(BitacorasCredito.FECHA, match.fecha)
                .withValue(BitacorasCredito.HORA, match.hora)
                .withValue(BitacorasCredito.NUMERO_AMORTIZACION, match.numero_amortizacion)
                .withValue(BitacorasCredito.DETALLES_PAGO, match.detalles_pago)
                .withValue(BitacorasCredito.DESCRIPCION, match.descripcion)
                .withValue(BitacorasCredito.VALOR_GARANTIA, match.valor_garantia)
                .withValue(BitacorasCredito.DESCRIPCION_GARANTIA, match.descripcion_garantia)
                .withValue(BitacorasCredito.VERSION, match.version)
                .withValue(BitacorasCredito.MODIFICADO, match.modificado)
                .build();
    }

    /**
     * Convierte una fila de un Cursor en un nuevo BitacoraCredito
     *
     * @param c cursor
     * @return objeto bitacoracredito
     */
    private BitacoraCredito deCursorABitacoraCredito(Cursor c) {
        return new BitacoraCredito(
                c.getString(Consulta.ID),
                c.getString(Consulta.ID_SOLICITUD),
                c.getString(Consulta.ASUNTO),
                c.getString(Consulta.FECHA),
                c.getString(Consulta.HORA),
                c.getString(Consulta.NUMERO_AMORTIZACION),
                c.getString(Consulta.DETALLES_PAGO),
                c.getString(Consulta.DESCRIPCION),
                c.getString(Consulta.VALOR_GARANTIA),
                c.getString(Consulta.DESCRIPCION_GARANTIA),
                c.getString(Consulta.VERSION),
                c.getInt(Consulta.MODIFICADO)
        );
    }
}
