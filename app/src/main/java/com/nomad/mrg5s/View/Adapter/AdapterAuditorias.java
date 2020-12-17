package com.nomad.mrg5s.View.Adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.nomad.mrg5s.Model.Auditoria;
import com.nomad.mrg5s.Model.Ese;
import com.nomad.mrg5s.R;
import com.nomad.mrg5s.Utils.FuncionesPublicas;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.NumberFormat;
import java.util.Locale;

import io.realm.RealmList;

/** ultima modificaion
 * 22/02/2018
 * Martirio
 */

public class AdapterAuditorias extends RecyclerView.Adapter implements View.OnClickListener, View.OnLongClickListener {

    private Context context;
    private RealmList<Auditoria> listaAuditsOriginales;
    private RealmList<Auditoria> listaAuditoriasFavoritos;
    private View.OnClickListener listener;
    private AdapterView.OnLongClickListener listenerLong;
    private String idAuditoria=null;


    public void setLongListener(View.OnLongClickListener unLongListener) {
        this.listenerLong = unLongListener;
    }

    public void setListener(View.OnClickListener listener) {
        this.listener = listener;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void setListaAuditsOriginales(RealmList<Auditoria> listaAuditsOriginales) {
        this.listaAuditsOriginales = listaAuditsOriginales;
    }

    public void addListaAuditoriasOriginales(RealmList<Auditoria> listaAuditoriasOriginales) {
        this.listaAuditsOriginales.addAll(listaAuditoriasOriginales);
    }


    public RealmList<Auditoria> getListaAuditsOriginales() {
        return listaAuditsOriginales;
    }

    //crear vista y viewholder
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View viewCelda;
        FragmentActivity unaActivity = (FragmentActivity) context;
        FragmentManager fragmentManager = (FragmentManager) unaActivity.getSupportFragmentManager();
        viewCelda = layoutInflater.inflate(R.layout.detalle_celda_ver_auditorias, parent, false);
        viewCelda.setOnClickListener(this);

        return new AuditoriaViewHolder(viewCelda);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        final Auditoria unAuditoria = listaAuditsOriginales.get(position);

            AuditoriaViewHolder auditoriasViewHolder = (AuditoriaViewHolder) holder;
            auditoriasViewHolder.cargarAuditoria(unAuditoria);
            ((AuditoriaViewHolder) holder).botonEliminarAuditoria.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new MaterialDialog.Builder(view.getContext())
                            .contentColor(ContextCompat.getColor(view.getContext(), R.color.primary_text))
                            .titleColor(ContextCompat.getColor(view.getContext(), R.color.tile4))
                            .title(R.string.advertencia)
                            .content(R.string.auditoriaSeEliminara)
                            .positiveText(R.string.ok)
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    listaAuditsOriginales.remove(position);
                                    if (FuncionesPublicas.borrarAuditoriaSeleccionada(unAuditoria.getIdAuditoria(),context)) {
                                        Toast.makeText(context, context.getString(R.string.auditEliminada), Toast.LENGTH_SHORT).show();
                                    }
                                    AdapterAuditorias.this.notifyDataSetChanged();
                                }
                            })
                            .negativeText(R.string.cancel)
                            .show();
                }
            });
    }

    @Override
    public int getItemCount() {
        return listaAuditsOriginales.size();
    }


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

    private class AuditoriaViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private TextView textFecha;
        private TextView text1s;
        private TextView text2s;
        private TextView text3s;
        private TextView textFinal;
        private TextView textFoto;
        private TextView text4s;
        private TextView text5s;


        private TextView tag1s;
        private TextView tag2s;
        private TextView tag3s;
        private TextView tagfinal;
        private TextView tag4s;
        private TextView tag5s;

        private CardView tarjetaPutaje;
        private ImageButton botonEliminarAuditoria;



        //private TextView textViewTitulo;


        public AuditoriaViewHolder(View itemView) {
            super(itemView);

            Typeface roboto = Typeface.createFromAsset(itemView.getContext().getAssets(), "fonts/Roboto-Light.ttf");
            imageView = (ImageView) itemView.findViewById(R.id.imagenAreaResumenAuditorias);

            tag1s =  itemView.findViewById(R.id.tagPuntaje1S);
            tag2s =  itemView.findViewById(R.id.tagPuntaje2s);
            tag3s =  itemView.findViewById(R.id.tagPuntaje3s);
            tagfinal =  itemView.findViewById(R.id.tagPuntajeFinal);
            text1s =  itemView.findViewById(R.id.puntaje1s);
            text2s =  itemView.findViewById(R.id.puntaje2s);
            text3s =  itemView.findViewById(R.id.puntaje3s);
            textFinal =  itemView.findViewById(R.id.textPuntajeFinal);
            textFecha =  itemView.findViewById(R.id.fechaAuditoria);
            textFoto =  itemView.findViewById(R.id.nombreAreaResumenAuditoria);
            tarjetaPutaje=itemView.findViewById(R.id.tarjetaPuntaje);
            text4s =  itemView.findViewById(R.id.puntaje4s);
            text5s =  itemView.findViewById(R.id.puntaje5s);
            tag4s=itemView.findViewById(R.id.tagPuntaje4s);
            tag5s=itemView.findViewById(R.id.tagPuntaje5s);
            botonEliminarAuditoria =  itemView.findViewById(R.id.botonEliminarAuditoria);


            tag1s.setTypeface(roboto);
            tag2s.setTypeface(roboto);
            tag3s.setTypeface(roboto);
            tagfinal.setTypeface(roboto);
            text1s.setTypeface(roboto);
            text2s.setTypeface(roboto);
            text3s.setTypeface(roboto);
            textFinal.setTypeface(roboto);
            textFecha.setTypeface(roboto);
            textFoto.setTypeface(roboto);
            tag4s.setTypeface(roboto);
            text4s.setTypeface(roboto);
            tag5s.setTypeface(roboto);
            text5s.setTypeface(roboto);
        }

        public void cargarAuditoria(Auditoria unAuditoria) {

            Double promedio5s;
            Double puntaje1;
            Double puntaje2;
            Double puntaje3;
            Double puntaje4;
            Double puntaje5;

                //COMIENZA CALCULO PUNTAJES

            if (unAuditoria.getEstructuraAuditoria().equals(FuncionesPublicas.ESTRUCTURA_ESTRUCTURADA)){
                 promedio5s = unAuditoria.getPuntajeFinal();
                puntaje1=unAuditoria.getListaEses().get(0).getPuntajeEse()/5;
                puntaje2=unAuditoria.getListaEses().get(1).getPuntajeEse()/5;
                puntaje3=unAuditoria.getListaEses().get(2).getPuntajeEse()/5;
                puntaje4=unAuditoria.getListaEses().get(3).getPuntajeEse()/5;
                puntaje5=unAuditoria.getListaEses().get(4).getPuntajeEse()/5;
            }
            else{
                double puntajeIdeal=0.0;
                for (Ese unaeses :
                        unAuditoria.getListaEses()) {
                    puntajeIdeal=puntajeIdeal+unaeses.getListaPreguntas().size()*4;
                }

                promedio5s=unAuditoria.getPuntajeFinal()/puntajeIdeal;
                puntaje1=unAuditoria.getListaEses().get(0).getPuntajeEse()/(unAuditoria.getListaEses().get(0).getListaPreguntas().size()*4);
                puntaje2=unAuditoria.getListaEses().get(1).getPuntajeEse()/(unAuditoria.getListaEses().get(1).getListaPreguntas().size()*4);
                puntaje3=unAuditoria.getListaEses().get(2).getPuntajeEse()/(unAuditoria.getListaEses().get(2).getListaPreguntas().size()*4);
                puntaje4=unAuditoria.getListaEses().get(3).getPuntajeEse()/(unAuditoria.getListaEses().get(3).getListaPreguntas().size()*4);
                puntaje5=unAuditoria.getListaEses().get(4).getPuntajeEse()/(unAuditoria.getListaEses().get(4).getListaPreguntas().size()*4);
            }
                //FIN CALCULO PUNTAJES

            if (promedio5s <=0.5f){
                tarjetaPutaje.setBackgroundColor(ContextCompat.getColor(context, R.color.semaRojo));
            }
            else{
                if (promedio5s <0.8f){
                    tarjetaPutaje.setBackgroundColor(ContextCompat.getColor(context,R.color.semaAmarillo));
                }
                else{
                    tarjetaPutaje.setBackgroundColor(ContextCompat.getColor(context,R.color.semaVerde));
                }
            }


            Locale locale = new Locale("en","US");
            NumberFormat format = NumberFormat.getPercentInstance(locale);
            String percentage1 = format.format(puntaje1);
            String percentage2 = format.format(puntaje2);
            String percentage3 = format.format(puntaje3);
            String percentage4 = format.format(puntaje4);
            String percentage5 = format.format(puntaje5);
            String percentage6 = format.format(promedio5s);

            if (unAuditoria.getListaEses().get(0).getPuntajeEse()==9.9) {
                text1s.setText("-");
            } else {
                text1s.setText(percentage1);
            }
            if (unAuditoria.getListaEses().get(1).getPuntajeEse()==9.9) {
                text2s.setText("-");
            } else {
                text2s.setText(percentage2);
            }
            if (unAuditoria.getListaEses().get(2).getPuntajeEse()==9.9) {
                text3s.setText("-");
            } else {
                text3s.setText(percentage3);
            }
            if (unAuditoria.getListaEses().get(3).getPuntajeEse()==9.9) {
                text4s.setText("-");
            } else {
                text4s.setText(percentage4);
            }
            if (unAuditoria.getListaEses().get(4).getPuntajeEse()==9.9) {
                text5s.setText("-");
            } else {
                text5s.setText(percentage5);
            }
            textFinal.setText(percentage6);
            textFecha.setText(FuncionesPublicas.dameFechaString(unAuditoria.getFechaAuditoria(),"largo"));
            textFoto.setText(unAuditoria.getAreaAuditada().getNombreArea());

            File f =new File(unAuditoria.getAreaAuditada().getFotoArea().getRutaFoto());
            Picasso.with(imageView.getContext())
                    .load(f)
                    .into(imageView);


        }



        }


    }




    // Decodes image and scales it to reduce memory consumption