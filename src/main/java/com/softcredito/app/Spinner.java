package com.softcredito.app;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.softcredito.app.Json.BancoCarg;
import com.softcredito.app.Json.Post;
import com.softcredito.app.Json.Tipos_Personas;
import com.softcredito.app.Utilidadess.Utilidades;

import java.util.ArrayList;

public class Spinner extends AppCompatActivity {

    android.widget.Spinner comboPersonas;
    TextView txtNombre,txtDocumento,txtTelefono;
    ArrayList<String> listaPersonas;
    ArrayList<BancoCarg> personasList;
    String nombre1;
    ConexionSQLiteHelper conn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_spinner );

        conn=new ConexionSQLiteHelper(getApplicationContext(),"softcreditoapp2.db",null,1);

        comboPersonas= (android.widget.Spinner) findViewById(R.id.comboPersonas);


        consultarListaPersonas();

        ArrayAdapter<CharSequence> adaptador=new ArrayAdapter
                (this,android.R.layout.simple_spinner_item,listaPersonas);

        comboPersonas.setAdapter(adaptador);

        comboPersonas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long idl) {

                String text2 = parent.getItemAtPosition ( position ).toString ();
                //  Toast.makeText ( parent.getContext (), "campo persona "+text2, Toast.LENGTH_SHORT ).show ();
                nombre1 = text2;
                Toast.makeText ( Spinner.this, nombre1, Toast.LENGTH_SHORT ).show ();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    private void consultarListaPersonas() {
        SQLiteDatabase db=conn.getReadableDatabase();

        BancoCarg persona=null;
        personasList =new ArrayList<BancoCarg>();
        //select * from usuarios
        Cursor cursor=db.rawQuery("SELECT * FROM "+ Utilidades.TABLA_BANCO,null);

        while (cursor.moveToNext()){
            persona=new BancoCarg ();
            persona.setId(cursor.getInt(0));
            persona.setInstitucion (cursor.getString(1));
            persona.setSucursal (cursor.getString(2));

            Log.i("id",persona.getId().toString());
            Log.i("Nombre",persona.getInstitucion ());
            Log.i("Tel",persona.getSucursal ());


            personasList.add(persona);

        }
        obtenerLista();
    }

    private void obtenerLista() {
        listaPersonas=new ArrayList<String>();
        listaPersonas.add("Seleccione");

        for(int i=0;i<personasList.size();i++){
            listaPersonas.add(personasList.get(i).getId()+" - "+personasList.get(i).getInstitucion ());
        }

    }

}
