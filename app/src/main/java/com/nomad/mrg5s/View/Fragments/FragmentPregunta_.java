package com.nomad.mrg5s.View.Fragments;


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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.nomad.mrg5s.Model.Auditoria;
import com.nomad.mrg5s.Model.Criterio;
import com.nomad.mrg5s.Model.Ese;
import com.nomad.mrg5s.Model.Foto;
import com.nomad.mrg5s.Model.Item;
import com.nomad.mrg5s.Model.Pregunta;
import com.nomad.mrg5s.R;
import com.nomad.mrg5s.Utils.FuncionesPublicas;
import com.nomad.mrg5s.View.Activities.ActivityAuditoria;
import com.nomad.mrg5s.View.Adapter.AdapterFotos;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.firebase.auth.FirebaseAuth;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import id.zelory.compressor.Compressor;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import io.realm.Sort;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;
import pl.tajchert.nammu.Nammu;
import pl.tajchert.nammu.PermissionCallback;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentPregunta_ extends Fragment {



    public static final String ENUNCIADOPREGUNTA="ENUNCIADOPREGUNTA";
    public static final String IDCUESTIONARIO ="IDCUESTIONARIO";
    public static final String IDPREGUNTA="IDPREGUNTA";
    public static final String IDITEM="IDITEM";
    public static final String IDAUDITORIA="IDAUDITORIA";
    private static final String ORIGEN = "ORIGEN";
    private static final String IDESE = "IDESE";

    private File fotoOriginal;
    private File fotoComprimida;
    private RecyclerView recyclerFotos;
    private AdapterFotos adapterFotos;
    private AdapterFotos adapterFotosViejas;
    private TextView tagCommentNuevo;
    private TextView tagCommentViejo;

    private Integer puntuacion;

    private Avisable avisable;
    private LinearLayout linear;

    RealmList<Foto> listaFotos;

    private String enunciado;
    private String idPregunta;
    private String idAudit;
    private String idEse;
    private String idItem;
    private String idCuestionario;

    private TextView textViewCommentNuevo;
    private TextView textViewCommentViejo;
    private RadioGroup rg1;
    private TextView tituloAuditVieja;

    private TextView evidenciaNueva;

    private RadioGroup rg2;


    private FloatingActionMenu fabMenu;
    private FloatingActionButton fabCamara;
    private FloatingActionButton fabGuardar;

    private RadioGroup.OnCheckedChangeListener listener1;
    private RadioGroup.OnCheckedChangeListener listener2;

    private ImageView separador;
    private ImageView separadorInvertido;


    private Foto unaFoto;
    private RecyclerView recyclerFotosViejas;
    private String origen;


    public FragmentPregunta_() {
        // Required empty public constructor
    }

    public interface Avisable{
        void cerrarAuditoria();
        void salirDeAca();
        void borrarFoto(Foto unaFoto);
        void zoomearImagen(Foto unaFoto);
        void actualizarPuntaje(String idAudit);
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view= inflater.inflate(R.layout.fragment_pregunta_, container, false);



        Bundle bundle=getArguments();
        if (bundle==null){
            Toast.makeText(getContext(), getResources().getString(R.string.noEncontroDatos), Toast.LENGTH_SHORT).show();
        }
        else{

            idAudit=bundle.getString(IDAUDITORIA);
            idItem=bundle.getString(IDITEM);
            idPregunta=bundle.getString(IDPREGUNTA);
            enunciado=bundle.getString(ENUNCIADOPREGUNTA);
            origen=bundle.getString(ORIGEN);
            idCuestionario=bundle.getString(IDCUESTIONARIO);
            idEse=bundle.getString(IDESE);
        }

        rg1= view.findViewById(R.id.rg1);
        rg2= view.findViewById(R.id.rg2);
        AppCompatRadioButton rb0 = view.findViewById(R.id.item0);
        AppCompatRadioButton rb1 = view.findViewById(R.id.item1);
        AppCompatRadioButton rb2 = view.findViewById(R.id.item2);
        AppCompatRadioButton rb3 = view.findViewById(R.id.item3);
        AppCompatRadioButton rb6 = view.findViewById(R.id.itemNA);

        TextView tagrb0 = view.findViewById(R.id.tagRg0);
        TextView tagrb1 = view.findViewById(R.id.tagRg1);
        TextView tagrb2 = view.findViewById(R.id.tagRg2);
        TextView tagrb3 = view.findViewById(R.id.tagRg3);
        TextView tagrb6 = view.findViewById(R.id.tagRgNa);

        TextView textoPregunta = view.findViewById(R.id.textoPregunta);
        textViewCommentNuevo = view.findViewById(R.id.tv_comment_nuevo);
        textViewCommentViejo = view.findViewById(R.id.tv_comment_viejo);
        tagCommentNuevo=view.findViewById(R.id.tv_tagCommentNuevo);
        tagCommentViejo=view.findViewById(R.id.tv_tagCommentViejo);
        tituloAuditVieja =view.findViewById(R.id.tv_fotos_viejas);
        evidenciaNueva = view.findViewById(R.id.tv_fotos_nuevas);
        separador=view.findViewById(R.id.SeparadorSuperior);
        separadorInvertido=view.findViewById(R.id.SeparadorInferior);
        TextView criterioTitulo = view.findViewById(R.id.tituloCriterio);
        TextView criterioDescripcion = view.findViewById(R.id.descripcionCriterio);
        linear=view.findViewById(R.id.vistaCentral);


        Realm realm = Realm.getDefaultInstance();
//        AGREGO TIULO DE ESE
        Ese laEseTitulo=realm.where(Ese.class)
                .equalTo("idAudit",idAudit)
                .equalTo("idEse", idEse)
                .sort("numeroEse", Sort.ASCENDING)
                .findFirst();
        if (laEseTitulo!=null){
            criterioTitulo.setText(laEseTitulo.getNombreEse());
        }

//      SI HAY AGREGO TITULO DE ITEM
        Item elItem;
        if (origen.equals(FuncionesPublicas.EDITAR_CUESTIONARIO)) {
            elItem = realm.where(Item.class)
                    .equalTo("idCuestionario",idCuestionario)
                    .equalTo("idItem",idItem)
                    .equalTo("idEse", idEse)
                    .findFirst();
        }
        else{
            elItem = realm.where(Item.class)

                    .equalTo("idAudit",idAudit)
                    .equalTo("idItem",idItem)
                    .equalTo("idEse",idEse)
                    .findFirst();
        }

        if (elItem!=null) {
            criterioDescripcion.setVisibility(View.VISIBLE);
            criterioDescripcion.setText(elItem.getTextoItem());
        }
        else {
            criterioDescripcion.setVisibility(View.GONE);
        }

        //---SI LA AUDITORIA YA ESTABA EMPEZADA QUE COMPLETE LOS RADIOBUTTONS Y LOS COMENTARIOS GENERALES---//

        Pregunta pregunta = realm.where(Pregunta.class)
                .equalTo("idAudit",idAudit)
                .equalTo("idPregunta",idPregunta)
                .findFirst();
        if (pregunta!=null && pregunta.getPuntaje()!=null){
            if (pregunta.getPuntaje()==0) {
                Toast.makeText(getContext(), "asd", Toast.LENGTH_SHORT).show();
                //no marca ninguno
            }
            else if( pregunta.getPuntaje()!=9){
             Integer puntaje=pregunta.getPuntaje();
                RadioButton unRadioButton=(RadioButton) (rg1.getChildAt(puntaje-1));
                unRadioButton.setChecked(true);
            }
            else{
                RadioButton unRadioButton=(RadioButton) (rg2.getChildAt(0));
                unRadioButton.setChecked(true);
            }
        }
        if (pregunta!=null&&pregunta.getComentario()!=null){
            tagCommentNuevo.setVisibility(View.VISIBLE);
            textViewCommentNuevo.setVisibility(View.VISIBLE);
            textViewCommentNuevo.setText(pregunta.getComentario());
            evidenciaNueva.setVisibility(View.VISIBLE);

        }

        textoPregunta.setText(enunciado);

        //TRAIGO LOS CRITERIOS

       RealmResults<Criterio>rr_listaCriterio;

        if (elItem==null){
            rr_listaCriterio =realm.where(Criterio.class)
                    .equalTo("idAudit", idAudit)
                    .equalTo("idEse", idEse)
                    .equalTo("idPregunta", idPregunta)
                    .sort("orden", Sort.ASCENDING)
                    .findAll();
        }
        else{
            rr_listaCriterio =realm.where(Criterio.class)
                    .equalTo("idAudit", idAudit)
                    .equalTo("idEse", idEse)
                    .equalTo("idItem", idItem)
                    .equalTo("idPregunta", idPregunta)
                    .sort("orden", Sort.ASCENDING)
                    .findAll();
        }
       if (rr_listaCriterio!=null){
            RealmList<Criterio>listaCriterio=new RealmList<>();
            listaCriterio.addAll(rr_listaCriterio);
           tagrb0.setText(String.valueOf(listaCriterio.get(0).getPuntajeCriterio()));
           tagrb1.setText(String.valueOf(listaCriterio.get(1).getPuntajeCriterio()));
           tagrb2.setText(String.valueOf(listaCriterio.get(2).getPuntajeCriterio()));
           tagrb3.setText(String.valueOf(listaCriterio.get(3).getPuntajeCriterio()));

           rb0.setText(listaCriterio.get(0).getTextoCriterio());
           rb1.setText(listaCriterio.get(1).getTextoCriterio());
           rb2.setText(listaCriterio.get(2).getTextoCriterio());
           rb3.setText(listaCriterio.get(3).getTextoCriterio());

       }
       tagrb6.setText("--");
       rb6.setText(getContext().getString(R.string.na));







        //agregar los fabs al menu
        fabMenu= view.findViewById(R.id.fab_menu);
        fabMenu.setMenuButtonColorNormal(ContextCompat.getColor(getContext(),R.color.colorAccent));


        if (origen.equals(FuncionesPublicas.NUEVA_AUDITORIA) || origen.equals(FuncionesPublicas.EDITAR_AUDITORIA)) {
//        HANDLE RADIOGROUP
            //LISTENER PARA EL RADIOGROUP

            listener1= new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {

                    if (checkedId!=-1) {
                            puntuacion =rg1.indexOfChild(view.findViewById(rg1.getCheckedRadioButtonId()))+1;
                        Realm realm = Realm.getDefaultInstance();
                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(@NonNull Realm realm) {

                                Pregunta preg = realm.where(Pregunta.class)
                                        .equalTo("idAudit",idAudit)
                                        .equalTo("idPregunta",idPregunta)
                                        .findFirst();
                                if (preg!=null) {
                                    preg.setPuntaje(puntuacion);
                                }


                            }
                        });

                        limpiarRadioGroups(1);

                    }
                }
            };

            listener2 =new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {

                    if (checkedId !=-1) {
                        Realm realm = Realm.getDefaultInstance();
                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(@NonNull Realm realm) {

                                Pregunta preg = realm.where(Pregunta.class)
                                        .equalTo("idAudit",idAudit)
                                        .equalTo("idPregunta", idPregunta)
                                        .findFirst();
                                if (preg!=null) {
                                    preg.setPuntaje(9);
                                }

                            }
                        });
                        limpiarRadioGroups(2);

                    }
                }
            };

            rg1.setOnCheckedChangeListener(listener1);
            rg2.setOnCheckedChangeListener(listener2);





