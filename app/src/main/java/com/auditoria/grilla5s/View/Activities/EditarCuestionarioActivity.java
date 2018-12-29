package com.auditoria.grilla5s.View.Activities;

import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.auditoria.grilla5s.Model.Cuestionario;
import com.auditoria.grilla5s.R;
import com.auditoria.grilla5s.View.Adapter.AdapterCuestionario;
import com.auditoria.grilla5s.View.Fragments.FragmentEditorCuestionarios;

public class EditarCuestionarioActivity extends AppCompatActivity  {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_cuestionario);

        //SETEAR TOOLBAR
        Toolbar toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        Typeface robotoR = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Regular.ttf");
        TextView unText=toolbar.findViewById(R.id.textoToolbar);
        unText.setTypeface(robotoR);
        unText.setTextColor(getResources().getColor(R.color.blancoNomad));
        unText.setText(getString(R.string.settings));

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        abrirFragmentEditorBase();

    }

    private void abrirFragmentEditorBase() {
        FragmentEditorCuestionarios fragmentEditorBaseCuestionarios = new FragmentEditorCuestionarios();
        android.support.v4.app.FragmentManager fragmentManager= getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.contenedorEditor,fragmentEditorBaseCuestionarios,"FragmentEditorBaseCuestionario");
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }


}
