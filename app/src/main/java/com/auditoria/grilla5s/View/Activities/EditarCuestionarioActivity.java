package com.auditoria.grilla5s.View.Activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.auditoria.grilla5s.Model.Cuestionario;
import com.auditoria.grilla5s.R;
import com.auditoria.grilla5s.Utils.FuncionesPublicas;
import com.auditoria.grilla5s.View.Adapter.AdapterCuestionario;
import com.auditoria.grilla5s.View.Fragments.FragmentEditorCuestionarios;
import com.auditoria.grilla5s.View.Fragments.FragmentPreAudit;

public class EditarCuestionarioActivity extends AppCompatActivity implements FragmentEditorCuestionarios.Notificable {

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
        unText.setText(getString(R.string.settingsMin));

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        abrirFragmentEditorBase();

    }

    private void abrirFragmentEditorBase() {
        FragmentEditorCuestionarios fragmentEditorBaseCuestionarios = new FragmentEditorCuestionarios();
        android.support.v4.app.FragmentManager fragmentManager= getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.contenedorEditor,fragmentEditorBaseCuestionarios,FuncionesPublicas.FRAGMENT_EDITOR_CUESTIONARIOS);
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    @Override
    public void abrirCuestionario(Cuestionario cuestionario) {
        //USO EL FRAGMENT PRE AUDIT PARA VER LOS ITEMS Y MODIFICAR
        Bundle bundle = new Bundle();
        bundle.putString(ActivityPreAuditoria.TIPOCUESTIONARIO , cuestionario.getIdCuestionario());
        bundle.putString(ActivityPreAuditoria.ORIGEN, FuncionesPublicas.EDITAR_CUESTIONARIO);

        Intent intent = new Intent(EditarCuestionarioActivity.this, ActivityPreAuditoria.class);
        intent.putExtras(bundle);

        startActivity(intent);
    }
}
