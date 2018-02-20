package com.auditoria.grilla5s.Model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by elmar on 9/2/2018.
 */

public class Foto extends RealmObject {

    private String rutaFoto;
    private String idAudit;
    @PrimaryKey
    private String idFoto;
    private String idPregunta;

    public Foto() {
    }

    public String getRutaFoto() {
        return rutaFoto;
    }

    public void setRutaFoto(String rutaFoto) {
        this.rutaFoto = rutaFoto;
    }

    public String getIdAudit() {
        return idAudit;
    }

    public void setIdAudit(String idAudit) {
        this.idAudit = idAudit;
    }

    public String getIdFoto() {
        return idFoto;
    }

    public void setIdFoto(String idFoto) {
        this.idFoto = idFoto;
    }

    public String getIdPregunta() {
        return idPregunta;
    }

    public void setIdPregunta(String idPregunta) {
        this.idPregunta = idPregunta;
    }
}
