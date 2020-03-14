package com.softcredito.app.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
//import android.support.v7.widget.CardView;
//import android.support.v7.widget.RecyclerView;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.softcredito.app.BuildConfig;
import com.softcredito.app.R;
import com.softcredito.app.provider.Contract;
import com.softcredito.app.provider.Contract.Clientes;
import com.softcredito.app.utilidades.UConsultas;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;

/**
 * Adaptador para la lista de clientes
 */
public class AdaptadorClientes extends RecyclerView.Adapter<AdaptadorClientes.ViewHolder> {
    private Cursor items;
    private final String ruta_clientes= Environment.getExternalStorageDirectory().getAbsolutePath() +"/softcredito/clientes/";

    /**
     * Interfaz para escuchar clicks del recycler
     */
    interface OnItemClickListener {
        public void onClick(ViewHolder holder, String id);
    }

    public class ViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        // Campos respectivos de un item
        public CardView tarjeta_cliente;
        public ImageView foto;
        public TextView nombre;
        public TextView telefono;
        public TextView correo;

        public Button btnEditar;
        public Button btnExpediente;
        public Button btnSolicitudes;
        public Button btnCotizadores;

        public ViewHolder(View v) {
            super(v);
            tarjeta_cliente = (CardView) v.findViewById(R.id.tarjeta_cliente);
            foto = (ImageView) v.findViewById(R.id.foto_cliente);
            nombre = (TextView) v.findViewById(R.id.nombre_cliente);
            telefono = (TextView) v.findViewById(R.id.telefono_cliente);
            correo = (TextView) v.findViewById(R.id.correo_cliente);

            btnEditar = (Button) v.findViewById(R.id.btnEditar);
            btnExpediente = (Button) v.findViewById(R.id.btnExpediente);
            btnExpediente.setOnClickListener ( new View.OnClickListener () {
                @Override
                public void onClick(View v) {

                    Intent intent= new Intent(v.getContext (), DocumentosRequeridos.class);
                    v.getContext ().startActivity ( intent );
                    Toast.makeText ( v.getContext() , "hi",Toast.LENGTH_SHORT ).show ();

                }
            } );


            btnSolicitudes = (Button) v.findViewById(R.id.btnSolicitud);
            btnCotizadores = (Button) v.findViewById(R.id.btnCotizador);

            foto.setOnClickListener(this);
            btnEditar.setOnClickListener(this);
            btnSolicitudes.setOnClickListener(this);
            btnCotizadores.setOnClickListener(this);



            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int id = view.getId();
            int pos = getAdapterPosition();
            String idCliente = obtenerId(pos);
            Uri uriCliente=null;
            String detalle="";

            switch(id){
                case R.id.foto_cliente:
                    uriCliente = Clientes.construirUri(idCliente);
                    //Se valida si se tienen los permisos para acceder a la camara
                    boolean conPermiso=false;
                    if (ActivityCompat.checkSelfPermission(view.getContext(), Manifest.permission.CAMERA)
                            != PackageManager.PERMISSION_GRANTED) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        /*if (shouldShowRequestPermissionRationale(
                                Manifest.permission.CAMERA)){*/
                            if(true){//Siempre se le pregunta al usuario ya que es un permiso necesario

                            }
                        }
                    }else{
                        conPermiso=true;
                    }

                    if(conPermiso){
                        String nombre_unico = idCliente + ".jpg";
                        String archivo = ruta_clientes + nombre_unico;
                        File file = new File(ruta_clientes);

                        //Si no existe crea la carpeta donde se guardaran las fotos
                        file.mkdirs();

                        File nuevo_documento = new File( archivo );

                        try {
                            nuevo_documento.createNewFile();
                        } catch (IOException ex) {
                            Log.e("ERROR ", "Error:" + ex);
                        }
                        Uri uri;
                        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
                            uri = FileProvider.getUriForFile(view.getContext(), BuildConfig.APPLICATION_ID + ".provider",nuevo_documento);
                        }else{
                            uri = Uri.fromFile( nuevo_documento );
                        }

