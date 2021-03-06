package com.nomad.mrg5s.View.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Typeface;
import android.media.Image;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nomad.mrg5s.Model.Cuestionario;
import com.nomad.mrg5s.R;
import com.nomad.mrg5s.Utils.FuncionesPublicas;
import com.nomad.mrg5s.View.Fragments.FragmentGestionCuestionarios;


import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

/**
 * Created by elmar on 18/5/2017.
 */

public class AdapterCuestionario extends RecyclerView.Adapter implements View.OnClickListener, View.OnLongClickListener {

    private Context context;
    private RealmList<Cuestionario> listaCuestionariosOriginales;
    private RealmList<Cuestionario> listaCuestionariosFavoritos;
    private View.OnClickListener listener;
    private AdapterView.OnLongClickListener listenerLong;
    private Eliminable eliminable;

    public void setLongListener(View.OnLongClickListener unLongListener) {
        this.listenerLong = unLongListener;
    }

    public Context getContext() {
        return context;
    }

    public void setListener(View.OnClickListener listener) {
        this.listener = listener;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void setListaCuestionariosOriginales(RealmList<Cuestionario> listaCuestionariosOriginales) {
        this.listaCuestionariosOriginales = listaCuestionariosOriginales;
    }

    public void addListaCuestionariosOriginales(RealmList<Cuestionario> listaCuestionariosOriginales) {
        this.listaCuestionariosOriginales.addAll(listaCuestionariosOriginales);
    }


    public RealmList<Cuestionario> getListaCuestionariosOriginales() {
        return listaCuestionariosOriginales;
    }

    //crear vista y viewholder
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View viewCelda = layoutInflater.inflate(R.layout.detalle_celda_manage_areas2, parent, false);
        viewCelda.setOnClickListener(this);

        CuestionarioViewHolder CuestionariosViewHolder = new CuestionarioViewHolder(viewCelda);

        return CuestionariosViewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final Cuestionario unCuestionario = listaCuestionariosOriginales.get(position);
        CuestionarioViewHolder CuestionarioViewHolder = (CuestionarioViewHolder) holder;
        CuestionarioViewHolder.cargarCuestionario(unCuestionario,context);

        FragmentActivity unaActivity = (FragmentActivity) context;
        FragmentManager fragmentManager = unaActivity.getSupportFragmentManager();
        final FragmentGestionCuestionarios fragmentManageCuestionarios = (FragmentGestionCuestionarios) fragmentManager.findFragmentByTag(FuncionesPublicas.FRAGMENT_EDITOR_CUESTIONARIOS);


        if (fragmentManageCuestionarios != null && fragmentManageCuestionarios.isVisible()) {

            CuestionarioViewHolder.btnEliminar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    eliminable = fragmentManageCuestionarios;
                    eliminable.EliminarCuestionario(unCuestionario);

                }
            });

            CuestionarioViewHolder.btnEditar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    eliminable =fragmentManageCuestionarios;
                    eliminable.editarNombreCuestionario(unCuestionario);
                }
            });
        }

    }



    private Activity getRequiredActivity(View req_view) {
        Context context = req_view.getContext();
        while (context instanceof ContextWrapper) {
            if (context instanceof Activity) {
                return (Activity)context;
            }
            context = ((ContextWrapper)context).getBaseContext();
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return listaCuestionariosOriginales.size();
    }

    @Override
    public void onClick(View view) {
        listener.onClick(view);
    }

    @Override
    public boolean onLongClick(View v) {
        listenerLong.onLongClick(v);
        return true;
    }

    //creo el viewholder que mantiene las referencias
    //de los elementos de la celda

    private static class CuestionarioViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private TextView textView;
        private ImageButton btnEliminar;
        private ImageButton btnEditar;
        private TextView textUltima;
        private LinearLayout linearUltima;
        private TextView tagultima;
        private TextView textViewTipo;
        private ImageView tapImage;

        public CuestionarioViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imagenCamara);
            textView= itemView.findViewById(R.id.nombreArea);
            linearUltima=itemView.findViewById(R.id.linearUltimoPuntaje);
            textUltima=itemView.findViewById(R.id.ultimoPuntaje);
            tagultima=itemView.findViewById(R.id.tagUltimoPuntaje);
            textViewTipo= itemView.findViewById(R.id.idCuestionario);
            btnEliminar = itemView.findViewById(R.id.botonEliminar);
            btnEditar=itemView.findViewById(R.id.botonEditarItem);
            tapImage=itemView.findViewById(R.id.tap6);

            Typeface robotoL = Typeface.createFromAsset(itemView.getContext().getAssets(), "fonts/Roboto-Light.ttf");
            textView.setTypeface(robotoL);
            textViewTipo.setTypeface(robotoL);
            btnEliminar.setVisibility(View.VISIBLE);
            btnEditar.setVisibility(View.VISIBLE);

            imageView.setVisibility(View.GONE);
            tapImage.setVisibility(View.VISIBLE);

        }

        public void cargarCuestionario(Cuestionario unCuestionario,Context context) {

            textView.setText(unCuestionario.getNombreCuestionario());


            Realm realm = null;
            try {
                realm = Realm.getDefaultInstance();
            } catch (Exception e) {
                e.printStackTrace();
                Realm.init(context.getApplicationContext());
                realm=Realm.getDefaultInstance();
            }
            RealmResults<Cuestionario> losCuestionarios =realm.where(Cuestionario.class)
                    .equalTo("idCuestionario",unCuestionario.getIdCuestionario())
                    .findAll();

            if (losCuestionarios !=null){
                textUltima.setText("");
            }


        }


    }

    public interface Eliminable {
        void EliminarCuestionario(Cuestionario unCuestionario);

        void editarNombreCuestionario(Cuestionario unCuestionario);
    }
}
