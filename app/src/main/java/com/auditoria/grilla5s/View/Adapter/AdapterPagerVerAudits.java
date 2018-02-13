package com.auditoria.grilla5s.View.Adapter;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.nomad.audit5s.fragments.FragmentRevisarAuditoria;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Pablo on 31/5/2017.
 */

public class AdapterPagerVerAudits extends FragmentStatePagerAdapter {

    //EL ADAPTER NECESITA SIEMPRE UNA LISTA DE FRAGMENTS PARA MOSTRAR
    private List<Fragment> listaFragments;
    private List<String> listaStrings = new ArrayList<>();
    private List<String> unaListaTitulos;

    public AdapterPagerVerAudits(FragmentManager fm) {
        super(fm);

        //INICIALIZO LA LISTA DE FRAGMENT
        listaFragments = new ArrayList<>();

        //LE CARGO LOS FRAGMENTS QUE QUIERO. UTILIZO LA LISTA DE PELICULAS Y SERIES PARA CREAR LOS FRAGMENTS.

        for (String unString : listaStrings) {
            listaFragments.add(FragmentRevisarAuditoria.crearFragment(unString));

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

    public void setListaEses(List<String> lista) {
        this.listaStrings = lista;
        for (String unStr : listaStrings) {
            listaFragments.add(FragmentRevisarAuditoria.crearFragment(unStr));
        }
        notifyDataSetChanged();
    }



    @Override
    public CharSequence getPageTitle(int position) {
        return unaListaTitulos.get(position);
    }

    public void setUnaListaTitulos(List<String> unaListaTitulos) {
        this.unaListaTitulos = unaListaTitulos;
    }
}
