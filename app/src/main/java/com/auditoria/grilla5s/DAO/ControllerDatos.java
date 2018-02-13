package com.auditoria.grilla5s.DAO;

import android.content.Context;

import com.auditoria.grilla5s.Model.Auditoria;
import com.auditoria.grilla5s.Model.Ese;
import com.auditoria.grilla5s.Model.Item;
import com.auditoria.grilla5s.Model.Pregunta;
import com.auditoria.grilla5s.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.UUID;

import io.realm.Realm;


/**
 * Created by elmar on 9/2/2018.
 */

public class ControllerDatos {

    private Context context;
    private String idAuditInstanciada;

    public ControllerDatos(Context context) {
        this.context = context;
    }

    public String instanciarAuditoria(){


        //ASIGNO ID, FECHA Y ULTIMA AUDIT
            Realm realm=Realm.getDefaultInstance();
            realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
            Auditoria nuevaAuditoria = realm.createObject(Auditoria.class);
                nuevaAuditoria.setIdAuditoria("Audit_" + UUID.randomUUID());
                idAuditInstanciada=nuevaAuditoria.getIdAuditoria();
                nuevaAuditoria.setFechaAuditoria(determinarFecha());
                nuevaAuditoria.setEsUltimaAuditoria(false);
                    
//                    PRIMER ESE
                
            Ese ese1= realm.createObject(Ese.class);
                ese1.setIdAudit(nuevaAuditoria.getIdAuditoria());
                ese1.setIdEse("1");
                ese1.setPuntajeEse(0.0);

//                        PRIMER ITEM
                Item item11 = realm.createObject(Item.class);
                item11.setCriterio(context.getResources().getString(R.string.criterio1_1S));
                item11.setTextoItem(context.getResources().getString(R.string.textoItem1_1S));
                item11.setIdItem("11");
                item11.setPuntajeItem(0.0);
                item11.setIdAudit(nuevaAuditoria.getIdAuditoria());

                Pregunta pregunta111 = realm.createObject(Pregunta.class);
                pregunta111.setIdAudit(nuevaAuditoria.getIdAuditoria());
                pregunta111.setIdPregunta("111");
                pregunta111.setPuntaje(0);
                pregunta111.setTextoPregunta(context.getResources().getString(R.string.textoPregunta111));

                Pregunta pregunta112 = realm.createObject(Pregunta.class);
                pregunta112.setIdAudit(nuevaAuditoria.getIdAuditoria());
                pregunta112.setIdPregunta("112");
                pregunta112.setPuntaje(0);
                pregunta112.setTextoPregunta(context.getResources().getString(R.string.textoPregunta112));

                Pregunta pregunta113 = realm.createObject(Pregunta.class);
                pregunta113.setIdAudit(nuevaAuditoria.getIdAuditoria());
                pregunta113.setIdPregunta("113");
                pregunta113.setPuntaje(0);
                pregunta113.setTextoPregunta(context.getResources().getString(R.string.textoPregunta113));

                Pregunta pregunta114 = realm.createObject(Pregunta.class);
                pregunta114.setIdAudit(nuevaAuditoria.getIdAuditoria());
                pregunta114.setIdPregunta("114");
                pregunta114.setPuntaje(0);
                pregunta114.setTextoPregunta(context.getResources().getString(R.string.textoPregunta114));

                Pregunta pregunta115 = realm.createObject(Pregunta.class);
                pregunta115.setIdAudit(nuevaAuditoria.getIdAuditoria());
                pregunta115.setIdPregunta("115");
                pregunta115.setPuntaje(0);
                pregunta115.setTextoPregunta(context.getResources().getString(R.string.textoPregunta115));

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
                item12.setIdItem("12");
                item12.setPuntajeItem(0.0);
                item12.setIdAudit(nuevaAuditoria.getIdAuditoria());

                Pregunta pregunta121 = realm.createObject(Pregunta.class);
                pregunta121.setIdAudit(nuevaAuditoria.getIdAuditoria());
                pregunta121.setIdPregunta("121");
                pregunta121.setPuntaje(0);
                pregunta121.setTextoPregunta(context.getResources().getString(R.string.textoPregunta121));

                Pregunta pregunta122 = realm.createObject(Pregunta.class);
                pregunta122.setIdAudit(nuevaAuditoria.getIdAuditoria());
                pregunta122.setIdPregunta("122");
                pregunta122.setPuntaje(0);
                pregunta122.setTextoPregunta(context.getResources().getString(R.string.textoPregunta122));

                Pregunta pregunta123 = realm.createObject(Pregunta.class);
                pregunta123.setIdAudit(nuevaAuditoria.getIdAuditoria());
                pregunta123.setIdPregunta("123");
                pregunta123.setPuntaje(0);
                pregunta123.setTextoPregunta(context.getResources().getString(R.string.textoPregunta123));

                Pregunta pregunta124 = realm.createObject(Pregunta.class);
                pregunta124.setIdAudit(nuevaAuditoria.getIdAuditoria());
                pregunta124.setIdPregunta("124");
                pregunta124.setPuntaje(0);
                pregunta124.setTextoPregunta(context.getResources().getString(R.string.textoPregunta124));

                item12.addPregunta(pregunta121);
                item12.addPregunta(pregunta122);
                item12.addPregunta(pregunta123);
                item12.addPregunta(pregunta124);

                ese1.addItem(item12);

//                      TERCER ITEM
                Item item13 = realm.createObject(Item.class);
                item13.setCriterio(context.getResources().getString(R.string.criterio13));
                item13.setTextoItem(context.getResources().getString(R.string.texto13));
                item13.setIdItem("13");
                item13.setPuntajeItem(0.0);
                item13.setIdAudit(nuevaAuditoria.getIdAuditoria());

                Pregunta pregunta131 = realm.createObject(Pregunta.class);
                pregunta131.setIdAudit(nuevaAuditoria.getIdAuditoria());
                pregunta131.setIdPregunta("131");
                pregunta131.setPuntaje(0);
                pregunta131.setTextoPregunta(context.getResources().getString(R.string.textoPregunta131));

                Pregunta pregunta132 = realm.createObject(Pregunta.class);
                pregunta132.setIdAudit(nuevaAuditoria.getIdAuditoria());
                pregunta132.setIdPregunta("132");
                pregunta132.setPuntaje(0);
                pregunta132.setTextoPregunta(context.getResources().getString(R.string.textoPregunta132));

                item13.addPregunta(pregunta131);
                item13.addPregunta(pregunta132);

                ese1.addItem(item13);

//                      CUARTO ITEM
                Item item14 = realm.createObject(Item.class);
                    item14.setCriterio(context.getResources().getString(R.string.criterio14));
                    item14.setTextoItem(context.getResources().getString(R.string.texto14));
                    item14.setIdItem("14");
                    item14.setPuntajeItem(0.0);
                    item14.setIdAudit(nuevaAuditoria.getIdAuditoria());
    
                    Pregunta pregunta141 = realm.createObject(Pregunta.class);
                        pregunta141.setIdAudit(nuevaAuditoria.getIdAuditoria());
                        pregunta141.setIdPregunta("141");
                        pregunta141.setPuntaje(0);
                        pregunta141.setTextoPregunta(context.getResources().getString(R.string.textoPregunta141));
    
                    Pregunta pregunta142 = realm.createObject(Pregunta.class);
                        pregunta142.setIdAudit(nuevaAuditoria.getIdAuditoria());
                        pregunta142.setIdPregunta("142");
                        pregunta142.setPuntaje(0);
                        pregunta142.setTextoPregunta(context.getResources().getString(R.string.textoPregunta142));
    
                    Pregunta pregunta143 = realm.createObject(Pregunta.class);
                        pregunta143.setIdAudit(nuevaAuditoria.getIdAuditoria());
                        pregunta143.setIdPregunta("143");
                        pregunta143.setPuntaje(0);
                        pregunta143.setTextoPregunta(context.getResources().getString(R.string.textoPregunta143));
    
                    Pregunta pregunta144 = realm.createObject(Pregunta.class);
                        pregunta144.setIdAudit(nuevaAuditoria.getIdAuditoria());
                        pregunta144.setIdPregunta("144");
                        pregunta144.setPuntaje(0);
                        pregunta144.setTextoPregunta(context.getResources().getString(R.string.textoPregunta144));
    
                    item14.addPregunta(pregunta141);
                    item14.addPregunta(pregunta142);
                    item14.addPregunta(pregunta143);
                    item14.addPregunta(pregunta144);
    
                    ese1.addItem(item14);

//                      QUINTO ITEM
                Item item15 = realm.createObject(Item.class);
                    item15.setCriterio(context.getResources().getString(R.string.criterio12));
                    item15.setTextoItem(context.getResources().getString(R.string.texto12));
                    item15.setIdItem("1S1I");
                    item15.setPuntajeItem(0.0);
                    item15.setIdAudit(nuevaAuditoria.getIdAuditoria());

                    Pregunta pregunta151 = realm.createObject(Pregunta.class);
                        pregunta151.setIdAudit(nuevaAuditoria.getIdAuditoria());
                        pregunta151.setIdPregunta("151");
                        pregunta151.setPuntaje(0);
                        pregunta151.setTextoPregunta(context.getResources().getString(R.string.textoPregunta151));
    
                    Pregunta pregunta152 = realm.createObject(Pregunta.class);
                        pregunta152.setIdAudit(nuevaAuditoria.getIdAuditoria());
                        pregunta152.setIdPregunta("152");
                        pregunta152.setPuntaje(0);
                        pregunta152.setTextoPregunta(context.getResources().getString(R.string.textoPregunta152));

                    item15.addPregunta(pregunta151);
                    item15.addPregunta(pregunta152);
    
                    ese1.addItem(item15);
                nuevaAuditoria.addEse(ese1);
                
//                SEGUNDA ESE

                Ese ese2= realm.createObject(Ese.class);
                ese2.setIdAudit(nuevaAuditoria.getIdAuditoria());
                ese2.setIdEse("2");
                ese2.setPuntajeEse(0.0);

//                        PRIMER ITEM
                Item item21 = realm.createObject(Item.class);
                    item21.setCriterio(context.getResources().getString(R.string.criterio21));
                    item21.setTextoItem(context.getResources().getString(R.string.textoItem21));
                    item21.setIdItem("21");
                    item21.setPuntajeItem(0.0);
                    item21.setIdAudit(nuevaAuditoria.getIdAuditoria());

                        Pregunta pregunta211 = realm.createObject(Pregunta.class);
                            pregunta211.setIdAudit(nuevaAuditoria.getIdAuditoria());
                            pregunta211.setIdPregunta("211");
                            pregunta211.setPuntaje(0);
                            pregunta211.setTextoPregunta(context.getResources().getString(R.string.textoPregunta211));
        
                        Pregunta pregunta212 = realm.createObject(Pregunta.class);
                            pregunta212.setIdAudit(nuevaAuditoria.getIdAuditoria());
                            pregunta212.setIdPregunta("212");
                            pregunta212.setPuntaje(0);
                            pregunta212.setTextoPregunta(context.getResources().getString(R.string.textoPregunta212));
        
                        Pregunta pregunta213 = realm.createObject(Pregunta.class);
                            pregunta213.setIdAudit(nuevaAuditoria.getIdAuditoria());
                            pregunta213.setIdPregunta("213");
                            pregunta213.setPuntaje(0);
                            pregunta213.setTextoPregunta(context.getResources().getString(R.string.textoPregunta213));
        
                        Pregunta pregunta214 = realm.createObject(Pregunta.class);
                            pregunta214.setIdAudit(nuevaAuditoria.getIdAuditoria());
                            pregunta214.setIdPregunta("214");
                            pregunta214.setPuntaje(0);
                            pregunta214.setTextoPregunta(context.getResources().getString(R.string.textoPregunta214));
        
                        Pregunta pregunta215 = realm.createObject(Pregunta.class);
                            pregunta215.setIdAudit(nuevaAuditoria.getIdAuditoria());
                            pregunta215.setIdPregunta("215");
                            pregunta215.setPuntaje(0);
                            pregunta215.setTextoPregunta(context.getResources().getString(R.string.textoPregunta215));
        
                        Pregunta pregunta216 = realm.createObject(Pregunta.class);
                            pregunta216.setIdAudit(nuevaAuditoria.getIdAuditoria());
                            pregunta216.setIdPregunta("216");
                            pregunta216.setPuntaje(0);
                            pregunta216.setTextoPregunta(context.getResources().getString(R.string.textoPregunta216));
        
                        Pregunta pregunta217 = realm.createObject(Pregunta.class);
                            pregunta217.setIdAudit(nuevaAuditoria.getIdAuditoria());
                            pregunta217.setIdPregunta("217");
                            pregunta217.setPuntaje(0);
                            pregunta217.setTextoPregunta(context.getResources().getString(R.string.textoPregunta217));
        
                        Pregunta pregunta218 = realm.createObject(Pregunta.class);
                            pregunta218.setIdAudit(nuevaAuditoria.getIdAuditoria());
                            pregunta218.setIdPregunta("218");
                            pregunta218.setPuntaje(0);
                            pregunta218.setTextoPregunta(context.getResources().getString(R.string.textoPregunta218));

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
                    item22.setIdItem("22");
                    item22.setPuntajeItem(0.0);
                    item22.setIdAudit(nuevaAuditoria.getIdAuditoria());
    
                        Pregunta pregunta221 = realm.createObject(Pregunta.class);
                            pregunta221.setIdAudit(nuevaAuditoria.getIdAuditoria());
                            pregunta221.setIdPregunta("221");
                            pregunta221.setPuntaje(0);
                            pregunta221.setTextoPregunta(context.getResources().getString(R.string.textoPregunta221));
            
                        Pregunta pregunta222 = realm.createObject(Pregunta.class);
                            pregunta222.setIdAudit(nuevaAuditoria.getIdAuditoria());
                            pregunta222.setIdPregunta("222");
                            pregunta222.setPuntaje(0);
                            pregunta222.setTextoPregunta(context.getResources().getString(R.string.textoPregunta222));
            
                        Pregunta pregunta223 = realm.createObject(Pregunta.class);
                            pregunta223.setIdAudit(nuevaAuditoria.getIdAuditoria());
                            pregunta223.setIdPregunta("223");
                            pregunta223.setPuntaje(0);
                            pregunta223.setTextoPregunta(context.getResources().getString(R.string.textoPregunta223));
            
                        Pregunta pregunta224 = realm.createObject(Pregunta.class);
                            pregunta224.setIdAudit(nuevaAuditoria.getIdAuditoria());
                            pregunta224.setIdPregunta("224");
                            pregunta224.setPuntaje(0);
                            pregunta224.setTextoPregunta(context.getResources().getString(R.string.textoPregunta224));
    
                    item22.addPregunta(pregunta221);
                    item22.addPregunta(pregunta222);
                    item22.addPregunta(pregunta223);
                    item22.addPregunta(pregunta224);
    
                    ese2.addItem(item22);

//                      TERCER ITEM
                Item item23 = realm.createObject(Item.class);
                    item23.setCriterio(context.getResources().getString(R.string.criterio23));
                    item23.setTextoItem(context.getResources().getString(R.string.texto23));
                    item23.setIdItem("23");
                    item23.setPuntajeItem(0.0);
                    item23.setIdAudit(nuevaAuditoria.getIdAuditoria());
    
                        Pregunta pregunta231 = realm.createObject(Pregunta.class);
                            pregunta231.setIdAudit(nuevaAuditoria.getIdAuditoria());
                            pregunta231.setIdPregunta("231");
                            pregunta231.setPuntaje(0);
                            pregunta231.setTextoPregunta(context.getResources().getString(R.string.textoPregunta231));
        
                        Pregunta pregunta232 = realm.createObject(Pregunta.class);
                            pregunta232.setIdAudit(nuevaAuditoria.getIdAuditoria());
                            pregunta232.setIdPregunta("232");
                            pregunta232.setPuntaje(0);
                            pregunta232.setTextoPregunta(context.getResources().getString(R.string.textoPregunta232));
        
                        Pregunta pregunta233 = realm.createObject(Pregunta.class);
                            pregunta233.setIdAudit(nuevaAuditoria.getIdAuditoria());
                            pregunta233.setIdPregunta("233");
                            pregunta233.setPuntaje(0);
                            pregunta233.setTextoPregunta(context.getResources().getString(R.string.textoPregunta233));
        
                        Pregunta pregunta234 = realm.createObject(Pregunta.class);
                            pregunta234.setIdAudit(nuevaAuditoria.getIdAuditoria());
                            pregunta234.setIdPregunta("234");
                            pregunta234.setPuntaje(0);
                            pregunta234.setTextoPregunta(context.getResources().getString(R.string.textoPregunta234));
        
                        Pregunta pregunta235 = realm.createObject(Pregunta.class);
                            pregunta235.setIdAudit(nuevaAuditoria.getIdAuditoria());
                            pregunta235.setIdPregunta("235");
                            pregunta235.setPuntaje(0);
                            pregunta235.setTextoPregunta(context.getResources().getString(R.string.textoPregunta235));
        
                        Pregunta pregunta236 = realm.createObject(Pregunta.class);
                            pregunta236.setIdAudit(nuevaAuditoria.getIdAuditoria());
                            pregunta236.setIdPregunta("236");
                            pregunta236.setPuntaje(0);
                            pregunta236.setTextoPregunta(context.getResources().getString(R.string.textoPregunta236));

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
                    item24.setIdItem("24");
                    item24.setPuntajeItem(0.0);
                    item24.setIdAudit(nuevaAuditoria.getIdAuditoria());
    
                        Pregunta pregunta241 = realm.createObject(Pregunta.class);
                            pregunta241.setIdAudit(nuevaAuditoria.getIdAuditoria());
                            pregunta241.setIdPregunta("241");
                            pregunta241.setPuntaje(0);
                            pregunta241.setTextoPregunta(context.getResources().getString(R.string.textoPregunta241));
        
                        Pregunta pregunta242 = realm.createObject(Pregunta.class);
                            pregunta242.setIdAudit(nuevaAuditoria.getIdAuditoria());
                            pregunta242.setIdPregunta("242");
                            pregunta242.setPuntaje(0);
                            pregunta242.setTextoPregunta(context.getResources().getString(R.string.textoPregunta242));
        
                        Pregunta pregunta243 = realm.createObject(Pregunta.class);
                            pregunta243.setIdAudit(nuevaAuditoria.getIdAuditoria());
                            pregunta243.setIdPregunta("243");
                            pregunta243.setPuntaje(0);
                            pregunta243.setTextoPregunta(context.getResources().getString(R.string.textoPregunta243));
        
                        Pregunta pregunta244 = realm.createObject(Pregunta.class);
                            pregunta244.setIdAudit(nuevaAuditoria.getIdAuditoria());
                            pregunta244.setIdPregunta("244");
                            pregunta244.setPuntaje(0);
                            pregunta244.setTextoPregunta(context.getResources().getString(R.string.textoPregunta244));
        
                        Pregunta pregunta245 = realm.createObject(Pregunta.class);
                            pregunta245.setIdAudit(nuevaAuditoria.getIdAuditoria());
                            pregunta245.setIdPregunta("245");
                            pregunta245.setPuntaje(0);
                            pregunta245.setTextoPregunta(context.getResources().getString(R.string.textoPregunta245));
        
                        Pregunta pregunta246 = realm.createObject(Pregunta.class);
                            pregunta246.setIdAudit(nuevaAuditoria.getIdAuditoria());
                            pregunta246.setIdPregunta("246");
                            pregunta246.setPuntaje(0);
                            pregunta246.setTextoPregunta(context.getResources().getString(R.string.textoPregunta246));
                
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
                    item25.setIdItem("25");
                    item25.setPuntajeItem(0.0);
                    item25.setIdAudit(nuevaAuditoria.getIdAuditoria());

                        Pregunta pregunta251 = realm.createObject(Pregunta.class);
                            pregunta251.setIdAudit(nuevaAuditoria.getIdAuditoria());
                            pregunta251.setIdPregunta("251");
                            pregunta251.setPuntaje(0);
                            pregunta251.setTextoPregunta(context.getResources().getString(R.string.textoPregunta251));

                        Pregunta pregunta252 = realm.createObject(Pregunta.class);
                            pregunta252.setIdAudit(nuevaAuditoria.getIdAuditoria());
                            pregunta252.setIdPregunta("252");
                            pregunta252.setPuntaje(0);
                            pregunta252.setTextoPregunta(context.getResources().getString(R.string.textoPregunta252));

                        Pregunta pregunta253 = realm.createObject(Pregunta.class);
                            pregunta253.setIdAudit(nuevaAuditoria.getIdAuditoria());
                            pregunta253.setIdPregunta("253");
                            pregunta253.setPuntaje(0);
                            pregunta253.setTextoPregunta(context.getResources().getString(R.string.textoPregunta253));

                        Pregunta pregunta254 = realm.createObject(Pregunta.class);
                            pregunta254.setIdAudit(nuevaAuditoria.getIdAuditoria());
                            pregunta254.setIdPregunta("254");
                            pregunta254.setPuntaje(0);
                            pregunta254.setTextoPregunta(context.getResources().getString(R.string.textoPregunta254));

                    item25.addPregunta(pregunta251);
                    item25.addPregunta(pregunta252);
                    item25.addPregunta(pregunta253);
                    item25.addPregunta(pregunta254);

                ese2.addItem(item25);
                nuevaAuditoria.addEse(ese2);

//                TERCERA ESE

                Ese ese3= realm.createObject(Ese.class);
                ese3.setIdAudit(nuevaAuditoria.getIdAuditoria());
                ese3.setIdEse("3");
                ese3.setPuntajeEse(0.0);

//                        PRIMER ITEM
                Item item31 = realm.createObject(Item.class);
                    item31.setCriterio(context.getResources().getString(R.string.criterio31));
                    item31.setTextoItem(context.getResources().getString(R.string.textoitem31));
                    item31.setIdItem("31");
                    item31.setPuntajeItem(0.0);
                    item31.setIdAudit(nuevaAuditoria.getIdAuditoria());

                        Pregunta pregunta311 = realm.createObject(Pregunta.class);
                            pregunta311.setIdAudit(nuevaAuditoria.getIdAuditoria());
                            pregunta311.setIdPregunta("311");
                            pregunta311.setPuntaje(0);
                            pregunta311.setTextoPregunta(context.getResources().getString(R.string.textoPregunta311));

                        Pregunta pregunta312 = realm.createObject(Pregunta.class);
                            pregunta312.setIdAudit(nuevaAuditoria.getIdAuditoria());
                            pregunta312.setIdPregunta("312");
                            pregunta312.setPuntaje(0);
                            pregunta312.setTextoPregunta(context.getResources().getString(R.string.textoPregunta312));

                        Pregunta pregunta313 = realm.createObject(Pregunta.class);
                            pregunta313.setIdAudit(nuevaAuditoria.getIdAuditoria());
                            pregunta313.setIdPregunta("313");
                            pregunta313.setPuntaje(0);
                            pregunta313.setTextoPregunta(context.getResources().getString(R.string.textoPregunta313));

                    item31.addPregunta(pregunta311);
                    item31.addPregunta(pregunta312);
                    item31.addPregunta(pregunta313);

                ese3.addItem(item31);

//                      SEGUNDO ITEM
                Item item32 = realm.createObject(Item.class);
                    item32.setCriterio(context.getResources().getString(R.string.criterio32));
                    item32.setTextoItem(context.getResources().getString(R.string.texto32));
                    item32.setIdItem("32");
                    item32.setPuntajeItem(0.0);
                    item32.setIdAudit(nuevaAuditoria.getIdAuditoria());

                        Pregunta pregunta321 = realm.createObject(Pregunta.class);
                            pregunta321.setIdAudit(nuevaAuditoria.getIdAuditoria());
                            pregunta321.setIdPregunta("321");
                            pregunta321.setPuntaje(0);
                            pregunta321.setTextoPregunta(context.getResources().getString(R.string.textoPregunta321));

                        Pregunta pregunta322 = realm.createObject(Pregunta.class);
                            pregunta322.setIdAudit(nuevaAuditoria.getIdAuditoria());
                            pregunta322.setIdPregunta("322");
                            pregunta322.setPuntaje(0);
                            pregunta322.setTextoPregunta(context.getResources().getString(R.string.textoPregunta322));

                        Pregunta pregunta323 = realm.createObject(Pregunta.class);
                            pregunta323.setIdAudit(nuevaAuditoria.getIdAuditoria());
                            pregunta323.setIdPregunta("323");
                            pregunta323.setPuntaje(0);
                            pregunta323.setTextoPregunta(context.getResources().getString(R.string.textoPregunta323));

                        Pregunta pregunta324 = realm.createObject(Pregunta.class);
                        pregunta324.setIdAudit(nuevaAuditoria.getIdAuditoria());
                            pregunta324.setIdPregunta("324");
                            pregunta324.setPuntaje(0);
                            pregunta324.setTextoPregunta(context.getResources().getString(R.string.textoPregunta324));

                        Pregunta pregunta325 = realm.createObject(Pregunta.class);
                            pregunta325.setIdAudit(nuevaAuditoria.getIdAuditoria());
                            pregunta325.setIdPregunta("325");
                            pregunta325.setPuntaje(0);
                            pregunta325.setTextoPregunta(context.getResources().getString(R.string.textoPregunta325));

                    item32.addPregunta(pregunta321);
                    item32.addPregunta(pregunta322);
                    item32.addPregunta(pregunta323);
                    item32.addPregunta(pregunta324);
                    item32.addPregunta(pregunta324);

                ese3.addItem(item32);

//                      TERCER ITEM
                Item item33 = realm.createObject(Item.class);
                    item33.setCriterio(context.getResources().getString(R.string.criterio33));
                    item33.setTextoItem(context.getResources().getString(R.string.texto33));
                    item33.setIdItem("33");
                    item33.setPuntajeItem(0.0);
                    item33.setIdAudit(nuevaAuditoria.getIdAuditoria());

                        Pregunta pregunta331 = realm.createObject(Pregunta.class);
                            pregunta331.setIdAudit(nuevaAuditoria.getIdAuditoria());
                            pregunta331.setIdPregunta("331");
                            pregunta331.setPuntaje(0);
                            pregunta331.setTextoPregunta(context.getResources().getString(R.string.textoPregunta331));

                        Pregunta pregunta332 = realm.createObject(Pregunta.class);
                            pregunta332.setIdAudit(nuevaAuditoria.getIdAuditoria());
                            pregunta332.setIdPregunta("332");
                            pregunta332.setPuntaje(0);
                            pregunta332.setTextoPregunta(context.getResources().getString(R.string.textoPregunta332));

                        Pregunta pregunta333 = realm.createObject(Pregunta.class);
                            pregunta333.setIdAudit(nuevaAuditoria.getIdAuditoria());
                            pregunta333.setIdPregunta("333");
                            pregunta333.setPuntaje(0);
                            pregunta333.setTextoPregunta(context.getResources().getString(R.string.textoPregunta333));

                        Pregunta pregunta334 = realm.createObject(Pregunta.class);
                            pregunta334.setIdAudit(nuevaAuditoria.getIdAuditoria());
                            pregunta334.setIdPregunta("334");
                            pregunta334.setPuntaje(0);
                            pregunta334.setTextoPregunta(context.getResources().getString(R.string.textoPregunta334));

                        Pregunta pregunta335 = realm.createObject(Pregunta.class);
                            pregunta335.setIdAudit(nuevaAuditoria.getIdAuditoria());
                            pregunta335.setIdPregunta("335");
                            pregunta335.setPuntaje(0);
                            pregunta335.setTextoPregunta(context.getResources().getString(R.string.textoPregunta335));

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
                    item34.setIdItem("34");
                    item34.setPuntajeItem(0.0);
                    item34.setIdAudit(nuevaAuditoria.getIdAuditoria());

                        Pregunta pregunta341 = realm.createObject(Pregunta.class);
                            pregunta341.setIdAudit(nuevaAuditoria.getIdAuditoria());
                            pregunta341.setIdPregunta("341");
                            pregunta341.setPuntaje(0);
                            pregunta341.setTextoPregunta(context.getResources().getString(R.string.textoPregunta341));

                        Pregunta pregunta342 = realm.createObject(Pregunta.class);
                            pregunta342.setIdAudit(nuevaAuditoria.getIdAuditoria());
                            pregunta342.setIdPregunta("342");
                            pregunta342.setPuntaje(0);
                            pregunta342.setTextoPregunta(context.getResources().getString(R.string.textoPregunta342));

                        Pregunta pregunta343 = realm.createObject(Pregunta.class);
                            pregunta343.setIdAudit(nuevaAuditoria.getIdAuditoria());
                            pregunta343.setIdPregunta("343");
                            pregunta343.setPuntaje(0);
                            pregunta343.setTextoPregunta(context.getResources().getString(R.string.textoPregunta343));

                        Pregunta pregunta344 = realm.createObject(Pregunta.class);
                            pregunta344.setIdAudit(nuevaAuditoria.getIdAuditoria());
                            pregunta344.setIdPregunta("344");
                            pregunta344.setPuntaje(0);
                            pregunta344.setTextoPregunta(context.getResources().getString(R.string.textoPregunta344));

                    item34.addPregunta(pregunta341);
                    item34.addPregunta(pregunta342);
                    item34.addPregunta(pregunta343);
                    item34.addPregunta(pregunta344);

                ese3.addItem(item34);

//                      QUINTO ITEM
                Item item35 = realm.createObject(Item.class);
                    item35.setCriterio(context.getResources().getString(R.string.criterio35));
                    item35.setTextoItem(context.getResources().getString(R.string.texto35));
                    item35.setIdItem("35");
                    item35.setPuntajeItem(0.0);
                    item35.setIdAudit(nuevaAuditoria.getIdAuditoria());

                        Pregunta pregunta351 = realm.createObject(Pregunta.class);
                            pregunta351.setIdAudit(nuevaAuditoria.getIdAuditoria());
                            pregunta351.setIdPregunta("351");
                            pregunta351.setPuntaje(0);
                            pregunta351.setTextoPregunta(context.getResources().getString(R.string.textoPregunta351));

                        Pregunta pregunta352 = realm.createObject(Pregunta.class);
                            pregunta352.setIdAudit(nuevaAuditoria.getIdAuditoria());
                            pregunta352.setIdPregunta("352");
                            pregunta352.setPuntaje(0);
                            pregunta352.setTextoPregunta(context.getResources().getString(R.string.textoPregunta352));

                        Pregunta pregunta353 = realm.createObject(Pregunta.class);
                            pregunta353.setIdAudit(nuevaAuditoria.getIdAuditoria());
                            pregunta353.setIdPregunta("353");
                            pregunta353.setPuntaje(0);
                            pregunta353.setTextoPregunta(context.getResources().getString(R.string.textoPregunta353));

                        Pregunta pregunta354 = realm.createObject(Pregunta.class);
                            pregunta354.setIdAudit(nuevaAuditoria.getIdAuditoria());
                            pregunta354.setIdPregunta("354");
                            pregunta354.setPuntaje(0);
                            pregunta354.setTextoPregunta(context.getResources().getString(R.string.textoPregunta354));

                    item35.addPregunta(pregunta351);
                    item35.addPregunta(pregunta352);
                    item35.addPregunta(pregunta353);
                    item35.addPregunta(pregunta354);

                ese3.addItem(item35);
                nuevaAuditoria.addEse(ese3);

                //                CUARTA ESE

                Ese ese4= realm.createObject(Ese.class);
                ese4.setIdAudit(nuevaAuditoria.getIdAuditoria());
                ese4.setIdEse("4");
                ese4.setPuntajeEse(0.0);

//                        PRIMER ITEM
                Item item41 = realm.createObject(Item.class);
                    item41.setCriterio(context.getResources().getString(R.string.criterio41));
                    item41.setTextoItem(context.getResources().getString(R.string.textoitem41));
                    item41.setIdItem("41");
                    item41.setPuntajeItem(0.0);
                    item41.setIdAudit(nuevaAuditoria.getIdAuditoria());

                        Pregunta pregunta411 = realm.createObject(Pregunta.class);
                            pregunta411.setIdAudit(nuevaAuditoria.getIdAuditoria());
                            pregunta411.setIdPregunta("411");
                            pregunta411.setPuntaje(0);
                            pregunta411.setTextoPregunta(context.getResources().getString(R.string.textoPregunta411));

                        Pregunta pregunta412 = realm.createObject(Pregunta.class);
                            pregunta412.setIdAudit(nuevaAuditoria.getIdAuditoria());
                            pregunta412.setIdPregunta("412");
                            pregunta412.setPuntaje(0);
                            pregunta412.setTextoPregunta(context.getResources().getString(R.string.textoPregunta412));

                        Pregunta pregunta413 = realm.createObject(Pregunta.class);
                            pregunta413.setIdAudit(nuevaAuditoria.getIdAuditoria());
                            pregunta413.setIdPregunta("413");
                            pregunta413.setPuntaje(0);
                            pregunta413.setTextoPregunta(context.getResources().getString(R.string.textoPregunta413));

                        Pregunta pregunta414 = realm.createObject(Pregunta.class);
                            pregunta414.setIdAudit(nuevaAuditoria.getIdAuditoria());
                            pregunta414.setIdPregunta("414");
                            pregunta414.setPuntaje(0);
                            pregunta414.setTextoPregunta(context.getResources().getString(R.string.textoPregunta414));

                        Pregunta pregunta415 = realm.createObject(Pregunta.class);
                            pregunta415.setIdAudit(nuevaAuditoria.getIdAuditoria());
                            pregunta415.setIdPregunta("415");
                            pregunta415.setPuntaje(0);
                            pregunta415.setTextoPregunta(context.getResources().getString(R.string.textoPregunta415));


                    item41.addPregunta(pregunta411);
                    item41.addPregunta(pregunta412);
                    item41.addPregunta(pregunta413);
                    item41.addPregunta(pregunta414);
                    item41.addPregunta(pregunta415);

                ese4.addItem(item41);

//                      SEGUNDO ITEM
                Item item42 = realm.createObject(Item.class);
                    item42.setCriterio(context.getResources().getString(R.string.criterio42));
                    item42.setTextoItem(context.getResources().getString(R.string.textoitem42));
                    item42.setIdItem("42");
                    item42.setPuntajeItem(0.0);
                    item42.setIdAudit(nuevaAuditoria.getIdAuditoria());

                        Pregunta pregunta421 = realm.createObject(Pregunta.class);
                            pregunta421.setIdAudit(nuevaAuditoria.getIdAuditoria());
                            pregunta421.setIdPregunta("421");
                            pregunta421.setPuntaje(0);
                            pregunta421.setTextoPregunta(context.getResources().getString(R.string.textoPregunta421));

                        Pregunta pregunta422 = realm.createObject(Pregunta.class);
                            pregunta422.setIdAudit(nuevaAuditoria.getIdAuditoria());
                            pregunta422.setIdPregunta("422");
                            pregunta422.setPuntaje(0);
                            pregunta422.setTextoPregunta(context.getResources().getString(R.string.textoPregunta422));

                    item42.addPregunta(pregunta421);
                    item42.addPregunta(pregunta422);

                ese4.addItem(item42);

//                      TERCER ITEM

                Item item43 = realm.createObject(Item.class);
                    item43.setCriterio(context.getResources().getString(R.string.criterio43));
                    item43.setTextoItem(context.getResources().getString(R.string.textoitem43));
                    item43.setIdItem("43");
                    item43.setPuntajeItem(0.0);
                    item43.setIdAudit(nuevaAuditoria.getIdAuditoria());

                        Pregunta pregunta431 = realm.createObject(Pregunta.class);
                            pregunta431.setIdAudit(nuevaAuditoria.getIdAuditoria());
                            pregunta431.setIdPregunta("431");
                            pregunta431.setPuntaje(0);
                            pregunta431.setTextoPregunta(context.getResources().getString(R.string.textoPregunta431));

                        Pregunta pregunta432 = realm.createObject(Pregunta.class);
                            pregunta432.setIdAudit(nuevaAuditoria.getIdAuditoria());
                            pregunta432.setIdPregunta("432");
                            pregunta432.setPuntaje(0);
                            pregunta432.setTextoPregunta(context.getResources().getString(R.string.textoPregunta432));

                        Pregunta pregunta433 = realm.createObject(Pregunta.class);
                            pregunta433.setIdAudit(nuevaAuditoria.getIdAuditoria());
                            pregunta433.setIdPregunta("433");
                            pregunta433.setPuntaje(0);
                            pregunta433.setTextoPregunta(context.getResources().getString(R.string.textoPregunta433));


                    item43.addPregunta(pregunta431);
                    item43.addPregunta(pregunta432);
                    item43.addPregunta(pregunta433);


                ese4.addItem(item43);
                

//                      CUARTO ITEM
                Item item44 = realm.createObject(Item.class);
                    item44.setCriterio(context.getResources().getString(R.string.criterio44));
                    item44.setTextoItem(context.getResources().getString(R.string.textoitem44));
                    item44.setIdItem("44");
                    item44.setPuntajeItem(0.0);
                    item44.setIdAudit(nuevaAuditoria.getIdAuditoria());

                        Pregunta pregunta441 = realm.createObject(Pregunta.class);
                            pregunta441.setIdAudit(nuevaAuditoria.getIdAuditoria());
                            pregunta441.setIdPregunta("441");
                            pregunta441.setPuntaje(0);
                            pregunta441.setTextoPregunta(context.getResources().getString(R.string.textoPregunta441));

                        Pregunta pregunta442 = realm.createObject(Pregunta.class);
                            pregunta442.setIdAudit(nuevaAuditoria.getIdAuditoria());
                            pregunta442.setIdPregunta("442");
                            pregunta442.setPuntaje(0);
                            pregunta442.setTextoPregunta(context.getResources().getString(R.string.textoPregunta442));


                    item44.addPregunta(pregunta441);
                    item44.addPregunta(pregunta442);


                ese4.addItem(item44);

//                      QUINTO ITEM
                Item item45 = realm.createObject(Item.class);
                    item45.setCriterio(context.getResources().getString(R.string.criterio45));
                    item45.setTextoItem(context.getResources().getString(R.string.textoitem45));
                    item45.setIdItem("45");
                    item45.setPuntajeItem(0.0);
                    item45.setIdAudit(nuevaAuditoria.getIdAuditoria());

                        Pregunta pregunta451 = realm.createObject(Pregunta.class);
                            pregunta451.setIdAudit(nuevaAuditoria.getIdAuditoria());
                            pregunta451.setIdPregunta("451");
                            pregunta451.setPuntaje(0);
                            pregunta451.setTextoPregunta(context.getResources().getString(R.string.textoPregunta451));

                        Pregunta pregunta452 = realm.createObject(Pregunta.class);
                            pregunta452.setIdAudit(nuevaAuditoria.getIdAuditoria());
                            pregunta452.setIdPregunta("452");
                            pregunta452.setPuntaje(0);
                            pregunta452.setTextoPregunta(context.getResources().getString(R.string.textoPregunta452));

                        Pregunta pregunta453 = realm.createObject(Pregunta.class);
                            pregunta453.setIdAudit(nuevaAuditoria.getIdAuditoria());
                            pregunta453.setIdPregunta("453");
                            pregunta453.setPuntaje(0);
                            pregunta453.setTextoPregunta(context.getResources().getString(R.string.textoPregunta453));

                        Pregunta pregunta454 = realm.createObject(Pregunta.class);
                            pregunta454.setIdAudit(nuevaAuditoria.getIdAuditoria());
                            pregunta454.setIdPregunta("454");
                            pregunta454.setPuntaje(0);
                            pregunta454.setTextoPregunta(context.getResources().getString(R.string.textoPregunta454));

                        Pregunta pregunta455 = realm.createObject(Pregunta.class);
                            pregunta455.setIdAudit(nuevaAuditoria.getIdAuditoria());
                            pregunta455.setIdPregunta("455");
                            pregunta455.setPuntaje(0);
                            pregunta455.setTextoPregunta(context.getResources().getString(R.string.textoPregunta455));

                    item45.addPregunta(pregunta451);
                    item45.addPregunta(pregunta452);
                    item45.addPregunta(pregunta453);
                    item45.addPregunta(pregunta454);
                    item45.addPregunta(pregunta455);
                ese4.addItem(item45);

//                      SEXTO ITEM
                Item item46 = realm.createObject(Item.class);
                    item46.setCriterio(context.getResources().getString(R.string.criterio46));
                    item46.setTextoItem(context.getResources().getString(R.string.textoitem46));
                    item46.setIdItem("46");
                    item46.setPuntajeItem(0.0);
                    item46.setIdAudit(nuevaAuditoria.getIdAuditoria());

                        Pregunta pregunta461 = realm.createObject(Pregunta.class);
                            pregunta461.setIdAudit(nuevaAuditoria.getIdAuditoria());
                            pregunta461.setIdPregunta("461");
                            pregunta461.setPuntaje(0);
                            pregunta461.setTextoPregunta(context.getResources().getString(R.string.textoPregunta461));

                        Pregunta pregunta462 = realm.createObject(Pregunta.class);
                            pregunta462.setIdAudit(nuevaAuditoria.getIdAuditoria());
                            pregunta462.setIdPregunta("462");
                            pregunta462.setPuntaje(0);
                            pregunta462.setTextoPregunta(context.getResources().getString(R.string.textoPregunta462));


                    item46.addPregunta(pregunta461);
                    item46.addPregunta(pregunta462);

                ese4.addItem(item46);
                nuevaAuditoria.addEse(ese4);



                //QUINTA ESE
                Ese ese5= realm.createObject(Ese.class);
                ese5.setIdAudit(nuevaAuditoria.getIdAuditoria());
                ese5.setIdEse("5");
                ese5.setPuntajeEse(0.0);
//                        PRIMER ITEM
                Item item51 = realm.createObject(Item.class);
                    item51.setCriterio(context.getResources().getString(R.string.criterio51));
                    item51.setTextoItem(context.getResources().getString(R.string.textoitem51));
                    item51.setIdItem("51");
                    item51.setPuntajeItem(0.0);
                    item51.setIdAudit(nuevaAuditoria.getIdAuditoria());

                        Pregunta pregunta511 = realm.createObject(Pregunta.class);
                            pregunta511.setIdAudit(nuevaAuditoria.getIdAuditoria());
                            pregunta511.setIdPregunta("511");
                            pregunta511.setPuntaje(0);
                            pregunta511.setTextoPregunta(context.getResources().getString(R.string.textoPregunta511));

                        Pregunta pregunta512 = realm.createObject(Pregunta.class);
                            pregunta512.setIdAudit(nuevaAuditoria.getIdAuditoria());
                            pregunta512.setIdPregunta("512");
                            pregunta512.setPuntaje(0);
                            pregunta512.setTextoPregunta(context.getResources().getString(R.string.textoPregunta512));

                        Pregunta pregunta513 = realm.createObject(Pregunta.class);
                            pregunta513.setIdAudit(nuevaAuditoria.getIdAuditoria());
                            pregunta513.setIdPregunta("513");
                            pregunta513.setPuntaje(0);
                            pregunta513.setTextoPregunta(context.getResources().getString(R.string.textoPregunta513));

                        Pregunta pregunta514 = realm.createObject(Pregunta.class);
                            pregunta514.setIdAudit(nuevaAuditoria.getIdAuditoria());
                            pregunta514.setIdPregunta("514");
                            pregunta514.setPuntaje(0);
                            pregunta514.setTextoPregunta(context.getResources().getString(R.string.textoPregunta514));

                        Pregunta pregunta515 = realm.createObject(Pregunta.class);
                            pregunta515.setIdAudit(nuevaAuditoria.getIdAuditoria());
                            pregunta515.setIdPregunta("515");
                            pregunta515.setPuntaje(0);
                            pregunta515.setTextoPregunta(context.getResources().getString(R.string.textoPregunta515));

                        Pregunta pregunta516 = realm.createObject(Pregunta.class);
                            pregunta516.setIdAudit(nuevaAuditoria.getIdAuditoria());
                            pregunta516.setIdPregunta("516");
                            pregunta516.setPuntaje(0);
                            pregunta516.setTextoPregunta(context.getResources().getString(R.string.textoPregunta516));

                        Pregunta pregunta517 = realm.createObject(Pregunta.class);
                            pregunta517.setIdAudit(nuevaAuditoria.getIdAuditoria());
                            pregunta517.setIdPregunta("517");
                            pregunta517.setPuntaje(0);
                            pregunta517.setTextoPregunta(context.getResources().getString(R.string.textoPregunta517));

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
                    item52.setTextoItem(context.getResources().getString(R.string.textoitem52));
                    item52.setIdItem("52");
                    item52.setPuntajeItem(0.0);
                    item52.setIdAudit(nuevaAuditoria.getIdAuditoria());

                        Pregunta pregunta521 = realm.createObject(Pregunta.class);
                            pregunta521.setIdAudit(nuevaAuditoria.getIdAuditoria());
                            pregunta521.setIdPregunta("521");
                            pregunta521.setPuntaje(0);
                            pregunta521.setTextoPregunta(context.getResources().getString(R.string.textoPregunta521));

                        Pregunta pregunta522 = realm.createObject(Pregunta.class);
                            pregunta522.setIdAudit(nuevaAuditoria.getIdAuditoria());
                            pregunta522.setIdPregunta("522");
                            pregunta522.setPuntaje(0);
                            pregunta522.setTextoPregunta(context.getResources().getString(R.string.textoPregunta522));

                    item52.addPregunta(pregunta521);
                    item52.addPregunta(pregunta522);

                ese5.addItem(item52);

//                      TERCER ITEM

                Item item53 = realm.createObject(Item.class);
                    item53.setCriterio(context.getResources().getString(R.string.criterio53));
                    item53.setTextoItem(context.getResources().getString(R.string.textoitem53));
                    item53.setIdItem("53");
                    item53.setPuntajeItem(0.0);
                    item53.setIdAudit(nuevaAuditoria.getIdAuditoria());

                        Pregunta pregunta531 = realm.createObject(Pregunta.class);
                            pregunta531.setIdAudit(nuevaAuditoria.getIdAuditoria());
                            pregunta531.setIdPregunta("531");
                            pregunta531.setPuntaje(0);
                            pregunta531.setTextoPregunta(context.getResources().getString(R.string.textoPregunta531));

                        Pregunta pregunta532 = realm.createObject(Pregunta.class);
                            pregunta532.setIdAudit(nuevaAuditoria.getIdAuditoria());
                            pregunta532.setIdPregunta("532");
                            pregunta532.setPuntaje(0);
                            pregunta532.setTextoPregunta(context.getResources().getString(R.string.textoPregunta532));

                    item53.addPregunta(pregunta531);
                    item53.addPregunta(pregunta532);

                ese5.addItem(item53);

//                      CUARTO ITEM
                Item item54 = realm.createObject(Item.class);
                    item54.setCriterio(context.getResources().getString(R.string.criterio54));
                    item54.setTextoItem(context.getResources().getString(R.string.textoitem54));
                    item54.setIdItem("54");
                    item54.setPuntajeItem(0.0);
                    item54.setIdAudit(nuevaAuditoria.getIdAuditoria());

                        Pregunta pregunta541 = realm.createObject(Pregunta.class);
                            pregunta541.setIdAudit(nuevaAuditoria.getIdAuditoria());
                            pregunta541.setIdPregunta("541");
                            pregunta541.setPuntaje(0);
                            pregunta541.setTextoPregunta(context.getResources().getString(R.string.textoPregunta541));

                        Pregunta pregunta542 = realm.createObject(Pregunta.class);
                            pregunta542.setIdAudit(nuevaAuditoria.getIdAuditoria());
                            pregunta542.setIdPregunta("542");
                            pregunta542.setPuntaje(0);
                            pregunta542.setTextoPregunta(context.getResources().getString(R.string.textoPregunta542));

                        Pregunta pregunta543 = realm.createObject(Pregunta.class);
                            pregunta543.setIdAudit(nuevaAuditoria.getIdAuditoria());
                            pregunta543.setIdPregunta("543");
                            pregunta543.setPuntaje(0);
                            pregunta543.setTextoPregunta(context.getResources().getString(R.string.textoPregunta543));

                        Pregunta pregunta544 = realm.createObject(Pregunta.class);
                            pregunta544.setIdAudit(nuevaAuditoria.getIdAuditoria());
                            pregunta544.setIdPregunta("544");
                            pregunta544.setPuntaje(0);
                            pregunta544.setTextoPregunta(context.getResources().getString(R.string.textoPregunta544));

                        Pregunta pregunta545 = realm.createObject(Pregunta.class);
                            pregunta545.setIdAudit(nuevaAuditoria.getIdAuditoria());
                            pregunta545.setIdPregunta("545");
                            pregunta545.setPuntaje(0);
                            pregunta545.setTextoPregunta(context.getResources().getString(R.string.textoPregunta545));

                    item54.addPregunta(pregunta541);
                    item54.addPregunta(pregunta542);
                    item54.addPregunta(pregunta543);
                    item54.addPregunta(pregunta544);
                    item54.addPregunta(pregunta545);

                ese5.addItem(item54);

//                      QUINTO ITEM
                Item item55 = realm.createObject(Item.class);
                    item55.setCriterio(context.getResources().getString(R.string.criterio55));
                    item55.setTextoItem(context.getResources().getString(R.string.textoitem55));
                    item55.setIdItem("55");
                    item55.setPuntajeItem(0.0);
                    item55.setIdAudit(nuevaAuditoria.getIdAuditoria());

                        Pregunta pregunta551 = realm.createObject(Pregunta.class);
                            pregunta551.setIdAudit(nuevaAuditoria.getIdAuditoria());
                            pregunta551.setIdPregunta("551");
                            pregunta551.setPuntaje(0);
                            pregunta551.setTextoPregunta(context.getResources().getString(R.string.textoPregunta551));

                        Pregunta pregunta552 = realm.createObject(Pregunta.class);
                            pregunta552.setIdAudit(nuevaAuditoria.getIdAuditoria());
                            pregunta552.setIdPregunta("552");
                            pregunta552.setPuntaje(0);
                            pregunta552.setTextoPregunta(context.getResources().getString(R.string.textoPregunta552));

                    item55.addPregunta(pregunta551);
                    item55.addPregunta(pregunta552);

                ese5.addItem(item55);
                nuevaAuditoria.addEse(ese5);
            }

        });


    return idAuditInstanciada;
    }
    private String determinarFecha(){
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        return sdf.format(cal.getTime());
    }
}
