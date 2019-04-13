package com.nomad.mrg5s.View.Activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.nomad.mrg5s.Model.Area;
import com.nomad.mrg5s.Model.Auditoria;
import com.nomad.mrg5s.Model.Cuestionario;
import com.nomad.mrg5s.Model.Foto;
import com.nomad.mrg5s.R;
import com.nomad.mrg5s.Utils.FuncionesPublicas;
import com.nomad.mrg5s.View.Adapter.AdapterArea;
import com.nomad.mrg5s.View.Fragments.FragmentManageAreas;
import com.nomad.mrg5s.View.Fragments.FragmentSeleccionArea;
import com.nomad.mrg5s.View.Fragments.FragmentSettings;


import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmResults;

import static com.nomad.mrg5s.Utils.FuncionesPublicas.MANAGE_AREAS;
import static com.nomad.mrg5s.Utils.FuncionesPublicas.SELECCION_AREAS;

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
        fragmentTransaction.replace(R.id.contenedorSettings, fragmentManageAreas,FuncionesPublicas.FRAGMENT_SETTINGS);
        fragmentTransaction.commit();
    }



    @Override
    public void EliminarArea(Area unArea) {

       crearDialogoBorrarArea(unArea);

    }

    @Override
    public void editarArea(final Area unArea) {
        new MaterialDialog.Builder(this)
                .title(this.getResources().getString(R.string.addNewArea))
                .inputRange(1,40)
                .contentColor(ContextCompat.getColor(this, R.color.primary_text))
                .backgroundColor(ContextCompat.getColor(this, R.color.tile1))
                .titleColor(ContextCompat.getColor(this, R.color.tile4))
                .content(this.getResources().getString(R.string.nombreArea))
                .inputType(InputType.TYPE_CLASS_TEXT)
                .input(this.getResources().getString(R.string.areaName),"", new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, final CharSequence input) {
                        FuncionesPublicas.modificarNombreArea(unArea,input.toString(),SettingsActivity.this);
                        Realm realm = null;
                        try {
                            realm = Realm.getDefaultInstance();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Realm.init(SettingsActivity.this.getApplicationContext());
                            realm = Realm.getDefaultInstance();
                        }
                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {

//                                LLAMO A LOS CUESTIONARIOS PARA DAR LAS OPCIONES
                                RealmResults<Cuestionario> losCuestionarios = realm.where(Cuestionario.class)
                                        .findAll();
                                List<String> unaListaNombre=new ArrayList<>();
                                final List<String> unaListaId=new ArrayList<>();
                                for (Cuestionario elCuestionario :
                                        losCuestionarios) {
                                    unaListaNombre.add(elCuestionario.getNombreCuestionario());
                                    unaListaId.add(elCuestionario.getIdCuestionario());
                                }

//                                LE DOY LAS OPCIONES AL USUARIO

                                new MaterialDialog.Builder(SettingsActivity.this)
                                        .title(R.string.tipoArea)
                                        .backgroundColor(ContextCompat.getColor(SettingsActivity.this, R.color.tile1))
                                        .items(unaListaNombre)
                                        .titleColor(ContextCompat.getColor(SettingsActivity.this, R.color.tile4))
                                        .cancelable(false)
                                        .itemsCallbackSingleChoice(0, new MaterialDialog.ListCallbackSingleChoice() {
                                            @Override
                                            public boolean onSelection(MaterialDialog dialog, View view, final int which, final CharSequence text) {
                                                Realm realm = null;
                                                try {
                                                    realm = Realm.getDefaultInstance();
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                    Realm.init(SettingsActivity.this.getApplicationContext());
                                                    realm = Realm.getDefaultInstance();
                                                }
                                                realm.executeTransaction(new Realm.Transaction() {
                                                    @Override
                                                    public void execute(@NonNull Realm realm) {
                                                        Area realmArea1= realm.where(Area.class)
                                                                .equalTo("idArea", unArea.getIdArea())
                                                                .findFirst();
                                                        if (realmArea1!=null) {
                                                            //ASIGNO ID DE CUESTIONARIO AL AREA
                                                            realmArea1.setIdCuestionario(unaListaId.get(which));
                                                        }
                                                        else{
                                                            Toast.makeText(SettingsActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });

                                                Toast.makeText(SettingsActivity.this, SettingsActivity.this.getString(R.string.areaSeModifico), Toast.LENGTH_SHORT).show();
                                                FragmentManager fragmentManager = getSupportFragmentManager();
                                                FragmentManageAreas fragmentManageAreas = (FragmentManageAreas)fragmentManager.findFragmentByTag(FuncionesPublicas.FRAGMENTMANAGER_AREAS);

                                                if (fragmentManageAreas!=null && fragmentManageAreas.isVisible()){
                                                    fragmentManageAreas.updateAdapter(SettingsActivity.this);
                                                }

                                                return true;
                                            }
                                        })
                                        .positiveText(R.string.ok)
                                        .show();
                                
                            }
                        });

                        final Area unArea = new Area();
                        unArea.setNombreArea(input.toString());
                        unArea.setIdArea(FuncionesPublicas.IDAREAS + UUID.randomUUID());
                        
                        
                       
                    }
                }).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case   android.R.id.home:
                   onBackPressed();
                    return true;
            case R.id.action_close:
                SettingsActivity.this.finish();

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    //este metodo anda bien?
    public void borrarDefinitivamente(final Area unArea){

        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
        } catch (Exception e) {
            e.printStackTrace();
            Realm.init(SettingsActivity.this.getApplicationContext());
            realm = Realm.getDefaultInstance();
        }

        RealmResults<Auditoria> result2 = realm.where(Auditoria.class)
                .equalTo("areaAuditada.idArea", unArea.getIdArea())
                .findAll();

        for (Auditoria audit:result2
                ) {
                FuncionesPublicas.borrarAuditoriaSeleccionada(audit.getIdAuditoria(),SettingsActivity.this);
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
            fragmentManageAreas.updateAdapter(SettingsActivity.this);
            Toast.makeText(this,getResources().getString(R.string.delteAreaOk), Toast.LENGTH_SHORT).show();
        }
    }
    
    public void crearDialogoBorrarArea(final Area unArea){
        new MaterialDialog.Builder(this)
                .title(getResources().getString(R.string.deleteTituloDialog))
                .contentColor(ContextCompat.getColor(this, R.color.primary_text))
                .titleColor(ContextCompat.getColor(this, R.color.tile4))
                .backgroundColor(ContextCompat.getColor(this, R.color.tile1))
                .content(getResources().getString(R.string.elArea) +" "+ unArea.getNombreArea() +"\n"+ getResources().getString(R.string.deletePermanente)+"\n"+getResources().getString(R.string.deseaContinuar))
                .positiveText(getResources().getString(R.string.ok))
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
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentManageAreas fragmentGestionAreas =(FragmentManageAreas) fragmentManager.findFragmentByTag(FuncionesPublicas.FRAGMENTMANAGER_AREAS);

        if (fragmentGestionAreas !=null && fragmentGestionAreas.isVisible()){
                fragmentManager.popBackStackImmediate();
        }
        else{
            super.onBackPressed();
            this.finish();
        }

    }

    @Override
    public void abrirEditorDeCuestionarios() {
        Intent intent = new Intent(this,ActivityGestionCuestionario.class);
        startActivity(intent);
    }

    @Override
    public void abrirFragmentGestionAreas() {
        FragmentManageAreas fragmentManageAreas = new FragmentManageAreas();
        FragmentManager fragmentManager=getSupportFragmentManager();
        FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.contenedorSettings, fragmentManageAreas,FuncionesPublicas.FRAGMENTMANAGER_AREAS);
        fragmentTransaction.addToBackStack(FuncionesPublicas.FRAGMENTMANAGER_AREAS);
        fragmentTransaction.commit();
    }

    @Override
    public void abrirEditorCriterios() {
        Intent intent = new Intent(this,ActivityGestionCuestionario.class);
        Bundle bundle = new Bundle();
        bundle.putString(ActivityGestionCuestionario.ORIGEN,FuncionesPublicas.EDITAR_CRITERIO);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void hacerLogout() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(this.getApplicationContext(), LoginActivity.class);
        startActivity(intent);
        this.finishAffinity();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.appbar, menu);
        return true;
    }
}
