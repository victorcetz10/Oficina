package com.softcredito.app.sync;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.Context;
import android.content.OperationApplicationException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.RemoteException;
import android.util.Log;

import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.softcredito.app.modelo.ProcesadorLocalActividadesEconomicas;
import com.softcredito.app.modelo.ProcesadorLocalArchivosDocumentosEntregados;
import com.softcredito.app.modelo.ProcesadorLocalBancos;
import com.softcredito.app.modelo.ProcesadorLocalBitacorasCredito;
import com.softcredito.app.modelo.ProcesadorLocalBitacorasCreditoArchivos;
import com.softcredito.app.modelo.ProcesadorLocalCanalesCobranzas;
import com.softcredito.app.modelo.ProcesadorLocalCategoriasActividadesEconomicas;
import com.softcredito.app.modelo.ProcesadorLocalClientes;
import com.softcredito.app.modelo.ProcesadorLocalCodigos;
import com.softcredito.app.modelo.ProcesadorLocalCodigosPostales;
import com.softcredito.app.modelo.ProcesadorLocalCotizadores;
import com.softcredito.app.modelo.ProcesadorLocalDocumentosEntregados;
import com.softcredito.app.modelo.ProcesadorLocalDocumentosRequeridos;
import com.softcredito.app.modelo.ProcesadorLocalPaises;
import com.softcredito.app.modelo.ProcesadorLocalEstados;
import com.softcredito.app.modelo.ProcesadorLocalEstadosCiviles;
import com.softcredito.app.modelo.ProcesadorLocalGrupos;
import com.softcredito.app.modelo.ProcesadorLocalInstrumentosMonetarios;
import com.softcredito.app.modelo.ProcesadorLocalMunicipios;
import com.softcredito.app.modelo.ProcesadorLocalProductos;
import com.softcredito.app.modelo.ProcesadorLocalSolicitudes;
import com.softcredito.app.modelo.ProcesadorLocalTiposAmortizacion;
import com.softcredito.app.modelo.ProcesadorLocalTiposContactos;
import com.softcredito.app.modelo.ProcesadorLocalTiposDocumentos;
import com.softcredito.app.modelo.ProcesadorLocalTiposPagos;
import com.softcredito.app.modelo.ProcesadorLocalTiposPersonas;
import com.softcredito.app.modelo.ProcesadorRemoto;
import com.softcredito.app.modelo.ProcesadorRemotoArchivosDocumentosEntregados;
import com.softcredito.app.modelo.ProcesadorRemotoBitacorasCredito;
import com.softcredito.app.modelo.ProcesadorRemotoBitacorasCreditoArchivos;
import com.softcredito.app.modelo.ProcesadorRemotoCotizadores;
import com.softcredito.app.modelo.ProcesadorRemotoDocumentosEntregados;
import com.softcredito.app.modelo.ProcesadorRemotoPagos;
import com.softcredito.app.modelo.ProcesadorRemotoSolicitudes;
import com.softcredito.app.provider.Contract;
import com.softcredito.app.utilidades.UPreferencias;
import com.softcredito.app.web.RESTService;
import com.softcredito.app.web.RespuestaApi;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import android.util.Base64;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

//<Params,Progress,Result>
public class AsyncSincronizacion extends AsyncTask<Void, String, Void>{

    private static final String TAG = SyncAdapter.class.getSimpleName();

    // Extras para intent local
    public static final String EXTRA_RESULTADO = "extra.resultado";
    private static final String EXTRA_MENSAJE = "extra.mensaje";

    // Recurso sync (10.0.3.2 -> Genymotion; 10.0.2.2 -> AVD)
    public static final String URL_SYNC = "api/sync/";

    public static final String SYNC_TABLE = "extra.tableSync";//Tabla que se actualiza
    public static final String SYNC_TABLE_ROW = "extra.tableRowSync";//Id del registro que se actualiza
    public static final String SYNC_CONVERSIONES = "extra.conversionesSync";//Id devuelto del registro que se actualiza

    private static final int ESTADO_PETICION_FALLIDA = 107;
    private static final int ESTADO_TIEMPO_ESPERA = 108;
    private static final int ESTADO_ERROR_PARSING = 109;
    private static final int ESTADO_ERROR_SERVIDOR = 110;

    private ContentResolver cr;
    private Gson gson = new Gson();
    private ProcesadorRemoto procRemoto = new ProcesadorRemoto();
    private ProcesadorRemotoSolicitudes procRemotoSolicitudes = new ProcesadorRemotoSolicitudes();
    private ProcesadorRemotoCotizadores procRemotoCotizadores = new ProcesadorRemotoCotizadores();
    private ProcesadorRemotoDocumentosEntregados procRemotoDocumentosEntregados = new ProcesadorRemotoDocumentosEntregados();
    private ProcesadorRemotoArchivosDocumentosEntregados procRemotoArchivosDocumentosEntregados = new ProcesadorRemotoArchivosDocumentosEntregados();
    private ProcesadorRemotoBitacorasCredito procRemotoBitacorasCredito = new ProcesadorRemotoBitacorasCredito();
    private ProcesadorRemotoBitacorasCreditoArchivos procRemotoBitacorasCreditoArchivos = new ProcesadorRemotoBitacorasCreditoArchivos();
    private ProcesadorRemotoPagos procRemotoPagos = new ProcesadorRemotoPagos();
    private String urlProduccion;

