package com.softcredito.app.ui;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.ContentValues;
import android.net.Uri;

import androidx.core.content.FileProvider;

import android.view.View;

import com.softcredito.app.R;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import androidx.appcompat.app.AppCompatActivity;

import android.widget.Button;

import com.coatedmoose.customviews.SignatureView;
import com.softcredito.app.provider.Contract;
import com.softcredito.app.utilidades.UTiempo;

public class ActividadFirma extends AppCompatActivity implements View.OnClickListener {

    private final String ruta_bitacoras_credito= Environment.getExternalStorageDirectory().getAbsolutePath() +"/softcredito/bitacoras_credito/";
    public static final String URI_SOLICITUD = "extra.uriSolicitud";
    public static final String URI_BITACORA_CREDITO = "extra.uriBitacoraCredito";

    SignatureView signature;

    private Uri uriBitacoraCredito;
    private Uri uriSolicitud;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actividad_firma);

        // Obtener el uri de la soliciutd
        String uri = getIntent().getStringExtra(URI_SOLICITUD);
        if (uri != null) {
            uriSolicitud= Uri.parse(uri);
        }
        // Obtener el uri de la bitacora de crédito
        String uri2 = getIntent().getStringExtra(URI_BITACORA_CREDITO);
        if (uri2 != null) {
            uriBitacoraCredito= Uri.parse(uri2);
        }

        signature = (SignatureView) this.findViewById(R.id.signatureView1);

        prepararBotones();
    }

    protected void prepararBotones() {
        Button mClickButton2 = (Button)findViewById(R.id.btnGuardar);
        mClickButton2.setOnClickListener(this);
        Button mClickButton1 = (Button)findViewById(R.id.btnBorrar);
        mClickButton1.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnBorrar: {
                signature.clearSignature();
                break;
            }
            case  R.id.btnGuardar: {
                saveSignature(v);
                break;
            }
        }
    }

    public void saveSignature(View view) {
        Bitmap image = signature.getImage();

        String nombre_unico = "Firma_" + getCode() + ".jpg";
        String archivo = ruta_bitacoras_credito + nombre_unico;
        File file = new File(ruta_bitacoras_credito);

        file.mkdirs();

        File nuevo_documento = new File( archivo );

        try {
            nuevo_documento.createNewFile();

            OutputStream os = new FileOutputStream(nuevo_documento);
            image.compress(Bitmap.CompressFormat.JPEG, 90, os);
            os.close();

            //Crea el registro del archivo documento entregado
            ContentValues valores = new ContentValues();
            String type = "image/jpeg";

            valores.put(Contract.BitacorasCreditoArchivos.ID, Contract.BitacorasCreditoArchivos.generarId());
            valores.put(Contract.BitacorasCreditoArchivos.ID_BITACORA_CREDITO, Contract.BitacorasCredito.obtenerId(uriBitacoraCredito));
            valores.put(Contract.BitacorasCreditoArchivos.FECHA, UTiempo.obtenerTiempo());
            valores.put(Contract.BitacorasCreditoArchivos.NOMBRE,nombre_unico);
            valores.put(Contract.BitacorasCreditoArchivos.TIPO,type);
            valores.put(Contract.BitacorasCreditoArchivos.RUTA,archivo);
            valores.put(Contract.BitacorasCreditoArchivos.VERSION, UTiempo.obtenerTiempo());
            // Iniciar inserción|actualización
            new AdaptadorBitacorasCreditoArchivos.TareaAnadirBitacoraCreditoArchivo(getContentResolver(), valores).execute();

            finish();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private String getCode()
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyymmddhhmmss");
        String date = dateFormat.format(new Date() );
        String documentoCode = date;
        return documentoCode;
    }
}