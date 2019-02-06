package com.auditoria.grilla5s.View.Activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.MenuItem;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.auditoria.grilla5s.DAO.ControllerDatos;
import com.auditoria.grilla5s.Model.Area;
import com.auditoria.grilla5s.Model.Auditoria;
import com.auditoria.grilla5s.Model.Item;
import com.auditoria.grilla5s.Model.Pregunta;
import com.auditoria.grilla5s.R;
import com.auditoria.grilla5s.Utils.FuncionesPublicas;
import com.auditoria.grilla5s.View.Adapter.AdapterItems;
import com.auditoria.grilla5s.View.Adapter.AdapterPagerEses;
import com.auditoria.grilla5s.View.Adapter.AdapterPreguntas;
import com.auditoria.grilla5s.View.Fragments.FragmentPreAudit;

import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmList;

public class ActivityPreAuditoria extends AppCompatActivity implements FragmentPreAudit.Auditable{


    public static String idAudit;
    private String idArea;
    public static final String IDAREA="IDAREA";
    public static final String ORIGEN="ORIGEN";
    public static final String IDCUESTIONARIO ="IDCUESTIONARIO" ;
    //SOLO RECIBO ESTA KEY CUANDO QUIERO EDITAR UNA AUDITORIA
    public static final String IDAUDIT="IDAUDIT";

    private ViewPager pager;
    private String origen;
    public static String idCuestionario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre_auditoria);
        ControllerDatos controllerDatos = new ControllerDatos(this);

        Intent intent=getIntent();
        Bundle bundle=intent.getExtras();

        if (bundle!=null){
            origen=bundle.getString(ORIGEN);
            idArea=bundle.getString(IDAREA);
            idAudit=bundle.getString(IDAUDIT);
            idCuestionario =bundle.getString(IDCUESTIONARIO);
        }

        //SI EL ORIGEN ES NUEVA AUDITORIA SIGO NORMAL, INSTANCIO UNA NUVA AUDITORIA
        if (origen!=null && origen.equals(FuncionesPublicas.NUEVA_AUDITORIA)) {

            if (bundle!=null) {
                idArea=bundle.getString(IDAREA);
            }

            //      INSTANCIO LA AUDITORIA Y LE CARGO EL AREA

            Realm realm = Realm.getDefaultInstance();
            Area elArea=realm.where(Area.class)
                    .equalTo("idArea",idArea)
                    .findFirst();


            assert elArea != null;

            idAudit= controllerDatos.instanciarAuditoria(elArea.getIdCuestionario());


            realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(@NonNull Realm realm) {
            Auditoria auditActual= realm.where(Auditoria.class)
                    .equalTo("idAuditoria",idAudit)
                    .findFirst();
            Area areaAIncluir = realm.where(Area.class)
                    .equalTo("idArea",idArea)
                    .findFirst();

            if (auditActual!=null && areaAIncluir!=null){
                auditActual.setAreaAuditada(areaAIncluir);
            }
            }
            });
        }

        Typeface robotoR = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Regular.ttf");


        Toolbar toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            TextView unText= toolbar.findViewById(R.id.textoToolbar);
            unText.setTypeface(robotoR);
            unText.setTextColor(getResources().getColor(R.color.blancoNomad));

            if (origen.equals(FuncionesPublicas.NUEVA_AUDITORIA)) {
                unText.setText(getResources().getText(R.string.tituloFragmentPreAudit));
            } else if (origen.equals(FuncionesPublicas.EDITAR_CUESTIONARIO)){
                unText.setText(getResources().getText(R.string.editarCuestionarios));
            }
        }

//       CARGO EL VIEWPAGER
        pager=findViewById(R.id.viewPagerPreAuditoria);
        AdapterPagerEses adapterPager=new AdapterPagerEses(getSupportFragmentManager(),origen, idCuestionario);
        adapterPager.setUnaListaTitulos(controllerDatos.traerEses());
        pager.setAdapter(adapterPager);
        adapterPager.notifyDataSetChanged();

        //        SETEAR EL TABLAYOUT

        TabLayout tabLayout =  findViewById(R.id.tabLayoutPreAudit);
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

