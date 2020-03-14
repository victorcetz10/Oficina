package com.softcredito.app.ui;

import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
//import android.support.design.widget.TextInputLayout;
import com.google.android.material.textfield.TextInputLayout;
//import android.support.v4.app.LoaderManager;
import androidx.loader.app.LoaderManager;
//import android.support.v4.content.CursorLoader;
import androidx.loader.content.CursorLoader;
//import android.support.v4.content.Loader;
import androidx.loader.content.Loader;
//import android.support.v4.widget.SimpleCursorAdapter;
import androidx.cursoradapter.widget.SimpleCursorAdapter;
//import android.support.v7.app.AppCompatActivity;
//import android.support.v7.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.softcredito.app.ConexionSQLiteHelper;
import com.softcredito.app.Json.Produc;
import com.softcredito.app.Json.TipoAmort;
import com.softcredito.app.Json.TipoPag;
import com.softcredito.app.R;
import com.softcredito.app.Utilidadess.Utilidades;
import com.softcredito.app.provider.Contract;
import com.softcredito.app.provider.Contract.Productos;
import com.softcredito.app.provider.Contract.Cotizadores;
import com.softcredito.app.provider.Contract.TiposAmortizacion;
import com.softcredito.app.provider.Contract.TiposPagos;
import com.softcredito.app.utilidades.UConsultas;
import com.softcredito.app.utilidades.UTiempo;
import com.softcredito.app.inputs.DatePickerFragment;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

public class ActividadInsercionCotizador extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor>, AdapterView.OnItemSelectedListener, View.OnClickListener{

    // Referencias UI

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

    Integer TipoAmortizacionId;
    String TipoAmortizacionNombre;

    Integer TipoPagoId;
    String TipoPagoNombre;

    Integer ProductoId;
    String ProductoNombre;
    String ConvercionIdProducto;

   // private Spinner campoProducto;
    private EditText campoValidez;
    private EditText campoMonto;
    private EditText campoPlazo;
    private EditText campoSobretasa;
    private EditText campoTasaMoratoria;
 //   private Spinner campoTipoAmortizacion;
 //   private Spinner campoTipoPago;
    private EditText campoFechaDisposicion;
    private EditText campoFechaInicioAmortizaciones;
    private EditText campoNotas;

    private TextInputLayout mascaraProducto;
    private TextInputLayout mascaraValidez;
    private TextInputLayout mascaraMonto;
    private TextInputLayout mascaraPlazo;
    private TextInputLayout mascaraSobretasa;
    private TextInputLayout mascaraTasaMoratoria;
    private TextInputLayout mascaraTipoAmortizacion;
    private TextInputLayout mascaraTipoPago;
    private TextInputLayout mascaraFechaDisposicion;
    private TextInputLayout getMascaraFechaInicioAmortizaciones;
    private TextInputLayout mascaraNotas;

    private GridLayout gridLayout;


    // Clave del uri del cliente como extra
    public static final String URI_COTIZADOR = "extra.uriCotizador";
    public static final String URI_CLIENTE = "extra.uriCliente";

    private Uri uriCotizador;
    private Uri uriCliente;

    private int loading=0;

