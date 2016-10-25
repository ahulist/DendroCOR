/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hulist.logic.daily.type1;

import org.joda.time.LocalDate;

/**
 *
 * @author Aleksander
 */
public class Type1LineContainer {
    private String station;
    /**
     * daily date!
     */
    private LocalDate date;
    private double value;

    public Type1LineContainer(String station, LocalDate date, double value) {
        this.station = station;
        this.date = date;
        this.value = value;
    }

    public Type1LineContainer() {
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
