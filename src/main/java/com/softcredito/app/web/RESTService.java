package com.softcredito.app.web;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.softcredito.app.web.ObjectRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Representa el acceso al servicio web REST del servidor
 */
public class RESTService {

    private static final String TAG = RESTService.class.getSimpleName();

    private final Context contexto;

    private Gson gson = new Gson();

    public RESTService(Context contexto) {
        this.contexto = contexto;
    }

    public void get(String uri, Response.Listener<JSONObject> jsonListener,
                    Response.ErrorListener errorListener,
                    final HashMap<String, String> cabeceras) {

        // Crear petición GET
        ObjectRequest peticion = new ObjectRequest<JSONObject> (
                Request.Method.GET,
                uri,
                null,
                jsonListener,
                errorListener
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return cabeceras;
            }
        };

        // Añadir petición a la pila
        VolleySingleton.getInstance(contexto).addToRequestQueue(peticion);
    }

    public void post(String uri, Map<String,Object> datos, Response.Listener<JSONObject> jsonListener,
                     Response.ErrorListener errorListener, final HashMap<String, String> cabeceras) {

        // Crear petición POST
        try {
            ObjectRequest peticion = new ObjectRequest<JSONObject>(
                    Request.Method.POST,
                    uri,
                    datos,
                    jsonListener,
                    errorListener
            ) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    return cabeceras;
                }
            };
            // Añadir petición a la pila
            VolleySingleton.getInstance(contexto).addToRequestQueue(peticion);
        } catch (Throwable tx) {
            Log.d(TAG, "Error de parsing: ");
        }
    }

}
