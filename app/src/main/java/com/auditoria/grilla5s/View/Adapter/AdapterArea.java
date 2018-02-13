package com.auditoria.grilla5s.View.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Typeface;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;


import com.auditoria.grilla5s.Model.Area;
import com.squareup.picasso.Picasso;

import java.io.File;

import io.realm.RealmList;

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
        FragmentManageAreas fragmentManageAreas = (FragmentManageAreas) fragmentManager.findFragmentByTag("fragmentManageAreas");


        if (fragmentManageAreas != null && fragmentManageAreas.isVisible()) {
            viewCelda = layoutInflater.inflate(R.layout.detalle_celda_manage_areas2, parent, false);

        } else {
            viewCelda = layoutInflater.inflate(R.layout.detalle_celda_manage_areas2, parent, false);
            viewCelda.setOnClickListener(this);
        }
        AreaViewHolder areasViewHolder = new AreaViewHolder(viewCelda);

        return areasViewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final Area unArea = listaAreasOriginales.get(position);
        AreaViewHolder AreaViewHolder = (AreaViewHolder) holder;
        AreaViewHolder.cargarArea(unArea);

        FragmentActivity unaActivity = (FragmentActivity) context;
        FragmentManager fragmentManager = (FragmentManager) unaActivity.getSupportFragmentManager();
        FragmentManageAreas fragmentManageAreas = (FragmentManageAreas) fragmentManager.findFragmentByTag("fragmentManageAreas");


        if (fragmentManageAreas != null && fragmentManageAreas.isVisible()) {

            AreaViewHolder.fabEliminar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    eliminable = (Eliminable) getRequiredActivity(v);
                    eliminable.EliminarArea(unArea);

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
        private TextView textView;
        private ImageButton fabEliminar;

        public AreaViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.imagenCamara);
            textView= (TextView) itemView.findViewById(R.id.nombreArea);
            Typeface robotoL = Typeface.createFromAsset(itemView.getContext().getAssets(), "fonts/Roboto-Light.ttf");
            textView.setTypeface(robotoL);
            fabEliminar = (ImageButton) itemView.findViewById(R.id.botonEliminar);
            fabEliminar.setVisibility(View.GONE);

            FragmentActivity unaActivity = (FragmentActivity) itemView.getContext();
            FragmentManager fragmentManager = (FragmentManager) unaActivity.getSupportFragmentManager();
            FragmentManageAreas fragmentManageAreas = (FragmentManageAreas) fragmentManager.findFragmentByTag("fragmentManageAreas");

            if (fragmentManageAreas != null && fragmentManageAreas.isVisible()) {
                fabEliminar.setVisibility(View.VISIBLE);
            }
        }

        public void cargarArea(Area unArea) {

            if (unArea.getFotoArea()!=null) {
            File f =new File(unArea.getFotoArea().getRutaFoto());
            Picasso.with(imageView.getContext())
                    .load(f)
                    .into(imageView);

            textView.setText(unArea.getNombreArea());
            }
        }


    }

    public interface Eliminable {
        public void EliminarArea(Area unArea);
    }
}
