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
import com.auditoria.grilla5s.Model.Item;
import com.auditoria.grilla5s.Model.Pregunta;
import com.auditoria.grilla5s.R;
import com.auditoria.grilla5s.Utils.FuncionesPublicas;

import io.realm.RealmList;


public class AdapterItems extends RecyclerView.Adapter implements View.OnClickListener, View.OnLongClickListener {

    private Context context;
    private RealmList<Item> listaItemsOriginales;
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

    public void setListaItemsOriginales(RealmList<Item> listaItemsOriginales) {
        this.listaItemsOriginales = listaItemsOriginales;
    }

    public void setOrigen(String origen) {
        AdapterItems.origen = origen;
    }

    public void addListaItemsOriginales(RealmList<Item> listaItemsOriginales) {
        this.listaItemsOriginales.addAll(listaItemsOriginales);
    }

    public Context getContext() {
        return context;
    }

    public void addItem(Item nuevoItem) {
    this.listaItemsOriginales.add(nuevoItem);
    AdapterItems.this.notifyDataSetChanged();
    }

    public void remove(Item elItemABorrar) {
        this.listaItemsOriginales.remove(elItemABorrar);
        notifyDataSetChanged();
    }

    public interface Notificable{
        void eliminarItem(Item unItem);
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
        viewCelda.setOnClickListener(this);

        return ItemsViewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final Item unItem = listaItemsOriginales.get(position);
        ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
        itemViewHolder.cargarItem(unItem,position);

        itemViewHolder.botonEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MaterialDialog.Builder(view.getContext())
                        .backgroundColor(ContextCompat.getColor(view.getContext(), R.color.tile1))
                        .contentColor(ContextCompat.getColor(view.getContext(), R.color.primary_text))
                        .titleColor(ContextCompat.getColor(view.getContext(), R.color.tile4))
                        .title(R.string.advertencia)
                        .content(R.string.itemSeEliminara)
                        .positiveText(R.string.continuar)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            private Context context;

                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                listaItemsOriginales.remove(position);
                                FuncionesPublicas.borrarItem(unItem.getIdItem(),unItem.getIdEse(), unItem.getIdCuestionario(), AdapterItems.this);

                            }
                        })
                        .negativeText(R.string.cancel)
                        .show();
            }
        });

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
                content.setText(unItem.getTituloItem());

                TextView tituloDialogo=laView.findViewById(R.id.tituloDialogoItem);
                tituloDialogo.setText(getContext().getString(R.string.tituloDialogoModificarItem));
                tituloDialogo.setFocusableInTouchMode(false);

                TextView botonOk= laView.findViewById(R.id.botonDialogoSi);
                botonOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (content.getText()!=null && !content.getText().toString().isEmpty()) {
                            FuncionesPublicas.cambiarTextoItem(unItem,content.getText().toString(),context);
                        }
                        AdapterItems.this.notifyDataSetChanged();
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
        private TextView textViewFaltantes;
        private ImageButton botonEliminar;
        private ImageButton botonEditar;



        ItemViewHolder(View itemView) {
            super(itemView);

            textViewNumero=  itemView.findViewById(R.id.tv_numero_item);
            textViewDescripcion=  itemView.findViewById(R.id.tv_descripcion_item);
            textViewFaltantes=itemView.findViewById(R.id.tv_preguntasFaltantes);
            botonEditar=itemView.findViewById(R.id.botonEditarItem);
            botonEliminar=itemView.findViewById(R.id.botonEliminarItem);



            Typeface robotoL = Typeface.createFromAsset(itemView.getContext().getAssets(), "fonts/Roboto-Light.ttf");
            Typeface robotoR = Typeface.createFromAsset(itemView.getContext().getAssets(), "fonts/Roboto-Regular.ttf");
            textViewNumero.setTypeface(robotoR);
            textViewDescripcion.setTypeface(robotoL);
            textViewFaltantes.setTypeface(robotoL);
        }

        void cargarItem(Item unItem, Integer ordenCarga) {
            textViewNumero.setText(String.valueOf(ordenCarga+1));
            textViewDescripcion.setText(unItem.getTituloItem());

            if (origen!=null && origen.equals(FuncionesPublicas.EDITAR_CUESTIONARIO)) {
                botonEditar.setVisibility(View.VISIBLE);
                botonEliminar.setVisibility(View.VISIBLE);
                textViewFaltantes.setTextColor(ContextCompat.getColor(textViewFaltantes.getContext(),R.color.tile5));
                String elTexto = unItem.getListaPreguntas().size() + " " +textViewFaltantes.getContext().getString(R.string.preguntas);
                textViewFaltantes.setText(elTexto);
            }
            else {
                botonEditar.setVisibility(View.GONE);
                botonEliminar.setVisibility(View.GONE);
                Integer faltante=unItem.getListaPreguntas().size();

                for (Pregunta preg:unItem.getListaPreguntas()
                        ) {
                    if (preg.getPuntaje()!=null){
                        faltante=faltante-1;
                    }
                }
                if (faltante==0){
                    textViewFaltantes.setTextColor(ContextCompat.getColor(textViewFaltantes.getContext(),R.color.tile5));
                    textViewFaltantes.setText(textViewFaltantes.getContext().getResources().getString(R.string.preguntasCompletadas));
                }
                else{
                    String texto=textViewFaltantes.getContext().getResources().getString(R.string.faltanPreguntas)+" " + faltante + " " + textViewFaltantes.getContext().getResources().getString(R.string.pregunta);
                    textViewFaltantes.setTextColor(ContextCompat.getColor(textViewFaltantes.getContext(),R.color.textoRojo));
                    textViewFaltantes.setText(texto);
                }
            }

        }


    }

    

}
