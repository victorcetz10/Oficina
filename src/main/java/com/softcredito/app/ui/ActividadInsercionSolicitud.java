package com.softcredito.app.ui;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
//import android.support.design.widget.TextInputLayout;
import com.entidades.ClientesList;
import com.google.android.material.textfield.TextInputLayout;
//import android.support.v4.app.LoaderManager;
import androidx.loader.app.LoaderManager;
//import android.support.v4.content.CursorLoader;
import androidx.loader.content.CursorLoader;
//import android.support.v4.content.Loader;
import androidx.loader.content.Loader;

import android.util.Log;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.view.View;
//import android.support.v4.widget.SimpleCursorAdapter;
import androidx.cursoradapter.widget.SimpleCursorAdapter;
//import android.support.v7.app.AppCompatActivity;
//import android.support.v7.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.GridLayout;
import android.widget.Toast;

import com.softcredito.app.ConexionSQLiteHelper;
import com.softcredito.app.Json.BancoCarg;
import com.softcredito.app.Json.Municipiossss;
import com.softcredito.app.Json.Produc;
import com.softcredito.app.Json.TipoAmort;
import com.softcredito.app.Json.TipoPag;
import com.softcredito.app.Json.Tipos_Personas;
import com.softcredito.app.R;
import com.softcredito.app.Utilidadess.Utilidades;
import com.softcredito.app.modelo.Banco;
import com.softcredito.app.provider.Contract;
import com.softcredito.app.provider.Contract.Solicitudes;
import com.softcredito.app.provider.Contract.Productos;
import com.softcredito.app.provider.Contract.Bancos;
import com.softcredito.app.provider.Contract.TiposPagos;
import com.softcredito.app.provider.Contract.TiposAmortizacion;
import com.softcredito.app.provider.Contract.StatusSolicitudes;
import com.softcredito.app.utilidades.UConsultas;
import com.softcredito.app.utilidades.UTiempo;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

public class ActividadInsercionSolicitud extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor>, AdapterView.OnItemSelectedListener{

    // Referencias UI
    android.widget.Spinner comboBanco;
    ArrayList<String> listaBanco;
    ArrayList<BancoCarg> BancoList;

    android.widget.Spinner comboTipoPago;
    ArrayList<String> listaTipoPago;
    ArrayList<TipoPag> TipoPagoList;


    android.widget.Spinner comboTipoAmortizacion;
    ArrayList<String> listaTipoAmortizacion;
    ArrayList<TipoAmort> TipoAmortizacionList;

    android.widget.Spinner comboProducto;
    ArrayList<String> listaProducto;
    ArrayList<Produc> ProductoList;


    ConexionSQLiteHelper conn;

    Integer Banco;
    String BancoNombre;

    Integer TipoAmortizacionId;
    String TipoAmortizacionNombre;

    Integer TipoPagoId;
    String TipoPagoNombre;

    Integer ProductoId;
    String ProductoNombre;
    String ConvercionIdProducto;




   // private Spinner campoProducto;
   // private Spinner campoBanco;
    private EditText campoMonto;
    private EditText campoPlazo;
    private EditText campoSobretasa;
    private EditText campoTasaMoratoria;
  //  private Spinner campoTipoAmortizacion;
    private Spinner campoTipoPago;
    private Spinner campoStatus;

    private TextInputLayout mascaraProducto;
    private TextInputLayout mascaraBanco;
    private TextInputLayout mascaraMonto;
    private TextInputLayout mascaraPlazo;
    private TextInputLayout mascaraSobretasa;
    private TextInputLayout mascaraTasaMoratoria;
    private TextInputLayout mascaraTipoAmortizacion;
    private TextInputLayout mascaraTipoPago;
    private TextInputLayout mascaraStatus;

    private GridLayout gridLayout;


    // Clave del uri del cliente como extra
    public static final String URI_SOLICITUD = "extra.uriSolicitud";
    public static final String URI_CLIENTE = "extra.uriCliente";

    private Uri uriSolicitud;
    private Uri uriCliente;

    private int loading=0;

