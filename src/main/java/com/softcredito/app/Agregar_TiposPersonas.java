package com.softcredito.app;

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

import com.softcredito.app.Json.BancoCarg;
import com.softcredito.app.Json.JsonPlaceHolderApi;
import com.softcredito.app.Json.Tipos_Personas;
import com.softcredito.app.Utilidadess.Utilidades;
import com.softcredito.app.ui.Actividad_CargarBancos;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Agregar_TiposPersonas extends AppCompatActivity {
    private TextView textViewResult2;
    private Context contexts;
    private Button Cargar_Personas;

    String contexto;
    String Clave;
    String Nombre;
    Integer Id;
    String Cuenta;
    ConexionSQLiteHelper conn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_agregar__tipos_personas );

        textViewResult2 = findViewById(R.id.text_view_result_Personas);
        Cargar_Personas = (Button) findViewById ( R.id.buttoPersonas );
        Cargar_Personas.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent ( Agregar_TiposPersonas.this, Spinner.class );
                startActivity ( intent );
            }
        } );



    }





}
