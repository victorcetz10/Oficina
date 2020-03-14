package com.softcredito.app.provider;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import com.softcredito.app.modelo.BitacoraCreditoArchivo;
import com.softcredito.app.modelo.EsCliente;
import com.softcredito.app.provider.Contract.TiposContactos;
import com.softcredito.app.provider.Contract.TiposPersonas;
import com.softcredito.app.provider.Contract.EsClientes;
import com.softcredito.app.provider.Contract.Clientes;
import com.softcredito.app.provider.Contract.Grupos;
import com.softcredito.app.provider.Contract.TiposDocumentos;
import com.softcredito.app.provider.Contract.DocumentosRequeridos;
import com.softcredito.app.provider.Contract.DocumentosEntregados;
import com.softcredito.app.provider.Contract.ArchivosDocumentosEntregados;
import com.softcredito.app.provider.Contract.Productos;
import com.softcredito.app.provider.Contract.Bancos;
import com.softcredito.app.provider.Contract.TiposPagos;
import com.softcredito.app.provider.Contract.TiposAmortizacion;
import com.softcredito.app.provider.Contract.StatusSolicitudes;
import com.softcredito.app.provider.Contract.Solicitudes;
import com.softcredito.app.provider.Contract.Cotizadores;
import com.softcredito.app.provider.Contract.BitacorasCredito;
import com.softcredito.app.provider.Contract.BitacorasCreditoArchivos;
import com.softcredito.app.provider.Contract.InstrumentosMonetarios;
import com.softcredito.app.provider.Contract.CanalesCobranzas;
import com.softcredito.app.provider.Contract.Pagos;
import com.softcredito.app.provider.Contract.CodigosPostales;
import com.softcredito.app.provider.Contract.Codigos;
import com.softcredito.app.provider.Contract.Paises;
import com.softcredito.app.provider.Contract.Estados;
import com.softcredito.app.provider.Contract.Municipios;
import com.softcredito.app.provider.Contract.EstadosCiviles;
import com.softcredito.app.provider.Contract.CategoriasActividadesEconomicas;
import com.softcredito.app.provider.Contract.ActividadesEconomicas;
import com.softcredito.app.provider.HelperDB.Tablas;
import com.softcredito.app.utilidades.UTiempo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * {@link ContentProvider} que encapsula el acceso a la base de datos de clientes
 */
public class ProviderSoftcredito extends ContentProvider {

    // Comparador de URIs de contenido
    public static final UriMatcher uriMatcher;
    private final String ruta_expedientes = Environment.getExternalStorageDirectory().getAbsolutePath() +"/softcredito/expedientes/";
    private final String ruta_bitacoras_credito = Environment.getExternalStorageDirectory().getAbsolutePath() +"/softcredito/bitacoras_credito/";