                        //Abre la camara para tomar la foto
                        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        //Guarda imagen
                        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);

                        //Retorna a la actividad
                        //v.getContext().startActivity(cameraIntent);
                        view.getContext().startActivity(cameraIntent);
                    }
                    break;
                case R.id.btnEditar:
                    uriCliente = Clientes.construirUri(idCliente);
                    detalle="cliente";
                    break;
                case R.id.btnSolicitud:
                    uriCliente = Clientes.construirUri(idCliente);
                    detalle="solicitudes";
                    break;
                case R.id.btnCotizador:
                    uriCliente = Clientes.construirUri(idCliente);
                    detalle="cotizadores";
                    break;
            }
            if(!TextUtils.isEmpty(detalle)){
                mostrarDetalles(view,uriCliente,detalle);
            }
        }
    }

    /**
     * Obtiene el valor de la columna 'id' basado en la posición actual del cursor
     * @param posicion Posición actual del cursor
     * @return Identificador del cliente
     */
    private String obtenerId(int posicion) {
        if (items != null) {
            if (items.moveToPosition(posicion)) {
                return UConsultas.obtenerString(items, Clientes.ID);
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    void mostrarDetalles(View v,Uri uri,String detalle) {
        Intent intent = new Intent();
        if (null != uri) {
            if(detalle.equals("cliente")){
                intent.setClass(v.getContext(),ActividadInsercionCliente.class);
                intent.putExtra(ActividadInsercionCliente.URI_CLIENTE, uri.toString());
            }else if(detalle.equals("solicitudes")){
                intent.setClass(v.getContext(),ActividadListaSolicitudes.class);
                intent.putExtra(ActividadListaSolicitudes.URI_CLIENTE, uri.toString());
            }else if(detalle.equals("cotizadores")){
                intent.setClass(v.getContext(),ActividadListaCotizadores.class);
                intent.putExtra(ActividadListaCotizadores.URI_CLIENTE, uri.toString());
            }
        }
        v.getContext().startActivity(intent);
    }

    public AdaptadorClientes() {

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //Se asigna al layout a utilizar
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.tarjeta_cliente, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        //Se llena el layout con la información del cursor
        items.moveToPosition(position);

        String id;

        id = UConsultas.obtenerString(items, Contract.Clientes.ID);

        File file = new File(ruta_clientes +"/"+ id + ".jpg");
        Uri uri = Uri.fromFile(file);
        String fileExt = MimeTypeMap.getFileExtensionFromUrl(uri.toString());
        int placeholder;
        placeholder=R.drawable.cliente;

        Picasso.with(holder.itemView.getContext()).load(file).placeholder(placeholder).resize(50,50).into(holder.foto);

        String nombre;
        String nombre2;
        String apellido_paterno;
        String apellido_materno;
        String telefono;
        String correo;

        nombre = UConsultas.obtenerString(items, Clientes.PRIMER_NOMBRE);
        nombre2 = UConsultas.obtenerString(items, Clientes.SEGUNDO_NOMBRE);
        apellido_paterno = UConsultas.obtenerString(items, Clientes.PRIMER_APELLIDO);
        apellido_materno = UConsultas.obtenerString(items, Clientes.SEGUNDO_APELLIDO);
        telefono = UConsultas.obtenerString(items, Clientes.TELEFONO);
        correo = UConsultas.obtenerString(items, Clientes.CORREO);

        //Se agregan los prefijos
        telefono = holder.itemView.getContext().getString(R.string.tarjeta_telefono) + telefono;
        correo = holder.itemView.getContext().getString(R.string.tarjeta_correo) + correo;

        holder.nombre.setText(String.format("%s %s %s %s", nombre, nombre2, apellido_paterno, apellido_materno));
        holder.telefono.setText(telefono);
        holder.correo.setText(correo);
    }

    @Override
    public int getItemCount() {
        if (items != null)
            return items.getCount();
        return 0;
    }

    public void swapCursor(Cursor nuevoCursor) {
        if (nuevoCursor != null) {
            items = nuevoCursor;
            notifyDataSetChanged();
        }
    }

    public Cursor getCursor() {
        return items;
    }

}
