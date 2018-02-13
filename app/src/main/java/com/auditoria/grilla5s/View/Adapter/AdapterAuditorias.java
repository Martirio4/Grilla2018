package com.auditoria.grilla5s.View.Adapter;

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

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.nomad.audit5s.R;
import com.nomad.audit5s.controller.ControllerDatos;
import com.nomad.audit5s.model.Auditoria;
import com.nomad.audit5s.model.SubItem;
import com.nomad.audit5s.utils.FuncionesPublicas;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmList;

/**
 * Created by elmar on 18/5/2017.
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
                        .positiveText(R.string.continuar)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                listaAuditsOriginales.remove(position);
                                FuncionesPublicas.borrarAuditoriaSeleccionada(unAuditoria.getIdAuditoria());
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

            tag1s = (TextView) itemView.findViewById(R.id.tagPuntaje1S);
            tag2s = (TextView) itemView.findViewById(R.id.tagPuntaje2s);
            tag3s = (TextView) itemView.findViewById(R.id.tagPuntaje3s);
            tagfinal = (TextView) itemView.findViewById(R.id.tagPuntajeFinal);
            text1s = (TextView) itemView.findViewById(R.id.puntaje1s);
            text2s = (TextView) itemView.findViewById(R.id.puntaje2s);
            text3s = (TextView) itemView.findViewById(R.id.puntaje3s);
            textFinal = (TextView) itemView.findViewById(R.id.textPuntajeFinal);
            textFecha = (TextView) itemView.findViewById(R.id.fechaAuditoria);
            textFoto = (TextView) itemView.findViewById(R.id.nombreAreaResumenAuditoria);
            tarjetaPutaje=(CardView) itemView.findViewById(R.id.tarjetaPuntaje);
            text4s = (TextView) itemView.findViewById(R.id.puntaje4s);
            text5s = (TextView) itemView.findViewById(R.id.puntaje5s);
            tag4s=(TextView)itemView.findViewById(R.id.tagPuntaje4s);
            tag5s=(TextView)itemView.findViewById(R.id.tagPuntaje5s);
            botonEliminarAuditoria = (ImageButton) itemView.findViewById(R.id.botonEliminarAuditoria);


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


                //COMIENZA CALCULO PUNTAJES
                if (unAuditoria.getIdAuditoria()!=null && !unAuditoria.getIdAuditoria().isEmpty()) {
                    ControllerDatos controllerDatos=new ControllerDatos(context);
                    List<String> listaSeiri=controllerDatos.traerSeiri();
                    List<String>listaSeiton=controllerDatos.traerSeiton();
                    List<String>listaSeiso=controllerDatos.traerSeiso();
                    List<String>listaSeiketsu=controllerDatos.traerSeiketsu();
                    List<String>listaShitsuke=controllerDatos.traerShitsuke();

                    Realm realm = Realm.getDefaultInstance();

                    Integer sumaSeiri=0;
                    Integer sumaSeiton =0;
                    Integer sumaSeiso=0;
                    Integer sumaSeiketsu=0;
                    Integer sumaShitsuke=0;

                    for (SubItem sub:unAuditoria.getSubItems()
                         ) {
                        if (sub.getId().equals("1S 1")||sub.getId().equals("1S 2")||sub.getId().equals("1S 3")||sub.getId().equals("1S 4")){
                            if (sub.getPuntuacion1()!=null) {
                                sumaSeiri = sumaSeiri + sub.getPuntuacion1();
                            }
                        }
                        if (sub.getId().equals("2S 1")||sub.getId().equals("2S 2")||sub.getId().equals("2S 3")||sub.getId().equals("2S 4")) {
                            if (sub.getPuntuacion1()!=null) {
                                sumaSeiton = sumaSeiton + sub.getPuntuacion1();
                            }
                        }
                        if (sub.getId().equals("3S 1")||sub.getId().equals("3S 2")||sub.getId().equals("3S 3")||sub.getId().equals("3S 4")) {
                            if (sub.getPuntuacion1()!=null) {
                                sumaSeiso = sumaSeiso + sub.getPuntuacion1();
                            }
                        }
                        if (sub.getId().equals("4S 1")||sub.getId().equals("4S 2")||sub.getId().equals("4S 3")||sub.getId().equals("4S 4")) {
                            if (sub.getPuntuacion1()!=null) {
                                sumaSeiketsu = sumaSeiketsu + sub.getPuntuacion1();
                            }
                        }
                        if (sub.getId().equals("5S 1")||sub.getId().equals("5S 2")||sub.getId().equals("5S 3")||sub.getId().equals("5S 4")) {
                            if (sub.getPuntuacion1()!=null) {
                                sumaShitsuke = sumaShitsuke + sub.getPuntuacion1();
                            }
                        }
                    }

                    Double promedioSeiri=((sumaSeiri/4.0)/5.0);
                    Double promedioSeiton=((sumaSeiton /4.0)/5.0);
                    Double promedioSeiso=((sumaSeiso/4.0)/5.0);
                    Double promedioSeiketsu=((sumaSeiketsu/4.0)/5.0);
                    Double promedioShitsuke=((sumaShitsuke/4.0)/5.0);
                    Double promedio5s =((promedioSeiso+promedioSeiri+promedioSeiton+promedioSeiketsu+promedioShitsuke)/5);
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
                    String percentage1 = format.format(promedioSeiri);
                    String percentage2 = format.format(promedioSeiton);
                    String percentage3 = format.format(promedioSeiso);
                    String percentage4 = format.format(promedioSeiketsu);
                    String percentage5 = format.format(promedioShitsuke);
                    String percentage6 = format.format(promedio5s);

                    text1s.setText(percentage1);
                    text2s.setText(percentage2);
                    text3s.setText(percentage3);
                    text4s.setText(percentage4);
                    text5s.setText(percentage5);
                    textFinal.setText(percentage6);
                    textFecha.setText(unAuditoria.getFechaAuditoria());
                    textFoto.setText(unAuditoria.getAreaAuditada().getNombreArea());

                    File f =new File(unAuditoria.getAreaAuditada().getFotoArea().getRutaFoto());
                    Picasso.with(imageView.getContext())
                            .load(f)
                            .into(imageView);
                }


        }
    }



}


    // Decodes image and scales it to reduce memory consumption