package com.auditoria.grilla5s.View.Adapter;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.auditoria.grilla5s.Model.Item;
import com.auditoria.grilla5s.Model.Pregunta;
import com.auditoria.grilla5s.View.Fragments.FragmentPreAudit;
import com.auditoria.grilla5s.View.Fragments.FragmentPregunta;

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmList;

/**
 * Created by Pablo on 31/5/2017.
 */

public class AdapterPagerPreguntas extends FragmentStatePagerAdapter {

    //EL ADAPTER NECESITA SIEMPRE UNA LISTA DE FRAGMENTS PARA MOSTRAR
    private List<Fragment> listaFragments;
    private RealmList<Pregunta> listaPregunta = new RealmList<>();
    private List<String> unaListaTitulos;

    public AdapterPagerPreguntas(FragmentManager fm) {
        super(fm);

        //INICIALIZO LA LISTA DE FRAGMENT
        listaFragments = new ArrayList<>();

        //LE CARGO LOS FRAGMENTS QUE QUIERO. UTILIZO LA LISTA DE PELICULAS Y SERIES PARA CREAR LOS FRAGMENTS.

        for (Pregunta unaPreg:listaPregunta
             ) {
            FragmentPregunta.CrearfragmentPregunta(unaPreg);
        }

        //LE AVISO AL ADAPTER QUE CAMBIO SU LISTA DE FRAGMENTS.
        notifyDataSetChanged();
    }


    @Override
    public Fragment getItem(int position) {
        return listaFragments.get(position);
    }

    @Override
    public int getCount() {
        return listaFragments.size();
    }



    @Override
    public CharSequence getPageTitle(int position) {
        return unaListaTitulos.get(position);
    }

    public void setUnaListaTitulos(List<String> unaListaTitulos) {
        this.unaListaTitulos = unaListaTitulos;
        notifyDataSetChanged();
    }

    public RealmList<Pregunta> getListaPregunta() {
        return listaPregunta;
    }

    public void setListaPregunta(RealmList<Pregunta> listaPregunta) {
        this.listaPregunta = listaPregunta;
        notifyDataSetChanged();
    }


}
