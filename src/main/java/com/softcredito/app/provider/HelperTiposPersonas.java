package com.softcredito.app.provider;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import com.softcredito.app.provider.Contract.TiposPersonas;
import com.softcredito.app.utilidades.UTiempo;

/**
 * Clase auxiliar para controlar accesos a la base de datos SQLite
 */
public class HelperTiposPersonas extends SQLiteOpenHelper {

    static final int VERSION = 1;

    static final String NOMBRE_BD = "softcreditoapp.db";


    interface Tablas {
        String TIPOS_PERSONAS = "tipos_personas";
    }

    public HelperTiposPersonas(Context context) {
        super(context, NOMBRE_BD, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE " + Tablas.TIPOS_PERSONAS + "("
                        + TiposPersonas._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + TiposPersonas.ID + " TEXT UNIQUE,"
                        + TiposPersonas.CLAVE + " TEXT UNIQUE,"
                        + TiposPersonas.NOMBRE + " TEXT NOT NULL,"
                        + TiposPersonas.DESCRIPCION + " TEXT NOT NULL,"
                        + TiposPersonas.VERSION + " DATE DEFAULT CURRENT_TIMESTAMP,"
                        + TiposPersonas.INSERTADO + " INTEGER DEFAULT 1,"
                        + TiposPersonas.MODIFICADO + " INTEGER DEFAULT 0,"
                        + TiposPersonas.ELIMINADO + " INTEGER DEFAULT 0)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        try {
                db.execSQL("DROP TABLE IF EXISTS " + Tablas.TIPOS_PERSONAS);
        } catch (SQLiteException e) {
            // Manejo de excepciones
        }
        onCreate(db);
    }
}