    private final String ruta_expedientes = Environment.getExternalStorageDirectory().getAbsolutePath() +"/softcredito/expedientes/";
    protected final String ruta_bitacoras_credito = Environment.getExternalStorageDirectory().getAbsolutePath() +"/softcredito/bitacoras_credito/";

    private Context context;

    private String table;
    private JSONObject response;

    public AsyncSincronizacion(Context context, String tabla,JSONObject response){
        this.context=context;
        this.table=tabla;
        this.response=response;
        this.urlProduccion = UPreferencias.obtenerProduccion(context);

        this.cr = context.getContentResolver();
    }

    @Override
    protected Void doInBackground(Void... params) {
        tratarGet(response,table);

        return null;
    }

    protected void onProgressUpdate(String... tabla) {


    }

    private void tratarGet(JSONObject respuesta, String tabla) {
        try {
            ArrayList<ContentProviderOperation> operaciones = new ArrayList<>();
            HashMap<String,String[]> nuevos;
            Uri uriTabla=Contract.Clientes.URI_CONTENIDO;
            switch (tabla){
                case "cliente":
                    ProcesadorLocalClientes manejador1 = new ProcesadorLocalClientes();
                    manejador1.procesar(respuesta.getJSONArray(tabla));
                    manejador1.procesarOperaciones(operaciones, cr);
                    uriTabla=Contract.Clientes.URI_CONTENIDO;
                    break;
                case "grupo":
                    ProcesadorLocalGrupos manejador2 = new ProcesadorLocalGrupos();
                    manejador2.procesar(respuesta.getJSONArray(tabla));
                    manejador2.procesarOperaciones(operaciones, cr);
                    uriTabla=Contract.Grupos.URI_CONTENIDO;
                    break;
                case "tipo_persona":
                    ProcesadorLocalTiposPersonas manejador3 = new ProcesadorLocalTiposPersonas();
                    manejador3.procesar(respuesta.getJSONArray(tabla));
                    manejador3.procesarOperaciones(operaciones, cr);
                    uriTabla=Contract.TiposPersonas.URI_CONTENIDO;
                    break;
                case "tipo_contacto":
                    ProcesadorLocalTiposContactos manejador4 = new ProcesadorLocalTiposContactos();
                    manejador4.procesar(respuesta.getJSONArray(tabla));
                    manejador4.procesarOperaciones(operaciones, cr);
                    uriTabla=Contract.TiposContactos.URI_CONTENIDO;
                    break;
                case "tipo_documento":
                    ProcesadorLocalTiposDocumentos manejador5 = new ProcesadorLocalTiposDocumentos();
                    manejador5.procesar(respuesta.getJSONArray(tabla));
                    manejador5.procesarOperaciones(operaciones, cr);
                    uriTabla=Contract.TiposDocumentos.URI_CONTENIDO;
                    break;
                case "documento_requerido":
                    ProcesadorLocalDocumentosRequeridos manejador6 = new ProcesadorLocalDocumentosRequeridos();
                    manejador6.procesar(respuesta.getJSONArray(tabla));
                    manejador6.procesarOperaciones(operaciones, cr);
                    uriTabla=Contract.DocumentosRequeridos.URI_CONTENIDO;
                    break;
                case "documento_entregado":
                    ProcesadorLocalDocumentosEntregados manejador7 = new ProcesadorLocalDocumentosEntregados();
                    manejador7.procesar(respuesta.getJSONArray(tabla));
                    manejador7.procesarOperaciones(operaciones, cr);
                    uriTabla=Contract.DocumentosEntregados.URI_CONTENIDO;
                    break;
                case "archivo_documento_entregado":
                    ProcesadorLocalArchivosDocumentosEntregados manejador8 = new ProcesadorLocalArchivosDocumentosEntregados();
                    manejador8.procesar(respuesta.getJSONArray(tabla));
                    nuevos=manejador8.procesarOperaciones(operaciones, cr);
                    uriTabla=Contract.ArchivosDocumentosEntregados.URI_CONTENIDO;
                    if(nuevos.size()>0){
                        for (String[] nuevo : nuevos.values()) {
                            descargarArchivoExpediente(nuevo);
                        }
                    }
                    break;
                case "producto":
                    ProcesadorLocalProductos manejador9 = new ProcesadorLocalProductos();
                    manejador9.procesar(respuesta.getJSONArray(tabla));
                    manejador9.procesarOperaciones(operaciones, cr);
                    uriTabla=Contract.Productos.URI_CONTENIDO;
                    break;
                case "banco":
                    ProcesadorLocalBancos manejador10 = new ProcesadorLocalBancos();
                    manejador10.procesar(respuesta.getJSONArray(tabla));
                    manejador10.procesarOperaciones(operaciones, cr);
                    uriTabla=Contract.Bancos.URI_CONTENIDO;
                    break;
                case "tipo_pago":
                    ProcesadorLocalTiposPagos manejador11 = new ProcesadorLocalTiposPagos();
                    manejador11.procesar(respuesta.getJSONArray(tabla));
                    manejador11.procesarOperaciones(operaciones, cr);
                    uriTabla=Contract.TiposPagos.URI_CONTENIDO;
                    break;
                case "tipo_amortizacion":
                    ProcesadorLocalTiposAmortizacion manejador12 = new ProcesadorLocalTiposAmortizacion();
                    manejador12.procesar(respuesta.getJSONArray(tabla));
                    manejador12.procesarOperaciones(operaciones, cr);
                    uriTabla=Contract.TiposAmortizacion.URI_CONTENIDO;
                    break;
                case "solicitud":
                    ProcesadorLocalSolicitudes manejador13 = new ProcesadorLocalSolicitudes();
                    manejador13.procesar(respuesta.getJSONArray(tabla));
                    manejador13.procesarOperaciones(operaciones, cr);
                    uriTabla=Contract.Solicitudes.URI_CONTENIDO;
                    break;
                case "cotizador":
                    ProcesadorLocalCotizadores manejador14 = new ProcesadorLocalCotizadores();
                    manejador14.procesar(respuesta.getJSONArray(tabla));
                    manejador14.procesarOperaciones(operaciones, cr);
                    uriTabla=Contract.Cotizadores.URI_CONTENIDO;
                    break;
                case "bitacora_credito":
                    ProcesadorLocalBitacorasCredito manejador15 = new ProcesadorLocalBitacorasCredito();
                    manejador15.procesar(respuesta.getJSONArray(tabla));
                    manejador15.procesarOperaciones(operaciones, cr);
                    uriTabla=Contract.BitacorasCredito.URI_CONTENIDO;
                    break;
                case "bitacora_credito_archivo":
                    ProcesadorLocalBitacorasCreditoArchivos manejador16 = new ProcesadorLocalBitacorasCreditoArchivos();
                    manejador16.procesar(respuesta.getJSONArray(tabla));
                    nuevos=manejador16.procesarOperaciones(operaciones, cr);
                    uriTabla=Contract.BitacorasCreditoArchivos.URI_CONTENIDO;
                    if(nuevos.size()>0){
                        for (String[] nuevo : nuevos.values()) {
                            descargarArchivoBitacora(nuevo);
                        }
                    }
                    break;
                case "instrumento_monetario":
                    ProcesadorLocalInstrumentosMonetarios manejador17 = new ProcesadorLocalInstrumentosMonetarios();
                    manejador17.procesar(respuesta.getJSONArray(tabla));
                    manejador17.procesarOperaciones(operaciones, cr);
                    uriTabla=Contract.InstrumentosMonetarios.URI_CONTENIDO;
                    break;
                case "canal_cobranza":
                    ProcesadorLocalCanalesCobranzas manejador18 = new ProcesadorLocalCanalesCobranzas();
                    manejador18.procesar(respuesta.getJSONArray(tabla));
                    manejador18.procesarOperaciones(operaciones, cr);
                    uriTabla=Contract.CanalesCobranzas.URI_CONTENIDO;
                    break;
                case "codigo_postal":
                    ProcesadorLocalCodigosPostales manejador19 = new ProcesadorLocalCodigosPostales();
                    manejador19.procesar(respuesta.getJSONArray(tabla));
                    manejador19.procesarOperaciones(operaciones, cr);
                    uriTabla=Contract.CodigosPostales.URI_CONTENIDO;
                    break;
                case "codigo":
                    ProcesadorLocalCodigos manejador20 = new ProcesadorLocalCodigos();
                    manejador20.procesar(respuesta.getJSONArray(tabla));
                    manejador20.procesarOperaciones(operaciones, cr);
                    uriTabla=Contract.Codigos.URI_CONTENIDO;
                    break;
                case "pais":
                    ProcesadorLocalPaises manejador21 = new ProcesadorLocalPaises();
                    manejador21.procesar(respuesta.getJSONArray(tabla));
                    manejador21.procesarOperaciones(operaciones, cr);
                    uriTabla=Contract.Paises.URI_CONTENIDO;
                    break;
                case "estado_pais":
                    ProcesadorLocalEstados manejador22 = new ProcesadorLocalEstados();
                    manejador22.procesar(respuesta.getJSONArray(tabla));
                    manejador22.procesarOperaciones(operaciones, cr);
                    uriTabla=Contract.Estados.URI_CONTENIDO;
                    break;
                case "municipio":
                    ProcesadorLocalMunicipios manejador23 = new ProcesadorLocalMunicipios();
                    manejador23.procesar(respuesta.getJSONArray(tabla));
                    manejador23.procesarOperaciones(operaciones, cr);
                    uriTabla=Contract.Municipios.URI_CONTENIDO;
                    break;
                case "estado_civil":
                    ProcesadorLocalEstadosCiviles manejador24 = new ProcesadorLocalEstadosCiviles();
                    manejador24.procesar(respuesta.getJSONArray(tabla));
                    manejador24.procesarOperaciones(operaciones, cr);
                    uriTabla=Contract.EstadosCiviles.URI_CONTENIDO;
                    break;
                case "categoria_actividad_economica":
                    ProcesadorLocalCategoriasActividadesEconomicas manejador25 = new ProcesadorLocalCategoriasActividadesEconomicas();
                    manejador25.procesar(respuesta.getJSONArray(tabla));
                    manejador25.procesarOperaciones(operaciones, cr);
                    uriTabla=Contract.CategoriasActividadesEconomicas.URI_CONTENIDO;
                    break;
                case "actividad_economica":
                    ProcesadorLocalActividadesEconomicas manejador26 = new ProcesadorLocalActividadesEconomicas();
                    manejador26.procesar(respuesta.getJSONArray(tabla));
                    manejador26.procesarOperaciones(operaciones, cr);
                    uriTabla=Contract.ActividadesEconomicas.URI_CONTENIDO;
                    break;
            }

            if(tabla.equals("estado_pais")){
                tabla="estado";
            }

            if(operaciones.size() == 0){
                Log.d(TAG, "Sin cambios remotos " + tabla);
                Log.e ( "URI_CONTENIDO", String.valueOf ( uriTabla ) );
            }else{
                //Si hay operaciones que procesar
                if (operaciones.size() > 0) {
                    Log.d(TAG, "# Cambios en \'" + tabla + "\': " + operaciones.size() + " ops.");
                    // Aplicar batch de operaciones
                    cr.applyBatch(Contract.AUTORIDAD, operaciones);
                    // Notificar cambio al content provider
                    cr.notifyChange(uriTabla, null, false);
                }
            }
            // Sincronización remota
            if(tabla=="cliente"){
                syncRemota();
            }else if(tabla=="documento_entregado"){
                try {
                    boolean salir=false;
                    do{
                        String status;
                        status=UPreferencias.obtenerEstatusSync(context,"cliente");
                        if(isNumeric(status)==false){
                            salir=true;
                        }

                        Thread.sleep(250);
                    }while(!salir);
                    syncRemotaDocumentosEntregados();
                }catch (InterruptedException e) {

                }
            }else if(tabla=="archivo_documento_entregado"){
                try {
                    boolean salir=false;
                    do{
                        String status;
                        status=UPreferencias.obtenerEstatusSync(context,"documento_entregado");
                        if(isNumeric(status)==false){
                            salir=true;
                        }

                        Thread.sleep(250);
                    }while(!salir);
                    syncRemotaArchivosDocumentosEntregados();
                }catch (InterruptedException e) {

                }
            }else if(tabla=="solicitud"){
                try {
                    boolean salir=false;
                    do{
                        String status;
                        status=UPreferencias.obtenerEstatusSync(context,"cliente");
                        if(isNumeric(status)==false){
                            salir=true;
                        }

                        Thread.sleep(250);
                    }while(!salir);
                    syncRemotaSolicitudes();
                }catch (InterruptedException e) {

                }
            }else if(tabla=="bitacora_credito"){
                try {
                    boolean salir=false;
                    do{
                        String status;
                        status=UPreferencias.obtenerEstatusSync(context,"solicitud");
                        if(isNumeric(status)==false){
                            salir=true;
                        }

                        Thread.sleep(250);
                    }while(!salir);
                    syncRemotaBitacorasCredito();
                }catch (InterruptedException e) {

                }
            }else if(tabla=="bitacora_credito_archivo"){
                try {
                    boolean salir=false;
                    do{
                        String status;
                        status=UPreferencias.obtenerEstatusSync(context,"bitacora_credito");
                        if(isNumeric(status)==false){
                            salir=true;
                        }

                        Thread.sleep(250);
                    }while(!salir);
                    syncRemotaBitacorasCreditoArchivos();;
                }catch (InterruptedException e) {

                }
            }else if(tabla=="pago"){
                try {
                    boolean salir=false;
                    do{
                        String status;
                        status=UPreferencias.obtenerEstatusSync(context,"solicitud");
                        if(isNumeric(status)==false){
                            salir=true;
                        }

                        Thread.sleep(250);
                    }while(!salir);
                    syncRemotaPagos();
                }catch (InterruptedException e) {

                }
            }else{
                SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd h:mm a");
                String date = formato.format(new Date() );
                UPreferencias.guardarEstatusSync(context, tabla, date);
            }
        } catch (RemoteException | OperationApplicationException | JSONException e) {
            e.printStackTrace();
            UPreferencias.guardarEstatusSync(context, tabla, "Error");
        }
    }

