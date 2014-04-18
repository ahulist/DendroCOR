/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hulist.logic;

import com.hulist.util.MonthsPair;
import java.util.HashMap;

/**
 *
 * @author Aleksander Hulist <aleksander.hulist@gmail.com>
 */
public class Results {

    int yearStart, yearEnd;
    String chronoTitle = null, climateTitle = null;
    WindowParams params;
    HashMap<MonthsPair, Double> map = new HashMap<>();

    public Results(WindowParams params) {
        this.params = params;
    }

}
