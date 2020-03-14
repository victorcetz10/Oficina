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
import androidx.appcompat.app.AlertDialog;
//import android.support.v7.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatActivity;
//import android.support.v7.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView;
//import android.support.v7.widget.Toolbar;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.softcredito.app.config.Config;
import com.softcredito.app.R;
import com.softcredito.app.provider.Contract.Solicitudes;
import com.softcredito.app.provider.Contract.Clientes;

/*public class ActividadListaSolicitudes extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor>, AdaptadorSolicitudes.OnItemClickListener {
 */
public class ActividadListaSolicitudes extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final String TAG = ActividadListaSolicitudes.class.getSimpleName();
    public static final String URI_CLIENTE = "extra.uriCliente";

    private Uri uriCliente;

    // Referencias UI
    private RecyclerView reciclador;
    private LinearLayoutManager layoutManager;
    private AdaptadorSolicitudes adaptador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actividad_lista_solicitudes);

        // Preparar elementos UI
        agregarToolbar();
        prepararFab();
        prepararLista();

        // Obtener el uri del cliente
        String uri = getIntent().getStringExtra(URI_CLIENTE);
        if (uri != null) {
            uriCliente= Uri.parse(uri);
        }

        getSupportLoaderManager().restartLoader(1, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_lista, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id==R.id.accion_logout){
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
        setTitle(R.string.titulo_actividad_actividad_solicitudes);
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
        //adaptador = new AdaptadorSolicitudes(this);
        adaptador = new AdaptadorSolicitudes();

        reciclador.setLayoutManager(layoutManager);
        reciclador.setAdapter(adaptador);
    }

    void mostrarDetalles(Uri uri) {
        Intent intent = new Intent(this, ActividadInsercionSolicitud.class);
        if (null != uri) {
            intent.putExtra(ActividadInsercionSolicitud.URI_SOLICITUD, uri.toString());
        }
        intent.putExtra(ActividadInsercionSolicitud.URI_CLIENTE, uriCliente.toString());
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
                        Intent intent = new Intent(ActividadListaSolicitudes.this, LoginActivity.class);
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
        String idCliente=Clientes.obtenerId(uriCliente);
        return new CursorLoader(
                this,
                Solicitudes.URI_CONTENIDO,
                null, Solicitudes.ELIMINADO + "=? AND " + Solicitudes.ID_CLIENTE + "=?", new String[]{"0",idCliente}, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adaptador.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adaptador.swapCursor(null);
    }

    public void onClickOld(AdaptadorSolicitudes.ViewHolder holder, String id) {
        mostrarDetalles(Solicitudes.construirUri(id));
    }
}