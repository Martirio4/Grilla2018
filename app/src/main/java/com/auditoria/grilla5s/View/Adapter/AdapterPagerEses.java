package com.auditoria.grilla5s.View.Adapter;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.auditoria.grilla5s.Model.Auditoria;
import com.auditoria.grilla5s.Utils.FuncionesPublicas;
import com.auditoria.grilla5s.View.Fragments.FragmentPreAudit;


import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;


public class AdapterPagerEses extends FragmentStatePagerAdapter {

    //EL ADAPTER NECESITA SIEMPRE UNA LISTA DE FRAGMENTS PARA MOSTRAR
    private List<Fragment> listaFragments;
    private String origen;
    private String idCuestionario;
    private List<String> unaListaTitulos;
    private List<String> listaDeIdEses;
    private String idEstructura;
    //CONSTRUNCTOR PARA EDITOR DE CUESTIONARIOS
    public AdapterPagerEses(FragmentManager fm, String origen, String idCuestionario, List<String> strings) {
        super(fm);
        this.listaFragments = listaFragments;
        this.origen = origen;
        this.idCuestionario = idCuestionario;
        this.listaDeIdEses=strings;

        //INICIALIZO LA LISTA DE FRAGMENT
        listaFragments = new ArrayList<>();

        //LE CARGO LOS FRAGMENTS QUE QUIERO. UTILIZO LA LISTA DE PELICULAS Y SERIES PARA CREAR LOS FRAGMENTS.

        switch (origen){
            case FuncionesPublicas.EDITAR_CUESTIONARIO:
                for (String idEse :
                        listaDeIdEses) {
                    listaFragments.add(FragmentPreAudit.CrearfragmentPreAudit(idEse,origen,idCuestionario));
                }
            break;
            default:
                Realm realm = Realm.getDefaultInstance();
                Auditoria mAudit=realm.where(Auditoria.class)
                        .equalTo("idAuditoria", idCuestionario)
                        .findFirst();
                if (mAudit!=null){
                    this.idEstructura=mAudit.getEstructuraAuditoria();
                }
                for (String idEse :
                        listaDeIdEses) {
                    listaFragments.add(FragmentPreAudit.CrearfragmentPreAudit(idEse,origen,idCuestionario,idEstructura));
                }
            break;
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
