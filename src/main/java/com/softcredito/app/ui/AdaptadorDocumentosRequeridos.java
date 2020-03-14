package com.softcredito.app.ui;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.softcredito.app.BuildConfig;
import com.softcredito.app.R;
import com.softcredito.app.provider.Contract;
import com.softcredito.app.provider.Contract.DocumentosRequeridos;
import com.softcredito.app.utilidades.UConsultas;
import com.softcredito.app.utilidades.UTiempo;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

//import android.support.v4.app.LoaderManager;
//import android.support.v4.content.CursorLoader;
//import android.support.v4.content.FileProvider;
//import android.support.v7.widget.CardView;
//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;

/**
 * Adaptador para la lista de clientes
 */
public class AdaptadorDocumentosRequeridos extends RecyclerView.Adapter<AdaptadorDocumentosRequeridos.ViewHolder> {
    private Cursor items;


    // Instancia de escucha
    private OnItemClickListener escucha;
    private AppCompatActivity activityListaDocumentos;
    private AdaptadorArchivosDocumentosEntregados.OnItemClickListener escuchaArchivos;

    private final String ruta_documentos = Environment.getExternalStorageDirectory().getAbsolutePath() +"/softcredito/expedientes/";

    //Recycler de archivos
    private static RecyclerView recicladorArchivos;
    private List<Document> personaList = new ArrayList<> ();
    /**
     * Interfaz para escuchar clicks del recycler
     */
    interface OnItemClickListener {
        public void onClick(ViewHolder holder, String id);

    }

    public class ViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        // Campos respectivos de un item
        public CardView seccion_documento_requerido;
        public TextView tipo_documento;
        public CheckBox entregado;
        public TextView nombre;
        public Button btnNuevo;



        private File file = new File(ruta_documentos);

        private AdaptadorArchivosDocumentosEntregados adaptadorArchivos;
        private LinearLayoutManager layoutManager;


        public ViewHolder(View v) {
            super(v);
            Log.e ( "Hey", "Hola" );
            seccion_documento_requerido = (CardView) v.findViewById(R.id.seccion_documento_requerido);
            entregado = (CheckBox) v.findViewById(R.id.check_entregado);
            tipo_documento = (TextView) v.findViewById(R.id.text_tipo_documento);
            nombre = (TextView) v.findViewById(R.id.text_nombre);
            btnNuevo = (Button) v.findViewById(R.id.btn_guardar);

            recicladorArchivos = (RecyclerView) v.findViewById(R.id.reciclador_archivos);
            layoutManager = new LinearLayoutManager(v.getContext());
            adaptadorArchivos = new AdaptadorArchivosDocumentosEntregados(escuchaArchivos,AdaptadorDocumentosRequeridos.this);

            recicladorArchivos.setLayoutManager(layoutManager);
            recicladorArchivos.setAdapter(adaptadorArchivos);



            //Si no existe crea la carpeta donde se guardaran las fotos
            file.mkdirs();

            btnNuevo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    Cursor c = getCursor();
                    c.moveToPosition(pos);
                    //El cursor tiene la columna de nombre porque hace un LEFT JOIN a la tabla de requeridos
                    String nombre = c.getString(c.getColumnIndex(Contract.DocumentosRequeridos.NOMBRE)) + "_" + getCode2() + ".jpg";
                    String nombre_unico = c.getString(c.getColumnIndex(Contract.DocumentosRequeridos.NOMBRE)) + getCode() + ".jpg";
                    String file = ruta_documentos + nombre;

                    File nuevo_documento = new File( file );

                    try {
                        nuevo_documento.createNewFile();
                    } catch (IOException ex) {
                        Log.e("ERROR ", "Error:" + ex);
                    }
                    Uri uri;
                    if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
                        uri = FileProvider.getUriForFile(v.getContext(), BuildConfig.APPLICATION_ID + ".provider",nuevo_documento);
                    }else{
                        uri = Uri.fromFile( nuevo_documento );
                    }

