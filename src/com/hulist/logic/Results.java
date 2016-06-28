/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hulist.logic;

import com.hulist.util.MonthsPair;
import java.util.HashMap;
import java.util.TreeMap;

/**
 *
 * @author Aleksander Hulist <aleksander.hulist@gmail.com>
 */
public class Results {

    int yearStart, yearEnd;
    String chronoTitle = null, climateTitle = null;
    RunParams params;
    HashMap<MonthsPair, MetaCorrelation> map = new HashMap<>();

    // significance testing
    private boolean isTTest = false;
    
    // running correlation
    /**
     * Integer in the inner map is starting year in correlation window
     */
    HashMap<MonthsPair, TreeMap<Integer, Double>> runningCorrMap = new HashMap<>();
    private boolean isRunningCorr = false;
    private int windowSize;

    public Results(RunParams params) {
        this.params = params;
    }

    public boolean isIsRunningCorr() {
        return isRunningCorr;
    }

    public void setIsRunningCorr(boolean isRunningCorr) {
        this.isRunningCorr = isRunningCorr;
    }

    public int getWindowSize() {
        return windowSize;
    }

    public void setWindowSize(int windowSize) {
        this.windowSize = windowSize;
    }

    public boolean isIsTTest() {
        return isTTest;
    }

    public void setIsTTest(boolean isTTest) {
        this.isTTest = isTTest;
    }

}
