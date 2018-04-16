package com.auditoria.grilla5s.View.Fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.auditoria.grilla5s.Model.Area;
import com.auditoria.grilla5s.Model.Auditoria;
import com.auditoria.grilla5s.R;
import com.auditoria.grilla5s.View.Adapter.AdapterArea;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentRankingAreas extends Fragment {

    private RealmList<Area> listaAreas;
    private RecyclerView recyclerAreas;
    private AdapterArea adapterAreas;
    private LinearLayoutManager layoutManager;

    private Graficable graficable;

    public void updateAdapter() {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Area> result2 = realm.where(Area.class)
                .findAll();
        listaAreas =new RealmList<>();
        adapterAreas.setListaAreasOriginales(new RealmList<Area>());
        listaAreas.addAll(result2);
        adapterAreas.setListaAreasOriginales(listaAreas);
        adapterAreas.notifyDataSetChanged();

    }

    public interface Graficable{
        public void GraficarAuditVieja(Auditoria unAuditoria);
        public void graficarArea(Area unArea, String elOrigen);

    }
    public FragmentRankingAreas() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_my_audits, container, false);
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Area> result2=realm.where(Area.class)
                .findAll();

        listaAreas =new RealmList<>();
        listaAreas.addAll(result2);
        recyclerAreas= view.findViewById(R.id.recyclerArea);
        adapterAreas = new AdapterArea();
        adapterAreas.setContext(getContext());
        layoutManager= new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL,false);
        recyclerAreas.setLayoutManager(layoutManager);
        adapterAreas.setListaAreasOriginales(listaAreas);
        recyclerAreas.setAdapter(adapterAreas);

        View.OnClickListener listenerArea = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer posicion = recyclerAreas.getChildAdapterPosition(v);
                RealmList<Area> listaAreas = adapterAreas.getListaAreasOriginales();
                Area areaClickeada = listaAreas.get(posicion);
                graficable.graficarArea(areaClickeada,"ranking");
            }
        };
        adapterAreas.setListener(listenerArea);

        /*
        View.OnClickListener listenerArea = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer posicion = recyclerAreas.getChildAdapterPosition(v);
                RealmList<Area> listaAreas = adapterArea.getListaAreasOriginales();
                Area areaClickeada = listaAreas.get(posicion);
               // notificable.comenzarArea(areaClickeada);
            }
        };
        adapterArea.setListener(listenerArea);
*/
        return view;
    }


    public static FragmentRankingAreas crearFragmentRankingAreas() {
        return new FragmentRankingAreas();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.graficable=(Graficable)context;
    }

    @Override
    public void onResume() {
        adapterAreas.notifyDataSetChanged();
        super.onResume();
    }
}
