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
import com.auditoria.grilla5s.Model.Criterio;
import com.auditoria.grilla5s.R;
import com.auditoria.grilla5s.Utils.FuncionesPublicas;

import io.realm.RealmList;


public class AdapterCriterios extends RecyclerView.Adapter implements View.OnClickListener, View.OnLongClickListener {

    private Context context;
    private RealmList<Criterio> listaCriteriosOriginales;
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

    public void setListaCriteriosOriginales(RealmList<Criterio> listaCriteriosOriginales) {
        this.listaCriteriosOriginales = listaCriteriosOriginales;
    }

    public void setOrigen(String origen) {
        AdapterCriterios.origen = origen;
    }

    public void addListaCriteriosOriginales(RealmList<Criterio> listaCriteriosOriginales) {
        this.listaCriteriosOriginales.addAll(listaCriteriosOriginales);
    }

    public Context getContext() {
        return context;
    }

    public void addCriterio(Criterio nuevoCriterio) {
    this.listaCriteriosOriginales.add(nuevoCriterio);
    AdapterCriterios.this.notifyDataSetChanged();
    }

    public void remove(Criterio elCriterioABorrar) {
        this.listaCriteriosOriginales.remove(elCriterioABorrar);
        notifyDataSetChanged();
    }

    public interface Notificable{
        void eliminarCriterio(Criterio unCriterio);
    }


    public RealmList<Criterio> getListaCriteriosOriginales() {
        return listaCriteriosOriginales;
    }

    //crear vista y viewholder
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View viewCelda = layoutInflater.inflate(R.layout.detalle_celda_editar_criterios, parent, false);
        CriterioViewHolder ItemsViewHolder = new CriterioViewHolder(viewCelda);
        viewCelda.setOnClickListener(this);

        return ItemsViewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final Criterio unCriterio = listaCriteriosOriginales.get(position);
        CriterioViewHolder itemViewHolder = (CriterioViewHolder) holder;
        itemViewHolder.cargarCriterio(unCriterio,position);


        itemViewHolder.botonEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final MaterialDialog mDialog = new MaterialDialog.Builder(view.getContext())
                        //.title(view.getResources().getString(R.string.EditarItem))
                        //.contentColor(ContextCompat.getColor(view.getContext(), R.color.primary_text))
                        //.backgroundColor(ContextCompat.getColor(view.getContext(), R.color.tile1))
                        //.titleColor(ContextCompat.getColor(view.getContext(), R.color.tile4))
                        //.content(view.getResources().getString(R.string.favorEditeItem))
                        //.inputType(InputType.TYPE_CLASS_TEXT)
                        /*.input(view.getResources().getString(R.string.comment),unItem.getTituloItem(), new MaterialDialog.InputCallback() {
                            @Override
                            public void onInput(MaterialDialog dialog, final CharSequence input) {


                            }
                        })*/
                        .customView(R.layout.dialogo_editar,false)
                        .build();

                View laView=mDialog.getCustomView();
                final EditText content= laView.findViewById(R.id.editTextoItem);
                assert unCriterio != null;
                content.setText(unCriterio.getTextoCriterio());

                TextView tituloDialogo=laView.findViewById(R.id.tituloDialogoItem);
                tituloDialogo.setText(getContext().getString(R.string.tituloDialogoModificarItem));
                tituloDialogo.setFocusableInTouchMode(false);

                TextView botonOk= laView.findViewById(R.id.botonDialogoSi);
                botonOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (content.getText()!=null && !content.getText().toString().isEmpty()) {
                            FuncionesPublicas.cambiarTextoCriterio(unCriterio,content.getText().toString(),context);
                        }
                        AdapterCriterios.this.notifyDataSetChanged();
                        mDialog.hide();
                    }
                });
                mDialog.show();
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
        return listaCriteriosOriginales.size();
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

    private static class CriterioViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewNumero;
        private TextView textViewDescripcion;
        private ImageButton botonEditar;



        CriterioViewHolder(View itemView) {
            super(itemView);

            textViewNumero=  itemView.findViewById(R.id.tv_numero_item);
            textViewDescripcion=  itemView.findViewById(R.id.tv_descripcion_item);
            botonEditar=itemView.findViewById(R.id.botonEditarItem);


            Typeface robotoL = Typeface.createFromAsset(itemView.getContext().getAssets(), "fonts/Roboto-Light.ttf");
            Typeface robotoR = Typeface.createFromAsset(itemView.getContext().getAssets(), "fonts/Roboto-Regular.ttf");
            textViewNumero.setTypeface(robotoR);
            textViewDescripcion.setTypeface(robotoL);

        }

        void cargarCriterio(Criterio unCriterio, Integer ordenCarga) {
            textViewNumero.setText(unCriterio.getPuntajeCriterio());
            textViewDescripcion.setText(unCriterio.getTextoCriterio());
        }


    }

    

}
