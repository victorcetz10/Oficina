package com.softcredito.app.sync;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.content.SyncResult;
import android.os.Bundle;
import android.os.RemoteException;
//import android.support.v4.content.LocalBroadcastManager;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

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
import com.softcredito.app.modelo.ProcesadorLocalBitacorasCreditoArchivos;
import com.softcredito.app.modelo.ProcesadorLocalCanalesCobranzas;
import com.softcredito.app.modelo.ProcesadorLocalClientes;
import com.softcredito.app.modelo.ProcesadorLocalGrupos;
import com.softcredito.app.modelo.ProcesadorLocalInstrumentosMonetarios;
import com.softcredito.app.modelo.ProcesadorLocalTiposPersonas;
import com.softcredito.app.modelo.ProcesadorLocalTiposContactos;
import com.softcredito.app.modelo.ProcesadorLocalTiposDocumentos;
import com.softcredito.app.modelo.ProcesadorLocalDocumentosRequeridos;
import com.softcredito.app.modelo.ProcesadorLocalDocumentosEntregados;
import com.softcredito.app.modelo.ProcesadorLocalArchivosDocumentosEntregados;
import com.softcredito.app.modelo.ProcesadorLocalProductos;
import com.softcredito.app.modelo.ProcesadorLocalBancos;
import com.softcredito.app.modelo.ProcesadorLocalTiposPagos;
import com.softcredito.app.modelo.ProcesadorLocalTiposAmortizacion;
import com.softcredito.app.modelo.ProcesadorLocalSolicitudes;
import com.softcredito.app.modelo.ProcesadorLocalCotizadores;
import com.softcredito.app.modelo.ProcesadorLocalBitacorasCredito;
import com.softcredito.app.modelo.ProcesadorLocalCodigosPostales;
import com.softcredito.app.modelo.ProcesadorLocalCodigos;
import com.softcredito.app.modelo.ProcesadorLocalPaises;
import com.softcredito.app.modelo.ProcesadorLocalEstados;
import com.softcredito.app.modelo.ProcesadorLocalMunicipios;
import com.softcredito.app.modelo.ProcesadorLocalEstadosCiviles;
import com.softcredito.app.modelo.ProcesadorLocalCategoriasActividadesEconomicas;
import com.softcredito.app.modelo.ProcesadorLocalActividadesEconomicas;
import com.softcredito.app.modelo.ProcesadorRemoto;
import com.softcredito.app.modelo.ProcesadorRemotoCotizadores;
import com.softcredito.app.modelo.ProcesadorRemotoDocumentosEntregados;
import com.softcredito.app.modelo.ProcesadorRemotoArchivosDocumentosEntregados;
import com.softcredito.app.modelo.ProcesadorRemotoSolicitudes;
import com.softcredito.app.modelo.ProcesadorRemotoBitacorasCredito;
import com.softcredito.app.modelo.ProcesadorRemotoBitacorasCreditoArchivos;
import com.softcredito.app.modelo.ProcesadorRemotoPagos;
import com.softcredito.app.provider.Contract;
import com.softcredito.app.provider.Contract.Clientes;


import com.softcredito.app.utilidades.UPreferencias;
import com.softcredito.app.web.RESTService;
import com.softcredito.app.web.RespuestaApi;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Sincronizador cliente-servidor
 */
public class SyncAdapter extends AbstractThreadedSyncAdapter {

    private static final String TAG = SyncAdapter.class.getSimpleName();

    // Extras para intent local
    public static final String EXTRA_RESULTADO = "extra.resultado";
    private static final String EXTRA_MENSAJE = "extra.mensaje";

    // Recurso sync (10.0.3.2 -> Genymotion; 10.0.2.2 -> AVD)
    public static final String URL_SYNC = "WebService/index.php/Welcome/";

    public static final String SYNC_TABLE = "extra.tableSync";//Tabla que se actualiza
    public static final String SYNC_TABLE_MULTIPLE = "extra.tableMultipleSync";//Tablas que se actualizan
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

    private String tableMultiple;
    private String table;
    private String row;

