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

import com.softcredito.app.provider.Promocion.Clientes;
import com.softcredito.app.provider.HelperClientes.Tablas;

/**
 * {@link ContentProvider} que encapsula el acceso a la base de datos de clientes
 */
public class ProviderClientes extends ContentProvider {

    // Comparador de URIs de contenido
    public static final UriMatcher uriMatcher;

    // Identificadores de tipos
    public static final int CLIENTES = 100;
    public static final int CLIENTES_ID = 101;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(Promocion.AUTORIDAD, "clientes", CLIENTES);
        uriMatcher.addURI(Promocion.AUTORIDAD, "clientes/*", CLIENTES_ID);
    }

    private HelperClientes manejadorBD;
    private ContentResolver resolver;


    @Override
    public boolean onCreate() {
        manejadorBD = new HelperClientes(getContext());
        resolver = getContext().getContentResolver();
        return true;
    }

    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {
            case CLIENTES:
                return Clientes.MIME_COLECCION;
            case CLIENTES_ID:
                return Clientes.MIME_RECURSO;
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
            case CLIENTES:
                // Consultando todos los registros
                c = db.query(Tablas.CLIENTE, projection,
                        selection, selectionArgs,
                        null, null, sortOrder);
                c.setNotificationUri(resolver, Clientes.URI_CONTENIDO);
                break;
            case CLIENTES_ID:
                // Consultando un solo registro basado en el Id del Uri
                String id = Clientes.obtenerId(uri);
                c = db.query(Tablas.CLIENTE, projection,
                        Clientes.ID + "=" + "\'" + id + "\'"
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
            case CLIENTES:

                filasAfectadas = db.delete(Tablas.CLIENTE,
                        selection,
                        selectionArgs);

                resolver.notifyChange(uri, null, false);

                break;
            case CLIENTES_ID:

                String id = Clientes.obtenerId(uri);

                filasAfectadas = db.delete(Tablas.CLIENTE,
                        Clientes.ID + "=" + "\'" + id + "\'"
                                + (!TextUtils.isEmpty(selection) ?
                                " AND (" + selection + ')' : ""),
                        selectionArgs);

                resolver.notifyChange(uri, null, false);
                break;
            default:
                throw new IllegalArgumentException("Cliente desconocido: " +
                        uri);
        }
        return filasAfectadas;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // Validar la uri
        if (uriMatcher.match(uri) != CLIENTES) {
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

        long _id = db.insert(Tablas.CLIENTE, null, contentValues);

        if (_id > 0) {

            resolver.notifyChange(uri, null, false);

            String id = contentValues.getAsString(Clientes.ID);

            return Clientes.construirUriCliente(id);
        }
        throw new SQLException("Falla al insertar fila en : " + uri);
    }


    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {

        SQLiteDatabase db = manejadorBD.getWritableDatabase();

        int filasAfectadas;

        switch (uriMatcher.match(uri)) {
            case CLIENTES:

                filasAfectadas = db.update(Tablas.CLIENTE, values,
                        selection, selectionArgs);

                resolver.notifyChange(uri, null, false);

                break;
            case CLIENTES_ID:

                String id = Clientes.obtenerId(uri);

                filasAfectadas = db.update(Tablas.CLIENTE, values,
                        Clientes.ID + "=" + "\'" + id + "\'"
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
