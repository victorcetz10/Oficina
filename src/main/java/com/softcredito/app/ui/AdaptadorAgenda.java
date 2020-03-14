package com.softcredito.app.ui;

import android.database.Cursor;
//import android.support.v7.widget.CardView;
import androidx.cardview.widget.CardView;
//import android.support.v7.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.softcredito.app.R;
import com.softcredito.app.provider.Contract.Clientes;
import com.softcredito.app.utilidades.UConsultas;

/**
 * Adaptador para la lista de clientes
 */
public class AdaptadorAgenda extends RecyclerView.Adapter<AdaptadorAgenda.ViewHolder> {
    private Cursor items;

    /**
     * Interfaz para escuchar clicks del recycler
     */
    interface OnItemClickListener {
        public void onClick(ViewHolder holder, String id);
    }

    public class ViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        // Campos respectivos de un item
        public CardView tarjeta_agenda;
        public ImageView foto;
        public TextView nombre;
        public TextView colocado;
        public TextView dispuesto;
        public TextView pagado;
        public TextView saldo;
        public TextView vencido;
        public TextView por_vencer;

        public ViewHolder(View v) {
            super(v);
            tarjeta_agenda = (CardView) v.findViewById(R.id.tarjeta_agenda);
            foto = (ImageView) v.findViewById(R.id.foto_cliente);
            nombre = (TextView) v.findViewById(R.id.nombre_cliente);
            colocado = (TextView) v.findViewById(R.id.colocado_cliente);
            dispuesto = (TextView) v.findViewById(R.id.dispuesto_cliente);
            pagado = (TextView) v.findViewById(R.id.pagado_cliente);
            saldo = (TextView) v.findViewById(R.id.saldo_cliente);
            vencido = (TextView) v.findViewById(R.id.vencido_cliente);
            por_vencer = (TextView) v.findViewById(R.id.por_vencer_cliente);

            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

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

    public AdaptadorAgenda() {

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //Se asigna al layout a utilizar
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.tarjeta_agenda, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        //Se llena el layout con la información del cursor
        items.moveToPosition(position);

        /*
        String nombre;
        String nombre2;
        String apellido_paterno;
        String apellido_materno;
        String colocado;
        String dispuesto;
        String pagado;
        String saldo;
        String vencido;
        String por_vencer;

        NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
        DecimalFormat formateador = (DecimalFormat) nf;
        formateador.applyPattern("###,###.##");

        nombre = UConsultas.obtenerString(items, Clientes.PRIMER_NOMBRE);
        nombre2 = UConsultas.obtenerString(items, Clientes.SEGUNDO_NOMBRE);
        apellido_paterno = UConsultas.obtenerString(items, Clientes.PRIMER_APELLIDO);
        apellido_materno = UConsultas.obtenerString(items, Clientes.SEGUNDO_APELLIDO);
        colocado = UConsultas.obtenerString(items, Clientes.COLOCADO);
        dispuesto = UConsultas.obtenerString(items, Clientes.DISPUESTO);
        pagado = UConsultas.obtenerString(items, Clientes.PAGADO);
        saldo = UConsultas.obtenerString(items, Clientes.SALDO);
        vencido = UConsultas.obtenerString(items, Clientes.VENCIDO);
        por_vencer = UConsultas.obtenerString(items, Clientes.POR_VENCER);



        if(TextUtils.isEmpty(colocado)){
            colocado="0";
        }
        if(TextUtils.isEmpty(dispuesto)){
            dispuesto="0";
        }
        if(TextUtils.isEmpty(pagado)){
            pagado="0";
        }
        if(TextUtils.isEmpty(saldo)){
            colocado="0";
        }
        if(TextUtils.isEmpty(vencido)){
            vencido="0";
        }
        if(TextUtils.isEmpty(por_vencer)){
            por_vencer="0";
        }

        colocado="Colocado: "+formateador.format(Float.parseFloat(colocado));
        dispuesto="Dispuesto: "+formateador.format(Float.parseFloat(dispuesto));
        pagado="Pagado: "+formateador.format(Float.parseFloat(pagado));
        saldo="Saldo: "+formateador.format(Float.parseFloat(saldo));
        vencido="Vencido: "+formateador.format(Float.parseFloat(vencido));
        por_vencer="Por Vencer: "+formateador.format(Float.parseFloat(por_vencer));

        holder.nombre.setText(String.format("%s %s %s %s", nombre, nombre2, apellido_paterno, apellido_materno));
        holder.colocado.setText(colocado);
        holder.dispuesto.setText(dispuesto);
        holder.pagado.setText(pagado);
        holder.saldo.setText(saldo);
        holder.vencido.setText(vencido);
        holder.por_vencer.setText(por_vencer);
        */
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
