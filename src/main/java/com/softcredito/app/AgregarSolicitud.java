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

import com.softcredito.app.Json.JsonPlaceHolderApi;
import com.softcredito.app.Json.Produc;
import com.softcredito.app.Json.Relacion;
import com.softcredito.app.Json.TipoAmort;
import com.softcredito.app.Json.TipoPag;
import com.softcredito.app.Utilidadess.Utilidades;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AgregarSolicitud extends AppCompatActivity {
    private TextView textViewResultProducto;
    private TextView textViewResultTipoAmortizacion;
    private TextView textViewResultTipoPago;
    private Context contexts;
    private Button Cargar_Personas;

    String contexto;

    ConexionSQLiteHelper conn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_agregar_solicitud );

        textViewResultProducto = findViewById(R.id.text_view_result_Producto);
        textViewResultTipoAmortizacion = findViewById(R.id.text_view_result_TipoAmortizacion);
        textViewResultTipoPago = findViewById(R.id.text_view_result_TipoPago);

    }


}
