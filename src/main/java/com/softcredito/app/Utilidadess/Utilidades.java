package com.softcredito.app.Utilidadess;

public class Utilidades {

    public static final String TABLA_BANCO="banco";
    public static final String CAMPO_ID_BANCO="id";
    public static final String CAMPO_INSTITUCION="institucion";
    public static final String CAMPO_SUCURSAL="sucursal";



    public static final String CREAR_TABLA_BANCO="CREATE TABLE " +
            ""+TABLA_BANCO+" ("+CAMPO_ID_BANCO+" INTEGER PRIMARY KEY AUTOINCREMENT, "
            +CAMPO_INSTITUCION+" TEXT, "+CAMPO_SUCURSAL+" TEXT)";

    public static final String TABLA_TIPOPERSONAS="tipos_personas";
    public static final String CAMPO_ID_TIPOPERSONAS="id";
    public static final String CAMPO_CLAVE="clave";
    public static final String CAMPO_NOMBRE="nombre";



    public static final String CREAR_TABLA_TIPOPERSONAS="CREATE TABLE " +
            ""+TABLA_TIPOPERSONAS+" ("+CAMPO_ID_TIPOPERSONAS+" INTEGER PRIMARY KEY AUTOINCREMENT, "
            +CAMPO_CLAVE+" TEXT, "+CAMPO_NOMBRE+" TEXT)";

    public static final String TABLA_PAIS="paise";
    public static final String CAMPO_ID_PAIS="id";
    public static final String CAMPO_PAIS="pais";
    public static final String CAMPO_ISO="iso";



    public static final String CREAR_TABLA_PAIS="CREATE TABLE " +
            ""+TABLA_PAIS+" ("+CAMPO_ID_PAIS+" INTEGER PRIMARY KEY AUTOINCREMENT, "
            +CAMPO_PAIS+" TEXT, "+CAMPO_ISO+" TEXT)";

    public static final String TABLA_ESTADOS="estados";
    public static final String CAMPO_ID_ESTADOS="id";
    public static final String CAMPO_ESTADO="estados";
    public static final String CAMPO_CLAVE_ESTADO="clave";



    public static final String CREAR_TABLA_ESTADOS="CREATE TABLE " +
            ""+TABLA_ESTADOS+" ("+CAMPO_ID_ESTADOS+" INTEGER PRIMARY KEY AUTOINCREMENT, "
            +CAMPO_ESTADO+" TEXT, "+CAMPO_CLAVE_ESTADO+" TEXT)";

    public static final String TABLA_MUNICIPIOS="municipios";
    public static final String CAMPO_ID_MUNICIPIOS="id";
    public static final String CAMPO_MUNICIPIO="municipio";
    public static final String CAMPO_CLAVE_MUNICIPIO="id_estado";



    public static final String CREAR_TABLA_MUNICIPIOS="CREATE TABLE " +
            ""+TABLA_MUNICIPIOS+" ("+CAMPO_ID_MUNICIPIOS+" INTEGER PRIMARY KEY AUTOINCREMENT, "
            +CAMPO_MUNICIPIO+" TEXT, "+CAMPO_CLAVE_MUNICIPIO+" TEXT)";

    public static final String TABLA_TIPOS_CONTACTOS="tipos_contactos";
    public static final String CAMPO_ID_CONTACTOS="id";
    public static final String CAMPO_NOMBRECONTACTOS="nombre";
    public static final String CAMPO_DESCRIPCION="descripcion";



    public static final String CREAR_TABLA_TIPOS_CONTACTOS="CREATE TABLE " +
            ""+TABLA_TIPOS_CONTACTOS+" ("+CAMPO_ID_CONTACTOS+" INTEGER PRIMARY KEY AUTOINCREMENT, "
            +CAMPO_NOMBRECONTACTOS+" TEXT, "+CAMPO_DESCRIPCION+" TEXT)";

    public static final String TABLA_CATEGORIA_ECONOMICAS="categorias_actividades_economicas";
    public static final String CAMPO_ID_CATEGORIA="id";
    public static final String CAMPO_NOMBRECATEGORIA="nombre";
    public static final String CAMPO_DESCRIPCIONCATEGORIA="descripcion";



    public static final String CREAR_TABLA_CATEGORIA_ECONOMICA="CREATE TABLE " +
            ""+TABLA_CATEGORIA_ECONOMICAS+" ("+CAMPO_ID_CATEGORIA+" INTEGER PRIMARY KEY AUTOINCREMENT, "
            +CAMPO_NOMBRECATEGORIA+" TEXT, "+CAMPO_DESCRIPCIONCATEGORIA+" TEXT)";

