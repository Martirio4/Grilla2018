package com.auditoria.grilla5s.View.Activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.auditoria.grilla5s.Model.Area;

import com.auditoria.grilla5s.R;
import com.auditoria.grilla5s.Utils.FuncionesPublicas;
import com.auditoria.grilla5s.View.Fragments.FragmentLanding;
import com.auditoria.grilla5s.View.Fragments.FragmentSeleccionArea;
import com.google.firebase.auth.FirebaseAuth;

import java.io.File;

import io.realm.Realm;
import pl.tajchert.nammu.Nammu;

import static com.auditoria.grilla5s.View.Fragments.FragmentSettings.deleteDirectory;

public class LandingActivity extends AppCompatActivity implements FragmentLanding.Landinable, FragmentSeleccionArea.Notificable {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);
        Realm.init(getApplicationContext());

        Nammu.init(getApplicationContext());

        //borrar cache auditorias PDF
        if (Nammu.checkPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)){
            Integer permisoParaEscribir = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (FuncionesPublicas.isExternalStorageWritable()) {
                File path = new File(getExternalFilesDir(null) + File.separator + "nomad" + File.separator + "audit5s" + File.separator + FirebaseAuth.getInstance().getCurrentUser().getEmail() + File.separator + "audits");
                Boolean deleteDirectorio = deleteDirectory(path);
            }
        }

        lanzarLandingFragment();
    }

    private void lanzarLandingFragment() {
        FragmentLanding fragmentLanding=new FragmentLanding();
        FragmentManager fragmentManager=getSupportFragmentManager();
        FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.contenedor_landing_completo,fragmentLanding,"landing");
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void comenzarAuditoria(Area unArea) {

        Intent intent = new Intent(this, ActivityAuditoria.class);
        Bundle bundle = new Bundle();
        bundle.putString(ActivityAuditoria.IDAREA, unArea.getIdArea());
        intent.putExtras(bundle);
        startActivity(intent);
        FragmentManager fragmentManager = (FragmentManager) this.getSupportFragmentManager();
        fragmentManager.popBackStack();

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager = (FragmentManager) this.getSupportFragmentManager();
        FragmentSeleccionArea seleccionAreas = (FragmentSeleccionArea) fragmentManager.findFragmentByTag("seleccion");


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
                            /* //FUNCIONES FIREBASE
                            FuncionesPublicas.sumarUsoDeApp(LandingActivity.this);
                            sumarVecesAbiertoFirebase();
                            */
                            LandingActivity.super.onBackPressed();
                            finishAffinity();
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
        FragmentSeleccionArea fragmentSeleccionAerea = (FragmentSeleccionArea) fragmentManager1.findFragmentByTag("seleccion");

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
        fragmentTransaction.replace(R.id.contenedor_landing_completo,fragmentSeleccionAerea,"seleccion");
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    /* //REGISTRAR EN FIREBASE
    public void sumarVecesAbiertoFirebase() {
        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference mDatabase= FirebaseDatabase.getInstance().getReference();
        if (user!=null) {

            SharedPreferences sharedPreferences=getSharedPreferences("prefs",0);
            final Integer vecesUsuario=sharedPreferences.getInt("cantidadUsos",0);

            Calendar cal = Calendar.getInstance();
            Date date=cal.getTime();

            String monthNumber  = (String) DateFormat.format("MM",   date); // 06
            String year         = (String) DateFormat.format("yyyy", date); // 2013

            final DatabaseReference reference = mDatabase.child("usuarios").child(user.getUid()).child("estadisticas").child("abrioApp").child(monthNumber+"-"+year);

            //---leer cantidad de auditorias---//
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    reference.setValue(vecesUsuario);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }
    }
    */
}
