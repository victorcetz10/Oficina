package com.softcredito.app.ui;

import android.accounts.Account;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.softcredito.app.R;
import com.softcredito.app.provider.Contract;
import com.softcredito.app.provider.Contract.Cotizadores;
import com.softcredito.app.provider.Contract.Productos;
import com.softcredito.app.sync.SyncAdapter;
import com.softcredito.app.utilidades.UConsultas;
import com.softcredito.app.utilidades.UCuentas;
import com.softcredito.app.utilidades.UWeb;

import java.text.DecimalFormat;

//import android.support.design.widget.Snackbar;
//import android.support.v4.content.LocalBroadcastManager;
//import android.support.v7.widget.CardView;
//import android.support.v7.widget.RecyclerView;

/**
 * Adaptador para la lista de cotizadores
 */
public class AdaptadorCotizadores extends RecyclerView.Adapter<AdaptadorCotizadores.ViewHolder> {

    private static final String TAG = ActividadListaCotizadores.class.getSimpleName();
    private final String ruta_cotizadores = Environment.getExternalStorageDirectory().getAbsolutePath() +"/softcredito/cotizadores/";
    private BroadcastReceiver receptorSync;
    public ProgressDialog progress;

    private Cursor items;

    // Instancia de escucha
    private OnItemClickListener escucha;
    private AppCompatActivity actividad;

    /**
     * Interfaz para escuchar clicks del recycler
     */
    interface OnItemClickListener {
        public void onClick(ViewHolder holder, String id);
    }

    public class ViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        // Campos respectivos de un item
        public CardView tarjeta_cotizador;
        public TextView producto;
        public TextView fecha;
        public TextView validez;
        public TextView monto;
        public TextView plazo;
        public TextView tipo_amortizacion;

        public Button btnPDF;

        public ViewHolder(View v) {
            super(v);
            tarjeta_cotizador = (CardView) v.findViewById(R.id.tarjeta_cotizador);
            producto = (TextView) v.findViewById(R.id.text_producto);
            fecha = (TextView) v.findViewById(R.id.text_fecha);
            validez = (TextView) v.findViewById(R.id.text_validez);
            monto = (TextView) v.findViewById(R.id.text_monto);
            plazo = (TextView) v.findViewById(R.id.text_plazo);
            tipo_amortizacion = (TextView) v.findViewById(R.id.text_tipo_amortizacion);

            final View view = v;

            btnPDF = (Button) v.findViewById(R.id.btnPdf);

            btnPDF.setOnClickListener(this);

            progress = new ProgressDialog(v.getContext());

            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int id = view.getId();
            int pos = getAdapterPosition();
            String idCotizador = obtenerId(pos);

            switch(id){
                case R.id.btnPdf:
                    mostrarPDF(view,idCotizador);
                    break;
                default:
                    escucha.onClick(this, obtenerId(getAdapterPosition()));
                    break;
            }
        }
    }

    /**
     * Obtiene el valor de la columna 'id' basado en la posición actual del cursor
     * @param posicion Posición actual del cursor
     * @return Identificador de la cotizador
     */
    private String obtenerId(int posicion) {
        if (items != null) {
            if (items.moveToPosition(posicion)) {
                return UConsultas.obtenerString(items, Cotizadores.ID);
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    private void mostrarPDF(View v, String id){
        // Verificación para evitar iniciar más de una sync a la vez
        Account cuentaActiva = UCuentas.obtenerCuentaActiva(v.getContext());
        if (ContentResolver.isSyncActive(cuentaActiva, Contract.AUTORIDAD)) {
            Log.d(TAG, "Ignorando sincronización ya que existe una en proceso.");
            return;
        }

        String table="cotizadores";
        String row=id;

        if (UWeb.hayConexion(v.getContext())) {
            mostrarProgreso(true);
            Log.d(TAG, "Solicitando sincronización manual");
            Bundle bundle = new Bundle();
            bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
            bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
            bundle.putString(SyncAdapter.SYNC_TABLE, table);
            bundle.putString(SyncAdapter.SYNC_TABLE_ROW, row);
            ContentResolver.requestSync(cuentaActiva, Contract.AUTORIDAD, bundle);
        } else {
            Snackbar.make(actividad.findViewById(R.id.coordinador),
                    "No hay conexion disponible no se puede generar el PDF",
                    Snackbar.LENGTH_LONG).show();
        }

    }

    public AdaptadorCotizadores(OnItemClickListener escucha, AppCompatActivity actividad) {
        this.escucha = escucha;
        this.actividad = actividad;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //Se asigna al layout a utilizar
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.tarjeta_cotizador, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        //Se llena el layout con la información del cursor
        items.moveToPosition(position);

        String id_producto;
        String fecha;
        String validez;
        Float monto;
        String plazo;
        String productosS;
        String amortizacionsS;
        String id_tipo_amortizacion;

        id_producto = UConsultas.obtenerString(items, Cotizadores.ID_PRODUCTO);
        fecha = UConsultas.obtenerString(items, Cotizadores.FECHA_COTIZACION);
        productosS = UConsultas.obtenerString ( items, Cotizadores.NOMBRE_PRODUCTO_COTIZADOR );
        amortizacionsS = UConsultas.obtenerString ( items, Cotizadores.NOMBRE_AMORTIZACION_COTIZADOR );
        validez = UConsultas.obtenerString(items, Cotizadores.VALIDEZ) +  " dìas";
        monto = Float.parseFloat(UConsultas.obtenerString(items, Cotizadores.MONTO_AUTORIZADO));
        plazo = UConsultas.obtenerString(items, Cotizadores.PLAZO_AUTORIZADO);
        id_tipo_amortizacion = UConsultas.obtenerString(items, Cotizadores.ID_TIPO_AMORTIZACION);

        DecimalFormat formateador = new DecimalFormat("###,###.00");


        Cursor cProducto;
        Cursor cTipoAmortizacion;
        Uri uProducto= Productos.construirUri(id_producto);
        Uri uTipoAmortizacion= Contract.TiposAmortizacion.construirUri(id_tipo_amortizacion);

        cProducto = holder.itemView.getContext().getContentResolver().query(uProducto,null,null,null,null);
        cTipoAmortizacion = holder.itemView.getContext().getContentResolver().query(uTipoAmortizacion,null,null,null,null);

        cProducto.moveToPosition(0);
        cTipoAmortizacion.moveToPosition(0);

       // String producto=UConsultas.obtenerString(cProducto, Contract.Solicitudes.ID_PRODUCTO );
        //String tipoAmortizacion=UConsultas.obtenerString(cTipoAmortizacion, Contract.TiposAmortizacion.PLAZO);

        holder.producto.setText(productosS);
        holder.fecha.setText(fecha);
        holder.validez.setText(validez);
        holder.monto.setText(formateador.format(monto));
        holder.plazo.setText(plazo);
        holder.tipo_amortizacion.setText(amortizacionsS);

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

    private void mostrarProgreso(boolean mostrar) {
        if(mostrar){
            progress.setTitle("Generando");
            progress.setMessage("Espere mientras se genera su Archivo");
            progress.setCancelable(false);
            progress.show();
        }else{
            progress.dismiss();
        }
    }
}
