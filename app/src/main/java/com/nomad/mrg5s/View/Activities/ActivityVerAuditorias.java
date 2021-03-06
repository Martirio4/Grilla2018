package com.nomad.mrg5s.View.Activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.nomad.mrg5s.DAO.ControllerDatos;
import com.nomad.mrg5s.R;
import com.nomad.mrg5s.View.Adapter.AdapterPagerVerAudits;


public class ActivityVerAuditorias extends AppCompatActivity {

    public static final String AUDITORIA="AUDITORIA";
    public static String idAuditoria;

    private ViewPager pager;
    private AdapterPagerVerAudits adapterPager;
    private ControllerDatos controllerDatos;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_audit);
        // Get a support ActionBar corresponding to this toolbar
        Toolbar toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.blancoNomad));

        Typeface robotoR = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Regular.ttf");
        TextView unText=toolbar.findViewById(R.id.textoToolbar);
        unText.setTypeface(robotoR);
        unText.setTextColor(getResources().getColor(R.color.tile5));
        unText.setText(getResources().getString(R.string.detalleAuditoria));


        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        }

        Intent unIntent=getIntent();
        Bundle bundle= unIntent.getExtras();

        idAuditoria=bundle.getString(AUDITORIA);

        pager=(ViewPager)findViewById(R.id.viewPagerVerAudit);

//        SETEAR EL VIEWPAGER
        controllerDatos=new ControllerDatos(this);
        adapterPager=new AdapterPagerVerAudits(getSupportFragmentManager());
        adapterPager.setListaEses(controllerDatos.traerListaVerAudit());
        adapterPager.setUnaListaTitulos(controllerDatos.traerEses());
        pager.setAdapter(adapterPager);
        adapterPager.notifyDataSetChanged();


        //        SETEAR EL TABLAYOUT
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
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





    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }
}