//      RECYCLERVIEW FOTOS
            recyclerFotos= view.findViewById(R.id.recyclerFotos);
            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
            recyclerFotos.setLayoutManager(layoutManager);
            adapterFotos= new AdapterFotos();
            adapterFotos.setContext(getContext());
            listaFotos=cargarFotos();
            adapterFotos.setListaFotosOriginales(listaFotos);
            recyclerFotos.setAdapter(adapterFotos);

            View.OnClickListener listenerFotoNueva =new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Integer posicion=recyclerFotos.getChildAdapterPosition(v);
                    RealmList<Foto> unaLista=adapterFotos.getListaFotosOriginales();
                    Foto unaFoto=unaLista.get(posicion);
                    avisable.zoomearImagen(unaFoto);

                }
            };
            View.OnLongClickListener listenerBorrarFoto = new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {

                    Integer posicion = recyclerFotos.getChildAdapterPosition(view);
                    RealmList<Foto> unaLista=adapterFotos.getListaFotosOriginales();
                    final Foto unaFoto=unaLista.get(posicion);

                    Realm realm=Realm.getDefaultInstance();
                    Auditoria mAudit= null;
                    if (unaFoto != null) {
                        mAudit = realm.where(Auditoria.class)
                                .equalTo("idAuditoria",unaFoto.getIdAudit())
                                .findFirst();
                    }

                    if (mAudit!=null && !mAudit.getAuditEstaCerrada()) {
                        new MaterialDialog.Builder(getContext())
                                .title(getResources().getString(R.string.borraFoto))
                                .contentColor(ContextCompat.getColor(getContext(), R.color.primary_text))
                                .backgroundColor(ContextCompat.getColor(getContext(), R.color.tile1))
                                .titleColor(ContextCompat.getColor(getContext(), R.color.tile4))
                                .content(getResources().getString(R.string.deseaBorrarFoto))
                                .negativeText(getResources().getString(R.string.no))
                                .positiveText(getResources().getString(R.string.si))
                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                        avisable.borrarFoto(unaFoto);
                                        adapterFotos.borrarFoto(unaFoto);
                                        adapterFotos.notifyDataSetChanged();

                                    }
                                })
                                .show();

                    }
                    return false;
                }
            };

            adapterFotos.setListener(listenerFotoNueva);
            //si estoy en modo revision no le permito que haga longclick
            if (origen.equals(FuncionesPublicas.REVISAR)) {
               adapterFotos.setLongListener(new View.OnLongClickListener() {
                   @Override
                   public boolean onLongClick(View view) {
                       return false;
                   }
               });
            }
            else{
                adapterFotos.setLongListener(listenerBorrarFoto);
                adapterFotos.notifyDataSetChanged();
            }