                    //Abre la camara para tomar la foto
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    //Guarda imagen
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);

                    //Retorna a la actividad
                    //v.getContext().startActivity(cameraIntent);
                    activityListaDocumentos.startActivityForResult(cameraIntent,0);

                    //finish

                    //Crea el registro del archivo documento entregado
                    ContentValues valores = new ContentValues();
                    String type = "image/jpeg";

                    valores.put(Contract.ArchivosDocumentosEntregados.ID, Contract.ArchivosDocumentosEntregados.generarId());
                    valores.put(Contract.ArchivosDocumentosEntregados.ID_DOCUMENTO_ENTREGADO, c.getString(c.getColumnIndex(Contract.DocumentosRequeridos.F_ID_DOCUMENTO_ENTREGADO)));
                    valores.put(Contract.ArchivosDocumentosEntregados.FECHA,UTiempo.obtenerTiempo());
                    valores.put(Contract.ArchivosDocumentosEntregados.NOMBRE,nombre_unico);
                    valores.put(Contract.ArchivosDocumentosEntregados.TIPO,type);
                    valores.put(Contract.ArchivosDocumentosEntregados.RUTA,file);
                    valores.put(Contract.ArchivosDocumentosEntregados.DESCRIPCION,nombre);
                    valores.put(Contract.ArchivosDocumentosEntregados.VERSION,UTiempo.obtenerTiempo());

                    valores.put(Contract.DocumentosEntregados.NOMBRE_DOCUMENTO,c.getString(c.getColumnIndex(DocumentosRequeridos.NOMBRE)));
                    valores.put(Contract.DocumentosEntregados.ID_DOCUMENTO_REQUERIDO,c.getString(c.getColumnIndex(DocumentosRequeridos.ID)));
                    valores.put(Contract.DocumentosEntregados.ID_CLIENTE,c.getString(c.getColumnIndex(DocumentosRequeridos.F_ID_CLIENTE)));
                    valores.put(Contract.DocumentosEntregados.TIPO_DOCUMENTO,c.getString(c.getColumnIndex(DocumentosRequeridos.F_TIPO_DOCUMENTO)));

                    // Iniciar inserción|actualización
                    new TareaAnadirArchivoDocumento(v.getContext().getContentResolver(), valores).execute();

                    notifyDataSetChanged();

                };
            });

            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            escucha.onClick(this, obtenerId(getAdapterPosition()));
        }
    }

    /**
     * Obtiene el valor de la columna 'id' basado en la posición actual del cursor
     * @param posicion Posición actual del cursor
     * @return Identificador del seccion_documento_requerido
     */
    private String obtenerId(int posicion) {
        if (items != null) {
            if (items.moveToPosition(posicion)) {
                return UConsultas.obtenerString(items, DocumentosRequeridos.ID);
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    private String getCode()
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyymmddhhmmss");
        String date = dateFormat.format(new Date() );
        String documentoCode = date;
        return documentoCode;
    }

    private String getCode2()
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyymmdd");
        String date = dateFormat.format(new Date() );
        String documentoCode = date;
        return documentoCode;
    }

    public AdaptadorDocumentosRequeridos(OnItemClickListener escucha, AppCompatActivity activity) {
        this.escucha = escucha;
        this.activityListaDocumentos = activity;
    }

    static class TareaAnadirArchivoDocumento extends AsyncTask<Uri, Void, Void> {
        private final ContentResolver resolver;
        private final ContentValues valores;

        public TareaAnadirArchivoDocumento(ContentResolver resolver, ContentValues valores) {
            this.resolver = resolver;
            this.valores = valores;
        }

        @Override
        protected Void doInBackground(Uri... args) {
            resolver.insert(Contract.ArchivosDocumentosEntregados.URI_CONTENIDO, valores);
            return null;
        }

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //Se asigna al layout a utilizar
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.seccion_documento_requerido, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        //Se llena el layout con la información del cursor
        items.moveToPosition(position);
        Document persona = personaList.get(position);
        boolean entregado;
        String id;
        String id_documento_entregado;
        String tipo;
        String nombre;

        id = UConsultas.obtenerString(items, String.valueOf ( persona.getId () ) );
        tipo = UConsultas.obtenerString(items, persona.getTipo ());//Campo Calculado
        nombre = UConsultas.obtenerString(items, persona.getNombre ());
        id_documento_entregado = UConsultas.obtenerString(items, DocumentosRequeridos.F_ID_DOCUMENTO_ENTREGADO);
        entregado = Boolean.parseBoolean(UConsultas.obtenerString(items, DocumentosRequeridos.F_ENTREGADO));

        holder.tipo_documento.setText(tipo);
        holder.nombre.setText(nombre);
        holder.entregado.setChecked(entregado);

        Uri uriArchivo= Contract.ArchivosDocumentosEntregados.construirUriRequerido(id);
        String id_cliente= UConsultas.obtenerString(items, DocumentosRequeridos.F_ID_CLIENTE);
        //Parametros
        String[] mSelectionArgs = {id_cliente};
        Cursor c=holder.itemView.getContext().getContentResolver().query(
                uriArchivo,
                null,
                null,
                mSelectionArgs,
                null
        );
        holder.adaptadorArchivos.swapCursor(c);
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
