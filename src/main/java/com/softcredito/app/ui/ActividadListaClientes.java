package com.softcredito.app.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
//import android.support.design.widget.FloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
//import android.support.v4.app.LoaderManager;
import androidx.loader.app.LoaderManager;
//import android.support.v4.content.CursorLoader;
import androidx.loader.content.CursorLoader;
//import android.support.v4.content.Loader;
import androidx.loader.content.Loader;
//import android.support.v7.app.AlertDialog;
//import android.support.v7.app.AppCompatActivity;
//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;
//import android.support.v7.widget.Toolbar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.softcredito.app.config.Config;
import com.softcredito.app.R;
import com.softcredito.app.provider.Contract.Clientes;

public class ActividadListaClientes extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final String TAG = ActividadListaClientes.class.getSimpleName();

    // Referencias UI
    private RecyclerView reciclador;
    private LinearLayoutManager layoutManager;
    private AdaptadorClientes adaptador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actividad_lista_clientes);

        // Preparar elementos UI
        agregarToolbar();
        prepararFab();
        prepararLista();

        getSupportLoaderManager().restartLoader(1, null, this);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_lista, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id==R.id.accion_logout){
            logout();
        }

        return super.onOptionsItemSelected(item);
    }

    private void agregarToolbar() {
        // Agregar toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        setTitle(R.string.titulo_actividad_actividad_clientes);
    }

    private void prepararFab() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mostrarDetalles(null);
            }
        });
    }

    private void prepararLista() {
        reciclador = (RecyclerView) findViewById(R.id.reciclador);
        layoutManager = new LinearLayoutManager(this);
        adaptador = new AdaptadorClientes();

        reciclador.setLayoutManager(layoutManager);
        reciclador.setAdapter(adaptador);
    }

    void mostrarDetalles(Uri uri) {
       Intent intent = new Intent(this, ActividadInsercionCliente.class);
        if (null != uri) {
            intent.putExtra(ActividadInsercionCliente.URI_CLIENTE, uri.toString());
        }
        startActivity(intent);
    }

    //Logout function
    private void logout() {
        //Creating an alert dialog to confirm logout
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("¿Desea salir de la aplicación?");
        alertDialogBuilder.setPositiveButton("Si",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                        //Getting out sharedpreferences
                        SharedPreferences preferences = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
                        //Getting editor
                        SharedPreferences.Editor editor = preferences.edit();

                        //Puting the value false for loggedin
                        editor.putBoolean(Config.LOGGEDIN_SHARED_PREF, false);

                        //Putting blank value to email
                        editor.putString(Config.EMAIL_SHARED_PREF, "");

                        //Saving the sharedpreferences
                        editor.commit();

                        //Starting login activity
                        Intent intent = new Intent(ActividadListaClientes.this, LoginActivity.class);
                        startActivity(intent);
                    }
                });

        alertDialogBuilder.setNegativeButton("No",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                    }
                });

        //Showing the alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(
                this,
                Clientes.URI_CONTENIDO,
                null, Clientes.ELIMINADO + "=?", new String[]{"0"}, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adaptador.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adaptador.swapCursor(null);
    }
}