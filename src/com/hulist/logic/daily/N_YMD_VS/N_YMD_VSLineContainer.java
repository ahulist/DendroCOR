/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hulist.logic.daily.N_YMD_VS;

import com.hulist.logic.daily.IDailyLineContainer;
import org.joda.time.LocalDate;

/**
 *
 * @author Aleksander
 */
public class N_YMD_VSLineContainer implements IDailyLineContainer{
    private String station;
    /**
     * daily date!
     */
    private LocalDate date;
    private double value;

    public N_YMD_VSLineContainer(String station, LocalDate date, double value) {
        this.station = station;
        this.date = date;
        this.value = value;
    }

    public N_YMD_VSLineContainer() {
    }

    public String getStation() {
        return station;
    }

    public void setStation(String station) {
        this.station = station;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public double getValue() {
        return value;
    }

}
