/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hulist.logic.correlation;

import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;

/**
 *
 * @author Aleksander
 */
public class PearsonCorrelation implements ICorrelation{

    private final PearsonsCorrelation pc;

    public PearsonCorrelation() {
        this.pc = new PearsonsCorrelation();
    }
    
    @Override
    public double correlation(double[] a, double[] b) {
        return pc.correlation(a, b);
    }
    
}
