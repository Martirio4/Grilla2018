package com.auditoria.grilla5s.Model;

import java.util.Date;
import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by elmar on 9/2/2018.
 */

public class Auditoria extends RealmObject {

    @PrimaryKey
    private String idAuditoria;
    private Date fechaAuditoria;
    private Usuario auditor;
    private Area areaAuditada;
    private Boolean esUltimaAuditoria;
    private RealmList<Ese> listaEses;
    private Double puntajeFinal;
    private Boolean auditEstaCerrada;
    private String estructuraAuditoria;

    public String getEstructuraAuditoria() {
        return estructuraAuditoria;
    }

    public void setEstructuraAuditoria(String estructuraAuditoria) {
        this.estructuraAuditoria = estructuraAuditoria;
    }

    public Boolean getAuditEstaCerrada() {
        return auditEstaCerrada;
    }

    public void setAuditEstaCerrada(Boolean auditEstaCerrada) {
        this.auditEstaCerrada = auditEstaCerrada;
    }

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
        return idAuditoria;
    }

    public void setIdAuditoria(String idAuditoria) {
        this.idAuditoria = idAuditoria;
    }

    public Date getFechaAuditoria() {
        return fechaAuditoria;
    }

    public void setFechaAuditoria(Date fechaAuditoria) {
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
