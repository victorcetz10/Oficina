package com.softcredito.app.provider;

import android.net.Uri;
import android.provider.BaseColumns;

import java.util.UUID;

/**
 * Contract con la estructura de la base de datos y forma de las URIs
 */
public class Contract {

    interface ColumnasSincronizacion {
        String MODIFICADO = "modificado";
        String ELIMINADO = "eliminado";
        String INSERTADO = "insertado";
    }

    interface ColumnasEsClientes {
        String ID = "id"; // Pk
        String NOMBRE = "nombre";
        String VERSION = "version";
    }

    interface ColumnasClientes {
        String ID = "id"; // Pk
        String TIPO_PERSONA = "tipo_persona";
        String RAZON_SOCIAL = "razon_social";
        String PRIMER_NOMBRE = "nombre1";
        String SEGUNDO_NOMBRE = "nombre2";
        String PRIMER_APELLIDO = "apellido_paterno";
        String SEGUNDO_APELLIDO = "apellido_materno";
        String CONTACTO = "contacto";
        String RELACION_CONTACTO = "relacion_contacto";
        String TELEFONO = "telefono";
        String CORREO = "correo";
        //
        String LATITUD = "vencido";
        String LONGITUD = "por_vencer";
        //
        String CURP = "curp";
        String RFC = "rfc";
        String INE = "ine";
        String CODIGO_POSTAL = "codigo_postal";
        String PAIS = "pais";
        String ESTADO = "estado";
        String MUNICIPIO = "municipio";
        String LOCALIDAD = "localidad";
        String COLONIA = "colonia";
        String CALLE = "calle";
        String NUMERO_EXTERIOR = "numero_exterior";
        String NUMERO_INTERIOR = "numero_interior";
        String REFERENCIA = "referencia";
        String FECHA_NACIMIENTO = "fecha_nacimiento";
        String OCUPACION = "ocupacion";
        String ESTADO_CIVIL = "estado_civil";
        String ID_CATEGORIA_ACTIVIDAD_ECONOMICA = "id_categoria_actividad_economica";
        String ID_ACTIVIDAD_ECONOMICA = "id_actividad_economica";
        String CELULAR = "celular";
        String ES_CLIENTE = "es_cliente";
        String NOTAS = "notas";
        //
        String VERSION = "version";
    }
    interface ColumnasGrupos {
        String ID = "id"; // Pk
        String CLAVE = "clave";
        String NOMBRE = "nombre";
        String DESCRIPCION = "descripcion";
        String DIA_REUNION = "dia_reunion";
        String HORA_REUNION = "hora_reunion";
        String VERSION = "version";
    }
    interface ColumnasTiposPersonas {
        String ID = "id"; // Pk
        String CLAVE = "clave";
        String NOMBRE = "nombre";
        String DESCRIPCION = "descripcion";
        String VERSION = "version";
    }
    interface ColumnasTiposContactos {
        String ID = "id"; // Pk
        String NOMBRE = "nombre";
        String DESCRIPCION = "descripcion";
        String VERSION = "version";
    }
    interface ColumnasTiposDocumentos {
        String ID = "id"; // Pk
        String NOMBRE = "nombre";
        String DESCRIPCION = "descripcion";
        String VERSION = "version";
    }
    interface ColumnasDocumentosRequeridos {
        String ID = "id"; // Pk
        String NOMBRE = "nombre";
        String ID_TIPO_DOCUMENTO = "id_tipo_documento";
        String DESCRIPCION = "descripcion";
        String OPCIONAL = "opcional";
        String F_ID_DOCUMENTO_ENTREGADO = "id_documento_entregado";//Campo calculado
        String F_ID_CLIENTE = "id_cliente";//Campo calculado
        String F_ENTREGADO = "entregado";//Campo calculado
        String F_TIPO_DOCUMENTO = "tipo_documento";//Campo calculado
        String VERSION = "version";
    }
    interface ColumnasDocumentosEntregados {
        String ID = "id"; // Pk
        String ID_CLIENTE = "id_cliente";
        String ID_DOCUMENTO_REQUERIDO = "id_documento_requerido";
        String NOMBRE_DOCUMENTO = "nombre_documento";
        String DESCRIPCION = "descripcion";
        String STATUS = "status";
        String TIPO_DOCUMENTO = "tipo_documento";
        String VERSION = "version";
    }
    interface ColumnasArchivosDocumentosEntregados {
        String ID = "id"; // Pk
        String ID_DOCUMENTO_ENTREGADO = "id_documento_entregado";
        String FECHA= "fecha";
        String NOMBRE = "nombre";
        String TIPO = "tipo";
        String RUTA = "ruta";//Esta columna es relativa al dispositivo por lo que no se debe sincronizar
        String DESCRIPCION = "descripcion";
        String BODY = "body";//Esta columna no se guarda solo se utiliza oara sincronizar los archivos
        String VERSION = "version";
    }
    interface ColumnasProductos {
        String ID = "id"; // Pk
        String PREFIJO = "prefijo";
        String CLAVE = "clave";
        String NOMBRE = "nombre";
        String ID_TIPO_AMORTIZACION = "id_tipo_amortizacion";
        String SOBRETASA = "sobretasa";
        String TASA_MORATORIA = "tasa_moratoria";
        String PLAZO_MAXIMO = "plazo_maximo";
        String MONTO_MAXIMO = "monto_maximo";
        String ID_TIPO_PAGO = "id_tipo_pago";
        String VERSION = "version";
    }
    interface ColumnasBancos {
        String ID = "id"; // Pk
        String INSTITUCION = "institucion";
        String SUCURSAL = "sucursal";
        String CLABE = "clabe";
        String NUMERO_CUENTA = "numero_cuenta";
        String VERSION = "version";
    }
    interface ColumnasTiposPagos {
        String ID = "id"; // Pk
        String NOMBRE = "nombre";
        String DESCRIPCION = "descripcion";
        String VERSION = "version";
    }
    interface ColumnasTiposAmortizacion {
        String ID = "id"; // Pk
        String NOMBRE = "nombre";
        String PAGO = "pago";
        String PLAZO = "plazo";
        String VERSION = "version";
    }
    interface ColumnasStatusSolicitudes {
        String ID = "id"; // Pk
        String NOMBRE = "nombre";
        String VERSION = "version";
    }
    interface ColumnasSolicitudes {
        String ID = "id"; // Pk
        String CLAVE = "clave";
        String CONTRATO = "contrato";
        String ID_GRUPO = "id_grupo";
        String ID_CLIENTE = "id_cliente";
        String FECHA_SOLICITUD = "fecha_solicitud";
        String ID_PRODUCTO = "id_producto";
        String NOMBRE_PRODUCTO = "nombreproducto";
        String NOMBRE_AMORTIZACION = "nombreamortizacion";
        String ID_BANCO = "id_banco";
        String MONTO_SOLICITADO = "monto_solicitado";
        String PLAZO_SOLICITADO = "plazo_solicitado";
        String ID_TASA_REFERENCIA = "id_tasa_referencia";
        String SOBRETASA = "sobretasa";
        String TASA_MORATORIA = "tasa_moratoria";
        String ID_TIPO_PAGO = "id_tipo_pago";
        String ID_TIPO_AMORTIZACION = "id_tipo_amortizacion";
        String MONTO_PAGAR = "monto_pagar";//Columna local, no se sincroniza
        String MONTO_VENCIDO = "monto_vencido";//Columna local, no se sincroniza
        String FECHA_VENCIMIENTO = "fecha_vencimiento";//Columna local, no se sincroniza
        String STATUS = "status";
        String VERSION = "version";
    }
    interface ColumnasCotizadores {
        String ID = "id"; // Pk
        String ID_CLIENTE = "id_cliente";
        String FECHA_COTIZACION = "fecha_cotizacion";
        String VALIDEZ = "validez";
        String FECHA_DISPOSICION = "fecha_disposicion";
        String FECHA_INICIO_AMORTIZACIONES = "fecha_inicio_amortizaciones";
        String NOMBRE_PRODUCTO_COTIZADOR = "nombreproductocotizador";
        String NOMBRE_AMORTIZACION_COTIZADOR = "nombreamortizacioncotizador";
        String ID_PRODUCTO = "id_producto";
        String MONTO_AUTORIZADO = "monto_autorizado";
        String PLAZO_AUTORIZADO = "plazo_autorizado";
        String ID_TASA_REFERENCIA = "id_tasa_referencia";
        String SOBRETASA = "sobretasa";
        String TASA_MORATORIA = "tasa_moratoria";
        String ID_TIPO_PAGO = "id_tipo_pago";
        String ID_TIPO_AMORTIZACION = "id_tipo_amortizacion";
        String NOTAS = "notas";
        String VERSION = "version";
    }
    interface ColumnasBitacorasCredito {
        String ID = "id"; // Pk
        String ID_SOLICITUD = "id_solicitud";
        String ASUNTO = "asunto";
        String FECHA = "fecha";
        String HORA = "hora";
        String NUMERO_AMORTIZACION = "numero_amortizacion";
        String DETALLES_PAGO = "detalles_pago";
        String DESCRIPCION = "descripcion";
        String VALOR_GARANTIA = "valor_garantia";
        String DESCRIPCION_GARANTIA = "descripcion_garantia";
        String VERSION = "version";
    }
    interface ColumnasBitacorasCreditoArchivos {
        String ID = "id"; // Pk
        String ID_BITACORA_CREDITO = "id_bitacora_credito";
        String FECHA= "fecha";
        String NOMBRE = "nombre";
        String TIPO = "tipo";
        String RUTA = "ruta";//Esta columna es relativa al dispositivo por lo que no se debe sincronizar
        String BODY = "body";//Esta columna no se guarda solo se utiliza oara sincronizar los archivos
        String VERSION = "version";
    }
    interface ColumnasIntsrumentosMonetarios {
        String ID = "id"; // Pk
        String CLAVE = "clave";
        String DESCRIPCION = "descripcion";
        String VERSION = "version";
    }
    interface ColumnasCanalesCobranzas {
        String ID = "id"; // Pk
        String CLAVE = "clave";
        String NOMBRE = "nombre";
        String REFERENCIA = "referencia";
        String VERSION = "version";
    }
    interface ColumnasPagos {
        String ID = "id"; // Pk
        String ID_SOLICITUD = "id_solicitud";
        String FECHA = "fecha";
        String MONTO = "monto";
        String ID_CANAL_COBRANZA = "id_canal_cobranza";
        String ID_INSTRUMENTO_MONETARIO = "id_instrumento_monetario";
        String VERSION = "version";
    }
    interface ColumnasCodigosPostales {
        String ID = "id"; // Pk
        String CODIGO_POSTAL = "codigo_postal";
        String ID_MUNICIPIO = "id_municipio";
        String VERSION = "version";
    }
    interface ColumnasCodigos {
        String ID = "id"; // Pk
        String ID_ESTADO = "id_estado";
        String ID_MUNICIPIO = "id_municipio";
        String CP = "cp";
        String ASENTAMIENTO = "asentamiento";
        String TIPO = "tipo";
        String VERSION = "version";
    }
    interface ColumnasPaises {
        String ID = "id"; // Pk
        String PAIS = "pais";
        String ISO = "iso";
        String RIESGO = "riesgo";
        String PREDETERMINADO = "predeterminado";
        String VERSION = "version";
    }
    interface ColumnasEstados {
        String ID = "id"; // Pk
        String ESTADO = "estado";
        String ID_PAIS = "id_pais";
        String CLAVE = "clave";
        String RIESGO = "riesgo";
        String CLAVE_BURO = "clave_buro";
        String TIPO = "tipo";
        String VERSION = "version";
    }
    interface ColumnasMunicipios {
        String ID = "id"; // Pk
        String MUNICIPIO = "municipio";
        String ID_ESTADO = "id_estado";
        String VERSION = "version";
    }
    interface ColumnasEstadosCiviles {
        String ID = "id"; // Pk
        String ESTADO_CIVIL = "estado_civil";
        String VERSION = "version";
    }
    interface ColumnasCategoriasActividadesEconomicas {
        String ID = "id"; // Pk
        String NOMBRE = "nombre";
        String DESCRIPCION = "descripcion";
        String VERSION = "version";
    }
    interface ColumnasActividadesEconomicas {
        String ID = "id"; // Pk
        String ID_CATEGORIA = "id_categoria";
        String CLAVE = "clave";
        String DESCRIPCION = "descripcion";
        String RIESGO = "riesgo";
        String VERSION = "version";
    }

