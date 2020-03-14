package com.softcredito.app;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.softcredito.app.Utilidadess.Utilidades;


/**
 * Created by CHENAO on 7/05/2017.
 */

public class ConexionSQLiteHelper extends SQLiteOpenHelper {



    public ConexionSQLiteHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL( Utilidades.CREAR_TABLA_BANCO);
        db.execSQL ( Utilidades.CREAR_TABLA_TIPOPERSONAS );
        db.execSQL ( Utilidades.CREAR_TABLA_PAIS );
        db.execSQL ( Utilidades.CREAR_TABLA_ESTADOS );
        db.execSQL ( Utilidades.CREAR_TABLA_MUNICIPIOS);
        db.execSQL ( Utilidades.CREAR_TABLA_TIPOS_CONTACTOS);
        db.execSQL ( Utilidades.CREAR_TABLA_CATEGORIA_ECONOMICA);
        db.execSQL ( Utilidades.CREAR_TABLA_ACTIVIDAD_ECONOMICA);
        db.execSQL ( Utilidades.CREAR_TABLA_PRODUCTOS);
        db.execSQL ( Utilidades.CREAR_TABLA_TIPO_AMORTIZACION);
        db.execSQL ( Utilidades.CREAR_TABLA_TIPO_PAGO);
        db.execSQL ( Utilidades.CREAR_TABLA_TIPO_DOCUMENTO);
        db.execSQL ( Utilidades.CREAR_TABLA_OBTENER_CLIENTES);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int versionAntigua, int versionNueva) {
        db.execSQL("DROP TABLE IF EXISTS "+Utilidades.TABLA_BANCO);
        db.execSQL ("DROP TABLE IF EXISTS "+Utilidades.TABLA_TIPOPERSONAS );
        db.execSQL ("DROP TABLE IF EXISTS "+Utilidades.TABLA_PAIS );
        db.execSQL ("DROP TABLE IF EXISTS "+Utilidades.TABLA_ESTADOS );
        db.execSQL ("DROP TABLE IF EXISTS "+Utilidades.TABLA_MUNICIPIOS);
        db.execSQL ("DROP TABLE IF EXISTS "+Utilidades.TABLA_TIPOS_CONTACTOS);
        db.execSQL ("DROP TABLE IF EXISTS "+Utilidades.TABLA_CATEGORIA_ECONOMICAS);
        db.execSQL ("DROP TABLE IF EXISTS "+Utilidades.TABLA_ACTIVIDAD_ECONOMICAS);
        db.execSQL ("DROP TABLE IF EXISTS "+Utilidades.TABLA_PRODUCTOS);
        db.execSQL ("DROP TABLE IF EXISTS "+Utilidades.TABLA_TIPO_AMORTIZACION);
        db.execSQL ("DROP TABLE IF EXISTS "+Utilidades.TABLA_TIPO_PAGO);
        db.execSQL ("DROP TABLE IF EXISTS "+Utilidades.TABLA_TIPO_DOCUMENTO);
        db.execSQL ("DROP TABLE IF EXISTS "+Utilidades.TABLA_OBTENER_CLIENTES);
        onCreate(db);
    }
}
