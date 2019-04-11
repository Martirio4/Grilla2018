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
import com.nomad.mrg5s.View.Adapter.AdapterCriterios;
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
    private TextView tituloAuditVieja;

    private TextView evidenciaNueva;



    private FloatingActionMenu fabMenu;
    private FloatingActionButton fabCamara;
    private FloatingActionButton fabGuardar;



    private ImageView separador;
    private ImageView separadorInvertido;


    private Foto unaFoto;
    private RecyclerView recyclerFotosViejas;
    private String origen;

    private RecyclerView recyclerCriterios;
    private AdapterCriterios adapterCriterios;

    private ImageView boton1;
    private ImageView boton2;
    private ImageView boton3;
    private ImageView boton4;

    private TextView criterio1;
    private TextView criterio2;
    private TextView criterio3;
    private TextView criterio4;


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
        final View view= inflater.inflate(R.layout.fragment_pregunta_alt, container, false);



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

        boton1=view.findViewById(R.id.circuloApretado);
        boton2=view.findViewById(R.id.circuloApretado2);
        boton3=view.findViewById(R.id.circuloApretado3);
        boton4=view.findViewById(R.id.circuloApretado4);

        criterio1=view.findViewById(R.id.tv_descripcion_item);
        criterio2=view.findViewById(R.id.tv_descripcion_item2);
        criterio3=view.findViewById(R.id.tv_descripcion_item3);
        criterio4=view.findViewById(R.id.tv_descripcion_item4);

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
        RealmList<Criterio> listacriterio = new RealmList<>();
        listacriterio.addAll(rr_listaCriterio);
        criterio1.setText(listacriterio.get(0).getTextoCriterio());
        criterio2.setText(listacriterio.get(1).getTextoCriterio());
        criterio3.setText(listacriterio.get(2).getTextoCriterio());
        criterio4.setText(listacriterio.get(3).getTextoCriterio());



        //agregar los fabs al menu
        fabMenu= view.findViewById(R.id.fab_menu);
        fabMenu.setMenuButtonColorNormal(ContextCompat.getColor(getContext(),R.color.colorAccent));

