package com.entidades;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;

public class ServicioTask extends AsyncTask<Void, Void, String> {
    //variables del hilo
    private Context httpContext;//contexto
    ProgressDialog progressDialog;//dialogo cargando
    public String resultadoapi="";
    public String linkrequestAPI= "" ;


    public String tipo_persona="";
    public String razon_social="";
    public String nombre="";
    public String nombre2="";
    public String apellido_paterno="";
    public String apellido_materno="";
    public String contacto="";
    public String relacion_contacto="";
    public String telefono="";
    public String correo="";
    public String latitud="";
    public String longitud="";
    public String curp="";
    public String rfc="";
    public String ine="";
    public String codigo_postal="";
    public String pais="";
    public String estado="";
    public String municipio="";
    public String localidad="";
    public String colonia="";
    public String calle="";
    public String numero_exterior="";
    public String numero_interior="";
    public String referencia="";
    public String fecha_nacimiento="";
    public String ocupacion="";
    public String estado_civil="";
    public String id_categoria_actividad_economica="";
    public String id_actividad_economica="";
    public String celular="";
    public String es_cliente="";
    public String notas="";
    //constructor del hilo (Asynctask)
    public ServicioTask(Context ctx, String linkAPI, String tipo_persona, String razon_social, String nombre, String nombre2, String apellido_paterno,
                        String apellido_materno, String contacto, String relacion_contacto, String telefono, String correo, String latitud
            , String longitud, String curp, String rfc, String ine, String codigo_postal, String pais
            , String estado, String municipio, String localidad, String colonia, String calle, String numero_exterior, String numero_interior
            , String referencia, String fecha_nacimiento, String ocupacion, String estado_civil, String id_categoria_actividad_economica, String id_actividad_economica
            , String celular, String es_cliente, String notas  ){
        this.httpContext=ctx;
        this.linkrequestAPI=linkAPI;

        this.tipo_persona=tipo_persona;
        this.razon_social=razon_social;
        this.nombre=nombre;
        this.nombre2=nombre2;
        this.apellido_paterno=apellido_paterno;
        this.apellido_materno=apellido_materno;
        this.contacto=contacto;
        this.relacion_contacto=relacion_contacto;
        this.telefono=telefono;
        this.correo=correo;
        this.latitud=latitud;
        this.longitud=longitud;
        this.curp=curp;
        this.rfc=rfc;
        this.ine=ine;
        this.codigo_postal=codigo_postal;
        this.pais=pais;
        this.estado=estado;
        this.municipio=municipio;
        this.localidad=localidad;
        this.colonia=colonia;
        this.calle=calle;
        this.numero_exterior=numero_exterior;
        this.numero_interior=numero_interior;
        this.referencia=referencia;
        this.fecha_nacimiento=fecha_nacimiento;
        this.ocupacion=ocupacion;
        this.estado_civil=estado_civil;

        this.id_categoria_actividad_economica=id_categoria_actividad_economica;
        this.id_actividad_economica=id_actividad_economica;
        this.id_categoria_actividad_economica=celular;
        this.id_actividad_economica=es_cliente;
        this.notas=notas;

    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog = ProgressDialog.show(httpContext, "Procesando Solicitud", "por favor, espere");
    }

    @Override
    protected String doInBackground(Void... params) {
        String result= null;

        String wsURL = linkrequestAPI;
        URL url = null;
        try {
            // se crea la conexion al api: http://localhost:15009/WEBAPIREST/api/persona
            url = new URL (wsURL);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);

            //crear el objeto json para enviar por POST
            JSONObject parametrosPost= new JSONObject ();
            //parametrosPost.put("id","95");
            parametrosPost.put("tipo_persona",tipo_persona);
            //parametrosPost.put("razon_social",razon_social);
            parametrosPost.put("nombre_completo",nombre + " " + nombre2 + " " + apellido_paterno + " " + apellido_materno);
            parametrosPost.put("nombre",nombre);
            parametrosPost.put("nombre2",nombre2);
            parametrosPost.put("apellido_paterno",apellido_paterno);
            parametrosPost.put("apellido_materno",apellido_materno);
            parametrosPost.put("contacto",contacto);
            parametrosPost.put("telefono",telefono);
         //   parametrosPost.put("correo",correo);
            parametrosPost.put("latitud",latitud);
            parametrosPost.put("longitud",longitud);
            parametrosPost.put("curp",curp);
            parametrosPost.put ( "rfc", rfc );
            parametrosPost.put("ine",ine);
            parametrosPost.put("codigo_postal",codigo_postal);
            parametrosPost.put("pais",pais);
            parametrosPost.put("estado",estado);
            parametrosPost.put("municipio",municipio);
            parametrosPost.put("localidad",localidad);
            parametrosPost.put("colonia",colonia);
            parametrosPost.put("calle",calle);
            parametrosPost.put("numero_exterior",numero_exterior);
            parametrosPost.put("numero_interior",numero_interior);
            parametrosPost.put("referencia",referencia);
            parametrosPost.put("fecha_nacimiento",fecha_nacimiento);
          //  parametrosPost.put("ocupacion",ocupacion);
            parametrosPost.put("estado_civil",estado_civil);
          //  parametrosPost.put("id_categoria_actividad_economica",id_categoria_actividad_economica);
          //  parametrosPost.put("id_actividad_economica",id_actividad_economica);
            parametrosPost.put("celular",celular);
            parametrosPost.put("es_cliente",es_cliente);
            parametrosPost.put("notas",notas);







            //OBTENER EL RESULTADO DEL REQUEST
            OutputStream os = urlConnection.getOutputStream();
            BufferedWriter writer = new BufferedWriter (new OutputStreamWriter (os, "UTF-8"));
            writer.write(getPostDataString(parametrosPost));
            writer.flush();
            writer.close();
            os.close();

            urlConnection.connect ();

            int responseCode=urlConnection.getResponseCode();// conexion OK?
            if(responseCode== HttpURLConnection.HTTP_OK){
                BufferedReader in= new BufferedReader (new InputStreamReader (urlConnection.getInputStream()));

                StringBuffer sb= new StringBuffer ("");
                String linea="";
                while ((linea=in.readLine())!= null){
                    sb.append(linea);
                    break;

                }
                in.close();
                result= sb.toString();
            }
            else{
                result= new String ("Error: "+ responseCode);


            }


        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }


        return  result;

    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        progressDialog.dismiss();
        resultadoapi=s;
        Toast.makeText(httpContext,resultadoapi, Toast.LENGTH_LONG).show();//mostrara una notificacion con el resultado del request

    }
    //FUNCIONES----------------------------------------------------------------------
    //Transformar JSON Obejct a String *******************************************
    public String getPostDataString(JSONObject params) throws Exception {

        StringBuilder result = new StringBuilder ();
        boolean first = true;
        Iterator<String> itr = params.keys();
        while(itr.hasNext()){

            String key= itr.next();
            Object value = params.get(key);

            if (first)
                first = false;
            else
                result.append("&");

            result.append( URLEncoder.encode(key, "UTF-8"));
            result.append("=");
            result.append( URLEncoder.encode(value.toString(), "UTF-8"));
        }
        return result.toString();
    }

}