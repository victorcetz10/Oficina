package com.softcredito.app.modelo;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.google.gson.Gson;

import com.softcredito.app.provider.Contract.Clientes;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Elemento que controla la transformación de JSON a POJO y viceversa
 */
public class ProcesadorLocalClientes {

    private static final String TAG = ProcesadorLocalClientes.class.getSimpleName();

    private interface Consulta{

        // Proyección para consulta de clientes
        String[] PROYECCION = {
                Clientes.ID,
                Clientes.TIPO_PERSONA,
                Clientes.RAZON_SOCIAL,
                Clientes.PRIMER_NOMBRE,
                Clientes.SEGUNDO_NOMBRE,
                Clientes.PRIMER_APELLIDO,
                Clientes.SEGUNDO_APELLIDO,
                Clientes.CONTACTO,
                Clientes.RELACION_CONTACTO,
                Clientes.TELEFONO,
                Clientes.CORREO,
                //
                Clientes.LATITUD,
                Clientes.LONGITUD,
                //
                Clientes.CURP,
                Clientes.RFC,
                Clientes.INE,
                Clientes.CODIGO_POSTAL,
                Clientes.PAIS,
                Clientes.ESTADO,
                Clientes.MUNICIPIO,
                Clientes.LOCALIDAD,
                Clientes.COLONIA,
                Clientes.CALLE,
                Clientes.NUMERO_EXTERIOR,
                Clientes.NUMERO_INTERIOR,
                Clientes.REFERENCIA,
                Clientes.FECHA_NACIMIENTO,
                Clientes.OCUPACION,
                Clientes.ESTADO_CIVIL,
                Clientes.ID_CATEGORIA_ACTIVIDAD_ECONOMICA,
                Clientes.ID_ACTIVIDAD_ECONOMICA,
                Clientes.CELULAR,
                Clientes.ES_CLIENTE,
                Clientes.NOTAS,
                //
                Clientes.VERSION,
                Clientes.MODIFICADO
        };

        // Indices de columnas
        int ID = 0;
        int TIPO_PERSONA = 1;
        int RAZON_SOCIAL = 2;
        int PRIMER_NOMBRE = 3;
        int SEGUNDO_NOMBRE = 4;
        int PRIMER_APELLIDO = 5;
        int SEGUNDO_APELLIDO = 6;
        int CONTACTO = 7;
        int RELACION_CONTACTO = 8;
        int TELEFONO = 9;
        int CORREO = 10;
        //
        int LATITUD = 11;
        int LONGITUD = 12;
        //
        int CURP = 13;
        int RFC = 14;
        int INE = 15;
        int CODIGO_POSTAL = 16;
        int PAIS = 17;
        int ESTADO = 18;
        int MUNICIPIO = 19;
        int LOCALIDAD = 20;
        int COLONIA = 21;
        int CALLE = 22;
        int NUMERO_EXTERIOR = 23;
        int NUMERO_INTERIOR = 24;
        int REFERENCIA = 25;
        int FECHA_NACIMIENTO = 26;
        int OCUPACION = 27;
        int ESTADO_CIVIL = 28;
        int ID_CATEGORIA_ACTIVIDAD_ECONOMICA = 29;
        int ID_ACTIVIDAD_ECONOMICA = 30;
        int CELULAR = 31;
        int ES_CLIENTE = 32;
        int NOTAS = 33;
        //
        int VERSION = 34;
        int MODIFICADO = 35;

    }

    // Mapa para filtrar solo los elementos a sincronizar
    private HashMap<String, Cliente> remotos = new HashMap<>();

    // Conversor JSON
    private Gson gson = new Gson();

    public ProcesadorLocalClientes() {
    }

    public void procesar(JSONArray arrayJson) {
        // Añadir elementos convertidos a los clientes remotos
        for (Cliente item : gson
                .fromJson(arrayJson.toString(), Cliente[].class)) {
            item.aplicarSanidad();
            remotos.put(item.id, item);//Se agregan al array para poder procesarlos despues
        }
    }

