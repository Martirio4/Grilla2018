package com.auditoria.grilla5s.DAO;

import android.content.Context;
import android.widget.Toast;

import com.auditoria.grilla5s.Model.Auditoria;
import com.auditoria.grilla5s.Model.Cuestionario;
import com.auditoria.grilla5s.Model.Ese;
import com.auditoria.grilla5s.Model.Foto;
import com.auditoria.grilla5s.Model.Item;
import com.auditoria.grilla5s.Model.Pregunta;
import com.auditoria.grilla5s.R;
import com.auditoria.grilla5s.Utils.FuncionesPublicas;
import com.google.firebase.auth.FirebaseAuth;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;


/**
 * Created by elmar on 9/2/2018.
 */

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
            public void execute(Realm realm) {

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
                        itemNuevo.setIdAudit(idAuditInstanciada);
                        itemNuevo.setPuntajeItem(itemModelo.getPuntajeItem());
                        itemNuevo.setCriterio(itemModelo.getCriterio());
                        itemNuevo.setTextoItem(itemModelo.getTextoItem());
                        itemNuevo.setListaPreguntas(new RealmList<Pregunta>());

                        for (Pregunta preguntaModelo:itemModelo.getListaPreguntas()
                             ) {
                            Pregunta preguntaNueva=new Pregunta();
                            preguntaNueva.setIdAudit(idAuditInstanciada);
                            preguntaNueva.setIdItem(preguntaModelo.getIdItem());
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

    public Auditoria traerAuditoria(String idCuestionario){
        Realm realm = Realm.getDefaultInstance();
        
        Auditoria unaAuditoriaModelo=new Auditoria();
        
        Cuestionario cuestionarioBuscado = realm.where(Cuestionario.class)
                .equalTo("idCuestionario", idCuestionario)
                .findFirst();
        
        if (cuestionarioBuscado!=null){
            unaAuditoriaModelo.setListaEses(cuestionarioBuscado.getListaEses());
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
        unaLista.add("auditoria");
        unaLista.add("ranking");
        unaLista.add("areas");
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



    public void crearCuestionariosDefault(){

        Realm nBgRealm=Realm.getDefaultInstance();

        //region  CREACION CUESTIONARIO A
        nBgRealm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Cuestionario nuevoCuestionario = realm.createObject(Cuestionario.class,"1");
                
                nuevoCuestionario.setNombreCuestionario(context.getString(R.string.areaIndustrial));

                Ese ese1= realm.createObject(Ese.class);
                ese1.setIdEse(1);
                ese1.setPuntajeEse(0.0);

//                        PRIMER ITEM
                Item item11 = realm.createObject(Item.class);
                item11.setCriterio(context.getResources().getString(R.string.criterio11));
                item11.setTextoItem(context.getResources().getString(R.string.texto11));
                item11.setIdItem(1);
                item11.setPuntajeItem(0.0);


                Pregunta pregunta111 = realm.createObject(Pregunta.class);
                pregunta111.setIdPregunta(111);
                pregunta111.setPuntaje(null);
                pregunta111.setTextoPregunta(context.getResources().getString(R.string.textoPregunta111));
                pregunta111.setIdItem(11);
                Pregunta pregunta112 = realm.createObject(Pregunta.class);
                pregunta112.setIdPregunta(112);
                pregunta112.setPuntaje(null);
                pregunta112.setTextoPregunta(context.getResources().getString(R.string.textoPregunta112));
                pregunta112.setIdItem(11);
                Pregunta pregunta113 = realm.createObject(Pregunta.class);
                pregunta113.setIdPregunta(113);
                pregunta113.setPuntaje(null);
                pregunta113.setTextoPregunta(context.getResources().getString(R.string.textoPregunta113));
                pregunta113.setIdItem(11);
                Pregunta pregunta114 = realm.createObject(Pregunta.class);
                pregunta114.setIdPregunta(114);
                pregunta114.setPuntaje(null);
                pregunta114.setTextoPregunta(context.getResources().getString(R.string.textoPregunta114));
                pregunta114.setIdItem(11);
                Pregunta pregunta115 = realm.createObject(Pregunta.class);
                pregunta115.setIdPregunta(115);
                pregunta115.setPuntaje(null);
                pregunta115.setTextoPregunta(context.getResources().getString(R.string.textoPregunta115));
                pregunta115.setIdItem(11);
                item11.addPregunta(pregunta111);
                item11.addPregunta(pregunta112);
                item11.addPregunta(pregunta113);
                item11.addPregunta(pregunta114);
                item11.addPregunta(pregunta115);

                ese1.addItem(item11);

//                      SEGUNDO ITEM
                Item item12 = realm.createObject(Item.class);
                item12.setCriterio(context.getResources().getString(R.string.criterio12));
                item12.setTextoItem(context.getResources().getString(R.string.texto12));
                item12.setIdItem(2);
                item12.setPuntajeItem(0.0);

                Pregunta pregunta121 = realm.createObject(Pregunta.class);
                pregunta121.setIdPregunta(121);
                pregunta121.setPuntaje(null);
                pregunta121.setTextoPregunta(context.getResources().getString(R.string.textoPregunta121));
                pregunta121.setIdItem(12);
                Pregunta pregunta122 = realm.createObject(Pregunta.class);
                pregunta122.setIdPregunta(122);
                pregunta122.setPuntaje(null);
                pregunta122.setTextoPregunta(context.getResources().getString(R.string.textoPregunta122));
                pregunta122.setIdItem(12);
                Pregunta pregunta123 = realm.createObject(Pregunta.class);
                pregunta123.setIdPregunta(123);
                pregunta123.setPuntaje(null);
                pregunta123.setTextoPregunta(context.getResources().getString(R.string.textoPregunta123));
                pregunta123.setIdItem(12);
                Pregunta pregunta124 = realm.createObject(Pregunta.class);
                pregunta124.setIdPregunta(124);
                pregunta124.setPuntaje(null);
                pregunta124.setTextoPregunta(context.getResources().getString(R.string.textoPregunta124));
                pregunta124.setIdItem(12);
                item12.addPregunta(pregunta121);
                item12.addPregunta(pregunta122);
                item12.addPregunta(pregunta123);
                item12.addPregunta(pregunta124);

                ese1.addItem(item12);

//                      TERCER ITEM
                Item item13 = realm.createObject(Item.class);
                item13.setCriterio(context.getResources().getString(R.string.criterio13));
                item13.setTextoItem(context.getResources().getString(R.string.texto13));
                item13.setIdItem(3);
                item13.setPuntajeItem(0.0);

                Pregunta pregunta131 = realm.createObject(Pregunta.class);
                pregunta131.setIdPregunta(131);
                pregunta131.setPuntaje(null);
                pregunta131.setTextoPregunta(context.getResources().getString(R.string.textoPregunta131));
                pregunta131.setIdItem(13);
                Pregunta pregunta132 = realm.createObject(Pregunta.class);
                pregunta132.setIdPregunta(132);
                pregunta132.setPuntaje(null);
                pregunta132.setTextoPregunta(context.getResources().getString(R.string.textoPregunta132));
                pregunta132.setIdItem(13);
                item13.addPregunta(pregunta131);
                item13.addPregunta(pregunta132);

                ese1.addItem(item13);

//                      CUARTO ITEM
                Item item14 = realm.createObject(Item.class);
                item14.setCriterio(context.getResources().getString(R.string.criterio14));
                item14.setTextoItem(context.getResources().getString(R.string.texto14));
                item14.setIdItem(4);
                item14.setPuntajeItem(0.0);

                Pregunta pregunta141 = realm.createObject(Pregunta.class);
                pregunta141.setIdPregunta(141);
                pregunta141.setPuntaje(null);
                pregunta141.setTextoPregunta(context.getResources().getString(R.string.textoPregunta141));
                pregunta141.setIdItem(14);
                Pregunta pregunta142 = realm.createObject(Pregunta.class);
                pregunta142.setIdPregunta(142);
                pregunta142.setPuntaje(null);
                pregunta142.setTextoPregunta(context.getResources().getString(R.string.textoPregunta142));
                pregunta142.setIdItem(14);
                Pregunta pregunta143 = realm.createObject(Pregunta.class);
                pregunta143.setIdPregunta(143);
                pregunta143.setPuntaje(null);
                pregunta143.setTextoPregunta(context.getResources().getString(R.string.textoPregunta143));
                pregunta143.setIdItem(14);
                Pregunta pregunta144 = realm.createObject(Pregunta.class);
                pregunta144.setIdPregunta(144);
                pregunta144.setPuntaje(null);
                pregunta144.setTextoPregunta(context.getResources().getString(R.string.textoPregunta144));
                pregunta144.setIdItem(14);
                item14.addPregunta(pregunta141);
                item14.addPregunta(pregunta142);
                item14.addPregunta(pregunta143);
                item14.addPregunta(pregunta144);

                ese1.addItem(item14);

//                      QUINTO ITEM
                Item item15 = realm.createObject(Item.class);
                item15.setCriterio(context.getResources().getString(R.string.criterio15));
                item15.setTextoItem(context.getResources().getString(R.string.texto15));
                item15.setIdItem(5);
                item15.setPuntajeItem(0.0);


                Pregunta pregunta151 = realm.createObject(Pregunta.class);

                pregunta151.setIdPregunta(151);
                pregunta151.setPuntaje(null);
                pregunta151.setTextoPregunta(context.getResources().getString(R.string.textoPregunta151));
                pregunta151.setIdItem(15);
                Pregunta pregunta152 = realm.createObject(Pregunta.class);
                pregunta152.setIdPregunta(152);
                pregunta152.setPuntaje(null);
                pregunta152.setTextoPregunta(context.getResources().getString(R.string.textoPregunta152));
                pregunta152.setIdItem(15);
                item15.addPregunta(pregunta151);
                item15.addPregunta(pregunta152);

                ese1.addItem(item15);
                nuevoCuestionario.addEse(ese1);

//                SEGUNDA ESE

                Ese ese2= realm.createObject(Ese.class);
                ese2.setIdEse(2);
                ese2.setPuntajeEse(0.0);

//                        PRIMER ITEM
                Item item21 = realm.createObject(Item.class);
                item21.setCriterio(context.getResources().getString(R.string.criterio21));
                item21.setTextoItem(context.getResources().getString(R.string.texto21));
                item21.setIdItem(1);
                item21.setPuntajeItem(0.0);

                Pregunta pregunta211 = realm.createObject(Pregunta.class);
                pregunta211.setIdPregunta(211);
                pregunta211.setPuntaje(null);
                pregunta211.setTextoPregunta(context.getResources().getString(R.string.textoPregunta211));
                pregunta211.setIdItem(21);
                Pregunta pregunta212 = realm.createObject(Pregunta.class);
                pregunta212.setIdPregunta(212);
                pregunta212.setPuntaje(null);
                pregunta212.setTextoPregunta(context.getResources().getString(R.string.textoPregunta212));
                pregunta212.setIdItem(21);
                Pregunta pregunta213 = realm.createObject(Pregunta.class);
                pregunta213.setIdPregunta(213);
                pregunta213.setPuntaje(null);
                pregunta213.setTextoPregunta(context.getResources().getString(R.string.textoPregunta213));
                pregunta213.setIdItem(21);
                Pregunta pregunta214 = realm.createObject(Pregunta.class);
                pregunta214.setIdPregunta(214);
                pregunta214.setPuntaje(null);
                pregunta214.setTextoPregunta(context.getResources().getString(R.string.textoPregunta214));
                pregunta214.setIdItem(21);
                Pregunta pregunta215 = realm.createObject(Pregunta.class);
                pregunta215.setIdPregunta(215);
                pregunta215.setPuntaje(null);
                pregunta215.setTextoPregunta(context.getResources().getString(R.string.textoPregunta215));
                pregunta215.setIdItem(21);
                Pregunta pregunta216 = realm.createObject(Pregunta.class);
                pregunta216.setIdPregunta(216);
                pregunta216.setPuntaje(null);
                pregunta216.setTextoPregunta(context.getResources().getString(R.string.textoPregunta216));
                pregunta216.setIdItem(21);
                Pregunta pregunta217 = realm.createObject(Pregunta.class);
                pregunta217.setIdPregunta(217);
                pregunta217.setPuntaje(null);
                pregunta217.setTextoPregunta(context.getResources().getString(R.string.textoPregunta217));
                pregunta217.setIdItem(21);
                Pregunta pregunta218 = realm.createObject(Pregunta.class);
                pregunta218.setIdPregunta(218);
                pregunta218.setPuntaje(null);
                pregunta218.setTextoPregunta(context.getResources().getString(R.string.textoPregunta218));
                pregunta218.setIdItem(21);
                item21.addPregunta(pregunta211);
                item21.addPregunta(pregunta212);
                item21.addPregunta(pregunta213);
                item21.addPregunta(pregunta214);
                item21.addPregunta(pregunta215);
                item21.addPregunta(pregunta216);
                item21.addPregunta(pregunta217);
                item21.addPregunta(pregunta218);

                ese2.addItem(item21);

//                      SEGUNDO ITEM
                Item item22 = realm.createObject(Item.class);
                item22.setCriterio(context.getResources().getString(R.string.criterio22));
                item22.setTextoItem(context.getResources().getString(R.string.texto22));
                item22.setIdItem(2);
                item22.setPuntajeItem(0.0);

                Pregunta pregunta221 = realm.createObject(Pregunta.class);
                pregunta221.setIdPregunta(221);
                pregunta221.setPuntaje(null);
                pregunta221.setTextoPregunta(context.getResources().getString(R.string.textoPregunta221));
                pregunta221.setIdItem(22);
                Pregunta pregunta222 = realm.createObject(Pregunta.class);
                pregunta222.setIdPregunta(222);
                pregunta222.setPuntaje(null);
                pregunta222.setTextoPregunta(context.getResources().getString(R.string.textoPregunta222));
                pregunta222.setIdItem(22);
                Pregunta pregunta223 = realm.createObject(Pregunta.class);
                pregunta223.setIdPregunta(223);
                pregunta223.setPuntaje(null);
                pregunta223.setTextoPregunta(context.getResources().getString(R.string.textoPregunta223));
                pregunta223.setIdItem(22);
                Pregunta pregunta224 = realm.createObject(Pregunta.class);
                pregunta224.setIdPregunta(224);
                pregunta224.setPuntaje(null);
                pregunta224.setTextoPregunta(context.getResources().getString(R.string.textoPregunta224));
                pregunta224.setIdItem(22);
                item22.addPregunta(pregunta221);
                item22.addPregunta(pregunta222);
                item22.addPregunta(pregunta223);
                item22.addPregunta(pregunta224);

                ese2.addItem(item22);

//                      TERCER ITEM
                Item item23 = realm.createObject(Item.class);
                item23.setCriterio(context.getResources().getString(R.string.criterio23));
                item23.setTextoItem(context.getResources().getString(R.string.texto23));
                item23.setIdItem(3);
                item23.setPuntajeItem(0.0);

                Pregunta pregunta231 = realm.createObject(Pregunta.class);
                pregunta231.setIdPregunta(231);
                pregunta231.setPuntaje(null);
                pregunta231.setTextoPregunta(context.getResources().getString(R.string.textoPregunta231));
                pregunta231.setIdItem(23);
                Pregunta pregunta232 = realm.createObject(Pregunta.class);
                pregunta232.setIdPregunta(232);
                pregunta232.setPuntaje(null);
                pregunta232.setTextoPregunta(context.getResources().getString(R.string.textoPregunta232));
                pregunta232.setIdItem(23);
                Pregunta pregunta233 = realm.createObject(Pregunta.class);
                pregunta233.setIdPregunta(233);
                pregunta233.setPuntaje(null);
                pregunta233.setTextoPregunta(context.getResources().getString(R.string.textoPregunta233));
                pregunta233.setIdItem(23);
                Pregunta pregunta234 = realm.createObject(Pregunta.class);
                pregunta234.setIdPregunta(234);
                pregunta234.setPuntaje(null);
                pregunta234.setTextoPregunta(context.getResources().getString(R.string.textoPregunta234));
                pregunta234.setIdItem(23);
                Pregunta pregunta235 = realm.createObject(Pregunta.class);
                pregunta235.setIdPregunta(235);
                pregunta235.setPuntaje(null);
                pregunta235.setTextoPregunta(context.getResources().getString(R.string.textoPregunta235));
                pregunta235.setIdItem(23);
                Pregunta pregunta236 = realm.createObject(Pregunta.class);
                pregunta236.setIdPregunta(236);
                pregunta236.setPuntaje(null);
                pregunta236.setTextoPregunta(context.getResources().getString(R.string.textoPregunta236));
                pregunta236.setIdItem(23);
                item23.addPregunta(pregunta231);
                item23.addPregunta(pregunta232);
                item23.addPregunta(pregunta233);
                item23.addPregunta(pregunta234);
                item23.addPregunta(pregunta235);
                item23.addPregunta(pregunta236);

                ese2.addItem(item23);

//                      CUARTO ITEM
                Item item24 = realm.createObject(Item.class);
                item24.setCriterio(context.getResources().getString(R.string.criterio24));
                item24.setTextoItem(context.getResources().getString(R.string.texto24));
                item24.setIdItem(4);
                item24.setPuntajeItem(0.0);

                Pregunta pregunta241 = realm.createObject(Pregunta.class);
                pregunta241.setIdPregunta(241);
                pregunta241.setPuntaje(null);
                pregunta241.setTextoPregunta(context.getResources().getString(R.string.textoPregunta241));
                pregunta241.setIdItem(24);
                Pregunta pregunta242 = realm.createObject(Pregunta.class);
                pregunta242.setIdPregunta(242);
                pregunta242.setPuntaje(null);
                pregunta242.setTextoPregunta(context.getResources().getString(R.string.textoPregunta242));
                pregunta242.setIdItem(24);
                Pregunta pregunta243 = realm.createObject(Pregunta.class);
                pregunta243.setIdPregunta(243);
                pregunta243.setPuntaje(null);
                pregunta243.setTextoPregunta(context.getResources().getString(R.string.textoPregunta243));
                pregunta243.setIdItem(24);
                Pregunta pregunta244 = realm.createObject(Pregunta.class);
                pregunta244.setIdPregunta(244);
                pregunta244.setPuntaje(null);
                pregunta244.setTextoPregunta(context.getResources().getString(R.string.textoPregunta244));
                pregunta244.setIdItem(24);
                Pregunta pregunta245 = realm.createObject(Pregunta.class);
                pregunta245.setIdPregunta(245);
                pregunta245.setPuntaje(null);
                pregunta245.setTextoPregunta(context.getResources().getString(R.string.textoPregunta245));
                pregunta245.setIdItem(24);
                Pregunta pregunta246 = realm.createObject(Pregunta.class);
                pregunta246.setIdPregunta(246);
                pregunta246.setPuntaje(null);
                pregunta246.setTextoPregunta(context.getResources().getString(R.string.textoPregunta246));
                pregunta246.setIdItem(24);
                item24.addPregunta(pregunta241);
                item24.addPregunta(pregunta242);
                item24.addPregunta(pregunta243);
                item24.addPregunta(pregunta244);
                item24.addPregunta(pregunta245);
                item24.addPregunta(pregunta246);

                ese2.addItem(item24);

//                      QUINTO ITEM
                Item item25 = realm.createObject(Item.class);
                item25.setCriterio(context.getResources().getString(R.string.criterio25));
                item25.setTextoItem(context.getResources().getString(R.string.texto25));
                item25.setIdItem(5);
                item25.setPuntajeItem(0.0);

                Pregunta pregunta251 = realm.createObject(Pregunta.class);
                pregunta251.setIdPregunta(251);
                pregunta251.setPuntaje(null);
                pregunta251.setTextoPregunta(context.getResources().getString(R.string.textoPregunta251));
                pregunta251.setIdItem(25);
                Pregunta pregunta252 = realm.createObject(Pregunta.class);
                pregunta252.setIdPregunta(252);
                pregunta252.setPuntaje(null);
                pregunta252.setTextoPregunta(context.getResources().getString(R.string.textoPregunta252));
                pregunta252.setIdItem(25);
                Pregunta pregunta253 = realm.createObject(Pregunta.class);
                pregunta253.setIdPregunta(253);
                pregunta253.setPuntaje(null);
                pregunta253.setTextoPregunta(context.getResources().getString(R.string.textoPregunta253));
                pregunta253.setIdItem(25);
                Pregunta pregunta254 = realm.createObject(Pregunta.class);
                pregunta254.setIdPregunta(254);
                pregunta254.setPuntaje(null);
                pregunta254.setTextoPregunta(context.getResources().getString(R.string.textoPregunta254));
                pregunta254.setIdItem(25);
                item25.addPregunta(pregunta251);
                item25.addPregunta(pregunta252);
                item25.addPregunta(pregunta253);
                item25.addPregunta(pregunta254);

                ese2.addItem(item25);
                nuevoCuestionario.addEse(ese2);

//                TERCERA ESE

                Ese ese3= realm.createObject(Ese.class);
                ese3.setIdEse(3);
                ese3.setPuntajeEse(0.0);

//                        PRIMER ITEM
                Item item31 = realm.createObject(Item.class);
                item31.setCriterio(context.getResources().getString(R.string.criterio31));
                item31.setTextoItem(context.getResources().getString(R.string.texto31));
                item31.setIdItem(1);
                item31.setPuntajeItem(0.0);

                Pregunta pregunta311 = realm.createObject(Pregunta.class);
                pregunta311.setIdPregunta(311);
                pregunta311.setPuntaje(null);
                pregunta311.setTextoPregunta(context.getResources().getString(R.string.textoPregunta311));
                pregunta311.setIdItem(31);
                Pregunta pregunta312 = realm.createObject(Pregunta.class);
                pregunta312.setIdPregunta(312);
                pregunta312.setPuntaje(null);
                pregunta312.setTextoPregunta(context.getResources().getString(R.string.textoPregunta312));
                pregunta312.setIdItem(31);
                Pregunta pregunta313 = realm.createObject(Pregunta.class);
                pregunta313.setIdPregunta(313);
                pregunta313.setPuntaje(null);
                pregunta313.setTextoPregunta(context.getResources().getString(R.string.textoPregunta313));
                pregunta313.setIdItem(31);
                item31.addPregunta(pregunta311);
                item31.addPregunta(pregunta312);
                item31.addPregunta(pregunta313);

                ese3.addItem(item31);

//                      SEGUNDO ITEM
                Item item32 = realm.createObject(Item.class);
                item32.setCriterio(context.getResources().getString(R.string.criterio32));
                item32.setTextoItem(context.getResources().getString(R.string.texto32));
                item32.setIdItem(2);
                item32.setPuntajeItem(0.0);

                Pregunta pregunta321 = realm.createObject(Pregunta.class);
                pregunta321.setIdPregunta(321);
                pregunta321.setPuntaje(null);
                pregunta321.setTextoPregunta(context.getResources().getString(R.string.textoPregunta321));
                pregunta321.setIdItem(32);
                Pregunta pregunta322 = realm.createObject(Pregunta.class);
                pregunta322.setIdPregunta(322);
                pregunta322.setPuntaje(null);
                pregunta322.setTextoPregunta(context.getResources().getString(R.string.textoPregunta322));
                pregunta322.setIdItem(32);
                Pregunta pregunta323 = realm.createObject(Pregunta.class);
                pregunta323.setIdPregunta(323);
                pregunta323.setPuntaje(null);
                pregunta323.setTextoPregunta(context.getResources().getString(R.string.textoPregunta323));
                pregunta323.setIdItem(32);
                Pregunta pregunta324 = realm.createObject(Pregunta.class);
                pregunta324.setIdPregunta(324);
                pregunta324.setPuntaje(null);
                pregunta324.setTextoPregunta(context.getResources().getString(R.string.textoPregunta324));
                pregunta324.setIdItem(32);
                Pregunta pregunta325 = realm.createObject(Pregunta.class);
                pregunta325.setIdPregunta(325);
                pregunta325.setPuntaje(null);
                pregunta325.setTextoPregunta(context.getResources().getString(R.string.textoPregunta325));
                pregunta325.setIdItem(32);
                item32.addPregunta(pregunta321);
                item32.addPregunta(pregunta322);
                item32.addPregunta(pregunta323);
                item32.addPregunta(pregunta324);
                item32.addPregunta(pregunta325);

                ese3.addItem(item32);

//                      TERCER ITEM
                Item item33 = realm.createObject(Item.class);
                item33.setCriterio(context.getResources().getString(R.string.criterio33));
                item33.setTextoItem(context.getResources().getString(R.string.texto33));
                item33.setIdItem(3);
                item33.setPuntajeItem(0.0);

                Pregunta pregunta331 = realm.createObject(Pregunta.class);
                pregunta331.setIdPregunta(331);
                pregunta331.setPuntaje(null);
                pregunta331.setTextoPregunta(context.getResources().getString(R.string.textoPregunta331));
                pregunta331.setIdItem(33);
                Pregunta pregunta332 = realm.createObject(Pregunta.class);
                pregunta332.setIdPregunta(332);
                pregunta332.setPuntaje(null);
                pregunta332.setTextoPregunta(context.getResources().getString(R.string.textoPregunta332));
                pregunta332.setIdItem(33);
                Pregunta pregunta333 = realm.createObject(Pregunta.class);
                pregunta333.setIdPregunta(333);
                pregunta333.setPuntaje(null);
                pregunta333.setTextoPregunta(context.getResources().getString(R.string.textoPregunta333));
                pregunta333.setIdItem(33);
                Pregunta pregunta334 = realm.createObject(Pregunta.class);
                pregunta334.setIdPregunta(334);
                pregunta334.setPuntaje(null);
                pregunta334.setTextoPregunta(context.getResources().getString(R.string.textoPregunta334));
                pregunta334.setIdItem(33);
                Pregunta pregunta335 = realm.createObject(Pregunta.class);
                pregunta335.setIdPregunta(335);
                pregunta335.setPuntaje(null);
                pregunta335.setTextoPregunta(context.getResources().getString(R.string.textoPregunta335));
                pregunta335.setIdItem(33);
                item33.addPregunta(pregunta331);
                item33.addPregunta(pregunta332);
                item33.addPregunta(pregunta333);
                item33.addPregunta(pregunta334);
                item33.addPregunta(pregunta335);

                ese3.addItem(item33);

//                      CUARTO ITEM
                Item item34 = realm.createObject(Item.class);
                item34.setCriterio(context.getResources().getString(R.string.criterio34));
                item34.setTextoItem(context.getResources().getString(R.string.texto34));
                item34.setIdItem(4);
                item34.setPuntajeItem(0.0);


                Pregunta pregunta341 = realm.createObject(Pregunta.class);
                pregunta341.setIdPregunta(341);
                pregunta341.setPuntaje(null);
                pregunta341.setTextoPregunta(context.getResources().getString(R.string.textoPregunta341));
                pregunta341.setIdItem(34);
                Pregunta pregunta342 = realm.createObject(Pregunta.class);
                pregunta342.setIdPregunta(342);
                pregunta342.setPuntaje(null);
                pregunta342.setTextoPregunta(context.getResources().getString(R.string.textoPregunta342));
                pregunta342.setIdItem(34);
                Pregunta pregunta343 = realm.createObject(Pregunta.class);
                pregunta343.setIdPregunta(343);
                pregunta343.setPuntaje(null);
                pregunta343.setTextoPregunta(context.getResources().getString(R.string.textoPregunta343));
                pregunta343.setIdItem(34);
                Pregunta pregunta344 = realm.createObject(Pregunta.class);
                pregunta344.setIdPregunta(344);
                pregunta344.setPuntaje(null);
                pregunta344.setTextoPregunta(context.getResources().getString(R.string.textoPregunta344));
                pregunta344.setIdItem(34);
                item34.addPregunta(pregunta341);
                item34.addPregunta(pregunta342);
                item34.addPregunta(pregunta343);
                item34.addPregunta(pregunta344);

                ese3.addItem(item34);

//                      QUINTO ITEM
                Item item35 = realm.createObject(Item.class);
                item35.setCriterio(context.getResources().getString(R.string.criterio35));
                item35.setTextoItem(context.getResources().getString(R.string.texto35));
                item35.setIdItem(5);
                item35.setPuntajeItem(0.0);

                Pregunta pregunta351 = realm.createObject(Pregunta.class);
                pregunta351.setIdPregunta(351);
                pregunta351.setPuntaje(null);
                pregunta351.setTextoPregunta(context.getResources().getString(R.string.textoPregunta351));
                pregunta351.setIdItem(35);
                Pregunta pregunta352 = realm.createObject(Pregunta.class);
                pregunta352.setIdPregunta(352);
                pregunta352.setPuntaje(null);
                pregunta352.setTextoPregunta(context.getResources().getString(R.string.textoPregunta352));
                pregunta352.setIdItem(35);
                Pregunta pregunta353 = realm.createObject(Pregunta.class);
                pregunta353.setIdPregunta(353);
                pregunta353.setPuntaje(null);
                pregunta353.setTextoPregunta(context.getResources().getString(R.string.textoPregunta353));
                pregunta353.setIdItem(35);
                Pregunta pregunta354 = realm.createObject(Pregunta.class);
                pregunta354.setIdPregunta(354);
                pregunta354.setPuntaje(null);
                pregunta354.setTextoPregunta(context.getResources().getString(R.string.textoPregunta354));
                pregunta354.setIdItem(35);
                item35.addPregunta(pregunta351);
                item35.addPregunta(pregunta352);
                item35.addPregunta(pregunta353);
                item35.addPregunta(pregunta354);

                ese3.addItem(item35);
                nuevoCuestionario.addEse(ese3);

                //                CUARTA ESE

                Ese ese4= realm.createObject(Ese.class);
                ese4.setIdEse(4);
                ese4.setPuntajeEse(0.0);

//                        PRIMER ITEM
                Item item41 = realm.createObject(Item.class);
                item41.setCriterio(context.getResources().getString(R.string.criterio41));
                item41.setTextoItem(context.getResources().getString(R.string.texto41));
                item41.setIdItem(1);
                item41.setPuntajeItem(0.0);

                Pregunta pregunta411 = realm.createObject(Pregunta.class);
                pregunta411.setIdPregunta(411);
                pregunta411.setPuntaje(null);
                pregunta411.setTextoPregunta(context.getResources().getString(R.string.textoPregunta411));
                pregunta411.setIdItem(41);
                Pregunta pregunta412 = realm.createObject(Pregunta.class);
                pregunta412.setIdPregunta(412);
                pregunta412.setPuntaje(null);
                pregunta412.setTextoPregunta(context.getResources().getString(R.string.textoPregunta412));
                pregunta412.setIdItem(41);
                Pregunta pregunta413 = realm.createObject(Pregunta.class);
                pregunta413.setIdPregunta(413);
                pregunta413.setPuntaje(null);
                pregunta413.setTextoPregunta(context.getResources().getString(R.string.textoPregunta413));
                pregunta413.setIdItem(41);
                Pregunta pregunta414 = realm.createObject(Pregunta.class);
                pregunta414.setIdPregunta(414);
                pregunta414.setPuntaje(null);
                pregunta414.setTextoPregunta(context.getResources().getString(R.string.textoPregunta414));
                pregunta414.setIdItem(41);
                Pregunta pregunta415 = realm.createObject(Pregunta.class);
                pregunta415.setIdPregunta(415);
                pregunta415.setPuntaje(null);
                pregunta415.setTextoPregunta(context.getResources().getString(R.string.textoPregunta415));
                pregunta415.setIdItem(41);

                item41.addPregunta(pregunta411);
                item41.addPregunta(pregunta412);
                item41.addPregunta(pregunta413);
                item41.addPregunta(pregunta414);
                item41.addPregunta(pregunta415);

                ese4.addItem(item41);

//                      SEGUNDO ITEM
                Item item42 = realm.createObject(Item.class);
                item42.setCriterio(context.getResources().getString(R.string.criterio42));
                item42.setTextoItem(context.getResources().getString(R.string.texto42));
                item42.setIdItem(2);
                item42.setPuntajeItem(0.0);

                Pregunta pregunta421 = realm.createObject(Pregunta.class);
                pregunta421.setIdPregunta(421);
                pregunta421.setPuntaje(null);
                pregunta421.setTextoPregunta(context.getResources().getString(R.string.textoPregunta421));
                pregunta421.setIdItem(42);
                Pregunta pregunta422 = realm.createObject(Pregunta.class);
                pregunta422.setIdPregunta(422);
                pregunta422.setPuntaje(null);
                pregunta422.setTextoPregunta(context.getResources().getString(R.string.textoPregunta422));
                pregunta422.setIdItem(42);
                item42.addPregunta(pregunta421);
                item42.addPregunta(pregunta422);

                ese4.addItem(item42);

//                      TERCER ITEM

                Item item43 = realm.createObject(Item.class);
                item43.setCriterio(context.getResources().getString(R.string.criterio43));
                item43.setTextoItem(context.getResources().getString(R.string.texto43));
                item43.setIdItem(3);
                item43.setPuntajeItem(0.0);

                Pregunta pregunta431 = realm.createObject(Pregunta.class);
                pregunta431.setIdPregunta(431);
                pregunta431.setPuntaje(null);
                pregunta431.setTextoPregunta(context.getResources().getString(R.string.textoPregunta431));
                pregunta431.setIdItem(43);
                Pregunta pregunta432 = realm.createObject(Pregunta.class);
                pregunta432.setIdPregunta(432);
                pregunta432.setPuntaje(null);
                pregunta432.setTextoPregunta(context.getResources().getString(R.string.textoPregunta432));
                pregunta432.setIdItem(43);
                Pregunta pregunta433 = realm.createObject(Pregunta.class);
                pregunta433.setIdPregunta(433);
                pregunta433.setPuntaje(null);
                pregunta433.setTextoPregunta(context.getResources().getString(R.string.textoPregunta433));
                pregunta433.setIdItem(43);

                item43.addPregunta(pregunta431);
                item43.addPregunta(pregunta432);
                item43.addPregunta(pregunta433);


                ese4.addItem(item43);


//                      CUARTO ITEM
                Item item44 = realm.createObject(Item.class);
                item44.setCriterio(context.getResources().getString(R.string.criterio44));
                item44.setTextoItem(context.getResources().getString(R.string.texto44));
                item44.setIdItem(4);
                item44.setPuntajeItem(0.0);

                Pregunta pregunta441 = realm.createObject(Pregunta.class);
                pregunta441.setIdPregunta(441);
                pregunta441.setPuntaje(null);
                pregunta441.setTextoPregunta(context.getResources().getString(R.string.textoPregunta441));
                pregunta441.setIdItem(44);
                Pregunta pregunta442 = realm.createObject(Pregunta.class);
                pregunta442.setIdPregunta(442);
                pregunta442.setPuntaje(null);
                pregunta442.setTextoPregunta(context.getResources().getString(R.string.textoPregunta442));
                pregunta442.setIdItem(44);

                item44.addPregunta(pregunta441);
                item44.addPregunta(pregunta442);


                ese4.addItem(item44);

//                      QUINTO ITEM
                Item item45 = realm.createObject(Item.class);
                item45.setCriterio(context.getResources().getString(R.string.criterio45));
                item45.setTextoItem(context.getResources().getString(R.string.texto45));
                item45.setIdItem(5);
                item45.setPuntajeItem(0.0);

                Pregunta pregunta451 = realm.createObject(Pregunta.class);
                pregunta451.setIdPregunta(451);
                pregunta451.setPuntaje(null);
                pregunta451.setTextoPregunta(context.getResources().getString(R.string.textoPregunta451));
                pregunta451.setIdItem(45);
                Pregunta pregunta452 = realm.createObject(Pregunta.class);
                pregunta452.setIdPregunta(452);
                pregunta452.setPuntaje(null);
                pregunta452.setTextoPregunta(context.getResources().getString(R.string.textoPregunta452));
                pregunta452.setIdItem(45);
                Pregunta pregunta453 = realm.createObject(Pregunta.class);
                pregunta453.setIdPregunta(453);
                pregunta453.setPuntaje(null);
                pregunta453.setTextoPregunta(context.getResources().getString(R.string.textoPregunta453));
                pregunta453.setIdItem(45);
                Pregunta pregunta454 = realm.createObject(Pregunta.class);
                pregunta454.setPuntaje(null);
                pregunta454.setTextoPregunta(context.getResources().getString(R.string.textoPregunta454));
                pregunta454.setIdItem(45);
                Pregunta pregunta455 = realm.createObject(Pregunta.class);
                pregunta455.setIdPregunta(455);
                pregunta455.setPuntaje(null);
                pregunta455.setTextoPregunta(context.getResources().getString(R.string.textoPregunta455));
                pregunta455.setIdItem(45);
                item45.addPregunta(pregunta451);
                item45.addPregunta(pregunta452);
                item45.addPregunta(pregunta453);
                item45.addPregunta(pregunta454);
                item45.addPregunta(pregunta455);
                ese4.addItem(item45);

//                      SEXTO ITEM
                Item item46 = realm.createObject(Item.class);
                item46.setCriterio(context.getResources().getString(R.string.criterio46));
                item46.setTextoItem(context.getResources().getString(R.string.texto46));
                item46.setIdItem(6);
                item46.setPuntajeItem(0.0);

                Pregunta pregunta461 = realm.createObject(Pregunta.class);
                pregunta461.setIdPregunta(461);
                pregunta461.setPuntaje(null);
                pregunta461.setTextoPregunta(context.getResources().getString(R.string.textoPregunta461));
                pregunta461.setIdItem(46);
                Pregunta pregunta462 = realm.createObject(Pregunta.class);
                pregunta462.setIdPregunta(462);
                pregunta462.setPuntaje(null);
                pregunta462.setTextoPregunta(context.getResources().getString(R.string.textoPregunta462));
                pregunta462.setIdItem(46);

                item46.addPregunta(pregunta461);
                item46.addPregunta(pregunta462);

                ese4.addItem(item46);
                nuevoCuestionario.addEse(ese4);


                //QUINTA ESE
                Ese ese5= realm.createObject(Ese.class);
                ese5.setIdEse(5);
                ese5.setPuntajeEse(0.0);
//                        PRIMER ITEM
                Item item51 = realm.createObject(Item.class);
                item51.setCriterio(context.getResources().getString(R.string.criterio51));
                item51.setTextoItem(context.getResources().getString(R.string.texto51));
                item51.setIdItem(1);
                item51.setPuntajeItem(0.0);

                Pregunta pregunta511 = realm.createObject(Pregunta.class);
                pregunta511.setIdPregunta(511);
                pregunta511.setPuntaje(null);
                pregunta511.setTextoPregunta(context.getResources().getString(R.string.textoPregunta511));
                pregunta511.setIdItem(51);
                Pregunta pregunta512 = realm.createObject(Pregunta.class);
                pregunta512.setIdPregunta(512);
                pregunta512.setPuntaje(null);
                pregunta512.setTextoPregunta(context.getResources().getString(R.string.textoPregunta512));
                pregunta512.setIdItem(51);
                Pregunta pregunta513 = realm.createObject(Pregunta.class);
                pregunta513.setIdPregunta(513);
                pregunta513.setPuntaje(null);
                pregunta513.setTextoPregunta(context.getResources().getString(R.string.textoPregunta513));
                pregunta513.setIdItem(51);
                Pregunta pregunta514 = realm.createObject(Pregunta.class);
                pregunta514.setIdPregunta(514);
                pregunta514.setPuntaje(null);
                pregunta514.setTextoPregunta(context.getResources().getString(R.string.textoPregunta514));
                pregunta514.setIdItem(51);
                Pregunta pregunta515 = realm.createObject(Pregunta.class);
                pregunta515.setIdPregunta(515);
                pregunta515.setPuntaje(null);
                pregunta515.setTextoPregunta(context.getResources().getString(R.string.textoPregunta515));
                pregunta515.setIdItem(51);
                Pregunta pregunta516 = realm.createObject(Pregunta.class);
                pregunta516.setIdPregunta(516);
                pregunta516.setPuntaje(null);
                pregunta516.setTextoPregunta(context.getResources().getString(R.string.textoPregunta516));
                pregunta516.setIdItem(51);
                Pregunta pregunta517 = realm.createObject(Pregunta.class);
                pregunta517.setIdPregunta(517);
                pregunta517.setPuntaje(null);
                pregunta517.setTextoPregunta(context.getResources().getString(R.string.textoPregunta517));
                pregunta517.setIdItem(51);
                item51.addPregunta(pregunta511);
                item51.addPregunta(pregunta512);
                item51.addPregunta(pregunta513);
                item51.addPregunta(pregunta514);
                item51.addPregunta(pregunta515);
                item51.addPregunta(pregunta516);
                item51.addPregunta(pregunta517);

                ese5.addItem(item51);

//                      SEGUNDO ITEM
                Item item52 = realm.createObject(Item.class);
                item52.setCriterio(context.getResources().getString(R.string.criterio52));
                item52.setTextoItem(context.getResources().getString(R.string.texto52));
                item52.setIdItem(2);
                item52.setPuntajeItem(0.0);

                Pregunta pregunta521 = realm.createObject(Pregunta.class);
                pregunta521.setIdPregunta(521);
                pregunta521.setPuntaje(null);
                pregunta521.setTextoPregunta(context.getResources().getString(R.string.textoPregunta521));
                pregunta521.setIdItem(52);
                Pregunta pregunta522 = realm.createObject(Pregunta.class);
                pregunta522.setIdPregunta(522);
                pregunta522.setPuntaje(null);
                pregunta522.setTextoPregunta(context.getResources().getString(R.string.textoPregunta522));
                pregunta522.setIdItem(52);
                item52.addPregunta(pregunta521);
                item52.addPregunta(pregunta522);

                ese5.addItem(item52);

//                      TERCER ITEM

                Item item53 = realm.createObject(Item.class);
                item53.setCriterio(context.getResources().getString(R.string.criterio53));
                item53.setTextoItem(context.getResources().getString(R.string.texto53));
                item53.setIdItem(3);
                item53.setPuntajeItem(0.0);

                Pregunta pregunta531 = realm.createObject(Pregunta.class);
                pregunta531.setIdPregunta(531);
                pregunta531.setPuntaje(null);
                pregunta531.setTextoPregunta(context.getResources().getString(R.string.textoPregunta531));
                pregunta531.setIdItem(53);
                Pregunta pregunta532 = realm.createObject(Pregunta.class);
                pregunta532.setIdPregunta(532);
                pregunta532.setPuntaje(null);
                pregunta532.setTextoPregunta(context.getResources().getString(R.string.textoPregunta532));
                pregunta532.setIdItem(53);
                item53.addPregunta(pregunta531);
                item53.addPregunta(pregunta532);

                ese5.addItem(item53);

//                      CUARTO ITEM
                Item item54 = realm.createObject(Item.class);
                item54.setCriterio(context.getResources().getString(R.string.criterio54));
                item54.setTextoItem(context.getResources().getString(R.string.texto54));
                item54.setIdItem(4);
                item54.setPuntajeItem(0.0);

                Pregunta pregunta541 = realm.createObject(Pregunta.class);
                pregunta541.setIdPregunta(541);
                pregunta541.setPuntaje(null);
                pregunta541.setTextoPregunta(context.getResources().getString(R.string.textoPregunta541));
                pregunta541.setIdItem(54);
                Pregunta pregunta542 = realm.createObject(Pregunta.class);
                pregunta542.setIdPregunta(542);
                pregunta542.setPuntaje(null);
                pregunta542.setTextoPregunta(context.getResources().getString(R.string.textoPregunta542));
                pregunta542.setIdItem(54);
                Pregunta pregunta543 = realm.createObject(Pregunta.class);
                pregunta543.setIdPregunta(543);
                pregunta543.setPuntaje(null);
                pregunta543.setTextoPregunta(context.getResources().getString(R.string.textoPregunta543));
                pregunta543.setIdItem(54);
                Pregunta pregunta544 = realm.createObject(Pregunta.class);
                pregunta544.setIdPregunta(544);
                pregunta544.setPuntaje(null);
                pregunta544.setTextoPregunta(context.getResources().getString(R.string.textoPregunta544));
                pregunta544.setIdItem(54);
                Pregunta pregunta545 = realm.createObject(Pregunta.class);
                pregunta545.setIdPregunta(545);
                pregunta545.setPuntaje(null);
                pregunta545.setTextoPregunta(context.getResources().getString(R.string.textoPregunta545));
                pregunta545.setIdItem(54);
                item54.addPregunta(pregunta541);
                item54.addPregunta(pregunta542);
                item54.addPregunta(pregunta543);
                item54.addPregunta(pregunta544);
                item54.addPregunta(pregunta545);

                ese5.addItem(item54);

//                      QUINTO ITEM
                Item item55 = realm.createObject(Item.class);
                item55.setCriterio(context.getResources().getString(R.string.criterio55));
                item55.setTextoItem(context.getResources().getString(R.string.texto55));
                item55.setIdItem(5);
                item55.setPuntajeItem(0.0);

                Pregunta pregunta551 = realm.createObject(Pregunta.class);
                pregunta551.setIdPregunta(551);
                pregunta551.setPuntaje(null);
                pregunta551.setTextoPregunta(context.getResources().getString(R.string.textoPregunta551));
                pregunta551.setIdItem(55);
                Pregunta pregunta552 = realm.createObject(Pregunta.class);
                pregunta552.setIdPregunta(552);
                pregunta552.setPuntaje(null);
                pregunta552.setTextoPregunta(context.getResources().getString(R.string.textoPregunta552));
                pregunta552.setIdItem(55);
                item55.addPregunta(pregunta551);
                item55.addPregunta(pregunta552);

                ese5.addItem(item55);
                nuevoCuestionario.addEse(ese5);

                for (Ese unaEses :
                        nuevoCuestionario.getListaEses()) {
                    unaEses.setIdCuestionario(nuevoCuestionario.getIdCuestionario());
                    for (Item unItem :
                            unaEses.getListaItem()) {
                        unItem.setIdCuestionario(nuevoCuestionario.getIdCuestionario());
                        unItem.setIdEse(unaEses.getIdEse());
                        for (Pregunta unaPregunta :
                                unItem.getListaPreguntas()) {
                            unaPregunta.setIdCuestioniario(nuevoCuestionario.getIdCuestionario());
                            unaPregunta.setIdItem(unItem.getIdItem());
                            unaPregunta.setIdEse(unaEses.getIdEse());
                        }
                    }
                }

            }
        });
        //endregion


        //region CREAR CUESTIONARIO B
        nBgRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Cuestionario nuevoCuestionario = realm.createObject(Cuestionario.class,"2");

                nuevoCuestionario.setNombreCuestionario(context.getString(R.string.areaOficina));


                Ese ese1= realm.createObject(Ese.class);
                ese1.setIdEse(1);
                ese1.setPuntajeEse(0.0);

//                        PRIMER ITEM
                Item item11 = realm.createObject(Item.class);
                item11.setCriterio(context.getResources().getString(R.string.criterio11B));
                item11.setTextoItem(context.getResources().getString(R.string.texto11B));
                item11.setIdItem(1);
                item11.setPuntajeItem(0.0);

                Pregunta pregunta111 = realm.createObject(Pregunta.class);
                pregunta111.setIdPregunta(111);
                pregunta111.setPuntaje(null);
                pregunta111.setTextoPregunta(context.getResources().getString(R.string.textoPregunta111B));
                pregunta111.setIdItem(11);
                Pregunta pregunta112 = realm.createObject(Pregunta.class);
                pregunta112.setIdPregunta(112);
                pregunta112.setPuntaje(null);
                pregunta112.setTextoPregunta(context.getResources().getString(R.string.textoPregunta112B));
                pregunta112.setIdItem(11);
                Pregunta pregunta113 = realm.createObject(Pregunta.class);
                pregunta113.setIdPregunta(113);
                pregunta113.setPuntaje(null);
                pregunta113.setTextoPregunta(context.getResources().getString(R.string.textoPregunta113B));
                pregunta113.setIdItem(11);
                Pregunta pregunta114 = realm.createObject(Pregunta.class);
                pregunta114.setIdPregunta(114);
                pregunta114.setPuntaje(null);
                pregunta114.setTextoPregunta(context.getResources().getString(R.string.textoPregunta114B));
                pregunta114.setIdItem(11);

                item11.addPregunta(pregunta111);
                item11.addPregunta(pregunta112);
                item11.addPregunta(pregunta113);
                item11.addPregunta(pregunta114);
                ese1.addItem(item11);

//                      SEGUNDO ITEM
                Item item12 = realm.createObject(Item.class);
                item12.setCriterio(context.getResources().getString(R.string.criterio12B));
                item12.setTextoItem(context.getResources().getString(R.string.texto12B));
                item12.setIdItem(2);
                item12.setPuntajeItem(0.0);

                Pregunta pregunta121 = realm.createObject(Pregunta.class);
                pregunta121.setIdPregunta(121);
                pregunta121.setPuntaje(null);
                pregunta121.setTextoPregunta(context.getResources().getString(R.string.textoPregunta121B));
                pregunta121.setIdItem(12);
                Pregunta pregunta122 = realm.createObject(Pregunta.class);
                pregunta122.setIdPregunta(122);
                pregunta122.setPuntaje(null);
                pregunta122.setTextoPregunta(context.getResources().getString(R.string.textoPregunta122B));
                pregunta122.setIdItem(12);
                Pregunta pregunta123 = realm.createObject(Pregunta.class);
                pregunta123.setIdPregunta(123);
                pregunta123.setPuntaje(null);
                pregunta123.setTextoPregunta(context.getResources().getString(R.string.textoPregunta123B));
                pregunta123.setIdItem(12);
                Pregunta pregunta124 = realm.createObject(Pregunta.class);
                pregunta124.setIdPregunta(124);
                pregunta124.setPuntaje(null);
                pregunta124.setTextoPregunta(context.getResources().getString(R.string.textoPregunta124B));
                pregunta124.setIdItem(12);
                item12.addPregunta(pregunta121);
                item12.addPregunta(pregunta122);
                item12.addPregunta(pregunta123);
                item12.addPregunta(pregunta124);

                ese1.addItem(item12);

//                      TERCER ITEM
                Item item13 = realm.createObject(Item.class);
                item13.setCriterio(context.getResources().getString(R.string.criterio13B));
                item13.setTextoItem(context.getResources().getString(R.string.texto13B));
                item13.setIdItem(3);
                item13.setPuntajeItem(0.0);

                Pregunta pregunta131 = realm.createObject(Pregunta.class);
                pregunta131.setIdPregunta(131);
                pregunta131.setPuntaje(null);
                pregunta131.setTextoPregunta(context.getResources().getString(R.string.textoPregunta131B));
                pregunta131.setIdItem(13);
                Pregunta pregunta132 = realm.createObject(Pregunta.class);
                pregunta132.setIdPregunta(132);
                pregunta132.setPuntaje(null);
                pregunta132.setTextoPregunta(context.getResources().getString(R.string.textoPregunta132B));
                pregunta132.setIdItem(13);
                item13.addPregunta(pregunta131);
                item13.addPregunta(pregunta132);

                ese1.addItem(item13);

//                      CUARTO ITEM
                Item item14 = realm.createObject(Item.class);
                item14.setCriterio(context.getResources().getString(R.string.criterio14B));
                item14.setTextoItem(context.getResources().getString(R.string.texto14B));
                item14.setIdItem(4);
                item14.setPuntajeItem(0.0);

                Pregunta pregunta141 = realm.createObject(Pregunta.class);
                pregunta141.setIdPregunta(141);
                pregunta141.setPuntaje(null);
                pregunta141.setTextoPregunta(context.getResources().getString(R.string.textoPregunta141B));
                pregunta141.setIdItem(14);
                Pregunta pregunta142 = realm.createObject(Pregunta.class);
                pregunta142.setIdPregunta(142);
                pregunta142.setPuntaje(null);
                pregunta142.setTextoPregunta(context.getResources().getString(R.string.textoPregunta142B));
                pregunta142.setIdItem(14);
                Pregunta pregunta143 = realm.createObject(Pregunta.class);
                pregunta143.setIdPregunta(143);
                pregunta143.setPuntaje(null);
                pregunta143.setTextoPregunta(context.getResources().getString(R.string.textoPregunta143B));
                pregunta143.setIdItem(14);
                Pregunta pregunta144 = realm.createObject(Pregunta.class);
                pregunta144.setIdPregunta(144);
                pregunta144.setPuntaje(null);
                pregunta144.setTextoPregunta(context.getResources().getString(R.string.textoPregunta144B));
                pregunta144.setIdItem(14);
                item14.addPregunta(pregunta141);
                item14.addPregunta(pregunta142);
                item14.addPregunta(pregunta143);
                item14.addPregunta(pregunta144);

                ese1.addItem(item14);

//                      QUINTO ITEM
                Item item15 = realm.createObject(Item.class);
                item15.setCriterio(context.getResources().getString(R.string.criterio15B));
                item15.setTextoItem(context.getResources().getString(R.string.texto15B));
                item15.setIdItem(5);
                item15.setPuntajeItem(0.0);

                Pregunta pregunta151 = realm.createObject(Pregunta.class);
                pregunta151.setIdPregunta(151);
                pregunta151.setPuntaje(null);
                pregunta151.setTextoPregunta(context.getResources().getString(R.string.textoPregunta151B));
                pregunta151.setIdItem(15);
                Pregunta pregunta152 = realm.createObject(Pregunta.class);
                pregunta152.setIdPregunta(152);
                pregunta152.setPuntaje(null);
                pregunta152.setTextoPregunta(context.getResources().getString(R.string.textoPregunta152B));
                pregunta152.setIdItem(15);
                item15.addPregunta(pregunta151);
                item15.addPregunta(pregunta152);

                ese1.addItem(item15);
                nuevoCuestionario.addEse(ese1);

//                SEGUNDA ESE

                Ese ese2= realm.createObject(Ese.class);
                ese2.setIdEse(2);
                ese2.setPuntajeEse(0.0);

//                        PRIMER ITEM
                Item item21 = realm.createObject(Item.class);
                item21.setCriterio(context.getResources().getString(R.string.criterio21B));
                item21.setTextoItem(context.getResources().getString(R.string.texto21B));
                item21.setIdItem(1);
                item21.setPuntajeItem(0.0);

                Pregunta pregunta211 = realm.createObject(Pregunta.class);
                pregunta211.setIdPregunta(211);
                pregunta211.setPuntaje(null);
                pregunta211.setTextoPregunta(context.getResources().getString(R.string.textoPregunta211B));
                pregunta211.setIdItem(21);
                Pregunta pregunta212 = realm.createObject(Pregunta.class);
                pregunta212.setIdPregunta(212);
                pregunta212.setPuntaje(null);
                pregunta212.setTextoPregunta(context.getResources().getString(R.string.textoPregunta212B));
                pregunta212.setIdItem(21);
                Pregunta pregunta213 = realm.createObject(Pregunta.class);
                pregunta213.setIdPregunta(213);
                pregunta213.setPuntaje(null);
                pregunta213.setTextoPregunta(context.getResources().getString(R.string.textoPregunta213B));
                pregunta213.setIdItem(21);

                item21.addPregunta(pregunta211);
                item21.addPregunta(pregunta212);
                item21.addPregunta(pregunta213);


                ese2.addItem(item21);

//                      SEGUNDO ITEM
                Item item22 = realm.createObject(Item.class);
                item22.setCriterio(context.getResources().getString(R.string.criterio22B));
                item22.setTextoItem(context.getResources().getString(R.string.texto22B));
                item22.setIdItem(2);
                item22.setPuntajeItem(0.0);

                Pregunta pregunta221 = realm.createObject(Pregunta.class);
                pregunta221.setIdPregunta(221);
                pregunta221.setPuntaje(null);
                pregunta221.setTextoPregunta(context.getResources().getString(R.string.textoPregunta221B));
                pregunta221.setIdItem(22);
                Pregunta pregunta222 = realm.createObject(Pregunta.class);
                pregunta222.setIdPregunta(222);
                pregunta222.setPuntaje(null);
                pregunta222.setTextoPregunta(context.getResources().getString(R.string.textoPregunta222B));
                pregunta222.setIdItem(22);
                Pregunta pregunta223 = realm.createObject(Pregunta.class);
                pregunta223.setIdPregunta(223);
                pregunta223.setPuntaje(null);
                pregunta223.setTextoPregunta(context.getResources().getString(R.string.textoPregunta223B));
                pregunta223.setIdItem(22);

                item22.addPregunta(pregunta221);
                item22.addPregunta(pregunta222);
                item22.addPregunta(pregunta223);


                ese2.addItem(item22);

//                      TERCER ITEM
                Item item23 = realm.createObject(Item.class);
                item23.setCriterio(context.getResources().getString(R.string.criterio23B));
                item23.setTextoItem(context.getResources().getString(R.string.texto23B));
                item23.setIdItem(3);
                item23.setPuntajeItem(0.0);

                Pregunta pregunta231 = realm.createObject(Pregunta.class);
                pregunta231.setIdPregunta(231);
                pregunta231.setPuntaje(null);
                pregunta231.setTextoPregunta(context.getResources().getString(R.string.textoPregunta231B));
                pregunta231.setIdItem(23);
                Pregunta pregunta232 = realm.createObject(Pregunta.class);
                pregunta232.setIdPregunta(232);
                pregunta232.setPuntaje(null);
                pregunta232.setTextoPregunta(context.getResources().getString(R.string.textoPregunta232B));
                pregunta232.setIdItem(23);
                Pregunta pregunta233 = realm.createObject(Pregunta.class);
                pregunta233.setIdPregunta(233);
                pregunta233.setPuntaje(null);
                pregunta233.setTextoPregunta(context.getResources().getString(R.string.textoPregunta233B));
                pregunta233.setIdItem(23);

                item23.addPregunta(pregunta231);
                item23.addPregunta(pregunta232);
                item23.addPregunta(pregunta233);

                ese2.addItem(item23);

//                      CUARTO ITEM
                Item item24 = realm.createObject(Item.class);
                item24.setCriterio(context.getResources().getString(R.string.criterio24B));
                item24.setTextoItem(context.getResources().getString(R.string.texto24B));
                item24.setIdItem(4);
                item24.setPuntajeItem(0.0);
                Pregunta pregunta241 = realm.createObject(Pregunta.class);
                pregunta241.setIdPregunta(241);
                pregunta241.setPuntaje(null);
                pregunta241.setTextoPregunta(context.getResources().getString(R.string.textoPregunta241B));
                pregunta241.setIdItem(24);
                Pregunta pregunta242 = realm.createObject(Pregunta.class);
                pregunta242.setIdPregunta(242);
                pregunta242.setPuntaje(null);
                pregunta242.setTextoPregunta(context.getResources().getString(R.string.textoPregunta242B));
                pregunta242.setIdItem(24);
                Pregunta pregunta243 = realm.createObject(Pregunta.class);
                pregunta243.setIdPregunta(243);
                pregunta243.setPuntaje(null);
                pregunta243.setTextoPregunta(context.getResources().getString(R.string.textoPregunta243B));
                pregunta243.setIdItem(24);

                item24.addPregunta(pregunta241);
                item24.addPregunta(pregunta242);
                item24.addPregunta(pregunta243);


                ese2.addItem(item24);

//                      QUINTO ITEM
                Item item25 = realm.createObject(Item.class);
                item25.setCriterio(context.getResources().getString(R.string.criterio25B));
                item25.setTextoItem(context.getResources().getString(R.string.texto25B));
                item25.setIdItem(5);
                item25.setPuntajeItem(0.0);

                Pregunta pregunta251 = realm.createObject(Pregunta.class);
                pregunta251.setIdPregunta(251);
                pregunta251.setPuntaje(null);
                pregunta251.setTextoPregunta(context.getResources().getString(R.string.textoPregunta251B));
                pregunta251.setIdItem(25);
                Pregunta pregunta252 = realm.createObject(Pregunta.class);
                pregunta252.setIdPregunta(252);
                pregunta252.setPuntaje(null);
                pregunta252.setTextoPregunta(context.getResources().getString(R.string.textoPregunta252B));
                pregunta252.setIdItem(25);
                Pregunta pregunta253 = realm.createObject(Pregunta.class);
                pregunta253.setIdPregunta(253);
                pregunta253.setPuntaje(null);
                pregunta253.setTextoPregunta(context.getResources().getString(R.string.textoPregunta253B));
                pregunta253.setIdItem(25);

                item25.addPregunta(pregunta251);
                item25.addPregunta(pregunta252);
                item25.addPregunta(pregunta253);


                ese2.addItem(item25);
                nuevoCuestionario.addEse(ese2);

//                TERCERA ESE

                Ese ese3= realm.createObject(Ese.class);
                ese3.setIdEse(3);
                ese3.setPuntajeEse(0.0);

//                        PRIMER ITEM
                Item item31 = realm.createObject(Item.class);
                item31.setCriterio(context.getResources().getString(R.string.criterio31B));
                item31.setTextoItem(context.getResources().getString(R.string.texto31B));
                item31.setIdItem(1);
                item31.setPuntajeItem(0.0);

                Pregunta pregunta311 = realm.createObject(Pregunta.class);
                pregunta311.setIdPregunta(311);
                pregunta311.setPuntaje(null);
                pregunta311.setTextoPregunta(context.getResources().getString(R.string.textoPregunta311B));
                pregunta311.setIdItem(31);
                Pregunta pregunta312 = realm.createObject(Pregunta.class);
                pregunta312.setIdPregunta(312);
                pregunta312.setPuntaje(null);
                pregunta312.setTextoPregunta(context.getResources().getString(R.string.textoPregunta312B));
                pregunta312.setIdItem(31);
                Pregunta pregunta313 = realm.createObject(Pregunta.class);
                pregunta313.setIdPregunta(313);
                pregunta313.setPuntaje(null);
                pregunta313.setTextoPregunta(context.getResources().getString(R.string.textoPregunta313B));
                pregunta313.setIdItem(31);
                item31.addPregunta(pregunta311);
                item31.addPregunta(pregunta312);
                item31.addPregunta(pregunta313);

                ese3.addItem(item31);

//                      SEGUNDO ITEM
                Item item32 = realm.createObject(Item.class);
                item32.setCriterio(context.getResources().getString(R.string.criterio32B));
                item32.setTextoItem(context.getResources().getString(R.string.texto32B));
                item32.setIdItem(2);
                item32.setPuntajeItem(0.0);

                Pregunta pregunta321 = realm.createObject(Pregunta.class);
                pregunta321.setIdPregunta(321);
                pregunta321.setPuntaje(null);
                pregunta321.setTextoPregunta(context.getResources().getString(R.string.textoPregunta321B));
                pregunta321.setIdItem(32);
                Pregunta pregunta322 = realm.createObject(Pregunta.class);
                pregunta322.setIdPregunta(322);
                pregunta322.setPuntaje(null);
                pregunta322.setTextoPregunta(context.getResources().getString(R.string.textoPregunta322B));
                pregunta322.setIdItem(32);
                Pregunta pregunta323 = realm.createObject(Pregunta.class);
                pregunta323.setIdPregunta(323);
                pregunta323.setPuntaje(null);
                pregunta323.setTextoPregunta(context.getResources().getString(R.string.textoPregunta323B));
                pregunta323.setIdItem(32);

                item32.addPregunta(pregunta321);
                item32.addPregunta(pregunta322);
                item32.addPregunta(pregunta323);


                ese3.addItem(item32);

//                      TERCER ITEM
                Item item33 = realm.createObject(Item.class);
                item33.setCriterio(context.getResources().getString(R.string.criterio33B));
                item33.setTextoItem(context.getResources().getString(R.string.texto33B));
                item33.setIdItem(3);
                item33.setPuntajeItem(0.0);

                Pregunta pregunta331 = realm.createObject(Pregunta.class);
                pregunta331.setIdPregunta(331);
                pregunta331.setPuntaje(null);
                pregunta331.setTextoPregunta(context.getResources().getString(R.string.textoPregunta331B));
                pregunta331.setIdItem(33);
                Pregunta pregunta332 = realm.createObject(Pregunta.class);
                pregunta332.setIdPregunta(332);
                pregunta332.setPuntaje(null);
                pregunta332.setTextoPregunta(context.getResources().getString(R.string.textoPregunta332B));
                pregunta332.setIdItem(33);
                Pregunta pregunta333 = realm.createObject(Pregunta.class);
                pregunta333.setIdPregunta(333);
                pregunta333.setPuntaje(null);
                pregunta333.setTextoPregunta(context.getResources().getString(R.string.textoPregunta333B));
                pregunta333.setIdItem(33);

                item33.addPregunta(pregunta331);
                item33.addPregunta(pregunta332);
                item33.addPregunta(pregunta333);


                ese3.addItem(item33);

//                      CUARTO ITEM
                Item item34 = realm.createObject(Item.class);
                item34.setCriterio(context.getResources().getString(R.string.criterio34B));
                item34.setTextoItem(context.getResources().getString(R.string.texto34B));
                item34.setIdItem(4);
                item34.setPuntajeItem(0.0);

                Pregunta pregunta341 = realm.createObject(Pregunta.class);
                pregunta341.setIdPregunta(341);
                pregunta341.setPuntaje(null);
                pregunta341.setTextoPregunta(context.getResources().getString(R.string.textoPregunta341B));
                pregunta341.setIdItem(34);
                Pregunta pregunta342 = realm.createObject(Pregunta.class);
                pregunta342.setIdPregunta(342);
                pregunta342.setPuntaje(null);
                pregunta342.setTextoPregunta(context.getResources().getString(R.string.textoPregunta342B));
                pregunta342.setIdItem(34);
                Pregunta pregunta343 = realm.createObject(Pregunta.class);
                pregunta343.setIdPregunta(343);
                pregunta343.setPuntaje(null);
                pregunta343.setTextoPregunta(context.getResources().getString(R.string.textoPregunta343B));
                pregunta343.setIdItem(34);
                Pregunta pregunta344 = realm.createObject(Pregunta.class);
                pregunta344.setIdPregunta(344);
                pregunta344.setPuntaje(null);
                pregunta344.setTextoPregunta(context.getResources().getString(R.string.textoPregunta344B));
                pregunta344.setIdItem(34);
                item34.addPregunta(pregunta341);
                item34.addPregunta(pregunta342);
                item34.addPregunta(pregunta343);
                item34.addPregunta(pregunta344);

                ese3.addItem(item34);

//                      QUINTO ITEM
                Item item35 = realm.createObject(Item.class);
                item35.setCriterio(context.getResources().getString(R.string.criterio35B));
                item35.setTextoItem(context.getResources().getString(R.string.texto35B));
                item35.setIdItem(5);
                item35.setPuntajeItem(0.0);

                Pregunta pregunta351 = realm.createObject(Pregunta.class);
                pregunta351.setIdPregunta(351);
                pregunta351.setPuntaje(null);
                pregunta351.setTextoPregunta(context.getResources().getString(R.string.textoPregunta351B));
                pregunta351.setIdItem(35);

                item35.addPregunta(pregunta351);

                ese3.addItem(item35);
                nuevoCuestionario.addEse(ese3);

                //                CUARTA ESE

                Ese ese4= realm.createObject(Ese.class);
                ese4.setIdEse(4);
                ese4.setPuntajeEse(0.0);

//                        PRIMER ITEM
                Item item41 = realm.createObject(Item.class);
                item41.setCriterio(context.getResources().getString(R.string.criterio41B));
                item41.setTextoItem(context.getResources().getString(R.string.texto41B));
                item41.setIdItem(1);
                item41.setPuntajeItem(0.0);

                Pregunta pregunta411 = realm.createObject(Pregunta.class);
                pregunta411.setIdPregunta(411);
                pregunta411.setPuntaje(null);
                pregunta411.setTextoPregunta(context.getResources().getString(R.string.textoPregunta411B));
                pregunta411.setIdItem(41);
                Pregunta pregunta412 = realm.createObject(Pregunta.class);
                pregunta412.setIdPregunta(412);
                pregunta412.setPuntaje(null);
                pregunta412.setTextoPregunta(context.getResources().getString(R.string.textoPregunta412B));
                pregunta412.setIdItem(41);
                Pregunta pregunta413 = realm.createObject(Pregunta.class);
                pregunta413.setIdPregunta(413);
                pregunta413.setPuntaje(null);
                pregunta413.setTextoPregunta(context.getResources().getString(R.string.textoPregunta413B));
                pregunta413.setIdItem(41);
                Pregunta pregunta414 = realm.createObject(Pregunta.class);
                pregunta414.setIdPregunta(414);
                pregunta414.setPuntaje(null);
                pregunta414.setTextoPregunta(context.getResources().getString(R.string.textoPregunta414B));
                pregunta414.setIdItem(41);


                item41.addPregunta(pregunta411);
                item41.addPregunta(pregunta412);
                item41.addPregunta(pregunta413);
                item41.addPregunta(pregunta414);


                ese4.addItem(item41);

//                      SEGUNDO ITEM
                Item item42 = realm.createObject(Item.class);
                item42.setCriterio(context.getResources().getString(R.string.criterio42B));
                item42.setTextoItem(context.getResources().getString(R.string.texto42B));
                item42.setIdItem(2);
                item42.setPuntajeItem(0.0);

                Pregunta pregunta421 = realm.createObject(Pregunta.class);
                pregunta421.setIdPregunta(421);
                pregunta421.setPuntaje(null);
                pregunta421.setTextoPregunta(context.getResources().getString(R.string.textoPregunta421B));
                pregunta421.setIdItem(42);
                Pregunta pregunta422 = realm.createObject(Pregunta.class);
                pregunta422.setIdPregunta(422);
                pregunta422.setPuntaje(null);
                pregunta422.setTextoPregunta(context.getResources().getString(R.string.textoPregunta422B));
                pregunta422.setIdItem(42);
                item42.addPregunta(pregunta421);
                item42.addPregunta(pregunta422);

                ese4.addItem(item42);

//                      TERCER ITEM

                Item item43 = realm.createObject(Item.class);
                item43.setCriterio(context.getResources().getString(R.string.criterio43B));
                item43.setTextoItem(context.getResources().getString(R.string.texto43B));
                item43.setIdItem(3);
                item43.setPuntajeItem(0.0);

                Pregunta pregunta431 = realm.createObject(Pregunta.class);
                pregunta431.setIdPregunta(431);
                pregunta431.setPuntaje(null);
                pregunta431.setTextoPregunta(context.getResources().getString(R.string.textoPregunta431B));
                pregunta431.setIdItem(43);
                Pregunta pregunta432 = realm.createObject(Pregunta.class);
                pregunta432.setIdPregunta(432);
                pregunta432.setPuntaje(null);
                pregunta432.setTextoPregunta(context.getResources().getString(R.string.textoPregunta432B));
                pregunta432.setIdItem(43);
                Pregunta pregunta433 = realm.createObject(Pregunta.class);
                pregunta433.setIdPregunta(433);
                pregunta433.setPuntaje(null);
                pregunta433.setTextoPregunta(context.getResources().getString(R.string.textoPregunta433B));
                pregunta433.setIdItem(43);

                item43.addPregunta(pregunta431);
                item43.addPregunta(pregunta432);
                item43.addPregunta(pregunta433);


                ese4.addItem(item43);


//                      CUARTO ITEM
                Item item44 = realm.createObject(Item.class);
                item44.setCriterio(context.getResources().getString(R.string.criterio44B));
                item44.setTextoItem(context.getResources().getString(R.string.texto44B));
                item44.setIdItem(4);
                item44.setPuntajeItem(0.0);

                Pregunta pregunta441 = realm.createObject(Pregunta.class);
                pregunta441.setIdPregunta(441);
                pregunta441.setPuntaje(null);
                pregunta441.setTextoPregunta(context.getResources().getString(R.string.textoPregunta441B));
                pregunta441.setIdItem(44);
                Pregunta pregunta442 = realm.createObject(Pregunta.class);
                pregunta442.setIdPregunta(442);
                pregunta442.setPuntaje(null);
                pregunta442.setTextoPregunta(context.getResources().getString(R.string.textoPregunta442B));
                pregunta442.setIdItem(44);

                item44.addPregunta(pregunta441);
                item44.addPregunta(pregunta442);


                ese4.addItem(item44);

//                      QUINTO ITEM
                Item item45 = realm.createObject(Item.class);
                item45.setCriterio(context.getResources().getString(R.string.criterio45B));
                item45.setTextoItem(context.getResources().getString(R.string.texto45B));
                item45.setIdItem(5);
                item45.setPuntajeItem(0.0);

                Pregunta pregunta451 = realm.createObject(Pregunta.class);
                pregunta451.setIdPregunta(451);
                pregunta451.setPuntaje(null);
                pregunta451.setTextoPregunta(context.getResources().getString(R.string.textoPregunta451B));
                pregunta451.setIdItem(45);
                Pregunta pregunta452 = realm.createObject(Pregunta.class);
                pregunta452.setIdPregunta(452);
                pregunta452.setPuntaje(null);
                pregunta452.setTextoPregunta(context.getResources().getString(R.string.textoPregunta452B));
                pregunta452.setIdItem(45);
                Pregunta pregunta453 = realm.createObject(Pregunta.class);
                pregunta453.setIdPregunta(453);
                pregunta453.setPuntaje(null);
                pregunta453.setTextoPregunta(context.getResources().getString(R.string.textoPregunta453B));
                pregunta453.setIdItem(45);
                Pregunta pregunta454 = realm.createObject(Pregunta.class);
                pregunta454.setIdPregunta(454);
                pregunta454.setPuntaje(null);
                pregunta454.setTextoPregunta(context.getResources().getString(R.string.textoPregunta454B));
                pregunta454.setIdItem(45);

                item45.addPregunta(pregunta451);
                item45.addPregunta(pregunta452);
                item45.addPregunta(pregunta453);
                item45.addPregunta(pregunta454);
                ese4.addItem(item45);

//                      SEXTO ITEM
                Item item46 = realm.createObject(Item.class);
                item46.setCriterio(context.getResources().getString(R.string.criterio46B));
                item46.setTextoItem(context.getResources().getString(R.string.texto46B));
                item46.setIdItem(6);
                item46.setPuntajeItem(0.0);

                Pregunta pregunta461 = realm.createObject(Pregunta.class);
                pregunta461.setIdPregunta(461);
                pregunta461.setPuntaje(null);
                pregunta461.setTextoPregunta(context.getResources().getString(R.string.textoPregunta461B));
                pregunta461.setIdItem(46);
                Pregunta pregunta462 = realm.createObject(Pregunta.class);
                pregunta462.setIdPregunta(462);
                pregunta462.setPuntaje(null);
                pregunta462.setTextoPregunta(context.getResources().getString(R.string.textoPregunta462B));
                pregunta462.setIdItem(46);

                item46.addPregunta(pregunta461);
                item46.addPregunta(pregunta462);

                ese4.addItem(item46);
                nuevoCuestionario.addEse(ese4);


                //QUINTA ESE
                Ese ese5= realm.createObject(Ese.class);
                ese5.setIdEse(5);
                ese5.setPuntajeEse(0.0);
//                        PRIMER ITEM
                Item item51 = realm.createObject(Item.class);
                item51.setCriterio(context.getResources().getString(R.string.criterio51B));
                item51.setTextoItem(context.getResources().getString(R.string.texto51B));
                item51.setIdItem(1);
                item51.setPuntajeItem(0.0);

                Pregunta pregunta511 = realm.createObject(Pregunta.class);
                pregunta511.setIdPregunta(511);
                pregunta511.setPuntaje(null);
                pregunta511.setTextoPregunta(context.getResources().getString(R.string.textoPregunta511B));
                pregunta511.setIdItem(51);
                Pregunta pregunta512 = realm.createObject(Pregunta.class);
                pregunta512.setIdPregunta(512);
                pregunta512.setPuntaje(null);
                pregunta512.setTextoPregunta(context.getResources().getString(R.string.textoPregunta512B));
                pregunta512.setIdItem(51);
                Pregunta pregunta513 = realm.createObject(Pregunta.class);
                pregunta513.setIdPregunta(513);
                pregunta513.setPuntaje(null);
                pregunta513.setTextoPregunta(context.getResources().getString(R.string.textoPregunta513B));
                pregunta513.setIdItem(51);
                Pregunta pregunta514 = realm.createObject(Pregunta.class);
                pregunta514.setIdPregunta(514);
                pregunta514.setPuntaje(null);
                pregunta514.setTextoPregunta(context.getResources().getString(R.string.textoPregunta514B));
                pregunta514.setIdItem(51);
                Pregunta pregunta515 = realm.createObject(Pregunta.class);
                pregunta515.setIdPregunta(515);
                pregunta515.setPuntaje(null);
                pregunta515.setTextoPregunta(context.getResources().getString(R.string.textoPregunta515B));
                pregunta515.setIdItem(51);
                Pregunta pregunta516 = realm.createObject(Pregunta.class);
                pregunta516.setIdPregunta(516);
                pregunta516.setPuntaje(null);
                pregunta516.setTextoPregunta(context.getResources().getString(R.string.textoPregunta516B));
                pregunta516.setIdItem(51);
                Pregunta pregunta517 = realm.createObject(Pregunta.class);
                pregunta517.setIdPregunta(517);
                pregunta517.setPuntaje(null);
                pregunta517.setTextoPregunta(context.getResources().getString(R.string.textoPregunta517B));
                pregunta517.setIdItem(51);
                item51.addPregunta(pregunta511);
                item51.addPregunta(pregunta512);
                item51.addPregunta(pregunta513);
                item51.addPregunta(pregunta514);
                item51.addPregunta(pregunta515);
                item51.addPregunta(pregunta516);
                item51.addPregunta(pregunta517);

                ese5.addItem(item51);

//                      SEGUNDO ITEM
                Item item52 = realm.createObject(Item.class);
                item52.setCriterio(context.getResources().getString(R.string.criterio52B));
                item52.setTextoItem(context.getResources().getString(R.string.texto52B));
                item52.setIdItem(2);
                item52.setPuntajeItem(0.0);

                Pregunta pregunta521 = realm.createObject(Pregunta.class);
                pregunta521.setIdPregunta(521);
                pregunta521.setPuntaje(null);
                pregunta521.setTextoPregunta(context.getResources().getString(R.string.textoPregunta521B));
                pregunta521.setIdItem(52);
                Pregunta pregunta522 = realm.createObject(Pregunta.class);
                pregunta522.setIdPregunta(522);
                pregunta522.setPuntaje(null);
                pregunta522.setTextoPregunta(context.getResources().getString(R.string.textoPregunta522B));
                pregunta522.setIdItem(52);
                item52.addPregunta(pregunta521);
                item52.addPregunta(pregunta522);

                ese5.addItem(item52);

//                      TERCER ITEM

                Item item53 = realm.createObject(Item.class);
                item53.setCriterio(context.getResources().getString(R.string.criterio53B));
                item53.setTextoItem(context.getResources().getString(R.string.texto53B));
                item53.setIdItem(3);
                item53.setPuntajeItem(0.0);

                Pregunta pregunta531 = realm.createObject(Pregunta.class);
                pregunta531.setIdPregunta(531);
                pregunta531.setPuntaje(null);
                pregunta531.setTextoPregunta(context.getResources().getString(R.string.textoPregunta531B));
                pregunta531.setIdItem(53);
                Pregunta pregunta532 = realm.createObject(Pregunta.class);
                pregunta532.setIdPregunta(532);
                pregunta532.setPuntaje(null);
                pregunta532.setTextoPregunta(context.getResources().getString(R.string.textoPregunta532B));
                pregunta532.setIdItem(53);
                item53.addPregunta(pregunta531);
                item53.addPregunta(pregunta532);

                ese5.addItem(item53);

//                      CUARTO ITEM
                Item item54 = realm.createObject(Item.class);
                item54.setCriterio(context.getResources().getString(R.string.criterio54B));
                item54.setTextoItem(context.getResources().getString(R.string.texto54B));
                item54.setIdItem(4);
                item54.setPuntajeItem(0.0);

                Pregunta pregunta541 = realm.createObject(Pregunta.class);
                pregunta541.setIdPregunta(541);
                pregunta541.setPuntaje(null);
                pregunta541.setTextoPregunta(context.getResources().getString(R.string.textoPregunta541B));
                pregunta541.setIdItem(54);
                Pregunta pregunta542 = realm.createObject(Pregunta.class);
                pregunta542.setIdPregunta(542);
                pregunta542.setPuntaje(null);
                pregunta542.setTextoPregunta(context.getResources().getString(R.string.textoPregunta542B));
                pregunta542.setIdItem(54);
                Pregunta pregunta543 = realm.createObject(Pregunta.class);
                pregunta543.setIdPregunta(543);
                pregunta543.setPuntaje(null);
                pregunta543.setTextoPregunta(context.getResources().getString(R.string.textoPregunta543B));
                pregunta543.setIdItem(54);
                Pregunta pregunta544 = realm.createObject(Pregunta.class);
                pregunta544.setIdPregunta(544);
                pregunta544.setPuntaje(null);
                pregunta544.setTextoPregunta(context.getResources().getString(R.string.textoPregunta544B));
                pregunta544.setIdItem(54);
                Pregunta pregunta545 = realm.createObject(Pregunta.class);
                pregunta545.setIdPregunta(545);
                pregunta545.setPuntaje(null);
                pregunta545.setTextoPregunta(context.getResources().getString(R.string.textoPregunta545B));
                pregunta545.setIdItem(54);
                item54.addPregunta(pregunta541);
                item54.addPregunta(pregunta542);
                item54.addPregunta(pregunta543);
                item54.addPregunta(pregunta544);
                item54.addPregunta(pregunta545);

                ese5.addItem(item54);

//                      QUINTO ITEM
                Item item55 = realm.createObject(Item.class);
                item55.setCriterio(context.getResources().getString(R.string.criterio55B));
                item55.setTextoItem(context.getResources().getString(R.string.texto55B));
                item55.setIdItem(5);
                item55.setPuntajeItem(0.0);

                Pregunta pregunta551 = realm.createObject(Pregunta.class);
                pregunta551.setIdPregunta(551);
                pregunta551.setPuntaje(null);
                pregunta551.setTextoPregunta(context.getResources().getString(R.string.textoPregunta551B));
                pregunta551.setIdItem(55);
                Pregunta pregunta552 = realm.createObject(Pregunta.class);
                pregunta552.setIdPregunta(552);
                pregunta552.setPuntaje(null);
                pregunta552.setTextoPregunta(context.getResources().getString(R.string.textoPregunta552B));
                pregunta552.setIdItem(55);
                item55.addPregunta(pregunta551);
                item55.addPregunta(pregunta552);

                ese5.addItem(item55);
                nuevoCuestionario.addEse(ese5);

                for (Ese unaEses :
                        nuevoCuestionario.getListaEses()) {
                    unaEses.setIdCuestionario(nuevoCuestionario.getIdCuestionario());
                    for (Item unItem :

                            unaEses.getListaItem()) {
                        unItem.setIdEse(unaEses.getIdEse());
                        unItem.setIdCuestionario(nuevoCuestionario.getIdCuestionario());
                        for (Pregunta unaPregunta :
                                unItem.getListaPreguntas()) {
                            unaPregunta.setIdItem(unItem.getIdItem());
                            unaPregunta.setIdCuestioniario(nuevoCuestionario.getIdCuestionario());
                            unaPregunta.setIdEse(unaEses.getIdEse());
                        }
                    }
                }
            }
        });
        //endregion

        //region CREAR CUESTIONARIO C
        nBgRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Cuestionario nuevoCuestionario = realm.createObject(Cuestionario.class,"3");

                nuevoCuestionario.setNombreCuestionario(context.getString(R.string.areaExterna));


                Ese ese1= realm.createObject(Ese.class);
                ese1.setIdEse(1);
                ese1.setPuntajeEse(0.0);

        //PRIMER ITEM
                Item item11 = realm.createObject(Item.class);
                item11.setCriterio(context.getResources().getString(R.string.criterio11C));
                item11.setTextoItem(context.getResources().getString(R.string.texto11C));
                item11.setIdItem(1);
                item11.setPuntajeItem(0.0);

                Pregunta pregunta111 = realm.createObject(Pregunta.class);
                pregunta111.setIdPregunta(111);
                pregunta111.setPuntaje(null);
                pregunta111.setTextoPregunta(context.getResources().getString(R.string.textoPregunta111C));
                pregunta111.setIdItem(11);
                Pregunta pregunta112 = realm.createObject(Pregunta.class);
                pregunta112.setIdPregunta(112);
                pregunta112.setPuntaje(null);
                pregunta112.setTextoPregunta(context.getResources().getString(R.string.textoPregunta112C));
                pregunta112.setIdItem(11);
                Pregunta pregunta113 = realm.createObject(Pregunta.class);
                pregunta113.setIdPregunta(113);
                pregunta113.setPuntaje(null);
                pregunta113.setTextoPregunta(context.getResources().getString(R.string.textoPregunta113C));
                pregunta113.setIdItem(11);
                Pregunta pregunta114 = realm.createObject(Pregunta.class);
                pregunta114.setIdPregunta(114);
                pregunta114.setPuntaje(null);
                pregunta114.setTextoPregunta(context.getResources().getString(R.string.textoPregunta114C));
                pregunta114.setIdItem(11);
                Pregunta pregunta115 = realm.createObject(Pregunta.class);
                pregunta115.setIdPregunta(115);
                pregunta115.setPuntaje(null);
                pregunta115.setTextoPregunta(context.getResources().getString(R.string.textoPregunta115C));
                pregunta115.setIdItem(11);

                item11.addPregunta(pregunta111);
                item11.addPregunta(pregunta112);
                item11.addPregunta(pregunta113);
                item11.addPregunta(pregunta114);
                item11.addPregunta(pregunta115);

                ese1.addItem(item11);

