package com.nomad.mrg5s.View.Activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
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

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.nomad.mrg5s.DAO.ControllerDatos;
import com.nomad.mrg5s.Model.Auditoria;
import com.nomad.mrg5s.Model.Foto;
import com.nomad.mrg5s.Model.Pregunta;
import com.nomad.mrg5s.R;
import com.nomad.mrg5s.Utils.FuncionesPublicas;
import com.nomad.mrg5s.Utils.ResultListener;
import com.nomad.mrg5s.View.Adapter.AdapterPagerPreguntas;
import com.nomad.mrg5s.View.Fragments.FragmentEditarPregunta;
import com.nomad.mrg5s.View.Fragments.FragmentPregunta_;
import com.nomad.mrg5s.View.Fragments.FragmentZoom;
import com.github.clans.fab.FloatingActionMenu;


import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import io.realm.Sort;


public class ActivityAuditoria extends AppCompatActivity implements FragmentPregunta_.Avisable, FragmentEditarPregunta.Auditable{

    public static final String IDAUDITORIA ="IDAUDITORIA";
    public static final String IDITEM="IDITEM";
    public static final String IDESE="IDESE";
    public static final String ORIGEN="ORIGEN";
    public static final String IDCUESTIONARIO = "IDCUESTIONARIO";
    public static final String TIPOESTRUCTURA = "TIPOESTRUCTURA";
    public static final String IDPREGUNTA_CLICKEADA = "IDPREGUNTA_CLICKEADA";