    // Autoridad del Content Provider
    public final static String AUTORIDAD = "com.softcredito.app";

    // Uri base
    public final static Uri URI_CONTENIDO_BASE = Uri.parse("content://" + AUTORIDAD);

    // Recursos
    public final static String RECURSO_ES_CLIENTES = "es_clientes";
    public final static String RECURSO_CLIENTES = "clientes";
    public final static String RECURSO_GRUPOS = "grupos";
    public final static String RECURSO_TIPOS_PERSONAS = "tipos_personas";
    public final static String RECURSO_TIPOS_CONTACTOS = "tipos_contactos";
    public final static String RECURSO_TIPOS_DOCUMENTOS = "tipos_documentos";
    public final static String RECURSO_DOCUMENTOS_REQUERIDOS = "documentos_requeridos";
    public final static String RECURSO_DOCUMENTOS_REQUERIDOS_CLIENTE = "documentos_requeridos_cliente";
    public final static String RECURSO_DOCUMENTOS_ENTREGADOS = "documentos_entregados";
    public final static String RECURSO_ARCHIVOS_DOCUMENTOS_ENTREGADOS = "archivos_documentos_entregados";
    public final static String RECURSO_ARCHIVOS_DOCUMENTOS_ENTREGADOS_REQUERIDO = "archivos_documentos_entregados_requerido";
    public final static String RECURSO_PRODUCTOS = "productos";
    public final static String RECURSO_BANCOS = "bancos";
    public final static String RECURSO_TIPOS_PAGOS = "tipos_pagos";
    public final static String RECURSO_TIPOS_AMORTIZACION = "tipos_amortizacion";
    public final static String RECURSO_STATUS_SOLICITUDES = "status_solicitudes";
    public final static String RECURSO_SOLICITUDES = "solicitudes";
    public final static String RECURSO_COTIZADORES = "cotizadores";
    public final static String RECURSO_BITACORAS_CREDITO = "bitacoras_credito";
    public final static String RECURSO_BITACORAS_CREDITO_ARCHIVOS = "bitacoras_credito_archivos";
    public final static String RECURSO_INSTRUMENTOS_MONETARIOS = "instrumentos_monetarios";
    public final static String RECURSO_CANALES_COBRANZAS = "canales_cobranzas";
    public final static String RECURSO_PAGOS = "pagos";
    public final static String RECURSO_CODIGOS_POSTALES = "codigos_postales";
    public final static String RECURSO_CODIGOS = "codigos";
    public final static String RECURSO_PAISES = "paises";
    public final static String RECURSO_ESTADOS = "estados";
    public final static String RECURSO_MUNICIPIOS = "municipios";
    public final static String RECURSO_ESTADOS_CIVILES = "estados_civiles";
    public final static String RECURSO_CATEGORIAS_ACTIVIDADES_ECONOMICAS = "categorias_actividades_economicas";
    public final static String RECURSO_ACTIVIDADES_ECONOMICAS = "actividades_economicas";



