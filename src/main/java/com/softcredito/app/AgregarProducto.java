package com.softcredito.app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.softcredito.app.Json.Estados;
import com.softcredito.app.Json.Estadosssss;
import com.softcredito.app.Json.JsonPlaceHolderApi;
import com.softcredito.app.Json.Municipiossss;
import com.softcredito.app.Json.Pais;
import com.softcredito.app.Json.Paisessss;
import com.softcredito.app.Json.Tipos_Personas;
import com.softcredito.app.Utilidadess.Utilidades;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AgregarProducto extends AppCompatActivity {

    private TextView textViewResultPais;
    private TextView textViewResultEstado;
    private TextView textViewResultMunicipio;
    private Context contexts;
    private Button Cargar_Personas;

    String contexto;
    Integer Id;
    String pais;
    String iso;

    Integer Ids;
    String estado;
    String clave;

    Integer Idss;
    String municipio;
    String id_estado;

    String Cuenta;
    ConexionSQLiteHelper conn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_agregar_producto );


        textViewResultPais = findViewById(R.id.text_view_result_Pais);
        textViewResultEstado = findViewById(R.id.text_view_result_Estado);
        textViewResultMunicipio = findViewById(R.id.text_view_result_Municipio);
        CargarPais ();
        CargarEstado ();
        Municipios();
    }

    public void CargarPais(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://softcredito.com/demos/dmo_unico2020Conta/app/WebService/index.php/Welcome/")
                .addConverterFactory( GsonConverterFactory.create())
                .build();

        JsonPlaceHolderApi jsonPlaceHolderApi = retrofit.create( JsonPlaceHolderApi.class);

        Call<List<Paisessss>> call = jsonPlaceHolderApi.Paisessss  ();

        call.enqueue(new Callback<List<Paisessss>> () {
            @Override
            public void onResponse(Call<List<Paisessss>> call, Response<List<Paisessss>> response) {

                if (response.isSuccessful()) {
                    //textViewResult.setText("Code: " + response.code());
                    Log.e ( "Prueba", textViewResultPais.getText ().toString () );
                    //return;

                    List<Paisessss> Paisessss = response.body();

                    for (int i = 0; i < Paisessss.size(); i++) {
                        Paisessss banco = Paisessss.get(i);
                        String content = "";
                        content += "ID: " +banco.getId () + "\n";
                        content += "PAIS: " + banco.getPais () + "\n";
                        content += "ISO: " + banco.getIso () + "\n";

                        Id = banco.getId ();
                        pais = banco.getPais ();
                        iso = banco.getIso ();



                        textViewResultPais.append(content);
                        contexto = textViewResultPais.getText ().toString ();
                        //Toast.makeText ( Actividad_CargarBancos.this, post.getInstitucion (), Toast.LENGTH_SHORT ).show ();
                        //Toast.makeText ( Actividad_CargarBancos.this, post.getSucursal (), Toast.LENGTH_SHORT ).show ();
                        //Toast.makeText ( Actividad_CargarBancos.this, post.getClabe (), Toast.LENGTH_SHORT ).show ();
                        //Toast.makeText ( Actividad_CargarBancos.this, post.getText (), Toast.LENGTH_SHORT ).show ();
                        //Toast.makeText ( Actividad_CargarBancos.this, "HOLA: "+ Instituto, Toast.LENGTH_SHORT ).show ();

                        conn=new ConexionSQLiteHelper(getApplicationContext(),"softcreditoapp2.db",null,1);
                        SQLiteDatabase db=conn.getWritableDatabase();



                        ContentValues valores = new ContentValues();

                        valores.put ( Utilidades.CAMPO_PAIS, pais );
                        valores.put ( Utilidades.CAMPO_ISO, iso );

                       // Toast.makeText ( AgregarProducto.this,  pais, Toast.LENGTH_SHORT ).show ();
                        Long idResultante=db.insert( Utilidades.TABLA_PAIS,Utilidades.CAMPO_ID_PAIS,valores);

                 //       Toast.makeText(getApplicationContext(),"Id Registro: "+idResultante,Toast.LENGTH_SHORT).show();
                        db.close();


                    }

                }


            }

            @Override
            public void onFailure(Call<List<Paisessss>> call, Throwable t) {
                textViewResultPais.setText(t.getMessage());
            }
        });
    }

    public void CargarEstado(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://softcredito.com/demos/dmo_unico2020Conta/app/WebService/index.php/Welcome/")
                .addConverterFactory( GsonConverterFactory.create())
                .build();

        JsonPlaceHolderApi jsonPlaceHolderApi = retrofit.create( JsonPlaceHolderApi.class);

        Call<List<Estadosssss>> call = jsonPlaceHolderApi.Estadosssss  ();

        call.enqueue(new Callback<List<Estadosssss>> () {
            @Override
            public void onResponse(Call<List<Estadosssss>> call, Response<List<Estadosssss>> response) {

                if (response.isSuccessful()) {
                    //textViewResult.setText("Code: " + response.code());
                    Log.e ( "Prueba", textViewResultEstado.getText ().toString () );
                    //return;

                    List<Estadosssss> Estadosssss = response.body();

                    for (int i = 0; i < Estadosssss.size(); i++) {
                        Estadosssss banco = Estadosssss.get(i);
                        String content = "";
                        content += "ID: " +banco.getId () + "\n";
                        content += "ESTADO: " + banco.getEstado () + "\n";
                        content += "CLAVE: " + banco.getClave () + "\n";

                        Ids = banco.getId ();
                        estado = banco.getEstado ();
                        clave = banco.getClave ();



                        textViewResultEstado.append(content);
                        contexto = textViewResultEstado.getText ().toString ();
                        //Toast.makeText ( Actividad_CargarBancos.this, post.getInstitucion (), Toast.LENGTH_SHORT ).show ();
                        //Toast.makeText ( Actividad_CargarBancos.this, post.getSucursal (), Toast.LENGTH_SHORT ).show ();
                        //Toast.makeText ( Actividad_CargarBancos.this, post.getClabe (), Toast.LENGTH_SHORT ).show ();
                        //Toast.makeText ( Actividad_CargarBancos.this, post.getText (), Toast.LENGTH_SHORT ).show ();
                        //Toast.makeText ( Actividad_CargarBancos.this, "HOLA: "+ Instituto, Toast.LENGTH_SHORT ).show ();

                        conn=new ConexionSQLiteHelper(getApplicationContext(),"softcreditoapp2.db",null,1);
                        SQLiteDatabase db=conn.getWritableDatabase();



                        ContentValues valores = new ContentValues();

                        valores.put ( Utilidades.CAMPO_ESTADO, estado );
                        valores.put ( Utilidades.CAMPO_CLAVE_ESTADO, clave );

                      //  Toast.makeText ( AgregarProducto.this,  estado, Toast.LENGTH_SHORT ).show ();
                        Long idResultante=db.insert( Utilidades.TABLA_ESTADOS,Utilidades.CAMPO_ID_ESTADOS,valores);

                     //   Toast.makeText(getApplicationContext(),"Id Registro: "+idResultante,Toast.LENGTH_SHORT).show();
                        db.close();


                    }

                }


            }

            @Override
            public void onFailure(Call<List<Estadosssss>> call, Throwable t) {
                textViewResultEstado.setText(t.getMessage());
            }
        });
    }
    public void Municipios(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://softcredito.com/demos/dmo_unico2020Conta/app/WebService/index.php/Welcome/")
                .addConverterFactory( GsonConverterFactory.create())
                .build();

        JsonPlaceHolderApi jsonPlaceHolderApi = retrofit.create( JsonPlaceHolderApi.class);

        Call<List<Municipiossss>> call = jsonPlaceHolderApi.Municipiossss  ();

        call.enqueue(new Callback<List<Municipiossss>> () {
            @Override
            public void onResponse(Call<List<Municipiossss>> call, Response<List<Municipiossss>> response) {

                if (response.isSuccessful()) {
                    //textViewResult.setText("Code: " + response.code());
                    Log.e ( "Prueba", textViewResultMunicipio.getText ().toString () );
                    //return;

                    List<Municipiossss> Municipiossss = response.body();

                    for (int i = 0; i < Municipiossss.size(); i++) {
                        Municipiossss banco = Municipiossss.get(i);
                        String content = "";
                        content += "ID: " +banco.getId () + "\n";
                        content += "ESTADO: " + banco.getMunicipio () + "\n";
                        content += "CLAVE: " + banco.getId_estado () + "\n";

                        Idss = banco.getId ();
                        municipio = banco.getMunicipio ();
                        id_estado = banco.getId_estado ();



                        textViewResultMunicipio.append(content);
                        contexto = textViewResultMunicipio.getText ().toString ();
                        //Toast.makeText ( Actividad_CargarBancos.this, post.getInstitucion (), Toast.LENGTH_SHORT ).show ();
                        //Toast.makeText ( Actividad_CargarBancos.this, post.getSucursal (), Toast.LENGTH_SHORT ).show ();
                        //Toast.makeText ( Actividad_CargarBancos.this, post.getClabe (), Toast.LENGTH_SHORT ).show ();
                        //Toast.makeText ( Actividad_CargarBancos.this, post.getText (), Toast.LENGTH_SHORT ).show ();
                        //Toast.makeText ( Actividad_CargarBancos.this, "HOLA: "+ Instituto, Toast.LENGTH_SHORT ).show ();

                        conn=new ConexionSQLiteHelper(getApplicationContext(),"softcreditoapp2.db",null,1);
                        SQLiteDatabase db=conn.getWritableDatabase();



                        ContentValues valores = new ContentValues();

                        valores.put ( Utilidades.CAMPO_MUNICIPIO, municipio );
                        valores.put ( Utilidades.CAMPO_CLAVE_MUNICIPIO, id_estado );

                        Toast.makeText ( AgregarProducto.this,  municipio, Toast.LENGTH_SHORT ).show ();
                        Long idResultante=db.insert( Utilidades.TABLA_MUNICIPIOS,Utilidades.CAMPO_ID_MUNICIPIOS,valores);

                        Toast.makeText(getApplicationContext(),"Id Registro: "+idResultante,Toast.LENGTH_SHORT).show();
                        db.close();


                    }

                }


            }

            @Override
            public void onFailure(Call<List<Municipiossss>> call, Throwable t) {
                textViewResultMunicipio.setText(t.getMessage());
            }
        });
    }
}