//      RECYCLERVIEW FOTOS AUDITORIA PREVIA Y COMMENT AUDITORIA VIEJA

            recyclerFotosViejas= view.findViewById(R.id.recyclerFotosViejas);
            LinearLayoutManager layoutManagerViejo = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
            recyclerFotosViejas.setLayoutManager(layoutManagerViejo);
            adapterFotosViejas= new AdapterFotos();
            adapterFotosViejas.setContext(getContext());

//      ESTE METODO LE CARGA LAS FOTOS VIEJAS Y EL COMENTARIO GRAL
            cargarFotosViejas();
            recyclerFotosViejas.setAdapter(adapterFotosViejas);
            View.OnClickListener listenerZoomViejo=new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Integer posicion=recyclerFotosViejas.getChildAdapterPosition(v);
                    RealmList<Foto> unaLista=adapterFotosViejas.getListaFotosOriginales();
                    Foto unaFoto=unaLista.get(posicion);
                    avisable.zoomearImagen(unaFoto);

                }
            };
            adapterFotosViejas.setListener(listenerZoomViejo);
            adapterFotosViejas.notifyDataSetChanged();



            fabCamara = new FloatingActionButton(getActivity());
            fabCamara.setColorNormal(ContextCompat.getColor(getContext(), R.color.tile3));
            fabCamara.setButtonSize(FloatingActionButton.SIZE_MINI);
            fabCamara.setLabelText(getString(R.string.sacarFoto));
            fabCamara.setImageResource(R.drawable.ic_camera_alt_black_24dp);
            fabMenu.addMenuButton(fabCamara);

            fabCamara.setLabelColors(ContextCompat.getColor(getActivity(), R.color.tile2),
                    ContextCompat.getColor(getActivity(), R.color.light_grey),
                    ContextCompat.getColor(getActivity(), R.color.white_transparent));
            fabCamara.setLabelTextColor(ContextCompat.getColor(getActivity(), R.color.primary_text));

            fabCamara.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (FuncionesPublicas.isExternalStorageWritable()&& FuncionesPublicas.hayEspacioEnMemoria()) {
                        if (Nammu.checkPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                            fabMenu.close(true);
                            EasyImage.openCamera(FragmentPregunta_.this, 1);
                        }
                        else {

    //                      PIDO PERMISO PARA USAR LA MEMORIA EXTERNA

                            if (Nammu.shouldShowRequestPermissionRationale(FragmentPregunta_.this,android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                                //User already refused to give us this permission or removed it
                                //Now he/she can mark "never ask again" (sic!)
                                Snackbar.make(getView(), getResources().getString(R.string.appNecesitaPermiso),
                                        Snackbar.LENGTH_INDEFINITE).setAction(getResources().getString(R.string.ok), new View.OnClickListener() {
                                    @Override public void onClick(View view) {
                                        Nammu.askForPermission(FragmentPregunta_.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                                new PermissionCallback() {
                                                    @Override
                                                    public void permissionGranted() {
                                                        fabMenu.close(true);
                                                        EasyImage.openCamera(FragmentPregunta_.this, 1);
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
                                Nammu.askForPermission(FragmentPregunta_.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                        new PermissionCallback() {
                                            @Override
                                            public void permissionGranted() {
                                                fabMenu.close(true);
                                                EasyImage.openCamera(FragmentPregunta_.this, 1);
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


            FloatingActionButton fabComment = new FloatingActionButton(getActivity());
            fabComment.setColorNormal(ContextCompat.getColor(getContext(), R.color.tile3));
            fabComment.setButtonSize(FloatingActionButton.SIZE_MINI);
            fabComment.setLabelText(getString(R.string.agregarComentario));
            fabComment.setImageResource(R.drawable.ic_comment_black_24dp);
            fabMenu.addMenuButton(fabComment);

            fabComment.setLabelColors(ContextCompat.getColor(getActivity(), R.color.tile2),
                    ContextCompat.getColor(getActivity(), R.color.light_grey),
                    ContextCompat.getColor(getActivity(), R.color.white_transparent));
            fabComment.setLabelTextColor(ContextCompat.getColor(getActivity(), R.color.primary_text));

            fabComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    crearDialogoCommentGeneral();

                    fabMenu.close(true);
                }
            });


            fabGuardar = new FloatingActionButton(getActivity());
            fabGuardar.setButtonSize(FloatingActionButton.SIZE_MINI);
            fabGuardar.setColorNormal(ContextCompat.getColor(getContext(), R.color.tile3));
            fabGuardar.setLabelText(getString(R.string.guardarAuditoria));
            fabGuardar.setImageResource(R.drawable.ic_save_black_24dp);
            fabMenu.addMenuButton(fabGuardar);

            fabGuardar.setLabelColors(ContextCompat.getColor(getActivity(), R.color.tile2),
                    ContextCompat.getColor(getActivity(), R.color.light_grey),
                    ContextCompat.getColor(getActivity(), R.color.white_transparent));
            fabGuardar.setLabelTextColor(ContextCompat.getColor(getActivity(), R.color.primary_text));

            fabGuardar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //--guardar auditoria en firebase--//

                    fabMenu.close(true);
                   corroborarFinalizacionAuditoria(idAudit);

                }
            });

            FloatingActionButton fabSalir = new FloatingActionButton(getActivity());
            fabSalir.setButtonSize(FloatingActionButton.SIZE_MINI);
            fabSalir.setColorNormal(ContextCompat.getColor(getContext(), R.color.tile3));
            fabSalir.setLabelText(getString(R.string.salir));
            fabSalir.setImageResource(R.drawable.ic_exit_to_app_black_24dp);
            fabMenu.addMenuButton(fabSalir);

            fabSalir.setLabelColors(ContextCompat.getColor(getActivity(), R.color.tile2),
                    ContextCompat.getColor(getActivity(), R.color.light_grey),
                    ContextCompat.getColor(getActivity(), R.color.white_transparent));
            fabSalir.setLabelTextColor(ContextCompat.getColor(getActivity(), R.color.primary_text));

            fabSalir.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    fabMenu.close(true);
                    new MaterialDialog.Builder(getContext())
                            .title(R.string.advertencia)
                            .contentColor(ContextCompat.getColor(getContext(), R.color.primary_text))
                            .titleColor(ContextCompat.getColor(getContext(), R.color.tile4))
                            .backgroundColor(ContextCompat.getColor(getContext(), R.color.tile1))
                            .content(getResources().getString(R.string.auditoriaSinTerminar)+"\n"+getResources().getString(R.string.guardardar))
                            .positiveText(getResources().getString(R.string.si))
                            .neutralText(getResources().getString(R.string.cancel))
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

                                    FuncionesPublicas.borrarAuditoriaSeleccionada(ActivityAuditoria.idAudit);
                                    //aca lo que pasa si voy para atras

                                    avisable.salirDeAca();
                                }
                            })
                            .onNeutral(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                                }
                            })
                            .show();

                }
            });
        }

        SharedPreferences config = getActivity().getSharedPreferences("prefs", 0);
        boolean quiereVerTuto = config.getBoolean("quiereVerTuto",false);
        boolean primeraVezFragmentSubitem = config.getBoolean("primeraVezFragmentSubitem",false);

        //SI EL USUARIO ELIGIO VER TUTORIALES ME FIJO SI YA PASO POR ESTA PAGINA.
        if (quiereVerTuto) {
            if (!primeraVezFragmentSubitem) {

                SharedPreferences.Editor editor = config.edit();
                editor.putBoolean("primeraVezFragmentSubitem",true);
                editor.commit();
                seguirConTutorial();
            }
        }

