package com.auditoria.grilla5s.DAO;

import android.content.Context;
import android.support.annotation.NonNull;

import com.auditoria.grilla5s.Model.Auditoria;
import com.auditoria.grilla5s.Model.Cuestionario;
import com.auditoria.grilla5s.Model.Ese;
import com.auditoria.grilla5s.Model.Item;
import com.auditoria.grilla5s.Model.Pregunta;
import com.auditoria.grilla5s.R;
import com.auditoria.grilla5s.Utils.FuncionesPublicas;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;



public class ControllerDatos {

    private Context context;
    private String idAuditInstanciada;

    public ControllerDatos(Context context) {
        this.context = context;
    }
    
    //region CREAR UNA NUEVA AUDITORIA Y LE COPIA LA ESTRUCTURA DE UNA AUDIT MODELO
    public String instanciarAuditoria(final String tipoArea){
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(@NonNull Realm realm) {

                Auditoria auditoriaModelo =traerAuditoria(tipoArea);

                Auditoria auditoriaNueva = realm.createObject(Auditoria.class,"Audit_" + UUID.randomUUID());
                //nuevaAuditoria.setIdAuditoria("Audit_" + UUID.randomUUID());
                idAuditInstanciada=auditoriaNueva.getIdAuditoria();
                auditoriaNueva.setFechaAuditoria(determinarFecha());
                auditoriaNueva.setEsUltimaAuditoria(false);
                auditoriaNueva.setAuditEstaCerrada(false);
                auditoriaNueva.setPuntajeFinal(0.0);

                for (Ese eseModelo:auditoriaModelo.getListaEses()
                     ) {
                    Ese eseNueva= new Ese();
                    eseNueva.setIdAudit(idAuditInstanciada);
                    eseNueva.setIdEse(eseModelo.getIdEse());
                    eseNueva.setPuntajeEse(eseModelo.getPuntajeEse());
                    eseNueva.setListaItem(new RealmList<Item>());

                    for (Item itemModelo:eseModelo.getListaItem()
                         ) {
                        Item itemNuevo = new Item();
                        itemNuevo.setIdItem(itemModelo.getIdItem());
                        itemNuevo.setIdEse(eseModelo.getIdEse());
                        itemNuevo.setIdAudit(idAuditInstanciada);
                        itemNuevo.setPuntajeItem(itemModelo.getPuntajeItem());
                        itemNuevo.setTituloItem(itemModelo.getTituloItem());
                        itemNuevo.setTextoItem(itemModelo.getTextoItem());
                        itemNuevo.setListaPreguntas(new RealmList<Pregunta>());

                        for (Pregunta preguntaModelo:itemModelo.getListaPreguntas()
                             ) {
                            Pregunta preguntaNueva=new Pregunta();
                            preguntaNueva.setIdAudit(idAuditInstanciada);
                            preguntaNueva.setIdItem(preguntaModelo.getIdItem());
                            preguntaNueva.setIdEse(eseModelo.getIdEse());
                            preguntaNueva.setIdPregunta(preguntaModelo.getIdPregunta());
                            preguntaNueva.setTextoPregunta(preguntaModelo.getTextoPregunta());
                            preguntaNueva.setPuntaje(preguntaModelo.getPuntaje());
                            itemNuevo.addPregunta(preguntaNueva);
                        }
                        eseNueva.addItem(itemNuevo);
                    }
                    auditoriaNueva.addEse(eseNueva);
                }

            }
        });

        return idAuditInstanciada;
    }

    //endregion

    private Auditoria traerAuditoria(String idCuestionario){
        Realm realm = Realm.getDefaultInstance();
        Auditoria unaAuditoriaModelo=new Auditoria();

        Cuestionario elCuestionario = realm.where(Cuestionario.class)
                .equalTo("idCuestionario",idCuestionario)
                .findFirst();
        if (elCuestionario!=null) {
            RealmList<Ese>listaEse = new RealmList<>();
            for (Ese unaEses :
                    elCuestionario.getListaEses()) {
                RealmList<Item> listaItem=new RealmList<>();
                for (Item unItem :
                        unaEses.getListaItem()) {
                    RealmList<Pregunta>listaPregunta=new RealmList<>();
                    listaPregunta.addAll(unItem.getListaPreguntas());
                    unItem.addlistaPreguntas(listaPregunta);
                    listaItem.add(unItem);
                }
                unaEses.setListaItem(listaItem);
                listaEse.add(unaEses);
            }
            unaAuditoriaModelo.setListaEses(listaEse);
        }
        return unaAuditoriaModelo;
    }


    private java.util.Date determinarFecha(){
        Calendar cal = Calendar.getInstance();
        return (cal.getTime());
    }

    public List<String> traerEses(){
        List<String>lista =new ArrayList<>();
        lista.add("1S Seiri");
        lista.add("2S Seiton");
        lista.add("3S Seiso");
        lista.add("4S Seiketsu");
        lista.add("5S Shitsuke");
        return lista;
    }

    public List<String> traerListaViewPager(){
        List<String>unaLista=new ArrayList<>();
        unaLista.add(FuncionesPublicas.AUDITORIA);
        unaLista.add(FuncionesPublicas.RANKING);
        unaLista.add(FuncionesPublicas.AREAS);
        return unaLista;
    }
    public List<String> traerListaVerAudit() {
        List<String>lista =new ArrayList<>();
        lista.add("1");
        lista.add("2");
        lista.add("3");
        lista.add("4");
        lista.add("5");
        return lista;
    }
    public List<String> traerTitulosVerAudit(){
        List<String>lista =new ArrayList<>();
        lista.add("1S Seiri");
        lista.add("2S Seiton");
        lista.add("3S Seiso");
        lista.add("4S Seiketsu");
        lista.add("5S Shitsuke");
        return lista;
    }



    public void crearCuestionariosDefault(final String nombreArea){

        Realm nBgRealm=Realm.getDefaultInstance();

        //region  CREACION CUESTIONARIO A
        nBgRealm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(@NonNull Realm realm) {
                Cuestionario nuevoCuestionario = realm.createObject(Cuestionario.class,"cuesDefault_"+UUID.randomUUID());
                
                nuevoCuestionario.setNombreCuestionario(nombreArea);

                    Ese ese1= realm.createObject(Ese.class);
                    ese1.setIdEse(String.valueOf(1));
                    ese1.setIdCuestionario(nuevoCuestionario.getIdCuestionario());
                    ese1.setPuntajeEse(0.0);
    //                  PRIMER ITEM
                        Item item11 = realm.createObject(Item.class);
                        item11.setTituloItem(context.getResources().getString(R.string.criterio11));
                        item11.setTextoItem(context.getResources().getString(R.string.texto11));
                        item11.setIdItem(FuncionesPublicas.IDITEMS+UUID.randomUUID());
                        item11.setIdEse(ese1.getIdEse());
                        item11.setIdCuestionario(nuevoCuestionario.getIdCuestionario());
                        item11.setPuntajeItem(0.0);
                            Pregunta pregunta111 = realm.createObject(Pregunta.class);
                            pregunta111.setIdPregunta(FuncionesPublicas.IDPREGUNTAS+UUID.randomUUID());
                            pregunta111.setPuntaje(null);
                            pregunta111.setTextoPregunta(context.getResources().getString(R.string.textoPregunta111));
                            pregunta111.setIdItem(item11.getIdItem());
                            pregunta111.setIdCuestioniario(nuevoCuestionario.getIdCuestionario());
                            pregunta111.setIdEse(ese1.getIdEse());
                            Pregunta pregunta112 = realm.createObject(Pregunta.class);
                            pregunta112.setIdPregunta(FuncionesPublicas.IDPREGUNTAS+UUID.randomUUID());
                            pregunta112.setPuntaje(null);
                            pregunta112.setTextoPregunta(context.getResources().getString(R.string.textoPregunta112));
                            pregunta112.setIdItem(item11.getIdItem());
                            pregunta112.setIdCuestioniario(nuevoCuestionario.getIdCuestionario());
                            pregunta112.setIdEse(ese1.getIdEse());
                        item11.addPregunta(pregunta111);
                        item11.addPregunta(pregunta112);
                    ese1.addItem(item11);

    //                      SEGUNDO ITEM
                        Item item12 = realm.createObject(Item.class);
                        item12.setTituloItem(context.getResources().getString(R.string.criterio12));
                        item12.setTextoItem(context.getResources().getString(R.string.texto12));
                        item12.setIdItem(FuncionesPublicas.IDITEMS+UUID.randomUUID());
                        item12.setIdEse(ese1.getIdEse());
                        item12.setIdCuestionario(nuevoCuestionario.getIdCuestionario());
                        item12.setPuntajeItem(0.0);

                            Pregunta pregunta121 = realm.createObject(Pregunta.class);
                            pregunta121.setIdPregunta(FuncionesPublicas.IDPREGUNTAS+UUID.randomUUID());
                            pregunta121.setPuntaje(null);
                            pregunta121.setTextoPregunta(context.getResources().getString(R.string.textoPregunta121));
                            pregunta121.setIdItem(item12.getIdItem());
                            pregunta121.setIdCuestioniario(nuevoCuestionario.getIdCuestionario());
                            pregunta121.setIdEse(ese1.getIdEse());
                            Pregunta pregunta122 = realm.createObject(Pregunta.class);
                            pregunta122.setIdPregunta(FuncionesPublicas.IDPREGUNTAS+UUID.randomUUID());
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

                    Ese ese2= realm.createObject(Ese.class);
                    ese2.setIdEse(String.valueOf(2));
                    ese2.setIdCuestionario(nuevoCuestionario.getIdCuestionario());
                    ese2.setPuntajeEse(0.0);

    //                        PRIMER ITEM
                        Item item21 = realm.createObject(Item.class);
                        item21.setTituloItem(context.getResources().getString(R.string.criterio21));
                        item21.setTextoItem(context.getResources().getString(R.string.texto21));
                        item21.setIdItem(FuncionesPublicas.IDITEMS+UUID.randomUUID());
                        item21.setIdEse(ese2.getIdEse());
                        item21.setIdCuestionario(nuevoCuestionario.getIdCuestionario());
                        item21.setPuntajeItem(0.0);

                            Pregunta pregunta211 = realm.createObject(Pregunta.class);
                            pregunta211.setIdPregunta(FuncionesPublicas.IDPREGUNTAS+UUID.randomUUID());
                            pregunta211.setPuntaje(null);
                            pregunta211.setTextoPregunta(context.getResources().getString(R.string.textoPregunta211));
                            pregunta211.setIdItem(item21.getIdItem());
                            pregunta211.setIdCuestioniario(nuevoCuestionario.getIdCuestionario());
                            pregunta211.setIdEse(ese2.getIdEse());

                            Pregunta pregunta212 = realm.createObject(Pregunta.class);
                            pregunta212.setIdPregunta(FuncionesPublicas.IDPREGUNTAS+UUID.randomUUID());
                            pregunta212.setPuntaje(null);
                            pregunta212.setTextoPregunta(context.getResources().getString(R.string.textoPregunta212));
                            pregunta212.setIdItem(item21.getIdItem());
                            pregunta212.setIdCuestioniario(nuevoCuestionario.getIdCuestionario());
                            pregunta212.setIdEse(ese2.getIdEse());


                        item21.addPregunta(pregunta211);
                        item21.addPregunta(pregunta212);


                    ese2.addItem(item21);

    //                      SEGUNDO ITEM
                        Item item22 = realm.createObject(Item.class);
                        item22.setTituloItem(context.getResources().getString(R.string.criterio22));
                        item22.setTextoItem(context.getResources().getString(R.string.texto22));
                        item22.setIdItem(FuncionesPublicas.IDITEMS+UUID.randomUUID());
                        item22.setIdEse(ese2.getIdEse());
                        item22.setIdCuestionario(nuevoCuestionario.getIdCuestionario());
                        item22.setPuntajeItem(0.0);

                            Pregunta pregunta221 = realm.createObject(Pregunta.class);
                            pregunta221.setIdPregunta(FuncionesPublicas.IDPREGUNTAS+UUID.randomUUID());
                            pregunta221.setPuntaje(null);
                            pregunta221.setTextoPregunta(context.getResources().getString(R.string.textoPregunta221));
                            pregunta221.setIdItem(item22.getIdItem());
                            pregunta221.setIdCuestioniario(nuevoCuestionario.getIdCuestionario());
                            pregunta221.setIdEse(ese2.getIdEse());
                            Pregunta pregunta222 = realm.createObject(Pregunta.class);
                            pregunta222.setIdPregunta(FuncionesPublicas.IDPREGUNTAS+UUID.randomUUID());
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

                    Ese ese3= realm.createObject(Ese.class);
                    ese3.setIdEse(String.valueOf(3));
                    ese3.setIdCuestionario(nuevoCuestionario.getIdCuestionario());
                    ese3.setPuntajeEse(0.0);

    //                        PRIMER ITEM
                        Item item31 = realm.createObject(Item.class);
                        item31.setTituloItem(context.getResources().getString(R.string.criterio31));
                        item31.setTextoItem(context.getResources().getString(R.string.texto31));
                        item31.setIdItem(FuncionesPublicas.IDITEMS+UUID.randomUUID());
                        item31.setIdEse(ese3.getIdEse());
                        item31.setIdCuestionario(nuevoCuestionario.getIdCuestionario());
                        item31.setPuntajeItem(0.0);

                            Pregunta pregunta311 = realm.createObject(Pregunta.class);
                            pregunta311.setIdPregunta(FuncionesPublicas.IDPREGUNTAS+UUID.randomUUID());
                            pregunta311.setPuntaje(null);
                            pregunta311.setTextoPregunta(context.getResources().getString(R.string.textoPregunta311));
                            pregunta311.setIdItem(item31.getIdItem());
                            pregunta311.setIdCuestioniario(nuevoCuestionario.getIdCuestionario());
                            pregunta311.setIdEse(ese3.getIdEse());
                            Pregunta pregunta312 = realm.createObject(Pregunta.class);
                            pregunta312.setIdPregunta(FuncionesPublicas.IDPREGUNTAS+UUID.randomUUID());
                            pregunta312.setPuntaje(null);
                            pregunta312.setTextoPregunta(context.getResources().getString(R.string.textoPregunta312));
                            pregunta312.setIdItem(item31.getIdItem());
                            pregunta312.setIdCuestioniario(nuevoCuestionario.getIdCuestionario());
                            pregunta312.setIdEse(ese3.getIdEse());

                        item31.addPregunta(pregunta311);
                        item31.addPregunta(pregunta312);


                    ese3.addItem(item31);

    //                      SEGUNDO ITEM
                        Item item32 = realm.createObject(Item.class);
                        item32.setTituloItem(context.getResources().getString(R.string.criterio32));
                        item32.setTextoItem(context.getResources().getString(R.string.texto32));
                        item32.setIdItem(FuncionesPublicas.IDITEMS+UUID.randomUUID());
                        item32.setIdEse(ese3.getIdEse());
                        item32.setIdCuestionario(nuevoCuestionario.getIdCuestionario());
                        item32.setPuntajeItem(0.0);

                            Pregunta pregunta321 = realm.createObject(Pregunta.class);
                            pregunta321.setIdPregunta(FuncionesPublicas.IDPREGUNTAS+UUID.randomUUID());
                            pregunta321.setPuntaje(null);
                            pregunta321.setTextoPregunta(context.getResources().getString(R.string.textoPregunta321));
                            pregunta321.setIdItem(item32.getIdItem());
                            pregunta321.setIdCuestioniario(nuevoCuestionario.getIdCuestionario());
                            pregunta321.setIdEse(ese3.getIdEse());
                            Pregunta pregunta322 = realm.createObject(Pregunta.class);
                            pregunta322.setIdPregunta(FuncionesPublicas.IDPREGUNTAS+UUID.randomUUID());
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

                    Ese ese4= realm.createObject(Ese.class);
                    ese4.setIdEse(String.valueOf(4));
                    ese4.setIdCuestionario(nuevoCuestionario.getIdCuestionario());
                    ese4.setPuntajeEse(0.0);

    //                        PRIMER ITEM
                        Item item41 = realm.createObject(Item.class);
                        item41.setTituloItem(context.getResources().getString(R.string.criterio41));
                        item41.setTextoItem(context.getResources().getString(R.string.texto41));
                        item41.setIdItem(FuncionesPublicas.IDITEMS+UUID.randomUUID());
                        item41.setIdEse(ese4.getIdEse());
                        item41.setIdCuestionario(nuevoCuestionario.getIdCuestionario());
                        item41.setPuntajeItem(0.0);

                            Pregunta pregunta411 = realm.createObject(Pregunta.class);
                            pregunta411.setIdPregunta(FuncionesPublicas.IDPREGUNTAS+UUID.randomUUID());
                            pregunta411.setPuntaje(null);
                            pregunta411.setTextoPregunta(context.getResources().getString(R.string.textoPregunta411));
                            pregunta411.setIdItem(item41.getIdItem());
                            pregunta411.setIdCuestioniario(nuevoCuestionario.getIdCuestionario());
                            pregunta411.setIdEse(ese4.getIdEse());
                            Pregunta pregunta412 = realm.createObject(Pregunta.class);
                            pregunta412.setIdPregunta(FuncionesPublicas.IDPREGUNTAS+UUID.randomUUID());
                            pregunta412.setPuntaje(null);
                            pregunta412.setTextoPregunta(context.getResources().getString(R.string.textoPregunta412));
                            pregunta412.setIdItem(item41.getIdItem());
                            pregunta412.setIdCuestioniario(nuevoCuestionario.getIdCuestionario());
                            pregunta412.setIdEse(ese4.getIdEse());


                        item41.addPregunta(pregunta411);
                        item41.addPregunta(pregunta412);


                    ese4.addItem(item41);

    //                      SEGUNDO ITEM
                        Item item42 = realm.createObject(Item.class);
                        item42.setTituloItem(context.getResources().getString(R.string.criterio42));
                        item42.setTextoItem(context.getResources().getString(R.string.texto42));
                        item42.setIdItem(FuncionesPublicas.IDITEMS+UUID.randomUUID());
                        item42.setIdEse(ese4.getIdEse());
                        item42.setIdCuestionario(nuevoCuestionario.getIdCuestionario());
                        item42.setPuntajeItem(0.0);

                            Pregunta pregunta421 = realm.createObject(Pregunta.class);
                            pregunta421.setIdPregunta(FuncionesPublicas.IDPREGUNTAS+UUID.randomUUID());
                            pregunta421.setPuntaje(null);
                            pregunta421.setTextoPregunta(context.getResources().getString(R.string.textoPregunta421));
                            pregunta421.setIdItem(item42.getIdItem());
                            pregunta421.setIdCuestioniario(nuevoCuestionario.getIdCuestionario());
                            pregunta421.setIdEse(ese4.getIdEse());
                            Pregunta pregunta422 = realm.createObject(Pregunta.class);
                            pregunta422.setIdPregunta(FuncionesPublicas.IDPREGUNTAS+UUID.randomUUID());
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
                    Ese ese5= realm.createObject(Ese.class);
                    ese5.setIdEse(String.valueOf(5));
                    ese5.setIdCuestionario(nuevoCuestionario.getIdCuestionario());
                    ese5.setPuntajeEse(0.0);
    //                        PRIMER ITEM
                        Item item51 = realm.createObject(Item.class);
                        item51.setTituloItem(context.getResources().getString(R.string.criterio51));
                        item51.setTextoItem(context.getResources().getString(R.string.texto51));
                        item51.setIdItem(FuncionesPublicas.IDITEMS+UUID.randomUUID());
                        item51.setIdEse(ese5.getIdEse());
                        item51.setIdCuestionario(nuevoCuestionario.getIdCuestionario());
                        item51.setPuntajeItem(0.0);

                            Pregunta pregunta511 = realm.createObject(Pregunta.class);
                            pregunta511.setIdPregunta(FuncionesPublicas.IDPREGUNTAS+UUID.randomUUID());
                            pregunta511.setPuntaje(null);
                            pregunta511.setTextoPregunta(context.getResources().getString(R.string.textoPregunta511));
                            pregunta511.setIdItem(item51.getIdItem());
                            pregunta511.setIdCuestioniario(nuevoCuestionario.getIdCuestionario());
                            pregunta511.setIdEse(ese5.getIdEse());
                            Pregunta pregunta512 = realm.createObject(Pregunta.class);
                            pregunta512.setIdPregunta(FuncionesPublicas.IDPREGUNTAS+UUID.randomUUID());
                            pregunta512.setPuntaje(null);
                            pregunta512.setTextoPregunta(context.getResources().getString(R.string.textoPregunta512));
                            pregunta512.setIdItem(item51.getIdItem());
                            pregunta512.setIdCuestioniario(nuevoCuestionario.getIdCuestionario());
                            pregunta512.setIdEse(ese5.getIdEse());

                        item51.addPregunta(pregunta511);
                        item51.addPregunta(pregunta512);


                    ese5.addItem(item51);

    //                      SEGUNDO ITEM
                        Item item52 = realm.createObject(Item.class);
                        item52.setTituloItem(context.getResources().getString(R.string.criterio52));
                        item52.setTextoItem(context.getResources().getString(R.string.texto52));
                        item52.setIdItem(FuncionesPublicas.IDITEMS+UUID.randomUUID());
                        item52.setIdEse(ese5.getIdEse());
                        item52.setIdCuestionario(nuevoCuestionario.getIdCuestionario());
                        item52.setPuntajeItem(0.0);

                            Pregunta pregunta521 = realm.createObject(Pregunta.class);
                            pregunta521.setIdPregunta(FuncionesPublicas.IDPREGUNTAS+UUID.randomUUID());
                            pregunta521.setPuntaje(null);
                            pregunta521.setTextoPregunta(context.getResources().getString(R.string.textoPregunta521));
                            pregunta521.setIdItem(item52.getIdItem());
                            pregunta521.setIdCuestioniario(nuevoCuestionario.getIdCuestionario());
                            pregunta521.setIdEse(ese5.getIdEse());
                            Pregunta pregunta522 = realm.createObject(Pregunta.class);
                            pregunta522.setIdPregunta(FuncionesPublicas.IDPREGUNTAS+UUID.randomUUID());
                            pregunta522.setPuntaje(null);
                            pregunta522.setTextoPregunta(context.getResources().getString(R.string.textoPregunta522));
                            pregunta522.setIdItem(item52.getIdItem());
                            pregunta522.setIdCuestioniario(nuevoCuestionario.getIdCuestionario());
                            pregunta522.setIdEse(ese5.getIdEse());
                        item52.addPregunta(pregunta521);
                        item52.addPregunta(pregunta522);

                    ese5.addItem(item52);


                nuevoCuestionario.addEse(ese5);



            }
        });



            }

    public void crearNuevoCuestionario(final String nombreCuestionario){
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(@NonNull Realm realm) {
                RealmResults<Cuestionario> listaCuestionarios = realm.where(Cuestionario.class)
                        .findAll();
                if (listaCuestionarios!=null){
                    Cuestionario nuevoCuestionario = realm.createObject(Cuestionario.class,"cues_"+UUID.randomUUID());
                    nuevoCuestionario.setNombreCuestionario(nombreCuestionario);
                }
            }
        });
    }

    public void eliminarCuestionario(final String idCuestionario){

        FuncionesPublicas.eliminarCuestionario(idCuestionario,context);

    }



}
