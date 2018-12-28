package com.auditoria.audit5S_Full.Model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by elmar on 9/2/2018.
 */

public class Area extends RealmObject {
    @PrimaryKey
    private String idArea;
    private Foto fotoArea;
    private String nombreArea;
    private String dueñoArea;
    private String tipoArea;

    public Area() {
    }

    public String getIdArea() {
        return idArea;
    }

    public void setIdArea(String idArea) {
        this.idArea = idArea;
    }

    public Foto getFotoArea() {
        return fotoArea;
    }

    public void setFotoArea(Foto fotoArea) {
        this.fotoArea = fotoArea;
    }

    public String getNombreArea() {
        return nombreArea;
    }

    public void setNombreArea(String nombreArea) {
        this.nombreArea = nombreArea;
    }

    public String getDueñoArea() {
        return dueñoArea;
    }

    public void setDueñoArea(String dueñoArea) {
        this.dueñoArea = dueñoArea;
    }

    public String getTipoArea() {
        return tipoArea;
    }

    public void setTipoArea(String tipoArea) {
        this.tipoArea = tipoArea;
    }
}
