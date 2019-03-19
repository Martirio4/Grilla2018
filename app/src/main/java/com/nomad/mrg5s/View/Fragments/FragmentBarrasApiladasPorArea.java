package com.nomad.mrg5s.View.Fragments;


import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.nomad.mrg5s.Model.Area;
import com.nomad.mrg5s.Model.Auditoria;
import com.nomad.mrg5s.R;
import com.nomad.mrg5s.Utils.FuncionesPublicas;
import com.nomad.mrg5s.Utils.MyAxisValueFormatterEquis;
import com.nomad.mrg5s.Utils.MyAxisValueFormatterPorcentaje;
import com.nomad.mrg5s.Utils.MyBarDataSet;
import com.nomad.mrg5s.Utils.MyValueFormatter;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentBarrasApiladasPorArea extends Fragment {


    private CombinedChart mChart;
    private SeekBar mSeekBarX, mSeekBarY;
    private TextView tvX, tvY;
    private ArrayList<String> listaFechas;
    private ArrayList<BarEntry> yVals1;
    private BarDataSet set1;


    public static final String IDAREA ="IDAREA";
    public static final String ORIGEN ="ORIGEN";


    private String idArea;
    private String elOrigen;


    public FragmentBarrasApiladasPorArea()  {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        Bundle bundle = getArguments();
        idArea=bundle.getString(IDAREA);
        elOrigen=bundle.getString(ORIGEN);
        View view;
        if (elOrigen.equals(FuncionesPublicas.RANKING)) {
            view = inflater.inflate(R.layout.fragment_barras_apiladas_por_area, container, false);
        } else {
            view = inflater.inflate(R.layout.fragment_barras_apiladas_por_area_corto, container, false);
        }

        TextView tituloFragment= view.findViewById(R.id.tituloAreaHistorico);

        Realm realm = Realm.getDefaultInstance();
        Area unArea = realm.where(Area.class)
                .equalTo("idArea",idArea)
                .findFirst();
        //pregunto si es null por q el fragment tambien se abre en graficosactivity y no tiene este textview

            tituloFragment.setText(getContext().getResources().getString(R.string.evolucionDelArea)+" "+unArea.getNombreArea());


        mChart =  view.findViewById(R.id.chart1);
        mChart.getDescription().setEnabled(false);



        // if more than 60 entries are displayed in the chart, no values will be
        // drawn
        mChart.setMaxVisibleValueCount(10);

        // scaling can now only be done on x- and y-axis separately
        mChart.setPinchZoom(true);

        mChart.setDrawGridBackground(true);
        mChart.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.marfil));
        mChart.setGridBackgroundColor(ContextCompat.getColor(getContext(),R.color.marfil));
        mChart.setDrawBarShadow(false);


        mChart.setHighlightFullBarEnabled(false);
        mChart.setDrawOrder(new CombinedChart.DrawOrder[]{
                CombinedChart.DrawOrder.BAR,  CombinedChart.DrawOrder.LINE
        });


        Legend l = mChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(true);
        l.setFormSize(8f);
        l.setFormToTextSpace(4f);
        l.setXEntrySpace(6f);





        yVals1 = new ArrayList<BarEntry>();



        RealmResults<Auditoria> todasAudits = realm.where(Auditoria.class)
                .equalTo("areaAuditada.idArea",idArea)
                .sort("fechaAuditoria")
                .findAll();


        Integer contador=1;
        listaFechas=new ArrayList<>();
        for (Auditoria unAudit:todasAudits
                ) {
            double puntDouble=unAudit.getPuntajeFinal();
            float punFloat=(float)puntDouble;

            punFloat=(Math.round(punFloat*100));
            punFloat=punFloat/100;

            yVals1.add(new BarEntry(
                    contador,
                    punFloat));
            contador++;
            listaFechas.add(FuncionesPublicas.dameFechaString(unAudit.getFechaAuditoria(),"corta"));
        }

        // change the position of the y-labels
        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setValueFormatter(new MyAxisValueFormatterPorcentaje());
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)
        mChart.getAxisRight().setEnabled(false);
        leftAxis.setDrawLabels(true);

        IAxisValueFormatter xAxisFormatter = new MyAxisValueFormatterEquis(listaFechas);
        XAxis xAxis =mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setLabelRotationAngle(-90);
        xAxis.setGranularity(1f); // only intervals of 1 day
        xAxis.setValueFormatter(xAxisFormatter);
        xAxis.setLabelCount(listaFechas.size());

        if (mChart.getData() != null &&
                mChart.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet) mChart.getData().getDataSetByIndex(0);
            set1.setValues(yVals1);
            mChart.getData().notifyDataChanged();
            mChart.notifyDataSetChanged();
        }
        else {
            CombinedData laData=new CombinedData();

            //SE CREA PRIMRO LOS DATOS DE LAS BARRAS, SINO NO PUEDO GRAFICAR LAS LINEAS
            BarData laBarData=generateBarData();
            LineData laLineData=generateLineData();


            ArrayList<BarDataSet> dataSets = new ArrayList<>();

            laData.setData(laBarData);
            laData.setData(laLineData);

            mChart.getXAxis().setAxisMaximum(laData.getXMax() + 0.5f);
            mChart.getXAxis().setAxisMinimum(laData.getXMin() - 0.25f);

            laData.setValueFormatter(new MyValueFormatter());
            laData.setValueTextColor(Color.BLACK);
            laData.setValueTextSize(0f);


            mChart.setData(laData);
            mChart.invalidate();
        }

        Legend legend=mChart.getLegend();
        legend.setTextSize(8f);
        mChart.setDrawValueAboveBar(false);
        mChart.invalidate();

        // mChart.setDrawLegend(false);
        return view;
    }

    private LineData generateLineData() {

        LineData d = new LineData();

        ArrayList<Entry> entries = new ArrayList<Entry>();

        for (int index = 0; index < yVals1.size(); index++)
            entries.add(new Entry(index+1f, 0.8f));

        LineDataSet set = new LineDataSet(entries, getContext().getResources().getString(R.string.target));
        set.setColor(Color.rgb(0, 110, 170));
        set.setLineWidth(2.5f);
        set.setCircleColor(Color.rgb(0, 110, 200));
        set.setCircleRadius(5f);
        set.setFillColor(Color.rgb(240, 238, 70));
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set.setDrawValues(true);
        set.setValueTextSize(10f);
        set.setValueTextColor(Color.rgb(240, 238, 70));

        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        d.addDataSet(set);

        return d;
    }

    private BarData generateBarData() {

        set1 = new MyBarDataSet(yVals1,getContext().getResources().getString(R.string.auditResult));
        set1.setValueTextColor(Color.rgb(60, 220, 78));
        set1.setValueTextSize(10f);
        set1.setAxisDependency(YAxis.AxisDependency.LEFT);

        set1.setColors(ContextCompat.getColor(getContext(), R.color.semaRojo),
                ContextCompat.getColor(getContext(), R.color.semaAmarillo),
                ContextCompat.getColor(getContext(), R.color.semaVerde));


        float groupSpace = 0.06f;

        float barSpace = 0.02f; // x2 dataset
        float barWidth = 0.45f; // x2 dataset
        // (0.45 + 0.02) * 2 + 0.06 = 1.00 -> interval per "group"


        BarData d = new BarData(set1);
        d.setBarWidth(barWidth);
        // make this BarData object grouped
        //d.groupBars(0, groupSpace, barSpace); // start at x = 0

        return d;
    }




}

