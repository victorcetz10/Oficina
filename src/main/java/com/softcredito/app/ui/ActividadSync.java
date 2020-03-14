package com.softcredito.app.ui;

import android.Manifest;
import android.accounts.Account;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
//import android.support.design.widget.Snackbar;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AlertDialog;

import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.softcredito.app.R;
import com.softcredito.app.provider.Contract;
import com.softcredito.app.sync.SyncAdapter;
import com.softcredito.app.utilidades.UCuentas;
import com.softcredito.app.utilidades.UPreferencias;
import com.softcredito.app.utilidades.UWeb;

public class ActividadSync extends Actividad
        implements View.OnClickListener{

    private static final String TAG = ActividadSync.class.getSimpleName();
    private ProgressDialog progress;

    private TextView tvTipoPersona;
    private ProgressBar proTipoPersona;
    private ImageView imagevTipoPersona;

    private TextView tvTipoDocumento;
    private ProgressBar proTipoDocumento;
    private ImageView imagevTipoDocumento;

    private TextView tvTipoPago;
    private ProgressBar proTipoPago;
    private ImageView imagevTipoPago;

    private TextView tvTipoAmortizacion;
    private ProgressBar proTipoAmortizacion;
    private ImageView imagevTipoAmortizacion;

    private TextView tvInstrumentoMonetario;
    private ProgressBar proInstrumentoMonetario;
    private ImageView imagevInstrumentoMonetario;

    private TextView tvCodigoPostal;
    private ProgressBar proCodigoPostal;
    private ImageView imagevCodigoPostal;

    private TextView tvCodigo;
    private ProgressBar proCodigo;
    private ImageView imagevCodigo;

    private TextView tvPais;
    private ProgressBar proPais;
    private ImageView imagevPais;

    private TextView tvEstado;
    private ProgressBar proEstado;
    private ImageView imagevEstado;

    private TextView tvMunicipio;
    private ProgressBar proMunicipio;
    private ImageView imagevMunicipio;

    private TextView tvEstadoCivil;
    private ProgressBar proEstadoCivil;
    private ImageView imagevEstadoCivil;

    private TextView tvCategoriaActividadEconomica;
    private ProgressBar proCategoriaActividadEconomica;
    private ImageView imagevCategoriaActividadEconomica;

    private TextView tvActividadEconomica;
    private ProgressBar proActividadEconomica;
    private ImageView imagevActividadEconomica;

    //------------------

    private TextView tvGrupoCliente;
    private ProgressBar proGrupoCliente;
    private ImageView imagevGrupoCliente;

    private TextView tvRelacionContacto;
    private ProgressBar proRelacionContacto;
    private ImageView imagevRelacionContacto;

    private TextView tvDocumentoRequerido;
    private ProgressBar proDocumentoRequerido;
    private ImageView imagevDocumentoRequerido;

    private TextView tvProducto;
    private ProgressBar proProducto;
    private ImageView imagevProducto;

    private TextView tvBanco;
    private ProgressBar proBanco;
    private ImageView imagevBanco;

    private TextView tvCanalCobranza;
    private ProgressBar proCanalCobranza;
    private ImageView imagevCanalCobranza;

    //---------------------

    private TextView tvCliente;
    private ProgressBar proCliente;
    private ImageView imagevCliente;

    private TextView tvDocumentoEntregado;
    private ProgressBar proDocumentoEntregado;
    private ImageView imagevDocumentoEntregado;

    private TextView tvArchivoDocumentoEntregado;
    private ProgressBar proArchivoDocumentoEntregado;
    private ImageView imagevArchivoDocumentoEntregado;

    private TextView tvCotizador;
    private ProgressBar proCotizador;
    private ImageView imagevCotizador;

    private TextView tvSolicitud;
    private ProgressBar proSolicitud;
    private ImageView imagevSolicitud;

    private TextView tvBitacoraCredito;
    private ProgressBar proBitacoraCredito;
    private ImageView imagevBitacoraCredito;

    private TextView tvArchivoBitacoraCredito;
    private ProgressBar proArchivoBitacoraCredito;
    private ImageView imagevArchivoBitacoraCredito;

    private TextView tvPago;
    private ProgressBar proPago;
    private ImageView imagevPago;

    //--------------------------------

    private String actualizar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tarjeta_sync);
        setTitle(R.string.titulo_actividad_sync);

        agregarToolbar();

        //Se valida si se tienen los permisos para acceder a la camara
        boolean conPermiso=false;
        if (ActivityCompat.checkSelfPermission(ActividadSync.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(ActividadSync.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        /*if (shouldShowRequestPermissionRationale(
                                Manifest.permission.CAMERA)){*/
                if(shouldShowRequestPermissionRationale(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) || shouldShowRequestPermissionRationale(
                        Manifest.permission.READ_EXTERNAL_STORAGE)){
                    //Si ya se le ha pedido anteriormente el permiso se muestra un mensaje explicativo
                    new AlertDialog.Builder(ActividadSync.this)
                            .setTitle("Se requieren permisos")
                            .setMessage("SOFTCREDITO requiere leer y guardar archivos de las bitácoras en el movíl.")
                            .setPositiveButton("Permitir", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //Se envia la solicitud del permiso y si el usuario lo autoriza se activa la actividad (revisar la funcion onRequestPermissionsResult)
                                    ActivityCompat.requestPermissions(ActividadSync.this,
                                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE},
                                            1);


                                }

                            })
                            .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                    finish();
                                }
                            })
                            .show();
                }else{
                    //Se envia la solicitud del permiso y si el usuario lo autoriza se activa la actividad (revisar la funcion onRequestPermissionsResult)
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE},
                            1);
                }
            }
        }else{
            conPermiso=true;
        }

        if(conPermiso){
            prepararBotones();
            if (UWeb.hayConexion(this)) {

            } else {
                Snackbar.make(findViewById(R.id.coordinador),
                        "No hay conexion disponible. La sincronización queda pendiente",
                        Snackbar.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //final Handler handler = new Handler();

        View v = findViewById(android.R.id.content);
        if(v!=null){
            findViewById(android.R.id.content).postDelayed(new Runnable(){
                public void run(){
                    actualizarBotones();

                    findViewById(android.R.id.content).postDelayed(this, 1000);
                }
            }, 1000);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            for (int i = 0; i < permissions.length; i++) {
                String permission = permissions[i];
                int grantResult = grantResults[i];

                if (permission.equals(Manifest.permission.WRITE_EXTERNAL_STORAGE) || permission.equals(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    if (grantResult == PackageManager.PERMISSION_GRANTED) {

                    } else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            new AlertDialog.Builder(ActividadSync.this)
                                    .setTitle("Se requieren permisos")
                                    .setMessage("SOFTCREDITO requiere leer y guardar archivos de las bitácoras en el movíl.")
                                    .setPositiveButton("Permitir", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            //Se envia la solicitud del permiso y si el usuario lo autoriza se activa la actividad (revisar la funcion onRequestPermissionsResult)
                                            ActivityCompat.requestPermissions(ActividadSync.this,
                                                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE},
                                                    1);


                                        }

                                    })
                                    .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                            finish();
                                        }
                                    })
                                    .show();
                        }
                    }
                }
            }
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
        getMenuInflater().inflate(R.menu.menu_sync, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.accion_sync:
                sincronizar();
                break;
            case R.id.accion_reiniciar:
                reiniciar();
                break;
            case android.R.id.home:
                Intent intent = new Intent(ActividadSync.this, ActividadHome.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void prepararBotones() {
        tvTipoPersona = (TextView) findViewById(R.id.et_clientes);
        proTipoPersona = (ProgressBar) findViewById(R.id.progress_tipo_persona);
        imagevTipoPersona = (ImageView) findViewById(R.id.imagev_tipo_persona);

        tvTipoDocumento = (TextView) findViewById(R.id.et_agenda);
        proTipoDocumento = (ProgressBar) findViewById(R.id.progress_tipo_documentos);
        imagevTipoDocumento = (ImageView) findViewById(R.id.imagev_tipo_documento);

        tvTipoPago = (TextView) findViewById(R.id.et_tipo_pago);
        proTipoPago = (ProgressBar) findViewById(R.id.progress_tipo_pago);
        imagevTipoPago = (ImageView) findViewById(R.id.imagev_tipo_pago);

        tvTipoAmortizacion = (TextView) findViewById(R.id.et_tipo_amortizacion);
        proTipoAmortizacion = (ProgressBar) findViewById(R.id.progress_tipo_amortizacion);
        imagevTipoAmortizacion = (ImageView) findViewById(R.id.imagev_tipo_amortizacion);

        tvInstrumentoMonetario = (TextView) findViewById(R.id.et_instrumento_monetario);
        proInstrumentoMonetario = (ProgressBar) findViewById(R.id.progress_instrumento_monetario);
        imagevInstrumentoMonetario = (ImageView) findViewById(R.id.imagev_instrumento_monetario);

        tvCodigoPostal = (TextView) findViewById(R.id.et_codigo_postal);
        proCodigoPostal = (ProgressBar) findViewById(R.id.progress_codigo_postal);
        imagevCodigoPostal = (ImageView) findViewById(R.id.imagev_codigo_postal);

        tvCodigo = (TextView) findViewById(R.id.et_codigo);
        proCodigo = (ProgressBar) findViewById(R.id.progress_codigo);
        imagevCodigo = (ImageView) findViewById(R.id.imagev_codigo);

        tvPais = (TextView) findViewById(R.id.et_pais);
        proPais = (ProgressBar) findViewById(R.id.progress_pais);
        imagevPais = (ImageView) findViewById(R.id.imagev_pais);

        tvEstado = (TextView) findViewById(R.id.et_estado);
        proEstado = (ProgressBar) findViewById(R.id.progress_estado);
        imagevEstado = (ImageView) findViewById(R.id.imagev_estado);

        tvMunicipio = (TextView) findViewById(R.id.et_municipio);
        proMunicipio = (ProgressBar) findViewById(R.id.progress_municipio);
        imagevMunicipio = (ImageView) findViewById(R.id.imagev_municipio);

        tvEstadoCivil = (TextView) findViewById(R.id.et_estado_civil);
        proEstadoCivil = (ProgressBar) findViewById(R.id.progress_estado_civil);
        imagevEstadoCivil = (ImageView) findViewById(R.id.imagev_estado_civil);

        tvCategoriaActividadEconomica = (TextView) findViewById(R.id.et_categoria_actividad_economica);
        proCategoriaActividadEconomica = (ProgressBar) findViewById(R.id.progress_categoria_actividad_economica);
        imagevCategoriaActividadEconomica = (ImageView) findViewById(R.id.imagev_categoria_actividad_economica);

        tvActividadEconomica = (TextView) findViewById(R.id.et_actividad_economica);
        proActividadEconomica = (ProgressBar) findViewById(R.id.progress_actividad_economica);
        imagevActividadEconomica = (ImageView) findViewById(R.id.imagev_actividad_economica);

        //------------------

        tvGrupoCliente = (TextView) findViewById(R.id.et_grupo_cliente);
        proGrupoCliente = (ProgressBar) findViewById(R.id.progress_grupo_cliente);
        imagevGrupoCliente = (ImageView) findViewById(R.id.imagev_grupo_cliente);

        tvRelacionContacto = (TextView) findViewById(R.id.et_relacion_contacto);
        proRelacionContacto = (ProgressBar) findViewById(R.id.progress_relacion_contacto);
        imagevRelacionContacto = (ImageView) findViewById(R.id.imagev_relacion_contacto);

        tvDocumentoRequerido = (TextView) findViewById(R.id.et_documento_requerido);
        proDocumentoRequerido = (ProgressBar) findViewById(R.id.progress_documento_requerido);
        imagevDocumentoRequerido = (ImageView) findViewById(R.id.imagev_documento_requerido);

        tvProducto = (TextView) findViewById(R.id.et_producto);
        proProducto = (ProgressBar) findViewById(R.id.progress_producto);
        imagevProducto = (ImageView) findViewById(R.id.imagev_producto);

        tvBanco = (TextView) findViewById(R.id.et_banco);
        proBanco = (ProgressBar) findViewById(R.id.progress_banco);
        imagevBanco = (ImageView) findViewById(R.id.imagev_banco);

        tvCanalCobranza = (TextView) findViewById(R.id.et_canal_cobranza);
        proCanalCobranza = (ProgressBar) findViewById(R.id.progress_canal_cobranza);
        imagevCanalCobranza = (ImageView) findViewById(R.id.imagev_canal_cobranza);

        //---------------------

        tvCliente = (TextView) findViewById(R.id.et_cliente);
        proCliente = (ProgressBar) findViewById(R.id.progress_cliente);
        imagevCliente = (ImageView) findViewById(R.id.imagev_cliente);

        tvDocumentoEntregado = (TextView) findViewById(R.id.et_documento_entregado);
        proDocumentoEntregado = (ProgressBar) findViewById(R.id.progress_documento_entregado);
        imagevDocumentoEntregado = (ImageView) findViewById(R.id.imagev_documento_entregado);

        tvArchivoDocumentoEntregado = (TextView) findViewById(R.id.et_archivo_documento_entregado);
        proArchivoDocumentoEntregado = (ProgressBar) findViewById(R.id.progress_archivo_documento_entregado);
        imagevArchivoDocumentoEntregado = (ImageView) findViewById(R.id.imagev_archivo_documento_entregado);

        tvCotizador = (TextView) findViewById(R.id.et_cotizador);
        proCotizador = (ProgressBar) findViewById(R.id.progress_cotizador);
        imagevCotizador = (ImageView) findViewById(R.id.imagev_cotizador);

        tvSolicitud = (TextView) findViewById(R.id.et_solicitud);
        proSolicitud = (ProgressBar) findViewById(R.id.progress_solicitud);
        imagevSolicitud = (ImageView) findViewById(R.id.imagev_solicitud);

        tvBitacoraCredito = (TextView) findViewById(R.id.et_bitacora_credito);
        proBitacoraCredito = (ProgressBar) findViewById(R.id.progress_bitacora_credito);
        imagevBitacoraCredito = (ImageView) findViewById(R.id.imagev_bitacora_credito);

        tvArchivoBitacoraCredito = (TextView) findViewById(R.id.et_archivo_bitacora_credito);
        proArchivoBitacoraCredito = (ProgressBar) findViewById(R.id.progress_archivo_bitacora_credito);
        imagevArchivoBitacoraCredito = (ImageView) findViewById(R.id.imagev_archivo_bitacora_credito);

        tvPago = (TextView) findViewById(R.id.et_pago);
        proPago = (ProgressBar) findViewById(R.id.progress_pago);
        imagevPago = (ImageView) findViewById(R.id.imagev_pago);

        //---------------------
        actualizarBotones();
    }

    protected void reiniciar(){
        UPreferencias.guardarEstatusSync(this,"tipos_personas","");
        UPreferencias.guardarEstatusSync(this,"tipos_documentos","");
        UPreferencias.guardarEstatusSync(this,"tipos_pagos","");
        UPreferencias.guardarEstatusSync(this,"tipos_amortizacion","");
        UPreferencias.guardarEstatusSync(this,"instrumentos_monetarios","");
        UPreferencias.guardarEstatusSync(this,"paises","");
        UPreferencias.guardarEstatusSync(this,"estados","");
        UPreferencias.guardarEstatusSync(this,"municipios","");
        UPreferencias.guardarEstatusSync(this,"estados_civiles","");
        UPreferencias.guardarEstatusSync(this,"categorias_actividades_economicas","");
        UPreferencias.guardarEstatusSync(this,"actividades_economicas","");
        UPreferencias.guardarEstatusSync(this,"grupos","");
        UPreferencias.guardarEstatusSync(this,"tipos_contactos","");
        UPreferencias.guardarEstatusSync(this,"documentos_requeridos","");
        UPreferencias.guardarEstatusSync(this,"productos","");
        UPreferencias.guardarEstatusSync(this,"bancos","");
        UPreferencias.guardarEstatusSync(this,"canales_cobranza","");
        UPreferencias.guardarEstatusSync(this,"cliente","");
        UPreferencias.guardarEstatusSync(this,"documentos_entregados","");
        UPreferencias.guardarEstatusSync(this,"archivos_documentos_entregados","");
        UPreferencias.guardarEstatusSync(this,"cotizador","");
        UPreferencias.guardarEstatusSync(this,"solicitudes","");
        UPreferencias.guardarEstatusSync(this,"bitacoras_credito","");
        UPreferencias.guardarEstatusSync(this,"bitacoras_credito_archivos","");
        UPreferencias.guardarEstatusSync(this,"pagos","");
    }

    protected void actualizarBotones() {
        if(tvTipoPersona!=null){
            actualizar="|";
            String status;
            status=UPreferencias.obtenerEstatusSync(this,"tipos_personas");
            if(isNumeric(status)){
                //Si el estatus es numerico significa que aun esta en la etapa del porcentaje por lo que no ha terminado
                String text = getString(R.string.sync_tipo_persona);
                tvTipoPersona.setText(text);
                proTipoPersona.setVisibility(View.VISIBLE);
                imagevTipoPersona.setVisibility(View.GONE);

            }else{
                String text = getString(R.string.sync_tipo_persona) + ": " + status;
                tvTipoPersona.setText(text);
                proTipoPersona.setVisibility(View.GONE);
                imagevTipoPersona.setVisibility(View.VISIBLE);

                actualizar+="|tipo_persona";
            }

            status=UPreferencias.obtenerEstatusSync(this,"tipos_documentos");
            if(isNumeric(status)){
                //Si el estatus es numerico significa que aun esta en la etapa del porcentaje por lo que no ha terminado
                String text = getString(R.string.sync_tipo_documento);
                tvTipoDocumento.setText(text);
                proTipoDocumento.setVisibility(View.VISIBLE);
                imagevTipoDocumento.setVisibility(View.GONE);
            }else{
                String text = getString(R.string.sync_tipo_documento) + ": " + status;
                tvTipoDocumento.setText(text);
                proTipoDocumento.setVisibility(View.GONE);
                imagevTipoDocumento.setVisibility(View.VISIBLE);

                actualizar+="|tipo_documento";
            }

            status=UPreferencias.obtenerEstatusSync(this,"tipos_pagos");
            if(isNumeric(status)){
                //Si el estatus es numerico significa que aun esta en la etapa del porcentaje por lo que no ha terminado
                String text = getString(R.string.sync_tipo_pago);
                tvTipoPago.setText(text);
                proTipoPago.setVisibility(View.VISIBLE);
                imagevTipoPago.setVisibility(View.GONE);
            }else{
                String text = getString(R.string.sync_tipo_pago) + ": " + status;
                tvTipoPago.setText(text);
                proTipoPago.setVisibility(View.GONE);
                imagevTipoPago.setVisibility(View.VISIBLE);

                actualizar+="|tipo_pago";
            }

            status=UPreferencias.obtenerEstatusSync(this,"tipos_amortizacion");
            if(isNumeric(status)){
                //Si el estatus es numerico significa que aun esta en la etapa del porcentaje por lo que no ha terminado
                String text = getString(R.string.sync_tipo_amortizacion);
                tvTipoAmortizacion.setText(text);
                proTipoAmortizacion.setVisibility(View.VISIBLE);
                imagevTipoAmortizacion.setVisibility(View.GONE);
            }else{
                String text = getString(R.string.sync_tipo_amortizacion) + ": " + status;
                tvTipoAmortizacion.setText(text);
                proTipoAmortizacion.setVisibility(View.GONE);
                imagevTipoAmortizacion.setVisibility(View.VISIBLE);

                actualizar+="|tipo_amortizacion";
            }

            status=UPreferencias.obtenerEstatusSync(this,"instrumentos_monetarios");
            if(isNumeric(status)){
                //Si el estatus es numerico significa que aun esta en la etapa del porcentaje por lo que no ha terminado
                String text = getString(R.string.sync_instrumento_monetarios);
                tvInstrumentoMonetario.setText(text);
                proInstrumentoMonetario.setVisibility(View.VISIBLE);
                imagevInstrumentoMonetario.setVisibility(View.GONE);
            }else{
                String text = getString(R.string.sync_instrumento_monetarios) + ": " + status;
                tvInstrumentoMonetario.setText(text);
                proInstrumentoMonetario.setVisibility(View.GONE);
                imagevInstrumentoMonetario.setVisibility(View.VISIBLE);

                actualizar+="|instrumento_monetario";
            }

            /*
            status=UPreferencias.obtenerEstatusSync(this,"codigo_postal");
            if(isNumeric(status)){
                //Si el estatus es numerico significa que aun esta en la etapa del porcentaje por lo que no ha terminado
                String text = getString(R.string.sync_codigo_postal);
                tvCodigoPostal.setText(text);
                proCodigoPostal.setVisibility(View.VISIBLE);
                imagevCodigoPostal.setVisibility(View.GONE);
            }else{
                String text = getString(R.string.sync_codigo_postal) + ": " + status;
                tvCodigoPostal.setText(text);
                proCodigoPostal.setVisibility(View.GONE);
                imagevCodigoPostal.setVisibility(View.VISIBLE);

                actualizar+="|codigo_postal";
            }
            */

            /*
            status=UPreferencias.obtenerEstatusSync(this,"codigo");
            if(isNumeric(status)){
                //Si el estatus es numerico significa que aun esta en la etapa del porcentaje por lo que no ha terminado
                String text = getString(R.string.sync_codigo);
                tvCodigo.setText(text);
                proCodigo.setVisibility(View.VISIBLE);
                imagevCodigo.setVisibility(View.GONE);
            }else{
                String text = getString(R.string.sync_codigo) + ": " + status;
                tvCodigo.setText(text);
                proCodigo.setVisibility(View.GONE);
                imagevCodigo.setVisibility(View.VISIBLE);

                actualizar+="|codigo";
            }
            */

            status=UPreferencias.obtenerEstatusSync(this,"paises");
            if(isNumeric(status)){
                //Si el estatus es numerico significa que aun esta en la etapa del porcentaje por lo que no ha terminado
                String text = getString(R.string.sync_pais);
                tvPais.setText(text);
                proPais.setVisibility(View.VISIBLE);
                imagevPais.setVisibility(View.GONE);
            }else{
                String text = getString(R.string.sync_pais) + ": " + status;
                tvPais.setText(text);
                proPais.setVisibility(View.GONE);
                imagevPais.setVisibility(View.VISIBLE);

                actualizar+="|pais";
            }

            status=UPreferencias.obtenerEstatusSync(this,"estados");
            if(isNumeric(status)){
                //Si el estatus es numerico significa que aun esta en la etapa del porcentaje por lo que no ha terminado
                String text = getString(R.string.sync_estado);
                tvEstado.setText(text);
                proEstado.setVisibility(View.VISIBLE);
                imagevEstado.setVisibility(View.GONE);
            }else{
                String text = getString(R.string.sync_estado) + ": " + status;
                tvEstado.setText(text);
                proEstado.setVisibility(View.GONE);
                imagevEstado.setVisibility(View.VISIBLE);

                actualizar+="|estado";
            }
            status=UPreferencias.obtenerEstatusSync(this,"municipios");
            if(isNumeric(status)){
                //Si el estatus es numerico significa que aun esta en la etapa del porcentaje por lo que no ha terminado
                String text = getString(R.string.sync_municipio);
                tvMunicipio.setText(text);
                proMunicipio.setVisibility(View.VISIBLE);
                imagevMunicipio.setVisibility(View.GONE);
            }else{
                String text = getString(R.string.sync_municipio) + ": " + status;
                tvMunicipio.setText(text);
                proMunicipio.setVisibility(View.GONE);
                imagevMunicipio.setVisibility(View.VISIBLE);

                actualizar+="|municipio";
            }
            status=UPreferencias.obtenerEstatusSync(this,"estados_civiles");
            if(isNumeric(status)){
                //Si el estatus es numerico significa que aun esta en la etapa del porcentaje por lo que no ha terminado
                String text = getString(R.string.sync_estado_civil);
                tvEstadoCivil.setText(text);
                proEstadoCivil.setVisibility(View.VISIBLE);
                imagevEstadoCivil.setVisibility(View.GONE);
            }else{
                String text = getString(R.string.sync_estado_civil) + ": " + status;
                tvEstadoCivil.setText(text);
                proEstadoCivil.setVisibility(View.GONE);
                imagevEstadoCivil.setVisibility(View.VISIBLE);

                actualizar+="|estado_civil";
            }
            status=UPreferencias.obtenerEstatusSync(this,"categorias_actividades_economicas");
            if(isNumeric(status)){
                //Si el estatus es numerico significa que aun esta en la etapa del porcentaje por lo que no ha terminado
                String text = getString(R.string.sync_categoria_actividad_economica);
                tvCategoriaActividadEconomica.setText(text);
                proCategoriaActividadEconomica.setVisibility(View.VISIBLE);
                imagevCategoriaActividadEconomica.setVisibility(View.GONE);
            }else{
                String text = getString(R.string.sync_categoria_actividad_economica) + ": " + status;
                tvCategoriaActividadEconomica.setText(text);
                proCategoriaActividadEconomica.setVisibility(View.GONE);
                imagevCategoriaActividadEconomica.setVisibility(View.VISIBLE);

                actualizar+="|categoria_actividad_economica";
            }
            status=UPreferencias.obtenerEstatusSync(this,"actividades_economicas");
            if(isNumeric(status)){
                //Si el estatus es numerico significa que aun esta en la etapa del porcentaje por lo que no ha terminado
                String text = getString(R.string.sync_actividad_economica);
                tvActividadEconomica.setText(text);
                proActividadEconomica.setVisibility(View.VISIBLE);
                imagevActividadEconomica.setVisibility(View.GONE);
            }else{
                String text = getString(R.string.sync_actividad_economica) + ": " + status;
                tvActividadEconomica.setText(text);
                proActividadEconomica.setVisibility(View.GONE);
                imagevActividadEconomica.setVisibility(View.VISIBLE);

                actualizar+="|actividad_economica";
            }
            //---------------------

            status=UPreferencias.obtenerEstatusSync(this,"grupos");
            if(isNumeric(status)){
                //Si el estatus es numerico significa que aun esta en la etapa del porcentaje por lo que no ha terminado
                String text = getString(R.string.sync_grupo_cliente);
                tvGrupoCliente.setText(text);
                proGrupoCliente.setVisibility(View.VISIBLE);
                imagevGrupoCliente.setVisibility(View.GONE);
            }else{
                String text = getString(R.string.sync_grupo_cliente) + ": " + status;
                tvGrupoCliente.setText(text);
                proGrupoCliente.setVisibility(View.GONE);
                imagevGrupoCliente.setVisibility(View.VISIBLE);

                actualizar+="|grupo";
            }

            status=UPreferencias.obtenerEstatusSync(this,"tipos_contactos");
            if(isNumeric(status)){
                //Si el estatus es numerico significa que aun esta en la etapa del porcentaje por lo que no ha terminado
                String text = getString(R.string.sync_relacion_contacto);
                tvRelacionContacto.setText(text);
                proRelacionContacto.setVisibility(View.VISIBLE);
                imagevRelacionContacto.setVisibility(View.GONE);
            }else{
                String text = getString(R.string.sync_relacion_contacto) + ": " + status;
                tvRelacionContacto.setText(text);
                proRelacionContacto.setVisibility(View.GONE);
                imagevRelacionContacto.setVisibility(View.VISIBLE);

                actualizar+="|tipo_contacto";
            }

            status=UPreferencias.obtenerEstatusSync(this,"documentos_requeridos");
            if(isNumeric(status)){
                //Si el estatus es numerico significa que aun esta en la etapa del porcentaje por lo que no ha terminado
                String text = getString(R.string.sync_documento_requerido);
                tvDocumentoRequerido.setText(text);
                proDocumentoRequerido.setVisibility(View.VISIBLE);
                imagevDocumentoRequerido.setVisibility(View.GONE);
            }else{
                String text = getString(R.string.sync_documento_requerido) + ": " + status;
                tvDocumentoRequerido.setText(text);
                proDocumentoRequerido.setVisibility(View.GONE);
                imagevDocumentoRequerido.setVisibility(View.VISIBLE);

                actualizar+="|documento_requerido";
            }

            status=UPreferencias.obtenerEstatusSync(this,"productos");
            if(isNumeric(status)){
                //Si el estatus es numerico significa que aun esta en la etapa del porcentaje por lo que no ha terminado
                String text = getString(R.string.sync_producto);
                tvProducto.setText(text);
                proProducto.setVisibility(View.VISIBLE);
                imagevProducto.setVisibility(View.GONE);
            }else{
                String text = getString(R.string.sync_producto) + ": " + status;
                tvProducto.setText(text);
                proProducto.setVisibility(View.GONE);
                imagevProducto.setVisibility(View.VISIBLE);

                actualizar+="|producto";
            }

            status=UPreferencias.obtenerEstatusSync(this,"bancos");
            if(isNumeric(status)){
                //Si el estatus es numerico significa que aun esta en la etapa del porcentaje por lo que no ha terminado
                String text = getString(R.string.sync_banco);
                tvBanco.setText(text);
                proBanco.setVisibility(View.VISIBLE);
                imagevBanco.setVisibility(View.GONE);
            }else{
                String text = getString(R.string.sync_banco) + ": " + status;
                tvBanco.setText(text);
                proBanco.setVisibility(View.GONE);
                imagevBanco.setVisibility(View.VISIBLE);

                actualizar+="|banco";
            }

            status=UPreferencias.obtenerEstatusSync(this,"canales_cobranza");
            if(isNumeric(status)){
                //Si el estatus es numerico significa que aun esta en la etapa del porcentaje por lo que no ha terminado
                String text = getString(R.string.sync_canal_cobranza);
                tvCanalCobranza.setText(text);
                proCanalCobranza.setVisibility(View.VISIBLE);
                imagevCanalCobranza.setVisibility(View.GONE);
            }else{
                String text = getString(R.string.sync_canal_cobranza) + ": " + status;
                tvCanalCobranza.setText(text);
                proCanalCobranza.setVisibility(View.GONE);
                imagevCanalCobranza.setVisibility(View.VISIBLE);

                actualizar+="|canal_cobranza";
            }

            //-------------------------

            status=UPreferencias.obtenerEstatusSync(this,"cliente");
            if(isNumeric(status)){
                //Si el estatus es numerico significa que aun esta en la etapa del porcentaje por lo que no ha terminado
                String text = getString(R.string.sync_cliente);
                tvCliente.setText(text);
                proCliente.setVisibility(View.VISIBLE);
                imagevCliente.setVisibility(View.GONE);
            }else{
                String text = getString(R.string.sync_cliente) + ": " + status;
                tvCliente.setText(text);
                proCliente.setVisibility(View.GONE);
                imagevCliente.setVisibility(View.VISIBLE);

                actualizar+="|cliente";
            }

            status=UPreferencias.obtenerEstatusSync(this,"documentos_entregados");
            if(isNumeric(status)){
                //Si el estatus es numerico significa que aun esta en la etapa del porcentaje por lo que no ha terminado
                String text = getString(R.string.sync_documento_entregado);
                tvDocumentoEntregado.setText(text);
                proDocumentoEntregado.setVisibility(View.VISIBLE);
                imagevDocumentoEntregado.setVisibility(View.GONE);
            }else{
                String text = getString(R.string.sync_documento_entregado) + ": " + status;
                tvDocumentoEntregado.setText(text);
                proDocumentoEntregado.setVisibility(View.GONE);
                imagevDocumentoEntregado.setVisibility(View.VISIBLE);

                actualizar+="|documento_entregado";
            }

            status=UPreferencias.obtenerEstatusSync(this,"archivos_documentos_entregados");
            if(isNumeric(status)){
                String text = getString(R.string.sync_archivo_documento_entregado);
                tvArchivoDocumentoEntregado.setText(text);
                proArchivoDocumentoEntregado.setVisibility(View.VISIBLE);
                imagevArchivoDocumentoEntregado.setVisibility(View.GONE);
            }else{
                String text = getString(R.string.sync_archivo_documento_entregado) + ": " + status;
                tvArchivoDocumentoEntregado.setText(text);
                proArchivoDocumentoEntregado.setVisibility(View.GONE);
                imagevArchivoDocumentoEntregado.setVisibility(View.VISIBLE);

                actualizar+="|archivo_documento_entregado";
            }

            status=UPreferencias.obtenerEstatusSync(this,"cotizador");
            if(isNumeric(status)){
                //Si el estatus es numerico significa que aun esta en la etapa del porcentaje por lo que no ha terminado
                String text = getString(R.string.sync_cotizador);
                tvCotizador.setText(text);
                proCotizador.setVisibility(View.VISIBLE);
                imagevCotizador.setVisibility(View.GONE);
            }else{
                String text = getString(R.string.sync_cotizador) + ": " + status;
                tvCotizador.setText(text);
                proCotizador.setVisibility(View.GONE);
                imagevCotizador.setVisibility(View.VISIBLE);

                actualizar+="|cotizador";
            }

            status=UPreferencias.obtenerEstatusSync(this,"solicitudes");
            if(isNumeric(status)){
                //Si el estatus es numerico significa que aun esta en la etapa del porcentaje por lo que no ha terminado
                String text = getString(R.string.sync_solicitud);
                tvSolicitud.setText(text);
                proSolicitud.setVisibility(View.VISIBLE);
                imagevSolicitud.setVisibility(View.GONE);
            }else{
                String text = getString(R.string.sync_solicitud) + ": " + status;
                tvSolicitud.setText(text);
                proSolicitud.setVisibility(View.GONE);
                imagevSolicitud.setVisibility(View.VISIBLE);

                actualizar+="|solicitud";
            }

            status=UPreferencias.obtenerEstatusSync(this,"bitacoras_credito");
            if(isNumeric(status)){
                //Si el estatus es numerico significa que aun esta en la etapa del porcentaje por lo que no ha terminado
                String text = getString(R.string.sync_bitacora_credito);
                tvBitacoraCredito.setText(text);
                proBitacoraCredito.setVisibility(View.VISIBLE);
                imagevBitacoraCredito.setVisibility(View.GONE);
            }else{
                String text = getString(R.string.sync_bitacora_credito) + ": " + status;
                tvBitacoraCredito.setText(text);
                proBitacoraCredito.setVisibility(View.GONE);
                imagevBitacoraCredito.setVisibility(View.VISIBLE);

                actualizar+="|bitacora_credito";
            }

            status=UPreferencias.obtenerEstatusSync(this,"bitacoras_credito_archivos");
            if(isNumeric(status)){
                //Si el estatus es numerico significa que aun esta en la etapa del porcentaje por lo que no ha terminado
                String text = getString(R.string.sync_archivo_bitacora_credito);
                tvArchivoBitacoraCredito.setText(text);
                proArchivoBitacoraCredito.setVisibility(View.VISIBLE);
                imagevArchivoBitacoraCredito.setVisibility(View.GONE);
            }else{
                String text = getString(R.string.sync_archivo_bitacora_credito) + ": " + status;
                tvArchivoBitacoraCredito.setText(text);
                proArchivoBitacoraCredito.setVisibility(View.GONE);
                imagevArchivoBitacoraCredito.setVisibility(View.VISIBLE);

                actualizar+="|bitacora_credito_archivo";
            }

            status=UPreferencias.obtenerEstatusSync(this,"pagos");
            if(isNumeric(status)){
                //Si el estatus es numerico significa que aun esta en la etapa del porcentaje por lo que no ha terminado
                String text = getString(R.string.sync_pago);
                tvPago.setText(text);
                proPago.setVisibility(View.VISIBLE);
                imagevPago.setVisibility(View.GONE);
            }else{
                String text = getString(R.string.sync_pago) + ": " + status;
                tvPago.setText(text);
                proPago.setVisibility(View.GONE);
                imagevPago.setVisibility(View.VISIBLE);

                actualizar+="|pago";
            }

            actualizar+="|";
        }
    }
    private void sincronizar() {
        // Verificación para evitar iniciar más de una sync a la vez
        Account cuentaActiva = UCuentas.obtenerCuentaActiva(this);
        if (ContentResolver.isSyncActive(cuentaActiva, Contract.AUTORIDAD)) {
            Log.d(TAG, "Ignorando sincronización ya que existe una en proceso.");
            return;
        }

        actualizarBotones();

        Log.d(TAG, "Solicitando sincronización manual. " + actualizar);
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        bundle.putString(SyncAdapter.SYNC_TABLE, "all");
        bundle.putString(SyncAdapter.SYNC_TABLE_MULTIPLE, actualizar);
        ContentResolver.requestSync(cuentaActiva, Contract.AUTORIDAD, bundle);
    }

    private boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch(NumberFormatException e){
            return false;
        }
    }
}
