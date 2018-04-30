package com.auditoria.grilla5s.View.Activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.auditoria.grilla5s.DAO.ControllerDatos;
import com.auditoria.grilla5s.Model.Area;
import com.auditoria.grilla5s.Model.Auditoria;
import com.auditoria.grilla5s.Model.Item;
import com.auditoria.grilla5s.R;
import com.auditoria.grilla5s.Utils.FuncionesPublicas;
import com.auditoria.grilla5s.View.Adapter.AdapterPagerEses;
import com.auditoria.grilla5s.View.Fragments.FragmentPreAudit;
import com.github.mikephil.charting.formatter.IFillFormatter;

import io.realm.Realm;
import io.realm.RealmResults;

public class ActivityPreAuditoria extends AppCompatActivity implements FragmentPreAudit.Auditable{

    private ControllerDatos controllerDatos;
    public static String idAudit;
    private String idArea;
    public static final String IDAREA="IDAREA";
    public static final String ORIGEN="ORIGEN";
    //SOLO RECIBO ESTA KEY CUANDO QUIERO EDITAR UNA AUDITORIA
    public static final String IDAUDIT="IDAUDIT";

    private ViewPager pager;
    private Toolbar toolbar;
    private String origen;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre_auditoria);

        Intent intent=getIntent();
        Bundle bundle=intent.getExtras();

        if (bundle!=null){
            origen=bundle.getString(ORIGEN);
            idArea=bundle.getString(IDAREA);
            idAudit=bundle.getString(IDAUDIT);
        }

        //SI EL ORIGEN ES NUEVA AUDITORIA SIGO NORMAL, INSTANCIO UNA NUVA AUDITORIA
        if (origen!=null && origen.equals("NUEVA_AUDITORIA")) {

            idArea=bundle.getString(IDAREA);

            //      INSTANCIO LA AUDITORIA Y LE CARGO EL AREA
            controllerDatos= new ControllerDatos(this);


            Realm realm = Realm.getDefaultInstance();
            Area elArea=realm.where(Area.class)
                    .equalTo("idArea",idArea)
                    .findFirst();

            if (elArea!=null && elArea.getTipoArea()!=null){
                idAudit=controllerDatos.instanciarAuditoria(elArea.getTipoArea());
            }
            else if(elArea!=null && elArea.getTipoArea()==null){
                //si no tiene tipo la instacio como zona industrial
                idAudit=controllerDatos.instanciarAuditoria("A");
            }

            realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
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

        toolbar =  findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.blancoNomad));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setTitle(getResources().getString(R.string.tituloFragmentPreAudit));
        }

//       CARGO EL VIEWPAGER
        pager=findViewById(R.id.viewPagerPreAuditoria);
        controllerDatos=new ControllerDatos(this);
        AdapterPagerEses adapterPager=new AdapterPagerEses(getSupportFragmentManager());
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
        bundle.putString(ActivityAuditoria.IDAUDITORIA, idAudit);
        bundle.putString(ActivityAuditoria.IDITEM, unItem.getIdItem());
        if(origen.equals("REVISAR")){
            bundle.putBoolean(ActivityAuditoria.ESREVISION, true);
        }
        else{
            bundle.putBoolean(ActivityAuditoria.ESREVISION, false);
        }
        intent.putExtras(bundle);
        startActivity(intent);

    }

    @Override
    public void titularToolbar() {
        TextView texto = toolbar.findViewById(R.id.textoToolbar);
        texto.setText(getResources().getString(R.string.tituloFragmentPreAudit));
    }

    @Override
    public void cerrarAuditoria() {

        Intent intent=new Intent(this, GraficosActivity.class);
        Bundle bundle=new Bundle();
        bundle.putString(GraficosActivity.AUDIT, idAudit);
        bundle.putString(GraficosActivity.ORIGEN, "auditoria");
        bundle.putString(GraficosActivity.AREA,idArea);
        intent.putExtras(bundle);
        startActivity(intent);
        this.finish();

    }

    @Override
    public void onBackPressed() {

        if (origen.equals("REVISAR")) {
            ActivityPreAuditoria.super.onBackPressed();
        }
        else {
            new MaterialDialog.Builder(this)
                    .title("Warning!")
                    .title(getResources().getString(R.string.advertencia))
                    .contentColor(ContextCompat.getColor(this, R.color.primary_text))
                    .titleColor(ContextCompat.getColor(this, R.color.tile4))
                    .backgroundColor(ContextCompat.getColor(this, R.color.tile1))
                    .content(getResources().getString(R.string.auditoriaSinTerminar)+"\n"+getResources().getString(R.string.continuar))
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
    public void cargarAuditoriaEnFirebase(String idAudit) {
        FuncionesPublicas.subirAFireBase(idAudit);
    }

    @Override
    public void actualizarPuntaje(String idAudit) {
        FuncionesPublicas.calcularPuntajesAuditoria(idAudit);
    }
}
