/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hulist.logic.correlation;

import com.hulist.logic.RunParams;

/**
 *
 * @author Aleksander
 */
public class Correlator {
    
    private ICorrelation c;
    private RunParams p;

    public Correlator(ICorrelation c, RunParams p) {
        this.c = c;
        this.p = p;
    }

    public double correlate(double[] a, double[] b){
        return c.correlation(a, b);
    }
}
