package com.softcredito.app.ui;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.os.Bundle;
//import android.support.design.widget.Snackbar;
//import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.ImageButton;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.softcredito.app.AgregarDocumentos;
import com.softcredito.app.AgregarEconomia;
import com.softcredito.app.AgregarProducto;
import com.softcredito.app.AgregarSolicitud;
import com.softcredito.app.Agregar_TiposPersonas;
import com.softcredito.app.R;
import com.softcredito.app.Sincronizacion;
import com.softcredito.app.service.SoftcreditoLocation;
import com.softcredito.app.utilidades.UPreferencias;

public class ActividadHome extends Actividad
        implements View.OnClickListener{

    private static final String TAG = ActividadHome.class.getSimpleName();
    private BroadcastReceiver receptorSync;
    private ProgressDialog progress;
    FloatingActionMenu actionMenu;
    FloatingActionButton actionButton;
    FloatingActionButton actionButton2;
    FloatingActionButton actionButton3;
    FloatingActionButton actionButton4;
    FloatingActionButton actionButton5;
    FloatingActionButton actionButton6;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actividad_home);
        setTitle(R.string.titulo_actividad_home);
        prepararBotones();

        progress = new ProgressDialog(this);

        String api = UPreferencias.obtenerClaveApi(progress.getContext());
        String urlProduccion = UPreferencias.obtenerProduccion(this);
        Intent servicio = new Intent(this, SoftcreditoLocation.class);
        servicio.putExtra(SoftcreditoLocation.API_KEY,api);
        servicio.putExtra(SoftcreditoLocation.URL_PRODUCCION,urlProduccion);
        startService(servicio);

        actionMenu=(FloatingActionMenu)findViewById ( R.id.fabPrincipal );
        actionMenu.setClosedOnTouchOutside ( true );
        actionButton6=(FloatingActionButton)findViewById ( R.id.submenu6 );
        actionButton6.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent ( ActividadHome.this, AgregarDocumentos.class );
                startActivity ( intent );
            }
        } );


    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    protected void prepararBotones() {
        ImageButton mClickImageButton2 = (ImageButton)findViewById(R.id.ib_clientes);
        mClickImageButton2.setOnClickListener(this);
        ImageButton mClickImageButton1 = (ImageButton)findViewById(R.id.ib_agenda);
        mClickImageButton1.setOnClickListener(this);
        ImageButton mClickImageButton3 = (ImageButton)findViewById(R.id.ib_mapa);
        mClickImageButton3.setOnClickListener(this);
        ImageButton mClickImageButton5 = (ImageButton)findViewById(R.id.ib_sync);
        mClickImageButton5.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ib_clientes: {
                Intent intent = new Intent(ActividadHome.this, ActividadListaClientes.class);
                startActivity(intent);
                break;
            }
            case  R.id.ib_agenda: {
                Intent intent = new Intent(ActividadHome.this, ActividadListaAgendaSolicitudes.class);
                startActivity(intent);
                break;
            }
            case R.id.ib_mapa: {
                Intent intent = new Intent(ActividadHome.this, ActividadMapa.class);
                startActivity(intent);
                break;
            }
            case R.id.ib_sync: {
                Intent intent = new Intent(ActividadHome.this, Sincronizacion.class);
                startActivity(intent);

                break;
            }
        }
    }

    private void mostrarProgreso(boolean mostrar) {
        if(mostrar){
            progress.setTitle("Sincronizando");
            progress.setMessage("Espere mientras se sincroniza su información");
            progress.setCancelable(false);
            progress.show();
        }else{
            progress.dismiss();
        }
    }
}
