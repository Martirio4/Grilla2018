package com.auditoria.grilla5s.Model;

import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by elmar on 9/2/2018.
 */

public class Item extends RealmObject {
    private String tituloItem;
    private String textoItem;
    private String idAudit;
    private String idEse;
    private String idItem;
    private RealmList<Pregunta> listaPreguntas;
    private Double puntajeItem;
    private String idCuestionario;

    public Item() {
    }

    public String getTituloItem() {
        return tituloItem;
    }

    public void setTituloItem(String tituloItem) {
        this.tituloItem = tituloItem;
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

    public String getIdCuestionario() {
        return idCuestionario;
    }

    public void setIdCuestionario(String idCuestionario) {
        this.idCuestionario = idCuestionario;
    }

    public String getIdEse() {
        return idEse;
    }

    public void setIdEse(String idEse) {
        this.idEse = idEse;
    }

    public void addlistaPreguntas(RealmList<Pregunta> listaPreguntas) {
        this.listaPreguntas.clear();
        this.listaPreguntas.addAll(listaPreguntas);
    }
}
