package com.nomad.mrg5s.DAO;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.nomad.mrg5s.Model.Area;
import com.nomad.mrg5s.Model.Auditoria;
import com.nomad.mrg5s.Model.Criterio;
import com.nomad.mrg5s.Model.Cuestionario;
import com.nomad.mrg5s.Model.Ese;
import com.nomad.mrg5s.Model.Foto;
import com.nomad.mrg5s.Model.Item;
import com.nomad.mrg5s.Model.Pregunta;
import com.nomad.mrg5s.R;
import com.nomad.mrg5s.Utils.FuncionesPublicas;
import com.nomad.mrg5s.Utils.ResultListener;
import com.nomad.mrg5s.View.Activities.ActivityAuditoria;
import com.nomad.mrg5s.View.Adapter.AdapterCuestionario;
import com.nomad.mrg5s.View.Adapter.AdapterItems;
import com.nomad.mrg5s.View.Adapter.AdapterPagerPreguntas;
import com.nomad.mrg5s.View.Adapter.AdapterPreguntas;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import io.realm.Sort;

import static com.nomad.mrg5s.View.Fragments.FragmentSettings.deleteDirectory;


public class ControllerDatos {

    private Context context;
    private String idAuditInstanciada;

    public ControllerDatos(Context context) {
        this.context = context;
    }