    public void procesarOperaciones(ArrayList<ContentProviderOperation> ops, ContentResolver resolver) {

        // Consultar clientes locales
        Cursor c = resolver.query(Clientes.URI_CONTENIDO,
                Consulta.PROYECCION,
                Clientes.INSERTADO + "=?",
                new String[]{"0"}, null);

        if (c != null) {

            while (c.moveToNext()) {

                // Convertir fila del cursor en objeto Cliente
                Cliente filaActual = deCursorACliente(c);

                // Buscar si el cliente actual se encuentra en el mapa de mapaclientes
                Cliente match = remotos.get(filaActual.id);

                if (match != null) {
                    // Esta entrada existe, por lo que se remueve del mapeado
                    remotos.remove(filaActual.id);

                    // Crear uri de este cliente
                    Uri updateUri = Clientes.construirUri(filaActual.id);

                    /*
                    Aquí se aplica la resolución de conflictos de modificaciones de un mismo recurso
                    tanto en el servidro como en la app. Quién tenga la versión más actual, será tomado
                    como preponderante
                     */
                    if (match.compararCon(filaActual)) {
                        int flag = match.esMasReciente(filaActual);
                        if (flag > 0) {
                            Log.d(TAG, "Programar actualización  del cliente " + updateUri);

                            // Verificación: ¿Existe conflicto de modificación?
                            if (filaActual.modificado == 1) {
                                match.modificado = 0;
                            }
                            ops.add(construirOperacionUpdate(match, updateUri));                        }

                    }

                } else {
                    /*
                    Se deduce que aquellos elementos que no coincidieron, ya no existen en el servidor,
                    por lo que se eliminarán
                     */
                    Uri deleteUri = Clientes.construirUri(filaActual.id);
                    Log.i(TAG, "Programar Eliminación del cliente " + deleteUri);
                    ops.add(ContentProviderOperation.newDelete(deleteUri).build());
                }
            }
            c.close();
        }

        // Insertar los items resultantes ya que se asume que no existen en el cliente
        for (Cliente cliente : remotos.values()) {
            Log.d(TAG, "Programar Inserción de un nuevo cliente con ID = " + cliente.id);
            ops.add(construirOperacionInsert(cliente));
        }
    }

    private ContentProviderOperation construirOperacionInsert(Cliente cliente) {
        return ContentProviderOperation.newInsert(Clientes.URI_CONTENIDO)
                .withValue(Clientes.ID, cliente.id)
                .withValue(Clientes.TIPO_PERSONA, cliente.tipo_persona)
                .withValue(Clientes.RAZON_SOCIAL, cliente.razon_social)
                .withValue(Clientes.PRIMER_NOMBRE, cliente.nombre1)
                .withValue(Clientes.SEGUNDO_NOMBRE, cliente.nombre2)
                .withValue(Clientes.PRIMER_APELLIDO, cliente.apellido_paterno)
                .withValue(Clientes.SEGUNDO_APELLIDO, cliente.apellido_materno)
                .withValue(Clientes.CONTACTO, cliente.contacto)
                .withValue(Clientes.RELACION_CONTACTO, cliente.relacion_contacto)
                .withValue(Clientes.TELEFONO, cliente.telefono)
                .withValue(Clientes.CORREO, cliente.correo)
                //
                .withValue(Clientes.LATITUD, cliente.latitud)
                .withValue(Clientes.LONGITUD, cliente.longitud)
                //
                .withValue(Clientes.CURP, cliente.curp)
                .withValue(Clientes.RFC, cliente.rfc)
                .withValue(Clientes.INE, cliente.ine)
                .withValue(Clientes.CODIGO_POSTAL, cliente.codigo_postal)
                .withValue(Clientes.PAIS, cliente.pais)
                .withValue(Clientes.ESTADO, cliente.estado)
                .withValue(Clientes.MUNICIPIO, cliente.municipio)
                .withValue(Clientes.LOCALIDAD, cliente.localidad)
                .withValue(Clientes.COLONIA, cliente.colonia)
                .withValue(Clientes.CALLE, cliente.calle)
                .withValue(Clientes.NUMERO_EXTERIOR, cliente.numero_exterior)
                .withValue(Clientes.NUMERO_INTERIOR, cliente.numero_interior)
                .withValue(Clientes.REFERENCIA, cliente.referencia)
                .withValue(Clientes.FECHA_NACIMIENTO, cliente.fecha_nacimiento)
                .withValue(Clientes.OCUPACION, cliente.ocupacion)
                .withValue(Clientes.ESTADO_CIVIL, cliente.estado_civil)
                .withValue(Clientes.ID_CATEGORIA_ACTIVIDAD_ECONOMICA, cliente.id_categoria_actividad_economica)
                .withValue(Clientes.ID_ACTIVIDAD_ECONOMICA, cliente.id_actividad_economica)
                .withValue(Clientes.CELULAR, cliente.celular)
                .withValue(Clientes.ES_CLIENTE, cliente.es_cliente)
                .withValue(Clientes.NOTAS, cliente.notas)
                //
                .withValue(Clientes.VERSION, cliente.version)
                .withValue(Clientes.INSERTADO, 0)
                .build();
    }

