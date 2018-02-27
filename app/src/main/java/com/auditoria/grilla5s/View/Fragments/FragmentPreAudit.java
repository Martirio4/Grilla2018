package com.auditoria.grilla5s.View.Fragments;


import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.auditoria.grilla5s.Model.Auditoria;
import com.auditoria.grilla5s.Model.Item;
import com.auditoria.grilla5s.Model.Pregunta;
import com.auditoria.grilla5s.R;
import com.auditoria.grilla5s.View.Activities.ActivityPreAuditoria;
import com.auditoria.grilla5s.View.Adapter.AdapterItems;
import com.github.clans.fab.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

import static com.auditoria.grilla5s.View.Activities.ActivityPreAuditoria.idAudit;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentPreAudit extends Fragment {


    public FragmentPreAudit() {
        // Required empty public constructor
    }
    private Auditable auditable;
    private AdapterItems adapterItems;

    public interface Auditable{
        void auditarItem(Item unItem);
        void titularToolbar();
        public void cerrarAuditoria();
    }



    public final static String LAESE="LAESE";
    private String laEse;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_fragment_pre_audit, container, false);
        Bundle bundle = getArguments();
        if (bundle!=null) {
            laEse=bundle.getString(LAESE);
        }
        else {
            Toast.makeText(getContext(), getResources().getString(R.string.errorPruebeNuevamente), Toast.LENGTH_SHORT).show();
        }

        FloatingActionButton fabGuardar = view.findViewById(R.id.fabGuardarAudit);
        fabGuardar.setColorNormal(ContextCompat.getColor(getContext(),R.color.colorAccent));
        fabGuardar.setImageResource(R.drawable.ic_save_black_24dp);
        fabGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                completoTodosLosPuntos();
            }
        });



        final RecyclerView recyclerPreAudit = view.findViewById(R.id.recyclerPreAudit);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
        recyclerPreAudit.setLayoutManager(linearLayoutManager);

//        LE PIDO A LA REALM TODOS LOS ITEM QUE TIENEN AUDITORIA Y QUE COMIENZAN CON LA MISMA ESE
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Item> listaItems=realm.where(Item.class)
                        .equalTo("idAudit",ActivityPreAuditoria.pedirIdAudit())
                        .beginsWith("idItem",laEse)
                        .findAll();

        RealmList<Item>listaItemsOriginales = new RealmList<>();
        listaItemsOriginales.addAll(listaItems);

        adapterItems=new AdapterItems();
        adapterItems.setContext(getContext());
        adapterItems.setListaItemsOriginales(listaItemsOriginales);
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

    public static FragmentPreAudit CrearfragmentPreAudit(String laEse) {
        FragmentPreAudit fragmentPreAudit = new FragmentPreAudit();
        Bundle unBundle = new Bundle();

        unBundle.putString(LAESE, laEse);
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

    private void completoTodosLosPuntos() {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Pregunta> result2 = realm.where(Pregunta.class)
                .equalTo("idAudit", idAudit)
                .findAll();
        List<String> unaLista=new ArrayList<>();

        for (Pregunta unaPreg :result2
                ) {
            if (unaPreg.getPuntaje()==null|| unaPreg.getPuntaje()==0){
                unaLista.add(unaPreg.getIdPregunta());
            }
        }

        if (unaLista.size()>0){
            new MaterialDialog.Builder(getContext())
                    .title("Warning!")
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
        //SI TODOS LOS PUNTOS ESTA COMPLERTOS
        else{
            //ME FIJO SI LA AUDITORIA ESTABA CERRADA
            Auditoria mAudit=realm.where(Auditoria.class)
                    .equalTo("idAuditoria",idAudit)
                    .findFirst();
            //SI NO ESTA CERRADA, LA CIERRO
            if (!mAudit.getAuditEstaCerrada()){
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        //BUSCO LA AUDITORIA ACTUAL
                        Auditoria estaAudit = realm.where(Auditoria.class)
                                .equalTo("idAuditoria", idAudit)
                                .findFirst();
                        //BUSCO TODAS LAS AUDITS QUE SON ULTIMAS
                        RealmResults<Auditoria> todasAudits =realm.where(Auditoria.class)
                                .equalTo("esUltimaAuditoria",true)
                                .findAll();
                        //ENTRE TODAS LAS ULTIMAS AUDITORIAS BUSCO LA QUE TIENE LA MISMA AREA QUE LA ACTUAL
                        for (Auditoria unAudit :
                                todasAudits) {
                            if (unAudit.getAreaAuditada().getIdArea().equals(estaAudit.getAreaAuditada().getIdArea())){
                                unAudit.setEsUltimaAuditoria(false);
                            }
                        }
                        //SETEO LA AUDITORIA ACTUAL COMO CERRADA Y ULTIMA AUDITORIA
                        estaAudit.setEsUltimaAuditoria(true);
                        estaAudit.setAuditEstaCerrada(true);
                    }
                });
            }
            //CIERRO LA AUDITORIA ACTUAL
            auditable.cerrarAuditoria();
        }
    }

}