//                      SEGUNDO ITEM
                Item item12 = realm.createObject(Item.class);
                item12.setCriterio(context.getResources().getString(R.string.criterio12C));
                item12.setTextoItem(context.getResources().getString(R.string.texto12C));
                item12.setIdItem(2);
                item12.setPuntajeItem(0.0);

                Pregunta pregunta121 = realm.createObject(Pregunta.class);
                pregunta121.setIdPregunta(121);
                pregunta121.setPuntaje(null);
                pregunta121.setTextoPregunta(context.getResources().getString(R.string.textoPregunta121C));
                pregunta121.setIdItem(12);
                Pregunta pregunta122 = realm.createObject(Pregunta.class);
                pregunta122.setIdPregunta(122);
                pregunta122.setPuntaje(null);
                pregunta122.setTextoPregunta(context.getResources().getString(R.string.textoPregunta122C));
                pregunta122.setIdItem(12);
                Pregunta pregunta123 = realm.createObject(Pregunta.class);
                pregunta123.setIdPregunta(123);
                pregunta123.setPuntaje(null);
                pregunta123.setTextoPregunta(context.getResources().getString(R.string.textoPregunta123C));
                pregunta123.setIdItem(12);
                Pregunta pregunta124 = realm.createObject(Pregunta.class);
                pregunta124.setIdPregunta(124);
                pregunta124.setPuntaje(null);
                pregunta124.setTextoPregunta(context.getResources().getString(R.string.textoPregunta124C));
                pregunta124.setIdItem(12);
                item12.addPregunta(pregunta121);
                item12.addPregunta(pregunta122);
                item12.addPregunta(pregunta123);
                item12.addPregunta(pregunta124);

                ese1.addItem(item12);

