package com.softcredito.app.ui;

import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Build;
import androidx.core.content.FileProvider;
//import android.support.design.widget.Snackbar;
//import android.support.v7.widget.CardView;
//import android.support.v7.widget.RecyclerView;
import com.google.android.material.snackbar.Snackbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.softcredito.app.BuildConfig;
import com.softcredito.app.R;
import com.softcredito.app.provider.Contract.BitacorasCreditoArchivos;
import com.softcredito.app.utilidades.UConsultas;
import com.squareup.picasso.Picasso;

import java.io.File;

/**
 * Adaptador para la lista de bitacoras de crédito
 */
public class AdaptadorBitacorasCreditoArchivos extends RecyclerView.Adapter<AdaptadorBitacorasCreditoArchivos.ViewHolder> {

    private final String ruta_bitacoras_credito = Environment.getExternalStorageDirectory().getAbsolutePath() +"/softcredito/bitacoras_credito/";

    private Cursor items;
    private Uri uriSolicitud;
    private Uri uriBitacoraCredito;

    private int selectionPosition;

    /**
     * Interfaz para escuchar clicks del recycler
     */
    interface OnItemClickListener {
        public void onClick(ViewHolder holder, String id);
    }

    public class ViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener, View.OnCreateContextMenuListener  {
        // Campos respectivos de un item
        public CardView tarjeta_bitacora_credito_archivo;
        public TextView nombre;
        public TextView fecha;
        public ImageView logo;

        public ImageButton btnEliminar;
        public ImageButton btnAbrir;

        public ViewHolder(View v) {
            super(v);
            tarjeta_bitacora_credito_archivo = (CardView) v.findViewById(R.id.tarjeta_bitacora_credito_archivo);
            nombre = (TextView) v.findViewById(R.id.text_nombre);
            fecha = (TextView) v.findViewById(R.id.text_fecha);
            logo = (ImageView) v.findViewById(R.id.fileLogo);

            //btnEliminar = (ImageButton) v.findViewById(R.id.btn_eliminar);

            //btnEliminar.setOnClickListener(this);
            //tarjeta_bitacora_credito_archivo.setOnClickListener(this);

            v.setOnClickListener(this);

            v.setOnCreateContextMenuListener(this);

        }



