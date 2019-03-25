package com.nomad.mrg5s.DAO;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
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
import com.nomad.mrg5s.View.Activities.ActivityAuditoria;
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

//              PRIMERA ESE
            Ese ese1 = realm.createObject(Ese.class, FuncionesPublicas.IDESES + UUID.randomUUID());
            ese1.setIdCuestionario(nuevoCuestionario.getIdCuestionario());
            ese1.setPuntajeEse(0.0);

            ese1.setNombreEse(FuncionesPublicas.PRIMERA_ESE);
            ese1.setNumeroEse(1);
            Pregunta pregunta111 = realm.createObject(Pregunta.class, FuncionesPublicas.IDPREGUNTAS + UUID.randomUUID());
            pregunta111.setPuntaje(null);
            pregunta111.setTextoPregunta(context.getResources().getString(R.string.textoPregunta111));
            pregunta111.setIdCuestioniario(nuevoCuestionario.getIdCuestionario());
            pregunta111.setIdEse(ese1.getIdEse());


            Pregunta pregunta112 = realm.createObject(Pregunta.class, FuncionesPublicas.IDPREGUNTAS + UUID.randomUUID());
            pregunta112.setPuntaje(null);
            pregunta112.setTextoPregunta(context.getResources().getString(R.string.textoPregunta112));
            pregunta112.setIdCuestioniario(nuevoCuestionario.getIdCuestionario());
            pregunta112.setIdEse(ese1.getIdEse());
            ese1.addPregunta(pregunta111);
            ese1.addPregunta(pregunta112);


//              SEGUNDA ESE
            Ese ese2 = realm.createObject(Ese.class, FuncionesPublicas.IDESES + UUID.randomUUID());
            ese2.setIdCuestionario(nuevoCuestionario.getIdCuestionario());
            ese2.setNombreEse(FuncionesPublicas.SEGUNDA_ESE);
            ese2.setNumeroEse(2);
            ese2.setPuntajeEse(0.0);
            Pregunta pregunta121 = realm.createObject(Pregunta.class, FuncionesPublicas.IDPREGUNTAS + UUID.randomUUID());
            pregunta121.setPuntaje(null);
            pregunta121.setTextoPregunta(context.getResources().getString(R.string.textoPregunta121));
            pregunta121.setIdCuestioniario(nuevoCuestionario.getIdCuestionario());
            pregunta121.setIdEse(ese1.getIdEse());
            Pregunta pregunta122 = realm.createObject(Pregunta.class, FuncionesPublicas.IDPREGUNTAS + UUID.randomUUID());
            pregunta122.setPuntaje(null);
            pregunta122.setTextoPregunta(context.getResources().getString(R.string.textoPregunta122));
            pregunta122.setIdCuestioniario(nuevoCuestionario.getIdCuestionario());
            pregunta122.setIdEse(ese1.getIdEse());
            ese2.addPregunta(pregunta121);
            ese2.addPregunta(pregunta122);

//              TERCERA ESE
            Ese ese3 = realm.createObject(Ese.class, FuncionesPublicas.IDESES + UUID.randomUUID());
            ese3.setIdCuestionario(nuevoCuestionario.getIdCuestionario());
            ese3.setNombreEse(FuncionesPublicas.TERCERA_ESE);
            ese3.setNumeroEse(3);
            ese3.setPuntajeEse(0.0);
            Pregunta pregunta211 = realm.createObject(Pregunta.class, FuncionesPublicas.IDPREGUNTAS + UUID.randomUUID());
            pregunta211.setPuntaje(null);
            pregunta211.setTextoPregunta(context.getResources().getString(R.string.textoPregunta211));
            pregunta211.setIdCuestioniario(nuevoCuestionario.getIdCuestionario());
            pregunta211.setIdEse(ese2.getIdEse());
            Pregunta pregunta212 = realm.createObject(Pregunta.class, FuncionesPublicas.IDPREGUNTAS + UUID.randomUUID());
            pregunta212.setPuntaje(null);
            pregunta212.setTextoPregunta(context.getResources().getString(R.string.textoPregunta212));
            pregunta212.setIdCuestioniario(nuevoCuestionario.getIdCuestionario());
            pregunta212.setIdEse(ese2.getIdEse());
            ese3.addPregunta(pregunta211);
            ese3.addPregunta(pregunta212);

//              CUARTA ESE
            Ese ese4 = realm.createObject(Ese.class, FuncionesPublicas.IDESES + UUID.randomUUID());
            ese4.setIdCuestionario(nuevoCuestionario.getIdCuestionario());
            ese4.setNombreEse(FuncionesPublicas.CUARTA_ESE);
            ese4.setNumeroEse(4);
            ese4.setPuntajeEse(0.0);
            Pregunta pregunta321 = realm.createObject(Pregunta.class, FuncionesPublicas.IDPREGUNTAS + UUID.randomUUID());
            pregunta321.setPuntaje(null);
            pregunta321.setTextoPregunta(context.getResources().getString(R.string.textoPregunta321));
            pregunta321.setIdCuestioniario(nuevoCuestionario.getIdCuestionario());
            pregunta321.setIdEse(ese3.getIdEse());
            Pregunta pregunta322 = realm.createObject(Pregunta.class, FuncionesPublicas.IDPREGUNTAS + UUID.randomUUID());
            pregunta322.setPuntaje(null);
            pregunta322.setTextoPregunta(context.getResources().getString(R.string.textoPregunta322));
            pregunta322.setIdCuestioniario(nuevoCuestionario.getIdCuestionario());
            pregunta322.setIdEse(ese3.getIdEse());
            ese4.addPregunta(pregunta321);
            ese4.addPregunta(pregunta322);

