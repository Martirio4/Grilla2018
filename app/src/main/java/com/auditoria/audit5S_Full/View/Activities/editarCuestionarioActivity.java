package com.auditoria.audit5S_Full.View.Activities;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.auditoria.audit5S_Full.R;
import com.auditoria.audit5S_Full.View.Fragments.FragmentEditorBaseCuestionarios;

public class editarCuestionarioActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_cuestionario);

        abrirFragmentEditorBase();

    }

    private void abrirFragmentEditorBase() {
        FragmentEditorBaseCuestionarios fragmentEditorBaseCuestionarios = new FragmentEditorBaseCuestionarios();
        android.support.v4.app.FragmentManager fragmentManager= getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.contenedorEditor,fragmentEditorBaseCuestionarios,"FragmentEditorBaseCuestionario");
        fragmentTransaction.commit();
    }
}
