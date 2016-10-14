/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hulist.logic;

/**
 *
 * @author Aleksander
 */
public class MetaCorrelation implements Comparable<MetaCorrelation>{

    private double correlation;
    private double tTestValue;
    private double tTestCritVal;

    public MetaCorrelation(double correlation) {
        this.correlation = correlation;
    }

    public double gettTestValue() {
        return tTestValue;
    }

    public void settTestValue(double tTestValue) {
        this.tTestValue = tTestValue;
    }

    public double getCorrelation() {
        return correlation;
    }

    public double gettTestCritVal() {
        return tTestCritVal;
    }

    public void settTestCritVal(double tTestCritVal) {
        this.tTestCritVal = tTestCritVal;
    }

    @Override
    public int compareTo(MetaCorrelation o) {
        return Double.compare(this.correlation, o.correlation);
    }

}
