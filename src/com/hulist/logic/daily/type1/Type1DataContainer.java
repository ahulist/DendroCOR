/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hulist.logic.daily.type1;

import com.hulist.logic.FileDataContainer;
import com.hulist.logic.daily.YearlyCombinations;
import com.hulist.util.Pair;
import java.io.File;
import java.util.HashMap;
import org.apache.commons.collections4.map.MultiKeyMap;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.MonthDay;

/**
 *
 * @author Aleksander
 */
public class Type1DataContainer extends FileDataContainer {

    private String station;
    
    private HashMap<LocalDate, Type1LineContainer> container = new HashMap<>();
//    private MultiKeyMap<LocalDate, /*LocalDate, */ Double> values = new MultiKeyMap();   // wszystkie kombinacje dat w ciągu roku + wartość
    // key: pair(year, pair(monthday, monthday))
    private HashMap<Pair<Integer,Pair<MonthDay,MonthDay>>, Double> v = new HashMap<>(YearlyCombinations.getCardinality(YearlyCombinations.DAYS_IN_YEAR));
    private boolean isValuesReady = false;

    public Type1DataContainer(File sourceFile) {
        super(sourceFile);
    }

    public Type1DataContainer(File sourceFile, String station) {
        this(sourceFile);
        this.station = station;
    }

    @Override
    public boolean isEmpty() {
        return container.isEmpty();
    }

    public void add(Type1LineContainer data) {
        updateMinMax(data.getDate().getYear());
        this.container.put(data.getDate(), data);
    }

    public String getStation() {
        return station;
    }

    public void setStation(String station) {
        this.station = station;
    }

    /**
     * Moc zbioru kombinacji to suma od 1 do N (inclusive), gdzie N to ilość
     * elementów
     */
    public void populateYearlyCombinations() {
        for (int currYear = getYearMin(); currYear <= getYearMax(); currYear++) {
            LocalDate start = new LocalDate(currYear, 1, 1);
            LocalDate end = new LocalDate(currYear, 12, 31);
            LocalDate currStart, currEnd;

            Pair<Integer, Pair<MonthDay, MonthDay>> pair;
            
            currStart = start;
            currEnd = currStart;
            while (currStart.isBefore(end.plusDays(1))) {
                boolean isValueMissing = false;
                
                Double prevVal;
                while (currEnd.isBefore(end.plusDays(1))) {
                    pair = new Pair(currStart.getYear(), new Pair(YearlyCombinations.getMDObj(currStart),YearlyCombinations.getMDObj(currEnd)));
                    if (!isValueMissing) {
                        prevVal = v.get(new Pair(currStart.getYear(), new Pair(YearlyCombinations.getMDObj(currStart), YearlyCombinations.getMDObj(currEnd.minusDays(1)))));
                        if (prevVal == null) {
                            prevVal = new Double(0);
                        }
                        Type1LineContainer tdc = container.get(currEnd);
                        
                        if (tdc == null) {    // missing value!
                            v.put(pair, FileDataContainer.MISSING_VALUE);
                            isValueMissing = true;
                        }
                        if (!isValueMissing) {
                            Double thisVal = tdc.getValue();
                            double daysBetween = Days.daysBetween(currStart, currEnd).getDays() + 1;
                            double newVal = prevVal+(thisVal-prevVal)/daysBetween;
//                            double newVal = ((daysBetween - 1) * prevVal + thisVal) / daysBetween;
                            v.put(pair, newVal);
                        }
                    }else{
                        v.put(pair, FileDataContainer.MISSING_VALUE);
                    }

                    currEnd = currEnd.plusDays(1);
                }
                currStart = currStart.plusDays(1);
                currEnd = currStart;
            }
            System.out.println("Averaging: " + ((currYear - getYearMin()) / (1.0 * getYearMax() - getYearMin()) * 100.0) +"%");
        }

        isValuesReady = true;
    }

    /**
     * gets avaraged value for all dates in between <b>start</b> and <b>end</b>
     * (both inclusive)
     *
     * @param start
     * @param end
     * @return
     */
    public double getAvaragedValue(LocalDate start, LocalDate end) {
        if (!isValuesReady) {
            populateYearlyCombinations();
        }
        return v.get(new Pair(start.getYear(), new Pair(YearlyCombinations.getMDObj(start), YearlyCombinations.getMDObj(end))));
    }

    public double getAvaragedValue(MonthDay start, MonthDay end, int year) {
        return getAvaragedValue(start.toLocalDate(year), end.toLocalDate(year));
    }

    public double[] getAvaragedValuesForYears(MonthDay start, MonthDay end, int yearStart, int yearEnd) {
        double[] vals = new double[yearEnd - yearStart + 1];
        for (int i = yearStart; i <= yearEnd; i++) {
            vals[i - yearStart] = getAvaragedValue(start, end, i);
        }
        return vals;
    }

}