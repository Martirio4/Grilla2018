package com.nomad.mrg5s.View.Activities;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.nomad.mrg5s.Model.Foto;
import com.nomad.mrg5s.R;
import com.nomad.mrg5s.Utils.FuncionesPublicas;
import com.nomad.mrg5s.View.Fragments.FragmentZoom;

import io.realm.Realm;

public class ActivityZoom extends AppCompatActivity implements FragmentZoom.Zoomeable {
    public static final String IDFOTO="IDFOTO";
    private String idFoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zoom);

            Intent intent = getIntent();
            Bundle bundle=intent.getExtras();
            idFoto=bundle.getString(IDFOTO);

        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
        } catch (Exception e) {
            e.printStackTrace();
            Realm.init(ActivityZoom.this.getApplicationContext());
            realm = Realm.getDefaultInstance();
        }
            Foto mFoto= realm.where(Foto.class)
                    .equalTo("idFoto",idFoto)
                    .findFirst();

            Bundle mBundle= new Bundle();
            mBundle.putString(FragmentZoom.RUTAFOTO,mFoto.getRutaFoto());
            mBundle.putString(FragmentZoom.COMENTARIOFOTO,mFoto.getComentarioFoto());

            FragmentZoom mFragmentZoom = new FragmentZoom();
            mFragmentZoom.setArguments(mBundle);
            FragmentManager fragmentManager=getSupportFragmentManager();
            FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.contenedorZoom,mFragmentZoom, FuncionesPublicas.FRAGMENT_ZOOM);
            fragmentTransaction.commit();

    }

    @Override
    public void cerrarZoom() {
        ActivityZoom.this.finish();
    }
}
