package com.auditoria.grilla5s.Utils;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class MyAxisValueFormatterEquis implements IAxisValueFormatter
{

    private DecimalFormat mFormat;
    private ArrayList<String> listaStrings;

    public MyAxisValueFormatterEquis(ArrayList<String> listaFechas) {
        this.listaStrings=listaFechas;
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        Integer unInt= (int)value;

        return listaStrings.get(unInt-1);
    }
}
