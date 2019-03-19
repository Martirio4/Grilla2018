package com.nomad.mrg5s.Model;

import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by elmar on 9/2/2018.
 */

public class Ese extends RealmObject {
    @PrimaryKey
    private String idEse;
    private String idAudit;
    private Double puntajeEse;
    private RealmList<Item> listaItem;
    private RealmList<Pregunta> listaPreguntas;
    private String idCuestionario;
    private String nombreEse;
    private Integer numeroEse;

    public Ese() {
    }

    public String getNombreEse() {
        return nombreEse;
    }

    public void setNombreEse(String nombreEse) {
        this.nombreEse = nombreEse;
    }

    public Integer getNumeroEse() {
        return numeroEse;
    }

    public void setNumeroEse(Integer numeroEse) {
        this.numeroEse = numeroEse;
    }

    public String getIdEse() {
        return idEse;
    }

    public void setIdEse(String idEse) {
        this.idEse = idEse;
    }

    public String getIdAudit() {
        return idAudit;
    }

    public void setIdAudit(String idAudit) {
        this.idAudit = idAudit;
    }

    public Double getPuntajeEse() {
        return puntajeEse;
    }

    public void setPuntajeEse(Double puntajeEse) {
        this.puntajeEse = puntajeEse;
    }

    public List<Item> getListaItem() {
        return listaItem;
    }

    public void addItem(Item unItem){
        this.listaItem.add(unItem);
    }

    public void setListaItem(RealmList<Item> listaItem) {
        this.listaItem = listaItem;
    }

    public RealmList<Pregunta> getListaPreguntas() {
        return listaPreguntas;
    }

    public void setListaPreguntas(RealmList<Pregunta> listaPreguntas) {
        this.listaPreguntas = listaPreguntas;
    }

    public String getIdCuestionario() {
        return idCuestionario;
    }

    public void setIdCuestionario(String idCuestionario) {
        this.idCuestionario = idCuestionario;
    }

    public void addPregunta(Pregunta unaPregunta){
        this.listaPreguntas.add(unaPregunta);
    }

    public void removePregunta(Pregunta unPregunta) {
        this.listaPreguntas.remove(unPregunta);
    }
}
