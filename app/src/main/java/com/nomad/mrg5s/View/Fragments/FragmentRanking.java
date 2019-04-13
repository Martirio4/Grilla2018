package com.nomad.mrg5s.View.Fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.nomad.mrg5s.Model.Auditoria;
import com.nomad.mrg5s.R;
import com.nomad.mrg5s.Utils.FuncionesPublicas;
import com.nomad.mrg5s.View.Adapter.AdapterAuditorias;

import io.realm.RealmList;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentRanking extends Fragment {

    private RealmList<Auditoria> listaAuditorias;
    private RecyclerView recyclerAreas;
    private AdapterAuditorias adapterAudits;
    private Graficable graficable;

    public void updateAdapter() {
        listaAuditorias=new RealmList<>();
        adapterAudits.setListaAuditsOriginales(new RealmList<Auditoria>());
        listaAuditorias.addAll(FuncionesPublicas.traerAuditoriasOrdenadas(getContext()));
        adapterAudits.setListaAuditsOriginales(listaAuditorias);
        adapterAudits.notifyDataSetChanged();

    }

    public interface Graficable{
        void GraficarAuditVieja(Auditoria unAuditoria);
    }
    public FragmentRanking() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_my_audits, container, false);

        listaAuditorias=new RealmList<>();
        listaAuditorias.addAll(FuncionesPublicas.traerAuditoriasOrdenadas(getContext()));
        recyclerAreas= view.findViewById(R.id.recyclerArea);
        adapterAudits= new AdapterAuditorias();
        adapterAudits.setContext(getContext());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerAreas.setLayoutManager(layoutManager);
        adapterAudits.setListaAuditsOriginales(listaAuditorias);
        recyclerAreas.setAdapter(adapterAudits);

        View.OnClickListener listenerArea = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer posicion = recyclerAreas.getChildAdapterPosition(v);
                RealmList<Auditoria> listaAuditorias = adapterAudits.getListaAuditsOriginales();
                Auditoria auditClickeada = listaAuditorias.get(posicion);
                graficable.GraficarAuditVieja(auditClickeada);
            }
        };
        adapterAudits.setListener(listenerArea);

        /*
        View.OnClickListener listenerArea = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer posicion = recyclerAreas.getChildAdapterPosition(v);
                RealmList<Area> listaAreas = adapterArea.getListaAreasOriginales();
                Area areaClickeada = listaAreas.get(posicion);
               // notificable.comenzarAuditoria(areaClickeada);
            }
        };
        adapterArea.setListener(listenerArea);
*/
        return view;
    }


    public static FragmentRanking crearFragmentRanking() {
        return new FragmentRanking();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.graficable=(Graficable)context;
    }

    @Override
    public void onResume() {
        adapterAudits.notifyDataSetChanged();
        super.onResume();
    }
}
