package com.softcredito.app.ui;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
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
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.Spinner;

import com.softcredito.app.R;
import com.softcredito.app.provider.Contract;
import com.softcredito.app.provider.Contract.Pagos;
import com.softcredito.app.provider.Contract.CanalesCobranzas;
import com.softcredito.app.provider.Contract.InstrumentosMonetarios;
import com.softcredito.app.utilidades.UConsultas;
import com.softcredito.app.utilidades.UTiempo;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class ActividadInsercionPago extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor>{

    // Referencias UI
    private EditText campoMonto;
    private Spinner campoCanal;
    private Spinner campoInstrumento;

    private TextInputLayout mascaraMonto;
    private TextInputLayout mascaraCanal;
    private TextInputLayout mascaraInstrumento;

    private GridLayout gridLayout;


    // Clave del uri del cliente como extra
    public static final String URI_PAGO = "extra.uriPago";
    public static final String URI_SOLICITUD = "extra.uriSolicitud";

    private Uri uriPago;
    private Uri uriSolicitud;

    private int loading=0;

    NumberFormat formatter = new DecimalFormat("###,###.00");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actividad_insercion_pago);

        agregarToolbar();

        // Encontrar Referencias UI
        campoMonto = (EditText) findViewById(R.id.campo_monto);
        campoCanal = (Spinner) findViewById(R.id.campo_canal);
        campoInstrumento = (Spinner) findViewById(R.id.campo_instrumento);
        gridLayout=(GridLayout) findViewById(R.id.grid);

        // Obtener el uri de la solicitud
        String uri = getIntent().getStringExtra(URI_SOLICITUD);
        if (uri != null) {
            uriSolicitud= Uri.parse(uri);
        }
        // Determinar si es detalle
        String uri2 = getIntent().getStringExtra(URI_PAGO);
        if (uri2 != null) {
            setTitle(R.string.titulo_actividad_actividad_editar_pago);
            uriPago= Uri.parse(uri2);
            getSupportLoaderManager().restartLoader(1, null, this);
        }

        prepararSpinnerCanal();
        prepararSpinnerInstrumento();
    }

    private void agregarToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void prepararSpinnerCanal() {
        Cursor mCursor;
        SimpleCursorAdapter mCursorAdapter;

        //Columnas a consultar
        String[] mProjection =
                {
                        CanalesCobranzas._ID,
                        CanalesCobranzas.ID,
                        CanalesCobranzas.CLAVE,
                        CanalesCobranzas.NOMBRE,
                };
        //Criterios de consulta
        String mSelectionClause = null;
        //Parametros
        String[] mSelectionArgs = {};

        String mSortOrder = CanalesCobranzas.NOMBRE + " ASC";
        mCursor = getContentResolver().query(
                CanalesCobranzas.URI_CONTENIDO,  // The content URI of the words table
                mProjection,                       // The columns to return for each row
                mSelectionClause,                   // Either null, or the word the user entered
                mSelectionArgs,                    // Either empty, or the string the user entered
                mSortOrder);

        //SE CREA EL ADAPTADOR
        //Columnas que se consultan
        String[] mCanalesCobranzasListColumns =
                {
                        CanalesCobranzas.NOMBRE,
                };
        //IDs de la vista donde se colocaran los valores
        int[] mCanalesCobranzasListItems = {R.id.item_spinner};

        mCursorAdapter = new SimpleCursorAdapter(this,
                R.layout.spinner_personalizado,//Layout simple
                mCursor,//Cursor
                mCanalesCobranzasListColumns,
                mCanalesCobranzasListItems,
                SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);//Observer para el refresco

        //Seteando el adaptador creado
        campoCanal.setAdapter(mCursorAdapter);
    }
    private void prepararSpinnerInstrumento() {
        Cursor mCursor;
        SimpleCursorAdapter mCursorAdapter;

        //Columnas a consultar
        String[] mProjection =
                {
                        InstrumentosMonetarios._ID,
                        InstrumentosMonetarios.ID,
                        InstrumentosMonetarios.CLAVE,
                        InstrumentosMonetarios.DESCRIPCION,
                };
        //Criterios de consulta
        String mSelectionClause = null;
        //Parametros
        String[] mSelectionArgs = {};

        String mSortOrder = InstrumentosMonetarios.DESCRIPCION + " ASC";
        mCursor = getContentResolver().query(
                InstrumentosMonetarios.URI_CONTENIDO,  // The content URI of the words table
                mProjection,                       // The columns to return for each row
                mSelectionClause,                   // Either null, or the word the user entered
                mSelectionArgs,                    // Either empty, or the string the user entered
                mSortOrder);

        //SE CREA EL ADAPTADOR
        //Columnas que se consultan
        String[] mInstrumentosMonetariosListColumns =
                {
                        InstrumentosMonetarios.DESCRIPCION,
                };
        //IDs de la vista donde se colocaran los valores
        int[] mInstrumentosMonetariosListItems = {R.id.item_spinner};

        mCursorAdapter = new SimpleCursorAdapter(this,
                R.layout.spinner_personalizado,//Layout simple
                mCursor,//Cursor
                mInstrumentosMonetariosListColumns,
                mInstrumentosMonetariosListItems,
                SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);//Observer para el refresco

        //Seteando el adaptador creado
        campoInstrumento.setAdapter(mCursorAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_insercion, menu);

        // Verificación de visibilidad acción eliminar
        if (uriPago != null) {
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
                Intent intent = new Intent(ActividadInsercionPago.this, ActividadListaPagos.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(ActividadListaPagos.URI_SOLICITUD,uriSolicitud.toString());
                startActivity(intent);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void insertar() {

        // Extraer datos de UI
        String idSolicitud= Contract.Solicitudes.obtenerId(uriSolicitud);
        String fecha = UTiempo.obtenerFecha();
        String monto = campoMonto.getText().toString();
        Cursor cCanal = (Cursor) campoCanal.getSelectedItem();
        String canal = cCanal.getString(
                cCanal.getColumnIndex(CanalesCobranzas.ID));
        Cursor cInstrumento = (Cursor) campoInstrumento.getSelectedItem();
        String instrumento = cInstrumento.getString(
                cInstrumento.getColumnIndex(InstrumentosMonetarios.ID));

        monto = monto.replaceAll(",", "");

        // Validaciones y pruebas de cordura
        if (TextUtils.isEmpty(monto) || TextUtils.isEmpty(canal) || TextUtils.isEmpty(instrumento)) {
            if(TextUtils.isEmpty(monto)){
                TextInputLayout mascaraCampoMonto = (TextInputLayout) findViewById(R.id.mascara_campo_monto);
                mascaraCampoMonto.setError("Este campo no puede quedar vacío");
            }
            if(TextUtils.isEmpty(canal)){
                TextInputLayout mascaraCampoCanal = (TextInputLayout) findViewById(R.id.mascara_campo_canal);
                mascaraCanal.setError("Este campo no puede quedar vacío");
            }
            if(TextUtils.isEmpty(instrumento)){
                TextInputLayout mascaraCampoInstrumento = (TextInputLayout) findViewById(R.id.mascara_campo_instrumento);
                mascaraInstrumento.setError("Este campo no puede quedar vacío");
            }
        } else {

            ContentValues valores = new ContentValues();

            // Verificación: ¿Es necesario generar un id?
            if (uriPago == null) {
                valores.put(Pagos.ID, Pagos.generarId());
            }
            valores.put(Pagos.ID_SOLICITUD, idSolicitud);
            valores.put(Pagos.FECHA, fecha);
            valores.put(Pagos.MONTO, monto);
            valores.put(Pagos.ID_CANAL_COBRANZA, canal);
            valores.put(Pagos.ID_INSTRUMENTO_MONETARIO, instrumento);
            valores.put(Pagos.VERSION, UTiempo.obtenerTiempo());

            // Iniciar inserción|actualización
            new TareaAnadirPago(getContentResolver(), valores).execute(uriPago);

            finish();
        }
    }

    private void eliminar() {
        if (uriPago!= null) {
            // Iniciar eliminación
            new TareaEliminarPago(getContentResolver()).execute(uriPago);
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

        Float monto=Float.parseFloat(UConsultas.obtenerString(data, Pagos.MONTO));

        String str_monto = formatter.format(monto);

        campoMonto.setText(str_monto);
        campoCanal.setSelection(obtenerIndiceSpinner(campoCanal,UConsultas.obtenerString(data, Pagos.ID_CANAL_COBRANZA), CanalesCobranzas.ID));
        campoInstrumento.setSelection(obtenerIndiceSpinner(campoInstrumento,UConsultas.obtenerString(data, Pagos.ID_INSTRUMENTO_MONETARIO), InstrumentosMonetarios.ID));
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, uriPago, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        poblarViews(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }


    static class TareaAnadirPago extends AsyncTask<Uri, Void, Void> {
        private final ContentResolver resolver;
        private final ContentValues valores;

        public TareaAnadirPago(ContentResolver resolver, ContentValues valores) {
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
                Cursor c = resolver.query(uri, new String[]{Pagos.INSERTADO}, null, null, null);

                if (c != null && c.moveToNext()) {

                    // Verificación de sincronización
                    if (UConsultas.obtenerInt(c, Pagos.INSERTADO) == 0) {
                        valores.put(Pagos.MODIFICADO, 1);
                    }

                    valores.put(Pagos.VERSION, UTiempo.obtenerTiempo());
                    resolver.update(uri, valores, null, null);
                }

            } else {
                resolver.insert(Pagos.URI_CONTENIDO, valores);
            }
            return null;
        }

    }

    static class TareaEliminarPago extends AsyncTask<Uri, Void, Void> {
        private final ContentResolver resolver;

        public TareaEliminarPago(ContentResolver resolver) {
            this.resolver = resolver;
        }

        @Override
        protected Void doInBackground(Uri... args) {

            /*
            Verificación: Si el registro no ha sido sincronizado aún, entonces puede eliminarse
            directamente. De lo contrario se marca como 'eliminado' = 1
             */
            Cursor c = resolver.query(args[0], new String[]{Pagos.INSERTADO}
                    , null, null, null);

            int insertado;

            if (c != null && c.moveToNext()) {
                insertado = UConsultas.obtenerInt(c, Pagos.INSERTADO);
                insertado = UConsultas.obtenerInt(c, Pagos.INSERTADO);
                insertado = UConsultas.obtenerInt(c, Pagos.INSERTADO);
                insertado = UConsultas.obtenerInt(c, Pagos.INSERTADO);
            } else {
                return null;
            }

            if (insertado == 1) {
                resolver.delete(args[0], null, null);
            } else if (insertado == 0) {
                ContentValues valores = new ContentValues();
                valores.put(Pagos.ELIMINADO, 1);
                resolver.update(args[0], valores, null, null);
            }

            return null;
        }
    }
}
