/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hulist.logic.chronology.tabs;

import java.util.HashMap;
import com.hulist.logic.FileDataContainer;
import java.io.File;

/**
 *
 * @author Aleksander Hulist <aleksander.hulist@gmail.com>
 */
public class TabsDataContainer extends FileDataContainer {

    /**
     * key (Integer) is a year
     */
    private final HashMap<Integer, TabsLineContainer> data = new HashMap<>();

    public TabsDataContainer(File sourceFile) {
        super(sourceFile);
    }

    public TabsLineContainer getLine(int year) {
        return data.get(year);
    }

    public void addLine(int year, int num, double seg, double age, double raw, double std, double res, double ars) {
        addLine(new TabsLineContainer(year, num, seg, age, raw, std, res, ars));
    }

    public void addLine(TabsLineContainer line) {
        data.put(line.getYear(), line);
        updateMinMax(line.getYear());
    }

    /**
     * hashmap key is the year
     *
     * @return
     */
    public HashMap<Integer, TabsLineContainer> getData() {
        return this.data;
    }

    /**
     * 
     * @param type
     * @return 
     */
    public double[] getArray(TabsColumnTypes type) {
        return getArray(type, getYearMin(), getYearMax());
    }

    public double[] getArray(TabsColumnTypes type, int yearStart, int yearEnd) {
        double[] arr = new double[yearEnd - yearStart + 1];
        for( int i = yearStart; i <= yearEnd; i++ ) {
            switch( type ) {
                /*case SEG:
                    arr[i - yearStart] = data.get(i).getSeg();
                    break;*/
                /*case AGE:
                    arr[i - yearStart] = data.get(i).getAge();
                    break;*/
                case RAW:
                    arr[i - yearStart] = data.get(i).getRaw();
                    break;
                case STD:
                    arr[i - yearStart] = data.get(i).getStd();
                    break;
                case RES:
                    arr[i - yearStart] = data.get(i).getRes();
                    break;
                case ARS:
                    arr[i - yearStart] = data.get(i).getArs();
                    break;
            }
        }
        return arr;
    }

    @Override
    public boolean isEmpty() {
        return data.isEmpty();
    }

}
