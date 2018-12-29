package com.auditoria.grilla5s.Model;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by elmar on 9/2/2018.
 */

public class Pregunta extends RealmObject {
    private String comentario;
    private String textoPregunta;
    private Integer idPregunta;
    private Integer puntaje;
    private String idAudit;
    private Integer idItem;
    private String idCuestionario;
    private RealmList<Foto> listaFotos;

    public Pregunta() {
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public String getTextoPregunta() {
        return textoPregunta;
    }

    public void setTextoPregunta(String textoPregunta) {
        this.textoPregunta = textoPregunta;
    }

    public Integer getIdPregunta() {
        return idPregunta;
    }

    public void setIdPregunta(Integer idPregunta) {
        this.idPregunta = idPregunta;
    }

    public Integer getPuntaje() {
        return puntaje;
    }

    public void setPuntaje(Integer puntaje) {
        this.puntaje = puntaje;
    }

    public String getIdAudit() {
        return idAudit;
    }

    public void setIdAudit(String idAudit) {
        this.idAudit = idAudit;
    }


    public void addFoto(Foto unaFoto){
        this.listaFotos.add(unaFoto);
    }

    public Integer getIdItem() {
        return idItem;
    }

    public void setIdItem(Integer idItem) {
        this.idItem = idItem;
    }

    public RealmList<Foto> getListaFotos() {
        return listaFotos;
    }

    public void setListaFotos(RealmList<Foto> listaFotos) {
        this.listaFotos = listaFotos;
    }

    public String getIdCuestioniario() {
        return idCuestionario;
    }

    public void setIdCuestioniario(String idCuestioniario) {
        this.idCuestionario = idCuestioniario;
    }
}