//              QUINTA ESE
            Ese ese5 = realm.createObject(Ese.class, FuncionesPublicas.IDESES + UUID.randomUUID());
            ese5.setIdCuestionario(nuevoCuestionario.getIdCuestionario());
            ese5.setNombreEse(FuncionesPublicas.QUINTA_ESE);
            ese5.setNumeroEse(5);
            ese5.setPuntajeEse(0.0);
            Pregunta pregunta411 = realm.createObject(Pregunta.class, FuncionesPublicas.IDPREGUNTAS + UUID.randomUUID());
            pregunta411.setPuntaje(null);
            pregunta411.setTextoPregunta(context.getResources().getString(R.string.textoPregunta411));
            pregunta411.setIdCuestioniario(nuevoCuestionario.getIdCuestionario());
            pregunta411.setIdEse(ese4.getIdEse());
            Pregunta pregunta412 = realm.createObject(Pregunta.class, FuncionesPublicas.IDPREGUNTAS + UUID.randomUUID());
            pregunta412.setPuntaje(null);
            pregunta412.setTextoPregunta(context.getResources().getString(R.string.textoPregunta412));
            pregunta412.setIdCuestioniario(nuevoCuestionario.getIdCuestionario());
            pregunta412.setIdEse(ese4.getIdEse());
            ese5.addPregunta(pregunta411);
            ese5.addPregunta(pregunta412);

            if (esEstructuraSimple) {
                nuevoCuestionario.addEse(ese1);
                nuevoCuestionario.addEse(ese2);
                nuevoCuestionario.addEse(ese3);
                nuevoCuestionario.addEse(ese4);
                nuevoCuestionario.addEse(ese5);
            }

            for (Ese ese : nuevoCuestionario.getListaEses()
                    ) {
                for (Pregunta preg :
                        ese.getListaPreguntas()) {
                    cargarCriteriosdDefaultPregunta(realm, preg);
                }
            }

            crearCuestionarioFirebase(nuevoCuestionario);
            }
        });



        //endregion
    }

    public void crearCuestionariosDefault(final String nombreArea) {
        Realm nBgRealm = Realm.getDefaultInstance();
        //region  CREACION CUESTIONARIO ESTRUCTURADO
        nBgRealm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(@NonNull Realm realm) {
                Cuestionario nuevoCuestionario = realm.createObject(Cuestionario.class, FuncionesPublicas.IDCUESTIONARIOS_DEFAULT + UUID.randomUUID());

                nuevoCuestionario.setNombreCuestionario(nombreArea);
                nuevoCuestionario.setTipoCuestionario(FuncionesPublicas.ESTRUCTURA_ESTRUCTURADA);

                Ese ese1 = realm.createObject(Ese.class, FuncionesPublicas.IDESES + UUID.randomUUID());
                ese1.setIdCuestionario(nuevoCuestionario.getIdCuestionario());
                ese1.setNombreEse(FuncionesPublicas.PRIMERA_ESE);
                ese1.setNumeroEse(1);
                ese1.setPuntajeEse(0.0);
                //                  PRIMER ITEM
                Item item11 = realm.createObject(Item.class, FuncionesPublicas.IDITEMS + UUID.randomUUID());
                item11.setTituloItem(context.getResources().getString(R.string.criterio11));
                item11.setTextoItem(context.getResources().getString(R.string.texto11));
                item11.setIdEse(ese1.getIdEse());
                item11.setIdCuestionario(nuevoCuestionario.getIdCuestionario());
                item11.setPuntajeItem(0.0);
                Pregunta pregunta111 = realm.createObject(Pregunta.class, FuncionesPublicas.IDPREGUNTAS + UUID.randomUUID());
                pregunta111.setPuntaje(null);
                pregunta111.setTextoPregunta(context.getResources().getString(R.string.textoPregunta111));
                pregunta111.setIdItem(item11.getIdItem());
                pregunta111.setIdCuestioniario(nuevoCuestionario.getIdCuestionario());
                pregunta111.setIdEse(ese1.getIdEse());
                Pregunta pregunta112 = realm.createObject(Pregunta.class, FuncionesPublicas.IDPREGUNTAS + UUID.randomUUID());
                pregunta112.setPuntaje(null);
                pregunta112.setTextoPregunta(context.getResources().getString(R.string.textoPregunta112));
                pregunta112.setIdItem(item11.getIdItem());
                pregunta112.setIdCuestioniario(nuevoCuestionario.getIdCuestionario());
                pregunta112.setIdEse(ese1.getIdEse());
                item11.addPregunta(pregunta111);
                item11.addPregunta(pregunta112);
                ese1.addItem(item11);

                //                      SEGUNDO ITEM
                Item item12 = realm.createObject(Item.class, FuncionesPublicas.IDITEMS + UUID.randomUUID());
                item12.setTituloItem(context.getResources().getString(R.string.criterio12));
                item12.setTextoItem(context.getResources().getString(R.string.texto12));
                item12.setIdEse(ese1.getIdEse());
                item12.setIdCuestionario(nuevoCuestionario.getIdCuestionario());
                item12.setPuntajeItem(0.0);

                Pregunta pregunta121 = realm.createObject(Pregunta.class, FuncionesPublicas.IDPREGUNTAS + UUID.randomUUID());
                pregunta121.setPuntaje(null);
                pregunta121.setTextoPregunta(context.getResources().getString(R.string.textoPregunta121));
                pregunta121.setIdItem(item12.getIdItem());
                pregunta121.setIdCuestioniario(nuevoCuestionario.getIdCuestionario());
                pregunta121.setIdEse(ese1.getIdEse());
                Pregunta pregunta122 = realm.createObject(Pregunta.class, FuncionesPublicas.IDPREGUNTAS + UUID.randomUUID());
                pregunta122.setPuntaje(null);
                pregunta122.setTextoPregunta(context.getResources().getString(R.string.textoPregunta122));
                pregunta122.setIdItem(item12.getIdItem());
                pregunta122.setIdCuestioniario(nuevoCuestionario.getIdCuestionario());
                pregunta122.setIdEse(ese1.getIdEse());

                item12.addPregunta(pregunta121);
                item12.addPregunta(pregunta122);
                ese1.addItem(item12);
                nuevoCuestionario.addEse(ese1);

//                SEGUNDA ESE

                Ese ese2 = realm.createObject(Ese.class, FuncionesPublicas.IDESES + UUID.randomUUID());
                ese2.setIdCuestionario(nuevoCuestionario.getIdCuestionario());
                ese2.setNombreEse(FuncionesPublicas.SEGUNDA_ESE);
                ese2.setNumeroEse(2);
                ese2.setPuntajeEse(0.0);

                //                        PRIMER ITEM
                Item item21 = realm.createObject(Item.class, FuncionesPublicas.IDITEMS + UUID.randomUUID());
                item21.setTituloItem(context.getResources().getString(R.string.criterio21));
                item21.setTextoItem(context.getResources().getString(R.string.texto21));
                item21.setIdEse(ese2.getIdEse());
                item21.setIdCuestionario(nuevoCuestionario.getIdCuestionario());
                item21.setPuntajeItem(0.0);

                Pregunta pregunta211 = realm.createObject(Pregunta.class, FuncionesPublicas.IDPREGUNTAS + UUID.randomUUID());
                pregunta211.setPuntaje(null);
                pregunta211.setTextoPregunta(context.getResources().getString(R.string.textoPregunta211));
                pregunta211.setIdItem(item21.getIdItem());
                pregunta211.setIdCuestioniario(nuevoCuestionario.getIdCuestionario());
                pregunta211.setIdEse(ese2.getIdEse());

                Pregunta pregunta212 = realm.createObject(Pregunta.class, FuncionesPublicas.IDPREGUNTAS + UUID.randomUUID());
                pregunta212.setPuntaje(null);
                pregunta212.setTextoPregunta(context.getResources().getString(R.string.textoPregunta212));
                pregunta212.setIdItem(item21.getIdItem());
                pregunta212.setIdCuestioniario(nuevoCuestionario.getIdCuestionario());
                pregunta212.setIdEse(ese2.getIdEse());


                item21.addPregunta(pregunta211);
                item21.addPregunta(pregunta212);


                ese2.addItem(item21);

                //                      SEGUNDO ITEM
                Item item22 = realm.createObject(Item.class, FuncionesPublicas.IDITEMS + UUID.randomUUID());
                item22.setTituloItem(context.getResources().getString(R.string.criterio22));
                item22.setTextoItem(context.getResources().getString(R.string.texto22));
                item22.setIdEse(ese2.getIdEse());
                item22.setIdCuestionario(nuevoCuestionario.getIdCuestionario());
                item22.setPuntajeItem(0.0);

                Pregunta pregunta221 = realm.createObject(Pregunta.class, FuncionesPublicas.IDPREGUNTAS + UUID.randomUUID());
                pregunta221.setPuntaje(null);
                pregunta221.setTextoPregunta(context.getResources().getString(R.string.textoPregunta221));
                pregunta221.setIdItem(item22.getIdItem());
                pregunta221.setIdCuestioniario(nuevoCuestionario.getIdCuestionario());
                pregunta221.setIdEse(ese2.getIdEse());
                Pregunta pregunta222 = realm.createObject(Pregunta.class, FuncionesPublicas.IDPREGUNTAS + UUID.randomUUID());
                pregunta222.setPuntaje(null);
                pregunta222.setTextoPregunta(context.getResources().getString(R.string.textoPregunta222));
                pregunta222.setIdItem(item22.getIdItem());
                pregunta222.setIdCuestioniario(nuevoCuestionario.getIdCuestionario());
                pregunta222.setIdEse(ese2.getIdEse());

                item22.addPregunta(pregunta221);
                item22.addPregunta(pregunta222);

                ese2.addItem(item22);


                nuevoCuestionario.addEse(ese2);

//                TERCERA ESE

                Ese ese3 = realm.createObject(Ese.class, FuncionesPublicas.IDESES + UUID.randomUUID());
                ese3.setIdCuestionario(nuevoCuestionario.getIdCuestionario());
                ese3.setNombreEse(FuncionesPublicas.TERCERA_ESE);
                ese3.setNumeroEse(3);
                ese3.setPuntajeEse(0.0);

                //                        PRIMER ITEM
                Item item31 = realm.createObject(Item.class, FuncionesPublicas.IDITEMS + UUID.randomUUID());
                item31.setTituloItem(context.getResources().getString(R.string.criterio31));
                item31.setTextoItem(context.getResources().getString(R.string.texto31));
                item31.setIdEse(ese3.getIdEse());
                item31.setIdCuestionario(nuevoCuestionario.getIdCuestionario());
                item31.setPuntajeItem(0.0);

                Pregunta pregunta311 = realm.createObject(Pregunta.class, FuncionesPublicas.IDPREGUNTAS + UUID.randomUUID());
                pregunta311.setPuntaje(null);
                pregunta311.setTextoPregunta(context.getResources().getString(R.string.textoPregunta311));
                pregunta311.setIdItem(item31.getIdItem());
                pregunta311.setIdCuestioniario(nuevoCuestionario.getIdCuestionario());
                pregunta311.setIdEse(ese3.getIdEse());
                Pregunta pregunta312 = realm.createObject(Pregunta.class, FuncionesPublicas.IDPREGUNTAS + UUID.randomUUID());
                pregunta312.setPuntaje(null);
                pregunta312.setTextoPregunta(context.getResources().getString(R.string.textoPregunta312));
                pregunta312.setIdItem(item31.getIdItem());
                pregunta312.setIdCuestioniario(nuevoCuestionario.getIdCuestionario());
                pregunta312.setIdEse(ese3.getIdEse());

                item31.addPregunta(pregunta311);
                item31.addPregunta(pregunta312);


                ese3.addItem(item31);

                //                      SEGUNDO ITEM
                Item item32 = realm.createObject(Item.class, FuncionesPublicas.IDITEMS + UUID.randomUUID());
                item32.setTituloItem(context.getResources().getString(R.string.criterio32));
                item32.setTextoItem(context.getResources().getString(R.string.texto32));
                item32.setIdEse(ese3.getIdEse());
                item32.setIdCuestionario(nuevoCuestionario.getIdCuestionario());
                item32.setPuntajeItem(0.0);

                Pregunta pregunta321 = realm.createObject(Pregunta.class, FuncionesPublicas.IDPREGUNTAS + UUID.randomUUID());
                pregunta321.setPuntaje(null);
                pregunta321.setTextoPregunta(context.getResources().getString(R.string.textoPregunta321));
                pregunta321.setIdItem(item32.getIdItem());
                pregunta321.setIdCuestioniario(nuevoCuestionario.getIdCuestionario());
                pregunta321.setIdEse(ese3.getIdEse());
                Pregunta pregunta322 = realm.createObject(Pregunta.class, FuncionesPublicas.IDPREGUNTAS + UUID.randomUUID());
                pregunta322.setPuntaje(null);
                pregunta322.setTextoPregunta(context.getResources().getString(R.string.textoPregunta322));
                pregunta322.setIdItem(item32.getIdItem());
                pregunta322.setIdCuestioniario(nuevoCuestionario.getIdCuestionario());
                pregunta322.setIdEse(ese3.getIdEse());

                item32.addPregunta(pregunta321);
                item32.addPregunta(pregunta322);

                ese3.addItem(item32);

                nuevoCuestionario.addEse(ese3);

                //                CUARTA ESE

                Ese ese4 = realm.createObject(Ese.class, FuncionesPublicas.IDESES + UUID.randomUUID());
                ese4.setIdCuestionario(nuevoCuestionario.getIdCuestionario());
                ese4.setNombreEse(FuncionesPublicas.CUARTA_ESE);
                ese4.setNumeroEse(4);
                ese4.setPuntajeEse(0.0);

                //                        PRIMER ITEM
                Item item41 = realm.createObject(Item.class, FuncionesPublicas.IDITEMS + UUID.randomUUID());
                item41.setTituloItem(context.getResources().getString(R.string.criterio41));
                item41.setTextoItem(context.getResources().getString(R.string.texto41));
                item41.setIdEse(ese4.getIdEse());
                item41.setIdCuestionario(nuevoCuestionario.getIdCuestionario());
                item41.setPuntajeItem(0.0);

                Pregunta pregunta411 = realm.createObject(Pregunta.class, FuncionesPublicas.IDPREGUNTAS + UUID.randomUUID());
                pregunta411.setPuntaje(null);
                pregunta411.setTextoPregunta(context.getResources().getString(R.string.textoPregunta411));
                pregunta411.setIdItem(item41.getIdItem());
                pregunta411.setIdCuestioniario(nuevoCuestionario.getIdCuestionario());
                pregunta411.setIdEse(ese4.getIdEse());
                Pregunta pregunta412 = realm.createObject(Pregunta.class, FuncionesPublicas.IDPREGUNTAS + UUID.randomUUID());
                pregunta412.setPuntaje(null);
                pregunta412.setTextoPregunta(context.getResources().getString(R.string.textoPregunta412));
                pregunta412.setIdItem(item41.getIdItem());
                pregunta412.setIdCuestioniario(nuevoCuestionario.getIdCuestionario());
                pregunta412.setIdEse(ese4.getIdEse());


                item41.addPregunta(pregunta411);
                item41.addPregunta(pregunta412);


                ese4.addItem(item41);

                //                      SEGUNDO ITEM
                Item item42 = realm.createObject(Item.class, FuncionesPublicas.IDITEMS + UUID.randomUUID());
                item42.setTituloItem(context.getResources().getString(R.string.criterio42));
                item42.setTextoItem(context.getResources().getString(R.string.texto42));
                item42.setIdEse(ese4.getIdEse());
                item42.setIdCuestionario(nuevoCuestionario.getIdCuestionario());
                item42.setPuntajeItem(0.0);

                Pregunta pregunta421 = realm.createObject(Pregunta.class, FuncionesPublicas.IDPREGUNTAS + UUID.randomUUID());
                pregunta421.setPuntaje(null);
                pregunta421.setTextoPregunta(context.getResources().getString(R.string.textoPregunta421));
                pregunta421.setIdItem(item42.getIdItem());
                pregunta421.setIdCuestioniario(nuevoCuestionario.getIdCuestionario());
                pregunta421.setIdEse(ese4.getIdEse());
                Pregunta pregunta422 = realm.createObject(Pregunta.class, FuncionesPublicas.IDPREGUNTAS + UUID.randomUUID());
                pregunta422.setPuntaje(null);
                pregunta422.setTextoPregunta(context.getResources().getString(R.string.textoPregunta422));
                pregunta422.setIdItem(item42.getIdItem());
                pregunta422.setIdCuestioniario(nuevoCuestionario.getIdCuestionario());
                pregunta422.setIdEse(ese4.getIdEse());
                item42.addPregunta(pregunta421);
                item42.addPregunta(pregunta422);

                ese4.addItem(item42);


                nuevoCuestionario.addEse(ese4);


                //QUINTA ESE
                Ese ese5 = realm.createObject(Ese.class, FuncionesPublicas.IDESES + UUID.randomUUID());
                ese5.setIdCuestionario(nuevoCuestionario.getIdCuestionario());
                ese5.setNombreEse(FuncionesPublicas.QUINTA_ESE);
                ese5.setNumeroEse(5);
                ese5.setPuntajeEse(0.0);
                //                        PRIMER ITEM
                Item item51 = realm.createObject(Item.class, FuncionesPublicas.IDITEMS + UUID.randomUUID());
                item51.setTituloItem(context.getResources().getString(R.string.criterio51));
                item51.setTextoItem(context.getResources().getString(R.string.texto51));
                item51.setIdEse(ese5.getIdEse());
                item51.setIdCuestionario(nuevoCuestionario.getIdCuestionario());
                item51.setPuntajeItem(0.0);

                Pregunta pregunta511 = realm.createObject(Pregunta.class, FuncionesPublicas.IDPREGUNTAS + UUID.randomUUID());
                pregunta511.setPuntaje(null);
                pregunta511.setTextoPregunta(context.getResources().getString(R.string.textoPregunta511));
                pregunta511.setIdItem(item51.getIdItem());
                pregunta511.setIdCuestioniario(nuevoCuestionario.getIdCuestionario());
                pregunta511.setIdEse(ese5.getIdEse());
                Pregunta pregunta512 = realm.createObject(Pregunta.class, FuncionesPublicas.IDPREGUNTAS + UUID.randomUUID());
                pregunta512.setPuntaje(null);
                pregunta512.setTextoPregunta(context.getResources().getString(R.string.textoPregunta512));
                pregunta512.setIdItem(item51.getIdItem());
                pregunta512.setIdCuestioniario(nuevoCuestionario.getIdCuestionario());
                pregunta512.setIdEse(ese5.getIdEse());

                item51.addPregunta(pregunta511);
                item51.addPregunta(pregunta512);


                ese5.addItem(item51);

                //                      SEGUNDO ITEM
                Item item52 = realm.createObject(Item.class, FuncionesPublicas.IDITEMS + UUID.randomUUID());
                item52.setTituloItem(context.getResources().getString(R.string.criterio52));
                item52.setTextoItem(context.getResources().getString(R.string.texto52));
                item52.setIdEse(ese5.getIdEse());
                item52.setIdCuestionario(nuevoCuestionario.getIdCuestionario());
                item52.setPuntajeItem(0.0);

                Pregunta pregunta521 = realm.createObject(Pregunta.class, FuncionesPublicas.IDPREGUNTAS + UUID.randomUUID());

                pregunta521.setPuntaje(null);
                pregunta521.setTextoPregunta(context.getResources().getString(R.string.textoPregunta521));
                pregunta521.setIdItem(item52.getIdItem());
                pregunta521.setIdCuestioniario(nuevoCuestionario.getIdCuestionario());
                pregunta521.setIdEse(ese5.getIdEse());
                Pregunta pregunta522 = realm.createObject(Pregunta.class, FuncionesPublicas.IDPREGUNTAS + UUID.randomUUID());
                pregunta522.setPuntaje(null);
                pregunta522.setTextoPregunta(context.getResources().getString(R.string.textoPregunta522));
                pregunta522.setIdItem(item52.getIdItem());
                pregunta522.setIdCuestioniario(nuevoCuestionario.getIdCuestionario());
                pregunta522.setIdEse(ese5.getIdEse());
                item52.addPregunta(pregunta521);
                item52.addPregunta(pregunta522);

                ese5.addItem(item52);


                nuevoCuestionario.addEse(ese5);

                for (Ese ese :
                        nuevoCuestionario.getListaEses()) {
                    for (Item item :
                            ese.getListaItem()) {
                        for (Pregunta preg :
                                item.getListaPreguntas()) {
                            cargarCriteriosdDefaultPregunta(realm, preg);
                        }
                    }
                }

                crearCuestionarioFirebase(nuevoCuestionario);

            }
        });
        //endregion
    }

    public void crearCriteriosDefault() {
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
            for (int i = 0; i <listaCriteriosDefault.size(); i++) {

                Criterio unCriterio = realm.createObject(Criterio.class, FuncionesPublicas.IDCRITERIOS + UUID.randomUUID());
                if (preg.getIdItem() != null) {
                    unCriterio.setIdItem(preg.getIdItem());
                }
                unCriterio.setIdCuestionario(preg.getIdCuestionario());
                unCriterio.setIdEse(preg.getIdEse());
                unCriterio.setIdPregunta(preg.getIdPregunta());
                unCriterio.setPuntajeCriterio(i + 1);
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

    public void crearNuevoCuestionario(final String nombreCuestionario, final String tipoCuestionario) {
        Realm realm = Realm.getDefaultInstance();

        realm.executeTransaction(new Realm.Transaction() {
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
                ese1.setNumeroEse(i+1);
                nuevoCuestionario.addEse(ese1);
            }

            }
        });
    }


    //--- METODOS FIREBASE
    public void crearCuestionarioFirebase(Cuestionario elCues) {
        DatabaseReference mbase= FirebaseDatabase.getInstance().getReference();

        mbase.child("Cuestionarios").child(elCues.getIdCuestionario()).child("1-Nombre").setValue(elCues.getNombreCuestionario());
        mbase.child("Cuestionarios").child(elCues.getIdCuestionario()).child("2-Tipo").setValue(elCues.getTipoCuestionario());
        mbase.child("Cuestionarios").child(elCues.getIdCuestionario()).child("3-IdCuestionario").setValue(elCues.getIdCuestionario());
        if (elCues.getTipoCuestionario().equals(FuncionesPublicas.ESTRUCTURA_SIMPLE)){
           
            for (Ese unaEse:
                    elCues.getListaEses()) {
                mbase.child("Cuestionarios").child(elCues.getIdCuestionario()).child("4-Estructura").child(unaEse.getNombreEse()).child("1-IdEse").setValue(unaEse.getIdEse());
                mbase.child("Cuestionarios").child(elCues.getIdCuestionario()).child("4-Estructura").child(unaEse.getNombreEse()).child("3-NumeroEse").setValue(String.valueOf( unaEse.getNumeroEse()));
                mbase.child("Cuestionarios").child(elCues.getIdCuestionario()).child("4-Estructura").child(unaEse.getNombreEse()).child("2-NombreEse").setValue(unaEse.getNombreEse());


                for (Pregunta unaPreg :
                        unaEse.getListaPreguntas()) {
                    mbase.child("Cuestionarios").child(elCues.getIdCuestionario()).child("4-Estructura").child(unaEse.getNombreEse()).child("4-Preguntas").child(unaPreg.getIdPregunta()).child("1-IdPregunta").setValue(unaPreg.getIdPregunta());
                    mbase.child("Cuestionarios").child(elCues.getIdCuestionario()).child("4-Estructura").child(unaEse.getNombreEse()).child("4-Preguntas").child(unaPreg.getIdPregunta()).child("2-TextoPregunta").setValue(unaPreg.getTextoPregunta());

                    for (Criterio unCrit:unaPreg.getListaCriterios()
                         ) {

                        mbase.child("Cuestionarios").child(elCues.getIdCuestionario()).child("4-Estructura").child(unaEse.getNombreEse()).child("4-Preguntas").child(unaPreg.getIdPregunta()).child("3-Criterios")
                                .child(unCrit.getIdCriterio()).child("1-IdCriterio").setValue(unCrit.getIdCriterio());
                        mbase.child("Cuestionarios").child(elCues.getIdCuestionario()).child("4-Estructura").child(unaEse.getNombreEse()).child("4-Preguntas").child(unaPreg.getIdPregunta()).child("3-Criterios")
                                .child(unCrit.getIdCriterio()).child("2-TextoCriterio").setValue(unCrit.getTextoCriterio());
                        mbase.child("Cuestionarios").child(elCues.getIdCuestionario()).child("4-Estructura").child(unaEse.getNombreEse()).child("4-Preguntas").child(unaPreg.getIdPregunta()).child("3-Criterios")
                                .child(unCrit.getIdCriterio()).child("3-PuntajeCriterio").setValue(String.valueOf(unCrit.getPuntajeCriterio()));

                    }

                }
            }
        }
        if (elCues.getTipoCuestionario().equals(FuncionesPublicas.ESTRUCTURA_ESTRUCTURADA)){
            for (Ese unaEse:
                    elCues.getListaEses()) {
                mbase.child("Cuestionarios").child(elCues.getIdCuestionario()).child("4-Estructura").child(unaEse.getNombreEse()).child("1-IdEse").setValue(unaEse.getIdEse());
                mbase.child("Cuestionarios").child(elCues.getIdCuestionario()).child("4-Estructura").child(unaEse.getNombreEse()).child("3-NumeroEse").setValue(String.valueOf( unaEse.getNumeroEse()));
                mbase.child("Cuestionarios").child(elCues.getIdCuestionario()).child("4-Estructura").child(unaEse.getNombreEse()).child("2-NombreEse").setValue(unaEse.getNombreEse());

                for (Item unItem :
                        unaEse.getListaItem()) {

                    mbase.child("Cuestionarios").child(elCues.getIdCuestionario()).child("4-Estructura").child(unaEse.getNombreEse()).child("4-Items").child(unItem.getIdItem()).child("1-IdItem").setValue(unItem.getIdItem());
                    mbase.child("Cuestionarios").child(elCues.getIdCuestionario()).child("4-Estructura").child(unaEse.getNombreEse()).child("4-Items").child(unItem.getIdItem()).child("2-TituloItem").setValue(unItem.getTituloItem());
                    mbase.child("Cuestionarios").child(elCues.getIdCuestionario()).child("4-Estructura").child(unaEse.getNombreEse()).child("4-Items").child(unItem.getIdItem()).child("3-TextoItem").setValue(unItem.getTextoItem());
                    for (Pregunta unaPreg :
                           unItem.getListaPreguntas()) {
                        mbase.child("Cuestionarios").child(elCues.getIdCuestionario()).child("4-Estructura").child(unaEse.getNombreEse()).child("4-Items").child(unItem.getIdItem()).child("4-Preguntas").child(unaPreg.getIdPregunta()).child("1-IdPregunta").setValue(unaPreg.getIdPregunta());
                        mbase.child("Cuestionarios").child(elCues.getIdCuestionario()).child("4-Estructura").child(unaEse.getNombreEse()).child("4-Items").child(unItem.getIdItem()).child("4-Preguntas").child(unaPreg.getIdPregunta()).child("2-TextoPregunta").setValue(unaPreg.getTextoPregunta());
                        for (Criterio unCrit:unaPreg.getListaCriterios()
                                ) {

                            mbase.child("Cuestionarios").child(elCues.getIdCuestionario()).child("4-Estructura").child(unaEse.getNombreEse()).child("4-Items").child(unItem.getIdItem()).child("4-Preguntas").child(unaPreg.getIdPregunta()).child("3-Criterios")
                                    .child(unCrit.getIdCriterio()).child("1-IdCriterio").setValue(unCrit.getIdCriterio());
                            mbase.child("Cuestionarios").child(elCues.getIdCuestionario()).child("4-Estructura").child(unaEse.getNombreEse()).child("4-Items").child(unItem.getIdItem()).child("4-Preguntas").child(unaPreg.getIdPregunta()).child("3-Criterios")
                                    .child(unCrit.getIdCriterio()).child("2-TextoCriterio").setValue(unCrit.getTextoCriterio());
                            mbase.child("Cuestionarios").child(elCues.getIdCuestionario()).child("4-Estructura").child(unaEse.getNombreEse()).child("4-Items").child(unItem.getIdItem()).child("4-Preguntas").child(unaPreg.getIdPregunta()).child("3-Criterios")
                                    .child(unCrit.getIdCriterio()).child("3-PuntajeCriterio").setValue(String.valueOf(unCrit.getPuntajeCriterio()));

                        }

                    }
                }
            }
        }
        
    }
    public void traerCuestionariosFirebase(){
        DatabaseReference mbase= FirebaseDatabase.getInstance().getReference();
        mbase.child("Cuestionarios").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull final DataSnapshot dataSnapshot, @Nullable String s) {


                    final String idCuestion = dataSnapshot.child("3-IdCuestionario").getValue(String.class);

                    //SOLO BAJA LOS CUESTIONARIOS SI NO EXISTEN EN LA REALM LOCAL.

                    if (noExisteCuestionario(idCuestion)) {

                        Realm realm =Realm.getDefaultInstance();
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
                                    eseNueva.setNumeroEse((Integer.parseInt((String)ese.child("3-NumeroEse").getValue())));

                                    //SI EL CUESTIONARIO ES ESTRUCTURADO
                                    if (nuevoCuestionario.getTipoCuestionario().equals(FuncionesPublicas.ESTRUCTURA_ESTRUCTURADA)) {
                                        eseNueva.setListaPreguntas(null);
                                        eseNueva.setListaItem(new RealmList<Item>());
                                        for (DataSnapshot item :
                                                ese.child("4-Items").getChildren()
                                                ) {
                                            Item itemNuevo = new Item();
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
                                                    criterioNuevo.setPuntajeCriterio((Integer.parseInt((String)criterio.child("3-PuntajeCriterio").getValue())));

                                                    Criterio mCriterio=realm.copyToRealm(criterioNuevo);
                                                    preguntaNueva.addCriterio(mCriterio);
                                                }
                                                Pregunta mPreguntaNueva = realm.copyToRealm(preguntaNueva);
                                                itemNuevo.addPregunta(mPreguntaNueva);
                                            }
                                            Item mItemNuevo=realm.copyToRealm(itemNuevo);
                                            eseNueva.addItem(mItemNuevo);
                                        }
                                        Ese mEseNueva=realm.copyToRealm(eseNueva);
                                        nuevoCuestionario.addEse(mEseNueva);
                                    }

                                    //SI EL CUESTIONARIO ES SIMPLE
                                    else {
                                        eseNueva.setListaItem(null);
                                        eseNueva.setListaPreguntas(new RealmList<Pregunta>());


                                        for (DataSnapshot pregunta :
                                                ese.child("4-Preguntas").getChildren()) {

                                            Pregunta preguntaNueva = new Pregunta();
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
                                                criterioNuevo.setPuntajeCriterio((Integer.parseInt((String)criterio.child("3-PuntajeCriterio").getValue())));

                                                Criterio mCriterio=realm.copyToRealm(criterioNuevo);
                                                preguntaNueva.addCriterio(mCriterio);
                                            }
                                            Pregunta mPreguntaNueva =realm.copyToRealm(preguntaNueva);
                                            eseNueva.addPregunta(mPreguntaNueva);
                                        }
                                        Ese mEseNueva=realm.copyToRealm(eseNueva);
                                        nuevoCuestionario.addEse(mEseNueva);
                                    }
                                }
                                realm.copyToRealm(nuevoCuestionario);
                            }
                        });
                    }

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

    private boolean noExisteCuestionario(String idCuestion) {
        Realm realm = Realm.getDefaultInstance();
        Cuestionario elCuestionarioBuscado= realm.where(Cuestionario.class)
                .equalTo("idCuestionario",idCuestion)
                .findFirst();
        if (elCuestionarioBuscado!=null){
            return false;
        }
        else{
            return true;
        }
    }
    //FIREBASE---//



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

    public void borrarBaseDatos() {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(@NonNull Realm realm) {

                RealmResults<Area> lasAreas=realm.where(Area.class)
                        .findAll();
                lasAreas.deleteAllFromRealm();

                RealmResults<Criterio> losCriterios=realm.where(Criterio.class)
                        .findAll();
                losCriterios.deleteAllFromRealm();

                RealmResults<Pregunta> lasPreguntas=realm.where(Pregunta.class)
                        .findAll();
                lasPreguntas.deleteAllFromRealm();

                RealmResults<Item> losItem=realm.where(Item.class)
                        .findAll();
                losItem.deleteAllFromRealm();

                RealmResults<Ese> lasEses=realm.where(Ese.class)
                        .findAll();
                lasEses.deleteAllFromRealm();


                RealmResults<Foto> lasFotos=realm.where(Foto.class)
                        .findAll();
                lasFotos.deleteAllFromRealm();

                RealmResults<Auditoria> lasAuditorias=realm.where(Auditoria.class)
                        .findAll();
                lasAuditorias.deleteAllFromRealm();

                RealmResults<Cuestionario> losCuestionarios=realm.where(Cuestionario.class)
                        .findAll();
                losCuestionarios.deleteAllFromRealm();

                //borrar directorios
                File path = new File(context.getExternalFilesDir(null)+ File.separator + "nomad" + File.separator + "audit5s" +File.separator+ FirebaseAuth.getInstance().getCurrentUser().getEmail());
                if (deleteDirectory(path)){
                }

            }

        });

        //crear cuestionarios default
        crearCriteriosDefault();
        crearCuestionariosDefault(context.getString(R.string.areaGeneral),true);
        crearCuestionariosDefault(context.getString(R.string.areaIndustrial));
        crearCuestionariosDefault(context.getString(R.string.areaOficina));
        crearCuestionariosDefault(context.getString(R.string.areaExterna));

    }

    public void agregarPregunta(final String idCuestionario, final Pregunta nuevaPregunta, final AdapterPreguntas adapterPreguntas) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransactionAsync(new Realm.Transaction() {
                                          @Override
                                          public void execute(Realm bgRealm) {
                                              Pregunta mPregunta = bgRealm.copyToRealm(nuevaPregunta);
                                              Cuestionario mCuestionario= bgRealm.where(Cuestionario.class)
                                                      .equalTo("idCuestionario", idCuestionario)
                                                      .findFirst();
                                              if (mCuestionario!=null){
                                                  if (mCuestionario.getTipoCuestionario().equals(FuncionesPublicas.ESTRUCTURA_SIMPLE)){
                                                      final Ese laEse = bgRealm.where(Ese.class)
                                                              .equalTo("idCuestionario", idCuestionario)
                                                              .equalTo("idEse",nuevaPregunta.getIdEse())
                                                              .findFirst();
                                                      if (laEse!=null){
                                                          laEse.addPregunta(mPregunta);
                                                      }
                                                  }
                                                  else{
                                                      Item mItem = bgRealm.where(Item.class)
                                                              .equalTo("idCuestionario", idCuestionario)
                                                              .equalTo("idEse", nuevaPregunta.getIdEse())
                                                              .equalTo("idItem", nuevaPregunta.getIdItem())
                                                              .findFirst();
                                                      if (mItem!=null){
                                                          mItem.addPregunta(mPregunta);
                                                      }
                                                  }
                                              }
                                              cargarCriteriosdDefaultPregunta(bgRealm,mPregunta);
                                          }
                                      }
                , new Realm.Transaction.OnSuccess() {
                    @Override
                    public void onSuccess() {
                        adapterPreguntas.addPregunta(nuevaPregunta);
                        adapterPreguntas.notifyDataSetChanged();
                    }
                }, new Realm.Transaction.OnError() {
                    @Override
                    public void onError(Throwable error) {
                        Toast.makeText(adapterPreguntas.getContext(), adapterPreguntas.getContext().getString(R.string.laPreguntaNoSeAgrego), Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }
    public void agregarPregunta(final String idCuestionario, final Pregunta nuevaPregunta, final AdapterPagerPreguntas adapterPreguntas) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransactionAsync(new Realm.Transaction() {
                                          @Override
                                          public void execute(Realm bgRealm) {
                                              Pregunta mPregunta = bgRealm.copyToRealm(nuevaPregunta);
                                              Cuestionario mCuestionario= bgRealm.where(Cuestionario.class)
                                                      .equalTo("idCuestionario", idCuestionario)
                                                      .findFirst();
                                              if (mCuestionario!=null){
                                                  if (mCuestionario.getTipoCuestionario().equals(FuncionesPublicas.ESTRUCTURA_SIMPLE)){
                                                      final Ese laEse = bgRealm.where(Ese.class)
                                                              .equalTo("idCuestionario", idCuestionario)
                                                              .equalTo("idEse",nuevaPregunta.getIdEse())
                                                              .findFirst();
                                                      if (laEse!=null){
                                                          laEse.addPregunta(mPregunta);
                                                      }
                                                  }
                                                  else{
                                                      Item mItem = bgRealm.where(Item.class)
                                                              .equalTo("idCuestionario", idCuestionario)
                                                              .equalTo("idEse", nuevaPregunta.getIdEse())
                                                              .equalTo("idItem", nuevaPregunta.getIdItem())
                                                              .findFirst();
                                                      if (mItem!=null){
                                                          mItem.addPregunta(mPregunta);
                                                      }
                                                  }
                                              }
                                              cargarCriteriosdDefaultPregunta(bgRealm,mPregunta);
                                          }
                                      }
                , new Realm.Transaction.OnSuccess() {
                    @Override
                    public void onSuccess() {
                        adapterPreguntas.addPregunta(nuevaPregunta);
                        adapterPreguntas.notifyDataSetChanged();
                        ActivityAuditoria unaAc = (ActivityAuditoria) context;
                        unaAc.irAPreguntaAgregada();
                    }
                }, new Realm.Transaction.OnError() {
                    @Override
                    public void onError(Throwable error) {
                        Toast.makeText(context, context.getString(R.string.laPreguntaNoSeAgrego), Toast.LENGTH_SHORT).show();
                    }
                }


        );
    }

    public void agregarItem(final String idCuestionario, final Item nuevoItem, final AdapterItems elAdapter) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransactionAsync(new Realm.Transaction() {
                                          @Override
                                          public void execute(Realm bgRealm) {
                                              Item mItem = bgRealm.copyToRealm(nuevoItem);
                                              String idEse=nuevoItem.getIdEse();

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
                        elAdapter.addItem(nuevoItem);
                        elAdapter.notifyDataSetChanged();
                    }
                }, new Realm.Transaction.OnError() {
                    @Override
                    public void onError(Throwable error) {
                        Toast.makeText(context,context.getString(R.string.elItemNoSeAgrego), Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    public void borrarPregunta(final Pregunta unPregunta, final AdapterPreguntas adapterPreguntas) {
       final Realm bgrealm=Realm.getDefaultInstance();
        bgrealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(@NonNull Realm realm) {
                RealmResults<Criterio> losCriterios=realm.where(Criterio.class)
                        .equalTo("idPregunta", unPregunta.getIdPregunta())
                        .not()
                        .beginsWith("idCriterio",FuncionesPublicas.IDCRITERIOS_DEFAULT)
                        .findAll();
                if (losCriterios!=null){
                    losCriterios.deleteAllFromRealm();
                }
                Pregunta laPregunta = realm.where(Pregunta.class)
                        .equalTo("idCuestionario", unPregunta.getIdCuestionario())
                        .equalTo("idPregunta", unPregunta.getIdPregunta())
                        .findFirst();
                if (laPregunta !=null) {
                    laPregunta.deleteFromRealm();
                }

                if (adapterPreguntas!=null) {
                    adapterPreguntas.notifyDataSetChanged();
                }

            }
        });
    }

    public  void borrarItem(final String idItem, final String idEse, final String idCuestionario, final AdapterItems adapterItems) {
        final Realm bgrealm = Realm.getDefaultInstance();
        bgrealm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                //BORRO LAS PREGUNTAS DE ESE ITEM

                RealmResults<Criterio> losCriterios=realm.where(Criterio.class)
                        .equalTo("idCuestionario", idCuestionario)
                        .equalTo("idItem", idItem)
                        .not()
                        .beginsWith("idCriterio", FuncionesPublicas.IDCRITERIOS_DEFAULT)
                        .findAll();
                losCriterios.deleteAllFromRealm();

                RealmResults<Pregunta> lasPreguntas = realm.where(Pregunta.class)
                        .equalTo("idCuestionario", idCuestionario)
                        .equalTo("idItem", idItem)
                        .equalTo("idEse", idEse)
                        .findAll();
                lasPreguntas.deleteAllFromRealm();
                //BORRO EL ITEM
                Item elItem = realm.where(Item.class)
                        .equalTo("idItem",idItem )
                        .equalTo("idEse", idEse)
                        .equalTo("idCuestionario", idCuestionario)
                        .findFirst();
                if (elItem!=null) {
                    elItem.deleteFromRealm();
                }
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                if (adapterItems!=null) {
                    adapterItems.notifyDataSetChanged();
                }
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
                if (elItem!=null&& !s.isEmpty()){
                    elItem.setTituloItem(s);
                    if (elAdapter!=null) {
                        elAdapter.notifyDataSetChanged();
                    }
                    Toast.makeText(context, context.getString(R.string.itemFueModificada), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    public void cambiarTextoPregunta(final Pregunta unaPregunta, final String s,final AdapterPreguntas elAdapter) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Pregunta laPre = realm.where(Pregunta.class)
                        .equalTo("idPregunta", unaPregunta.getIdPregunta())
                        .equalTo("idEse", unaPregunta.getIdEse())
                        .equalTo("idCuestionario", unaPregunta.getIdCuestionario())
                        .findFirst();
                if (laPre !=null&& !s.isEmpty()){
                    laPre.setTextoPregunta(s);
                    if (elAdapter!=null) {
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
                idPreguntaVacia[0] =pregunta111.getIdPregunta();
                pregunta111.setPuntaje(null);
                pregunta111.setTextoPregunta(context.getResources().getString(R.string.clicParaCambiarPregunta));
                pregunta111.setIdCuestioniario(idCuestionario);
                pregunta111.setIdEse(idese);
                pregunta111.setIdItem(idItem);

                cargarCriteriosdDefaultPregunta(realm,pregunta111);

                Item mItem = realm.where(Item.class)
                        .equalTo("idCuestionario", idCuestionario)
                        .equalTo("idEse", idese)
                        .equalTo("idItem", idItem)
                        .findFirst();
                if (mItem!=null){
                    mItem.addPregunta(pregunta111);
                }
            }
        });
        return idPreguntaVacia[0];
    }

    public RealmList<Criterio> dameCriteriosDefault() {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Criterio> losCrit= realm.where(Criterio.class)
                .beginsWith("idCriterio",FuncionesPublicas.IDCRITERIOS_DEFAULT)
                .findAll();
        RealmList<Criterio> listaCrit = new RealmList<>();
        listaCrit.addAll(losCrit);
        return listaCrit;

    }


}
