package com.softcredito.app.modelo;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.google.gson.Gson;

import com.softcredito.app.provider.Contract.Cotizadores;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Elemento que controla la transformación de JSON a POJO y viceversa
 */
public class ProcesadorLocalCotizadores {

    private static final String TAG = ProcesadorLocalCotizadores.class.getSimpleName();

    private interface Consulta{

        // Proyección para la consulta
        String[] PROYECCION = {
                Cotizadores.ID,
                Cotizadores.ID_CLIENTE,
                Cotizadores.FECHA_COTIZACION,
                Cotizadores.VALIDEZ,
                Cotizadores.FECHA_DISPOSICION,
                Cotizadores.FECHA_INICIO_AMORTIZACIONES,
                Cotizadores.ID_PRODUCTO,
                Cotizadores.MONTO_AUTORIZADO,
                Cotizadores.PLAZO_AUTORIZADO,
                Cotizadores.ID_TASA_REFERENCIA,
                Cotizadores.SOBRETASA,
                Cotizadores.TASA_MORATORIA,
                Cotizadores.ID_TIPO_PAGO,
                Cotizadores.ID_TIPO_AMORTIZACION,
                Cotizadores.NOTAS,
                Cotizadores.VERSION,
                Cotizadores.MODIFICADO
        };

        // Indices de columnas
        int ID = 0;
        int ID_CLIENTE = 1;
        int FECHA_COTIZACION = 2;
        int VALIDEZ = 3;
        int FECHA_DISPOSICION = 4;
        int FECHA_INICIO_AMORTIZACIONES = 5;
        int ID_PRODUCTO = 6;
        int MONTO_AUTORIZADO = 7;
        int PLAZO_AUTORIZADO = 8;
        int ID_TASA_REFERENCIA = 9;
        int SOBRETASA = 10;
        int TASA_MORATORIA = 11;
        int ID_TIPO_PAGO = 12;
        int ID_TIPO_AMORTIZACION = 13;
        int NOTAS = 14;
        int VERSION = 15;
        int MODIFICADO = 16;

    }

    // Mapa para filtrar solo los elementos a sincronizar
    private HashMap<String, Cotizador> remotos = new HashMap<>();

    // Conversor JSON
    private Gson gson = new Gson();

    public ProcesadorLocalCotizadores() {
    }

    public void procesar(JSONArray arrayJson) {
        // Añadir elementos convertidos al mapa de los remotos
        for (Cotizador item : gson
                .fromJson(arrayJson.toString(), Cotizador[].class)) {
            item.aplicarSanidad();
            remotos.put(item.id, item);//Se agregan al mapa para poder procesarlos despues
        }
    }

