package com.auditoria.grilla5s.View.Adapter;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.auditoria.grilla5s.Model.Foto;
import com.auditoria.grilla5s.R;
import com.squareup.picasso.Picasso;

import java.io.File;

import io.realm.RealmList;

/**
 * Created by elmar on 18/5/2017.
 */

public class AdapterFotos extends RecyclerView.Adapter implements View.OnClickListener, View.OnLongClickListener {

    private Context context;
    private RealmList<Foto> listaFotosOriginales;
    private RealmList<Foto> listaFotosFavoritos;
    private View.OnClickListener listener;
    private AdapterView.OnLongClickListener listenerLong;
    private Favoritable favoritable;
    private Boolean esRecyclerThumbNail;

    public void setLongListener(View.OnLongClickListener unLongListener) {
        this.listenerLong = unLongListener;
    }

    public void setListener(View.OnClickListener listener) {
        this.listener = listener;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void setListaFotosOriginales(RealmList<Foto> listaFotosOriginales) {
        this.listaFotosOriginales = listaFotosOriginales;
    }

    public void addListaFotosOriginales(RealmList<Foto> listaFotosOriginales) {
        this.listaFotosOriginales.addAll(listaFotosOriginales);
    }


    public RealmList<Foto> getListaFotosOriginales() {
        return listaFotosOriginales;
    }

    //crear vista y viewholder
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View viewCelda;
        FragmentActivity unaActivity = (FragmentActivity) context;
        FragmentManager fragmentManager = unaActivity.getSupportFragmentManager();
        if (esRecyclerThumbNail){
            viewCelda = layoutInflater.inflate(R.layout.detalle_celda_recycler_fotos_viejas, parent, false);
        }
        else{
            viewCelda = layoutInflater.inflate(R.layout.detalle_celda_recycler_fotos, parent, false);
        }

        viewCelda.setOnClickListener(this);

        return new FotoViewHolder(viewCelda);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final Foto unFoto = listaFotosOriginales.get(position);
        FotoViewHolder fotoViewHolder = (FotoViewHolder) holder;
        fotoViewHolder.cargarFoto(unFoto);
    }

    @Override
    public int getItemCount() {
        return listaFotosOriginales.size();
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

    private class FotoViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private TextView textView;
        private EditText editText;
        //private TextView textViewTitulo;


        public FotoViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imagenCamara);
            if (!esRecyclerThumbNail){
                textView = itemView.findViewById(R.id.nombreNuevaArea);
                editText= itemView.findViewById(R.id.editFoto);
            }

        }

        public void cargarFoto(Foto unFoto) {

                if (!esRecyclerThumbNail && unFoto.getComentarioFoto()!=null && !unFoto.getComentarioFoto().isEmpty()){
                    textView.setText(unFoto.getComentarioFoto());
                }

                File f = new File(unFoto.getRutaFoto());


                Picasso.with(imageView.getContext())
                        .load(f)
                        .into(imageView);

        }


    }

    public interface Favoritable {
        void recibirFotoFavorito(Foto unFoto);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {

        esRecyclerThumbNail = recyclerView.getId() == R.id.recyclerFotosViejas;

        super.onAttachedToRecyclerView(recyclerView);
    }

    // Decodes image and scales it to reduce memory consumption
    

}