        @Override
        public void onClick(View view) {
            int id = view.getId();
            int pos = getAdapterPosition();
            String idBitacoraCreditoArchivo = obtenerId(pos);
            Uri uriBitacoraCreditoArchivo=null;
            String detalle="";

            uriBitacoraCreditoArchivo = BitacorasCreditoArchivos.construirUri(idBitacoraCreditoArchivo);

            switch(id){
                case R.id.btn_eliminar:
                    detalle="borrar";
                    break;
                case R.id.tarjeta_bitacora_credito_archivo:
                    detalle="abrir";
                    break;
            }
            if(!TextUtils.isEmpty(detalle)){
                mostrarDetalles(view,uriBitacoraCreditoArchivo,detalle,pos);
            }
        }


        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.setHeaderTitle("Seleccione una opción");
            menu.add(0, R.id.tarjeta_bitacora_credito_archivo, 0, "Abrir");
            menu.add(0, R.id.btn_eliminar, 1, "Eliminar");
        }
    }

    /**
     * Obtiene el valor de la columna 'id' basado en la posición actual del cursor
     * @param posicion Posición actual del cursor
     * @return Identificador de la bitacora de crédito
     */
    private String obtenerId(int posicion) {
        if (items != null) {
            if (items.moveToPosition(posicion)) {
                return UConsultas.obtenerString(items, BitacorasCreditoArchivos.ID);
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public int getSelecionPosition() {
        return selectionPosition;
    }

    public void setSelectionPosition(int selectionPosition) {
        this.selectionPosition = selectionPosition;
    }

    static class TareaAnadirBitacoraCreditoArchivo extends AsyncTask<Uri, Void, Void> {
        private final ContentResolver resolver;
        private final ContentValues valores;

        public TareaAnadirBitacoraCreditoArchivo(ContentResolver resolver, ContentValues valores) {
            this.resolver = resolver;
            this.valores = valores;
        }

        @Override
        protected Void doInBackground(Uri... args) {

            resolver.insert(BitacorasCreditoArchivos.URI_CONTENIDO, valores);

            return null;
        }

    }

    static class TareaEliminarBitacoraCreditoArchivo extends AsyncTask<Uri, Void, Void> {
        private final ContentResolver resolver;

        public TareaEliminarBitacoraCreditoArchivo(ContentResolver resolver) {
            this.resolver = resolver;
        }

        @Override
        protected Void doInBackground(Uri... args) {

            /*
            Verificación: Si el registro no ha sido sincronizado aún, entonces puede eliminarse
            directamente. De lo contrario se marca como 'eliminado' = 1
             */
            Cursor c = resolver.query(args[0], new String[]{BitacorasCreditoArchivos.INSERTADO}
                    , null, null, null);

            int insertado;

            if (c != null && c.moveToNext()) {
                insertado = UConsultas.obtenerInt(c, BitacorasCreditoArchivos.INSERTADO);
            } else {
                return null;
            }

            if (insertado == 1) {
                resolver.delete(args[0], null, null);
            } else if (insertado == 0) {
                ContentValues valores = new ContentValues();
                valores.put(BitacorasCreditoArchivos.ELIMINADO, 1);
                resolver.update(args[0], valores, null, null);
            }

            return null;
        }
    }

    void mostrarDetalles(View v,Uri uri,String detalle,int pos) {
        Intent intent = new Intent();
        if(null == uri){
            uri=BitacorasCreditoArchivos.construirUri(obtenerId(pos));
        }
        if (null != uri) {
            if(detalle.equals("abrir")){
                String archivo = UConsultas.obtenerString(items, BitacorasCreditoArchivos.RUTA);
                String tipo = UConsultas.obtenerString(items, BitacorasCreditoArchivos.TIPO);
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
                Uri uriArchivo = BitacorasCreditoArchivos.construirUri(items.getString(items.getColumnIndex(BitacorasCreditoArchivos.ID)));
                //Borrar archivo
                String archivo = UConsultas.obtenerString(items, BitacorasCreditoArchivos.RUTA);
                File file = new File(archivo);
                boolean deleted=false;
                if(file.exists()){
                    deleted = file.delete();
                }else{
                    deleted = true;
                }
                if(deleted){
                    // Iniciar inserción|actualización
                    new TareaEliminarBitacoraCreditoArchivo(v.getContext().getContentResolver()).execute(uriArchivo);
                }
            }
        }
    }

    public AdaptadorBitacorasCreditoArchivos(Uri uriS, Uri uriBC) {
        uriSolicitud=uriS;
        uriBitacoraCredito=uriBC;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //Se asigna al layout a utilizar
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.tarjeta_bitacora_credito_archivo, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        //Se llena el layout con la información del cursor
        items.moveToPosition(position);

        String nombre;
        String fecha;

        nombre = UConsultas.obtenerString(items, BitacorasCreditoArchivos.NOMBRE);
        fecha = UConsultas.obtenerString(items, BitacorasCreditoArchivos.FECHA);
        String tipo = UConsultas.obtenerString(items, BitacorasCreditoArchivos.TIPO);

        File file = new File(ruta_bitacoras_credito +"/"+ nombre);
        Uri uri = Uri.fromFile(file);
        String fileExt = MimeTypeMap.getFileExtensionFromUrl(uri.toString());
        int placeholder;
        if(fileExt.equals("pdf")){
            placeholder=R.drawable.file_pdf;
        }else if(fileExt.equals("jpeg")){
            placeholder=R.drawable.file_jpeg;
        }else if(fileExt.equals("jpg")){
            placeholder=R.drawable.file_jpg;
        }else if(fileExt.equals("png")){
            placeholder=R.drawable.file_png;
        }else if(fileExt.equals("bmp")){
            placeholder=R.drawable.file_bmp;
        }else{
            placeholder=R.drawable.file_jpeg;
        }

        Picasso.with(holder.itemView.getContext()).load(file).placeholder(placeholder).resize(50,50).into(holder.logo);

        //holder.logo.setImageBitmap(myBitmap);
        holder.nombre.setText(nombre);
        holder.fecha.setText(fecha);

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                setSelectionPosition(holder.getAdapterPosition());
                return false;
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = holder.getAdapterPosition();
                String idBitacoraCreditoArchivo = obtenerId(pos);
                Uri uriBitacoraCreditoArchivo=null;
                String detalle="";

                uriBitacoraCreditoArchivo = BitacorasCreditoArchivos.construirUri(idBitacoraCreditoArchivo);

                detalle="abrir";

                if(!TextUtils.isEmpty(detalle)){
                    mostrarDetalles(v,uriBitacoraCreditoArchivo,detalle,pos);
                }
            }
        });
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
