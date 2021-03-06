package com.nomad.mrg5s.View.Fragments;


import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nomad.mrg5s.R;
import com.nomad.mrg5s.Utils.FuncionesPublicas;
import com.nomad.mrg5s.Utils.RadarMarkerView;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.RadarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.data.RadarDataSet;
import com.github.mikephil.charting.data.RadarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IRadarDataSet;


import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentRadar extends Fragment {

    private RadarChart mChart;
    public static final String  PUNJTAJE1="PUNTAJE1";
    public static final String  PUNJTAJE2="PUNTAJE2";
    public static final String  PUNJTAJE3="PUNTAJE3";
    public static final String  PUNJTAJE4="PUNTAJE4";
    public static final String  PUNJTAJE5="PUNTAJE5";
    public static final String  COMPLETO="COMPLETO";
    public static final String  ESTRUCTURA="ESTRUCTURA";



    public static final String  AREA="AREA";

    private Double punt1;
    private Double punt2;
    private Double punt3;
    private Double punt4;
    private Double punt5;
    private String estructura;
    private Boolean completo;


    public FragmentRadar() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_radar_chart, container, false);

        Bundle unBundle=getArguments();

        String areaAuditada = null;
        if (unBundle!=null) {
            punt1=unBundle.getDouble(PUNJTAJE1);
            punt2=unBundle.getDouble(PUNJTAJE2);
            punt3=unBundle.getDouble(PUNJTAJE3);
            punt4=unBundle.getDouble(PUNJTAJE4);
            punt5=unBundle.getDouble(PUNJTAJE5);
            areaAuditada = unBundle.getString(AREA);
            completo=unBundle.getBoolean(COMPLETO);
            estructura=unBundle.getString(ESTRUCTURA);
        }

        if (punt1==9.9){
            punt1=0.0;
        }
        if (punt2==9.9){
            punt2=0.0;
        }
        if (punt3==9.9){
            punt3=0.0;
        }
        if (punt4==9.9){
            punt4=0.0;
        }
        if (punt5==9.9){
            punt5=0.0;
        }


        Typeface robotoR = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Roboto-Regular.ttf");
        TextView textoTitulo= view.findViewById(R.id.textoAreaResultado);
        TextView textViewIncompleto = view.findViewById(R.id.textoIncompleto);

        if (completo){
            textViewIncompleto.setVisibility(View.GONE);
        }
        else{
            textViewIncompleto.setVisibility(View.VISIBLE);
        }

        textoTitulo.setTypeface(robotoR);
        textViewIncompleto.setTypeface(robotoR);

        textoTitulo.setText(areaAuditada);
        /*TextView tv = (TextView) findViewById(R.id.textView);
        tv.setTextColor(Color.WHITE);
        tv.setBackgroundColor(Color.rgb(60, 65, 82));
*/
        mChart = view.findViewById(R.id.chart1);
        mChart.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.marfil));

        mChart.getDescription().setEnabled(false);

        mChart.setWebLineWidth(1f);
        mChart.setWebColor(Color.LTGRAY);
        mChart.setWebLineWidthInner(1f);
        mChart.setWebColorInner(Color.LTGRAY);
        mChart.setWebAlpha(100);

        // create a custom MarkerView (extend MarkerView) and specify the layout
        // to use for it
        MarkerView mv = new RadarMarkerView(getContext(), R.layout.radar_markerview);
        mv.setChartView(mChart); // For bounds control
        mChart.setMarker(mv); // Set the marker to the chart


        setData();

        mChart.animateXY(
                1400, 1400,
                Easing.EasingOption.EaseInOutQuad,
                Easing.EasingOption.EaseInOutQuad);

        XAxis xAxis = mChart.getXAxis();
        xAxis.setTextSize(9f);
        xAxis.setYOffset(0f);
        xAxis.setXOffset(0f);
        xAxis.setValueFormatter(new IAxisValueFormatter() {

            private String[] mActivities = new String[]{"1S", "2S", "3S", "4S","5S"};

            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return mActivities[(int) value % mActivities.length];
            }
        });
        xAxis.setTextColor(Color.BLACK);
        xAxis.setTextSize(20f);

        YAxis yAxis = mChart.getYAxis();
        yAxis.setLabelCount(5, false);
        yAxis.setTextSize(9f);
        yAxis.setAxisMinimum(0f);
        yAxis.setAxisMaximum(80f);
        yAxis.setDrawLabels(false);

        Legend l = mChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setXEntrySpace(7f);
        l.setYEntrySpace(5f);
        l.setTextColor(Color.BLACK);
        l.setTextSize(15f);




        return view;

    }


    public void setData() {
        
        ArrayList<RadarEntry> entries1 = new ArrayList<>();
        ArrayList<RadarEntry> entries2 = new ArrayList<>();

        Float punto1= Float.parseFloat(punt1.toString());
        Float punto2= Float.parseFloat(punt2.toString());
        Float punto3= Float.parseFloat(punt3.toString());
        Float punto4= Float.parseFloat(punt4.toString());
        Float punto5= Float.parseFloat(punt5.toString());


        if (estructura.equals(FuncionesPublicas.ESTRUCTURA_ESTRUCTURADA)) {
            entries1.add(new RadarEntry(punto1*20));
            entries1.add(new RadarEntry(punto2*20));
            entries1.add(new RadarEntry(punto3*20));
            entries1.add(new RadarEntry(punto4*20));
            entries1.add(new RadarEntry(punto5*20));
        }
        if (estructura.equals(FuncionesPublicas.ESTRUCTURA_SIMPLE)){
            entries1.add(new RadarEntry(punto1*20*4));
            entries1.add(new RadarEntry(punto2*20*4));
            entries1.add(new RadarEntry(punto3*20*4));
            entries1.add(new RadarEntry(punto4*20*4));
            entries1.add(new RadarEntry(punto5*20*4));
        }


//        TARGET
        entries2.add(new RadarEntry(4f*20));
        entries2.add(new RadarEntry(4f*20));
        entries2.add(new RadarEntry(4f*20));
        entries2.add(new RadarEntry(4f*20));
        entries2.add(new RadarEntry(4f*20));


        // NOTE: The order of the entries when being added to the entries array determines their position around the center of
        // the chart.
       /* for (int i = 0; i < cnt; i++) {
            float val1 = (float) (Math.random() * mult) + min;
            entries1.add(new RadarEntry(val1));

            float val2 = (float) (Math.random() * mult) + min;
            entries2.add(new RadarEntry(val2));
        }
        */

        RadarDataSet set1 = new RadarDataSet(entries1, getResources().getString(R.string.audit));
        set1.setColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
        set1.setFillColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
        set1.setDrawFilled(true);
        set1.setFillAlpha(100);
        set1.setLineWidth(2f);
        set1.setDrawHighlightCircleEnabled(true);
        set1.setDrawHighlightIndicators(false);


        RadarDataSet set2 = new RadarDataSet(entries2, getResources().getString(R.string.target));
        set2.setColor(ContextCompat.getColor(getContext(), R.color.tile3));
        set2.setFillColor(ContextCompat.getColor(getContext(), R.color.tile3));
        set2.setDrawFilled(true);
        set2.setFillAlpha(50);
        set2.setLineWidth(2f);
        set2.setDrawHighlightCircleEnabled(true);
        set2.setDrawHighlightIndicators(false);

        ArrayList<IRadarDataSet> sets = new ArrayList<>();
        sets.add(set1);
        sets.add(set2);

        RadarData data = new RadarData(sets);
        data.setValueTextSize(8f);
        data.setDrawValues(false);
        data.setValueTextColor(Color.BLACK);

        mChart.setData(data);
        mChart.invalidate();
    }


}
