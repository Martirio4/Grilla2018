package com.auditoria.grilla5s.View.Fragments;



import android.os.Bundle;
import android.support.annotation.NonNull;

import android.support.v4.app.Fragment;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;
import android.widget.Toast;

import com.auditoria.grilla5s.Model.Item;
import com.auditoria.grilla5s.Model.Pregunta;
import com.auditoria.grilla5s.R;
import com.auditoria.grilla5s.View.Adapter.AdapterPreguntas;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.RealmResults;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentVerPregunta extends Fragment {


    public static final String TIPOCUESTIONARIO = "TIPOCUESTIONARIO";
    public static final String IDITEM = "IDITEM";
    public static final String ORIGEN = "ORIGEN";
    public static final String IDESE = "IDESE";


    private String idItem;
    private String origen;
    private String idCuestionario;
    private String idEse;

    private RecyclerView recyclerPreguntas;
    private AdapterPreguntas adapterPreguntas;

    public FragmentVerPregunta() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_editar_preguntas, container, false);

        Bundle bundle = getArguments();
        if (bundle == null) {
            Toast.makeText(getContext(), getResources().getString(R.string.noEncontroDatos), Toast.LENGTH_SHORT).show();
        } else {

            idItem = bundle.getString(IDITEM);
            origen = bundle.getString(ORIGEN);
            idCuestionario = bundle.getString(TIPOCUESTIONARIO);
            idEse = bundle.getString(IDESE);
        }
        TextView textoPregunta = view.findViewById(R.id.textoPregunta);
        TextView criterioTitulo = view.findViewById(R.id.tituloCriterio);
        TextView criterioDescripcion = view.findViewById(R.id.descripcionCriterio);

        recyclerPreguntas= view.findViewById(R.id.RecyclerVerPreguntas);
        adapterPreguntas = new AdapterPreguntas();
        adapterPreguntas.setContext(view.getContext());
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(view.getContext(),LinearLayoutManager.VERTICAL,false);
        recyclerPreguntas.setLayoutManager(linearLayoutManager);

        Realm realm = Realm.getDefaultInstance();
        RealmResults<Pregunta> lasPreguntas= realm.where(Pregunta.class)
                .equalTo("idCuestionario", idCuestionario)
                .equalTo("idEse", idEse)
                .equalTo("idItem", idItem)
                .findAll();

        RealmList<Pregunta>listaPreguntasRealm=new RealmList<>();
        listaPreguntasRealm.addAll(lasPreguntas);
        adapterPreguntas.setListaPreguntasOriginales(listaPreguntasRealm);
        recyclerPreguntas.setAdapter(adapterPreguntas);
        adapterPreguntas.notifyDataSetChanged();


        Item elItem = realm.where(Item.class)
                .equalTo("idCuestionario",idCuestionario)
                .equalTo("idItem",idItem)
                .findFirst();
        if (elItem!=null){
            criterioTitulo.setText(elItem.getTituloItem());
            criterioDescripcion.setText(elItem.getTextoItem());
        }

        return view;
    }

}







