package com.softcredito.app;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.entidades.ServicioTask;

public class Sincronizacion extends AppCompatActivity {

    Button buttonagregar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_sincronizacion );

        buttonagregar= findViewById(R.id.button);

        buttonagregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                consumirServicio();

            }
        });


    }

    public void consumirServicio(){
        // ahora ejecutaremos el hilo creado
        String tipo_persona = "Persona Fisica";
        String razon_social = "";
        String nombre = "Miguel";
        String nombre2 = "Gomez";
        String apellido_paterno = "Carrillo";
        String apellido_materno = "Perez";
        String contacto = "Manuel";
        String relacion_contacto = "Familiar";
        String telefono = "9971126410";
        String correo = "victor_cetzi-10@hotmail.com";
        String latitud = "21.0068461";
        String longitud = "-89.5989939";
        String curp = "CEMV960818HYNTRC03";
        String rfc = "CEMV960818CW8";
        String ine = "CTMRVC96081831H900";
        String codigo_postal = "97970";
        String pais = "Mexico";
        String estado = "Yucatan";
        String municipio = "Tekax";
        String localidad = "Tekax";
        String colonia = "Lazaro cardenas";
        String calle = "37 x 46 y 48";
        String numero_exterior = "S/N";
        String numero_interior = "S/N";
        String referencia = "";
        String fecha_nacimiento = "08-18-96";
        String ocupacion = "Programador";
        String estado_civil = "soltero";
        String id_categoria_actividad_economica = "11";
        String id_actividad_economica = "1259";
        String celular = "9971126410";
        String es_cliente = "Si";
        String notas = "";

        ServicioTask servicioTask= new ServicioTask (this,"https://softcredito.com/demos/dmo_unico2020Conta/app/WebService/index.php/product"
                ,tipo_persona,razon_social,nombre,nombre2,apellido_paterno,apellido_materno,contacto,relacion_contacto,telefono,correo,latitud,longitud,curp
                ,rfc,ine,codigo_postal,pais,estado
                ,municipio,localidad,colonia,calle,numero_exterior,numero_interior,referencia,fecha_nacimiento,ocupacion,estado_civil,id_categoria_actividad_economica
                ,id_actividad_economica,celular,es_cliente,notas);
        servicioTask.execute();



    }
}
