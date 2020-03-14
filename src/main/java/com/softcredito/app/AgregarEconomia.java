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

import com.softcredito.app.Json.ActividadEconomica;
import com.softcredito.app.Json.Categoria_Economia;
import com.softcredito.app.Json.JsonPlaceHolderApi;
import com.softcredito.app.Json.Paisessss;
import com.softcredito.app.Json.Relacion;
import com.softcredito.app.Utilidadess.Utilidades;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AgregarEconomia extends AppCompatActivity {
    private TextView textViewResultActiEconomi;
    private TextView textViewResultEconomi2;
    private TextView textViewResultRelacion;
    private Context contexts;
    private Button Cargar_Personas;

    String contexto;
    Integer Id;
    String descripcion;
    String NombActEco;

    Integer Ids;
    String nombre;
    String descripcio;

    Integer Idss;
    String nombreCate;
    String descripcioCate;

    String Cuenta;
    ConexionSQLiteHelper conn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_agregar_economia );

        textViewResultRelacion = findViewById(R.id.text_view_result_Relacion);
        textViewResultEconomi2= findViewById(R.id.text_view_result_Economia5);
        textViewResultActiEconomi = findViewById(R.id.text_view_result_ActEconomica);
        CargarRelacion();
        CargarCategoriaAct ();
        CargarActividadEconomica();
    }


    public void CargarRelacion(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://softcredito.com/demos/dmo_unico2020Conta/app/WebService/index.php/Welcome/")
                .addConverterFactory( GsonConverterFactory.create())
                .build();

        JsonPlaceHolderApi jsonPlaceHolderApi = retrofit.create( JsonPlaceHolderApi.class);

        Call<List<Relacion>> call = jsonPlaceHolderApi.tipos_contactos  ();

        call.enqueue(new Callback<List<Relacion>> () {
            @Override
            public void onResponse(Call<List<Relacion>> call, Response<List<Relacion>> response) {

                if (response.isSuccessful()) {
                    //textViewResult.setText("Code: " + response.code());
                    Log.e ( "Prueba", textViewResultEconomi2.getText ().toString () );
                    //return;

                    List<Relacion> Relacionss = response.body();

                    for (int i = 0; i < Relacionss.size(); i++) {
                        Relacion banco = Relacionss.get(i);
                        String content = "";
                        content += "ID: " +banco.getId () + "\n";
                        content += "PAIS: " + banco.getNombre () + "\n";
                        content += "ISO: " + banco.getDescripcion () + "\n";

                        Ids = banco.getId ();
                        nombre = banco.getNombre ();
                        descripcio = banco.getDescripcion ();



                        textViewResultEconomi2.append(content);
                        contexto = textViewResultEconomi2.getText ().toString ();
                        //Toast.makeText ( Actividad_CargarBancos.this, post.getInstitucion (), Toast.LENGTH_SHORT ).show ();
                        //Toast.makeText ( Actividad_CargarBancos.this, post.getSucursal (), Toast.LENGTH_SHORT ).show ();
                        //Toast.makeText ( Actividad_CargarBancos.this, post.getClabe (), Toast.LENGTH_SHORT ).show ();
                        //Toast.makeText ( Actividad_CargarBancos.this, post.getText (), Toast.LENGTH_SHORT ).show ();
                        //Toast.makeText ( Actividad_CargarBancos.this, "HOLA: "+ Instituto, Toast.LENGTH_SHORT ).show ();

                        conn=new ConexionSQLiteHelper(getApplicationContext(),"softcreditoapp2.db",null,1);
                        SQLiteDatabase db=conn.getWritableDatabase();



                        ContentValues valores = new ContentValues();

                        valores.put ( Utilidades.CAMPO_NOMBRECONTACTOS, nombre );
                        valores.put ( Utilidades.CAMPO_DESCRIPCION, descripcio );


                        // Toast.makeText ( AgregarEconomia.this,  nombre, Toast.LENGTH_SHORT ).show ();
                        Long idResultante=db.insert( Utilidades.TABLA_TIPOS_CONTACTOS,Utilidades.CAMPO_ID_CONTACTOS,valores);

                        Toast.makeText(getApplicationContext(),"Id Registro: "+idResultante,Toast.LENGTH_SHORT).show();
                        db.close();


                    }

                }


            }

            @Override
            public void onFailure(Call<List<Relacion>> call, Throwable t) {
                textViewResultEconomi2.setText(t.getMessage());
            }
        });
    }

    public void CargarCategoriaAct(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://softcredito.com/demos/dmo_unico2020Conta/app/WebService/index.php/Welcome/")
                .addConverterFactory( GsonConverterFactory.create())
                .build();

        JsonPlaceHolderApi jsonPlaceHolderApi = retrofit.create( JsonPlaceHolderApi.class);

        Call<List<Categoria_Economia>> call = jsonPlaceHolderApi.categorias_actividades_economicas  ();

        call.enqueue(new Callback<List<Categoria_Economia>> () {
            @Override
            public void onResponse(Call<List<Categoria_Economia>> call, Response<List<Categoria_Economia>> response) {

                if (response.isSuccessful()) {
                    //textViewResult.setText("Code: " + response.code());
                    Log.e ( "Prueba", textViewResultActiEconomi.getText ().toString () );
                    //return;

                    List<Categoria_Economia> CategoEco = response.body();

                    for (int i = 0; i < CategoEco.size(); i++) {
                        Categoria_Economia banco = CategoEco.get(i);
                        String content = "";
                        content += "ID: " +banco.getId () + "\n";
                        content += "PAIS: " + banco.getNombre () + "\n";
                        content += "ISO: " + banco.getDescripcion () + "\n";

                        Id = banco.getId ();
                        NombActEco = banco.getNombre ();
                        descripcion = banco.getDescripcion ();



                        textViewResultActiEconomi.append(content);
                        contexto = textViewResultActiEconomi.getText ().toString ();
                        //Toast.makeText ( Actividad_CargarBancos.this, post.getInstitucion (), Toast.LENGTH_SHORT ).show ();
                        //Toast.makeText ( Actividad_CargarBancos.this, post.getSucursal (), Toast.LENGTH_SHORT ).show ();
                        //Toast.makeText ( Actividad_CargarBancos.this, post.getClabe (), Toast.LENGTH_SHORT ).show ();
                        //Toast.makeText ( Actividad_CargarBancos.this, post.getText (), Toast.LENGTH_SHORT ).show ();
                        //Toast.makeText ( Actividad_CargarBancos.this, "HOLA: "+ Instituto, Toast.LENGTH_SHORT ).show ();

                        conn=new ConexionSQLiteHelper(getApplicationContext(),"softcreditoapp2.db",null,1);
                        SQLiteDatabase db=conn.getWritableDatabase();



                        ContentValues valores = new ContentValues();

                        valores.put ( Utilidades.CAMPO_NOMBRECATEGORIA, NombActEco );
                        valores.put ( Utilidades.CAMPO_DESCRIPCIONCATEGORIA, descripcion );

                      //  Toast.makeText ( AgregarEconomia.this,  NombActEco, Toast.LENGTH_SHORT ).show ();
                        Long idResultante=db.insert( Utilidades.TABLA_CATEGORIA_ECONOMICAS,Utilidades.CAMPO_ID_CATEGORIA,valores);

                      //  Toast.makeText(getApplicationContext(),"Id Registro: "+idResultante,Toast.LENGTH_SHORT).show();
                        db.close();


                    }

                }


            }

            @Override
            public void onFailure(Call<List<Categoria_Economia>> call, Throwable t) {
                textViewResultActiEconomi.setText(t.getMessage());
            }
        });
    }
    public void CargarActividadEconomica(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://softcredito.com/demos/dmo_unico2020Conta/app/WebService/index.php/Welcome/")
                .addConverterFactory( GsonConverterFactory.create())
                .build();

        JsonPlaceHolderApi jsonPlaceHolderApi = retrofit.create( JsonPlaceHolderApi.class);

        Call<List<ActividadEconomica>> call = jsonPlaceHolderApi.ActividadEconomica  ();

        call.enqueue(new Callback<List<ActividadEconomica>> () {
            @Override
            public void onResponse(Call<List<ActividadEconomica>> call, Response<List<ActividadEconomica>> response) {

                if (response.isSuccessful()) {
                    //textViewResult.setText("Code: " + response.code());
                    Log.e ( "Prueba", textViewResultRelacion.getText ().toString () );
                    //return;

                    List<ActividadEconomica> ActEco = response.body();

                    for (int i = 0; i < ActEco.size(); i++) {
                        ActividadEconomica banco = ActEco.get(i);
                        String content = "";
                        content += "ID: " +banco.getId () + "\n";
                        content += "PAIS: " + banco.getClave () + "\n";
                        content += "ISO: " + banco.getDescripcion () + "\n";

                        Idss = banco.getId ();
                        nombreCate = banco.getClave ();
                        descripcioCate = banco.getDescripcion ();



                        textViewResultRelacion.append(content);
                        contexto = textViewResultRelacion.getText ().toString ();
                        //Toast.makeText ( Actividad_CargarBancos.this, post.getInstitucion (), Toast.LENGTH_SHORT ).show ();
                        //Toast.makeText ( Actividad_CargarBancos.this, post.getSucursal (), Toast.LENGTH_SHORT ).show ();
                        //Toast.makeText ( Actividad_CargarBancos.this, post.getClabe (), Toast.LENGTH_SHORT ).show ();
                        //Toast.makeText ( Actividad_CargarBancos.this, post.getText (), Toast.LENGTH_SHORT ).show ();
                        //Toast.makeText ( Actividad_CargarBancos.this, "HOLA: "+ Instituto, Toast.LENGTH_SHORT ).show ();

                        conn=new ConexionSQLiteHelper(getApplicationContext(),"softcreditoapp2.db",null,1);
                        SQLiteDatabase db=conn.getWritableDatabase();



                        ContentValues valores = new ContentValues();

                        valores.put ( Utilidades.CAMPO_CLAVEACTIVIDAD, nombreCate );
                        valores.put ( Utilidades.CAMPO_DESCRIPCIONACTIVIDAD, descripcioCate );

                        //Toast.makeText ( AgregarEconomia.this,  NombActEco, Toast.LENGTH_SHORT ).show ();
                        //Toast.makeText ( AgregarEconomia.this,  "hola", Toast.LENGTH_SHORT ).show ();
                        Long idResultante=db.insert( Utilidades.TABLA_ACTIVIDAD_ECONOMICAS,Utilidades.CAMPO_ID_ACTIVIDAD,valores);

                        //Toast.makeText(getApplicationContext(),"Id Registro: "+idResultante,Toast.LENGTH_SHORT).show();
                        db.close();


                    }

                }


            }

            @Override
            public void onFailure(Call<List<ActividadEconomica>> call, Throwable t) {
                textViewResultRelacion.setText(t.getMessage());
            }
        });
    }
}
