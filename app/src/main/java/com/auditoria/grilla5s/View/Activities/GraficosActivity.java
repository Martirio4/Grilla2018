package com.auditoria.grilla5s.View.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.auditoria.grilla5s.Model.Area;
import com.auditoria.grilla5s.Model.Auditoria;
import com.auditoria.grilla5s.Model.Ese;
import com.auditoria.grilla5s.Model.Foto;
import com.auditoria.grilla5s.Model.Item;
import com.auditoria.grilla5s.Model.Pregunta;
import com.auditoria.grilla5s.R;
import com.auditoria.grilla5s.Utils.FuncionesPublicas;
import com.auditoria.grilla5s.View.Fragments.FragmentBarrasApiladas;
import com.auditoria.grilla5s.View.Fragments.FragmentRadar;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import crl.android.pdfwriter.PDFWriter;
import crl.android.pdfwriter.PaperSize;
import crl.android.pdfwriter.StandardFonts;
import io.realm.Realm;
import io.realm.RealmResults;
import pl.tajchert.nammu.Nammu;

public class GraficosActivity extends AppCompatActivity {

    public static final String AUDIT = "AUDIT";
    public static final String AREA = "AREA";
    public static final String ORIGEN = "ORIGEN";

    private String origenIntent;
    private String idAudit;
    private String areaAuditada;
    private Double promedioSeiri;
    private Double promedioSeiton;
    private Double promedioSeiso;
    private Double promedio5s;

    private FloatingActionMenu fabMenuGraficos;
    private FloatingActionButton fabGenerarPDF;
    private FloatingActionButton fabQuit;
    private FloatingActionButton fabVerAuditoria;
    private FloatingActionButton fabBorrarAuditoria;
    private FloatingActionButton fabEditarAuditoria;
    Boolean auditEstaCompleta;


    private ProgressBar progressBar;
    private Double promedioSeiketsu;
    private Double promedioShitsuke;

    private PDFWriter writer;

    private File fotoComprimida;
    private Bitmap fotoOriginal;


    public static final int MARGEN_IZQUIERDO = 30;
    public static final int SALTO_LINEA = 18;
    public static final int SEPARACIONFOTOS = 9;

    private DatabaseReference mDatabase;

    private SharedPreferences config;

    private Integer sumatoriaPreguntas;
    private Integer divisorPreguntas;

    private Double sumatoriaItems;
    private Integer divisorItems;

