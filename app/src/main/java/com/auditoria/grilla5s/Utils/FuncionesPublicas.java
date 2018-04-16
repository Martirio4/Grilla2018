package com.auditoria.grilla5s.Utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.auditoria.grilla5s.Model.Auditoria;
import com.auditoria.grilla5s.Model.Ese;
import com.auditoria.grilla5s.Model.Foto;
import com.auditoria.grilla5s.Model.Item;
import com.auditoria.grilla5s.Model.Pregunta;
import com.auditoria.grilla5s.R;
import com.auditoria.grilla5s.View.Fragments.FragmentPregunta;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import pl.aprilapps.easyphotopicker.EasyImage;
import pl.tajchert.nammu.Nammu;
import pl.tajchert.nammu.PermissionCallback;

import static com.auditoria.grilla5s.View.Activities.ActivityPreAuditoria.idAudit;

/**
 * Created by elmar on 15/1/2018.
 */

public class FuncionesPublicas {

    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    public static Boolean borrarAuditoriaSeleccionada(final String idAudit) {
        Realm realm = Realm.getDefaultInstance();

       realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {

                String usuario = FirebaseAuth.getInstance().getCurrentUser().getEmail();

                RealmResults<Pregunta> preguntas = realm.where(Pregunta.class)
                        .equalTo("idAudit", idAudit)
                        .findAll();
                preguntas.deleteAllFromRealm();

                RealmResults<Foto> fotos = realm.where(Foto.class)
                        .equalTo("idAudit", idAudit)
                        .findAll();
                for (Foto foti : fotos
                        ) {
                    File file = new File(foti.getRutaFoto());
                    boolean deleted = file.delete();
                }
                fotos.deleteAllFromRealm();

                RealmResults<Item> items = realm.where(Item.class)
                        .equalTo("idAudit", idAudit)
                        .findAll();
                items.deleteAllFromRealm();

                RealmResults<Ese> eses = realm.where(Ese.class)
                        .equalTo("idAudit", idAudit)
                        .findAll();
                eses.deleteAllFromRealm();

                Auditoria result2 = realm.where(Auditoria.class)
                        .equalTo("idAuditoria", idAudit)
                        .findFirst();

                result2.deleteFromRealm();
            }
        });
        return true;


    }

    public static void sumarUsoDeApp(Context context) {
        SharedPreferences config = context.getSharedPreferences("prefs", 0);
        Integer cantidadUsos = config.getInt("cantidadUsos", 0);

        SharedPreferences.Editor editor = config.edit();
        editor.putInt("cantidadUsos", cantidadUsos + 1);
        editor.commit();

    }

    public static Boolean hayPermisoParaEscribir(final Context context, View view) {
        final Boolean[] resultado = {false};
        if (Nammu.checkPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

            resultado[0] = true;
        } else {

//                      PIDO PERMISO PARA USAR LA MEMORIA EXTERNA

            if (Nammu.shouldShowRequestPermissionRationale((Activity) (context), android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                //User already refused to give us this permission or removed it
                //Now he/she can mark "never ask again" (sic!)
                Snackbar.make(view, (context).getResources().getString(R.string.appNecesitaPermiso),
                        Snackbar.LENGTH_INDEFINITE).setAction(context.getResources().getString(R.string.ok), new View.OnClickListener() {
                    @Override
                    public void onClick(final View view) {
                        Nammu.askForPermission((Activity) context, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                new PermissionCallback() {
                                    @Override
                                    public void permissionGranted() {
                                        resultado[0] = true;
                                    }

                                    @Override
                                    public void permissionRefused() {
                                        resultado[0] = false;
                                    }
                                });
                    }
                }).show();
            } else {
                //First time asking for permission
                // or phone doesn't offer permission
                // or user marked "never ask again"
                Nammu.askForPermission((Activity) context, android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        new PermissionCallback() {
                            @Override
                            public void permissionGranted() {
                                resultado[0] = true;
                            }

                            @Override
                            public void permissionRefused() {
                                resultado[0] = false;

                            }
                        });
            }
        }
        return resultado[0];
    }

    public static String dameFechaString(Date fecha, String largo) {
        SimpleDateFormat sdf;
        if (largo.equals("largo")) {
            sdf = new SimpleDateFormat("dd-MM-yyyy");
        } else {
            sdf = new SimpleDateFormat("dd-MM-yy");
        }
        return sdf.format(fecha);
    }

    public static Boolean completoTodosLosPuntos(final String idAudit) {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Pregunta> result2 = realm.where(Pregunta.class)
                .equalTo("idAudit", idAudit)
                .findAll();
        List<String> unaLista=new ArrayList<>();

        for (Pregunta unaPreg :result2
                ) {
            if (unaPreg.getPuntaje()==null){
                unaLista.add(unaPreg.getIdPregunta());
            }
        }
        //SI LA LISTA TIENE ELEMENTOS QUIERE DECIR QUE LA AUDITORIA NO ESTA COMPLETA
        if (unaLista.size()>0){
            return false;
        }
        //SI TODOS LOS PUNTOS ESTA COMPLERTOS
        else{
            //ME FIJO SI LA AUDITORIA ESTABA CERRADA
            Auditoria mAudit=realm.where(Auditoria.class)
                    .equalTo("idAuditoria",idAudit)
                    .findFirst();
            //SI NO ESTA CERRADA, LA CIERRO
            if (!mAudit.getAuditEstaCerrada()){
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        //BUSCO LA AUDITORIA ACTUAL
                        Auditoria estaAudit = realm.where(Auditoria.class)
                                .equalTo("idAuditoria", idAudit)
                                .findFirst();
                        //BUSCO TODAS LAS AUDITS QUE SON ULTIMAS
                        RealmResults<Auditoria> todasAudits =realm.where(Auditoria.class)
                                .equalTo("esUltimaAuditoria",true)
                                .findAll();
                        //ENTRE TODAS LAS ULTIMAS AUDITORIAS BUSCO LA QUE TIENE LA MISMA AREA QUE LA ACTUAL
                        for (Auditoria unAudit :
                                todasAudits) {
                            if (unAudit.getAreaAuditada().getIdArea().equals(estaAudit.getAreaAuditada().getIdArea())){
                                unAudit.setEsUltimaAuditoria(false);
                            }
                        }
                        //SETEO LA AUDITORIA ACTUAL COMO CERRADA Y ULTIMA AUDITORIA
                        estaAudit.setEsUltimaAuditoria(true);
                        estaAudit.setAuditEstaCerrada(true);
                    }
                });

            }
            return true;
        }
    }


    public static void subirAFireBase(String idAudit){
        new Subidor().execute(idAudit);
    }

    public static class Subidor extends AsyncTask< String, Void, Void> {

        @Override
        protected Void doInBackground(String... args) {
            String laAudit=args[0];
            subirAuditFirebase(laAudit);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }
    }

    private static void subirAuditFirebase( String idAudit) {
        //TRAIGO LA AUDITORIA
        Realm realm = Realm.getDefaultInstance();
        Auditoria mAudit= realm.where(Auditoria.class)
                .equalTo("idAuditoria", idAudit)
                .findFirst();
        //BUSCO LA BASE DE DATOS
        FirebaseDatabase mdatabase = FirebaseDatabase.getInstance();
        DatabaseReference reference=mdatabase.getReference().child("auditorias").child(idAudit);

        reference.child("id").setValue(idAudit);
        reference.child("fecha").setValue(FuncionesPublicas.dameFechaString(mAudit.getFechaAuditoria(),"largo"));
        reference.child("usuario").setValue(FirebaseAuth.getInstance().getCurrentUser().getEmail());
        reference.child("idarea").setValue(mAudit.getAreaAuditada().getIdArea());
        reference.child("nombrearea").setValue(mAudit.getAreaAuditada().getNombreArea());
        reference.child("punt1S").setValue(mAudit.getListaEses().get(0).getPuntajeEse());
        reference.child("punt2S").setValue(mAudit.getListaEses().get(1).getPuntajeEse());
        reference.child("punt3S").setValue(mAudit.getListaEses().get(2).getPuntajeEse());
        reference.child("punt4S").setValue(mAudit.getListaEses().get(3).getPuntajeEse());
        reference.child("punt5S").setValue(mAudit.getListaEses().get(4).getPuntajeEse());
        reference.child("puntajeFinal").setValue(mAudit.getPuntajeFinal());


    }
    public static void calcularPuntajesAuditoria(final String idAudit) {

        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(@NonNull Realm realm) {
                Auditoria mAudit = realm.where(Auditoria.class)
                        .equalTo("idAuditoria", idAudit)
                        .findFirst();

                if (mAudit != null) {
                    Double sumatoriaEse = 0.0;
                    Integer divisorEse = 0;
                    for (Ese unaEse : mAudit.getListaEses()) {
                       Double sumatoriaItems = 0.0;
                        Integer divisorItems = 0;
                        for (Item unItem : unaEse.getListaItem()) {
                            Integer sumatoriaPreguntas = 0;
                            Integer divisorPreguntas = 0;
                            for (Pregunta unaPregunta : unItem.getListaPreguntas()) {
                                if (unaPregunta.getPuntaje() != null) {
                                    sumatoriaPreguntas = sumatoriaPreguntas + unaPregunta.getPuntaje();
                                } else {
                                   Boolean auditEstaCompleta = false;
                                }
                                divisorPreguntas++;
                            }
                            if (divisorPreguntas == 0) {
                                unItem.setPuntajeItem(0.0);
                            } else {
                                Double auxPuntaje = sumatoriaPreguntas * 1.00;
                                Double auxDivisor = divisorPreguntas * 1.00;
                                unItem.setPuntajeItem(auxPuntaje / auxDivisor);
                            }
                            sumatoriaItems = sumatoriaItems + unItem.getPuntajeItem();
                            divisorItems++;
                        }
                        unaEse.setPuntajeEse((sumatoriaItems / divisorItems));
                        sumatoriaEse = sumatoriaEse + unaEse.getPuntajeEse();
                        divisorEse++;
                    }
                    //DIVIDO POR 25 QUE ES EL 100%
                    mAudit.setPuntajeFinal(sumatoriaEse / 25);
                }

            }
        });


    }


}