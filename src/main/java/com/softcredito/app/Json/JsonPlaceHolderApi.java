package com.softcredito.app.Json;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface JsonPlaceHolderApi {

    @GET("bancos")
    Call<List<BancoCarg>> bancos();

    @GET("tipos_personas")
    Call<List<Tipos_Personas>> TiposPersonas();

    @GET("paises")
    Call<List<Paisessss>> Paisessss();

    @GET("estados")
    Call<List<Estadosssss>> Estadosssss();

    @GET("municipios")
    Call<List<Municipiossss>> Municipiossss();

    @GET("tipos_contactos")
    Call<List<Relacion>> tipos_contactos();

    @GET("categorias_actividades_economicas")
    Call<List<Categoria_Economia>> categorias_actividades_economicas();

    @GET("actividades_economicas")
    Call<List<ActividadEconomica>> ActividadEconomica();

    @GET("productos")
    Call<List<Produc>> Produc();

    @GET("tipos_amortizacion")
    Call<List<TipoAmort>> TipoAmort();

    @GET("tipos_pagos")
    Call<List<TipoPag>> TipoPag();

    @GET("documentos_requeridos")
    Call<List<Documents>> Documents();

    @GET("cliente")
    Call<List<ObtenerClientes>> ObtenerClientes();
}