    public static String idAudit;
    public String origen;
    public static String idItem;
    public String idese;
    private ViewPager pager;
    private String resultadoInputFoto;
    private FloatingActionMenu fabMenu;
    private Toolbar toolbar;
    private String idCuestionario;
    private String tipoEstructura;
    private String idpreguntaClickeada;
    private AdapterPagerPreguntas adapterPager;
    private ControllerDatos controllerDatos;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auditoria);
        //RECIBO EL INTENT Y EN FUNCION DEL ORIGEN CARGO EL LAYOUT QUE CORRESPONDE

        controllerDatos=new ControllerDatos(this);
        Intent intent= getIntent();
        Bundle bundle= intent.getExtras();

        if (bundle!=null) {
            idAudit =bundle.getString(IDAUDITORIA);
            idItem=bundle.getString(IDITEM);
            origen=bundle.getString(ORIGEN);
            idese=bundle.getString(IDESE);
            idCuestionario=bundle.getString(IDCUESTIONARIO);
            tipoEstructura=bundle.getString(TIPOESTRUCTURA);
            idpreguntaClickeada=bundle.getString(IDPREGUNTA_CLICKEADA);
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

        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
        } catch (Exception e) {
            e.printStackTrace();
            Realm.init(ActivityAuditoria.this.getApplicationContext());
            realm=Realm.getDefaultInstance();
        }
        RealmResults<Pregunta>resultPregunta;

        switch (origen){
            case FuncionesPublicas.EDITAR_CUESTIONARIO:
               resultPregunta= cargarPreguntasCuestionario();
               if (resultPregunta.size()==0){
                   String idPreguntaVacia =controllerDatos.crearPreguntaVacia(idCuestionario,idese,idItem);
                  resultPregunta =realm.where(Pregunta.class)
                          .equalTo("idPregunta", idPreguntaVacia)
                          .findAll();
               }

               break;
            default:
               resultPregunta= cargarPreguntasAuditoria();
               break;
        }
        RealmList<Pregunta> listaPreguntasOriginales=new RealmList<>();
        listaPreguntasOriginales.addAll(resultPregunta);



        //      SETEAR EL VIEWPAGER
        //GENERO LISTA DE TITULOS ORDINALES
        List<String> laListaDeTitulos=new ArrayList<>();
        for (Integer i=0;i<listaPreguntasOriginales.size();i++){
            Integer aux=i+1;
            laListaDeTitulos.add(aux.toString()+FuncionesPublicas.SIMBOLO_ORDINAL);
        }
        pager=findViewById(R.id.viewPagerAuditoria);
        adapterPager = new AdapterPagerPreguntas(getSupportFragmentManager(), listaPreguntasOriginales,origen,idese);
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


        Integer contador=0;
        for (Pregunta laPreg :
                listaPreguntasOriginales) {
            if (laPreg.getIdPregunta().equals(idpreguntaClickeada)){
                pager.setCurrentItem(contador);
            }
            else{
                contador++;
            }
        }

    }

    private RealmResults<Pregunta> cargarPreguntasAuditoria() {
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
        } catch (Exception e) {
            e.printStackTrace();
            Realm.init(ActivityAuditoria.this.getApplicationContext());
            realm=Realm.getDefaultInstance();
        }
        switch (tipoEstructura){
            case FuncionesPublicas.ESTRUCTURA_ESTRUCTURADA:
                return realm.where(Pregunta.class)
                        .equalTo("idAudit", idAudit)
                        .equalTo("idItem", idItem)
                        .equalTo("idEse",idese )
                        .sort("orden", Sort.ASCENDING)
                        .findAll();

            case FuncionesPublicas.ESTRUCTURA_SIMPLE:
                return realm.where(Pregunta.class)
                        .equalTo("idAudit", idAudit)
                        .equalTo("idEse",idese )
                        .sort("orden", Sort.ASCENDING)
                        .findAll();

            default:
                return realm.where(Pregunta.class)
                        .equalTo("idAudit", idAudit)
                        .equalTo("idItem", idItem)
                        .equalTo("idEse",idese )
                        .sort("orden", Sort.ASCENDING)
                        .findAll();
        }
    }

    private RealmResults<Pregunta> cargarPreguntasCuestionario() {
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
        } catch (Exception e) {
            e.printStackTrace();
            Realm.init(ActivityAuditoria.this.getApplicationContext());
            realm=Realm.getDefaultInstance();
        }

        switch (tipoEstructura){
            case FuncionesPublicas.ESTRUCTURA_ESTRUCTURADA:
                return realm.where(Pregunta.class)
                        .equalTo("idCuestionario", idCuestionario)
                        .equalTo("idItem", idItem)
                        .equalTo("idEse",idese )
                        .sort("orden", Sort.ASCENDING)
                        .findAll();

            case FuncionesPublicas.ESTRUCTURA_SIMPLE:
                return realm.where(Pregunta.class)
                        .equalTo("idCuestionario", idCuestionario)
                        .equalTo("idEse",idese )
                        .sort("orden", Sort.ASCENDING)
                        .findAll();

            default:
                return realm.where(Pregunta.class)
                        .equalTo("idCuestionario", idCuestionario)
                        .equalTo("idItem", idItem)
                        .equalTo("idEse",idese )
                        .sort("orden", Sort.ASCENDING)
                        .findAll();
        }
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
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
        } catch (Exception e) {
            e.printStackTrace();
            Realm.init(ActivityAuditoria.this.getApplicationContext());
            realm=Realm.getDefaultInstance();
        }
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

        switch (item.getItemId()){
            case R.id.action_close:
                if (origen.equals(FuncionesPublicas.NUEVA_AUDITORIA)||origen.equals(FuncionesPublicas.EDITAR_AUDITORIA)) {
                    new MaterialDialog.Builder(this)
                            .title(getString(R.string.advertencia))
                            .title(getResources().getString(R.string.advertencia))
                            .contentColor(ContextCompat.getColor(this, R.color.primary_text))
                            .titleColor(ContextCompat.getColor(this, R.color.tile4))
                            .backgroundColor(ContextCompat.getColor(this, R.color.tile1))
                            .content(getResources().getString(R.string.auditoriaSinTerminar) + "\n" + getResources().getString(R.string.continuar))
                            .positiveText(getResources().getString(R.string.si))
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    volverLanding();

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
                else {
                    volverLanding();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }



    public boolean volverLanding(){
        Intent intent = new Intent(ActivityAuditoria.this, LandingActivity.class);
        startActivity(intent);
        ActivityAuditoria.this.finish();
        return true;
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
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
        } catch (Exception e) {
            e.printStackTrace();
            Realm.init(ActivityAuditoria.this.getApplicationContext());
            realm=Realm.getDefaultInstance();
        }
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
        FuncionesPublicas.calcularPuntajesAuditoria(idAudit,ActivityAuditoria.this);
    }

    @Override
    public void agregarPregunta(CharSequence input, String idEse, String idItem, String idCuestionario) {
        final Pregunta nuevaPregunta= new Pregunta();
        nuevaPregunta.setTextoPregunta(input.toString());
        nuevaPregunta.setIdCuestioniario(idCuestionario);
        nuevaPregunta.setIdEse(idEse);
        nuevaPregunta.setIdItem(idItem);
        nuevaPregunta.setIdPregunta(FuncionesPublicas.IDPREGUNTAS + UUID.randomUUID());
        controllerDatos.agregarPregunta(idCuestionario, nuevaPregunta, new ResultListener<Boolean>() {
            @Override
            public void finish(Boolean resultado) {
                if (resultado) {
                    adapterPager.addPregunta(nuevaPregunta);
                    adapterPager.notifyDataSetChanged();
                    irAPreguntaAgregada();
                    Toast.makeText(ActivityAuditoria.this, ActivityAuditoria.this.getString(R.string.laPreguntaFueAgregada), Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(ActivityAuditoria.this, ActivityAuditoria.this.getString(R.string.laPreguntaNoSeAgrego), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void irAPreguntaAgregada() {
        pager.setCurrentItem(adapterPager.getCount());
    }

    @Override
    public void cerrarFragmentEdicion() {
        ActivityAuditoria.this.finish();
    }


}
