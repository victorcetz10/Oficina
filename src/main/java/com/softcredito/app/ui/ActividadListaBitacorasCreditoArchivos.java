package com.softcredito.app.ui;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
//import android.support.design.widget.FloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
//import android.support.v4.app.ActivityCompat;
import androidx.core.app.ActivityCompat;
//import android.support.v4.app.LoaderManager;
import androidx.loader.app.LoaderManager;
//import android.support.v4.content.ContextCompat;
import androidx.core.content.ContextCompat;
//import android.support.v4.content.CursorLoader;
import androidx.loader.content.CursorLoader;
//import android.support.v4.content.FileProvider;
import androidx.core.content.FileProvider;
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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.softcredito.app.BuildConfig;
import com.softcredito.app.R;
import com.softcredito.app.config.Config;
import com.softcredito.app.provider.Contract.BitacorasCreditoArchivos;
import com.softcredito.app.provider.Contract.BitacorasCredito;
import com.softcredito.app.utilidades.UTiempo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ActividadListaBitacorasCreditoArchivos extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final String TAG = ActividadListaBitacorasCreditoArchivos.class.getSimpleName();
    private final String ruta_bitacoras_credito= Environment.getExternalStorageDirectory().getAbsolutePath() +"/softcredito/bitacoras_credito/";
    public static final String URI_SOLICITUD = "extra.uriSolicitud";
    public static final String URI_BITACORA_CREDITO = "extra.uriBitacoraCredito";

    private Uri uriSolicitud;
    private Uri uriBitacoraCredito;

    // Referencias UI
    private RecyclerView reciclador;
    private LinearLayoutManager layoutManager;
    private AdaptadorBitacorasCreditoArchivos adaptador;

    //Archivo Foto
    private String nombre_unico;
    private String archivo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actividad_lista_bitacoras_credito_archivos);

        // Obtener el uri de la soliciutd
        String uri = getIntent().getStringExtra(URI_SOLICITUD);
        if (uri != null) {
            uriSolicitud= Uri.parse(uri);
        }
        // Obtener el uri de la bitacora de crédito
        String uri2 = getIntent().getStringExtra(URI_BITACORA_CREDITO);
        if (uri2 != null) {
            uriBitacoraCredito= Uri.parse(uri2);
        }

        // Preparar elementos UI
        agregarToolbar();
        prepararFab();
        prepararLista();
        prepararMenuContextual();

        //--------
        //Se valida si se tienen los permisos para acceder a la camara
        boolean conPermiso=false;
        if (ActivityCompat.checkSelfPermission(ActividadListaBitacorasCreditoArchivos.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(ActividadListaBitacorasCreditoArchivos.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(ActividadListaBitacorasCreditoArchivos.this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        /*if (shouldShowRequestPermissionRationale(
                                Manifest.permission.CAMERA)){*/
                if(shouldShowRequestPermissionRationale(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) || shouldShowRequestPermissionRationale(
                        Manifest.permission.READ_EXTERNAL_STORAGE) || shouldShowRequestPermissionRationale(
                        Manifest.permission.CAMERA)){
                    //Si ya se le ha pedido anteriormente el permiso se muestra un mensaje explicativo
                    new AlertDialog.Builder(ActividadListaBitacorasCreditoArchivos.this)
                            .setTitle("Se requieren permisos")
                            .setMessage("SOFTCREDITO requiere leer y guardar archivos de las bitácoras en el movíl.")
                            .setPositiveButton("Permitir", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //Se envia la solicitud del permiso y si el usuario lo autoriza se activa la actividad (revisar la funcion onRequestPermissionsResult)
                                    ActivityCompat.requestPermissions(ActividadListaBitacorasCreditoArchivos.this,
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
        //------

        LoaderManager.getInstance(this).restartLoader(1, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_lista, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.accion_logout:
                logout();
                break;
            case android.R.id.home:
                Intent intent = new Intent(ActividadListaBitacorasCreditoArchivos.this, ActividadListaBitacorasCredito.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(ActividadListaBitacorasCredito.URI_SOLICITUD,uriSolicitud.toString());
                startActivity(intent);
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int position = -1;
        try {
            position = adaptador.getSelecionPosition();
        } catch (Exception e) {
            Log.d(TAG, e.getLocalizedMessage(), e);
            return super.onContextItemSelected(item);
        }
        switch (item.getItemId()) {
            case R.id.tarjeta_bitacora_credito_archivo:
                adaptador.mostrarDetalles(reciclador,null,"abrir",position);
                break;
            case R.id.btn_eliminar:
                adaptador.mostrarDetalles(reciclador,null,"borrar",position);
                break;
        }
        return super.onContextItemSelected(item);
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
                            new AlertDialog.Builder(ActividadListaBitacorasCreditoArchivos.this)
                                    .setTitle("Se requieren permisos")
                                    .setMessage("SOFTCREDITO requiere leer y guardar archivos de las bitácoras en el movíl.")
                                    .setPositiveButton("Permitir", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            //Se envia la solicitud del permiso y si el usuario lo autoriza se activa la actividad (revisar la funcion onRequestPermissionsResult)
                                            ActivityCompat.requestPermissions(ActividadListaBitacorasCreditoArchivos.this,
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

    private void agregarToolbar() {
        // Agregar toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        setTitle(R.string.titulo_actividad_actividad_bitacoras_creditos_archivos);
    }

    private void prepararFab() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Se valida si se tienen los permisos para acceder a la camara
                boolean conPermiso=false;
                if (ActivityCompat.checkSelfPermission(ActividadListaBitacorasCreditoArchivos.this, Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        /*if (shouldShowRequestPermissionRationale(
                                Manifest.permission.CAMERA)){*/
                        if(true){//Siempre se le pregunta al usuario ya que es un permiso necesario
                            //Se envia la solicitud del permiso y si el usuario lo autoriza se activa la camara (revisar la funcion onRequestPermissionsResult)
                            requestPermissions(new String[]{Manifest.permission.CAMERA},
                                    1);
                        }
                    }
                }else{
                    conPermiso=true;
                }

                if(conPermiso){
                    nombre_unico = "BC" + getCode() + ".jpg";
                    archivo = ruta_bitacoras_credito + nombre_unico;
                    File file = new File(ruta_bitacoras_credito);

                    //Si no existe crea la carpeta donde se guardaran las fotos
                    file.mkdirs();

                    File nuevo_documento = new File( archivo );

                    try {
                        nuevo_documento.createNewFile();
                    } catch (IOException ex) {
                        Log.e("ERROR ", "Error:" + ex);
                    }
                    Uri uri;
                    if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
                        uri = FileProvider.getUriForFile(view.getContext(), BuildConfig.APPLICATION_ID + ".provider",nuevo_documento);
                    }else{
                        uri = Uri.fromFile( nuevo_documento );
                    }

                    //Abre la camara para tomar la foto
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    //Guarda imagen
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);

                    //Retorna a la actividad
                    //v.getContext().startActivity(cameraIntent);
                    startActivityForResult(cameraIntent,1);
                }
                //finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {

            Bitmap bitmap;
            try {
                File file = new File(archivo);
                Uri uri;
                if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
                    uri = FileProvider.getUriForFile(reciclador.getContext(), BuildConfig.APPLICATION_ID + ".provider",file);
                }else{
                    uri = Uri.fromFile(file);
                }
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);

                if(file.exists()){
                    if(file.length()>0){
                        //Crea el registro del archivo documento entregado
                        ContentValues valores = new ContentValues();
                        String type = "image/jpeg";

                        valores.put(BitacorasCreditoArchivos.ID, BitacorasCreditoArchivos.generarId());
                        valores.put(BitacorasCreditoArchivos.ID_BITACORA_CREDITO, BitacorasCredito.obtenerId(uriBitacoraCredito));
                        valores.put(BitacorasCreditoArchivos.FECHA,UTiempo.obtenerTiempo());
                        valores.put(BitacorasCreditoArchivos.NOMBRE,nombre_unico);
                        valores.put(BitacorasCreditoArchivos.TIPO,type);
                        valores.put(BitacorasCreditoArchivos.RUTA,archivo);
                        valores.put(BitacorasCreditoArchivos.VERSION, UTiempo.obtenerTiempo());
                        // Iniciar inserción|actualización
                        new AdaptadorBitacorasCreditoArchivos.TareaAnadirBitacoraCreditoArchivo(getContentResolver(), valores).execute();
                    }else{
                        file.delete();
                    }
                }

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String getCode()
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyymmddhhmmss");
        String date = dateFormat.format(new Date() );
        String documentoCode = date;
        return documentoCode;
    }

    private void prepararLista() {
        reciclador = (RecyclerView) findViewById(R.id.reciclador);
        layoutManager = new LinearLayoutManager(this);
        adaptador = new AdaptadorBitacorasCreditoArchivos(uriSolicitud, uriBitacoraCredito);

        reciclador.setLayoutManager(layoutManager);
        reciclador.setAdapter(adaptador);
    }

    private void prepararMenuContextual() {
        registerForContextMenu(reciclador);
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
                        Intent intent = new Intent(ActividadListaBitacorasCreditoArchivos.this, LoginActivity.class);
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
        String idBitacoraCredito=BitacorasCredito.obtenerId(uriBitacoraCredito);
        return new CursorLoader(
                this,
                BitacorasCreditoArchivos.URI_CONTENIDO,
                null, BitacorasCreditoArchivos.ELIMINADO + "=? AND " + BitacorasCreditoArchivos.ID_BITACORA_CREDITO + "=?", new String[]{"0",idBitacoraCredito}, null);
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