    private void descargarArchivoBitacora(String[] id){
        /*
        HashMap<String, String> cabeceras = new HashMap<>();
        cabeceras.put("Authorization", UPreferencias.obtenerClaveApi(context));

        new RESTService(context).get(urlProduccion + URL_SYNC + "bitacora_credito_archivo/" + id,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Procesar GET
                        new TareaGuardarArchivo(ruta_bitacoras_credito).execute(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Procesar Error
                        tratarErrores(error);
                    }
                }, cabeceras);
         */

        try{
            Log.v(TAG, "downloading data");

            URL url  = new URL(urlProduccion + URL_SYNC + "bitacora_credito_archivo/" + id[0]);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Authorization", "Bearer " + UPreferencias.obtenerClaveApi(context));
            connection.setDoOutput(false);
            connection.setDoInput(true);
            connection.connect();

            int lenghtOfFile = connection.getContentLength();

            Log.v(TAG, "lenghtOfFile = "+lenghtOfFile);
            int status = connection.getResponseCode();

            InputStream is;
            if (status != HttpURLConnection.HTTP_OK)
                is = connection.getErrorStream();
            else
                is = connection.getInputStream();

            //InputStream is = url.openStream();

            File testDirectory = new File(ruta_bitacoras_credito);
            if(!testDirectory.exists()){
                testDirectory.mkdir();
            }

            String extension;
            extension = id[1].substring(id[1].lastIndexOf("."));
            if(extension.length()==0){
                extension=".pdf";
            }

            FileOutputStream fos = new FileOutputStream(ruta_bitacoras_credito + id[0] + extension);

            byte data[] = new byte[1024];

            int count = 0;
            long total = 0;
            int progress = 0;

            while ((count=is.read(data)) != -1)
            {
                total += count;
                int progress_temp = (int)total*100/lenghtOfFile;
                if(progress_temp%10 == 0 && progress != progress_temp){
                    progress = progress_temp;
                    Log.v(TAG, "total = "+progress);
                }
                fos.write(data, 0, count);
            }

            is.close();
            fos.close();

            Log.v(TAG, "downloading finished");

        }catch(Exception e){
            Log.v(TAG, "exception in downloadData");
            e.printStackTrace();
        }
    }

