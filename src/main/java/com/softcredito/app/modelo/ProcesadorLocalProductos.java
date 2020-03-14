package com.softcredito.app.modelo;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.google.gson.Gson;

import com.softcredito.app.provider.Contract.Productos;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Elemento que controla la transformación de JSON a POJO y viceversa
 */
public class ProcesadorLocalProductos {

    private static final String TAG = ProcesadorLocalProductos.class.getSimpleName();

    private interface Consulta{

        // Proyección para la consulta
        String[] PROYECCION = {
                Productos.ID,
                Productos.PREFIJO,
                Productos.CLAVE,
                Productos.NOMBRE,
                Productos.ID_TIPO_AMORTIZACION,
                Productos.SOBRETASA,
                Productos.TASA_MORATORIA,
                Productos.PLAZO_MAXIMO,
                Productos.MONTO_MAXIMO,
                Productos.ID_TIPO_PAGO,
                Productos.VERSION,
                Productos.MODIFICADO
        };

        // Indices de columnas
        int ID = 0;
        int PREFIJO = 1;
        int CLAVE = 2;
        int NOMBRE = 3;
        int ID_TIPO_AMORTIZACION = 4;
        int SOBRETASA = 5;
        int TASA_MORATORIA = 6;
        int PLAZO_MAXIMO = 7;
        int MONTO_MAXIMO = 8;
        int ID_TIPO_PAGO = 9;
        int VERSION = 10;
        int MODIFICADO = 11;

    }

    // Mapa para filtrar solo los elementos a sincronizar
    private HashMap<String, Producto> remotos = new HashMap<>();

    // Conversor JSON
    private Gson gson = new Gson();

    public ProcesadorLocalProductos() {
    }

    public void procesar(JSONArray arrayJson) {
        // Añadir elementos convertidos al mapa de los remotos
        for (Producto item : gson
                .fromJson(arrayJson.toString(), Producto[].class)) {
            item.aplicarSanidad();
            remotos.put(item.id, item);//Se agregan al mapa para poder procesarlos despues
        }
    }

    public void procesarOperaciones(ArrayList<ContentProviderOperation> ops, ContentResolver resolver) {
        // Consultar datos locales
        Cursor c = resolver.query(Productos.URI_CONTENIDO,
                Consulta.PROYECCION,
                Productos.INSERTADO + "=?",
                new String[]{"0"}, null);

        if (c != null) {

            while (c.moveToNext()) {

                // Convertir fila del cursor en objeto
                Producto filaActual = deCursorAProducto(c);

                // Buscar si el dato local se encuentra en el mapa de remotos
                Producto match = remotos.get(filaActual.id);

                if (match != null) {
                    // Esta entrada existe, por lo que se remueve del mapa de remotos
                    remotos.remove(filaActual.id);

                    // Crear uri del item
                    Uri updateUri = Productos.construirUri(filaActual.id);

                    /*
                    Aquí se aplica la resolución de conflictos de modificaciones de un mismo recurso
                    tanto en el servidro como en la app. Quién tenga la versión más actual, será tomado
                    como preponderante
                     */
                    if (match.compararCon(filaActual)) {
                        int flag = match.esMasReciente(filaActual);
                        if (flag > 0) {
                            Log.d(TAG, "Programar actualización  del producto " + updateUri);

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
                    Uri deleteUri = Productos.construirUri(filaActual.id);
                    Log.i(TAG, "Programar Eliminación del producto " + deleteUri);
                    ops.add(ContentProviderOperation.newDelete(deleteUri).build());
                }
            }
            c.close();
        }

        // Insertar los items resultantes ya que se asume que no existen en local
        for (Producto producto : remotos.values()) {
            Log.d(TAG, "Programar Inserción de un nuevo producto con ID = " + producto.id);
            ops.add(construirOperacionInsert(producto));
        }
    }

    private ContentProviderOperation construirOperacionInsert(Producto nuevo) {
        return ContentProviderOperation.newInsert(Productos.URI_CONTENIDO)
                .withValue(Productos.ID, nuevo.id)
                .withValue(Productos.PREFIJO, nuevo.prefijo)
                .withValue(Productos.CLAVE, nuevo.clave)
                .withValue(Productos.NOMBRE, nuevo.nombre)
                .withValue(Productos.ID_TIPO_AMORTIZACION, nuevo.id_tipo_amortizacion)
                .withValue(Productos.SOBRETASA, nuevo.sobretasa)
                .withValue(Productos.TASA_MORATORIA, nuevo.tasa_moratoria)
                .withValue(Productos.PLAZO_MAXIMO, nuevo.plazo_maximo)
                .withValue(Productos.MONTO_MAXIMO, nuevo.monto_maximo)
                .withValue(Productos.ID_TIPO_PAGO, nuevo.id_tipo_pago)
                .withValue(Productos.VERSION, nuevo.version)
                .withValue(Productos.INSERTADO, 0)
                .build();
    }

    private ContentProviderOperation construirOperacionUpdate(Producto match, Uri updateUri) {
        return ContentProviderOperation.newUpdate(updateUri)
                .withValue(Productos.ID, match.id)
                .withValue(Productos.PREFIJO, match.prefijo)
                .withValue(Productos.CLAVE, match.clave)
                .withValue(Productos.NOMBRE, match.nombre)
                .withValue(Productos.ID_TIPO_AMORTIZACION, match.id_tipo_amortizacion)
                .withValue(Productos.SOBRETASA, match.sobretasa)
                .withValue(Productos.TASA_MORATORIA, match.tasa_moratoria)
                .withValue(Productos.PLAZO_MAXIMO, match.plazo_maximo)
                .withValue(Productos.MONTO_MAXIMO, match.monto_maximo)
                .withValue(Productos.ID_TIPO_PAGO, match.id_tipo_pago)
                .withValue(Productos.VERSION, match.version)
                .withValue(Productos.MODIFICADO, match.modificado)
                .build();
    }

    /**
     * Convierte una fila de un Cursor en un nuevo Cliente
     *
     * @param c cursor
     * @return objeto cliente
     */
    private Producto deCursorAProducto(Cursor c) {
        return new Producto(
                c.getString(Consulta.ID),
                c.getString(Consulta.PREFIJO),
                c.getString(Consulta.CLAVE),
                c.getString(Consulta.NOMBRE),
                c.getInt(Consulta.ID_TIPO_AMORTIZACION),
                c.getString(Consulta.SOBRETASA),
                c.getString(Consulta.TASA_MORATORIA),
                c.getInt(Consulta.PLAZO_MAXIMO),
                c.getString(Consulta.MONTO_MAXIMO),
                c.getInt(Consulta.ID_TIPO_PAGO),
                c.getString(Consulta.VERSION),
                c.getInt(Consulta.MODIFICADO)
        );
    }
}
