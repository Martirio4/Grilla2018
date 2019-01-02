package com.auditoria.grilla5s.View.Fragments;


import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.auditoria.grilla5s.DAO.ControllerDatos;
import com.auditoria.grilla5s.Model.Cuestionario;
import com.auditoria.grilla5s.R;
import com.auditoria.grilla5s.View.Adapter.AdapterCuestionario;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.firebase.database.DatabaseReference;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentEditorCuestionarios extends Fragment implements AdapterCuestionario.Eliminable {

    private RealmList<Cuestionario> listaCuestionarios;
    private RecyclerView recyclerCuestionarios;
    private AdapterCuestionario adapterCuestionario;
    private LinearLayoutManager layoutManager;


    private FloatingActionMenu fabMenuManage;
    private FloatingActionButton fabNuevoCuestionario;
    private FloatingActionButton fabSalir;

    private ControllerDatos controllerDatos;


    private FragmentManageAreas.Avisable unAvisable;

    private TextView textView;
    private DatabaseReference mDatabase;

    private LinearLayout linearSnackbar;
    private Notificable notificable;


    public FragmentEditorCuestionarios() {
        // Required empty public constructor
    }

    public interface Notificable{
        void abrirCuestionario(Cuestionario cuestionario);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_editor_base_cuestionarios, container, false);
        controllerDatos=new ControllerDatos(view.getContext());

        recyclerCuestionarios=view.findViewById(R.id.recyclerCuestionarios);

        linearSnackbar =view.findViewById(R.id.linearParaCoordinar);

        // String usuario=FirebaseAuth.getInstance().getCurrentUser().getEmail();


        adapterCuestionario= new AdapterCuestionario();
        adapterCuestionario.setContext(getContext());
        layoutManager= new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL,false);
        recyclerCuestionarios.setLayoutManager(layoutManager);
        recyclerCuestionarios.setAdapter(adapterCuestionario);

        actualizarDatosRecycler();

        textView= view.findViewById(R.id.textoTituloManage);
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


        fabMenuManage = view.findViewById(R.id.agregarArea);
        fabMenuManage.setMenuButtonColorNormal(ContextCompat.getColor(getContext(), R.color.colorAccent));
        fabNuevoCuestionario =new FloatingActionButton(getActivity());


        fabNuevoCuestionario.setButtonSize(FloatingActionButton.SIZE_MINI);
        fabNuevoCuestionario.setColorNormal(ContextCompat.getColor(getContext(), R.color.tile3));
        fabNuevoCuestionario.setLabelText(getString(R.string.addCuestionario));
        fabNuevoCuestionario.setImageResource(R.drawable.ic_nuevo_cuestionario_black_24dp);
        fabMenuManage.addMenuButton(fabNuevoCuestionario);

        fabNuevoCuestionario.setLabelColors(ContextCompat.getColor(getActivity(), R.color.tile3),
                ContextCompat.getColor(getActivity(), R.color.light_grey),
                ContextCompat.getColor(getActivity(), R.color.white_transparent));
        fabNuevoCuestionario.setLabelTextColor(ContextCompat.getColor(getActivity(), R.color.black));

        fabNuevoCuestionario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
            crearDialogoNuevoCuestionario();
            fabMenuManage.close(true);
                
            }
        });


        return view;
    }

    private void crearDialogoNuevoCuestionario() {
        new MaterialDialog.Builder(getContext())
                .title(getResources().getString(R.string.addCuestionario))
                .inputRange(1,40)
                .contentColor(ContextCompat.getColor(getContext(), R.color.primary_text))
                .backgroundColor(ContextCompat.getColor(getContext(), R.color.tile1))
                .titleColor(ContextCompat.getColor(getContext(), R.color.tile4))
                .content(getResources().getString(R.string.nombreCuestionario))
                .inputType(InputType.TYPE_CLASS_TEXT)
                .input(getResources().getString(R.string.areaName),"", new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {
                       controllerDatos.crearNuevoCuestionario(input.toString());
                       actualizarDatosRecycler();
                    }
                }).show();

    }

    private void actualizarDatosRecycler() {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Cuestionario> result2 = realm.where(Cuestionario.class)
                //las areas son para todos los usuarios// .equalTo("usuario",usuario)
                .findAll();
        listaCuestionarios=new RealmList<>();
        listaCuestionarios.addAll(result2);
        adapterCuestionario.setListaCuestionariosOriginales(listaCuestionarios);
        adapterCuestionario.notifyDataSetChanged();
    }

    @Override
    public void EliminarCuestionario(Cuestionario unCuestionario) {
        adapterCuestionario.getListaCuestionariosOriginales().remove(unCuestionario);
        adapterCuestionario.notifyDataSetChanged();
        controllerDatos.eliminarCuestionario(unCuestionario.getIdCuestionario());
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        notificable = (Notificable)context;
    }
}