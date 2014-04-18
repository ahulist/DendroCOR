/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hulist.logic.climate.icru;

import java.util.HashMap;
import com.hulist.util.Months;

/**
 *
 * @author Aleksander Hulist <aleksander.hulist@gmail.com>
 */
public class IcruLineContainer {

    private final int year;
    private HashMap<Months, Double> monthTemps = new HashMap<>();

    public IcruLineContainer(int year) {
        this.year = year;
    }

    public IcruLineContainer(int year, HashMap<Months, Double> monthTemps) {
        this(year);
        this.monthTemps = monthTemps;
    }

    public double getTemp(Months month) {
        return monthTemps.get(month);
    }

    public void addMonthlyData(Months month, Double temperature) {
        this.monthTemps.put(month, temperature);
    }

    public int getYear() {
        return year;
    }
    
}
