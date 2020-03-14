package com.softcredito.app.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.softcredito.app.ConexionSQLiteHelper;
import com.softcredito.app.IAxiliarPersona;
import com.softcredito.app.R;
import com.softcredito.app.ui.AdaptadorDoc;
import com.softcredito.app.ui.Document;

import java.io.File;
import java.util.ArrayList;

public class DocumentosRequeridos extends AppCompatActivity implements IAxiliarPersona {

    RecyclerView idrecyclerview;
    ArrayList<Document> personaArrayList;
    ConexionSQLiteHelper sqlLite;
    TextView hola;
    String cambio= "hola";
    final int REQUEST_CODE_GALLERY = 999;
    private AdaptadorDoc personaAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_documentos_requeridos );

        idrecyclerview = findViewById(R.id.idrecyclerview);
        personaArrayList = new ArrayList<>();
        sqlLite=new ConexionSQLiteHelper(getApplicationContext(),"softcreditoapp2.db",null,1);

        personaAdapter = new AdaptadorDoc (this, personaArrayList);

        RecyclerView recyclerView = findViewById(R.id.idrecyclerview);
        recyclerView.setLayoutManager(new GridLayoutManager (this, 1));
        recyclerView.setAdapter(personaAdapter);
        mostrarDatos();


    }
    public void click(View v){
        ActivityCompat.requestPermissions( (Activity) v.getContext (), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                REQUEST_CODE_GALLERY
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode == REQUEST_CODE_GALLERY){
            if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_CODE_GALLERY);
            }
            else {
                Toast.makeText(getApplicationContext(), "You don't have permission to access file location!", Toast.LENGTH_SHORT).show();
            }
            return;
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }



    public void mostrarDatos() {
        SQLiteDatabase sqLiteDatabase = sqlLite.getReadableDatabase();
        Document persona = null;
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM documentos_requeridos", null);
        while (cursor.moveToNext()) {
            persona = new Document();
            persona.setId (cursor.getInt(0));
            persona.setNombre(cursor.getString(1));
            persona.setTipo (cursor.getString(2));
            persona.setRuta (cursor.getString(3));
            personaAdapter.agregarPersona(persona);
        }
    }


    @Override
    public void OpcionEditar(Document persona) {
       // Intent intento1 = new Intent( MediaStore.ACTION_IMAGE_CAPTURE);

        Toast.makeText ( this, "GAy", Toast.LENGTH_SHORT ).show ();
       // startActivity(intento1);

    }

    @Override
    public void OpcionEliminar(final Document persona) {

        Toast.makeText ( this, "GAy", Toast.LENGTH_SHORT ).show ();

    }





}