    private void descargarArchivoExpediente(String[] id){
        try{
            Log.v(TAG, "downloading data");

            URL url  = new URL(urlProduccion + URL_SYNC + "archivo_documento_entregado/" + id[0]);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Authorization", "Bearer " + UPreferencias.obtenerClaveApi(context));
            connection.setDoOutput(false);
            connection.setDoInput(true);
            connection.connect();

            int lenghtOfFile = connection.getContentLength();

            Log.v(TAG, "lenghtOfFile = "+lenghtOfFile);
            int status = connection.getResponseCode();

            InputStream is;
            if (status != HttpURLConnection.HTTP_OK)
                is = connection.getErrorStream();
            else
                is = connection.getInputStream();

            //InputStream is = url.openStream();

            File testDirectory = new File(ruta_expedientes);
            if(!testDirectory.exists()){
                testDirectory.mkdir();
            }

            String extension;
            extension = id[1].substring(id[1].lastIndexOf("."));
            if(extension.length()==0){
                extension=".pdf";
            }

            FileOutputStream fos = new FileOutputStream(ruta_expedientes + id[0] + extension);

            byte data[] = new byte[1024];

            int count = 0;
            long total = 0;
            int progress = 0;

            while ((count=is.read(data)) != -1)
            {
                total += count;
                int progress_temp = (int)total*100/lenghtOfFile;
                if(progress_temp%10 == 0 && progress != progress_temp){
                    progress = progress_temp;
                    Log.v(TAG, "total = "+progress);
                }
                fos.write(data, 0, count);
            }

            is.close();
            fos.close();

            Log.v(TAG, "downloading finished");

        }catch(Exception e){
            Log.v(TAG, "exception in downloadData");
            e.printStackTrace();
        }
    }

