package com.auditoria.grilla5s.View.Activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.auditoria.grilla5s.Model.Area;
import com.auditoria.grilla5s.Model.Auditoria;
import com.auditoria.grilla5s.Model.Foto;
import com.auditoria.grilla5s.R;
import com.auditoria.grilla5s.Utils.FuncionesPublicas;
import com.auditoria.grilla5s.View.Adapter.AdapterArea;
import com.auditoria.grilla5s.View.Fragments.FragmentManageAreas;
import com.auditoria.grilla5s.View.Fragments.FragmentSettings;


import java.io.File;

import io.realm.Realm;
import io.realm.RealmResults;

public class SettingsActivity extends AppCompatActivity implements FragmentSettings.Notificable, AdapterArea.Eliminable, FragmentManageAreas.Avisable{

    private LinearLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        layout=findViewById(R.id.contenedor_landing);
        cargarFragmentSettings();

        Toolbar toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        Typeface robotoR = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Regular.ttf");
        TextView unText=toolbar.findViewById(R.id.textoToolbar);
        unText.setTypeface(robotoR);
        unText.setTextColor(getResources().getColor(R.color.blancoNomad));
        unText.setText(getResources().getText(R.string.settingsMin));

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        }


    }

    private void cargarFragmentSettings() {
        FragmentSettings fragmentManageAreas = new FragmentSettings();
        FragmentManager fragmentManager= getSupportFragmentManager();
        FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.contenedorSettings, fragmentManageAreas,FuncionesPublicas.FRAGMENTMANAGER_AREAS);
        fragmentTransaction.commit();
    }



    @Override
    public void EliminarArea(Area unArea) {

       crearDialogoBorrarArea(unArea);

    }





    //este metodo anda bien?
    public void borrarDefinitivamente(final Area unArea){

        Realm realm = Realm.getDefaultInstance();

        RealmResults<Auditoria> result2 = realm.where(Auditoria.class)

                .findAll();

        for (Auditoria audit:result2
                ) {
            if (audit.getAreaAuditada().getIdArea().equals(unArea.getIdArea())){

                FuncionesPublicas.borrarAuditoriaSeleccionada(audit.getIdAuditoria());

            }

        }

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {

                RealmResults<Area> lasAreas=realm.where(Area.class)
                        .equalTo("idArea",unArea.getIdArea())
                        .findAll();
                    for (Area elArea:lasAreas
                         ) {

                        //BORRO LAS FOTOS DE REALM
                        Foto laFoto= realm.where(Foto.class)
                                .equalTo("idFoto", elArea.getFotoArea().getIdFoto())
                                .findFirst();

                        File file = new File(elArea.getFotoArea().getRutaFoto());
                        file.delete();


                        if (laFoto!=null) {
                            laFoto.deleteFromRealm();
                        }


                    }
                lasAreas.deleteAllFromRealm();
            }

        });




        FragmentManager fragmentManager = this.getSupportFragmentManager();
        FragmentManageAreas fragmentManageAreas = (FragmentManageAreas) fragmentManager.findFragmentByTag(FuncionesPublicas.FRAGMENTMANAGER_AREAS);

        if (fragmentManageAreas != null && fragmentManageAreas.isVisible()) {
            fragmentManageAreas.updateAdapter();
            Snackbar.make(layout,getResources().getString(R.string.delteAreaOk), Snackbar.LENGTH_SHORT)
                    .show();

        }
    }
    
    public void crearDialogoBorrarArea(final Area unArea){
        new MaterialDialog.Builder(this)
                .title(getResources().getString(R.string.deleteTituloDialog))
                .contentColor(ContextCompat.getColor(this, R.color.primary_text))
                .titleColor(ContextCompat.getColor(this, R.color.tile4))
                .backgroundColor(ContextCompat.getColor(this, R.color.tile1))
                .content(getResources().getString(R.string.elArea) + unArea.getNombreArea() +"\n"+ getResources().getString(R.string.deletePermanente)+"\n"+getResources().getString(R.string.deseaContinuar))
                .positiveText(getResources().getString(R.string.delete))
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        borrarDefinitivamente(unArea);
                    }
                })
                .negativeText(getResources().getString(R.string.cancel))
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                    }
                })
                .show();
        
    }

    @Override
    public void salirDeAca() {
        Intent intent=new Intent(this, LandingActivity.class);
        startActivity(intent);
        this.finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    @Override
    public void abrirEditorDeCuestionarios() {
        Intent intent = new Intent(this,EditarCuestionarioActivity.class);
        startActivity(intent);
    }

    @Override
    public void abrirFragmentGestionAreas() {
        FragmentManageAreas fragmentManageAreas = new FragmentManageAreas();
        FragmentManager fragmentManager=getSupportFragmentManager();
        FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.contenedorSettings, fragmentManageAreas,FuncionesPublicas.FRAGMENTMANAGER_AREAS);
        fragmentTransaction.commit();
    }
}
