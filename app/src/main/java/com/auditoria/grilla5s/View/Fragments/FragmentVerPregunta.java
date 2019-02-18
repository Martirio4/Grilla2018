package com.auditoria.grilla5s.View.Fragments;



import android.os.Bundle;
import android.support.annotation.NonNull;

import android.support.v4.app.Fragment;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.auditoria.grilla5s.DAO.ControllerDatos;
import com.auditoria.grilla5s.Model.Criterio;
import com.auditoria.grilla5s.Model.Cuestionario;
import com.auditoria.grilla5s.Model.Item;
import com.auditoria.grilla5s.Model.Pregunta;
import com.auditoria.grilla5s.R;
import com.auditoria.grilla5s.Utils.FuncionesPublicas;
import com.auditoria.grilla5s.View.Adapter.AdapterCriterios;
import com.auditoria.grilla5s.View.Adapter.AdapterPreguntas;

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
            case FuncionesPublicas.ESTRUCTURA_ESTRUCTURADA:
                RealmResults<Criterio> losCriterios1 = realm.where(Criterio.class)
                        .equalTo("idCuestionario", idCuestionario)
                        .equalTo("idEse", idEse)
                        .equalTo("idItem", idItem)
                        .equalTo("idPregunta",idpregunta )
                        .findAll();
                listaCriteriosRealm.addAll(losCriterios1);
                break;

            case FuncionesPublicas.ESTRUCTURA_SIMPLE:
                RealmResults<Criterio> losCriterios2 = realm.where(Criterio.class)
                        .equalTo("idCuestionario", idCuestionario)
                        .equalTo("idEse", idEse)
                        .equalTo("idPregunta",idpregunta )
                        .findAll();
                listaCriteriosRealm.addAll(losCriterios2);
                break;

            default:
                RealmResults<Criterio> losCriterios3 = realm.where(Criterio.class)
                        .equalTo("idCuestionario", idCuestionario)
                        .equalTo("idEse", idEse)
                        .equalTo("idPregunta",idpregunta )
                        .findAll();
                listaCriteriosRealm.addAll(losCriterios3);
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
        criterioTitulo.setText(controllerDatos.traerEses().get(Integer.parseInt(idEse)-1));
        switch (tipoEstructura){
            case FuncionesPublicas.ESTRUCTURA_SIMPLE:
                criterioDescripcion.setVisibility(View.GONE);
                final Pregunta mPregunta1=realm.where(Pregunta.class)
                        .equalTo("idCuestionario", idCuestionario)
                        .equalTo("idEse", idEse)
                        .equalTo("idPregunta", idpregunta)
                        .findFirst();
                if (mPregunta1!=null){
                    textoPregunta.setText(mPregunta1.getTextoPregunta());
                    textoPregunta.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            final MaterialDialog mDialog = new MaterialDialog.Builder(view.getContext())
                                    .title(view.getResources().getString(R.string.EditarPregunta))
                                    .contentColor(ContextCompat.getColor(view.getContext(), R.color.primary_text))
                                    .backgroundColor(ContextCompat.getColor(view.getContext(), R.color.tile1))
                                    .titleColor(ContextCompat.getColor(view.getContext(), R.color.tile4))
                                    .content(view.getResources().getString(R.string.favorEditePregunta))
                                    .input(view.getResources().getString(R.string.textoPregunta),mPregunta1.getTextoPregunta(),new MaterialDialog.InputCallback() {
                                        @Override
                                        public void onInput(@NonNull MaterialDialog dialog, final CharSequence input) {
                                            if (input!=null && !input.toString().isEmpty()) {

                                                FuncionesPublicas.cambiarTextoPregunta(mPregunta1,input.toString(),getContext());
                                                textoPregunta.setText(input.toString());
                                            }
                                            adapterCriterios.notifyDataSetChanged();
                                        }
                                    })

                                    .build();
                            EditText elEdit = mDialog.getInputEditText();
                            if (elEdit!=null) {
                                elEdit.setInputType(InputType.TYPE_CLASS_TEXT |
                                        InputType.TYPE_TEXT_FLAG_MULTI_LINE |
                                        InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
                            }
                            mDialog.show();
                        }
                    });
                }
                break;
                default:
                    criterioDescripcion.setVisibility(View.VISIBLE);
                    final Pregunta mPregunta2=realm.where(Pregunta.class)
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
                        textoPregunta.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                final MaterialDialog mDialog = new MaterialDialog.Builder(view.getContext())
                                        .title(view.getResources().getString(R.string.EditarPregunta))
                                        .contentColor(ContextCompat.getColor(view.getContext(), R.color.primary_text))
                                        .backgroundColor(ContextCompat.getColor(view.getContext(), R.color.tile1))
                                        .titleColor(ContextCompat.getColor(view.getContext(), R.color.tile4))
                                        .content(view.getResources().getString(R.string.favorEditePregunta))
                                        .input(view.getResources().getString(R.string.textoPregunta),mPregunta2.getTextoPregunta(),new MaterialDialog.InputCallback() {
                                            @Override
                                            public void onInput(@NonNull MaterialDialog dialog, final CharSequence input) {
                                                if (input!=null && !input.toString().isEmpty()) {

                                                    FuncionesPublicas.cambiarTextoPregunta(mPregunta2,input.toString(),getContext());
                                                    textoPregunta.setText(input.toString());
                                                }
                                                adapterCriterios.notifyDataSetChanged();
                                            }
                                        })

                                        .build();
                                EditText elEdit = mDialog.getInputEditText();
                                if (elEdit!=null) {
                                    elEdit.setInputType(InputType.TYPE_CLASS_TEXT |
                                            InputType.TYPE_TEXT_FLAG_MULTI_LINE |
                                            InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
                                }
                                mDialog.show();
                            }
                        });
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







