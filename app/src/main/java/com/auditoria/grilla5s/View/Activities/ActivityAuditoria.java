package com.auditoria.grilla5s.View.Activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.auditoria.grilla5s.DAO.ControllerDatos;

import com.auditoria.grilla5s.R;
import com.auditoria.grilla5s.View.Adapter.AdapterPagerPreguntas;
import com.auditoria.grilla5s.View.Adapter.AdapterPagerEses;
import com.auditoria.grilla5s.View.Fragments.FragmentPregunta;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetView;
import com.github.clans.fab.FloatingActionMenu;
import com.google.firebase.auth.FirebaseAuth;


import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;


public class ActivityAuditoria extends AppCompatActivity implements FragmentPregunta.Avisable{

    public static final String IDAUDITORIA ="IDAUDITORIA";
    public static final String IDAREA="IDAREA";

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private ViewPager pager;
    private AdapterPagerPreguntas adapterPager;
    public static String idAuditoria;
    public static String areaAuditada;
    private String resultadoInputFoto;
    private ControllerDatos controllerDatos;
    private FloatingActionMenu fabMenu;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auditoria);



        Intent intent= getIntent();
        Bundle bundle= intent.getExtras();

        areaAuditada=bundle.getString(IDAREA);
        instanciarNuevaAuditoria();

        drawerLayout = (DrawerLayout) findViewById(R.id.elDrawer);
        navigationView = (NavigationView) findViewById(R.id.naview);
        pager=(ViewPager)findViewById(R.id.viewPagerAuditoria);

//        SETEAR EL VIEWPAGER
        controllerDatos=new ControllerDatos(this);
        adapterPager=new AdapterPagerEses(getSupportFragmentManager());
        adapterPager.setListaSubItems(controllerDatos.traerSubItems());
        adapterPager.setUnaListaTitulos(controllerDatos.traerTitulos());
        pager.setAdapter(adapterPager);
        adapterPager.notifyDataSetChanged();

//        CAMBIAR LA FUENTE DE LOS TITUTLOS DEL DRAWER
       /* Menu menu = navigationView.getMenu();
        MenuItem tools= menu.findItem(R.id.titulo1s);
        SpannableString s = new SpannableString(tools.getTitle());
        s.setSpan(new TextAppearanceSpan(this, R.style.tituloNav), 0, s.length(), 0);
        tools.setTitle(s);

        MenuItem tools1= menu.findItem(R.id.titulo2s);
        SpannableString s1 = new SpannableString(tools1.getTitle());
        s1.setSpan(new TextAppearanceSpan(this, R.style.tituloNav), 0, s1.length(), 0);
        tools1.setTitle(s1);

        MenuItem tools2= menu.findItem(R.id.titulo3s);
        SpannableString s2 = new SpannableString(tools2.getTitle());
        s2.setSpan(new TextAppearanceSpan(this, R.style.tituloNav), 0, s2.length(), 0);
        tools2.setTitle(s2);
*/
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

        View headerLayout = navigationView.getHeaderView(0);

//        TOGGLE PARA EL BOTON HAMBURGUESA
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);
        actionBarDrawerToggle.getDrawerArrowDrawable().setColor(ContextCompat.getColor(this, R.color.marfil));


        drawerLayout.addDrawerListener(actionBarDrawerToggle);

