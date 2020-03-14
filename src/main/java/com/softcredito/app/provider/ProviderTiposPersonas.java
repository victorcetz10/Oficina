package com.softcredito.app.provider;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.text.TextUtils;

import com.softcredito.app.provider.Contract.TiposPersonas;
import com.softcredito.app.provider.HelperTiposPersonas.Tablas;

/**
 * {@link ContentProvider} que encapsula el acceso a la base de datos de tipos de personas
 */
public class ProviderTiposPersonas extends ContentProvider {

    // Comparador de URIs de contenido
    public static final UriMatcher uriMatcher;

    // Identificadores de tipos
    public static final int TIPOS_PERSONAS = 100;
    public static final int TIPOS_PERSONAS_ID = 101;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(Contract.AUTORIDAD, "tipos_personas", TIPOS_PERSONAS);
        uriMatcher.addURI(Contract.AUTORIDAD, "tipos_personas/*", TIPOS_PERSONAS_ID);
    }

    private HelperTiposPersonas manejadorBD;
    private ContentResolver resolver;


    @Override
    public boolean onCreate() {
        manejadorBD = new HelperTiposPersonas(getContext());
        resolver = getContext().getContentResolver();
        return true;
    }

    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {
            case TIPOS_PERSONAS:
                return TiposPersonas.MIME_COLECCION;
            case TIPOS_PERSONAS_ID:
                return TiposPersonas.MIME_RECURSO;
            default:
                throw new IllegalArgumentException("Tipo desconocido: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        // Obtener base de datos
        SQLiteDatabase db = manejadorBD.getWritableDatabase();
        // Comparar Uri
        int match = uriMatcher.match(uri);

        Cursor c;

        switch (match) {
            case TIPOS_PERSONAS:
                // Consultando todos los registros
                c = db.query(Tablas.TIPOS_PERSONAS, projection,
                        selection, selectionArgs,
                        null, null, sortOrder);
                c.setNotificationUri(resolver, TiposPersonas.URI_CONTENIDO);
                break;
            case TIPOS_PERSONAS_ID:
                // Consultando un solo registro basado en el Id del Uri
                String id = TiposPersonas.obtenerId(uri);
                c = db.query(Tablas.TIPOS_PERSONAS, projection,
                        TiposPersonas.ID + "=" + "\'" + id + "\'"
                                + (!TextUtils.isEmpty(selection) ?
                                " AND (" + selection + ')' : ""),
                        selectionArgs, null, null, sortOrder);
                c.setNotificationUri(resolver, uri);
                break;
            default:
                throw new IllegalArgumentException("URI no soportada: " + uri);
        }
        return c;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = manejadorBD.getWritableDatabase();

        int match = uriMatcher.match(uri);
        int filasAfectadas;

        switch (match) {
            case TIPOS_PERSONAS:

                filasAfectadas = db.delete(Tablas.TIPOS_PERSONAS,
                        selection,
                        selectionArgs);

                resolver.notifyChange(uri, null, false);

                break;
            case TIPOS_PERSONAS_ID:

                String id = TiposPersonas.obtenerId(uri);

                filasAfectadas = db.delete(Tablas.TIPOS_PERSONAS,
                        TiposPersonas.ID + "=" + "\'" + id + "\'"
                                + (!TextUtils.isEmpty(selection) ?
                                " AND (" + selection + ')' : ""),
                        selectionArgs);

                resolver.notifyChange(uri, null, false);
                break;
            default:
                throw new IllegalArgumentException("Tipo de Persona desconocido: " +
                        uri);
        }
        return filasAfectadas;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // Validar la uri
        if (uriMatcher.match(uri) != TIPOS_PERSONAS) {
            throw new IllegalArgumentException("URI desconocida : " + uri);
        }

        ContentValues contentValues;
        if (values != null) {
            contentValues = new ContentValues(values);
        } else {
            contentValues = new ContentValues();
        }

        // Inserción de nueva fila
        SQLiteDatabase db = manejadorBD.getWritableDatabase();

        long _id = db.insert(Tablas.TIPOS_PERSONAS, null, contentValues);

        if (_id > 0) {

            resolver.notifyChange(uri, null, false);

            String id = contentValues.getAsString(TiposPersonas.ID);

            return TiposPersonas.construirUri(id);
        }
        throw new SQLException("Falla al insertar fila en : " + uri);
    }


    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {

        SQLiteDatabase db = manejadorBD.getWritableDatabase();

        int filasAfectadas;

        switch (uriMatcher.match(uri)) {
            case TIPOS_PERSONAS:

                filasAfectadas = db.update(Tablas.TIPOS_PERSONAS, values,
                        selection, selectionArgs);

                resolver.notifyChange(uri, null, false);

                break;
            case TIPOS_PERSONAS_ID:

                String id = TiposPersonas.obtenerId(uri);

                filasAfectadas = db.update(Tablas.TIPOS_PERSONAS, values,
                        TiposPersonas.ID + "=" + "\'" + id + "\'"
                                + (!TextUtils.isEmpty(selection) ?
                                " AND (" + selection + ')' : ""),
                        selectionArgs);

                resolver.notifyChange(uri, null, false);

                break;
            default:
                throw new IllegalArgumentException("URI desconocida: " + uri);
        }



        return filasAfectadas;
    }
}