    private Double sumatoriaEse;
    private Integer divisorEse;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graficos);

        config = getSharedPreferences("prefs", 0);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        promedioSeiso = 0.0;
        promedioSeiton = 0.0;
        promedioSeiri = 0.0;
        promedio5s = 0.0;

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        if (bundle!=null) {
            idAudit = bundle.getString(AUDIT);
            origenIntent = bundle.getString(ORIGEN);
        }

        //--guardar auditoria en firebase--//


        FragmentActivity unaActivity = this;
        FragmentManager fragmentManager = (FragmentManager) unaActivity.getSupportFragmentManager();
        FragmentRadar fragmentRadar = (FragmentRadar) fragmentManager.findFragmentByTag("radar");

        if (fragmentRadar != null && fragmentRadar.isVisible()) {
        }
        else {
            cargarGraficoRadar();
            cargarGraficoBarras();
        }


        fabMenuGraficos = (FloatingActionMenu) findViewById(R.id.menuSalida);

        fabMenuGraficos.setMenuButtonColorNormal(ContextCompat.getColor(this, R.color.colorAccent));


        fabEditarAuditoria=new FloatingActionButton(this);
        fabEditarAuditoria.setColorNormal(ContextCompat.getColor(this, R.color.tutorial1));
        fabEditarAuditoria.setButtonSize(FloatingActionButton.SIZE_MINI);
        fabEditarAuditoria.setLabelText(getResources().getString(R.string.editarAuditoria));
        fabEditarAuditoria.setImageResource(R.drawable.ic_edit_black_24dp);
        fabMenuGraficos.addMenuButton(fabEditarAuditoria);

        fabEditarAuditoria.setLabelColors(ContextCompat.getColor(this, R.color.primary_text),
                ContextCompat.getColor(this, R.color.light_grey),
                ContextCompat.getColor(this, R.color.white_transparent));
        fabEditarAuditoria.setLabelTextColor(ContextCompat.getColor(this, R.color.tutorial1));

        fabEditarAuditoria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Realm realm = Realm.getDefaultInstance();
                Auditoria mAudit=realm.where(Auditoria.class)
                        .equalTo("idAuditoria",idAudit)
                        .findFirst();
                if (mAudit==null || !mAudit.getAuditEstaCerrada()) {
                    fabMenuGraficos.close(true);
                    editarAuditoria(idAudit);
                }
                else{
                    Snackbar.make(fabEditarAuditoria,getResources().getString(R.string.auditCerradaNoPuedeEditar),Snackbar.LENGTH_SHORT)
                            .setAction("Ok", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                }
                            })
                            .show();
                }

            }
        });










        fabVerAuditoria = new FloatingActionButton(this);
        fabVerAuditoria.setColorNormal(ContextCompat.getColor(this, R.color.tutorial1));
        fabVerAuditoria.setButtonSize(FloatingActionButton.SIZE_MINI);
        fabVerAuditoria.setLabelText(getResources().getString(R.string.verAuditoria));
        fabVerAuditoria.setImageResource(R.drawable.ic_find_in_page_black_24dp);
        fabMenuGraficos.addMenuButton(fabVerAuditoria);

        fabVerAuditoria.setLabelColors(ContextCompat.getColor(this, R.color.primary_text),
                ContextCompat.getColor(this, R.color.light_grey),
                ContextCompat.getColor(this, R.color.white_transparent));
        fabVerAuditoria.setLabelTextColor(ContextCompat.getColor(this, R.color.tutorial1));

        fabVerAuditoria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fabMenuGraficos.close(true);
                Intent inten = new Intent(v.getContext(), ActivityVerAuditorias.class);
                Bundle bundle123 = new Bundle();
                bundle123.putString(ActivityVerAuditorias.AUDITORIA, idAudit);
                inten.putExtras(bundle123);
                startActivity(inten);

            }
        });

        fabGenerarPDF = new FloatingActionButton(this);
        fabGenerarPDF.setColorNormal(ContextCompat.getColor(this, R.color.tutorial1));
        fabGenerarPDF.setButtonSize(FloatingActionButton.SIZE_MINI);
        fabGenerarPDF.setLabelText(getString(R.string.generarPDF));
        fabGenerarPDF.setImageResource(R.drawable.ic_insert_drive_file_black_24dp);
        fabMenuGraficos.addMenuButton(fabGenerarPDF);

        fabGenerarPDF.setLabelColors(ContextCompat.getColor(this, R.color.primary_text),
                ContextCompat.getColor(this, R.color.light_grey),
                ContextCompat.getColor(this, R.color.white_transparent));
        fabGenerarPDF.setLabelTextColor(ContextCompat.getColor(this, R.color.tutorial1));

        fabGenerarPDF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (FuncionesPublicas.isExternalStorageWritable()) {
                    fabMenuGraficos.close(true);
                    new EnviarPDF().execute();
                } else {
                    new MaterialDialog.Builder(v.getContext())
                            .title(getResources().getString(R.string.titNoMemoria))
                            .contentColor(ContextCompat.getColor(v.getContext(), R.color.primary_text))
                            .backgroundColor(ContextCompat.getColor(v.getContext(), R.color.tile1))
                            .positiveText(getResources().getString(R.string.ok))
                            .titleColor(ContextCompat.getColor(v.getContext(), R.color.tile4))
                            .content(getResources().getString(R.string.noMemoria))
                            .show();
                }
            }
        });


        fabBorrarAuditoria = new FloatingActionButton(this);
        fabBorrarAuditoria.setColorNormal(ContextCompat.getColor(this, R.color.semaRojo));
        fabBorrarAuditoria.setButtonSize(FloatingActionButton.SIZE_MINI);
        fabBorrarAuditoria.setLabelText(getString(R.string.deleteAudit));
        fabBorrarAuditoria.setImageResource(R.drawable.ic_delete_forever_black_24dp);
        fabMenuGraficos.addMenuButton(fabBorrarAuditoria);

        fabBorrarAuditoria.setLabelColors(ContextCompat.getColor(this, R.color.primary_text),
                ContextCompat.getColor(this, R.color.light_grey),
                ContextCompat.getColor(this, R.color.white_transparent));
        fabBorrarAuditoria.setLabelTextColor(ContextCompat.getColor(this, R.color.semaRojo));

        fabBorrarAuditoria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                fabMenuGraficos.close(true);


                new MaterialDialog.Builder(v.getContext())
                        .title("Warning!")
                        .title(getResources().getString(R.string.advertencia))
                        .contentColor(ContextCompat.getColor(v.getContext(), R.color.primary_text))
                        .titleColor(ContextCompat.getColor(v.getContext(), R.color.tile4))
                        .backgroundColor(ContextCompat.getColor(v.getContext(), R.color.tile1))
                        .content(getResources().getString(R.string.auditoriaSeEliminara) + "\n" + getResources().getString(R.string.continuar))
                        .positiveText(getResources().getString(R.string.si))
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                if (FuncionesPublicas.borrarAuditoriaSeleccionada(idAudit)) {
                                    Intent intent = new Intent(GraficosActivity.this, ActivityMyAudits.class);
                                    startActivity(intent);
                                    GraficosActivity.this.finish();
                                }
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
        });

        fabQuit = new FloatingActionButton(this);
        fabQuit.setColorNormal(ContextCompat.getColor(this, R.color.tutorial1));
        fabQuit.setButtonSize(FloatingActionButton.SIZE_MINI);
        fabQuit.setLabelText(getString(R.string.quit));
        fabQuit.setImageResource(R.drawable.ic_exit_to_app_black_24dp);
        fabMenuGraficos.addMenuButton(fabQuit);

        fabQuit.setLabelColors(ContextCompat.getColor(this, R.color.primary_text),
                ContextCompat.getColor(this, R.color.light_grey),
                ContextCompat.getColor(this, R.color.white_transparent));
        fabQuit.setLabelTextColor(ContextCompat.getColor(this, R.color.tutorial1));

        fabQuit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fabMenuGraficos.close(true);
                //metodo rating
                metodoRating();
            }
        });


        boolean quiereVerTuto = config.getBoolean("quiereVerTuto", false);
        boolean primeraVezFragmentRadar = config.getBoolean("primeraVezFragmentRadar", false);

        //SI EL USUARIO ELIGIO VER TUTORIALES ME FIJO SI YA PASO POR ESTA PAGINA.
        if (quiereVerTuto) {
            if (!primeraVezFragmentRadar) {

                SharedPreferences.Editor editor = config.edit();


                editor.putBoolean("primeraVezFragmentRadar", true);
                editor.commit();

                seguirConTutorial();
            }
        }

    }

    public void editarAuditoria(String idAudit) {

        Intent intent = new Intent(this, ActivityPreAuditoria.class);
        Bundle bundle = new Bundle();
        bundle.putString(ActivityPreAuditoria.IDAREA, "NULL");
        bundle.putString(ActivityPreAuditoria.ORIGEN, "EDITAR_AUDITORIA");
        bundle.putString(ActivityPreAuditoria.IDAUDIT, idAudit);

        intent.putExtras(bundle);
        GraficosActivity.this.finish();
        startActivity(intent);
        FragmentManager fragmentManager =  this.getSupportFragmentManager();
        fragmentManager.popBackStack();

    }

    private void seguirConTutorial() {
        fabMenuGraficos.open(true);
        Typeface roboto = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Light.ttf");

        new TapTargetSequence(this)
                .targets(
                        TapTarget.forView(fabVerAuditoria, getResources().getString(R.string.tutorial_tit_graficos_ver), getResources().getString(R.string.tutorial_desc_graficos_ver))
                                .outerCircleColor(R.color.tutorial2)      // Specify a color for the outer circle
                                .outerCircleAlpha(0.85f)            // Specify the alpha amount for the outer circle
                                .textTypeface(roboto)  // Specify a typeface for the text
                                .drawShadow(true)                   // Whether to draw a drop shadow or not
                                .cancelable(false)
                                .id(1)// Whether tapping outside the outer circle dismisses the view
                                .tintTarget(false),                   // Whether to tint the target view's color
                        TapTarget.forView(fabGenerarPDF, getResources().getString(R.string.tutorial_tit_graficos_pdf), getResources().getString(R.string.tutorial_desc_graficos_pdf))
                                .outerCircleColor(R.color.tutorial1)      // Specify a color for the outer circle
                                .textColor(R.color.primary_text)// Specify the alpha amount for the outer circle
                                .outerCircleAlpha(0.95f)
                                .textTypeface(roboto)  // Specify a typeface for the text
                                .drawShadow(true)                   // Whether to draw a drop shadow or not
                                .cancelable(true)
                                .id(2)// Whether tapping outside the outer circle dismisses the view
                                .tintTarget(false))                 // Whether to tint the target view's color

                .listener(new TapTargetSequence.Listener() {
                    // This listener will tell us when interesting(tm) events happen in regards
                    // to the sequence
                    @Override
                    public void onSequenceFinish() {
                        fabMenuGraficos.close(true);

                        SharedPreferences config = config = getSharedPreferences("prefs", 0);
                        Boolean seTermino = config.getBoolean("estadoTuto", false);
                        if (!seTermino) {
                            SharedPreferences.Editor editor = config.edit();
                            editor.putBoolean("estadoTuto", true);
                            editor.commit();
                        }
                    }

                    @Override
                    public void onSequenceStep(TapTarget tapTarget, boolean b) {

                    }

                    @Override
                    public void onSequenceCanceled(TapTarget lastTarget) {
                        // Boo
                    }
                })
                .start();
    }


    public void cargarGraficoRadar() {

        calcularPuntajesAuditoria();

        Realm realm = Realm.getDefaultInstance();
        Auditoria laAudit=realm.where(Auditoria.class)
                .equalTo("idAuditoria",idAudit)
                .findFirst();

        if (laAudit!=null){
            promedioSeiri = laAudit.getListaEses().get(0).getPuntajeEse();
            promedioSeiton = laAudit.getListaEses().get(1).getPuntajeEse();
            promedioSeiso = laAudit.getListaEses().get(2).getPuntajeEse();
            promedioSeiketsu = laAudit.getListaEses().get(3).getPuntajeEse();
            promedioShitsuke = laAudit.getListaEses().get(4).getPuntajeEse();
        }

        FragmentRadar graficoFragment = new FragmentRadar();
        Bundle bundle = new Bundle();
        bundle.putDouble(FragmentRadar.PUNJTAJE1, promedioSeiri);
        bundle.putDouble(FragmentRadar.PUNJTAJE2, promedioSeiton);
        bundle.putDouble(FragmentRadar.PUNJTAJE3, promedioSeiso);
        bundle.putDouble(FragmentRadar.PUNJTAJE4, promedioSeiketsu);
        bundle.putDouble(FragmentRadar.PUNJTAJE5, promedioShitsuke);
        bundle.putString(FragmentRadar.AREA, laAudit.getAreaAuditada().getNombreArea());

        graficoFragment.setArguments(bundle);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.contenedorGraficos, graficoFragment, "radar");
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void calcularPuntajesAuditoria() {
        Realm realm =Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Auditoria mAudit= realm.where(Auditoria.class)
                        .equalTo("idAuditoria",idAudit)
                        .findFirst();

                if (mAudit!=null) {
                    sumatoriaEse=0.0;
                    divisorEse=0;
                    for (Ese unaEse:mAudit.getListaEses())
                    {
                        sumatoriaItems=0.0;
                        divisorItems=0;
                        for (Item unItem:unaEse.getListaItem())
                        {
                            sumatoriaPreguntas =0;
                            divisorPreguntas =0;
                            for (Pregunta unaPregunta:unItem.getListaPreguntas())
                            {
                                if (unaPregunta.getPuntaje()!=null) {
                                    sumatoriaPreguntas = sumatoriaPreguntas +unaPregunta.getPuntaje();
                                }
                                else{
                                    auditEstaCompleta=false;
                                }
                                divisorPreguntas++;
                            }
                            if (divisorPreguntas==0) {
                                unItem.setPuntajeItem(0.0);
                            }
                            else {
                                unItem.setPuntajeItem((sumatoriaPreguntas / divisorPreguntas)*1.0);
                            }
                            sumatoriaItems=sumatoriaItems+unItem.getPuntajeItem();
                            divisorItems++;
                        }
                        unaEse.setPuntajeEse((sumatoriaItems/divisorItems));
                        sumatoriaEse=sumatoriaEse+unaEse.getPuntajeEse();
                        divisorEse++;
                    }
                    //DIVIDO POR 25 QUE ES EL 100%
                    mAudit.setPuntajeFinal(sumatoriaEse/25);
                }

            }
        });


    }

    public void cargarGraficoBarras() {
        FragmentBarrasApiladas fragmentBarrasApiladas = new FragmentBarrasApiladas();

        Bundle bundle = new Bundle();

        Realm realm = Realm.getDefaultInstance();
        Auditoria laAudit = realm.where(Auditoria.class)
                .equalTo("idAuditoria", idAudit)
                .findFirst();

        if (laAudit != null) {

            bundle.putDouble(FragmentBarrasApiladas.PUNTAJE_AUDITORIA, laAudit.getPuntajeFinal());
        }

        fragmentBarrasApiladas.setArguments(bundle);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.contenedorGraficos, fragmentBarrasApiladas, "barras");
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }



    public void irALanding(){
        Intent intent = new Intent(this, LandingActivity.class);
        startActivity(intent);
        GraficosActivity.this.finish();
    }



    /*
    public void enviarePDF(){

        writer = new PDFWriter(PaperSize.LETTER_WIDTH, PaperSize.LETTER_HEIGHT);
        ControllerDatos controllerDatos=new ControllerDatos(this);
        Realm realm= Realm.getDefaultInstance();
        Auditoria mAudit= realm.where(Auditoria.class)
                .equalTo("idAudit",idAudit)
                .findFirst();

        
        List<String>alistaSeiri=controllerDatos.traerSeiri();
        List<String>alistaSeiton=controllerDatos.traerSeiton();
        List<String>alistaSeiso=controllerDatos.traerSeiso();
        List<String>alistaSeiketsu=controllerDatos.traerSeiketsu();
        List<String>alistaShitsuke=controllerDatos.traerShitsuke();
        
        List<SubItem>unListaSeiri=new ArrayList<>();
        List<SubItem>unListaSeiton=new ArrayList<>();
        List<SubItem>unListaSeiso=new ArrayList<>();
        List<SubItem>unListaSeiketsu=new ArrayList<>();
        List<SubItem>unListaShitsuke=new ArrayList<>();
        
        for (SubItem sub:mAudit.getSubItems()
             ) {
            if (alistaSeiri.contains(sub.getId())){
                unListaSeiri.add(sub);
            }
            if (alistaSeiton.contains(sub.getId())){
                unListaSeiton.add(sub);
            }
            if (alistaSeiso.contains(sub.getId())){
                unListaSeiso.add(sub);
            }
            if (alistaSeiketsu.contains(sub.getId())){
                unListaSeiketsu.add(sub);
            }
            if (alistaShitsuke.contains(sub.getId())){
                unListaShitsuke.add(sub);
            }
        }
        //fuente titulo
        writer.setFont(StandardFonts.SUBTYPE, StandardFonts.HELVETICA, StandardFonts.WIN_ANSI_ENCODING);
        //escribir titulo
        writer.addText(MARGEN_IZQUIERDO,PaperSize.LETTER_HEIGHT - MARGEN_IZQUIERDO,20,"5S Audit Report");
        //fuente fecha escribir fecga
        writer.addText(MARGEN_IZQUIERDO, PaperSize.LETTER_HEIGHT-(75),12,"Date: "+mAudit.getFechaAuditoria());


        armarPagina(unListaSeiri);
        writer.newPage();
        armarPagina(unListaSeiton);
        writer.newPage();
        armarPagina(unListaSeiso);
        writer.newPage();
        armarPagina(unListaSeiketsu);
        writer.newPage();
        armarPagina(unListaShitsuke);
        writer.newPage();
        cargarGraficos(mAudit);

        outputToFile("5S Report-"+mAudit.getAreaAuditada().getNombreArea()+"-"+mAudit.getFechaAuditoria()+".pdf", writer.asString(), "ISO-8859-1");
    }
    */


    private class EnviarPDF extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... args) {
            enviarPDF();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            progressBar.setVisibility(View.INVISIBLE);
            super.onPostExecute(result);
        }

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            super.onPreExecute();

        }
    }



    /*
    public void armarPagina(List<SubItem> unaLista){

        Integer inicioFotos=85;
        Integer altoImagen=120;

        writer.setFont(StandardFonts.SUBTYPE, StandardFonts.HELVETICA, StandardFonts.WIN_ANSI_ENCODING);
        writer.addText(PaperSize.LETTER_WIDTH-4*MARGEN_IZQUIERDO, PaperSize.LETTER_HEIGHT-(75),12,unaLista.get(0).getaQuePertenece());

        //linea separacion
        writer.addLine(MARGEN_IZQUIERDO,PaperSize.LETTER_HEIGHT-(85),PaperSize.LETTER_WIDTH-MARGEN_IZQUIERDO,PaperSize.LETTER_HEIGHT-(85));
        
        //agrego primer subitem
        writer.addText(MARGEN_IZQUIERDO,PaperSize.LETTER_HEIGHT-(inicioFotos+SALTO_LINEA),12,unaLista.get(0).getEnunciado());
        writer.addText(MARGEN_IZQUIERDO,PaperSize.LETTER_HEIGHT-(inicioFotos+SALTO_LINEA+12),12,"Score: "+unaLista.get(0).getPuntuacion1().toString());
        Integer cantidadFotos=unaLista.get(0).getListaFotos().size();
        if (cantidadFotos>3){
            cantidadFotos=3;
        }

        for (int i=0;i<cantidadFotos;i++){

            Foto unaFoto=unaLista.get(0).getListaFotos().get(i);
            File unFile= new File(unaFoto.getRutaFoto());
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            Bitmap bitmap = BitmapFactory.decodeFile(unFile.getAbsolutePath(),bmOptions);
            Bitmap bitmapScaled= Bitmap.createScaledBitmap(bitmap,120,120,false);
            writer.addImage(MARGEN_IZQUIERDO+(i*145),PaperSize.LETTER_HEIGHT-(inicioFotos+SALTO_LINEA*2+altoImagen),bitmapScaled);
            writer.addText(MARGEN_IZQUIERDO+(i*145),PaperSize.LETTER_HEIGHT-(inicioFotos+SALTO_LINEA*2+altoImagen+10),10,unaFoto.getComentario());

        }
        //agrego segundo subitem
        writer.addText(MARGEN_IZQUIERDO,PaperSize.LETTER_HEIGHT-(inicioFotos+SALTO_LINEA*3+altoImagen),12,unaLista.get(1).getEnunciado());
        writer.addText(MARGEN_IZQUIERDO,PaperSize.LETTER_HEIGHT-(inicioFotos+SALTO_LINEA*3+altoImagen+12),12,"Score: "+unaLista.get(1).getPuntuacion1().toString());
        cantidadFotos=unaLista.get(1).getListaFotos().size();
        if (cantidadFotos>3){
            cantidadFotos=3;
        }

        for (int i=0;i<cantidadFotos;i++){

            Foto unaFoto=unaLista.get(1).getListaFotos().get(i);
            File unFile= new File(unaFoto.getRutaFoto());
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            Bitmap bitmap = BitmapFactory.decodeFile(unFile.getAbsolutePath(),bmOptions);
            Bitmap bitmapScaled= Bitmap.createScaledBitmap(bitmap,120,120,false);
            writer.addImage(MARGEN_IZQUIERDO+(i*145),PaperSize.LETTER_HEIGHT-(inicioFotos+SALTO_LINEA*4+altoImagen*2),bitmapScaled);
            writer.addText(MARGEN_IZQUIERDO+(i*145),PaperSize.LETTER_HEIGHT-(inicioFotos+SALTO_LINEA*4+altoImagen*2+10),10,unaFoto.getComentario());

        }
        //agrego tercer subitem
        writer.addText(MARGEN_IZQUIERDO,PaperSize.LETTER_HEIGHT-(inicioFotos+SALTO_LINEA*5+altoImagen*2),12,unaLista.get(2).getEnunciado());
        writer.addText(MARGEN_IZQUIERDO,PaperSize.LETTER_HEIGHT-(inicioFotos+SALTO_LINEA*5+altoImagen*2+12),12,"Score: "+unaLista.get(2).getPuntuacion1().toString());
        cantidadFotos=unaLista.get(2).getListaFotos().size();
        if (cantidadFotos>3){
            cantidadFotos=3;
        }

        for (int i=0;i<cantidadFotos;i++){

            Foto unaFoto=unaLista.get(2).getListaFotos().get(i);
            File unFile= new File(unaFoto.getRutaFoto());
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            Bitmap bitmap = BitmapFactory.decodeFile(unFile.getAbsolutePath(),bmOptions);
            Bitmap bitmapScaled= Bitmap.createScaledBitmap(bitmap,120,120,false);
            writer.addImage(MARGEN_IZQUIERDO+(i*145),PaperSize.LETTER_HEIGHT-(inicioFotos+SALTO_LINEA*6+altoImagen*3),bitmapScaled);
            writer.addText(MARGEN_IZQUIERDO+(i*145),PaperSize.LETTER_HEIGHT-(inicioFotos+SALTO_LINEA*6+altoImagen*3+10),10,unaFoto.getComentario());

        }
        //agrego cuarto subitem
        writer.addText(MARGEN_IZQUIERDO,PaperSize.LETTER_HEIGHT-(inicioFotos+SALTO_LINEA*7+altoImagen*3),12,unaLista.get(3).getEnunciado());
        writer.addText(MARGEN_IZQUIERDO,PaperSize.LETTER_HEIGHT-(inicioFotos+SALTO_LINEA*7+altoImagen*3+12),12,"Score: "+unaLista.get(3).getPuntuacion1().toString());
        cantidadFotos=unaLista.get(3).getListaFotos().size();
        if (cantidadFotos>3){
            cantidadFotos=3;
        }

        for (int i=0;i<cantidadFotos;i++){

            Foto unaFoto=unaLista.get(3).getListaFotos().get(i);
            File unFile= new File(unaFoto.getRutaFoto());
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            Bitmap bitmap = BitmapFactory.decodeFile(unFile.getAbsolutePath(),bmOptions);
            Bitmap bitmapScaled= Bitmap.createScaledBitmap(bitmap,120,120,false);
            writer.addImage(MARGEN_IZQUIERDO+(i*145),PaperSize.LETTER_HEIGHT-(inicioFotos+SALTO_LINEA*8+altoImagen*4),bitmapScaled);
            writer.addText(MARGEN_IZQUIERDO+(i*145),PaperSize.LETTER_HEIGHT-(inicioFotos+SALTO_LINEA*8+altoImagen*4+10),10,unaFoto.getComentario());

        }
    }
    */
    public void outputToFile(String fileName, String pdfContent, String encoding) {
        if (existeDirectorio()) {

            File newFile = new File(getExternalFilesDir(null) + File.separator + "nomad" + File.separator + "audit5s" + File.separator + FirebaseAuth.getInstance().getCurrentUser().getEmail() + File.separator + "audits" + File.separator + fileName);

            try {
                FileOutputStream pdfFile = new FileOutputStream(newFile);
                pdfFile.write(pdfContent.getBytes(encoding));
                pdfFile.close();
                Uri path = FileProvider.getUriForFile(this, this.getApplicationContext().getPackageName() + ".my.package.name.provider", newFile);

                //Uri path = Uri.fromFile(newFile);
                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                // set the type to 'email'
                emailIntent.setType("vnd.android.cursor.dir/email");
                String to[] = {""};
                emailIntent.putExtra(Intent.EXTRA_EMAIL, to);
                // the attachment
                emailIntent.putExtra(Intent.EXTRA_STREAM, path);
                // the mail subject
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.reporteAuditoria));
                startActivity(Intent.createChooser(emailIntent, getResources().getString(R.string.enviarMail)));


            } catch (IOException e) {
                Log.e("NOMAD/ERROR", "exception", e);
            }
        } else {
            Toast.makeText(this, getResources().getString(R.string.errorRevisarPermisos), Toast.LENGTH_SHORT).show();
        }
    }

    private Boolean existeDirectorio() {
        Boolean hayPermit = Nammu.checkPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        Boolean sePudo = true;
        File dir = new File(getExternalFilesDir(null) + File.separator + "nomad" + File.separator + "audit5s" + File.separator + FirebaseAuth.getInstance().getCurrentUser().getEmail() + File.separator + "audits");
        if (!dir.exists() || !dir.isDirectory()) {
            sePudo = dir.mkdirs();
        }
        return sePudo;
    }

    public Bitmap screenShot(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(),
                view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }

    public void cargarGraficos(Auditoria unAudit) {
        writer.newPage();

        int cursorX = MARGEN_IZQUIERDO;
        int cursorY = 792;
        //fuente titulo
        writer.setFont(StandardFonts.SUBTYPE, StandardFonts.HELVETICA, StandardFonts.WIN_ANSI_ENCODING);
        //escribir titulo
        writer.addText(cursorX, PaperSize.LETTER_HEIGHT - MARGEN_IZQUIERDO, 20, getResources().getString(R.string.tituloPdf));
        cursorY = cursorY - MARGEN_IZQUIERDO;
        cursorX = cursorX + MARGEN_IZQUIERDO;
        //fuente fecha escribir fecga

        cursorY = cursorY - SALTO_LINEA;


        //linea separacion
        writer.addLine(MARGEN_IZQUIERDO, PaperSize.LETTER_HEIGHT - (65), PaperSize.LETTER_WIDTH - MARGEN_IZQUIERDO, PaperSize.LETTER_HEIGHT - (65));
        cursorY = PaperSize.LETTER_HEIGHT - 65;
        cursorY = cursorY - SALTO_LINEA;
        cursorX = MARGEN_IZQUIERDO;


        writer.setFont(StandardFonts.SUBTYPE, StandardFonts.HELVETICA, StandardFonts.WIN_ANSI_ENCODING);
        writer.addText(PaperSize.LETTER_WIDTH - 4 * MARGEN_IZQUIERDO, PaperSize.LETTER_HEIGHT - (75), 12, getResources().getString(R.string.ResultadoFinal));


        //agrego puntaje final
        Locale locale = new Locale("en", "US");
        NumberFormat format = NumberFormat.getPercentInstance(locale);
        String percentage1 = format.format(unAudit.getPuntajeFinal());
        writer.addText(MARGEN_IZQUIERDO, PaperSize.LETTER_HEIGHT - (85 + SALTO_LINEA), 12, getResources().getString(R.string.puntajeFinal) + percentage1);

        View rootView = this.getWindow().getDecorView().findViewById(android.R.id.content);
        View v = rootView.findViewById(R.id.contenedorGraficos);

        Bitmap unBitmap = screenShot(v);
        Bitmap scaledBitmap = scaleSinRotar(unBitmap, 250);
        // Bitmap SunBitmap=Bitmap.createScaledBitmap(unBitmap, 300,510,false);
        writer.addImage(MARGEN_IZQUIERDO, PaperSize.LETTER_HEIGHT - 85 - SALTO_LINEA - scaledBitmap.getHeight() - SEPARACIONFOTOS, scaledBitmap);

    }


    public void enviarPDF() {

        Realm realm = Realm.getDefaultInstance();
        RealmResults<Pregunta> preguntasSeiri =realm.where(Pregunta.class)
                .equalTo("idAudit",idAudit)
                .beginsWith("idPregunta","1")
                .findAll();
        RealmResults<Pregunta> preguntasSeiton =realm.where(Pregunta.class)
                .equalTo("idAudit",idAudit)
                .beginsWith("idPregunta","1")
                .findAll();
        RealmResults<Pregunta> preguntasSeiso =realm.where(Pregunta.class)
                .equalTo("idAudit",idAudit)
                .beginsWith("idPregunta","1")
                .findAll();
        RealmResults<Pregunta> preguntasSeiketsu =realm.where(Pregunta.class)
                .equalTo("idAudit",idAudit)
                .beginsWith("idPregunta","1")
                .findAll();
        RealmResults<Pregunta> preguntasShitsuke =realm.where(Pregunta.class)
                .equalTo("idAudit",idAudit)
                .beginsWith("idPregunta","1")
                .findAll();
        Auditoria mAudit=realm.where(Auditoria.class)
                .equalTo("idAudit",idAudit)
                .findFirst();


        List<Pregunta> unListaSeiri = new ArrayList<>();
        unListaSeiri.addAll(preguntasSeiri);
        List<Pregunta> unListaSeiton = new ArrayList<>();
        unListaSeiton.addAll(preguntasSeiton);
        List<Pregunta> unListaSeiso = new ArrayList<>();
        unListaSeiso.addAll(preguntasSeiso);
        List<Pregunta> unListaSeiketsu = new ArrayList<>();
        unListaSeiketsu.addAll(preguntasSeiketsu);
        List<Pregunta> unListaShitsuke = new ArrayList<>();
        unListaShitsuke.addAll(preguntasShitsuke);

        writer = new PDFWriter(PaperSize.LETTER_WIDTH, PaperSize.LETTER_HEIGHT);
        crearPdfEse(unListaSeiri);
        writer.newPage();
        crearPdfEse(unListaSeiton);
        writer.newPage();
        crearPdfEse(unListaSeiso);
        writer.newPage();
        crearPdfEse(unListaSeiketsu);
        writer.newPage();
        crearPdfEse(unListaShitsuke);
        cargarGraficos(mAudit);

        outputToFile("5S Report-" + mAudit.getAreaAuditada().getNombreArea() + "-" + mAudit.getFechaAuditoria() + ".pdf", writer.asString(), "ISO-8859-1");
    }


    /**
     * Scales the provided bitmap to have the height and width provided.
     * (Alternative method for scaling bitmaps
     * since Bitmap.createScaledBitmap(...) produces bad (blocky) quality bitmaps.)
     */

    public void crearPdfEse(List<Pregunta> laLista) {

        Integer cursorX = 0;
        Integer cursorY = 792;
        Integer renglonesFoto;

//        traigo la auditoria que quiero armar
        Realm realm = Realm.getDefaultInstance();
        Auditoria mAudit = realm.where(Auditoria.class)
                .equalTo("idAuditoria", idAudit)
                .findFirst();

//        declaro el pdwriter
        //fuente titulo
        writer.setFont(StandardFonts.SUBTYPE, StandardFonts.HELVETICA, StandardFonts.WIN_ANSI_ENCODING);
        //escribir titulo
        writer.addText(MARGEN_IZQUIERDO, PaperSize.LETTER_HEIGHT - MARGEN_IZQUIERDO, 20, getResources().getString(R.string.tituloPdf));
        cursorY = cursorY - MARGEN_IZQUIERDO;
        cursorX = cursorX + MARGEN_IZQUIERDO;
        //fuente fecha escribir fecga
        writer.addText(cursorX, cursorY - SALTO_LINEA, 12, getResources().getString(R.string.fecha) + mAudit.getFechaAuditoria());
        cursorY = cursorY - SALTO_LINEA;
        writer.setFont(StandardFonts.SUBTYPE, StandardFonts.HELVETICA, StandardFonts.WIN_ANSI_ENCODING);
        writer.addText(PaperSize.LETTER_WIDTH - 4 * MARGEN_IZQUIERDO, cursorY, 12, laLista.get(0).getIdPregunta().substring(0,1));

        //linea separacion
        writer.addLine(MARGEN_IZQUIERDO, PaperSize.LETTER_HEIGHT - (65), PaperSize.LETTER_WIDTH - MARGEN_IZQUIERDO, PaperSize.LETTER_HEIGHT - (65));
        cursorY = PaperSize.LETTER_HEIGHT - 65;
        cursorY = cursorY - SALTO_LINEA;
        cursorX = MARGEN_IZQUIERDO;
//        EMPIEZO A RECORRER LOS SUBITEMS
        recorrerSubitemLista(laLista, cursorX, cursorY, mAudit.getFechaAuditoria());
    }

    private void recorrerSubitemLista(List<Pregunta> laLista, int x, int y, String fecha) {
        int cursorX = x;
        int cursorY = y;
        for (Pregunta sub : laLista) {
            cursorX = MARGEN_IZQUIERDO;
            cursorY = cursorY - (SEPARACIONFOTOS / 2);
            if (cursorY < 2 * MARGEN_IZQUIERDO) {
                writer.newPage();

                cursorX = 0;
                cursorY = 792;
                //fuente titulo
                writer.setFont(StandardFonts.SUBTYPE, StandardFonts.HELVETICA, StandardFonts.WIN_ANSI_ENCODING);
                //escribir titulo
                writer.addText(MARGEN_IZQUIERDO, PaperSize.LETTER_HEIGHT - MARGEN_IZQUIERDO, 20, getResources().getString(R.string.tituloPdf));
                cursorY = cursorY - MARGEN_IZQUIERDO;
                cursorX = cursorX + MARGEN_IZQUIERDO;
                //fuente fecha escribir fecga
                writer.addText(cursorX, cursorY - SALTO_LINEA, 12, getResources().getString(R.string.fecha) + fecha);
                cursorY = cursorY - SALTO_LINEA;
                writer.setFont(StandardFonts.SUBTYPE, StandardFonts.HELVETICA, StandardFonts.WIN_ANSI_ENCODING);
                writer.addText(PaperSize.LETTER_WIDTH - 4 * MARGEN_IZQUIERDO, cursorY, 12, laLista.get(0).getIdPregunta().substring(0,1));

                //linea separacion
                writer.addLine(MARGEN_IZQUIERDO, PaperSize.LETTER_HEIGHT - (65), PaperSize.LETTER_WIDTH - MARGEN_IZQUIERDO, PaperSize.LETTER_HEIGHT - (65));
                cursorY = PaperSize.LETTER_HEIGHT - 65;
                cursorY = cursorY - SALTO_LINEA;
                cursorX = MARGEN_IZQUIERDO;

            }

            writer.addText(cursorX, cursorY - SALTO_LINEA, 12, sub.getTextoPregunta());
            cursorY = cursorY - SALTO_LINEA;
            writer.addText(cursorX, cursorY - SALTO_LINEA, 12, getResources().getString(R.string.score) + sub.getPuntaje().toString());
            cursorY = cursorY - 2 * SALTO_LINEA;

            //renglonesFoto=Math.round(sub.getListaFotos().size()/3);
            for (Foto foto : sub.getListaFotos()
                    ) {
                Foto unaFoto = foto;
                File unFile = new File(unaFoto.getRutaFoto());
                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                bmOptions.inScaled = false;
                Bitmap rawBitmap = BitmapFactory.decodeFile(unFile.getAbsolutePath(), bmOptions);
                Bitmap bitmapScaled = scaleBitmap(rawBitmap, 270);


//                    SI LA FOTO NO ENTRA EN LO QUE QUEDA DE PAGINA
                if (cursorY - bitmapScaled.getHeight() < SALTO_LINEA) {
                    //si la imagen no entra armo una pagina nueva
                    //vuelvo a poner los titulos y demas
                    writer.newPage();
                    cursorX = 0;
                    cursorY = 792;
                    //fuente titulo
                    writer.setFont(StandardFonts.SUBTYPE, StandardFonts.HELVETICA, StandardFonts.WIN_ANSI_ENCODING);
                    //escribir titulo
                    writer.addText(MARGEN_IZQUIERDO, PaperSize.LETTER_HEIGHT - MARGEN_IZQUIERDO, 20, getResources().getString(R.string.tituloPdf));
                    cursorY = cursorY - MARGEN_IZQUIERDO;
                    cursorX = cursorX + MARGEN_IZQUIERDO;
                    //fuente fecha escribir fecga
                    writer.addText(cursorX, cursorY - SALTO_LINEA, 12, getResources().getString(R.string.fecha) + fecha);
                    cursorY = cursorY - SALTO_LINEA;
                    writer.setFont(StandardFonts.SUBTYPE, StandardFonts.HELVETICA, StandardFonts.WIN_ANSI_ENCODING);
                    writer.addText(PaperSize.LETTER_WIDTH - 4 * MARGEN_IZQUIERDO, cursorY, 12, laLista.get(0).getIdPregunta().substring(0,1));

                    //linea separacion
                    writer.addLine(MARGEN_IZQUIERDO, PaperSize.LETTER_HEIGHT - (65), PaperSize.LETTER_WIDTH - MARGEN_IZQUIERDO, PaperSize.LETTER_HEIGHT - (65));
                    cursorY = PaperSize.LETTER_HEIGHT - 65;
                    cursorY = cursorY - SALTO_LINEA;
                    cursorX = MARGEN_IZQUIERDO;

                    //FINALMENTE AGREGO LA FOTO
                    writer.addImage(cursorX, cursorY - bitmapScaled.getHeight(), bitmapScaled);
                    writer.addText(cursorX, cursorY - bitmapScaled.getHeight() - SEPARACIONFOTOS, 10, unaFoto.getComentarioFoto());
                    cursorX = cursorX + bitmapScaled.getWidth() + SALTO_LINEA;
                    cursorY = cursorY - bitmapScaled.getHeight();
                } else {
//                        SI HAY LUGAR HACIA ABAJO CHEQUEO QUE ENTRE HORIZONTALMENTE
                    if (cursorX + bitmapScaled.getWidth() > 612 - MARGEN_IZQUIERDO) {
//                            NO ENTRA
//                            LA PONGO ABAJO
                        cursorX = MARGEN_IZQUIERDO;
                        cursorY = cursorY - bitmapScaled.getHeight() - SEPARACIONFOTOS;
                        writer.addImage(cursorX, cursorY, bitmapScaled);
                        writer.addText(cursorX, cursorY - SEPARACIONFOTOS, 10, unaFoto.getComentarioFoto());
                        cursorX = cursorX + bitmapScaled.getWidth();

                    } else {
                        if (cursorX == MARGEN_IZQUIERDO) {
                            writer.addImage(cursorX, cursorY - bitmapScaled.getHeight(), bitmapScaled);
                            writer.addText(cursorX, cursorY - bitmapScaled.getHeight() - SEPARACIONFOTOS, 10, unaFoto.getComentarioFoto());
                            cursorX = cursorX + bitmapScaled.getWidth();
                            cursorY = cursorY - bitmapScaled.getHeight();
                        } else {
//                       ENTRA EN LA MISMA LINEA
                            writer.addImage(cursorX + SEPARACIONFOTOS, cursorY, bitmapScaled);
                            writer.addText(cursorX + SEPARACIONFOTOS, cursorY - SEPARACIONFOTOS, 10, unaFoto.getComentarioFoto());
                            cursorX = cursorX + SEPARACIONFOTOS + bitmapScaled.getWidth();
                            cursorY = cursorY - SEPARACIONFOTOS;
                        }
                    }
                }
            }
        }
    }

    public static Bitmap scaleBitmap(Bitmap elBit, int newWidth) {
        Bitmap bitmap;
        Matrix rotMatrix = new Matrix();
        rotMatrix.postRotate(90);

        if (elBit.getWidth() > elBit.getHeight()) {
            bitmap = elBit;
        } else {
            bitmap = Bitmap.createBitmap(elBit, 0, 0, elBit.getWidth(), elBit.getHeight(), rotMatrix, true);
        }
        Double proporcion = (bitmap.getWidth() * 1.00 / bitmap.getHeight() * 1.00);

        int newHeight = (int) Math.round(newWidth / proporcion);
        Bitmap scaledBitmap = Bitmap.createBitmap(newWidth, newHeight, Bitmap.Config.ARGB_8888);

        float scaleX = newWidth / (float) bitmap.getWidth();
        float scaleY = newHeight / (float) bitmap.getHeight();
        float pivotX = 0;
        float pivotY = 0;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(scaleX, scaleY, pivotX, pivotY);
        if (bitmap.getHeight() > bitmap.getWidth()) {
            scaleMatrix.postRotate(90);
        }
        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bitmap, 0, 0, new Paint(Paint.FILTER_BITMAP_FLAG));
        return scaledBitmap;
    }

    public static Bitmap scaleSinRotar(Bitmap bitmap, int newWidth) {


        Double proporcion = (bitmap.getWidth() * 1.00 / bitmap.getHeight() * 1.00);

        int newHeight = (int) Math.round(newWidth / proporcion);
        Bitmap scaledBitmap = Bitmap.createBitmap(newWidth, newHeight, Bitmap.Config.ARGB_8888);

        float scaleX = newWidth / (float) bitmap.getWidth();
        float scaleY = newHeight / (float) bitmap.getHeight();
        float pivotX = 0;
        float pivotY = 0;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(scaleX, scaleY, pivotX, pivotY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bitmap, 0, 0, new Paint(Paint.FILTER_BITMAP_FLAG));
        return scaledBitmap;
    }

    public void metodoRating() {
        boolean quiereVerRating = config.getBoolean("quiereVerRating", true);

        if (quiereVerRating) {
            Integer contadorPaRating = config.getInt("contadorPaRating", 0);
            final SharedPreferences.Editor editor = config.edit();

            if (contadorPaRating == 0 || contadorPaRating == 4) {
                //dialogo de rating

                new MaterialDialog.Builder(this)
                        .title(getResources().getString(R.string.titRating))
                        .buttonsGravity(GravityEnum.CENTER)
                        .contentColor(ContextCompat.getColor(this, R.color.primary_text))
                        .backgroundColor(ContextCompat.getColor(this, R.color.tile1))
                        .positiveText(getResources().getString(R.string.ok))
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                editor.putBoolean("quiereVerRating", false);
                                editor.commit();
                            }
                        })
                        .negativeText(getResources().getString(R.string.no))
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                editor.putBoolean("quiereVerRating", false);
                                editor.commit();

                                irALanding();
                            }
                        })
                        .neutralText(getResources().getString(R.string.later))
                        .onNeutral(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                editor.putInt("contadorPaRating", 1);
                                editor.commit();
                                irALanding();
                            }
                        })
                        .titleColor(ContextCompat.getColor(this, R.color.tile4))
                        .content(getResources().getString(R.string.rateUsl1)+"\n"+getResources().getString(R.string.rateUsl2))
                        .show();

            } else {
                contadorPaRating = contadorPaRating + 1;
                editor.putInt("contadorPaRating", contadorPaRating);
                editor.commit();
                irALanding();
            }

        }
        else{
            irALanding();
        }

    }




    public void metodoRatingOnBack() {
        boolean quiereVerRating = config.getBoolean("quiereVerRating", true);

        if (quiereVerRating) {
            Integer contadorPaRating = config.getInt("contadorPaRating", 0);
            final SharedPreferences.Editor editor = config.edit();

            if (contadorPaRating == 0 || contadorPaRating == 4) {
                //dialogo de rating

                new MaterialDialog.Builder(this)
                        .title(getResources().getString(R.string.titRating))
                        .buttonsGravity(GravityEnum.CENTER)
                        .contentColor(ContextCompat.getColor(this, R.color.primary_text))
                        .backgroundColor(ContextCompat.getColor(this, R.color.tile1))
                        .positiveText(getResources().getString(R.string.ok))
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                editor.putBoolean("quiereVerRating", false);
                                editor.commit();
                            }
                        })
                        .negativeText(getResources().getString(R.string.no))
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                editor.putBoolean("quiereVerRating", false);
                                editor.commit();

                                definirDondeIrOnBack();
                            }
                        })
                        .neutralText(getResources().getString(R.string.later))
                        .onNeutral(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                editor.putInt("contadorPaRating", 1);
                                editor.commit();

                                definirDondeIrOnBack();
                            }
                        })
                        .titleColor(ContextCompat.getColor(this, R.color.tile4))
                        .content(getResources().getString(R.string.rateUsl1)+"\n"+getResources().getString(R.string.rateUsl2))
                        .show();

            } else {
                contadorPaRating = contadorPaRating + 1;
                editor.putInt("contadorPaRating", contadorPaRating);
                editor.commit();
                definirDondeIrOnBack();

            }

        }
        else{
            definirDondeIrOnBack();
        }

    }




    private Intent rateIntentForUrl(String url)
    {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(String.format("%s?id=%s", url, getPackageName())));
        int flags = Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_MULTIPLE_TASK;
        if (Build.VERSION.SDK_INT >= 21)
        {
            flags |= Intent.FLAG_ACTIVITY_NEW_DOCUMENT;
        }
        else
        {
            //noinspection deprecation
            flags |= Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET;
        }
        intent.addFlags(flags);
        return intent;
    }
    @Override
    public void onBackPressed() {
        metodoRatingOnBack();

    }

    public void definirDondeIrOnBack(){
        if (origenIntent.equals("myAudits")) {
            Intent unIntent = new Intent(GraficosActivity.this, ActivityMyAudits.class);
            startActivity(unIntent);
        } else {
            GraficosActivity.super.onBackPressed();

        }
        GraficosActivity.this.finish();
    }




}

