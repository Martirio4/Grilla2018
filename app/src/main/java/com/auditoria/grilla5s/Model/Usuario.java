package com.auditoria.grilla5s.Model;

import io.realm.RealmObject;

/**
 * Created by elmar on 9/2/2018.
 */

public class Usuario extends RealmObject {
    private String rutaFoto;
    private String idFoto;
    private String idAuditOArea;
    private String idPregunta;

    public Usuario() {
    }

    public String getRutaFoto() {
        return rutaFoto;
    }

    public void setRutaFoto(String rutaFoto) {
        this.rutaFoto = rutaFoto;
    }

    public String getIdFoto() {
        return idFoto;
    }

    public void setIdFoto(String idFoto) {
        this.idFoto = idFoto;
    }

    public String getIdAuditOArea() {
        return idAuditOArea;
    }

    public void setIdAuditOArea(String idAuditOArea) {
        this.idAuditOArea = idAuditOArea;
    }

    public String getIdPregunta() {
        return idPregunta;
    }

    public void setIdPregunta(String idPregunta) {
        this.idPregunta = idPregunta;
    }
}