    public void procesarOperaciones(ArrayList<ContentProviderOperation> ops, ContentResolver resolver) {
        // Consultar datos locales
        Cursor c = resolver.query(Cotizadores.URI_CONTENIDO,
                Consulta.PROYECCION,
                Cotizadores.INSERTADO + "=?",
                new String[]{"0"}, null);

        if (c != null) {

            while (c.moveToNext()) {

                // Convertir fila del cursor en objeto
                Cotizador filaActual = deCursorACotizador(c);

                // Buscar si el dato local se encuentra en el mapa de remotos
                Cotizador match = remotos.get(filaActual.id);

                if (match != null) {
                    // Esta entrada existe, por lo que se remueve del mapa de remotos
                    remotos.remove(filaActual.id);

                    // Crear uri del item
                    Uri updateUri = Cotizadores.construirUri(filaActual.id);

                    /*
                    Aquí se aplica la resolución de conflictos de modificaciones de un mismo recurso
                    tanto en el servidro como en la app. Quién tenga la versión más actual, será tomado
                    como preponderante
                     */
                    if (match.compararCon(filaActual)) {
                        int flag = match.esMasReciente(filaActual);
                        if (flag > 0) {
                            Log.d(TAG, "Programar actualización  de la solicitud " + updateUri);

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
                    Uri deleteUri = Cotizadores.construirUri(filaActual.id);
                    Log.i(TAG, "Programar Eliminación de la cotización " + deleteUri);
                    ops.add(ContentProviderOperation.newDelete(deleteUri).build());
                }
            }
            c.close();
        }

        // Insertar los items resultantes ya que se asume que no existen en local
        for (Cotizador cotizador : remotos.values()) {
            Log.d(TAG, "Programar Inserción de una nueva cotización con ID = " + cotizador.id);
            ops.add(construirOperacionInsert(cotizador));
        }
    }

    private ContentProviderOperation construirOperacionInsert(Cotizador nuevo) {
        return ContentProviderOperation.newInsert(Cotizadores.URI_CONTENIDO)
                .withValue(Cotizadores.ID, nuevo.id)
                .withValue(Cotizadores.ID_CLIENTE, nuevo.id_cliente)
                .withValue(Cotizadores.FECHA_COTIZACION, nuevo.fecha_cotizacion)
                .withValue(Cotizadores.VALIDEZ, nuevo.validez)
                .withValue(Cotizadores.FECHA_DISPOSICION, nuevo.fecha_disposicion)
                .withValue(Cotizadores.FECHA_INICIO_AMORTIZACIONES, nuevo.fecha_inicio_amortizaciones)
                .withValue(Cotizadores.ID_PRODUCTO, nuevo.id_producto)
                .withValue(Cotizadores.MONTO_AUTORIZADO, nuevo.monto_autorizado)
                .withValue(Cotizadores.PLAZO_AUTORIZADO, nuevo.plazo_autorizado)
                .withValue(Cotizadores.ID_TASA_REFERENCIA, nuevo.id_tasa_referencia)
                .withValue(Cotizadores.SOBRETASA, nuevo.sobretasa)
                .withValue(Cotizadores.TASA_MORATORIA, nuevo.tasa_moratoria)
                .withValue(Cotizadores.ID_TIPO_PAGO, nuevo.id_tipo_pago)
                .withValue(Cotizadores.ID_TIPO_AMORTIZACION, nuevo.id_tipo_amortizacion)
                .withValue(Cotizadores.NOTAS, nuevo.notas)
                .withValue(Cotizadores.VERSION, nuevo.version)
                .withValue(Cotizadores.INSERTADO, 0)
                .build();
    }

    private ContentProviderOperation construirOperacionUpdate(Cotizador match, Uri updateUri) {
        return ContentProviderOperation.newUpdate(updateUri)
                .withValue(Cotizadores.ID, match.id)
                .withValue(Cotizadores.ID_CLIENTE, match.id_cliente)
                .withValue(Cotizadores.FECHA_COTIZACION, match.fecha_cotizacion)
                .withValue(Cotizadores.VALIDEZ, match.validez)
                .withValue(Cotizadores.FECHA_DISPOSICION, match.fecha_disposicion)
                .withValue(Cotizadores.FECHA_INICIO_AMORTIZACIONES, match.fecha_inicio_amortizaciones)
                .withValue(Cotizadores.ID_PRODUCTO, match.id_producto)
                .withValue(Cotizadores.MONTO_AUTORIZADO, match.monto_autorizado)
                .withValue(Cotizadores.PLAZO_AUTORIZADO, match.plazo_autorizado)
                .withValue(Cotizadores.ID_TASA_REFERENCIA, match.id_tasa_referencia)
                .withValue(Cotizadores.SOBRETASA, match.sobretasa)
                .withValue(Cotizadores.TASA_MORATORIA, match.tasa_moratoria)
                .withValue(Cotizadores.ID_TIPO_PAGO, match.id_tipo_pago)
                .withValue(Cotizadores.ID_TIPO_AMORTIZACION, match.id_tipo_amortizacion)
                .withValue(Cotizadores.NOTAS, match.notas)
                .withValue(Cotizadores.VERSION, match.version)
                .withValue(Cotizadores.MODIFICADO, match.modificado)
                .build();
    }

    /**
     * Convierte una fila de un Cursor en un nuevo Cotizador
     *
     * @param c cursor
     * @return objeto cotizador
     */
    private Cotizador deCursorACotizador(Cursor c) {
        return new Cotizador(
                c.getString(Consulta.ID),
                c.getString(Consulta.ID_CLIENTE),
                c.getString(Consulta.FECHA_COTIZACION),
                c.getInt(Consulta.VALIDEZ),
                c.getString(Consulta.FECHA_DISPOSICION),
                c.getString(Consulta.FECHA_INICIO_AMORTIZACIONES),
                c.getString(Consulta.ID_PRODUCTO),
                c.getString(Consulta.MONTO_AUTORIZADO),
                c.getInt(Consulta.PLAZO_AUTORIZADO),
                c.getInt(Consulta.ID_TASA_REFERENCIA),
                c.getString(Consulta.SOBRETASA),
                c.getString(Consulta.TASA_MORATORIA),
                c.getInt(Consulta.ID_TIPO_PAGO),
                c.getInt(Consulta.ID_TIPO_AMORTIZACION),
                c.getString(Consulta.NOTAS),
                c.getString(Consulta.VERSION),
                c.getInt(Consulta.MODIFICADO)
        );
    }
}
