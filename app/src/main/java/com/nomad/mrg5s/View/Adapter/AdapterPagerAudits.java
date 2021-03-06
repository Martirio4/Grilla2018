package com.nomad.mrg5s.View.Adapter;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;


import com.nomad.mrg5s.Utils.FuncionesPublicas;
import com.nomad.mrg5s.View.Fragments.FragmentMyAudits;
import com.nomad.mrg5s.View.Fragments.FragmentRanking;
import com.nomad.mrg5s.View.Fragments.FragmentRankingAreas;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Pablo on 31/5/2017.
 */

public class AdapterPagerAudits extends FragmentStatePagerAdapter {

    //EL ADAPTER NECESITA SIEMPRE UNA LISTA DE FRAGMENTS PARA MOSTRAR
    private List<Fragment> listaFragments;

    private List<String> unaListaTitulos;

    public AdapterPagerAudits(FragmentManager fm, List<String>listaTitulos) {
        super(fm);

        //INICIALIZO LA LISTA DE FRAGMENT
        unaListaTitulos=listaTitulos;
        listaFragments = new ArrayList<>();


        //LE CARGO LOS FRAGMENTS QUE QUIERO. UTILIZO LA LISTA DE PELICULAS Y SERIES PARA CREAR LOS FRAGMENTS.

        for (String unString : unaListaTitulos) {
            if (unString.equals(FuncionesPublicas.AUDITORIA)){
                listaFragments.add(FragmentMyAudits.crearFragmentMyAudit());
            }
            if (unString.equals(FuncionesPublicas.RANKING)){
                listaFragments.add(FragmentRanking.crearFragmentRanking());
            }
            if (unString.equals(FuncionesPublicas.AREAS)){
                listaFragments.add(FragmentRankingAreas.crearFragmentRankingAreas());
            }
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
    }

    public void updateAdapters(){
        FragmentRanking fragmentRanking=(FragmentRanking)listaFragments.get(1);
        fragmentRanking.updateAdapter();
        FragmentMyAudits fragmentMyAudits=(FragmentMyAudits) listaFragments.get(0);
        fragmentMyAudits.updateAdapter();
    }
}
