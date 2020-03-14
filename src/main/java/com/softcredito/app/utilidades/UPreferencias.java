package com.softcredito.app.utilidades;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.softcredito.app.config.Config;

/**
 * Utilidades para obtener y modificar preferencias
 */
public class UPreferencias {
    private static final String PREFERENCIA_CLAVE_API = "preferencia.claveApi";

    private static SharedPreferences getDefaultSharedPreferences(Context contexto) {
        return PreferenceManager.getDefaultSharedPreferences(contexto);
    }

    public static void guardarClaveApi(Context contexto, String claveApi) {
        SharedPreferences sp = getDefaultSharedPreferences(contexto);
        sp.edit().putString(PREFERENCIA_CLAVE_API, claveApi).apply();
    }

    public static String obtenerClaveApi(Context contexto) {
        return getDefaultSharedPreferences(contexto).getString(PREFERENCIA_CLAVE_API, null);
    }

    public static void guardarProduccion(Context contexto, String produccion) {
        SharedPreferences sp = getDefaultSharedPreferences(contexto);
        String last = produccion.substring(produccion.length() - 1);
        if(!last.equals("/")){
            produccion = produccion + "/";
        }
        sp.edit().putString(Config.PRODUCCION_URL, produccion).apply();
    }
    public static String obtenerProduccion(Context contexto) {
        return getDefaultSharedPreferences(contexto).getString(Config.PRODUCCION_URL, null);
    }

    public static void guardarEstatusSync(Context contexto, String tabla, String status) {
        SharedPreferences sp = getDefaultSharedPreferences(contexto);
        sp.edit().putString(Config.ESTATUS_SYNC + tabla, status).apply();
    }
    public static String obtenerEstatusSync(Context contexto, String tabla) {
        return getDefaultSharedPreferences(contexto).getString(Config.ESTATUS_SYNC + tabla, "");
    }
}
