package com.softcredito.app.provider;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class SQLControladorBancos {

    private HelperDB helperDB;
    private Context ourcontext;
    private SQLiteDatabase database;

    public SQLControladorBancos(Context c) {
        ourcontext = c;
    }

    public SQLControladorBancos abrirBaseDeDatos() throws SQLException {
        helperDB = new HelperDB (ourcontext);
        database = helperDB.getWritableDatabase();
        return this;
    }

    public void cerrar() {
        helperDB.close();
    }



    public void deleteData(Integer _ID) {
        database.delete( HelperDB.Tablas.CLIENTES, "_id="
                + _ID, null);

    }
    public void insertData(Integer _ID) {
        database.insert ( HelperDB.Tablas.BANCOS, "_id="
                + _ID, null);

    }
}