package com.softcredito.app.ui;

import android.app.DatePickerDialog;
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
//import android.support.v7.app.AppCompatActivity;
//import android.support.v7.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.Spinner;

import com.softcredito.app.R;
import com.softcredito.app.inputs.DatePickerFragment;
import com.softcredito.app.provider.Contract;
import com.softcredito.app.provider.Contract.BitacorasCredito;
import com.softcredito.app.utilidades.UConsultas;
import com.softcredito.app.utilidades.UTiempo;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class ActividadInsercionBitacoraCredito extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor>{

    // Referencias UI
    private EditText campoAsunto;
    private EditText campoNumeroAmortizacion;
    private EditText campoDetallesPago;
    private EditText campoDescripcion;
    private EditText campoValorGarantia;
    private EditText campoDescripcionGarantia;

    private TextInputLayout mascaraAsunto;
    private TextInputLayout mascaraNumeroAmortizacion;
    private TextInputLayout mascaraDetallesPago;
    private TextInputLayout mascaraDescripcion;
    private TextInputLayout mascaraValorGarantia;
    private TextInputLayout mascaraDescripcionGarantia;

    private GridLayout gridLayout;


    // Clave del uri del cliente como extra
    public static final String URI_BITACORA_CREDITO = "extra.uriBitacoraCredito";
    public static final String URI_SOLICITUD = "extra.uriSolicitud";

    private Uri uriBitacoraCredito;
    private Uri uriSolicitud;

    private int loading=0;

    NumberFormat formatter = new DecimalFormat("###,###.00");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actividad_insercion_bitacora_credito);

        agregarToolbar();

        // Encontrar Referencias UI
        campoAsunto = (EditText) findViewById(R.id.campo_asunto);
        campoNumeroAmortizacion = (EditText) findViewById(R.id.campo_numero_amortizacion);
        campoDetallesPago = (EditText) findViewById(R.id.campo_detalles_pago);
        campoDescripcion = (EditText) findViewById(R.id.campo_descripcion);
        campoValorGarantia = (EditText) findViewById(R.id.campo_valor_garantia);
        campoDescripcionGarantia = (EditText) findViewById(R.id.campo_descripcion_garantia);
        gridLayout=(GridLayout) findViewById(R.id.grid);

        // Obtener el uri de la solicitud
        String uri = getIntent().getStringExtra(URI_SOLICITUD);
        if (uri != null) {
            uriSolicitud= Uri.parse(uri);
        }
        // Determinar si es detalle
        String uri2 = getIntent().getStringExtra(URI_BITACORA_CREDITO);
        if (uri2 != null) {
            setTitle(R.string.titulo_actividad_editar_bitacora_credito);
            uriBitacoraCredito= Uri.parse(uri2);
            getSupportLoaderManager().restartLoader(1, null, this);
        }
    }

    private void agregarToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_insercion, menu);

        // Verificación de visibilidad acción eliminar
        if (uriBitacoraCredito != null) {
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
                Intent intent = new Intent(ActividadInsercionBitacoraCredito.this, ActividadListaBitacorasCredito.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(ActividadListaBitacorasCredito.URI_SOLICITUD,uriSolicitud.toString());
                startActivity(intent);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void insertar() {

        // Extraer datos de UI
        String idSolicitud= Contract.Solicitudes.obtenerId(uriSolicitud);
        String asunto = campoAsunto.getText().toString();
        String fecha = UTiempo.obtenerFecha();
        String hora = UTiempo.obtenerHora();
        String numero_amortizacion = campoNumeroAmortizacion.getText().toString();
        String detalles_pago = campoDetallesPago.getText().toString();
        String descripcion = campoDescripcion.getText().toString();
        String valor_garantia = campoValorGarantia.getText().toString();
        String descripcion_garantia = campoDescripcionGarantia.getText().toString();

        valor_garantia = valor_garantia.replaceAll(",", "");

        // Validaciones y pruebas de cordura
        if (TextUtils.isEmpty(asunto)  || TextUtils.isEmpty(descripcion) || TextUtils.isEmpty(valor_garantia) || TextUtils.isEmpty(descripcion_garantia)) {
            if(TextUtils.isEmpty(asunto)){
                TextInputLayout mascaraCampoAsunto = (TextInputLayout) findViewById(R.id.mascara_campo_asunto);
                mascaraCampoAsunto.setError("Este campo no puede quedar vacío");
            }
            if(TextUtils.isEmpty(descripcion)){
                TextInputLayout mascaraCampoDescripcion = (TextInputLayout) findViewById(R.id.mascara_campo_descripcion);
                mascaraCampoDescripcion.setError("Este campo no puede quedar vacío");
            }
            if(TextUtils.isEmpty(valor_garantia)){
                TextInputLayout mascaraCampoValorGarantia = (TextInputLayout) findViewById(R.id.mascara_campo_valor_garantia);
                mascaraCampoValorGarantia.setError("Este campo no puede quedar vacío");
            }
            if(TextUtils.isEmpty(descripcion_garantia)){
                TextInputLayout mascaraCampoDescripcionGarantia = (TextInputLayout) findViewById(R.id.mascara_campo_descripcion_garantia);
                mascaraCampoDescripcionGarantia.setError("Este campo no puede quedar vacío");
            }
        } else {

            ContentValues valores = new ContentValues();

            // Verificación: ¿Es necesario generar un id?
            if (uriBitacoraCredito == null) {
                valores.put(BitacorasCredito.ID, BitacorasCredito.generarId());
            }
            valores.put(BitacorasCredito.ID_SOLICITUD, idSolicitud);
            valores.put(BitacorasCredito.ASUNTO, asunto);
            valores.put(BitacorasCredito.FECHA, fecha);
            valores.put(BitacorasCredito.HORA, hora);
            valores.put(BitacorasCredito.NUMERO_AMORTIZACION, numero_amortizacion);
            valores.put(BitacorasCredito.DETALLES_PAGO, detalles_pago);
            valores.put(BitacorasCredito.DESCRIPCION, descripcion);
            valores.put(BitacorasCredito.VALOR_GARANTIA, valor_garantia);
            valores.put(BitacorasCredito.DESCRIPCION_GARANTIA, descripcion_garantia);
            valores.put(BitacorasCredito.VERSION, UTiempo.obtenerTiempo());

            // Iniciar inserción|actualización
            new TareaAnadirBitacoraCredito(getContentResolver(), valores).execute(uriBitacoraCredito);

            finish();
        }
    }

    private void eliminar() {
        if (uriBitacoraCredito!= null) {
            // Iniciar eliminación
            new TareaEliminarBitacoraCredito(getContentResolver()).execute(uriBitacoraCredito);
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

        Float valor_garantia=Float.parseFloat(UConsultas.obtenerString(data, BitacorasCredito.VALOR_GARANTIA));

        String str_valor_garantia = formatter.format(valor_garantia);

        campoAsunto.setText(UConsultas.obtenerString(data, BitacorasCredito.ASUNTO));
        campoNumeroAmortizacion.setText(UConsultas.obtenerString(data, BitacorasCredito.NUMERO_AMORTIZACION));
        campoDetallesPago.setText(UConsultas.obtenerString(data, BitacorasCredito.DETALLES_PAGO));
        campoDescripcion.setText(UConsultas.obtenerString(data, BitacorasCredito.DESCRIPCION));
        campoValorGarantia.setText(str_valor_garantia);
        campoDescripcionGarantia.setText(UConsultas.obtenerString(data, BitacorasCredito.DESCRIPCION_GARANTIA));
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
        return new CursorLoader(this, uriBitacoraCredito, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        poblarViews(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }


    static class TareaAnadirBitacoraCredito extends AsyncTask<Uri, Void, Void> {
        private final ContentResolver resolver;
        private final ContentValues valores;

        public TareaAnadirBitacoraCredito(ContentResolver resolver, ContentValues valores) {
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
                Cursor c = resolver.query(uri, new String[]{BitacorasCredito.INSERTADO}, null, null, null);

                if (c != null && c.moveToNext()) {

                    // Verificación de sincronización
                    if (UConsultas.obtenerInt(c, BitacorasCredito.INSERTADO) == 0) {
                        valores.put(BitacorasCredito.MODIFICADO, 1);
                    }

                    valores.put(BitacorasCredito.VERSION, UTiempo.obtenerTiempo());
                    resolver.update(uri, valores, null, null);
                }

            } else {
                resolver.insert(BitacorasCredito.URI_CONTENIDO, valores);
            }
            return null;
        }

    }

    static class TareaEliminarBitacoraCredito extends AsyncTask<Uri, Void, Void> {
        private final ContentResolver resolver;

        public TareaEliminarBitacoraCredito(ContentResolver resolver) {
            this.resolver = resolver;
        }

        @Override
        protected Void doInBackground(Uri... args) {

            /*
            Verificación: Si el registro no ha sido sincronizado aún, entonces puede eliminarse
            directamente. De lo contrario se marca como 'eliminado' = 1
             */
            Cursor c = resolver.query(args[0], new String[]{BitacorasCredito.INSERTADO}
                    , null, null, null);

            int insertado;

            if (c != null && c.moveToNext()) {
                insertado = UConsultas.obtenerInt(c, BitacorasCredito.INSERTADO);
            } else {
                return null;
            }

            if (insertado == 1) {
                resolver.delete(args[0], null, null);
            } else if (insertado == 0) {
                ContentValues valores = new ContentValues();
                valores.put(BitacorasCredito.ELIMINADO, 1);
                resolver.update(args[0], valores, null, null);
            }

            return null;
        }
    }
}
