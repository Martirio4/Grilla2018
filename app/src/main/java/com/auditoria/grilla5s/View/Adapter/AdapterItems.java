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

import com.auditoria.grilla5s.Model.Item;
import com.auditoria.grilla5s.R;
import com.squareup.picasso.Picasso;

import java.io.File;

import io.realm.RealmList;

/**
 * Created by elmar on 18/5/2017.
 */

public class AdapterItems extends RecyclerView.Adapter implements View.OnClickListener, View.OnLongClickListener {

    private Context context;
    private RealmList<Item> listaItemsOriginales;
    private RealmList<Item> listaItemsFavoritos;
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

    public void setListaItemsOriginales(RealmList<Item> listaItemsOriginales) {
        this.listaItemsOriginales = listaItemsOriginales;
    }

    public void addListaItemsOriginales(RealmList<Item> listaItemsOriginales) {
        this.listaItemsOriginales.addAll(listaItemsOriginales);
    }


    public RealmList<Item> getListaItemsOriginales() {
        return listaItemsOriginales;
    }

    //crear vista y viewholder
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);

        View viewCelda = layoutInflater.inflate(R.layout.detalle_celda_pre_auditoria, parent, false);
        ItemViewHolder ItemsViewHolder = new ItemViewHolder(viewCelda);

        return ItemsViewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final Item unItem = listaItemsOriginales.get(position);
        ItemViewHolder ItemViewHolder = (ItemViewHolder) holder;
        ItemViewHolder.cargarItem(unItem);


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
        return listaItemsOriginales.size();
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

    private static class ItemViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewNumero;
        private TextView textViewDescripcion;


        public ItemViewHolder(View itemView) {
            super(itemView);

            textViewNumero= (TextView) itemView.findViewById(R.id.tv_numero_item);
            textViewDescripcion= (TextView) itemView.findViewById(R.id.tv_descripcion_item);
            Typeface robotoL = Typeface.createFromAsset(itemView.getContext().getAssets(), "fonts/Roboto-Light.ttf");
            Typeface robotoR = Typeface.createFromAsset(itemView.getContext().getAssets(), "fonts/Roboto-Regular.ttf");
            textViewNumero.setTypeface(robotoR);
            textViewDescripcion.setTypeface(robotoL);
        }

        public void cargarItem(Item unItem) {
            String idItem=unItem.getIdItem().substring(unItem.getIdItem().length()-1);
            textViewNumero.setText(idItem);
            textViewDescripcion.setText(unItem.getCriterio());
        }


    }

}
