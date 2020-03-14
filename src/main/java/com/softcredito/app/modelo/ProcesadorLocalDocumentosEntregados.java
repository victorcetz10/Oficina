package com.softcredito.app.modelo;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.google.gson.Gson;
import com.softcredito.app.provider.Contract.DocumentosEntregados;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Elemento que controla la transformación de JSON a POJO y viceversa
 */
public class ProcesadorLocalDocumentosEntregados {

    private static final String TAG = ProcesadorLocalDocumentosEntregados.class.getSimpleName();

    private interface Consulta{

        // Proyección para la consulta
        String[] PROYECCION = {
                DocumentosEntregados.ID,
                DocumentosEntregados.ID_CLIENTE,
                DocumentosEntregados.ID_DOCUMENTO_REQUERIDO,
                DocumentosEntregados.NOMBRE_DOCUMENTO,
                DocumentosEntregados.DESCRIPCION,
                DocumentosEntregados.STATUS,
                DocumentosEntregados.TIPO_DOCUMENTO,
                DocumentosEntregados.VERSION,
                DocumentosEntregados.MODIFICADO
        };

        // Indices de columnas
        int ID = 0;
        int ID_CLIENTE = 1;
        int ID_DOCUMENTO_REQUERIDO = 2;
        int NOMBRE_DOCUMENTO = 3;
        int DESCRIPCION = 4;
        int STATUS = 5;
        int TIPO_DOCUMENTO = 6;
        int VERSION = 7;
        int MODIFICADO = 8;

    }

    // Mapa para filtrar solo los elementos a sincronizar
    private HashMap<String, DocumentoEntregado> remotos = new HashMap<>();

    // Conversor JSON
    private Gson gson = new Gson();

    public ProcesadorLocalDocumentosEntregados() {
    }

    public void procesar(JSONArray arrayJson) {
        // Añadir elementos convertidos al mapa de los remotos
        for (DocumentoEntregado item : gson
                .fromJson(arrayJson.toString(), DocumentoEntregado[].class)) {
            item.aplicarSanidad();
            remotos.put(item.id, item);//Se agregan al mapa para poder procesarlos despues
        }
    }

    public void procesarOperaciones(ArrayList<ContentProviderOperation> ops, ContentResolver resolver) {
        // Consultar datos locales
        Cursor c = resolver.query(DocumentosEntregados.URI_CONTENIDO,
                Consulta.PROYECCION,
                DocumentosEntregados.INSERTADO + "=?",
                new String[]{"0"}, null);

        if (c != null) {

            while (c.moveToNext()) {

                // Convertir fila del cursor en objeto
                DocumentoEntregado filaActual = deCursorADocumentoEntregado(c);

                // Buscar si el dato local se encuentra en el mapa de remotos
                DocumentoEntregado match = remotos.get(filaActual.id);

                if (match != null) {
                    // Esta entrada existe, por lo que se remueve del mapa de remotos
                    remotos.remove(filaActual.id);

                    // Crear uri del item
                    Uri updateUri = DocumentosEntregados.construirUri(filaActual.id);

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
                    Uri deleteUri = DocumentosEntregados.construirUri(filaActual.id);
                    Log.i(TAG, "Programar Eliminación del documento entregado " + deleteUri);
                    ops.add(ContentProviderOperation.newDelete(deleteUri).build());
                }
            }
            c.close();
        }

        // Insertar los items resultantes ya que se asume que no existen en local
        for (DocumentoEntregado documentoEntregado : remotos.values()) {
            Log.d(TAG, "Programar Inserción de un nuevo documento entregado con ID = " + documentoEntregado.id);
            ops.add(construirOperacionInsert(documentoEntregado));
        }
    }

    private ContentProviderOperation construirOperacionInsert(DocumentoEntregado nuevo) {
        return ContentProviderOperation.newInsert(DocumentosEntregados.URI_CONTENIDO)
                .withValue(DocumentosEntregados.ID, nuevo.id)
                .withValue(DocumentosEntregados.ID_CLIENTE, nuevo.id_cliente)
                .withValue(DocumentosEntregados.ID_DOCUMENTO_REQUERIDO, nuevo.id_documento_requerido)
                .withValue(DocumentosEntregados.NOMBRE_DOCUMENTO, nuevo.nombre_documento)
                .withValue(DocumentosEntregados.DESCRIPCION, nuevo.descripcion)
                .withValue(DocumentosEntregados.STATUS, nuevo.status)
                .withValue(DocumentosEntregados.TIPO_DOCUMENTO, nuevo.tipo_documento)
                .withValue(DocumentosEntregados.VERSION, nuevo.version)
                .withValue(DocumentosEntregados.INSERTADO, 0)
                .build();
    }

    private ContentProviderOperation construirOperacionUpdate(DocumentoEntregado match, Uri updateUri) {
        return ContentProviderOperation.newUpdate(updateUri)
                .withValue(DocumentosEntregados.ID, match.id)
                .withValue(DocumentosEntregados.ID_CLIENTE, match.id_cliente)
                .withValue(DocumentosEntregados.ID_DOCUMENTO_REQUERIDO, match.id_documento_requerido)
                .withValue(DocumentosEntregados.NOMBRE_DOCUMENTO, match.nombre_documento)
                .withValue(DocumentosEntregados.DESCRIPCION, match.descripcion)
                .withValue(DocumentosEntregados.STATUS, match.status)
                .withValue(DocumentosEntregados.VERSION, match.version)
                .withValue(DocumentosEntregados.MODIFICADO, match.modificado)
                .build();
    }

    /**
     * Convierte una fila de un Cursor en un nuevo Documento Entregado
     *
     * @param c cursor
     * @return objeto documento entregado
     */
    private DocumentoEntregado deCursorADocumentoEntregado(Cursor c) {
        return new DocumentoEntregado(
                c.getString(Consulta.ID),
                c.getString(Consulta.ID_CLIENTE),
                c.getString(Consulta.ID_DOCUMENTO_REQUERIDO),
                c.getString(Consulta.NOMBRE_DOCUMENTO),
                c.getString(Consulta.DESCRIPCION),
                c.getInt(Consulta.STATUS),
                c.getString(Consulta.TIPO_DOCUMENTO),
                c.getString(Consulta.VERSION),
                c.getInt(Consulta.MODIFICADO)
        );
    }
}
