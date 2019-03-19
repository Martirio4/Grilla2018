package com.nomad.mrg5s.Utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.InputType;
import android.view.View;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.nomad.mrg5s.Model.Area;
import com.nomad.mrg5s.Model.Auditoria;
import com.nomad.mrg5s.Model.Criterio;
import com.nomad.mrg5s.Model.Cuestionario;
import com.nomad.mrg5s.Model.Ese;
import com.nomad.mrg5s.Model.Foto;
import com.nomad.mrg5s.Model.Item;
import com.nomad.mrg5s.Model.Pregunta;
import com.nomad.mrg5s.R;
import com.nomad.mrg5s.View.Adapter.AdapterCuestionario;
import com.nomad.mrg5s.View.Adapter.AdapterItems;
import com.nomad.mrg5s.View.Adapter.AdapterPreguntas;
import com.nomad.mrg5s.View.Fragments.FragmentManageAreas;
import com.nomad.mrg5s.View.Fragments.FragmentSeleccionArea;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import pl.tajchert.nammu.Nammu;
import pl.tajchert.nammu.PermissionCallback;

/**
 * Created by elmar on 15/1/2018.
 */

public class FuncionesPublicas {

    public static final String PRIMERA_ESE ="1S SEIRI";
    public static final String  SEGUNDA_ESE="2S SEITON";
    public static final String  TERCERA_ESE="3S SEISO";
    public static final String  CUARTA_ESE="4S SEIKETSU";
    public static final String  QUINTA_ESE="5S SHITSUKE";

    public static final String REVISAR="REVISAR_AUDITORIA";
    public static final String NUEVA_AUDITORIA="NUEVA_AUDITORIA";
    public static final String EDITAR_CUESTIONARIO="EDITAR_CUESTIONARIO";
    public static final String FRAGMENTMANAGER_AREAS = "FRAGMENTMANAGER_AREAS";
    public static final String FRAGMENT_RADAR = "FRAGMENT_RADAR";
    public static final String FRAGMENT_GRAFICO_AREA = "FRAGMENT_GRAFICO_AREA";
    public static final String EDITAR_AUDITORIA = "EDITAR_AUDITORIA";
    public static final String FRAGMENT_GRAFICO_BARRAS = "FRAGMENT_BARRAS_APILADAS";
    public static final String MIS_AUDITORIAS = "MIS_AUDITORIAS";
    public static final String FRAGMENT_EDITOR_CUESTIONARIOS = "FRAGMENT_EDITOR_CUESTIONARIOS";
    public static final String FRAGMENT_ZOOM = "FRAGMENT_ZOOM";
    public static final String SELECCION_AREAS = "SELECCION_AREAS";
    public static final String MANAGE_AREAS = "MANAGE_AREAS";
    public static final String RANKING = "RANKING";
    public static final String AUDITORIA = "AUDITORIA";
    public static final String AREAS = "AREAS";

    public static final String ESTRUCTURA_SIMPLE="ESTRUCTURA_SIMPLE";
    public static final String ESTRUCTURA_ESTRUCTURADA="ESTRUCTURA_ESTRUCTURADA";

//    ID'S
    public static final String IDITEMS = "ITEM_";
    public static final String IDAREAS = "AREA_";
    public static final String IDESES = "ESE_";
    public static final String IDCRITERIOS = "CRIT_";
    public static final String IDCRITERIOS_DEFAULT = "CRITDEF_";
    public static final String IDCUESTIONARIOS = "CUE_";
    public static final String IDPREGUNTAS = "PREG_";
    public static final String FRAGMENT_VER_PREGUNTAS ="FRAGMENT_VER_PREGUNTAS";
    public static final String FRAGMENT_SELECCION_AREAS ="FRAGMENT_SELECCION_AREAS" ;
    public static final String FRAGMENT_LANDING = "FRAGMENT_LANDING";
    public static final String ID_AUDITORIA = "AUDIT_";
    public static final String SIMBOLO_ORDINAL = "°";
    public static final String IDCUESTIONARIOS_DEFAULT = "CUEDEF_";
    public static final double MAXIMO_PUNTAJE = 4.0;


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

