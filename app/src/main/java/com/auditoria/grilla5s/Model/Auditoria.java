package com.auditoria.grilla5s.Model;

import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by elmar on 9/2/2018.
 */

public class Auditoria extends RealmObject {
    @PrimaryKey
    private String IdAuditoria;
    private String fechaAuditoria;
    private Usuario auditor;
    private Area areaAuditada;
    private Boolean esUltimaAuditoria;
    private RealmList<Ese> listaEses;
    private Double puntajeFinal;

    public Auditoria() {
    }

    public Double getPuntajeFinal() {
        return puntajeFinal;
    }

    public void setPuntajeFinal(Double puntajeFinal) {
        this.puntajeFinal = puntajeFinal;
    }

    public List<Ese> getListaEses() {
        return listaEses;
    }

    public void setListaEses(RealmList<Ese> listaEses) {
        this.listaEses = listaEses;
    }

    public String getIdAuditoria() {
        return IdAuditoria;
    }

    public void setIdAuditoria(String idAuditoria) {
        IdAuditoria = idAuditoria;
    }

    public String getFechaAuditoria() {
        return fechaAuditoria;
    }

    public void setFechaAuditoria(String fechaAuditoria) {
        this.fechaAuditoria = fechaAuditoria;
    }

    public Usuario getAuditor() {
        return auditor;
    }

    public void setAuditor(Usuario auditor) {
        this.auditor = auditor;
    }

    public Area getAreaAuditada() {
        return areaAuditada;
    }

    public void setAreaAuditada(Area areaAuditada) {
        this.areaAuditada = areaAuditada;
    }

    public Boolean getEsUltimaAuditoria() {
        return esUltimaAuditoria;
    }

    public void setEsUltimaAuditoria(Boolean esUltimaAuditoria) {
        this.esUltimaAuditoria = esUltimaAuditoria;
    }
    public void addEse(Ese unaEse){
        this.listaEses.add(unaEse);
    }
}
