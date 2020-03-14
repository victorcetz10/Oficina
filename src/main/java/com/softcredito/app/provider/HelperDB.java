package com.softcredito.app.provider;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import com.softcredito.app.modelo.InstrumentoMonetario;
import com.softcredito.app.provider.Contract.EsClientes;
import com.softcredito.app.provider.Contract.Clientes;
import com.softcredito.app.provider.Contract.Grupos;
import com.softcredito.app.provider.Contract.TiposPersonas;
import com.softcredito.app.provider.Contract.TiposContactos;
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

import com.softcredito.app.utilidades.UTiempo;

/**
 * Clase auxiliar para controlar accesos a la base de datos SQLite
 */
public class HelperDB extends SQLiteOpenHelper {

    static final int VERSION = 1;

    static final String NOMBRE_BD = "softcreditoapp.db";




    interface Tablas {
        String ES_CLIENTES = "es_clientes";
        String CLIENTES = "clientes";
        String GRUPOS = "grupos";
        String TIPOS_PERSONAS = "tipos_personas";
        String TIPOS_CONTACTOS = "tipos_contactos";
        String TIPOS_DOCUMENTOS = "tipos_documentos";
        String DOCUMENTOS_REQUERIDOS = "documentos_requeridos";
        String DOCUMENTOS_ENTREGADOS = "documentos_entregados";
        String ARCHIVOS_DOCUMENTOS_ENTREGADOS = "archivos_documentos_entregados";
        String PRODUCTOS = "productos";
        String BANCOS = "bancos";
        String TIPOS_PAGOS = "tipos_pagos";
        String TIPOS_AMORTIZACION = "tipos_amortizacion";
        String STATUS_SOLICITUDES = "status_solicitudes";
        String SOLICITUDES = "solicitudes";
        //verificar el cotizador
        String COTIZADORES = "cotizadores";
        //
        String BITACORAS_CREDITO = "bitacoras_credito";
        String BITACORAS_CREDITO_ARCHIVOS = "bitacoras_credito_archivos";
        String INSTRUMENTOS_MONETARIOS = "instrumentos_monetarios";
        //verificar el canal
        String CANALES_COBRANZAS = "canales_cobranzas";
        //
        String PAGOS = "pagos";
        String CODIGOS_POSTALES = "codigos_postales";
        //verificar el codigo
        String CODIGOS = "codigos";
        //
        String PAISES = "paises";
        String ESTADOS = "estados";
        String MUNICIPIOS = "municipios";
        //array
        String ESTADOS_CIVILES = "estados_civiles";


        String CATEGORIAS_ACTIVIDADES_ECONOMICAS = "categorias_actividades_economicas";
        String ACTIVIDADES_ECONOMICAS = "actividades_economicas";
    }