                if (result2!=null) {
                    result2.deleteFromRealm();
                }
            }
        });
        return true;


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



    public static void agregarItem(final String idCuestionario, final Item unItem, final AdapterItems adapterItems){
        Realm realm = Realm.getDefaultInstance();
            realm.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(Realm bgRealm) {
                    Item mItem = bgRealm.copyToRealm(unItem);
                    String idEse=unItem.getIdEse();

                    final Ese laEse = bgRealm.where(Ese.class)
                            .equalTo("idCuestionario", idCuestionario)
                            .equalTo("idEse",idEse)
                            .findFirst();
                    if (laEse!=null){
                        laEse.addItem(mItem);
                    }
                }
            }
                    , new Realm.Transaction.OnSuccess() {
                        @Override
                        public void onSuccess() {
                            adapterItems.addItem(unItem);
                        }
                    }, new Realm.Transaction.OnError() {
                        @Override
                        public void onError(Throwable error) {
                            Toast.makeText(adapterItems.getContext(), adapterItems.getContext().getString(R.string.elItemNoSeAgrego), Toast.LENGTH_SHORT).show();
                        }
                    }


            );
    }




    public static Integer dameEseParaIdItem(Integer idItem){
        return Integer.parseInt(String.valueOf(idItem).substring(0,1));
    }

    public static void eliminarCuestionario(final String idCuestionario, final Context context) {
        Realm realm = Realm.getDefaultInstance();

        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm bgRealm) {
                RealmResults<Pregunta> lasPreguntas = bgRealm.where(Pregunta.class)
                        .equalTo("idCuestionario", idCuestionario)
                        .isNull("idAudit")
                        .findAll();
                lasPreguntas.deleteAllFromRealm();

                RealmResults<Item> losItem = bgRealm.where(Item.class)
                        .equalTo("idCuestionario", idCuestionario)
                        .isNull("idAudit")
                        .findAll();
                losItem.deleteAllFromRealm();

                RealmResults<Ese>lasEses=bgRealm.where(Ese.class)
                        .equalTo("idCuestionario", idCuestionario)
                        .isNull("idAudit")
                        .findAll();
                lasEses.deleteAllFromRealm();

                RealmResults<Criterio>losCriterios=bgRealm.where(Criterio.class)
                        .equalTo("idCuestionario", idCuestionario)
                        .isNull("idAudit")
                        .not()
                        .beginsWith("idCriterio", FuncionesPublicas.IDCRITERIOS_DEFAULT)
                        .findAll();
                losCriterios.deleteAllFromRealm();

                Cuestionario elCuestionario = bgRealm.where(Cuestionario.class)
                        .equalTo("idCuestionario", idCuestionario)
                        .findFirst();
                if (elCuestionario!=null){
                    elCuestionario.deleteFromRealm();
                }
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                Toast.makeText(context, context.getString(R.string.cuestionarioEliminado), Toast.LENGTH_SHORT).show();
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                Toast.makeText(context, context.getString(R.string.cuestionarioNoEliminado), Toast.LENGTH_SHORT).show();
            }
        });
    }



    public static void cambiarNombreCuestionario(final Cuestionario unCuestionario, final String nuevoTexto, final AdapterCuestionario adapterCuestionario) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Cuestionario elCues = realm.where(Cuestionario.class)
                        .equalTo("idCuestionario", unCuestionario.getIdCuestionario())
                        .findFirst();
                if (elCues!=null){
                    elCues.setNombreCuestionario(nuevoTexto);
                    adapterCuestionario.notifyDataSetChanged();
                }
            }
        });

    }

    public static void agregarPregunta(final String idCuestionario, final Pregunta nuevaPregunta, final AdapterPreguntas adapterPreguntas) {

    }





    public static void cambiarTextoArea(final Area unArea, final String s, final Context context) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Area elArea = realm.where(Area.class)
                        .equalTo("idArea", unArea.getIdArea())
                        .findFirst();
                if (elArea !=null&& !s.isEmpty()){
                    elArea.setNombreArea(s);
                    Toast.makeText(context, context.getString(R.string.areaFueModificada), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public static void cambiarTextoCriterio(final Criterio unCriterio, final String s, final Context context) {
        Realm realm =Realm.getDefaultInstance();
        realm   .executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {

                Criterio mCriterio= realm   .where(Criterio.class)
                        .equalTo("idPregunta", unCriterio.getIdPregunta())
                        .equalTo("idCriterio", unCriterio.getIdCriterio())
                        .findFirst();
                if (mCriterio!=null){
                    mCriterio.setTextoCriterio(s);
                    Toast.makeText(context, context.getString(R.string.criterioModificado), Toast.LENGTH_SHORT).show();
                }
            }
        });


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

                String tipoEstructura=null;
                Auditoria mAudit = realm.where(Auditoria.class)
                        .equalTo("idAuditoria", idAudit)
                        .findFirst();
                //si una pregunta de esta auditoria no tiene idItem, es porque la estructura es simple
                if (mAudit!=null){
                    tipoEstructura=mAudit.getEstructuraAuditoria();
                }

                assert tipoEstructura != null;
                if (tipoEstructura.equals(FuncionesPublicas.ESTRUCTURA_ESTRUCTURADA)) {
                    Double sumatoriaEse = 0.0;
                    Integer divisorEse = 0;
                    Integer cantidadItems =0;
                    for (Ese unaEse : mAudit.getListaEses()) {
                        Double sumatoriaItems = 0.0;
                        Integer divisorItems = 0;

                        for (Item unItem : unaEse.getListaItem()) {
                            Integer sumatoriaPreguntas = 0;
                            Integer divisorPreguntas = 0;

                            for (Pregunta unaPregunta : unItem.getListaPreguntas()) {

                                if (unaPregunta.getPuntaje()==null) {
                                    mAudit.setAuditEstaCerrada(false);
                                    divisorPreguntas++;
                                }
                                else if (unaPregunta.getPuntaje()!=9){

                                    sumatoriaPreguntas = sumatoriaPreguntas + unaPregunta.getPuntaje();
                                    divisorPreguntas++;
                                }
                            }

                            if (divisorPreguntas == 0) {
                                unItem.setPuntajeItem(9.9);
                            } else {
                                Double auxPuntaje = sumatoriaPreguntas * 1.00;
                                Double auxDivisor = divisorPreguntas * 1.00;
                                unItem.setPuntajeItem(auxPuntaje / auxDivisor);
                            }

                            if (unItem.getPuntajeItem()!=0 && unItem.getPuntajeItem()!=9.9) {
                                    sumatoriaItems = sumatoriaItems + unItem.getPuntajeItem();
                                    divisorItems++;
                                }
                            else if(unItem.getPuntajeItem()==0){
                                divisorItems++;
                            }
                        }

                        if (divisorItems==0) {
                            unaEse.setPuntajeEse(9.9);
                        }
                        else{

                            unaEse.setPuntajeEse(sumatoriaItems/divisorItems);
                            sumatoriaEse = sumatoriaEse + unaEse.getPuntajeEse();
                            divisorEse++;
                        }

                        cantidadItems=cantidadItems+divisorItems;
                    }

                    mAudit.setPuntajeFinal((sumatoriaEse /divisorEse )/FuncionesPublicas.MAXIMO_PUNTAJE);
                }
                if (mAudit != null && tipoEstructura.equals(FuncionesPublicas.ESTRUCTURA_SIMPLE)){
                    Double sumatoriaEse = 0.0;
                    Integer divisorEse = 0;

                    for (Ese unaEse : mAudit.getListaEses()) {

                        Integer sumatoriaPreguntas = 0;
                        Integer divisorPreguntas = 0;

                        for (Pregunta unaPregunta : unaEse.getListaPreguntas()) {

                            if (unaPregunta.getPuntaje()==null) {
                                mAudit.setAuditEstaCerrada(false);
                                divisorPreguntas++;
                            }
                            else if (unaPregunta.getPuntaje()!=9){

                                sumatoriaPreguntas = sumatoriaPreguntas + unaPregunta.getPuntaje();
                                divisorPreguntas++;
                            }
                        }

                        if (divisorPreguntas==0) {
                            unaEse.setPuntajeEse(9.9);
                        }
                        else{

                            unaEse.setPuntajeEse((sumatoriaPreguntas/divisorPreguntas)*1.0);
                            sumatoriaEse = sumatoriaEse + unaEse.getPuntajeEse();
                            divisorEse++;
                        }
                    }
                    //chequear esta linea
                    mAudit.setPuntajeFinal((sumatoriaEse /divisorEse )/5.0);
                }
            }
        });
    }


    public static RealmResults<Auditoria> traerAuditoriasOrdenadas(){
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Auditoria> resulta2 = realm.where(Auditoria.class)
                .sort("fechaAuditoria", Sort.DESCENDING)
                .findAll();
        return resulta2;
    }

    public static void crearDialogoNombreArea(final Foto unaFoto, final Fragment fragment, final String origen){

        
        new MaterialDialog.Builder(fragment.getContext())
                .title(fragment.getContext().getResources().getString(R.string.addNewArea))
                .inputRange(1,40)
                .contentColor(ContextCompat.getColor(fragment.getContext(), R.color.primary_text))
                .backgroundColor(ContextCompat.getColor(fragment.getContext(), R.color.tile1))
                .titleColor(ContextCompat.getColor(fragment.getContext(), R.color.tile4))
                .content(fragment.getContext().getResources().getString(R.string.nombreArea))
                .inputType(InputType.TYPE_CLASS_TEXT)
                .input(fragment.getContext().getResources().getString(R.string.areaName),"", new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {

                        final Area unArea = new Area();
                        unArea.setNombreArea(input.toString());
                        unArea.setFotoArea(unaFoto);
                        unArea.setIdArea("area" + UUID.randomUUID());


                        //guardo nueva area en Realm
                        Realm realm = Realm.getDefaultInstance();
                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                Area realmArea = realm.copyToRealm(unArea);
                            }
                        });
                        RealmResults<Cuestionario> losCuestionarios = realm.where(Cuestionario.class)
                                .findAll();

                        List<String> unaListaNombre=new ArrayList<>();
                        final List<String> unaListaId=new ArrayList<>();

                        for (Cuestionario elCuestionario :
                                losCuestionarios) {
                            unaListaNombre.add(elCuestionario.getNombreCuestionario());
                            unaListaId.add(elCuestionario.getIdCuestionario());
                        }

                        new MaterialDialog.Builder(fragment.getContext())
                                .title(R.string.tipoArea)
                                .backgroundColor(ContextCompat.getColor(fragment.getContext(), R.color.tile1))
                                .items(unaListaNombre)
                                .titleColor(ContextCompat.getColor(fragment.getContext(), R.color.tile4))
                                .cancelable(false)
                                .itemsCallbackSingleChoice(0, new MaterialDialog.ListCallbackSingleChoice() {
                                    @Override
                                    public boolean onSelection(MaterialDialog dialog, View view, final int which, final CharSequence text) {
                                        Realm realm =Realm.getDefaultInstance();
                                        realm.executeTransaction(new Realm.Transaction() {
                                            @Override
                                            public void execute(@NonNull Realm realm) {
                                                Area realmArea1= realm.where(Area.class)
                                                        .equalTo("idArea", unArea.getIdArea())
                                                        .findFirst();
                                                if (realmArea1!=null) {
                                                    //ASIGNO ID DE CUESTIONARIO AL AREA
                                                    realmArea1.setIdCuestionario(unaListaId.get(which));
                                                }
                                                else{
                                                    Toast.makeText(fragment.getContext(), "Error", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });

                                        if (origen.equals(MANAGE_AREAS)){
                                            FragmentManageAreas elFragment = (FragmentManageAreas) fragment;
                                            elFragment.updateAdapter();
                                        }
                                        else if (origen.equals(SELECCION_AREAS)){
                                            FragmentSeleccionArea elFragment = (FragmentSeleccionArea) fragment;
                                            elFragment.updateAdapter();
                                        }
                                        else{
                                            //do nothing
                                        }


                                        Toast.makeText(fragment.getContext(), unArea.getNombreArea()+" "+fragment.getContext().getResources().getString(R.string.creadoExitosamente), Toast.LENGTH_SHORT).show();
                                        return true;
                                    }
                                })
                                .positiveText(R.string.ok)
                                .show();




                    }
                }).show();


    }

    public static List<String> traerIdEses(String origen, String idAudit){
        List<String> laLista=new ArrayList<>();
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Ese>listaEses;

        switch (origen){
            case FuncionesPublicas.EDITAR_CUESTIONARIO:
                listaEses =realm.where(Ese.class)
                        .equalTo("idCuestionario", idAudit)
                        .isNull("idAudit")
                        .findAll();
                if (listaEses!=null){
                    for (Ese unaEse :
                            listaEses) {
                        laLista.add(unaEse.getIdEse());
                    }
                }
            break;
            case FuncionesPublicas.NUEVA_AUDITORIA:
                listaEses =realm.where(Ese.class)
                        .equalTo("idAudit", idAudit)
                        .findAll();
                if (listaEses!=null){
                    for (Ese unaEse :
                            listaEses) {
                        laLista.add(unaEse.getIdEse());
                    }
                }
            break;
            default:
                    listaEses =realm.where(Ese.class)
                            .equalTo("idAudit", idAudit)
                            .findAll();
                    if (listaEses!=null){
                        for (Ese unaEse :
                                listaEses) {
                            laLista.add(unaEse.getIdEse());
                        }
                    }
            break;
        }
        return laLista;
    }
    
    


}