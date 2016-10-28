/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hulist.logic.chronology.tabs_multicol;

import java.util.HashMap;
import com.hulist.logic.FileDataContainer;
import java.io.File;

/**
 * This class represents one (!) correlation column in MultiCol Tabs file,
 * i.e. primary years column & column to correlate.
 * @author Aleksander Hulist <aleksander.hulist@gmail.com>
 */
public class TabsMulticolDataContainer extends FileDataContainer {

    /**
     * key (Integer) is a year, value (Double) is a value in given column
     */
    private final HashMap<Integer, Double> data = new HashMap<>();
    private final int columnNumber;
    
    //private int colCount = -1;

    public TabsMulticolDataContainer(File sourceFile, int columnNumber) {
        super(sourceFile);
        this.columnNumber = columnNumber;
    }

    /*public TabsMulticolDataContainer(File sourceFile, int columnsCount) {
        super(sourceFile);
        this.colCount = columnsCount;
    }

    public TabsMulticolDataContainer(int colCount, File sourceFile) {
        super(sourceFile);
        this.colCount = colCount;
    }*/

    public Double getValue(int year) {
        return data.get(year);
    }

    /*public void addValue(int year, double val) {
        if (!data.containsKey(year)) {
            data.put(year, new ArrayList<>());
            updateMinMax(year);
        }
        data.get(year).add(val);
    }*/

    /**
     * provided <i>values</i> replace existing data for given year
     * @param year
     * @param values 
     * @throws java.io.IOException 
     */
    /*public void addLine(int year, ArrayList<Double> values) throws IOException {
        if (colCount==-1) {
            colCount = values.size();
        }else{
            if (colCount != values.size()) {
                String msg = String.format(Misc.getInternationalized("TabsMulticolDataContainer błędna liczba kolumn"), getSourceFile().getName(), values.size(), colCount);
                throw new IOException(msg);
            }
        }
        
        data.put(year, values);
        updateMinMax(year);
    }*/
    
    /**
     * 
     * @param year
     * @param value
     * @return False if some old value was replaced. True otherwise.
     */
    public boolean addValue(int year, double value){
        Object v = data.put(year, value);
        updateMinMax(year);
        return v==null;
    }

    /**
     * year is maps' key
     * @return 
     */
    public HashMap<Integer, Double> getData() {
        return this.data;
    }

    /**
     * 
     * @param col   Column number 0-based
     * @return 
     */
    public double[] getColumnArray(int col) {
        return getArray(getYearMin(), getYearMax());
    }

    public double[] getArray(int yearStart, int yearEnd) {
        double[] arr = new double[yearEnd - yearStart + 1];
        for( int i = yearStart; i <= yearEnd; i++ ) {
            arr[i - yearStart] = data.get(i);
        }
        return arr;
    }

    @Override
    public boolean isEmpty() {
        return data.isEmpty();
    }

    public int getColumnNumber() {
        return columnNumber;
    }

    /**
     * number of columns is determined upon first data insertion or via constructor
     * @return number of columns or -1 if empty
     */
    /*public int getColCount() {
        return colCount;
    }*/

    /*public void setColCount(int colCount) {
        this.colCount = colCount;
    }*/
    
}
