/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hulist.logic.climate.icru;

import com.hulist.logic.FileDataContainer;
import com.hulist.util.Months;
import com.hulist.util.MonthsPair;
import java.io.File;
import java.util.HashMap;

/**
 *
 * @author Aleksander Hulist <aleksander.hulist@gmail.com>
 */
public class IcruDataContainer extends FileDataContainer {

    /**
     * Integer              - year
     * IcruLineContainer    - line in Icru file (i.e. month=>value)
     */
    private final HashMap<Integer, IcruLineContainer> data = new HashMap<>();

    public IcruDataContainer(File sourceFile) {
        super(sourceFile);
    }

    public double getTemp(int year, Months month) {
        return data.get(year).getTemp(month);
    }

    public IcruLineContainer getYearlyTemps(int year) {
        return data.get(year);
    }

    public void addYearlyData(int year, IcruLineContainer ilc) {
        this.data.put(year, ilc);
        updateMinMax(ilc.getYear());
    }

    public HashMap<Integer, IcruLineContainer> getData() {
        return data;
    }

    public double[] getArray(MonthsPair col) {
        return getArray(col, getYearMin(), getYearMax());
    }

    public double[] getArray(MonthsPair col, int yearStart, int yearEnd) {
        int numOfYears = yearEnd - yearStart + 1;
        double[] arr = new double[numOfYears];
        for( int i = yearStart; i <= yearEnd; i++ ) {
            //System.out.println(col.toString()+"\t"+i);
            double sum = 0;
            double dividers = 0;
            Months currMonth = col.start;
            int currMonthOrdinal = currMonth.ordinal();
            while( currMonthOrdinal <= col.end.ordinal() ) {
                sum += this.data.get(i).getTemp(currMonth);
                currMonth = currMonth.getNext();
                currMonthOrdinal++;
                dividers++;
            }
            sum /= dividers;

            arr[i - yearStart/*getYearMin()*/] = sum;
        }
        return arr;
    }

    @Override
    public boolean isEmpty() {
        return data.isEmpty();
    }
    
}