    /**
     * Controlador de la tabla "es_clientes"
     */
    public static class EsClientes
            implements BaseColumns, ColumnasEsClientes, ColumnasSincronizacion {

        public static final Uri URI_CONTENIDO =
                URI_CONTENIDO_BASE.buildUpon().appendPath(RECURSO_ES_CLIENTES).build();

        public final static String MIME_RECURSO =
                "vnd.android.cursor.item/vnd." + AUTORIDAD + "/" + RECURSO_ES_CLIENTES;

        public final static String MIME_COLECCION =
                "vnd.android.cursor.dir/vnd." + AUTORIDAD + "/" + RECURSO_ES_CLIENTES;


        /**
         * Construye una {@link Uri} para el {@link #ID} solicitado.
         */
        public static Uri construirUri(String id) {
            return URI_CONTENIDO.buildUpon().appendPath(id).build();
        }

        public static String generarId() {
            return "C-" + UUID.randomUUID();
        }

        public static String obtenerId(Uri uri) {
            return uri.getLastPathSegment();
        }
    }

    /**
     * Controlador de la tabla "clientes"
     */
    public static class Clientes
            implements BaseColumns, ColumnasClientes, ColumnasSincronizacion {

        public static final Uri URI_CONTENIDO =
                URI_CONTENIDO_BASE.buildUpon().appendPath(RECURSO_CLIENTES).build();

        public final static String MIME_RECURSO =
                "vnd.android.cursor.item/vnd." + AUTORIDAD + "/" + RECURSO_CLIENTES;

        public final static String MIME_COLECCION =
                "vnd.android.cursor.dir/vnd." + AUTORIDAD + "/" + RECURSO_CLIENTES;


        /**
         * Construye una {@link Uri} para el {@link #ID} solicitado.
         */
        public static Uri construirUri(String id) {
            return URI_CONTENIDO.buildUpon().appendPath(id).build();
        }

        public static String generarId() {
            return "C-" + UUID.randomUUID();
        }

        public static String obtenerId(Uri uri) {
            return uri.getLastPathSegment();
        }
    }
    /**
     * Controlador de la tabla "clientes"
     */
    public static class Grupos
            implements BaseColumns, ColumnasGrupos, ColumnasSincronizacion {

        public static final Uri URI_CONTENIDO =
                URI_CONTENIDO_BASE.buildUpon().appendPath(RECURSO_GRUPOS).build();

        public final static String MIME_RECURSO =
                "vnd.android.cursor.item/vnd." + AUTORIDAD + "/" + RECURSO_GRUPOS;

        public final static String MIME_COLECCION =
                "vnd.android.cursor.dir/vnd." + AUTORIDAD + "/" + RECURSO_GRUPOS;


        /**
         * Construye una {@link Uri} para el {@link #ID} solicitado.
         */
        public static Uri construirUri(String id) {
            return URI_CONTENIDO.buildUpon().appendPath(id).build();
        }

        public static String generarId() {
            return "C-" + UUID.randomUUID();
        }

        public static String obtenerId(Uri uri) {
            return uri.getLastPathSegment();
        }
    }
    /**
     * Controlador de la tabla "tipos_personas"
     */
    public static class TiposPersonas
            implements BaseColumns, ColumnasTiposPersonas, ColumnasSincronizacion {

        public static final Uri URI_CONTENIDO =
                URI_CONTENIDO_BASE.buildUpon().appendPath(RECURSO_TIPOS_PERSONAS).build();

        public final static String MIME_RECURSO =
                "vnd.android.cursor.item/vnd." + AUTORIDAD + "/" + RECURSO_TIPOS_PERSONAS;

        public final static String MIME_COLECCION =
                "vnd.android.cursor.dir/vnd." + AUTORIDAD + "/" + RECURSO_TIPOS_PERSONAS;


        /**
         * Construye una {@link Uri} para el {@link #ID} solicitado.
         */
        public static Uri construirUri(String id) {
            return URI_CONTENIDO.buildUpon().appendPath(id).build();
        }

        public static String generarId() {
            return "C-" + UUID.randomUUID();
        }

        public static String obtenerId(Uri uri) {
            return uri.getLastPathSegment();
        }
    }
    /**
     * Controlador de la tabla "tipos_contactos"
     */
    public static class TiposContactos
            implements BaseColumns, ColumnasTiposContactos, ColumnasSincronizacion {

        public static final Uri URI_CONTENIDO =
                URI_CONTENIDO_BASE.buildUpon().appendPath(RECURSO_TIPOS_CONTACTOS).build();

        public final static String MIME_RECURSO =
                "vnd.android.cursor.item/vnd." + AUTORIDAD + "/" + RECURSO_TIPOS_CONTACTOS;

        public final static String MIME_COLECCION =
                "vnd.android.cursor.dir/vnd." + AUTORIDAD + "/" + RECURSO_TIPOS_CONTACTOS;


        /**
         * Construye una {@link Uri} para el {@link #ID} solicitado.
         */
        public static Uri construirUri(String id) {
            return URI_CONTENIDO.buildUpon().appendPath(id).build();
        }

        public static String generarId() {
            return "C-" + UUID.randomUUID();
        }

        public static String obtenerId(Uri uri) {
            return uri.getLastPathSegment();
        }
    }
    /**
     * Controlador de la tabla "tipos_documentos"
     */
    public static class TiposDocumentos
            implements BaseColumns, ColumnasTiposDocumentos, ColumnasSincronizacion {

        public static final Uri URI_CONTENIDO =
                URI_CONTENIDO_BASE.buildUpon().appendPath(RECURSO_TIPOS_DOCUMENTOS).build();

        public final static String MIME_RECURSO =
                "vnd.android.cursor.item/vnd." + AUTORIDAD + "/" + RECURSO_TIPOS_DOCUMENTOS;

        public final static String MIME_COLECCION =
                "vnd.android.cursor.dir/vnd." + AUTORIDAD + "/" + RECURSO_TIPOS_DOCUMENTOS;


        /**
         * Construye una {@link Uri} para el {@link #ID} solicitado.
         */
        public static Uri construirUri(String id) {
            return URI_CONTENIDO.buildUpon().appendPath(id).build();
        }

        public static String generarId() {
            return "C-" + UUID.randomUUID();
        }

        public static String obtenerId(Uri uri) {
            return uri.getLastPathSegment();
        }
    }
    /**
     * Controlador de la tabla "documentos_requeridos"
     */
    public static class DocumentosRequeridos
            implements BaseColumns, ColumnasDocumentosRequeridos, ColumnasSincronizacion {

        public static final Uri URI_CONTENIDO =
                URI_CONTENIDO_BASE.buildUpon().appendPath(RECURSO_DOCUMENTOS_REQUERIDOS).build();

        public static final Uri URI_CONTENIDO_CLIENTE =
                URI_CONTENIDO_BASE.buildUpon().appendPath(RECURSO_DOCUMENTOS_REQUERIDOS_CLIENTE).build();

        public final static String MIME_RECURSO =
                "vnd.android.cursor.item/vnd." + AUTORIDAD + "/" + RECURSO_DOCUMENTOS_REQUERIDOS;

        public final static String MIME_COLECCION =
                "vnd.android.cursor.dir/vnd." + AUTORIDAD + "/" + RECURSO_DOCUMENTOS_REQUERIDOS;

        public final static String MIME_COLECCION_CLIENTE =
                "vnd.android.cursor.dir/vnd." + AUTORIDAD + "/" + RECURSO_DOCUMENTOS_REQUERIDOS_CLIENTE;

        /**
         * Construye una {@link Uri} para el {@link #ID} solicitado.
         */
        public static Uri construirUri(String id) {
            return URI_CONTENIDO.buildUpon().appendPath(id).build();
        }
        public static Uri construirUriCliente(String id) {
            return URI_CONTENIDO_CLIENTE.buildUpon().appendPath(id).build();
        }

        public static String generarId() {
            return "C-" + UUID.randomUUID();
        }
        //Aplica para el id del documento requerido y para el id del cliente
        public static String obtenerId(Uri uri) {
            return uri.getLastPathSegment();
        }
    }
    /**
     * Controlador de la tabla "documentos_entregados"
     */
    public static class DocumentosEntregados
            implements BaseColumns, ColumnasDocumentosEntregados, ColumnasSincronizacion {

        public static final Uri URI_CONTENIDO =
                URI_CONTENIDO_BASE.buildUpon().appendPath(RECURSO_DOCUMENTOS_ENTREGADOS).build();

        public final static String MIME_RECURSO =
                "vnd.android.cursor.item/vnd." + AUTORIDAD + "/" + RECURSO_DOCUMENTOS_ENTREGADOS;

        public final static String MIME_COLECCION =
                "vnd.android.cursor.dir/vnd." + AUTORIDAD + "/" + RECURSO_DOCUMENTOS_ENTREGADOS;

        /**
         * Construye una {@link Uri} para el {@link #ID} solicitado.
         */
        public static Uri construirUri(String id) {
            return URI_CONTENIDO.buildUpon().appendPath(id).build();
        }

        public static String generarId() {
            return "C-" + UUID.randomUUID();
        }

        public static String obtenerId(Uri uri) {
            return uri.getLastPathSegment();
        }
    }
    /**
     * Controlador de la tabla "archivos_documentos_entregados"
     */
    public static class ArchivosDocumentosEntregados
            implements BaseColumns, ColumnasArchivosDocumentosEntregados, ColumnasSincronizacion {

        public static final Uri URI_CONTENIDO =
                URI_CONTENIDO_BASE.buildUpon().appendPath(RECURSO_ARCHIVOS_DOCUMENTOS_ENTREGADOS).build();

        public static final Uri URI_CONTENIDO_REQUERIDO =
                URI_CONTENIDO_BASE.buildUpon().appendPath(RECURSO_ARCHIVOS_DOCUMENTOS_ENTREGADOS_REQUERIDO).build();

        public final static String MIME_RECURSO =
                "vnd.android.cursor.item/vnd." + AUTORIDAD + "/" + RECURSO_ARCHIVOS_DOCUMENTOS_ENTREGADOS;

        public final static String MIME_COLECCION =
                "vnd.android.cursor.dir/vnd." + AUTORIDAD + "/" + RECURSO_ARCHIVOS_DOCUMENTOS_ENTREGADOS;

        public final static String MIME_COLECCION_REQUERIDO =
                "vnd.android.cursor.dir/vnd." + AUTORIDAD + "/" + RECURSO_ARCHIVOS_DOCUMENTOS_ENTREGADOS_REQUERIDO;

        /**
         * Construye una {@link Uri} para el {@link #ID} solicitado.
         */
        public static Uri construirUri(String id) {
            return URI_CONTENIDO.buildUpon().appendPath(id).build();
        }
        public static Uri construirUriRequerido(String id) {
            return URI_CONTENIDO_REQUERIDO.buildUpon().appendPath(id).build();
        }

        public static String generarId() {
            return "C-" + UUID.randomUUID();
        }
        //Aplica para el id del archivo del documento entregado como para el id del documento requerido
        public static String obtenerId(Uri uri) {
            return uri.getLastPathSegment();
        }
    }
    /**
     * Controlador de la tabla "productos"
     */
    public static class Productos
            implements BaseColumns, ColumnasProductos, ColumnasSincronizacion {

        public static final Uri URI_CONTENIDO =
                URI_CONTENIDO_BASE.buildUpon().appendPath(RECURSO_PRODUCTOS).build();

        public final static String MIME_RECURSO =
                "vnd.android.cursor.item/vnd." + AUTORIDAD + "/" + RECURSO_PRODUCTOS;

        public final static String MIME_COLECCION =
                "vnd.android.cursor.dir/vnd." + AUTORIDAD + "/" + RECURSO_PRODUCTOS;


        /**
         * Construye una {@link Uri} para el {@link #ID} solicitado.
         */
        public static Uri construirUri(String id) {
            return URI_CONTENIDO.buildUpon().appendPath(id).build();
        }

        public static String generarId() {
            return "C-" + UUID.randomUUID();
        }

        public static String obtenerId(Uri uri) {
            return uri.getLastPathSegment();
        }
    }
    /**
     * Controlador de la tabla "bancos"
     */
    public static class Bancos
            implements BaseColumns, ColumnasBancos, ColumnasSincronizacion {

        public static final Uri URI_CONTENIDO =
                URI_CONTENIDO_BASE.buildUpon().appendPath(RECURSO_BANCOS).build();

        public final static String MIME_RECURSO =
                "vnd.android.cursor.item/vnd." + AUTORIDAD + "/" + RECURSO_BANCOS;

        public final static String MIME_COLECCION =
                "vnd.android.cursor.dir/vnd." + AUTORIDAD + "/" + RECURSO_BANCOS;


        /**
         * Construye una {@link Uri} para el {@link #ID} solicitado.
         */
        public static Uri construirUri(String id) {
            return URI_CONTENIDO.buildUpon().appendPath(id).build();
        }

        public static String generarId() {
            return "C-" + UUID.randomUUID();
        }

        public static String obtenerId(Uri uri) {
            return uri.getLastPathSegment();
        }
    }
    /**
     * Controlador de la tabla "tipos_pagos"
     */
    public static class TiposPagos
            implements BaseColumns, ColumnasTiposPagos, ColumnasSincronizacion {

        public static final Uri URI_CONTENIDO =
                URI_CONTENIDO_BASE.buildUpon().appendPath(RECURSO_TIPOS_PAGOS).build();

        public final static String MIME_RECURSO =
                "vnd.android.cursor.item/vnd." + AUTORIDAD + "/" + RECURSO_TIPOS_PAGOS;

        public final static String MIME_COLECCION =
                "vnd.android.cursor.dir/vnd." + AUTORIDAD + "/" + RECURSO_TIPOS_PAGOS;


        /**
         * Construye una {@link Uri} para el {@link #ID} solicitado.
         */
        public static Uri construirUri(String id) {
            return URI_CONTENIDO.buildUpon().appendPath(id).build();
        }

        public static String generarId() {
            return "C-" + UUID.randomUUID();
        }

        public static String obtenerId(Uri uri) {
            return uri.getLastPathSegment();
        }
    }
    /**
     * Controlador de la tabla "tipos_amortizacion"
     */
    public static class TiposAmortizacion
            implements BaseColumns, ColumnasTiposAmortizacion, ColumnasSincronizacion {

        public static final Uri URI_CONTENIDO =
                URI_CONTENIDO_BASE.buildUpon().appendPath(RECURSO_TIPOS_AMORTIZACION).build();

        public final static String MIME_RECURSO =
                "vnd.android.cursor.item/vnd." + AUTORIDAD + "/" + RECURSO_TIPOS_AMORTIZACION;

        public final static String MIME_COLECCION =
                "vnd.android.cursor.dir/vnd." + AUTORIDAD + "/" + RECURSO_TIPOS_AMORTIZACION;


        /**
         * Construye una {@link Uri} para el {@link #ID} solicitado.
         */
        public static Uri construirUri(String id) {
            return URI_CONTENIDO.buildUpon().appendPath(id).build();
        }

        public static String generarId() {
            return "C-" + UUID.randomUUID();
        }

        public static String obtenerId(Uri uri) {
            return uri.getLastPathSegment();
        }
    }
    /**
     * Controlador de la tabla "status_solicitudes"
     */
    public static class StatusSolicitudes
            implements BaseColumns, ColumnasStatusSolicitudes, ColumnasSincronizacion {

        public static final Uri URI_CONTENIDO =
                URI_CONTENIDO_BASE.buildUpon().appendPath(RECURSO_STATUS_SOLICITUDES).build();

        public final static String MIME_RECURSO =
                "vnd.android.cursor.item/vnd." + AUTORIDAD + "/" + RECURSO_STATUS_SOLICITUDES;

        public final static String MIME_COLECCION =
                "vnd.android.cursor.dir/vnd." + AUTORIDAD + "/" + RECURSO_SOLICITUDES;


        /**
         * Construye una {@link Uri} para el {@link #ID} solicitado.
         */
        public static Uri construirUri(String id) {
            return URI_CONTENIDO.buildUpon().appendPath(id).build();
        }

        public static String generarId() {
            return "C-" + UUID.randomUUID();
        }

        public static String obtenerId(Uri uri) {
            return uri.getLastPathSegment();
        }
    }
    /**
     * Controlador de la tabla "solicitudes"
     */
    public static class Solicitudes
            implements BaseColumns, ColumnasSolicitudes, ColumnasSincronizacion {

        public static final Uri URI_CONTENIDO =
                URI_CONTENIDO_BASE.buildUpon().appendPath(RECURSO_SOLICITUDES).build();

        public final static String MIME_RECURSO =
                "vnd.android.cursor.item/vnd." + AUTORIDAD + "/" + RECURSO_SOLICITUDES;

        public final static String MIME_COLECCION =
                "vnd.android.cursor.dir/vnd." + AUTORIDAD + "/" + RECURSO_SOLICITUDES;


        /**
         * Construye una {@link Uri} para el {@link #ID} solicitado.
         */
        public static Uri construirUri(String id) {
            return URI_CONTENIDO.buildUpon().appendPath(id).build();
        }

        public static String generarId() {
            return "C-" + UUID.randomUUID();
        }

        public static String obtenerId(Uri uri) {
            return uri.getLastPathSegment();
        }
    }
    /**
     * Controlador de la tabla "cotizador"
     */
    public static class Cotizadores
            implements BaseColumns, ColumnasCotizadores, ColumnasSincronizacion {

        public static final Uri URI_CONTENIDO =
                URI_CONTENIDO_BASE.buildUpon().appendPath(RECURSO_COTIZADORES).build();

        public final static String MIME_RECURSO =
                "vnd.android.cursor.item/vnd." + AUTORIDAD + "/" + RECURSO_COTIZADORES;

        public final static String MIME_COLECCION =
                "vnd.android.cursor.dir/vnd." + AUTORIDAD + "/" + RECURSO_COTIZADORES;


        /**
         * Construye una {@link Uri} para el {@link #ID} solicitado.
         */
        public static Uri construirUri(String id) {
            return URI_CONTENIDO.buildUpon().appendPath(id).build();
        }

        public static String generarId() {
            return "C-" + UUID.randomUUID();
        }

        public static String obtenerId(Uri uri) {
            return uri.getLastPathSegment();
        }
    }
    /**
     * Controlador de la tabla "bitacora_credito"
     */
    public static class BitacorasCredito
            implements BaseColumns, ColumnasBitacorasCredito, ColumnasSincronizacion {

        public static final Uri URI_CONTENIDO =
                URI_CONTENIDO_BASE.buildUpon().appendPath(RECURSO_BITACORAS_CREDITO).build();

        public final static String MIME_RECURSO =
                "vnd.android.cursor.item/vnd." + AUTORIDAD + "/" + RECURSO_BITACORAS_CREDITO;

        public final static String MIME_COLECCION =
                "vnd.android.cursor.dir/vnd." + AUTORIDAD + "/" + RECURSO_BITACORAS_CREDITO;


        /**
         * Construye una {@link Uri} para el {@link #ID} solicitado.
         */
        public static Uri construirUri(String id) {
            return URI_CONTENIDO.buildUpon().appendPath(id).build();
        }

        public static String generarId() {
            return "C-" + UUID.randomUUID();
        }

        public static String obtenerId(Uri uri) {
            return uri.getLastPathSegment();
        }
    }
    /**
     * Controlador de la tabla "bitacora_credito_archivos"
     */
    public static class BitacorasCreditoArchivos
            implements BaseColumns, ColumnasBitacorasCreditoArchivos, ColumnasSincronizacion {

        public static final Uri URI_CONTENIDO =
                URI_CONTENIDO_BASE.buildUpon().appendPath(RECURSO_BITACORAS_CREDITO_ARCHIVOS).build();

        public final static String MIME_RECURSO =
                "vnd.android.cursor.item/vnd." + AUTORIDAD + "/" + RECURSO_BITACORAS_CREDITO_ARCHIVOS;

        public final static String MIME_COLECCION =
                "vnd.android.cursor.dir/vnd." + AUTORIDAD + "/" + RECURSO_BITACORAS_CREDITO_ARCHIVOS;


        /**
         * Construye una {@link Uri} para el {@link #ID} solicitado.
         */
        public static Uri construirUri(String id) {
            return URI_CONTENIDO.buildUpon().appendPath(id).build();
        }

        public static String generarId() {
            return "C-" + UUID.randomUUID();
        }

        public static String obtenerId(Uri uri) {
            return uri.getLastPathSegment();
        }
    }
    /**
     * Controlador de la tabla "instrumentos_monetarios"
     */
    public static class InstrumentosMonetarios
            implements BaseColumns, ColumnasIntsrumentosMonetarios, ColumnasSincronizacion {

        public static final Uri URI_CONTENIDO =
                URI_CONTENIDO_BASE.buildUpon().appendPath(RECURSO_INSTRUMENTOS_MONETARIOS).build();

        public final static String MIME_RECURSO =
                "vnd.android.cursor.item/vnd." + AUTORIDAD + "/" + RECURSO_INSTRUMENTOS_MONETARIOS;

        public final static String MIME_COLECCION =
                "vnd.android.cursor.dir/vnd." + AUTORIDAD + "/" + RECURSO_INSTRUMENTOS_MONETARIOS;


        /**
         * Construye una {@link Uri} para el {@link #ID} solicitado.
         */
        public static Uri construirUri(String id) {
            return URI_CONTENIDO.buildUpon().appendPath(id).build();
        }

        public static String generarId() {
            return "C-" + UUID.randomUUID();
        }

        public static String obtenerId(Uri uri) {
            return uri.getLastPathSegment();
        }
    }
    /**
     * Controlador de la tabla "canales_cobranzas"
     */
    public static class CanalesCobranzas
            implements BaseColumns, ColumnasCanalesCobranzas, ColumnasSincronizacion {

        public static final Uri URI_CONTENIDO =
                URI_CONTENIDO_BASE.buildUpon().appendPath(RECURSO_CANALES_COBRANZAS).build();

        public final static String MIME_RECURSO =
                "vnd.android.cursor.item/vnd." + AUTORIDAD + "/" + RECURSO_CANALES_COBRANZAS;

        public final static String MIME_COLECCION =
                "vnd.android.cursor.dir/vnd." + AUTORIDAD + "/" + RECURSO_CANALES_COBRANZAS;


        /**
         * Construye una {@link Uri} para el {@link #ID} solicitado.
         */
        public static Uri construirUri(String id) {
            return URI_CONTENIDO.buildUpon().appendPath(id).build();
        }

        public static String generarId() {
            return "C-" + UUID.randomUUID();
        }

        public static String obtenerId(Uri uri) {
            return uri.getLastPathSegment();
        }
    }
    /**
     * Controlador de la tabla "pagos"
     */
    public static class Pagos
            implements BaseColumns, ColumnasPagos, ColumnasSincronizacion {

        public static final Uri URI_CONTENIDO =
                URI_CONTENIDO_BASE.buildUpon().appendPath(RECURSO_PAGOS).build();

        public final static String MIME_RECURSO =
                "vnd.android.cursor.item/vnd." + AUTORIDAD + "/" + RECURSO_PAGOS;

        public final static String MIME_COLECCION =
                "vnd.android.cursor.dir/vnd." + AUTORIDAD + "/" + RECURSO_PAGOS;


        /**
         * Construye una {@link Uri} para el {@link #ID} solicitado.
         */
        public static Uri construirUri(String id) {
            return URI_CONTENIDO.buildUpon().appendPath(id).build();
        }

        public static String generarId() {
            return "C-" + UUID.randomUUID();
        }

        public static String obtenerId(Uri uri) {
            return uri.getLastPathSegment();
        }
    }
    /**
     * Controlador de la tabla "codigos_postales"
     */
    public static class CodigosPostales
            implements BaseColumns, ColumnasCodigosPostales, ColumnasSincronizacion {

        public static final Uri URI_CONTENIDO =
                URI_CONTENIDO_BASE.buildUpon().appendPath(RECURSO_CODIGOS_POSTALES).build();

        public final static String MIME_RECURSO =
                "vnd.android.cursor.item/vnd." + AUTORIDAD + "/" + RECURSO_CODIGOS_POSTALES;

        public final static String MIME_COLECCION =
                "vnd.android.cursor.dir/vnd." + AUTORIDAD + "/" + RECURSO_CODIGOS_POSTALES;


        /**
         * Construye una {@link Uri} para el {@link #ID} solicitado.
         */
        public static Uri construirUri(String id) {
            return URI_CONTENIDO.buildUpon().appendPath(id).build();
        }

        public static String generarId() {
            return "C-" + UUID.randomUUID();
        }

        public static String obtenerId(Uri uri) {
            return uri.getLastPathSegment();
        }
    }
    /**
     * Controlador de la tabla "codigos"
     */
    public static class Codigos
            implements BaseColumns, ColumnasCodigos, ColumnasSincronizacion {

        public static final Uri URI_CONTENIDO =
                URI_CONTENIDO_BASE.buildUpon().appendPath(RECURSO_CODIGOS).build();

        public final static String MIME_RECURSO =
                "vnd.android.cursor.item/vnd." + AUTORIDAD + "/" + RECURSO_CODIGOS;

        public final static String MIME_COLECCION =
                "vnd.android.cursor.dir/vnd." + AUTORIDAD + "/" + RECURSO_CODIGOS;


        /**
         * Construye una {@link Uri} para el {@link #ID} solicitado.
         */
        public static Uri construirUri(String id) {
            return URI_CONTENIDO.buildUpon().appendPath(id).build();
        }

        public static String generarId() {
            return "C-" + UUID.randomUUID();
        }

        public static String obtenerId(Uri uri) {
            return uri.getLastPathSegment();
        }
    }
    /**
     * Controlador de la tabla "paises"
     */
    public static class Paises
            implements BaseColumns, ColumnasPaises, ColumnasSincronizacion {

        public static final Uri URI_CONTENIDO =
                URI_CONTENIDO_BASE.buildUpon().appendPath(RECURSO_PAISES).build();

        public final static String MIME_RECURSO =
                "vnd.android.cursor.item/vnd." + AUTORIDAD + "/" + RECURSO_PAISES;

        public final static String MIME_COLECCION =
                "vnd.android.cursor.dir/vnd." + AUTORIDAD + "/" + RECURSO_PAISES;


        /**
         * Construye una {@link Uri} para el {@link #ID} solicitado.
         */
        public static Uri construirUri(String id) {
            return URI_CONTENIDO.buildUpon().appendPath(id).build();
        }

        public static String generarId() {
            return "C-" + UUID.randomUUID();
        }

        public static String obtenerId(Uri uri) {
            return uri.getLastPathSegment();
        }
    }
    /**
     * Controlador de la tabla "estados"
     */
    public static class Estados
            implements BaseColumns, ColumnasEstados, ColumnasSincronizacion {

        public static final Uri URI_CONTENIDO =
                URI_CONTENIDO_BASE.buildUpon().appendPath(RECURSO_ESTADOS).build();

        public final static String MIME_RECURSO =
                "vnd.android.cursor.item/vnd." + AUTORIDAD + "/" + RECURSO_ESTADOS;

        public final static String MIME_COLECCION =
                "vnd.android.cursor.dir/vnd." + AUTORIDAD + "/" + RECURSO_ESTADOS;


        /**
         * Construye una {@link Uri} para el {@link #ID} solicitado.
         */
        public static Uri construirUri(String id) {
            return URI_CONTENIDO.buildUpon().appendPath(id).build();
        }

        public static String generarId() {
            return "C-" + UUID.randomUUID();
        }

        public static String obtenerId(Uri uri) {
            return uri.getLastPathSegment();
        }
    }
    /**
     * Controlador de la tabla "municipios"
     */
    public static class Municipios
            implements BaseColumns, ColumnasMunicipios, ColumnasSincronizacion {

        public static final Uri URI_CONTENIDO =
                URI_CONTENIDO_BASE.buildUpon().appendPath(RECURSO_MUNICIPIOS).build();

        public final static String MIME_RECURSO =
                "vnd.android.cursor.item/vnd." + AUTORIDAD + "/" + RECURSO_MUNICIPIOS;

        public final static String MIME_COLECCION =
                "vnd.android.cursor.dir/vnd." + AUTORIDAD + "/" + RECURSO_MUNICIPIOS;


        /**
         * Construye una {@link Uri} para el {@link #ID} solicitado.
         */
        public static Uri construirUri(String id) {
            return URI_CONTENIDO.buildUpon().appendPath(id).build();
        }

        public static String generarId() {
            return "C-" + UUID.randomUUID();
        }

        public static String obtenerId(Uri uri) {
            return uri.getLastPathSegment();
        }
    }
    /**
     * Controlador de la tabla "municipios"
     */
    public static class EstadosCiviles
            implements BaseColumns, ColumnasEstadosCiviles, ColumnasSincronizacion {

        public static final Uri URI_CONTENIDO =
                URI_CONTENIDO_BASE.buildUpon().appendPath(RECURSO_ESTADOS_CIVILES).build();

        public final static String MIME_RECURSO =
                "vnd.android.cursor.item/vnd." + AUTORIDAD + "/" + RECURSO_ESTADOS_CIVILES;

        public final static String MIME_COLECCION =
                "vnd.android.cursor.dir/vnd." + AUTORIDAD + "/" + RECURSO_ESTADOS_CIVILES;


        /**
         * Construye una {@link Uri} para el {@link #ID} solicitado.
         */
        public static Uri construirUri(String id) {
            return URI_CONTENIDO.buildUpon().appendPath(id).build();
        }

        public static String generarId() {
            return "C-" + UUID.randomUUID();
        }

        public static String obtenerId(Uri uri) {
            return uri.getLastPathSegment();
        }
    }
    /**
     * Controlador de la tabla "categorias_actividades_economicas"
     */
    public static class CategoriasActividadesEconomicas
            implements BaseColumns, ColumnasCategoriasActividadesEconomicas, ColumnasSincronizacion {

        public static final Uri URI_CONTENIDO =
                URI_CONTENIDO_BASE.buildUpon().appendPath(RECURSO_CATEGORIAS_ACTIVIDADES_ECONOMICAS).build();

        public final static String MIME_RECURSO =
                "vnd.android.cursor.item/vnd." + AUTORIDAD + "/" + RECURSO_CATEGORIAS_ACTIVIDADES_ECONOMICAS;

        public final static String MIME_COLECCION =
                "vnd.android.cursor.dir/vnd." + AUTORIDAD + "/" + RECURSO_CATEGORIAS_ACTIVIDADES_ECONOMICAS;


        /**
         * Construye una {@link Uri} para el {@link #ID} solicitado.
         */
        public static Uri construirUri(String id) {
            return URI_CONTENIDO.buildUpon().appendPath(id).build();
        }

        public static String generarId() {
            return "C-" + UUID.randomUUID();
        }

        public static String obtenerId(Uri uri) {
            return uri.getLastPathSegment();
        }
    }
     /**
     * Controlador de la tabla "categorias_actividades_economicas"
     */
    public static class ActividadesEconomicas
            implements BaseColumns, ColumnasActividadesEconomicas, ColumnasSincronizacion {

        public static final Uri URI_CONTENIDO =
                URI_CONTENIDO_BASE.buildUpon().appendPath(RECURSO_ACTIVIDADES_ECONOMICAS).build();

        public final static String MIME_RECURSO =
                "vnd.android.cursor.item/vnd." + AUTORIDAD + "/" + RECURSO_ACTIVIDADES_ECONOMICAS;

        public final static String MIME_COLECCION =
                "vnd.android.cursor.dir/vnd." + AUTORIDAD + "/" + RECURSO_ACTIVIDADES_ECONOMICAS;


        /**
         * Construye una {@link Uri} para el {@link #ID} solicitado.
         */
        public static Uri construirUri(String id) {
            return URI_CONTENIDO.buildUpon().appendPath(id).build();
        }

        public static String generarId() {
            return "C-" + UUID.randomUUID();
        }

        public static String obtenerId(Uri uri) {
            return uri.getLastPathSegment();
        }
    }
}
