package com.softcredito.app.ui;

import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
//import android.support.v7.widget.CardView;
//import android.support.v7.widget.RecyclerView;
import android.os.Build;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.content.FileProvider;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.softcredito.app.BuildConfig;
import com.softcredito.app.R;
import com.softcredito.app.provider.Contract.ArchivosDocumentosEntregados;
import com.softcredito.app.utilidades.UConsultas;

import java.io.File;

/**
 * Adaptador para la lista de clientes
 */
public class AdaptadorArchivosDocumentosEntregados extends RecyclerView.Adapter<AdaptadorArchivosDocumentosEntregados.ViewHolder> {
    private Cursor items;

    // Instancia de escucha
    private OnItemClickListener escucha;
    //Adaptador padre
    private  AdaptadorDocumentosRequeridos adaptadorDocumentosRequeridos;

    /**
     * Interfaz para escuchar clicks del recycler
     */
    interface OnItemClickListener {
        public void onClick(ViewHolder holder, String id);
    }

    public class ViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        // Campos respectivos de un item
        public CardView seccion_archivo_documento_entregado;
        public TextView descripcion;
        public TextView fecha;
        public ImageButton btnEliminar;

        public ViewHolder(View v) {
            super(v);
            seccion_archivo_documento_entregado = (CardView) v.findViewById(R.id.seccion_archivo_documento_entregado);
            descripcion = (TextView) v.findViewById(R.id.text_descripcion);
            fecha = (TextView) v.findViewById(R.id.text_fecha);
            btnEliminar = (ImageButton) v.findViewById(R.id.btn_eliminar);

            btnEliminar.setOnClickListener(this);
            descripcion.setOnClickListener(this);

            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            //escucha.onClick(this, obtenerId(getAdapterPosition()));
            int id = view.getId();
            int pos = getAdapterPosition();
            String idArchivo = obtenerId(pos);
            Uri uriArchivo=null;
            String detalle="";

            switch(id){
                case R.id.text_descripcion:
                    uriArchivo = ArchivosDocumentosEntregados.construirUri(idArchivo);
                    detalle="archivo";
                    break;
                case R.id.btn_eliminar:
                    uriArchivo = ArchivosDocumentosEntregados.construirUri(idArchivo);
                    detalle="borrar";
            }
            if(!TextUtils.isEmpty(detalle)){
                mostrarDetalles(view,uriArchivo,detalle,pos);
            }
        }
    }

    /**
     * Obtiene el valor de la columna 'id' basado en la posición actual del cursor
     * @param posicion Posición actual del cursor
     * @return Identificador del seccion_archivo_documento_entregado
     */
    private String obtenerId(int posicion) {
        if (items != null) {
            if (items.moveToPosition(posicion)) {
                return UConsultas.obtenerString(items, ArchivosDocumentosEntregados.ID);
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    void mostrarDetalles(View v,Uri uri,String detalle,int pos) {
        Intent intent = new Intent();
        if (null != uri) {
            if(detalle.equals("archivo")){
                String archivo = UConsultas.obtenerString(items, ArchivosDocumentosEntregados.RUTA);
                String tipo = UConsultas.obtenerString(items, ArchivosDocumentosEntregados.TIPO);

                //String archivo = c.getString(c.getColumnIndex(ArchivosDocumentosEntregados.RUTA));
                //String tipo = c.getString(c.getColumnIndex(ArchivosDocumentosEntregados.TIPO));
                File file = new File(archivo);

                if(file.exists()){
                    if(file.length()>0){
                        Intent target = new Intent(Intent.ACTION_VIEW);
                        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
                            target.setDataAndType(FileProvider.getUriForFile(v.getContext(), BuildConfig.APPLICATION_ID + ".provider",file),tipo);
                        }else{
                            target.setDataAndType(Uri.fromFile(file),tipo);
                        }
                        target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                        target.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

                        Intent openIntent = Intent.createChooser(target, "Open File");
                        try {
                            v.getContext().startActivity(openIntent);
                        } catch (ActivityNotFoundException e) {
                            Snackbar.make(v.findViewById(R.id.coordinador),
                                    "Su movìl no cuenta una App para abrir el tipo de Archivo",
                                    Snackbar.LENGTH_LONG).show();
                        };
                    }else{
                        Snackbar.make(v.getRootView().findViewById(R.id.coordinador),
                                "El archivo esta vacío",
                                Snackbar.LENGTH_LONG).show();
                    }
                }else{
                    Snackbar.make(v.getRootView().findViewById(R.id.coordinador),
                            "El archivo no existe",
                            Snackbar.LENGTH_LONG).show();
                }
            }else if(detalle.equals("borrar")){
                // Iniciar borrado
                new TareaEliminarArchivoDocumento(v.getContext().getContentResolver()).execute(uri);

                notifyDataSetChanged();
                adaptadorDocumentosRequeridos.notifyDataSetChanged();
            }
        }
    }

    static class TareaEliminarArchivoDocumento extends AsyncTask<Uri, Void, Void> {
        private final ContentResolver resolver;

        public TareaEliminarArchivoDocumento(ContentResolver resolver) {
            this.resolver = resolver;
        }

        @Override
        protected Void doInBackground(Uri... args) {

            /*
            Verificación: Si el registro no ha sido sincronizado aún, entonces puede eliminarse
            directamente. De lo contrario se marca como 'eliminado' = 1
             */
            Cursor c = resolver.query(args[0], new String[]{ArchivosDocumentosEntregados.INSERTADO}
                    , null, null, null);

            int insertado;

            if (c != null && c.moveToNext()) {
                insertado = UConsultas.obtenerInt(c, ArchivosDocumentosEntregados.INSERTADO);
            } else {
                return null;
            }

            if (insertado == 1) {
                resolver.delete(args[0], null, null);
            } else if (insertado == 0) {
                ContentValues valores = new ContentValues();
                valores.put(ArchivosDocumentosEntregados.ELIMINADO, 1);
                resolver.update(args[0], valores, null, null);
            }

            return null;
        }
    }

    public AdaptadorArchivosDocumentosEntregados(OnItemClickListener escucha, AdaptadorDocumentosRequeridos adaptadorDocumentosRequeridos) {
        this.escucha = escucha;
        this.adaptadorDocumentosRequeridos = adaptadorDocumentosRequeridos;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //Se asigna al layout a utilizar
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.seccion_archivo_documento_entregado, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        //Se llena el layout con la información del cursor
        items.moveToPosition(position);

        String descripcion;
        String fecha;

        descripcion = UConsultas.obtenerString(items, ArchivosDocumentosEntregados.DESCRIPCION);
        fecha = UConsultas.obtenerString(items, ArchivosDocumentosEntregados.FECHA);

        holder.descripcion.setText(descripcion);
        holder.fecha.setText(fecha);
    }

    @Override
    public int getItemCount() {
        if (items != null)
            return items.getCount();
        return 0;
    }

    public void swapCursor(Cursor nuevoCursor) {
        if (nuevoCursor != null) {
            this.items = nuevoCursor;
            notifyDataSetChanged();
        }
    }

    public Cursor getCursor() {
        return this.items;
    }

}
