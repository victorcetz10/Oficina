package com.softcredito.app.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.softcredito.app.ConexionSQLiteHelper;
import com.softcredito.app.Json.BancoCarg;
import com.softcredito.app.Json.JsonPlaceHolderApi;
import com.softcredito.app.Json.Post;
import com.softcredito.app.R;
import com.softcredito.app.Spinner;
import com.softcredito.app.Utilidadess.Utilidades;
import com.softcredito.app.provider.Contract;
import com.softcredito.app.provider.HelperDB;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class Actividad_CargarBancos extends AppCompatActivity {
    private TextView textViewResult;
    private Context contexts;
    private Button Cargar_Bancos;

    String contexto;
    String Instituto;
    String Sucursal;
    Integer Id;
    String Cuenta;
    ConexionSQLiteHelper conn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_actividad__cargar_bancos );

        textViewResult = findViewById(R.id.text_view_result);
        Cargar_Bancos = (Button) findViewById ( R.id.buttoBancos );
        Cargar_Bancos.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent ( Actividad_CargarBancos.this, Spinner.class );
                startActivity ( intent );
            }
        } );


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://softcredito.com/demos/dmo_unico2020Conta/app/WebService/index.php/Welcome/")
                .addConverterFactory( GsonConverterFactory.create())
                .build();

        JsonPlaceHolderApi jsonPlaceHolderApi = retrofit.create( JsonPlaceHolderApi.class);

        Call<List<BancoCarg>> call = jsonPlaceHolderApi.bancos  ();

        call.enqueue(new Callback<List<BancoCarg>> () {
            @Override
            public void onResponse(Call<List<BancoCarg>> call, Response<List<BancoCarg>> response) {

                if (response.isSuccessful()) {
                    //textViewResult.setText("Code: " + response.code());
                    Log.e ( "Prueba", textViewResult.getText ().toString () );
                    //return;

                    List<BancoCarg> bancos = response.body();

                    for (int i = 0; i < bancos.size(); i++) {
                        BancoCarg banco = bancos.get(i);
                        String content = "";
                        content += "ID: " +banco.getId () + "\n";
                        content += "INSTITUCION: " + banco.getInstitucion () + "\n";
                        content += "SUCURSAL: " + banco.getSucursal () + "\n";

                        Id = banco.getId ();
                        Instituto = banco.getInstitucion ();
                        Sucursal = banco.getSucursal ();



                        textViewResult.append(content);
                        contexto = textViewResult.getText ().toString ();
                        //Toast.makeText ( Actividad_CargarBancos.this, post.getInstitucion (), Toast.LENGTH_SHORT ).show ();
                        //Toast.makeText ( Actividad_CargarBancos.this, post.getSucursal (), Toast.LENGTH_SHORT ).show ();
                        //Toast.makeText ( Actividad_CargarBancos.this, post.getClabe (), Toast.LENGTH_SHORT ).show ();
                        //Toast.makeText ( Actividad_CargarBancos.this, post.getText (), Toast.LENGTH_SHORT ).show ();
                        //Toast.makeText ( Actividad_CargarBancos.this, "HOLA: "+ Instituto, Toast.LENGTH_SHORT ).show ();

                        conn=new ConexionSQLiteHelper(getApplicationContext(),"softcreditoapp2.db",null,1);
                        SQLiteDatabase db=conn.getWritableDatabase();



                        ContentValues valores = new ContentValues();

                        valores.put ( Utilidades.CAMPO_INSTITUCION, Instituto );
                        valores.put ( Utilidades.CAMPO_SUCURSAL, Sucursal );

                        Toast.makeText ( Actividad_CargarBancos.this,  Instituto, Toast.LENGTH_SHORT ).show ();
                        Long idResultante=db.insert( Utilidades.TABLA_BANCO,Utilidades.CAMPO_ID_BANCO,valores);

                        Toast.makeText(getApplicationContext(),"Id Registro: "+idResultante,Toast.LENGTH_SHORT).show();
                        db.close();


                    }

                }


            }

            @Override
            public void onFailure(Call<List<BancoCarg>> call, Throwable t) {
                textViewResult.setText(t.getMessage());
            }
        });
    }





}