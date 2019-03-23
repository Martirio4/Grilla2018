package com.nomad.mrg5s.View.Fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.nomad.mrg5s.Model.Cuestionario;
import com.nomad.mrg5s.Model.Item;
import com.nomad.mrg5s.Model.Pregunta;
import com.nomad.mrg5s.R;
import com.nomad.mrg5s.Utils.FuncionesPublicas;
import com.nomad.mrg5s.View.Activities.ActivityPreAuditoria;
import com.nomad.mrg5s.View.Adapter.AdapterItems;
import com.nomad.mrg5s.View.Adapter.AdapterPreguntas;
import com.github.clans.fab.FloatingActionButton;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

import static com.nomad.mrg5s.View.Activities.ActivityPreAuditoria.idAudit;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentPreAudit extends Fragment {

    public final static String LAESE = "LAESE";
    private static final String IDESTRUCTURA = "IDESTRUCTURA";
    private String laEse;

    public static final String IDCUESTIONARIO = "IDCUESTIONARIO";
    private String idCuestionario;

    public static final String ORIGEN = "ORIGEN";
    private String origen;
    private RecyclerView recyclerPreAudit;

    public FragmentPreAudit() {
        // Required empty public constructor
    }

    private Auditable auditable;
    private AdapterItems adapterItems;
    private AdapterPreguntas adapterPreguntas;
    private TextView textoFab;
    private String estructuraCuestionario;

    public interface Auditable {
        void auditar(Item unItem);
        void auditar(Pregunta unaPregunta);
        void cerrarAuditoria();
        void actualizarPuntaje(String idAudit);
        void agregarNuevoItem(String laEse, String idCuestionario, AdapterItems elAdapter);
        void agregarNuevaPregunta(String laEse, String idCuestionario, AdapterPreguntas adapterPreguntas);
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pre_audit, container, false);
        Bundle bundle = getArguments();
        if (bundle != null) {
            laEse = bundle.getString(LAESE);
            origen = bundle.getString(ORIGEN);
            idCuestionario = bundle.getString(IDCUESTIONARIO);

            Realm realm= Realm.getDefaultInstance();
            Cuestionario elCuestionario = realm.where(Cuestionario.class)
                .equalTo("idCuestionario",idCuestionario)
                .findFirst();
            if (elCuestionario !=null){
                estructuraCuestionario = elCuestionario.getTipoCuestionario();
            }
            else{
                estructuraCuestionario=bundle.getString(IDESTRUCTURA);
            }

         }
        else {
            Toast.makeText(getContext(), getResources().getString(R.string.errorPruebeNuevamente), Toast.LENGTH_SHORT).show();
        }

        textoFab=view.findViewById(R.id.textoFabPreAudit);

        if (origen != null && origen.equals(FuncionesPublicas.EDITAR_CUESTIONARIO)) {
            FloatingActionButton fabPreAudit = view.findViewById(R.id.fabGuardarAudit);
            fabPreAudit.setColorNormal(ContextCompat.getColor(getContext(), R.color.colorAccent));
            fabPreAudit.setImageResource(R.drawable.ic_nuevo_cuestionario_black_24dp);

            //SI LA AUDITORIA ES ESTRUCTURADA EL FAB AGREGA ITEM, SINO AGREGA PREGUNTAS.
            switch (estructuraCuestionario){
                case FuncionesPublicas.ESTRUCTURA_ESTRUCTURADA:
                    fabPreAudit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            auditable.agregarNuevoItem(laEse, idCuestionario, adapterItems);
                        }
                    });
                    textoFab.setText(getString(R.string.nuevoItem));
                    break;

                case FuncionesPublicas.ESTRUCTURA_SIMPLE:
                    fabPreAudit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            auditable.agregarNuevaPregunta(laEse,idCuestionario,adapterPreguntas);
                        }
                    });
                    textoFab.setText(getString(R.string.nuevaPregunta));
                    break;
            }
        }
        else {
            FloatingActionButton fabPreAudit = view.findViewById(R.id.fabGuardarAudit);
            fabPreAudit.setColorNormal(ContextCompat.getColor(getContext(), R.color.colorAccent));
            fabPreAudit.setImageResource(R.drawable.ic_save_black_24dp);
            fabPreAudit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    corroborarFinalizacionAuditoria(idAudit);
                }
            });
            textoFab.setText(getString(R.string.guardarAuditoria));
        }

        recyclerPreAudit = view.findViewById(R.id.recyclerPreAudit);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerPreAudit.setLayoutManager(linearLayoutManager);

