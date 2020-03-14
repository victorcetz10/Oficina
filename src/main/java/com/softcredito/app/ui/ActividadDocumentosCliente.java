package com.softcredito.app.ui;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
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

import android.util.Log;
import android.view.Menu;

import com.softcredito.app.R;
import com.softcredito.app.provider.Contract.DocumentosRequeridos;

public class ActividadDocumentosCliente extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor>, AdaptadorDocumentosRequeridos.OnItemClickListener {

    private static final String TAG = ActividadDocumentosCliente.class.getSimpleName();

    // Referencias UI
    private RecyclerView reciclador;
    private LinearLayoutManager layoutManager;
    private AdaptadorDocumentosRequeridos adaptador;

    private BroadcastReceiver receptorSync;

    // Clave del uri del cliente como extra
    public static final String URI_CLIENTE = "extra.uriCliente";

    private Uri uriCliente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actividad_documentos_cliente);
        Log.e ( "claro", "aqui" );
        // Preparar elementos UI
        agregarToolbar();
        prepararLista();

        // Determinar si es detalle
        String uri = getIntent().getStringExtra(URI_CLIENTE);
        uriCliente = Uri.parse(uri);

        boolean conPermiso=false;
        if (ActivityCompat.checkSelfPermission(ActividadDocumentosCliente.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(ActividadDocumentosCliente.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(ActividadDocumentosCliente.this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        /*if (shouldShowRequestPermissionRationale(
                                Manifest.permission.CAMERA)){*/
                if(shouldShowRequestPermissionRationale(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) || shouldShowRequestPermissionRationale(
                        Manifest.permission.READ_EXTERNAL_STORAGE) || shouldShowRequestPermissionRationale(
                        Manifest.permission.CAMERA)){
                    //Si ya se le ha pedido anteriormente el permiso se muestra un mensaje explicativo
                    new AlertDialog.Builder(ActividadDocumentosCliente.this)
                            .setTitle("Se requieren permisos")
                            .setMessage("SOFTCREDITO requiere leer y guardar archivos de los expedientes en el movíl.")
                            .setPositiveButton("Permitir", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //Se envia la solicitud del permiso y si el usuario lo autoriza se activa la actividad (revisar la funcion onRequestPermissionsResult)
                                    ActivityCompat.requestPermissions(ActividadDocumentosCliente.this,
                                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.CAMERA},
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
                }else{
                    //Se envia la solicitud del permiso y si el usuario lo autoriza se activa la actividad (revisar la funcion onRequestPermissionsResult)
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.CAMERA},
                            1);
                }
            }
        }else{
            conPermiso=true;
        }

        if(conPermiso){

        }

        getSupportLoaderManager().restartLoader(1, null, this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            for (int i = 0; i < permissions.length; i++) {
                String permission = permissions[i];
                int grantResult = grantResults[i];

                if (permission.equals(Manifest.permission.CAMERA) || permission.equals(Manifest.permission.WRITE_EXTERNAL_STORAGE) || permission.equals(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    if (grantResult == PackageManager.PERMISSION_GRANTED) {

                    } else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            new AlertDialog.Builder(ActividadDocumentosCliente.this)
                                    .setTitle("Se requieren permisos")
                                    .setMessage("SOFTCREDITO requiere leer y guardar archivos de los expedientes en el movíl.")
                                    .setPositiveButton("Permitir", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            //Se envia la solicitud del permiso y si el usuario lo autoriza se activa la actividad (revisar la funcion onRequestPermissionsResult)
                                            ActivityCompat.requestPermissions(ActividadDocumentosCliente.this,
                                                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.CAMERA},
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
                        }
                    }
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_lista, menu);
        return true;
    }

    private void agregarToolbar() {
        // Agregar toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        setTitle(R.string.titulo_actividad_actividad_documentos_cliente);
    }

    private void prepararLista() {
        reciclador = (RecyclerView) findViewById(R.id.reciclador);
        layoutManager = new LinearLayoutManager(this);
        adaptador = new AdaptadorDocumentosRequeridos(this,ActividadDocumentosCliente.this);

        reciclador.setLayoutManager(layoutManager);
        reciclador.setAdapter(adaptador);
    }

    void marcarEntregado(Uri uri) {
        /*
        Intent intent = new Intent(this, ActividadInsercionCliente.class);
        if (null != uri) {
            intent.putExtra(ActividadInsercionCliente.URI_CLIENTE, uri.toString());
        }
        startActivity(intent);
        */
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {

           
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, uriCliente, null, null, null, null);
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
    public void onClick(AdaptadorDocumentosRequeridos.ViewHolder holder, String id) {
        marcarEntregado(DocumentosRequeridos.construirUri(id));
    }
}