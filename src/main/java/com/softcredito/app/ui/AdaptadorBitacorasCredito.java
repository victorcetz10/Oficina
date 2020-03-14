package com.softcredito.app.ui;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
//import android.support.design.widget.Snackbar;
//import android.support.v7.widget.CardView;
//import android.support.v7.widget.RecyclerView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.softcredito.app.R;
import com.softcredito.app.provider.Contract.BitacorasCredito;
import com.softcredito.app.utilidades.UConsultas;

/**
 * Adaptador para la lista de bitacoras de crédito
 */
public class AdaptadorBitacorasCredito extends RecyclerView.Adapter<AdaptadorBitacorasCredito.ViewHolder> {

    private Cursor items;
    private Uri uriSolicitud;

    /**
     * Interfaz para escuchar clicks del recycler
     */
    interface OnItemClickListener {
        public void onClick(ViewHolder holder, String id);
    }

    public class ViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        // Campos respectivos de un item
        public CardView tarjeta_bitacora_credito;
        public TextView asunto;
        public TextView fecha;
        public TextView hora;
        public TextView descripcion;

        public Button btnEditar;
        public Button btnArchivos;
        public Button btnFirma;

        public ViewHolder(View v) {
            super(v);
            tarjeta_bitacora_credito = (CardView) v.findViewById(R.id.tarjeta_bitacora_credito);
            asunto = (TextView) v.findViewById(R.id.text_asunto);
            fecha = (TextView) v.findViewById(R.id.text_fecha);
            hora = (TextView) v.findViewById(R.id.text_hora);
            descripcion = (TextView) v.findViewById(R.id.text_descripcion);

            btnEditar = (Button) v.findViewById(R.id.btnEditar);
            btnArchivos = (Button) v.findViewById(R.id.btnArchivos);
            btnFirma = (Button) v.findViewById(R.id.btnFirma);

            btnEditar.setOnClickListener(this);
            btnArchivos.setOnClickListener(this);
            btnFirma.setOnClickListener(this);

            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int id = view.getId();
            int pos = getAdapterPosition();
            String idBitacoraCredito = obtenerId(pos);
            Uri uriBitacoraCredito=null;
            String detalle="";

            uriBitacoraCredito = BitacorasCredito.construirUri(idBitacoraCredito);

            switch(id){
                case R.id.btnEditar:
                    detalle="bitacora_credito";
                    break;
                case R.id.btnArchivos:
                    detalle="archivos";
                    break;
                case R.id.btnFirma:
                    detalle="firma";
                    break;
            }
            if(!TextUtils.isEmpty(detalle)){
                mostrarDetalles(view,uriBitacoraCredito,detalle);
            }
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
                return UConsultas.obtenerString(items, BitacorasCredito.ID);
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
            if(detalle.equals("bitacora_credito")){
                intent.setClass(v.getContext(),ActividadInsercionBitacoraCredito.class);
                intent.putExtra(ActividadInsercionBitacoraCredito.URI_SOLICITUD, uriSolicitud.toString());
                intent.putExtra(ActividadInsercionBitacoraCredito.URI_BITACORA_CREDITO, uri.toString());
            }else if(detalle.equals("archivos")){
                intent.setClass(v.getContext(),ActividadListaBitacorasCreditoArchivos.class);
                intent.putExtra(ActividadListaBitacorasCreditoArchivos.URI_SOLICITUD, uriSolicitud.toString());
                intent.putExtra(ActividadListaBitacorasCreditoArchivos.URI_BITACORA_CREDITO, uri.toString());
            }else if(detalle.equals("firma")){
                intent.setClass(v.getContext(),ActividadFirma.class);
                intent.putExtra(ActividadFirma.URI_SOLICITUD, uriSolicitud.toString());
                intent.putExtra(ActividadFirma.URI_BITACORA_CREDITO, uri.toString());
            }
        }
        v.getContext().startActivity(intent);
    }

    public AdaptadorBitacorasCredito(Uri uri) {
        uriSolicitud=uri;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //Se asigna al layout a utilizar
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.tarjeta_bitacora_credito, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        //Se llena el layout con la información del cursor
        items.moveToPosition(position);

        String asunto;
        String fecha;
        String hora;
        String descripcion;

        asunto = UConsultas.obtenerString(items, BitacorasCredito.ASUNTO);
        fecha = UConsultas.obtenerString(items, BitacorasCredito.FECHA);
        hora = UConsultas.obtenerString(items, BitacorasCredito.HORA);
        descripcion = UConsultas.obtenerString(items, BitacorasCredito.DESCRIPCION);

        holder.asunto.setText(asunto);
        holder.fecha.setText(fecha);
        holder.hora.setText(hora);
        holder.descripcion.setText(descripcion);
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
