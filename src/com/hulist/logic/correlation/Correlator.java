/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hulist.logic.correlation;

import com.hulist.logic.MetaCorrelation;
import com.hulist.logic.RunParams;
import com.hulist.logic.SignificanceLevel;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import jdk.nashorn.internal.runtime.arrays.ArrayLikeIterator;

/**
 *
 * @author Aleksander
 */
public class Correlator {

    private ICorrelation c;
    private RunParams p;

    SignificanceLevel sl = new SignificanceLevel();
    private final Random random = new Random();

    private boolean isBootstrapped = false;
    private boolean isSignificanceLevels = false;
    private double alpha;
    private int bootstrapSamples;
    private MetaCorrelation correlation = null;

    public Correlator(ICorrelation c, RunParams p) {
        this.c = c;
        this.p = p;
    }

    public MetaCorrelation correlate(double[] a, double[] b) {
        if (isBootstrapped) {
            double newCorr = 0, corr = 0, tTestVal = 0;
            for (int i = 0; i < bootstrapSamples; i++) {
                newCorr = c.correlation(getBootstrappedArrays(a, b));
                if (isSignificanceLevels) {
                    tTestVal += sl.getTTestSignifLevel(newCorr, a.length);
                }
                corr += newCorr;
            }
            correlation = new MetaCorrelation(corr / bootstrapSamples);
            correlation.settTestValue(tTestVal / bootstrapSamples);
        } else {
            correlation = new MetaCorrelation(c.correlation(a, b));
            if (isSignificanceLevels) {
                correlation.settTestValue(sl.getTTestSignifLevel(correlation.getCorrelation(), a.length));
            }
        }
        correlation.settTestCritVal(sl.getTTestCritLevel(alpha, a.length));

        return correlation;
    }

    public MetaCorrelation correlate(double[][] ab) {
        return correlate(ab[0], ab[1]);
    }

    private double[][] getBootstrappedArrays(double[] a, double[] b) {
        double[][] r = new double[2][a.length];
        int rand;
        for (int i = 0; i < a.length; i++) {
            rand = this.random.nextInt(a.length);
            r[0][i] = a[rand];
            r[1][i] = b[rand];
        }
        return r;
    }

    public void setBootstrapValues(double alpha, int bootstrapSamples) {
        this.alpha = alpha;
        this.bootstrapSamples = bootstrapSamples;
    }

    public boolean isIsBootstrapped() {
        return isBootstrapped;
    }

    public void setIsBootstrapped(boolean isBootstrapped) {
        this.isBootstrapped = isBootstrapped;
    }

    public boolean isIsSignificanceLevels() {
        return isSignificanceLevels;
    }

    public void setIsSignificanceLevels(boolean isSignificanceLevels) {
        this.isSignificanceLevels = isSignificanceLevels;
    }

}