    //region CREAR UNA NUEVA AUDITORIA Y LE COPIA LA ESTRUCTURA DE UNA AUDIT MODELO
    public String instanciarAuditoria(final String idCuestionario) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(@NonNull Realm realm) {
                Cuestionario elCuestionario = realm.where(Cuestionario.class)
                        .equalTo("idCuestionario", idCuestionario)
                        .findFirst();

                if (elCuestionario != null) {
                    switch (elCuestionario.getTipoCuestionario()) {
                        case FuncionesPublicas.ESTRUCTURA_ESTRUCTURADA:
//                            CREO EL OBJETO AUDITORIA EN LA REALM Y LE SETEO LOS VALORES DEFAULT USANDO EL MODELO CREADO
                            Auditoria auditoriaNuevaEstructurada = realm.createObject(Auditoria.class, FuncionesPublicas.ID_AUDITORIA + UUID.randomUUID());
                            auditoriaNuevaEstructurada.setEstructuraAuditoria(FuncionesPublicas.ESTRUCTURA_ESTRUCTURADA);
                            idAuditInstanciada = auditoriaNuevaEstructurada.getIdAuditoria();
                            auditoriaNuevaEstructurada.setFechaAuditoria(determinarFecha());
                            auditoriaNuevaEstructurada.setEsUltimaAuditoria(false);
                            auditoriaNuevaEstructurada.setAuditEstaCerrada(false);
                            auditoriaNuevaEstructurada.setPuntajeFinal(0.0);

                            for (Ese eseModelo : elCuestionario.getListaEses()
                                    ) {
                                Ese eseNueva = realm.createObject(Ese.class, FuncionesPublicas.IDESES + UUID.randomUUID());
                                eseNueva.setIdCuestionario(idCuestionario);
                                eseNueva.setIdAudit(idAuditInstanciada);
                                eseNueva.setPuntajeEse(eseModelo.getPuntajeEse());
                                eseNueva.setNombreEse(eseModelo.getNombreEse());
                                eseNueva.setNumeroEse(eseModelo.getNumeroEse());
                                eseNueva.setListaItem(new RealmList<Item>());

                                for (Item itemModelo : eseModelo.getListaItem()
                                        ) {
                                    Item itemNuevo = realm.createObject(Item.class, FuncionesPublicas.IDITEMS + UUID.randomUUID());
                                    itemNuevo.setIdCuestionario(idCuestionario);
                                    itemNuevo.setIdEse(eseNueva.getIdEse());
                                    itemNuevo.setIdAudit(idAuditInstanciada);
                                    itemNuevo.setPuntajeItem(itemModelo.getPuntajeItem());
                                    itemNuevo.setTituloItem(itemModelo.getTituloItem());
                                    itemNuevo.setTextoItem(itemModelo.getTextoItem());
                                    itemNuevo.setListaPreguntas(new RealmList<Pregunta>());

                                    for (Pregunta preguntaModelo : itemModelo.getListaPreguntas()
                                            ) {
                                        Pregunta preguntaNueva = realm.createObject(Pregunta.class, FuncionesPublicas.IDPREGUNTAS + UUID.randomUUID());
                                        preguntaNueva.setIdCuestioniario(idCuestionario);
                                        preguntaNueva.setIdAudit(idAuditInstanciada);
                                        preguntaNueva.setIdItem(itemNuevo.getIdItem());
                                        preguntaNueva.setIdEse(eseNueva.getIdEse());
                                        preguntaNueva.setTextoPregunta(preguntaModelo.getTextoPregunta());
                                        preguntaNueva.setPuntaje(preguntaModelo.getPuntaje());
                                        preguntaNueva.setListaCriterios(new RealmList<Criterio>());

                                        for (Criterio criterioModelo :
                                                preguntaModelo.getListaCriterios()) {
                                            Criterio criterioNuevo = realm.createObject(Criterio.class, FuncionesPublicas.IDCRITERIOS + UUID.randomUUID());
                                            criterioNuevo.setIdCuestionario(idCuestionario);
                                            criterioNuevo.setIdAudit(idAuditInstanciada);
                                            criterioNuevo.setIdEse(eseNueva.getIdEse());
                                            criterioNuevo.setIdItem(itemNuevo.getIdItem());
                                            criterioNuevo.setIdPregunta(preguntaNueva.getIdPregunta());
                                            criterioNuevo.setPuntajeCriterio(criterioModelo.getPuntajeCriterio());
                                            criterioNuevo.setTextoCriterio(criterioModelo.getTextoCriterio());
                                            preguntaNueva.addCriterio(criterioNuevo);
                                        }
                                        itemNuevo.addPregunta(preguntaNueva);
                                    }
                                    eseNueva.addItem(itemNuevo);
                                }
                                auditoriaNuevaEstructurada.addEse(eseNueva);
                            }
                            break;
                        case FuncionesPublicas.ESTRUCTURA_SIMPLE:

//                            CREO EL OBJETO EN LA REALM
                            Auditoria auditoriaNuevaSimple = realm.createObject(Auditoria.class, "Audit_" + UUID.randomUUID());
                            //nuevaAuditoria.setIdAuditoria("Audit_" + UUID.randomUUID());
                            idAuditInstanciada = auditoriaNuevaSimple.getIdAuditoria();
                            auditoriaNuevaSimple.setFechaAuditoria(determinarFecha());
                            auditoriaNuevaSimple.setEsUltimaAuditoria(false);
                            auditoriaNuevaSimple.setAuditEstaCerrada(false);
                            auditoriaNuevaSimple.setPuntajeFinal(0.0);
                            auditoriaNuevaSimple.setEstructuraAuditoria(FuncionesPublicas.ESTRUCTURA_SIMPLE);

                            for (Ese eseModelo : elCuestionario.getListaEses()
                                    ) {
                                Ese eseNueva = realm.createObject(Ese.class, FuncionesPublicas.IDESES + UUID.randomUUID());
                                eseNueva.setIdAudit(idAuditInstanciada);
                                eseNueva.setPuntajeEse(eseModelo.getPuntajeEse());
                                eseNueva.setNombreEse(eseModelo.getNombreEse());
                                eseNueva.setNumeroEse(eseModelo.getNumeroEse());
                                eseNueva.setListaPreguntas(new RealmList<Pregunta>());
                                for (Pregunta preguntaModelo : eseModelo.getListaPreguntas()
                                        ) {
                                    Pregunta preguntaNueva = realm.createObject(Pregunta.class, FuncionesPublicas.IDPREGUNTAS + UUID.randomUUID());
                                    preguntaNueva.setIdAudit(idAuditInstanciada);
                                    preguntaNueva.setIdItem(null);
                                    preguntaNueva.setIdEse(eseNueva.getIdEse());
                                    preguntaNueva.setTextoPregunta(preguntaModelo.getTextoPregunta());
                                    preguntaNueva.setPuntaje(preguntaModelo.getPuntaje());
                                    preguntaNueva.setListaCriterios(new RealmList<Criterio>());
                                    for (Criterio criterioModelo :
                                            preguntaModelo.getListaCriterios()) {
                                        Criterio criterioNuevo = realm.createObject(Criterio.class, FuncionesPublicas.IDCRITERIOS + UUID.randomUUID());
                                        criterioNuevo.setIdCuestionario(idCuestionario);
                                        criterioNuevo.setIdAudit(idAuditInstanciada);
                                        criterioNuevo.setIdEse(eseNueva.getIdEse());
                                        criterioNuevo.setIdItem(null);
                                        criterioNuevo.setIdPregunta(preguntaNueva.getIdPregunta());
                                        criterioNuevo.setPuntajeCriterio(criterioModelo.getPuntajeCriterio());
                                        criterioNuevo.setTextoCriterio(criterioModelo.getTextoCriterio());
                                        preguntaNueva.addCriterio(criterioNuevo);
                                    }
                                    eseNueva.addPregunta(preguntaNueva);
                                }
                                auditoriaNuevaSimple.addEse(eseNueva);
                            }
                            break;
                    }
                } else {
                    idAuditInstanciada = null;
                }
                //------ FIN - COPIO ESTRUCTURA DEL CUESTIONARIO ------//
            }
        });
        return idAuditInstanciada;

        //endregion
    }

    private java.util.Date determinarFecha() {
        Calendar cal = Calendar.getInstance();
        return (cal.getTime());
    }

    public List<String> traerEses() {
        List<String> lista = new ArrayList<>();

        lista.add(FuncionesPublicas.PRIMERA_ESE);
        lista.add(FuncionesPublicas.SEGUNDA_ESE);
        lista.add(FuncionesPublicas.TERCERA_ESE);
        lista.add(FuncionesPublicas.CUARTA_ESE);
        lista.add(FuncionesPublicas.QUINTA_ESE);

        return lista;
    }

    public List<String> traerListaViewPager() {
        List<String> unaLista = new ArrayList<>();
        unaLista.add(FuncionesPublicas.AUDITORIA);
        unaLista.add(FuncionesPublicas.RANKING);
        unaLista.add(FuncionesPublicas.AREAS);
        return unaLista;
    }

    public List<String> traerListaVerAudit() {
        List<String> lista = new ArrayList<>();
        lista.add("1");
        lista.add("2");
        lista.add("3");
        lista.add("4");
        lista.add("5");
        return lista;
    }

    public void crearCuestionariosDefault(final String nombreArea, final Boolean esEstructuraSimple) {
        //region CREACION CUESTIONARIO SIMPLE
        Realm nBgRealm = Realm.getDefaultInstance();

        nBgRealm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(@NonNull Realm realm) {
                Cuestionario nuevoCuestionario = realm.createObject(Cuestionario.class, FuncionesPublicas.IDCUESTIONARIOS_DEFAULT + UUID.randomUUID());

                nuevoCuestionario.setNombreCuestionario(nombreArea);
                nuevoCuestionario.setTipoCuestionario(FuncionesPublicas.ESTRUCTURA_SIMPLE);
                nuevoCuestionario.setListaEses(new RealmList<Ese>());

                for (int i = 0; i < 5; i++) {
                    Ese unaEse = realm.createObject(Ese.class, FuncionesPublicas.IDESES + UUID.randomUUID());
                    unaEse.setIdCuestionario(nuevoCuestionario.getIdCuestionario());
                    unaEse.setPuntajeEse(0.0);
                    unaEse.setNombreEse(traerEses().get(i));
                    unaEse.setNumeroEse(i + 1);
                    unaEse.setListaPreguntas(new RealmList<Pregunta>());

                    for (int j = 0; j < 5; j++) {
                        Pregunta pregunta111 = realm.createObject(Pregunta.class, FuncionesPublicas.IDPREGUNTAS + UUID.randomUUID());
                        pregunta111.setPuntaje(null);
                        pregunta111.setIdCuestioniario(nuevoCuestionario.getIdCuestionario());
                        pregunta111.setIdEse(unaEse.getIdEse());
                        pregunta111.setOrden(j + 1);

                        //calcularTexto

                        String nombreString = nombreArea+"_"+String.valueOf(i)+"_"+String.valueOf(j)+"_";
                        Integer idString= context.getResources().getIdentifier(nombreString,"string",context.getPackageName());

                        if (idString!=0){
                            pregunta111.setTextoPregunta(context.getString(idString));
                        }
                        else{
                            pregunta111.setTextoPregunta(context.getString(R.string.error));
                        }

                        for (int k = 0; k < 4; i++) {

                            Criterio unCriterio = realm.createObject(Criterio.class, FuncionesPublicas.IDCRITERIOS + UUID.randomUUID());
                            unCriterio.setIdCuestionario(nuevoCuestionario.getIdCuestionario());
                            unCriterio.setIdEse(unaEse.getIdEse());
                            unCriterio.setIdPregunta(pregunta111.getIdPregunta());
                            unCriterio.setPuntajeCriterio(i + 1);
                            unCriterio.setOrden(i + 1);

                            String nombreStringCrit = nombreArea+"_"+String.valueOf(i+1)+"_"+String.valueOf(j+1)+"_"+String.valueOf(k+1);
                            Integer idStringCrit= context.getResources().getIdentifier(nombreString,"string",context.getPackageName());

                            if (idStringCrit!=0){
                                unCriterio.setTextoCriterio(context.getString(idStringCrit));
                            }
                            else{
                                unCriterio.setTextoCriterio(context.getString(R.string.error));
                            }
                            pregunta111.addCriterio(unCriterio);
                        }
                        unaEse.addPregunta(pregunta111);
                    }
                    nuevoCuestionario.addEse(unaEse);
                }
            }
        });
        //endregion
    }


    public void crearCuestionariosDefault(final String nombreArea) {
        //region CREACION CUESTIONARIO SIMPLE
        Realm nBgRealm = Realm.getDefaultInstance();

        nBgRealm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(@NonNull Realm realm) {
                Cuestionario nuevoCuestionario = realm.createObject(Cuestionario.class, FuncionesPublicas.IDCUESTIONARIOS_DEFAULT + UUID.randomUUID());
                nuevoCuestionario.setNombreCuestionario(nombreArea);
                nuevoCuestionario.setTipoCuestionario(FuncionesPublicas.ESTRUCTURA_ESTRUCTURADA);
                nuevoCuestionario.setListaEses(new RealmList<Ese>());

                for (int i = 0; i < 5; i++) {
                    Ese unaEse = realm.createObject(Ese.class, FuncionesPublicas.IDESES + UUID.randomUUID());
                    unaEse.setIdCuestionario(nuevoCuestionario.getIdCuestionario());
                    unaEse.setPuntajeEse(0.0);
                    unaEse.setNombreEse(traerEses().get(i));
                    unaEse.setNumeroEse(i + 1);
                    unaEse.setListaItem(new RealmList<Item>());

                    for (int k = 0; k < 5; k++) {
                        Item item11 = realm.createObject(Item.class, FuncionesPublicas.IDITEMS + UUID.randomUUID());
                        item11.setTituloItem(context.getResources().getString(R.string.criterio11));
                        item11.setTextoItem(context.getResources().getString(R.string.texto11));
                        item11.setIdEse(unaEse.getIdEse());
                        item11.setIdCuestionario(nuevoCuestionario.getIdCuestionario());
                        item11.setPuntajeItem(0.0);
                        item11.setOrden(k + 1);
                        item11.setListaPreguntas(new RealmList<Pregunta>());

                        for (int j = 0; j < 5; j++) {
                            Pregunta pregunta111 = realm.createObject(Pregunta.class, FuncionesPublicas.IDPREGUNTAS + UUID.randomUUID());
                            pregunta111.setPuntaje(null);
                            pregunta111.setTextoPregunta(context.getResources().getString(R.string.textoPregunta111));
                            pregunta111.setIdItem(item11.getIdItem());
                            pregunta111.setIdCuestioniario(nuevoCuestionario.getIdCuestionario());
                            pregunta111.setIdEse(unaEse.getIdEse());
                            item11.addPregunta(pregunta111);
                            pregunta111.setOrden(j + 1);
                            cargarCriteriosdDefaultPregunta(realm, pregunta111);
                        }
                        unaEse.addItem(item11);
                    }
                    nuevoCuestionario.addEse(unaEse);
                }

            }
        });
        //endregion
    }


    public void
    crearCriteriosDefault() {
        //CUATRO CRITERIOS DEFAULT
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                for (int i = 0; i < 4; i++) {

                    Criterio unCriterio = realm.createObject(Criterio.class, FuncionesPublicas.IDCRITERIOS_DEFAULT + i);
                    unCriterio.setIdCuestionario(null);
                    unCriterio.setIdEse(null);
                    unCriterio.setIdPregunta(null);
                    unCriterio.setPuntajeCriterio(i + 1);
                    unCriterio.setOrden(i + 1);
                    switch (i) {
                        case 0:
                            unCriterio.setTextoCriterio(context.getString(R.string.criterioPreg1));
                            break;
                        case 1:
                            unCriterio.setTextoCriterio(context.getString(R.string.criterioPreg2));
                            break;
                        case 2:
                            unCriterio.setTextoCriterio(context.getString(R.string.criterioPreg3));
                            break;
                        case 3:
                            unCriterio.setTextoCriterio(context.getString(R.string.criterioPreg4));
                            break;
                    }
                }
            }
        });
    }

    private void cargarCriteriosdDefaultPregunta(Realm realm, Pregunta preg) {
        //ESTO OCURRE DENTO DE UN EXECUTE TRANSACTION
        //SE TOMARON CUATRO CRITERIOS
        preg.setListaCriterios(new RealmList<Criterio>());

        RealmResults<Criterio> listaCriteriosDefault = realm.where(Criterio.class)
                .beginsWith("idCriterio", FuncionesPublicas.IDCRITERIOS_DEFAULT)
                .findAll();
        if (listaCriteriosDefault != null) {
            for (int i = 0; i < listaCriteriosDefault.size(); i++) {

                Criterio unCriterio = realm.createObject(Criterio.class, FuncionesPublicas.IDCRITERIOS + UUID.randomUUID());
                if (preg.getIdItem() != null) {
                    unCriterio.setIdItem(preg.getIdItem());
                }
                unCriterio.setIdCuestionario(preg.getIdCuestionario());
                unCriterio.setIdEse(preg.getIdEse());
                unCriterio.setIdPregunta(preg.getIdPregunta());
                unCriterio.setPuntajeCriterio(i + 1);
                unCriterio.setOrden(i + 1);
                switch (i) {
                    case 0:
                        unCriterio.setTextoCriterio(listaCriteriosDefault.get(i).getTextoCriterio());
                        break;
                    case 1:
                        unCriterio.setTextoCriterio(listaCriteriosDefault.get(i).getTextoCriterio());
                        break;
                    case 2:
                        unCriterio.setTextoCriterio(listaCriteriosDefault.get(i).getTextoCriterio());
                        break;
                    case 3:
                        unCriterio.setTextoCriterio(listaCriteriosDefault.get(i).getTextoCriterio());
                        break;
                }
                preg.addCriterio(unCriterio);
            }


        }
    }

    public void crearNuevoCuestionario(final ResultListener<Boolean> listenerCompletado, final String nombreCuestionario, final String tipoCuestionario) {
        Realm laRealm = Realm.getDefaultInstance();

        laRealm.executeTransactionAsync(new Realm.Transaction() {
                                            @Override
                                            public void execute(@NonNull Realm realm) {
                                                Cuestionario nuevoCuestionario = realm.createObject(Cuestionario.class, FuncionesPublicas.IDCUESTIONARIOS + UUID.randomUUID());
                                                nuevoCuestionario.setNombreCuestionario(nombreCuestionario);
                                                nuevoCuestionario.setTipoCuestionario(tipoCuestionario);

                                                for (int i = 0; i < 5; i++) {
                                                    Ese ese1 = realm.createObject(Ese.class, FuncionesPublicas.IDESES + UUID.randomUUID());
                                                    ese1.setIdCuestionario(nuevoCuestionario.getIdCuestionario());
                                                    ese1.setPuntajeEse(0.0);
                                                    ese1.setNombreEse(traerEses().get(i));
                                                    ese1.setNumeroEse(i + 1);
                                                    nuevoCuestionario.addEse(ese1);
                                                }
                                            }
                                        }
                , new Realm.Transaction.OnSuccess() {
                    @Override
                    public void onSuccess() {

                        listenerCompletado.finish(true);
                    }
                }, new Realm.Transaction.OnError() {
                    @Override
                    public void onError(Throwable error) {
                        listenerCompletado.finish(false);
                        Toast.makeText(context, context.getString(R.string.cuestionarioNoCreado) + " " + error.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


    //--- METODOS FIREBASE ---START//
    public void crearCuestionarioFirebase(final Cuestionario elCues) {
        DatabaseReference mbase = FirebaseDatabase.getInstance().getReference();
        mbase.child("Cuestionarios").child(elCues.getIdCuestionario()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    generarNodosFirebase(elCues);
                } else {
                    Toast.makeText(context, context.getString(R.string.errorPruebeNuevamente), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void generarNodosFirebase(Cuestionario elCues) {
        DatabaseReference mbase = FirebaseDatabase.getInstance().getReference();
        mbase.child("Cuestionarios").child(elCues.getIdCuestionario()).child("1-Nombre").setValue(elCues.getNombreCuestionario());
        mbase.child("Cuestionarios").child(elCues.getIdCuestionario()).child("2-Tipo").setValue(elCues.getTipoCuestionario());
        mbase.child("Cuestionarios").child(elCues.getIdCuestionario()).child("3-IdCuestionario").setValue(elCues.getIdCuestionario());
        if (elCues.getTipoCuestionario().equals(FuncionesPublicas.ESTRUCTURA_SIMPLE)) {

            for (Ese unaEse :
                    elCues.getListaEses()) {
                mbase.child("Cuestionarios").child(elCues.getIdCuestionario()).child("4-Estructura").child(unaEse.getNombreEse()).child("1-IdEse").setValue(unaEse.getIdEse());
                mbase.child("Cuestionarios").child(elCues.getIdCuestionario()).child("4-Estructura").child(unaEse.getNombreEse()).child("3-NumeroEse").setValue(String.valueOf(unaEse.getNumeroEse()));
                mbase.child("Cuestionarios").child(elCues.getIdCuestionario()).child("4-Estructura").child(unaEse.getNombreEse()).child("2-NombreEse").setValue(unaEse.getNombreEse());


                for (Pregunta unaPreg :
                        unaEse.getListaPreguntas()) {
                    mbase.child("Cuestionarios").child(elCues.getIdCuestionario()).child("4-Estructura").child(unaEse.getNombreEse()).child("4-Preguntas").child(unaPreg.getIdPregunta()).child("0-OrdenPregunta").setValue(String.valueOf(unaPreg.getOrden()));
                    mbase.child("Cuestionarios").child(elCues.getIdCuestionario()).child("4-Estructura").child(unaEse.getNombreEse()).child("4-Preguntas").child(unaPreg.getIdPregunta()).child("1-IdPregunta").setValue(unaPreg.getIdPregunta());
                    mbase.child("Cuestionarios").child(elCues.getIdCuestionario()).child("4-Estructura").child(unaEse.getNombreEse()).child("4-Preguntas").child(unaPreg.getIdPregunta()).child("2-TextoPregunta").setValue(unaPreg.getTextoPregunta());

                    for (Criterio unCrit : unaPreg.getListaCriterios()
                            ) {

                        mbase.child("Cuestionarios").child(elCues.getIdCuestionario()).child("4-Estructura").child(unaEse.getNombreEse()).child("4-Preguntas").child(unaPreg.getIdPregunta()).child("3-Criterios")
                                .child(unCrit.getIdCriterio()).child("1-IdCriterio").setValue(unCrit.getIdCriterio());
                        mbase.child("Cuestionarios").child(elCues.getIdCuestionario()).child("4-Estructura").child(unaEse.getNombreEse()).child("4-Preguntas").child(unaPreg.getIdPregunta()).child("3-Criterios")
                                .child(unCrit.getIdCriterio()).child("2-TextoCriterio").setValue(unCrit.getTextoCriterio());
                        mbase.child("Cuestionarios").child(elCues.getIdCuestionario()).child("4-Estructura").child(unaEse.getNombreEse()).child("4-Preguntas").child(unaPreg.getIdPregunta()).child("3-Criterios")
                                .child(unCrit.getIdCriterio()).child("3-PuntajeCriterio").setValue(String.valueOf(unCrit.getPuntajeCriterio()));
                        mbase.child("Cuestionarios").child(elCues.getIdCuestionario()).child("4-Estructura").child(unaEse.getNombreEse()).child("4-Preguntas").child(unaPreg.getIdPregunta()).child("3-Criterios")
                                .child(unCrit.getIdCriterio()).child("0-OrdenCriterio").setValue(String.valueOf(unCrit.getOrden()));

                    }

                }
            }
        }
        if (elCues.getTipoCuestionario().equals(FuncionesPublicas.ESTRUCTURA_ESTRUCTURADA)) {
            for (Ese unaEse :
                    elCues.getListaEses()) {
                mbase.child("Cuestionarios").child(elCues.getIdCuestionario()).child("4-Estructura").child(unaEse.getNombreEse()).child("1-IdEse").setValue(unaEse.getIdEse());
                mbase.child("Cuestionarios").child(elCues.getIdCuestionario()).child("4-Estructura").child(unaEse.getNombreEse()).child("3-NumeroEse").setValue(String.valueOf(unaEse.getNumeroEse()));
                mbase.child("Cuestionarios").child(elCues.getIdCuestionario()).child("4-Estructura").child(unaEse.getNombreEse()).child("2-NombreEse").setValue(unaEse.getNombreEse());

                for (Item unItem :
                        unaEse.getListaItem()) {

                    mbase.child("Cuestionarios").child(elCues.getIdCuestionario()).child("4-Estructura").child(unaEse.getNombreEse()).child("4-Items").child(unItem.getIdItem()).child("0-OrdenItem").setValue(String.valueOf(unItem.getOrden()));
                    mbase.child("Cuestionarios").child(elCues.getIdCuestionario()).child("4-Estructura").child(unaEse.getNombreEse()).child("4-Items").child(unItem.getIdItem()).child("1-IdItem").setValue(unItem.getIdItem());
                    mbase.child("Cuestionarios").child(elCues.getIdCuestionario()).child("4-Estructura").child(unaEse.getNombreEse()).child("4-Items").child(unItem.getIdItem()).child("2-TituloItem").setValue(unItem.getTituloItem());
                    mbase.child("Cuestionarios").child(elCues.getIdCuestionario()).child("4-Estructura").child(unaEse.getNombreEse()).child("4-Items").child(unItem.getIdItem()).child("3-TextoItem").setValue(unItem.getTextoItem());
                    for (Pregunta unaPreg :
                            unItem.getListaPreguntas()) {
                        mbase.child("Cuestionarios").child(elCues.getIdCuestionario()).child("4-Estructura").child(unaEse.getNombreEse()).child("4-Items").child(unItem.getIdItem()).child("4-Preguntas").child(unaPreg.getIdPregunta()).child("0-OrdenPregunta").setValue(String.valueOf(unaPreg.getOrden()));
                        mbase.child("Cuestionarios").child(elCues.getIdCuestionario()).child("4-Estructura").child(unaEse.getNombreEse()).child("4-Items").child(unItem.getIdItem()).child("4-Preguntas").child(unaPreg.getIdPregunta()).child("1-IdPregunta").setValue(unaPreg.getIdPregunta());
                        mbase.child("Cuestionarios").child(elCues.getIdCuestionario()).child("4-Estructura").child(unaEse.getNombreEse()).child("4-Items").child(unItem.getIdItem()).child("4-Preguntas").child(unaPreg.getIdPregunta()).child("2-TextoPregunta").setValue(unaPreg.getTextoPregunta());
                        for (Criterio unCrit : unaPreg.getListaCriterios()
                                ) {

                            mbase.child("Cuestionarios").child(elCues.getIdCuestionario()).child("4-Estructura").child(unaEse.getNombreEse()).child("4-Items").child(unItem.getIdItem()).child("4-Preguntas").child(unaPreg.getIdPregunta()).child("3-Criterios")
                                    .child(unCrit.getIdCriterio()).child("1-IdCriterio").setValue(unCrit.getIdCriterio());
                            mbase.child("Cuestionarios").child(elCues.getIdCuestionario()).child("4-Estructura").child(unaEse.getNombreEse()).child("4-Items").child(unItem.getIdItem()).child("4-Preguntas").child(unaPreg.getIdPregunta()).child("3-Criterios")
                                    .child(unCrit.getIdCriterio()).child("2-TextoCriterio").setValue(unCrit.getTextoCriterio());
                            mbase.child("Cuestionarios").child(elCues.getIdCuestionario()).child("4-Estructura").child(unaEse.getNombreEse()).child("4-Items").child(unItem.getIdItem()).child("4-Preguntas").child(unaPreg.getIdPregunta()).child("3-Criterios")
                                    .child(unCrit.getIdCriterio()).child("3-PuntajeCriterio").setValue(String.valueOf(unCrit.getPuntajeCriterio()));
                            mbase.child("Cuestionarios").child(elCues.getIdCuestionario()).child("4-Estructura").child(unaEse.getNombreEse()).child("4-Items").child(unItem.getIdItem()).child("4-Preguntas").child(unaPreg.getIdPregunta()).child("3-Criterios")
                                    .child(unCrit.getIdCriterio()).child("0-OrdenCriterio").setValue(String.valueOf(unCrit.getOrden()));

                        }

                    }
                }
            }
        }
    }
    public void traerCuestionariosFirebase() {
        DatabaseReference mbase = FirebaseDatabase.getInstance().getReference();
        mbase.child("Cuestionarios").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull final DataSnapshot dataSnapshot, @Nullable String s) {


                final String idCuestion = dataSnapshot.child("3-IdCuestionario").getValue(String.class);


                eliminarCuestionarioEnMainThread(idCuestion);
                Realm realm = Realm.getDefaultInstance();


                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {

                        Cuestionario nuevoCuestionario = new Cuestionario();

                        nuevoCuestionario.setIdCuestionario(idCuestion);
                        nuevoCuestionario.setNombreCuestionario((String) dataSnapshot.child("1-Nombre").getValue());
                        nuevoCuestionario.setTipoCuestionario((String) dataSnapshot.child("2-Tipo").getValue());
                        nuevoCuestionario.setListaEses(new RealmList<Ese>());

                        for (DataSnapshot ese :
                                dataSnapshot.child("4-Estructura").getChildren()
                                ) {
                            Ese eseNueva = new Ese();
                            eseNueva.setIdEse((String) ese.child("1-IdEse").getValue());
                            eseNueva.setPuntajeEse(0.0);
                            eseNueva.setIdCuestionario(nuevoCuestionario.getIdCuestionario());
                            eseNueva.setNombreEse((String) ese.child("2-NombreEse").getValue());
                            eseNueva.setNumeroEse((Integer.parseInt((String) ese.child("3-NumeroEse").getValue())));

                            //SI EL CUESTIONARIO ES ESTRUCTURADO
                            if (nuevoCuestionario.getTipoCuestionario().equals(FuncionesPublicas.ESTRUCTURA_ESTRUCTURADA)) {
                                eseNueva.setListaPreguntas(null);
                                eseNueva.setListaItem(new RealmList<Item>());
                                for (DataSnapshot item :
                                        ese.child("4-Items").getChildren()
                                        ) {
                                    Item itemNuevo = new Item();
                                    itemNuevo.setOrden((Integer.parseInt((String) item.child("0-OrdenItem").getValue())));
                                    itemNuevo.setIdItem((String) item.child("1-IdItem").getValue());
                                    itemNuevo.setPuntajeItem(0.0);
                                    itemNuevo.setIdCuestionario(nuevoCuestionario.getIdCuestionario());
                                    itemNuevo.setTituloItem((String) item.child("2-TituloItem").getValue());
                                    itemNuevo.setTextoItem((String) item.child("3-TextoItem").getValue());
                                    itemNuevo.setIdEse(eseNueva.getIdEse());
                                    itemNuevo.setListaPreguntas(new RealmList<Pregunta>());

                                    for (DataSnapshot pregunta :
                                            item.child("4-Preguntas").getChildren()) {

                                        Pregunta preguntaNueva = new Pregunta();
                                        preguntaNueva.setOrden((Integer.parseInt((String) pregunta.child("0-OrdenPregunta").getValue())));
                                        preguntaNueva.setTextoPregunta((String) pregunta.child("2-TextoPregunta").getValue());
                                        preguntaNueva.setIdPregunta((String) pregunta.child("1-IdPregunta").getValue());
                                        preguntaNueva.setPuntaje(null);
                                        preguntaNueva.setIdItem(itemNuevo.getIdItem());
                                        preguntaNueva.setIdEse(eseNueva.getIdEse());
                                        preguntaNueva.setIdCuestionario(nuevoCuestionario.getIdCuestionario());
                                        preguntaNueva.setListaCriterios(new RealmList<Criterio>());


                                        for (DataSnapshot criterio :
                                                pregunta.child("3-Criterios").getChildren()) {

                                            Criterio criterioNuevo = new Criterio();
                                            criterioNuevo.setIdCriterio((String) criterio.child("1-IdCriterio").getValue());
                                            criterioNuevo.setIdEse(eseNueva.getIdEse());
                                            criterioNuevo.setIdItem(itemNuevo.getIdItem());
                                            criterioNuevo.setIdPregunta(preguntaNueva.getIdPregunta());
                                            criterioNuevo.setIdCuestionario(nuevoCuestionario.getIdCuestionario());
                                            criterioNuevo.setTextoCriterio((String) criterio.child("2-TextoCriterio").getValue());
                                            criterioNuevo.setPuntajeCriterio((Integer.parseInt((String) criterio.child("3-PuntajeCriterio").getValue())));
                                            criterioNuevo.setPuntajeCriterio((Integer.parseInt((String) criterio.child("0-OrdenCriterio").getValue())));

                                            Criterio mCriterio = realm.copyToRealmOrUpdate(criterioNuevo);
                                            preguntaNueva.addCriterio(mCriterio);
                                        }
                                        Pregunta mPreguntaNueva = realm.copyToRealmOrUpdate(preguntaNueva);
                                        itemNuevo.addPregunta(mPreguntaNueva);
                                    }
                                    Item mItemNuevo = realm.copyToRealmOrUpdate(itemNuevo);
                                    eseNueva.addItem(mItemNuevo);
                                }
                                Ese mEseNueva = realm.copyToRealmOrUpdate(eseNueva);
                                nuevoCuestionario.addEse(mEseNueva);
                            }

                            //SI EL CUESTIONARIO ES SIMPLE
                            else {
                                eseNueva.setListaItem(null);
                                eseNueva.setListaPreguntas(new RealmList<Pregunta>());


                                for (DataSnapshot pregunta :
                                        ese.child("4-Preguntas").getChildren()) {

                                    Pregunta preguntaNueva = new Pregunta();
                                    preguntaNueva.setOrden((Integer.parseInt((String) pregunta.child("0-OrdenPregunta").getValue())));
                                    preguntaNueva.setTextoPregunta((String) pregunta.child("2-TextoPregunta").getValue());
                                    preguntaNueva.setIdPregunta((String) pregunta.child("1-IdPregunta").getValue());
                                    preguntaNueva.setPuntaje(null);
                                    preguntaNueva.setIdEse(eseNueva.getIdEse());
                                    preguntaNueva.setIdCuestionario(nuevoCuestionario.getIdCuestionario());
                                    preguntaNueva.setListaCriterios(new RealmList<Criterio>());

                                    for (DataSnapshot criterio :
                                            pregunta.child("3-Criterios").getChildren()) {

                                        Criterio criterioNuevo = new Criterio();
                                        criterioNuevo.setIdCriterio((String) criterio.child("1-IdCriterio").getValue());
                                        criterioNuevo.setIdEse(eseNueva.getIdEse());
                                        criterioNuevo.setIdPregunta(preguntaNueva.getIdPregunta());
                                        criterioNuevo.setIdCuestionario(nuevoCuestionario.getIdCuestionario());
                                        criterioNuevo.setTextoCriterio((String) criterio.child("2-TextoCriterio").getValue());
                                        criterioNuevo.setPuntajeCriterio((Integer.parseInt((String) criterio.child("3-PuntajeCriterio").getValue())));
                                        criterioNuevo.setPuntajeCriterio((Integer.parseInt((String) criterio.child("0-OrdenCriterio").getValue())));

                                        Criterio mCriterio = realm.copyToRealmOrUpdate(criterioNuevo);
                                        preguntaNueva.addCriterio(mCriterio);
                                    }
                                    Pregunta mPreguntaNueva = realm.copyToRealmOrUpdate(preguntaNueva);
                                    eseNueva.addPregunta(mPreguntaNueva);
                                }
                                Ese mEseNueva = realm.copyToRealmOrUpdate(eseNueva);
                                nuevoCuestionario.addEse(mEseNueva);
                            }
                        }
                        realm.copyToRealmOrUpdate(nuevoCuestionario);
                    }
                });


                Toast.makeText(context, "fin?", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public void eliminarCuestionarioEnMainThread(final String idCuestionario) {

        Realm realm = Realm.getDefaultInstance();

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<Pregunta> lasPreguntas = realm.where(Pregunta.class)
                        .equalTo("idCuestionario", idCuestionario)
                        .isNull("idAudit")
                        .findAll();
                lasPreguntas.deleteAllFromRealm();

                RealmResults<Item> losItem = realm.where(Item.class)
                        .equalTo("idCuestionario", idCuestionario)
                        .isNull("idAudit")
                        .findAll();
                losItem.deleteAllFromRealm();

                RealmResults<Ese> lasEses = realm.where(Ese.class)
                        .equalTo("idCuestionario", idCuestionario)
                        .isNull("idAudit")
                        .findAll();
                lasEses.deleteAllFromRealm();

                RealmResults<Criterio> losCriterios = realm.where(Criterio.class)
                        .equalTo("idCuestionario", idCuestionario)
                        .isNull("idAudit")
                        .not()
                        .beginsWith("idCriterio", FuncionesPublicas.IDCRITERIOS_DEFAULT)
                        .findAll();
                losCriterios.deleteAllFromRealm();

                Cuestionario elCuestionario = realm.where(Cuestionario.class)
                        .equalTo("idCuestionario", idCuestionario)
                        .findFirst();
                if (elCuestionario != null) {
                    elCuestionario.deleteFromRealm();
                }
            }
        });

    }

    private boolean noExisteCuestionario(String idCuestion) {
        Realm realm = Realm.getDefaultInstance();
        Cuestionario elCuestionarioBuscado = realm.where(Cuestionario.class)
                .equalTo("idCuestionario", idCuestion)
                .findFirst();
        if (elCuestionarioBuscado != null) {
            return false;
        } else {
            return true;
        }
    }

    //--- METODOS FIREBASE ---END//

    public void eliminarCuestionario(final String idCuestionario) {

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

                RealmResults<Ese> lasEses = bgRealm.where(Ese.class)
                        .equalTo("idCuestionario", idCuestionario)
                        .isNull("idAudit")
                        .findAll();
                lasEses.deleteAllFromRealm();

                RealmResults<Criterio> losCriterios = bgRealm.where(Criterio.class)
                        .equalTo("idCuestionario", idCuestionario)
                        .isNull("idAudit")
                        .not()
                        .beginsWith("idCriterio", FuncionesPublicas.IDCRITERIOS_DEFAULT)
                        .findAll();
                losCriterios.deleteAllFromRealm();

                Cuestionario elCuestionario = bgRealm.where(Cuestionario.class)
                        .equalTo("idCuestionario", idCuestionario)
                        .findFirst();
                if (elCuestionario != null) {
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

    public void borrarBaseDatos() {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(@NonNull Realm realm) {

                RealmResults<Area> lasAreas = realm.where(Area.class)
                        .findAll();
                lasAreas.deleteAllFromRealm();

                RealmResults<Criterio> losCriterios = realm.where(Criterio.class)
                        .findAll();
                losCriterios.deleteAllFromRealm();

                RealmResults<Pregunta> lasPreguntas = realm.where(Pregunta.class)
                        .findAll();
                lasPreguntas.deleteAllFromRealm();

                RealmResults<Item> losItem = realm.where(Item.class)
                        .findAll();
                losItem.deleteAllFromRealm();

                RealmResults<Ese> lasEses = realm.where(Ese.class)
                        .findAll();
                lasEses.deleteAllFromRealm();


                RealmResults<Foto> lasFotos = realm.where(Foto.class)
                        .findAll();
                lasFotos.deleteAllFromRealm();

                RealmResults<Auditoria> lasAuditorias = realm.where(Auditoria.class)
                        .findAll();
                lasAuditorias.deleteAllFromRealm();

                RealmResults<Cuestionario> losCuestionarios = realm.where(Cuestionario.class)
                        .findAll();
                losCuestionarios.deleteAllFromRealm();

                //borrar directorios
                File path = new File(context.getExternalFilesDir(null) + File.separator + "nomad" + File.separator + "audit5s" + File.separator + FirebaseAuth.getInstance().getCurrentUser().getEmail());
                if (deleteDirectory(path)) {
                }

            }

        });

        //crear cuestionarios default
        crearCriteriosDefault();
        crearCuestionariosDefault(context.getString(R.string.areaGeneral), true);
        crearCuestionariosDefault(context.getString(R.string.areaIndustrial));
        crearCuestionariosDefault(context.getString(R.string.areaOficina));
        crearCuestionariosDefault(context.getString(R.string.areaExterna));

    }
    public void agregarPregunta(final String idCuestionario, final Pregunta nuevaPregunta, final ResultListener<Boolean> listenerCompletado) {
        final Realm realm = Realm.getDefaultInstance();
        realm.executeTransactionAsync(new Realm.Transaction() {
                                          @Override
                                          public void execute(Realm bgRealm) {
                                              Pregunta mPregunta = bgRealm.copyToRealm(nuevaPregunta);
                                              Cuestionario mCuestionario = bgRealm.where(Cuestionario.class)
                                                      .equalTo("idCuestionario", idCuestionario)
                                                      .findFirst();
                                              if (mCuestionario != null) {
                                                  if (mCuestionario.getTipoCuestionario().equals(FuncionesPublicas.ESTRUCTURA_SIMPLE)) {

                                                      RealmResults<Pregunta> lasPreguntas = bgRealm.where(Pregunta.class)
                                                              .equalTo("idCuestionario", idCuestionario)
                                                              .equalTo("idEse", mPregunta.getIdEse())
                                                              .findAll();
                                                      mPregunta.setOrden(lasPreguntas.size());

                                                      final Ese laEse = bgRealm.where(Ese.class)
                                                              .equalTo("idCuestionario", idCuestionario)
                                                              .equalTo("idEse", nuevaPregunta.getIdEse())
                                                              .findFirst();
                                                      if (laEse != null) {
                                                          laEse.addPregunta(mPregunta);
                                                      }
                                                  } else {
                                                      RealmResults<Pregunta> lasPreguntas = bgRealm.where(Pregunta.class)
                                                              .equalTo("idCuestionario", idCuestionario)
                                                              .equalTo("idEse", mPregunta.getIdEse())
                                                              .equalTo("idItem", mPregunta.getIdItem())
                                                              .findAll();
                                                      mPregunta.setOrden(lasPreguntas.size());

                                                      Item mItem = bgRealm.where(Item.class)
                                                              .equalTo("idCuestionario", idCuestionario)
                                                              .equalTo("idEse", nuevaPregunta.getIdEse())
                                                              .equalTo("idItem", nuevaPregunta.getIdItem())
                                                              .findFirst();
                                                      if (mItem != null) {
                                                          mItem.addPregunta(mPregunta);
                                                      }
                                                  }
                                              }
                                              cargarCriteriosdDefaultPregunta(bgRealm, mPregunta);
                                          }
                                      }
                , new Realm.Transaction.OnSuccess() {
                    @Override
                    public void onSuccess() {
                        listenerCompletado.finish(true);

                    }
                }, new Realm.Transaction.OnError() {
                    @Override
                    public void onError(Throwable error) {
                        listenerCompletado.finish(false);
                    }
                }
        );
    }

    public void agregarItem(final String idCuestionario, final Item nuevoItem, final AdapterItems elAdapter, final ResultListener<Boolean> listenerCompletado) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransactionAsync(new Realm.Transaction() {
                                          @Override
                                          public void execute(Realm bgRealm) {
                                              Item mItem = bgRealm.copyToRealm(nuevoItem);
                                              String idEse = nuevoItem.getIdEse();

                                              RealmResults<Pregunta> losItem = bgRealm.where(Pregunta.class)
                                                      .equalTo("idCuestionario", idCuestionario)
                                                      .equalTo("idEse", nuevoItem.getIdEse())
                                                      .findAll();
                                              mItem.setOrden(losItem.size());

                                              final Ese laEse = bgRealm.where(Ese.class)
                                                      .equalTo("idCuestionario", idCuestionario)
                                                      .equalTo("idEse", idEse)
                                                      .findFirst();
                                              if (laEse != null) {
                                                  laEse.addItem(mItem);
                                              }
                                          }
                                      }
                , new Realm.Transaction.OnSuccess() {
                    @Override
                    public void onSuccess() {
                        listenerCompletado.finish(true);

                    }
                }, new Realm.Transaction.OnError() {
                    @Override
                    public void onError(Throwable error) {
                        listenerCompletado.finish(false);

                    }
                }
        );
    }

    public void borrarPregunta(final Pregunta unPregunta, final AdapterPreguntas adapterPreguntas) {
        final Realm realm = Realm.getDefaultInstance();

        realm.executeTransactionAsync(new Realm.Transaction() {
                                          @Override
                                          public void execute(@NonNull Realm bgRealm) {

                                              RealmResults<Criterio> losCriterios = bgRealm.where(Criterio.class)
                                                      .equalTo("idPregunta", unPregunta.getIdPregunta())
                                                      .not()
                                                      .beginsWith("idCriterio", FuncionesPublicas.IDCRITERIOS_DEFAULT)
                                                      .findAll();
                                              if (losCriterios != null) {
                                                  losCriterios.deleteAllFromRealm();
                                              }
                                              Pregunta laPregunta = bgRealm.where(Pregunta.class)
                                                      .equalTo("idCuestionario", unPregunta.getIdCuestionario())
                                                      .equalTo("idPregunta", unPregunta.getIdPregunta())
                                                      .findFirst();
                                              if (laPregunta != null) {
                                                  laPregunta.deleteFromRealm();
                                              }


                                              //reordenar preguntas


                                          }
                                      }
                , new Realm.Transaction.OnSuccess() {
                    @Override
                    public void onSuccess() {

                        Toast.makeText(context, context.getString(R.string.preguntaFueEliminada), Toast.LENGTH_SHORT).show();
                        if (adapterPreguntas != null) {
                            adapterPreguntas.remove(unPregunta);
                            adapterPreguntas.notifyDataSetChanged();
                        }
//                        SI LA PREGUNTA TIENE ITEM NULL ES UN CUESTIONARIO SIMPLE
                        if (unPregunta.getIdItem() != null) {
                            realm.executeTransactionAsync(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    RealmResults<Pregunta> pregunasParaReordenar = realm.where(Pregunta.class)
                                            .equalTo("idCuestionario", unPregunta.getIdCuestionario())
                                            .equalTo("idEse", unPregunta.getIdEse())
                                            .equalTo("idItem", unPregunta.getIdItem())
                                            .sort("orden", Sort.ASCENDING)
                                            .findAll();

                                    if (pregunasParaReordenar != null) {
                                        for (int i = 0; i < pregunasParaReordenar.size(); i++) {
                                            pregunasParaReordenar.get(i).setOrden(i + 1);
                                        }
                                    }
                                }
                            });


                        } else {
                            realm.executeTransactionAsync(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    RealmResults<Pregunta> pregunasParaReordenar = realm.where(Pregunta.class)
                                            .equalTo("idCuestionario", unPregunta.getIdCuestionario())
                                            .equalTo("idEse", unPregunta.getIdEse())
                                            .sort("orden", Sort.ASCENDING)
                                            .findAll();

                                    if (pregunasParaReordenar != null) {
                                        for (int i = 0; i < pregunasParaReordenar.size(); i++) {
                                            pregunasParaReordenar.get(i).setOrden(i + 1);
                                        }
                                    }
                                }
                            });
                        }

                    }
                }, new Realm.Transaction.OnError() {
                    @Override
                    public void onError(Throwable error) {
                        Toast.makeText(context, context.getString(R.string.laPreguntaNoSeBorro), Toast.LENGTH_SHORT).show();
                    }
                }
        );

    }

    public void borrarItem(final String idItem, final String idEse, final String idCuestionario, final AdapterItems adapterItems) {
        final Realm realm = Realm.getDefaultInstance();
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm bgRealm) {
                //BORRO LAS PREGUNTAS DE ESE ITEM

                RealmResults<Criterio> losCriterios = bgRealm.where(Criterio.class)
                        .equalTo("idCuestionario", idCuestionario)
                        .equalTo("idItem", idItem)
                        .not()
                        .beginsWith("idCriterio", FuncionesPublicas.IDCRITERIOS_DEFAULT)
                        .findAll();
                losCriterios.deleteAllFromRealm();

                RealmResults<Pregunta> lasPreguntas = bgRealm.where(Pregunta.class)
                        .equalTo("idCuestionario", idCuestionario)
                        .equalTo("idItem", idItem)
                        .equalTo("idEse", idEse)
                        .findAll();
                lasPreguntas.deleteAllFromRealm();
                //BORRO EL ITEM
                Item elItem = bgRealm.where(Item.class)
                        .equalTo("idItem", idItem)
                        .equalTo("idEse", idEse)
                        .equalTo("idCuestionario", idCuestionario)
                        .findFirst();
                if (elItem != null) {
                    elItem.deleteFromRealm();
                }
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                Toast.makeText(adapterItems.getContext(), adapterItems.getContext().getString(R.string.itemFueEliminado), Toast.LENGTH_SHORT).show();
                if (adapterItems != null) {
                    adapterItems.notifyDataSetChanged();
                }

                //REORDENAR LOS ITEM
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        RealmResults<Item> itemParaReordenar = realm.where(Item.class)
                                .equalTo("idCuestionario", idCuestionario)
                                .equalTo("idEse", idEse)
                                .sort("orden", Sort.ASCENDING)
                                .findAll();

                        if (itemParaReordenar != null) {
                            for (int i = 0; i < itemParaReordenar.size(); i++) {
                                itemParaReordenar.get(i).setOrden(i + 1);
                            }
                        }
                    }
                });


            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                Toast.makeText(adapterItems.getContext(), adapterItems.getContext().getString(R.string.itemNoEliminado), Toast.LENGTH_SHORT).show();
            }
        });


    }

    public void cambiarTextoItem(final Item unItem, final String s, final AdapterItems elAdapter) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Item elItem = realm.where(Item.class)
                        .equalTo("idItem", unItem.getIdItem())
                        .equalTo("idEse", unItem.getIdEse())
                        .equalTo("idCuestionario", unItem.getIdCuestionario())
                        .findFirst();
                if (elItem != null && !s.isEmpty()) {
                    elItem.setTituloItem(s);
                    if (elAdapter != null) {
                        elAdapter.notifyDataSetChanged();
                    }
                    Toast.makeText(context, context.getString(R.string.itemFueModificada), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void cambiarTextoPregunta(final Pregunta unaPregunta, final String s, final AdapterPreguntas elAdapter) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Pregunta laPre = realm.where(Pregunta.class)
                        .equalTo("idPregunta", unaPregunta.getIdPregunta())
                        .equalTo("idEse", unaPregunta.getIdEse())
                        .equalTo("idCuestionario", unaPregunta.getIdCuestionario())
                        .findFirst();
                if (laPre != null && !s.isEmpty()) {
                    laPre.setTextoPregunta(s);
                    if (elAdapter != null) {
                        elAdapter.notifyDataSetChanged();
                    }
                    Toast.makeText(context, context.getString(R.string.preguntaFueModificada), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public String crearPreguntaVacia(final String idCuestionario, final String idese, final String idItem) {
        Realm realm = Realm.getDefaultInstance();
        final String[] idPreguntaVacia = new String[1];

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Pregunta pregunta111 = realm.createObject(Pregunta.class, FuncionesPublicas.IDPREGUNTAS + UUID.randomUUID());
                idPreguntaVacia[0] = pregunta111.getIdPregunta();
                pregunta111.setPuntaje(null);
                pregunta111.setTextoPregunta(context.getResources().getString(R.string.clicParaCambiarPregunta));
                pregunta111.setIdCuestioniario(idCuestionario);
                pregunta111.setIdEse(idese);
                pregunta111.setIdItem(idItem);

                cargarCriteriosdDefaultPregunta(realm, pregunta111);

                Item mItem = realm.where(Item.class)
                        .equalTo("idCuestionario", idCuestionario)
                        .equalTo("idEse", idese)
                        .equalTo("idItem", idItem)
                        .findFirst();
                if (mItem != null) {
                    mItem.addPregunta(pregunta111);
                }
            }
        });
        return idPreguntaVacia[0];
    }

    public RealmList<Criterio> dameCriteriosDefault() {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Criterio> losCrit = realm.where(Criterio.class)
                .beginsWith("idCriterio", FuncionesPublicas.IDCRITERIOS_DEFAULT)
                .findAll();
        RealmList<Criterio> listaCrit = new RealmList<>();
        listaCrit.addAll(losCrit);
        return listaCrit;

    }


}
