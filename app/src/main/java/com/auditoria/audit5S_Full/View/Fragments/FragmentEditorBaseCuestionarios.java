package com.auditoria.audit5S_Full.View.Fragments;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.auditoria.audit5S_Full.Model.Area;
import com.auditoria.audit5S_Full.Model.Cuestionario;
import com.auditoria.audit5S_Full.R;
import com.auditoria.audit5S_Full.View.Adapter.AdapterArea;
import com.auditoria.audit5S_Full.View.Adapter.AdapterCuestionario;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.firebase.database.DatabaseReference;

import java.io.File;

import io.realm.RealmList;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentEditorBaseCuestionarios extends Fragment {

    private RealmList<Cuestionario> listaCuestionarios;
    private RecyclerView recyclerCuestionarios;
    private AdapterCuestionario adapterCuestionario;
    private LinearLayoutManager layoutManager;

    private FragmentManageAreas.Notificable notificable;
    private FloatingActionMenu fabMenuManage;
    private FloatingActionButton fabAgregarArea;
    private FloatingActionButton fabSalir;


    private FragmentManageAreas.Avisable unAvisable;

    private TextView textView;
    private DatabaseReference mDatabase;

    private LinearLayout linearSnackbar;


    public FragmentEditorBaseCuestionarios() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_editor_base_cuestionarios, container, false);

        return view;
    }

}
