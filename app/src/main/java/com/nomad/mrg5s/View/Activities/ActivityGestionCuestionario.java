package com.nomad.mrg5s.View.Activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.nomad.mrg5s.DAO.ControllerDatos;
import com.nomad.mrg5s.Model.Cuestionario;
import com.nomad.mrg5s.R;
import com.nomad.mrg5s.Utils.FuncionesPublicas;
import com.nomad.mrg5s.View.Fragments.FragmentEditarCriteriosDefault;
import com.nomad.mrg5s.View.Fragments.FragmentGestionCuestionarios;

public class ActivityGestionCuestionario extends AppCompatActivity implements FragmentGestionCuestionarios.Notificable {

    public static final String ORIGEN="ORIGEN";
    private String elOrigen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_cuestionario);

        Intent elIntent=getIntent();
        Bundle elBundle=elIntent.getExtras();
        if (elBundle!=null){
            elOrigen=elBundle.getString(ORIGEN);
        }

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

        if (elOrigen!=null && elOrigen.equals(FuncionesPublicas.EDITAR_CRITERIO)){
            abrirFragmentEditorCriterios();
        }
        else {
            abrirFragmentEditorBase();
        }

    }

    private void abrirFragmentEditorCriterios() {
        FragmentEditarCriteriosDefault fragmentEditorBaseCuestionarios = new FragmentEditarCriteriosDefault();
        android.support.v4.app.FragmentManager fragmentManager= getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.contenedorEditor,fragmentEditorBaseCuestionarios,FuncionesPublicas.FRAGMENT_EDITOR_CRITERIOS);
        fragmentTransaction.commit();
    }

    private void abrirFragmentEditorBase() {
        FragmentGestionCuestionarios fragmentEditorBaseCuestionarios = new FragmentGestionCuestionarios();
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
        bundle.putString(ActivityPreAuditoria.IDCUESTIONARIO, cuestionario.getIdCuestionario());
        bundle.putString(ActivityPreAuditoria.ORIGEN, FuncionesPublicas.EDITAR_CUESTIONARIO);

        Intent intent = new Intent(ActivityGestionCuestionario.this, ActivityPreAuditoria.class);
        intent.putExtras(bundle);

        startActivity(intent);
    }

    @Override
    public void crearCuestionario(String nombre, String tipoCuestionario) {
        ControllerDatos controllerDatos=new ControllerDatos(this);
        controllerDatos.crearNuevoCuestionario(nombre, tipoCuestionario);
    }

    @Override
    public void eliminarCuestionario(String idCuestionario) {
        ControllerDatos controllerDatos=new ControllerDatos(this);
        controllerDatos.eliminarCuestionario(idCuestionario);
    }
}
