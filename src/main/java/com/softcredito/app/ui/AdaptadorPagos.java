package com.softcredito.app.ui;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
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
import com.softcredito.app.provider.Contract.Pagos;
import com.softcredito.app.provider.Contract.CanalesCobranzas;
import com.softcredito.app.provider.Contract.InstrumentosMonetarios;
import com.softcredito.app.utilidades.UConsultas;

import java.text.DecimalFormat;

/**
 * Adaptador para la lista de bitacoras de crédito
 */
public class AdaptadorPagos extends RecyclerView.Adapter<AdaptadorPagos.ViewHolder> {

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
        public CardView tarjeta_pago;
        public TextView fecha;
        public TextView monto;
        public TextView canal_cobranza;
        public TextView instrumento_monetario;

        public Button btnEditar;

        public ViewHolder(View v) {
            super(v);
            tarjeta_pago = (CardView) v.findViewById(R.id.tarjeta_pago);
            fecha = (TextView) v.findViewById(R.id.text_fecha);
            monto = (TextView) v.findViewById(R.id.text_monto);
            canal_cobranza = (TextView) v.findViewById(R.id.text_canal);
            instrumento_monetario = (TextView) v.findViewById(R.id.text_instrumento);

            btnEditar = (Button) v.findViewById(R.id.btnEditar);

            btnEditar.setOnClickListener(this);

            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int id = view.getId();
            int pos = getAdapterPosition();
            String idPago = obtenerId(pos);
            Uri uriPago=null;
            String detalle="";

            uriPago = Pagos.construirUri(idPago);

            switch(id){
                case R.id.btnEditar:
                    detalle="pago";
                    break;
            }
            if(!TextUtils.isEmpty(detalle)){
                mostrarDetalles(view,uriPago,detalle);
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
                return UConsultas.obtenerString(items, Pagos.ID);
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
            if(detalle.equals("pago")){
                intent.setClass(v.getContext(),ActividadInsercionPago.class);
                intent.putExtra(ActividadInsercionPago.URI_SOLICITUD, uriSolicitud.toString());
                intent.putExtra(ActividadInsercionPago.URI_PAGO, uri.toString());
            }
        }
        v.getContext().startActivity(intent);
    }

    public AdaptadorPagos(Uri uri) {
        uriSolicitud=uri;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //Se asigna al layout a utilizar
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.tarjeta_pago, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        //Se llena el layout con la información del cursor
        items.moveToPosition(position);

        String fecha;
        Float monto;
        String canal;
        String instrumento;

        fecha = UConsultas.obtenerString(items, Pagos.FECHA);
        monto = Float.parseFloat(UConsultas.obtenerString(items, Pagos.MONTO));
        canal = UConsultas.obtenerString(items, Pagos.ID_CANAL_COBRANZA);
        instrumento = UConsultas.obtenerString(items, Pagos.ID_INSTRUMENTO_MONETARIO);

        DecimalFormat formateador = new DecimalFormat("###,###.00");

        Cursor cCanal;
        Cursor cInstrumento;
        Uri uCanal= CanalesCobranzas.construirUri(canal);
        Uri uInstrumento= InstrumentosMonetarios.construirUri(instrumento);

        cCanal = holder.itemView.getContext().getContentResolver().query(uCanal,null,null,null,null);
        cInstrumento = holder.itemView.getContext().getContentResolver().query(uInstrumento,null,null,null,null);

        cCanal.moveToPosition(0);
        cInstrumento.moveToPosition(0);

        canal=UConsultas.obtenerString(cCanal, CanalesCobranzas.NOMBRE);
        instrumento=UConsultas.obtenerString(cInstrumento, InstrumentosMonetarios.DESCRIPCION);

        holder.fecha.setText(fecha);
        holder.monto.setText(formateador.format(monto));
        holder.canal_cobranza.setText(canal);
        holder.instrumento_monetario.setText(instrumento);
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