//      INSTANCIO EL ADAPTER SEGUN CORRESPONDA Y LE CARGO SU SET DE DATOS Y SU LISTENER
        instanciarAdapter_CargarDatos_CargarListener();
        return view;
    }

    private void instanciarAdapter_CargarDatos_CargarListener() {
        switch (estructuraCuestionario){
            case FuncionesPublicas.ESTRUCTURA_ESTRUCTURADA:
                popularRecyclerItems();
                break;
            case FuncionesPublicas.ESTRUCTURA_SIMPLE:
                popularRecyclerPreguntas();
                break;
        }
    }

    private void popularRecyclerItems() {
        Realm realm = Realm.getDefaultInstance();
        RealmList<Item> listaItemsOriginales = new RealmList<>();

        adapterItems = new AdapterItems(getContext());
        adapterItems.setOrigen(origen);
        recyclerPreAudit.setAdapter(adapterItems);


        switch (origen){
            case FuncionesPublicas.EDITAR_CUESTIONARIO:
                RealmResults<Item> listaItems1 = realm.where(Item.class)
                        .equalTo("idCuestionario", idCuestionario)
                        .equalTo("idEse", laEse)
                        .findAll();
                listaItemsOriginales.addAll(listaItems1);
                break;
            default:
                RealmResults<Item> listaItems2 = realm.where(Item.class)
                        .equalTo("idAudit", ActivityPreAuditoria.pedirIdAudit())
                        .equalTo("idEse", laEse)
                        .findAll();
                listaItemsOriginales.addAll(listaItems2);

                break;
        }
        View.OnClickListener listenerItem;
        listenerItem = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer posicion = recyclerPreAudit.getChildAdapterPosition(v);
                RealmList<Item> listaAuditorias = adapterItems.getListaItemsOriginales();
                Item itemClickeado = listaAuditorias.get(posicion);
                auditable.auditar(itemClickeado);
            }
        };

        adapterItems.setListener(listenerItem);
        adapterItems.setListaItemsOriginales(listaItemsOriginales);
        adapterItems.notifyDataSetChanged();

    }
    private void popularRecyclerPreguntas() {
        Realm realm = Realm.getDefaultInstance();
        RealmList<Pregunta> listaPreguntasOriginales = new RealmList<>();

         //si el item pertenece a la ese, loo agrego a la lista
        adapterPreguntas = new AdapterPreguntas(getContext());
        adapterPreguntas.setOrigen(origen);
        recyclerPreAudit.setAdapter(adapterPreguntas);

        switch (origen){
            case FuncionesPublicas.EDITAR_CUESTIONARIO:
                RealmResults<Pregunta> listaPreguntas1 = realm.where(Pregunta.class)
                        .equalTo("idCuestionario", idCuestionario)
                        .equalTo("idEse", laEse)
                        .findAll();
                listaPreguntasOriginales.addAll(listaPreguntas1);
            break;
            default:
                RealmResults<Pregunta> listaPreguntas2 = realm.where(Pregunta.class)
                        .equalTo("idAudit", ActivityPreAuditoria.pedirIdAudit())
                        .equalTo("idEse", laEse)
                        .findAll();
                listaPreguntasOriginales.addAll(listaPreguntas2);
            break;
        }


        View.OnClickListener listenerPregunta;

        listenerPregunta = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer posicion = recyclerPreAudit.getChildAdapterPosition(v);
                RealmList<Pregunta> listaPreguntas = adapterPreguntas.getListaPreguntasOriginales();
                Pregunta preguntaClickeada = listaPreguntas.get(posicion);
                auditable.auditar(preguntaClickeada);
            }
        };
        adapterPreguntas.setListener(listenerPregunta);
        adapterPreguntas.setListaPreguntasOriginales(listaPreguntasOriginales);
        adapterPreguntas.notifyDataSetChanged();
    }

    public static FragmentPreAudit CrearfragmentPreAudit(String laEse, String origen, String idCuestionario) {
        FragmentPreAudit fragmentPreAudit = new FragmentPreAudit();
        Bundle unBundle = new Bundle();
        unBundle.putString(LAESE, laEse);
        unBundle.putString(ORIGEN, origen);
        unBundle.putString(IDCUESTIONARIO, idCuestionario);
        fragmentPreAudit.setArguments(unBundle);

        return fragmentPreAudit;
    }
    public static FragmentPreAudit CrearfragmentPreAudit(String laEse, String origen, String idCuestionario,String idEstructura) {
        FragmentPreAudit fragmentPreAudit = new FragmentPreAudit();
        Bundle unBundle = new Bundle();
        unBundle.putString(LAESE, laEse);
        unBundle.putString(ORIGEN, origen);
        unBundle.putString(IDCUESTIONARIO, idCuestionario);
        unBundle.putString(IDESTRUCTURA,idEstructura);
        fragmentPreAudit.setArguments(unBundle);

        return fragmentPreAudit;
    }

    @Override
    public void onAttach(Context context) {
        auditable = (Auditable) context;
        super.onAttach(context);
    }

    @Override
    public void onResume() {
        switch (estructuraCuestionario){
            case FuncionesPublicas.ESTRUCTURA_ESTRUCTURADA:
                popularRecyclerItems();
                break;
            case FuncionesPublicas.ESTRUCTURA_SIMPLE:
                popularRecyclerPreguntas();
                break;
            default:
                break;
        }
        super.onResume();
    }

    private void corroborarFinalizacionAuditoria(final String idAudit) {

        if (FuncionesPublicas.completoTodosLosPuntos(idAudit)) {
            auditable.cerrarAuditoria();
            auditable.actualizarPuntaje(idAudit);
            //auditable.cargarAuditoriaEnFirebase(idAudit);
        } else {

            new MaterialDialog.Builder(getContext())
                    .title(getString(R.string.advertencia))
                    .title(getResources().getString(R.string.advertencia))
                    .contentColor(ContextCompat.getColor(getContext(), R.color.primary_text))
                    .titleColor(ContextCompat.getColor(getContext(), R.color.tile4))
                    .backgroundColor(ContextCompat.getColor(getContext(), R.color.tile1))
                    .content(getResources().getString(R.string.auditoriaSinTerminar) + "\n" + getResources().getString(R.string.continuar))
                    .positiveText(getResources().getString(R.string.si))
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            auditable.cerrarAuditoria();
                        }
                    })
                    .negativeText(getResources().getString(R.string.cancel))
                    .onNegative(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        }
                    })
                    .show();

        }

    }


}
