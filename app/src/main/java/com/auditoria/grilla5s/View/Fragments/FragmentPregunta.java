package com.auditoria.grilla5s.View.Fragments;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatRadioButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.auditoria.grilla5s.Model.Foto;
import com.auditoria.grilla5s.Model.Pregunta;
import com.auditoria.grilla5s.R;
import com.auditoria.grilla5s.Utils.FuncionesPublicas;
import com.auditoria.grilla5s.View.Activities.ActivityAuditoria;
import com.auditoria.grilla5s.View.Adapter.AdapterFotos;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import id.zelory.compressor.Compressor;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;
import pl.tajchert.nammu.Nammu;
import pl.tajchert.nammu.PermissionCallback;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentPregunta extends Fragment {



    public static final String ENUNCIADOPREGUNTA="ENUNCIADOPREGUNTA";
    public static final String IDPREGUNTA="IDPREGUNTA";
    public static final String IDITEM="IDITEM";
    public static final String IDAUDITORIA="IDAUDITORIA";

    private File fotoOriginal;
    private File fotoComprimida;
    private RecyclerView recyclerFotos;
    private AdapterFotos adapterFotos;
    private LinearLayoutManager layoutManager;
    private Integer puntuacion;

    private Avisable avisable;
    private SharedPreferences config;
    private LinearLayout linear;
    private Toolbar toolbar;

    RealmList<Foto> listaFotos;

    private String enunciado;
    private String idPregunta;
    private String idAudit;
    private String idItem;

    private TextView textViewEnunciado;
    private RadioGroup rg1;

    private AppCompatRadioButton rb0;
    private AppCompatRadioButton rb1;
    private AppCompatRadioButton rb2;
    private AppCompatRadioButton rb3;
    private AppCompatRadioButton rb4;
    private AppCompatRadioButton rb5;


    private Button verCriterio;

    private FloatingActionMenu fabMenu;
    private FloatingActionButton fabCamara;
    private FloatingActionButton fabGuardar;
    private FloatingActionButton fabSalir;

    private Foto unaFoto;


    public FragmentPregunta() {
        // Required empty public constructor
    }

    public interface Avisable{
        public void cerrarAuditoria();
        public void salirDeAca();
        public void mostrarToolbar();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view= inflater.inflate(R.layout.fragment_pregunta, container, false);



        Bundle bundle=getArguments();
        if (bundle==null){
            Toast.makeText(getContext(), getResources().getString(R.string.noEncontroDatos), Toast.LENGTH_SHORT).show();
        }
        else{

            idAudit=bundle.getString(IDAUDITORIA);
            idItem=bundle.getString(IDITEM);
            idPregunta=bundle.getString(IDPREGUNTA);
            enunciado=bundle.getString(ENUNCIADOPREGUNTA);
        }

        rg1=(RadioGroup) view.findViewById(R.id.rg1);
        verCriterio=(Button)view.findViewById(R.id.btn_criterios);
        rb0 = view.findViewById(R.id.item0);
        rb1 = view.findViewById(R.id.item1);
        rb2 = view.findViewById(R.id.item2);
        rb3 = view.findViewById(R.id.item3);
        rb4 = view.findViewById(R.id.item4);
        rb5 = view.findViewById(R.id.item5);
        textViewEnunciado= view.findViewById(R.id.textoEnunciado);

        linear=view.findViewById(R.id.vistaCentral);

        rb0.setText("1");
        rb1.setText("2");
        rb2.setText("3");
        rb3.setText("4");
        rb4.setText("5");
        rb5.setText("6");
        textViewEnunciado.setText(enunciado);


//        HANDLE RADIOGROUP

        //In Activity

        rg1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                puntuacion =rg1.indexOfChild(view.findViewById(rg1.getCheckedRadioButtonId()))+1;

                Realm realm = Realm.getDefaultInstance();
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {

                        Pregunta preg = realm.where(Pregunta.class)
                                .equalTo("IdAudit",idAudit)
                                .equalTo("idPregunta",idPregunta)
                                .findFirst();
                        preg.setPuntaje(puntuacion);
                    }
                });
            }
        });


        verCriterio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialDialog.Builder(v.getContext())
                        .title(R.string.criteriaTitulo)
                        .contentColor(ContextCompat.getColor(v.getContext(), R.color.primary_text))
                        .titleColor(ContextCompat.getColor(v.getContext(), R.color.tile4))
                        .backgroundColor(ContextCompat.getColor(v.getContext(), R.color.tile1))
                        .content("MOSTRAR CRITERIOS")
                        .positiveText(getResources().getString(R.string.dismiss))
                        .show();
            }
        });




