package com.softcredito.app.modelo;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.google.gson.Gson;
import com.softcredito.app.provider.Contract.DocumentosRequeridos;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Elemento que controla la transformación de JSON a POJO y viceversa
 */
public class ProcesadorLocalDocumentosRequeridos {

    private static final String TAG = ProcesadorLocalDocumentosRequeridos.class.getSimpleName();

    private interface Consulta{

        // Proyección para la consulta
        String[] PROYECCION = {
                DocumentosRequeridos.ID,
                DocumentosRequeridos.NOMBRE,
                DocumentosRequeridos.ID_TIPO_DOCUMENTO,
                DocumentosRequeridos.DESCRIPCION,
                DocumentosRequeridos.OPCIONAL,
                DocumentosRequeridos.VERSION,
                DocumentosRequeridos.MODIFICADO
        };

        // Indices de columnas
        int ID = 0;
        int NOMBRE = 1;
        int ID_TIPO_DOCUMENTO = 2;
        int DESCRIPCION = 3;
        int OPCIONAL = 4;
        int VERSION = 5;
        int MODIFICADO = 6;

    }

    // Mapa para filtrar solo los elementos a sincronizar
    private HashMap<String, DocumentoRequerido> remotos = new HashMap<>();

    // Conversor JSON
    private Gson gson = new Gson();

    public ProcesadorLocalDocumentosRequeridos() {
    }

    public void procesar(JSONArray arrayJson) {
        // Añadir elementos convertidos al mapa de los remotos
        for (DocumentoRequerido item : gson
                .fromJson(arrayJson.toString(), DocumentoRequerido[].class)) {
            item.aplicarSanidad();
            remotos.put(item.id, item);//Se agregan al mapa para poder procesarlos despues
        }
    }

    public void procesarOperaciones(ArrayList<ContentProviderOperation> ops, ContentResolver resolver) {
        // Consultar datos locales
        Cursor c = resolver.query(DocumentosRequeridos.URI_CONTENIDO,
                Consulta.PROYECCION,
                DocumentosRequeridos.INSERTADO + "=?",
                new String[]{"0"}, null);

        if (c != null) {

            while (c.moveToNext()) {

                // Convertir fila del cursor en objeto
                DocumentoRequerido filaActual = deCursorADocumentoRequerido(c);

                // Buscar si el dato local se encuentra en el mapa de remotos
                DocumentoRequerido match = remotos.get(filaActual.id);

                if (match != null) {
                    // Esta entrada existe, por lo que se remueve del mapa de remotos
                    remotos.remove(filaActual.id);

                    // Crear uri del item
                    Uri updateUri = DocumentosRequeridos.construirUri(filaActual.id);

                    /*
                    Aquí se aplica la resolución de conflictos de modificaciones de un mismo recurso
                    tanto en el servidro como en la app. Quién tenga la versión más actual, será tomado
                    como preponderante
                     */
                    if (match.compararCon(filaActual)) {
                        int flag = match.esMasReciente(filaActual);
                        if (flag > 0) {
                            Log.d(TAG, "Programar actualización  del documento requerido " + updateUri);

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
                    Uri deleteUri = DocumentosRequeridos.construirUri(filaActual.id);
                    Log.i(TAG, "Programar Eliminación del documento requerido " + deleteUri);
                    ops.add(ContentProviderOperation.newDelete(deleteUri).build());
                }
            }
            c.close();
        }

        // Insertar los items resultantes ya que se asume que no existen en local
        for (DocumentoRequerido documentoRequerido : remotos.values()) {
            Log.d(TAG, "Programar Inserción de un nuevo documento requerido con ID = " + documentoRequerido.id);
            ops.add(construirOperacionInsert(documentoRequerido));
        }
    }

    private ContentProviderOperation construirOperacionInsert(DocumentoRequerido nuevo) {
        return ContentProviderOperation.newInsert(DocumentosRequeridos.URI_CONTENIDO)
                .withValue(DocumentosRequeridos.ID, nuevo.id)
                .withValue(DocumentosRequeridos.NOMBRE, nuevo.nombre)
                .withValue(DocumentosRequeridos.ID_TIPO_DOCUMENTO, nuevo.id_tipo_documento)
                .withValue(DocumentosRequeridos.DESCRIPCION, nuevo.descripcion)
                .withValue(DocumentosRequeridos.OPCIONAL, nuevo.opcional)
                .withValue(DocumentosRequeridos.VERSION, nuevo.version)
                .withValue(DocumentosRequeridos.INSERTADO, 0)
                .build();
    }

    private ContentProviderOperation construirOperacionUpdate(DocumentoRequerido match, Uri updateUri) {
        return ContentProviderOperation.newUpdate(updateUri)
                .withValue(DocumentosRequeridos.ID, match.id)
                .withValue(DocumentosRequeridos.NOMBRE, match.nombre)
                .withValue(DocumentosRequeridos.ID_TIPO_DOCUMENTO, match.id_tipo_documento)
                .withValue(DocumentosRequeridos.DESCRIPCION, match.descripcion)
                .withValue(DocumentosRequeridos.OPCIONAL, match.opcional)
                .withValue(DocumentosRequeridos.VERSION, match.version)
                .withValue(DocumentosRequeridos.MODIFICADO, match.modificado)
                .build();
    }

    /**
     * Convierte una fila de un Cursor en un nuevo Cliente
     *
     * @param c cursor
     * @return objeto cliente
     */
    private DocumentoRequerido deCursorADocumentoRequerido(Cursor c) {
        return new DocumentoRequerido(
                c.getString(Consulta.ID),
                c.getString(Consulta.NOMBRE),
                c.getInt(Consulta.ID_TIPO_DOCUMENTO),
                c.getString(Consulta.DESCRIPCION),
                c.getInt(Consulta.OPCIONAL),
                c.getString(Consulta.VERSION),
                c.getInt(Consulta.MODIFICADO)
        );
    }
}
