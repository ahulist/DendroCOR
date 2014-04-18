/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hulist.logic.chronology.deka;

import com.hulist.logic.DetailedFileDataContainer;
import java.io.File;
import java.util.HashMap;

/**
 *
 * @author Aleksander Hulist <aleksander.hulist@gmail.com>
 */
public class DekaSerie extends DetailedFileDataContainer {

    private String chronoCode;
    private HashMap<Integer, Double> data;

    public DekaSerie(File sourceFile, String chronoCode) {
        super(sourceFile);
        this.data = new HashMap<>();
        this.chronoCode = chronoCode;
    }
    
    public DekaSerie(File sourceFile){
        this(sourceFile, "");
    }

    public void addYear(int year, double value) {
        data.put(year, value);
        updateMinMax(year);
    }

    public double getYear(int year) {
        return data.get(year);
    }

    public String getChronoCode() {
        return chronoCode;
    }

    public void setChronoCode(String chronoCode) {
        this.chronoCode = chronoCode;
    }

    public HashMap<Integer, Double> getData() {
        return data;
    }
    
    public double[] getArrayData(int yearStart, int yearEnd){
        double[] arr = new double[yearEnd-yearStart+1];
        for( int i = yearStart; i <= yearEnd; i++ ) {
            arr[i-yearStart] = data.get(i);
        }
        return arr;
    }

    public void setData(HashMap<Integer, Double> data) {
        this.data = data;
        for( Integer year : data.keySet() ) {
            updateMinMax(year);
        }
    }

    @Override
    public boolean isEmpty() {
        return data.isEmpty();
    }
    
}
