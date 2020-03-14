package com.softcredito.app.ui;

import android.content.Intent;
import android.database.Cursor;

import java.text.DecimalFormat;

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

import com.entidades.ClientesList;
import com.softcredito.app.R;
import com.softcredito.app.provider.Contract;
import com.softcredito.app.provider.Contract.Solicitudes;
import com.softcredito.app.provider.Contract.Productos;
import com.softcredito.app.utilidades.UConsultas;

/**
 * Adaptador para la lista de solicitudes
 */
public class AdaptadorSolicitudes extends RecyclerView.Adapter<AdaptadorSolicitudes.ViewHolder> {
    private Cursor items;

    // Instancia de escucha
    private OnItemClickListener escucha;

    /**
     * Interfaz para escuchar clicks del recycler
     */
    interface OnItemClickListener {
        public void onClick(ViewHolder holder, String id);
    }

    public class ViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        // Campos respectivos de un item
        public CardView tarjeta_solicitud;
        public TextView producto;
        public TextView fecha;
        public TextView monto;
        public TextView plazo;
        public TextView tipo_amortizacion;
        public Button btnEditar;

        public ViewHolder(View v) {
            super(v);
            tarjeta_solicitud = (CardView) v.findViewById(R.id.tarjeta_solicitud);
            producto = (TextView) v.findViewById(R.id.text_producto);
            fecha = (TextView) v.findViewById(R.id.text_fecha);
            monto = (TextView) v.findViewById(R.id.text_monto);
            plazo = (TextView) v.findViewById(R.id.text_plazo);
            tipo_amortizacion = (TextView) v.findViewById(R.id.text_tipo_amortizacion);
            btnEditar = (Button) v.findViewById(R.id.btnEditar);

            btnEditar.setOnClickListener(this);

            v.setOnClickListener(this);
        }

        public void onClickOld(View view) {
            escucha.onClick(this, obtenerId(getAdapterPosition()));
        }

        @Override
        public void onClick(View view) {
            int id = view.getId();
            int pos = getAdapterPosition();
            String idSolicitud = obtenerId(pos);
            Uri uriSolicitud=null;
            String detalle="";

            uriSolicitud = Solicitudes.construirUri(idSolicitud);

            switch(id){
                case R.id.btnEditar:
                    detalle="solicitud";
                    break;
            }
            if(!TextUtils.isEmpty(detalle)){
                mostrarDetalles(view,uriSolicitud,detalle);
            }
        }
    }

    /**
     * Obtiene el valor de la columna 'id' basado en la posición actual del cursor
     * @param posicion Posición actual del cursor
     * @return Identificador de la solicitud
     */
    private String obtenerId(int posicion) {
        if (items != null) {
            if (items.moveToPosition(posicion)) {
                return UConsultas.obtenerString(items, Solicitudes.ID);
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    //public AdaptadorSolicitudes(OnItemClickListener escucha) {
    public AdaptadorSolicitudes() {

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //Se asigna al layout a utilizar
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.tarjeta_solicitud, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        //Se llena el layout con la información del cursor
        items.moveToPosition(position);

        String id_producto;
        String fecha;
        String productos;
        String amortizacions;
        Float monto;
        String plazo;
        String id_tipo_amortizacion;

        id_producto = UConsultas.obtenerString(items, Solicitudes.ID_PRODUCTO);
        productos = UConsultas.obtenerString ( items, Solicitudes.NOMBRE_PRODUCTO );
        amortizacions = UConsultas.obtenerString ( items, Solicitudes.NOMBRE_AMORTIZACION );
        fecha = UConsultas.obtenerString(items, Solicitudes.FECHA_SOLICITUD);
        monto = Float.parseFloat(UConsultas.obtenerString(items, Solicitudes.MONTO_SOLICITADO));
        plazo = UConsultas.obtenerString(items, Solicitudes.PLAZO_SOLICITADO);
        id_tipo_amortizacion = UConsultas.obtenerString(items, Solicitudes.ID_TIPO_AMORTIZACION);

        DecimalFormat formateador = new DecimalFormat("###,###.00");


        Cursor cProducto;
        Cursor cTipoAmortizacion;
        Uri uProducto= Contract.Productos.construirUri(id_producto);
        Uri uTipoAmortizacion= Contract.TiposAmortizacion.construirUri(id_tipo_amortizacion);

        cProducto = holder.itemView.getContext().getContentResolver().query(uProducto,null,null,null,null);
        cTipoAmortizacion = holder.itemView.getContext().getContentResolver().query(uTipoAmortizacion,null,null,null,null);

        cProducto.moveToPosition(0);
        cTipoAmortizacion.moveToPosition(0);

       // String producto=UConsultas.obtenerString(cProducto, Solicitudes.ID_PRODUCTO );
        //String tipoAmortizacion=ClientesList.NOMBRE_AMORTIZACION;

        holder.producto.setText(productos);
        holder.fecha.setText(fecha);
        holder.monto.setText(formateador.format(monto));
        holder.plazo.setText(plazo);
        holder.tipo_amortizacion.setText(amortizacions);
    }


    void mostrarDetalles(View v,Uri uri,String detalle) {
        Intent intent = new Intent();
        if (null != uri) {
            if(detalle.equals("solicitud")){
                intent.setClass(v.getContext(),ActividadInsercionSolicitud.class);
                intent.putExtra(ActividadInsercionSolicitud.URI_SOLICITUD, uri.toString());
            }
        }
        v.getContext().startActivity(intent);
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