    NumberFormat formatter = new DecimalFormat("###,###.00");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actividad_insercion_cotizador);
        conn=new ConexionSQLiteHelper(getApplicationContext(),"softcreditoapp2.db",null,1);
        consultarListaProducto ();
        consultarListaTiposAmortizacion();
        consultarListaTiposPagos();
        agregarToolbar();

        // Encontrar Referencias UI
      //  campoProducto = (Spinner) findViewById(R.id.campo_producto);
        comboProducto= (android.widget.Spinner) findViewById(R.id.campo_producto);
        ArrayAdapter<CharSequence> adaptador1=new ArrayAdapter
                (this,android.R.layout.simple_spinner_item,listaProducto);
        comboProducto.setAdapter(adaptador1);
        comboProducto.setOnItemSelectedListener(this);

        campoValidez = (EditText) findViewById(R.id.campo_validez);
        campoMonto = (EditText) findViewById(R.id.campo_monto);
        campoPlazo = (EditText) findViewById(R.id.campo_plazo);
        campoSobretasa = (EditText) findViewById(R.id.campo_sobretasa);
        campoTasaMoratoria = (EditText) findViewById(R.id.campo_tasa_moratoria);
        //campoTipoAmortizacion = (Spinner) findViewById(R.id.campo_tipo_amortizacion);
        //campoTipoPago = (Spinner) findViewById(R.id.campo_tipo_pago);

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

        campoFechaDisposicion = (EditText) findViewById(R.id.campo_fecha_disposicion);
        campoFechaInicioAmortizaciones = (EditText) findViewById(R.id.campo_fecha_inicio_amortizaciones);
        campoNotas = (EditText) findViewById(R.id.campo_notas);
        mascaraProducto = (TextInputLayout) findViewById(R.id.mascara_campo_producto);
        mascaraMonto = (TextInputLayout) findViewById(R.id.mascara_campo_monto);
        mascaraPlazo = (TextInputLayout) findViewById(R.id.mascara_campo_plazo);
        mascaraSobretasa = (TextInputLayout) findViewById(R.id.mascara_campo_sobretasa);
        mascaraTasaMoratoria = (TextInputLayout) findViewById(R.id.mascara_campo_tasa_moratoria);
        mascaraTipoAmortizacion = (TextInputLayout) findViewById(R.id.mascara_campo_tipo_amortizacion);
        mascaraTipoPago = (TextInputLayout) findViewById(R.id.mascara_campo_tipo_pago);
        mascaraFechaDisposicion = (TextInputLayout) findViewById(R.id.mascara_campo_fecha_disposicion);
        getMascaraFechaInicioAmortizaciones = (TextInputLayout) findViewById(R.id.mascara_campo_fecha_inicio_amortizaciones);
        mascaraNotas = (TextInputLayout) findViewById(R.id.mascara_campo_notas);
        gridLayout=(GridLayout) findViewById(R.id.grid);

        campoFechaDisposicion.setOnClickListener(this);
        campoFechaInicioAmortizaciones.setOnClickListener(this);

        //prepararSpinnerProducto();
        //prepararSpinnerTipoAmortizacion();
        //prepararSpinnerTipoPago();

        // Obtener el uri del cliente
        String uri = getIntent().getStringExtra(URI_CLIENTE);
        if (uri != null) {
            uriCliente= Uri.parse(uri);
        }
        // Determinar si es detalle
        String uri2 = getIntent().getStringExtra(URI_COTIZADOR);
        if (uri2 != null) {
            setTitle(R.string.titulo_actividad_editar_cotizador);
            uriCotizador= Uri.parse(uri2);
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
        //campoProducto.setAdapter(mCursorAdapter);

        //Relacionado la escucha
        //campoProducto.setOnItemSelectedListener(this);
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
        //campoTipoAmortizacion.setAdapter(mCursorAdapter);
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
        //campoTipoPago.setAdapter(mCursorAdapter);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        /*
        Obteniendo el id del Spinner que recibió el evento
         */
        if (parent.getId ()==R.id.campo_producto)
        {
            String text2 = parent.getItemAtPosition ( position ).toString ();
            //  Toast.makeText ( parent.getContext (), "campo persona "+text2, Toast.LENGTH_SHORT ).show ();
            ProductoNombre= text2;
            Toast.makeText ( ActividadInsercionCotizador.this, ProductoNombre, Toast.LENGTH_SHORT ).show ();
            if (position!=0){
                String numero1 = (ProductoList.get(position-1).getId().toString());
                Toast.makeText ( ActividadInsercionCotizador.this, numero1, Toast.LENGTH_SHORT ).show ();
                ProductoId = Integer.parseInt ( numero1 );
                ConvercionIdProducto = String.valueOf ( ProductoId );
            }
        }
        if (parent.getId ()==R.id.campo_tipo_amortizacion)
        {
            String text3 = parent.getItemAtPosition ( position ).toString ();
            //  Toast.makeText ( parent.getContext (), "campo persona "+text2, Toast.LENGTH_SHORT ).show ();
            TipoAmortizacionNombre= text3;
            Toast.makeText ( ActividadInsercionCotizador.this, TipoAmortizacionNombre, Toast.LENGTH_SHORT ).show ();
            if (position!=0){
                String numero2 = (TipoAmortizacionList.get(position-1).getId().toString());
                Toast.makeText ( ActividadInsercionCotizador.this, numero2, Toast.LENGTH_SHORT ).show ();
                TipoAmortizacionId= Integer.parseInt ( numero2 );
            }
        }
        if (parent.getId ()==R.id.campo_tipo_pago)
        {
            String text4 = parent.getItemAtPosition ( position ).toString ();
            //  Toast.makeText ( parent.getContext (), "campo persona "+text2, Toast.LENGTH_SHORT ).show ();
            TipoPagoNombre= text4;
            Toast.makeText ( ActividadInsercionCotizador.this, TipoPagoNombre, Toast.LENGTH_SHORT ).show ();
            if (position!=0){
                String numero3 = (TipoPagoList.get(position-1).getId().toString());
                Toast.makeText ( ActividadInsercionCotizador.this, numero3, Toast.LENGTH_SHORT ).show ();
                TipoPagoId= Integer.parseInt ( numero3 );
            }
        }
    }
    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        //Si no se selecciona nada
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_insercion, menu);

        // Verificación de visibilidad acción eliminar
        if (uriCotizador != null) {
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
                Intent intent = new Intent(ActividadInsercionCotizador.this, ActividadListaCotizadores.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(ActividadListaCotizadores.URI_CLIENTE,uriCliente.toString());
                startActivity(intent);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void insertar() {

        // Extraer datos de UI
        String idCliente= Contract.Clientes.obtenerId(uriCliente);
        String fechaCotizador = UTiempo.obtenerTiempo();
       // Cursor cProducto = (Cursor) campoProducto.getSelectedItem();
        String produto = String.valueOf ( ProductoId );
         //       cProducto.getColumnIndex(Productos.ID));
        String validez = campoValidez.getText().toString();
        String monto = campoMonto.getText().toString();
        String plazo = campoPlazo.getText().toString();
        int tasaReferencia = 1;
        Float fSobretasa=Float.parseFloat(campoSobretasa.getText().toString())/100;
        String sobretasa = fSobretasa.toString();
        Float fTasaMoratoria=Float.parseFloat(campoTasaMoratoria.getText().toString())/100;
        String tasaMoratoria = fTasaMoratoria.toString();
       // Cursor cTipoPago = (Cursor) campoTipoPago.getSelectedItem();
        //String tipoPago = cTipoPago.getString(
          //      cTipoPago.getColumnIndex(TiposPagos.ID));
        //Cursor cTipoAmortizacion = (Cursor) campoTipoAmortizacion.getSelectedItem();
        //String tipoAmortizacion = cTipoAmortizacion.getString(
          //      cTipoAmortizacion.getColumnIndex(TiposAmortizacion.ID));
        String fechaDisposicion = campoFechaDisposicion.getText().toString();
        String fechaInicioAmortizaciones = campoFechaInicioAmortizaciones.getText().toString();
        String notas = campoNotas.getText().toString();

        monto = monto.replaceAll(",", "");

        // Validaciones y pruebas de cordura
        if (TextUtils.isEmpty(ProductoNombre)  || TextUtils.isEmpty(validez) || TextUtils.isEmpty(monto) || TextUtils.isEmpty(plazo) ||
                TextUtils.isEmpty(TipoPagoNombre) || TextUtils.isEmpty(TipoAmortizacionNombre) || TextUtils.isEmpty(fechaDisposicion)  || TextUtils.isEmpty(fechaInicioAmortizaciones)) {
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
            if(TextUtils.isEmpty(fechaDisposicion)){
                TextInputLayout mascaraCampoFechaDisposicion = (TextInputLayout) findViewById(R.id.mascara_campo_fecha_disposicion);
                mascaraCampoFechaDisposicion.setError("Este campo no puede quedar vacío");
            }
            if(TextUtils.isEmpty(fechaInicioAmortizaciones)){
                TextInputLayout mascaraCampoFechaInicioAmortizaciones = (TextInputLayout) findViewById(R.id.mascara_campo_fecha_inicio_amortizaciones);
                mascaraCampoFechaInicioAmortizaciones.setError("Este campo no puede quedar vacío");
            }
        } else {

            ContentValues valores = new ContentValues();

            // Verificación: ¿Es necesario generar un id?
            if (uriCotizador == null) {
                valores.put(Cotizadores.ID, Cotizadores.generarId());
            }
            valores.put(Cotizadores.ID_CLIENTE, idCliente);
            valores.put(Cotizadores.FECHA_COTIZACION, fechaCotizador);
            valores.put(Cotizadores.ID_PRODUCTO, produto);
            valores.put(Cotizadores.VALIDEZ, validez);
            valores.put(Cotizadores.MONTO_AUTORIZADO, monto);
            valores.put(Cotizadores.PLAZO_AUTORIZADO, plazo);
            valores.put ( Cotizadores.NOMBRE_PRODUCTO_COTIZADOR, ProductoNombre );
            valores.put ( Cotizadores.NOMBRE_AMORTIZACION_COTIZADOR, TipoAmortizacionNombre );
            valores.put(Cotizadores.ID_TASA_REFERENCIA, tasaReferencia);
            valores.put(Cotizadores.SOBRETASA, sobretasa);
            valores.put(Cotizadores.TASA_MORATORIA, tasaMoratoria);
            valores.put(Cotizadores.ID_TIPO_PAGO, TipoPagoId);
            valores.put(Cotizadores.ID_TIPO_AMORTIZACION, TipoAmortizacionId);
            valores.put(Cotizadores.FECHA_DISPOSICION, fechaDisposicion);
            valores.put(Cotizadores.FECHA_INICIO_AMORTIZACIONES, fechaInicioAmortizaciones);
            valores.put(Cotizadores.NOTAS, notas);
            valores.put(Cotizadores.VERSION, UTiempo.obtenerTiempo());

            // Iniciar inserción|actualización
            new TareaAnadirCotizador(getContentResolver(), valores).execute(uriCotizador);

            finish();
        }
    }

    private void eliminar() {
        if (uriCotizador != null) {
            // Iniciar eliminación
            new TareaEliminarCotizador(getContentResolver()).execute(uriCotizador);
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

        Float monto=Float.parseFloat(UConsultas.obtenerString(data, Cotizadores.MONTO_AUTORIZADO));
        Float sobretasa=Float.parseFloat(UConsultas.obtenerString(data, Cotizadores.SOBRETASA))*100;
        Float tasa_moratoria=Float.parseFloat(UConsultas.obtenerString(data, Cotizadores.TASA_MORATORIA))*100;

        String str_monto = formatter.format(monto);

        comboProducto.setSelection(obtenerIndiceSpinner(comboProducto,UConsultas.obtenerString(data, Cotizadores.ID_PRODUCTO), Productos.ID));
        campoValidez.setText(UConsultas.obtenerString(data, Cotizadores.VALIDEZ));
        campoMonto.setText(str_monto);
        campoPlazo.setText(UConsultas.obtenerString(data, Cotizadores.PLAZO_AUTORIZADO));
        campoSobretasa.setText(sobretasa.toString());
        campoTasaMoratoria.setText(tasa_moratoria.toString());
        comboTipoPago.setSelection(obtenerIndiceSpinner(comboTipoPago,UConsultas.obtenerString(data, Cotizadores.ID_TIPO_PAGO), TiposPagos.ID));
        comboTipoAmortizacion.setSelection(obtenerIndiceSpinner(comboTipoAmortizacion,UConsultas.obtenerString(data, Cotizadores.ID_TIPO_AMORTIZACION), TiposAmortizacion.ID));
        campoFechaDisposicion.setText(UConsultas.obtenerString(data, Cotizadores.FECHA_DISPOSICION));
        campoFechaInicioAmortizaciones.setText(UConsultas.obtenerString(data, Cotizadores.FECHA_INICIO_AMORTIZACIONES));
        campoNotas.setText(UConsultas.obtenerString(data, Cotizadores.NOTAS));
    }

    private void showDatePickerDialog(final EditText editText) {
        DatePickerFragment newFragment = DatePickerFragment.newInstance(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                // +1 because january is zero
                final String selectedDate = year + "/" + twoDigits(month+1) + "/" + twoDigits(day);
                editText.setText(selectedDate);
            }
        });
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    private String twoDigits(int n) {
        return (n<=9) ? ("0"+n) : String.valueOf(n);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, uriCotizador, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        poblarViews(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }


    static class TareaAnadirCotizador extends AsyncTask<Uri, Void, Void> {
        private final ContentResolver resolver;
        private final ContentValues valores;

        public TareaAnadirCotizador(ContentResolver resolver, ContentValues valores) {
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
                Cursor c = resolver.query(uri, new String[]{Cotizadores.INSERTADO}, null, null, null);

                if (c != null && c.moveToNext()) {

                    // Verificación de sincronización
                    if (UConsultas.obtenerInt(c, Cotizadores.INSERTADO) == 0) {
                        valores.put(Cotizadores.MODIFICADO, 1);
                    }

                    valores.put(Cotizadores.VERSION, UTiempo.obtenerTiempo());
                    resolver.update(uri, valores, null, null);
                }

            } else {
                resolver.insert(Cotizadores.URI_CONTENIDO, valores);
            }
            return null;
        }

    }

    static class TareaEliminarCotizador extends AsyncTask<Uri, Void, Void> {
        private final ContentResolver resolver;

        public TareaEliminarCotizador(ContentResolver resolver) {
            this.resolver = resolver;
        }

        @Override
        protected Void doInBackground(Uri... args) {

            /*
            Verificación: Si el registro no ha sido sincronizado aún, entonces puede eliminarse
            directamente. De lo contrario se marca como 'eliminado' = 1
             */
            Cursor c = resolver.query(args[0], new String[]{Cotizadores.INSERTADO}
                    , null, null, null);

            int insertado;

            if (c != null && c.moveToNext()) {
                insertado = UConsultas.obtenerInt(c, Cotizadores.INSERTADO);
            } else {
                return null;
            }

            if (insertado == 1) {
                resolver.delete(args[0], null, null);
            } else if (insertado == 0) {
                ContentValues valores = new ContentValues();
                valores.put(Cotizadores.ELIMINADO, 1);
                resolver.update(args[0], valores, null, null);
            }

            return null;
        }
    }

    @Override
    public void onClick(View v){
        switch (v.getId()) {
            case R.id.campo_fecha_disposicion:
                showDatePickerDialog(campoFechaDisposicion);
                break;
            case R.id.campo_fecha_inicio_amortizaciones:
                showDatePickerDialog(campoFechaInicioAmortizaciones);
                break;
        }
    }
}
