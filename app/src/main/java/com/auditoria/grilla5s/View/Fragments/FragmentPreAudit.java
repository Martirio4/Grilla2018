package com.auditoria.grilla5s.View.Fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.auditoria.grilla5s.Model.Item;
import com.auditoria.grilla5s.R;
import com.auditoria.grilla5s.View.Activities.ActivityPreAuditoria;
import com.auditoria.grilla5s.View.Adapter.AdapterItems;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

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
        super.onAttach(context);
        Auditable auditable= (Auditable) context;
    }
}
