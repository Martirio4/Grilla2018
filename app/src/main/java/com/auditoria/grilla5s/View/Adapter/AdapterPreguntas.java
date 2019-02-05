package com.auditoria.grilla5s.View.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.auditoria.grilla5s.Model.Pregunta;
import com.auditoria.grilla5s.Model.Pregunta;
import com.auditoria.grilla5s.R;
import com.auditoria.grilla5s.Utils.FuncionesPublicas;

import io.realm.RealmList;


public class AdapterPreguntas extends RecyclerView.Adapter implements View.OnClickListener, View.OnLongClickListener {

    private Context context;
    private RealmList<Pregunta> listaPreguntasOriginales;
    private View.OnClickListener listener;
    private AdapterView.OnLongClickListener listenerLong;
    private static String origen;
    private Notificable notificable;


    public void setListener(View.OnClickListener listener) {
        this.listener = listener;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void setListaPreguntasOriginales(RealmList<Pregunta> listaPreguntasOriginales) {
        this.listaPreguntasOriginales = listaPreguntasOriginales;
    }

    public void setOrigen(String origen) {
        AdapterPreguntas.origen = origen;
    }

    public void addListaPreguntasOriginales(RealmList<Pregunta> listaPreguntasOriginales) {
        this.listaPreguntasOriginales.addAll(listaPreguntasOriginales);
    }

    public Context getContext() {
        return context;
    }

    public void addPregunta(Pregunta nuevoPregunta) {
    this.listaPreguntasOriginales.add(nuevoPregunta);
    AdapterPreguntas.this.notifyDataSetChanged();
    }

    public void remove(Pregunta elPreguntaABorrar) {
        this.listaPreguntasOriginales.remove(elPreguntaABorrar);
        notifyDataSetChanged();
    }

    public interface Notificable{
        void eliminarPregunta(Pregunta unPregunta);
    }


    public RealmList<Pregunta> getListaPreguntasOriginales() {
        return listaPreguntasOriginales;
    }

    //crear vista y viewholder
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View viewCelda = layoutInflater.inflate(R.layout.detalle_celda_pre_auditoria, parent, false);
        PreguntaViewHolder ItemsViewHolder = new PreguntaViewHolder(viewCelda);
        viewCelda.setOnClickListener(this);

        return ItemsViewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final Pregunta unPregunta = listaPreguntasOriginales.get(position);
        PreguntaViewHolder itemViewHolder = (PreguntaViewHolder) holder;
        itemViewHolder.cargarPregunta(unPregunta,position);

        itemViewHolder.botonEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MaterialDialog.Builder(view.getContext())
                        .backgroundColor(ContextCompat.getColor(view.getContext(), R.color.tile1))
                        .contentColor(ContextCompat.getColor(view.getContext(), R.color.primary_text))
                        .titleColor(ContextCompat.getColor(view.getContext(), R.color.tile4))
                        .title(R.string.advertencia)
                        .content(R.string.preguntaSeElimina)
                        .positiveText(R.string.eliminar)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                FuncionesPublicas.borrarPregunta(unPregunta, AdapterPreguntas.this);
                            }
                        })
                        .negativeText(R.string.cancel)
                        .show();
            }

        });




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
        return listaPreguntasOriginales.size();
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

    private static class PreguntaViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewNumero;
        private TextView textViewDescripcion;
        private TextView textViewFaltantes;
        private ImageButton botonEliminar;
        private ImageButton botonEditar;



        PreguntaViewHolder(View itemView) {
            super(itemView);

            textViewNumero=  itemView.findViewById(R.id.tv_numero_item);
            textViewDescripcion=  itemView.findViewById(R.id.tv_descripcion_item);
            textViewFaltantes=itemView.findViewById(R.id.tv_preguntasFaltantes);
            botonEliminar = itemView.findViewById(R.id.botonEliminarItem);
            botonEditar=itemView.findViewById(R.id.botonEditarItem);


            Typeface robotoL = Typeface.createFromAsset(itemView.getContext().getAssets(), "fonts/Roboto-Light.ttf");
            Typeface robotoR = Typeface.createFromAsset(itemView.getContext().getAssets(), "fonts/Roboto-Regular.ttf");
            textViewNumero.setTypeface(robotoR);
            textViewDescripcion.setTypeface(robotoL);
            textViewFaltantes.setTypeface(robotoL);
        }

        void cargarPregunta(Pregunta unPregunta, Integer ordenCarga) {
            textViewNumero.setText(String.valueOf(ordenCarga+1));
            textViewDescripcion.setText(unPregunta.getTextoPregunta());
        }


    }

    

}