//        ON PAGE CHANGE LISTENER
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


    public static String pedirIdAudit(){
        return idAudit;
    }

    @Override
    public void auditarItem(Item unItem) {
        Intent intent=new Intent(this, ActivityAuditoria.class);
        Bundle bundle=new Bundle();
        bundle.putString(ActivityAuditoria.IDCUESTIONARIO,unItem.getIdCuestionario());
        bundle.putString(ActivityAuditoria.IDAUDITORIA, idAudit);
        bundle.putString(ActivityAuditoria.IDESE,unItem.getIdEse() );
        bundle.putString(ActivityAuditoria.IDITEM, unItem.getIdItem());
        bundle.putString(ActivityAuditoria.ORIGEN, origen);
        bundle.putString(ActivityAuditoria.TIPOESTRUCTURA, FuncionesPublicas.ESTRUCTURA_ESTRUCTURADA);
        intent.putExtras(bundle);
        startActivity(intent);

    }



    @Override
    public void cerrarAuditoria() {

        Intent intent=new Intent(this, GraficosActivity.class);
        Bundle bundle=new Bundle();
        bundle.putString(GraficosActivity.AUDIT, idAudit);
        bundle.putString(GraficosActivity.ORIGEN, FuncionesPublicas.NUEVA_AUDITORIA);
        bundle.putString(GraficosActivity.AREA,idArea);
        intent.putExtras(bundle);
        startActivity(intent);
        this.finish();

    }

    @Override
    public void onBackPressed() {

        switch (origen) {
            case FuncionesPublicas.REVISAR:
                ActivityPreAuditoria.super.onBackPressed();
                break;
            case FuncionesPublicas.EDITAR_CUESTIONARIO:
                ActivityPreAuditoria.super.onBackPressed();
                break;
            default:
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
                                ActivityPreAuditoria.super.onBackPressed();
                            }
                        })
                        .negativeText(getResources().getString(R.string.cancel))
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            }
                        })
                        .show();
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();    //Call the back button's method
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    @Override
    public void actualizarPuntaje(String idAudit) {
        FuncionesPublicas.calcularPuntajesAuditoria(idAudit);
    }

    @Override
    public void agregarNuevoItem(final String laEse, final String idCuestionario, final AdapterItems elAdapter) {
        new MaterialDialog.Builder(this)
                .title(getResources().getString(R.string.nuevoItem))
                .contentColor(ContextCompat.getColor(this, R.color.primary_text))
                .backgroundColor(ContextCompat.getColor(this, R.color.tile1))
                .titleColor(ContextCompat.getColor(this, R.color.tile4))
                .content(getResources().getString(R.string.agregueTituloItem))
                .inputType(InputType.TYPE_CLASS_TEXT)
                .input(getResources().getString(R.string.comment),"", new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, final CharSequence input) {
                        if (input!=null && !input.toString().isEmpty()) {
                            Item nuevoItem= new Item();
                            nuevoItem.setTituloItem(input.toString());
                            nuevoItem.setIdCuestionario(idCuestionario);
                            nuevoItem.setIdEse(laEse);
                            nuevoItem.setListaPreguntas(new RealmList<Pregunta>());
                            nuevoItem.setIdItem(FuncionesPublicas.IDITEMS + UUID.randomUUID());

                            FuncionesPublicas.agregarItem(idCuestionario,nuevoItem,elAdapter);
                        }

                    }
                }).show();
    }

    @Override
    public void agregarNuevaPregunta(final String laEse, final String idCuestionario, final AdapterPreguntas adapterPreguntas) {
        new MaterialDialog.Builder(this)
                .title(getResources().getString(R.string.nuevaPregunta))
                .contentColor(ContextCompat.getColor(this, R.color.primary_text))
                .backgroundColor(ContextCompat.getColor(this, R.color.tile1))
                .titleColor(ContextCompat.getColor(this, R.color.tile4))
                .content(getResources().getString(R.string.agreguePRegunta))
                .inputType(InputType.TYPE_CLASS_TEXT)
                .input(getResources().getString(R.string.comment),"", new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, final CharSequence input) {
                        if (input!=null && !input.toString().isEmpty()) {
                            Pregunta nuevaPregunta= new Pregunta();
                            nuevaPregunta.setTextoPregunta(input.toString());
                            nuevaPregunta.setIdCuestioniario(idCuestionario);
                            nuevaPregunta.setIdEse(laEse);
                            nuevaPregunta.setIdItem(null);
                            nuevaPregunta.setIdPregunta(FuncionesPublicas.IDPREGUNTAS + UUID.randomUUID());

                            FuncionesPublicas.agregarPregunta(idCuestionario,nuevaPregunta,adapterPreguntas);
                        }

                    }
                }).show();
    }

    @Override
    public void auditarPregunta(Pregunta preguntaClickeada) {
        Intent intent=new Intent(this, ActivityAuditoria.class);
        Bundle bundle=new Bundle();
        bundle.putString(ActivityAuditoria.IDCUESTIONARIO,preguntaClickeada.getIdCuestionario());
        bundle.putString(ActivityAuditoria.IDAUDITORIA, idAudit);
        bundle.putString(ActivityAuditoria.IDESE,preguntaClickeada.getIdEse() );
        bundle.putString(ActivityAuditoria.IDITEM, null);
        bundle.putString(ActivityAuditoria.ORIGEN, origen);
        bundle  .putString(ActivityAuditoria.TIPOESTRUCTURA, FuncionesPublicas.ESTRUCTURA_SIMPLE);
        intent.putExtras(bundle);
        startActivity(intent);

    }
}
