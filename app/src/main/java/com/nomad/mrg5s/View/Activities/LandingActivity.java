package com.nomad.mrg5s.View.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.nomad.mrg5s.DAO.ControllerDatos;
import com.nomad.mrg5s.Model.Area;

import com.nomad.mrg5s.Model.Cuestionario;
import com.nomad.mrg5s.R;
import com.nomad.mrg5s.Utils.FuncionesPublicas;
import com.nomad.mrg5s.View.Fragments.FragmentLanding;
import com.nomad.mrg5s.View.Fragments.FragmentSeleccionArea;
import com.google.firebase.auth.FirebaseAuth;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import pl.tajchert.nammu.Nammu;

import static com.nomad.mrg5s.View.Fragments.FragmentSettings.deleteDirectory;

public class LandingActivity extends AppCompatActivity implements FragmentLanding.Landinable, FragmentSeleccionArea.Notificable {

    private SharedPreferences config;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);

        Nammu.init(getApplicationContext());
        config=this.getSharedPreferences("prefs",0);

//BORRAR CACHE AUDITORIAS GENERADAS PARA ENVIAR POR MAIL
        if (Nammu.checkPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)){
            if (FuncionesPublicas.isExternalStorageWritable()) {
                File path = new File(getExternalFilesDir(null) + File.separator + "nomad" + File.separator + "audit5s" + File.separator + FirebaseAuth.getInstance().getCurrentUser().getEmail() + File.separator + "audits");
                try {
                    deleteDirectory(path);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        evaluarGeneracionAuditoriasModelo();
        lanzarLandingFragment();
    }

    private void evaluarGeneracionAuditoriasModelo() {
        boolean firstRun = config.getBoolean("firstRun", false);
        if (!firstRun){
            ControllerDatos controllerDatos=new ControllerDatos(this);
            controllerDatos.crearCriteriosDefault();
            controllerDatos.crearCuestionariosDefault(getString(R.string.areaGeneral),true);
            controllerDatos.crearCuestionariosDefault(getString(R.string.areaIndustrial));
            controllerDatos.crearCuestionariosDefault(getString(R.string.areaOficina));
            controllerDatos.crearCuestionariosDefault(getString(R.string.areaExterna));

            SharedPreferences.Editor editor = config.edit();
            editor.putBoolean("firstRun", true);
            editor.commit();

        }
    }

    private void lanzarLandingFragment() {
        FragmentLanding fragmentLanding=new FragmentLanding();
        FragmentManager fragmentManager=getSupportFragmentManager();
        FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.contenedor_landing_completo,fragmentLanding,FuncionesPublicas.FRAGMENT_LANDING);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void comenzarAuditoria(Area unArea) {
        Realm realm=Realm.getDefaultInstance();
        Cuestionario elCuestionario = realm.where(Cuestionario.class)
                .equalTo("idCuestionario", unArea.getIdCuestionario())
                .findFirst();

        if (elCuestionario!=null) {
            abrirActivityPreAuditoria(unArea);
        } else {
            pedirCuestionario(unArea);
        }

    }

    private void pedirCuestionario(final Area unArea) {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Cuestionario> losCuestionarios = realm.where(Cuestionario.class)
                .findAll();

        List<String> unaListaNombre=new ArrayList<>();
        final List<String> unaListaId=new ArrayList<>();

        for (Cuestionario elCuestionario :
                losCuestionarios) {
            unaListaNombre.add(elCuestionario.getNombreCuestionario());
            unaListaId.add(elCuestionario.getIdCuestionario());
        }

        new MaterialDialog.Builder(this)
                .title(R.string.tipoArea)
                .items(unaListaNombre)
                .widgetColor(getResources().getColor(R.color.tile3))
                .cancelable(false)
                .itemsCallbackSingleChoice(0, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, final int which, final CharSequence text) {
                        Realm realm =Realm.getDefaultInstance();
                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(@NonNull Realm realm) {
                                Area realmArea1= realm.where(Area.class)
                                        .equalTo("idArea", unArea.getIdArea())
                                        .findFirst();
                                if (realmArea1!=null) {
                                    //ASIGNO ID DE CUESTIONARIO AL AREA
                                    realmArea1.setIdCuestionario(unaListaId.get(which));
                                    //ABRO PREAUDITORIA Y SIGO
                                    abrirActivityPreAuditoria(unArea);
                                }
                                else{
                                    Toast.makeText(LandingActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                        Toast.makeText(LandingActivity.this,getString(R.string.cambioRealizado), Toast.LENGTH_SHORT).show();
                        return true;
                    }
                })
                .positiveText(R.string.ok)
                .show();
    }

    private void abrirActivityPreAuditoria(Area unArea) {
        Intent intent = new Intent(this, ActivityPreAuditoria.class);
        Bundle bundle = new Bundle();
        bundle.putString(ActivityPreAuditoria.IDAREA, unArea.getIdArea());
        bundle.putString(ActivityPreAuditoria.ORIGEN, FuncionesPublicas.NUEVA_AUDITORIA);
        bundle.putString(ActivityPreAuditoria.IDAUDIT, "NULL");
        bundle.putString(ActivityPreAuditoria.IDCUESTIONARIO, unArea.getIdCuestionario());

        intent.putExtras(bundle);
        startActivity(intent);
        FragmentManager fragmentManager = this.getSupportFragmentManager();
        fragmentManager.popBackStack();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager = this.getSupportFragmentManager();
        FragmentSeleccionArea seleccionAreas = (FragmentSeleccionArea) fragmentManager.findFragmentByTag(FuncionesPublicas.FRAGMENT_SELECCION_AREAS);


        if (seleccionAreas != null && seleccionAreas.isVisible()) {
            fragmentManager.popBackStack();
            lanzarLandingFragment();
        } else {
            new MaterialDialog.Builder(this)
                    .contentColor(ContextCompat.getColor(this, R.color.primary_text))
                    .titleColor(ContextCompat.getColor(this, R.color.tile4))
                    .title(R.string.quit)
                    .content(R.string.des_quit)
                    .positiveText(R.string.quit)
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                            LandingActivity.this.finishAffinity();
                        }
                    })
                    .negativeText(R.string.cancel)
                    .show();
        }

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        FragmentManager fragmentManager1 = this.getSupportFragmentManager();
        FragmentSeleccionArea fragmentSeleccionAerea = (FragmentSeleccionArea) fragmentManager1.findFragmentByTag(FuncionesPublicas.FRAGMENT_SELECCION_AREAS);

        if (fragmentSeleccionAerea != null && fragmentSeleccionAerea.isVisible()) {

            if (id == android.R.id.home) {
                onBackPressed();
                return true;
            }
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void irASelecccionAreas() {
        FragmentSeleccionArea fragmentSeleccionAerea = new FragmentSeleccionArea();
        FragmentManager fragmentManager= getSupportFragmentManager();
        FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.contenedor_landing_completo,fragmentSeleccionAerea,FuncionesPublicas.FRAGMENT_SELECCION_AREAS);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void salirCompleto() {
        finishAffinity();
    }

    @Override
    public void abrirSettings() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    @Override
    public void abrirMisAudits() {
        Intent intent = new Intent(this, ActivityMyAudits.class);
        startActivity(intent);
    }
}
