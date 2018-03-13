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
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import crl.android.pdfwriter.PDFWriter;
import crl.android.pdfwriter.PaperSize;
import crl.android.pdfwriter.StandardFonts;
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
    private FloatingActionButton fabGenerarXLS;
    Boolean auditEstaCompleta;

    private ProgressBar progressBar;
    private Double promedioSeiketsu;
    private Double promedioShitsuke;

    private PDFWriter writer;

    private File fotoComprimida;
    private Bitmap fotoOriginal;

   private WritableSheet laHojaFotos;

    public static final int MARGEN_IZQUIERDO = 25;
    public static final int SALTO_LINEA = 14;
    public static final int SEPARACIONFOTOS = 7;
    private static final int letraTitulo=20;
    private static final int letraPreguntas=10;
    private static final int anchoFoto=5;
    private static final int altoFoto=10;


    //para pdf
    Integer cursorX;
    Integer cursorY;
    //para excel
    Integer fila;
    Integer columna;

    private DatabaseReference mDatabase;

    private SharedPreferences config;

    private Integer sumatoriaPreguntas;
    private Integer divisorPreguntas;

    private Double sumatoriaItems;
    private Integer divisorItems;

    private Double sumatoriaEse;
    private Integer divisorEse;

    private Auditoria auditActual;
    private DecimalFormat df;

    private Integer cantItem;
    private Integer cantPreguntasTotales;








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

        df = new DecimalFormat("#.##");

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

        fabEditarAuditoria.setLabelColors(ContextCompat.getColor(this, R.color.tutorial1),
                ContextCompat.getColor(this, R.color.light_grey),
                ContextCompat.getColor(this, R.color.white_transparent));
        fabEditarAuditoria.setLabelTextColor(ContextCompat.getColor(this, R.color.primary_text));

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

        fabGenerarPDF = new FloatingActionButton(this);
        fabGenerarPDF.setColorNormal(ContextCompat.getColor(this, R.color.tile3));
        fabGenerarPDF.setButtonSize(FloatingActionButton.SIZE_MINI);
        fabGenerarPDF.setLabelText(getString(R.string.generarPDF));
        fabGenerarPDF.setImageResource(R.drawable.ic_insert_drive_file_black_24dp);
        fabMenuGraficos.addMenuButton(fabGenerarPDF);

        fabGenerarPDF.setLabelColors(ContextCompat.getColor(this, R.color.tile2),
                ContextCompat.getColor(this, R.color.light_grey),
                ContextCompat.getColor(this, R.color.white_transparent));
        fabGenerarPDF.setLabelTextColor(ContextCompat.getColor(this, R.color.primary_text));

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



        fabGenerarXLS = new FloatingActionButton(this);
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
                   new EnviarXLS().execute();
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

        fabQuit = new FloatingActionButton(this);
        fabQuit.setColorNormal(ContextCompat.getColor(this, R.color.tile3));
        fabQuit.setButtonSize(FloatingActionButton.SIZE_MINI);
        fabQuit.setLabelText(getString(R.string.quit));
        fabQuit.setImageResource(R.drawable.ic_exit_to_app_black_24dp);
        fabMenuGraficos.addMenuButton(fabQuit);

        fabQuit.setLabelColors(ContextCompat.getColor(this, R.color.tile2),
                ContextCompat.getColor(this, R.color.light_grey),
                ContextCompat.getColor(this, R.color.white_transparent));
        fabQuit.setLabelTextColor(ContextCompat.getColor(this, R.color.primary_text));

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
        auditActual=laAudit;

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
        bundle.putBoolean(FragmentRadar.COMPLETO,laAudit.getAuditEstaCerrada());

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
                                Double auxPuntaje = sumatoriaPreguntas*1.00;
                                Double auxDivisor=divisorPreguntas*1.00;
                                unItem.setPuntajeItem(auxPuntaje/auxDivisor);
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
        }
        else{
            sePudo= false;
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
        writer.addText(cursorX, PaperSize.LETTER_HEIGHT - MARGEN_IZQUIERDO, letraTitulo, getResources().getString(R.string.tituloPdf));
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
        writer.addText(PaperSize.LETTER_WIDTH - 4 * MARGEN_IZQUIERDO, PaperSize.LETTER_HEIGHT - (75), letraPreguntas, getResources().getString(R.string.ResultadoFinal));


        //agrego puntaje final
        Locale locale = new Locale("en", "US");
        NumberFormat format = NumberFormat.getPercentInstance(locale);
        String percentage1 = format.format(unAudit.getPuntajeFinal());
        writer.addText(MARGEN_IZQUIERDO, PaperSize.LETTER_HEIGHT - (85 + SALTO_LINEA), letraPreguntas, getResources().getString(R.string.puntajeFinal) + percentage1);

        View rootView = this.getWindow().getDecorView().findViewById(android.R.id.content);
        View v = rootView.findViewById(R.id.contenedorGraficos);

        Bitmap unBitmap = screenShot(v);
        Bitmap scaledBitmap = scaleSinRotar(unBitmap, 250);
        // Bitmap SunBitmap=Bitmap.createScaledBitmap(unBitmap, 300,510,false);
        writer.addImage(MARGEN_IZQUIERDO, PaperSize.LETTER_HEIGHT - 85 - SALTO_LINEA - scaledBitmap.getHeight() - SEPARACIONFOTOS, scaledBitmap);

    }


    public void enviarPDF() {

        Realm realm = Realm.getDefaultInstance();
        RealmResults<Item> itemsSeiri =realm.where(Item.class)
                .equalTo("idAudit",idAudit)
                .beginsWith("idItem","1")
                .findAll();
        RealmResults<Item> ItemsSeiton =realm.where(Item.class)
                .equalTo("idAudit",idAudit)
                .beginsWith("idItem","2")
                .findAll();
        RealmResults<Item> ItemsSeiso =realm.where(Item.class)
                .equalTo("idAudit",idAudit)
                .beginsWith("idItem","3")
                .findAll();
        RealmResults<Item> ItemsSeiketsu =realm.where(Item.class)
                .equalTo("idAudit",idAudit)
                .beginsWith("idItem","4")
                .findAll();
        RealmResults<Item> ItemsShitsuke =realm.where(Item.class)
                .equalTo("idAudit",idAudit)
                .beginsWith("idItem","5")
                .findAll();
        Auditoria mAudit=realm.where(Auditoria.class)
                .equalTo("idAuditoria",idAudit)
                .findFirst();


        List<Item> unListaSeiri = new ArrayList<>();
        unListaSeiri.addAll(itemsSeiri);
        List<Item> unListaSeiton = new ArrayList<>();
        unListaSeiton.addAll(ItemsSeiton);
        List<Item> unListaSeiso = new ArrayList<>();
        unListaSeiso.addAll(ItemsSeiso);
        List<Item> unListaSeiketsu = new ArrayList<>();
        unListaSeiketsu.addAll(ItemsSeiketsu);
        List<Item> unListaShitsuke = new ArrayList<>();
        unListaShitsuke.addAll(ItemsShitsuke);

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

    public void crearPdfEse(List<Item> laLista) {

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
        writer.addText(MARGEN_IZQUIERDO, PaperSize.LETTER_HEIGHT - MARGEN_IZQUIERDO, letraTitulo, getResources().getString(R.string.tituloPdf));
        cursorY = cursorY - MARGEN_IZQUIERDO;
        cursorX = cursorX + MARGEN_IZQUIERDO;
        //fuente fecha escribir fecga
        writer.addText(cursorX, cursorY - SALTO_LINEA, letraPreguntas, getResources().getString(R.string.fecha) + mAudit.getFechaAuditoria());
        cursorY = cursorY - SALTO_LINEA;
        writer.setFont(StandardFonts.SUBTYPE, StandardFonts.HELVETICA, StandardFonts.WIN_ANSI_ENCODING);
        //ESCRIBO LA S
        writer.addText(PaperSize.LETTER_WIDTH - 4 * MARGEN_IZQUIERDO, cursorY, letraTitulo, laLista.get(0).getIdItem().substring(0,1)+"S");

        //linea separacion
        writer.addLine(MARGEN_IZQUIERDO, PaperSize.LETTER_HEIGHT - (65), PaperSize.LETTER_WIDTH - MARGEN_IZQUIERDO, PaperSize.LETTER_HEIGHT - (65));
        cursorY = PaperSize.LETTER_HEIGHT - 65;
        cursorY = cursorY - SALTO_LINEA;
        cursorX = MARGEN_IZQUIERDO;
//        EMPIEZO A RECORRER LOS SUBITEMS
        recorrerSubitemLista(laLista, cursorX, cursorY, mAudit.getFechaAuditoria());
    }

    private void determinaSiEntranDatosVerticalmente(String ese){
        if (cursorY < 2 * MARGEN_IZQUIERDO) {
            crearNuevaPagina(ese);
        }
    }

    private void recorrerSubitemLista(List<Item> laLista, int x, int y, String fecha) {
        cursorX = x;
        cursorY = y;
        Realm realm  = Realm.getDefaultInstance();

        determinaSiEntranDatosVerticalmente(laLista.get(0).getIdItem().substring(0,1));
        
        for (Item sub : laLista) {
            determinaSiEntranDatosVerticalmente(laLista.get(0).getIdItem().substring(0,1));
            String criterio=sub.getCriterio();
            String textoItem=sub.getTextoItem();
            writer.addText(cursorX, cursorY - SALTO_LINEA, letraPreguntas, getResources().getString(R.string.criterio)+" "+sub.getCriterio());
            cursorY = cursorY - SALTO_LINEA;
            writer.addText(cursorX, cursorY - SALTO_LINEA, letraPreguntas, getResources().getString(R.string.item) + " "+ sub.getTextoItem());
            Double saltoLinea = 1.5 * SALTO_LINEA;
            Integer saltoLineaInt= saltoLinea.intValue();
            cursorY = cursorY - saltoLineaInt;


            cursorX = MARGEN_IZQUIERDO;
            cursorY = cursorY - (SEPARACIONFOTOS / 2);

            for (Pregunta unaPreg :
                    sub.getListaPreguntas()) {
                    determinaSiEntranDatosVerticalmente(laLista.get(0).getIdItem().substring(0,1));

                    cursorX=MARGEN_IZQUIERDO+(MARGEN_IZQUIERDO/2);

                if (unaPreg.getPuntaje()!=null ) {
                    writer.addText(cursorX, cursorY - SALTO_LINEA, letraPreguntas, unaPreg.getTextoPregunta());
                    cursorY = cursorY - SALTO_LINEA;
                    writer.addText(cursorX, cursorY - SALTO_LINEA, letraPreguntas, getResources().getString(R.string.score) + unaPreg.getPuntaje().toString());
                    Double saltoLineaD = 1.5 * SALTO_LINEA;
                    Integer saltoLineaIntD= saltoLinea.intValue();
                    cursorY = cursorY - saltoLineaInt;
                }
                else{
                    writer.addText(cursorX, cursorY - SALTO_LINEA, letraPreguntas, unaPreg.getTextoPregunta());
                    cursorY = cursorY - SALTO_LINEA;
                    writer.addText(cursorX, cursorY - SALTO_LINEA, letraPreguntas, getResources().getString(R.string.score) + " 0");
                    Double saltoLineaA = 1.5 * SALTO_LINEA;
                    Integer saltoLineaIntB= saltoLinea.intValue();
                    cursorY = cursorY - saltoLineaInt;

                }

                //renglonesFoto=Math.round(sub.getListaFotos().size()/3);
                cursorX=MARGEN_IZQUIERDO;
                for (Foto foto : unaPreg.getListaFotos()
                        ) {
                    Foto unaFoto = foto;
                    File unFile = new File(unaFoto.getRutaFoto());
                    BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                    bmOptions.inScaled = false;
                    Bitmap rawBitmap = BitmapFactory.decodeFile(unFile.getAbsolutePath(), bmOptions);
                    Bitmap bitmapScaled = scaleBitmap(rawBitmap, 270);

//              SI LA FOTO NO ENTRA EN LO QUE QUEDA DE PAGINA
                    if (cursorY - bitmapScaled.getHeight() < SALTO_LINEA) {
                        //si la imagen no entra armo una pagina nueva
                        //vuelvo a poner los titulos y demas
                        crearNuevaPagina(laLista.get(0).getIdItem().substring(0,1));

                        //FINALMENTE AGREGO LA FOTO
                        writer.addImage(cursorX, cursorY - bitmapScaled.getHeight(), bitmapScaled);
                        writer.addText(cursorX, cursorY - bitmapScaled.getHeight() - SEPARACIONFOTOS, letraPreguntas, unaFoto.getComentarioFoto());
                        //CURSOR X = MARGEN MAS ANCHO FOTO
                        cursorX = cursorX + bitmapScaled.getWidth();
                        //CURSOR Y = POSICION ACTUAL MENOS ALTO FOTO
                    }
                    else {
//                      HAY LUGAR HACIA ABAJO, CHEQUEO QUE ENTRE HORIZONTALMENTE
                        if (cursorX + bitmapScaled.getWidth() > 612 - MARGEN_IZQUIERDO) {
//                            NO ENTRA
//                            LA PONGO ABAJO
                            cursorX = MARGEN_IZQUIERDO;
                            //POSICIONE EL CURSOR INMEDIATAMENTE DEBAJO DE LA FOTO DE ARRIBA Y LE AGREGUE UN SALTO DE LINEA PARA QUE NO QUEDEN PEGADAS LAS FOTOS
                            cursorY = cursorY - bitmapScaled.getHeight()-SALTO_LINEA;
                            writer.addImage(cursorX, cursorY-bitmapScaled.getHeight(), bitmapScaled);
//                            AGREGO SALTO DE LINEA AL ALTO DE LA IMAGEN PARA QUE QUEDE UNA SEPARACION
                            writer.addText(cursorX, cursorY-bitmapScaled.getHeight()-SEPARACIONFOTOS , letraPreguntas, unaFoto.getComentarioFoto());
                            cursorX = cursorX + bitmapScaled.getWidth();


                        } else {
                                if (cursorX == MARGEN_IZQUIERDO) {
                                    writer.addImage(cursorX, cursorY - bitmapScaled.getHeight(), bitmapScaled);
                                    writer.addText(cursorX, cursorY - bitmapScaled.getHeight() - SEPARACIONFOTOS, letraPreguntas, unaFoto.getComentarioFoto());
                                    cursorX = cursorX + bitmapScaled.getWidth();

                                } else {
    //                              ENTRA EN LA MISMA LINEA
                                    writer.addImage(cursorX + SEPARACIONFOTOS, cursorY-bitmapScaled.getHeight(), bitmapScaled);
                                    writer.addText(cursorX + SEPARACIONFOTOS, cursorY -bitmapScaled.getHeight()- SEPARACIONFOTOS, letraPreguntas, unaFoto.getComentarioFoto());
                                    cursorX = cursorX + SEPARACIONFOTOS + bitmapScaled.getWidth();
                                }
                        }
                    }
                }

//                DEJO EL CURSOR Y LISTO PARA SEGUIR AGREGANDO COSAS
                if (unaPreg.getListaFotos().size()>0) {
                    cursorY =cursorY-152-SALTO_LINEA;
                    cursorX=MARGEN_IZQUIERDO;
                }
            }
        }
    }
public void crearNuevaPagina(String ese){
        Realm realm = Realm.getDefaultInstance();
        Auditoria nAudit= realm.where(Auditoria.class)
                .equalTo("idAuditoria",idAudit)
                .findFirst();
    writer.newPage();
    cursorX = 0;
    cursorY = 792;
    //fuente titulo
    writer.setFont(StandardFonts.SUBTYPE, StandardFonts.HELVETICA, StandardFonts.WIN_ANSI_ENCODING);
    //escribir titulo
    writer.addText(MARGEN_IZQUIERDO, PaperSize.LETTER_HEIGHT - MARGEN_IZQUIERDO, letraTitulo, getResources().getString(R.string.tituloPdf));
    cursorY = cursorY - MARGEN_IZQUIERDO;
    cursorX = cursorX + MARGEN_IZQUIERDO;
    //fuente fecha escribir fecga
    writer.addText(cursorX, cursorY - SALTO_LINEA, letraPreguntas, getResources().getString(R.string.fecha) + nAudit.getFechaAuditoria());
    cursorY = cursorY - SALTO_LINEA;
    writer.setFont(StandardFonts.SUBTYPE, StandardFonts.HELVETICA, StandardFonts.WIN_ANSI_ENCODING);
    //ESCRIBO LA S
    writer.addText(PaperSize.LETTER_WIDTH - 4 * MARGEN_IZQUIERDO, cursorY, letraTitulo, ese+"S");

    //linea separacion
    writer.addLine(MARGEN_IZQUIERDO, PaperSize.LETTER_HEIGHT - (65), PaperSize.LETTER_WIDTH - MARGEN_IZQUIERDO, PaperSize.LETTER_HEIGHT - (65));
    cursorY = PaperSize.LETTER_HEIGHT - 65;
    cursorY = cursorY - SALTO_LINEA;
    cursorX = MARGEN_IZQUIERDO;
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

     public WritableWorkbook crearLibroExcel(String fileName){
         //exports must use a temp file while writing to avoid memory hogging
         WorkbookSettings wbSettings = new WorkbookSettings();
         wbSettings.setUseTemporaryFileDuringWrite(true);

         //add on the your app's path
         File dir = new File(getExternalFilesDir(null) + File.separator + "nomad" + File.separator + "audit5s" + File.separator + FirebaseAuth.getInstance().getCurrentUser().getEmail() + File.separator + "audits");
         //make them in case they're not there
         if (FuncionesPublicas.hayPermisoParaEscribir(this,fabMenuGraficos)) {
             if (!dir.exists()) {
                 dir.mkdirs();
             }
         }
         else{
             Toast.makeText(this, getResources().getString(R.string.errorRevisarPermisos), Toast.LENGTH_SHORT).show();
         }
         //create a standard java.io.File object for the Workbook to use
         File wbfile = new File(dir,fileName);

         WritableWorkbook wb = null;

         try{
             //create a new WritableWorkbook using the java.io.File and
             //WorkbookSettings from above
             wb = Workbook.createWorkbook(wbfile,wbSettings);
         }catch(IOException ex){

         }
         return wb;
     }

    public WritableSheet crearHoja(WritableWorkbook wb,
                                     String sheetName, int sheetIndex){
        //create a new WritableSheet and return it
        return wb.createSheet(sheetName, sheetIndex);
    }

    public void escribirCelda(int columnPosition, int rowPosition, String contents, String headerCell,
                          WritableSheet sheet) throws RowsExceededException, WriteException {
        //create a new cell with contents at position
        Label newCell = new Label(columnPosition,rowPosition,contents);

        if (headerCell.equals("titulo")){
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
        if (headerCell.equals("subTotal")){
            //give header cells size 10 Arial bolded
            WritableFont headerFont = new WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD);
            WritableCellFormat headerFormat = new WritableCellFormat(headerFont);
            //center align the cells' contents
            headerFormat.setAlignment(Alignment.LEFT);
            headerFormat.setBackground(Colour.VERY_LIGHT_YELLOW);
            headerFormat.setBorder(Border.ALL, BorderLineStyle.THIN);
            newCell.setCellFormat(headerFormat);
        }

        if (headerCell.equals("subTotalEse")){
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

        if (headerCell.equals("totalAudit")){
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
        if (headerCell.equals("textoNormal")){
            //give header cells size 10 Arial bolded
            WritableFont headerFont = new WritableFont(WritableFont.ARIAL, 10);
            WritableCellFormat headerFormat = new WritableCellFormat(headerFont);
            //center align the cells' contents
            headerFormat.setVerticalAlignment(VerticalAlignment.CENTRE);
            headerFormat.setWrap(true);
            headerFormat.setBorder(Border.ALL, BorderLineStyle.THIN);
            newCell.setCellFormat(headerFormat);
        }
        if (headerCell.equals("textoNormal2")){
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
        if (headerCell.equals("textoNormalCentrado")){
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
        if (headerCell.equals("textoNormalCentradoSinBorde")){
            //give header cells size 10 Arial bolded
            WritableFont headerFont = new WritableFont(WritableFont.ARIAL, 10);
            WritableCellFormat headerFormat = new WritableCellFormat(headerFont);
            //center align the cells' contents
            headerFormat.setVerticalAlignment(VerticalAlignment.CENTRE);
            headerFormat.setAlignment(Alignment.CENTRE);
            headerFormat.setWrap(true);
            newCell.setCellFormat(headerFormat);
        }
        if (headerCell.equals("bordeInferior")){
            WritableFont headerFont = new WritableFont(WritableFont.ARIAL, 10);
            WritableCellFormat headerFormat = new WritableCellFormat(headerFont);

            headerFormat.setBorder(Border.BOTTOM,BorderLineStyle.THIN);
            headerFormat.setWrap(true);
            newCell.setCellFormat(headerFormat);
        }
        if (headerCell.equals("bordeLateral")){
            WritableFont headerFont = new WritableFont(WritableFont.ARIAL, 10);
            WritableCellFormat headerFormat = new WritableCellFormat(headerFont);

            headerFormat.setBorder(Border.RIGHT,BorderLineStyle.THIN);
            headerFormat.setWrap(true);
            newCell.setCellFormat(headerFormat);
        }
        if (headerCell.equals("ultimaCelda")){
            WritableFont headerFont = new WritableFont(WritableFont.ARIAL, 10);
            WritableCellFormat headerFormat = new WritableCellFormat(headerFont);
            headerFormat.setBorder(Border.RIGHT,BorderLineStyle.THIN);
            headerFormat.setBorder(Border.BOTTOM,BorderLineStyle.THIN);
            headerFormat.setWrap(true);
            newCell.setCellFormat(headerFormat);
        }
        if (headerCell.equals("bordeSuperior")){
            WritableFont headerFont = new WritableFont(WritableFont.ARIAL, 10);
            WritableCellFormat headerFormat = new WritableCellFormat(headerFont);

            headerFormat.setBorder(Border.TOP,BorderLineStyle.THIN);
            headerFormat.setWrap(true);
            newCell.setCellFormat(headerFormat);
        }
        if (headerCell.equals("bordeSuperiorUltimaCelda")){
            WritableFont headerFont = new WritableFont(WritableFont.ARIAL, 10);
            WritableCellFormat headerFormat = new WritableCellFormat(headerFont);

            headerFormat.setBorder(Border.TOP,BorderLineStyle.THIN);
            headerFormat.setBorder(Border.RIGHT,BorderLineStyle.THIN);
            headerFormat.setWrap(true);
            newCell.setCellFormat(headerFormat);
        }

        sheet.addCell(newCell);
    }

    public void dibujarBordesFoto(Integer numColumna, Integer numFila){
        try {
//            dibujarBordesFoto
            for(Integer i=0;i<7;i++){
                escribirCelda(numColumna+i, numFila+altoFoto+2,"","bordeInferior",laHojaFotos);
            }
            for(Integer i=0;i<7;i++){
                if (i==6) {
                    escribirCelda(numColumna+i, numFila,"","bordeSuperiorUltimaCelda",laHojaFotos);
                } else {
                    escribirCelda(numColumna+i, numFila,"","bordeSuperior",laHojaFotos);
                }
            }
//            linea lateral
            for(Integer i=0;i<13;i++){
                if (i==12) {
                    escribirCelda(numColumna+anchoFoto+1, numFila+i,"","ultimaCelda",laHojaFotos);
                } else {
                    escribirCelda(numColumna+anchoFoto+1, numFila+i,"","bordeLateral",laHojaFotos);
                }
            }
        } catch (WriteException e) {
            e.printStackTrace();
        }
    }

    public void crearExcel(){
        fila=0;
        columna=0;
        Realm realm = Realm.getDefaultInstance();
        Auditoria mAudit=realm.where(Auditoria.class)
                .equalTo("idAuditoria",idAudit)
                .findFirst();

//        CREO EL LIBRO CON EL NOMBRE AREA+FECHA

        WritableWorkbook elLibro=crearLibroExcel("5S Report-" + mAudit.getAreaAuditada().getNombreArea() + "-" + mAudit.getFechaAuditoria() + ".xls");
        WritableSheet laHoja=crearHoja(elLibro,getResources().getString(R.string.resultados),0);
        laHojaFotos=crearHoja(elLibro,getResources().getString(R.string.tabImagenes),1);
        

        laHoja.setColumnView(1,32);
        laHoja.setColumnView(2,32);
        laHoja.setColumnView(4,32);
        laHoja.setColumnView(7,32);
        laHoja.setColumnView(3,11);

//        laHoja.setColumnView(23,520);
//        laHoja.setColumnView(57,520);
//        laHoja.setColumnView(84,520);
//        laHoja.setColumnView(110,520);
//        laHoja.setColumnView(134,520);
//        laHoja.setColumnView(135,520);



        //SETEO LOS TITULOS DE LA HOJA
        try {
            escribirCelda(columna,fila,"S","titulo",laHoja);
            columna=columna+1;
            escribirCelda(columna,fila,getResources().getString(R.string.titCriterio),"titulo",laHoja);
            columna=columna+1;
            escribirCelda(columna,fila,getResources().getString(R.string.titItem),"titulo",laHoja);
            columna=columna+1;
            escribirCelda(columna,fila,getResources().getString(R.string.titNumPregunta),"titulo",laHoja);
            columna=columna+1;
            escribirCelda(columna,fila,getResources().getString(R.string.titPregunta),"titulo",laHoja);
            columna=columna+1;
            escribirCelda(columna,fila,getResources().getString(R.string.titPuntaje),"titulo",laHoja);
            columna=columna+1;
            escribirCelda(columna,fila,getResources().getString(R.string.titPorcentaje),"titulo",laHoja);
            columna=columna+1;
            escribirCelda(columna,fila,getResources().getString(R.string.titComentario),"titulo",laHoja);
            columna=columna+1;
            escribirCelda(columna,fila,getResources().getString(R.string.titFotos),"titulo",laHoja);
            columna=0;
            fila=1;


        for (Ese unaEse:
                mAudit.getListaEses()) {
//            escribo el numero de ese

            escribirCelda(columna,fila,unaEse.getIdEse() + "S","textoNormal2",laHoja);
            columna=columna+1;

            //recorro los item de la ese

            cantItem=unaEse.getListaItem().size();
            cantPreguntasTotales=0;

            for (Item unItem :
                    unaEse.getListaItem()) {
                Integer cantPreguntas=unItem.getListaPreguntas().size();
//
//                merge columna criterio
                laHoja.mergeCells(columna,fila,columna,fila+cantPreguntas-1);
                escribirCelda(columna,fila,unItem.getCriterio(),"textoNormal",laHoja);
                columna++;
//                merge columna item
               laHoja.mergeCells(columna,fila,columna,fila+cantPreguntas-1);
                escribirCelda(columna,fila,unItem.getTextoItem(),"textoNormal",laHoja);
                columna++;

                //recorro las preguntas del item
                for (Pregunta unaPreg:
                    unItem.getListaPreguntas() ) {

                    escribirCelda(columna,fila,unaPreg.getIdPregunta(),"textoNormalCentrado",laHoja);
                    columna++;
                    escribirCelda(columna,fila,unaPreg.getTextoPregunta(),"textoNormal",laHoja);
                    columna++;
                    escribirCelda(columna,fila,unaPreg.getPuntaje().toString(),"textoNormalCentrado",laHoja);
                    columna++;
                    Double unPuntaje =((unaPreg.getPuntaje()/5.00)*100);
                    String elPuntaje = df.format(unPuntaje);
                    escribirCelda(columna,fila,elPuntaje+"%","textoNormalCentrado",laHoja);
                    columna++;
                    escribirCelda(columna,fila,unaPreg.getComentario(),"textoNormal",laHoja);
                    columna++;
                    escribirCelda(columna,fila,"","textoNormal",laHoja);
                    columna=3;
                    fila++;
                    cantPreguntasTotales++;
                }
                columna=0;
                escribirCelda(1,fila,getResources().getString(R.string.totalSubitem )+ unItem.getIdItem(),"subTotal",laHoja);
               laHoja.mergeCells(1,fila,4,fila);
               Double punItemDouble=(unItem.getPuntajeItem()/5)*100;
               String puntItemStr=df.format(punItemDouble);
                escribirCelda(5,fila,puntItemStr+"%","subTotal",laHoja);
                laHoja.mergeCells(5,fila,8,fila);
                columna=1;
                fila++;
            }

            laHoja.mergeCells(0,fila-cantPreguntasTotales-cantItem,0,fila-1);
            columna=0;

            escribirCelda(0,fila,getResources().getString(R.string.totalEse )+" "+unaEse.getIdEse()+"S","subTotalEse",laHoja);
            laHoja.mergeCells(0,fila,4,fila);

            Double puntEseDouble=(unaEse.getPuntajeEse()/5)*100;

            String puntEseStr=df.format(puntEseDouble);
            escribirCelda(5,fila,puntEseStr+"%","subTotalEse",laHoja);
            laHoja.mergeCells(5,fila,8,fila);
            columna=0;
            fila++;
        }
            
            Double puntFinalDouble=mAudit.getPuntajeFinal()*100.00;
            String puntFinal= df.format(puntFinalDouble);
            escribirCelda(0,fila,getResources().getString(R.string.totalAudit ),"totalAudit",laHoja);
            laHoja.mergeCells(0,fila,4,fila);

            escribirCelda(5,fila,puntFinal+"%","totalAudit",laHoja);
            laHoja.mergeCells(5,fila,8,fila);
            columna=0;
            fila=0;

            //mando el mail


        } catch (Exception e) {
            e.printStackTrace();
        }

        //popular imagenes
        columna=0;
        fila=0;

        try {
            escribirCelda(columna,fila,"S","titulo",laHojaFotos);
            columna=columna+1;
            escribirCelda(columna,fila,getResources().getString(R.string.titItem),"titulo",laHojaFotos);
            columna=columna+1;
            escribirCelda(columna,fila,getResources().getString(R.string.titPregunta),"titulo",laHojaFotos);
            columna=columna+1;
            escribirCelda(columna,fila,getResources().getString(R.string.titPuntaje),"titulo",laHojaFotos);
            columna++;

            columna=0;
            fila=1;
            laHojaFotos.setColumnView(1,32);
            laHojaFotos.setColumnView(2,32);

            for (Ese unaEse: mAudit.getListaEses()
                 ) {
                //pongo la S en la primera columna

                for (Item unItem : unaEse.getListaItem()
                        ) {
                    for (Pregunta unaPregunta : unItem.getListaPreguntas()
                            ) {
                        if (unaPregunta.getListaFotos()!=null && unaPregunta.getListaFotos().size()>0) {
                            //ESCRIBO LA ESE
                            escribirCelda(columna, fila, unaEse.getIdEse() + "S", "textoNormal2", laHojaFotos);
                            columna = columna + 1;
                            //ESCRIBO EL ITEM
                            escribirCelda(columna, fila, unItem.getTextoItem(), "textoNormal", laHojaFotos);
                            columna++;
                            //ESCRIBO LA PREGUNTA
                            escribirCelda(columna,fila,unaPregunta.getTextoPregunta(),"textoNormal",laHojaFotos);
                            columna++;
                            escribirCelda(columna,fila,unaPregunta.getPuntaje().toString(),"textoNormalCentrado",laHojaFotos);
                            columna++;

                            laHojaFotos.mergeCells(0,fila,0,fila+altoFoto+2);
                            laHojaFotos.mergeCells(1,fila,1,fila+altoFoto+2);
                            laHojaFotos.mergeCells(2,fila,2,fila+altoFoto+2);
                            laHojaFotos.mergeCells(3,fila,3,fila+altoFoto+2);
                            //PEGO LAS FOTOS5
                            Integer contador=1;
                            for (Foto unaFoto:unaPregunta.getListaFotos()
                                 ) {
//                                separacion entre fotos
                                laHojaFotos.setColumnView(columna,4);
                                laHojaFotos.setColumnView(columna+anchoFoto+1,4);
                                //ESCRIBO EL TITULO

                                    columna = columna+1;

                                WritableImage image = new WritableImage(
                                        columna, fila+1,anchoFoto, altoFoto,new File(unaFoto.getRutaFoto())); //Supports only 'png' images
                                laHojaFotos.addImage(image);


                                escribirCelda(columna-1,0,getResources().getString(R.string.titFotos)+" "+contador.toString(),"titulo",laHojaFotos);
                                laHojaFotos.mergeCells(columna-1,0,columna+5,0);


//                                escribo el comentario de la foto y mergeo las celdas esas
                                escribirCelda(columna,fila+altoFoto+1,unaFoto.getComentarioFoto(),"textoNormalCentradoSinBorde",laHojaFotos);
                                laHojaFotos.mergeCells(columna,fila+altoFoto+1,columna+anchoFoto-1,fila+altoFoto+1);
                                dibujarBordesFoto(columna-1,fila);
                                columna = columna+anchoFoto+1;

                                contador++;
                            }
                            fila= fila+altoFoto+3;
                            columna=0;

                        }

                    }

                }
            }

            elLibro.write();
            elLibro.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        mandarExcelPorMail("5S Report-" + mAudit.getAreaAuditada().getNombreArea() + "-" + mAudit.getFechaAuditoria() + ".xls");

    }

 }