//        RecyclerView FOTOS
        recyclerFotos= view.findViewById(R.id.recyclerFotos);
        layoutManager= new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerFotos.setLayoutManager(layoutManager);
        adapterFotos= new AdapterFotos();
        adapterFotos.setContext(getContext());
        listaFotos=cargarFotos();
        adapterFotos.setListaFotosOriginales(listaFotos);
        recyclerFotos.setAdapter(adapterFotos);

        View.OnClickListener listenerComentario=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer posicion=recyclerFotos.getChildAdapterPosition(v);
                RealmList<Foto> unaLista=adapterFotos.getListaFotosOriginales();
                Foto unaFoto=unaLista.get(posicion);
                crearDialogoParaModificarComentario(unaFoto);
                adapterFotos.notifyDataSetChanged();

            }
        };
        adapterFotos.setListener(listenerComentario);
        adapterFotos.notifyDataSetChanged();


        //agregar los fabs al menu
        fabMenu=(FloatingActionMenu)view.findViewById(R.id.fab_menu);
        fabMenu.setMenuButtonColorNormal(ContextCompat.getColor(getContext(),R.color.colorAccent));



        fabCamara = new FloatingActionButton(getActivity());
        fabCamara.setColorNormal(ContextCompat.getColor(getContext(), R.color.tile3));
        fabCamara.setButtonSize(FloatingActionButton.SIZE_MINI);
        fabCamara.setLabelText(getString(R.string.sacarFoto));
        fabCamara.setImageResource(R.drawable.ic_camera_alt_black_24dp);
        fabMenu.addMenuButton(fabCamara);

            fabCamara.setLabelColors(ContextCompat.getColor(getActivity(), R.color.tile3),
                    ContextCompat.getColor(getActivity(), R.color.light_grey),
                    ContextCompat.getColor(getActivity(), R.color.white_transparent));
            fabCamara.setLabelTextColor(ContextCompat.getColor(getActivity(), R.color.black));

        fabCamara.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (FuncionesPublicas.isExternalStorageWritable()) {
                    if (Nammu.checkPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        fabMenu.close(true);
                        EasyImage.openCamera(FragmentPregunta.this, 1);
                    }
                    else {

//                      PIDO PERMISO PARA USAR LA MEMORIA EXTERNA

                        if (Nammu.shouldShowRequestPermissionRationale(FragmentPregunta.this,android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                            //User already refused to give us this permission or removed it
                            //Now he/she can mark "never ask again" (sic!)
                            Snackbar.make(getView(), getResources().getString(R.string.appNecesitaPermiso),
                                    Snackbar.LENGTH_INDEFINITE).setAction(getResources().getString(R.string.ok), new View.OnClickListener() {
                                @Override public void onClick(View view) {
                                    Nammu.askForPermission(FragmentPregunta.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                            new PermissionCallback() {
                                                @Override
                                                public void permissionGranted() {
                                                    fabMenu.close(true);
                                                    EasyImage.openCamera(FragmentPregunta.this, 1);
                                                }

                                                @Override
                                                public void permissionRefused() {
                                                    Toast.makeText(getContext(), getResources().getString(R.string.damePermiso), Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }
                            }).show();
                        } else {
                            //First time asking for permission
                            // or phone doesn't offer permission
                            // or user marked "never ask again"
                            Nammu.askForPermission(FragmentPregunta.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                    new PermissionCallback() {
                                        @Override
                                        public void permissionGranted() {
                                            fabMenu.close(true);
                                            EasyImage.openCamera(FragmentPregunta.this, 1);
                                        }

                                        @Override
                                        public void permissionRefused() {
                                            Toast.makeText(getContext(), getResources().getString(R.string.damePermiso), Toast.LENGTH_SHORT).show();

                                        }
                                    });
                        }
                    }
                }
                else {
                    new MaterialDialog.Builder(getContext())
                            .title(getResources().getString(R.string.titNoMemoria))
                            .contentColor(ContextCompat.getColor(getContext(), R.color.primary_text))
                            .backgroundColor(ContextCompat.getColor(getContext(), R.color.tile1))
                            .titleColor(ContextCompat.getColor(getContext(), R.color.tile4))
                            .content(getResources().getString(R.string.noMemoria))
                            .positiveText(getResources().getString(R.string.ok))
                            .show();
                }
            }
        });



        fabGuardar = new FloatingActionButton(getActivity());
        fabGuardar.setButtonSize(FloatingActionButton.SIZE_MINI);
        fabGuardar.setColorNormal(ContextCompat.getColor(getContext(), R.color.tile3));
        fabGuardar.setLabelText(getString(R.string.guardarAuditoria));
        fabGuardar.setImageResource(R.drawable.ic_save_black_24dp);
        fabMenu.addMenuButton(fabGuardar);

            fabGuardar.setLabelColors(ContextCompat.getColor(getActivity(), R.color.tile3),
                    ContextCompat.getColor(getActivity(), R.color.light_grey),
                    ContextCompat.getColor(getActivity(), R.color.white_transparent));
            fabGuardar.setLabelTextColor(ContextCompat.getColor(getActivity(), R.color.black));

        fabGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //--guardar auditoria en firebase--//

                fabMenu.close(true);
               completoTodosLosPuntos();

            }
        });

        fabSalir = new FloatingActionButton(getActivity());
        fabSalir.setButtonSize(FloatingActionButton.SIZE_MINI);
        fabSalir.setColorNormal(ContextCompat.getColor(getContext(), R.color.tile3));
        fabSalir.setLabelText(getString(R.string.salir));
        fabSalir.setImageResource(R.drawable.ic_exit_to_app_black_24dp);
        fabMenu.addMenuButton(fabSalir);

            fabSalir.setLabelColors(ContextCompat.getColor(getActivity(), R.color.tile3),
                    ContextCompat.getColor(getActivity(), R.color.light_grey),
                    ContextCompat.getColor(getActivity(), R.color.white_transparent));
            fabSalir.setLabelTextColor(ContextCompat.getColor(getActivity(), R.color.black));

        fabSalir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new MaterialDialog.Builder(getContext())
                        .title("Warning!")
                        .contentColor(ContextCompat.getColor(getContext(), R.color.primary_text))
                        .titleColor(ContextCompat.getColor(getContext(), R.color.tile4))
                        .backgroundColor(ContextCompat.getColor(getContext(), R.color.tile1))
                        .content(getResources().getString(R.string.auditoriaSinTerminar)+"\n"+getResources().getString(R.string.guardardar))
                        .positiveText(getResources().getString(R.string.si))
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                avisable.salirDeAca();
                            }
                        })
                        .negativeText(getResources().getString(R.string.no))
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                                FuncionesPublicas.borrarAuditoriaSeleccionada(ActivityAuditoria.idAuditoria);
                                //aca lo que pasa si voy para atras
                                fabMenu.close(true);
                                avisable.salirDeAca();
                            }
                        })
                        .show();

            }
        });

        config = getActivity().getSharedPreferences("prefs",0);
        boolean quiereVerTuto = config.getBoolean("quiereVerTuto",false);
        boolean primeraVezFragmentSubitem =config.getBoolean("primeraVezFragmentSubitem",false);

        //SI EL USUARIO ELIGIO VER TUTORIALES ME FIJO SI YA PASO POR ESTA PAGINA.
        if (quiereVerTuto) {
            if (!primeraVezFragmentSubitem) {

                SharedPreferences.Editor editor = config.edit();
                editor.putBoolean("primeraVezFragmentSubitem",true);
                editor.commit();
                seguirConTutorial();
            }
        }

        return view;
    }

    private void seguirConTutorial() {
        Typeface roboto = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Roboto-Light.ttf");
        new TapTargetSequence(getActivity())
                .targets(
                        TapTarget.forView(linear, getResources().getString(R.string.tutorial_tit_subitem_inicial), getResources().getString(R.string.tutorial_desc_subitem_inicial))
                                .transparentTarget(true)
                                .textColor(R.color.primary_text)
                                .outerCircleColor(R.color.tutorial1)      // Specify a color for the outer circle
                                .outerCircleAlpha(0.95f)            // Specify the alpha amount for the outer circle
                                .textTypeface(roboto)  // Specify a typeface for the text
                                .drawShadow(true)                   // Whether to draw a drop shadow or not
                                .cancelable(false)                  // Whether tapping outside the outer circle dismisses the view
                                .tintTarget(false)
                                .icon(getResources().getDrawable(R.drawable.ic_check_black_24dp))
                                .id(1),                   // Whether to tint the target view's color
                        TapTarget.forView(verCriterio, getResources().getString(R.string.tutorial_tit_subitem_criteria), getResources().getString(R.string.tutorial_desc_subitem_criteria))
                                .transparentTarget(true)
                                .outerCircleColor(R.color.tutorial2)
                                .textColor(R.color.blancoNomad)// Specify a color for the outer circle
                                .outerCircleAlpha(0.95f)            // Specify the alpha amount for the outer circle
                                .textTypeface(roboto)  // Specify a typeface for the text
                                .drawShadow(true)                   // Whether to draw a drop shadow or not
                                .cancelable(false)                  // Whether tapping outside the outer circle dismisses the view
                                .tintTarget(false)
                                .targetRadius(75)
                                .id(2),                 // Whether to tint the target view's color
                        TapTarget.forView(fabCamara, getResources().getString(R.string.tutorial_tit_subitem_foto), getResources().getString(R.string.tutorial_desc_subitem_foto))
                                .transparentTarget(true)
                                .outerCircleColor(R.color.tutorial1)      // Specify a color for the outer circle
                                .textColor(R.color.primary_text)
                                .outerCircleAlpha(0.95f)            // Specify the alpha amount for the outer circle
                                .textTypeface(roboto)  // Specify a typeface for the text
                                .drawShadow(true)                   // Whether to draw a drop shadow or not
                                .cancelable(false)                  // Whether tapping outside the outer circle dismisses the view
                                .tintTarget(false)
                                .id(4),
                        TapTarget.forView(fabGuardar, getResources().getString(R.string.tutorial_tit_guardar), getResources().getString(R.string.tutorial_desc_guardar))
                                .outerCircleColor(R.color.tutorial2)      // Specify a color for the outer circle
                                .transparentTarget(true)
                                .outerCircleAlpha(0.85f)            // Specify the alpha amount for the outer circle
                                .textTypeface(roboto)  // Specify a typeface for the text
                                .drawShadow(true)                   // Whether to draw a drop shadow or not
                                .cancelable(false)                  // Whether tapping outside the outer circle dismisses the view
                                .tintTarget(false)
                                .id(5)
                )

                .listener(new TapTargetSequence.Listener() {
                    // This listener will tell us when interesting(tm) events happen in regards
                    // to the sequence
                    @Override
                    public void onSequenceFinish() {
                        // Yay
                        fabMenu.close(true);
                        avisable.mostrarToolbar();
                    }

                    @Override
                    public void onSequenceStep(TapTarget tapTarget, boolean b) {
                        if (tapTarget.id()==2){
                            fabMenu.open(true);
                        }


                    }

                    @Override
                    public void onSequenceCanceled(TapTarget lastTarget) {
                        // Boo
                    }
                })
                .start();
    }


    private void completoTodosLosPuntos() {
        Integer cantFotos=0;
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Pregunta> result2 = realm.where(Pregunta.class)
                .equalTo("idAudit", idAudit)
                .equalTo("idItem",idItem)
                .findAll();

        List<String> unaLista=new ArrayList<>();

        for (Pregunta unaPreg :result2
             ) {
            if (unaPreg.getPuntaje()==null|| unaPreg.getPuntaje()==0){
                unaLista.add(unaPreg.getIdPregunta());
            }
        }

        if (unaLista.size()>0){
            new MaterialDialog.Builder(getContext())
                    .title("Warning!")
                    .title(getResources().getString(R.string.advertencia))
                    .contentColor(ContextCompat.getColor(getContext(), R.color.primary_text))
                    .titleColor(ContextCompat.getColor(getContext(), R.color.tile4))
                    .backgroundColor(ContextCompat.getColor(getContext(), R.color.tile1))
                    .content(getResources().getString(R.string.auditoriaSinTerminar)+"\n"+getResources().getString(R.string.continuar))
                    .positiveText(getResources().getString(R.string.si))
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            avisable.cerrarAuditoria();
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
        else{

            avisable.cerrarAuditoria();
        }
    }




    public static FragmentPregunta CrearfragmentPregunta(Pregunta laPregunta) {
        FragmentPregunta detalleFragment = new FragmentPregunta();
        Bundle unBundle = new Bundle();

            unBundle.putString(ENUNCIADOPREGUNTA, laPregunta.getTextoPregunta());
            unBundle.putString(IDITEM, laPregunta.getIdItem());
            unBundle.putString(IDPREGUNTA, laPregunta.getIdPregunta());
            unBundle.putString(IDAUDITORIA, laPregunta.getIdAudit());

        detalleFragment.setArguments(unBundle);
        return detalleFragment;
    }


    @Override
    public void onActivityResult( int  requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        EasyImage.handleActivityResult(requestCode, resultCode, data, getActivity(), new DefaultCallback() {
            @Override
            public void onImagePickerError(Exception e, EasyImage.ImageSource source, int type) {
                //Some error handling
            }

            @Override
            public void onImagePicked(File imageFile, EasyImage.ImageSource source, int type) {

                if (type == 1) {
                    fotoOriginal = imageFile;
                    if (existeDirectorioImagenes()) {
                        try {
                            fotoComprimida = new Compressor(getContext())
                                    .setMaxWidth(640)
                                    .setMaxHeight(480)
                                    .setQuality(75)
                                    .setCompressFormat(Bitmap.CompressFormat.JPEG)
                                    .setDestinationDirectoryPath(getContext().getExternalFilesDir(null)+ File.separator + "nomad" + File.separator + "audit5s"+ File.separator  + FirebaseAuth.getInstance().getCurrentUser().getEmail() + File.separator + "images" + File.separator + "evidencias")
                                    .compressToFile(fotoOriginal);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        unaFoto=new Foto();
                        unaFoto.setIdFoto("foto_"+ UUID.randomUUID());
                        unaFoto.setRutaFoto(fotoComprimida.getAbsolutePath());
                        unaFoto.setIdAudit(idAudit);
                        unaFoto.setIdPregunta(idPregunta);


                        Realm realm = Realm.getDefaultInstance();
                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                Pregunta preg = realm.where(Pregunta.class)
                                        .equalTo("idAudit", idAudit)
                                        .equalTo("idPregunta", idPregunta)
                                        .findFirst();

                                    realm.copyToRealmOrUpdate(unaFoto);

                                final Foto foto = realm.where(Foto.class)
                                        .equalTo("idFoto", unaFoto.getIdFoto())
                                        .findFirst();

                                preg.getListaFotos().add(foto);
                                //posible bug dejar esto adentro
                            }
                        });
                        crearDialogoParaModificarComentario(unaFoto);
                        listaFotos.add(unaFoto);
                        adapterFotos.notifyDataSetChanged();

                        Boolean seBorro = imageFile.delete();
                        if (seBorro) {
                            //                        Toast.makeText(getContext(), "borrada con exito", Toast.LENGTH_SHORT).show();
                        } else {
                            //                        Toast.makeText(getContext(), "No se pudo borrar", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }

            @Override
            public void onCanceled(EasyImage.ImageSource source, int type) {
                super.onCanceled(source, type);
                //Cancel handling, you might wanna remove taken photo if it was canceled
                if (source == EasyImage.ImageSource.CAMERA) {
                    File photoFile = EasyImage.lastlyTakenButCanceledPhoto(getActivity());
                    if (photoFile != null) photoFile.delete();
                }
            }
        });
    }


    public Boolean  existeDirectorioImagenes(){
        Boolean sePudo=true;
        File dir = new File( getContext().getExternalFilesDir(null)+ File.separator + "nomad" + File.separator + "audit5s"+ File.separator + FirebaseAuth.getInstance().getCurrentUser().getEmail() + File.separator + "images" + File.separator + "evidencias");
        if(!dir.exists() || !dir.isDirectory()) {
            sePudo=dir.mkdirs();
        }
        return sePudo;

    }

    public RealmList<Foto> cargarFotos(){
        RealmList<Foto> unaLista= new RealmList<>();

        Realm realm = Realm.getDefaultInstance();
        Pregunta preg=realm.where(Pregunta.class)
                .equalTo("idAudit", idAudit)
                .equalTo("idPregunta",idPregunta)
                .findFirst();

        if (preg==null||preg.getListaFotos().size()<1){
            return new RealmList<>();
        }
        else{
            unaLista.addAll(preg.getListaFotos());
            return unaLista;
        }

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.avisable = (Avisable)context;
    }


    public void crearDialogoParaModificarComentario(final Foto unaFoto){

        new MaterialDialog.Builder(getContext())
                .title(getResources().getString(R.string.agregarComentario))
                .contentColor(ContextCompat.getColor(getContext(), R.color.primary_text))
                .backgroundColor(ContextCompat.getColor(getContext(), R.color.tile1))
                .titleColor(ContextCompat.getColor(getContext(), R.color.tile4))
                .content(getResources().getString(R.string.favorAgregueComentario))
                .inputType(InputType.TYPE_CLASS_TEXT)
                .inputRange(0,40)
                .input(getResources().getString(R.string.comment),"", new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, final CharSequence input) {

                        Realm realm= Realm.getDefaultInstance();
                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                               Foto foto = realm.where(Foto.class)
                                       .equalTo("idFoto",unaFoto.getIdFoto())
                                       .findFirst();

                                if (foto!=null) {
                                    foto.setComentarioFoto(input.toString());
                                }
                                adapterFotos.notifyDataSetChanged();
                            }
                        });
                    }
                }).show();
    }

    //DOY DE ALTA LA FOTO EN REALM

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        Nammu.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

}




