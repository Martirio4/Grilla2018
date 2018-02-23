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
import com.auditoria.grilla5s.View.Adapter.AdapterAuditorias;
import com.google.firebase.auth.FirebaseAuth;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentRanking extends Fragment {

    private RealmList<Auditoria> listaAuditorias;
    private RecyclerView recyclerAreas;
    private AdapterAuditorias adapterAudits;
    private LinearLayoutManager layoutManager;
    private RealmList<Area> listaAreas;
    private Graficable graficable;

    public interface Graficable{
        public void GraficarAuditVieja(Auditoria unAuditoria);
    }
    public FragmentRanking() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_my_audits, container, false);
        String usuario= FirebaseAuth.getInstance().getCurrentUser().getEmail();
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Auditoria> result2=realm.where(Auditoria.class)
                .equalTo("usuario",usuario)
                .findAll();
         result2=result2.sort("puntajeFinal", Sort.DESCENDING);




        listaAuditorias=new RealmList<>();
        listaAuditorias.addAll(result2);
        recyclerAreas= (RecyclerView)view.findViewById(R.id.recyclerArea);
        adapterAudits= new AdapterAuditorias();
        adapterAudits.setContext(getContext());
        layoutManager= new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL,false);
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

}