    public static final String TABLA_ACTIVIDAD_ECONOMICAS="actividades_economicas";
    public static final String CAMPO_ID_ACTIVIDAD="id";
    public static final String CAMPO_CLAVEACTIVIDAD="clave";
    public static final String CAMPO_DESCRIPCIONACTIVIDAD="descripcion";



    public static final String CREAR_TABLA_ACTIVIDAD_ECONOMICA="CREATE TABLE " +
            ""+TABLA_ACTIVIDAD_ECONOMICAS+" ("+CAMPO_ID_ACTIVIDAD+" INTEGER PRIMARY KEY AUTOINCREMENT, "
            +CAMPO_CLAVEACTIVIDAD+" TEXT, "+CAMPO_DESCRIPCIONACTIVIDAD+" TEXT)";

    public static final String TABLA_PRODUCTOS="productos";
    public static final String CAMPO_ID_PRODUCTOS="id";
    public static final String CAMPO_CLAVE_PRODUCTO="clave";
    public static final String CAMPO_NOMBRE_PRODUCTO="nombre";



    public static final String CREAR_TABLA_PRODUCTOS="CREATE TABLE " +
            ""+TABLA_PRODUCTOS+" ("+CAMPO_ID_PRODUCTOS+" INTEGER PRIMARY KEY AUTOINCREMENT, "
            +CAMPO_CLAVE_PRODUCTO+" TEXT, "+CAMPO_NOMBRE_PRODUCTO+" TEXT)";

    public static final String TABLA_TIPO_AMORTIZACION="tipo_amortizacion";
    public static final String CAMPO_ID_TIPO_AMORTIZACION="id";
    public static final String CAMPO_NOMBRE_TIPO_AMORTIZACION="nombre";
    public static final String CAMPO_DESCRIPCION_TIPO_AMORTIZACION="descripcion";



    public static final String CREAR_TABLA_TIPO_AMORTIZACION="CREATE TABLE " +
            ""+TABLA_TIPO_AMORTIZACION+" ("+CAMPO_ID_TIPO_AMORTIZACION+" INTEGER PRIMARY KEY AUTOINCREMENT, "
            +CAMPO_NOMBRE_TIPO_AMORTIZACION+" TEXT, "+CAMPO_DESCRIPCION_TIPO_AMORTIZACION+" TEXT)";

    public static final String TABLA_TIPO_PAGO="tipo_pago";
    public static final String CAMPO_ID_TIPO_PAGO="id";
    public static final String CAMPO_NOMBRE_TIPO_PAGO="nombre";
    public static final String CAMPO_DESCRIPCION_TIPO_PAGO="descripcion";



    public static final String CREAR_TABLA_TIPO_PAGO="CREATE TABLE " +
            ""+TABLA_TIPO_PAGO+" ("+CAMPO_ID_TIPO_PAGO+" INTEGER PRIMARY KEY AUTOINCREMENT, "
            +CAMPO_NOMBRE_TIPO_PAGO+" TEXT, "+CAMPO_DESCRIPCION_TIPO_PAGO+" TEXT)";

    public static final String TABLA_TIPO_DOCUMENTO="documentos_requeridos";
    public static final String CAMPO_ID_DOCUMENTO="id";
    public static final String CAMPO_NOMBRE_DOCUMENTO="nombre";
    public static final String CAMPO_DESCRIPCION_TIPO_DOCUMENTO="tipo";
    public static final String CAMPO_DESCRIPCION_RUTA_DOCUMENTO="ruta";



    public static final String CREAR_TABLA_TIPO_DOCUMENTO="CREATE TABLE " +
            ""+TABLA_TIPO_DOCUMENTO+" ("+CAMPO_ID_DOCUMENTO+" INTEGER PRIMARY KEY AUTOINCREMENT, "
            +CAMPO_NOMBRE_DOCUMENTO+" TEXT, "+CAMPO_DESCRIPCION_TIPO_DOCUMENTO+" TEXT , "+CAMPO_DESCRIPCION_RUTA_DOCUMENTO+" TEXT)";

    public static final String TABLA_OBTENER_CLIENTES="clientes_obtenidos";
    public static final String CAMPO_ID_CLIENTES="id";
    public static final String CAMPO_RFC_CLIENTES="rfc";
    public static final String CAMPO_INE_CLIENTES="ine";
    public static final String CAMPO_CURP_CLIENTES="curp";



    public static final String CREAR_TABLA_OBTENER_CLIENTES="CREATE TABLE " +
            ""+TABLA_OBTENER_CLIENTES+" ("+CAMPO_ID_CLIENTES+" INTEGER PRIMARY KEY AUTOINCREMENT, "
            +CAMPO_RFC_CLIENTES+" TEXT, "+CAMPO_INE_CLIENTES+" TEXT , "+CAMPO_CURP_CLIENTES+" TEXT)";



}
