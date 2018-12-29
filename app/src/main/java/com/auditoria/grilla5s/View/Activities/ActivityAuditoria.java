package com.auditoria.grilla5s.View.Activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.auditoria.grilla5s.Model.Auditoria;
import com.auditoria.grilla5s.Model.Foto;
import com.auditoria.grilla5s.Model.Pregunta;
import com.auditoria.grilla5s.R;
import com.auditoria.grilla5s.Utils.FuncionesPublicas;
import com.auditoria.grilla5s.View.Adapter.AdapterPagerPreguntas;
import com.auditoria.grilla5s.View.Fragments.FragmentPregunta;
import com.auditoria.grilla5s.View.Fragments.FragmentZoom;
import com.github.clans.fab.FloatingActionMenu;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;


public class ActivityAuditoria extends AppCompatActivity implements FragmentPregunta.Avisable{

    public static final String IDAUDITORIA ="IDAUDITORIA";
    public static final String IDITEM="IDITEM";
    public static final String ESREVISION="ESREVISION";

    public static String idAudit;
    public static String idItem;
    private ViewPager pager;
    private String resultadoInputFoto;
    private FloatingActionMenu fabMenu;
    private Toolbar toolbar;
    private Boolean esRevision;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auditoria);


        Intent intent= getIntent();
        Bundle bundle= intent.getExtras();

        if (bundle!=null) {
            idAudit =bundle.getString(IDAUDITORIA);
            idItem=bundle.getString(IDITEM);
            esRevision=bundle.getBoolean(ESREVISION);
        }


//        SETEAR EL VIEWPAGER


        Realm realm = Realm.getDefaultInstance();
        RealmResults<Pregunta>resultPregunta=realm.where(Pregunta.class)
                .equalTo("idAudit", idAudit)
                .findAll();

        RealmList<Pregunta> listaPreguntasOriginales=new RealmList<>();
        for (Pregunta laPregunta :resultPregunta) {
            String elItemStr = String.valueOf(laPregunta.getIdItem());
            if (elItemStr.startsWith(idItem)){
                listaPreguntasOriginales.add(laPregunta);
            }
        }

        //GENERO LISTA DE TITULOS ORDINALES
        List<String> laListaDeTitulos=new ArrayList<>();
        for (Integer i=0;i<listaPreguntasOriginales.size();i++){
            Integer aux=i+1;
            laListaDeTitulos.add(aux.toString()+"°");
        }
        pager=findViewById(R.id.viewPagerAuditoria);
        AdapterPagerPreguntas adapterPager = new AdapterPagerPreguntas(getSupportFragmentManager(), listaPreguntasOriginales,esRevision);
        pager.setAdapter(adapterPager);
        adapterPager.setUnaListaTitulos(laListaDeTitulos);
        adapterPager.notifyDataSetChanged();


        // Get a support ActionBar corresponding to this toolbar
        toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.marfil));

        Typeface robotoR = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Regular.ttf");
        TextView unText=toolbar.findViewById(R.id.textoToolbar);
        unText.setTypeface(robotoR);
        unText.setTextColor(getResources().getColor(R.color.tile5));

        unText.setText(getResources().getString(R.string.audit5s));

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        }


//        SETEAR EL TABLAYOUT

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(pager);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                pager.setCurrentItem(tab.getPosition(), true);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                pager.setCurrentItem(tab.getPosition(), true);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                pager.setCurrentItem(tab.getPosition(), true);
            }
        });

        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {
                
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.appbar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

    }





    @Override
    public void cerrarAuditoria() {
        Realm realm = Realm.getDefaultInstance();
        Auditoria maAudit=realm.where(Auditoria.class)
                .equalTo("idAuditoria",idAudit)
                .findFirst();

        Intent intent=new Intent(this, GraficosActivity.class);
        Bundle bundle=new Bundle();
        bundle.putString(GraficosActivity.AUDIT, idAudit);
        bundle.putString(GraficosActivity.ORIGEN, "auditoria");
        bundle.putString(GraficosActivity.AREA, maAudit.getAreaAuditada().getIdArea());
        intent.putExtras(bundle);
        startActivity(intent);
        ActivityAuditoria.this.finish();

    }


    @Override
    public void salirDeAca() {
        Intent intent= new Intent(this, LandingActivity.class);
        startActivity(intent);
        this.finish();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }



    @Override
    public void onBackPressed() {

        FragmentManager fragmentManager =  getSupportFragmentManager();
        FragmentZoom fragmentZoom = (FragmentZoom) fragmentManager.findFragmentByTag("zoom");


        if (fragmentZoom != null && fragmentZoom.isVisible()) {
           fragmentManager.popBackStack();
        }
        else {
                super.onBackPressed();
        }


    }

    @Override
    public void zoomearImagen(Foto unaFoto) {
        Intent intent = new Intent(this,ActivityZoom.class);
        Bundle mBundle= new Bundle();
        mBundle.putString(ActivityZoom.IDFOTO,unaFoto.getIdFoto());
        intent.putExtras(mBundle);
        startActivity(intent);
    }

    @Override
    public void borrarFoto(final Foto unaFoto) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {

              Pregunta unaPreg= realm.where(Pregunta.class)
                      .equalTo("idPregunta", unaFoto.getIdPregunta())
                      .equalTo("idAudit", unaFoto.getIdAudit())
                      .findFirst();

              if (unaPreg!=null){
                  if (unaPreg.getListaFotos().contains(unaFoto)){
                      unaPreg.getListaFotos().remove(unaPreg.getListaFotos().indexOf(unaFoto));
                  }
              }
                File file = new File(unaFoto.getRutaFoto());
                boolean deleted = file.delete();
                Foto laFoto = realm.where(Foto.class)
                        .equalTo("idFoto", unaFoto.getIdFoto())
                        .findFirst();
                laFoto.deleteFromRealm();

                Toast.makeText(ActivityAuditoria.this, getResources().getString(R.string.fotoBorrada), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void cargarAuditoriaEnFirebase(String idAudit) {
        FuncionesPublicas.subirAFireBase(idAudit);
    }

    @Override
    public void actualizarPuntaje(String idAudit) {
        FuncionesPublicas.calcularPuntajesAuditoria(idAudit);
    }
}
