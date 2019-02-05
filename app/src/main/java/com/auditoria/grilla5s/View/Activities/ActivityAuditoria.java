package com.auditoria.grilla5s.View.Activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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
import com.auditoria.grilla5s.View.Fragments.FragmentVerPregunta;
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
    public static final String IDESE="IDESE";
    public static final String ORIGEN="ORIGEN";
    public static final String IDCUESTIONARIO = "IDCUESTIONARIO";


    public static String idAudit;
    public String origen;
    public static String idItem;
    public String idese;
    private ViewPager pager;
    private String resultadoInputFoto;
    private FloatingActionMenu fabMenu;
    private Toolbar toolbar;
    private String idCuestionario;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auditoria);
        //RECIBO EL INTENT Y EN FUNCION DEL ORIGEN CARGO EL LAYOUT QUE CORRESPONDE

        Intent intent= getIntent();
        Bundle bundle= intent.getExtras();

        if (bundle!=null) {
            idAudit =bundle.getString(IDAUDITORIA);
            idItem=bundle.getString(IDITEM);
            origen=bundle.getString(ORIGEN);
            idese=bundle.getString(IDESE);
            idCuestionario=bundle.getString(IDCUESTIONARIO);
        }
//------ TOOLBAR HANDLING
        toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        Typeface robotoR = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Regular.ttf");
        TextView unText=toolbar.findViewById(R.id.textoToolbar);
        unText.setTypeface(robotoR);
        unText.setTextColor(getResources().getColor(R.color.blancoNomad));

        if (origen.equals(FuncionesPublicas.EDITAR_CUESTIONARIO)) {
            unText.setText(getResources().getString(R.string.editarCuestionarios));
        } else {
            unText.setText(getResources().getString(R.string.audit5s));
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
//------ TOOLBAR HANDLING//

        //TRAIGO LA LISTA DE PREGUNTAS

        Realm realm = Realm.getDefaultInstance();
        RealmResults<Pregunta>resultPregunta;
        if (origen.equals(FuncionesPublicas.EDITAR_CUESTIONARIO)) {
            resultPregunta = realm.where(Pregunta.class)
                    .equalTo("idCuestionario", idCuestionario)
                    .equalTo("idItem", idItem)
                    .equalTo("idEse",idese )
                    .findAll();
        } else {
            resultPregunta = realm.where(Pregunta.class)

             .equalTo("idAudit", idAudit)
                    .equalTo("idItem", idItem)
                    .equalTo("idEse",idese )
                    .findAll();
        }

        RealmList<Pregunta> listaPreguntasOriginales=new RealmList<>();
        listaPreguntasOriginales.addAll(resultPregunta);

        if (origen.equals(FuncionesPublicas.EDITAR_CUESTIONARIO)){
            cargarFragmentVerPreguntas();
        }
        else{
            //      SETEAR EL VIEWPAGER
            //GENERO LISTA DE TITULOS ORDINALES
            List<String> laListaDeTitulos=new ArrayList<>();
            for (Integer i=0;i<listaPreguntasOriginales.size();i++){
                Integer aux=i+1;
                laListaDeTitulos.add(aux.toString()+"°");
            }
            pager=findViewById(R.id.viewPagerAuditoria);
            AdapterPagerPreguntas adapterPager = new AdapterPagerPreguntas(getSupportFragmentManager(), listaPreguntasOriginales,origen,idese);
            pager.setAdapter(adapterPager);
            adapterPager.setUnaListaTitulos(laListaDeTitulos);
            adapterPager.notifyDataSetChanged();

            TabLayout tabLayout = findViewById(R.id.tabLayout);
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

    }

    private void cargarFragmentVerPreguntas() {
        Bundle bundle = new Bundle();
        bundle.putString(FragmentVerPregunta.IDCUESTIONARIO,idCuestionario);
        bundle.putString(FragmentVerPregunta.IDITEM,idItem);
        bundle.putString(FragmentVerPregunta.IDESE,idese);
        bundle.putString(FragmentVerPregunta.ORIGEN,origen);

        FragmentVerPregunta fragmentVerPregunta= new FragmentVerPregunta();
        fragmentVerPregunta.setArguments(bundle);
        FragmentManager fragmentManager=getSupportFragmentManager();
        FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.contenedorViewPager,fragmentVerPregunta,FuncionesPublicas.FRAGMENT_VER_PREGUNTAS);
        fragmentTransaction.commit();

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
        if (maAudit!=null) {
            bundle.putString(GraficosActivity.AREA, maAudit.getAreaAuditada().getIdArea());
        }
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
    public void actualizarPuntaje(String idAudit) {
        FuncionesPublicas.calcularPuntajesAuditoria(idAudit);
    }
}
