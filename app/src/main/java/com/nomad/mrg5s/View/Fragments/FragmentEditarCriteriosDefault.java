package com.nomad.mrg5s.View.Fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.clans.fab.FloatingActionButton;
import com.nomad.mrg5s.DAO.ControllerDatos;
import com.nomad.mrg5s.Model.Criterio;
import com.nomad.mrg5s.R;
import com.nomad.mrg5s.View.Adapter.AdapterCriterios;

import io.realm.RealmList;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentEditarCriteriosDefault extends Fragment {


    public FragmentEditarCriteriosDefault() {
        // Required empty public constructor
    }

    private RecyclerView recyclerCriterios;
    private AdapterCriterios adapterCriterios;
    private FloatingActionButton fabVolver;
    private Cerrable cerrable;
    public interface Cerrable{
        void cerrarEsto();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_editar_crit_default, container, false);

        recyclerCriterios = view.findViewById(R.id.recyclercritDefault);
        fabVolver=view.findViewById(R.id.fabCerrarEditCriterios);
        adapterCriterios = new AdapterCriterios();
        adapterCriterios.setContext(getContext());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
        recyclerCriterios.setLayoutManager(linearLayoutManager);
        recyclerCriterios.setAdapter(adapterCriterios);
        adapterCriterios.setListaCriteriosOriginales(dameCriteriosDefault());

        fabVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cerrable.cerrarEsto();
            }
        });
        fabVolver.setVisibility(View.GONE);




        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.cerrable= (Cerrable)context;
    }

    private RealmList<Criterio> dameCriteriosDefault() {
        ControllerDatos controllerDatos = new ControllerDatos(getContext());
        return controllerDatos.dameCriteriosDefault();
    }

}
