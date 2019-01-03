package com.auditoria.grilla5s.View.Fragments;


import android.content.Context;
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
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.auditoria.grilla5s.Model.Ese;
import com.auditoria.grilla5s.Model.Foto;
import com.auditoria.grilla5s.Model.Item;
import com.auditoria.grilla5s.Model.Pregunta;
import com.auditoria.grilla5s.R;
import com.auditoria.grilla5s.Utils.FuncionesPublicas;
import com.auditoria.grilla5s.View.Activities.ActivityPreAuditoria;
import com.auditoria.grilla5s.View.Adapter.AdapterItems;
import com.github.clans.fab.FloatingActionButton;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import io.realm.Sort;

import static com.auditoria.grilla5s.View.Activities.ActivityPreAuditoria.idAudit;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentPreAudit extends Fragment {

    public final static String LAESE="LAESE";
    private String laEse;

    public static final String TIPOCUESTIONARIO ="TIPOCUESTIONARIO" ;
    private String tipoCuestionario;

    public static final String ORIGEN ="ORIGEN" ;
    private String origen;

    public FragmentPreAudit() {
        // Required empty public constructor
    }
    private Auditable auditable;
    private AdapterItems adapterItems;

    public interface Auditable{
        void auditarItem(Item unItem);
        void titularToolbar();
        void cerrarAuditoria();
        void actualizarPuntaje(String idAudit);
        void agregarNuevoCriterio(String laEse, String tipoCuestionario, AdapterItems elAdapter);
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_fragment_pre_audit, container, false);
        Bundle bundle = getArguments();
        if (bundle!=null) {
            laEse=bundle.getString(LAESE);
            origen=bundle.getString(ORIGEN);
            tipoCuestionario=bundle.getString(TIPOCUESTIONARIO);
        }
        else {
            Toast.makeText(getContext(), getResources().getString(R.string.errorPruebeNuevamente), Toast.LENGTH_SHORT).show();
        }

        if (origen!=null && origen.equals(FuncionesPublicas.EDITAR_CUESTIONARIO)) {
            FloatingActionButton fabPreAudit = view.findViewById(R.id.fabGuardarAudit);
            fabPreAudit.setColorNormal(ContextCompat.getColor(getContext(),R.color.colorAccent));
            fabPreAudit.setImageResource(R.drawable.ic_nuevo_cuestionario_black_24dp);
            fabPreAudit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                   auditable.agregarNuevoCriterio(laEse,tipoCuestionario,adapterItems);

                }
            });
        }
        else {
            FloatingActionButton fabPreAudit = view.findViewById(R.id.fabGuardarAudit);
            fabPreAudit.setColorNormal(ContextCompat.getColor(getContext(),R.color.colorAccent));
            fabPreAudit.setImageResource(R.drawable.ic_save_black_24dp);
            fabPreAudit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    corroborarFinalizacionAuditoria(idAudit);
                }
            });
        }

        final RecyclerView recyclerPreAudit = view.findViewById(R.id.recyclerPreAudit);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
        recyclerPreAudit.setLayoutManager(linearLayoutManager);

//      LE PIDO A LA REALM TODOS LOS ITEM QUE TIENEN AUDITORIA Y QUE COMIENZAN CON LA MISMA ESE
        Realm realm = Realm.getDefaultInstance();
        RealmList<Item>listaItemsOriginales;

        if (origen.equals(FuncionesPublicas.EDITAR_CUESTIONARIO)) {

            RealmResults<Item> listaItems=realm.where(Item.class)
                    .equalTo("idCuestionario",tipoCuestionario)
                    .equalTo("idEse", Integer.parseInt(laEse))
                    .sort("idItem", Sort.ASCENDING)
                    .findAll();

            //si el item pertenece a la ese, loo agrego a la lista
            listaItemsOriginales = new RealmList<>();
            listaItemsOriginales.addAll(listaItems);


        } else {

            RealmResults<Item> listaItems=realm.where(Item.class)
                    .equalTo("idAudit",ActivityPreAuditoria.pedirIdAudit())
                    .equalTo("idEse", Integer.parseInt(laEse))
                    .findAll();

            //si el item pertenece a la ese, loo agrego a la lista
            listaItemsOriginales = new RealmList<>();
            listaItemsOriginales.addAll(listaItems);

        }

        adapterItems=new AdapterItems();
        adapterItems.setContext(getContext());
        adapterItems.setListaItemsOriginales(listaItemsOriginales);
        adapterItems.setOrigen(origen);
        recyclerPreAudit.setAdapter(adapterItems);
        adapterItems.notifyDataSetChanged();

        View.OnClickListener listenerItem = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer posicion = recyclerPreAudit.getChildAdapterPosition(v);
                RealmList<Item> listaAuditorias = adapterItems.getListaItemsOriginales();
                Item itemClickeado = listaAuditorias.get(posicion);
                auditable.auditarItem(itemClickeado);
            }
        };
        adapterItems.setListener(listenerItem);
        auditable.titularToolbar();
        return view;
    }

    public static FragmentPreAudit CrearfragmentPreAudit(String laEse, String origen) {
        FragmentPreAudit fragmentPreAudit = new FragmentPreAudit();
        Bundle unBundle = new Bundle();
        unBundle.putString(LAESE, laEse);
        unBundle.putString(ORIGEN,origen);
        fragmentPreAudit.setArguments(unBundle);

        return fragmentPreAudit;
    }
    public static FragmentPreAudit CrearfragmentPreAudit(String laEse, String origen,String idCuestionario) {
        FragmentPreAudit fragmentPreAudit = new FragmentPreAudit();
        Bundle unBundle = new Bundle();
        unBundle.putString(LAESE, laEse);
        unBundle.putString(ORIGEN, origen);
        unBundle.putString(TIPOCUESTIONARIO, idCuestionario);
        fragmentPreAudit.setArguments(unBundle);

        return fragmentPreAudit;
    }

    @Override
    public void onAttach(Context context) {
        auditable= (Auditable) context;
        super.onAttach(context);
    }

    @Override
    public void onResume() {
        adapterItems.notifyDataSetChanged();
        super.onResume();
    }

    private void corroborarFinalizacionAuditoria(final String idAudit) {

        if (FuncionesPublicas.completoTodosLosPuntos(idAudit)){
            auditable.cerrarAuditoria();
            auditable.actualizarPuntaje(idAudit);
            //auditable.cargarAuditoriaEnFirebase(idAudit);
        }
        else {

            new MaterialDialog.Builder(getContext())
                    .title(getString(R.string.advertencia))
                    .title(getResources().getString(R.string.advertencia))
                    .contentColor(ContextCompat.getColor(getContext(), R.color.primary_text))
                    .titleColor(ContextCompat.getColor(getContext(), R.color.tile4))
                    .backgroundColor(ContextCompat.getColor(getContext(), R.color.tile1))
                    .content(getResources().getString(R.string.auditoriaSinTerminar)+"\n"+getResources().getString(R.string.continuar))
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
