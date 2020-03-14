package com.softcredito.app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.softcredito.app.Json.ActividadEconomica;
import com.softcredito.app.Json.BancoCarg;
import com.softcredito.app.Json.Categoria_Economia;
import com.softcredito.app.Json.Documents;
import com.softcredito.app.Json.Estadosssss;
import com.softcredito.app.Json.JsonPlaceHolderApi;
import com.softcredito.app.Json.Municipiossss;
import com.softcredito.app.Json.ObtenerClientes;
import com.softcredito.app.Json.Paisessss;
import com.softcredito.app.Json.Produc;
import com.softcredito.app.Json.Relacion;
import com.softcredito.app.Json.TipoAmort;
import com.softcredito.app.Json.TipoPag;
import com.softcredito.app.Json.Tipos_Personas;
import com.softcredito.app.Utilidadess.Utilidades;
import com.softcredito.app.ui.Actividad_CargarBancos;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AgregarDocumentos extends AppCompatActivity {

    private TextView textViewResultDocumentos;

    Integer IdssDoc ;
    String municipioDoc ;
    String id_estadoDoc ;
    String id_estadoSDoc ;
    String contexto;
    String Cuenta;
    ConexionSQLiteHelper conn;

    Integer Idproducto;
    String claveproducto;
    String nombreproducto;

    Integer IdtipoAmortizacion;
    String nombretipoamortizacion;
    String descripciontipoamortizacion;

    Integer Idtipopago;
    String nombretipopago;
    String descripciontipopago;

    String ClaveTipoPersona;
    String NombreTipoPersona;
    Integer IdTipoPersona;

    Integer IdNomActEco;
    String descripcionNomActEco;
    String NombActEco;

    Integer IdsRelacion;
    String nombreRelacion;
    String descripcioRelacion;

    Integer IdDescat;
    String nombreCate;
    String descripcioCate;

    String Instituto;
    String Sucursal;
    Integer IdCargarBanco;

    Integer Id;
    String pais;
    String iso;

    Integer Ids;
    String estado;
    String clave;

    Integer Idss;
    String municipio;
    String id_estado;

    Integer IdsClientes;
    String rfcClientes;
    String curpClientes;
    String ineClientes;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_agregar_documentos );

        textViewResultDocumentos = findViewById(R.id.text_view_result_Documentos);
        Documentos();
        CargarProducto();
        CargarTipoAmortizacion ();
        CargarTipoPago();
        CargarTipoPersona();
        CargarRelacion();
        CargarCategoriaAct ();
        CargarActividadEconomica();
        CargarBanco();
        CargarPais ();
        CargarEstado ();
        Municipios();
        CargarClientes();
    }

    public void Documentos(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://softcredito.com/demos/dmo_unico2020Conta/app/WebService/index.php/Welcome/")
                .addConverterFactory( GsonConverterFactory.create())
                .build();

        JsonPlaceHolderApi jsonPlaceHolderApi = retrofit.create( JsonPlaceHolderApi.class);

        Call<List<Documents>> call = jsonPlaceHolderApi.Documents  ();

        call.enqueue(new Callback<List<Documents>> () {
            @Override
            public void onResponse(Call<List<Documents>> call, Response<List<Documents>> response) {

                if (response.isSuccessful()) {
                    //textViewResult.setText("Code: " + response.code());
                    Log.e ( "Prueba", textViewResultDocumentos.getText ().toString () );
                    //return;

                    List<Documents> Documents = response.body();

                    for (int i = 0; i < Documents.size(); i++) {
                        Documents banco = Documents.get(i);
                        String content = "";
                        content += "ID: " +banco.getId () + "\n";
                        content += "ESTADO: " + banco.getNombre () + "\n";
                        content += "CLAVE: " + banco.getTipo () + "\n";
                        content += "RUTA: " + banco.getRuta () + "\n";

                        IdssDoc = banco.getId ();
                        municipioDoc  = banco.getNombre ();
                        id_estadoDoc  = banco.getTipo ();
                        id_estadoSDoc  = banco.getRuta ();



                        //textViewResultDocumentos.append(content);
                        //contexto = textViewResultDocumentos.getText ().toString ();
                        //Toast.makeText ( Actividad_CargarBancos.this, post.getInstitucion (), Toast.LENGTH_SHORT ).show ();
                        //Toast.makeText ( Actividad_CargarBancos.this, post.getSucursal (), Toast.LENGTH_SHORT ).show ();
                        //Toast.makeText ( Actividad_CargarBancos.this, post.getClabe (), Toast.LENGTH_SHORT ).show ();
                        //Toast.makeText ( Actividad_CargarBancos.this, post.getText (), Toast.LENGTH_SHORT ).show ();
                        //Toast.makeText ( Actividad_CargarBancos.this, "HOLA: "+ Instituto, Toast.LENGTH_SHORT ).show ();

                        conn=new ConexionSQLiteHelper(getApplicationContext(),"softcreditoapp2.db",null,1);
                        SQLiteDatabase db=conn.getWritableDatabase();



                        ContentValues valores = new ContentValues();

                        valores.put ( Utilidades.CAMPO_NOMBRE_DOCUMENTO, municipioDoc  );
                        valores.put ( Utilidades.CAMPO_DESCRIPCION_TIPO_DOCUMENTO, id_estadoDoc  );
                        valores.put ( Utilidades.CAMPO_DESCRIPCION_RUTA_DOCUMENTO, id_estadoSDoc  );

                        //Toast.makeText ( AgregarDocumentos.this,  municipio, Toast.LENGTH_SHORT ).show ();
                        Long idResultante=db.insert( Utilidades.TABLA_TIPO_DOCUMENTO,Utilidades.CAMPO_ID_DOCUMENTO,valores);

                        //Toast.makeText(getApplicationContext(),"Id Registro: "+idResultante,Toast.LENGTH_SHORT).show();

                        db.close();


                    }

                }
                Toast.makeText ( AgregarDocumentos.this, "Documentos sincronizados", Toast.LENGTH_SHORT ).show ();


            }

            @Override
            public void onFailure(Call<List<Documents>> call, Throwable t) {
                //textViewResultDocumentos.setText(t.getMessage());
            }
        });
    }
    public void CargarProducto(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://softcredito.com/demos/dmo_unico2020Conta/app/WebService/index.php/Welcome/")
                .addConverterFactory( GsonConverterFactory.create())
                .build();

        JsonPlaceHolderApi jsonPlaceHolderApi = retrofit.create( JsonPlaceHolderApi.class);

        Call<List<Produc>> call = jsonPlaceHolderApi.Produc  ();

        call.enqueue(new Callback<List<Produc>> () {
            @Override
            public void onResponse(Call<List<Produc>> call, Response<List<Produc>> response) {

                if (response.isSuccessful()) {
                    //textViewResult.setText("Code: " + response.code());
                    //Log.e ( "Prueba", textViewResultProducto.getText ().toString () );
                    //return;

                    List<Produc> Relacionss = response.body();

                    for (int i = 0; i < Relacionss.size(); i++) {
                        Produc banco = Relacionss.get(i);
                        String content = "";
                        content += "ID: " +banco.getId () + "\n";
                        content += "PAIS: " + banco.getClave () + "\n";
                        content += "ISO: " + banco.getNombre () + "\n";

                        Idproducto = banco.getId ();
                        claveproducto = banco.getClave ();
                        nombreproducto = banco.getNombre ();



                        //textViewResultProducto.append(content);
                        //contexto = textViewResultProducto.getText ().toString ();
                        //Toast.makeText ( Actividad_CargarBancos.this, post.getInstitucion (), Toast.LENGTH_SHORT ).show ();
                        //Toast.makeText ( Actividad_CargarBancos.this, post.getSucursal (), Toast.LENGTH_SHORT ).show ();
                        //Toast.makeText ( Actividad_CargarBancos.this, post.getClabe (), Toast.LENGTH_SHORT ).show ();
                        //Toast.makeText ( Actividad_CargarBancos.this, post.getText (), Toast.LENGTH_SHORT ).show ();
                        //Toast.makeText ( Actividad_CargarBancos.this, "HOLA: "+ Instituto, Toast.LENGTH_SHORT ).show ();

                        conn=new ConexionSQLiteHelper(getApplicationContext(),"softcreditoapp2.db",null,1);
                        SQLiteDatabase db=conn.getWritableDatabase();



                        ContentValues valores = new ContentValues();

                        valores.put ( Utilidades.CAMPO_CLAVE_PRODUCTO, claveproducto );
                        valores.put ( Utilidades.CAMPO_NOMBRE_PRODUCTO, nombreproducto );


                        // Toast.makeText ( AgregarEconomia.this,  nombre, Toast.LENGTH_SHORT ).show ();
                        Long idResultante=db.insert( Utilidades.TABLA_PRODUCTOS,Utilidades.CAMPO_ID_PRODUCTOS,valores);


                        //Toast.makeText(getApplicationContext(),"Id Registro: "+idResultante,Toast.LENGTH_SHORT).show();
                        db.close();


                    }

                }
                Toast.makeText ( AgregarDocumentos.this, "Productos sincronizados", Toast.LENGTH_SHORT ).show ();


            }

            @Override
            public void onFailure(Call<List<Produc>> call, Throwable t) {
                //textViewResultProducto.setText(t.getMessage());
            }
        });
    }

    public void CargarTipoAmortizacion(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://softcredito.com/demos/dmo_unico2020Conta/app/WebService/index.php/Welcome/")
                .addConverterFactory( GsonConverterFactory.create())
                .build();

        JsonPlaceHolderApi jsonPlaceHolderApi = retrofit.create( JsonPlaceHolderApi.class);

        Call<List<TipoAmort>> call = jsonPlaceHolderApi.TipoAmort  ();

        call.enqueue(new Callback<List<TipoAmort>> () {
            @Override
            public void onResponse(Call<List<TipoAmort>> call, Response<List<TipoAmort>> response) {

                if (response.isSuccessful()) {
                    //textViewResult.setText("Code: " + response.code());
                    //Log.e ( "Prueba", textViewResultTipoAmortizacion.getText ().toString () );
                    //return;

                    List<TipoAmort> Relacionss = response.body();

                    for (int i = 0; i < Relacionss.size(); i++) {
                        TipoAmort banco = Relacionss.get(i);
                        String content = "";
                        content += "ID: " +banco.getId () + "\n";
                        content += "PAIS: " + banco.getNombre () + "\n";
                        content += "ISO: " + banco.getDescripcion () + "\n";

                        IdtipoAmortizacion = banco.getId ();
                        nombretipoamortizacion = banco.getNombre ();
                        descripciontipoamortizacion = banco.getDescripcion ();



                        //textViewResultTipoAmortizacion.append(content);
                        //contexto = textViewResultTipoAmortizacion.getText ().toString ();
                        //Toast.makeText ( Actividad_CargarBancos.this, post.getInstitucion (), Toast.LENGTH_SHORT ).show ();
                        //Toast.makeText ( Actividad_CargarBancos.this, post.getSucursal (), Toast.LENGTH_SHORT ).show ();
                        //Toast.makeText ( Actividad_CargarBancos.this, post.getClabe (), Toast.LENGTH_SHORT ).show ();
                        //Toast.makeText ( Actividad_CargarBancos.this, post.getText (), Toast.LENGTH_SHORT ).show ();
                        //Toast.makeText ( Actividad_CargarBancos.this, "HOLA: "+ Instituto, Toast.LENGTH_SHORT ).show ();

                        conn=new ConexionSQLiteHelper(getApplicationContext(),"softcreditoapp2.db",null,1);
                        SQLiteDatabase db=conn.getWritableDatabase();



                        ContentValues valores = new ContentValues();

                        valores.put ( Utilidades.CAMPO_NOMBRE_TIPO_AMORTIZACION, nombretipoamortizacion );
                        valores.put ( Utilidades.CAMPO_DESCRIPCION_TIPO_AMORTIZACION, descripciontipoamortizacion );


                        // Toast.makeText ( AgregarEconomia.this,  nombre, Toast.LENGTH_SHORT ).show ();
                        Long idResultante=db.insert( Utilidades.TABLA_TIPO_AMORTIZACION,Utilidades.CAMPO_ID_TIPO_AMORTIZACION,valores);

                        //Toast.makeText(getApplicationContext(),"Id Registro: "+idResultante,Toast.LENGTH_SHORT).show();

                        db.close();


                    }

                }
                Toast.makeText ( AgregarDocumentos.this, "Tipo Amortizacion sincronizados", Toast.LENGTH_SHORT ).show ();


            }

            @Override
            public void onFailure(Call<List<TipoAmort>> call, Throwable t) {
                //textViewResultTipoAmortizacion.setText(t.getMessage());
            }
        });
    }

    public void CargarTipoPago(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://softcredito.com/demos/dmo_unico2020Conta/app/WebService/index.php/Welcome/")
                .addConverterFactory( GsonConverterFactory.create())
                .build();

        JsonPlaceHolderApi jsonPlaceHolderApi = retrofit.create( JsonPlaceHolderApi.class);

        Call<List<TipoPag>> call = jsonPlaceHolderApi.TipoPag  ();

        call.enqueue(new Callback<List<TipoPag>> () {
            @Override
            public void onResponse(Call<List<TipoPag>> call, Response<List<TipoPag>> response) {

                if (response.isSuccessful()) {
                    //textViewResult.setText("Code: " + response.code());
                    //Log.e ( "Prueba", textViewResultTipoPago.getText ().toString () );
                    //return;

                    List<TipoPag> Relacionss = response.body();

                    for (int i = 0; i < Relacionss.size(); i++) {
                        TipoPag banco = Relacionss.get(i);
                        String content = "";
                        content += "ID: " +banco.getId () + "\n";
                        content += "PAIS: " + banco.getNombre () + "\n";
                        content += "ISO: " + banco.getDescripcion () + "\n";

                        Idtipopago = banco.getId ();
                        nombretipopago = banco.getNombre ();
                        descripciontipopago= banco.getDescripcion ();



                        //textViewResultTipoPago.append(content);
                        //contexto = textViewResultTipoPago.getText ().toString ();
                        //Toast.makeText ( Actividad_CargarBancos.this, post.getInstitucion (), Toast.LENGTH_SHORT ).show ();
                        //Toast.makeText ( Actividad_CargarBancos.this, post.getSucursal (), Toast.LENGTH_SHORT ).show ();
                        //Toast.makeText ( Actividad_CargarBancos.this, post.getClabe (), Toast.LENGTH_SHORT ).show ();
                        //Toast.makeText ( Actividad_CargarBancos.this, post.getText (), Toast.LENGTH_SHORT ).show ();
                        //Toast.makeText ( Actividad_CargarBancos.this, "HOLA: "+ Instituto, Toast.LENGTH_SHORT ).show ();

                        conn=new ConexionSQLiteHelper(getApplicationContext(),"softcreditoapp2.db",null,1);
                        SQLiteDatabase db=conn.getWritableDatabase();



                        ContentValues valores = new ContentValues();

                        valores.put ( Utilidades.CAMPO_NOMBRE_TIPO_PAGO, nombretipopago );
                        valores.put ( Utilidades.CAMPO_DESCRIPCION_TIPO_PAGO, descripciontipopago );


                        // Toast.makeText ( AgregarEconomia.this,  nombre, Toast.LENGTH_SHORT ).show ();
                        Long idResultante=db.insert( Utilidades.TABLA_TIPO_PAGO,Utilidades.CAMPO_ID_TIPO_PAGO,valores);

                        //Toast.makeText(getApplicationContext(),"Id Registro: "+idResultante,Toast.LENGTH_SHORT).show();

                        db.close();


                    }

                }
                Toast.makeText ( AgregarDocumentos.this, "Tipo Pago sincronizados", Toast.LENGTH_SHORT ).show ();


            }

            @Override
            public void onFailure(Call<List<TipoPag>> call, Throwable t) {
                //textViewResultTipoPago.setText(t.getMessage());
            }
        });
    }

    public void CargarTipoPersona(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://softcredito.com/demos/dmo_unico2020Conta/app/WebService/index.php/Welcome/")
                .addConverterFactory( GsonConverterFactory.create())
                .build();

        JsonPlaceHolderApi jsonPlaceHolderApi = retrofit.create( JsonPlaceHolderApi.class);

        Call<List<Tipos_Personas>> call = jsonPlaceHolderApi.TiposPersonas  ();

        call.enqueue(new Callback<List<Tipos_Personas>> () {
            @Override
            public void onResponse(Call<List<Tipos_Personas>> call, Response<List<Tipos_Personas>> response) {

                if (response.isSuccessful()) {
                    //textViewResult.setText("Code: " + response.code());
                    //Log.e ( "Prueba", textViewResult2.getText ().toString () );
                    //return;

                    List<Tipos_Personas> TiposPersonas = response.body();

                    for (int i = 0; i < TiposPersonas.size(); i++) {
                        Tipos_Personas banco = TiposPersonas.get(i);
                        String content = "";
                        content += "ID: " +banco.getId () + "\n";
                        content += "CLAVE: " + banco.getClave () + "\n";
                        content += "NOMBRE: " + banco.getNombre () + "\n";

                        IdTipoPersona = banco.getId ();
                        ClaveTipoPersona = banco.getClave ();
                        NombreTipoPersona = banco.getNombre ();



                        //textViewResult2.append(content);
                        //contexto = textViewResult2.getText ().toString ();
                        //Toast.makeText ( Actividad_CargarBancos.this, post.getInstitucion (), Toast.LENGTH_SHORT ).show ();
                        //Toast.makeText ( Actividad_CargarBancos.this, post.getSucursal (), Toast.LENGTH_SHORT ).show ();
                        //Toast.makeText ( Actividad_CargarBancos.this, post.getClabe (), Toast.LENGTH_SHORT ).show ();
                        //Toast.makeText ( Actividad_CargarBancos.this, post.getText (), Toast.LENGTH_SHORT ).show ();
                        //Toast.makeText ( Actividad_CargarBancos.this, "HOLA: "+ Instituto, Toast.LENGTH_SHORT ).show ();

                        conn=new ConexionSQLiteHelper(getApplicationContext(),"softcreditoapp2.db",null,1);
                        SQLiteDatabase db=conn.getWritableDatabase();



                        ContentValues valores = new ContentValues();

                        valores.put ( Utilidades.CAMPO_CLAVE, ClaveTipoPersona );
                        valores.put ( Utilidades.CAMPO_NOMBRE, NombreTipoPersona );

                        //Toast.makeText ( Agregar_TiposPersonas.this,  Nombre, Toast.LENGTH_SHORT ).show ();
                        Long idResultante=db.insert( Utilidades.TABLA_TIPOPERSONAS,Utilidades.CAMPO_ID_TIPOPERSONAS,valores);

                        //Toast.makeText(getApplicationContext(),"Id Registro: "+idResultante,Toast.LENGTH_SHORT).show();
                        db.close();


                    }

                }

                Toast.makeText ( AgregarDocumentos.this, "Tipo Personas sincronizados", Toast.LENGTH_SHORT ).show ();


            }


            @Override
            public void onFailure(Call<List<Tipos_Personas>> call, Throwable t) {
               // textViewResult2.setText(t.getMessage());
            }
        });
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
                    //Log.e ( "Prueba", textViewResultEconomi2.getText ().toString () );
                    //return;

                    List<Relacion> Relacionss = response.body();

                    for (int i = 0; i < Relacionss.size(); i++) {
                        Relacion banco = Relacionss.get(i);
                        String content = "";
                        content += "ID: " +banco.getId () + "\n";
                        content += "PAIS: " + banco.getNombre () + "\n";
                        content += "ISO: " + banco.getDescripcion () + "\n";

                        IdsRelacion = banco.getId ();
                        nombreRelacion = banco.getNombre ();
                        descripcioRelacion = banco.getDescripcion ();



                       // textViewResultEconomi2.append(content);
                        //contexto = textViewResultEconomi2.getText ().toString ();
                        //Toast.makeText ( Actividad_CargarBancos.this, post.getInstitucion (), Toast.LENGTH_SHORT ).show ();
                        //Toast.makeText ( Actividad_CargarBancos.this, post.getSucursal (), Toast.LENGTH_SHORT ).show ();
                        //Toast.makeText ( Actividad_CargarBancos.this, post.getClabe (), Toast.LENGTH_SHORT ).show ();
                        //Toast.makeText ( Actividad_CargarBancos.this, post.getText (), Toast.LENGTH_SHORT ).show ();
                        //Toast.makeText ( Actividad_CargarBancos.this, "HOLA: "+ Instituto, Toast.LENGTH_SHORT ).show ();

                        conn=new ConexionSQLiteHelper(getApplicationContext(),"softcreditoapp2.db",null,1);
                        SQLiteDatabase db=conn.getWritableDatabase();



                        ContentValues valores = new ContentValues();

                        valores.put ( Utilidades.CAMPO_NOMBRECONTACTOS, nombreRelacion );
                        valores.put ( Utilidades.CAMPO_DESCRIPCION, descripcioRelacion );


                        // Toast.makeText ( AgregarEconomia.this,  nombre, Toast.LENGTH_SHORT ).show ();
                        Long idResultante=db.insert( Utilidades.TABLA_TIPOS_CONTACTOS,Utilidades.CAMPO_ID_CONTACTOS,valores);

                        //Toast.makeText(getApplicationContext(),"Id Registro: "+idResultante,Toast.LENGTH_SHORT).show();
                        db.close();


                    }

                }
                Toast.makeText ( AgregarDocumentos.this, "Tipo Contactos sincronizados", Toast.LENGTH_SHORT ).show ();


            }

            @Override
            public void onFailure(Call<List<Relacion>> call, Throwable t) {
                //textViewResultEconomi2.setText(t.getMessage());
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
                   // Log.e ( "Prueba", textViewResultActiEconomi.getText ().toString () );
                    //return;

                    List<Categoria_Economia> CategoEco = response.body();

                    for (int i = 0; i < CategoEco.size(); i++) {
                        Categoria_Economia banco = CategoEco.get(i);
                        String content = "";
                        content += "ID: " +banco.getId () + "\n";
                        content += "PAIS: " + banco.getNombre () + "\n";
                        content += "ISO: " + banco.getDescripcion () + "\n";

                        IdNomActEco = banco.getId ();
                        NombActEco = banco.getNombre ();
                        descripcionNomActEco = banco.getDescripcion ();



                        //textViewResultActiEconomi.append(content);
                        //contexto = textViewResultActiEconomi.getText ().toString ();
                        //Toast.makeText ( Actividad_CargarBancos.this, post.getInstitucion (), Toast.LENGTH_SHORT ).show ();
                        //Toast.makeText ( Actividad_CargarBancos.this, post.getSucursal (), Toast.LENGTH_SHORT ).show ();
                        //Toast.makeText ( Actividad_CargarBancos.this, post.getClabe (), Toast.LENGTH_SHORT ).show ();
                        //Toast.makeText ( Actividad_CargarBancos.this, post.getText (), Toast.LENGTH_SHORT ).show ();
                        //Toast.makeText ( Actividad_CargarBancos.this, "HOLA: "+ Instituto, Toast.LENGTH_SHORT ).show ();

                        conn=new ConexionSQLiteHelper(getApplicationContext(),"softcreditoapp2.db",null,1);
                        SQLiteDatabase db=conn.getWritableDatabase();



                        ContentValues valores = new ContentValues();

                        valores.put ( Utilidades.CAMPO_NOMBRECATEGORIA, NombActEco );
                        valores.put ( Utilidades.CAMPO_DESCRIPCIONCATEGORIA, descripcionNomActEco );

                        //  Toast.makeText ( AgregarEconomia.this,  NombActEco, Toast.LENGTH_SHORT ).show ();
                        Long idResultante=db.insert( Utilidades.TABLA_CATEGORIA_ECONOMICAS,Utilidades.CAMPO_ID_CATEGORIA,valores);

                        //  Toast.makeText(getApplicationContext(),"Id Registro: "+idResultante,Toast.LENGTH_SHORT).show();
                        db.close();


                    }

                }
                Toast.makeText ( AgregarDocumentos.this, "Tipo Categoria Economicas sincronizados", Toast.LENGTH_SHORT ).show ();


            }

            @Override
            public void onFailure(Call<List<Categoria_Economia>> call, Throwable t) {
                //textViewResultActiEconomi.setText(t.getMessage());
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
                    //Log.e ( "Prueba", textViewResultRelacion.getText ().toString () );
                    //return;

                    List<ActividadEconomica> ActEco = response.body();

                    for (int i = 0; i < ActEco.size(); i++) {
                        ActividadEconomica banco = ActEco.get(i);
                        String content = "";
                        content += "ID: " +banco.getId () + "\n";
                        content += "PAIS: " + banco.getClave () + "\n";
                        content += "ISO: " + banco.getDescripcion () + "\n";

                        IdDescat = banco.getId ();
                        nombreCate = banco.getClave ();
                        descripcioCate = banco.getDescripcion ();



                        //textViewResultRelacion.append(content);
                        //contexto = textViewResultRelacion.getText ().toString ();
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
                Toast.makeText ( AgregarDocumentos.this, "Tipo Actividad Economica sincronizados", Toast.LENGTH_SHORT ).show ();


            }

            @Override
            public void onFailure(Call<List<ActividadEconomica>> call, Throwable t) {
                //textViewResultRelacion.setText(t.getMessage());
            }
        });
    }

    public void CargarBanco(){
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
                    //Log.e ( "Prueba", textViewResult.getText ().toString () );
                    //return;

                    List<BancoCarg> bancos = response.body();

                    for (int i = 0; i < bancos.size(); i++) {
                        BancoCarg banco = bancos.get(i);
                        String content = "";
                        content += "ID: " +banco.getId () + "\n";
                        content += "INSTITUCION: " + banco.getInstitucion () + "\n";
                        content += "SUCURSAL: " + banco.getSucursal () + "\n";

                        IdCargarBanco = banco.getId ();
                        Instituto = banco.getInstitucion ();
                        Sucursal = banco.getSucursal ();



                        //textViewResult.append(content);
                        //contexto = textViewResult.getText ().toString ();
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

                       // Toast.makeText ( Actividad_CargarBancos.this,  Instituto, Toast.LENGTH_SHORT ).show ();
                        Long idResultante=db.insert( Utilidades.TABLA_BANCO,Utilidades.CAMPO_ID_BANCO,valores);

                        //Toast.makeText(getApplicationContext(),"Id Registro: "+idResultante,Toast.LENGTH_SHORT).show();
                        db.close();


                    }

                }

                Toast.makeText ( AgregarDocumentos.this, "Bancos sincronizados", Toast.LENGTH_SHORT ).show ();


            }

            @Override
            public void onFailure(Call<List<BancoCarg>> call, Throwable t) {
                //textViewResult.setText(t.getMessage());
            }
        });
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
                   // Log.e ( "Prueba", textViewResultPais.getText ().toString () );
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



                        //textViewResultPais.append(content);
                        //contexto = textViewResultPais.getText ().toString ();
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
                Toast.makeText ( AgregarDocumentos.this, "Paises sincronizados", Toast.LENGTH_SHORT ).show ();


            }

            @Override
            public void onFailure(Call<List<Paisessss>> call, Throwable t) {
               // textViewResultPais.setText(t.getMessage());
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
                    //Log.e ( "Prueba", textViewResultEstado.getText ().toString () );
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



                        //textViewResultEstado.append(content);
                        //contexto = textViewResultEstado.getText ().toString ();
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
                Toast.makeText ( AgregarDocumentos.this, "Estados sincronizados", Toast.LENGTH_SHORT ).show ();


            }

            @Override
            public void onFailure(Call<List<Estadosssss>> call, Throwable t) {
                //textViewResultEstado.setText(t.getMessage());
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
                    //Log.e ( "Prueba", textViewResultMunicipio.getText ().toString () );
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



                        //textViewResultMunicipio.append(content);
                        //contexto = textViewResultMunicipio.getText ().toString ();
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

                        //Toast.makeText ( AgregarProducto.this,  municipio, Toast.LENGTH_SHORT ).show ();
                        Long idResultante=db.insert( Utilidades.TABLA_MUNICIPIOS,Utilidades.CAMPO_ID_MUNICIPIOS,valores);

                        //Toast.makeText(getApplicationContext(),"Id Registro: "+idResultante,Toast.LENGTH_SHORT).show();
                        db.close();


                    }

                }
                Toast.makeText ( AgregarDocumentos.this, "Municipios sincronizados", Toast.LENGTH_SHORT ).show ();


            }

            @Override
            public void onFailure(Call<List<Municipiossss>> call, Throwable t) {
                //textViewResultMunicipio.setText(t.getMessage());
            }
        });
    }

    public void CargarClientes(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://softcredito.com/demos/dmo_unico2020Conta/app/WebService/index.php/Welcome/")
                .addConverterFactory( GsonConverterFactory.create())
                .build();

        JsonPlaceHolderApi jsonPlaceHolderApi = retrofit.create( JsonPlaceHolderApi.class);

        Call<List<ObtenerClientes>> call = jsonPlaceHolderApi.ObtenerClientes  ();

        call.enqueue(new Callback<List<ObtenerClientes>> () {
            @Override
            public void onResponse(Call<List<ObtenerClientes>> call, Response<List<ObtenerClientes>> response) {

                if (response.isSuccessful()) {
                    //textViewResult.setText("Code: " + response.code());
                    //Log.e ( "Prueba", textViewResultMunicipio.getText ().toString () );
                    //return;

                    List<ObtenerClientes> ObtenerClientes = response.body();

                    for (int i = 0; i < ObtenerClientes.size(); i++) {
                        ObtenerClientes banco = ObtenerClientes.get(i);
                        String content = "";
                        content += "ID: " +banco.getId () + "\n";
                        content += "ESTADO: " + banco.getRfc () + "\n";
                        content += "CLAVE: " + banco.getIne () + "\n";
                        content += "CLAVES: " + banco.getCurp () + "\n";

                        IdsClientes = banco.getId ();
                        rfcClientes = banco.getRfc ();
                        ineClientes = banco.getIne ();
                        curpClientes = banco.getCurp ();



                        //textViewResultMunicipio.append(content);
                        //contexto = textViewResultMunicipio.getText ().toString ();
                        //Toast.makeText ( Actividad_CargarBancos.this, post.getInstitucion (), Toast.LENGTH_SHORT ).show ();
                        //Toast.makeText ( Actividad_CargarBancos.this, post.getSucursal (), Toast.LENGTH_SHORT ).show ();
                        //Toast.makeText ( Actividad_CargarBancos.this, post.getClabe (), Toast.LENGTH_SHORT ).show ();
                        //Toast.makeText ( Actividad_CargarBancos.this, post.getText (), Toast.LENGTH_SHORT ).show ();
                        //Toast.makeText ( Actividad_CargarBancos.this, "HOLA: "+ Instituto, Toast.LENGTH_SHORT ).show ();

                        conn=new ConexionSQLiteHelper(getApplicationContext(),"softcreditoapp2.db",null,1);
                        SQLiteDatabase db=conn.getWritableDatabase();



                        ContentValues valores = new ContentValues();

                        valores.put ( Utilidades.CAMPO_RFC_CLIENTES, rfcClientes );
                        valores.put ( Utilidades.CAMPO_CURP_CLIENTES, curpClientes );
                        valores.put ( Utilidades.CAMPO_INE_CLIENTES, ineClientes );

                        //Toast.makeText ( AgregarProducto.this,  municipio, Toast.LENGTH_SHORT ).show ();
                        Long idResultante=db.insert( Utilidades.TABLA_OBTENER_CLIENTES,Utilidades.CAMPO_ID_CLIENTES,valores);

                        //Toast.makeText(getApplicationContext(),"Id Registro: "+idResultante,Toast.LENGTH_SHORT).show();
                        db.close();


                    }

                }
                Toast.makeText ( AgregarDocumentos.this, "Clientes sincronizados", Toast.LENGTH_SHORT ).show ();


            }

            @Override
            public void onFailure(Call<List<ObtenerClientes>> call, Throwable t) {
                //textViewResultMunicipio.setText(t.getMessage());
            }
        });
    }

}
