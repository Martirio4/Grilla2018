package com.auditoria.grilla5s.View.Adapter;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.auditoria.grilla5s.View.Fragments.FragmentPreAudit;


import java.util.ArrayList;
import java.util.List;


public class AdapterPagerEses extends FragmentStatePagerAdapter {

    //EL ADAPTER NECESITA SIEMPRE UNA LISTA DE FRAGMENTS PARA MOSTRAR
    private List<Fragment> listaFragments;
    private String origen;
    private String idCuestionario;
    private List<String> unaListaTitulos;

    //CONSTRUNCTOR PARA EDITOR DE CUESTIONARIOS
    public AdapterPagerEses(FragmentManager fm, String origen, String idCuestionario) {
        super(fm);
        this.listaFragments = listaFragments;
        this.origen = origen;
        this.idCuestionario = idCuestionario;

        //INICIALIZO LA LISTA DE FRAGMENT
        listaFragments = new ArrayList<>();

        //LE CARGO LOS FRAGMENTS QUE QUIERO. UTILIZO LA LISTA DE PELICULAS Y SERIES PARA CREAR LOS FRAGMENTS.


        for(Integer i=1;i<6;i++){
            if (origen!=null && origen.equals("EDITARCUESTIONARIO")) {
                listaFragments.add(FragmentPreAudit.CrearfragmentPreAudit(i.toString(),origen,idCuestionario));
            } else {
                listaFragments.add(FragmentPreAudit.CrearfragmentPreAudit((i.toString()),origen));
            }
        }

        //LE AVISO AL ADAPTER QUE CAMBIO SU LISTA DE FRAGMENTS.
        notifyDataSetChanged();
    }



    //CONSTRUCTOR GENERAL
    public AdapterPagerEses(FragmentManager fm, String origen) {
        super(fm);
        this.origen=origen;
        //INICIALIZO LA LISTA DE FRAGMENT
        listaFragments = new ArrayList<>();

        //LE CARGO LOS FRAGMENTS QUE QUIERO. UTILIZO LA LISTA DE PELICULAS Y SERIES PARA CREAR LOS FRAGMENTS.

        for(Integer i=1;i<6;i++){
                listaFragments.add(FragmentPreAudit.CrearfragmentPreAudit((i.toString()),origen));
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

    public String getOrigen() {
        return origen;
    }

    public void setOrigen(String origen) {
        this.origen = origen;
    }

    public void setIdCuestionario(String idCuestionario) {
        this.idCuestionario = idCuestionario;
    }
}
