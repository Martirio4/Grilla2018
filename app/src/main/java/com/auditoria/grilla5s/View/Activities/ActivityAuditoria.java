package com.auditoria.grilla5s.View.Activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.auditoria.grilla5s.Model.Auditoria;
import com.auditoria.grilla5s.Model.Foto;
import com.auditoria.grilla5s.Model.Pregunta;
import com.auditoria.grilla5s.R;
import com.auditoria.grilla5s.View.Adapter.AdapterPagerPreguntas;
import com.auditoria.grilla5s.View.Fragments.FragmentPregunta;
import com.auditoria.grilla5s.View.Fragments.FragmentZoom;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetView;
import com.github.clans.fab.FloatingActionMenu;


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
                .beginsWith("idPregunta",idItem)
                .findAll();

        RealmList<Pregunta> listaPreguntasOriginales=new RealmList<>();
        listaPreguntasOriginales.addAll(resultPregunta);

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

/*
    private void updateTabTextColors(){
        LinearLayout tabsContainer=(LinearLayout)tabLayout.getChildAt(0);

        for(int i =4;i<8;i++){
            LinearLayout item = (LinearLayout) tabsContainer.getChildAt(i);
            TextView tv =(TextView)item.getChildAt(1);
            tv.setTextColor(ContextCompat.getColor(this, R.color.tile1));
        }

    }
    */

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

        Intent intent=new Intent(this, GraficosActivity.class);
        Bundle bundle=new Bundle();
        bundle.putString(GraficosActivity.AUDIT, idAudit);
        bundle.putString(GraficosActivity.ORIGEN, "auditoria");
        intent.putExtras(bundle);
        startActivity(intent);
        this.finish();

    }


    @Override
    public void salirDeAca() {
        Intent intent= new Intent(this, LandingActivity.class);
        startActivity(intent);
        this.finish();
    }






    @Override
    public void mostrarToolbar() {
        Typeface roboto=Typeface.createFromAsset(getAssets(), "fonts/Roboto-Regular.ttf");
        TapTargetView.showFor(this,                 // `this` is an Activity
                TapTarget.forToolbarNavigationIcon(toolbar, getResources().getString(R.string.tutorial_tit_navegar), getResources().getString(R.string.tutorial_desc_navegar
                ))
                        // All options below are optional
                        .outerCircleColor(R.color.tutorial1)      // Specify a color for the outer circle
                        .outerCircleAlpha(0.85f)
                        .textColor(R.color.primary_text)// Specify the alpha amount for the outer circle
                        .textTypeface(roboto)  // Specify a typeface for the text
                        .drawShadow(true)                   // Whether to draw a drop shadow or not
                        .cancelable(false)                  // Whether tapping outside the outer circle dismisses the view
                        .tintTarget(false)                   // Whether to tint the target view's color
                        .transparentTarget(true),           // Specify whether the target is transparent (displays the content underneath)

                    new TapTargetView.Listener() {          // The listener can listen for regular clicks, long clicks or cancels
                    @Override
                    public void onTargetClick(TapTargetView view) {
                        super.onTargetClick(view);      // This call is optional
                    }
                });

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
}
