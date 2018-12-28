package com.auditoria.audit5S_Full.Model;

import java.util.Date;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by elmar on 28/12/2018.
 */

public class Cuestionario extends RealmObject {
    private String tipoCuestionario;
    private RealmList<Ese> listaEses;

    public String getTipoCuestionario() {
        return tipoCuestionario;
    }

    public void setTipoCuestionario(String tipoCuestionario) {
        this.tipoCuestionario = tipoCuestionario;
    }

    public RealmList<Ese> getListaEses() {
        return listaEses;
    }

    public void setListaEses(RealmList<Ese> listaEses) {
        this.listaEses = listaEses;
    }
}
