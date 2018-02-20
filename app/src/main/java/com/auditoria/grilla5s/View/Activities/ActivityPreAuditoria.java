package com.auditoria.grilla5s.View.Activities;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.auditoria.grilla5s.DAO.ControllerDatos;
import com.auditoria.grilla5s.Model.Area;
import com.auditoria.grilla5s.Model.Auditoria;
import com.auditoria.grilla5s.Model.Item;
import com.auditoria.grilla5s.R;
import com.auditoria.grilla5s.View.Adapter.AdapterPagerEses;
import com.auditoria.grilla5s.View.Fragments.FragmentPreAudit;

import io.realm.Realm;
import io.realm.RealmResults;

public class ActivityPreAuditoria extends AppCompatActivity implements FragmentPreAudit.Auditable{

    private ControllerDatos controllerDatos;
    public static String idAudit;
    private String idArea;
    public static final String IDAREA="IDAREA";
    private ViewPager pager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre_auditoria);

        Intent intent=getIntent();
        Bundle bundle=intent.getExtras();
        if (bundle!=null) {
            idArea=bundle.getString(IDAREA);
        }
        else {
            Toast.makeText(this, getResources().getString(R.string.errorPruebeNuevamente), Toast.LENGTH_SHORT).show();
            return;
        }

//      INSTANCIO LA AUDITORIA Y LE CARGO EL AREA
       controllerDatos= new ControllerDatos(this);
       idAudit=controllerDatos.instanciarAuditoria();
       Realm realm = Realm.getDefaultInstance();
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

//       CARGO EL VIEWPAGER
        pager=findViewById(R.id.viewPagerPreAuditoria);
        controllerDatos=new ControllerDatos(this);
        AdapterPagerEses adapterPager=new AdapterPagerEses(getSupportFragmentManager());
        adapterPager.setUnaListaTitulos(controllerDatos.traerEses());
        pager.setAdapter(adapterPager);
        adapterPager.notifyDataSetChanged();

        //        SETEAR EL TABLAYOUT

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayoutPreAudit);
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

    }
}
