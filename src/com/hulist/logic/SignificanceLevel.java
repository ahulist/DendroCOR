/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hulist.logic;

import java.util.HashMap;
import org.apache.commons.math3.distribution.TDistribution;
import org.apache.commons.math3.util.FastMath;

/**
 *
 * @author Aleksander
 */
public class SignificanceLevel {

    /**
     * <1-alpha,<tabLength, tTestValue>>
     */
    private final static HashMap<Double, HashMap<Integer, Double>> T_TEST_CRIT_LOOKUP_CACHE = new HashMap<>();
    private TDistribution tDist = null;
    private int tabLengthForTDist = 0;

    public double getTTestSignifLevel(double corrCoeff, int tabLength) {
        return corrCoeff / (FastMath.sqrt((1 - corrCoeff * corrCoeff) / (tabLength - 2)));
    }

    public double getTTestCritLevel(double alpha, int tabLength) {
        if (this.tDist==null || this.tabLengthForTDist!=tabLength) {
            
            if (T_TEST_CRIT_LOOKUP_CACHE.get(1-alpha)==null) {
                T_TEST_CRIT_LOOKUP_CACHE.put(1-alpha, new HashMap<>());
            }
        }
        
        if (T_TEST_CRIT_LOOKUP_CACHE.get(1 - alpha).get(tabLength) == null) {
            this.tDist = new TDistribution(/*2 * */tabLength - 2);
            this.tabLengthForTDist = tabLength;
            double tTestCritVal = this.tDist.inverseCumulativeProbability(1 - alpha);
            T_TEST_CRIT_LOOKUP_CACHE.get(1 - alpha).put(tabLength, tTestCritVal);
            return tTestCritVal;
        } else {
            return T_TEST_CRIT_LOOKUP_CACHE.get(1 - alpha).get(tabLength);
        }
    }

}
