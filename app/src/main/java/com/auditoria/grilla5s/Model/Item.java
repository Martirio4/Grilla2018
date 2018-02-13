package com.auditoria.grilla5s.Model;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by elmar on 9/2/2018.
 */

public class Item extends RealmObject {
    private String criterio;
    private String textoItem;
    private String idAudit;
    private String idItem;
    private RealmList<Pregunta> listaPreguntas;
    private Double puntajeItem;

    public Item() {
    }

    public String getCriterio() {
        return criterio;
    }

    public void setCriterio(String criterio) {
        this.criterio = criterio;
    }

    public String getTextoItem() {
        return textoItem;
    }

    public void setTextoItem(String textoItem) {
        this.textoItem = textoItem;
    }

    public String getIdAudit() {
        return idAudit;
    }

    public void setIdAudit(String idAudit) {
        this.idAudit = idAudit;
    }

    public String getIdItem() {
        return idItem;
    }

    public void setIdItem(String idItem) {
        this.idItem = idItem;
    }

    public List<Pregunta> getListaPreguntas() {
        return listaPreguntas;
    }

    public void setListaPreguntas(RealmList<Pregunta> listaPreguntas) {
        this.listaPreguntas = listaPreguntas;
    }

    public Double getPuntajeItem() {
        return puntajeItem;
    }

    public void setPuntajeItem(Double puntajeItem) {
        this.puntajeItem = puntajeItem;
    }

    public void addPregunta(Pregunta unaPregunta){
        this.listaPreguntas.add(unaPregunta);
    }
}