    private ArrayList terminados=new ArrayList();

    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        cr = context.getContentResolver();
    }

    /**
     * Constructor para mantener compatibilidad en versiones inferiores a 3.0
     */
    public SyncAdapter(
            Context context,
            boolean autoInitialize,
            boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        cr = context.getContentResolver();
    }

    @Override
    public void onPerformSync(Account account,
                              Bundle extras,
                              String authority,
                              ContentProviderClient provider,
                              final SyncResult syncResult) {

        Log.i(TAG, "Comenzando a sincronizar:" + account);

        urlProduccion = UPreferencias.obtenerProduccion(getContext());

        tableMultiple = extras.getString(SYNC_TABLE_MULTIPLE);
        table = extras.getString(SYNC_TABLE);
        row = extras.getString(SYNC_TABLE_ROW);
        // Sincronización local
        if(table.equals("cotizadores")){
            syncRemotaCotizador();
        }else{
            if(tableMultiple!=null){
                syncLocal();
            }
        }

    }

    private void syncLocal() {
    	//0: Iniciando, 1,2,3,...,etc.: Porcentaje, 2019-09-17 06:50 p.m.: finalizado
        if(tableMultiple.contains("|tipos_personas|")){
            UPreferencias.guardarEstatusSync(getContext(), "tipos_personas", "0");
        }
        if(tableMultiple.contains("|tipos_documentos|")){
            UPreferencias.guardarEstatusSync(getContext(), "tipos_documentos", "0");
        }
        if(tableMultiple.contains("|tipos_pagos|")){
            UPreferencias.guardarEstatusSync(getContext(), "tipos_pagos", "0");
        }
        if(tableMultiple.contains("|tipos_amortizacion|")){
            UPreferencias.guardarEstatusSync(getContext(), "tipos_amortizacion", "0");
        }
        if(tableMultiple.contains("|instrumentos_monetarios|")){
            UPreferencias.guardarEstatusSync(getContext(), "instrumentos_monetarios", "0");
        }
        if(tableMultiple.contains("|codigos_postales|")){
            UPreferencias.guardarEstatusSync(getContext(), "codigos_postales", "0");
        }
        if(tableMultiple.contains("|codigo|")){
            UPreferencias.guardarEstatusSync(getContext(), "codigo", "0");
        }
        if(tableMultiple.contains("|paises|")){
            UPreferencias.guardarEstatusSync(getContext(), "paises", "0");
        }
        if(tableMultiple.contains("|estados|")){
            UPreferencias.guardarEstatusSync(getContext(), "estados", "0");
        }
        if(tableMultiple.contains("|municipios|")){
            UPreferencias.guardarEstatusSync(getContext(), "municipios", "0");
        }
        if(tableMultiple.contains("|estados_civiles|")){
            UPreferencias.guardarEstatusSync(getContext(), "estados_civiles", "0");
        }
        if(tableMultiple.contains("|categorias_actividades_economicas|")){
            UPreferencias.guardarEstatusSync(getContext(), "categorias_actividades_economicas", "0");
        }
        if(tableMultiple.contains("|actividades_economicas|")){
            UPreferencias.guardarEstatusSync(getContext(), "actividades_economicas", "0");
        }
        //CONFIGURACIONES
        if(tableMultiple.contains("|grupos|")){
            UPreferencias.guardarEstatusSync(getContext(), "grupos", "0");
        }
        if(tableMultiple.contains("|tipos_contactos|")){
            UPreferencias.guardarEstatusSync(getContext(), "tipos_contactos", "0");
        }
        if(tableMultiple.contains("|documentos_requeridos|")){
            UPreferencias.guardarEstatusSync(getContext(), "documentos_requeridos", "0");
        }
        if(tableMultiple.contains("|productos|")){
            UPreferencias.guardarEstatusSync(getContext(), "productos", "0");
        }
        if(tableMultiple.contains("|bancos|")){
            UPreferencias.guardarEstatusSync(getContext(), "bancos", "0");
        }
        if(tableMultiple.contains("|canales_cobranza|")){
            UPreferencias.guardarEstatusSync(getContext(), "canales_cobranza", "0");
        }
        //OPERACION
        if(tableMultiple.contains("|cliente|")){
            UPreferencias.guardarEstatusSync(getContext(), "cliente", "0");
        }
        if(tableMultiple.contains("|documentos_entregados|")){
            UPreferencias.guardarEstatusSync(getContext(), "documentos_entregados", "0");
        }
        if(tableMultiple.contains("|archivos_documentos_entregados|")){
            UPreferencias.guardarEstatusSync(getContext(), "archivos_documentos_entregados", "0");
        }
        if(tableMultiple.contains("|cotizador|")){
            UPreferencias.guardarEstatusSync(getContext(), "cotizador", "0");
        }
        if(tableMultiple.contains("|solicitudes|")){
            UPreferencias.guardarEstatusSync(getContext(), "solicitudes", "0");
        }
        if(tableMultiple.contains("|bitacoras_credito|")){
            UPreferencias.guardarEstatusSync(getContext(), "bitacoras_credito", "0");
        }
        if(tableMultiple.contains("|bitacoras_credito_archivos|")){
            UPreferencias.guardarEstatusSync(getContext(), "bitacoras_credito_archivos", "0");
        }
        if(tableMultiple.contains("|pagos|")){
            UPreferencias.guardarEstatusSync(getContext(), "pagos", "0");
        }

        // Construcción de cabeceras
        HashMap<String, String> cabeceras = new HashMap<>();
        cabeceras.put("Authorization", UPreferencias.obtenerClaveApi(getContext()));

        //CATALOGOS

        // Petición GET
        if(tableMultiple.contains("|tipos_personas|")){
            new RESTService(getContext()).get(urlProduccion + URL_SYNC + "tipos_personas",
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            // Procesar GET
                            tratarGet(response,"tipos_personas");
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // Procesar Error
                            tratarErrores(error);
                        }
                    }, cabeceras);
        }


        // Petición GET
        if(tableMultiple.contains("|tipos_documentos|")){
            new RESTService(getContext()).get(urlProduccion + URL_SYNC + "tipos_documentos",
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            // Procesar GET
                            tratarGet(response,"tipos_documentos");
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // Procesar Error
                            tratarErrores(error);
                        }
                    }, cabeceras);
        }

        // Petición GET
        if(tableMultiple.contains("|tipos_pagos|")){
            new RESTService(getContext()).get(urlProduccion + URL_SYNC + "tipos_pagos",
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            // Procesar GET
                            tratarGet(response,"tipos_pagos");
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // Procesar Error
                            tratarErrores(error);
                        }
                    }, cabeceras);
        }


        // Petición GET
        if(tableMultiple.contains("|tipos_amortizacion|")){
            new RESTService(getContext()).get(urlProduccion + URL_SYNC + "tipos_amortizacion",
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            // Procesar GET
                            tratarGet(response,"tipos_amortizacion");
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // Procesar Error
                            tratarErrores(error);
                        }
                    }, cabeceras);
        }

        // Petición GET
        if(tableMultiple.contains("|instrumentos_monetarios|")){
            new RESTService(getContext()).get(urlProduccion + URL_SYNC + "instrumentos_monetarios",
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            // Procesar GET
                            tratarGet(response,"instrumentos_monetarios");
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // Procesar Error
                            tratarErrores(error);
                        }
                    }, cabeceras);
        }

        // Petición GET
        if(tableMultiple.contains("|codigos_postales|")){
            new RESTService(getContext()).get(urlProduccion + URL_SYNC + "codigos_postales",
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            // Procesar GET
                            tratarGet(response,"codigos_postales");
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // Procesar Error
                            tratarErrores(error);
                        }
                    }, cabeceras);
        }

        // Petición GET
        if(tableMultiple.contains("|codigo|")){
            new RESTService(getContext()).get(urlProduccion + URL_SYNC + "codigo",
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            // Procesar GET
                            tratarGet(response,"codigo");
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // Procesar Error
                            tratarErrores(error);
                        }
                    }, cabeceras);
        }

        // Petición GET
        if(tableMultiple.contains("|paises|")){
            new RESTService(getContext()).get(urlProduccion + URL_SYNC + "paises",
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            // Procesar GET
                            tratarGet(response,"paises");
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // Procesar Error
                            tratarErrores(error);
                        }
                    }, cabeceras);
        }

        // Petición GET
        if(tableMultiple.contains("|estados|")){
            new RESTService(getContext()).get(urlProduccion + URL_SYNC + "estados",
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            // Procesar GET
                            tratarGet(response,"estados");
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // Procesar Error
                            tratarErrores(error);
                        }
                    }, cabeceras);
        }

		// Petición GET
        if(tableMultiple.contains("|municipios|")){
            new RESTService(getContext()).get(urlProduccion + URL_SYNC + "municipios",
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            // Procesar GET
                            tratarGet(response,"municipios");
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // Procesar Error
                            tratarErrores(error);
                        }
                    }, cabeceras);
        }

        // Petición GET
        if(tableMultiple.contains("|estados_civiles|")){
            new RESTService(getContext()).get(urlProduccion + URL_SYNC + "estados_civiles",
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            // Procesar GET
                            tratarGet(response,"estados_civiles");
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // Procesar Error
                            tratarErrores(error);
                        }
                    }, cabeceras);
        }

        // Petición GET
        if(tableMultiple.contains("|categorias_actividades_economicas|")){
            new RESTService(getContext()).get(urlProduccion + URL_SYNC + "categorias_actividades_economicas",
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            // Procesar GET
                            tratarGet(response,"categorias_actividades_economicas");
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // Procesar Error
                            tratarErrores(error);
                        }
                    }, cabeceras);
        }

        // Petición GET
        if(tableMultiple.contains("|actividades_economicas|")){
            new RESTService(getContext()).get(urlProduccion + URL_SYNC + "actividades_economicas",
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            // Procesar GET
                            tratarGet(response,"actividades_economicas");
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // Procesar Error
                            tratarErrores(error);
                        }
                    }, cabeceras);
        }






        //CONFIGURACIONES
        // Petición GET
        if(tableMultiple.contains("|grupos|")){
            new RESTService(getContext()).get(urlProduccion + URL_SYNC + "grupos",
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            // Procesar GET
                            tratarGet(response,"grupos");
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // Procesar Error
                            tratarErrores(error);
                        }
                    }, cabeceras);
        }


        // Petición GET
        if(tableMultiple.contains("|tipos_contactos|")){
            new RESTService(getContext()).get(urlProduccion + URL_SYNC + "tipos_contactos",
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            // Procesar GET
                            tratarGet(response,"tipos_contactos");
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // Procesar Error
                            tratarErrores(error);
                        }
                    }, cabeceras);
        }


        // Petición GET
        if(tableMultiple.contains("|documentos_requeridos|")){
            new RESTService(getContext()).get(urlProduccion + URL_SYNC + "documentos_requeridos",
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            // Procesar GET
                            tratarGet(response,"documentos_requeridos");
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // Procesar Error
                            tratarErrores(error);
                        }
                    }, cabeceras);
        }

        // Petición GET
        if(tableMultiple.contains("|productos|")){
            new RESTService(getContext()).get(urlProduccion + URL_SYNC + "productos",
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            // Procesar GET
                            tratarGet(response,"productos");
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // Procesar Error
                            tratarErrores(error);
                        }
                    }, cabeceras);
        }

        // Petición GET
        if(tableMultiple.contains("|bancos|")){
            new RESTService(getContext()).get(urlProduccion + URL_SYNC + "bancos",
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            // Procesar GET
                            tratarGet(response,"bancos");
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // Procesar Error
                            tratarErrores(error);
                        }
                    }, cabeceras);
        }


        // Petición GET
        if(tableMultiple.contains("|canales_cobranza|")){
            new RESTService(getContext()).get(urlProduccion + URL_SYNC + "canales_cobranza",
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            // Procesar GET
                            tratarGet(response,"canales_cobranza");
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // Procesar Error
                            tratarErrores(error);
                        }
                    }, cabeceras);
        }

        //OPERACION
        // Petición GET
        if(tableMultiple.contains("|cliente|")){
            new RESTService(getContext()).get(urlProduccion + URL_SYNC + "cliente",
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            // Procesar GET
                            tratarGet(response,"cliente");
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // Procesar Error
                            tratarErrores(error);
                        }
                    }, cabeceras);
        }

        // Petición GET
        if(tableMultiple.contains("|documentos_entregados|")){
            new RESTService(getContext()).get(urlProduccion + URL_SYNC + "documentos_entregados",
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            // Procesar GET
                            tratarGet(response,"documentos_entregados");
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // Procesar Error
                            tratarErrores(error);
                        }
                    }, cabeceras);
        }

        // Petición GET
        if(tableMultiple.contains("|archivos_documentos_entregados|")){
            new RESTService(getContext()).get(urlProduccion + URL_SYNC + "archivos_documentos_entregados",
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            // Procesar GET
                            tratarGet(response,"archivos_documentos_entregados");
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // Procesar Error
                            tratarErrores(error);
                        }
                    }, cabeceras);
        }

        // Petición GET
        if(tableMultiple.contains("|cotizador|")){
            new RESTService(getContext()).get(urlProduccion + URL_SYNC + "cotizador",
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            // Procesar GET
                            tratarGet(response,"cotizador");
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // Procesar Error
                            tratarErrores(error);
                        }
                    }, cabeceras);
        }

        // Petición GET
        if(tableMultiple.contains("|solicitudes|")){
            new RESTService(getContext()).get(urlProduccion + URL_SYNC + "solicitudes",
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            // Procesar GET
                            tratarGet(response,"solicitudes");
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // Procesar Error
                            tratarErrores(error);
                        }
                    }, cabeceras);
        }

        // Petición GET
        if(tableMultiple.contains("|bitacoras_credito|")){
            new RESTService(getContext()).get(urlProduccion + URL_SYNC + "bitacoras_credito",
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            // Procesar GET
                            tratarGet(response,"bitacoras_credito");
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // Procesar Error
                            tratarErrores(error);
                        }
                    }, cabeceras);
        }

        // Petición GET
        if(tableMultiple.contains("|bitacoras_credito_archivos|")){
            new RESTService(getContext()).get(urlProduccion + URL_SYNC + "bitacoras_credito_archivos",
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            // Procesar GET
                            tratarGet(response,"bitacoras_credito_archivos");
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // Procesar Error
                            tratarErrores(error);
                        }
                    }, cabeceras);
        }

        // Petición GET
        if(tableMultiple.contains("|pagos|")){
            new RESTService(getContext()).get(urlProduccion + URL_SYNC + "pagos",
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            // Procesar GET
                            tratarGet(response,"pagos");
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // Procesar Error
                            tratarErrores(error);
                        }
                    }, cabeceras);
        }
    }

    private void tratarGet(JSONObject respuesta,String tabla){
        AsyncSincronizacion tarea = new AsyncSincronizacion(getContext(),tabla,respuesta);
        tarea.execute();
    }

    private void tratarGetOld(JSONObject respuesta, String tabla) {

        try {
            ArrayList<ContentProviderOperation> operaciones = new ArrayList<>();
            switch (tabla){
                case "cliente":
                    ProcesadorLocalClientes manejador1 = new ProcesadorLocalClientes();
                    manejador1.procesar(respuesta.getJSONArray(tabla));
                    manejador1.procesarOperaciones(operaciones, cr);
                    break;
                case "grupos":
                    ProcesadorLocalGrupos manejador2 = new ProcesadorLocalGrupos();
                    manejador2.procesar(respuesta.getJSONArray(tabla));
                    manejador2.procesarOperaciones(operaciones, cr);
                    break;
                case "tipos_personas":
                    ProcesadorLocalTiposPersonas manejador3 = new ProcesadorLocalTiposPersonas();
                    manejador3.procesar(respuesta.getJSONArray(tabla));
                    manejador3.procesarOperaciones(operaciones, cr);
                    break;
                case "tipos_contactos":
                    ProcesadorLocalTiposContactos manejador4 = new ProcesadorLocalTiposContactos();
                    manejador4.procesar(respuesta.getJSONArray(tabla));
                    manejador4.procesarOperaciones(operaciones, cr);
                    break;
                case "tipos_documentos":
                    ProcesadorLocalTiposDocumentos manejador5 = new ProcesadorLocalTiposDocumentos();
                    manejador5.procesar(respuesta.getJSONArray(tabla));
                    manejador5.procesarOperaciones(operaciones, cr);
                    break;
                case "documentos_requeridos":
                    ProcesadorLocalDocumentosRequeridos manejador6 = new ProcesadorLocalDocumentosRequeridos();
                    manejador6.procesar(respuesta.getJSONArray(tabla));
                    manejador6.procesarOperaciones(operaciones, cr);
                    break;
                case "documentos_entregados":
                    ProcesadorLocalDocumentosEntregados manejador7 = new ProcesadorLocalDocumentosEntregados();
                    manejador7.procesar(respuesta.getJSONArray(tabla));
                    manejador7.procesarOperaciones(operaciones, cr);
                    break;
                case "archivos_documentos_entregados":
                    ProcesadorLocalArchivosDocumentosEntregados manejador8 = new ProcesadorLocalArchivosDocumentosEntregados();
                    manejador8.procesar(respuesta.getJSONArray(tabla));
                    manejador8.procesarOperaciones(operaciones, cr);
                    break;
                case "productos":
                    ProcesadorLocalProductos manejador9 = new ProcesadorLocalProductos();
                    manejador9.procesar(respuesta.getJSONArray(tabla));
                    manejador9.procesarOperaciones(operaciones, cr);
                    break;
                case "bancos":
                    ProcesadorLocalBancos manejador10 = new ProcesadorLocalBancos();
                    manejador10.procesar(respuesta.getJSONArray(tabla));
                    manejador10.procesarOperaciones(operaciones, cr);
                    break;
                case "tipos_pagos":
                    ProcesadorLocalTiposPagos manejador11 = new ProcesadorLocalTiposPagos();
                    manejador11.procesar(respuesta.getJSONArray(tabla));
                    manejador11.procesarOperaciones(operaciones, cr);
                    break;
                case "tipos_amortizacion":
                    ProcesadorLocalTiposAmortizacion manejador12 = new ProcesadorLocalTiposAmortizacion();
                    manejador12.procesar(respuesta.getJSONArray(tabla));
                    manejador12.procesarOperaciones(operaciones, cr);
                    break;
                case "solicitudes":
                    ProcesadorLocalSolicitudes manejador13 = new ProcesadorLocalSolicitudes();
                    manejador13.procesar(respuesta.getJSONArray(tabla));
                    manejador13.procesarOperaciones(operaciones, cr);
                    break;
                case "cotizador":
                    ProcesadorLocalCotizadores manejador14 = new ProcesadorLocalCotizadores();
                    manejador14.procesar(respuesta.getJSONArray(tabla));
                    manejador14.procesarOperaciones(operaciones, cr);
                    break;
                case "bitacoras_credito":
                    ProcesadorLocalBitacorasCredito manejador15 = new ProcesadorLocalBitacorasCredito();
                    manejador15.procesar(respuesta.getJSONArray(tabla));
                    manejador15.procesarOperaciones(operaciones, cr);
                    break;
                case "bitacoras_credito_archivos":
                    ProcesadorLocalBitacorasCreditoArchivos manejador16 = new ProcesadorLocalBitacorasCreditoArchivos();
                    manejador16.procesar(respuesta.getJSONArray(tabla));
                    manejador16.procesarOperaciones(operaciones, cr);
                    break;
                case "instrumentos_monetarios":
                    ProcesadorLocalInstrumentosMonetarios manejador17 = new ProcesadorLocalInstrumentosMonetarios();
                    manejador17.procesar(respuesta.getJSONArray(tabla));
                    manejador17.procesarOperaciones(operaciones, cr);
                    break;
                case "canales_cobranza":
                    ProcesadorLocalCanalesCobranzas manejador18 = new ProcesadorLocalCanalesCobranzas();
                    manejador18.procesar(respuesta.getJSONArray(tabla));
                    manejador18.procesarOperaciones(operaciones, cr);
                    break;
                case "codigos_postales":
                    ProcesadorLocalCodigosPostales manejador19 = new ProcesadorLocalCodigosPostales();
                    manejador19.procesar(respuesta.getJSONArray(tabla));
                    manejador19.procesarOperaciones(operaciones, cr);
                    break;
                case "codigo":
                    ProcesadorLocalCodigos manejador20 = new ProcesadorLocalCodigos();
                    manejador20.procesar(respuesta.getJSONArray(tabla));
                    manejador20.procesarOperaciones(operaciones, cr);
                    break;
                case "paises":
                    ProcesadorLocalPaises manejador21 = new ProcesadorLocalPaises();
                    manejador21.procesar(respuesta.getJSONArray(tabla));
                    manejador21.procesarOperaciones(operaciones, cr);
                    break;
                case "estados":
                    ProcesadorLocalEstados manejador22 = new ProcesadorLocalEstados();
                    manejador22.procesar(respuesta.getJSONArray(tabla));
                    manejador22.procesarOperaciones(operaciones, cr);
                    break;
                case "municipios":
                    ProcesadorLocalMunicipios manejador23 = new ProcesadorLocalMunicipios();
                    manejador23.procesar(respuesta.getJSONArray(tabla));
                    manejador23.procesarOperaciones(operaciones, cr);
                    break;
                case "estados_civiles":
                    ProcesadorLocalEstadosCiviles manejador24 = new ProcesadorLocalEstadosCiviles();
                    manejador24.procesar(respuesta.getJSONArray(tabla));
                    manejador24.procesarOperaciones(operaciones, cr);
                    break;
                case "categorias_actividades_economicas":
                    ProcesadorLocalCategoriasActividadesEconomicas manejador25 = new ProcesadorLocalCategoriasActividadesEconomicas();
                    manejador25.procesar(respuesta.getJSONArray(tabla));
                    manejador25.procesarOperaciones(operaciones, cr);
                    break;
                case "actividades_economicas":
                    ProcesadorLocalActividadesEconomicas manejador26 = new ProcesadorLocalActividadesEconomicas();
                    manejador26.procesar(respuesta.getJSONArray(tabla));
                    manejador26.procesarOperaciones(operaciones, cr);
                    break;
            }

            if(operaciones.size() == 0){
                //Se colocan todos terminados (17 ya que no se incluye el cotizador)

                Log.d(TAG, "Sin cambios remotos " + tabla);
            }else{
                //Si hay operaciones que procesar
                if (operaciones.size() > 0) {
                    Log.d(TAG, "# Cambios en \'" + tabla + "\': " + operaciones.size() + " ops.");
                    // Aplicar batch de operaciones
                    cr.applyBatch(Contract.AUTORIDAD, operaciones);
                    // Notificar cambio al content provider
                    cr.notifyChange(Clientes.URI_CONTENIDO, null, false);
                }
            }
            // Sincronización remota
            if(tabla=="cliente"){
                syncRemota();
            }else if(tabla=="documentos_entregados"){
                syncRemotaDocumentosEntregados();
            }else if(tabla=="archivos_documentos_entregados"){
                syncRemotaArchivosDocumentosEntregados();
            }else if(tabla=="solicitudes"){
                syncRemotaSolicitudes();
            }else if(tabla=="bitacoras_credito"){
                syncRemotaBitacorasCredito();
            }else if(tabla=="bitacoras_credito_archivos"){
                syncRemotaBitacorasCreditoArchivos();
            }else if(tabla=="pagos"){
                syncRemotaPagos();
            }else{
                SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd h:mm a");
                String date = formato.format(new Date() );
                UPreferencias.guardarEstatusSync(getContext(), tabla, date);
            }
        } catch (RemoteException | OperationApplicationException | JSONException e) {
            e.printStackTrace();
        }
    }

    private void syncRemota() {
        procRemoto = new ProcesadorRemoto();

        // Construir payload con todas las operaciones remotas pendientes
        Map<String,Object> datos = procRemoto.crearPayload(cr);

        if (datos != null) {
            Log.d(TAG, "Payload de " + table + ": " + datos);

            HashMap<String, String> cabeceras = new HashMap<>();
            cabeceras.put("Authorization", UPreferencias.obtenerClaveApi(getContext()));

            new RESTService(getContext()).post(urlProduccion + URL_SYNC + table, datos,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            procRemoto.desmarcarClientes(cr);
                            procRemoto.convertirClientes(response,cr);

                            SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd h:mm a");
                            String date = formato.format(new Date() );
                            UPreferencias.guardarEstatusSync(getContext(), "cliente", date);

                            syncRemotaSolicitudes();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            tratarErrores(error);
                        }
                    }
                    , cabeceras);
        } else {
            Log.d(TAG, "Sin cambios locales en clientes");

            SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd h:mm a");
            String date = formato.format(new Date() );
            UPreferencias.guardarEstatusSync(getContext(), "cliente", date);

            syncRemotaSolicitudes();
        }
    }

    private void syncRemotaSolicitudes() {
        final String tableSync = "solicitudes";
        procRemotoSolicitudes = new ProcesadorRemotoSolicitudes();

        // Construir payload con todas las operaciones remotas pendientes
        final Map<String, Object> datos = procRemotoSolicitudes.crearPayload(cr);

        if (datos != null) {
            Log.d(TAG, "Payload de solicitudes: " + datos);

            HashMap<String, String> cabeceras = new HashMap<>();
            cabeceras.put("Authorization", UPreferencias.obtenerClaveApi(getContext()));

            new RESTService(getContext()).post(urlProduccion + URL_SYNC + tableSync, datos,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            procRemotoSolicitudes.desmarcar(cr);
                            procRemotoSolicitudes.convertir(response,cr);
                            Log.e ("1", urlProduccion + URL_SYNC + tableSync + datos );

                            SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd h:mm a");
                            String date = formato.format(new Date() );
                            UPreferencias.guardarEstatusSync(getContext(), "solicitudes", date);

                            syncRemotaDocumentosEntregados();
                            syncRemotaBitacorasCredito();
                            syncRemotaPagos();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            tratarErrores(error);
                        }
                    }
                    , cabeceras);
        } else {
            Log.d(TAG, "Sin cambios locales en solicitudes");

            SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd h:mm a");
            String date = formato.format(new Date() );
            UPreferencias.guardarEstatusSync(getContext(), "solicitudes", date);

            syncRemotaDocumentosEntregados();
            syncRemotaBitacorasCredito();
            syncRemotaPagos();

            enviarBroadcast(true, "Sincronización completa",null);
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
            cabeceras.put("Authorization", UPreferencias.obtenerClaveApi(getContext()));

            new RESTService(getContext()).post(urlProduccion + URL_SYNC + tableSync, datos,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            procRemotoDocumentosEntregados.desmarcar(cr);
                            procRemotoDocumentosEntregados.convertir(response,cr);

                            SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd h:mm a");
                            String date = formato.format(new Date() );
                            UPreferencias.guardarEstatusSync(getContext(), "documentos_entregados", date);

                            syncRemotaArchivosDocumentosEntregados();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            tratarErrores(error);
                        }
                    }
                    , cabeceras);
        } else {
            Log.d(TAG, "Sin cambios locales en documentos entregados");

            SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd h:mm a");
            String date = formato.format(new Date() );
            UPreferencias.guardarEstatusSync(getContext(), "documentos_entregados", date);

            syncRemotaArchivosDocumentosEntregados();
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
            cabeceras.put("Authorization", UPreferencias.obtenerClaveApi(getContext()));

            new RESTService(getContext()).post(urlProduccion + URL_SYNC + tableSync, datos,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            procRemotoArchivosDocumentosEntregados.desmarcar(cr);
                            procRemotoArchivosDocumentosEntregados.convertir(response,cr);

                            SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd h:mm a");
                            String date = formato.format(new Date() );
                            UPreferencias.guardarEstatusSync(getContext(), "archivos_documentos_entregados", date);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            tratarErrores(error);
                        }
                    }
                    , cabeceras);
        } else {
            Log.d(TAG, "Sin cambios locales en archivos de documentos entregados");

            SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd h:mm a");
            String date = formato.format(new Date() );
            UPreferencias.guardarEstatusSync(getContext(), "archivos_documentos_entregados", date);
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
            cabeceras.put("Authorization", UPreferencias.obtenerClaveApi(getContext()));

            new RESTService(getContext()).post(urlProduccion + URL_SYNC + tableSync, datos,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            procRemotoBitacorasCredito.desmarcar(cr);
                            procRemotoBitacorasCredito.convertir(response,cr);

                            SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd h:mm a");
                            String date = formato.format(new Date() );
                            UPreferencias.guardarEstatusSync(getContext(), "bitacoras_credito", date);

                            syncRemotaBitacorasCreditoArchivos();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            tratarErrores(error);
                        }
                    }
                    , cabeceras);
        } else {
            Log.d(TAG, "Sin cambios locales en bitacoras de crédito");

            SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd h:mm a");
            String date = formato.format(new Date() );
            UPreferencias.guardarEstatusSync(getContext(), "bitacoras_credito", date);

            syncRemotaBitacorasCreditoArchivos();
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
            cabeceras.put("Authorization", UPreferencias.obtenerClaveApi(getContext()));

            new RESTService(getContext()).post(urlProduccion + URL_SYNC + tableSync, datos,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            procRemotoBitacorasCreditoArchivos.desmarcar(cr);
                            procRemotoBitacorasCreditoArchivos.convertir(response,cr);

                            SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd h:mm a");
                            String date = formato.format(new Date() );
                            UPreferencias.guardarEstatusSync(getContext(), "bitacoras_credito_archivos", date);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            tratarErrores(error);
                        }
                    }
                    , cabeceras);
        } else {
            Log.d(TAG, "Sin cambios locales en bitacoras de crédito archivos");

            SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd h:mm a");
            String date = formato.format(new Date() );
            UPreferencias.guardarEstatusSync(getContext(), "bitacoras_credito_archivos", date);
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
            cabeceras.put("Authorization", UPreferencias.obtenerClaveApi(getContext()));

            new RESTService(getContext()).post(urlProduccion + URL_SYNC + tableSync, datos,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            procRemotoPagos.desmarcar(cr);
                            procRemotoPagos.convertir(response,cr);

                            SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd h:mm a");
                            String date = formato.format(new Date() );
                            UPreferencias.guardarEstatusSync(getContext(), "pagos", date);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            tratarErrores(error);
                        }
                    }
                    , cabeceras);
        } else {
            Log.d(TAG, "Sin cambios locales en pagos");

            SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd h:mm a");
            String date = formato.format(new Date() );
            UPreferencias.guardarEstatusSync(getContext(), "pagos", date);
        }
    }

    private void syncRemotaCotizador() {
        final ProcesadorRemotoCotizadores procRemotoCotizador = new ProcesadorRemotoCotizadores();

        // Construir payload con todas las operaciones remotas pendientes
        Map<String, Object> datos = procRemotoCotizador.crearPayload(cr,row);

        if (datos != null) {
            Log.d(TAG, "Payload de " + table + ": " + datos);

            HashMap<String, String> cabeceras = new HashMap<>();
            cabeceras.put("Authorization", UPreferencias.obtenerClaveApi(getContext()));

            new RESTService(getContext()).post(urlProduccion + URL_SYNC + table, datos,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            procRemotoCotizador.desmarcarCotizadores(cr);
                            procRemotoCotizador.convertirCotizadores(response,cr);
                            procRemotoCotizador.actualizarCotizadores(response,cr);
                            procRemotoCotizador.generarPDFCotizadores(response,cr);

                            //Las conversiones son las equivalencias entre el id local y el remoto
                            //El id remoto de utiliza al guardar el pdf de la cotización
                            //En este caso siempre es una sola cotización que se sincroniza
                            JSONObject oConversiones=null;
                            String conversiones="";

                            try {
                                if(!response.isNull("conversiones")){
                                    oConversiones=response.getJSONObject("conversiones");
                                    conversiones=oConversiones.toString();
                                }
                            }catch (JSONException e){

                            }


                            enviarBroadcast(true, "Sincronización completa",conversiones);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            tratarErrores(error);
                        }
                    }
                    , cabeceras);
        } else {
            Log.d(TAG, "Sin cambios locales");
            enviarBroadcast(true, "Sincronización completa",null);
        }
    }


    private void enviarBroadcast(boolean estado, String mensaje, String conversiones) {
        Intent intentLocal = new Intent(Intent.ACTION_SYNC);
        intentLocal.putExtra(EXTRA_RESULTADO, estado);
        intentLocal.putExtra(EXTRA_MENSAJE, mensaje);
        intentLocal.putExtra(SYNC_TABLE, table);
        intentLocal.putExtra(SYNC_TABLE_ROW, row);
        intentLocal.putExtra(SYNC_CONVERSIONES, conversiones);
        LocalBroadcastManager.getInstance(getContext()).sendBroadcast(intentLocal);
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

        enviarBroadcast(false, respuesta.getMensaje(),null);

    }
}