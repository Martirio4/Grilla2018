package com.auditoria.audit5S_Full.Utils;

import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.util.List;

/**
 * Created by elmar on 21/3/2018.
 */
public class MyBarDataSet extends BarDataSet {


    public MyBarDataSet(List<BarEntry> yVals, String label) {
        super(yVals, label);
    }

    @Override
    public int getColor(int index) {
        if(getEntryForIndex(index).getY()<0.5) // l
            return mColors.get(0);
        else if(getEntryForIndex(index).getY()<0.8) //
            return mColors.get(1);
        else // greater or equal than 100 red
            return mColors.get(2);
    }

}

