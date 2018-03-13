package com.auditoria.grilla5s.Utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.Toast;

import com.auditoria.grilla5s.Model.Auditoria;
import com.auditoria.grilla5s.Model.Ese;
import com.auditoria.grilla5s.Model.Foto;
import com.auditoria.grilla5s.Model.Item;
import com.auditoria.grilla5s.Model.Pregunta;
import com.auditoria.grilla5s.R;
import com.auditoria.grilla5s.View.Fragments.FragmentPregunta;
import com.google.firebase.auth.FirebaseAuth;


import java.io.File;

import io.realm.Realm;
import io.realm.RealmResults;
import pl.aprilapps.easyphotopicker.EasyImage;
import pl.tajchert.nammu.Nammu;
import pl.tajchert.nammu.PermissionCallback;

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

                RealmResults<Pregunta> preguntas = realm.where(Pregunta.class)
                        .equalTo("idAudit", idAudit)
                        .findAll();
                preguntas.deleteAllFromRealm();

                RealmResults<Foto> fotos = realm.where(Foto.class)
                        .equalTo("idAudit", idAudit)
                        .findAll();
                for (Foto foti : fotos
                        ) {
                    File file = new File(foti.getRutaFoto());
                    boolean deleted = file.delete();
                }
                fotos.deleteAllFromRealm();

                RealmResults<Item> items = realm.where(Item.class)
                        .equalTo("idAudit",idAudit)
                        .findAll();
                items.deleteAllFromRealm();

                RealmResults<Ese> eses = realm.where(Ese.class)
                        .equalTo("idAudit",idAudit)
                        .findAll();
                eses.deleteAllFromRealm();

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
    
    public static Boolean hayPermisoParaEscribir(final Context context, View view){
        final Boolean[] resultado = {false};
        if (Nammu.checkPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
          
           resultado[0] =true;
        }
        else {

//                      PIDO PERMISO PARA USAR LA MEMORIA EXTERNA

            if (Nammu.shouldShowRequestPermissionRationale((Activity)(context),android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                //User already refused to give us this permission or removed it
                //Now he/she can mark "never ask again" (sic!)
                Snackbar.make(view, (context).getResources().getString(R.string.appNecesitaPermiso),
                        Snackbar.LENGTH_INDEFINITE).setAction(context.getResources().getString(R.string.ok), new View.OnClickListener() {
                    @Override public void onClick(final View view) {
                        Nammu.askForPermission((Activity) context, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                new PermissionCallback() {
                                    @Override
                                    public void permissionGranted() {
                                        resultado[0] =true;
                                    }

                                    @Override
                                    public void permissionRefused() {
                                        resultado[0]=false;
                                    }
                                });
                    }
                }).show();
            } else {
                //First time asking for permission
                // or phone doesn't offer permission
                // or user marked "never ask again"
                Nammu.askForPermission((Activity)context, android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        new PermissionCallback() {
                            @Override
                            public void permissionGranted() {
                                resultado[0] =true;
                            }

                            @Override
                            public void permissionRefused() {
                                resultado[0]=false;

                            }
                        });
            }
        }
        return resultado[0];
    }

}
