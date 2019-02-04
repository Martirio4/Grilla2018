package com.auditoria.grilla5s.Model;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by elmar on 28/12/2018.
 */

public class Cuestionario extends RealmObject {

    @PrimaryKey
    private String idCuestionario;
    private String tipoCuestionario;
    private String nombreCuestionario;
    private RealmList<Ese> listaEses;

    public String getTipoCuestionario() {
        return tipoCuestionario;
    }

    public void setTipoCuestionario(String tipoCuestionario) {
        this.tipoCuestionario = tipoCuestionario;
    }

    public String getIdCuestionario() {
        return idCuestionario;
    }

    public void setIdCuestionario(String idCuestionario) {
        this.idCuestionario = idCuestionario;
    }

    public RealmList<Ese> getListaEses() {
        return listaEses;
    }

    public void setListaEses(RealmList<Ese> listaEses) {
        this.listaEses = listaEses;
    }

    public String getNombreCuestionario() {
        return nombreCuestionario;
    }

    public void setNombreCuestionario(String nombreCuestionario) {
        this.nombreCuestionario = nombreCuestionario;
    }

    public void addEse(Ese unaEse){
        this.listaEses.add(unaEse);
    }
}
