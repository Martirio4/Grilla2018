package com.nomad.mrg5s.View.Activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.nomad.mrg5s.DAO.ControllerDatos;
import com.nomad.mrg5s.Model.Area;
import com.nomad.mrg5s.Model.Auditoria;
import com.nomad.mrg5s.R;
import com.nomad.mrg5s.Utils.FuncionesPublicas;
import com.nomad.mrg5s.View.Adapter.AdapterPagerAudits;
import com.nomad.mrg5s.View.Fragments.FragmentBarrasApiladasPorArea;
import com.nomad.mrg5s.View.Fragments.FragmentMyAudits;
import com.nomad.mrg5s.View.Fragments.FragmentRanking;
import com.nomad.mrg5s.View.Fragments.FragmentRankingAreas;

import io.realm.Realm;
import io.realm.RealmResults;


public class ActivityMyAudits extends AppCompatActivity implements FragmentRankingAreas.Graficable, FragmentMyAudits.Graficable, FragmentRanking.Graficable {

    private ViewPager pager;
    private AdapterPagerAudits adapterPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_audits);
        pager= findViewById(R.id.viewPagerMyAudits);
        ControllerDatos controllerDatos = new ControllerDatos(this);
        adapterPager= new AdapterPagerAudits(getSupportFragmentManager(), controllerDatos.traerListaViewPager());
        pager.setAdapter(adapterPager);
        adapterPager.notifyDataSetChanged();

//        SETEAR EL TABLAYOUT
        TabLayout tabLayout = findViewById(R.id.tabLayoutMyAudits);
        tabLayout.setupWithViewPager(pager);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                pager.setCurrentItem(tab.getPosition(), true);
                adapterPager.updateAdapters();
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

        // Get a support ActionBar corresponding to this toolbar
        Toolbar toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.marfil));

        Typeface robotoR = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Regular.ttf");
        TextView unText=toolbar.findViewById(R.id.textoToolbar);
        unText.setTypeface(robotoR);
        unText.setTextColor(getResources().getColor(R.color.blancoNomad));
        unText.setText(getResources().getString(R.string.misAuditorias));

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public void onBackPressed() {

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentByTag(FuncionesPublicas.FRAGMENT_GRAFICO_AREA);
        if (fragment!=null && fragment.isVisible()){
            fragmentManager.popBackStack();
        }
        else{
            super.onBackPressed();
            this.finish();
        }

    }

    @Override
    public void GraficarAuditVieja(Auditoria unAuditoria) {
        Intent intent=new Intent(this, GraficosActivity.class);
        Bundle unBundle=new Bundle();
        unBundle.putString(GraficosActivity.AUDIT,unAuditoria.getIdAuditoria());
        unBundle.putString(GraficosActivity.ORIGEN, FuncionesPublicas.MIS_AUDITORIAS);
        unBundle.putString(GraficosActivity.AREA, unAuditoria.getAreaAuditada().getIdArea());
        intent.putExtras(unBundle);
        startActivity(intent);
        this.finish();
    }

    @Override
    public void graficarArea(Area unArea, String elOrigen) {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Auditoria> todasAudits = realm.where(Auditoria.class)
                .equalTo("areaAuditada.idArea",unArea.getIdArea())
                .sort("fechaAuditoria")
                .findAll();
        //SI EL AREA NO TIENE AUDITORIAS, NO CORRE EL METODO.
        if (todasAudits==null || todasAudits.size()<1) {
            Toast.makeText(this, getResources().getString(R.string.noHayAuditorias), Toast.LENGTH_SHORT).show();
        }
        else {
            FragmentBarrasApiladasPorArea fragmentBarrasApiladasPorArea = new FragmentBarrasApiladasPorArea();
            Bundle bundle= new Bundle();
            bundle.putString(FragmentBarrasApiladasPorArea.IDAREA, unArea.getIdArea());
            bundle.putString(FragmentBarrasApiladasPorArea.ORIGEN, elOrigen);
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentBarrasApiladasPorArea.setArguments(bundle);
            fragmentTransaction.replace(R.id.otroReferenta, fragmentBarrasApiladasPorArea,FuncionesPublicas.FRAGMENT_GRAFICO_AREA);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:

                FragmentManager fragmentManager = getSupportFragmentManager();
                Fragment fragment = fragmentManager.findFragmentByTag(FuncionesPublicas.FRAGMENT_GRAFICO_AREA);
                if (fragment!=null && fragment.isVisible()){
                    fragmentManager.popBackStack();
                }
                else{
                    return super.onOptionsItemSelected(item);
                }

                break;
        }
        return true;
    }
}