//                      TERCER ITEM
                Item item13 = realm.createObject(Item.class);
                item13.setCriterio(context.getResources().getString(R.string.criterio13C));
                item13.setTextoItem(context.getResources().getString(R.string.texto13C));
                item13.setIdItem(3);
                item13.setPuntajeItem(0.0);

                Pregunta pregunta131 = realm.createObject(Pregunta.class);
                pregunta131.setIdPregunta(131);
                pregunta131.setPuntaje(null);
                pregunta131.setTextoPregunta(context.getResources().getString(R.string.textoPregunta131C));
                pregunta131.setIdItem(13);
                Pregunta pregunta132 = realm.createObject(Pregunta.class);
                pregunta132.setIdPregunta(132);
                pregunta132.setPuntaje(null);
                pregunta132.setTextoPregunta(context.getResources().getString(R.string.textoPregunta132C));
                pregunta132.setIdItem(13);
                Pregunta pregunta133 = realm.createObject(Pregunta.class);
                pregunta133.setIdPregunta(133);
                pregunta133.setPuntaje(null);
                pregunta133.setTextoPregunta(context.getResources().getString(R.string.textoPregunta133C));
                pregunta133.setIdItem(13);
                item13.addPregunta(pregunta131);
                item13.addPregunta(pregunta132);
                item13.addPregunta(pregunta133);

                ese1.addItem(item13);

