package com.auditoria.grilla5s.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;

import com.auditoria.grilla5s.Model.Auditoria;
import com.auditoria.grilla5s.Model.Foto;
import com.google.firebase.auth.FirebaseAuth;


import java.io.File;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by elmar on 15/1/2018.
 */

public class FuncionesPublicas {



    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    public static Boolean borrarAuditoriaSeleccionada(final String idAudit){
        Realm realm = Realm.getDefaultInstance();

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {

                String usuario = FirebaseAuth.getInstance().getCurrentUser().getEmail();

                RealmResults<SubItem> Subitems = realm.where(SubItem.class)
                        .equalTo("auditoria", idAudit)
                        .findAll();
                Subitems.deleteAllFromRealm();

                RealmResults<Foto> fotos = realm.where(Foto.class)
                        .equalTo("auditoria", idAudit)
                        .findAll();
                for (Foto foti : fotos
                        ) {
                    File file = new File(foti.getRutaFoto());
                    boolean deleted = file.delete();
                }
                fotos.deleteAllFromRealm();

                Auditoria result2 = realm.where(Auditoria.class)
                        .equalTo("idAuditoria", idAudit)
                        .findFirst();

                result2.deleteFromRealm();
            }
        });
        return true;


    }
    public static void sumarUsoDeApp(Context context){
        SharedPreferences config = context.getSharedPreferences("prefs",0);
        Integer cantidadUsos = config.getInt("cantidadUsos",0);

        SharedPreferences.Editor editor = config.edit();
        editor.putInt("cantidadUsos",cantidadUsos+1);
        editor.commit();

    }

}
