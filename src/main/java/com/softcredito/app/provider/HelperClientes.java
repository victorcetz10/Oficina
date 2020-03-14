package com.softcredito.app.provider;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import com.softcredito.app.provider.Promocion.Clientes;
import com.softcredito.app.utilidades.UTiempo;

/**
 * Clase auxiliar para controlar accesos a la base de datos SQLite
 */
public class HelperClientes extends SQLiteOpenHelper {

    static final int VERSION = 1;

    static final String NOMBRE_BD = "softcreditoapp.db";


    interface Tablas {
        String CLIENTE = "cliente";
    }

    public HelperClientes(Context context) {
        super(context, NOMBRE_BD, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE " + Tablas.CLIENTE + "("
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
                        + Clientes.VERSION + " DATE DEFAULT CURRENT_TIMESTAMP,"
                        + Clientes.INSERTADO + " INTEGER DEFAULT 1,"
                        + Clientes.MODIFICADO + " INTEGER DEFAULT 0,"
                        + Clientes.ELIMINADO + " INTEGER DEFAULT 0)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        try {
                db.execSQL("DROP TABLE IF EXISTS " + Tablas.CLIENTE);
        } catch (SQLiteException e) {
            // Manejo de excepciones
        }
        onCreate(db);
    }
}