//                      CUARTO ITEM
                Item item14 = realm.createObject(Item.class);
                item14.setCriterio(context.getResources().getString(R.string.criterio14C));
                item14.setTextoItem(context.getResources().getString(R.string.texto14C));
                item14.setIdItem(4);
                item14.setPuntajeItem(0.0);

                Pregunta pregunta141 = realm.createObject(Pregunta.class);
                pregunta141.setIdPregunta(141);
                pregunta141.setPuntaje(null);
                pregunta141.setTextoPregunta(context.getResources().getString(R.string.textoPregunta141C));
                pregunta141.setIdItem(14);
                Pregunta pregunta142 = realm.createObject(Pregunta.class);
                pregunta142.setIdPregunta(142);
                pregunta142.setPuntaje(null);
                pregunta142.setTextoPregunta(context.getResources().getString(R.string.textoPregunta142C));
                pregunta142.setIdItem(14);

                item14.addPregunta(pregunta141);
                item14.addPregunta(pregunta142);

                ese1.addItem(item14);

                nuevoCuestionario.addEse(ese1);

//                SEGUNDA ESE

                Ese ese2= realm.createObject(Ese.class);
                ese2.setIdEse(2);
                ese2.setPuntajeEse(0.0);

//                        PRIMER ITEM
                Item item21 = realm.createObject(Item.class);
                item21.setCriterio(context.getResources().getString(R.string.criterio21C));
                item21.setTextoItem(context.getResources().getString(R.string.texto21C));
                item21.setIdItem(1);
                item21.setPuntajeItem(0.0);

                Pregunta pregunta211 = realm.createObject(Pregunta.class);
                pregunta211.setIdPregunta(211);
                pregunta211.setPuntaje(null);
                pregunta211.setTextoPregunta(context.getResources().getString(R.string.textoPregunta211C));
                pregunta211.setIdItem(21);
                Pregunta pregunta212 = realm.createObject(Pregunta.class);
                pregunta212.setIdPregunta(212);
                pregunta212.setPuntaje(null);
                pregunta212.setTextoPregunta(context.getResources().getString(R.string.textoPregunta212C));
                pregunta212.setIdItem(21);
                Pregunta pregunta213 = realm.createObject(Pregunta.class);
                pregunta213.setIdPregunta(213);
                pregunta213.setPuntaje(null);
                pregunta213.setTextoPregunta(context.getResources().getString(R.string.textoPregunta213C));
                pregunta213.setIdItem(21);

                item21.addPregunta(pregunta211);
                item21.addPregunta(pregunta212);
                item21.addPregunta(pregunta213);


                ese2.addItem(item21);