    private class UploadFileAsync extends AsyncTask<String, Void, String> {
        private String tabla;
        private String urlPost;
        private String urlRemoto;
        private String urlLocal;

        public UploadFileAsync(String tabla, String urlPost, String urlRemoto, String urlLocal) {
            this.tabla = tabla;
            this.urlPost = urlPost;
            this.urlRemoto = urlRemoto;
            this.urlLocal = urlLocal;
        }

        @Override
        protected String doInBackground(String... params) {
            Log.d(TAG, "Subiendo archivo : " + tabla);
            try {
                String sourceFileUri = urlLocal;

                HttpURLConnection conn = null;
                DataOutputStream dos = null;
                String lineEnd = "\r\n";
                String twoHyphens = "--";
                String boundary = "*****";
                int bytesRead, bytesAvailable, bufferSize;
                byte[] buffer;
                int maxBufferSize = 1 * 1024 * 1024;
                File sourceFile = new File(sourceFileUri);

                if (sourceFile.isFile()) {

                    try {
                        String upLoadServerUri = urlPost;

                        // open a URL connection to the Servlet
                        FileInputStream fileInputStream = new FileInputStream(
                                sourceFile);
                        URL url = new URL(upLoadServerUri);

                        // Open a HTTP connection to the URL
                        conn = (HttpURLConnection) url.openConnection();
                        conn.setDoInput(true); // Allow Inputs
                        conn.setDoOutput(true); // Allow Outputs
                        conn.setUseCaches(false); // Don't use a Cached Copy
                        conn.setRequestMethod("POST");
                        conn.setRequestProperty("Authorization", "Bearer " + UPreferencias.obtenerClaveApi(context));
                        conn.setRequestProperty("Connection", "Keep-Alive");
                        conn.setRequestProperty("ENCTYPE","multipart/form-data");
                        conn.setRequestProperty("Content-Type","multipart/form-data;boundary=" + boundary);
                        conn.setRequestProperty(tabla, sourceFileUri);
                        Log.e ( "boun", boundary );
                        Log.e ( "tab", tabla );
                        Log.e ( "sou", sourceFileUri );

                        dos = new DataOutputStream(conn.getOutputStream());

                        dos.writeBytes(twoHyphens + boundary + lineEnd);
                        dos.writeBytes("Content-Disposition: form-data; name=\"" + tabla + "\";filename=\"" + urlRemoto + "\"" + lineEnd);

                        dos.writeBytes(lineEnd);

                        // create a buffer of maximum size
                        bytesAvailable = fileInputStream.available();

                        bufferSize = Math.min(bytesAvailable, maxBufferSize);
                        buffer = new byte[bufferSize];

                        // read file and write it into form...
                        bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                        while (bytesRead > 0) {

                            dos.write(buffer, 0, bufferSize);
                            bytesAvailable = fileInputStream.available();
                            bufferSize = Math
                                    .min(bytesAvailable, maxBufferSize);
                            bytesRead = fileInputStream.read(buffer, 0,
                                    bufferSize);

                        }

                        // send multipart form data necesssary after file
                        // data...
                        dos.writeBytes(lineEnd);
                        dos.writeBytes(twoHyphens + boundary + twoHyphens
                                + lineEnd);

                        // Responses from the server (code and message)
                        int serverResponseCode;
                        serverResponseCode = conn.getResponseCode();
                        String serverResponseMessage = conn
                                .getResponseMessage();

                        if (serverResponseCode == HttpURLConnection.HTTP_OK) {
                            // messageText.setText(msg);
                            //Toast.makeText(ctx, "File Upload Complete.",
                            //      Toast.LENGTH_SHORT).show();

                            // recursiveDelete(mDirectory1);
                        }

                        // close the streams //
                        fileInputStream.close();
                        dos.flush();
                        dos.close();

                    } catch (Exception e) {

                        // dialog.dismiss();
                        e.printStackTrace();

                    }
                    // dialog.dismiss();

                } // End else block


            } catch (Exception ex) {
                // dialog.dismiss();

                ex.printStackTrace();
            }
            return "Executed";
        }
    }

