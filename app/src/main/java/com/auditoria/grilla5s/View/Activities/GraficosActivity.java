package com.auditoria.grilla5s.View.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
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
import com.auditoria.grilla5s.Model.Auditoria;
import com.auditoria.grilla5s.Model.Ese;
import com.auditoria.grilla5s.Model.Foto;
import com.auditoria.grilla5s.Model.Item;
import com.auditoria.grilla5s.Model.Pregunta;
import com.auditoria.grilla5s.R;
import com.auditoria.grilla5s.Utils.FuncionesPublicas;
import com.auditoria.grilla5s.View.Fragments.FragmentBarrasApiladas;
import com.auditoria.grilla5s.View.Fragments.FragmentBarrasApiladasPorArea;
import com.auditoria.grilla5s.View.Fragments.FragmentRadar;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.firebase.auth.FirebaseAuth;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.Random;
import crl.android.pdfwriter.PDFWriter;
import id.zelory.compressor.Compressor;
import io.realm.Realm;
import io.realm.RealmResults;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.Colour;
import jxl.format.VerticalAlignment;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableImage;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;
import pl.tajchert.nammu.Nammu;

public class GraficosActivity extends AppCompatActivity {

    public static final String AUDIT = "AUDIT";
    public static final String AREA = "AREA";
    public static final String ORIGEN = "ORIGEN";
    public static final int MARGEN_IZQUIERDO = 25;
    public static final int SALTO_LINEA = 14;
    public static final int SEPARACIONFOTOS = 7;
    private static final int letraTitulo = 20;
    private static final int letraPreguntas = 10;
    private static final int anchoFoto = 5;
    private static final int altoFoto = 10;

    private Integer netWidth=270;
    private Boolean auditEstaCompleta;
    //para pdf
    private Integer cursorX;
    private Integer cursorY;
    private String origenIntent;
    private String idAudit;
    private String areaAuditada;
    private Double promedioSeiri;
    private Double promedioSeiton;
    private Double promedioSeiso;
    private Double promedio5s;
    private FloatingActionMenu fabMenuGraficos;
    private FloatingActionButton fabGenerarPDF;
    private FloatingActionButton fabVerAuditoria;
    private FloatingActionButton fabEditarAuditoria;
    private FloatingActionButton fabGenerarXLS;
    private ProgressBar progressBar;
    private Double promedioSeiketsu;
    private Double promedioShitsuke;
    private PDFWriter writer;
    private File fotoComprimida;
    private WritableSheet laHojaFotos;
    private SharedPreferences config;
    private Integer sumatoriaPreguntas;
    private Integer divisorPreguntas;
    private Double sumatoriaItems;
    private Integer divisorItems;
    private Double sumatoriaEse;
    private Integer divisorEse;
    private Auditoria auditActual;
    private DecimalFormat df;

    private String cheatLevel;
    

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

        if (bundle != null) {
            idAudit = bundle.getString(AUDIT);
            origenIntent = bundle.getString(ORIGEN);
            areaAuditada = bundle.getString(AREA);
        }

        df = new DecimalFormat("#.##");


        FragmentActivity unaActivity = this;
        FragmentManager fragmentManager = (FragmentManager) unaActivity.getSupportFragmentManager();
        FragmentRadar fragmentRadar = (FragmentRadar) fragmentManager.findFragmentByTag("radar");

        if (fragmentRadar != null && fragmentRadar.isVisible()) {
            //
            Toast.makeText(unaActivity, "", Toast.LENGTH_SHORT).show();
        } else {

            cargarGraficoRadar();
            cargarGraficoBarras();
            cargarGraficoHistorico();
        }


        fabMenuGraficos = (FloatingActionMenu) findViewById(R.id.menuSalida);

        fabMenuGraficos.setMenuButtonColorNormal(ContextCompat.getColor(this, R.color.colorAccent));

        fabEditarAuditoria = new FloatingActionButton(this);
        fabEditarAuditoria.setColorNormal(ContextCompat.getColor(this, R.color.tutorial1));
        fabEditarAuditoria.setButtonSize(FloatingActionButton.SIZE_MINI);
        fabEditarAuditoria.setLabelText(getResources().getString(R.string.editarAuditoria));
        fabEditarAuditoria.setImageResource(R.drawable.ic_edit_black_24dp);
        fabMenuGraficos.addMenuButton(fabEditarAuditoria);

        fabEditarAuditoria.setLabelColors(ContextCompat.getColor(this, R.color.tutorial1),
                ContextCompat.getColor(this, R.color.light_grey),
                ContextCompat.getColor(this, R.color.white_transparent));
        fabEditarAuditoria.setLabelTextColor(ContextCompat.getColor(this, R.color.primary_text));

