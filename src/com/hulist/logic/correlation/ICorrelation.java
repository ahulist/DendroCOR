/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hulist.logic.correlation;

/**
 * Interface for correlation solvers (i.e. Pearson)
 * @author Aleksander
 */
public interface ICorrelation {
    public double correlation(double[] a, double[] b);
}
