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
import com.softcredito.app.provider.Contract;
import com.softcredito.app.provider.Contract.Solicitudes;
import com.softcredito.app.provider.Contract.Clientes;
import com.softcredito.app.provider.Contract.Grupos;
import com.softcredito.app.utilidades.UConsultas;
import com.softcredito.app.utilidades.UTiempo;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Adaptador para la lista de solicitudes
 */
public class AdaptadorAgendaSolicitudes extends RecyclerView.Adapter<AdaptadorAgendaSolicitudes.ViewHolder> {
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
        public CardView tarjeta_agenda_solicitud;
        public TextView cliente_solicitud;
        public TextView detalle_solicitud;
        public TextView clave;
        public TextView contrato;
        public TextView dias_traso;
        public TextView monto_pagar;

        public Button btnBitacoraCredito;
        public Button btnPagar;

        public ViewHolder(View v) {
            super(v);
            tarjeta_agenda_solicitud = (CardView) v.findViewById(R.id.tarjeta_agenda_solicitud);
            cliente_solicitud = (TextView) v.findViewById(R.id.text_cliente);
            detalle_solicitud = (TextView) v.findViewById(R.id.text_detalle_solicitud);
            clave = (TextView) v.findViewById(R.id.text_clave_solicitud);
            contrato = (TextView) v.findViewById(R.id.text_contrato_solicitud);
            dias_traso = (TextView) v.findViewById(R.id.text_dias_atraso);
            monto_pagar = (TextView) v.findViewById(R.id.text_monto_pago);

            btnBitacoraCredito = (Button) v.findViewById(R.id.btnBitacora);
            btnPagar = (Button) v.findViewById(R.id.btnPagar);

            btnBitacoraCredito.setOnClickListener(this);
            btnPagar.setOnClickListener(this);

            //v.setOnClickListener(this);
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
                case R.id.btnBitacora:
                    detalle="bitacora_credito";
                    break;
                case R.id.btnPagar:
                    detalle="pagar";
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

    void mostrarDetalles(View v,Uri uri,String detalle) {
        Intent intent = new Intent();
        if (null != uri) {
            if(detalle.equals("bitacora_credito")){
                intent.setClass(v.getContext(),ActividadListaBitacorasCredito.class);
                intent.putExtra(ActividadListaBitacorasCredito.URI_SOLICITUD, uri.toString());
            }else if(detalle.equals("pagar")){
                intent.setClass(v.getContext(),ActividadListaPagos.class);
                intent.putExtra(ActividadListaPagos.URI_SOLICITUD, uri.toString());
            }
        }
        v.getContext().startActivity(intent);
    }

    public AdaptadorAgendaSolicitudes() {

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //Se asigna al layout a utilizar
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.tarjeta_agenda_solicitud, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        //Se llena el layout con la información del cursor
        items.moveToPosition(position);

        String cliente;
        String grupo;
        String fecha_solicitud;
        String monto_solicitud;
        Float f_monto_solicitud;
        String clave;
        String contrato;
        String monto_pagar;
        Float f_monto_pagar;
        String fecha_vencimiento;
        Integer dias_atraso=0;

        cliente = UConsultas.obtenerString(items, Solicitudes.ID_CLIENTE);
        grupo = UConsultas.obtenerString(items, Solicitudes.ID_GRUPO);

        fecha_solicitud = UConsultas.obtenerString(items, Solicitudes.FECHA_SOLICITUD);
        monto_solicitud = UConsultas.obtenerString(items, Solicitudes.MONTO_SOLICITADO);
        clave = UConsultas.obtenerString(items, Solicitudes.CLAVE);
        contrato = UConsultas.obtenerString(items, Solicitudes.CONTRATO);
        monto_pagar = UConsultas.obtenerString(items, Solicitudes.MONTO_PAGAR);
        fecha_vencimiento = UConsultas.obtenerString(items, Solicitudes.FECHA_VENCIMIENTO);

        String razon_social;
        String nombre1 = "";
        String nombre2 = "";
        String apellido_paterno = "";
        String apellido_materno = "";
        String nombre_completo = "";
        String tipo_persona = "";

        if(TextUtils.isEmpty(grupo)){
            Uri uriCliente = Clientes.construirUri(cliente);
            Cursor cCliente = holder.cliente_solicitud.getContext().getContentResolver().query(
                    uriCliente,
                    null,
                    null,
                    null,
                    null
            );
            if(cCliente.getCount()>0){
                cCliente.moveToPosition(0);
                razon_social = UConsultas.obtenerString(cCliente, Clientes.RAZON_SOCIAL);
                nombre1 = UConsultas.obtenerString(cCliente, Contract.Clientes.PRIMER_NOMBRE);
                nombre2 = UConsultas.obtenerString(cCliente, Contract.Clientes.SEGUNDO_NOMBRE);
                apellido_paterno = UConsultas.obtenerString(cCliente, Clientes.PRIMER_APELLIDO);
                apellido_materno = UConsultas.obtenerString(cCliente, Clientes.SEGUNDO_APELLIDO);
                tipo_persona = UConsultas.obtenerString(cCliente, Clientes.TIPO_PERSONA);
                if(tipo_persona.equals("Persona Fisica")){
                    nombre_completo=String.format("%s %s %s %s", nombre1, nombre2, apellido_paterno, apellido_materno);
                }else if(tipo_persona.equals("Persona Moral")){
                    nombre_completo=razon_social;
                }
            }
        }else{
            Uri uriGrupo = Grupos.construirUri(grupo);
            Cursor cGrupo = holder.cliente_solicitud.getContext().getContentResolver().query(
                    uriGrupo,
                    null,
                    null,
                    null,
                    null
            );
            if(cGrupo.getCount()>0){
                cGrupo.moveToPosition(0);
                nombre1 = UConsultas.obtenerString(cGrupo, Grupos.NOMBRE);
                nombre_completo=nombre1;
            }
        }


        SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");

        if(!TextUtils.isEmpty(fecha_vencimiento)){
            try {
                Date fechaA = formato.parse(fecha_vencimiento);
                Date fechaB = formato.parse(UTiempo.obtenerFecha());

                dias_atraso = fechaA.compareTo(fechaB);

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }else{
            dias_atraso=0;
        }

        NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
        DecimalFormat formateador = (DecimalFormat) nf;
        formateador.applyPattern("###,###.##");

        if(TextUtils.isEmpty(monto_solicitud)){
            f_monto_solicitud=Float.parseFloat("0");
            monto_solicitud=f_monto_solicitud.toString();
        }else{
            f_monto_solicitud=Float.parseFloat(monto_solicitud);
            monto_solicitud=formateador.format(f_monto_solicitud);
        }
        if(TextUtils.isEmpty(monto_pagar)){
            f_monto_pagar=Float.parseFloat("0");
            monto_pagar=f_monto_pagar.toString();
        }else{
            f_monto_pagar=Float.parseFloat(monto_pagar);
            monto_pagar=formateador.format(f_monto_pagar);
        }

        holder.cliente_solicitud.setText(nombre_completo);
        holder.detalle_solicitud.setText(monto_solicitud + " el " + fecha_solicitud);
        holder.clave.setText("Sol." + clave);
        holder.contrato.setText("Cont.:" + contrato);
        holder.monto_pagar.setText(monto_pagar);
        holder.dias_traso.setText("Atraso: " + dias_atraso.toString());
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
