package com.nomad.mrg5s.View.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.nomad.mrg5s.Model.Criterio;
import com.nomad.mrg5s.R;
import com.nomad.mrg5s.Utils.FuncionesPublicas;

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
        //viewCelda.setOnClickListener(this);

        return ItemsViewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final Criterio unCriterio = listaCriteriosOriginales.get(position);
        final CriterioViewHolder itemViewHolder = (CriterioViewHolder) holder;
        itemViewHolder.cargarCriterio(unCriterio);


        itemViewHolder.botonEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                assert unCriterio != null;
                final MaterialDialog mDialog = new MaterialDialog.Builder(view.getContext())
                        .title(view.getResources().getString(R.string.EditarCriterio))
                        .contentColor(ContextCompat.getColor(view.getContext(), R.color.primary_text))
                        .backgroundColor(ContextCompat.getColor(view.getContext(), R.color.tile1))
                        .titleColor(ContextCompat.getColor(view.getContext(), R.color.tile4))
                        .content(view.getResources().getString(R.string.favorEditeCriterio))
                        .input(view.getResources().getString(R.string.comment), unCriterio.getTextoCriterio(), new MaterialDialog.InputCallback() {
                            @Override
                            public void onInput(@NonNull MaterialDialog dialog, final CharSequence input) {

                                if (input!=null && !input.toString().isEmpty()) {
                                    FuncionesPublicas.cambiarTextoCriterio(unCriterio,input.toString(),context);
                                    itemViewHolder.textViewDescripcion.setText(input.toString());
                                }
                                AdapterCriterios.this.notifyDataSetChanged();
                            }
                        })
                        .build();
                EditText elEdit = mDialog.getInputEditText();
                if (elEdit!=null) {
                    elEdit.setInputType(InputType.TYPE_CLASS_TEXT |
                            InputType.TYPE_TEXT_FLAG_MULTI_LINE |
                            InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
                }
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
        private ScrollView scroll;



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

        void cargarCriterio(Criterio unCriterio) {
            textViewNumero.setText(String.valueOf(unCriterio.getPuntajeCriterio()));
            textViewDescripcion.setText(unCriterio.getTextoCriterio());
            switch (unCriterio.getPuntajeCriterio()){
                case 1:
                    textViewNumero.setBackground(textViewDescripcion.getContext().getResources().getDrawable(R.drawable.boton_malo));
                    break;
                case 2:
                    textViewNumero.setBackground(textViewDescripcion.getContext().getResources().getDrawable(R.drawable.boton_regular));
                    break;
                case 3:
                    textViewNumero.setBackground(textViewDescripcion.getContext().getResources().getDrawable(R.drawable.boton_bueno));
                    break;
                case 4:
                    textViewNumero.setBackground(textViewDescripcion.getContext().getResources().getDrawable(R.drawable.boton_excelente));
                    break;
            }
        }


    }

    

}