    // Identificadores de tipos
    public static final int CLIENTES = 100;
    public static final int CLIENTES_ID = 101;
    public static final int GRUPOS = 102;
    public static final int GRUPOS_ID = 103;
    public static final int TIPOS_PERSONAS = 104;
    public static final int TIPOS_PERSONAS_ID = 105;
    public static final int TIPOS_CONTACTOS = 106;
    public static final int TIPOS_CONTACTOS_ID = 107;
    public static final int TIPOS_DOCUMENTOS = 108;
    public static final int TIPOS_DOCUMENTOS_ID = 109;
    public static final int DOCUMENTOS_REQUERIDOS = 110;
    public static final int DOCUMENTOS_REQUERIDOS_ID = 111;
    public static final int DOCUMENTOS_REQUERIDOS_CLIENTE = 112;
    public static final int DOCUMENTOS_ENTREGADOS = 113;
    public static final int DOCUMENTOS_ENTREGADOS_ID = 114;
    public static final int ARCHIVOS_DOCUMENTOS_ENTREGADOS = 115;
    public static final int ARCHIVOS_DOCUMENTOS_ENTREGADOS_ID = 116;
    public static final int ARCHIVOS_DOCUMENTOS_ENTREGADOS_REQUERIDO = 117;
    public static final int PRODUCTOS = 118;
    public static final int PRODUCTOS_ID = 119;
    public static final int BANCOS = 120;
    public static final int BANCOS_ID = 121;
    public static final int TIPOS_PAGOS = 122;
    public static final int TIPOS_PAGOS_ID = 123;
    public static final int TIPOS_AMORTIZACION = 124;
    public static final int TIPOS_AMORTIZACION_ID = 125;
    public static final int SOLICITUDES = 126;
    public static final int SOLICITUDES_ID = 127;
    public static final int COTIZADORES = 128;
    public static final int COTIZADORES_ID = 129;
    public static final int BITACORAS_CREDITO = 130;
    public static final int BITACORAS_CREDITO_ID = 131;
    public static final int BITACORAS_CREDITO_ARCHIVOS = 132;
    public static final int BITACORAS_CREDITO_ARCHIVOS_ID = 133;
    public static final int INSTRUMENTOS_MONETARIOS = 134;
    public static final int INSTRUMENTOS_MONETARIOS_ID = 135;
    public static final int CANALES_COBRANZAS = 136;
    public static final int CANALES_COBRANZAS_ID = 137;
    public static final int PAGOS = 138;
    public static final int PAGOS_ID = 139;
    public static final int CODIGOS_POSTALES = 140;
    public static final int CODIGOS_POSTALES_ID = 141;
    public static final int CODIGOS = 142;
    public static final int CODIGOS_ID = 143;
    public static final int ESTADOS = 144;
    public static final int ESTADOS_ID = 145;
    public static final int MUNICIPIOS = 146;
    public static final int MUNICIPIOS_ID = 147;
    public static final int ESTADOS_CIVILES = 148;
    public static final int ESTADOS_CIVILES_ID = 149;
    public static final int CATEGORIAS_ACTIVIDADES_ECONOMICAS = 150;
    public static final int CATEGORIAS_ACTIVIDADES_ECONOMICAS_ID = 151;
    public static final int ACTIVIDADES_ECONOMICAS = 152;
    public static final int ACTIVIDADES_ECONOMICAS_ID = 153;
    public static final int PAISES = 154;
    public static final int PAISES_ID = 155;
    public static final int ES_CLIENTES = 156;
    public static final int ES_CLIENTES_ID = 157;
    public static final int STATUS_SOLICITUDES = 158;
    public static final int STATUS_SOLICITUDES_ID = 159;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(Contract.AUTORIDAD, "es_clientes", ES_CLIENTES);
        uriMatcher.addURI(Contract.AUTORIDAD, "clientes", CLIENTES);
        uriMatcher.addURI(Contract.AUTORIDAD, "clientes/*", CLIENTES_ID);
        uriMatcher.addURI(Contract.AUTORIDAD, "grupos", GRUPOS);
        uriMatcher.addURI(Contract.AUTORIDAD, "grupos/*", GRUPOS_ID);
        uriMatcher.addURI(Contract.AUTORIDAD, "tipos_personas", TIPOS_PERSONAS);
        uriMatcher.addURI(Contract.AUTORIDAD, "tipos_personas/*", TIPOS_PERSONAS_ID);
        uriMatcher.addURI(Contract.AUTORIDAD, "tipos_contactos", TIPOS_CONTACTOS);
        uriMatcher.addURI(Contract.AUTORIDAD, "tipos_contactos/*", TIPOS_CONTACTOS_ID);
        uriMatcher.addURI(Contract.AUTORIDAD, "tipos_documentos", TIPOS_DOCUMENTOS);
        uriMatcher.addURI(Contract.AUTORIDAD, "tipos_documentos/*", TIPOS_DOCUMENTOS_ID);
        uriMatcher.addURI(Contract.AUTORIDAD, "documentos_requeridos", DOCUMENTOS_REQUERIDOS);
        uriMatcher.addURI(Contract.AUTORIDAD, "documentos_requeridos/*", DOCUMENTOS_REQUERIDOS_ID);
        uriMatcher.addURI(Contract.AUTORIDAD, "documentos_requeridos_cliente/*", DOCUMENTOS_REQUERIDOS_CLIENTE);
        uriMatcher.addURI(Contract.AUTORIDAD, "documentos_entregados", DOCUMENTOS_ENTREGADOS);
        uriMatcher.addURI(Contract.AUTORIDAD, "documentos_entregados/*", DOCUMENTOS_ENTREGADOS_ID);
        uriMatcher.addURI(Contract.AUTORIDAD, "archivos_documentos_entregados", ARCHIVOS_DOCUMENTOS_ENTREGADOS);
        uriMatcher.addURI(Contract.AUTORIDAD, "archivos_documentos_entregados/*", ARCHIVOS_DOCUMENTOS_ENTREGADOS_ID);
        uriMatcher.addURI(Contract.AUTORIDAD, "archivos_documentos_entregados_requerido/*", ARCHIVOS_DOCUMENTOS_ENTREGADOS_REQUERIDO);
        uriMatcher.addURI(Contract.AUTORIDAD, "productos", PRODUCTOS);
        uriMatcher.addURI(Contract.AUTORIDAD, "productos/*", PRODUCTOS_ID);
        uriMatcher.addURI(Contract.AUTORIDAD, "bancos", BANCOS);
        uriMatcher.addURI(Contract.AUTORIDAD, "bancos/*", BANCOS_ID);
        uriMatcher.addURI(Contract.AUTORIDAD, "tipos_pagos", TIPOS_PAGOS);
        uriMatcher.addURI(Contract.AUTORIDAD, "tipos_pagos/*", TIPOS_PAGOS_ID);
        uriMatcher.addURI(Contract.AUTORIDAD, "tipos_amortizacion", TIPOS_AMORTIZACION);
        uriMatcher.addURI(Contract.AUTORIDAD, "tipos_amortizacion/*", TIPOS_AMORTIZACION_ID);
        uriMatcher.addURI(Contract.AUTORIDAD, "status_solicitudes", STATUS_SOLICITUDES);
        uriMatcher.addURI(Contract.AUTORIDAD, "status_solicitudes/*", STATUS_SOLICITUDES_ID);
        uriMatcher.addURI(Contract.AUTORIDAD, "solicitudes", SOLICITUDES);
        uriMatcher.addURI(Contract.AUTORIDAD, "solicitudes/*", SOLICITUDES_ID);
        uriMatcher.addURI(Contract.AUTORIDAD, "cotizadores", COTIZADORES);
        uriMatcher.addURI(Contract.AUTORIDAD, "cotizadores/*", COTIZADORES_ID);
        uriMatcher.addURI(Contract.AUTORIDAD, "bitacoras_credito", BITACORAS_CREDITO);
        uriMatcher.addURI(Contract.AUTORIDAD, "bitacoras_credito/*", BITACORAS_CREDITO_ID);
        uriMatcher.addURI(Contract.AUTORIDAD, "bitacoras_credito_archivos", BITACORAS_CREDITO_ARCHIVOS);
        uriMatcher.addURI(Contract.AUTORIDAD, "bitacoras_credito_archivos/*", BITACORAS_CREDITO_ARCHIVOS_ID);
        uriMatcher.addURI(Contract.AUTORIDAD, "instrumentos_monetarios", INSTRUMENTOS_MONETARIOS);
        uriMatcher.addURI(Contract.AUTORIDAD, "instrumentos_monetarios/*", INSTRUMENTOS_MONETARIOS_ID);
        uriMatcher.addURI(Contract.AUTORIDAD, "canales_cobranzas", CANALES_COBRANZAS);
        uriMatcher.addURI(Contract.AUTORIDAD, "canales_cobranzas/*", CANALES_COBRANZAS_ID);
        uriMatcher.addURI(Contract.AUTORIDAD, "pagos", PAGOS);
        uriMatcher.addURI(Contract.AUTORIDAD, "pagos/*", PAGOS_ID);
        uriMatcher.addURI(Contract.AUTORIDAD, "codigos_postales", CODIGOS_POSTALES);
        uriMatcher.addURI(Contract.AUTORIDAD, "codigos_postales/*", CODIGOS_POSTALES_ID);
        uriMatcher.addURI(Contract.AUTORIDAD, "codigos", CODIGOS);
        uriMatcher.addURI(Contract.AUTORIDAD, "codigos/*", CODIGOS_ID);
        uriMatcher.addURI(Contract.AUTORIDAD, "estados", ESTADOS);
        uriMatcher.addURI(Contract.AUTORIDAD, "estados/*", ESTADOS_ID);
        uriMatcher.addURI(Contract.AUTORIDAD, "paises", PAISES);
        uriMatcher.addURI(Contract.AUTORIDAD, "paises/*", PAISES_ID);
        uriMatcher.addURI(Contract.AUTORIDAD, "municipios", MUNICIPIOS);
        uriMatcher.addURI(Contract.AUTORIDAD, "municipios/*", MUNICIPIOS_ID);
        uriMatcher.addURI(Contract.AUTORIDAD, "estados_civiles", ESTADOS_CIVILES);
        uriMatcher.addURI(Contract.AUTORIDAD, "estados_civiles/*", ESTADOS_CIVILES_ID);
        uriMatcher.addURI(Contract.AUTORIDAD, "categorias_actividades_economicas", CATEGORIAS_ACTIVIDADES_ECONOMICAS);
        uriMatcher.addURI(Contract.AUTORIDAD, "categorias_actividades_economicas/*", CATEGORIAS_ACTIVIDADES_ECONOMICAS_ID);
        uriMatcher.addURI(Contract.AUTORIDAD, "actividades_economicas", ACTIVIDADES_ECONOMICAS);
        uriMatcher.addURI(Contract.AUTORIDAD, "actividades_economicas/*", ACTIVIDADES_ECONOMICAS_ID);
    }

    private HelperDB manejadorBD;
    private ContentResolver resolver;


    @Override
    public boolean onCreate() {
        manejadorBD = new HelperDB(getContext());
        resolver = getContext().getContentResolver();
        return true;
    }

    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {
            case ES_CLIENTES:
                return EsClientes.MIME_COLECCION;
            case ES_CLIENTES_ID:
                return EsClientes.MIME_RECURSO;
            case CLIENTES:
                return Clientes.MIME_COLECCION;
            case CLIENTES_ID:
                return Clientes.MIME_RECURSO;
            case GRUPOS:
                return Grupos.MIME_COLECCION;
            case GRUPOS_ID:
                return Grupos.MIME_RECURSO;
            case TIPOS_PERSONAS:
                return TiposPersonas.MIME_COLECCION;
            case TIPOS_PERSONAS_ID:
                return TiposPersonas.MIME_RECURSO;
            case TIPOS_CONTACTOS:
                return TiposContactos.MIME_COLECCION;
            case TIPOS_CONTACTOS_ID:
                return TiposContactos.MIME_RECURSO;
            case TIPOS_DOCUMENTOS:
                return TiposDocumentos.MIME_COLECCION;
            case TIPOS_DOCUMENTOS_ID:
                return TiposDocumentos.MIME_RECURSO;
            case DOCUMENTOS_REQUERIDOS:
                return DocumentosRequeridos.MIME_COLECCION;
            case DOCUMENTOS_REQUERIDOS_ID:
                return DocumentosRequeridos.MIME_RECURSO;
            case DOCUMENTOS_REQUERIDOS_CLIENTE:
                return DocumentosRequeridos.MIME_COLECCION_CLIENTE;
            case DOCUMENTOS_ENTREGADOS:
                return DocumentosEntregados.MIME_COLECCION;
            case DOCUMENTOS_ENTREGADOS_ID:
                return DocumentosEntregados.MIME_RECURSO;
            case ARCHIVOS_DOCUMENTOS_ENTREGADOS:
                return ArchivosDocumentosEntregados.MIME_COLECCION;
            case ARCHIVOS_DOCUMENTOS_ENTREGADOS_ID:
                return ArchivosDocumentosEntregados.MIME_RECURSO;
            case ARCHIVOS_DOCUMENTOS_ENTREGADOS_REQUERIDO:
                return ArchivosDocumentosEntregados.MIME_COLECCION_REQUERIDO;
            case PRODUCTOS:
                return Productos.MIME_COLECCION;
            case PRODUCTOS_ID:
                return Productos.MIME_RECURSO;
            case BANCOS:
                return Bancos.MIME_COLECCION;
            case BANCOS_ID:
                return Bancos.MIME_RECURSO;
            case TIPOS_PAGOS:
                return TiposPagos.MIME_COLECCION;
            case TIPOS_PAGOS_ID:
                return TiposPagos.MIME_RECURSO;
            case TIPOS_AMORTIZACION:
                return TiposAmortizacion.MIME_COLECCION;
            case TIPOS_AMORTIZACION_ID:
                return TiposAmortizacion.MIME_RECURSO;
            case STATUS_SOLICITUDES:
                return StatusSolicitudes.MIME_COLECCION;
            case STATUS_SOLICITUDES_ID:
                return TiposAmortizacion.MIME_RECURSO;
            case SOLICITUDES:
                return Solicitudes.MIME_COLECCION;
            case SOLICITUDES_ID:
                return Solicitudes.MIME_RECURSO;
            case COTIZADORES:
                return Cotizadores.MIME_COLECCION;
            case COTIZADORES_ID:
                return Cotizadores.MIME_RECURSO;
            case BITACORAS_CREDITO:
                return BitacorasCredito.MIME_COLECCION;
            case BITACORAS_CREDITO_ID:
                return BitacorasCredito.MIME_RECURSO;
            case BITACORAS_CREDITO_ARCHIVOS:
                return BitacorasCreditoArchivos.MIME_COLECCION;
            case BITACORAS_CREDITO_ARCHIVOS_ID:
                return BitacorasCreditoArchivos.MIME_RECURSO;
            case INSTRUMENTOS_MONETARIOS:
                return InstrumentosMonetarios.MIME_COLECCION;
            case INSTRUMENTOS_MONETARIOS_ID:
                return InstrumentosMonetarios.MIME_RECURSO;
            case CANALES_COBRANZAS:
                return CanalesCobranzas.MIME_COLECCION;
            case CANALES_COBRANZAS_ID:
                return CanalesCobranzas.MIME_RECURSO;
            case PAGOS:
                return Pagos.MIME_COLECCION;
            case PAGOS_ID:
                return Pagos.MIME_RECURSO;
            case CODIGOS_POSTALES:
                return CodigosPostales.MIME_COLECCION;
            case CODIGOS_POSTALES_ID:
                return CodigosPostales.MIME_RECURSO;
            case CODIGOS:
                return Codigos.MIME_COLECCION;
            case CODIGOS_ID:
                return Codigos.MIME_RECURSO;
            case PAISES:
                return Paises.MIME_COLECCION;
            case PAISES_ID:
                return Estados.MIME_RECURSO;
            case ESTADOS:
                return Estados.MIME_COLECCION;
            case ESTADOS_ID:
                return Estados.MIME_RECURSO;
            case MUNICIPIOS:
                return Municipios.MIME_COLECCION;
            case MUNICIPIOS_ID:
                return Municipios.MIME_RECURSO;
            case ESTADOS_CIVILES:
                return EstadosCiviles.MIME_COLECCION;
            case ESTADOS_CIVILES_ID:
                return EstadosCiviles.MIME_RECURSO;
            case CATEGORIAS_ACTIVIDADES_ECONOMICAS:
                return CategoriasActividadesEconomicas.MIME_COLECCION;
            case CATEGORIAS_ACTIVIDADES_ECONOMICAS_ID:
                return CategoriasActividadesEconomicas.MIME_RECURSO;
            case ACTIVIDADES_ECONOMICAS:
                return ActividadesEconomicas.MIME_COLECCION;
            case ACTIVIDADES_ECONOMICAS_ID:
                return ActividadesEconomicas.MIME_RECURSO;
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
        String id;
        String pathdb;

        //pathdb=getContext().getDatabasePath(manejadorBD.getDatabaseName()).toString();

        switch (match) {
            case ES_CLIENTES:
                // Consultando todos los registros
                c = db.query(Tablas.ES_CLIENTES, projection,
                        selection, selectionArgs,
                        null, null, sortOrder);
                c.setNotificationUri(resolver, EsClientes.URI_CONTENIDO);
                break;
            case ES_CLIENTES_ID:
                // Consultando un solo registro basado en el Id del Uri
                id = EsClientes.obtenerId(uri);
                c = db.query(Tablas.ES_CLIENTES, projection,
                        EsClientes.ID + "=" + "\'" + id + "\'"
                                + (!TextUtils.isEmpty(selection) ?
                                " AND (" + selection + ')' : ""),
                        selectionArgs, null, null, sortOrder);
                c.setNotificationUri(resolver, uri);
                break;
            case CLIENTES:
                // Consultando todos los registros
                c = db.query(Tablas.CLIENTES, projection,
                        selection, selectionArgs,
                        null, null, sortOrder);
                c.setNotificationUri(resolver, Clientes.URI_CONTENIDO);
                break;
            case CLIENTES_ID:
                // Consultando un solo registro basado en el Id del Uri
                id = Clientes.obtenerId(uri);
                c = db.query(Tablas.CLIENTES, projection,
                        Clientes.ID + "=" + "\'" + id + "\'"
                                + (!TextUtils.isEmpty(selection) ?
                                " AND (" + selection + ')' : ""),
                        selectionArgs, null, null, sortOrder);
                c.setNotificationUri(resolver, uri);
                break;
            case GRUPOS:
                // Consultando todos los registros
                c = db.query(Tablas.GRUPOS, projection,
                        selection, selectionArgs,
                        null, null, sortOrder);
                c.setNotificationUri(resolver, Grupos.URI_CONTENIDO);
                break;
            case GRUPOS_ID:
                // Consultando un solo registro basado en el Id del Uri
                id = Grupos.obtenerId(uri);
                c = db.query(Tablas.GRUPOS, projection,
                        Grupos.ID + "=" + "\'" + id + "\'"
                                + (!TextUtils.isEmpty(selection) ?
                                " AND (" + selection + ')' : ""),
                        selectionArgs, null, null, sortOrder);
                c.setNotificationUri(resolver, uri);
                break;
            case TIPOS_PERSONAS:
                // Consultando todos los registros
                c = db.query(Tablas.TIPOS_PERSONAS, projection,
                        selection, selectionArgs,
                        null, null, sortOrder);
                c.setNotificationUri(resolver, TiposPersonas.URI_CONTENIDO);
                break;
            case TIPOS_PERSONAS_ID:
                // Consultando un solo registro basado en el Id del Uri
                id = TiposPersonas.obtenerId(uri);
                c = db.query(Tablas.TIPOS_PERSONAS, projection,
                        TiposPersonas.ID + "=" + "\'" + id + "\'"
                                + (!TextUtils.isEmpty(selection) ?
                                " AND (" + selection + ')' : ""),
                        selectionArgs, null, null, sortOrder);
                c.setNotificationUri(resolver, uri);
                break;
            case TIPOS_CONTACTOS:
                // Consultando todos los registros
                c = db.query(Tablas.TIPOS_CONTACTOS, projection,
                        selection, selectionArgs,
                        null, null, sortOrder);
                c.setNotificationUri(resolver, TiposContactos.URI_CONTENIDO);
                break;
            case TIPOS_CONTACTOS_ID:
                // Consultando un solo registro basado en el Id del Uri
                id = TiposContactos.obtenerId(uri);
                c = db.query(Tablas.TIPOS_CONTACTOS, projection,
                        TiposContactos.ID + "=" + "\'" + id + "\'"
                                + (!TextUtils.isEmpty(selection) ?
                                " AND (" + selection + ')' : ""),
                        selectionArgs, null, null, sortOrder);
                c.setNotificationUri(resolver, uri);
                break;
            case TIPOS_DOCUMENTOS:
                // Consultando todos los registros
                c = db.query(Tablas.TIPOS_DOCUMENTOS, projection,
                        selection, selectionArgs,
                        null, null, sortOrder);
                c.setNotificationUri(resolver, TiposDocumentos.URI_CONTENIDO);
                break;
            case TIPOS_DOCUMENTOS_ID:
                // Consultando un solo registro basado en el Id del Uri
                id = TiposDocumentos.obtenerId(uri);
                c = db.query(Tablas.TIPOS_DOCUMENTOS, projection,
                        TiposDocumentos.ID + "=" + "\'" + id + "\'"
                                + (!TextUtils.isEmpty(selection) ?
                                " AND (" + selection + ')' : ""),
                        selectionArgs, null, null, sortOrder);
                c.setNotificationUri(resolver, uri);
                break;
            case DOCUMENTOS_REQUERIDOS:
                // Consultando todos los registros
                c = db.query(Tablas.DOCUMENTOS_REQUERIDOS, projection,
                        selection, selectionArgs,
                        null, null, sortOrder);
                c.setNotificationUri(resolver, DocumentosRequeridos.URI_CONTENIDO);
                break;
            case DOCUMENTOS_REQUERIDOS_ID:
                // Consultando un solo registro basado en el Id del Uri
                id = DocumentosRequeridos.obtenerId(uri);
                c = db.query(Tablas.DOCUMENTOS_REQUERIDOS, projection,
                        DocumentosRequeridos.ID + "=" + "\'" + id + "\'"
                                + (!TextUtils.isEmpty(selection) ?
                                " AND (" + selection + ')' : ""),
                        selectionArgs, null, null, sortOrder);
                c.setNotificationUri(resolver, uri);
                break;
            case DOCUMENTOS_REQUERIDOS_CLIENTE:
                // Consultando los registros basado en el cliente del Uri
                id = DocumentosRequeridos.obtenerId(uri);
                c = db.rawQuery(
                        "SELECT "
                                + " dr.id, "
                                + " dr.nombre, "
                                + " de.id AS id_documento_entregado, "
                                + " '" + id +"' AS id_cliente, "
                                + " dr.id_tipo_documento, "
                                + " dr.descripcion, "
                                + " dr.opcional, "
                                + " td.nombre AS tipo_documento, "
                                + " CASE "
                                + " WHEN de.id IS NULL THEN "
                                + " 'FALSE' "
                                + " WHEN de.status=1 THEN "
                                + " 'TRUE' "
                                + " ELSE "
                                + " 'FALSE' "
                                + " END AS entregado, "
                                + " dr.version, "
                                + " dr.modificado, "
                                + " dr.eliminado "
                        + "FROM "
                                + Tablas.DOCUMENTOS_REQUERIDOS + " dr LEFT JOIN "
                                + Tablas.TIPOS_DOCUMENTOS + " td ON(dr.id_tipo_documento=td.id) LEFT JOIN "
                                + Tablas.DOCUMENTOS_ENTREGADOS + " de ON(dr.id=de.id_documento_requerido AND de.id_cliente='" + id + "') "
                        + "ORDER BY td.id"
                        ,
                        null
                );
                c.setNotificationUri(resolver, DocumentosRequeridos.URI_CONTENIDO_CLIENTE);
                break;
            case DOCUMENTOS_ENTREGADOS:
                // Consultando todos los registros
                c = db.query(Tablas.DOCUMENTOS_ENTREGADOS, projection,
                        selection, selectionArgs,
                        null, null, sortOrder);
                c.setNotificationUri(resolver, DocumentosEntregados.URI_CONTENIDO);
                break;
            case DOCUMENTOS_ENTREGADOS_ID:
                // Consultando un solo registro basado en el Id del Uri
                id = DocumentosEntregados.obtenerId(uri);
                c = db.query(Tablas.DOCUMENTOS_ENTREGADOS, projection,
                        DocumentosEntregados.ID + "=" + "\'" + id + "\'"
                                + (!TextUtils.isEmpty(selection) ?
                                " AND (" + selection + ')' : ""),
                        selectionArgs, null, null, sortOrder);
                c.setNotificationUri(resolver, uri);
                break;
            case ARCHIVOS_DOCUMENTOS_ENTREGADOS:
                // Consultando todos los registros
                c = db.query(Tablas.ARCHIVOS_DOCUMENTOS_ENTREGADOS, projection,
                        selection, selectionArgs,
                        null, null, sortOrder);
                c.setNotificationUri(resolver, ArchivosDocumentosEntregados.URI_CONTENIDO);
                break;
            case ARCHIVOS_DOCUMENTOS_ENTREGADOS_ID:
                // Consultando un solo registro basado en el Id del Uri
                id = ArchivosDocumentosEntregados.obtenerId(uri);
                c = db.query(Tablas.ARCHIVOS_DOCUMENTOS_ENTREGADOS, projection,
                        ArchivosDocumentosEntregados.ID + "=" + "\'" + id + "\'"
                                + (!TextUtils.isEmpty(selection) ?
                                " AND (" + selection + ')' : ""),
                        selectionArgs, null, null, sortOrder);
                c.setNotificationUri(resolver, uri);
                break;
            case ARCHIVOS_DOCUMENTOS_ENTREGADOS_REQUERIDO:
                // Consultando los registros basado en el documento requerido
                id = ArchivosDocumentosEntregados.obtenerId(uri);
                c = db.rawQuery(
                        "SELECT "
                                + " ade.id, "
                                + " ade.descripcion, "
                                + " ade.fecha, "
                                + " ade.ruta, "
                                + " ade.tipo "
                        + "FROM "
                                + Tablas.ARCHIVOS_DOCUMENTOS_ENTREGADOS + " ade LEFT JOIN "
                                + Tablas.DOCUMENTOS_ENTREGADOS + " de ON(ade.id_documento_entregado=de.id) "
                        + "WHERE "
                                + "de.id_documento_requerido=? AND "
                                + "de.id_cliente=? AND "
                                + "ade.eliminado!=? "
                        + "ORDER BY ade.fecha"
                        ,
                        new String[]{id,selectionArgs[0],"1"}//El argumento 0 es el id del cliente, no se deben mostrar los eliminados
                );
                c.setNotificationUri(resolver, ArchivosDocumentosEntregados.URI_CONTENIDO_REQUERIDO);
                break;
            case PRODUCTOS:
                // Consultando todos los registros
                c = db.query(Tablas.PRODUCTOS, projection,
                        selection, selectionArgs,
                        null, null, sortOrder);
                c.setNotificationUri(resolver, Productos.URI_CONTENIDO);
                break;
            case PRODUCTOS_ID:
                // Consultando un solo registro basado en el Id del Uri
                id = Productos.obtenerId(uri);
                c = db.query(Tablas.PRODUCTOS, projection,
                        Productos.ID + "=" + "\'" + id + "\'"
                                + (!TextUtils.isEmpty(selection) ?
                                " AND (" + selection + ')' : ""),
                        selectionArgs, null, null, sortOrder);
                c.setNotificationUri(resolver, uri);
                break;
            case BANCOS:
                // Consultando todos los registros
                c = db.query(Tablas.BANCOS, projection,
                        selection, selectionArgs,
                        null, null, sortOrder);
                c.setNotificationUri(resolver, Bancos.URI_CONTENIDO);
                break;
            case BANCOS_ID:
                // Consultando un solo registro basado en el Id del Uri
                id = Bancos.obtenerId(uri);
                c = db.query(Tablas.BANCOS, projection,
                        Bancos.ID + "=" + "\'" + id + "\'"
                                + (!TextUtils.isEmpty(selection) ?
                                " AND (" + selection + ')' : ""),
                        selectionArgs, null, null, sortOrder);
                c.setNotificationUri(resolver, uri);
                break;
            case TIPOS_PAGOS:
                // Consultando todos los registros
                c = db.query(Tablas.TIPOS_PAGOS, projection,
                        selection, selectionArgs,
                        null, null, sortOrder);
                c.setNotificationUri(resolver, TiposPagos.URI_CONTENIDO);
                break;
            case TIPOS_PAGOS_ID:
                // Consultando un solo registro basado en el Id del Uri
                id = TiposPagos.obtenerId(uri);
                c = db.query(Tablas.TIPOS_PAGOS, projection,
                        TiposPagos.ID + "=" + "\'" + id + "\'"
                                + (!TextUtils.isEmpty(selection) ?
                                " AND (" + selection + ')' : ""),
                        selectionArgs, null, null, sortOrder);
                c.setNotificationUri(resolver, uri);
                break;
            case TIPOS_AMORTIZACION:
                // Consultando todos los registros
                c = db.query(Tablas.TIPOS_AMORTIZACION, projection,
                        selection, selectionArgs,
                        null, null, sortOrder);
                c.setNotificationUri(resolver, TiposAmortizacion.URI_CONTENIDO);
                break;
            case TIPOS_AMORTIZACION_ID:
                // Consultando un solo registro basado en el Id del Uri
                id = TiposAmortizacion.obtenerId(uri);
                c = db.query(Tablas.TIPOS_AMORTIZACION, projection,
                        TiposAmortizacion.ID + "=" + "\'" + id + "\'"
                                + (!TextUtils.isEmpty(selection) ?
                                " AND (" + selection + ')' : ""),
                        selectionArgs, null, null, sortOrder);
                c.setNotificationUri(resolver, uri);
                break;
            case STATUS_SOLICITUDES:
                // Consultando todos los registros
                c = db.query(Tablas.STATUS_SOLICITUDES, projection,
                        selection, selectionArgs,
                        null, null, sortOrder);
                c.setNotificationUri(resolver, StatusSolicitudes.URI_CONTENIDO);
                break;
            case STATUS_SOLICITUDES_ID:
                // Consultando un solo registro basado en el Id del Uri
                id = StatusSolicitudes.obtenerId(uri);
                c = db.query(Tablas.STATUS_SOLICITUDES, projection,
                        StatusSolicitudes.ID + "=" + "\'" + id + "\'"
                                + (!TextUtils.isEmpty(selection) ?
                                " AND (" + selection + ')' : ""),
                        selectionArgs, null, null, sortOrder);
                c.setNotificationUri(resolver, uri);
                break;
            case SOLICITUDES:
                // Consultando todos los registros
                c = db.query(Tablas.SOLICITUDES, projection,
                        selection, selectionArgs,
                        null, null, sortOrder);
                c.setNotificationUri(resolver, Solicitudes.URI_CONTENIDO);
                break;
            case SOLICITUDES_ID:
                // Consultando un solo registro basado en el Id del Uri
                id = Solicitudes.obtenerId(uri);
                c = db.query(Tablas.SOLICITUDES, projection,
                        Solicitudes.ID + "=" + "\'" + id + "\'"
                                + (!TextUtils.isEmpty(selection) ?
                                " AND (" + selection + ')' : ""),
                        selectionArgs, null, null, sortOrder);
                c.setNotificationUri(resolver, uri);
                break;
            case COTIZADORES:
                // Consultando todos los registros
                c = db.query(Tablas.COTIZADORES, projection,
                        selection, selectionArgs,
                        null, null, sortOrder);
                c.setNotificationUri(resolver, Cotizadores.URI_CONTENIDO);
                break;
            case COTIZADORES_ID:
                // Consultando un solo registro basado en el Id del Uri
                id = Cotizadores.obtenerId(uri);
                c = db.query(Tablas.COTIZADORES, projection,
                        Cotizadores.ID + "=" + "\'" + id + "\'"
                                + (!TextUtils.isEmpty(selection) ?
                                " AND (" + selection + ')' : ""),
                        selectionArgs, null, null, sortOrder);
                c.setNotificationUri(resolver, uri);
                break;
            case BITACORAS_CREDITO:
                // Consultando todos los registros
                c = db.query(Tablas.BITACORAS_CREDITO, projection,
                        selection, selectionArgs,
                        null, null, sortOrder);
                c.setNotificationUri(resolver, BitacorasCredito.URI_CONTENIDO);
                break;
            case BITACORAS_CREDITO_ID:
                // Consultando un solo registro basado en el Id del Uri
                id = BitacorasCredito.obtenerId(uri);
                c = db.query(Tablas.BITACORAS_CREDITO, projection,
                        BitacorasCredito.ID + "=" + "\'" + id + "\'"
                                + (!TextUtils.isEmpty(selection) ?
                                " AND (" + selection + ')' : ""),
                        selectionArgs, null, null, sortOrder);
                c.setNotificationUri(resolver, uri);
                break;
            case BITACORAS_CREDITO_ARCHIVOS:
                // Consultando todos los registros
                c = db.query(Tablas.BITACORAS_CREDITO_ARCHIVOS, projection,
                        selection, selectionArgs,
                        null, null, sortOrder);
                c.setNotificationUri(resolver, BitacorasCreditoArchivos.URI_CONTENIDO);
                break;
            case BITACORAS_CREDITO_ARCHIVOS_ID:
                // Consultando un solo registro basado en el Id del Uri
                id = BitacorasCreditoArchivos.obtenerId(uri);
                c = db.query(Tablas.BITACORAS_CREDITO_ARCHIVOS, projection,
                        BitacorasCreditoArchivos.ID + "=" + "\'" + id + "\'"
                                + (!TextUtils.isEmpty(selection) ?
                                " AND (" + selection + ')' : ""),
                        selectionArgs, null, null, sortOrder);
                c.setNotificationUri(resolver, uri);
                break;
            case INSTRUMENTOS_MONETARIOS:
                // Consultando todos los registros
                c = db.query(Tablas.INSTRUMENTOS_MONETARIOS, projection,
                        selection, selectionArgs,
                        null, null, sortOrder);
                c.setNotificationUri(resolver, InstrumentosMonetarios.URI_CONTENIDO);
                break;
            case INSTRUMENTOS_MONETARIOS_ID:
                // Consultando un solo registro basado en el Id del Uri
                id = InstrumentosMonetarios.obtenerId(uri);
                c = db.query(Tablas.INSTRUMENTOS_MONETARIOS, projection,
                        InstrumentosMonetarios.ID + "=" + "\'" + id + "\'"
                                + (!TextUtils.isEmpty(selection) ?
                                " AND (" + selection + ')' : ""),
                        selectionArgs, null, null, sortOrder);
                c.setNotificationUri(resolver, uri);
                break;
            case CANALES_COBRANZAS:
                // Consultando todos los registros
                c = db.query(Tablas.CANALES_COBRANZAS, projection,
                        selection, selectionArgs,
                        null, null, sortOrder);
                c.setNotificationUri(resolver, CanalesCobranzas.URI_CONTENIDO);
                break;
            case CANALES_COBRANZAS_ID:
                // Consultando un solo registro basado en el Id del Uri
                id = CanalesCobranzas.obtenerId(uri);
                c = db.query(Tablas.CANALES_COBRANZAS, projection,
                        CanalesCobranzas.ID + "=" + "\'" + id + "\'"
                                + (!TextUtils.isEmpty(selection) ?
                                " AND (" + selection + ')' : ""),
                        selectionArgs, null, null, sortOrder);
                c.setNotificationUri(resolver, uri);
                break;
            case PAGOS:
                // Consultando todos los registros
                c = db.query(Tablas.PAGOS, projection,
                        selection, selectionArgs,
                        null, null, sortOrder);
                c.setNotificationUri(resolver, Pagos.URI_CONTENIDO);
                break;
            case PAGOS_ID:
                // Consultando un solo registro basado en el Id del Uri
                id = Pagos.obtenerId(uri);
                c = db.query(Tablas.PAGOS, projection,
                        Pagos.ID + "=" + "\'" + id + "\'"
                                + (!TextUtils.isEmpty(selection) ?
                                " AND (" + selection + ')' : ""),
                        selectionArgs, null, null, sortOrder);
                c.setNotificationUri(resolver, uri);
                break;
            case CODIGOS_POSTALES:
                // Consultando todos los registros
                c = db.query(Tablas.CODIGOS_POSTALES, projection,
                        selection, selectionArgs,
                        null, null, sortOrder);
                c.setNotificationUri(resolver, CodigosPostales.URI_CONTENIDO);
                break;
            case CODIGOS_POSTALES_ID:
                // Consultando un solo registro basado en el Id del Uri
                id = CodigosPostales.obtenerId(uri);
                c = db.query(Tablas.CODIGOS_POSTALES, projection,
                        CodigosPostales.ID + "=" + "\'" + id + "\'"
                                + (!TextUtils.isEmpty(selection) ?
                                " AND (" + selection + ')' : ""),
                        selectionArgs, null, null, sortOrder);
                c.setNotificationUri(resolver, uri);
                break;
            case CODIGOS:
                // Consultando todos los registros
                c = db.query(Tablas.CODIGOS, projection,
                        selection, selectionArgs,
                        null, null, sortOrder);
                c.setNotificationUri(resolver, Codigos.URI_CONTENIDO);
                break;
            case CODIGOS_ID:
                // Consultando un solo registro basado en el Id del Uri
                id = Codigos.obtenerId(uri);
                c = db.query(Tablas.CODIGOS, projection,
                        Codigos.ID + "=" + "\'" + id + "\'"
                                + (!TextUtils.isEmpty(selection) ?
                                " AND (" + selection + ')' : ""),
                        selectionArgs, null, null, sortOrder);
                c.setNotificationUri(resolver, uri);
                break;
            case PAISES:
                // Consultando todos los registros
                c = db.query(Tablas.PAISES, projection,
                        selection, selectionArgs,
                        null, null, sortOrder);
                c.setNotificationUri(resolver, Paises.URI_CONTENIDO);
                break;
            case PAISES_ID:
                // Consultando un solo registro basado en el Id del Uri
                id = Paises.obtenerId(uri);
                c = db.query(Tablas.PAISES, projection,
                        Paises.ID + "=" + "\'" + id + "\'"
                                + (!TextUtils.isEmpty(selection) ?
                                " AND (" + selection + ')' : ""),
                        selectionArgs, null, null, sortOrder);
                c.setNotificationUri(resolver, uri);
                break;
            case ESTADOS:
                // Consultando todos los registros
                c = db.query(Tablas.ESTADOS, projection,
                        selection, selectionArgs,
                        null, null, sortOrder);
                c.setNotificationUri(resolver, Estados.URI_CONTENIDO);
                break;
            case ESTADOS_ID:
                // Consultando un solo registro basado en el Id del Uri
                id = Estados.obtenerId(uri);
                c = db.query(Tablas.ESTADOS, projection,
                        Estados.ID + "=" + "\'" + id + "\'"
                                + (!TextUtils.isEmpty(selection) ?
                                " AND (" + selection + ')' : ""),
                        selectionArgs, null, null, sortOrder);
                c.setNotificationUri(resolver, uri);
                break;
            case MUNICIPIOS:
                // Consultando todos los registros
                c = db.query(Tablas.MUNICIPIOS, projection,
                        selection, selectionArgs,
                        null, null, sortOrder);
                c.setNotificationUri(resolver, Municipios.URI_CONTENIDO);
                break;
            case MUNICIPIOS_ID:
                // Consultando un solo registro basado en el Id del Uri
                id = Municipios.obtenerId(uri);
                c = db.query(Tablas.MUNICIPIOS, projection,
                        Municipios.ID + "=" + "\'" + id + "\'"
                                + (!TextUtils.isEmpty(selection) ?
                                " AND (" + selection + ')' : ""),
                        selectionArgs, null, null, sortOrder);
                c.setNotificationUri(resolver, uri);
                break;
            case ESTADOS_CIVILES:
                // Consultando todos los registros
                c = db.query(Tablas.ESTADOS_CIVILES, projection,
                        selection, selectionArgs,
                        null, null, sortOrder);
                c.setNotificationUri(resolver, EstadosCiviles.URI_CONTENIDO);
                break;
            case ESTADOS_CIVILES_ID:
                // Consultando un solo registro basado en el Id del Uri
                id = EstadosCiviles.obtenerId(uri);
                c = db.query(Tablas.ESTADOS_CIVILES, projection,
                        EstadosCiviles.ID + "=" + "\'" + id + "\'"
                                + (!TextUtils.isEmpty(selection) ?
                                " AND (" + selection + ')' : ""),
                        selectionArgs, null, null, sortOrder);
                c.setNotificationUri(resolver, uri);
                break;
            case CATEGORIAS_ACTIVIDADES_ECONOMICAS:
                // Consultando todos los registros
                c = db.query(Tablas.CATEGORIAS_ACTIVIDADES_ECONOMICAS, projection,
                        selection, selectionArgs,
                        null, null, sortOrder);
                c.setNotificationUri(resolver, CategoriasActividadesEconomicas.URI_CONTENIDO);
                break;
            case CATEGORIAS_ACTIVIDADES_ECONOMICAS_ID:
                // Consultando un solo registro basado en el Id del Uri
                id = CategoriasActividadesEconomicas.obtenerId(uri);
                c = db.query(Tablas.CATEGORIAS_ACTIVIDADES_ECONOMICAS, projection,
                        CategoriasActividadesEconomicas.ID + "=" + "\'" + id + "\'"
                                + (!TextUtils.isEmpty(selection) ?
                                " AND (" + selection + ')' : ""),
                        selectionArgs, null, null, sortOrder);
                c.setNotificationUri(resolver, uri);
                break;
            case ACTIVIDADES_ECONOMICAS:
                // Consultando todos los registros
                c = db.query(Tablas.ACTIVIDADES_ECONOMICAS, projection,
                        selection, selectionArgs,
                        null, null, sortOrder);
                c.setNotificationUri(resolver, ActividadesEconomicas.URI_CONTENIDO);
                break;
            case ACTIVIDADES_ECONOMICAS_ID:
                // Consultando un solo registro basado en el Id del Uri
                id = ActividadesEconomicas.obtenerId(uri);
                c = db.query(Tablas.ACTIVIDADES_ECONOMICAS, projection,
                        ActividadesEconomicas.ID + "=" + "\'" + id + "\'"
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
        String id;

        switch (match) {
            case ES_CLIENTES:
                filasAfectadas = db.delete(Tablas.ES_CLIENTES,
                        selection,
                        selectionArgs);
                resolver.notifyChange(uri, null, false);
                break;
            case ES_CLIENTES_ID:
                id = EsClientes.obtenerId(uri);
                filasAfectadas = db.delete(Tablas.ES_CLIENTES,
                        EsClientes.ID + "=" + "\'" + id + "\'"
                                + (!TextUtils.isEmpty(selection) ?
                                " AND (" + selection + ')' : ""),
                        selectionArgs);
                resolver.notifyChange(uri, null, false);
                break;
            case CLIENTES:
                filasAfectadas = db.delete(Tablas.CLIENTES,
                        selection,
                        selectionArgs);
                resolver.notifyChange(uri, null, false);
                break;
            case CLIENTES_ID:
                id = Clientes.obtenerId(uri);
                filasAfectadas = db.delete(Tablas.CLIENTES,
                        Clientes.ID + "=" + "\'" + id + "\'"
                                + (!TextUtils.isEmpty(selection) ?
                                " AND (" + selection + ')' : ""),
                        selectionArgs);
                resolver.notifyChange(uri, null, false);
                break;
            case GRUPOS:
                filasAfectadas = db.delete(Tablas.GRUPOS,
                        selection,
                        selectionArgs);
                resolver.notifyChange(uri, null, false);
                break;
            case GRUPOS_ID:
                id = Grupos.obtenerId(uri);
                filasAfectadas = db.delete(Tablas.GRUPOS,
                        Grupos.ID + "=" + "\'" + id + "\'"
                                + (!TextUtils.isEmpty(selection) ?
                                " AND (" + selection + ')' : ""),
                        selectionArgs);
                resolver.notifyChange(uri, null, false);
                break;
            case TIPOS_PERSONAS:
                filasAfectadas = db.delete(Tablas.TIPOS_PERSONAS,
                        selection,
                        selectionArgs);
                resolver.notifyChange(uri, null, false);
                break;
            case TIPOS_PERSONAS_ID:
                id = TiposPersonas.obtenerId(uri);
                filasAfectadas = db.delete(Tablas.TIPOS_PERSONAS,
                        TiposPersonas.ID + "=" + "\'" + id + "\'"
                                + (!TextUtils.isEmpty(selection) ?
                                " AND (" + selection + ')' : ""),
                        selectionArgs);
                resolver.notifyChange(uri, null, false);
                break;
            case TIPOS_CONTACTOS:
                filasAfectadas = db.delete(Tablas.TIPOS_CONTACTOS,
                        selection,
                        selectionArgs);
                resolver.notifyChange(uri, null, false);
                break;
            case TIPOS_CONTACTOS_ID:
                id = TiposContactos.obtenerId(uri);
                filasAfectadas = db.delete(Tablas.TIPOS_CONTACTOS,
                        TiposContactos.ID + "=" + "\'" + id + "\'"
                                + (!TextUtils.isEmpty(selection) ?
                                " AND (" + selection + ')' : ""),
                        selectionArgs);
                resolver.notifyChange(uri, null, false);
                break;
            case TIPOS_DOCUMENTOS:
                filasAfectadas = db.delete(Tablas.TIPOS_DOCUMENTOS,
                        selection,
                        selectionArgs);
                resolver.notifyChange(uri, null, false);
                break;
            case TIPOS_DOCUMENTOS_ID:
                id = TiposDocumentos.obtenerId(uri);
                filasAfectadas = db.delete(Tablas.TIPOS_DOCUMENTOS,
                        TiposDocumentos.ID + "=" + "\'" + id + "\'"
                                + (!TextUtils.isEmpty(selection) ?
                                " AND (" + selection + ')' : ""),
                        selectionArgs);
                resolver.notifyChange(uri, null, false);
                break;
            case DOCUMENTOS_REQUERIDOS:
                filasAfectadas = db.delete(Tablas.DOCUMENTOS_REQUERIDOS,
                        selection,
                        selectionArgs);
                resolver.notifyChange(uri, null, false);
                break;
            case DOCUMENTOS_REQUERIDOS_ID:
                id = DocumentosRequeridos.obtenerId(uri);
                filasAfectadas = db.delete(Tablas.DOCUMENTOS_REQUERIDOS,
                        DocumentosRequeridos.ID + "=" + "\'" + id + "\'"
                                + (!TextUtils.isEmpty(selection) ?
                                " AND (" + selection + ')' : ""),
                        selectionArgs);
                resolver.notifyChange(uri, null, false);
                break;
            case DOCUMENTOS_ENTREGADOS:
                filasAfectadas = db.delete(Tablas.DOCUMENTOS_ENTREGADOS,
                        selection,
                        selectionArgs);
                resolver.notifyChange(uri, null, false);
                break;
            case DOCUMENTOS_ENTREGADOS_ID:
                id = DocumentosEntregados.obtenerId(uri);
                filasAfectadas = db.delete(Tablas.DOCUMENTOS_ENTREGADOS,
                        DocumentosEntregados.ID + "=" + "\'" + id + "\'"
                                + (!TextUtils.isEmpty(selection) ?
                                " AND (" + selection + ')' : ""),
                        selectionArgs);
                resolver.notifyChange(uri, null, false);
                break;
            case ARCHIVOS_DOCUMENTOS_ENTREGADOS:
                filasAfectadas = db.delete(Tablas.ARCHIVOS_DOCUMENTOS_ENTREGADOS,
                        selection,
                        selectionArgs);
                resolver.notifyChange(uri, null, false);
                break;
            case ARCHIVOS_DOCUMENTOS_ENTREGADOS_ID:
                id = ArchivosDocumentosEntregados.obtenerId(uri);
                filasAfectadas = db.delete(Tablas.ARCHIVOS_DOCUMENTOS_ENTREGADOS,
                        ArchivosDocumentosEntregados.ID + "=" + "\'" + id + "\'"
                                + (!TextUtils.isEmpty(selection) ?
                                " AND (" + selection + ')' : ""),
                        selectionArgs);
                resolver.notifyChange(uri, null, false);
                break;
            case PRODUCTOS:
                filasAfectadas = db.delete(Tablas.PRODUCTOS,
                        selection,
                        selectionArgs);
                resolver.notifyChange(uri, null, false);
                break;
            case PRODUCTOS_ID:
                id = Productos.obtenerId(uri);
                filasAfectadas = db.delete(Tablas.PRODUCTOS,
                        Productos.ID + "=" + "\'" + id + "\'"
                                + (!TextUtils.isEmpty(selection) ?
                                " AND (" + selection + ')' : ""),
                        selectionArgs);
                resolver.notifyChange(uri, null, false);
                break;
            case BANCOS:
                filasAfectadas = db.delete(Tablas.BANCOS,
                        selection,
                        selectionArgs);
                resolver.notifyChange(uri, null, false);
                break;
            case BANCOS_ID:
                id = Bancos.obtenerId(uri);
                filasAfectadas = db.delete(Tablas.BANCOS,
                        Bancos.ID + "=" + "\'" + id + "\'"
                                + (!TextUtils.isEmpty(selection) ?
                                " AND (" + selection + ')' : ""),
                        selectionArgs);
                resolver.notifyChange(uri, null, false);
                break;
            case TIPOS_PAGOS:
                filasAfectadas = db.delete(Tablas.TIPOS_PAGOS,
                        selection,
                        selectionArgs);
                resolver.notifyChange(uri, null, false);
                break;
            case TIPOS_PAGOS_ID:
                id = TiposPagos.obtenerId(uri);
                filasAfectadas = db.delete(Tablas.TIPOS_PAGOS,
                        TiposPagos.ID + "=" + "\'" + id + "\'"
                                + (!TextUtils.isEmpty(selection) ?
                                " AND (" + selection + ')' : ""),
                        selectionArgs);
                resolver.notifyChange(uri, null, false);
                break;
            case TIPOS_AMORTIZACION:
                filasAfectadas = db.delete(Tablas.TIPOS_AMORTIZACION,
                        selection,
                        selectionArgs);
                resolver.notifyChange(uri, null, false);
                break;
            case TIPOS_AMORTIZACION_ID:
                id = TiposAmortizacion.obtenerId(uri);
                filasAfectadas = db.delete(Tablas.TIPOS_AMORTIZACION,
                        TiposAmortizacion.ID + "=" + "\'" + id + "\'"
                                + (!TextUtils.isEmpty(selection) ?
                                " AND (" + selection + ')' : ""),
                        selectionArgs);
                resolver.notifyChange(uri, null, false);
                break;
            case STATUS_SOLICITUDES:
                filasAfectadas = db.delete(Tablas.STATUS_SOLICITUDES,
                        selection,
                        selectionArgs);
                resolver.notifyChange(uri, null, false);
                break;
            case STATUS_SOLICITUDES_ID:
                id = StatusSolicitudes.obtenerId(uri);
                filasAfectadas = db.delete(Tablas.STATUS_SOLICITUDES,
                        StatusSolicitudes.ID + "=" + "\'" + id + "\'"
                                + (!TextUtils.isEmpty(selection) ?
                                " AND (" + selection + ')' : ""),
                        selectionArgs);
                resolver.notifyChange(uri, null, false);
                break;
            case SOLICITUDES:
                filasAfectadas = db.delete(Tablas.SOLICITUDES,
                        selection,
                        selectionArgs);
                resolver.notifyChange(uri, null, false);
                break;
            case SOLICITUDES_ID:
                id = Solicitudes.obtenerId(uri);
                filasAfectadas = db.delete(Tablas.SOLICITUDES,
                        Solicitudes.ID + "=" + "\'" + id + "\'"
                                + (!TextUtils.isEmpty(selection) ?
                                " AND (" + selection + ')' : ""),
                        selectionArgs);
                resolver.notifyChange(uri, null, false);
                break;
            case COTIZADORES:
                filasAfectadas = db.delete(Tablas.COTIZADORES,
                        selection,
                        selectionArgs);
                resolver.notifyChange(uri, null, false);
                break;
            case COTIZADORES_ID:
                id = Cotizadores.obtenerId(uri);
                filasAfectadas = db.delete(Tablas.COTIZADORES,
                        Cotizadores.ID + "=" + "\'" + id + "\'"
                                + (!TextUtils.isEmpty(selection) ?
                                " AND (" + selection + ')' : ""),
                        selectionArgs);
                resolver.notifyChange(uri, null, false);
                break;
            case BITACORAS_CREDITO:
                filasAfectadas = db.delete(Tablas.BITACORAS_CREDITO,
                        selection,
                        selectionArgs);
                resolver.notifyChange(uri, null, false);
                break;
            case BITACORAS_CREDITO_ID:
                id = BitacorasCredito.obtenerId(uri);
                filasAfectadas = db.delete(Tablas.BITACORAS_CREDITO,
                        BitacorasCredito.ID + "=" + "\'" + id + "\'"
                                + (!TextUtils.isEmpty(selection) ?
                                " AND (" + selection + ')' : ""),
                        selectionArgs);
                resolver.notifyChange(uri, null, false);
                break;
            case BITACORAS_CREDITO_ARCHIVOS:
                filasAfectadas = db.delete(Tablas.BITACORAS_CREDITO_ARCHIVOS,
                        selection,
                        selectionArgs);
                resolver.notifyChange(uri, null, false);
                break;
            case BITACORAS_CREDITO_ARCHIVOS_ID:
                id = BitacorasCreditoArchivos.obtenerId(uri);
                filasAfectadas = db.delete(Tablas.BITACORAS_CREDITO_ARCHIVOS,
                        BitacorasCreditoArchivos.ID + "=" + "\'" + id + "\'"
                                + (!TextUtils.isEmpty(selection) ?
                                " AND (" + selection + ')' : ""),
                        selectionArgs);
                resolver.notifyChange(uri, null, false);
                break;
            case INSTRUMENTOS_MONETARIOS:
                filasAfectadas = db.delete(Tablas.INSTRUMENTOS_MONETARIOS,
                        selection,
                        selectionArgs);
                resolver.notifyChange(uri, null, false);
                break;
            case INSTRUMENTOS_MONETARIOS_ID:
                id = InstrumentosMonetarios.obtenerId(uri);
                filasAfectadas = db.delete(Tablas.INSTRUMENTOS_MONETARIOS,
                        InstrumentosMonetarios.ID + "=" + "\'" + id + "\'"
                                + (!TextUtils.isEmpty(selection) ?
                                " AND (" + selection + ')' : ""),
                        selectionArgs);
                resolver.notifyChange(uri, null, false);
                break;
            case CANALES_COBRANZAS:
                filasAfectadas = db.delete(Tablas.CANALES_COBRANZAS,
                        selection,
                        selectionArgs);
                resolver.notifyChange(uri, null, false);
                break;
            case CANALES_COBRANZAS_ID:
                id = CanalesCobranzas.obtenerId(uri);
                filasAfectadas = db.delete(Tablas.CANALES_COBRANZAS,
                        CanalesCobranzas.ID + "=" + "\'" + id + "\'"
                                + (!TextUtils.isEmpty(selection) ?
                                " AND (" + selection + ')' : ""),
                        selectionArgs);
                resolver.notifyChange(uri, null, false);
                break;
            case PAGOS:
                filasAfectadas = db.delete(Tablas.PAGOS,
                        selection,
                        selectionArgs);
                resolver.notifyChange(uri, null, false);
                break;
            case PAGOS_ID:
                id = Pagos.obtenerId(uri);
                filasAfectadas = db.delete(Tablas.PAGOS,
                        Pagos.ID + "=" + "\'" + id + "\'"
                                + (!TextUtils.isEmpty(selection) ?
                                " AND (" + selection + ')' : ""),
                        selectionArgs);
                resolver.notifyChange(uri, null, false);
                break;
            case CODIGOS_POSTALES:
                filasAfectadas = db.delete(Tablas.CODIGOS_POSTALES,
                        selection,
                        selectionArgs);
                resolver.notifyChange(uri, null, false);
                break;
            case CODIGOS_POSTALES_ID:
                id = CodigosPostales.obtenerId(uri);
                filasAfectadas = db.delete(Tablas.CODIGOS_POSTALES,
                        CodigosPostales.ID + "=" + "\'" + id + "\'"
                                + (!TextUtils.isEmpty(selection) ?
                                " AND (" + selection + ')' : ""),
                        selectionArgs);
                resolver.notifyChange(uri, null, false);
                break;
            case CODIGOS:
                filasAfectadas = db.delete(Tablas.CODIGOS,
                        selection,
                        selectionArgs);
                resolver.notifyChange(uri, null, false);
                break;
            case CODIGOS_ID:
                id = Codigos.obtenerId(uri);
                filasAfectadas = db.delete(Tablas.CODIGOS,
                        Codigos.ID + "=" + "\'" + id + "\'"
                                + (!TextUtils.isEmpty(selection) ?
                                " AND (" + selection + ')' : ""),
                        selectionArgs);
                resolver.notifyChange(uri, null, false);
                break;
            case PAISES:
                filasAfectadas = db.delete(Tablas.PAISES,
                        selection,
                        selectionArgs);
                resolver.notifyChange(uri, null, false);
                break;
            case PAISES_ID:
                id = Paises.obtenerId(uri);
                filasAfectadas = db.delete(Tablas.PAISES,
                        Paises.ID + "=" + "\'" + id + "\'"
                                + (!TextUtils.isEmpty(selection) ?
                                " AND (" + selection + ')' : ""),
                        selectionArgs);
                resolver.notifyChange(uri, null, false);
                break;
            case ESTADOS:
                filasAfectadas = db.delete(Tablas.ESTADOS,
                        selection,
                        selectionArgs);
                resolver.notifyChange(uri, null, false);
                break;
            case ESTADOS_ID:
                id = Estados.obtenerId(uri);
                filasAfectadas = db.delete(Tablas.ESTADOS,
                        Estados.ID + "=" + "\'" + id + "\'"
                                + (!TextUtils.isEmpty(selection) ?
                                " AND (" + selection + ')' : ""),
                        selectionArgs);
                resolver.notifyChange(uri, null, false);
                break;
            case MUNICIPIOS:
                filasAfectadas = db.delete(Tablas.MUNICIPIOS,
                        selection,
                        selectionArgs);
                resolver.notifyChange(uri, null, false);
                break;
            case MUNICIPIOS_ID:
                id = Municipios.obtenerId(uri);
                filasAfectadas = db.delete(Tablas.MUNICIPIOS,
                        Municipios.ID + "=" + "\'" + id + "\'"
                                + (!TextUtils.isEmpty(selection) ?
                                " AND (" + selection + ')' : ""),
                        selectionArgs);
                resolver.notifyChange(uri, null, false);
                break;
            case ESTADOS_CIVILES:
                filasAfectadas = db.delete(Tablas.ESTADOS_CIVILES,
                        selection,
                        selectionArgs);
                resolver.notifyChange(uri, null, false);
                break;
            case ESTADOS_CIVILES_ID:
                id = EstadosCiviles.obtenerId(uri);
                filasAfectadas = db.delete(Tablas.ESTADOS_CIVILES,
                        EstadosCiviles.ID + "=" + "\'" + id + "\'"
                                + (!TextUtils.isEmpty(selection) ?
                                " AND (" + selection + ')' : ""),
                        selectionArgs);
                resolver.notifyChange(uri, null, false);
                break;
            case CATEGORIAS_ACTIVIDADES_ECONOMICAS:
                filasAfectadas = db.delete(Tablas.CATEGORIAS_ACTIVIDADES_ECONOMICAS,
                        selection,
                        selectionArgs);
                resolver.notifyChange(uri, null, false);
                break;
            case CATEGORIAS_ACTIVIDADES_ECONOMICAS_ID:
                id = CategoriasActividadesEconomicas.obtenerId(uri);
                filasAfectadas = db.delete(Tablas.CATEGORIAS_ACTIVIDADES_ECONOMICAS,
                        CategoriasActividadesEconomicas.ID + "=" + "\'" + id + "\'"
                                + (!TextUtils.isEmpty(selection) ?
                                " AND (" + selection + ')' : ""),
                        selectionArgs);
                resolver.notifyChange(uri, null, false);
                break;
            case ACTIVIDADES_ECONOMICAS:
                filasAfectadas = db.delete(Tablas.ACTIVIDADES_ECONOMICAS,
                        selection,
                        selectionArgs);
                resolver.notifyChange(uri, null, false);
                break;
            case ACTIVIDADES_ECONOMICAS_ID:
                id = ActividadesEconomicas.obtenerId(uri);
                filasAfectadas = db.delete(Tablas.ACTIVIDADES_ECONOMICAS,
                        ActividadesEconomicas.ID + "=" + "\'" + id + "\'"
                                + (!TextUtils.isEmpty(selection) ?
                                " AND (" + selection + ')' : ""),
                        selectionArgs);
                resolver.notifyChange(uri, null, false);
                break;
            default:
                throw new IllegalArgumentException("Instrucción desconocida: " +
                        uri);
        }
        return filasAfectadas;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        int match = uriMatcher.match(uri);

        long _id;
        long _idR;
        String id;
        String body;

        ContentValues contentValues;
        if (values != null) {
            contentValues = new ContentValues(values);
        } else {
            contentValues = new ContentValues();
        }

        // Inserción de nueva fila
        SQLiteDatabase db = manejadorBD.getWritableDatabase();

        switch (match) {
            case ES_CLIENTES:
                _id = db.insert(Tablas.ES_CLIENTES, null, contentValues);
                if (_id > 0) {
                    resolver.notifyChange(uri, null, false);
                    id = contentValues.getAsString(EsClientes.ID);
                    return EsClientes.construirUri(id);
                }
                break;
            case CLIENTES:
                _id = db.insert(Tablas.CLIENTES, null, contentValues);
                if (_id > 0) {
                    resolver.notifyChange(uri, null, false);
                    id = contentValues.getAsString(Clientes.ID);
                    return Clientes.construirUri(id);
                }
                break;
            case GRUPOS:
                _id = db.insert(Tablas.GRUPOS, null, contentValues);
                if (_id > 0) {
                    resolver.notifyChange(uri, null, false);
                    id = contentValues.getAsString(Grupos.ID);
                    return Grupos.construirUri(id);
                }
                break;
            case TIPOS_PERSONAS:
                _id = db.insert(Tablas.TIPOS_PERSONAS, null, contentValues);
                if (_id > 0) {
                    resolver.notifyChange(uri, null, false);
                    id = contentValues.getAsString(TiposPersonas.ID);
                    return TiposPersonas.construirUri(id);
                }
                break;
            case TIPOS_CONTACTOS:
                _id = db.insert(Tablas.TIPOS_CONTACTOS, null, contentValues);
                if (_id > 0) {
                    resolver.notifyChange(uri, null, false);
                    id = contentValues.getAsString(TiposPersonas.ID);
                    return TiposContactos.construirUri(id);
                }
                break;
            case TIPOS_DOCUMENTOS:
                _id = db.insertOrThrow(Tablas.TIPOS_DOCUMENTOS, null, contentValues);
                if (_id > 0) {
                    resolver.notifyChange(uri, null, false);
                    id = contentValues.getAsString(TiposDocumentos.ID);
                    return TiposDocumentos.construirUri(id);
                }
                break;
            case DOCUMENTOS_REQUERIDOS:
                _id = db.insertOrThrow (Tablas.DOCUMENTOS_REQUERIDOS, null, contentValues);
                if (_id > 0) {
                    resolver.notifyChange(uri, null, false);
                    id = contentValues.getAsString(DocumentosRequeridos.ID);
                    return DocumentosRequeridos.construirUri(id);
                }
                break;
            case DOCUMENTOS_ENTREGADOS:
                _id = db.insertOrThrow(Tablas.DOCUMENTOS_ENTREGADOS, null, contentValues);
                if (_id > 0) {
                    resolver.notifyChange(uri, null, false);
                    id = contentValues.getAsString(DocumentosEntregados.ID);
                    return DocumentosEntregados.construirUri(id);
                }
                break;
            case ARCHIVOS_DOCUMENTOS_ENTREGADOS:
                id = contentValues.getAsString(ArchivosDocumentosEntregados.ID);
                String id_documento = contentValues.getAsString(ArchivosDocumentosEntregados.ID_DOCUMENTO_ENTREGADO);
                String ruta_archivo = contentValues.getAsString(ArchivosDocumentosEntregados.RUTA);
                if(TextUtils.isEmpty(id_documento)){
                    ContentValues valoresDocumento = new ContentValues();
                    valoresDocumento.put(DocumentosEntregados.ID, DocumentosEntregados.generarId());
                    valoresDocumento.put(DocumentosEntregados.ID_CLIENTE, contentValues.getAsString(DocumentosEntregados.ID_CLIENTE));
                    valoresDocumento.put(DocumentosEntregados.ID_DOCUMENTO_REQUERIDO, contentValues.getAsString(DocumentosEntregados.ID_DOCUMENTO_REQUERIDO));
                    valoresDocumento.put(DocumentosEntregados.NOMBRE_DOCUMENTO,contentValues.getAsString(DocumentosEntregados.NOMBRE_DOCUMENTO));
                    valoresDocumento.put(DocumentosEntregados.DESCRIPCION,"");
                    valoresDocumento.put(DocumentosEntregados.STATUS,1);
                    valoresDocumento.put(DocumentosEntregados.TIPO_DOCUMENTO,contentValues.getAsString(DocumentosEntregados.TIPO_DOCUMENTO));
                    valoresDocumento.put(DocumentosEntregados.VERSION, UTiempo.obtenerTiempo());

                    id_documento = valoresDocumento.getAsString(DocumentosEntregados.ID);

                    _idR = db.insertOrThrow(Tablas.DOCUMENTOS_ENTREGADOS, null, valoresDocumento);
                    if (_idR > 0) {
                        contentValues.put(ArchivosDocumentosEntregados.ID_DOCUMENTO_ENTREGADO,id_documento);
                    }
                }
                //Se quitan los datos extras que no pertenece al archivo
                contentValues.remove(DocumentosEntregados.ID_CLIENTE);
                contentValues.remove(DocumentosEntregados.ID_DOCUMENTO_REQUERIDO);
                contentValues.remove(DocumentosEntregados.NOMBRE_DOCUMENTO);
                contentValues.remove(DocumentosEntregados.TIPO_DOCUMENTO);

                //Inicia Guardado de Archivo
                /*
                body = contentValues.getAsString(ArchivosDocumentosEntregados.BODY);
                if(!TextUtils.isEmpty(body)){
                    byte[] contenido = Base64.decode(body, 0);
                    File carpeta = new File(ruta_expedientes);
                    carpeta.mkdirs();
                    String ruta_archivo = ruta_expedientes + id + ".pdf";//Los archivos se guardan con el ID del cotizador
                    File archivo = new File( ruta_archivo );
                    try {
                        FileOutputStream output = new FileOutputStream(archivo);
                        output.write(contenido);
                        output.flush();
                        output.close();

                        contentValues.put(ArchivosDocumentosEntregados.RUTA,ruta_archivo);
                        contentValues.remove(ArchivosDocumentosEntregados.BODY);
                    } catch (IOException ex) {

                    }
                }
                */
                if(ruta_archivo==null){
                    ruta_archivo = ruta_expedientes + id + ".pdf";//Los archivos se guardan co7n el ID del archivo
                    contentValues.put(ArchivosDocumentosEntregados.RUTA,ruta_archivo);
                }
                contentValues.remove(ArchivosDocumentosEntregados.BODY);//El archivo se descarga por aparte

                //Finaliza Guardado de Archivo
                _id = db.insertOrThrow(Tablas.ARCHIVOS_DOCUMENTOS_ENTREGADOS, null, contentValues);
                if (_id > 0) {
                    resolver.notifyChange(uri, null, false);
                    return ArchivosDocumentosEntregados.construirUri(id);
                }
                break;
            case PRODUCTOS:
                _id = db.insert(Tablas.PRODUCTOS, null, contentValues);
                if (_id > 0) {
                    resolver.notifyChange(uri, null, false);
                    id = contentValues.getAsString(Productos.ID);
                    return Productos.construirUri(id);
                }
                break;
            case BANCOS:
                _id = db.insert(Tablas.BANCOS, null, contentValues);
                if (_id > 0) {
                    resolver.notifyChange(uri, null, false);
                    id = contentValues.getAsString(Bancos.ID);
                    return Bancos.construirUri(id);
                }
                break;
            case TIPOS_PAGOS:
                _id = db.insert(Tablas.TIPOS_PAGOS, null, contentValues);
                if (_id > 0) {
                    resolver.notifyChange(uri, null, false);
                    id = contentValues.getAsString(Bancos.ID);
                    return TiposPagos.construirUri(id);
                }
                break;
            case TIPOS_AMORTIZACION:
                _id = db.insertOrThrow(Tablas.TIPOS_AMORTIZACION, null, contentValues);
                if (_id > 0) {
                    resolver.notifyChange(uri, null, false);
                    id = contentValues.getAsString(TiposAmortizacion.ID);
                    return TiposAmortizacion.construirUri(id);
                }
                break;
            case STATUS_SOLICITUDES:
                _id = db.insertOrThrow(Tablas.STATUS_SOLICITUDES, null, contentValues);
                if (_id > 0) {
                    resolver.notifyChange(uri, null, false);
                    id = contentValues.getAsString(StatusSolicitudes.ID);
                    return StatusSolicitudes.construirUri(id);
                }
                break;
            case SOLICITUDES:
                _id = db.insertOrThrow(Tablas.SOLICITUDES, null, contentValues);
                if (_id > 0) {
                    resolver.notifyChange(uri, null, false);
                    id = contentValues.getAsString(Solicitudes.ID);
                    return Solicitudes.construirUri(id);
                }
                break;
            case COTIZADORES:
                _id = db.insert(Tablas.COTIZADORES, null, contentValues);
                if (_id > 0) {
                    resolver.notifyChange(uri, null, false);
                    id = contentValues.getAsString(Cotizadores.ID);
                    return Cotizadores.construirUri(id);
                }
                break;
            case BITACORAS_CREDITO:
                _id = db.insert(Tablas.BITACORAS_CREDITO, null, contentValues);
                if (_id > 0) {
                    resolver.notifyChange(uri, null, false);
                    id = contentValues.getAsString(BitacorasCredito.ID);
                    return BitacorasCredito.construirUri(id);
                }
                break;
            case BITACORAS_CREDITO_ARCHIVOS:
                id = contentValues.getAsString(BitacorasCreditoArchivos.ID);
                String ruta_archivo_bitacora = contentValues.getAsString(BitacorasCreditoArchivos.RUTA);
                //Inicia Guardado de Archivo
                /*
                body = contentValues.getAsString(BitacorasCreditoArchivos.BODY);
                if(!TextUtils.isEmpty(body)){
                    byte[] contenido = Base64.decode(body, 0);
                    File carpeta = new File(ruta_bitacoras_credito);
                    carpeta.mkdirs();
                    String ruta_archivo = ruta_bitacoras_credito + id + ".pdf";//Los archivos se guardan con el ID del archivo
                    File archivo = new File( ruta_archivo );
                    try {
                        FileOutputStream output = new FileOutputStream(archivo);
                        output.write(contenido);
                        output.flush();
                        output.close();

                        contentValues.put(BitacorasCreditoArchivos.RUTA,ruta_archivo);
                        contentValues.remove(BitacorasCreditoArchivos.BODY);
                    } catch (IOException ex) {

                    }
                }
                */
                if(ruta_archivo_bitacora==null){
                    ruta_archivo_bitacora = ruta_bitacoras_credito + id + ".pdf";//Los archivos se guardan con el ID del archivo
                    contentValues.put(BitacorasCreditoArchivos.RUTA,ruta_archivo_bitacora);
                }
                contentValues.remove(BitacorasCreditoArchivos.BODY);//El archivo se descarga por aparte

                //Finaliza Guardado de Archivo
                _id = db.insert(Tablas.BITACORAS_CREDITO_ARCHIVOS, null, contentValues);
                if (_id > 0) {
                    resolver.notifyChange(uri, null, false);
                    return BitacorasCreditoArchivos.construirUri(id);
                }
                break;
            case INSTRUMENTOS_MONETARIOS:
                _id = db.insert(Tablas.INSTRUMENTOS_MONETARIOS, null, contentValues);
                if (_id > 0) {
                    resolver.notifyChange(uri, null, false);
                    id = contentValues.getAsString(InstrumentosMonetarios.ID);
                    return InstrumentosMonetarios.construirUri(id);
                }
                break;
            case CANALES_COBRANZAS:
                _id = db.insert(Tablas.CANALES_COBRANZAS, null, contentValues);
                if (_id > 0) {
                    resolver.notifyChange(uri, null, false);
                    id = contentValues.getAsString(CanalesCobranzas.ID);
                    return CanalesCobranzas.construirUri(id);
                }
                break;
            case PAGOS:
                _id = db.insert(Tablas.PAGOS, null, contentValues);
                if (_id > 0) {
                    resolver.notifyChange(uri, null, false);
                    id = contentValues.getAsString(Pagos.ID);
                    return Pagos.construirUri(id);
                }
                break;
            case CODIGOS_POSTALES:
                _id = db.insert(Tablas.CODIGOS_POSTALES, null, contentValues);
                if (_id > 0) {
                    resolver.notifyChange(uri, null, false);
                    id = contentValues.getAsString(CodigosPostales.ID);
                    return CodigosPostales.construirUri(id);
                }
                break;
            case CODIGOS:
                _id = db.insert(Tablas.CODIGOS, null, contentValues);
                if (_id > 0) {
                    resolver.notifyChange(uri, null, false);
                    id = contentValues.getAsString(Codigos.ID);
                    return Codigos.construirUri(id);
                }
                break;
            case PAISES:
                _id = db.insert(Tablas.PAISES, null, contentValues);
                if (_id > 0) {
                    resolver.notifyChange(uri, null, false);
                    id = contentValues.getAsString(Paises.ID);
                    return Paises.construirUri(id);
                }
                break;
            case ESTADOS:
                _id = db.insert(Tablas.ESTADOS, null, contentValues);
                if (_id > 0) {
                    resolver.notifyChange(uri, null, false);
                    id = contentValues.getAsString(Estados.ID);
                    return Estados.construirUri(id);
                }
                break;
            case MUNICIPIOS:
                _id = db.insert(Tablas.MUNICIPIOS, null, contentValues);
                if (_id > 0) {
                    resolver.notifyChange(uri, null, false);
                    id = contentValues.getAsString(Municipios.ID);
                    return Municipios.construirUri(id);
                }
                break;
            case ESTADOS_CIVILES:
                _id = db.insert(Tablas.ESTADOS_CIVILES, null, contentValues);
                if (_id > 0) {
                    resolver.notifyChange(uri, null, false);
                    id = contentValues.getAsString(EstadosCiviles.ID);
                    return EstadosCiviles.construirUri(id);
                }
                break;
            case CATEGORIAS_ACTIVIDADES_ECONOMICAS:
                _id = db.insert(Tablas.CATEGORIAS_ACTIVIDADES_ECONOMICAS, null, contentValues);
                if (_id > 0) {
                    resolver.notifyChange(uri, null, false);
                    id = contentValues.getAsString(CategoriasActividadesEconomicas.ID);
                    return CategoriasActividadesEconomicas.construirUri(id);
                }
                break;
            case ACTIVIDADES_ECONOMICAS:
                _id = db.insert(Tablas.ACTIVIDADES_ECONOMICAS, null, contentValues);
                if (_id > 0) {
                    resolver.notifyChange(uri, null, false);
                    id = contentValues.getAsString(ActividadesEconomicas.ID);
                    return ActividadesEconomicas.construirUri(id);
                }
                break;
            default:
                throw new IllegalArgumentException("Instrucción desconocida: " +
                        uri);
        }
        throw new SQLException("Falla al insertar fila en : " + uri);
    }


    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {

        SQLiteDatabase db = manejadorBD.getWritableDatabase();

        int filasAfectadas;
        String id;

        switch (uriMatcher.match(uri)) {
            case ES_CLIENTES:
                filasAfectadas = db.update(Tablas.ES_CLIENTES, values,
                        selection, selectionArgs);
                resolver.notifyChange(uri, null, false);
                break;
            case ES_CLIENTES_ID:
                id = EsClientes.obtenerId(uri);
                filasAfectadas = db.update(Tablas.ES_CLIENTES, values,
                        EsClientes.ID + "=" + "\'" + id + "\'"
                                + (!TextUtils.isEmpty(selection) ?
                                " AND (" + selection + ')' : ""),
                        selectionArgs);
                resolver.notifyChange(uri, null, false);
                break;
            case CLIENTES:
                filasAfectadas = db.update(Tablas.CLIENTES, values,
                        selection, selectionArgs);
                resolver.notifyChange(uri, null, false);
                break;
            case CLIENTES_ID:
                id = Clientes.obtenerId(uri);
                filasAfectadas = db.update(Tablas.CLIENTES, values,
                        Clientes.ID + "=" + "\'" + id + "\'"
                                + (!TextUtils.isEmpty(selection) ?
                                " AND (" + selection + ')' : ""),
                        selectionArgs);
                resolver.notifyChange(uri, null, false);
                break;
            case GRUPOS:
                filasAfectadas = db.update(Tablas.GRUPOS, values,
                        selection, selectionArgs);
                resolver.notifyChange(uri, null, false);
                break;
            case GRUPOS_ID:
                id = Grupos.obtenerId(uri);
                filasAfectadas = db.update(Tablas.GRUPOS, values,
                        Grupos.ID + "=" + "\'" + id + "\'"
                                + (!TextUtils.isEmpty(selection) ?
                                " AND (" + selection + ')' : ""),
                        selectionArgs);
                resolver.notifyChange(uri, null, false);
                break;
            case TIPOS_PERSONAS:
                filasAfectadas = db.update(Tablas.TIPOS_PERSONAS, values,
                        selection, selectionArgs);
                resolver.notifyChange(uri, null, false);
                break;
            case TIPOS_PERSONAS_ID:
                id = TiposPersonas.obtenerId(uri);
                filasAfectadas = db.update(Tablas.TIPOS_PERSONAS, values,
                        TiposPersonas.ID + "=" + "\'" + id + "\'"
                                + (!TextUtils.isEmpty(selection) ?
                                " AND (" + selection + ')' : ""),
                        selectionArgs);
                resolver.notifyChange(uri, null, false);
                break;
            case TIPOS_CONTACTOS:
                filasAfectadas = db.update(Tablas.TIPOS_CONTACTOS, values,
                        selection, selectionArgs);
                resolver.notifyChange(uri, null, false);
                break;
            case TIPOS_CONTACTOS_ID:
                id = TiposContactos.obtenerId(uri);
                filasAfectadas = db.update(Tablas.TIPOS_CONTACTOS, values,
                        TiposContactos.ID + "=" + "\'" + id + "\'"
                                + (!TextUtils.isEmpty(selection) ?
                                " AND (" + selection + ')' : ""),
                        selectionArgs);
                resolver.notifyChange(uri, null, false);
                break;
            case TIPOS_DOCUMENTOS:
                filasAfectadas = db.update(Tablas.TIPOS_DOCUMENTOS, values,
                        selection, selectionArgs);
                resolver.notifyChange(uri, null, false);
                break;
            case TIPOS_DOCUMENTOS_ID:
                id = TiposDocumentos.obtenerId(uri);
                filasAfectadas = db.update(Tablas.TIPOS_DOCUMENTOS, values,
                        TiposDocumentos.ID + "=" + "\'" + id + "\'"
                                + (!TextUtils.isEmpty(selection) ?
                                " AND (" + selection + ')' : ""),
                        selectionArgs);
                resolver.notifyChange(uri, null, false);
                break;
            case DOCUMENTOS_REQUERIDOS:
                filasAfectadas = db.update(Tablas.DOCUMENTOS_REQUERIDOS, values,
                        selection, selectionArgs);
                resolver.notifyChange(uri, null, false);
                break;
            case DOCUMENTOS_REQUERIDOS_ID:
                id = DocumentosRequeridos.obtenerId(uri);
                filasAfectadas = db.update(Tablas.DOCUMENTOS_REQUERIDOS, values,
                        DocumentosRequeridos.ID + "=" + "\'" + id + "\'"
                                + (!TextUtils.isEmpty(selection) ?
                                " AND (" + selection + ')' : ""),
                        selectionArgs);
                resolver.notifyChange(uri, null, false);
                break;
            case DOCUMENTOS_ENTREGADOS:
                filasAfectadas = db.update(Tablas.DOCUMENTOS_ENTREGADOS, values,
                        selection, selectionArgs);
                resolver.notifyChange(uri, null, false);
                break;
            case DOCUMENTOS_ENTREGADOS_ID:
                id = DocumentosEntregados.obtenerId(uri);
                filasAfectadas = db.update(Tablas.DOCUMENTOS_REQUERIDOS, values,
                        DocumentosRequeridos.ID + "=" + "\'" + id + "\'"
                                + (!TextUtils.isEmpty(selection) ?
                                " AND (" + selection + ')' : ""),
                        selectionArgs);
                resolver.notifyChange(uri, null, false);
                break;
            case ARCHIVOS_DOCUMENTOS_ENTREGADOS:
                filasAfectadas = db.update(Tablas.ARCHIVOS_DOCUMENTOS_ENTREGADOS, values,
                        selection, selectionArgs);
                resolver.notifyChange(uri, null, false);
                break;
            case ARCHIVOS_DOCUMENTOS_ENTREGADOS_ID:
                id = ArchivosDocumentosEntregados.obtenerId(uri);
                filasAfectadas = db.update(Tablas.ARCHIVOS_DOCUMENTOS_ENTREGADOS, values,
                        ArchivosDocumentosEntregados.ID + "=" + "\'" + id + "\'"
                                + (!TextUtils.isEmpty(selection) ?
                                " AND (" + selection + ')' : ""),
                        selectionArgs);
                resolver.notifyChange(uri, null, false);
                break;
            case PRODUCTOS:
                filasAfectadas = db.update(Tablas.PRODUCTOS, values,
                        selection, selectionArgs);
                resolver.notifyChange(uri, null, false);
                break;
            case PRODUCTOS_ID:
                id = Productos.obtenerId(uri);
                filasAfectadas = db.update(Tablas.PRODUCTOS, values,
                        Productos.ID + "=" + "\'" + id + "\'"
                                + (!TextUtils.isEmpty(selection) ?
                                " AND (" + selection + ')' : ""),
                        selectionArgs);
                resolver.notifyChange(uri, null, false);
                break;
            case BANCOS:
                filasAfectadas = db.update(Tablas.BANCOS, values,
                        selection, selectionArgs);
                resolver.notifyChange(uri, null, false);
                break;
            case BANCOS_ID:
                id = Bancos.obtenerId(uri);
                filasAfectadas = db.update(Tablas.BANCOS, values,
                        Bancos.ID + "=" + "\'" + id + "\'"
                                + (!TextUtils.isEmpty(selection) ?
                                " AND (" + selection + ')' : ""),
                        selectionArgs);
                resolver.notifyChange(uri, null, false);
                break;
            case TIPOS_PAGOS:
                filasAfectadas = db.update(Tablas.TIPOS_PAGOS, values,
                        selection, selectionArgs);
                resolver.notifyChange(uri, null, false);
                break;
            case TIPOS_PAGOS_ID:
                id = TiposPagos.obtenerId(uri);
                filasAfectadas = db.update(Tablas.TIPOS_PAGOS, values,
                        TiposPagos.ID + "=" + "\'" + id + "\'"
                                + (!TextUtils.isEmpty(selection) ?
                                " AND (" + selection + ')' : ""),
                        selectionArgs);
                resolver.notifyChange(uri, null, false);
                break;
            case TIPOS_AMORTIZACION:
                filasAfectadas = db.update(Tablas.TIPOS_AMORTIZACION, values,
                        selection, selectionArgs);
                resolver.notifyChange(uri, null, false);
                break;
            case TIPOS_AMORTIZACION_ID:
                id = TiposAmortizacion.obtenerId(uri);
                filasAfectadas = db.update(Tablas.TIPOS_AMORTIZACION, values,
                        TiposAmortizacion.ID + "=" + "\'" + id + "\'"
                                + (!TextUtils.isEmpty(selection) ?
                                " AND (" + selection + ')' : ""),
                        selectionArgs);
                resolver.notifyChange(uri, null, false);
                break;
            case STATUS_SOLICITUDES:
                filasAfectadas = db.update(Tablas.STATUS_SOLICITUDES, values,
                        selection, selectionArgs);
                resolver.notifyChange(uri, null, false);
                break;
            case STATUS_SOLICITUDES_ID:
                id = StatusSolicitudes.obtenerId(uri);
                filasAfectadas = db.update(Tablas.STATUS_SOLICITUDES, values,
                        StatusSolicitudes.ID + "=" + "\'" + id + "\'"
                                + (!TextUtils.isEmpty(selection) ?
                                " AND (" + selection + ')' : ""),
                        selectionArgs);
                resolver.notifyChange(uri, null, false);
                break;
            case SOLICITUDES:
                filasAfectadas = db.update(Tablas.SOLICITUDES, values,
                        selection, selectionArgs);
                resolver.notifyChange(uri, null, false);
                break;
            case SOLICITUDES_ID:
                id = Solicitudes.obtenerId(uri);
                filasAfectadas = db.update(Tablas.SOLICITUDES, values,
                        Solicitudes.ID + "=" + "\'" + id + "\'"
                                + (!TextUtils.isEmpty(selection) ?
                                " AND (" + selection + ')' : ""),
                        selectionArgs);
                resolver.notifyChange(uri, null, false);
                break;
            case COTIZADORES:
                filasAfectadas = db.update(Tablas.COTIZADORES, values,
                        selection, selectionArgs);
                resolver.notifyChange(uri, null, false);
                break;
            case COTIZADORES_ID:
                id = Cotizadores.obtenerId(uri);
                filasAfectadas = db.update(Tablas.COTIZADORES, values,
                        Cotizadores.ID + "=" + "\'" + id + "\'"
                                + (!TextUtils.isEmpty(selection) ?
                                " AND (" + selection + ')' : ""),
                        selectionArgs);
                resolver.notifyChange(uri, null, false);
                break;
            case BITACORAS_CREDITO:
                filasAfectadas = db.update(Tablas.BITACORAS_CREDITO, values,
                        selection, selectionArgs);
                resolver.notifyChange(uri, null, false);
                break;
            case BITACORAS_CREDITO_ID:
                id = BitacorasCredito.obtenerId(uri);
                filasAfectadas = db.update(Tablas.BITACORAS_CREDITO, values,
                        BitacorasCredito.ID + "=" + "\'" + id + "\'"
                                + (!TextUtils.isEmpty(selection) ?
                                " AND (" + selection + ')' : ""),
                        selectionArgs);
                resolver.notifyChange(uri, null, false);
                break;
            case BITACORAS_CREDITO_ARCHIVOS:
                filasAfectadas = db.update(Tablas.BITACORAS_CREDITO_ARCHIVOS, values,
                        selection, selectionArgs);
                resolver.notifyChange(uri, null, false);
                break;
            case BITACORAS_CREDITO_ARCHIVOS_ID:
                id = BitacorasCreditoArchivos.obtenerId(uri);
                filasAfectadas = db.update(Tablas.BITACORAS_CREDITO_ARCHIVOS, values,
                        BitacorasCreditoArchivos.ID + "=" + "\'" + id + "\'"
                                + (!TextUtils.isEmpty(selection) ?
                                " AND (" + selection + ')' : ""),
                        selectionArgs);
                resolver.notifyChange(uri, null, false);
                break;
            case INSTRUMENTOS_MONETARIOS:
                filasAfectadas = db.update(Tablas.INSTRUMENTOS_MONETARIOS, values,
                        selection, selectionArgs);
                resolver.notifyChange(uri, null, false);
                break;
            case INSTRUMENTOS_MONETARIOS_ID:
                id = InstrumentosMonetarios.obtenerId(uri);
                filasAfectadas = db.update(Tablas.INSTRUMENTOS_MONETARIOS, values,
                        InstrumentosMonetarios.ID + "=" + "\'" + id + "\'"
                                + (!TextUtils.isEmpty(selection) ?
                                " AND (" + selection + ')' : ""),
                        selectionArgs);
                resolver.notifyChange(uri, null, false);
                break;
            case CANALES_COBRANZAS:
                filasAfectadas = db.update(Tablas.CANALES_COBRANZAS, values,
                        selection, selectionArgs);
                resolver.notifyChange(uri, null, false);
                break;
            case CANALES_COBRANZAS_ID:
                id = CanalesCobranzas.obtenerId(uri);
                filasAfectadas = db.update(Tablas.CANALES_COBRANZAS, values,
                        CanalesCobranzas.ID + "=" + "\'" + id + "\'"
                                + (!TextUtils.isEmpty(selection) ?
                                " AND (" + selection + ')' : ""),
                        selectionArgs);
                resolver.notifyChange(uri, null, false);
                break;
            case PAGOS:
                filasAfectadas = db.update(Tablas.PAGOS, values,
                        selection, selectionArgs);
                resolver.notifyChange(uri, null, false);
                break;
            case PAGOS_ID:
                id = Pagos.obtenerId(uri);
                filasAfectadas = db.update(Tablas.PAGOS, values,
                        Pagos.ID + "=" + "\'" + id + "\'"
                                + (!TextUtils.isEmpty(selection) ?
                                " AND (" + selection + ')' : ""),
                        selectionArgs);
                resolver.notifyChange(uri, null, false);
                break;
            case CODIGOS_POSTALES:
                filasAfectadas = db.update(Tablas.CODIGOS_POSTALES, values,
                        selection, selectionArgs);
                resolver.notifyChange(uri, null, false);
                break;
            case CODIGOS_POSTALES_ID:
                id = CodigosPostales.obtenerId(uri);
                filasAfectadas = db.update(Tablas.CODIGOS_POSTALES, values,
                        CodigosPostales.ID + "=" + "\'" + id + "\'"
                                + (!TextUtils.isEmpty(selection) ?
                                " AND (" + selection + ')' : ""),
                        selectionArgs);
                resolver.notifyChange(uri, null, false);
                break;
            case CODIGOS:
                filasAfectadas = db.update(Tablas.CODIGOS, values,
                        selection, selectionArgs);
                resolver.notifyChange(uri, null, false);
                break;
            case CODIGOS_ID:
                id = Codigos.obtenerId(uri);
                filasAfectadas = db.update(Tablas.CODIGOS, values,
                        Codigos.ID + "=" + "\'" + id + "\'"
                                + (!TextUtils.isEmpty(selection) ?
                                " AND (" + selection + ')' : ""),
                        selectionArgs);
                resolver.notifyChange(uri, null, false);
                break;
            case PAISES:
                filasAfectadas = db.update(Tablas.PAISES, values,
                        selection, selectionArgs);
                resolver.notifyChange(uri, null, false);
                break;
            case PAISES_ID:
                id = Paises.obtenerId(uri);
                filasAfectadas = db.update(Tablas.PAISES, values,
                        Paises.ID + "=" + "\'" + id + "\'"
                                + (!TextUtils.isEmpty(selection) ?
                                " AND (" + selection + ')' : ""),
                        selectionArgs);
                resolver.notifyChange(uri, null, false);
                break;
            case ESTADOS:
                filasAfectadas = db.update(Tablas.ESTADOS, values,
                        selection, selectionArgs);
                resolver.notifyChange(uri, null, false);
                break;
            case ESTADOS_ID:
                id = Estados.obtenerId(uri);
                filasAfectadas = db.update(Tablas.ESTADOS, values,
                        Estados.ID + "=" + "\'" + id + "\'"
                                + (!TextUtils.isEmpty(selection) ?
                                " AND (" + selection + ')' : ""),
                        selectionArgs);
                resolver.notifyChange(uri, null, false);
                break;
            case MUNICIPIOS:
                filasAfectadas = db.update(Tablas.MUNICIPIOS, values,
                        selection, selectionArgs);
                resolver.notifyChange(uri, null, false);
                break;
            case MUNICIPIOS_ID:
                id = Municipios.obtenerId(uri);
                filasAfectadas = db.update(Tablas.MUNICIPIOS, values,
                        Municipios.ID + "=" + "\'" + id + "\'"
                                + (!TextUtils.isEmpty(selection) ?
                                " AND (" + selection + ')' : ""),
                        selectionArgs);
                resolver.notifyChange(uri, null, false);
                break;
            case ESTADOS_CIVILES:
                filasAfectadas = db.update(Tablas.ESTADOS_CIVILES, values,
                        selection, selectionArgs);
                resolver.notifyChange(uri, null, false);
                break;
            case ESTADOS_CIVILES_ID:
                id = EstadosCiviles.obtenerId(uri);
                filasAfectadas = db.update(Tablas.ESTADOS_CIVILES, values,
                        EstadosCiviles.ID + "=" + "\'" + id + "\'"
                                + (!TextUtils.isEmpty(selection) ?
                                " AND (" + selection + ')' : ""),
                        selectionArgs);
                resolver.notifyChange(uri, null, false);
                break;
            case CATEGORIAS_ACTIVIDADES_ECONOMICAS:
                filasAfectadas = db.update(Tablas.CATEGORIAS_ACTIVIDADES_ECONOMICAS, values,
                        selection, selectionArgs);
                resolver.notifyChange(uri, null, false);
                break;
            case CATEGORIAS_ACTIVIDADES_ECONOMICAS_ID:
                id = CategoriasActividadesEconomicas.obtenerId(uri);
                filasAfectadas = db.update(Tablas.CATEGORIAS_ACTIVIDADES_ECONOMICAS, values,
                        CategoriasActividadesEconomicas.ID + "=" + "\'" + id + "\'"
                                + (!TextUtils.isEmpty(selection) ?
                                " AND (" + selection + ')' : ""),
                        selectionArgs);
                resolver.notifyChange(uri, null, false);
                break;
            case ACTIVIDADES_ECONOMICAS:
                filasAfectadas = db.update(Tablas.ACTIVIDADES_ECONOMICAS, values,
                        selection, selectionArgs);
                resolver.notifyChange(uri, null, false);
                break;
            case ACTIVIDADES_ECONOMICAS_ID:
                id = ActividadesEconomicas.obtenerId(uri);
                filasAfectadas = db.update(Tablas.ACTIVIDADES_ECONOMICAS, values,
                        ActividadesEconomicas.ID + "=" + "\'" + id + "\'"
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