    NumberFormat formatter = new DecimalFormat("###,###.00");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actividad_insercion_solicitud);
        conn=new ConexionSQLiteHelper(getApplicationContext(),"softcreditoapp2.db",null,1);
        agregarToolbar();
        consultarListaPersonas();
        consultarListaProducto ();
        consultarListaTiposAmortizacion();
        consultarListaTiposPagos();
        // Encontrar Referencias UI
        //campoProducto = (Spinner) findViewById(R.id.campo_producto);

        comboProducto= (android.widget.Spinner) findViewById(R.id.campo_producto);
        ArrayAdapter<CharSequence> adaptador1=new ArrayAdapter
                (this,android.R.layout.simple_spinner_item,listaProducto);
        comboProducto.setAdapter(adaptador1);
        comboProducto.setOnItemSelectedListener(this);



        comboBanco= (android.widget.Spinner) findViewById(R.id.campo_banco4);
        ArrayAdapter<CharSequence> adaptador=new ArrayAdapter
                (this,android.R.layout.simple_spinner_item,listaBanco);
        comboBanco.setAdapter(adaptador);
        comboBanco.setOnItemSelectedListener(this);



        campoMonto = (EditText) findViewById(R.id.campo_monto);
        campoPlazo = (EditText) findViewById(R.id.campo_plazo);
        campoSobretasa = (EditText) findViewById(R.id.campo_sobretasa);
        campoTasaMoratoria = (EditText) findViewById(R.id.campo_tasa_moratoria);
        //campoTipoAmortizacion = (Spinner) findViewById(R.id.campo_tipo_amortizacion);

        comboTipoAmortizacion= (android.widget.Spinner) findViewById(R.id.campo_tipo_amortizacion);
        ArrayAdapter<CharSequence> adaptador2=new ArrayAdapter
                (this,android.R.layout.simple_spinner_item,listaTipoAmortizacion);
        comboTipoAmortizacion.setAdapter(adaptador2);
        comboTipoAmortizacion.setOnItemSelectedListener(this);

        comboTipoPago= (android.widget.Spinner) findViewById(R.id.campo_tipo_pago);
        ArrayAdapter<CharSequence> adaptador3=new ArrayAdapter
                (this,android.R.layout.simple_spinner_item,listaTipoPago);
        comboTipoPago.setAdapter(adaptador3);
        comboTipoPago.setOnItemSelectedListener(this);




        //campoTipoPago = (Spinner) findViewById(R.id.campo_tipo_pago);
        campoStatus = (Spinner) findViewById(R.id.campo_status);

        mascaraProducto = (TextInputLayout) findViewById(R.id.mascara_campo_tipo_persona);
        mascaraBanco = (TextInputLayout) findViewById(R.id.mascara_campo_razon_social);
        mascaraMonto = (TextInputLayout) findViewById(R.id.mascara_campo_nombre1);
        mascaraPlazo = (TextInputLayout) findViewById(R.id.mascara_campo_nombre2);
        mascaraSobretasa = (TextInputLayout) findViewById(R.id.mascara_campo_apellido_paterno);
        mascaraTasaMoratoria = (TextInputLayout) findViewById(R.id.mascara_campo_apellido_materno);
        mascaraTipoAmortizacion = (TextInputLayout) findViewById(R.id.mascara_campo_contacto);
        mascaraTipoPago = (TextInputLayout) findViewById(R.id.mascara_campo_relacion_contacto);
        mascaraStatus = (TextInputLayout) findViewById(R.id.mascara_campo_status);

        gridLayout=(GridLayout) findViewById(R.id.grid);

       // prepararSpinnerProducto();
       // prepararSpinnerBanco();
       // prepararSpinnerTipoAmortizacion();
       // prepararSpinnerTipoPago();
        prepararSpinnerStatusSolicitudes();

        // Obtener el uri del cliente
        String uri = getIntent().getStringExtra(URI_CLIENTE);
        if (uri != null) {
            uriCliente= Uri.parse(uri);
        }
        // Determinar si es detalle
        String uri2 = getIntent().getStringExtra(URI_SOLICITUD);
        if (uri2 != null) {
            setTitle(R.string.titulo_actividad_editar_solicitud);
            uriSolicitud= Uri.parse(uri2);
            getSupportLoaderManager().restartLoader(1, null, this);
        }
    }

    private void consultarListaProducto() {
        SQLiteDatabase db=conn.getReadableDatabase();

        Produc persona=null;
        ProductoList =new ArrayList<Produc>();
        //select * from usuarios
        Cursor cursor=db.rawQuery("SELECT * FROM "+ Utilidades.TABLA_PRODUCTOS,null);

        while (cursor.moveToNext()){
            persona=new Produc();
            persona.setId(cursor.getInt(0));
            persona.setClave (cursor.getString(1));
            persona.setNombre (cursor.getString(2));

            Log.i("id",persona.getId().toString());
            Log.i("Nombre",persona.getClave ());
            Log.i("Tel",persona.getNombre ());


            ProductoList.add(persona);

        }
        obtenerListaProducto();
    }

    private void obtenerListaProducto() {
        listaProducto=new ArrayList<String>();
        listaProducto.add("Seleccione");

        for(int i=0;i<ProductoList.size();i++){
            listaProducto.add(ProductoList.get(i).getNombre ());
        }

    }



    private void consultarListaTiposAmortizacion() {
        SQLiteDatabase db=conn.getReadableDatabase();

        TipoAmort persona=null;
        TipoAmortizacionList=new ArrayList<TipoAmort>();
        //select * from usuarios
        Cursor cursor=db.rawQuery("SELECT * FROM "+ Utilidades.TABLA_TIPO_AMORTIZACION,null);

        while (cursor.moveToNext()){
            persona=new TipoAmort();
            persona.setId(cursor.getInt(0));
            persona.setNombre (cursor.getString(1));
            persona.setDescripcion (cursor.getString(2));

            Log.i("id",persona.getId().toString());
            Log.i("Nombre",persona.getNombre ());
//            Log.i("Tel",persona.getDescripcion ());


            TipoAmortizacionList.add(persona);

        }
        obtenerListaTipoAmortizacion();
    }

    private void obtenerListaTipoAmortizacion() {
        listaTipoAmortizacion=new ArrayList<String>();
        listaTipoAmortizacion.add("Seleccione");

        for(int i=0;i<TipoAmortizacionList.size();i++){
            listaTipoAmortizacion.add(TipoAmortizacionList.get(i).getNombre ());
        }

    }

    private void consultarListaTiposPagos() {
        SQLiteDatabase db=conn.getReadableDatabase();

        TipoPag persona=null;
        TipoPagoList=new ArrayList<TipoPag>();
        //select * from usuarios
        Cursor cursor=db.rawQuery("SELECT * FROM "+ Utilidades.TABLA_TIPO_PAGO,null);

        while (cursor.moveToNext()){
            persona=new TipoPag();
            persona.setId(cursor.getInt(0));
            persona.setNombre (cursor.getString(1));
            persona.setDescripcion (cursor.getString(2));

            Log.i("id",persona.getId().toString());
            Log.i("Nombre",persona.getNombre ());
            Log.i("Tel",persona.getDescripcion ());


            TipoPagoList.add(persona);

        }
        obtenerListaTipoPago();
    }

    private void obtenerListaTipoPago() {
        listaTipoPago=new ArrayList<String>();
        listaTipoPago.add("Seleccione");

        for(int i=0;i<TipoPagoList.size();i++){
            listaTipoPago.add(TipoPagoList.get(i).getNombre ());
        }

    }

    private void consultarListaPersonas() {
        SQLiteDatabase db=conn.getReadableDatabase();

        BancoCarg persona=null;
        BancoList =new ArrayList<BancoCarg>();
        //select * from usuarios
        Cursor cursor=db.rawQuery("SELECT * FROM "+ Utilidades.TABLA_BANCO,null);

        while (cursor.moveToNext()){
            persona=new BancoCarg();
            persona.setId(cursor.getInt(0));
            persona.setInstitucion (cursor.getString(1));
            persona.setSucursal (cursor.getString(2));

            Log.i("id",persona.getId().toString());
            Log.i("Nombre",persona.getInstitucion ());
            Log.i("Tel",persona.getSucursal ());


            BancoList.add(persona);

        }
        obtenerLista();
    }

    private void obtenerLista() {
        listaBanco=new ArrayList<String>();
        listaBanco.add("Seleccione");

        for(int i=0;i<BancoList.size();i++){
            listaBanco.add(BancoList.get(i).getInstitucion ());
        }

    }


    private void agregarToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void prepararSpinnerProducto() {
        Cursor mCursor;
        SimpleCursorAdapter mCursorAdapter;

        //Columnas a consultar
        String[] mProjection =
        {
                Productos._ID,
                Productos.ID,
                Productos.NOMBRE,
                Productos.SOBRETASA,
                Productos.TASA_MORATORIA,
                Productos.ID_TIPO_AMORTIZACION,
                Productos.ID_TIPO_PAGO,
        };
        //Criterios de consulta
        String mSelectionClause = null;
        //Parametros
        String[] mSelectionArgs = {};

        String mSortOrder = Productos.NOMBRE + " ASC";
        mCursor = getContentResolver().query(
                Productos.URI_CONTENIDO,  // The content URI of the words table
                mProjection,                       // The columns to return for each row
                mSelectionClause,                   // Either null, or the word the user entered
                mSelectionArgs,                    // Either empty, or the string the user entered
                mSortOrder);

        //SE CREA EL ADAPTADOR
        //Columnas que se consultan
        String[] mProductosListColumns =
                {
                        Productos.NOMBRE,
                };
        //IDs de la vista donde se colocaran los valores
        int[] mProductosListItems = {R.id.item_spinner};

        mCursorAdapter = new SimpleCursorAdapter(this,
                R.layout.spinner_personalizado,//Layout simple
                mCursor,//Cursor
                mProductosListColumns,
                mProductosListItems,
                SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);//Observer para el refresco

        //Seteando el adaptador creado
       // campoProducto.setAdapter(mCursorAdapter);

        //Relacionado la escucha
      //  campoProducto.setOnItemSelectedListener(this);
    }
    private void prepararSpinnerBanco() {
        Cursor mCursor;
        SimpleCursorAdapter mCursorAdapter;

        //Columnas a consultar
        String[] mProjection =
                {
                        Bancos._ID,
                        Bancos.ID,
                        Bancos.INSTITUCION,
                        Bancos.CLABE,
                        Bancos.NUMERO_CUENTA,
                        Bancos.INSTITUCION + "||SUBSTR(" + Bancos.NUMERO_CUENTA + ",-4,4) AS ident"
                };
        //Criterios de consulta
        String mSelectionClause = null;
        //Parametros
        String[] mSelectionArgs = {};

        String mSortOrder = Bancos.INSTITUCION + " ASC";
        mCursor = getContentResolver().query(
                Bancos.URI_CONTENIDO,  // The content URI of the words table
                mProjection,                       // The columns to return for each row
                mSelectionClause,                   // Either null, or the word the user entered
                mSelectionArgs,                    // Either empty, or the string the user entered
                mSortOrder);

        //SE CREA EL ADAPTADOR
        //Columnas que se consultan
        String[] mBancosListColumns =
                {
                        "ident",
                };
        //IDs de la vista donde se colocaran los valores
        int[] mBancosListItems = {R.id.item_spinner};

        mCursorAdapter = new SimpleCursorAdapter(this,
                R.layout.spinner_personalizado,//Layout simple
                mCursor,//Cursor
                mBancosListColumns,
                mBancosListItems,
                SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);//Observer para el refresco

        //Seteando el adaptador creado
        //campoBanco.setAdapter(mCursorAdapter);
    }
    private void prepararSpinnerTipoAmortizacion() {
        Cursor mCursor;
        SimpleCursorAdapter mCursorAdapter;

        //Columnas a consultar
        String[] mProjection =
                {
                        TiposAmortizacion._ID,
                        TiposAmortizacion.ID,
                        TiposAmortizacion.NOMBRE,
                };
        //Criterios de consulta
        String mSelectionClause = null;
        //Parametros
        String[] mSelectionArgs = {};

        String mSortOrder = TiposAmortizacion.NOMBRE + " ASC";
        mCursor = getContentResolver().query(
                TiposAmortizacion.URI_CONTENIDO,  // The content URI of the words table
                mProjection,                       // The columns to return for each row
                mSelectionClause,                   // Either null, or the word the user entered
                mSelectionArgs,                    // Either empty, or the string the user entered
                mSortOrder);

        //SE CREA EL ADAPTADOR
        //Columnas que se consultan
        String[] mTiposAmortizacionListColumns =
                {
                        TiposAmortizacion.NOMBRE,
                };
        //IDs de la vista donde se colocaran los valores
        int[] mTiposAmortizacionListItems = {R.id.item_spinner};

        mCursorAdapter = new SimpleCursorAdapter(this,
                R.layout.spinner_personalizado,//Layout simple
                mCursor,//Cursor
                mTiposAmortizacionListColumns,
                mTiposAmortizacionListItems,
                SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);//Observer para el refresco

        //Seteando el adaptador creado
     //   campoTipoAmortizacion.setAdapter(mCursorAdapter);
    }
    private void prepararSpinnerTipoPago() {
        Cursor mCursor;
        SimpleCursorAdapter mCursorAdapter;

        //Columnas a consultar
        String[] mProjection =
                {
                        TiposPagos._ID,
                        TiposPagos.ID,
                        TiposPagos.NOMBRE,
                };
        //Criterios de consulta
        String mSelectionClause = null;
        //Parametros
        String[] mSelectionArgs = {};

        String mSortOrder = TiposPagos.NOMBRE + " ASC";
        mCursor = getContentResolver().query(
                TiposPagos.URI_CONTENIDO,  // The content URI of the words table
                mProjection,                       // The columns to return for each row
                mSelectionClause,                   // Either null, or the word the user entered
                mSelectionArgs,                    // Either empty, or the string the user entered
                mSortOrder);

        //SE CREA EL ADAPTADOR
        //Columnas que se consultan
        String[] mTiposPagosListColumns =
                {
                        TiposPagos.NOMBRE,
                };
        //IDs de la vista donde se colocaran los valores
        int[] mTiposPagosListItems = {R.id.item_spinner};

        mCursorAdapter = new SimpleCursorAdapter(this,
                R.layout.spinner_personalizado,//Layout simple
                mCursor,//Cursor
                mTiposPagosListColumns,
                mTiposPagosListItems,
                SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);//Observer para el refresco

        //Seteando el adaptador creado
   //     campoTipoPago.setAdapter(mCursorAdapter);
    }

    private void prepararSpinnerStatusSolicitudes() {
        Cursor mCursor;
        SimpleCursorAdapter mCursorAdapter;

        //Columnas a consultar
        String[] mProjection =
                {
                        StatusSolicitudes._ID,
                        StatusSolicitudes.ID,
                        StatusSolicitudes.NOMBRE,
                };
        //Criterios de consulta
        String mSelectionClause = null;
        //Parametros
        String[] mSelectionArgs = {};

        String mSortOrder = StatusSolicitudes.NOMBRE + " ASC";
        mCursor = getContentResolver().query(
                StatusSolicitudes.URI_CONTENIDO,  // The content URI of the words table
                mProjection,                       // The columns to return for each row
                mSelectionClause,                   // Either null, or the word the user entered
                mSelectionArgs,                    // Either empty, or the string the user entered
                mSortOrder);

        //SE CREA EL ADAPTADOR
        //Columnas que se consultan
        String[] mStatusSolicitudesListColumns =
                {
                        StatusSolicitudes.NOMBRE,
                };
        //IDs de la vista donde se colocaran los valores
        int[] mStatusSolicitudesListItems = {R.id.item_spinner};

        mCursorAdapter = new SimpleCursorAdapter(this,
                R.layout.spinner_personalizado,//Layout simple
                mCursor,//Cursor
                mStatusSolicitudesListColumns,
                mStatusSolicitudesListItems,
                SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);//Observer para el refresco

        //Seteando el adaptador creado
        campoStatus.setAdapter(mCursorAdapter);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {



        if (parent.getId ()==R.id.campo_banco4)
        {
            String text1 = parent.getItemAtPosition ( position ).toString ();
            //  Toast.makeText ( parent.getContext (), "campo persona "+text2, Toast.LENGTH_SHORT ).show ();
            BancoNombre = text1;
            Toast.makeText ( ActividadInsercionSolicitud.this, BancoNombre, Toast.LENGTH_SHORT ).show ();
            if (position!=0){
                String numero = (BancoList.get(position-1).getId().toString());
                Toast.makeText ( ActividadInsercionSolicitud.this, numero, Toast.LENGTH_SHORT ).show ();
                Banco = Integer.parseInt ( numero );
            }
        }
        if (parent.getId ()==R.id.campo_producto)
        {
            String text2 = parent.getItemAtPosition ( position ).toString ();
            //  Toast.makeText ( parent.getContext (), "campo persona "+text2, Toast.LENGTH_SHORT ).show ();
            ProductoNombre= text2;
            Toast.makeText ( ActividadInsercionSolicitud.this, ProductoNombre, Toast.LENGTH_SHORT ).show ();
            if (position!=0){
                String numero1 = (ProductoList.get(position-1).getId().toString());
                Toast.makeText ( ActividadInsercionSolicitud.this, numero1, Toast.LENGTH_SHORT ).show ();
                ProductoId = Integer.parseInt ( numero1 );
                ConvercionIdProducto = String.valueOf ( ProductoId );
            }
        }
        if (parent.getId ()==R.id.campo_tipo_amortizacion)
        {
            String text3 = parent.getItemAtPosition ( position ).toString ();
            //  Toast.makeText ( parent.getContext (), "campo persona "+text2, Toast.LENGTH_SHORT ).show ();
            TipoAmortizacionNombre= text3;
            Toast.makeText ( ActividadInsercionSolicitud.this, TipoAmortizacionNombre, Toast.LENGTH_SHORT ).show ();
            if (position!=0){
                String numero2 = (TipoAmortizacionList.get(position-1).getId().toString());
                Toast.makeText ( ActividadInsercionSolicitud.this, numero2, Toast.LENGTH_SHORT ).show ();
                TipoAmortizacionId= Integer.parseInt ( numero2 );
            }
        }
        if (parent.getId ()==R.id.campo_tipo_pago)
        {
            String text4 = parent.getItemAtPosition ( position ).toString ();
            //  Toast.makeText ( parent.getContext (), "campo persona "+text2, Toast.LENGTH_SHORT ).show ();
            TipoPagoNombre= text4;
            Toast.makeText ( ActividadInsercionSolicitud.this, TipoPagoNombre, Toast.LENGTH_SHORT ).show ();
            if (position!=0){
                String numero3 = (TipoPagoList.get(position-1).getId().toString());
                Toast.makeText ( ActividadInsercionSolicitud.this, numero3, Toast.LENGTH_SHORT ).show ();
                TipoPagoId= Integer.parseInt ( numero3 );
            }
        }


        /*
        Obteniendo el id del Spinner que recibió el evento
         */
       /* int idSpinner = parent.getId();
        ViewGroup.LayoutParams parametrosLayout;

        switch(idSpinner) {

            case R.id.campo_producto:
                if(++loading>2 || uriSolicitud==null){
                    Cursor c1 = (Cursor) parent.getItemAtPosition(position);
                    String sobretasa = c1.getString(
                            c1.getColumnIndex(Productos.SOBRETASA));
                    String tasa_moratoria = c1.getString(
                            c1.getColumnIndex(Productos.TASA_MORATORIA));
                    String id_tipo_amortizacion = c1.getString(
                            c1.getColumnIndex(Productos.ID_TIPO_AMORTIZACION));
                    String id_tipo_pago = c1.getString(
                            c1.getColumnIndex(Productos.ID_TIPO_PAGO));

                    Float fSobretasa=Float.parseFloat(sobretasa)*100;
                    Float fTasaMoratoria=Float.parseFloat(tasa_moratoria)*100;

                    //Se asignan valores
                    campoSobretasa.setText(fSobretasa.toString());
                    campoTasaMoratoria.setText(fTasaMoratoria.toString());
                    campoTipoAmortizacion.setSelection(obtenerIndiceSpinner(campoTipoAmortizacion,UConsultas.obtenerString(c1, Solicitudes.ID_TIPO_AMORTIZACION), Productos.ID));
                    campoTipoPago.setSelection(obtenerIndiceSpinner(campoTipoPago,UConsultas.obtenerString(c1, Solicitudes.ID_TIPO_PAGO), Productos.ID));
                }

                break;
        }*/
    }
    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        //Si no se selecciona nada
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_insercion, menu);

        // Verificación de visibilidad acción eliminar
        if (uriSolicitud != null) {
            menu.findItem(R.id.accion_eliminar).setVisible(true);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.accion_confirmar:
                insertar();
                break;
            case R.id.accion_eliminar:
                eliminar();
                break;
            case android.R.id.home:
                Intent intent = new Intent(ActividadInsercionSolicitud.this, ActividadListaSolicitudes.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(ActividadListaSolicitudes.URI_CLIENTE,uriCliente.toString());
                startActivity(intent);
                finish();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }


    private void insertar() {

        // Extraer datos de UI
        String idCliente= Contract.Clientes.obtenerId(uriCliente);
        String fechaSolicitud = UTiempo.obtenerTiempo();
       // Cursor cProducto = (Cursor) campoProducto.getSelectedItem();
        String produto = String.valueOf ( ProductoId );
         //       cProducto.getColumnIndex(Productos.ID));
       // Cursor cBanco = (Cursor) campoBanco.getSelectedItem();
        //String banco = cBanco.getString(
          //      cBanco.getColumnIndex( Bancos.ID));
        String monto = campoMonto.getText().toString();
        String plazo = campoPlazo.getText().toString();
        int tasaReferencia = 1;
        Float fSobretasa=Float.parseFloat(campoSobretasa.getText().toString())/100;
        String sobretasa = fSobretasa.toString();
        Float fTasaMoratoria=Float.parseFloat(campoTasaMoratoria.getText().toString())/100;
        String tasaMoratoria = fTasaMoratoria.toString();
//        Cursor cTipoPago = (Cursor) campoTipoPago.getSelectedItem();
  //      String tipoPago = cTipoPago.getString(
    //            cTipoPago.getColumnIndex(TiposPagos.ID));
        //Cursor cTipoAmortizacion = (Cursor) campoTipoAmortizacion.getSelectedItem();
        //String tipoAmortizacion = cTipoAmortizacion.getString(
          //      cTipoAmortizacion.getColumnIndex(TiposAmortizacion.ID));

        monto = monto.replaceAll(",", "");

        // Validaciones y pruebas de cordura
        if (TextUtils.isEmpty(ProductoNombre) || TextUtils.isEmpty(monto) || TextUtils.isEmpty(plazo) || TextUtils.isEmpty(TipoPagoNombre) || TextUtils.isEmpty(TipoAmortizacionNombre)) {
            if(TextUtils.isEmpty(ProductoNombre)){
                TextInputLayout mascaraCampoProducto = (TextInputLayout) findViewById(R.id.mascara_campo_producto);
                mascaraCampoProducto.setError("Este campo no puede quedar vacío");
            }
            if(TextUtils.isEmpty(monto)){
                TextInputLayout mascaraCampoMonto = (TextInputLayout) findViewById(R.id.mascara_campo_monto);
                mascaraCampoMonto.setError("Este campo no puede quedar vacío");
            }
            if(TextUtils.isEmpty(plazo)){
                TextInputLayout mascaraCampoPlazo = (TextInputLayout) findViewById(R.id.mascara_campo_plazo);
                mascaraCampoPlazo.setError("Este campo no puede quedar vacío");
            }
            if(TextUtils.isEmpty(TipoPagoNombre)){
                TextInputLayout mascaraCampoTipoPago = (TextInputLayout) findViewById(R.id.mascara_campo_tipo_pago);
                mascaraCampoTipoPago.setError("Este campo no puede quedar vacío");
            }
            if(TextUtils.isEmpty(TipoAmortizacionNombre)){
                TextInputLayout mascaraCampoTipoAmortizacion = (TextInputLayout) findViewById(R.id.mascara_campo_tipo_amortizacion);
                mascaraCampoTipoAmortizacion.setError("Este campo no puede quedar vacío");
            }
        } else {

            ContentValues valores = new ContentValues();

            // Verificación: ¿Es necesario generar un id?
            if (uriSolicitud == null) {
                valores.put(Solicitudes.ID, Solicitudes.generarId());
            }
            valores.put(Solicitudes.ID_CLIENTE, idCliente);
            valores.put(Solicitudes.FECHA_SOLICITUD, fechaSolicitud);
            valores.put(Solicitudes.ID_PRODUCTO, produto);
            valores.put(Solicitudes.ID_BANCO, Banco);
            valores.put(Solicitudes.MONTO_SOLICITADO, monto);
            valores.put(Solicitudes.PLAZO_SOLICITADO, plazo);
            valores.put ( Solicitudes.NOMBRE_PRODUCTO, ProductoNombre );
            valores.put ( Solicitudes.NOMBRE_AMORTIZACION, TipoAmortizacionNombre );
            valores.put(Solicitudes.ID_TASA_REFERENCIA, tasaReferencia);
            valores.put(Solicitudes.SOBRETASA, sobretasa);
            valores.put(Solicitudes.TASA_MORATORIA, tasaMoratoria);
            valores.put(Solicitudes.ID_TIPO_PAGO, TipoPagoId);
            valores.put(Solicitudes.ID_TIPO_AMORTIZACION, TipoAmortizacionId);
            valores.put(Solicitudes.VERSION, UTiempo.obtenerTiempo());
            ClientesList.ID_PRODUCTO = ConvercionIdProducto;
            //ClientesList.NOMBRE_PRODUCTO = ProductoNombre;
            ClientesList.NOMBRE_AMORTIZACION = TipoAmortizacionNombre;

            // Iniciar inserción|actualización
            new TareaAnadirSolicitud(getContentResolver(), valores).execute(uriSolicitud);

            finish();
        }
    }

    private void eliminar() {
        if (uriSolicitud != null) {
            // Iniciar eliminación

            new TareaEliminarSolicitud(getContentResolver()).execute(uriSolicitud);
            finish();
        }
    }

    private int obtenerIndiceSpinner(Spinner spinner,String value,String column){
        int i=0;
        int iSeleccion=0;
        String item;

        for (i=0;i<spinner.getCount();i++){
            Cursor cSpinner = (Cursor) spinner.getItemAtPosition(i);
            item = cSpinner.getString(
                    cSpinner.getColumnIndex(column));
            if(item.equals(value)){
                iSeleccion=i;
                break;
            }
        }
        return iSeleccion;
    }

    private void poblarViews(Cursor data) {
        if (!data.moveToNext()) {
            return;
        }

        // Asignar valores a UI
        //int spinnerPosition = adapter.getPosition(UConsultas.obtenerString(data, Clientes.TIPO_PERSONA));

        Float monto=Float.parseFloat(UConsultas.obtenerString(data, Solicitudes.MONTO_SOLICITADO));
        Float sobretasa=Float.parseFloat(UConsultas.obtenerString(data, Solicitudes.SOBRETASA))*100;
        Float tasa_moratoria=Float.parseFloat(UConsultas.obtenerString(data, Solicitudes.TASA_MORATORIA))*100;

        String str_monto = formatter.format(monto);

        comboProducto.setSelection(obtenerIndiceSpinner(comboProducto,UConsultas.obtenerString(data, Solicitudes.ID_PRODUCTO), Productos.ID));
        comboBanco.setSelection(obtenerIndiceSpinner(comboBanco,UConsultas.obtenerString(data, Solicitudes.ID_BANCO), Bancos.ID));
        campoMonto.setText(str_monto);
        campoPlazo.setText(UConsultas.obtenerString(data, Solicitudes.PLAZO_SOLICITADO));
        campoSobretasa.setText(sobretasa.toString());
        campoTasaMoratoria.setText(tasa_moratoria.toString());
        comboTipoPago.setSelection(obtenerIndiceSpinner(comboTipoPago,UConsultas.obtenerString(data, Solicitudes.ID_TIPO_PAGO), TiposPagos.ID));
        comboTipoAmortizacion.setSelection(obtenerIndiceSpinner(comboTipoAmortizacion,UConsultas.obtenerString(data, Solicitudes.ID_TIPO_AMORTIZACION), TiposAmortizacion.ID));
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, uriSolicitud, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        poblarViews(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }


    static class TareaAnadirSolicitud extends AsyncTask<Uri, Void, Void> {
        private final ContentResolver resolver;
        private final ContentValues valores;

        public TareaAnadirSolicitud(ContentResolver resolver, ContentValues valores) {
            this.resolver = resolver;
            this.valores = valores;
        }

        @Override
        protected Void doInBackground(Uri... args) {
            Uri uri = args[0];
            if (null != uri) {
                /*
                Verificación: Si el cliente que se va a actualizar aún no ha sido sincronizado,
                es decir su columna 'insertado' = 1, entonces la columna 'modificado' no debe ser
                alterada
                 */
                Cursor c = resolver.query(uri, new String[]{Solicitudes.INSERTADO}, null, null, null);

                if (c != null && c.moveToNext()) {

                    // Verificación de sincronización
                    if (UConsultas.obtenerInt(c, Solicitudes.INSERTADO) == 0) {
                        valores.put(Solicitudes.MODIFICADO, 1);
                    }

                    valores.put(Solicitudes.VERSION, UTiempo.obtenerTiempo());
                    resolver.update(uri, valores, null, null);
                }

            } else {
                resolver.insert(Solicitudes.URI_CONTENIDO, valores);
            }
            return null;
        }

    }

    static class TareaEliminarSolicitud extends AsyncTask<Uri, Void, Void> {
        private final ContentResolver resolver;

        public TareaEliminarSolicitud(ContentResolver resolver) {
            this.resolver = resolver;
        }

        @Override
        protected Void doInBackground(Uri... args) {

            /*
            Verificación: Si el registro no ha sido sincronizado aún, entonces puede eliminarse
            directamente. De lo contrario se marca como 'eliminado' = 1
             */
            Cursor c = resolver.query(args[0], new String[]{Solicitudes.INSERTADO}
                    , null, null, null);

            int insertado;

            if (c != null && c.moveToNext()) {
                insertado = UConsultas.obtenerInt(c, Solicitudes.INSERTADO);
            } else {
                return null;
            }

            if (insertado == 1) {
                resolver.delete(args[0], null, null);
            } else if (insertado == 0) {
                ContentValues valores = new ContentValues();
                valores.put(Solicitudes.ELIMINADO, 1);
                resolver.update(args[0], valores, null, null);
            }

            return null;
        }
    }
}