//        SI ESTOY EN MODO EDITAR LA AUDITORIA SETEO LOS LISTENER PARA LOS BOTONES, SINO NO LO CARGO
        if (origen.equals(FuncionesPublicas.NUEVA_AUDITORIA) || origen.equals(FuncionesPublicas.EDITAR_AUDITORIA)){
            boton1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    borrarTodosApretados();
                    boton1.setBackground(ContextCompat.getDrawable(view.getContext(),R.drawable.boton_apretado));
                    if (criterio1.getText().toString().equals(FuncionesPublicas.N_A)){
                        registrarPuntaje(9);
                    }
                    else{
                        registrarPuntaje(1);
                    }
                }
            });
            boton2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    borrarTodosApretados();
                    boton2.setBackground(ContextCompat.getDrawable(view.getContext(),R.drawable.boton_apretado));
                    if (criterio2.getText().toString().equals(FuncionesPublicas.N_A)){
                        registrarPuntaje(9);
                    }
                    else{
                        registrarPuntaje(2);
                    }
                }
            });
            boton3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    borrarTodosApretados();
                    boton3.setBackground(ContextCompat.getDrawable(view.getContext(),R.drawable.boton_apretado));
                    if (criterio3.getText().toString().equals(FuncionesPublicas.N_A)){
                        registrarPuntaje(9);
                    }
                    else{
                        registrarPuntaje(3);
                    }
                }
            });
            boton4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    borrarTodosApretados();
                    boton4.setBackground(ContextCompat.getDrawable(view.getContext(),R.drawable.boton_apretado));
                    if (criterio4.getText().toString().equals(FuncionesPublicas.N_A)){
                        registrarPuntaje(9);
                    }
                    else{
                        registrarPuntaje(4);
                    }
                }
            });
        }


        if (origen.equals(FuncionesPublicas.NUEVA_AUDITORIA) || origen.equals(FuncionesPublicas.EDITAR_AUDITORIA)||origen.equals(FuncionesPublicas.REVISAR)) {

            //RECYCLERVIEW CRITERIOS







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
                    if (FuncionesPublicas.hayLugarYPuedoEscribir(getContext(),fabCamara)){
                        fabMenu.close(true);
                        EasyImage.openCamera(FragmentPregunta_.this, 1);
                    }
                    else {
                        new MaterialDialog.Builder(getContext())
                                .title(getResources().getString(R.string.error))
                                .contentColor(ContextCompat.getColor(getContext(), R.color.primary_text))
                                .backgroundColor(ContextCompat.getColor(getContext(), R.color.tile1))
                                .titleColor(ContextCompat.getColor(getContext(), R.color.tile4))
                                .content(getResources().getString(R.string.problemaMemoriaEspacio))
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

           fabMenu.setVisibility(View.GONE);
        }
        else{

            fabMenu.setVisibility(View.VISIBLE);
        }

        //COMPLETAR PREGUNTAS EMPEZADAS
        Pregunta pregunta = realm.where(Pregunta.class)
                .equalTo("idAudit",idAudit)
                .equalTo("idPregunta",idPregunta)
                .findFirst();
        if (pregunta!=null && pregunta.getPuntaje()!=null){


            Integer puntaje=pregunta.getPuntaje();
            switch (puntaje){
                case 1:
                    borrarTodosApretados();
                    boton1.setBackground(view.getContext().getResources().getDrawable(R.drawable.boton_apretado));
                    break;
                case 2:
                    borrarTodosApretados();
                    boton2.setBackground(view.getContext().getResources().getDrawable(R.drawable.boton_apretado));
                    break;
                case 3:
                    borrarTodosApretados();
                    boton3.setBackground(view.getContext().getResources().getDrawable(R.drawable.boton_apretado));
                    break;
                case 4:
                    borrarTodosApretados();
                    boton4.setBackground(view.getContext().getResources().getDrawable(R.drawable.boton_apretado));
                    break;
                case 9:
                    borrarTodosApretados();
                    if (criterio1.getText().toString().equals(FuncionesPublicas.N_A)){
                        boton1.setBackground(view.getContext().getResources().getDrawable(R.drawable.boton_apretado));
                        break;
                    }
                    if (criterio2.getText().toString().equals(FuncionesPublicas.N_A)){
                        boton2.setBackground(view.getContext().getResources().getDrawable(R.drawable.boton_apretado));
                        break;
                    }
                    if (criterio3.getText().toString().equals(FuncionesPublicas.N_A)){
                        boton3.setBackground(view.getContext().getResources().getDrawable(R.drawable.boton_apretado));
                        break;
                    }
                    if (criterio4.getText().toString().equals(FuncionesPublicas.N_A)){
                        boton4.setBackground(view.getContext().getResources().getDrawable(R.drawable.boton_apretado));
                        break;
                    }
                    else{
                        break;
                    }
                default:
                    boton1.setBackground(view.getContext().getResources().getDrawable(R.drawable.boton_suelto));
                    boton2.setBackground(view.getContext().getResources().getDrawable(R.drawable.boton_suelto));
                    boton3.setBackground(view.getContext().getResources().getDrawable(R.drawable.boton_suelto));
                    boton4.setBackground(view.getContext().getResources().getDrawable(R.drawable.boton_suelto));
                    break;
            }
        }
        if (pregunta!=null&&pregunta.getComentario()!=null){
            tagCommentNuevo.setVisibility(View.VISIBLE);
            textViewCommentNuevo.setVisibility(View.VISIBLE);
            textViewCommentNuevo.setText(pregunta.getComentario());
            evidenciaNueva.setVisibility(View.VISIBLE);

        }

        return view;
    }

    private void registrarPuntaje(final int i) {

        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Pregunta mPregunta=realm.where(Pregunta.class)
                        .equalTo("idPregunta",idPregunta)
                        .findFirst();
                if (mPregunta!=null){
                    mPregunta.setPuntaje(i);
                }
            }
        });

    }

    private void borrarTodosApretados() {
        boton1.setBackground(ContextCompat.getDrawable(boton1.getContext(),R.drawable.boton_suelto));
        boton2.setBackground(ContextCompat.getDrawable(boton1.getContext(),R.drawable.boton_suelto));
        boton3.setBackground(ContextCompat.getDrawable(boton1.getContext(),R.drawable.boton_suelto));
        boton4.setBackground(ContextCompat.getDrawable(boton1.getContext(),R.drawable.boton_suelto));
    }

    private void cargarFotosViejas() {
        RealmList<Foto>listaFotosViejas=new RealmList<>();
        Pregunta laPreguntaVieja;

        Realm realm = Realm.getDefaultInstance();
        String idArea=null;
        Integer ordenPregunta=0;
        Integer ordenItem=0;
        Integer ordenEse=0;

        Ese eseActual = realm.where(Ese.class)
                .equalTo("idEse", idEse)
                .findFirst();
        if (eseActual!=null){
            ordenEse=eseActual.getNumeroEse()-1;
        }
        Pregunta preguntaActual=realm.where(Pregunta.class)
                .equalTo("idPregunta",idPregunta)
                .findFirst();
        if (preguntaActual!=null){
            ordenPregunta=preguntaActual.getOrden()-1;
        }
        Item itemActual=realm.where(Item.class)
                .equalTo("idItem", idItem)
                .findFirst();
        if (itemActual!=null){
            ordenItem=itemActual.getOrden()-1;
        }
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

                if (unAudit.getEstructuraAuditoria().equals(FuncionesPublicas.ESTRUCTURA_SIMPLE)) {
                    String idPreguntaVieja = unAudit.getListaEses().get(ordenEse).getListaPreguntas().get(ordenPregunta).getIdPregunta();
                    laPreguntaVieja=realm.where(Pregunta.class)
                            .equalTo("idAudit", unAudit.getIdAuditoria())
                            .equalTo("idPregunta",idPreguntaVieja)
                            .findFirst();
                }
                else {
                    String idPreguntaVieja = unAudit.getListaEses().get(ordenEse).getListaItem().get(ordenItem).getListaPreguntas().get(ordenPregunta).getIdPregunta();
                    laPreguntaVieja=realm.where(Pregunta.class)
                            .equalTo("idAudit", unAudit.getIdAuditoria())
                            .equalTo("idPregunta",idPreguntaVieja)
                            .findFirst();
                }


                    //SI LA PREGUNTA VIEJA TIENE FOTOS

                    if (laPreguntaVieja != null && laPreguntaVieja.getListaFotos().size() > 0) {
                        tituloAuditVieja.setVisibility(View.VISIBLE);
                        separadorInvertido.setVisibility(View.VISIBLE);
                        separador.setVisibility(View.VISIBLE);
                        listaFotosViejas.addAll(laPreguntaVieja.getListaFotos());
                        adapterFotosViejas.setListaFotosOriginales(listaFotosViejas);
                        adapterFotosViejas.notifyDataSetChanged();

                        if (laPreguntaVieja.getComentario() != null && !laPreguntaVieja.getComentario().isEmpty()) {
                            tagCommentViejo.setVisibility(View.VISIBLE);
                            textViewCommentViejo.setVisibility(View.VISIBLE);
                            textViewCommentViejo.setText(laPreguntaVieja.getComentario());
                        }
                    } else {
                        if (laPreguntaVieja != null && laPreguntaVieja.getComentario() != null && !laPreguntaVieja.getComentario().isEmpty()) {
                            separadorInvertido.setVisibility(View.VISIBLE);
                            separador.setVisibility(View.VISIBLE);
                            tagCommentViejo.setVisibility(View.VISIBLE);
                            textViewCommentViejo.setVisibility(View.VISIBLE);
                            textViewCommentViejo.setText(laPreguntaVieja.getComentario());
                        } else {
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