    public HelperDB(Context context) {
        super(context, NOMBRE_BD, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE " + Tablas.ES_CLIENTES + "("
                        + EsClientes._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + EsClientes.ID + " TEXT UNIQUE,"
                        + EsClientes.NOMBRE + " TEXT NOT NULL,"
                        + EsClientes.INSERTADO + " INTEGER DEFAULT 1,"
                        + EsClientes.MODIFICADO + " INTEGER DEFAULT 0,"
                        + EsClientes.ELIMINADO + " INTEGER DEFAULT 0)"
        );
        db.execSQL(
                "CREATE TABLE " + Tablas.CLIENTES + "("
                        + Clientes._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + Clientes.ID + " TEXT UNIQUE,"
                        + Clientes.TIPO_PERSONA + " TEXT NOT NULL,"
                        + Clientes.RAZON_SOCIAL + " TEXT NOT NULL,"
                        + Clientes.PRIMER_NOMBRE + " TEXT NOT NULL,"
                        + Clientes.SEGUNDO_NOMBRE + " TEXT,"
                        + Clientes.PRIMER_APELLIDO + " TEXT,"
                        + Clientes.SEGUNDO_APELLIDO + " TEXT,"
                        + Clientes.CONTACTO + " TEXT,"
                        + Clientes.RELACION_CONTACTO + " TEXT,"
                        + Clientes.TELEFONO + " TEXT,"
                        + Clientes.CORREO + " TEXT,"
                        //
                        + Clientes.LATITUD + " TEXT,"
                        + Clientes.LONGITUD + " TEXT,"
                        //
                        + Clientes.CURP + " TEXT,"
                        + Clientes.RFC + " TEXT,"
                        + Clientes.INE + " TEXT,"
                        + Clientes.CODIGO_POSTAL + " TEXT,"
                        + Clientes.PAIS + " TEXT,"
                        + Clientes.ESTADO + " TEXT,"
                        + Clientes.MUNICIPIO + " TEXT,"
                        + Clientes.LOCALIDAD + " TEXT,"
                        + Clientes.COLONIA + " TEXT,"
                        + Clientes.CALLE + " TEXT,"
                        + Clientes.NUMERO_EXTERIOR + " TEXT,"
                        + Clientes.NUMERO_INTERIOR + " TEXT,"
                        + Clientes.REFERENCIA + " TEXT,"
                        + Clientes.FECHA_NACIMIENTO + " TEXT,"
                        + Clientes.OCUPACION + " TEXT,"
                        + Clientes.ESTADO_CIVIL + " TEXT,"
                        + Clientes.ID_CATEGORIA_ACTIVIDAD_ECONOMICA + " INTEGER DEFAULT 0,"
                        + Clientes.ID_ACTIVIDAD_ECONOMICA + " INTEGER DEFAULT 0,"
                        + Clientes.CELULAR + " TEXT,"
                        + Clientes.ES_CLIENTE + " INTEGER DEFAULT 0,"
                        + Clientes.NOTAS + " TEXT,"
                        //
                        + Clientes.VERSION + " DATE DEFAULT CURRENT_TIMESTAMP,"
                        + Clientes.INSERTADO + " INTEGER DEFAULT 1,"
                        + Clientes.MODIFICADO + " INTEGER DEFAULT 0,"
                        + Clientes.ELIMINADO + " INTEGER DEFAULT 0)"
        );
        db.execSQL(
                "CREATE TABLE " + Tablas.GRUPOS + "("
                        + Grupos._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + Grupos.ID + " TEXT UNIQUE,"
                        + Grupos.CLAVE + " TEXT NOT NULL,"
                        + Grupos.NOMBRE + " TEXT NOT NULL,"
                        + Grupos.DESCRIPCION + " TEXT NOT NULL,"
                        + Grupos.DIA_REUNION + " TEXT NOT NULL,"
                        + Grupos.HORA_REUNION + " TEXT NOT NULL,"
                        + Grupos.VERSION + " DATE DEFAULT CURRENT_TIMESTAMP,"
                        + Grupos.INSERTADO + " INTEGER DEFAULT 1,"
                        + Grupos.MODIFICADO + " INTEGER DEFAULT 0,"
                        + Grupos.ELIMINADO + " INTEGER DEFAULT 0)"
        );
        db.execSQL(
                "CREATE TABLE " + Tablas.TIPOS_PERSONAS + "("
                        + TiposPersonas._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + TiposPersonas.ID + " TEXT UNIQUE,"
                        + TiposPersonas.CLAVE + " TEXT NOT NULL,"
                        + TiposPersonas.NOMBRE + " TEXT NOT NULL,"
                        + TiposPersonas.DESCRIPCION + " TEXT DEFAULT NULL,"
                        + TiposPersonas.VERSION + " DATE DEFAULT CURRENT_TIMESTAMP,"
                        + TiposPersonas.INSERTADO + " INTEGER DEFAULT 1,"
                        + TiposPersonas.MODIFICADO + " INTEGER DEFAULT 0,"
                        + TiposPersonas.ELIMINADO + " INTEGER DEFAULT 0)"
        );
        db.execSQL(
                "CREATE TABLE " + Tablas.TIPOS_CONTACTOS + "("
                        + TiposContactos._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + TiposContactos.ID + " TEXT UNIQUE,"
                        + TiposContactos.NOMBRE + " TEXT NOT NULL,"
                        + TiposContactos.DESCRIPCION + " TEXT DEFAULT NULL,"
                        + TiposContactos.VERSION + " DATE DEFAULT CURRENT_TIMESTAMP,"
                        + TiposContactos.INSERTADO + " INTEGER DEFAULT 1,"
                        + TiposContactos.MODIFICADO + " INTEGER DEFAULT 0,"
                        + TiposContactos.ELIMINADO + " INTEGER DEFAULT 0)"
        );
        db.execSQL(
                "CREATE TABLE " + Tablas.TIPOS_DOCUMENTOS + "("
                        + TiposDocumentos._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + TiposDocumentos.ID + " TEXT UNIQUE,"
                        + TiposDocumentos.NOMBRE + " TEXT NOT NULL,"
                        + TiposDocumentos.DESCRIPCION + " TEXT DEFAULT NULL,"
                        + TiposDocumentos.VERSION + " DATE DEFAULT CURRENT_TIMESTAMP,"
                        + TiposDocumentos.INSERTADO + " INTEGER DEFAULT 1,"
                        + TiposDocumentos.MODIFICADO + " INTEGER DEFAULT 0,"
                        + TiposDocumentos.ELIMINADO + " INTEGER DEFAULT 0)"
        );
        db.execSQL(
                "CREATE TABLE " + Tablas.DOCUMENTOS_REQUERIDOS + "("
                        + DocumentosRequeridos._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + DocumentosRequeridos.ID + " TEXT UNIQUE,"
                        + DocumentosRequeridos.NOMBRE + " TEXT NOT NULL,"
                        + DocumentosRequeridos.ID_TIPO_DOCUMENTO + " INTEGER NOT NULL,"
                        + DocumentosRequeridos.DESCRIPCION + " TEXT DEFAULT NULL,"
                        + DocumentosRequeridos.OPCIONAL + " INTEGER DEFAULT 0,"
                        + DocumentosRequeridos.VERSION + " DATE DEFAULT CURRENT_TIMESTAMP,"
                        + DocumentosRequeridos.INSERTADO + " INTEGER DEFAULT 1,"
                        + DocumentosRequeridos.MODIFICADO + " INTEGER DEFAULT 0,"
                        + DocumentosRequeridos.ELIMINADO + " INTEGER DEFAULT 0)"
        );
        db.execSQL(
                "CREATE TABLE " + Tablas.DOCUMENTOS_ENTREGADOS + "("
                        + DocumentosEntregados._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + DocumentosEntregados.ID + " TEXT UNIQUE,"
                        + DocumentosEntregados.ID_CLIENTE + " INTEGER NOT NULL,"
                        + DocumentosEntregados.ID_DOCUMENTO_REQUERIDO + " INTEGER NOT NULL,"
                        + DocumentosEntregados.NOMBRE_DOCUMENTO + " TEXT NOT NULL,"
                        + DocumentosEntregados.DESCRIPCION + " TEXT DEFAULT NULL,"
                        + DocumentosEntregados.STATUS + " INTEGER DEFAULT 0,"
                        + DocumentosEntregados.TIPO_DOCUMENTO + " TEXT NOT NULL,"
                        + DocumentosEntregados.VERSION + " DATE DEFAULT CURRENT_TIMESTAMP,"
                        + DocumentosEntregados.INSERTADO + " INTEGER DEFAULT 1,"
                        + DocumentosEntregados.MODIFICADO + " INTEGER DEFAULT 0,"
                        + DocumentosEntregados.ELIMINADO + " INTEGER DEFAULT 0)"
        );
        db.execSQL(
                "CREATE TABLE " + Tablas.ARCHIVOS_DOCUMENTOS_ENTREGADOS + "("
                        + ArchivosDocumentosEntregados._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + ArchivosDocumentosEntregados.ID + " TEXT UNIQUE,"
                        + ArchivosDocumentosEntregados.ID_DOCUMENTO_ENTREGADO + " INTEGER NOT NULL,"
                        + ArchivosDocumentosEntregados.FECHA + " DATE DEFAULT CURRENT_TIMESTAMP,"
                        + ArchivosDocumentosEntregados.NOMBRE + " TEXT DEFAULT NULL,"
                        + ArchivosDocumentosEntregados.TIPO + " TEXT DEFAULT NULL,"
                        + ArchivosDocumentosEntregados.RUTA + " TEXT DEFAULT NULL,"
                        + ArchivosDocumentosEntregados.DESCRIPCION + " TEXT DEFAULT NULL,"
                        + ArchivosDocumentosEntregados.BODY + " TEXT DEFAULT NULL,"
                        + ArchivosDocumentosEntregados.VERSION + " DATE DEFAULT CURRENT_TIMESTAMP,"
                        + ArchivosDocumentosEntregados.INSERTADO + " INTEGER DEFAULT 1,"
                        + ArchivosDocumentosEntregados.MODIFICADO + " INTEGER DEFAULT 0,"
                        + ArchivosDocumentosEntregados.ELIMINADO + " INTEGER DEFAULT 0)"
        );
        db.execSQL(
                "CREATE TABLE " + Tablas.PRODUCTOS + "("
                        + Productos._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + Productos.ID + " TEXT UNIQUE,"
                        + Productos.PREFIJO + " TEXT NOT NULL,"
                        + Productos.CLAVE + " TEXT NOT NULL,"
                        + Productos.NOMBRE + " TEXT NOT NULL,"
                        + Productos.ID_TIPO_AMORTIZACION + " INTEGER DEFAULT NULL,"
                        + Productos.SOBRETASA + " TEXT DEFAULT NULL,"
                        + Productos.TASA_MORATORIA + " TEXT DEFAULT NULL,"
                        + Productos.PLAZO_MAXIMO + " INTEGER DEFAULT NULL,"
                        + Productos.MONTO_MAXIMO + " TEXT DEFAULT NULL,"
                        + Productos.ID_TIPO_PAGO + " INTEGER DEFAULT NULL,"
                        + Productos.VERSION + " DATE DEFAULT CURRENT_TIMESTAMP,"
                        + Productos.INSERTADO + " INTEGER DEFAULT 1,"
                        + Productos.MODIFICADO + " INTEGER DEFAULT 0,"
                        + Productos.ELIMINADO + " INTEGER DEFAULT 0)"
        );
        db.execSQL(
                "CREATE TABLE " + Tablas.BANCOS + "("
                        + Bancos._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + Bancos.ID + " TEXT UNIQUE,"
                        + Bancos.INSTITUCION + " TEXT NOT NULL,"
                        + Bancos.SUCURSAL + " TEXT NOT NULL,"
                        + Bancos.CLABE + " TEXT NOT NULL,"
                        + Bancos.NUMERO_CUENTA + " TEXT NOT NULL,"
                        + Bancos.VERSION + " DATE DEFAULT CURRENT_TIMESTAMP,"
                        + Bancos.INSERTADO + " INTEGER DEFAULT 1,"
                        + Bancos.MODIFICADO + " INTEGER DEFAULT 0,"
                        + Bancos.ELIMINADO + " INTEGER DEFAULT 0)"
        );
        db.execSQL(
                "CREATE TABLE " + Tablas.TIPOS_PAGOS + "("
                        + TiposPagos._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + TiposPagos.ID + " TEXT UNIQUE,"
                        + TiposPagos.NOMBRE + " TEXT NOT NULL,"
                        + TiposPagos.DESCRIPCION + " TEXT NOT NULL,"
                        + TiposPagos.VERSION + " DATE DEFAULT CURRENT_TIMESTAMP,"
                        + TiposPagos.INSERTADO + " INTEGER DEFAULT 1,"
                        + TiposPagos.MODIFICADO + " INTEGER DEFAULT 0,"
                        + TiposPagos.ELIMINADO + " INTEGER DEFAULT 0)"
        );
        db.execSQL(
                "CREATE TABLE " + Tablas.TIPOS_AMORTIZACION + "("
                        + TiposAmortizacion._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + TiposAmortizacion.ID + " TEXT UNIQUE,"
                        + TiposAmortizacion.NOMBRE + " TEXT NOT NULL,"
                        + TiposAmortizacion.PAGO + " TEXT NOT NULL,"
                        + TiposAmortizacion.PLAZO + " TEXT NOT NULL,"
                        + TiposAmortizacion.VERSION + " DATE DEFAULT CURRENT_TIMESTAMP,"
                        + TiposAmortizacion.INSERTADO + " INTEGER DEFAULT 1,"
                        + TiposAmortizacion.MODIFICADO + " INTEGER DEFAULT 0,"
                        + TiposAmortizacion.ELIMINADO + " INTEGER DEFAULT 0)"
        );
        db.execSQL(
                "CREATE TABLE " + Tablas.SOLICITUDES + "("
                        + Solicitudes._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + Solicitudes.ID + " TEXT UNIQUE,"
                        + Solicitudes.CLAVE + " TEXT DEFAULT NULL,"
                        + Solicitudes.CONTRATO + " TEXT DEFAULT NULL,"
                        + Solicitudes.ID_GRUPO + " TEXT DEFAULT NULL,"
                        + Solicitudes.ID_CLIENTE + " TEXT DEFAULT NULL,"
                        + Solicitudes.FECHA_SOLICITUD + " DATE DEFAULT CURRENT_TIMESTAMP,"
                        + Solicitudes.ID_PRODUCTO + " TEXT NOT NULL,"
                        + Solicitudes.NOMBRE_PRODUCTO + " TEXT NOT NULL,"
                        + Solicitudes.NOMBRE_AMORTIZACION + " TEXT NOT NULL,"
                        + Solicitudes.ID_BANCO + " TEXT NOT NULL,"
                        + Solicitudes.MONTO_SOLICITADO + " TEXT NOT NULL,"
                        + Solicitudes.PLAZO_SOLICITADO + " INTEGER NOT NULL,"
                        + Solicitudes.ID_TASA_REFERENCIA + " INTEGER NOT NULL,"
                        + Solicitudes.SOBRETASA + " TEXT NOT NULL,"
                        + Solicitudes.TASA_MORATORIA + " TEXT NOT NULL,"
                        + Solicitudes.ID_TIPO_PAGO + " INTEGER NOT NULL,"
                        + Solicitudes.ID_TIPO_AMORTIZACION + " INTEGER NOT NULL,"
                        + Solicitudes.MONTO_PAGAR + " TEXT DEFAULT NULL,"
                        + Solicitudes.MONTO_VENCIDO + " TEXT DEFAULT NULL,"
                        + Solicitudes.FECHA_VENCIMIENTO + " TEXT DEFAULT NULL,"
                        + Solicitudes.STATUS + " TEXT DEFAULT NULL,"
                        + Solicitudes.VERSION + " DATE DEFAULT CURRENT_TIMESTAMP,"
                        + Solicitudes.INSERTADO + " INTEGER DEFAULT 1,"
                        + Solicitudes.MODIFICADO + " INTEGER DEFAULT 0,"
                        + Solicitudes.ELIMINADO + " INTEGER DEFAULT 0)"
        );
        db.execSQL(
                "CREATE TABLE " + Tablas.STATUS_SOLICITUDES + "("
                        + StatusSolicitudes._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + StatusSolicitudes.ID + " TEXT UNIQUE,"
                        + StatusSolicitudes.NOMBRE + " TEXT NOT NULL,"
                        + StatusSolicitudes.INSERTADO + " INTEGER DEFAULT 1,"
                        + StatusSolicitudes.MODIFICADO + " INTEGER DEFAULT 0,"
                        + StatusSolicitudes.ELIMINADO + " INTEGER DEFAULT 0)"
        );
        db.execSQL(
                "CREATE TABLE " + Tablas.COTIZADORES + "("
                        + Cotizadores._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + Cotizadores.ID + " TEXT UNIQUE,"
                        + Cotizadores.ID_CLIENTE + " TEXT NOT NULL,"
                        + Cotizadores.FECHA_COTIZACION + " DATE DEFAULT CURRENT_TIMESTAMP,"
                        + Cotizadores.VALIDEZ + " INTEGER DEFAULT 10,"
                        + Cotizadores.FECHA_DISPOSICION + " DATE DEFAULT CURRENT_DATE,"
                        + Cotizadores.FECHA_INICIO_AMORTIZACIONES + " DATE DEFAULT CURRENT_DATE,"
                        + Cotizadores.NOMBRE_PRODUCTO_COTIZADOR + " TEXT NOT NULL,"
                        + Cotizadores.NOMBRE_AMORTIZACION_COTIZADOR + " TEXT NOT NULL,"
                        + Cotizadores.ID_PRODUCTO + " TEXT NOT NULL,"
                        + Cotizadores.MONTO_AUTORIZADO + " TEXT NOT NULL,"
                        + Cotizadores.PLAZO_AUTORIZADO + " INTEGER NOT NULL,"
                        + Cotizadores.ID_TASA_REFERENCIA + " INTEGER NOT NULL,"
                        + Cotizadores.SOBRETASA + " TEXT NOT NULL,"
                        + Cotizadores.TASA_MORATORIA + " TEXT NOT NULL,"
                        + Cotizadores.ID_TIPO_PAGO + " INTEGER NOT NULL,"
                        + Cotizadores.ID_TIPO_AMORTIZACION + " INTEGER NOT NULL,"
                        + Cotizadores.NOTAS + " TEXT DEFAULT NULL,"
                        + Cotizadores.VERSION + " DATE DEFAULT CURRENT_TIMESTAMP,"
                        + Cotizadores.INSERTADO + " INTEGER DEFAULT 1,"
                        + Cotizadores.MODIFICADO + " INTEGER DEFAULT 0,"
                        + Cotizadores.ELIMINADO + " INTEGER DEFAULT 0)"
        );
        db.execSQL(
                "CREATE TABLE " + Tablas.BITACORAS_CREDITO + "("
                        + BitacorasCredito._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + BitacorasCredito.ID + " TEXT UNIQUE,"
                        + BitacorasCredito.ID_SOLICITUD+ " TEXT NOT NULL,"
                        + BitacorasCredito.ASUNTO + " TEXT NOT NULL,"
                        + BitacorasCredito.FECHA + " DATE DEFAULT CURRENT_DATE,"
                        + BitacorasCredito.HORA + " TIME DEFAULT CURRENT_TIME,"
                        + BitacorasCredito.NUMERO_AMORTIZACION + " TEXT DEFAULT NULL,"
                        + BitacorasCredito.DETALLES_PAGO + " TEXT DEFAULT NULL,"
                        + BitacorasCredito.DESCRIPCION + " TEXT NOT NULL,"
                        + BitacorasCredito.VALOR_GARANTIA + " TEXT NOT NULL,"
                        + BitacorasCredito.DESCRIPCION_GARANTIA + " TEXT NOT NULL,"
                        + BitacorasCredito.VERSION + " DATE DEFAULT CURRENT_TIMESTAMP,"
                        + BitacorasCredito.INSERTADO + " INTEGER DEFAULT 1,"
                        + BitacorasCredito.MODIFICADO + " INTEGER DEFAULT 0,"
                        + BitacorasCredito.ELIMINADO + " INTEGER DEFAULT 0)"
        );
        db.execSQL(
                "CREATE TABLE " + Tablas.BITACORAS_CREDITO_ARCHIVOS + "("
                        + BitacorasCreditoArchivos._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + BitacorasCreditoArchivos.ID + " TEXT UNIQUE,"
                        + BitacorasCreditoArchivos.ID_BITACORA_CREDITO + " INTEGER NOT NULL,"
                        + BitacorasCreditoArchivos.FECHA + " DATE DEFAULT CURRENT_TIMESTAMP,"
                        + BitacorasCreditoArchivos.NOMBRE + " TEXT DEFAULT NULL,"
                        + BitacorasCreditoArchivos.TIPO + " TEXT DEFAULT NULL,"
                        + BitacorasCreditoArchivos.RUTA + " TEXT DEFAULT NULL,"
                        + BitacorasCreditoArchivos.BODY + " TEXT DEFAULT NULL,"
                        + BitacorasCreditoArchivos.VERSION + " DATE DEFAULT CURRENT_TIMESTAMP,"
                        + BitacorasCreditoArchivos.INSERTADO + " INTEGER DEFAULT 1,"
                        + BitacorasCreditoArchivos.MODIFICADO + " INTEGER DEFAULT 0,"
                        + BitacorasCreditoArchivos.ELIMINADO + " INTEGER DEFAULT 0)"
        );
        db.execSQL(
                "CREATE TABLE " + Tablas.INSTRUMENTOS_MONETARIOS + "("
                        + InstrumentosMonetarios._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + InstrumentosMonetarios.ID + " TEXT UNIQUE,"
                        + InstrumentosMonetarios.CLAVE + " TEXT NOT NULL,"
                        + InstrumentosMonetarios.DESCRIPCION + " TEXT NOT NULL,"
                        + InstrumentosMonetarios.VERSION + " DATE DEFAULT CURRENT_TIMESTAMP,"
                        + InstrumentosMonetarios.INSERTADO + " INTEGER DEFAULT 1,"
                        + InstrumentosMonetarios.MODIFICADO + " INTEGER DEFAULT 0,"
                        + InstrumentosMonetarios.ELIMINADO + " INTEGER DEFAULT 0)"
        );
        db.execSQL(
                "CREATE TABLE " + Tablas.CANALES_COBRANZAS + "("
                        + CanalesCobranzas._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + CanalesCobranzas.ID + " TEXT UNIQUE,"
                        + CanalesCobranzas.CLAVE + " TEXT NOT NULL,"
                        + CanalesCobranzas.NOMBRE + " TEXT NOT NULL,"
                        + CanalesCobranzas.REFERENCIA + " TEXT DEFAULT NULL,"
                        + CanalesCobranzas.VERSION + " DATE DEFAULT CURRENT_TIMESTAMP,"
                        + CanalesCobranzas.INSERTADO + " INTEGER DEFAULT 1,"
                        + CanalesCobranzas.MODIFICADO + " INTEGER DEFAULT 0,"
                        + CanalesCobranzas.ELIMINADO + " INTEGER DEFAULT 0)"
        );
        db.execSQL(
                "CREATE TABLE " + Tablas.PAGOS + "("
                        + Pagos._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + Pagos.ID + " TEXT UNIQUE,"
                        + Pagos.ID_SOLICITUD + " INTEGER NOT NULL,"
                        + Pagos.FECHA + " TEXT NOT NULL,"
                        + Pagos.MONTO + " TEXT NOT NULL,"
                        + Pagos.ID_CANAL_COBRANZA + " INTEGER DEFAULT NULL,"
                        + Pagos.ID_INSTRUMENTO_MONETARIO + " INTEGER DEFAULT NULL,"
                        + Pagos.VERSION + " DATE DEFAULT CURRENT_TIMESTAMP,"
                        + Pagos.INSERTADO + " INTEGER DEFAULT 1,"
                        + Pagos.MODIFICADO + " INTEGER DEFAULT 0,"
                        + Pagos.ELIMINADO + " INTEGER DEFAULT 0)"
        );
        db.execSQL(
                "CREATE TABLE " + Tablas.CODIGOS_POSTALES + "("
                        + CodigosPostales._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + CodigosPostales.ID + " TEXT UNIQUE,"
                        + CodigosPostales.CODIGO_POSTAL + " TEXT NOT NULL,"
                        + CodigosPostales.ID_MUNICIPIO + " INTEGER NOT NULL,"
                        + CodigosPostales.VERSION + " DATE DEFAULT CURRENT_TIMESTAMP,"
                        + CodigosPostales.INSERTADO + " INTEGER DEFAULT 1,"
                        + CodigosPostales.MODIFICADO + " INTEGER DEFAULT 0,"
                        + CodigosPostales.ELIMINADO + " INTEGER DEFAULT 0)"
        );
        db.execSQL(
                "CREATE TABLE " + Tablas.CODIGOS + "("
                        + Codigos._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + Codigos.ID + " TEXT UNIQUE,"
                        + Codigos.ID_ESTADO + " INTEGER NOT NULL,"
                        + Codigos.ID_MUNICIPIO + " INTEGER NOT NULL,"
                        + Codigos.CP + " TEXT NOT NULL,"
                        + Codigos.ASENTAMIENTO + " TEXT NOT NULL,"
                        + Codigos.TIPO + " TEXT NOT NULL,"
                        + Codigos.VERSION + " DATE DEFAULT CURRENT_TIMESTAMP,"
                        + Codigos.INSERTADO + " INTEGER DEFAULT 1,"
                        + Codigos.MODIFICADO + " INTEGER DEFAULT 0,"
                        + Codigos.ELIMINADO + " INTEGER DEFAULT 0)"
        );
        db.execSQL(
                "CREATE TABLE " + Tablas.PAISES + "("
                        + Paises._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + Paises.ID + " TEXT UNIQUE,"
                        + Paises.PAIS + " TEXT NOT NULL,"
                        + Paises.ISO + " INTEGER NOT NULL,"
                        + Paises.RIESGO + " TEXT NOT NULL,"
                        + Paises.PREDETERMINADO + " INTEGER DEFAULT 0,"
                        + Paises.VERSION + " DATE DEFAULT CURRENT_TIMESTAMP,"
                        + Paises.INSERTADO + " INTEGER DEFAULT 1,"
                        + Paises.MODIFICADO + " INTEGER DEFAULT 0,"
                        + Paises.ELIMINADO + " INTEGER DEFAULT 0)"
        );
        db.execSQL(
                "CREATE TABLE " + Tablas.ESTADOS + "("
                        + Estados._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + Estados.ID + " TEXT UNIQUE,"
                        + Estados.ESTADO + " TEXT NOT NULL,"
                        + Estados.ID_PAIS + " INTEGER NOT NULL,"
                        + Estados.CLAVE + " TEXT NOT NULL,"
                        + Estados.RIESGO + " INTEGER NOT NULL,"
                        + Estados.CLAVE_BURO + " TEXT NOT NULL,"
                        + Estados.VERSION + " DATE DEFAULT CURRENT_TIMESTAMP,"
                        + Estados.INSERTADO + " INTEGER DEFAULT 1,"
                        + Estados.MODIFICADO + " INTEGER DEFAULT 0,"
                        + Estados.ELIMINADO + " INTEGER DEFAULT 0)"
        );
        db.execSQL(
                "CREATE TABLE " + Tablas.MUNICIPIOS + "("
                        + Municipios._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + Municipios.ID + " TEXT UNIQUE,"
                        + Municipios.MUNICIPIO + " TEXT NOT NULL,"
                        + Municipios.ID_ESTADO + " INTEGER NOT NULL,"
                        + Municipios.VERSION + " DATE DEFAULT CURRENT_TIMESTAMP,"
                        + Municipios.INSERTADO + " INTEGER DEFAULT 1,"
                        + Municipios.MODIFICADO + " INTEGER DEFAULT 0,"
                        + Municipios.ELIMINADO + " INTEGER DEFAULT 0)"
        );
        db.execSQL(
                "CREATE TABLE " + Tablas.ESTADOS_CIVILES + "("
                        + EstadosCiviles._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + EstadosCiviles.ID + " TEXT UNIQUE,"
                        + EstadosCiviles.ESTADO_CIVIL + " TEXT NOT NULL,"
                        + EstadosCiviles.VERSION + " DATE DEFAULT CURRENT_TIMESTAMP,"
                        + EstadosCiviles.INSERTADO + " INTEGER DEFAULT 1,"
                        + EstadosCiviles.MODIFICADO + " INTEGER DEFAULT 0,"
                        + EstadosCiviles.ELIMINADO + " INTEGER DEFAULT 0)"
        );
        db.execSQL(
                "CREATE TABLE " + Tablas.CATEGORIAS_ACTIVIDADES_ECONOMICAS + "("
                        + CategoriasActividadesEconomicas._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + CategoriasActividadesEconomicas.ID + " TEXT UNIQUE,"
                        + CategoriasActividadesEconomicas.NOMBRE + " TEXT NOT NULL,"
                        + CategoriasActividadesEconomicas.DESCRIPCION + " TEXT NOT NULL,"
                        + CategoriasActividadesEconomicas.VERSION + " DATE DEFAULT CURRENT_TIMESTAMP,"
                        + CategoriasActividadesEconomicas.INSERTADO + " INTEGER DEFAULT 1,"
                        + CategoriasActividadesEconomicas.MODIFICADO + " INTEGER DEFAULT 0,"
                        + CategoriasActividadesEconomicas.ELIMINADO + " INTEGER DEFAULT 0)"
        );
        db.execSQL(
                "CREATE TABLE " + Tablas.ACTIVIDADES_ECONOMICAS + "("
                        + ActividadesEconomicas._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + ActividadesEconomicas.ID + " TEXT UNIQUE,"
                        + ActividadesEconomicas.ID_CATEGORIA + " INTEGER NOT NULL,"
                        + ActividadesEconomicas.CLAVE + " TEXT NOT NULL,"
                        + ActividadesEconomicas.DESCRIPCION + " TEXT NOT NULL,"
                        + ActividadesEconomicas.RIESGO + " INTEGER NOT NULL,"
                        + ActividadesEconomicas.VERSION + " DATE DEFAULT CURRENT_TIMESTAMP,"
                        + ActividadesEconomicas.INSERTADO + " INTEGER DEFAULT 1,"
                        + ActividadesEconomicas.MODIFICADO + " INTEGER DEFAULT 0,"
                        + ActividadesEconomicas.ELIMINADO + " INTEGER DEFAULT 0)"
        );

        //-------------------------------------------------

        db.execSQL("INSERT INTO " + Tablas.ES_CLIENTES + " VALUES (0,0,'NO',0,0,'2019-09-09 09:00:00')");
        db.execSQL("INSERT INTO " + Tablas.ES_CLIENTES + " VALUES (1,1,'SI',0,0,'2019-09-09 09:00:00')");

        db.execSQL("INSERT INTO " + Tablas.STATUS_SOLICITUDES + " VALUES (0,0,'Registrada',0,0,'2019-09-09 09:00:00')");
        db.execSQL("INSERT INTO " + Tablas.STATUS_SOLICITUDES + " VALUES (1,1,'En Revisión',0,0,'2019-09-09 09:00:00')");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        try {
                db.execSQL("DROP TABLE IF EXISTS " + Tablas.ES_CLIENTES);
                db.execSQL("DROP TABLE IF EXISTS " + Tablas.CLIENTES);
                db.execSQL("DROP TABLE IF EXISTS " + Tablas.GRUPOS);
                db.execSQL("DROP TABLE IF EXISTS " + Tablas.TIPOS_PERSONAS);
                db.execSQL("DROP TABLE IF EXISTS " + Tablas.TIPOS_CONTACTOS);
                db.execSQL("DROP TABLE IF EXISTS " + Tablas.TIPOS_DOCUMENTOS);
                db.execSQL("DROP TABLE IF EXISTS " + Tablas.DOCUMENTOS_REQUERIDOS);
                db.execSQL("DROP TABLE IF EXISTS " + Tablas.DOCUMENTOS_ENTREGADOS);
                db.execSQL("DROP TABLE IF EXISTS " + Tablas.ARCHIVOS_DOCUMENTOS_ENTREGADOS);
                db.execSQL("DROP TABLE IF EXISTS " + Tablas.PRODUCTOS);
                db.execSQL("DROP TABLE IF EXISTS " + Tablas.BANCOS);
                db.execSQL("DROP TABLE IF EXISTS " + Tablas.TIPOS_PAGOS);
                db.execSQL("DROP TABLE IF EXISTS " + Tablas.TIPOS_AMORTIZACION);
                db.execSQL("DROP TABLE IF EXISTS " + Tablas.STATUS_SOLICITUDES);
                db.execSQL("DROP TABLE IF EXISTS " + Tablas.SOLICITUDES);
                db.execSQL("DROP TABLE IF EXISTS " + Tablas.COTIZADORES);
                db.execSQL("DROP TABLE IF EXISTS " + Tablas.BITACORAS_CREDITO);
                db.execSQL("DROP TABLE IF EXISTS " + Tablas.BITACORAS_CREDITO_ARCHIVOS);
                db.execSQL("DROP TABLE IF EXISTS " + Tablas.INSTRUMENTOS_MONETARIOS);
                db.execSQL("DROP TABLE IF EXISTS " + Tablas.CANALES_COBRANZAS);
                db.execSQL("DROP TABLE IF EXISTS " + Tablas.PAGOS);
                db.execSQL("DROP TABLE IF EXISTS " + Tablas.CODIGOS_POSTALES);
                db.execSQL("DROP TABLE IF EXISTS " + Tablas.CODIGOS);
                db.execSQL("DROP TABLE IF EXISTS " + Tablas.PAISES);
                db.execSQL("DROP TABLE IF EXISTS " + Tablas.ESTADOS);
                db.execSQL("DROP TABLE IF EXISTS " + Tablas.MUNICIPIOS);
                db.execSQL("DROP TABLE IF EXISTS " + Tablas.ESTADOS_CIVILES);
                db.execSQL("DROP TABLE IF EXISTS " + Tablas.CATEGORIAS_ACTIVIDADES_ECONOMICAS);
                db.execSQL("DROP TABLE IF EXISTS " + Tablas.ACTIVIDADES_ECONOMICAS);

        } catch (SQLiteException e) {
            // Manejo de excepciones
        }
        onCreate(db);
    }
}
