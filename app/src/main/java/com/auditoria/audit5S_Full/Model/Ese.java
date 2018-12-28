package com.auditoria.audit5S_Full.Model;

import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by elmar on 9/2/2018.
 */

public class Ese extends RealmObject {

    private Integer idEse;
    private String idAudit;
    private Double puntajeEse;
    private RealmList<Item> listaItem;

    public Ese() {
    }

    public Integer getIdEse() {
        return idEse;
    }

    public void setIdEse(Integer idEse) {
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
}