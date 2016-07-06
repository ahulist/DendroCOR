/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hulist.logic.daily;

import org.joda.time.LocalDate;

/**
 *
 * @author Aleksander
 */
public class DailyResult implements Comparable<DailyResult>{
    LocalDate start, end;
    int year;
    double value;

    public DailyResult(LocalDate start, LocalDate end, int year, double value) {
        this.start = start;
        this.end = end;
        this.year = year;
        this.value = value;
    }

    @Override
    public int compareTo(DailyResult o) {
        if (this.value < o.value) {
            return -1;
        }else if (this.value > o.value) {
            return 1;
        }else{
            return 0;
        }
    }
    
}