//        SI ES SOLO REVISION BLOQUEO TODAS LAS EDICIONES
        if (origen.equals(FuncionesPublicas.REVISAR) || origen .equals(FuncionesPublicas.EDITAR_CUESTIONARIO)){
            rb0.setEnabled(false);
            rb1.setEnabled(false);
            rb3.setEnabled(false);

            rb2.setEnabled(false);
            rb6.setEnabled(false);
           fabMenu.setVisibility(View.GONE);
        }
        else{
            rb0.setEnabled(true);
            rb1.setEnabled(true);
            rb3.setEnabled(true);

            rb2.setEnabled(true);
            rb6.setEnabled(true);
            fabMenu.setVisibility(View.VISIBLE);
        }
        return view;
    }

    private void cargarFotosViejas() {
        RealmList<Foto>listaFotosViejas=new RealmList<>();
        Pregunta laPreguntaVieja;

        Realm realm = Realm.getDefaultInstance();
        String idArea=null;
        Auditoria auditActual= realm. where(Auditoria.class)
                .equalTo("idAuditoria", idAudit)
                .findFirst();
        if (auditActual!=null) {
            idArea=auditActual.getAreaAuditada().getIdArea();
        }

        //TRAIGO TODAS LAS AUDITS QUE NO SEAN LA ACTUAL
        RealmResults<Auditoria>allAudits=realm.where(Auditoria.class)
                .notEqualTo("idAuditoria",idAudit)
                .equalTo("esUltimaAuditoria",true)
                .findAll();

        for (Auditoria unAudit:allAudits
             ) {
            if (unAudit.getAreaAuditada().getIdArea().equals(idArea)&&unAudit.getEsUltimaAuditoria()){
                 laPreguntaVieja = realm.where(Pregunta.class)
                        .equalTo("idAudit",unAudit.getIdAuditoria())
                        .equalTo("idPregunta", idPregunta)
                        .findFirst();
                 //SI LA PREGUNTA VIEJA TIENE FOTOS

                    if (laPreguntaVieja!=null&&laPreguntaVieja.getListaFotos().size()>0){
                        tituloAuditVieja.setVisibility(View.VISIBLE);
                        separadorInvertido.setVisibility(View.VISIBLE);
                        separador.setVisibility(View.VISIBLE);
                        listaFotosViejas.addAll(laPreguntaVieja.getListaFotos());
                        adapterFotosViejas.setListaFotosOriginales(listaFotosViejas);
                        adapterFotosViejas.notifyDataSetChanged();

                        if (laPreguntaVieja.getComentario()!=null && !laPreguntaVieja.getComentario().isEmpty()){
                            tagCommentViejo.setVisibility(View.VISIBLE);
                            textViewCommentViejo.setVisibility(View.VISIBLE);
                            textViewCommentViejo.setText(laPreguntaVieja.getComentario());
                        }
                    }
                    else {
                        if (laPreguntaVieja!=null && laPreguntaVieja.getComentario()!=null && !laPreguntaVieja.getComentario().isEmpty()){
                            separadorInvertido.setVisibility(View.VISIBLE);
                            separador.setVisibility(View.VISIBLE);
                            tagCommentViejo.setVisibility(View.VISIBLE);
                            textViewCommentViejo.setVisibility(View.VISIBLE);
                            textViewCommentViejo.setText(laPreguntaVieja.getComentario());
                        }
                        else{
                            separadorInvertido.setVisibility(View.GONE);
                            separador.setVisibility(View.GONE);
                            tituloAuditVieja.setVisibility(View.GONE);
                            tagCommentViejo.setVisibility(View.GONE);
                            textViewCommentViejo.setVisibility(View.GONE);
                        }
                    }


            }
        }
        adapterFotosViejas.setListaFotosOriginales(listaFotosViejas);
       adapterFotosViejas.notifyDataSetChanged();
    }

    public void limpiarRadioGroups(Integer cualToque){
        switch (cualToque){
            case 1:
                rg2.setOnCheckedChangeListener(null);
                rg2.clearCheck();
                rg2.setOnCheckedChangeListener(listener2);
                break;
            case 2:
                rg1.setOnCheckedChangeListener(null);
                rg1.clearCheck();
                rg1.setOnCheckedChangeListener(listener1);
                break;
        }
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


    private void corroborarFinalizacionAuditoria(String idAudit) {

        if (FuncionesPublicas.completoTodosLosPuntos(idAudit)){
            avisable.cerrarAuditoria();
            avisable.actualizarPuntaje(idAudit);
            //avisable.cargarAuditoriaEnFirebase(idAudit);
        }
        else {

            new MaterialDialog.Builder(getContext())
                    .title(getString(R.string.advertencia))
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
    }




    public static FragmentPregunta_ CrearfragmentPregunta(Pregunta laPregunta, String origen, String idEse) {
        FragmentPregunta_ detalleFragment = new FragmentPregunta_();
        Bundle unBundle = new Bundle();
            unBundle.putString(IDESE,idEse);
            unBundle.putString(ENUNCIADOPREGUNTA, laPregunta.getTextoPregunta());
            unBundle.putString(IDITEM,laPregunta.getIdItem());
            unBundle.putString(IDPREGUNTA,laPregunta.getIdPregunta());
            unBundle.putString(IDAUDITORIA, laPregunta.getIdAudit());
            unBundle.putString(ORIGEN, origen);

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
                                    .compressToFile(fotoOriginal,fotoOriginal.getName().replace(".jpg",".png"))
                            ;
                            unaFoto=new Foto();
                            unaFoto.setIdFoto("foto_"+ UUID.randomUUID());
                            unaFoto.setRutaFoto(fotoComprimida.getAbsolutePath());
                            unaFoto.setIdAudit(idAudit);
                            unaFoto.setIdPregunta(idPregunta);

                            Realm realm = Realm.getDefaultInstance();
                            realm.executeTransaction(new Realm.Transaction() {
                                @Override
                                public void execute(@NonNull Realm realm) {
                                    Pregunta preg = realm.where(Pregunta.class)
                                            .equalTo("idAudit", idAudit)
                                            .equalTo("idPregunta",  idPregunta)
                                            .findFirst();

                                    realm.copyToRealmOrUpdate(unaFoto);

                                    final Foto foto = realm.where(Foto.class)
                                            .equalTo("idFoto", unaFoto.getIdFoto())
                                            .findFirst();

                                    if (preg!=null) {
                                        preg.getListaFotos().add(foto);
                                    }
                                    //posible bug dejar esto adentro
                                }
                            });
                            crearDialogoParaModificarComentario(unaFoto);
                            listaFotos.add(unaFoto);
                            evidenciaNueva.setVisibility(View.VISIBLE);
                            adapterFotos.notifyDataSetChanged();

                            Boolean seBorro = imageFile.delete();
                            if (seBorro) {
                                //                        Toast.makeText(getContext(), "borrada con exito", Toast.LENGTH_SHORT).show();
                            } else {
                                //                        Toast.makeText(getContext(), "No se pudo borrar", Toast.LENGTH_SHORT).show();
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
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
            evidenciaNueva.setVisibility(View.VISIBLE);
            return unaLista;
        }

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.avisable = (Avisable)context;
    }


    public void crearDialogoParaModificarComentario(final Foto laFotoParaComentar){

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
                    public void onInput(@NonNull MaterialDialog dialog, final CharSequence input) {

                        Realm realm= Realm.getDefaultInstance();
                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                               Foto foto = realm.where(Foto.class)
                                       .equalTo("idFoto",laFotoParaComentar.getIdFoto())
                                       .findFirst();
                                if (foto!=null) {
                                    foto.setComentarioFoto(input.toString());
                                }

                            }
                        });
                        unaFoto.setComentarioFoto(input.toString());
                        adapterFotos.notifyDataSetChanged();
                    }
                }).show();
    }

    public void crearDialogoCommentGeneral(){
        new MaterialDialog.Builder(getContext())
                .title(getResources().getString(R.string.agregarComentario))
                .contentColor(ContextCompat.getColor(getContext(), R.color.primary_text))
                .backgroundColor(ContextCompat.getColor(getContext(), R.color.tile1))
                .titleColor(ContextCompat.getColor(getContext(), R.color.tile4))
                .content(getResources().getString(R.string.favorAgregueComentarioGeneral))
                .inputType(InputType.TYPE_CLASS_TEXT)
                .input(getResources().getString(R.string.comment),"", new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, final CharSequence input) {
                        String inputString=input.toString();
                        if (!inputString.isEmpty()){
                            evidenciaNueva.setVisibility(View.VISIBLE);
                            tagCommentNuevo.setVisibility(View.VISIBLE);
                            textViewCommentNuevo.setVisibility(View.VISIBLE);
                            textViewCommentNuevo.setText(input.toString());
                        }
                        else{
                            tagCommentNuevo.setVisibility(View.GONE);
                            textViewCommentNuevo.setVisibility(View.GONE);
                        }

                        Realm realm= Realm.getDefaultInstance();
                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(@NonNull Realm realm) {
                                Pregunta pregunta = realm.where(Pregunta.class)
                                        .equalTo("idAudit",idAudit)
                                        .equalTo("idPregunta",idPregunta)
                                        .findFirst();

                                if (pregunta!=null) {
                                  pregunta.setComentario(input.toString());
                                }
                            }
                        });
                    }
                }).show();
    }

    //DOY DE ALTA LA FOTO EN REALM

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Nammu.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


}