//                      SEGUNDO ITEM
                Item item22 = realm.createObject(Item.class);
                item22.setCriterio(context.getResources().getString(R.string.criterio22C));
                item22.setTextoItem(context.getResources().getString(R.string.texto22C));
                item22.setIdItem(2);
                item22.setPuntajeItem(0.0);

                Pregunta pregunta221 = realm.createObject(Pregunta.class);
                pregunta221.setIdPregunta(221);
                pregunta221.setPuntaje(null);
                pregunta221.setTextoPregunta(context.getResources().getString(R.string.textoPregunta221C));
                pregunta221.setIdItem(22);

                item22.addPregunta(pregunta221);


                ese2.addItem(item22);

//                      TERCER ITEM
                Item item23 = realm.createObject(Item.class);
                item23.setCriterio(context.getResources().getString(R.string.criterio23C));
                item23.setTextoItem(context.getResources().getString(R.string.texto23C));
                item23.setIdItem(3);
                item23.setPuntajeItem(0.0);

                Pregunta pregunta231 = realm.createObject(Pregunta.class);
                pregunta231.setIdPregunta(231);
                pregunta231.setPuntaje(null);
                pregunta231.setTextoPregunta(context.getResources().getString(R.string.textoPregunta231C));
                pregunta231.setIdItem(23);
                Pregunta pregunta232 = realm.createObject(Pregunta.class);
                pregunta232.setIdPregunta(232);
                pregunta232.setPuntaje(null);
                pregunta232.setTextoPregunta(context.getResources().getString(R.string.textoPregunta232C));
                pregunta232.setIdItem(23);
                Pregunta pregunta233 = realm.createObject(Pregunta.class);
                pregunta233.setIdPregunta(233);
                pregunta233.setPuntaje(null);
                pregunta233.setTextoPregunta(context.getResources().getString(R.string.textoPregunta233C));
                pregunta233.setIdItem(23);

                item23.addPregunta(pregunta231);
                item23.addPregunta(pregunta232);
                item23.addPregunta(pregunta233);


                ese2.addItem(item23);

