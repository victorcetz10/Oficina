package com.softcredito.app.modelo;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.google.gson.Gson;

import com.softcredito.app.provider.Contract.Solicitudes;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Elemento que controla la transformación de JSON a POJO y viceversa
 */
public class ProcesadorLocalSolicitudes {

    private static final String TAG = ProcesadorLocalSolicitudes.class.getSimpleName();

    private interface Consulta{

        // Proyección para la consulta
        String[] PROYECCION = {
                Solicitudes.ID,
                Solicitudes.CLAVE,
                Solicitudes.CONTRATO,
                Solicitudes.ID_GRUPO,
                Solicitudes.ID_CLIENTE,
                Solicitudes.FECHA_SOLICITUD,
                Solicitudes.ID_PRODUCTO,
                Solicitudes.ID_BANCO,
                Solicitudes.MONTO_SOLICITADO,
                Solicitudes.PLAZO_SOLICITADO,
                Solicitudes.ID_TASA_REFERENCIA,
                Solicitudes.SOBRETASA,
                Solicitudes.TASA_MORATORIA,
                Solicitudes.ID_TIPO_PAGO,
                Solicitudes.ID_TIPO_AMORTIZACION,
                Solicitudes.MONTO_PAGAR,
                Solicitudes.MONTO_VENCIDO,
                Solicitudes.FECHA_VENCIMIENTO,
                Solicitudes.VERSION,
                Solicitudes.STATUS,
                Solicitudes.MODIFICADO
        };

        // Indices de columnas
        int ID = 0;
        int CLAVE = 1;
        int CONTRATO = 2;
        int ID_GRUPO = 3;
        int ID_CLIENTE = 4;
        int FECHA_SOLICITUD = 5;
        int ID_PRODUCTO = 6;
        int ID_BANCO = 7;
        int MONTO_SOLICITADO = 8;
        int PLAZO_SOLICITADO = 9;
        int ID_TASA_REFERENCIA = 10;
        int SOBRETASA = 11;
        int TASA_MORATORIA = 12;
        int ID_TIPO_PAGO = 13;
        int ID_TIPO_AMORTIZACION = 14;
        int MONTO_PAGAR = 15;
        int MONTO_VENCIDO = 16;
        int FECHA_VENCIMIENTO = 17;
        int STATUS = 18;
        int VERSION = 19;
        int MODIFICADO = 20;

    }

    // Mapa para filtrar solo los elementos a sincronizar
    private HashMap<String, Solicitud> remotos = new HashMap<>();

    // Conversor JSON
    private Gson gson = new Gson();

    public ProcesadorLocalSolicitudes() {
    }

    public void procesar(JSONArray arrayJson) {
        // Añadir elementos convertidos al mapa de los remotos
        for (Solicitud item : gson
                .fromJson(arrayJson.toString(), Solicitud[].class)) {
            item.aplicarSanidad();
            remotos.put(item.id, item);//Se agregan al mapa para poder procesarlos despues
        }
    }

    public void procesarOperaciones(ArrayList<ContentProviderOperation> ops, ContentResolver resolver) {
        // Consultar datos locales
        Cursor c = resolver.query(Solicitudes.URI_CONTENIDO,
                Consulta.PROYECCION,
                Solicitudes.INSERTADO + "=?",
                new String[]{"0"}, null);

        if (c != null) {

            while (c.moveToNext()) {

                // Convertir fila del cursor en objeto
                Solicitud filaActual = deCursorASolicitud(c);

                // Buscar si el dato local se encuentra en el mapa de remotos
                Solicitud match = remotos.get(filaActual.id);

                if (match != null) {
                    // Esta entrada existe, por lo que se remueve del mapa de remotos
                    remotos.remove(filaActual.id);

                    // Crear uri del item
                    Uri updateUri = Solicitudes.construirUri(filaActual.id);

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
                    Uri deleteUri = Solicitudes.construirUri(filaActual.id);
                    Log.i(TAG, "Programar Eliminación de la solicitud " + deleteUri);
                    ops.add(ContentProviderOperation.newDelete(deleteUri).build());
                }
            }
            c.close();
        }

        // Insertar los items resultantes ya que se asume que no existen en local
        for (Solicitud solicitud : remotos.values()) {
            Log.d(TAG, "Programar Inserción de una nueva solicitud con ID = " + solicitud.id);
            ops.add(construirOperacionInsert(solicitud));
        }
    }

