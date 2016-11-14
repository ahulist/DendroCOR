/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hulist.logic.daily;

import com.hulist.logic.FileDataContainer;
import com.hulist.logic.IProgressable;
import com.hulist.logic.RunParams;
import com.hulist.util.Debug;
import com.hulist.util.Pair;
import com.hulist.util.Progress;
import java.io.File;
import java.util.HashMap;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.MonthDay;

/**
 *
 * @author Aleksander
 * @param <T>
 */
public abstract class DailyFileDataContainer<T extends IDailyLineContainer> extends FileDataContainer implements IProgressable{

    protected Progress progress = null;
    
    protected HashMap<LocalDate, T> container = new HashMap<>();
    // key: pair(year, pair(monthday, monthday))
    protected HashMap<Pair<Integer, Pair<MonthDay, MonthDay>>, Double> v = new HashMap<>(YearlyCombinations.getCardinality(YearlyCombinations.DAYS_IN_YEAR));
    protected boolean isValuesReady = false;
    
    public DailyFileDataContainer(File sourceFile) {
        super(sourceFile);
    }
    
    public void add(T data) {
        updateMinMax(data.getDate().getYear());
        this.container.put(data.getDate(), data);
    }
    
    public void populateYearlyCombinations() {
        populateYearlyCombinations(null);
    }
    
    /**
     * Moc zbioru kombinacji to suma od 1 do N (inclusive), gdzie N to ilość
     * elementów
     */
    public void populateYearlyCombinations(RunParams rp) {
        int min = getYearMin(), max = getYearMax();
        if (rp!=null && !rp.isAllYears()) {
            min = Math.max(min, rp.getStartYear());
            max = Math.min(max, rp.getEndYear());
        }
        for (int currYear = min; currYear <= max; currYear++) {
            LocalDate start = new LocalDate(currYear, 1, 1);
            LocalDate end = new LocalDate(currYear, 12, 31);
            LocalDate currStart, currEnd;

            double done = 0;

            Pair<Integer, Pair<MonthDay, MonthDay>> pair;

            currStart = start;
            currEnd = currStart;
            while (currStart.isBefore(end.plusDays(1))) {
                boolean isValueMissing = false;

                Double prevVal;
                while (currEnd.isBefore(end.plusDays(1))) {
                    pair = new Pair(currStart.getYear(), new Pair(YearlyCombinations.getMDObj(currStart), YearlyCombinations.getMDObj(currEnd)));
                    if (!isValueMissing) {
                        prevVal = v.get(new Pair(currStart.getYear(), new Pair(YearlyCombinations.getMDObj(currStart), YearlyCombinations.getMDObj(currEnd.minusDays(1)))));
                        if (prevVal == null) {
                            prevVal = new Double(0);
                        }
                        T tdc = container.get(currEnd);

                        if (tdc == null) {    // missing value!
                            v.put(pair, FileDataContainer.MISSING_VALUE);
                            isValueMissing = true;
                        }
                        if (!isValueMissing) {
                            Double thisVal = tdc.getValue();
                            double daysBetween = Days.daysBetween(currStart, currEnd).getDays() + 1;
                            double newVal = prevVal + (thisVal - prevVal) / daysBetween;
//                            double newVal = ((daysBetween - 1) * prevVal + thisVal) / daysBetween;
                            v.put(pair, newVal);
                        }
                    } else {
                        v.put(pair, FileDataContainer.MISSING_VALUE);
                    }
                    currEnd = currEnd.plusDays(1);
                }
                currStart = currStart.plusDays(1);
                currEnd = currStart;
            }
            done = (currYear - getYearMin()) / (1.0 * getYearMax() - getYearMin()) * 100.0;
            if (Debug.IS_DUBUGGGING) {
                System.out.println("Averaging: " + done + "%");
            }
            if (progress != null) {
                progress.setCurrentJobProgress(done / 100);
            }
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

    @Override
    public String toString() {
        return super.toString();
    }
}
