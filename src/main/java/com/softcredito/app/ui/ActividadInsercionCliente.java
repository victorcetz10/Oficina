package com.softcredito.app.ui;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
//import android.support.design.widget.TextInputLayout;
import com.entidades.ClientesList;
import com.entidades.Validaciones;
import com.google.android.material.textfield.TextInputLayout;
//import android.support.v4.app.LoaderManager;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.loader.app.LoaderManager;
//import android.support.v4.content.CursorLoader;
import androidx.loader.content.CursorLoader;
//import android.support.v4.content.Loader;
import androidx.loader.content.Loader;

import android.provider.Settings;
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
import android.widget.TextView;
import android.widget.Toast;

import com.softcredito.app.ConexionSQLiteHelper;
import com.softcredito.app.Json.ActividadEconomica;
import com.softcredito.app.Json.BancoCarg;
import com.softcredito.app.Json.Categoria_Economia;
import com.softcredito.app.Json.Estadosssss;
import com.softcredito.app.Json.Municipiossss;
import com.softcredito.app.Json.Paisessss;
import com.softcredito.app.Json.Relacion;
import com.softcredito.app.Json.Tipos_Personas;
import com.softcredito.app.R;
import com.softcredito.app.Utilidadess.Utilidades;
import com.softcredito.app.modelo.EsCliente;
import com.softcredito.app.modelo.Estado;
import com.softcredito.app.modelo.Municipio;
import com.softcredito.app.modelo.Pais;
import com.softcredito.app.provider.Contract;
import com.softcredito.app.provider.Contract.EsClientes;
import com.softcredito.app.provider.Contract.Clientes;
import com.softcredito.app.provider.Contract.TiposPersonas;
import com.softcredito.app.provider.Contract.TiposContactos;
import com.softcredito.app.provider.Contract.EstadosCiviles;
import com.softcredito.app.provider.Contract.CategoriasActividadesEconomicas;
import com.softcredito.app.provider.Contract.ActividadesEconomicas;
import com.softcredito.app.provider.Contract.Paises;
import com.softcredito.app.provider.Contract.Estados;
import com.softcredito.app.provider.Contract.Municipios;
import com.softcredito.app.utilidades.UConsultas;
import com.softcredito.app.utilidades.UTiempo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ActividadInsercionCliente extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor>, AdapterView.OnItemSelectedListener{

    // Referencias UI

    android.widget.Spinner comboPersonas;
    android.widget.Spinner comboEsCliente;
    android.widget.Spinner comboEstadoCivil;
    android.widget.Spinner comboPais;
    android.widget.Spinner comboEstado;
    android.widget.Spinner comboMunicipio;
    android.widget.Spinner comboRelacion;
    android.widget.Spinner comboActividadEconomica;
    android.widget.Spinner comboEconomica;
    TextView txtNombre,txtDocumento,txtTelefono;
    ArrayList<String> listaPersonas;
    ArrayList<String> listaPais;
    ArrayList<String> listaEstados;

    ArrayList<String> listaMunicipio;
    ArrayList<String> listaPersonas2;
    ArrayList<String> listaRelacion;
    ArrayList<String> listaActividadEconomica;
    ArrayList<String> listaEconomica;
    ArrayList<Tipos_Personas> personasList;
    ArrayList<Paisessss> PaisList;
    ArrayList<Estadosssss> EstadosList;
    ArrayList<Municipiossss> MunicipiosList;
    ArrayList<Relacion> RelacionList;
    ArrayList<Categoria_Economia> ActividadEconomicaList;
    ArrayList<ActividadEconomica> EconomicaList;
    ConexionSQLiteHelper conn;


    private Spinner campoEsCliente;
    private Spinner campoTipoPersona;
    private EditText campoRazonSocial;
    private EditText campoNombre1;
    private EditText campoNombre2;
    private EditText campoApellidoPaterno;
    private EditText campoApellidoMaterno;
    private EditText campoCurp;
    private EditText campoRfc;
    private EditText campoIne;
    private EditText campoFechaNacimiento;
    private Spinner campoEstadoCivil;
    private EditText campoOcupacion;
    private Spinner campoCategoriaActividadEconomica;
    private Spinner campoActividadEconomica;
    private EditText campoContacto;
    private Spinner campoRelacionContacto;
    private EditText campoTelefono;
    private EditText campoCelular;
    private EditText campoCorreo;
    //private Spinner campoPais;
    private Spinner campoEstado;
    private Spinner campoMunicipio;
    private EditText campoLocalidad;
    private EditText campoColonia;
    private EditText campoCodigoPostal;
    private EditText campoCalle;
    private EditText campoNumeroExterior;
    private EditText campoNumeroInterior;
    private EditText campoReferencia;
    private EditText campoNotas;

    private TextInputLayout mascaraEsCliente;
    private TextInputLayout mascaraTipoPersona;
    private TextInputLayout mascaraRazonSocial;
    private TextInputLayout mascaraNombre1;
    private TextInputLayout mascaraNombre2;
    private TextInputLayout mascaraApellidoPaterno;
    private TextInputLayout mascaraApellidoMaterno;
    private TextInputLayout mascaraCurp;
    private TextInputLayout mascaraRfc;
    private TextInputLayout mascaraIne;
    private TextInputLayout mascaraFechaNacimiento;
    private TextInputLayout mascaraEstadoCivil;
    private TextInputLayout mascaraOcupacion;
    private TextInputLayout mascaraCategoriaActividadEconomica;
    private TextInputLayout mascaraActividadEconomica;
    private TextInputLayout mascaraContacto;
    private TextInputLayout mascaraRelacionContacto;
    private TextInputLayout mascaraTelefono;
    private TextInputLayout mascaraCelular;
    private TextInputLayout mascaraCorreo;
    private TextInputLayout mascaraPais;
    private TextInputLayout mascaraEstado;
    private TextInputLayout mascaraMunicipio;
    private TextInputLayout mascaraLocalidad;
    private TextInputLayout mascaraColonia;
    private TextInputLayout mascaraCodigoPostal;
    private TextInputLayout mascaraCalle;
    private TextInputLayout mascaraNumeroExterior;
    private TextInputLayout mascaraNumeroInterior;
    private TextInputLayout mascaraReferencia;
    private TextInputLayout mascaraNotas;

    private TextView labelEsCliente;
    private TextView textvidcambi;
    private TextView labelTipoPersona;
    private TextView labelRazonSocial;
    private TextView labelNombre1;
    private TextView labelNombre2;
    private TextView labelApellidoPaterno;
    private TextView labelApellidoMaterno;
    private TextView labelCurp;
    private TextView labelRfc;
    private TextView labelIne;
    private TextView labelFechaNacimiento;
    private TextView labelEstadoCivil;
    private TextView labelOcupacion;
    private TextView labelCategoriaActividadEconomica;
    private TextView labelActividadEconomica;
    private TextView labelContacto;
    private TextView labelRelacionContacto;
    private TextView labelTelefono;
    private TextView labelCelular;
    private TextView labelCorreo;
    private TextView labelPais;
    private TextView labelEstado;
    private TextView labelMunicipio;
    private TextView labelLocalidad;
    private TextView labelColonia;
    private TextView labelCodigoPostal;
    private TextView labelCalle;
    private TextView labelNumeroExterior;
    private TextView labelNumeroInterior;
    private TextView labelReferencia;
    private TextView labelNotas;

    private TextView Latitud;
    private TextView Longitud;
    private GridLayout gridLayout;

    String StringLatitud;
    String StringLongitud;

    String nombre1;
    String Tinombre1;
    String GEsCliente;
    String EstadoCivil;
    String estados;
    String clave_estado;
    String[] parametro = {"1"} ;
    String[] clave;
    Integer CorreoP;
    Integer RfcP;
    Integer CurpP;

    String paises;
    String municipios;
    Integer Escli ;
    String Relac;
    Integer Activ1;
    Integer Activ;
    String Act;
    String Act2;
    Validaciones  objValidar;

    // Clave del uri del cliente como extra
    public static final String URI_CLIENTE = "extra.uriCliente";

    private Uri uriCliente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actividad_insercion_cliente);
        objValidar = new Validaciones ();
        agregarToolbar();
        int permissionCheck = ContextCompat.checkSelfPermission ( this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionCheck == PackageManager.PERMISSION_DENIED){
            Toast.makeText ( this,"Autoriza",Toast.LENGTH_SHORT ).show ();
            if(ActivityCompat.shouldShowRequestPermissionRationale ( this, Manifest.permission.ACCESS_FINE_LOCATION )){
                Toast.makeText ( this,"Si tienes",Toast.LENGTH_SHORT ).show ();

            }else {
                Toast.makeText ( this,"Si",Toast.LENGTH_SHORT ).show ();
                ActivityCompat.requestPermissions ( this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1 );
            }
        }



        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);
        } else {
            locationStart();
        }

        // Encontrar Referencias UI
        campoEsCliente = (Spinner) findViewById(R.id.campo_es_cliente);
       // campoTipoPersona = (Spinner) findViewById(R.id.campo_tipo_persona);

        conn=new ConexionSQLiteHelper(getApplicationContext(),"softcreditoapp2.db",null,1);
        consultarListaPersonas();
        consultarListaPais();
        consultarListaEstados ();
        consultarListaMunicipios2();
        consultarListaRelacion();
        consultarListaCategoriaActividadEconomica();
        consultarListaCategoriaEconomica();
        comboPersonas= (android.widget.Spinner) findViewById(R.id.campo_tipo_persona);
        ArrayAdapter<CharSequence> adaptador=new ArrayAdapter
                (this,android.R.layout.simple_spinner_item,listaPersonas);
        comboPersonas.setAdapter(adaptador);
        comboPersonas.setOnItemSelectedListener(this);

        comboPais= (android.widget.Spinner) findViewById(R.id.campo_pais);
        ArrayAdapter<CharSequence> adaptador2=new ArrayAdapter
                (this,android.R.layout.simple_spinner_item,listaPais);
        comboPais.setAdapter(adaptador2);
        comboPais.setOnItemSelectedListener(this);

        comboEstado= (android.widget.Spinner) findViewById(R.id.campo_estado);
        ArrayAdapter<CharSequence> adaptador3=new ArrayAdapter
                (this,android.R.layout.simple_spinner_item,listaEstados);
        comboEstado.setAdapter(adaptador3);
        comboEstado.setOnItemSelectedListener(this);



        comboRelacion= (android.widget.Spinner) findViewById(R.id.campo_relacion_contacto);
        ArrayAdapter<CharSequence> adaptador9=new ArrayAdapter
                (this,android.R.layout.simple_spinner_item,listaRelacion);
        comboRelacion.setAdapter(adaptador9);
        comboRelacion.setOnItemSelectedListener(this);

        comboActividadEconomica= (android.widget.Spinner) findViewById(R.id.campo_categoria_actividad_economica);
        ArrayAdapter<CharSequence> adaptador10=new ArrayAdapter
                (this,android.R.layout.simple_spinner_item,listaActividadEconomica);
        comboActividadEconomica.setAdapter(adaptador10);
        comboActividadEconomica.setOnItemSelectedListener(this);

        comboEconomica= (android.widget.Spinner) findViewById(R.id.campo_actividad_economica);
        ArrayAdapter<CharSequence> adaptador11=new ArrayAdapter
                (this,android.R.layout.simple_spinner_item,listaEconomica);
        comboEconomica.setAdapter(adaptador11);
        comboEconomica.setOnItemSelectedListener(this);


        comboEsCliente = (android.widget.Spinner) findViewById(R.id.campo_es_cliente);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this,
                R.array.array_name2, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        comboEsCliente.setAdapter(adapter2);
        comboEsCliente.setOnItemSelectedListener(this);

        comboEstadoCivil = (android.widget.Spinner) findViewById(R.id.campo_estado_civil);
        ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(this,
                R.array.array_name3, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        comboEstadoCivil.setAdapter(adapter4);
        comboEstadoCivil.setOnItemSelectedListener(this);
        Latitud = (TextView)findViewById ( R.id.textv_lat );
        Longitud = (TextView)findViewById ( R.id.textv_lon );

        campoRazonSocial = (EditText) findViewById(R.id.campo_razon_social);
        campoNombre1 = (EditText) findViewById(R.id.campo_nombre1);
        campoNombre2 = (EditText) findViewById(R.id.campo_nombre2);
        campoApellidoPaterno = (EditText) findViewById(R.id.campo_apellido_paterno);
        campoApellidoMaterno = (EditText) findViewById(R.id.campo_apellido_materno);
        campoCurp = (EditText) findViewById(R.id.campo_curp);
        campoRfc = (EditText) findViewById(R.id.campo_rfc);
        campoIne = (EditText) findViewById(R.id.campo_ine);
        campoFechaNacimiento = (EditText) findViewById(R.id.campo_fecha_nacimiento);
      //  campoEstadoCivil = (Spinner) findViewById(R.id.campo_estado_civil);
        campoOcupacion = (EditText) findViewById(R.id.campo_ocupacion);
      //  campoCategoriaActividadEconomica = (Spinner) findViewById(R.id.campo_categoria_actividad_economica);
      //  campoActividadEconomica = (Spinner) findViewById(R.id.campo_actividad_economica);
        campoContacto = (EditText) findViewById(R.id.campo_contacto);
      //  campoRelacionContacto = (Spinner) findViewById(R.id.campo_relacion_contacto);
        campoTelefono = (EditText) findViewById(R.id.campo_telefono);
        campoCelular = (EditText) findViewById(R.id.campo_celular);
        campoCorreo = (EditText) findViewById(R.id.campo_correo);
      //  campoPais = (Spinner) findViewById(R.id.campo_pais);
      //  campoEstado = (Spinner) findViewById(R.id.campo_estado);
      //  campoMunicipio = (Spinner) findViewById(R.id.campo_municipio);
        campoLocalidad = (EditText) findViewById(R.id.campo_localidad);
        campoColonia = (EditText) findViewById(R.id.campo_colonia);
        campoCodigoPostal = (EditText) findViewById(R.id.campo_codigo_postal);
        campoCalle = (EditText) findViewById(R.id.campo_calle);
        campoNumeroExterior = (EditText) findViewById(R.id.campo_numero_exterior);
        campoNumeroInterior = (EditText) findViewById(R.id.campo_numero_interior);
        campoReferencia = (EditText) findViewById(R.id.campo_referencia);
        campoNotas = (EditText) findViewById(R.id.campo_notas);

        mascaraEsCliente = (TextInputLayout) findViewById(R.id.mascara_campo_es_cliente);
        mascaraTipoPersona = (TextInputLayout) findViewById(R.id.mascara_campo_tipo_persona);
        mascaraRazonSocial = (TextInputLayout) findViewById(R.id.mascara_campo_razon_social);
        mascaraNombre1 = (TextInputLayout) findViewById(R.id.mascara_campo_nombre1);
        mascaraNombre2 = (TextInputLayout) findViewById(R.id.mascara_campo_nombre2);
        mascaraApellidoPaterno = (TextInputLayout) findViewById(R.id.mascara_campo_apellido_paterno);
        mascaraApellidoMaterno = (TextInputLayout) findViewById(R.id.mascara_campo_apellido_materno);
        mascaraCurp = (TextInputLayout) findViewById(R.id.mascara_campo_curp);
        mascaraRfc = (TextInputLayout) findViewById(R.id.mascara_campo_rfc);
        mascaraIne = (TextInputLayout) findViewById(R.id.mascara_campo_ine);
        mascaraFechaNacimiento = (TextInputLayout) findViewById(R.id.mascara_campo_fecha_nacimiento);
        mascaraEstadoCivil = (TextInputLayout) findViewById(R.id.mascara_campo_estado_civil);
        mascaraOcupacion = (TextInputLayout) findViewById(R.id.mascara_campo_ocupacion);
        mascaraCategoriaActividadEconomica = (TextInputLayout) findViewById(R.id.mascara_campo_categoria_actividad_economica);
        mascaraActividadEconomica = (TextInputLayout) findViewById(R.id.mascara_campo_actividad_economica);
        mascaraContacto = (TextInputLayout) findViewById(R.id.mascara_campo_contacto);
        mascaraRelacionContacto = (TextInputLayout) findViewById(R.id.mascara_campo_relacion_contacto);
        mascaraTelefono = (TextInputLayout) findViewById(R.id.mascara_campo_telefono);
        mascaraCelular = (TextInputLayout) findViewById(R.id.mascara_campo_celular);
        mascaraCorreo = (TextInputLayout) findViewById(R.id.mascara_campo_correo);
        mascaraPais = (TextInputLayout) findViewById(R.id.mascara_campo_pais);
        mascaraEstado = (TextInputLayout) findViewById(R.id.mascara_campo_estado);
        mascaraMunicipio = (TextInputLayout) findViewById(R.id.mascara_campo_municipio);
        mascaraLocalidad = (TextInputLayout) findViewById(R.id.mascara_campo_localidad);
        mascaraColonia = (TextInputLayout) findViewById(R.id.mascara_campo_colonia);
        mascaraCodigoPostal = (TextInputLayout) findViewById(R.id.mascara_campo_codigo_postal);
        mascaraCalle = (TextInputLayout) findViewById(R.id.mascara_campo_calle);
        mascaraNumeroExterior = (TextInputLayout) findViewById(R.id.mascara_campo_numero_exterior);
        mascaraNumeroInterior = (TextInputLayout) findViewById(R.id.mascara_campo_numero_interior);
        mascaraReferencia = (TextInputLayout) findViewById(R.id.mascara_campo_referencia);
        mascaraNotas = (TextInputLayout) findViewById(R.id.mascara_campo_notas);

        labelEsCliente = (TextView) findViewById(R.id.textv_es_cliente);
        labelTipoPersona = (TextView) findViewById(R.id.textv_tipo_persona);
        labelRazonSocial = (TextView) findViewById(R.id.textv_razon_social);
        labelNombre1 = (TextView) findViewById(R.id.textv_nombre1);
        labelNombre2 = (TextView) findViewById(R.id.textv_nombre2);
        labelApellidoPaterno = (TextView) findViewById(R.id.textv_apellido_paterno);
        labelApellidoMaterno = (TextView) findViewById(R.id.textv_apellido_materno);
        labelCurp = (TextView) findViewById(R.id.textv_curp);
        labelRfc = (TextView) findViewById(R.id.textv_rfc);
        labelIne = (TextView) findViewById(R.id.textv_ine);
        labelFechaNacimiento = (TextView) findViewById(R.id.textv_fecha_nacimiento);
        labelEstadoCivil = (TextView) findViewById(R.id.textv_estado_civil);
        labelOcupacion = (TextView) findViewById(R.id.textv_ocupacion);
        labelCategoriaActividadEconomica = (TextView) findViewById(R.id.textv_categoria_actividad_economica);
        labelActividadEconomica = (TextView) findViewById(R.id.textv_actividad_economica);
        labelContacto = (TextView) findViewById(R.id.textv_contacto);
        labelRelacionContacto = (TextView) findViewById(R.id.textv_relacion_contacto);
        labelTelefono = (TextView) findViewById(R.id.textv_telefono);
        labelCelular = (TextView) findViewById(R.id.textv_celular);
        labelCorreo = (TextView) findViewById(R.id.textv_correo);
        labelPais = (TextView) findViewById(R.id.textv_pais);
        labelEstado = (TextView) findViewById(R.id.textv_estado);
        labelMunicipio = (TextView) findViewById(R.id.textv_municipio);
        labelLocalidad = (TextView) findViewById(R.id.textv_localidad);
        labelColonia = (TextView) findViewById(R.id.textv_colonia);
        labelCodigoPostal = (TextView) findViewById(R.id.textv_codigo_postal);
        labelCalle = (TextView) findViewById(R.id.textv_calle);
        labelNumeroExterior = (TextView) findViewById(R.id.textv_numero_exterior);
        labelNumeroInterior = (TextView) findViewById(R.id.textv_numero_interior);
        labelReferencia = (TextView) findViewById(R.id.textv_referencia);
        labelNotas = (TextView) findViewById(R.id.textv_notas);

        gridLayout=(GridLayout) findViewById(R.id.grid);
        Log.e ( "Cliente", EsClientes.NOMBRE );

        //prepararSpinnerEsCliente();
      //  prepararSpinnerTipoPersona();
        //prepararSpinnerTipoContacto();
        //prepararSpinnerEstadoCivil();

        //prepararSpinnerCategoriaActividadEconomica();
        //prepararSpinnerActividadEconomica();
        //prepararSpinnerPais();
        //prepararSpinnerEstado();
        //prepararSpinnerMunicipio();

        // Determinar si es detalle
        String uri = getIntent().getStringExtra(URI_CLIENTE);
        if (uri != null) {
            setTitle(R.string.titulo_actividad_editar_cliente);
            uriCliente = Uri.parse(uri);
            getSupportLoaderManager().restartLoader(1, null, this);
        }else{
//            campoCategoriaActividadEconomica.setOnItemSelectedListener(this);
//            campoActividadEconomica.setOnItemSelectedListener(this);
//            campoPais.setOnItemSelectedListener(this);
//            campoEstado.setOnItemSelectedListener(this);


        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void consultarListaPersonas() {
        SQLiteDatabase db=conn.getReadableDatabase();

        Tipos_Personas persona=null;
        personasList =new ArrayList<Tipos_Personas>();
        //select * from usuarios
        Cursor cursor=db.rawQuery("SELECT * FROM "+ Utilidades.TABLA_TIPOPERSONAS,null);

        while (cursor.moveToNext()){
            persona=new Tipos_Personas();
            persona.setId(cursor.getInt(0));
            persona.setClave (cursor.getString(1));
            persona.setNombre (cursor.getString(2));

            Log.i("id",persona.getId().toString());
            Log.i("Nombre",persona.getClave ());
            Log.i("Tel",persona.getNombre ());


            personasList.add(persona);

        }
        obtenerLista();
    }

    private void obtenerLista() {
        listaPersonas=new ArrayList<String>();
        listaPersonas.add("Seleccione");

        for(int i=0;i<personasList.size();i++){
            listaPersonas.add(personasList.get(i).getNombre ());
        }

    }

    private void consultarListaPais() {
        SQLiteDatabase db=conn.getReadableDatabase();

        Paisessss pas=null;
        PaisList =new ArrayList<Paisessss>();
        //select * from usuarios
        Cursor cursor=db.rawQuery("SELECT * FROM "+ Utilidades.TABLA_PAIS,null);

        while (cursor.moveToNext()){
            pas=new Paisessss();
            pas.setId(cursor.getInt(0));
            pas.setPais(cursor.getString(1));
            pas.setIso  (cursor.getString(2));

            Log.i("id",pas.getId().toString());
            Log.i("Nombre",pas.getPais ());
            Log.i("Tel",pas.getIso ());


            PaisList.add(pas);

        }
        obtenerListaPais();

    }

    private void obtenerListaPais() {
        listaPais=new ArrayList<String>();
        listaPais.add("Mexico");

        for(int i=0;i<PaisList.size();i++){
            listaPais.add(PaisList.get(i).getPais ());
        }

    }

    private void consultarListaEstados() {
        SQLiteDatabase db=conn.getReadableDatabase();

        Estadosssss pas=null;
        EstadosList =new ArrayList<Estadosssss>();
        //select * from usuarios
        Cursor cursor=db.rawQuery("SELECT * FROM "+ Utilidades.TABLA_ESTADOS,null);

        while (cursor.moveToNext()){
            pas=new Estadosssss();
            pas.setId(cursor.getInt(0));
            pas.setEstado (cursor.getString(1));
            pas.setClave  (cursor.getString(2));

            Log.i("id",pas.getId().toString());
            Log.i("Nombre",pas.getEstado ());
            Log.i("Tel",pas.getClave ());


            EstadosList.add(pas);

        }
        obtenerListaEstados();
    }

    private void obtenerListaEstados() {
        listaEstados=new ArrayList<String>();
        listaEstados.add("Seleccione");

        for(int i=0;i<EstadosList.size();i++){
            listaEstados.add(EstadosList.get(i).getEstado ());

        }

    }

    private void consultarListaMunicipios() {
        SQLiteDatabase db=conn.getReadableDatabase();

        Municipiossss pas=null;
        MunicipiosList =new ArrayList<Municipiossss>();
        //select * from usuarios
        Cursor cursor=db.rawQuery("SELECT * FROM "+ Utilidades.TABLA_MUNICIPIOS,null);

        while (cursor.moveToNext()){
            pas=new Municipiossss();
            pas.setId(cursor.getInt(0));
            pas.setMunicipio (cursor.getString(1));
            pas.setId_estado  (cursor.getString(2));

            Log.i("id",pas.getId().toString());
            Log.i("Nombre",pas.getMunicipio ());
            Log.i("Tel",pas.getId_estado ());


            MunicipiosList.add(pas);

        }
        obtenerListaMunicipios();
    }

    private void obtenerListaMunicipios() {
        listaMunicipio=new ArrayList<String>();
        listaMunicipio.add("Seleccione");

        for(int i=0;i<MunicipiosList.size();i++){
            listaMunicipio.add(MunicipiosList.get(i).getMunicipio ());
        }

    }

    private void consultarListaMunicipios2( ) {
        SQLiteDatabase db=conn.getReadableDatabase();
        //String _id = textvidcambi.getText ().toString ();

        Toast.makeText ( ActividadInsercionCliente.this,"correcto"+ ClientesList.ID_ESTADOS,Toast.LENGTH_SHORT ).show ();

        Municipiossss pas=null;
        MunicipiosList =new ArrayList<Municipiossss>();
        //select * from usuarios
        String[] para={ClientesList.ID_ESTADOS};
        parametro = para;

        Cursor cursor=db.rawQuery("SELECT * FROM "+ Utilidades.TABLA_MUNICIPIOS+" WHERE "+Utilidades.CAMPO_CLAVE_MUNICIPIO + "=? ", parametro);

        while (cursor.moveToNext()){
            pas=new Municipiossss();
            pas.setId(cursor.getInt(0));
            pas.setMunicipio (cursor.getString(1));
            pas.setId_estado  (cursor.getString(2));

            Log.i("id",pas.getId().toString());
            Log.i("Nombre",pas.getMunicipio ());
            Log.i("Tel",pas.getId_estado ());


            MunicipiosList.add(pas);

        }
        obtenerListaMunicipios2();
    }

    private void obtenerListaMunicipios2() {
        listaMunicipio=new ArrayList<String>();
        listaMunicipio.add("Seleccione");

        for(int i=0;i<MunicipiosList.size();i++){
            listaMunicipio.add(MunicipiosList.get(i).getMunicipio ());

        }

        comboMunicipio= (android.widget.Spinner) findViewById(R.id.campo_municipio);
        ArrayAdapter<CharSequence> adaptador4=new ArrayAdapter
                (this,android.R.layout.simple_spinner_item,listaMunicipio);
        comboMunicipio.setAdapter(adaptador4);
        comboMunicipio.setOnItemSelectedListener(this);

    }

    private void consultarListaRelacion() {
        SQLiteDatabase db=conn.getReadableDatabase();

        Relacion pas=null;
        RelacionList =new ArrayList<Relacion>();
        //select * from usuarios
        Cursor cursor=db.rawQuery("SELECT * FROM "+ Utilidades.TABLA_TIPOS_CONTACTOS,null);

        while (cursor.moveToNext()){
            pas=new Relacion ();
            pas.setId(cursor.getInt(0));
            pas.setNombre (cursor.getString(1));
            pas.setDescripcion  (cursor.getString(2));

            Log.i("id",pas.getId().toString());
            Log.i("Nombre",pas.getNombre ());
//            Log.i("Tel",pas.getDescripcion ());


            RelacionList.add(pas);

        }
        obtenerListaRelacion();
    }

    private void obtenerListaRelacion() {
        listaRelacion=new ArrayList<String>();
        listaRelacion.add("Seleccione");

        for(int i=0;i<RelacionList.size();i++){
            listaRelacion.add(RelacionList.get(i).getNombre ());
        }

    }

    private void consultarListaCategoriaActividadEconomica() {
        SQLiteDatabase db=conn.getReadableDatabase();

        Categoria_Economia pas=null;
        ActividadEconomicaList =new ArrayList<Categoria_Economia>();
        //select * from usuarios
        Cursor cursor=db.rawQuery("SELECT * FROM "+ Utilidades.TABLA_CATEGORIA_ECONOMICAS,null);

        while (cursor.moveToNext()){
            pas=new Categoria_Economia ();
            pas.setId(cursor.getInt(0));
            pas.setNombre (cursor.getString(1));
            pas.setDescripcion  (cursor.getString(2));

            Log.i("id",pas.getId().toString());
            Log.i("Nombre",pas.getNombre ());
//            Log.i("Tel",pas.getDescripcion ());


            ActividadEconomicaList.add(pas);

        }
        obtenerListaCategoriaActividadEconomica();
    }

    private void obtenerListaCategoriaActividadEconomica() {
        listaActividadEconomica=new ArrayList<String>();
        listaActividadEconomica.add("Seleccione");

        for(int i=0;i<ActividadEconomicaList.size();i++){
            listaActividadEconomica.add(ActividadEconomicaList.get(i).getNombre ());
        }

    }

    private void consultarListaCategoriaEconomica() {
        SQLiteDatabase db=conn.getReadableDatabase();

        ActividadEconomica pas=null;
        EconomicaList =new ArrayList<ActividadEconomica>();
        //select * from usuarios
        Cursor cursor=db.rawQuery("SELECT * FROM "+ Utilidades.TABLA_ACTIVIDAD_ECONOMICAS,null);

        while (cursor.moveToNext()){
            pas=new ActividadEconomica ();
            pas.setId(cursor.getInt(0));
            pas.setClave (cursor.getString(1));
            pas.setDescripcion  (cursor.getString(2));

            Log.i("id",pas.getId().toString());
            Log.i("Nombre",pas.getClave ());
           Log.i("descripcion",pas.getDescripcion ());


            EconomicaList.add(pas);

        }
        obtenerListaCategoriaEconomica();
    }

    private void obtenerListaCategoriaEconomica() {
        listaEconomica=new ArrayList<String>();
        listaEconomica.add("Seleccione");

        for(int i=0;i<EconomicaList.size();i++){
            listaEconomica.add( EconomicaList.get(i).getDescripcion ()  );
        }

    }

    private void agregarToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }
    private void prepararSpinnerEsCliente() {
        Cursor mCursor;
        SimpleCursorAdapter mCursorAdapter;

        //Columnas a consultar
        String[] mProjection =
                {
                        EsClientes._ID,
                        EsClientes.ID,
                        EsClientes.NOMBRE,
                };
        //Criterios de consulta
        String mSelectionClause = null;
        //Parametros
        String[] mSelectionArgs = {};

        String mSortOrder = EsClientes.NOMBRE + " ASC";
        mCursor = getContentResolver().query(
                EsClientes.URI_CONTENIDO,  // The content URI of the words table
                mProjection,                       // The columns to return for each row
                mSelectionClause,                   // Either null, or the word the user entered
                mSelectionArgs,                    // Either empty, or the string the user entered
                mSortOrder);

        //SE CREA EL ADAPTADOR
        //Columnas que se consultan
        String[] mEsClientesListColumns =
                {
                        EsClientes.NOMBRE,
                };
        //IDs de la vista donde se colocaran los valores
        int[] mEsClientesListItems = {R.id.item_spinner};

        mCursorAdapter = new SimpleCursorAdapter(this,
                R.layout.spinner_personalizado,//Layout simple
                mCursor,//Cursor
                mEsClientesListColumns,
                mEsClientesListItems,
                SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);//Observer para el refresco

        //Seteando el adaptador creado
        campoEsCliente.setAdapter(mCursorAdapter);

        //Relacionado la escucha
        campoEsCliente.setOnItemSelectedListener(this);

        campoEsCliente.setSelection(1);//SI
    }
    private void prepararSpinnerTipoPersona() {
        Cursor mCursor;
        SimpleCursorAdapter mCursorAdapter;

        //Columnas a consultar
        String[] mProjection =
        {
                TiposPersonas._ID,
                TiposPersonas.CLAVE,
                TiposPersonas.NOMBRE,
        };
        //Criterios de consulta
        String mSelectionClause = null;
        //Parametros
        String[] mSelectionArgs = {};

        String mSortOrder = TiposPersonas.NOMBRE + " ASC";
        mCursor = getContentResolver().query(
                TiposPersonas.URI_CONTENIDO,  // The content URI of the words table
                mProjection,                       // The columns to return for each row
                mSelectionClause,                   // Either null, or the word the user entered
                mSelectionArgs,                    // Either empty, or the string the user entered
                mSortOrder);

        //SE CREA EL ADAPTADOR
        //Columnas que se consultan
        String[] mTiposPersonasListColumns =
                {
                        TiposPersonas.NOMBRE,
                };
        //IDs de la vista donde se colocaran los valores
        int[] mTiposPersonasListItems = {R.id.item_spinner};

        mCursorAdapter = new SimpleCursorAdapter(this,
                R.layout.spinner_personalizado,//Layout simple
                mCursor,//Cursor
                mTiposPersonasListColumns,
                mTiposPersonasListItems,
                SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);//Observer para el refresco

        //Seteando el adaptador creado
        campoTipoPersona.setAdapter(mCursorAdapter);

        //Relacionado la escucha
        campoTipoPersona.setOnItemSelectedListener(this);
    }





    private void locationStart() {
        LocationManager mlocManager = (LocationManager) getSystemService( Context.LOCATION_SERVICE);
        Localizacion Local = new Localizacion();
        Local.setMainActivity(this);
        final boolean gpsEnabled = mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!gpsEnabled) {
            Intent settingsIntent = new Intent( Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(settingsIntent);
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);
            return;
        }
        mlocManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, (LocationListener) Local);
        mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, (LocationListener) Local);

      //  Latitud.setText("");
    //    Longitud.setText("");
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 1000) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                locationStart();
                return;
            }
        }
    }

    public void setLocation(Location loc) {
        //Obtener la direccion de la calle a partir de la latitud y la longitud
        if (loc.getLatitude() != 0.0 && loc.getLongitude() != 0.0) {
            try {
                Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                List<Address> list = geocoder.getFromLocation(
                        loc.getLatitude(), loc.getLongitude(), 1);
                if (!list.isEmpty()) {

                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    /* Aqui empieza la Clase Localizacion */
    public class Localizacion implements LocationListener {
        ActividadInsercionCliente mainActivity;

        public ActividadInsercionCliente getMainActivity() {
            return mainActivity;
        }

        public void setMainActivity(ActividadInsercionCliente mainActivity) {
            this.mainActivity = mainActivity;
        }

        @Override
        public void onLocationChanged(Location loc) {
            // Este metodo se ejecuta cada vez que el GPS recibe nuevas coordenadas
            // debido a la deteccion de un cambio de ubicacion

            loc.getLatitude();
            String la = String.valueOf ( loc.getLatitude () );
            String lo = String.valueOf ( loc.getLongitude () );
            loc.getLongitude();
            StringLatitud = la;
            StringLongitud = lo;


            String Text1 =String.valueOf ( loc.getLatitude()  );
            String Text2 =String.valueOf ( loc.getLongitude()  );
            Latitud.setText(Text1);
            Longitud.setText(Text2);
            this.mainActivity.setLocation(loc);
        }

        @Override
        public void onProviderDisabled(String provider) {
            // Este metodo se ejecuta cuando el GPS es desactivado
            Latitud.setText("GPS Desactivado");
        }

        @Override
        public void onProviderEnabled(String provider) {
            // Este metodo se ejecuta cuando el GPS es activado
            Longitud.setText("GPS Activado");
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            switch (status) {
                case LocationProvider.AVAILABLE:
                    Log.d("debug", "LocationProvider.AVAILABLE");
                    break;
                case LocationProvider.OUT_OF_SERVICE:
                    Log.d("debug", "LocationProvider.OUT_OF_SERVICE");
                    break;
                case LocationProvider.TEMPORARILY_UNAVAILABLE:
                    Log.d("debug", "LocationProvider.TEMPORARILY_UNAVAILABLE");
                    break;
            }
        }
    }
    private void prepararSpinnerTipoContacto() {
        Cursor mCursor;
        SimpleCursorAdapter mCursorAdapter;

        //Columnas a consultar
        String[] mProjection =
                {
                        TiposContactos._ID,
                        TiposContactos.ID,
                        TiposContactos.NOMBRE,
                };
        //Criterios de consulta
        String mSelectionClause = null;
        //Parametros
        String[] mSelectionArgs = {};

        String mSortOrder = TiposContactos.NOMBRE + " ASC";
        mCursor = getContentResolver().query(
                TiposContactos.URI_CONTENIDO,  // The content URI of the words table
                mProjection,                       // The columns to return for each row
                mSelectionClause,                   // Either null, or the word the user entered
                mSelectionArgs,                    // Either empty, or the string the user entered
                mSortOrder);

        //SE CREA EL ADAPTADOR
        //Columnas que se consultan
        String[] mTiposContactosListColumns =
                {
                        TiposContactos.NOMBRE,
                };
        //IDs de la vista donde se colocaran los valores
        int[] mTiposContactosListItems = {R.id.item_spinner};

        mCursorAdapter = new SimpleCursorAdapter(this,
                R.layout.spinner_personalizado,//Layout simple
                mCursor,//Cursor
                mTiposContactosListColumns,
                mTiposContactosListItems,
                SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);//Observer para el refresco

        //Seteando el adaptador creado
        campoRelacionContacto.setAdapter(mCursorAdapter);

        //Relacionado la escucha
        campoRelacionContacto.setOnItemSelectedListener(this);
    }

    private void prepararSpinnerEstadoCivil() {
        Cursor mCursor;
        SimpleCursorAdapter mCursorAdapter;

        //Columnas a consultar
        String[] mProjection =
                {
                        EstadosCiviles._ID,
                        EstadosCiviles.ID,
                        EstadosCiviles.ESTADO_CIVIL,
                };
        //Criterios de consulta
        String mSelectionClause = null;
        //Parametros
        String[] mSelectionArgs = {};

        String mSortOrder = EstadosCiviles.ESTADO_CIVIL + " ASC";
        mCursor = getContentResolver().query(
                EstadosCiviles.URI_CONTENIDO,  // The content URI of the words table
                mProjection,                       // The columns to return for each row
                mSelectionClause,                   // Either null, or the word the user entered
                mSelectionArgs,                    // Either empty, or the string the user entered
                mSortOrder);

        //SE CREA EL ADAPTADOR
        //Columnas que se consultan
        String[] mEstadosCivilesListColumns =
                {
                        EstadosCiviles.ESTADO_CIVIL,
                };
        //IDs de la vista donde se colocaran los valores
        int[] mEstadosCivilesListItems = {R.id.item_spinner};

        mCursorAdapter = new SimpleCursorAdapter(this,
                R.layout.spinner_personalizado,//Layout simple
                mCursor,//Cursor
                mEstadosCivilesListColumns,
                mEstadosCivilesListItems,
                SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);//Observer para el refresco

        //Seteando el adaptador creado
        campoEstadoCivil.setAdapter(mCursorAdapter);

        //Relacionado la escucha
        campoEstadoCivil.setOnItemSelectedListener(this);
    }

    private void prepararSpinnerCategoriaActividadEconomica() {
        Cursor mCursor;
        SimpleCursorAdapter mCursorAdapter;

        //Columnas a consultar
        String[] mProjection =
                {
                        CategoriasActividadesEconomicas._ID,
                        CategoriasActividadesEconomicas.ID,
                        CategoriasActividadesEconomicas.NOMBRE,
                };
        //Criterios de consulta
        String mSelectionClause = null;
        //Parametros
        String[] mSelectionArgs = {};

        String mSortOrder = CategoriasActividadesEconomicas.NOMBRE + " ASC";
        mCursor = getContentResolver().query(
                CategoriasActividadesEconomicas.URI_CONTENIDO,  // The content URI of the words table
                mProjection,                       // The columns to return for each row
                mSelectionClause,                   // Either null, or the word the user entered
                mSelectionArgs,                    // Either empty, or the string the user entered
                mSortOrder);

        //SE CREA EL ADAPTADOR
        //Columnas que se consultan
        String[] mCategoriasActividadesEconomicasListColumns =
                {
                        CategoriasActividadesEconomicas.NOMBRE,
                };
        //IDs de la vista donde se colocaran los valores
        int[] mCategoriasActividadesEconomicasListItems = {R.id.item_spinner};

        mCursorAdapter = new SimpleCursorAdapter(this,
                R.layout.spinner_personalizado,//Layout simple
                mCursor,//Cursor
                mCategoriasActividadesEconomicasListColumns,
                mCategoriasActividadesEconomicasListItems,
                SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);//Observer para el refresco

        //Seteando el adaptador creado
        campoCategoriaActividadEconomica.setAdapter(mCursorAdapter);
    }

    private void prepararSpinnerActividadEconomica() {
        Cursor mCursor;
        SimpleCursorAdapter mCursorAdapter;

        //Filtro
        Cursor cCategoriaActividadEconomica=(Cursor) campoCategoriaActividadEconomica.getItemAtPosition(campoCategoriaActividadEconomica.getSelectedItemPosition());
        String sCategoriaActividadEconomica="";
        if(cCategoriaActividadEconomica!=null){
            sCategoriaActividadEconomica = cCategoriaActividadEconomica.getString(
                    cCategoriaActividadEconomica.getColumnIndex(CategoriasActividadesEconomicas.ID));
        }


        //Columnas a consultar
        String[] mProjection =
                {
                        ActividadesEconomicas._ID,
                        ActividadesEconomicas.ID,
                        ActividadesEconomicas.DESCRIPCION,
                };
        //Criterios de consulta
        String mSelectionClause = null;
        if(sCategoriaActividadEconomica.isEmpty()){
            mSelectionClause = null;
        }else{
            mSelectionClause = "id_categoria=" + sCategoriaActividadEconomica;
        }

        //Parametros
        String[] mSelectionArgs = {};

        String mSortOrder = ActividadesEconomicas.DESCRIPCION + " ASC";
        mCursor = getContentResolver().query(
                ActividadesEconomicas.URI_CONTENIDO,  // The content URI of the words table
                mProjection,                       // The columns to return for each row
                mSelectionClause,                   // Either null, or the word the user entered
                mSelectionArgs,                    // Either empty, or the string the user entered
                mSortOrder);

        //SE CREA EL ADAPTADOR
        //Columnas que se consultan
        String[] mActividadesEconomicasListColumns =
                {
                        ActividadesEconomicas.DESCRIPCION,
                };
        //IDs de la vista donde se colocaran los valores
        int[] mActividadesEconomicasListItems = {R.id.item_spinner};

        mCursorAdapter = new SimpleCursorAdapter(this,
                R.layout.spinner_personalizado,//Layout simple
                mCursor,//Cursor
                mActividadesEconomicasListColumns,
                mActividadesEconomicasListItems,
                SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);//Observer para el refresco

        //Seteando el adaptador creado
        campoActividadEconomica.setAdapter(mCursorAdapter);

        //Relacionado la escucha
        campoActividadEconomica.setOnItemSelectedListener(this);
    }

    private void prepararSpinnerPais() {
        Cursor mCursor;
        SimpleCursorAdapter mCursorAdapter;

        //Columnas a consultar
        String[] mProjection =
                {
                        Paises._ID,
                        Paises.ID,
                        Paises.PREDETERMINADO,
                        Paises.PAIS,
                };
        //Criterios de consulta
        String mSelectionClause = null;
        //Parametros
        String[] mSelectionArgs = {};

        String mSortOrder = Paises.PAIS + " ASC";
        mCursor = getContentResolver().query(
                Paises.URI_CONTENIDO,  // The content URI of the words table
                mProjection,                       // The columns to return for each row
                mSelectionClause,                   // Either null, or the word the user entered
                mSelectionArgs,                    // Either empty, or the string the user entered
                mSortOrder);

        //SE CREA EL ADAPTADOR
        //Columnas que se consultan
        String[] mPaisesListColumns =
                {
                        Paises.PAIS,
                };
        //IDs de la vista donde se colocaran los valores
        int[] mPaisesListItems = {R.id.item_spinner};

        mCursorAdapter = new SimpleCursorAdapter(this,
                R.layout.spinner_personalizado,//Layout simple
                mCursor,//Cursor
                mPaisesListColumns,
                mPaisesListItems,
                SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);//Observer para el refresco

        //Seteando el adaptador creado
        comboPais.setAdapter(mCursorAdapter);
    }

    private void prepararSpinnerEstado() {
        Cursor mCursor;
        SimpleCursorAdapter mCursorAdapter;

        //Filtro
        Cursor cPais=(Cursor) comboPais.getItemAtPosition(comboPais.getSelectedItemPosition());
        String sPais="";
        if(cPais!=null){
            sPais = cPais.getString(
                    cPais.getColumnIndex(Paises.ID));
        }

        //Columnas a consultar
        String[] mProjection =
                {
                        Estados._ID,
                        Estados.ID,
                        Estados.ESTADO,
                };
        //Criterios de consulta
        String mSelectionClause = null;
        if(sPais.isEmpty()){
            mSelectionClause = null;
        }else{
            mSelectionClause = "id_pais=" + sPais;
        }

        //Parametros
        String[] mSelectionArgs = {};

        String mSortOrder = Estados.ESTADO + " ASC";
        mCursor = getContentResolver().query(
                Estados.URI_CONTENIDO,  // The content URI of the words table
                mProjection,                       // The columns to return for each row
                mSelectionClause,                   // Either null, or the word the user entered
                mSelectionArgs,                    // Either empty, or the string the user entered
                mSortOrder);

        //SE CREA EL ADAPTADOR
        //Columnas que se consultan
        String[] mEstadosListColumns =
                {
                        Estados.ESTADO,
                };
        //IDs de la vista donde se colocaran los valores
        int[] mEstadosListItems = {R.id.item_spinner};

        mCursorAdapter = new SimpleCursorAdapter(this,
                R.layout.spinner_personalizado,//Layout simple
                mCursor,//Cursor
                mEstadosListColumns,
                mEstadosListItems,
                SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);//Observer para el refresco

        //Seteando el adaptador creado
        campoEstado.setAdapter(mCursorAdapter);
    }

    private void prepararSpinnerMunicipio() {
        Cursor mCursor;
        SimpleCursorAdapter mCursorAdapter;

        //Filtro
        Cursor cEstado=(Cursor) campoEstado.getItemAtPosition(campoEstado.getSelectedItemPosition());
        String sEstado="";
        if(cEstado!=null){
            sEstado = cEstado.getString(
                    cEstado.getColumnIndex(Estados.ID));
        }

        //Columnas a consultar
        String[] mProjection =
                {
                        Municipios._ID,
                        Municipios.ID,
                        Municipios.MUNICIPIO,
                };
        //Criterios de consulta
        String mSelectionClause = null;
        if(sEstado.isEmpty()){
            mSelectionClause = null;
        }else{
            mSelectionClause = "id_estado=" + sEstado;
        }

        //Parametros
        String[] mSelectionArgs = {};

        String mSortOrder = Municipios.MUNICIPIO + " ASC";
        mCursor = getContentResolver().query(
                Municipios.URI_CONTENIDO,  // The content URI of the words table
                mProjection,                       // The columns to return for each row
                mSelectionClause,                   // Either null, or the word the user entered
                mSelectionArgs,                    // Either empty, or the string the user entered
                mSortOrder);

        //SE CREA EL ADAPTADOR
        //Columnas que se consultan
        String[] mMunicipiosListColumns =
                {
                        Municipios.MUNICIPIO,
                };
        //IDs de la vista donde se colocaran los valores
        int[] mMunicipiosListItems = {R.id.item_spinner};

        mCursorAdapter = new SimpleCursorAdapter(this,
                R.layout.spinner_personalizado,//Layout simple
                mCursor,//Cursor
                mMunicipiosListColumns,
                mMunicipiosListItems,
                SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);//Observer para el refresco

        //Seteando el adaptador creado
        campoMunicipio.setAdapter(mCursorAdapter);

        //Relacionado la escucha
        campoMunicipio.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        ViewGroup.LayoutParams parametrosLayout;
        ViewGroup.LayoutParams parametrosLabel;

        /*
        Obteniendo el id del Spinner que recibió el evento
         */
        if (parent.getId ()==R.id.campo_es_cliente)
        {
            String text3 = parent.getItemAtPosition ( position ).toString ();
            //  Toast.makeText ( parent.getContext (), "campo persona "+text2, Toast.LENGTH_SHORT ).show ();
            GEsCliente = text3;
            if (GEsCliente.equals ( "SI" )){
                Integer numero = 1;
                Escli = numero;
                Toast.makeText ( ActividadInsercionCliente.this, GEsCliente + " - " + Escli, Toast.LENGTH_SHORT ).show ();

            }else{
                Integer numer1 = 0;
                Escli = numer1;
                Toast.makeText ( ActividadInsercionCliente.this, GEsCliente + " - " + Escli, Toast.LENGTH_SHORT ).show ();
            }

        }
        if (parent.getId () == R.id.campo_tipo_persona) {
            String text2 = parent.getItemAtPosition ( position ).toString ();
            //  Toast.makeText ( parent.getContext (), "campo persona "+text2, Toast.LENGTH_SHORT ).show ();
            Tinombre1 = text2;

            Toast.makeText ( ActividadInsercionCliente.this, Tinombre1, Toast.LENGTH_SHORT ).show ();

            if (Tinombre1.equals ( "Persona Fisica" )) {
                labelNombre1.setText ( "Primer Nombre" );
                labelFechaNacimiento.setText ( "Fecha Nacimiento" );
                labelOcupacion.setText ( "Ocupación" );
            } else if (Tinombre1.equals ( "Persona Moral" )) {
                labelNombre1.setText ( "Denominación" );
                labelFechaNacimiento.setText ( "Fecha Constitución" );
                labelOcupacion.setText ( "Giro" );
            }

            mascaraRazonSocial.setVisibility(View.GONE);
            mascaraNombre1.setVisibility(View.GONE);
            mascaraNombre2.setVisibility(View.GONE);
            mascaraApellidoPaterno.setVisibility(View.GONE);
            mascaraApellidoMaterno.setVisibility(View.GONE);
            mascaraCurp.setVisibility(View.GONE);
            mascaraRfc.setVisibility(View.GONE);
            mascaraIne.setVisibility(View.GONE);
            mascaraFechaNacimiento.setVisibility(View.GONE);
            mascaraEstadoCivil.setVisibility(View.GONE);
            mascaraCategoriaActividadEconomica.setVisibility(View.GONE);
            mascaraActividadEconomica.setVisibility(View.GONE);
            mascaraContacto.setVisibility(View.GONE);
            mascaraRelacionContacto.setVisibility(View.GONE);
            mascaraTelefono.setVisibility(View.GONE);
            mascaraCelular.setVisibility(View.GONE);
            mascaraCorreo.setVisibility(View.GONE);
            //---------------------------------
            labelRazonSocial.setVisibility(View.GONE);
            labelNombre1.setVisibility(View.GONE);
            labelNombre2.setVisibility(View.GONE);
            labelApellidoPaterno.setVisibility(View.GONE);
            labelApellidoMaterno.setVisibility(View.GONE);
            labelCurp.setVisibility(View.GONE);
            labelRfc.setVisibility(View.GONE);
            labelIne.setVisibility(View.GONE);
            labelFechaNacimiento.setVisibility(View.GONE);
            labelEstadoCivil.setVisibility(View.GONE);
            labelCategoriaActividadEconomica.setVisibility(View.GONE);
            labelActividadEconomica.setVisibility(View.GONE);
            labelContacto.setVisibility(View.GONE);
            labelRelacionContacto.setVisibility(View.GONE);
            labelTelefono.setVisibility(View.GONE);
            labelCelular.setVisibility(View.GONE);
            labelCorreo.setVisibility(View.GONE);

            parametrosLayout = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            parametrosLabel = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );

            if(GEsCliente.equals("SI") && Tinombre1.equals("Persona Fisica")){
                mascaraNombre1.setVisibility(View.VISIBLE);
                mascaraNombre2.setVisibility(View.VISIBLE);
                mascaraApellidoPaterno.setVisibility(View.VISIBLE);
                mascaraApellidoMaterno.setVisibility(View.VISIBLE);
                mascaraCurp.setVisibility(View.VISIBLE);
                mascaraRfc.setVisibility(View.VISIBLE);
                mascaraIne.setVisibility(View.VISIBLE);
                mascaraFechaNacimiento.setVisibility(View.VISIBLE);
                mascaraEstadoCivil.setVisibility(View.VISIBLE);
                mascaraCategoriaActividadEconomica.setVisibility(View.VISIBLE);
                mascaraActividadEconomica.setVisibility(View.VISIBLE);
                mascaraTelefono.setVisibility(View.VISIBLE);
                mascaraCelular.setVisibility(View.VISIBLE);
                mascaraCorreo.setVisibility(View.VISIBLE);

                labelNombre1.setVisibility(View.VISIBLE);
                labelNombre2.setVisibility(View.VISIBLE);
                labelApellidoPaterno.setVisibility(View.VISIBLE);
                labelApellidoMaterno.setVisibility(View.VISIBLE);
                labelCurp.setVisibility(View.VISIBLE);
                labelRfc.setVisibility(View.VISIBLE);
                labelIne.setVisibility(View.VISIBLE);
                labelFechaNacimiento.setVisibility(View.VISIBLE);
                labelEstadoCivil.setVisibility(View.VISIBLE);
                labelCategoriaActividadEconomica.setVisibility(View.VISIBLE);
                labelActividadEconomica.setVisibility(View.VISIBLE);
                labelTelefono.setVisibility(View.VISIBLE);
                labelCelular.setVisibility(View.VISIBLE);
                labelCorreo.setVisibility(View.VISIBLE);
            }else if(GEsCliente.equals("SI") && Tinombre1.equals("Persona Moral")){
                mascaraRazonSocial.setVisibility(View.VISIBLE);
                mascaraNombre1.setVisibility(View.VISIBLE);
                mascaraRfc.setVisibility(View.VISIBLE);
                mascaraFechaNacimiento.setVisibility(View.VISIBLE);
                mascaraCategoriaActividadEconomica.setVisibility(View.VISIBLE);
                mascaraActividadEconomica.setVisibility(View.VISIBLE);
                mascaraContacto.setVisibility(View.VISIBLE);
                mascaraRelacionContacto.setVisibility(View.VISIBLE);
                mascaraTelefono.setVisibility(View.VISIBLE);
                mascaraCelular.setVisibility(View.VISIBLE);
                mascaraCorreo.setVisibility(View.VISIBLE);

                labelRazonSocial.setVisibility(View.VISIBLE);
                labelNombre1.setVisibility(View.VISIBLE);
                labelRfc.setVisibility(View.VISIBLE);
                labelFechaNacimiento.setVisibility(View.VISIBLE);
                labelCategoriaActividadEconomica.setVisibility(View.VISIBLE);
                labelActividadEconomica.setVisibility(View.VISIBLE);
                labelContacto.setVisibility(View.VISIBLE);
                labelRelacionContacto.setVisibility(View.VISIBLE);
                labelTelefono.setVisibility(View.VISIBLE);
                labelCelular.setVisibility(View.VISIBLE);
                labelCorreo.setVisibility(View.VISIBLE);
            }else if(GEsCliente.equals("NO") && Tinombre1.equals("Persona Fisica")){
                mascaraNombre1.setVisibility(View.VISIBLE);
                mascaraNombre2.setVisibility(View.VISIBLE);
                mascaraApellidoPaterno.setVisibility(View.VISIBLE);
                mascaraApellidoMaterno.setVisibility(View.VISIBLE);
                mascaraTelefono.setVisibility(View.VISIBLE);
                mascaraCelular.setVisibility(View.VISIBLE);
                mascaraCorreo.setVisibility(View.VISIBLE);

                labelNombre1.setVisibility(View.VISIBLE);
                labelNombre2.setVisibility(View.VISIBLE);
                labelApellidoPaterno.setVisibility(View.VISIBLE);
                labelApellidoMaterno.setVisibility(View.VISIBLE);
                labelTelefono.setVisibility(View.VISIBLE);
                labelCelular.setVisibility(View.VISIBLE);
                labelCorreo.setVisibility(View.VISIBLE);
            }else if(GEsCliente.equals("NO") && Tinombre1.equals("Persona Moral")){
                mascaraRazonSocial.setVisibility(View.VISIBLE);
                mascaraNombre1.setVisibility(View.VISIBLE);
                mascaraContacto.setVisibility(View.VISIBLE);
                mascaraRelacionContacto.setVisibility(View.VISIBLE);
                mascaraTelefono.setVisibility(View.VISIBLE);
                mascaraCelular.setVisibility(View.VISIBLE);
                mascaraCorreo.setVisibility(View.VISIBLE);

                labelRazonSocial.setVisibility(View.VISIBLE);
                labelNombre1.setVisibility(View.VISIBLE);
                labelContacto.setVisibility(View.VISIBLE);
                labelRelacionContacto.setVisibility(View.VISIBLE);
                labelTelefono.setVisibility(View.VISIBLE);
                labelCelular.setVisibility(View.VISIBLE);
                labelCorreo.setVisibility(View.VISIBLE);
            }


        }if (parent.getId ()==R.id.campo_pais)
        {
            String text4 = parent.getItemAtPosition ( position ).toString ();
            //  Toast.makeText ( parent.getContext (), "campo persona "+text2, Toast.LENGTH_SHORT ).show ();
            paises = text4;
            Toast.makeText ( ActividadInsercionCliente.this, paises, Toast.LENGTH_SHORT ).show ();
        }
        if (parent.getId ()==R.id.campo_estado)
        {
            String text5 = parent.getItemAtPosition ( position ).toString ();
            String textClave = parent.getItemAtPosition ( 2 ).toString ();
            //  Toast.makeText ( parent.getContext (), "campo persona "+text2, Toast.LENGTH_SHORT ).show ();
            estados = text5;
            if (position!=0){
                String numero = (EstadosList.get(position-1).getId().toString());
                //Toast.makeText ( ActividadInsercionCliente.this, numero, Toast.LENGTH_SHORT ).show ();
                clave_estado =  numero;
                ClientesList.ID_ESTADOS = clave_estado;

                consultarListaMunicipios2();
            }

            Toast.makeText ( ActividadInsercionCliente.this, estados, Toast.LENGTH_SHORT ).show ();

        }
        if (parent.getId ()==R.id.campo_municipio)
        {
            String text6 = parent.getItemAtPosition ( position ).toString ();
            //  Toast.makeText ( parent.getContext (), "campo persona "+text2, Toast.LENGTH_SHORT ).show ();
            municipios = text6;
            Toast.makeText ( ActividadInsercionCliente.this, municipios, Toast.LENGTH_SHORT ).show ();
        }
        if (parent.getId ()==R.id.campo_estado_civil)
        {
            String text7 = parent.getItemAtPosition ( position ).toString ();
            //  Toast.makeText ( parent.getContext (), "campo persona "+text2, Toast.LENGTH_SHORT ).show ();
            EstadoCivil = text7;
            Toast.makeText ( ActividadInsercionCliente.this, EstadoCivil, Toast.LENGTH_SHORT ).show ();
        }
        if (parent.getId ()==R.id.campo_relacion_contacto)
        {
            String text8 = parent.getItemAtPosition ( position ).toString ();
            //  Toast.makeText ( parent.getContext (), "campo persona "+text2, Toast.LENGTH_SHORT ).show ();
            Relac = text8;
            Toast.makeText ( ActividadInsercionCliente.this, Relac, Toast.LENGTH_SHORT ).show ();
        }
        if (parent.getId ()==R.id.campo_categoria_actividad_economica)
        {
            String text9 = parent.getItemAtPosition ( position ).toString ();
            //  Toast.makeText ( parent.getContext (), "campo persona "+text2, Toast.LENGTH_SHORT ).show ();
            Act = text9;
            Toast.makeText ( ActividadInsercionCliente.this, Act, Toast.LENGTH_SHORT ).show ();

            if (position!=0){
               String numero = (ActividadEconomicaList.get(position-1).getId().toString());
                Toast.makeText ( ActividadInsercionCliente.this, numero, Toast.LENGTH_SHORT ).show ();
                Activ = Integer.parseInt ( numero );
            }
        }
        if (parent.getId ()==R.id.campo_actividad_economica)
        {
            String text10 = parent.getItemAtPosition ( position ).toString ();
            //  Toast.makeText ( parent.getContext (), "campo persona "+text2, Toast.LENGTH_SHORT ).show ();
            Act2 = text10;
            Toast.makeText ( ActividadInsercionCliente.this, Act2, Toast.LENGTH_SHORT ).show ();

            if (position!=0){
                String numero = (EconomicaList.get(position-1).getId().toString());
                Toast.makeText ( ActividadInsercionCliente.this, numero, Toast.LENGTH_SHORT ).show ();
                Activ1 = Integer.parseInt ( numero );
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
        if (uriCliente != null) {
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
        }
        return super.onOptionsItemSelected(item);
    }


    private void insertar() {
        String correo = campoCorreo.getText().toString().trim();
        String curp = campoCurp.getText().toString().trim();
        String rfc = campoRfc.getText ().toString ().trim ();
        Pattern pat = Pattern.compile("([a-z0-9]+(\\.?[a-z0-9])*)+@(([a-z]+)\\.([a-z]+))+");
        Pattern patPM = Pattern.compile ( "[A-Z]{1}[AEIOU]{1}[A-Z]{2}[0-9]{2}" +
                "(0[1-9]|1[0-2])(0[1-9]|1[0-9]|2[0-9]|3[0-1])" +
                "[HM]{1}" +
                "(AS|BC|BS|CC|CS|CH|CL|CM|DF|DG|GT|GR|HG|JC|MC|MN|MS|NT|NL|OC|PL|QT|QR|SP|SL|SR|TC|TS|TL|VZ|YN|ZS|NE)" +
                "[B-DF-HJ-NP-TV-Z]{3}" +
                "[0-9A-Z]{1}[0-9]{1}$");
        Pattern patternPM = Pattern.compile( "^(([A-ZÑ&]{3})([0-9]{2})([0][13578]|[1][02])(([0][1-9]|[12][\\d])|[3][01])([A-Z0-9]{3}))|" +
                "(([A-ZÑ&]{3})([0-9]{2})([0][13456789]|[1][012])(([0][1-9]|[12][\\d])|[3][0])([A-Z0-9]{3}))|" +
                "(([A-ZÑ&]{3})([02468][048]|[13579][26])[0][2]([0][1-9]|[12][\\d])([A-Z0-9]{3}))|" +
                "(([A-ZÑ&]{3})([0-9]{2})[0][2]([0][1-9]|[1][0-9]|[2][0-8])([A-Z0-9]{3}))$");
        Pattern patternPF = Pattern.compile( "^(([A-ZÑ&]{4})([0-9]{2})([0][13578]|[1][02])(([0][1-9]|[12][\\d])|[3][01])([A-Z0-9]{3}))|" +
                "(([A-ZÑ&]{4})([0-9]{2})([0][13456789]|[1][012])(([0][1-9]|[12][\\d])|[3][0])([A-Z0-9]{3}))|" +
                "(([A-ZÑ&]{4})([02468][048]|[13579][26])[0][2]([0][1-9]|[12][\\d])([A-Z0-9]{3}))|" +
                "(([A-ZÑ&]{4})([0-9]{2})[0][2]([0][1-9]|[1][0-9]|[2][0-8])([A-Z0-9]{3}))$");

    //    Pattern patternCurp = Pattern.compile ( "^([A-Z][AEIOUX][A-Z]{2}\\d{2}(?:0[1-9]|1[0-2])(?:0[1-9]|[12]\\d|3[01])[HM](?:AS|B[CS]|C[CLMSH]|D[FG]|G[TR]|HG|JC|M[CNS]|N[ETL]|OC|PL|Q[TR]|S[PLR]|T[CSL]|VZ|YN|ZS)[B-DF-HJ-NP-TV-Z]{3}[A-Z\\d])(\\d)$" );


        Matcher mather = pat.matcher(correo);
        Matcher matcher2 = patPM.matcher ( curp );
        Matcher matcher3 = patternPF.matcher ( rfc );
        Matcher matcher4 = patternPM.matcher ( rfc );



        //valido que editext no este vacio
        if(campoCorreo.getText().toString().isEmpty()) {
            //valido si el dato ingresado es un correo
            campoCorreo.setError("Campo Obligatorio");
        }
        else{
            if (mather.find ()== true) {
                CorreoP = 1;
                Toast.makeText ( ActividadInsercionCliente.this, "Hola", Toast.LENGTH_SHORT ).show ();
            } else {
                campoCorreo.setError("Correo NO Valido");
            }

        }
        if(campoCurp.getText().toString().isEmpty()) {
            //valido si el dato ingresado es un correo
            campoCurp.setError("Campo Obligatorio");
        }
        else{
            if (matcher2.find ()== true) {
                SQLiteDatabase db=conn.getReadableDatabase();
                String[] parametros={campoCurp.getText().toString()};

                try {
                    //select nombre,telefono from usuario where codigo=?
                    Cursor cursor=db.rawQuery("SELECT * FROM "+ Utilidades.TABLA_OBTENER_CLIENTES+" WHERE "+Utilidades.CAMPO_CURP_CLIENTES + "=? ", parametros);

                    cursor.moveToFirst();
                    campoCurp.setText(cursor.getString(3));
                    CurpP = 1;
                    Toast.makeText(ActividadInsercionCliente.this,"El documento existe",Toast.LENGTH_LONG).show();

                }catch (Exception e){
                    Toast.makeText(ActividadInsercionCliente.this,"El documento no existe",Toast.LENGTH_LONG).show();

                }


            } else {
                campoCurp.setError("Correo NO Valido");
            }

        }





    }

    public void Insercion(){
        // Extraer datos de UI
//        Cursor cEsCliente = (Cursor) campoEsCliente.getSelectedItem();
        String esCliente =String.valueOf ( Escli ) ;
//                cEsCliente.getColumnIndex(EsClientes.ID));
//        Cursor cTipoPersona = (Cursor) campoTipoPersona.getSelectedItem();
        String tipoPersona = Tinombre1;
//                cTipoPersona.getColumnIndex(TiposPersonas.NOMBRE));
        String razonSocial = campoRazonSocial.getText().toString();
        String nombre1 = campoNombre1.getText().toString();
        String nombre2 = campoNombre2.getText().toString();
        String apellidoPaterno = campoApellidoPaterno.getText().toString();
        String apellidoMaterno = campoApellidoMaterno.getText().toString();
        String curp = campoCurp.getText().toString();
        String rfc = campoRfc.getText().toString();
        String ine = campoIne.getText().toString();
        String fechaNacimiento = campoFechaNacimiento.getText().toString();
        //   Cursor cEstadoCivil= (Cursor) campoEstadoCivil.getSelectedItem();
        String estadoCivil = EstadoCivil;
        //           cEstadoCivil.getColumnIndex(EstadosCiviles.ID));
        String ocupacion = campoOcupacion.getText().toString();
        //   Cursor cCategoriaActividadEconomica= (Cursor) campoCategoriaActividadEconomica.getSelectedItem();
        Integer categoriaActividadEconomica = Activ;
        //           cCategoriaActividadEconomica.getColumnIndex(CategoriasActividadesEconomicas.ID));
        //   Cursor cActividadEconomica= (Cursor) campoActividadEconomica.getSelectedItem();
        Integer actividadEconomica = Activ1;
        //           cActividadEconomica.getColumnIndex(ActividadesEconomicas.ID));
        String contacto = campoContacto.getText().toString();
        //   Cursor cRelacionContacto = (Cursor) campoRelacionContacto.getSelectedItem();
        String relacionContacto = Relac;
        //           cRelacionContacto.getColumnIndex(TiposContactos.ID));
        String telefono = campoTelefono.getText().toString();
        String celular = campoCelular.getText().toString();
        String correo = campoCorreo.getText().toString();
//        Cursor cPais = (Cursor) comboPais.getSelectedItem();
        String pais = paises;
//                cPais.getColumnIndex(Paises.PAIS));
//        Cursor cEstado = (Cursor) campoEstado.getSelectedItem();
        String estado = estados;
//                cEstado.getColumnIndex(Estados.ESTADO));
//        Cursor cMunicipio = (Cursor) campoMunicipio.getSelectedItem();
        String municipio = municipios;
//                cMunicipio.getColumnIndex(Municipios.MUNICIPIO));
        String localidad = campoLocalidad.getText().toString();
        String colonia = campoColonia.getText().toString();
        String codigoPostal = campoCodigoPostal.getText().toString();
        String calle = campoCalle.getText().toString();
        String numeroExterior = campoNumeroExterior.getText().toString();
        String numeroInterior = campoNumeroInterior.getText().toString();
        String referencia = campoReferencia.getText().toString();
        String notas = campoNotas.getText().toString();


        // Validaciones y pruebas de cordura
        if (!esNombreValido(nombre1)) {
            TextInputLayout mascaraCampoNombre1 = (TextInputLayout) findViewById(R.id.mascara_campo_nombre1);
            mascaraCampoNombre1.setError("Este campo no puede quedar vacío");

        } else {

            ContentValues valores = new ContentValues();

            // Verificación: ¿Es necesario generar un id?
            if (uriCliente == null) {
                valores.put(Clientes.ID, Clientes.generarId());
            }

            valores.put(Clientes.ES_CLIENTE, esCliente);
            valores.put(Clientes.TIPO_PERSONA, tipoPersona);
            valores.put(Clientes.RAZON_SOCIAL, razonSocial);
            valores.put(Clientes.PRIMER_NOMBRE, nombre1);
            valores.put(Clientes.SEGUNDO_NOMBRE, nombre2);
            valores.put(Clientes.PRIMER_APELLIDO, apellidoPaterno);
            valores.put(Clientes.SEGUNDO_APELLIDO, apellidoMaterno);
            valores.put(Clientes.CURP, curp);
            valores.put(Clientes.RFC, rfc);
            valores.put(Clientes.INE, ine);
            valores.put(Clientes.FECHA_NACIMIENTO, fechaNacimiento);
            valores.put(Clientes.ESTADO_CIVIL, estadoCivil);
            valores.put(Clientes.OCUPACION, ocupacion);
            valores.put(Clientes.ID_CATEGORIA_ACTIVIDAD_ECONOMICA, categoriaActividadEconomica);
            valores.put(Clientes.ID_ACTIVIDAD_ECONOMICA, actividadEconomica);
            valores.put(Clientes.CONTACTO, contacto);
            valores.put(Clientes.RELACION_CONTACTO, relacionContacto);
            valores.put(Clientes.TELEFONO, telefono);
            valores.put(Clientes.CELULAR, celular);
            valores.put(Clientes.CORREO, correo);
            valores.put(Clientes.PAIS, pais);
            valores.put(Clientes.ESTADO, estado);
            valores.put(Clientes.MUNICIPIO, municipio);
            valores.put(Clientes.LOCALIDAD, localidad);
            valores.put(Clientes.COLONIA, colonia);
            valores.put(Clientes.CODIGO_POSTAL, codigoPostal);
            valores.put(Clientes.CALLE, calle);
            valores.put(Clientes.LATITUD, StringLatitud);
            valores.put(Clientes.LONGITUD, StringLongitud);
            valores.put(Clientes.NUMERO_EXTERIOR, numeroExterior);
            valores.put(Clientes.NUMERO_INTERIOR, numeroInterior);
            valores.put(Clientes.REFERENCIA, referencia);
            valores.put(Clientes.NOTAS, notas);
            valores.put(Clientes.VERSION, UTiempo.obtenerTiempo());

            // Iniciar inserción|actualización
            new TareaAnadirCliente(getContentResolver(), valores).execute(uriCliente);
            //   Toast.makeText ( this, StringLatitud, Toast.LENGTH_SHORT ).show ();
            //   Toast.makeText ( this, StringLongitud, Toast.LENGTH_SHORT ).show ();
            finish();
        }
    }

    private boolean esNombreValido(String nombre) {
        return !TextUtils.isEmpty(nombre);
    }

    private void eliminar() {
        if (uriCliente != null) {
            // Iniciar eliminación
            new TareaEliminarCliente(getContentResolver()).execute(uriCliente);
            finish();
        }
    }


    private int obtenerIndiceSpinner(Spinner spinner,String value,String column){
        int i=0;
        int iSeleccion=0;
        String item;

        for (i=0;i<spinner.getCount();i++){
            Cursor cSpinner = (Cursor) spinner.getItemAtPosition(i);
            int columnIndex=cSpinner.getColumnIndex(column);
            if(cSpinner!=null){
                item = cSpinner.getString(columnIndex);
                if(item.equals(value)){
                    iSeleccion=i;
                    break;
                }
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

        campoEsCliente.setSelection(obtenerIndiceSpinner(campoEsCliente,UConsultas.obtenerString(data, Clientes.ES_CLIENTE), EsClientes.ID));
        campoTipoPersona.setSelection(obtenerIndiceSpinner(campoTipoPersona,UConsultas.obtenerString(data, Clientes.TIPO_PERSONA), TiposPersonas.NOMBRE));
        campoRazonSocial.setText(UConsultas.obtenerString(data, Clientes.RAZON_SOCIAL));
        campoNombre1.setText(UConsultas.obtenerString(data, Clientes.PRIMER_NOMBRE));
        campoNombre2.setText(UConsultas.obtenerString(data, Clientes.SEGUNDO_NOMBRE));
        campoApellidoPaterno.setText(UConsultas.obtenerString(data, Clientes.PRIMER_APELLIDO));
        campoApellidoMaterno.setText(UConsultas.obtenerString(data, Clientes.SEGUNDO_APELLIDO));
        campoCurp.setText(UConsultas.obtenerString(data, Clientes.CURP));
        String rfc=UConsultas.obtenerString(data, Clientes.RFC);
        campoRfc.setText(rfc);
        campoIne.setText(UConsultas.obtenerString(data, Clientes.INE));
        campoFechaNacimiento.setText(UConsultas.obtenerString(data, Clientes.FECHA_NACIMIENTO));
        campoEstadoCivil.setSelection(obtenerIndiceSpinner(campoEstadoCivil,UConsultas.obtenerString(data, Clientes.ESTADO_CIVIL), EstadosCiviles.ESTADO_CIVIL));
        campoOcupacion.setText(UConsultas.obtenerString(data, Clientes.OCUPACION));
        //prepararSpinnerCategoriaActividadEconomica();
        campoCategoriaActividadEconomica.setSelection(obtenerIndiceSpinner(campoCategoriaActividadEconomica,UConsultas.obtenerString(data, Clientes.ID_CATEGORIA_ACTIVIDAD_ECONOMICA), CategoriasActividadesEconomicas.ID),false);
        //prepararSpinnerActividadEconomica();
        campoActividadEconomica.setSelection(obtenerIndiceSpinner(campoActividadEconomica,UConsultas.obtenerString(data, Clientes.ID_ACTIVIDAD_ECONOMICA), ActividadesEconomicas.ID));
        campoContacto.setText(UConsultas.obtenerString(data, Clientes.CONTACTO));
        campoRelacionContacto.setSelection(obtenerIndiceSpinner(campoRelacionContacto,UConsultas.obtenerString(data, Clientes.RELACION_CONTACTO), TiposContactos.ID));
        campoTelefono.setText(UConsultas.obtenerString(data, Clientes.TELEFONO));
        campoCelular.setText(UConsultas.obtenerString(data, Clientes.CELULAR));
        campoCorreo.setText(UConsultas.obtenerString(data, Clientes.CORREO));
        //prepararSpinnerPais();
        String sPais=UConsultas.obtenerString(data, Clientes.PAIS);
        int iPais=obtenerIndiceSpinner(comboPais,sPais, Paises.PAIS);
        comboPais.setSelection(iPais,false);
        //prepararSpinnerEstado();
        int iEstado=obtenerIndiceSpinner(campoEstado,UConsultas.obtenerString(data, Clientes.ESTADO), Estados.ESTADO);
        campoEstado.setSelection(iEstado,false);
        //prepararSpinnerMunicipio();
        campoMunicipio.setSelection(obtenerIndiceSpinner(campoMunicipio,UConsultas.obtenerString(data, Clientes.MUNICIPIO), Municipios.MUNICIPIO));
        campoLocalidad.setText(UConsultas.obtenerString(data, Clientes.LOCALIDAD));
        campoColonia.setText(UConsultas.obtenerString(data, Clientes.COLONIA));
        campoCodigoPostal.setText(UConsultas.obtenerString(data, Clientes.CODIGO_POSTAL));
        campoCalle.setText(UConsultas.obtenerString(data, Clientes.CALLE));
        campoNumeroExterior.setText(UConsultas.obtenerString(data, Clientes.NUMERO_EXTERIOR));
        campoNumeroInterior.setText(UConsultas.obtenerString(data, Clientes.NUMERO_INTERIOR));
        campoReferencia.setText(UConsultas.obtenerString(data, Clientes.REFERENCIA));
        campoNotas.setText(UConsultas.obtenerString(data, Clientes.NOTAS));

        campoCategoriaActividadEconomica.setOnItemSelectedListener(this);
        comboPais.setOnItemSelectedListener(this);
        campoEstado.setOnItemSelectedListener(this);
    }

    //private method of your class
    private int getIndex(Spinner spinner, String myString, String column){
        int index=-1;

        return index;
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, uriCliente, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        poblarViews(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }


    static class TareaAnadirCliente extends AsyncTask<Uri, Void, Void> {
        private final ContentResolver resolver;
        private final ContentValues valores;

        public TareaAnadirCliente(ContentResolver resolver, ContentValues valores) {
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
                Cursor c = resolver.query(uri, new String[]{Clientes.INSERTADO}, null, null, null);

                if (c != null && c.moveToNext()) {

                    // Verificación de sincronización
                    if (UConsultas.obtenerInt(c, Clientes.INSERTADO) == 0) {
                        valores.put(Clientes.MODIFICADO, 1);
                    }

                    valores.put(Clientes.VERSION, UTiempo.obtenerTiempo());
                    resolver.update(uri, valores, null, null);
                }

            } else {
                resolver.insert(Clientes.URI_CONTENIDO, valores);
            }
            return null;
        }

    }

    static class TareaEliminarCliente extends AsyncTask<Uri, Void, Void> {
        private final ContentResolver resolver;

        public TareaEliminarCliente(ContentResolver resolver) {
            this.resolver = resolver;
        }

        @Override
        protected Void doInBackground(Uri... args) {

            /*
            Verificación: Si el registro no ha sido sincronizado aún, entonces puede eliminarse
            directamente. De lo contrario se marca como 'eliminado' = 1
             */
            Cursor c = resolver.query(args[0], new String[]{Clientes.INSERTADO}
                    , null, null, null);

            int insertado;

            if (c != null && c.moveToNext()) {
                insertado = UConsultas.obtenerInt(c, Clientes.INSERTADO);
            } else {
                return null;
            }

            if (insertado == 1) {
                resolver.delete(args[0], null, null);
            } else if (insertado == 0) {
                ContentValues valores = new ContentValues();
                valores.put(Clientes.ELIMINADO, 1);
                resolver.update(args[0], valores, null, null);
            }

            return null;
        }
    }
}