    private ContentProviderOperation construirOperacionInsert(Solicitud nuevo) {
        return ContentProviderOperation.newInsert(Solicitudes.URI_CONTENIDO)
                .withValue(Solicitudes.ID, nuevo.id)
                .withValue(Solicitudes.CLAVE, nuevo.clave)
                .withValue(Solicitudes.CONTRATO, nuevo.contrato)
                .withValue(Solicitudes.ID_GRUPO, nuevo.id_grupo)
                .withValue(Solicitudes.ID_CLIENTE, nuevo.id_cliente)
                .withValue(Solicitudes.FECHA_SOLICITUD, nuevo.fecha_solicitud)
                .withValue(Solicitudes.ID_PRODUCTO, nuevo.id_producto)
                .withValue(Solicitudes.ID_BANCO, nuevo.id_banco)
                .withValue(Solicitudes.MONTO_SOLICITADO, nuevo.monto_solicitado)
                .withValue(Solicitudes.PLAZO_SOLICITADO, nuevo.plazo_solicitado)
                .withValue(Solicitudes.ID_TASA_REFERENCIA, nuevo.id_tasa_referencia)
                .withValue(Solicitudes.SOBRETASA, nuevo.sobretasa)
                .withValue(Solicitudes.TASA_MORATORIA, nuevo.tasa_moratoria)
                .withValue(Solicitudes.ID_TIPO_PAGO, nuevo.id_tipo_pago)
                .withValue(Solicitudes.ID_TIPO_AMORTIZACION, nuevo.id_tipo_amortizacion)
                .withValue(Solicitudes.MONTO_PAGAR, nuevo.monto_pagar)
                .withValue(Solicitudes.MONTO_VENCIDO, nuevo.monto_vencido)
                .withValue(Solicitudes.FECHA_VENCIMIENTO, nuevo.fecha_vencimiento)
                .withValue(Solicitudes.STATUS, nuevo.status)
                .withValue(Solicitudes.VERSION, nuevo.version)
                .withValue(Solicitudes.INSERTADO, 0)
                .build();
    }

    private ContentProviderOperation construirOperacionUpdate(Solicitud match, Uri updateUri) {
        return ContentProviderOperation.newUpdate(updateUri)
                .withValue(Solicitudes.ID, match.id)
                .withValue(Solicitudes.CLAVE, match.clave)
                .withValue(Solicitudes.CONTRATO, match.contrato)
                .withValue(Solicitudes.ID_GRUPO, match.id_grupo)
                .withValue(Solicitudes.ID_CLIENTE, match.id_cliente)
                .withValue(Solicitudes.FECHA_SOLICITUD, match.fecha_solicitud)
                .withValue(Solicitudes.ID_PRODUCTO, match.id_producto)
                .withValue(Solicitudes.ID_BANCO, match.id_banco)
                .withValue(Solicitudes.MONTO_SOLICITADO, match.monto_solicitado)
                .withValue(Solicitudes.PLAZO_SOLICITADO, match.plazo_solicitado)
                .withValue(Solicitudes.ID_TASA_REFERENCIA, match.id_tasa_referencia)
                .withValue(Solicitudes.SOBRETASA, match.sobretasa)
                .withValue(Solicitudes.TASA_MORATORIA, match.tasa_moratoria)
                .withValue(Solicitudes.ID_TIPO_PAGO, match.id_tipo_pago)
                .withValue(Solicitudes.ID_TIPO_AMORTIZACION, match.id_tipo_amortizacion)
                .withValue(Solicitudes.MONTO_PAGAR, match.monto_pagar)
                .withValue(Solicitudes.MONTO_VENCIDO, match.monto_vencido)
                .withValue(Solicitudes.FECHA_VENCIMIENTO, match.fecha_vencimiento)
                .withValue(Solicitudes.STATUS, match.status)
                .withValue(Solicitudes.VERSION, match.version)
                .withValue(Solicitudes.MODIFICADO, match.modificado)
                .build();
    }

    /**
     * Convierte una fila de un Cursor en un nuevo TipoAmortizacion
     *
     * @param c cursor
     * @return objeto solicitud
     */
    private Solicitud deCursorASolicitud(Cursor c) {
        return new Solicitud(
                c.getString(Consulta.ID),
                c.getString(Consulta.CLAVE),
                c.getString(Consulta.CONTRATO),
                c.getString(Consulta.ID_GRUPO),
                c.getString(Consulta.ID_CLIENTE),
                c.getString(Consulta.FECHA_SOLICITUD),
                c.getString(Consulta.ID_PRODUCTO),
                c.getString(Consulta.ID_BANCO),
                c.getString(Consulta.MONTO_SOLICITADO),
                c.getInt(Consulta.PLAZO_SOLICITADO),
                c.getInt(Consulta.ID_TASA_REFERENCIA),
                c.getString(Consulta.SOBRETASA),
                c.getString(Consulta.TASA_MORATORIA),
                c.getInt(Consulta.ID_TIPO_PAGO),
                c.getInt(Consulta.ID_TIPO_AMORTIZACION),
                c.getString(Consulta.MONTO_PAGAR),
                c.getString(Consulta.MONTO_VENCIDO),
                c.getString(Consulta.FECHA_VENCIMIENTO),
                c.getString(Consulta.STATUS),
                c.getString(Consulta.VERSION),
                c.getInt(Consulta.MODIFICADO)
        );
    }
}
