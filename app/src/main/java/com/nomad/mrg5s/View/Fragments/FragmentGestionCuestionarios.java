package com.nomad.mrg5s.View.Fragments;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.github.clans.fab.FloatingActionMenu;
import com.nomad.mrg5s.DAO.ControllerDatos;
import com.nomad.mrg5s.Model.Cuestionario;
import com.nomad.mrg5s.R;
import com.nomad.mrg5s.Utils.FuncionesPublicas;
import com.nomad.mrg5s.Utils.HTTPConnectionManager;
import com.nomad.mrg5s.Utils.ResultListener;
import com.nomad.mrg5s.View.Adapter.AdapterCuestionario;
import com.github.clans.fab.FloatingActionButton;

import java.util.ResourceBundle;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import pl.aprilapps.easyphotopicker.EasyImage;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentGestionCuestionarios extends Fragment implements AdapterCuestionario.Eliminable {

    private RealmList<Cuestionario> listaCuestionarios;
    private RecyclerView recyclerCuestionarios;
    private AdapterCuestionario adapterCuestionario;
    private LinearLayoutManager layoutManager;
    private Notificable notificable;
    private ControllerDatos controllerDatos;


    public FragmentGestionCuestionarios() {
        // Required empty public constructor
    }

    public interface Notificable{
        void abrirCuestionario(Cuestionario cuestionario);

        void crearCuestionario(String cuestionario, String tipoCuestionario, ResultListener<Boolean> listenerCompletado);

        void eliminarCuestionario(String idCuestionario);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_gestion_cuestionarios, container, false);
        controllerDatos=new ControllerDatos(view.getContext());

        recyclerCuestionarios=view.findViewById(R.id.recyclerCuestionarios);
        TextView textoBotonEditorCuestionarios = view.findViewById(R.id.botonAgregarCuestionario);
        textoBotonEditorCuestionarios.setVisibility(View.GONE);

        // String usuario=FirebaseAuth.getInstance().getCurrentUser().getEmail();

        adapterCuestionario= new AdapterCuestionario();
        adapterCuestionario.setContext(getContext());
        layoutManager= new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL,false);
        recyclerCuestionarios.setLayoutManager(layoutManager);
        recyclerCuestionarios.setAdapter(adapterCuestionario);

        actualizarDatosRecycler(view.getContext());

        TextView textView = view.findViewById(R.id.textoTituloManage);
        Typeface roboto = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Roboto-Light.ttf");
        textView.setTypeface(roboto);


        View.OnClickListener listenerCuestionario = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer posicion = recyclerCuestionarios.getChildAdapterPosition(v);
                RealmList<Cuestionario> listaCuestionarios = adapterCuestionario.getListaCuestionariosOriginales();
                Cuestionario cuestionarioClickeado = listaCuestionarios.get(posicion);
                notificable.abrirCuestionario(cuestionarioClickeado);
            }
        };
        adapterCuestionario.setListener(listenerCuestionario);


        final FloatingActionMenu fabMenu = view.findViewById(R.id.agregarArea);
        fabMenu.setMenuButtonColorNormal(ContextCompat.getColor(view.getContext(),R.color.colorAccent));

        FloatingActionButton fabNuevoCuestionario = new FloatingActionButton(view.getContext());
        fabNuevoCuestionario.setColorNormal(ContextCompat.getColor(view.getContext(), R.color.tile3));
        fabNuevoCuestionario.setButtonSize(FloatingActionButton.SIZE_MINI);
        fabNuevoCuestionario.setLabelText(getString(R.string.addCuestionario));
        fabNuevoCuestionario.setImageResource(R.drawable.ic_nuevo_cuestionario_black_24dp);
        fabMenu.addMenuButton(fabNuevoCuestionario);

        fabNuevoCuestionario.setLabelColors(ContextCompat.getColor(getActivity(), R.color.tile2),
                ContextCompat.getColor(getActivity(), R.color.light_grey),
                ContextCompat.getColor(getActivity(), R.color.white_transparent));
        fabNuevoCuestionario.setLabelTextColor(ContextCompat.getColor(getActivity(), R.color.primary_text));


        fabNuevoCuestionario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                fabMenu.close(true);
                crearDialogoNuevoCuestionario(view);

            }
        });

        FloatingActionButton fabSincronizar = new FloatingActionButton(view.getContext());
        fabSincronizar.setColorNormal(ContextCompat.getColor(view.getContext(), R.color.tile3));
        fabSincronizar.setButtonSize(FloatingActionButton.SIZE_MINI);
        fabSincronizar.setLabelText(getString(R.string.sincronizar));
        fabSincronizar.setImageResource(R.drawable.ic_cloud_upload_black_24dp);
        fabMenu.addMenuButton(fabSincronizar);

        fabSincronizar.setLabelColors(ContextCompat.getColor(getActivity(), R.color.tile2),
                ContextCompat.getColor(getActivity(), R.color.light_grey),
                ContextCompat.getColor(getActivity(), R.color.white_transparent));
        fabSincronizar.setLabelTextColor(ContextCompat.getColor(getActivity(), R.color.primary_text));

        fabSincronizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fabMenu.close(true);

                for (Cuestionario elCuestionario :
                        listaCuestionarios) {
                    controllerDatos.crearCuestionarioFirebase(elCuestionario);
                }

            }
        });

        FloatingActionButton fabImportar = new FloatingActionButton(view.getContext());
        fabImportar.setColorNormal(ContextCompat.getColor(view.getContext(), R.color.tile3));
        fabImportar.setButtonSize(FloatingActionButton.SIZE_MINI);
        fabImportar.setLabelText("importar");
        fabImportar.setImageResource(R.drawable.ic_arrow_back_black_24dp);
        fabMenu.addMenuButton(fabImportar);

        fabImportar.setLabelColors(ContextCompat.getColor(getActivity(), R.color.tile2),
                ContextCompat.getColor(getActivity(), R.color.light_grey),
                ContextCompat.getColor(getActivity(), R.color.white_transparent));
        fabImportar.setLabelTextColor(ContextCompat.getColor(getActivity(), R.color.primary_text));

        fabImportar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fabMenu.close(true);
                if (HTTPConnectionManager.isNetworkingOnline(view.getContext())) {
                    controllerDatos.traerCuestionariosFirebase(new ResultListener<Boolean>() {
                        @Override
                        public void finish(Boolean resultado) {
                            if (resultado){
                                actualizarDatosRecycler(view.getContext());
                                Toast.makeText(view.getContext(), view.getContext().getString(R.string.cuestionariosImportados), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(view.getContext(), view.getContext().getString(R.string.noSeRealizoImportacion), Toast.LENGTH_SHORT).show();
                }
            }
        });








        return view;
    }

    private void crearDialogoNuevoCuestionario(View view) {

        final MaterialDialog mDialog = new MaterialDialog.Builder(view.getContext())
                .cancelable(true)
                .customView(R.layout.dialogo_estructura,false)
                .build();

        View laView=mDialog.getCustomView();
        assert laView != null;
        final ImageView imagenSimple = laView.findViewById(R.id.img_simple);
        final ImageView imagenEstructurado = laView.findViewById(R.id.img_estructurado);
        final RadioGroup radioGroup =laView.findViewById(R.id.rg_estructura);


        radioGroup.check(R.id.rbEstructuraSimple);

        imagenSimple.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                radioGroup.check(R.id.rbEstructuraSimple);
            }
        });
        imagenEstructurado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                radioGroup.check(R.id.rbEstructuraEstructurada);
            }
        });


        TextView tituloDialogo=laView.findViewById(R.id.tituloDialogoItem);
        tituloDialogo.setFocusableInTouchMode(false);

        TextView botonOk= laView.findViewById(R.id.botonDialogoSi);
        botonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tipoCuestionario=null;
                switch (radioGroup.getCheckedRadioButtonId()){
                    case R.id.rbEstructuraSimple:
                        tipoCuestionario=FuncionesPublicas.ESTRUCTURA_SIMPLE;
                        break;
                    case    R.id.rbEstructuraEstructurada:
                        tipoCuestionario=FuncionesPublicas.ESTRUCTURA_ESTRUCTURADA;
                        break;
                }
                darNombreCuestionario(tipoCuestionario,view.getContext());
                mDialog.hide();
            }
        });
        mDialog.show();



    }

    public void darNombreCuestionario(final String tipoCuestionario, final Context context){
        new MaterialDialog.Builder(context)
                .title(getResources().getString(R.string.addCuestionario))
                .inputRange(1,40)
                .contentColor(ContextCompat.getColor(getContext(), R.color.primary_text))
                .backgroundColor(ContextCompat.getColor(getContext(), R.color.tile1))
                .titleColor(ContextCompat.getColor(getContext(), R.color.tile4))
                .content(getResources().getString(R.string.nombreCuestionario))
                .inputType(InputType.TYPE_CLASS_TEXT)
                .input(getResources().getString(R.string.cuestionarioName),"", new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {
                        notificable.crearCuestionario(input.toString(), tipoCuestionario, new ResultListener<Boolean>() {
                            @Override
                            public void finish(Boolean resultado) {
                                if (resultado) {
                                    actualizarDatosRecycler(context);
                                    recyclerCuestionarios.scrollToPosition(adapterCuestionario.getListaCuestionariosOriginales().size()-1);
                                    Toast.makeText(getContext(), getContext().getString(R.string.cuestionarioCreado), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                    }
                }).show();
    }

    private void actualizarDatosRecycler(Context context) {
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
        } catch (Exception e) {
            e.printStackTrace();
            Realm.init(context.getApplicationContext());
            realm=Realm.getDefaultInstance();
        }
        RealmResults<Cuestionario> result2 = realm.where(Cuestionario.class)
                //las areas son para todos los usuarios// .equalTo("usuario",usuario)
                .findAll();
        listaCuestionarios=new RealmList<>();
        listaCuestionarios.addAll(result2);
        adapterCuestionario.setListaCuestionariosOriginales(listaCuestionarios);
        adapterCuestionario.notifyDataSetChanged();
    }

    @Override
    public void EliminarCuestionario(final Cuestionario unCuestionario) {

            new MaterialDialog.Builder(getContext())
                    .title(getResources().getString(R.string.tituloDeleteCuestionario))
                    .contentColor(ContextCompat.getColor(getContext(), R.color.primary_text))
                    .titleColor(ContextCompat.getColor(getContext(), R.color.tile4))
                    .backgroundColor(ContextCompat.getColor(getContext(), R.color.tile1))
                    .content(R.string.deseaBorrarCuestionario)
                    .positiveText(getResources().getString(R.string.delete))
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            //esto esta mal que actualice solo si la eliminacion fue exitosa
                            adapterCuestionario.getListaCuestionariosOriginales().remove(unCuestionario);
                            adapterCuestionario.notifyDataSetChanged();
                            notificable.eliminarCuestionario(unCuestionario.getIdCuestionario());

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

    @Override
    public void editarNombreCuestionario(final Cuestionario unCuestionario) {
        final MaterialDialog mDialog = new MaterialDialog.Builder(FragmentGestionCuestionarios.this.getContext())
                .inputRange(1,40)
                .title(FragmentGestionCuestionarios.this.getContext().getResources().getString(R.string.editarNombreCuestionario))
                .contentColor(ContextCompat.getColor(FragmentGestionCuestionarios.this.getContext(), R.color.primary_text))
                .backgroundColor(ContextCompat.getColor(FragmentGestionCuestionarios.this.getContext(), R.color.tile1))
                .titleColor(ContextCompat.getColor(FragmentGestionCuestionarios.this.getContext(), R.color.tile4))
                .content(FragmentGestionCuestionarios.this.getResources().getString(R.string.favorEditeItem))
                .input(FragmentGestionCuestionarios.this.getResources().getString(R.string.nombreCues),"", new MaterialDialog.InputCallback() {
                            @Override
                            public void onInput(MaterialDialog dialog, final CharSequence input) {
                                FuncionesPublicas.cambiarNombreCuestionario(unCuestionario,input.toString(),adapterCuestionario);

                            }
                        })

                .show();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        notificable = (Notificable)context;
    }
}
