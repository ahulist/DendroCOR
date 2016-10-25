/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hulist.logic.daily;

import com.hulist.util.Pair;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.collections4.map.MultiKeyMap;
import org.joda.time.LocalDate;
import org.joda.time.MonthDay;

/**
 *
 * @author Aleksander
 */
public class YearlyCombinations {

    private static boolean isInitialized = false;

    public final static int DAYS_IN_YEAR = 366;
    public final static int SAMPLE_LEAP_YEAR = 2016;
    private final static MultiKeyMap<Integer, /*Integer*/ MonthDay> days = new MultiKeyMap();
    private final static Set<Pair<MonthDay, MonthDay>> combinations = new HashSet<>(getCardinality(DAYS_IN_YEAR));

    /**
     * This initialization takes ~160 ms
     */
    public static void initialize() {
        initDays();
        initCombinations();
        isInitialized = true;
    }

    private static void initDays() {
        MonthDay current = new MonthDay(1, 1);
        for (int i = 0; i < DAYS_IN_YEAR; i++) {
            days.put(current.getMonthOfYear(), current.getDayOfMonth(), current);
            current = current.plusDays(1);
        }
    }

    public static int getCardinality(int x) {
        return (int) (0.5 * (x * x + x));
    }

    private static void initCombinations() {
        LocalDate start, end, currStart, currEnd;

        start = new LocalDate(SAMPLE_LEAP_YEAR, 1, 1);
        end = new LocalDate(SAMPLE_LEAP_YEAR, 12, 31);
        currStart = start;
        currEnd = currStart;

        while (currStart.isBefore(end.plusDays(1))) {
            while (currEnd.isBefore(end.plusDays(1))) {
                MonthDay beginning = days.get(currStart.getMonthOfYear(), currStart.getDayOfMonth());
                MonthDay ending = days.get(currEnd.getMonthOfYear(), currEnd.getDayOfMonth());
                Pair p = new Pair(beginning, ending);
                combinations.add(p);
                currEnd = currEnd.plusDays(1);
            }
            currStart = currStart.plusDays(1);
            currEnd = currStart;
        }
    }

    public static boolean isInitialized() {
        return isInitialized;
    }

    public static Set<Pair<MonthDay, MonthDay>> getCombinations() {
        while (!isInitialized) {
            // czeka aż wszystko będzie zainicjalizowane.
            // teoretycznie DC nigdy nie powinien tutaj zawisnąć na wieki.
            // teoretycznie.
        }
        return combinations;
    }

    public static MultiKeyMap<Integer, /*Integer*/ MonthDay> getDays() {
        while (!isInitialized) {
            // czeka aż wszystko będzie zainicjalizowane.
            // teoretycznie DC nigdy nie powinien tutaj zawisnąć na wieki.
            // teoretycznie.
        }
        return days;
    }

    public static MonthDay getMDObj(LocalDate ld){
        return days.get(ld.getMonthOfYear(), ld.getDayOfMonth());
    }
}