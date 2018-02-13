package com.auditoria.grilla5s.View.Fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetView;
import com.google.firebase.auth.FirebaseAuth;
import com.nomad.audit5s.R;
import com.nomad.audit5s.adapter.AdapterArea;
import com.nomad.audit5s.model.Area;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentSeleccionAerea extends Fragment {

    private RealmList<Area> listaAreas;
    private RecyclerView recyclerAreas;
    private AdapterArea adapterArea;
    private LinearLayoutManager layoutManager;

    private Notificable notificable;
    private LinearLayout linear;

    public FragmentSeleccionAerea() {
        // Required empty public constructor
    }

    public interface Notificable{
        public void comenzarAuditoria(Area unArea);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_seleccion_aerea, container, false);
        linear=view.findViewById(R.id.vistaCentral);
        TextView textoSinAreas=(TextView)view.findViewById(R.id.textoSinAreas);
        String usuario= FirebaseAuth.getInstance().getCurrentUser().getEmail();
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Area> result2 = realm.where(Area.class)
                .equalTo("usuario", usuario)
                .findAll();

        if  (result2.size()<1){
            textoSinAreas.setVisibility(View.VISIBLE);
        }
        else{
            textoSinAreas.setVisibility(View.GONE);
        }

        listaAreas=new RealmList<>();
        listaAreas.addAll(result2);
        recyclerAreas= (RecyclerView)view.findViewById(R.id.recyclerArea);
        adapterArea= new AdapterArea();
        adapterArea.setContext(getContext());
        layoutManager= new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL,false);
        recyclerAreas.setLayoutManager(layoutManager);
        adapterArea.setListaAreasOriginales(listaAreas);
        recyclerAreas.setAdapter(adapterArea);

        View.OnClickListener listenerArea = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer posicion = recyclerAreas.getChildAdapterPosition(v);
                RealmList<Area> listaAreas = adapterArea.getListaAreasOriginales();
                Area areaClickeada = listaAreas.get(posicion);
                notificable.comenzarAuditoria(areaClickeada);
            }
        };
        adapterArea.setListener(listenerArea);

        Toolbar toolbar = (Toolbar) view.findViewById(R.id.my_toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);

        Typeface robotoR = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Roboto-Regular.ttf");
        TextView unText=toolbar.findViewById(R.id.textoToolbar);
        unText.setTypeface(robotoR);
        unText.setTextColor(getResources().getColor(R.color.tile5));
        unText.setText(getResources().getString(R.string.selectArea));

        if (((AppCompatActivity)getActivity()).getSupportActionBar() != null) {
            ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

       SharedPreferences config = getActivity().getSharedPreferences("prefs",0);
        boolean quiereVerTuto = config.getBoolean("quiereVerTuto",false);
        boolean primeraVezFragmentSubitem =config.getBoolean("primeraVezFragmentSeleccion",false);
        //SI EL USUARIO ELIGIO VER TUTORIALES ME FIJO SI YA PASO POR ESTA PAGINA.
        if (quiereVerTuto) {
            if (!primeraVezFragmentSubitem) {

                if (result2.size()>=1) {
                    SharedPreferences.Editor editor = config.edit();
                    editor.putBoolean("primeraVezFragmentSeleccion",true);
                    editor.commit();

                    seguirConTutorial();
                }
            }
        }
        return view;
    }

    private void seguirConTutorial() {
        Typeface roboto=Typeface.createFromAsset(getActivity().getAssets(), "fonts/Roboto-Regular.ttf");
        TapTargetView.showFor(getActivity(),                 // `this` is an Activity
                TapTarget.forView(linear, getResources().getString(R.string.tutorial_tit_subitem_general), getResources().getString(R.string.tutorial_desc_subitem_general))
                        .transparentTarget(true)
                        .textColor(R.color.primary_text)
                        .outerCircleColor(R.color.tutorial1)      // Specify a color for the outer circle
                        .outerCircleAlpha(0.95f)            // Specify the alpha amount for the outer circle
                        .textTypeface(roboto)  // Specify a typeface for the text
                        .drawShadow(true)                   // Whether to draw a drop shadow or not
                        .cancelable(false)                  // Whether tapping outside the outer circle dismisses the view
                        .tintTarget(false)
                        .icon(getResources().getDrawable(R.drawable.ic_check_black_24dp))
                        .id(1));
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.notificable=(Notificable) context;
    }
}
