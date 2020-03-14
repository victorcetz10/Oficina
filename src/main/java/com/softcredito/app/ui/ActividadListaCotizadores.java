package com.softcredito.app.ui;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
//import android.support.design.widget.FloatingActionButton;
//import android.support.design.widget.Snackbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
//import android.support.v4.app.ActivityCompat;
import androidx.core.app.ActivityCompat;
//import android.support.v4.app.LoaderManager;
import androidx.loader.app.LoaderManager;
//import android.support.v4.content.ContextCompat;
import androidx.core.content.ContextCompat;
//import android.support.v4.content.CursorLoader;
import androidx.loader.content.CursorLoader;
//import android.support.v4.content.Loader;
import androidx.core.content.FileProvider;
import androidx.loader.content.Loader;
//import android.support.v4.content.LocalBroadcastManager;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
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
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.softcredito.app.BuildConfig;
import com.softcredito.app.R;
import com.softcredito.app.config.Config;
import com.softcredito.app.provider.Contract.Clientes;
import com.softcredito.app.provider.Contract.Cotizadores;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

public class ActividadListaCotizadores extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor>, AdaptadorCotizadores.OnItemClickListener {

    private static final String TAG = ActividadListaCotizadores.class.getSimpleName();
    private final String ruta_cotizadores = Environment.getExternalStorageDirectory().getAbsolutePath() +"/softcredito/cotizadores/";
    public static final String URI_CLIENTE = "extra.uriCliente";
    private BroadcastReceiver receptorSync;
    private ProgressDialog progress;

    private Uri uriCliente;

    // Referencias UI
    private RecyclerView reciclador;
    private LinearLayoutManager layoutManager;
    private AdaptadorCotizadores adaptador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actividad_lista_cotizadores);

        // Preparar elementos UI
        agregarToolbar();
        prepararFab();
        prepararLista();

        // Obtener el uri del cliente
        String uri = getIntent().getStringExtra(URI_CLIENTE);
        if (uri != null) {
            uriCliente= Uri.parse(uri);
        }

        progress = new ProgressDialog(this);

        // Crear receptor de mensajes de sincronización
        receptorSync = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                adaptador.progress.dismiss();
                String idCotizador = intent.getStringExtra("extra.tableRowSync");
                String conversiones = intent.getStringExtra("extra.conversionesSync");
                JSONObject oConversiones=null;
                try {
                    if(!TextUtils.isEmpty(conversiones)){
                        oConversiones = new JSONObject(conversiones);
                        if(!TextUtils.isEmpty(oConversiones.getString(idCotizador))){
                            idCotizador=oConversiones.getString(idCotizador);
                        }
                    }
                }catch (JSONException e){

                }

                File file = new File(ruta_cotizadores +"/"+ idCotizador + ".pdf");
                if(file.exists()){
                    if(file.length()>0){
                        Intent target = new Intent(Intent.ACTION_VIEW);
                        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
                            target.setDataAndType(FileProvider.getUriForFile(ActividadListaCotizadores.this, BuildConfig.APPLICATION_ID + ".provider",file),"application/pdf");
                        }else{
                            target.setDataAndType(Uri.fromFile(file),"application/pdf");
                        }
                        target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                        target.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

                        Intent openIntent = Intent.createChooser(target, "Open File");
                        try {
                            ActividadListaCotizadores.this.startActivity(openIntent);
                        } catch (ActivityNotFoundException e) {
                            Snackbar.make(ActividadListaCotizadores.this.findViewById(R.id.coordinador),
                                    "Su movìl no cuenta una App para abrir el tipo de Archivo",
                                    Snackbar.LENGTH_LONG).show();
                        };
                    }else{
                        Snackbar.make(ActividadListaCotizadores.this.findViewById(R.id.coordinador),
                                "El archivo esta vacío",
                                Snackbar.LENGTH_LONG).show();
                    }
                }else{
                    Snackbar.make(ActividadListaCotizadores.this.findViewById(R.id.coordinador),
                            "El archivo no existe",
                            Snackbar.LENGTH_LONG).show();
                }
                adaptador.notifyDataSetChanged();
            }
        };

        Boolean permiso = false;

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                permiso=true;
            }

        }
        if(!permiso){
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) || ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                new AlertDialog.Builder(ActividadListaCotizadores.this)
                        .setTitle("Se requieren permisos")
                        .setMessage("SOFTCREDITO necesita guardar los PDF de las cotizaciones en el movíl.")
                        .setPositiveButton("Permitir", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(ActividadListaCotizadores.this,
                                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE},
                                        1);


                            }

                        })
                        .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                                finish();
                            }
                        })
                        .show();
            } else {
                // Solicitar permiso
                ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE},
                        1);
            }
        }
        getSupportLoaderManager().restartLoader(1, null, this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Registrar receptor
        IntentFilter filtroSync = new IntentFilter(Intent.ACTION_SYNC);
        LocalBroadcastManager.getInstance(this).registerReceiver(receptorSync, filtroSync);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Desregistrar receptor
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receptorSync);
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
        setTitle(R.string.titulo_actividad_actividad_cotizaciones);
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
        adaptador = new AdaptadorCotizadores(this,ActividadListaCotizadores.this);

        reciclador.setLayoutManager(layoutManager);
        reciclador.setAdapter(adaptador);
    }

    void mostrarDetalles(Uri uri) {
        Intent intent = new Intent(this, ActividadInsercionCotizador.class);
        if (null != uri) {
            intent.putExtra(ActividadInsercionCotizador.URI_COTIZADOR, uri.toString());
        }
        intent.putExtra(ActividadInsercionCotizador.URI_CLIENTE, uriCliente.toString());
        startActivity(intent);
    }

    private void mostrarProgreso(boolean mostrar) {
        if(mostrar){
            progress.setTitle("Sincronizando");
            progress.setMessage("Espere mientras se sincroniza su información");
            progress.setCancelable(false);
            progress.show();
        }else{
            progress.dismiss();
        }
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
                        Intent intent = new Intent(ActividadListaCotizadores.this, LoginActivity.class);
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
                Cotizadores.URI_CONTENIDO,
                null, Cotizadores.ELIMINADO + "=? AND " + Cotizadores.ID_CLIENTE + "=?", new String[]{"0",idCliente}, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adaptador.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adaptador.swapCursor(null);
    }

    @Override
    public void onClick(AdaptadorCotizadores.ViewHolder holder, String id) {
        mostrarDetalles(Cotizadores.construirUri(id));
    }
}