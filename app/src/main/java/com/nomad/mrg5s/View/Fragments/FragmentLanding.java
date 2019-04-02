package com.nomad.mrg5s.View.Fragments;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nomad.mrg5s.DAO.ControllerDatos;
import com.nomad.mrg5s.Model.Cuestionario;
import com.nomad.mrg5s.R;
import com.nomad.mrg5s.Utils.HTTPConnectionManager;
import com.nomad.mrg5s.View.Activities.ActivityMyAudits;
import com.nomad.mrg5s.View.Activities.SettingsActivity;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;
import com.google.firebase.auth.FirebaseAuth;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentLanding extends Fragment {

    private ImageButton botonStart;
    private ImageButton botonEdicion;
    private ImageButton botonaudits;
    private ImageButton botonSettings;

    private TextView texto1;
    private TextView texto2;
    private TextView texto3;
    private TextView texto31;
    private TextView texto4;

    private LinearLayout lin1;
    private LinearLayout lin2;
    private LinearLayout lin3;
    private LinearLayout lin4;
    private Landinable landinable;
    private ImageButton animationTarget;
    private Animation animation;

    private Typeface roboto;

    private SharedPreferences config;

    public FragmentLanding() {
        // Required empty public constructor
    }

    public interface Landinable{
       public void irASelecccionAreas();
       public void salirCompleto();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_landing, container, false);

        botonEdicion = view.findViewById(R.id.btn_issue);
        botonaudits = view.findViewById(R.id.btn_search);
        botonSettings = view.findViewById(R.id.btn_setting);
        botonStart = view.findViewById(R.id.btn_start);


        //region PARAFIREBASEDATABASE
        /*
            ImageButton botonImportar=view.findViewById(R.id.btn_importar);
            ImageButton botonExpo = view.findViewById(R.id.btn_exportar);
            botonImportar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new ControllerDatos(getContext()).traerCuestionariosFirebase();
                }
            });
            botonExpo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Realm realm = Realm.getDefaultInstance();
                    RealmResults<Cuestionario> losCu = realm.where(Cuestionario.class)
                            .findAll();
                    RealmList<Cuestionario>lista=new RealmList<>();
                    lista.addAll(losCu);

                    ControllerDatos controllerDatos = new ControllerDatos(getContext());
                    for (Cuestionario elCues :
                            lista) {
                        controllerDatos.crearCuestionarioFirebase(elCues);
                    }

                }
            });
        */
        //endregion

        texto1 = view.findViewById(R.id.primeraOpcion);
        texto2 = view.findViewById(R.id.segundaOpcion);
        texto3 = view.findViewById(R.id.terceraOpcion);
        texto4 = view.findViewById(R.id.cuartaOpcion);

        lin1 = view.findViewById(R.id.lin1);
        lin2 = view.findViewById(R.id.lin2);
        lin3 = view.findViewById(R.id.lin3);
        lin4 = view.findViewById(R.id.line4);


        roboto = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Roboto-Light.ttf");
        texto1.setTypeface(roboto);
        texto2.setTypeface(roboto);
        texto3.setTypeface(roboto);
        texto4.setTypeface(roboto);

        //DECLARO LOS LISTENER QUE VOY A USAR
        View.OnClickListener listener1 = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                landinable.irASelecccionAreas();
            }
        };
        View.OnClickListener listener2 = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              Intent intent = new Intent(v.getContext(), SettingsActivity.class);
              startActivity(intent);
            }
        };
        View.OnClickListener listener3 = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), ActivityMyAudits.class);
                startActivity(intent);
            }
        };
        View.OnClickListener listener4 = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /* //METODO OBSOLETO PARA CONTACTAR DESARROLLADOR
                Intent send = new Intent(Intent.ACTION_SENDTO);
                String uriText = "mailto:" + Uri.encode("contacto@benomad.com.ar") +
                        "?subject=" + Uri.encode(getResources().getString(R.string.quieroReportar)) +
                        "&body=" + Uri.encode(getResources().getString(R.string.textoIssue));
                Uri uri = Uri.parse(uriText);

                send.setData(uri);
                startActivity(Intent.createChooser(send, getResources().getString(R.string.enviarMail)));
                */
                //METODO PARA ABRIR PANTALLA EDICION CUESTIONARIO Y CRITERIOS


            }
        };

        //ASIGNO LISTENER PARA LOS BOTONES
        
        botonStart.setOnClickListener(listener1);
        lin1.setOnClickListener(listener1);
        texto1.setOnClickListener(listener1);
        
        botonSettings.setOnClickListener(listener2);
        texto3.setOnClickListener(listener2);
        lin3.setOnClickListener(listener2);
        
        botonaudits.setOnClickListener(listener3);
        texto2.setOnClickListener(listener3);
        lin2.setOnClickListener(listener3);
        
        botonEdicion.setOnClickListener(listener4);
        lin4.setOnClickListener(listener4);
        texto4.setOnClickListener(listener4);



        config = getActivity().getSharedPreferences("prefs", 0);
        boolean firstRun = config.getBoolean("firstRunLandingFragment", false);
        if (!firstRun){
            crearDialogoBienvenida();
            SharedPreferences.Editor editor = config.edit();
            editor.putBoolean("firstRunLandingFragment", true);
            editor.commit();
        }
        else{
            if (HTTPConnectionManager.isNetworkingOnline(getContext())){
                DatabaseReference mbase= FirebaseDatabase.getInstance().getReference();

                mbase.child("data").child("version").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String versionFireBase=dataSnapshot.getValue().toString();
                        String versionLocal=null;

                        if (versionFireBase.equals("6.6.6")) {
                            avisarNoVaMas();

                        } else {
                            try {
                                versionLocal = getContext().getPackageManager()
                                        .getPackageInfo(getContext().getPackageName(), 0).versionName;
                            } catch (PackageManager.NameNotFoundException e) {
                                e.printStackTrace();
                            }
                            if ( versionLocal!=null && !versionLocal.equals(versionFireBase)){
                                avisarVersionVieja();
                            }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        }




        return view;
    }

    private void avisarNoVaMas() {
        new MaterialDialog.Builder(getActivity())
                .title(getContext().getString(R.string.advertencia))
                .buttonsGravity(GravityEnum.CENTER)
                .cancelable(false)
                .contentColor(ContextCompat.getColor(getActivity(), R.color.primary_text))
                .backgroundColor(ContextCompat.getColor(getActivity(), R.color.tile1))
                .titleColor(ContextCompat.getColor(getActivity(), R.color.tile4))
                .content(getResources().getString(R.string.yaNoUsar))
                .positiveText(R.string.ok)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
                        landinable.salirCompleto();
                    }
                })
                .show();
    }

    private void avisarVersionVieja() {

        new MaterialDialog.Builder(getActivity())
                .title(getContext().getString(R.string.advertencia))
                .buttonsGravity(GravityEnum.CENTER)
                .contentColor(ContextCompat.getColor(getActivity(), R.color.primary_text))
                .backgroundColor(ContextCompat.getColor(getActivity(), R.color.tile1))
                .titleColor(ContextCompat.getColor(getActivity(), R.color.tile4))
                .content(getResources().getString(R.string.versionVieja))
                .positiveText(R.string.ok)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {

                    }
                })
                .negativeText(getResources().getString(R.string.salir))
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
                    landinable.salirCompleto();
                    }
                })
                .show();
    }

    public void lanzarTuto() {

        new TapTargetSequence(getActivity())
                .targets(
                        TapTarget.forView(getActivity().findViewById(R.id.btn_start), getResources().getString(R.string.tutorial_tit_strt), getResources().getString(R.string.tutorial_desc_strt))
                                .outerCircleColor(R.color.tutorial2)      // Specify a color for the outer circle
                                .outerCircleAlpha(0.85f)            // Specify the alpha amount for the outer circle
                                .textTypeface(roboto)  // Specify a typeface for the text
                                .drawShadow(true)                   // Whether to draw a drop shadow or not
                                .cancelable(false)
                                .id(1)// Whether tapping outside the outer circle dismisses the view
                                .tintTarget(false),
                        TapTarget.forView(getActivity().findViewById(R.id.btn_search), getResources().getString(R.string.tutorial_tit_search), getResources().getString(R.string.tutorial_desc_search))
                                .outerCircleColor(R.color.tutorial1)      // Specify a color for the outer circle
                                .outerCircleAlpha(0.95f)
                                .textColor(R.color.primary_text)
                                .textTypeface(roboto)  // Specify a typeface for the text
                                .drawShadow(true)                   // Whether to draw a drop shadow or not
                                .cancelable(true)
                                .id(2)// Whether tapping outside the outer circle dismisses the view
                                .tintTarget(true),// Whether to tint the target view's color
                        TapTarget.forView(getActivity().findViewById(R.id.btn_setting), getResources().getString(R.string.tutorial_tit_setting), getResources().getString(R.string.tutorial_desc_setting))
                                .outerCircleColor(R.color.tutorial2)      // Specify a color for the outer circle
                                .outerCircleAlpha(0.85f)
                                .textTypeface(roboto)  // Specify a typeface for the text
                                .drawShadow(true)                   // Whether to draw a drop shadow or not
                                .cancelable(true)
                                .id(2)// Whether tapping outside the outer circle dismisses the view
                                .tintTarget(false) )                 // Whether to tint the target view's color

                .listener(new TapTargetSequence.Listener() {
                    // getActivity() listener will tell us when interesting(tm) events happen in regards
                    // to the sequence
                    @Override
                    public void onSequenceFinish() {
                        // Yay
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


//MODIFICAR METODO CON FIREBASE AUTH
    private void crearDialogoBienvenida() {

        new MaterialDialog.Builder(getActivity())
                .title((getResources().getString(R.string.bienVenida))+" "+ FirebaseAuth.getInstance().getCurrentUser().getEmail())
                .buttonsGravity(GravityEnum.CENTER)
                .contentColor(ContextCompat.getColor(getActivity(), R.color.primary_text))
                .backgroundColor(ContextCompat.getColor(getActivity(), R.color.tile1))
                .titleColor(ContextCompat.getColor(getActivity(), R.color.tile4))
                .content(getResources().getString(R.string.bienvenido))
                .positiveText(R.string.si)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
                        SharedPreferences.Editor editor = config.edit();
                        editor.putBoolean("quiereVerTuto",true);
                        editor.putBoolean("estadoTuto",false);
                        editor.commit();

                        lanzarTuto();
                    }
                })
                .negativeText(getResources().getString(R.string.no))
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
                        SharedPreferences.Editor editor = config.edit();
                        editor.putBoolean("quiereVerTuto",false);
                        editor.putBoolean("estadoTuto",true);
                        editor.commit();
                        //escribir las sharedPreferences para todos los fragments
                    }
                })
                .show();

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.landinable =(Landinable)context;
    }

    @Override
    public void onResume() {
        SharedPreferences conf=getActivity().getSharedPreferences("prefs",0);
        SharedPreferences.Editor editor = conf.edit();
        Boolean estadoTuto=conf.getBoolean("estadoTuto",false);
        Boolean quiereVerTuto = conf.getBoolean("quiereVerTuto",false);
        Boolean primeraVezLanding=conf.getBoolean("primeraVezFragmentLanding",false);
        Boolean firstRun = conf.getBoolean("firstRun", false);

        if (!firstRun){
            crearDialogoBienvenida();
            editor.putBoolean("firstRun", true);
            editor.commit();
        }
        else{
            if (!estadoTuto&&quiereVerTuto){
                if (!primeraVezLanding){
                    editor.putBoolean("primeraVezFragmentLanding",true);
                    editor.commit();
                    lanzarTuto();
                }
            }
        }
        super.onResume();
    }
}
