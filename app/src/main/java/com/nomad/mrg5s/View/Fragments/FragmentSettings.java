package com.nomad.mrg5s.View.Fragments;


import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.github.clans.fab.FloatingActionButton;
import com.nomad.mrg5s.DAO.ControllerDatos;
import com.nomad.mrg5s.R;
import com.nomad.mrg5s.View.Activities.LoginActivity;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.io.File;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentSettings extends Fragment {

    private Button areas;
    private Button borrar;
    private Button tuto;
    private Button logout;
    private Button editCuestionarios;
    private FloatingActionButton fabVolver;
    private Button editarCriterios;

    private ImageView v_area;
    private ImageView v_borrar;
    private ImageView v_tuto;
    private ImageView v_logout;
    private ImageView v_editCues;
    private ImageView v_editCrit;


    private Notificable notificable;


    public FragmentSettings() {
        // Required empty public constructor
    }

    private SharedPreferences config;
    private ControllerDatos controllerDatos;

    public interface Notificable{
        public void abrirEditorDeCuestionarios();
        void abrirEditorCriterios();

        void abrirFragmentGestionAreas();

        void hacerLogout();
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        controllerDatos=new ControllerDatos(getContext());
        config = getActivity().getSharedPreferences("prefs",0);
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_settings_copia, container, false);

        v_area = view.findViewById(R.id.tap1);
        v_logout = view.findViewById(R.id.tap2);
        v_tuto = view.findViewById(R.id.tap3);
        v_borrar= view.findViewById(R.id.tap4);
        v_editCues = view.findViewById(R.id.tap5);
        v_editCrit = view.findViewById(R.id.tap6);


        areas=view.findViewById(R.id.botonManageAreas);
        logout=view.findViewById(R.id.botonLogOut);
        borrar=view.findViewById(R.id.botonBorrarTodo);
        tuto=view.findViewById(R.id.botonTuto);
        editCuestionarios = view.findViewById(R.id.botonRateApp);
        fabVolver =view.findViewById(R.id.botonVolver);
        TextView salir=view.findViewById(R.id.textoFabVolver);
        editarCriterios=view.findViewById(R.id.botonEditCriterioDefault);

        Typeface roboto = Typeface.createFromAsset(getContext().getAssets(), "fonts/Roboto-Light.ttf");

        editCuestionarios.setTypeface(roboto);
        editarCriterios.setTypeface(roboto);
        areas.setTypeface(roboto);
        logout.setTypeface(roboto);
        borrar.setTypeface(roboto);
        salir.setTypeface(roboto);

        tuto.setTypeface(roboto);

        Boolean textoParaBotonTuto=config.getBoolean("estadoTuto",false);
        if (textoParaBotonTuto){
            tuto.setText(R.string.activarTuto);
        }
        else{
            tuto.setText(R.string.desactivarTuto);
        }

        areas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                notificable.abrirFragmentGestionAreas();

            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialDialog.Builder(v.getContext())
                        .title(getResources().getString(R.string.signOut))
                        .contentColor(ContextCompat.getColor(v.getContext(), R.color.primary_text))
                        .titleColor(ContextCompat.getColor(v.getContext(), R.color.tile4))
                        .backgroundColor(ContextCompat.getColor(v.getContext(), R.color.tile1))
                        .content(getResources().getString(R.string.avisoSignout)+"\n"+getResources().getString(R.string.continuar))
                        .positiveText(getResources().getString(R.string.signOut))
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                notificable.hacerLogout();
                            }
                        })
                        .negativeText(getResources().getString(R.string.cancel))
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                            }
                        })
                        .show();
            }
        });

        borrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new MaterialDialog.Builder(v.getContext())
                        .title(getResources().getString(R.string.databaseDelete))
                        .contentColor(ContextCompat.getColor(v.getContext(), R.color.primary_text))
                        .titleColor(ContextCompat.getColor(v.getContext(), R.color.tile4))
                        .backgroundColor(ContextCompat.getColor(v.getContext(), R.color.tile1))
                        .content(getResources().getString(R.string.auditsWillBeDeleted)+"\n"+getResources().getString(R.string.continuar))
                        .positiveText(getResources().getString(R.string.delete))
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                            new BorrarTodo().execute();

                            }
                        })
                        .negativeText(getResources().getString(R.string.cancel))
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                            }
                        })
                        .show();

            }
        });

        salir.setText(salir.getContext().getString(R.string.volver));
        salir.setTextColor(ContextCompat.getColor(salir.getContext(),R.color.primary_text));
        fabVolver.setColorNormal(ContextCompat.getColor(fabVolver.getContext(),R.color.mirgorNaranja));
        fabVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            FragmentManager fragmentManager=getActivity().getSupportFragmentManager();
                fragmentManager.popBackStack();
                getActivity().finish();
            }
        });

        tuto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor=config.edit();
                Boolean estadoTuto=config.getBoolean("estadoTuto",false);
                //SI ESTADOTUTO ES VERDADERO EL TUTO NO CORRE
                if (estadoTuto){
                    editor.putBoolean("estadoTuto",false);
                    editor.putBoolean("quiereVerTuto", true);
                    //SI SON FALSOS, EL TUTORIAL CORRE SI SON VERDADEROS EL TUTO NO CORRE
                    editor.putBoolean("primeraVezFragmentSetting", false);
                    editor.putBoolean("primeraVezFragmentSubitem", false);
                    editor.putBoolean("primeraVezFragmentRadar", false);
                    editor.putBoolean("primeraVezFragmentManage", false);
                    editor.putBoolean("primeraVezFragmentSeleccion", false);
                    editor.putBoolean("primeraVezFragmentLanding",false);
                    editor.commit();
                    tuto.setText(R.string.desactivarTuto);

                }
                else{
                    editor.putBoolean("estadoTuto",true);
                    editor.putBoolean("quiereVerTuto", false);
                    //SI SON FALSOS, EL TUTORIAL CORRE SI SON VERDADEROS EL TUTO NO CORRE
                    editor.putBoolean("primeraVezFragmentSetting", true);
                    editor.putBoolean("primeraVezFragmentSubitem", true);
                    editor.putBoolean("primeraVezFragmentRadar", true);
                    editor.putBoolean("primeraVezFragmentManage", true);
                    editor.putBoolean("primeraVezFragmentSeleccion", true);
                    editor.putBoolean("primeraVezFragmentLanding",true);
                    editor.commit();

                    tuto.setText(R.string.activarTuto);
                }
            }
        });

        editCuestionarios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //LE DIGO A SETTING ACTIVITY QUE ABRA EL EDITOR DE CUESTIONARIOS
               notificable.abrirEditorDeCuestionarios();
            }
        });

        editarCriterios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notificable.abrirEditorCriterios();
            }
        });


        boolean quiereVerTuto = config.getBoolean("quiereVerTuto",false);
        boolean primeraVezFragmentSetting=config.getBoolean("primeraVezFragmentSetting",false);

        //SI EL USUARIO ELIGIO VER TUTORIALES ME FIJO SI YA PASO POR ESTA PAGINA.
        if (quiereVerTuto) {
            if (!primeraVezFragmentSetting) {
                SharedPreferences.Editor editor = config.edit();
                editor.putBoolean("primeraVezFragmentSetting",true);
                editor.commit();

                seguirConTutorial();
            }
        }
        return view;
    }

    private void registrarEnvioDeRating() {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        if (user != null) {
            final DatabaseReference reference = mDatabase.child("usuarios").child(user.getUid()).child("estadisticas").child("calificoApp");

            //---leer cantidad de auditorias---//
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    reference.setValue("si");
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }



    private void seguirConTutorial() {
        Typeface roboto = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Roboto-Light.ttf");

        new TapTargetSequence(getActivity())
                .targets(
                        TapTarget.forView(v_area, getResources().getString(R.string.tutorial_tit_area), getResources().getString(R.string.tutorial_desc_area))
                                .transparentTarget(true)
                                .outerCircleColor(R.color.tutorial2)      // Specify a color for the outer circle
                                .outerCircleAlpha(0.85f)            // Specify the alpha amount for the outer circle
                                .textTypeface(roboto)  // Specify a typeface for the text
                                .drawShadow(true)                   // Whether to draw a drop shadow or not
                                .cancelable(false)                  // Whether tapping outside the outer circle dismisses the view
                                .tintTarget(false)
                                .targetRadius(80)
                                .id(1),                   // Whether to tint the target view's color
                        TapTarget.forView(v_logout, getResources().getString(R.string.tutorial_tit_logout), getResources().getString(R.string.tutorial_desc_logout))
                                .transparentTarget(true)
                                .outerCircleColor(R.color.tutorial1)
                                .textColor(R.color.primary_text)
                                .outerCircleAlpha(0.95f)            // Specify the alpha amount for the outer circle
                                .textTypeface(roboto)  // Specify a typeface for the text
                                .drawShadow(true)                   // Whether to draw a drop shadow or not
                                .cancelable(false)                  // Whether tapping outside the outer circle dismisses the view
                                .tintTarget(false)
                                .targetRadius(80)
                                .id(2),
                        TapTarget.forView(v_tuto, getResources().getString(R.string.tutorial_tit_tuto), getResources().getString(R.string.tutorial_desc_tuto))
                                .transparentTarget(true)
                                .outerCircleColor(R.color.tutorial2)      // Specify a color for the outer circle
                                .outerCircleAlpha(0.95f)            // Specify the alpha amount for the outer circle
                                .textTypeface(roboto)  // Specify a typeface for the text
                                .drawShadow(true)                   // Whether to draw a drop shadow or not
                                .cancelable(false)                  // Whether tapping outside the outer circle dismisses the view
                                .tintTarget(false)
                                .targetRadius(80)
                                .id(3),
                        TapTarget.forView(v_borrar, getResources().getString(R.string.tutorial_tit_delete), getResources().getString(R.string.tutorial_desc_delete))
                                .transparentTarget(true)
                                .outerCircleColor(R.color.tutorial1)
                                .textColor(R.color.primary_text)
                                .textColor(R.color.blancoNomad)// Specify a color for the outer circle
                                .outerCircleAlpha(0.95f)            // Specify the alpha amount for the outer circle
                                .textTypeface(roboto)  // Specify a typeface for the text
                                .drawShadow(true)                   // Whether to draw a drop shadow or not
                                .cancelable(true)                  // Whether tapping outside the outer circle dismisses the view
                                .tintTarget(false)
                                .targetRadius(80)
                                .id(4),
                        TapTarget.forView(v_editCues, getResources().getString(R.string.tutorial_tit_cues), getResources().getString(R.string.tutorial_desc_cues))
                                .transparentTarget(true)
                                .outerCircleColor(R.color.tutorial2)      // Specify a color for the outer circle
                                .outerCircleAlpha(0.95f)            // Specify the alpha amount for the outer circle
                                .textTypeface(roboto)  // Specify a typeface for the text
                                .drawShadow(true)                   // Whether to draw a drop shadow or not
                                .cancelable(false)                  // Whether tapping outside the outer circle dismisses the view
                                .tintTarget(false)
                                .targetRadius(80)
                                .id(5),
                        TapTarget.forView(v_editCrit, getResources().getString(R.string.tutorial_tit_crit), getResources().getString(R.string.tutorial_desc_crit))
                                .transparentTarget(true)
                                .outerCircleColor(R.color.tutorial1)
                                .textColor(R.color.primary_text)
                                .textColor(R.color.blancoNomad)// Specify a color for the outer circle
                                .outerCircleAlpha(0.95f)            // Specify the alpha amount for the outer circle
                                .textTypeface(roboto)  // Specify a typeface for the text
                                .drawShadow(true)                   // Whether to draw a drop shadow or not
                                .cancelable(true)                  // Whether tapping outside the outer circle dismisses the view
                                .tintTarget(false)
                                .targetRadius(80)
                                .id(6)
                )

                .listener(new TapTargetSequence.Listener() {
                    // This listener will tell us when interesting(tm) events happen in regards
                    // to the sequence
                    @Override
                    public void onSequenceFinish() {

                    }

                    @Override
                    public void onSequenceStep(TapTarget tapTarget, boolean b) {


                    }

                    @Override
                    public void onSequenceCanceled(TapTarget lastTarget) {
                        // Boo
                    }
                })
                .start();
    }



    private class BorrarTodo extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... args) {
            controllerDatos.borrarBaseDatos();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            Toast.makeText(getContext(), getResources().getString(R.string.confirmaBorrarBaseDeDato), Toast.LENGTH_SHORT).show();
            super.onPostExecute(result);
        }

        @Override
        protected void onPreExecute() {

            super.onPreExecute();

        }
    }
    public static boolean deleteDirectory(File path) {
        if( path.exists() ) {
            File[] files = path.listFiles();
            if (files == null) {
                return true;
            }
            for(int i=0; i<files.length; i++) {
                if(files[i].isDirectory()) {
                    deleteDirectory(files[i]);
                }
                else {
                    files[i].delete();
                }
            }
        }
        return( path.delete() );
    }

    @Override
    public void onResume() {
        super.onResume();
       /* Boolean estadoTuto=config.getBoolean("estadoTuto",false);
        if (estadoTuto){
            tuto.setText(R.string.activarTuto);
        }
        else{
            tuto.setText(R.string.desactivarTuto);

        }
        */
    }
    public void rateApp()
    {
        try
        {
            Intent rateIntent = rateIntentForUrl("market://details");
            startActivity(rateIntent);
        }
        catch (ActivityNotFoundException e)
        {
            Intent rateIntent = rateIntentForUrl("https://play.google.com/store/apps/details");
            startActivity(rateIntent);
        }
    }

    private Intent rateIntentForUrl(String url)
    {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(String.format("%s?id=%s", url,getActivity().getPackageName())));
        int flags = Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_MULTIPLE_TASK;
        if (Build.VERSION.SDK_INT >= 21)
        {
            flags |= Intent.FLAG_ACTIVITY_NEW_DOCUMENT;
        }
        else
        {
            //noinspection deprecation
            flags |= Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET;
        }
        intent.addFlags(flags);
        return intent;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.notificable=(Notificable)context;
    }
}