//                      CUARTO ITEM
                Item item24 = realm.createObject(Item.class);
                item24.setCriterio(context.getResources().getString(R.string.criterio24C));
                item24.setTextoItem(context.getResources().getString(R.string.texto24C));
                item24.setIdItem(4);
                item24.setPuntajeItem(0.0);

                Pregunta pregunta241 = realm.createObject(Pregunta.class);
                pregunta241.setIdPregunta(241);
                pregunta241.setPuntaje(null);
                pregunta241.setTextoPregunta(context.getResources().getString(R.string.textoPregunta241C));
                pregunta241.setIdItem(24);
                Pregunta pregunta242 = realm.createObject(Pregunta.class);
                pregunta242.setIdPregunta(242);
                pregunta242.setPuntaje(null);
                pregunta242.setTextoPregunta(context.getResources().getString(R.string.textoPregunta242C));
                pregunta242.setIdItem(24);
                Pregunta pregunta243 = realm.createObject(Pregunta.class);
                pregunta243.setIdPregunta(243);
                pregunta243.setPuntaje(null);
                pregunta243.setTextoPregunta(context.getResources().getString(R.string.textoPregunta243C));
                pregunta243.setIdItem(24);

                item24.addPregunta(pregunta241);
                item24.addPregunta(pregunta242);
                item24.addPregunta(pregunta243);


                ese2.addItem(item24);

