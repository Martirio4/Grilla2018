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

import com.auditoria.grilla5s.DAO.ControllerDatos;
import com.auditoria.grilla5s.Model.Criterio;
import com.auditoria.grilla5s.Model.Cuestionario;
import com.auditoria.grilla5s.Model.Item;
import com.auditoria.grilla5s.Model.Pregunta;
import com.auditoria.grilla5s.R;
import com.auditoria.grilla5s.Utils.FuncionesPublicas;
import com.auditoria.grilla5s.View.Adapter.AdapterCriterios;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentVerPregunta extends Fragment {


    public static final String IDCUESTIONARIO = "IDCUESTIONARIO";
    public static final String IDITEM = "IDITEM";
    public static final String IDESE = "IDESE";
    private static final String IDPREGUNTA = "IDPREGUNTA";


    private String idItem;
    private String idCuestionario;
    private String idEse;
    private String tipoEstructura;
    private String idpregunta;
    private TextView textoPregunta;
    private TextView criterioTitulo;
    private TextView criterioDescripcion;
    private ControllerDatos controllerDatos;


    private RecyclerView recyclerPreguntas;
    private AdapterCriterios adapterCriterios;

    public FragmentVerPregunta() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_editar_preguntas, container, false);
        controllerDatos=new ControllerDatos(getContext());
        Bundle bundle = getArguments();
        if (bundle == null) {
            Toast.makeText(getContext(), getResources().getString(R.string.noEncontroDatos), Toast.LENGTH_SHORT).show();
        } else {

            idItem = bundle.getString(IDITEM);
            idCuestionario = bundle.getString(IDCUESTIONARIO);
            idEse = bundle.getString(IDESE);
            idpregunta=bundle.getString(IDPREGUNTA);
        }
        traerTipoEstructura();

         textoPregunta = view.findViewById(R.id.textoPregunta);
         criterioTitulo = view.findViewById(R.id.tituloCriterio);
         criterioDescripcion = view.findViewById(R.id.descripcionCriterio);

        recyclerPreguntas= view.findViewById(R.id.RecyclerVerPreguntas);
        adapterCriterios = new AdapterCriterios();
        adapterCriterios.setContext(view.getContext());
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(view.getContext(),LinearLayoutManager.VERTICAL,false);
        recyclerPreguntas.setLayoutManager(linearLayoutManager);

        Realm realm = Realm.getDefaultInstance();
        RealmList<Criterio> listaCriteriosRealm =new RealmList<>();
        switch (tipoEstructura){

            case FuncionesPublicas.ESTRUCTURA_SIMPLE:
                RealmResults<Criterio> losCriterios1 = realm.where(Criterio.class)
                        .equalTo("idCuestionario", idCuestionario)
                        .equalTo("idEse", idEse)
                        .equalTo("idItem", idItem)
                        .equalTo("idPregunta",idpregunta )
                        .findAll();
                listaCriteriosRealm.addAll(losCriterios1);

                break;

            default:
                RealmResults<Criterio> losCriterios2 = realm.where(Criterio.class)
                        .equalTo("idCuestionario", idCuestionario)
                        .equalTo("idEse", idEse)
                        .equalTo("idPregunta",idpregunta )
                        .findAll();
                listaCriteriosRealm.addAll(losCriterios2);
                break;

        }

        adapterCriterios.setListaCriteriosOriginales(listaCriteriosRealm);
        recyclerPreguntas.setAdapter(adapterCriterios);
        adapterCriterios.notifyDataSetChanged();
        cargarTitulosFragment();

        return view;
    }

    private void cargarTitulosFragment() {
        Realm realm= Realm.getDefaultInstance();
        criterioTitulo.setText(controllerDatos.traerEses().get(Integer.parseInt(idEse)+1));
        switch (tipoEstructura){
            case FuncionesPublicas.ESTRUCTURA_SIMPLE:
                criterioDescripcion.setVisibility(View.GONE);
                Pregunta mPregunta1=realm.where(Pregunta.class)
                        .equalTo("idCuestionario", idCuestionario)
                        .equalTo("idEse", idEse)
                        .equalTo("idPregunta", idpregunta)
                        .findFirst();
                if (mPregunta1!=null){
                    textoPregunta.setText(mPregunta1.getTextoPregunta());
                }
                break;
                default:
                    criterioDescripcion.setVisibility(View.VISIBLE);
                    Pregunta mPregunta2=realm.where(Pregunta.class)
                            .equalTo("idCuestionario", idCuestionario)
                            .equalTo("idEse", idEse)
                            .equalTo("idItem", idItem)
                            .equalTo("idPregunta", idpregunta)
                            .findFirst();

                    Item mItem1=realm.where(Item.class)
                            .equalTo("idCuestionario", idCuestionario)
                            .equalTo("idEse", idEse)
                            .equalTo("idItem", idItem)
                            .findFirst();

                    if (mPregunta2!=null && mItem1!=null ){
                        criterioDescripcion.setText(mItem1.getTituloItem());
                        textoPregunta.setText(mPregunta2.getTextoPregunta());
                    }
                break;
        }
    }



    private void traerTipoEstructura() {
        Realm realm= Realm.getDefaultInstance();
        Cuestionario elCues=realm.where(Cuestionario.class)
                .equalTo("idCuestionario", idCuestionario)
                .findFirst();
        if (elCues!=null){
            tipoEstructura=elCues.getTipoCuestionario();
        }
    }

    public static Fragment CrearfragmentVerPregunta(Pregunta unaPreg) {
        FragmentVerPregunta fragmentVerPregunta=new FragmentVerPregunta();
        Bundle bundle=new Bundle();
        bundle.putString(FragmentVerPregunta.IDCUESTIONARIO,unaPreg.getIdCuestionario());
        bundle.putString(FragmentVerPregunta.IDITEM,unaPreg.getIdItem());
        bundle.putString(FragmentVerPregunta.IDESE,unaPreg.getIdEse());
        bundle.putString(FragmentVerPregunta.IDPREGUNTA,unaPreg.getIdPregunta());
        fragmentVerPregunta.setArguments(bundle);
        return fragmentVerPregunta;
    }


}