    private ContentProviderOperation construirOperacionUpdate(Cliente match, Uri updateUri) {
        return ContentProviderOperation.newUpdate(updateUri)
                .withValue(Clientes.ID, match.id)
                .withValue(Clientes.TIPO_PERSONA, match.tipo_persona)
                .withValue(Clientes.RAZON_SOCIAL, match.razon_social)
                .withValue(Clientes.PRIMER_NOMBRE, match.nombre1)
                .withValue(Clientes.SEGUNDO_NOMBRE, match.nombre2)
                .withValue(Clientes.PRIMER_APELLIDO, match.apellido_paterno)
                .withValue(Clientes.SEGUNDO_APELLIDO, match.apellido_materno)
                .withValue(Clientes.CONTACTO, match.contacto)
                .withValue(Clientes.RELACION_CONTACTO, match.relacion_contacto)
                .withValue(Clientes.TELEFONO, match.telefono)
                .withValue(Clientes.CORREO, match.correo)
                //
                .withValue(Clientes.LATITUD, match.latitud)
                .withValue(Clientes.LONGITUD, match.longitud)
                //
                .withValue(Clientes.CURP, match.curp)
                .withValue(Clientes.RFC, match.rfc)
                .withValue(Clientes.INE, match.ine)
                .withValue(Clientes.CODIGO_POSTAL, match.codigo_postal)
                .withValue(Clientes.PAIS, match.pais)
                .withValue(Clientes.ESTADO, match.estado)
                .withValue(Clientes.MUNICIPIO, match.municipio)
                .withValue(Clientes.LOCALIDAD, match.localidad)
                .withValue(Clientes.COLONIA, match.colonia)
                .withValue(Clientes.CALLE, match.calle)
                .withValue(Clientes.NUMERO_EXTERIOR, match.numero_exterior)
                .withValue(Clientes.NUMERO_INTERIOR, match.numero_interior)
                .withValue(Clientes.REFERENCIA, match.referencia)
                .withValue(Clientes.FECHA_NACIMIENTO, match.fecha_nacimiento)
                .withValue(Clientes.OCUPACION, match.ocupacion)
                .withValue(Clientes.ESTADO_CIVIL, match.estado_civil)
                .withValue(Clientes.ID_CATEGORIA_ACTIVIDAD_ECONOMICA, match.id_categoria_actividad_economica)
                .withValue(Clientes.ID_ACTIVIDAD_ECONOMICA, match.id_actividad_economica)
                .withValue(Clientes.CELULAR, match.celular)
                .withValue(Clientes.ES_CLIENTE, match.es_cliente)
                .withValue(Clientes.NOTAS, match.notas)
                //
                .withValue(Clientes.VERSION, match.version)
                .withValue(Clientes.MODIFICADO, match.modificado)
                .build();
    }

    /**
     * Convierte una fila de un Cursor en un nuevo Cliente
     *
     * @param c cursor
     * @return objeto cliente
     */
    private Cliente deCursorACliente(Cursor c) {
        return new Cliente(
                c.getString(Consulta.ID),
                c.getString(Consulta.TIPO_PERSONA),
                c.getString(Consulta.RAZON_SOCIAL),
                c.getString(Consulta.PRIMER_NOMBRE),
                c.getString(Consulta.SEGUNDO_NOMBRE),
                c.getString(Consulta.PRIMER_APELLIDO),
                c.getString(Consulta.SEGUNDO_APELLIDO),
                c.getString(Consulta.CONTACTO),
                c.getString(Consulta.RELACION_CONTACTO),
                c.getString(Consulta.TELEFONO),
                c.getString(Consulta.CORREO),
                //
                c.getString(Consulta.LATITUD),
                c.getString(Consulta.LONGITUD),
                //
                c.getString(Consulta.CURP),
                c.getString(Consulta.RFC),
                c.getString(Consulta.INE),
                c.getString(Consulta.CODIGO_POSTAL),
                c.getString(Consulta.PAIS),
                c.getString(Consulta.ESTADO),
                c.getString(Consulta.MUNICIPIO),
                c.getString(Consulta.LOCALIDAD),
                c.getString(Consulta.COLONIA),
                c.getString(Consulta.CALLE),
                c.getString(Consulta.NUMERO_EXTERIOR),
                c.getString(Consulta.NUMERO_INTERIOR),
                c.getString(Consulta.REFERENCIA),
                c.getString(Consulta.FECHA_NACIMIENTO),
                c.getString(Consulta.OCUPACION),
                c.getString(Consulta.ESTADO_CIVIL),
                c.getInt(Consulta.ID_CATEGORIA_ACTIVIDAD_ECONOMICA),
                c.getInt(Consulta.ID_ACTIVIDAD_ECONOMICA),
                c.getString(Consulta.CELULAR),
                c.getInt(Consulta.ES_CLIENTE),
                c.getString(Consulta.NOTAS),
                //
                c.getString(Consulta.VERSION),
                c.getInt(Consulta.MODIFICADO)
        );
    }
}