//                      QUINTO ITEM
                Item item25 = realm.createObject(Item.class);
                item25.setCriterio(context.getResources().getString(R.string.criterio25C));
                item25.setTextoItem(context.getResources().getString(R.string.texto25C));
                item25.setIdItem(5);
                item25.setPuntajeItem(0.0);

                Pregunta pregunta251 = realm.createObject(Pregunta.class);
                pregunta251.setIdPregunta(251);
                pregunta251.setPuntaje(null);
                pregunta251.setTextoPregunta(context.getResources().getString(R.string.textoPregunta251C));
                pregunta251.setIdItem(25);
                Pregunta pregunta252 = realm.createObject(Pregunta.class);
                pregunta252.setIdPregunta(252);
                pregunta252.setPuntaje(null);
                pregunta252.setTextoPregunta(context.getResources().getString(R.string.textoPregunta252C));
                pregunta252.setIdItem(25);
                Pregunta pregunta253 = realm.createObject(Pregunta.class);
                pregunta253.setIdPregunta(253);
                pregunta253.setPuntaje(null);
                pregunta253.setTextoPregunta(context.getResources().getString(R.string.textoPregunta253C));
                pregunta253.setIdItem(25);
                Pregunta pregunta254 = realm.createObject(Pregunta.class);
                pregunta254.setIdPregunta(254);
                pregunta254.setPuntaje(null);
                pregunta254.setTextoPregunta(context.getResources().getString(R.string.textoPregunta254C));
                pregunta254.setIdItem(25);
                Pregunta pregunta255 = realm.createObject(Pregunta.class);
                pregunta255.setIdPregunta(255);
                pregunta255.setPuntaje(null);
                pregunta255.setTextoPregunta(context.getResources().getString(R.string.textoPregunta255C));
                pregunta255.setIdItem(25);
                Pregunta pregunta256 = realm.createObject(Pregunta.class);
                pregunta256.setIdPregunta(256);
                pregunta256.setPuntaje(null);
                pregunta256.setTextoPregunta(context.getResources().getString(R.string.textoPregunta256C));
                pregunta256.setIdItem(25);
                item25.addPregunta(pregunta251);
                item25.addPregunta(pregunta252);
                item25.addPregunta(pregunta253);
                item25.addPregunta(pregunta254);
                item25.addPregunta(pregunta255);
                item25.addPregunta(pregunta256);

                ese2.addItem(item25);
                nuevoCuestionario.addEse(ese2);

//                TERCERA ESE

                Ese ese3= realm.createObject(Ese.class);
                ese3.setIdEse(3);
                ese3.setPuntajeEse(0.0);

//                        PRIMER ITEM
                Item item31 = realm.createObject(Item.class);
                item31.setCriterio(context.getResources().getString(R.string.criterio31C));
                item31.setTextoItem(context.getResources().getString(R.string.texto31C));
                item31.setIdItem(1);
                item31.setPuntajeItem(0.0);

                Pregunta pregunta311 = realm.createObject(Pregunta.class);
                pregunta311.setIdPregunta(311);
                pregunta311.setPuntaje(null);
                pregunta311.setTextoPregunta(context.getResources().getString(R.string.textoPregunta311C));
                pregunta311.setIdItem(31);
                Pregunta pregunta312 = realm.createObject(Pregunta.class);
                pregunta312.setIdPregunta(312);
                pregunta312.setPuntaje(null);
                pregunta312.setTextoPregunta(context.getResources().getString(R.string.textoPregunta312C));
                pregunta312.setIdItem(31);
                Pregunta pregunta313 = realm.createObject(Pregunta.class);
                pregunta313.setIdPregunta(313);
                pregunta313.setPuntaje(null);
                pregunta313.setTextoPregunta(context.getResources().getString(R.string.textoPregunta313C));
                pregunta313.setIdItem(31);
                item31.addPregunta(pregunta311);
                item31.addPregunta(pregunta312);
                item31.addPregunta(pregunta313);

                ese3.addItem(item31);

//                      SEGUNDO ITEM
                Item item32 = realm.createObject(Item.class);
                item32.setCriterio(context.getResources().getString(R.string.criterio32C));
                item32.setTextoItem(context.getResources().getString(R.string.texto32C));
                item32.setIdItem(2);
                item32.setPuntajeItem(0.0);

                Pregunta pregunta321 = realm.createObject(Pregunta.class);
                pregunta321.setIdPregunta(321);
                pregunta321.setPuntaje(null);
                pregunta321.setTextoPregunta(context.getResources().getString(R.string.textoPregunta321C));
                pregunta321.setIdItem(32);
                Pregunta pregunta322 = realm.createObject(Pregunta.class);
                pregunta322.setIdPregunta(322);
                pregunta322.setPuntaje(null);
                pregunta322.setTextoPregunta(context.getResources().getString(R.string.textoPregunta322C));
                pregunta322.setIdItem(32);
                Pregunta pregunta323 = realm.createObject(Pregunta.class);
                pregunta323.setIdPregunta(323);
                pregunta323.setPuntaje(null);
                pregunta323.setTextoPregunta(context.getResources().getString(R.string.textoPregunta323C));
                pregunta323.setIdItem(32);

                item32.addPregunta(pregunta321);
                item32.addPregunta(pregunta322);
                item32.addPregunta(pregunta323);


                ese3.addItem(item32);

//                      TERCER ITEM
                Item item33 = realm.createObject(Item.class);
                item33.setCriterio(context.getResources().getString(R.string.criterio33C));
                item33.setTextoItem(context.getResources().getString(R.string.texto33C));
                item33.setIdItem(3);
                item33.setPuntajeItem(0.0);

                Pregunta pregunta331 = realm.createObject(Pregunta.class);
                pregunta331.setIdPregunta(331);
                pregunta331.setPuntaje(null);
                pregunta331.setTextoPregunta(context.getResources().getString(R.string.textoPregunta331C));
                pregunta331.setIdItem(33);
                Pregunta pregunta332 = realm.createObject(Pregunta.class);
                pregunta332.setIdPregunta(332);
                pregunta332.setPuntaje(null);
                pregunta332.setTextoPregunta(context.getResources().getString(R.string.textoPregunta332C));
                pregunta332.setIdItem(33);
                Pregunta pregunta333 = realm.createObject(Pregunta.class);
                pregunta333.setIdPregunta(333);
                pregunta333.setPuntaje(null);
                pregunta333.setTextoPregunta(context.getResources().getString(R.string.textoPregunta333C));
                pregunta333.setIdItem(33);
                Pregunta pregunta334 = realm.createObject(Pregunta.class);
                pregunta334.setIdPregunta(334);
                pregunta334.setPuntaje(null);
                pregunta334.setTextoPregunta(context.getResources().getString(R.string.textoPregunta334C));
                pregunta334.setIdItem(33);

                item33.addPregunta(pregunta331);
                item33.addPregunta(pregunta332);
                item33.addPregunta(pregunta333);
                item33.addPregunta(pregunta334);


                ese3.addItem(item33);

//                      CUARTO ITEM
                Item item34 = realm.createObject(Item.class);
                item34.setCriterio(context.getResources().getString(R.string.criterio34C));
                item34.setTextoItem(context.getResources().getString(R.string.texto34C));
                item34.setIdItem(4);
                item34.setPuntajeItem(0.0);

                Pregunta pregunta341 = realm.createObject(Pregunta.class);
                pregunta341.setIdPregunta(341);
                pregunta341.setPuntaje(null);
                pregunta341.setTextoPregunta(context.getResources().getString(R.string.textoPregunta341C));
                pregunta341.setIdItem(34);
                Pregunta pregunta342 = realm.createObject(Pregunta.class);
                pregunta342.setIdPregunta(342);
                pregunta342.setPuntaje(null);
                pregunta342.setTextoPregunta(context.getResources().getString(R.string.textoPregunta342C));
                pregunta342.setIdItem(34);
                Pregunta pregunta343 = realm.createObject(Pregunta.class);
                pregunta343.setIdPregunta(343);
                pregunta343.setPuntaje(null);
                pregunta343.setTextoPregunta(context.getResources().getString(R.string.textoPregunta343C));
                pregunta343.setIdItem(34);

                item34.addPregunta(pregunta341);
                item34.addPregunta(pregunta342);
                item34.addPregunta(pregunta343);


                ese3.addItem(item34);

//                      QUINTO ITEM
                Item item35 = realm.createObject(Item.class);
                item35.setCriterio(context.getResources().getString(R.string.criterio35C));
                item35.setTextoItem(context.getResources().getString(R.string.texto35C));
                item35.setIdItem(5);
                item35.setPuntajeItem(0.0);

                Pregunta pregunta351 = realm.createObject(Pregunta.class);
                pregunta351.setIdPregunta(351);
                pregunta351.setPuntaje(null);
                pregunta351.setTextoPregunta(context.getResources().getString(R.string.textoPregunta351C));
                pregunta351.setIdItem(35);
                Pregunta pregunta352 = realm.createObject(Pregunta.class);
                pregunta352.setIdPregunta(352);
                pregunta352.setPuntaje(null);
                pregunta352.setTextoPregunta(context.getResources().getString(R.string.textoPregunta352C));
                pregunta352.setIdItem(35);
                Pregunta pregunta353 = realm.createObject(Pregunta.class);
                pregunta353.setIdPregunta(353);
                pregunta353.setPuntaje(null);
                pregunta353.setTextoPregunta(context.getResources().getString(R.string.textoPregunta353C));
                pregunta353.setIdItem(35);
                Pregunta pregunta354 = realm.createObject(Pregunta.class);
                pregunta354.setIdPregunta(354);
                pregunta354.setPuntaje(null);
                pregunta354.setTextoPregunta(context.getResources().getString(R.string.textoPregunta354C));
                pregunta354.setIdItem(35);
                item35.addPregunta(pregunta351);
                item35.addPregunta(pregunta352);
                item35.addPregunta(pregunta353);
                item35.addPregunta(pregunta354);

                ese3.addItem(item35);
                nuevoCuestionario.addEse(ese3);

                //                CUARTA ESE

                Ese ese4= realm.createObject(Ese.class);
                ese4.setIdEse(4);
                ese4.setPuntajeEse(0.0);

//                        PRIMER ITEM
                Item item41 = realm.createObject(Item.class);
                item41.setCriterio(context.getResources().getString(R.string.criterio41C));
                item41.setTextoItem(context.getResources().getString(R.string.texto41C));
                item41.setIdItem(1);
                item41.setPuntajeItem(0.0);

                Pregunta pregunta411 = realm.createObject(Pregunta.class);
                pregunta411.setIdPregunta(411);
                pregunta411.setPuntaje(null);
                pregunta411.setTextoPregunta(context.getResources().getString(R.string.textoPregunta411C));
                pregunta411.setIdItem(41);
                Pregunta pregunta412 = realm.createObject(Pregunta.class);
                pregunta412.setIdPregunta(412);
                pregunta412.setPuntaje(null);
                pregunta412.setTextoPregunta(context.getResources().getString(R.string.textoPregunta412C));
                pregunta412.setIdItem(41);
                Pregunta pregunta413 = realm.createObject(Pregunta.class);
                pregunta413.setIdPregunta(413);
                pregunta413.setPuntaje(null);
                pregunta413.setTextoPregunta(context.getResources().getString(R.string.textoPregunta413C));
                pregunta413.setIdItem(41);
                Pregunta pregunta414 = realm.createObject(Pregunta.class);
                pregunta414.setIdPregunta(414);
                pregunta414.setPuntaje(null);
                pregunta414.setTextoPregunta(context.getResources().getString(R.string.textoPregunta414C));
                pregunta414.setIdItem(41);


                item41.addPregunta(pregunta411);
                item41.addPregunta(pregunta412);
                item41.addPregunta(pregunta413);
                item41.addPregunta(pregunta414);


                ese4.addItem(item41);

//                      SEGUNDO ITEM
                Item item42 = realm.createObject(Item.class);
                item42.setCriterio(context.getResources().getString(R.string.criterio42C));
                item42.setTextoItem(context.getResources().getString(R.string.texto42C));
                item42.setIdItem(2);
                item42.setPuntajeItem(0.0);

                Pregunta pregunta421 = realm.createObject(Pregunta.class);
                pregunta421.setIdPregunta(421);
                pregunta421.setPuntaje(null);
                pregunta421.setTextoPregunta(context.getResources().getString(R.string.textoPregunta421C));
                pregunta421.setIdItem(42);
                Pregunta pregunta422 = realm.createObject(Pregunta.class);
                pregunta422.setIdPregunta(422);
                pregunta422.setPuntaje(null);
                pregunta422.setTextoPregunta(context.getResources().getString(R.string.textoPregunta422C));
                pregunta422.setIdItem(42);
                item42.addPregunta(pregunta421);
                item42.addPregunta(pregunta422);

                ese4.addItem(item42);

