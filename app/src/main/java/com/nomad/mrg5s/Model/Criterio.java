package com.nomad.mrg5s.Model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by elmar on 10/1/2019.
 */

public class Criterio extends RealmObject {
    @PrimaryKey
    private String idCriterio;
    private String idAudit;
    private String idEse;
    private String idItem;
    private String idPregunta;
    private String idCuestionario;
    private String textoCriterio;
    private Integer puntajeCriterio;
    private Integer orden;

    public Integer getOrden() {
        return orden;
    }

    public void setOrden(Integer orden) {
        this.orden = orden;
    }

    public String getIdAudit() {
        return idAudit;
    }

    public void setIdAudit(String idAudit) {
        this.idAudit = idAudit;
    }

    public String getIdCriterio() {
        return idCriterio;
    }

    public void setIdCriterio(String idCriterio) {
        this.idCriterio = idCriterio;
    }

    public String getIdEse() {
        return idEse;
    }

    public void setIdEse(String idEse) {
        this.idEse = idEse;
    }

    public String getIdItem() {
        return idItem;
    }

    public void setIdItem(String idItem) {
        this.idItem = idItem;
    }

    public String getIdPregunta() {
        return idPregunta;
    }

    public void setIdPregunta(String idPregunta) {
        this.idPregunta = idPregunta;
    }

    public String getIdCuestionario() {
        return idCuestionario;
    }

    public void setIdCuestionario(String idCuestionario) {
        this.idCuestionario = idCuestionario;
    }

    public String getTextoCriterio() {
        return textoCriterio;
    }

    public void setTextoCriterio(String textoCriterio) {
        this.textoCriterio = textoCriterio;
    }

    public Integer getPuntajeCriterio() {
        return puntajeCriterio;
    }

    public void setPuntajeCriterio(Integer puntajeCriterio) {
        this.puntajeCriterio = puntajeCriterio;
    }
}
