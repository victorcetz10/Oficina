package com.softcredito.app.modelo;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.google.gson.Gson;
import com.softcredito.app.provider.Contract.ArchivosDocumentosEntregados;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Elemento que controla la transformación de JSON a POJO y viceversa
 */
public class ProcesadorLocalArchivosDocumentosEntregados {

    private static final String TAG = ProcesadorLocalArchivosDocumentosEntregados.class.getSimpleName();

    private interface Consulta{

        // Proyección para la consulta
        String[] PROYECCION = {
                ArchivosDocumentosEntregados.ID,
                ArchivosDocumentosEntregados.ID_DOCUMENTO_ENTREGADO,
                ArchivosDocumentosEntregados.FECHA,
                ArchivosDocumentosEntregados.NOMBRE,
                ArchivosDocumentosEntregados.TIPO,
                ArchivosDocumentosEntregados.RUTA,
                ArchivosDocumentosEntregados.DESCRIPCION,
                ArchivosDocumentosEntregados.BODY,
                ArchivosDocumentosEntregados.VERSION,
                ArchivosDocumentosEntregados.MODIFICADO
        };

        // Indices de columnas
        int ID = 0;
        int ID_DOCUMENTO_ENTREGADO = 1;
        int FECHA = 2;
        int NOMBRE = 3;
        int TIPO = 4;
        int RUTA = 5;
        int DESCRIPCION = 6;
        int BODY = 7;
        int VERSION = 8;
        int MODIFICADO = 9;

    }

    // Mapa para filtrar solo los elementos a sincronizar
    private HashMap<String, ArchivoDocumentoEntregado> remotos = new HashMap<>();

    // Conversor JSON
    private Gson gson = new Gson();

    public ProcesadorLocalArchivosDocumentosEntregados() {
    }

    public void procesar(JSONArray arrayJson) {
        // Añadir elementos convertidos al mapa de los remotos
        for (ArchivoDocumentoEntregado item : gson
                .fromJson(arrayJson.toString(), ArchivoDocumentoEntregado[].class)) {
            item.aplicarSanidad();
            remotos.put(item.id, item);//Se agregan al mapa para poder procesarlos despues
        }
    }

    public HashMap<String, String[]> procesarOperaciones(ArrayList<ContentProviderOperation> ops, ContentResolver resolver) {
        // Consultar datos locales
        Cursor c = resolver.query(ArchivosDocumentosEntregados.URI_CONTENIDO,
                Consulta.PROYECCION,
                ArchivosDocumentosEntregados.INSERTADO + "=?",
                new String[]{"0"}, null);

        if (c != null) {

            while (c.moveToNext()) {

                // Convertir fila del cursor en objeto
                ArchivoDocumentoEntregado filaActual = deCursorAArchivoDocumentoEntregado(c);

                // Buscar si el dato local se encuentra en el mapa de remotos
                ArchivoDocumentoEntregado match = remotos.get(filaActual.id);

                if (match != null) {
                    // Esta entrada existe, por lo que se remueve del mapa de remotos
                    remotos.remove(filaActual.id);

                    // Crear uri del item
                    Uri updateUri = ArchivosDocumentosEntregados.construirUri(filaActual.id);

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
                    Uri deleteUri = ArchivosDocumentosEntregados.construirUri(filaActual.id);
                    Log.i(TAG, "Programar Eliminación del archivo documento entregado " + deleteUri);
                    ops.add(ContentProviderOperation.newDelete(deleteUri).build());
                }
            }
            c.close();
        }

        // Insertar los items resultantes ya que se asume que no existen en local
        HashMap<String, String[]> nuevos = new HashMap<>();
        for (ArchivoDocumentoEntregado archivoDocumentoEntregado : remotos.values()) {
            nuevos.put(archivoDocumentoEntregado.id,new String[]{archivoDocumentoEntregado.id,archivoDocumentoEntregado.nombre});
            Log.d(TAG, "Programar Inserción de un nuevo archivo documento entregado con ID = " + archivoDocumentoEntregado.id);
            ops.add(construirOperacionInsert(archivoDocumentoEntregado));
        }

        return nuevos;
    }

    private ContentProviderOperation construirOperacionInsert(ArchivoDocumentoEntregado nuevo) {
        return ContentProviderOperation.newInsert(ArchivosDocumentosEntregados.URI_CONTENIDO)
                .withValue(ArchivosDocumentosEntregados.ID, nuevo.id)
                .withValue(ArchivosDocumentosEntregados.ID_DOCUMENTO_ENTREGADO, nuevo.id_documento_entregado)
                .withValue(ArchivosDocumentosEntregados.FECHA, nuevo.fecha)
                .withValue(ArchivosDocumentosEntregados.NOMBRE, nuevo.nombre)
                .withValue(ArchivosDocumentosEntregados.TIPO, nuevo.tipo)
                //.withValue(ArchivosDocumentosEntregados.RUTA, nuevo.ruta)//La ruta no se guarda, porque es relativa al dispositivo
                .withValue(ArchivosDocumentosEntregados.DESCRIPCION, nuevo.descripcion)
                .withValue(ArchivosDocumentosEntregados.BODY, nuevo.body)
                .withValue(ArchivosDocumentosEntregados.VERSION, nuevo.version)
                .withValue(ArchivosDocumentosEntregados.INSERTADO, 0)
                .build();
    }

    private ContentProviderOperation construirOperacionUpdate(ArchivoDocumentoEntregado match, Uri updateUri) {
        return ContentProviderOperation.newUpdate(updateUri)
                .withValue(ArchivosDocumentosEntregados.ID, match.id)
                .withValue(ArchivosDocumentosEntregados.ID_DOCUMENTO_ENTREGADO, match.id_documento_entregado)
                .withValue(ArchivosDocumentosEntregados.FECHA, match.fecha)
                .withValue(ArchivosDocumentosEntregados.NOMBRE, match.nombre)
                .withValue(ArchivosDocumentosEntregados.TIPO, match.tipo)
                //.withValue(ArchivosDocumentosEntregados.RUTA, match.ruta)//La ruta no se actualiza porque es relativa al dispositivo
                .withValue(ArchivosDocumentosEntregados.DESCRIPCION, match.descripcion)
                .withValue(ArchivosDocumentosEntregados.VERSION, match.version)
                .withValue(ArchivosDocumentosEntregados.MODIFICADO, match.modificado)
                .build();
    }

    /**
     * Convierte una fila de un Cursor en un nuevo Archivo Documento Entregado
     *
     * @param c cursor
     * @return objeto archivo documento entregado
     */
    private ArchivoDocumentoEntregado deCursorAArchivoDocumentoEntregado(Cursor c) {
        return new ArchivoDocumentoEntregado(
                c.getString(Consulta.ID),
                c.getString(Consulta.ID_DOCUMENTO_ENTREGADO),
                c.getString(Consulta.FECHA),
                c.getString(Consulta.NOMBRE),
                c.getString(Consulta.TIPO),
                c.getString(Consulta.RUTA),
                c.getString(Consulta.DESCRIPCION),
                c.getString(Consulta.BODY),
                c.getString(Consulta.VERSION),
                c.getInt(Consulta.MODIFICADO)
        );
    }
}