    private void syncRemota() {
        procRemoto = new ProcesadorRemoto();

        // Construir payload con todas las operaciones remotas pendientes
        Map<String,Object> datos = procRemoto.crearPayload(cr);
        Log.e ( "ProRemoto", String.valueOf ( datos ) );

        if (datos != null) {
            Log.d(TAG, "Payload de " + table + ": " + datos);

            HashMap<String, String> cabeceras = new HashMap<>();
            cabeceras.put("Authorization", UPreferencias.obtenerClaveApi(context));

            new RESTService(context).post(urlProduccion + URL_SYNC + table, datos,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            procRemoto.desmarcarClientes(cr);
                            procRemoto.convertirClientes(response,cr);

                            SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd h:mm a");
                            String date = formato.format(new Date() );

                            UPreferencias.guardarEstatusSync(context, "cliente", date);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            UPreferencias.guardarEstatusSync(context, "cliente", "Error");
                            tratarErrores(error);
                        }
                    }
                    , cabeceras);
        } else {
            Log.d(TAG, "Sin cambios locales en clientes");
            Log.e ( "URL", urlProduccion );
            Log.e ( "SYNC", URL_SYNC );
            Log.e ( "Tabla", table );
            Log.e ( "Datos", String.valueOf ( datos ) );
            Log.e ( "Token", UPreferencias.obtenerClaveApi(context) );




            SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd h:mm a");
            String date = formato.format(new Date() );

            UPreferencias.guardarEstatusSync(context, "cliente", date);
        }
    }

    private void syncRemotaSolicitudes() {
        String tableSync = "solicitudes";
        String Solic= "solicitudes";
        procRemotoSolicitudes = new ProcesadorRemotoSolicitudes();

        // Construir payload con todas las operaciones remotas pendientes
        Map<String, Object> datos = procRemotoSolicitudes.crearPayload(cr);

        if (datos != null) {
            Log.d(TAG, "Payload de solicitudes: " + datos);

            HashMap<String, String> cabeceras = new HashMap<>();
            cabeceras.put("Authorization", UPreferencias.obtenerClaveApi(context));

            new RESTService(context).post(urlProduccion + URL_SYNC + tableSync, datos,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            procRemotoSolicitudes.desmarcar(cr);
                            procRemotoSolicitudes.convertir(response,cr);

                            SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd h:mm a");
                            String date = formato.format(new Date() );

                            UPreferencias.guardarEstatusSync(context, "solicitud", date);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            UPreferencias.guardarEstatusSync(context, "solicitud", "Error");
                            tratarErrores(error);
                        }
                    }
                    , cabeceras);
        } else {
            Log.d(TAG, "Sin cambios locales en solicitudes");

            Log.e ( "URL", urlProduccion );
            Log.e ( "SYNC", URL_SYNC );
            Log.e ( "TablaSync", tableSync );
            Log.e ( "Datos", String.valueOf ( datos ) );

            SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd h:mm a");
            String date = formato.format(new Date() );

            UPreferencias.guardarEstatusSync(context, "solicitud", date);
        }
    }

    private void syncRemotaDocumentosEntregados() {
        String tableSync = "documentos_entregados";
        procRemotoDocumentosEntregados = new ProcesadorRemotoDocumentosEntregados();

        // Construir payload con todas las operaciones remotas pendientes
        Map<String, Object> datos = procRemotoDocumentosEntregados.crearPayload(cr);

        if (datos != null) {
            Log.d(TAG, "Payload de documentos entregados: " + datos);

            HashMap<String, String> cabeceras = new HashMap<>();
            cabeceras.put("Authorization", UPreferencias.obtenerClaveApi(context));

            new RESTService(context).post(urlProduccion + URL_SYNC + tableSync, datos,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            procRemotoDocumentosEntregados.desmarcar(cr);
                            procRemotoDocumentosEntregados.convertir(response,cr);

                            SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd h:mm a");
                            String date = formato.format(new Date() );
                            UPreferencias.guardarEstatusSync(context, "documento_entregado", date);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            UPreferencias.guardarEstatusSync(context, "documento_entregado", "Error");
                            tratarErrores(error);
                        }
                    }
                    , cabeceras);
        } else {
            Log.d(TAG, "Sin cambios locales en documentos entregados");

            SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd h:mm a");
            String date = formato.format(new Date() );

            UPreferencias.guardarEstatusSync(context, "documento_entregado", date);
        }
    }

    private void syncRemotaArchivosDocumentosEntregados() {
        String tableSync = "archivos_documentos_entregados";
        procRemotoArchivosDocumentosEntregados = new ProcesadorRemotoArchivosDocumentosEntregados();

        // Construir payload con todas las operaciones remotas pendientes
        Map<String, Object> datos = procRemotoArchivosDocumentosEntregados.crearPayload(cr);

        if (datos != null) {
            Log.d(TAG, "Payload de archivos de documentos entregados: " + datos);

            HashMap<String, String> cabeceras = new HashMap<>();
            cabeceras.put("Authorization", UPreferencias.obtenerClaveApi(context));

            new RESTService(context).post(urlProduccion + URL_SYNC + tableSync, datos,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            procRemotoArchivosDocumentosEntregados.desmarcar(cr);
                            procRemotoArchivosDocumentosEntregados.convertir(response,cr);

                            String error="";
                            try {
                                JSONObject uploads=response.getJSONObject("archivosUpload");
                                Iterator<?> keys = uploads.keys();
                                while( keys.hasNext() ) {
                                    String urlLocal = (String)keys.next();
                                    String urlRemoto=uploads.get(urlLocal).toString();
                                    if (uploads.get(urlLocal).toString().length()>0) {
                                        UploadFileAsync task = new UploadFileAsync("archivo_documento_entregado",urlProduccion + URL_SYNC + "archivo_documento_entregado",urlRemoto,urlLocal);
                                        task.execute();
                                    }
                                }
                            }catch (JSONException ex){
                                Log.d(TAG, "Error al subir archivo: " + ex.getMessage());
                                error="Error";
                            }

                            if(error.isEmpty()){
                                SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd h:mm a");
                                String date = formato.format(new Date() );
                                UPreferencias.guardarEstatusSync(context, "archivo_documento_entregado", date);
                            }else{
                                UPreferencias.guardarEstatusSync(context, "archivo_documento_entregado", error);
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            UPreferencias.guardarEstatusSync(context, "archivo_documento_entregado", "Error");
                            tratarErrores(error);
                        }
                    }
                    , cabeceras);
        } else {
            Log.d(TAG, "Sin cambios locales en archivos de documentos entregados");

            SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd h:mm a");
            String date = formato.format(new Date() );
            UPreferencias.guardarEstatusSync(context, "archivo_documento_entregado", date);
        }
    }

    private void syncRemotaBitacorasCredito() {
        String tableSync = "bitacoras_credito";
        procRemotoBitacorasCredito = new ProcesadorRemotoBitacorasCredito();

        // Construir payload con todas las operaciones remotas pendientes
        Map<String, Object> datos = procRemotoBitacorasCredito.crearPayload(cr);

        if (datos != null) {
            Log.d(TAG, "Payload de bitacoras de crédito: " + datos);

            HashMap<String, String> cabeceras = new HashMap<>();
            cabeceras.put("Authorization", UPreferencias.obtenerClaveApi(context));

            new RESTService(context).post(urlProduccion + URL_SYNC + tableSync, datos,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            procRemotoBitacorasCredito.desmarcar(cr);
                            procRemotoBitacorasCredito.convertir(response,cr);

                            SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd h:mm a");
                            String date = formato.format(new Date() );
                            UPreferencias.guardarEstatusSync(context, "bitacora_credito", date);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            UPreferencias.guardarEstatusSync(context, "bitacora_credito", "Error");
                            tratarErrores(error);
                        }
                    }
                    , cabeceras);
        } else {
            Log.d(TAG, "Sin cambios locales en bitacoras de crédito");

            SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd h:mm a");
            String date = formato.format(new Date() );
            UPreferencias.guardarEstatusSync(context, "bitacora_credito", date);
        }
    }

    private void syncRemotaBitacorasCreditoArchivos() {
        String tableSync = "bitacoras_credito_archivos";
        procRemotoBitacorasCreditoArchivos = new ProcesadorRemotoBitacorasCreditoArchivos();

        // Construir payload con todas las operaciones remotas pendientes
        Map<String, Object> datos = procRemotoBitacorasCreditoArchivos.crearPayload(cr);

        if (datos != null) {
            Log.d(TAG, "Payload de bitacoras de crédito archivos: " + datos);

            HashMap<String, String> cabeceras = new HashMap<>();
            cabeceras.put("Authorization", UPreferencias.obtenerClaveApi(context));

            new RESTService(context).post(urlProduccion + URL_SYNC + tableSync, datos,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            procRemotoBitacorasCreditoArchivos.desmarcar(cr);
                            procRemotoBitacorasCreditoArchivos.convertir(response,cr);

                            String error="";
                            try {
                                JSONObject uploads=response.getJSONObject("archivosUpload");
                                Iterator<?> keys = uploads.keys();
                                while( keys.hasNext() ) {
                                    String urlLocal = (String)keys.next();
                                    String urlRemoto=uploads.get(urlLocal).toString();
                                    if (uploads.get(urlLocal).toString().length()>0) {
                                        UploadFileAsync task = new UploadFileAsync("bitacora_credito_archivo",urlProduccion + URL_SYNC + "bitacora_credito_archivo",urlRemoto,urlLocal);
                                        task.execute();
                                    }
                                }
                            }catch (JSONException ex){
                                Log.d(TAG, "Error al subir archivo: " + ex.getMessage());
                                error="Error";
                            }

                            if(error.isEmpty()){
                                SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd h:mm a");
                                String date = formato.format(new Date() );
                                UPreferencias.guardarEstatusSync(context, "bitacora_credito_archivo", date);
                            }else{
                                UPreferencias.guardarEstatusSync(context, "bitacora_credito_archivo", error);
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            UPreferencias.guardarEstatusSync(context, "bitacora_credito_archivo", "Error");
                            tratarErrores(error);
                        }
                    }
                    , cabeceras);
        } else {
            Log.d(TAG, "Sin cambios locales en bitacoras de crédito archivos");

            SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd h:mm a");
            String date = formato.format(new Date() );
            UPreferencias.guardarEstatusSync(context, "bitacora_credito_archivo", date);
        }
    }

    private void syncRemotaPagos() {
        String tableSync = "pagos";
        procRemotoPagos = new ProcesadorRemotoPagos();

        // Construir payload con todas las operaciones remotas pendientes
        Map<String, Object> datos = procRemotoPagos.crearPayload(cr);

        if (datos != null) {
            Log.d(TAG, "Payload de pagos: " + datos);

            HashMap<String, String> cabeceras = new HashMap<>();
            cabeceras.put("Authorization", UPreferencias.obtenerClaveApi(context));

            new RESTService(context).post(urlProduccion + URL_SYNC + tableSync, datos,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            procRemotoPagos.desmarcar(cr);
                            procRemotoPagos.convertir(response,cr);

                            SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd h:mm a");
                            String date = formato.format(new Date() );
                            UPreferencias.guardarEstatusSync(context, "pago", date);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            UPreferencias.guardarEstatusSync(context, "pago", "Error");
                            tratarErrores(error);
                        }
                    }
                    , cabeceras);
        } else {
            Log.d(TAG, "Sin cambios locales en pagos");

            SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd h:mm a");
            String date = formato.format(new Date() );
            UPreferencias.guardarEstatusSync(context, "pago", date);
        }
    }

    private void tratarErrores(VolleyError error) {
        // Crear respuesta de error por defecto
        RespuestaApi respuesta = new RespuestaApi(ESTADO_PETICION_FALLIDA,
                "Petición incorrecta");


        // Verificación: ¿La respuesta tiene contenido interpretable?
        if (error.networkResponse != null) {

            String s = new String(error.networkResponse.data);
            try {
                respuesta = gson.fromJson(s, RespuestaApi.class);
            } catch (JsonSyntaxException e) {
                Log.d(TAG, "Error de parsing: " + s);
            }

        }

        if (error instanceof NetworkError) {
            respuesta = new RespuestaApi(ESTADO_TIEMPO_ESPERA
                    , "Error en la conexión. Intentalo de nuevo");
        }

        // Error de espera al servidor
        if (error instanceof TimeoutError) {
            respuesta = new RespuestaApi(ESTADO_TIEMPO_ESPERA, "Error de espera del servidor");
        }

        // Error de parsing
        if (error instanceof ParseError) {
            respuesta = new RespuestaApi(ESTADO_ERROR_PARSING, "La respuesta no es formato JSON");
        }

        // Error conexión servidor
        if (error instanceof ServerError) {
            respuesta = new RespuestaApi(ESTADO_ERROR_SERVIDOR, "Error en el servidor");
        }

        if (error instanceof NoConnectionError) {
            respuesta = new RespuestaApi(ESTADO_ERROR_SERVIDOR
                    , "Servidor no disponible, prueba mas tarde");
        }

        Log.d(TAG, "Error Respuesta:" + (respuesta != null ? respuesta.toString() : "()")
                + "\nDetalles:" + error.getMessage());
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