        fabEditarAuditoria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Realm realm = Realm.getDefaultInstance();
                Auditoria mAudit = realm.where(Auditoria.class)
                        .equalTo("idAuditoria", idAudit)
                        .findFirst();
                if (mAudit != null && !mAudit.getAuditEstaCerrada()) {
                    fabMenuGraficos.close(true);
                    editarAuditoria(idAudit, mAudit.getAreaAuditada().getIdArea());
                } else {
                    Snackbar.make(fabEditarAuditoria, getResources().getString(R.string.auditCerradaNoPuedeEditar), Snackbar.LENGTH_SHORT)
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
        fabVerAuditoria.setColorNormal(ContextCompat.getColor(this, R.color.tile3));
        fabVerAuditoria.setButtonSize(FloatingActionButton.SIZE_MINI);
        fabVerAuditoria.setLabelText(getResources().getString(R.string.verAuditoria));
        fabVerAuditoria.setImageResource(R.drawable.ic_find_in_page_black_24dp);
        fabMenuGraficos.addMenuButton(fabVerAuditoria);

        fabVerAuditoria.setLabelColors(ContextCompat.getColor(this, R.color.tile2),
                ContextCompat.getColor(this, R.color.light_grey),
                ContextCompat.getColor(this, R.color.white_transparent));
        fabVerAuditoria.setLabelTextColor(ContextCompat.getColor(this, R.color.primary_text));

        fabVerAuditoria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fabMenuGraficos.close(true);
                Intent inten = new Intent(v.getContext(), ActivityPreAuditoria.class);
                Bundle bundle123 = new Bundle();
                bundle123.putString(ActivityPreAuditoria.IDAUDIT, idAudit);
                bundle123.putString(ActivityPreAuditoria.IDAREA, auditActual.getAreaAuditada().getIdArea());
                bundle123.putString(ActivityPreAuditoria.ORIGEN, "REVISAR");

                inten.putExtras(bundle123);
                startActivity(inten);

            }
        });

        //SI LA AUDITORIA ESTA CERRADA HABILITO BOTON REVISAR SI LA AUDIT ESTA INCOMPLETA HBILITO BOTON EDITAR

        Realm realm = Realm.getDefaultInstance();
        Auditoria mAudit = realm.where(Auditoria.class)
                .equalTo("idAuditoria", idAudit)
                .findFirst();
        if (mAudit == null || !mAudit.getAuditEstaCerrada()) {
            fabMenuGraficos.removeMenuButton(fabVerAuditoria);
        } else {
            fabMenuGraficos.removeMenuButton(fabEditarAuditoria);
        }


        fabGenerarXLS= new FloatingActionButton(this);
        fabGenerarXLS.setColorNormal(ContextCompat.getColor(this, R.color.tile3));
        fabGenerarXLS.setButtonSize(FloatingActionButton.SIZE_MINI);
        fabGenerarXLS.setLabelText(getString(R.string.generarXLS));
        fabGenerarXLS.setImageResource(R.drawable.ic_explicit_black_24dp);
        fabMenuGraficos.addMenuButton(fabGenerarXLS);

        fabGenerarXLS.setLabelColors(ContextCompat.getColor(this, R.color.tile2),
                ContextCompat.getColor(this, R.color.light_grey),
                ContextCompat.getColor(this, R.color.white_transparent));
        fabGenerarXLS.setLabelTextColor(ContextCompat.getColor(this, R.color.primary_text));

        fabGenerarXLS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (FuncionesPublicas.isExternalStorageWritable()) {
                    fabMenuGraficos.close(true);
                    if (auditActual.getAuditEstaCerrada()) {

                        new EnviarXLS().execute();
                    } else {


                        new MaterialDialog.Builder(GraficosActivity.this)
                                .title("Warning!")
                                .title(getResources().getString(R.string.advertencia))
                                .contentColor(ContextCompat.getColor(GraficosActivity.this, R.color.primary_text))
                                .titleColor(ContextCompat.getColor(GraficosActivity.this, R.color.tile4))
                                .backgroundColor(ContextCompat.getColor(GraficosActivity.this, R.color.tile1))
                                .content(getResources().getString(R.string.auditoriaSinTerminar)+"\n"+getResources().getString(R.string.continuar))
                                .positiveText(getResources().getString(R.string.si))
                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                                        new EnviarXLS().execute();
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


        FloatingActionButton fabBorrarAuditoria = new FloatingActionButton(this);
        fabBorrarAuditoria.setColorNormal(ContextCompat.getColor(this, R.color.semaRojo));
        fabBorrarAuditoria.setButtonSize(FloatingActionButton.SIZE_MINI);
        fabBorrarAuditoria.setLabelText(getString(R.string.deleteAudit));
        fabBorrarAuditoria.setImageResource(R.drawable.ic_delete_forever_black_24dp);
        fabMenuGraficos.addMenuButton(fabBorrarAuditoria);

        fabBorrarAuditoria.setLabelColors(ContextCompat.getColor(this, R.color.semaRojo),
                ContextCompat.getColor(this, R.color.light_grey),
                ContextCompat.getColor(this, R.color.white_transparent));
        fabBorrarAuditoria.setLabelTextColor(ContextCompat.getColor(this, R.color.primary_text));

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
            String user = FirebaseAuth.getInstance().getCurrentUser().getEmail();


        //si somos superUser
        if (user!=null && (user.equals("martin@gol.com") || user.equals("h.fontanet@hotmail.com") || user.equals("hrfontanet@gmail.com") )) {
            FloatingActionButton fabCompletarAudit = new FloatingActionButton(this);
            fabCompletarAudit.setColorNormal(ContextCompat.getColor(this, R.color.tile3));
            fabCompletarAudit.setButtonSize(FloatingActionButton.SIZE_MINI);
            fabCompletarAudit.setLabelText("completar Audit");
            fabCompletarAudit.setImageResource(R.drawable.ic_check_black_24dp);
            fabMenuGraficos.addMenuButton(fabCompletarAudit);

            fabCompletarAudit.setLabelColors(ContextCompat.getColor(this, R.color.tile2),
                    ContextCompat.getColor(this, R.color.light_grey),
                    ContextCompat.getColor(this, R.color.white_transparent));
            fabCompletarAudit.setLabelTextColor(ContextCompat.getColor(this, R.color.primary_text));


//            ONCLICK DEL CHEAT BUTTON
            fabCompletarAudit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    new MaterialDialog.Builder(GraficosActivity.this)
                            .cancelable(false)
                            .title("Cheat Menu")
                            .buttonsGravity(GravityEnum.CENTER)
                            .contentColor(ContextCompat.getColor(GraficosActivity.this, R.color.primary_text))
                            .backgroundColor(ContextCompat.getColor(GraficosActivity.this, R.color.tile1))
                            .positiveText("alto")
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                   cheatLevel ="alto";
                                   chetearPregunta();
                                }
                            })
                            .negativeText("bajo")
                            
                            .onNegative(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    cheatLevel ="bajo";
                                    chetearPregunta();
                                }
                            })
                            .neutralText("medio")
                            .onNeutral(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    cheatLevel ="medio";
                                    chetearPregunta();
                                }
                            })
                            .titleColor(ContextCompat.getColor(GraficosActivity.this, R.color.tile4))
                            .content("Nivel de puntaje deseado")
                            .show();



                }
            });
        }




        //SI EL USUARIO ELIGIO VER TUTORIALES ME FIJO SI YA PASO POR ESTA PAGINA.
        boolean quiereVerTuto = config.getBoolean("quiereVerTuto", false);
        boolean primeraVezFragmentRadar = config.getBoolean("primeraVezFragmentRadar", false);

        if (quiereVerTuto) {
            if (!primeraVezFragmentRadar) {

                SharedPreferences.Editor editor = config.edit();


                editor.putBoolean("primeraVezFragmentRadar", true);
                editor.commit();

                seguirConTutorial();
            }
        }


    }

    private void chetearPregunta(){
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<Pregunta> lasPreguntas = realm.where(Pregunta.class)
                        .equalTo("idAudit", idAudit)
                        .findAll();

                for (Pregunta unaPreg : lasPreguntas
                        ) {

                    if (unaPreg==lasPreguntas.get(0)||unaPreg==lasPreguntas.get(1)) {
                        //nada
                    } else {
                        Random r = new Random();
                        int i1;
                        switch (cheatLevel){
                            case "alto":

                                i1 = r.nextInt((5-4) + 1)+4;
                                unaPreg.setPuntaje(i1);
                                break;
                            case "medio":

                                i1 = r.nextInt((4-3) + 1)+3;
                                unaPreg.setPuntaje(i1);
                                break;
                            case "bajo":

                                i1 = r.nextInt((3) + 1);
                                unaPreg.setPuntaje(i1);
                                break;

                        }

                    }
                }
            }
        });

        Toast.makeText(GraficosActivity.this, "cheat enabled", Toast.LENGTH_SHORT).show();
    }

    private void cargarGraficoHistorico() {

        FragmentBarrasApiladasPorArea fragmentBarrasApiladasPorArea = new FragmentBarrasApiladasPorArea();
        Bundle bundle = new Bundle();
        bundle.putString(FragmentBarrasApiladasPorArea.IDAREA, areaAuditada);
        bundle.putString(FragmentBarrasApiladasPorArea.ORIGEN, "graficos");
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentBarrasApiladasPorArea.setArguments(bundle);
        fragmentTransaction.add(R.id.contenedorGraficos, fragmentBarrasApiladasPorArea, "fragmentGraficoArea");
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    public void editarAuditoria(String idAudit, String idArea) {

        Intent intent = new Intent(this, ActivityPreAuditoria.class);
        Bundle bundle = new Bundle();
        bundle.putString(ActivityPreAuditoria.IDAREA, idArea);
        bundle.putString(ActivityPreAuditoria.ORIGEN, "EDITAR_AUDITORIA");
        bundle.putString(ActivityPreAuditoria.IDAUDIT, idAudit);

        intent.putExtras(bundle);
        GraficosActivity.this.finish();
        startActivity(intent);
        FragmentManager fragmentManager = this.getSupportFragmentManager();
        fragmentManager.popBackStack();

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
                        TapTarget.forView(fabGenerarXLS, getResources().getString(R.string.tutorial_desc_graficos_pdf), getResources().getString(R.string.tutorial_desc_graficos_pdf))
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

        FuncionesPublicas.calcularPuntajesAuditoria(idAudit);

        Realm realm = Realm.getDefaultInstance();
        Auditoria laAudit = realm.where(Auditoria.class)
                .equalTo("idAuditoria", idAudit)
                .findFirst();
        auditActual = laAudit;

        if (laAudit != null) {
            promedioSeiri = laAudit.getListaEses().get(0).getPuntajeEse();
            promedioSeiton = laAudit.getListaEses().get(1).getPuntajeEse();
            promedioSeiso = laAudit.getListaEses().get(2).getPuntajeEse();
            promedioSeiketsu = laAudit.getListaEses().get(3).getPuntajeEse();
            promedioShitsuke = laAudit.getListaEses().get(4).getPuntajeEse();

        FragmentRadar graficoFragment = new FragmentRadar();
        Bundle bundle = new Bundle();
        bundle.putDouble(FragmentRadar.PUNJTAJE1, promedioSeiri);
        bundle.putDouble(FragmentRadar.PUNJTAJE2, promedioSeiton);
        bundle.putDouble(FragmentRadar.PUNJTAJE3, promedioSeiso);
        bundle.putDouble(FragmentRadar.PUNJTAJE4, promedioSeiketsu);
        bundle.putDouble(FragmentRadar.PUNJTAJE5, promedioShitsuke);
        bundle.putString(FragmentRadar.AREA, laAudit.getAreaAuditada().getNombreArea());
        bundle.putBoolean(FragmentRadar.COMPLETO, laAudit.getAuditEstaCerrada());

        graficoFragment.setArguments(bundle);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.contenedorGraficos, graficoFragment, "radar");
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
        }
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

    public void irALanding() {
        Intent intent = new Intent(this, LandingActivity.class);
        startActivity(intent);
        GraficosActivity.this.finish();
    }



    public void mandarExcelPorMail(String fileName) {
        if (existeDirectorio()) {

            File newFile = new File(getExternalFilesDir(null) + File.separator + "nomad" + File.separator + "audit5s" + File.separator + FirebaseAuth.getInstance().getCurrentUser().getEmail() + File.separator + "audits" + File.separator + fileName);

            try {
                Uri path = FileProvider.getUriForFile(this, this.getApplicationContext().getPackageName() + ".my.package.name.provider", newFile);

                //Uri path = Uri.fromFile(newFile);
                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                // set the type to 'email'
                emailIntent.setType("application/excel");
                String to[] = {""};
                emailIntent.putExtra(Intent.EXTRA_EMAIL, to);
                // the attachment
                emailIntent.putExtra(Intent.EXTRA_STREAM, path);
                // the mail subject
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.reporteAuditoria));
                startActivity(Intent.createChooser(emailIntent, getResources().getString(R.string.enviarMail)));


            } catch (Exception e) {
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
        if (hayPermit) {
            if (!dir.exists() || !dir.isDirectory()) {
                sePudo = dir.mkdirs();
            }
        } else {
            sePudo = false;
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

    @Override
    public void onBackPressed() {
        definirDondeIrOnBack();
    }

    public void definirDondeIrOnBack() {
        if (origenIntent.equals("myAudits")) {
            Intent unIntent = new Intent(GraficosActivity.this, ActivityMyAudits.class);
            startActivity(unIntent);
        } else {
            Intent unIntent = new Intent(GraficosActivity.this, LandingActivity.class);
            startActivity(unIntent);

        }
        GraficosActivity.this.finish();
    }

    public WritableWorkbook crearLibroExcel(String fileName) {
        //exports must use a temp file while writing to avoid memory hogging
        WorkbookSettings wbSettings = new WorkbookSettings();
        wbSettings.setUseTemporaryFileDuringWrite(true);

        //add on the your app's path
        File dir = new File(getExternalFilesDir(null) + File.separator + "nomad" + File.separator + "audit5s" + File.separator + FirebaseAuth.getInstance().getCurrentUser().getEmail() + File.separator + "audits");
        //make them in case they're not there
        if (FuncionesPublicas.hayPermisoParaEscribir(this, fabMenuGraficos)) {
            if (!dir.exists()) {
                dir.mkdirs();
            }
        } else {
            Toast.makeText(this, getResources().getString(R.string.errorRevisarPermisos), Toast.LENGTH_SHORT).show();
        }
        //create a standard java.io.File object for the Workbook to use
        File wbfile = new File(dir, fileName);

        WritableWorkbook wb = null;

        try {
            //create a new WritableWorkbook using the java.io.File and
            //WorkbookSettings from above
            wb = Workbook.createWorkbook(wbfile, wbSettings);
        } catch (IOException ex) {

        }
        return wb;
    }

    public WritableSheet crearHoja(WritableWorkbook wb,
                                   String sheetName, int sheetIndex) {
        //create a new WritableSheet and return it
        return wb.createSheet(sheetName, sheetIndex);
    }

    public void escribirCelda(int columnPosition, int rowPosition, String contents, String headerCell,
                              WritableSheet sheet) throws RowsExceededException, WriteException {
        //create a new cell with contents at position
        Label newCell = new Label(columnPosition, rowPosition, contents);

        if (headerCell.equals("titulo")) {
            //give header cells size 10 Arial bolded
            WritableFont headerFont = new WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD);
            headerFont.setColour(Colour.WHITE);
            WritableCellFormat headerFormat = new WritableCellFormat(headerFont);
            //center align the cells' contents
            headerFormat.setAlignment(Alignment.CENTRE);
            headerFormat.setBackground(Colour.BLUE_GREY);
            headerFormat.setBorder(Border.ALL, BorderLineStyle.THICK);
            newCell.setCellFormat(headerFormat);
        }
        if (headerCell.equals("subTotal")) {
            //give header cells size 10 Arial bolded
            WritableFont headerFont = new WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD);
            WritableCellFormat headerFormat = new WritableCellFormat(headerFont);
            //center align the cells' contents
            headerFormat.setAlignment(Alignment.LEFT);
            headerFormat.setBackground(Colour.VERY_LIGHT_YELLOW);
            headerFormat.setBorder(Border.ALL, BorderLineStyle.THIN);
            newCell.setCellFormat(headerFormat);
        }

        if (headerCell.equals("subTotalEse")) {
            //give header cells size 10 Arial bolded
            WritableFont headerFont = new WritableFont(WritableFont.ARIAL, 14);
            WritableCellFormat headerFormat = new WritableCellFormat(headerFont);
            //center align the cells' contents
            headerFormat.setVerticalAlignment(VerticalAlignment.CENTRE);
            headerFormat.setBackground(Colour.YELLOW);
            headerFormat.setWrap(true);
            headerFormat.setBorder(Border.ALL, BorderLineStyle.THIN);
            newCell.setCellFormat(headerFormat);
        }

        if (headerCell.equals("totalAudit")) {
            //give header cells size 10 Arial bolded
            WritableFont headerFont = new WritableFont(WritableFont.ARIAL, 14);
            WritableCellFormat headerFormat = new WritableCellFormat(headerFont);
            //center align the cells' contents
            headerFormat.setVerticalAlignment(VerticalAlignment.CENTRE);
            headerFormat.setWrap(true);
            headerFormat.setBorder(Border.ALL, BorderLineStyle.THICK);
            headerFormat.setBackground(Colour.PALE_BLUE);
            newCell.setCellFormat(headerFormat);
        }
        if (headerCell.equals("textoNormal")) {
            //give header cells size 10 Arial bolded
            WritableFont headerFont = new WritableFont(WritableFont.ARIAL, 10);
            WritableCellFormat headerFormat = new WritableCellFormat(headerFont);
            //center align the cells' contents
            headerFormat.setVerticalAlignment(VerticalAlignment.CENTRE);
            headerFormat.setWrap(true);
            headerFormat.setBorder(Border.ALL, BorderLineStyle.THIN);
            newCell.setCellFormat(headerFormat);
        }
        if (headerCell.equals("textoNormal2")) {
            //give header cells size 10 Arial bolded
            WritableFont headerFont = new WritableFont(WritableFont.ARIAL, 16);
            WritableCellFormat headerFormat = new WritableCellFormat(headerFont);
            //center align the cells' contents
            headerFormat.setVerticalAlignment(VerticalAlignment.CENTRE);
            headerFormat.setAlignment(Alignment.CENTRE);
            headerFormat.setWrap(true);
            headerFormat.setBorder(Border.ALL, BorderLineStyle.THIN);
            newCell.setCellFormat(headerFormat);
        }
        if (headerCell.equals("textoNormalCentrado")) {
            //give header cells size 10 Arial bolded
            WritableFont headerFont = new WritableFont(WritableFont.ARIAL, 12);
            WritableCellFormat headerFormat = new WritableCellFormat(headerFont);
            //center align the cells' contents
            headerFormat.setVerticalAlignment(VerticalAlignment.CENTRE);
            headerFormat.setAlignment(Alignment.CENTRE);
            headerFormat.setWrap(true);
            headerFormat.setBorder(Border.ALL, BorderLineStyle.THIN);
            newCell.setCellFormat(headerFormat);
        }
        if (headerCell.equals("textoNormalCentradoSinBorde")) {
            //give header cells size 10 Arial bolded
            WritableFont headerFont = new WritableFont(WritableFont.ARIAL, 10);
            WritableCellFormat headerFormat = new WritableCellFormat(headerFont);
            //center align the cells' contents
            headerFormat.setVerticalAlignment(VerticalAlignment.CENTRE);
            headerFormat.setAlignment(Alignment.CENTRE);
            headerFormat.setWrap(true);
            newCell.setCellFormat(headerFormat);
        }
        if (headerCell.equals("bordeInferior")) {
            WritableFont headerFont = new WritableFont(WritableFont.ARIAL, 10);
            WritableCellFormat headerFormat = new WritableCellFormat(headerFont);

            headerFormat.setBorder(Border.BOTTOM, BorderLineStyle.THIN);
            headerFormat.setWrap(true);
            newCell.setCellFormat(headerFormat);
        }
        if (headerCell.equals("bordeLateral")) {
            WritableFont headerFont = new WritableFont(WritableFont.ARIAL, 10);
            WritableCellFormat headerFormat = new WritableCellFormat(headerFont);

            headerFormat.setBorder(Border.RIGHT, BorderLineStyle.THIN);
            headerFormat.setWrap(true);
            newCell.setCellFormat(headerFormat);
        }
        if (headerCell.equals("ultimaCelda")) {
            WritableFont headerFont = new WritableFont(WritableFont.ARIAL, 10);
            WritableCellFormat headerFormat = new WritableCellFormat(headerFont);
            headerFormat.setBorder(Border.RIGHT, BorderLineStyle.THIN);
            headerFormat.setBorder(Border.BOTTOM, BorderLineStyle.THIN);
            headerFormat.setWrap(true);
            newCell.setCellFormat(headerFormat);
        }
        if (headerCell.equals("bordeSuperior")) {
            WritableFont headerFont = new WritableFont(WritableFont.ARIAL, 10);
            WritableCellFormat headerFormat = new WritableCellFormat(headerFont);

            headerFormat.setBorder(Border.TOP, BorderLineStyle.THIN);
            headerFormat.setWrap(true);
            newCell.setCellFormat(headerFormat);
        }
        if (headerCell.equals("bordeSuperiorUltimaCelda")) {
            WritableFont headerFont = new WritableFont(WritableFont.ARIAL, 10);
            WritableCellFormat headerFormat = new WritableCellFormat(headerFont);

            headerFormat.setBorder(Border.TOP, BorderLineStyle.THIN);
            headerFormat.setBorder(Border.RIGHT, BorderLineStyle.THIN);
            headerFormat.setWrap(true);
            newCell.setCellFormat(headerFormat);
        }

        sheet.addCell(newCell);
    }

    public void dibujarBordesFoto(Integer numColumna, Integer numFila) {
        try {
//            dibujarBordesFoto
            for (Integer i = 0; i < 7; i++) {
                escribirCelda(numColumna + i, numFila + altoFoto + 2, "", "bordeInferior", laHojaFotos);
            }
            for (Integer i = 0; i < 7; i++) {
                if (i == 6) {
                    escribirCelda(numColumna + i, numFila, "", "bordeSuperiorUltimaCelda", laHojaFotos);
                } else {
                    escribirCelda(numColumna + i, numFila, "", "bordeSuperior", laHojaFotos);
                }
            }
//            linea lateral
            for (Integer i = 0; i < 13; i++) {
                if (i == 12) {
                    escribirCelda(numColumna + anchoFoto + 1, numFila + i, "", "ultimaCelda", laHojaFotos);
                } else {
                    escribirCelda(numColumna + anchoFoto + 1, numFila + i, "", "bordeLateral", laHojaFotos);
                }
            }
        } catch (WriteException e) {
            e.printStackTrace();
        }
    }

    public void crearExcel() {
        Integer fila = 0;
        Integer columna = 0;
        Realm realm = Realm.getDefaultInstance();
        Auditoria mAudit = realm.where(Auditoria.class)
                .equalTo("idAuditoria", idAudit)
                .findFirst();

//        CREO EL LIBRO CON EL NOMBRE AREA+FECHA

        WritableWorkbook elLibro = crearLibroExcel("5S Report-" + mAudit.getAreaAuditada().getNombreArea() + "-" + FuncionesPublicas.dameFechaString(mAudit.getFechaAuditoria(), "corta") + ".xls");
        WritableSheet laHoja = crearHoja(elLibro, getResources().getString(R.string.resultados), 0);
        laHojaFotos = crearHoja(elLibro, getResources().getString(R.string.tabImagenes), 1);
        WritableSheet laHojaResumen = crearHoja(elLibro, "Resumen", 2);

        //saco el screen y lo convierto en un FILE
        View rootView = this.getWindow().getDecorView().findViewById(android.R.id.content);
        View v = rootView.findViewById(R.id.contenedorGraficos);

        Bitmap unBitmap = screenShot(v);
        Bitmap scaledBitmap = scaleSinRotar(unBitmap, 250);

        File filesDir = getApplicationContext().getFilesDir();
        File imageFile = new File(filesDir, "temp" + ".jpg");

        OutputStream os;
        try {
            os = new FileOutputStream(imageFile);
            unBitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
            os.flush();
            os.close();
        } catch (Exception e) {
            Log.e(getClass().getSimpleName(), "Error writing bitmap", e);
        }

//        lo comprimo
        // Bitmap SunBitmap=Bitmap.createScaledBitmap(unBitmap, 300,510,false);
        try {
            fotoComprimida = new Compressor(this)
                    .setMaxWidth(640)
                    .setMaxHeight(480)
                    .setQuality(75)
                    .setCompressFormat(Bitmap.CompressFormat.JPEG)
                    .compressToFile(imageFile, imageFile.getName().replace(".jpg", ".png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        WritableImage image = new WritableImage(
                1, 1, 4, 44, fotoComprimida); //Supports only 'png' images
        laHojaResumen.addImage(image);


        laHoja.setColumnView(1, 32);
        laHoja.setColumnView(2, 32);
        laHoja.setColumnView(4, 32);
        laHoja.setColumnView(7, 32);
        laHoja.setColumnView(3, 11);

//        laHoja.setColumnView(23,520);
//        laHoja.setColumnView(57,520);
//        laHoja.setColumnView(84,520);
//        laHoja.setColumnView(110,520);
//        laHoja.setColumnView(134,520);
//        laHoja.setColumnView(135,520);


        //SETEO LOS TITULOS DE LA HOJA
        try {
            escribirCelda(columna, fila, "S", "titulo", laHoja);
            columna = columna + 1;
            escribirCelda(columna, fila, getResources().getString(R.string.titCriterio), "titulo", laHoja);
            columna = columna + 1;
            escribirCelda(columna, fila, getResources().getString(R.string.titItem), "titulo", laHoja);
            columna = columna + 1;
            escribirCelda(columna, fila, getResources().getString(R.string.titNumPregunta), "titulo", laHoja);
            columna = columna + 1;
            escribirCelda(columna, fila, getResources().getString(R.string.titPregunta), "titulo", laHoja);
            columna = columna + 1;
            escribirCelda(columna, fila, getResources().getString(R.string.titPuntaje), "titulo", laHoja);
            columna = columna + 1;
            escribirCelda(columna, fila, getResources().getString(R.string.titPorcentaje), "titulo", laHoja);
            columna = columna + 1;
            escribirCelda(columna, fila, getResources().getString(R.string.titComentario), "titulo", laHoja);
            columna = columna + 1;
            escribirCelda(columna, fila, getResources().getString(R.string.titFotos), "titulo", laHoja);
            columna = 0;
            fila = 1;


            for (Ese unaEse :
                    mAudit.getListaEses()) {
//            escribo el numero de ese

                escribirCelda(columna, fila, unaEse.getIdEse() + "S", "textoNormal2", laHoja);
                columna = columna + 1;

                //recorro los item de la ese

                Integer cantItem = unaEse.getListaItem().size();
                Integer cantPreguntasTotales = 0;

                for (Item unItem :
                        unaEse.getListaItem()) {
                    Integer cantPreguntas = unItem.getListaPreguntas().size();
//
//                merge columna criterio
                    laHoja.mergeCells(columna, fila, columna, fila + cantPreguntas - 1);
                    escribirCelda(columna, fila, unItem.getCriterio(), "textoNormal", laHoja);
                    columna++;
//                merge columna item
                    laHoja.mergeCells(columna, fila, columna, fila + cantPreguntas - 1);
                    escribirCelda(columna, fila, unItem.getTextoItem(), "textoNormal", laHoja);
                    columna++;

                    //recorro las preguntas del item
                    for (Pregunta unaPreg :
                            unItem.getListaPreguntas()) {

                        escribirCelda(columna, fila, String.valueOf(unaPreg.getIdPregunta()), "textoNormalCentrado", laHoja);
                        columna++;
                        escribirCelda(columna, fila, unaPreg.getTextoPregunta(), "textoNormal", laHoja);
                        columna++;
                        Double unPuntaje;
                        //si el puntaje es cero o null pone cero
                        if (unaPreg.getPuntaje()!=null && unaPreg.getPuntaje()!=0 && unaPreg.getPuntaje()!=9 ) {
                            escribirCelda(columna, fila, unaPreg.getPuntaje().toString(), "textoNormalCentrado", laHoja);
                            columna++;
                            unPuntaje = ((unaPreg.getPuntaje() / 5.00) * 100);
                        } else if(unaPreg.getPuntaje()!=null && unaPreg.getPuntaje()==9) {
                            escribirCelda(columna, fila, "-", "textoNormalCentrado", laHoja);
                            columna++;
                            unPuntaje = (9.9);
                        }
                        else {
                            escribirCelda(columna, fila, "0", "textoNormalCentrado", laHoja);
                            columna++;
                            unPuntaje = (0.0);
                        }
                        String elPuntaje;
                        if (unPuntaje==9.9){
                            elPuntaje="-";
                            escribirCelda(columna, fila, elPuntaje, "textoNormalCentrado", laHoja);
                        }
                        else{
                            elPuntaje = df.format(unPuntaje);
                            escribirCelda(columna, fila, elPuntaje + "%", "textoNormalCentrado", laHoja);
                        }


                        columna++;
                        escribirCelda(columna, fila, unaPreg.getComentario(), "textoNormal", laHoja);
                        columna++;
                        escribirCelda(columna, fila, "", "textoNormal", laHoja);
                        columna = 3;
                        fila++;
                        cantPreguntasTotales++;
                    }
                    columna = 0;
                    escribirCelda(1, fila, getResources().getString(R.string.totalSubitem) +String.valueOf(unItem.getIdItem()), "subTotal", laHoja);
                    laHoja.mergeCells(1, fila, 4, fila);
                    if (unItem.getPuntajeItem()!=9.9) {
                        Double punItemDouble = (unItem.getPuntajeItem() / 5) * 100;
                        String puntItemStr = df.format(punItemDouble);
                        escribirCelda(5, fila, puntItemStr + "%", "subTotal", laHoja);
                    } else {
                        escribirCelda(5, fila, "-", "subTotal", laHoja);
                    }
                    laHoja.mergeCells(5, fila, 8, fila);
                    columna = 1;
                    fila++;
                }

                laHoja.mergeCells(0, fila - cantPreguntasTotales - cantItem, 0, fila - 1);
                columna = 0;

                escribirCelda(0, fila, getResources().getString(R.string.totalEse) + " " + unaEse.getIdEse() + "S", "subTotalEse", laHoja);
                laHoja.mergeCells(0, fila, 4, fila);

                if (unaEse.getPuntajeEse()!=9.9) {
                    Double puntEseDouble = (unaEse.getPuntajeEse() / 5) * 100;
                    String puntEseStr = df.format(puntEseDouble);
                    escribirCelda(5, fila, puntEseStr + "%", "subTotalEse", laHoja);
                } else {
                    escribirCelda(5, fila, "-", "subTotalEse", laHoja);
                }
                laHoja.mergeCells(5, fila, 8, fila);
                columna = 0;
                fila++;
            }

            escribirCelda(0, fila, getResources().getString(R.string.totalAudit), "totalAudit", laHoja);
            laHoja.mergeCells(0, fila, 4, fila);

            if (mAudit.getPuntajeFinal()!=9.9) {
                Double puntFinalDouble = mAudit.getPuntajeFinal() * 100.00;
                String puntFinal = df.format(puntFinalDouble);
                escribirCelda(5, fila, puntFinal + "%", "totalAudit", laHoja);
            } else {
                escribirCelda(5, fila,  "-", "totalAudit", laHoja);
            }

            laHoja.mergeCells(5, fila, 8, fila);
            columna = 0;
            fila = 0;

            //mando el mail


        } catch (Exception e) {
            e.printStackTrace();
        }

        //popular imagenes
        columna = 0;
        fila = 0;

        try {
            escribirCelda(columna, fila, "S", "titulo", laHojaFotos);
            columna = columna + 1;
            escribirCelda(columna, fila, getResources().getString(R.string.titItem), "titulo", laHojaFotos);
            columna = columna + 1;
            escribirCelda(columna, fila, getResources().getString(R.string.titPregunta), "titulo", laHojaFotos);
            columna = columna + 1;
            escribirCelda(columna, fila, getResources().getString(R.string.titPuntaje), "titulo", laHojaFotos);
            columna++;

            columna = 0;
            fila = 1;
            laHojaFotos.setColumnView(1, 32);
            laHojaFotos.setColumnView(2, 32);

            for (Ese unaEse : mAudit.getListaEses()
                    ) {
                //pongo la S en la primera columna

                for (Item unItem : unaEse.getListaItem()
                        ) {
                    for (Pregunta unaPregunta : unItem.getListaPreguntas()
                            ) {
                        if (unaPregunta.getListaFotos() != null && unaPregunta.getListaFotos().size() > 0) {
                            //ESCRIBO LA ESE
                            escribirCelda(columna, fila, unaEse.getIdEse() + "S", "textoNormal2", laHojaFotos);
                            columna = columna + 1;
                            //ESCRIBO EL ITEM
                            escribirCelda(columna, fila, unItem.getTextoItem(), "textoNormal", laHojaFotos);
                            columna++;
                            //ESCRIBO LA PREGUNTA
                            escribirCelda(columna, fila, unaPregunta.getTextoPregunta(), "textoNormal", laHojaFotos);
                            columna++;
                            if (unaPregunta.getPuntaje()==null) {

                                escribirCelda(columna, fila, "-", "textoNormalCentrado", laHojaFotos);

                            } else {

                                escribirCelda(columna, fila, unaPregunta.getPuntaje().toString(), "textoNormalCentrado", laHojaFotos);
                            }
                            columna++;

                            laHojaFotos.mergeCells(0, fila, 0, fila + altoFoto + 2);
                            laHojaFotos.mergeCells(1, fila, 1, fila + altoFoto + 2);
                            laHojaFotos.mergeCells(2, fila, 2, fila + altoFoto + 2);
                            laHojaFotos.mergeCells(3, fila, 3, fila + altoFoto + 2);
                            //PEGO LAS FOTOS5
                            Integer contador = 1;
                            for (Foto unaFoto : unaPregunta.getListaFotos()
                                    ) {
//                                separacion entre fotos
                                laHojaFotos.setColumnView(columna, 4);
                                laHojaFotos.setColumnView(columna + anchoFoto + 1, 4);
                                //ESCRIBO EL TITULO

                                columna = columna + 1;

                                WritableImage imagen = new WritableImage(
                                        columna, fila + 1, anchoFoto, altoFoto, new File(unaFoto.getRutaFoto())); //Supports only 'png' images
                                laHojaFotos.addImage(imagen);


                                escribirCelda(columna - 1, 0, getResources().getString(R.string.titFotos) + " " + contador.toString(), "titulo", laHojaFotos);
                                laHojaFotos.mergeCells(columna - 1, 0, columna + 5, 0);


//                                escribo el comentario de la foto y mergeo las celdas esas
                                escribirCelda(columna, fila + altoFoto + 1, unaFoto.getComentarioFoto(), "textoNormalCentradoSinBorde", laHojaFotos);
                                laHojaFotos.mergeCells(columna, fila + altoFoto + 1, columna + anchoFoto - 1, fila + altoFoto + 1);
                                dibujarBordesFoto(columna - 1, fila);
                                columna = columna + anchoFoto + 1;

                                contador++;
                            }
                            fila = fila + altoFoto + 3;
                            columna = 0;

                        }

                    }

                }
            }

            elLibro.write();
            elLibro.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        mandarExcelPorMail("5S Report-" + mAudit.getAreaAuditada().getNombreArea() + "-" + FuncionesPublicas.dameFechaString(mAudit.getFechaAuditoria(), "corta") + ".xls");

    }



    private class EnviarXLS extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... args) {
            crearExcel();
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

}



