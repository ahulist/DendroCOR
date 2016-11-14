/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hulist.logic.daily.Y_M_D_V;

import com.hulist.logic.daily.IDailyLineContainer;
import org.joda.time.LocalDate;

/**
 *
 * @author Aleksander
 */
public class Y_M_D_VLineContainer implements IDailyLineContainer{
    /**
     * daily date!
     */
    private LocalDate date;
    private double value;

    public Y_M_D_VLineContainer(LocalDate date, double value) {
        this.date = date;
        this.value = value;
    }

    public Y_M_D_VLineContainer() {
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
