package com.nomad.mrg5s.View.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Typeface;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.afollestad.materialdialogs.MaterialDialog;
import com.nomad.mrg5s.Model.Area;
import com.nomad.mrg5s.Model.Auditoria;
import com.nomad.mrg5s.Model.Cuestionario;
import com.nomad.mrg5s.R;
import com.nomad.mrg5s.Utils.FuncionesPublicas;
import com.nomad.mrg5s.View.Fragments.FragmentManageAreas;
import com.nomad.mrg5s.View.Fragments.FragmentSeleccionArea;
import com.squareup.picasso.Picasso;

import java.io.File;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

/**
 * Created by elmar on 18/5/2017.
 */

public class AdapterArea extends RecyclerView.Adapter implements View.OnClickListener, View.OnLongClickListener {

    private Context context;
    private RealmList<Area> listaAreasOriginales;
    private RealmList<Area> listaAreasFavoritos;
    private View.OnClickListener listener;
    private AdapterView.OnLongClickListener listenerLong;
    private Eliminable eliminable;

    public void setLongListener(View.OnLongClickListener unLongListener) {
        this.listenerLong = unLongListener;
    }

    public void setListener(View.OnClickListener listener) {
        this.listener = listener;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void setListaAreasOriginales(RealmList<Area> listaAreasOriginales) {
        this.listaAreasOriginales = listaAreasOriginales;
    }

    public void addListaAreasOriginales(RealmList<Area> listaAreasOriginales) {
        this.listaAreasOriginales.addAll(listaAreasOriginales);
    }


    public RealmList<Area> getListaAreasOriginales() {
        return listaAreasOriginales;
    }

    //crear vista y viewholder
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View viewCelda;
        FragmentActivity unaActivity = (FragmentActivity) context;
        FragmentManager fragmentManager = (FragmentManager) unaActivity.getSupportFragmentManager();
        FragmentManageAreas fragmentManageAreas = (FragmentManageAreas) fragmentManager.findFragmentByTag(FuncionesPublicas.FRAGMENTMANAGER_AREAS);

        viewCelda = layoutInflater.inflate(R.layout.detalle_celda_manage_areas2, parent, false);
        if (fragmentManageAreas != null && fragmentManageAreas.isVisible()) {

        } else {

            viewCelda.setOnClickListener(this);
        }
        AreaViewHolder areasViewHolder = new AreaViewHolder(viewCelda);

        return areasViewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final Area unArea = listaAreasOriginales.get(position);
        final AreaViewHolder AreaViewHolder = (AreaViewHolder) holder;
        AreaViewHolder.cargarArea(unArea, context);


        FragmentActivity unaActivity = (FragmentActivity) context;
        FragmentManager fragmentManager = unaActivity.getSupportFragmentManager();
        FragmentManageAreas fragmentManageAreas = (FragmentManageAreas) fragmentManager.findFragmentByTag(FuncionesPublicas.FRAGMENTMANAGER_AREAS);

        //solo permito editar y borrar areas desde manageAreas
        if (fragmentManageAreas != null && fragmentManageAreas.isVisible()) {

            AreaViewHolder.fabEliminar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    eliminable = (Eliminable) getRequiredActivity(v);
                    if (eliminable!=null) {
                        eliminable.EliminarArea(unArea);
                    }
                }
            });

            AreaViewHolder.fabEditar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    eliminable = (Eliminable) getRequiredActivity(view);
                    if (eliminable!=null) {
                        eliminable.editarArea(unArea);
                    }
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
        return listaAreasOriginales.size();
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

    private static class AreaViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private TextView nombreArea;
        private ImageButton fabEliminar;
        private ImageButton fabEditar;
        private TextView textUltima;
        private LinearLayout linearUltima;
        private TextView tagultima;
        private TextView textViewTipo;

        public AreaViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.imagenCamara);
            nombreArea = (TextView) itemView.findViewById(R.id.nombreArea);
            linearUltima=itemView.findViewById(R.id.linearUltimoPuntaje);
            textUltima=itemView.findViewById(R.id.ultimoPuntaje);
            tagultima=itemView.findViewById(R.id.tagUltimoPuntaje);
            textViewTipo= itemView.findViewById(R.id.idCuestionario);


            Typeface robotoL = Typeface.createFromAsset(itemView.getContext().getAssets(), "fonts/Roboto-Light.ttf");
            nombreArea.setTypeface(robotoL);
            textViewTipo.setTypeface(robotoL);
            fabEliminar = itemView.findViewById(R.id.botonEliminar);
            fabEliminar.setVisibility(View.GONE);
            fabEditar= itemView.findViewById(R.id.botonEditarItem);
            fabEditar.setVisibility(View.GONE);

            FragmentActivity unaActivity = (FragmentActivity) itemView.getContext();
            FragmentManager fragmentManager = (FragmentManager) unaActivity.getSupportFragmentManager();
            FragmentManageAreas fragmentManageAreas = (FragmentManageAreas) fragmentManager.findFragmentByTag(FuncionesPublicas.FRAGMENTMANAGER_AREAS);
            FragmentSeleccionArea fragmentSeleccionArea = (FragmentSeleccionArea) fragmentManager.findFragmentByTag(FuncionesPublicas.FRAGMENT_SELECCION_AREAS);

            if (fragmentManageAreas != null && fragmentManageAreas.isVisible()) {
                fabEliminar.setVisibility(View.VISIBLE);
                fabEditar.setVisibility(View.VISIBLE);
            }
            if (fragmentManageAreas==null&&fragmentSeleccionArea==null){
                linearUltima.setVisibility(View.VISIBLE);
                textUltima.setVisibility(View.VISIBLE);
                tagultima.setVisibility(View.VISIBLE);
            }

        }

        public void cargarArea(Area unArea, Context context) {
            Realm realm = null;
            try {
                realm = Realm.getDefaultInstance();
            } catch (Exception e) {
                e.printStackTrace();
                Realm.init(context.getApplicationContext());
                realm=Realm.getDefaultInstance();
            }

            if (unArea.getFotoArea()!=null) {
                //CARGO LA IMAGEN DEL AREA
                File f =new File(unArea.getFotoArea().getRutaFoto());
                Picasso.with(imageView.getContext())
                        .load(f)
                        .into(imageView);
            }

            nombreArea.setText(unArea.getNombreArea());

            if (unArea.getIdCuestionario()!=null) {
                //CARGO EL TIPO DE AREA
                Cuestionario elCuestionario = realm.where(Cuestionario.class)
                        .equalTo("idCuestionario", unArea.getIdCuestionario())
                        .findFirst();

                if (elCuestionario!=null){
                    textViewTipo.setText(elCuestionario.getNombreCuestionario());
                }
            }

            //CARGO CANTIDAD DE AUDITORIAS REALIZADAS
            RealmResults<Auditoria> lasAudit=realm.where(Auditoria.class)
                    .equalTo("areaAuditada.idArea",unArea.getIdArea())
                    .findAll();

            if (lasAudit!=null){
                String numeroAudits = String.valueOf(lasAudit.size());
                textUltima.setText(numeroAudits);
            }


        }


    }

    public interface Eliminable {
        void EliminarArea(Area unArea);

        void editarArea(Area unArea);
    }
}
