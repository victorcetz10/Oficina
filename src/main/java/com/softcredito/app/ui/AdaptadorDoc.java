package com.softcredito.app.ui;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.softcredito.app.CapturarDocumento;
import com.softcredito.app.IAxiliarPersona;
import com.softcredito.app.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AdaptadorDoc extends RecyclerView.Adapter<AdaptadorDoc.personaView> implements Filterable, IAxiliarPersona {


    private List<Document> personaList = new ArrayList<> ();
    private Context context;
    final int REQUEST_CODE_GALLERY = 999;

    private ArrayList<Document> personaArrayList;


    private IAxiliarPersona iAxiliarPersona;

    public AdaptadorDoc(IAxiliarPersona iAxiliarPersona, ArrayList<Document> personaList) {
        this.iAxiliarPersona = iAxiliarPersona;
        this.personaList = personaList;
        this.personaArrayList = personaList;
    }

    @NonNull
    @Override
    public personaView onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_mostrar, viewGroup, false);
        return new personaView(view);

    }

    @Override
    public void onBindViewHolder(final personaView personaView, int i) {
        final Document persona = personaList.get(i);

        personaView.txtcodigoMostrar.setText(String.valueOf(persona.getId ()));
        personaView.txtnombreMostrar.setText(persona.getNombre());
        personaView.txtapellidosMostrar.setText(persona.getRuta ());
        personaView.txtedadMostrar.setText(persona.getTipo ());
        personaView.btnEditar.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                Intent intento1 = new Intent( v.getContext (), CapturarDocumento.class);
                v.getContext ().startActivity ( intento1 );
                SimpleDateFormat timeStampFormat = new SimpleDateFormat ("yyyy-MM-dd-HH-mm-ss-SS");
                Date myDate = new Date ();
                String date = timeStampFormat.format(myDate);
                personaView.txtapellidosMostrar.setText ( date );
                Toast.makeText ( v.getContext (), "Hola",Toast.LENGTH_SHORT ).show ();
            }
        } );
        personaView.btnEliminar.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {

            }
        } );
    }




    @Override
    public int getItemCount() {
        return personaList.size();
    }




    public void agregarPersona(Document persona) {
        personaList.add(persona);
        this.notifyDataSetChanged();
    }

    public void eliminarPersona(Document persona) {
        personaList.remove(persona);
        this.notifyDataSetChanged();
    }

    @Override
    public void OpcionEditar(Document persona ) {
        //Intent intento1 = new Intent( MediaStore.ACTION_IMAGE_CAPTURE);




    }

    @Override
    public void OpcionEliminar(Document persona) {

    }

    @Override
    public Filter getFilter() {

        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String palabra = constraint.toString();

                if (palabra.isEmpty()) {
                    personaList = personaArrayList;
                } else {
                    ArrayList<Document> filtrarLista = new ArrayList<>();
                    for (Document persona : personaArrayList) {
                        if (persona.getNombre().toLowerCase().contains(constraint)) {
                            filtrarLista.add(persona);
                        }
                    }
                    personaList = filtrarLista;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = personaList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                personaList = (ArrayList<Document>) results.values;
                notifyDataSetChanged();
            }
        };
    }


    class eventoEditar implements View.OnClickListener {

        private Document persona;

        public eventoEditar(Document persona) {
            this.persona = persona;

        }

        @Override
        public void onClick(View v) {
            iAxiliarPersona.OpcionEditar(persona);
        }
    }


    class eventoEliminar implements View.OnClickListener {
        private Document persona;

        public eventoEliminar(Document persona) {
            this.persona = persona;
        }

        @Override
        public void onClick(View v) {
            iAxiliarPersona.OpcionEliminar(persona);
        }
    }


    public class personaView extends RecyclerView.ViewHolder {
        private TextView txtcodigoMostrar, txtnombreMostrar, txtapellidosMostrar, txtedadMostrar;
        private Button btnEditar, btnEliminar;
        private ImageView ImB;


        public personaView(@NonNull View itemView) {
            super(itemView);
            txtcodigoMostrar = itemView.findViewById( R.id.txtcodigoMostrar);
            txtnombreMostrar = itemView.findViewById(R.id.txtNombreMostrar);
            txtapellidosMostrar = itemView.findViewById(R.id.txtApellidosMostrar);
            txtedadMostrar = itemView.findViewById(R.id.txtEdadMostrar);
            btnEditar = itemView.findViewById(R.id.btnEditar);
            btnEliminar = itemView.findViewById(R.id.btnEliminar);
        }
    }

}