//                      TERCER ITEM

                Item item43 = realm.createObject(Item.class);
                item43.setCriterio(context.getResources().getString(R.string.criterio43C));
                item43.setTextoItem(context.getResources().getString(R.string.texto43C));
                item43.setIdItem(3);
                item43.setPuntajeItem(0.0);

                Pregunta pregunta431 = realm.createObject(Pregunta.class);
                pregunta431.setIdPregunta(431);
                pregunta431.setPuntaje(null);
                pregunta431.setTextoPregunta(context.getResources().getString(R.string.textoPregunta431C));
                pregunta431.setIdItem(43);
                Pregunta pregunta432 = realm.createObject(Pregunta.class);
                pregunta432.setIdPregunta(432);
                pregunta432.setPuntaje(null);
                pregunta432.setTextoPregunta(context.getResources().getString(R.string.textoPregunta432C));
                pregunta432.setIdItem(43);
                Pregunta pregunta433 = realm.createObject(Pregunta.class);
                pregunta433.setIdPregunta(433);
                pregunta433.setPuntaje(null);
                pregunta433.setTextoPregunta(context.getResources().getString(R.string.textoPregunta433C));
                pregunta433.setIdItem(43);

                item43.addPregunta(pregunta431);
                item43.addPregunta(pregunta432);
                item43.addPregunta(pregunta433);


                ese4.addItem(item43);


//                      CUARTO ITEM
                Item item44 = realm.createObject(Item.class);
                item44.setCriterio(context.getResources().getString(R.string.criterio44C));
                item44.setTextoItem(context.getResources().getString(R.string.texto44C));
                item44.setIdItem(4);
                item44.setPuntajeItem(0.0);

                Pregunta pregunta441 = realm.createObject(Pregunta.class);
                pregunta441.setIdPregunta(441);
                pregunta441.setPuntaje(null);
                pregunta441.setTextoPregunta(context.getResources().getString(R.string.textoPregunta441C));
                pregunta441.setIdItem(44);
                Pregunta pregunta442 = realm.createObject(Pregunta.class);
                pregunta442.setIdPregunta(442);
                pregunta442.setPuntaje(null);
                pregunta442.setTextoPregunta(context.getResources().getString(R.string.textoPregunta442C));
                pregunta442.setIdItem(44);

                item44.addPregunta(pregunta441);
                item44.addPregunta(pregunta442);


                ese4.addItem(item44);

//                      QUINTO ITEM
                Item item45 = realm.createObject(Item.class);
                item45.setCriterio(context.getResources().getString(R.string.criterio45C));
                item45.setTextoItem(context.getResources().getString(R.string.texto45C));
                item45.setIdItem(5);
                item45.setPuntajeItem(0.0);

                Pregunta pregunta451 = realm.createObject(Pregunta.class);
                pregunta451.setIdPregunta(451);
                pregunta451.setPuntaje(null);
                pregunta451.setTextoPregunta(context.getResources().getString(R.string.textoPregunta451C));
                pregunta451.setIdItem(45);
                Pregunta pregunta452 = realm.createObject(Pregunta.class);
                pregunta452.setIdPregunta(452);
                pregunta452.setPuntaje(null);
                pregunta452.setTextoPregunta(context.getResources().getString(R.string.textoPregunta452));
                pregunta452.setIdItem(45);
                Pregunta pregunta453 = realm.createObject(Pregunta.class);
                pregunta453.setIdPregunta(453);
                pregunta453.setPuntaje(null);
                pregunta453.setTextoPregunta(context.getResources().getString(R.string.textoPregunta453C));
                pregunta453.setIdItem(45);
                Pregunta pregunta454 = realm.createObject(Pregunta.class);
                pregunta454.setIdPregunta(454);
                pregunta454.setPuntaje(null);
                pregunta454.setTextoPregunta(context.getResources().getString(R.string.textoPregunta454C));
                pregunta454.setIdItem(45);
                Pregunta pregunta455 = realm.createObject(Pregunta.class);
                pregunta455.setIdPregunta(455);
                pregunta455.setPuntaje(null);
                pregunta455.setTextoPregunta(context.getResources().getString(R.string.textoPregunta455C));
                pregunta455.setIdItem(45);
                item45.addPregunta(pregunta451);
                item45.addPregunta(pregunta452);
                item45.addPregunta(pregunta453);
                item45.addPregunta(pregunta454);
                item45.addPregunta(pregunta455);
                ese4.addItem(item45);

//                      SEXTO ITEM
                Item item46 = realm.createObject(Item.class);
                item46.setCriterio(context.getResources().getString(R.string.criterio46C));
                item46.setTextoItem(context.getResources().getString(R.string.texto46C));
                item46.setIdItem(6);
                item46.setPuntajeItem(0.0);

                Pregunta pregunta461 = realm.createObject(Pregunta.class);
                pregunta461.setIdPregunta(461);
                pregunta461.setPuntaje(null);
                pregunta461.setTextoPregunta(context.getResources().getString(R.string.textoPregunta461C));
                pregunta461.setIdItem(46);
                Pregunta pregunta462 = realm.createObject(Pregunta.class);
                pregunta462.setIdPregunta(462);
                pregunta462.setPuntaje(null);
                pregunta462.setTextoPregunta(context.getResources().getString(R.string.textoPregunta462C));
                pregunta462.setIdItem(46);

                item46.addPregunta(pregunta461);
                item46.addPregunta(pregunta462);

                ese4.addItem(item46);
                nuevoCuestionario.addEse(ese4);


                //QUINTA ESE
                Ese ese5= realm.createObject(Ese.class);
                ese5.setIdEse(5);
                ese5.setPuntajeEse(0.0);
//                        PRIMER ITEM
                Item item51 = realm.createObject(Item.class);
                item51.setCriterio(context.getResources().getString(R.string.criterio51C));
                item51.setTextoItem(context.getResources().getString(R.string.texto51C));
                item51.setIdItem(1);
                item51.setPuntajeItem(0.0);

                Pregunta pregunta511 = realm.createObject(Pregunta.class);
                pregunta511.setIdPregunta(511);
                pregunta511.setPuntaje(null);
                pregunta511.setTextoPregunta(context.getResources().getString(R.string.textoPregunta511C));
                pregunta511.setIdItem(51);
                Pregunta pregunta512 = realm.createObject(Pregunta.class);
                pregunta512.setIdPregunta(512);
                pregunta512.setPuntaje(null);
                pregunta512.setTextoPregunta(context.getResources().getString(R.string.textoPregunta512C));
                pregunta512.setIdItem(51);
                Pregunta pregunta513 = realm.createObject(Pregunta.class);
                pregunta513.setIdPregunta(513);
                pregunta513.setPuntaje(null);
                pregunta513.setTextoPregunta(context.getResources().getString(R.string.textoPregunta513C));
                pregunta513.setIdItem(51);
                Pregunta pregunta514 = realm.createObject(Pregunta.class);
                pregunta514.setIdPregunta(514);
                pregunta514.setPuntaje(null);
                pregunta514.setTextoPregunta(context.getResources().getString(R.string.textoPregunta514C));
                pregunta514.setIdItem(51);
                Pregunta pregunta515 = realm.createObject(Pregunta.class);
                pregunta515.setIdPregunta(515);
                pregunta515.setPuntaje(null);
                pregunta515.setTextoPregunta(context.getResources().getString(R.string.textoPregunta515C));
                pregunta515.setIdItem(51);
                Pregunta pregunta516 = realm.createObject(Pregunta.class);
                pregunta516.setIdPregunta(516);
                pregunta516.setPuntaje(null);
                pregunta516.setTextoPregunta(context.getResources().getString(R.string.textoPregunta516C));
                pregunta516.setIdItem(51);
                Pregunta pregunta517 = realm.createObject(Pregunta.class);
                pregunta517.setIdPregunta(517);
                pregunta517.setPuntaje(null);
                pregunta517.setTextoPregunta(context.getResources().getString(R.string.textoPregunta517C));
                pregunta517.setIdItem(51);
                item51.addPregunta(pregunta511);
                item51.addPregunta(pregunta512);
                item51.addPregunta(pregunta513);
                item51.addPregunta(pregunta514);
                item51.addPregunta(pregunta515);
                item51.addPregunta(pregunta516);
                item51.addPregunta(pregunta517);

                ese5.addItem(item51);

//                      SEGUNDO ITEM
                Item item52 = realm.createObject(Item.class);
                item52.setCriterio(context.getResources().getString(R.string.criterio52C));
                item52.setTextoItem(context.getResources().getString(R.string.texto52C));
                item52.setIdItem(2);
                item52.setPuntajeItem(0.0);

                Pregunta pregunta521 = realm.createObject(Pregunta.class);
                pregunta521.setIdPregunta(521);
                pregunta521.setPuntaje(null);
                pregunta521.setTextoPregunta(context.getResources().getString(R.string.textoPregunta521C));
                pregunta521.setIdItem(52);
                Pregunta pregunta522 = realm.createObject(Pregunta.class);
                pregunta522.setIdPregunta(522);
                pregunta522.setPuntaje(null);
                pregunta522.setTextoPregunta(context.getResources().getString(R.string.textoPregunta522C));
                pregunta522.setIdItem(52);
                item52.addPregunta(pregunta521);
                item52.addPregunta(pregunta522);

                ese5.addItem(item52);

//                      TERCER ITEM

                Item item53 = realm.createObject(Item.class);
                item53.setCriterio(context.getResources().getString(R.string.criterio53C));
                item53.setTextoItem(context.getResources().getString(R.string.texto53C));
                item53.setIdItem(3);
                item53.setPuntajeItem(0.0);

                Pregunta pregunta531 = realm.createObject(Pregunta.class);
                pregunta531.setIdPregunta(531);
                pregunta531.setPuntaje(null);
                pregunta531.setTextoPregunta(context.getResources().getString(R.string.textoPregunta531C));
                pregunta531.setIdItem(53);
                Pregunta pregunta532 = realm.createObject(Pregunta.class);
                pregunta532.setIdPregunta(532);
                pregunta532.setPuntaje(null);
                pregunta532.setTextoPregunta(context.getResources().getString(R.string.textoPregunta532C));
                pregunta532.setIdItem(53);
                item53.addPregunta(pregunta531);
                item53.addPregunta(pregunta532);

                ese5.addItem(item53);

//                      CUARTO ITEM
                Item item54 = realm.createObject(Item.class);
                item54.setCriterio(context.getResources().getString(R.string.criterio54C));
                item54.setTextoItem(context.getResources().getString(R.string.texto54C));
                item54.setIdItem(4);
                item54.setPuntajeItem(0.0);

                Pregunta pregunta541 = realm.createObject(Pregunta.class);
                pregunta541.setIdPregunta(541);
                pregunta541.setPuntaje(null);
                pregunta541.setTextoPregunta(context.getResources().getString(R.string.textoPregunta541C));
                pregunta541.setIdItem(54);
                Pregunta pregunta542 = realm.createObject(Pregunta.class);
                pregunta542.setIdPregunta(542);
                pregunta542.setPuntaje(null);
                pregunta542.setTextoPregunta(context.getResources().getString(R.string.textoPregunta542C));
                pregunta542.setIdItem(54);
                Pregunta pregunta543 = realm.createObject(Pregunta.class);
                pregunta543.setIdPregunta(543);
                pregunta543.setPuntaje(null);
                pregunta543.setTextoPregunta(context.getResources().getString(R.string.textoPregunta543C));
                pregunta543.setIdItem(54);
                Pregunta pregunta544 = realm.createObject(Pregunta.class);
                pregunta544.setIdPregunta(544);
                pregunta544.setPuntaje(null);
                pregunta544.setTextoPregunta(context.getResources().getString(R.string.textoPregunta544C));
                pregunta544.setIdItem(54);
                Pregunta pregunta545 = realm.createObject(Pregunta.class);
                pregunta545.setIdPregunta(545);
                pregunta545.setPuntaje(null);
                pregunta545.setTextoPregunta(context.getResources().getString(R.string.textoPregunta545C));
                pregunta545.setIdItem(54);
                item54.addPregunta(pregunta541);
                item54.addPregunta(pregunta542);
                item54.addPregunta(pregunta543);
                item54.addPregunta(pregunta544);
                item54.addPregunta(pregunta545);

                ese5.addItem(item54);

//                      QUINTO ITEM
                Item item55 = realm.createObject(Item.class);
                item55.setCriterio(context.getResources().getString(R.string.criterio55C));
                item55.setTextoItem(context.getResources().getString(R.string.texto55C));
                item55.setIdItem(5);
                item55.setPuntajeItem(0.0);

                Pregunta pregunta551 = realm.createObject(Pregunta.class);
                pregunta551.setIdPregunta(551);
                pregunta551.setPuntaje(null);
                pregunta551.setTextoPregunta(context.getResources().getString(R.string.textoPregunta551C));
                pregunta551.setIdItem(55);
                Pregunta pregunta552 = realm.createObject(Pregunta.class);
                pregunta552.setIdPregunta(552);
                pregunta552.setPuntaje(null);
                pregunta552.setTextoPregunta(context.getResources().getString(R.string.textoPregunta552C));
                pregunta552.setIdItem(55);
                item55.addPregunta(pregunta551);
                item55.addPregunta(pregunta552);

                ese5.addItem(item55);
                nuevoCuestionario.addEse(ese5);

                for (Ese unaEses :
                        nuevoCuestionario.getListaEses()) {
                    unaEses.setIdCuestionario(nuevoCuestionario.getIdCuestionario());
                    for (Item unItem :
                            unaEses.getListaItem()) {
                        unItem.setIdEse(unaEses.getIdEse());
                        unItem.setIdCuestionario(nuevoCuestionario.getIdCuestionario());
                        for (Pregunta unaPregunta :
                                unItem.getListaPreguntas()) {
                            unaPregunta.setIdItem(unItem.getIdItem());
                            unaPregunta.setIdCuestioniario(nuevoCuestionario.getIdCuestionario());
                            unaPregunta.setIdEse(unaEses.getIdEse());
                        }
                    }
                }
            }

        });
        //endregion
    }

    public void crearNuevoCuestionario(final String nombreCuestionario){
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<Cuestionario> listaCuestionarios = realm.where(Cuestionario.class)
                        .findAll();
                if (listaCuestionarios!=null){

                    Cuestionario nuevoCuestionario = realm.createObject(Cuestionario.class,"cues_"+UUID.randomUUID());
                    nuevoCuestionario.setNombreCuestionario(nombreCuestionario);
                    nuevoCuestionario.setListaEses(new RealmList<Ese>());
                    for(int i=0; i<5; i++){
                        Ese unaEse= new Ese();
                        unaEse.setPuntajeEse(0.0);
                        unaEse.setIdEse(nuevoCuestionario.getListaEses().size()+1);
                        nuevoCuestionario.addEse(unaEse);
                    }
                }
            }
        });
    }

    public void eliminarCuestionario(final String idCuestionario){

        FuncionesPublicas.eliminarCuestionario(idCuestionario,context);

    }



}