//        ACCION DE LOS BOTONES DEL DRAWER
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                    switch (item.getItemId()) {
                        case R.id.item11:
                            pager.setCurrentItem(0);
                            break;
                        case R.id.item12:
                            pager.setCurrentItem(1);
                            break;
                        case R.id.item13:
                            pager.setCurrentItem(2);
                            break;
                        case R.id.item14:
                            pager.setCurrentItem(3);
                            break;
                        case R.id.item21:
                            pager.setCurrentItem(4);
                            break;
                        case R.id.item22:
                            pager.setCurrentItem(5);
                            break;
                        case R.id.item23:
                            pager.setCurrentItem(6);
                            break;
                        case R.id.item24:
                            pager.setCurrentItem(7);
                            break;
                        case R.id.item31:
                            pager.setCurrentItem(8);
                            break;
                        case R.id.item32:
                            pager.setCurrentItem(9);
                            break;
                        case R.id.item33:
                            pager.setCurrentItem(10);
                            break;
                        case R.id.item34:
                            pager.setCurrentItem(11);
                            break;
                        case R.id.item41:
                            pager.setCurrentItem(12);
                            break;
                        case R.id.item42:
                            pager.setCurrentItem(13);
                            break;
                        case R.id.item43:
                            pager.setCurrentItem(14);
                            break;
                        case R.id.item44:
                            pager.setCurrentItem(15);
                            break;
                        case R.id.item51:
                            pager.setCurrentItem(16);
                            break;
                        case R.id.item52:
                            pager.setCurrentItem(17);
                            break;
                        case R.id.item53:
                            pager.setCurrentItem(18);
                            break;
                        case R.id.item54:
                            pager.setCurrentItem(19);
                            break;


                    }

                drawerLayout.closeDrawers();
                return true;
            }
        });
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




        //updateTabTextColors();


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
        actionBarDrawerToggle.syncState();
    }



    public void instanciarNuevaAuditoria(){
//   CREAR UNA AUDITORIA NUEVA
        final Auditoria nuevaAuditoria=new Auditoria();
        Realm realm= Realm.getDefaultInstance();
        nuevaAuditoria.setIdAuditoria("AUDIT-"+UUID.randomUUID());
        idAuditoria=nuevaAuditoria.getIdAuditoria();
        nuevaAuditoria.setFechaAuditoria(determinarFecha());
        nuevaAuditoria.setUsuario(FirebaseAuth.getInstance().getCurrentUser().getEmail());


        Area mAreaAuditada = realm.where(Area.class)
                .equalTo("idArea", areaAuditada)
                .findFirst();
        nuevaAuditoria.setAreaAuditada(mAreaAuditada);

        //ESPACIO PARA GUARDAR EL USUARIO DE LA DATABASE
        //ESPACIO PARA GUARDAR EL USUARIO DE LA DATABASE
        //ESPACIO PARA GUARDAR EL USUARIO DE LA DATABASE
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
               Auditoria auditoriaRealm= realm.copyToRealmOrUpdate(nuevaAuditoria);
                updateSubItems(realm, auditoriaRealm);
            }
        });

    }
    public String determinarFecha(){
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        String date=sdf.format(cal.getTime());
        return date;
    }

    @Override
    public void cerrarAuditoria() {

        Intent intent=new Intent(this, GraficosActivity.class);
        Bundle bundle=new Bundle();
        bundle.putString(GraficosActivity.AUDIT, idAuditoria);
        bundle.putString(GraficosActivity.ORIGEN, "auditoria");
        intent.putExtras(bundle);
        startActivity(intent);
        this.finish();

    }

    public void calcularPuntajeAuditoria(){

    }

    @Override
    public void salirDeAca() {
        Intent intent= new Intent(this, ActivityLanding.class);
        startActivity(intent);
        this.finish();
    }

    public void pedirComment(){

        new MaterialDialog.Builder(this)
                .title(getResources().getString(R.string.agregarComentario))
                .content(getResources().getString(R.string.favorAgregueComentario))
                .inputType(InputType.TYPE_CLASS_TEXT)
                .input(getResources().getString(R.string.comment),"", new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {
                        resultadoInputFoto=input.toString();
                        entregarString();
                    }
                }).show();
    }

    private String entregarString() {
        return resultadoInputFoto;
    }

    public void updateSubItems(Realm realm, final Auditoria unAudit){
        ControllerDatos controler= new ControllerDatos(this);
        RealmList<SubItem> unaListaDummie=controler.traerSubItems();

        for (final SubItem sub:unaListaDummie
                ) {
            sub.setPertenencia(idAuditoria+sub.getId());
            sub.setAuditoria(idAuditoria);
            SubItem subItemSubidoARealm = realm.copyToRealmOrUpdate(sub);
            unAudit.getSubItems().add(subItemSubidoARealm);

        }
    }

    @Override
    public void onBackPressed() {
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

                        Realm realm = Realm.getDefaultInstance();

                        realm.executeTransaction(new Realm.Transaction() {
                             @Override
                             public void execute(Realm realm) {

                                 String usuario = FirebaseAuth.getInstance().getCurrentUser().getEmail();

                                 RealmResults<SubItem> Subitems = realm.where(SubItem.class)
                                         .equalTo("auditoria", idAuditoria)
                                         .findAll();
                                 Subitems.deleteAllFromRealm();

                                 RealmResults<Foto> fotos = realm.where(Foto.class)
                                         .equalTo("auditoria", idAuditoria)
                                         .findAll();
                                 for (Foto foti : fotos
                                         ) {
                                     File file = new File(foti.getRutaFoto());
                                     boolean deleted = file.delete();
                                 }
                                 fotos.deleteAllFromRealm();

                                 Auditoria result2 = realm.where(Auditoria.class)
                                         .equalTo("idAuditoria", idAuditoria)
                                         .findFirst();

                                 result2.deleteFromRealm();
                             }
                         });


                        ActivityAuditoria.this.finish();
                        ActivityAuditoria.super.onBackPressed();
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

    public void cerrarSinGuardar(){



    }

}
