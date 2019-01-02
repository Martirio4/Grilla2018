package com.auditoria.grilla5s.Utils;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.text.DecimalFormat;

public class MyAxisValueFormatterPorcentaje implements IAxisValueFormatter
{

    private DecimalFormat mFormat;

    public MyAxisValueFormatterPorcentaje() {
        mFormat = new DecimalFormat("###,###,###,##0.0");
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {

        return mFormat.format(value*100) + " %";
    }
}