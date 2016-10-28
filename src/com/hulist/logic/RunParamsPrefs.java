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
public class RunParamsPrefs {
    private boolean isStatisticalSignificance = false;
    private boolean isTwoTailedTest = false;
    private double significanceLevelAlpha = 0.05;
    private boolean isRunningCorrelation = false;
    private int runningCorrWindowSize = 49;
    private boolean isBootstrapSampling = false;
    private int bootstrapSamples = 500;

    public RunParamsPrefs() {}

    public RunParamsPrefs(boolean isStatisticalSignificance, boolean isTwoTailedTest,
            double significanceLevelAlpha, boolean isRunningCorrelation,
            int runningCorrWindowSize, boolean isBootstrapSampling,int bootstrapSamples) {
        this.isStatisticalSignificance = isStatisticalSignificance;
        this.isTwoTailedTest = isTwoTailedTest;
        this.significanceLevelAlpha = significanceLevelAlpha;
        this.isRunningCorrelation = isRunningCorrelation;
        this.runningCorrWindowSize = runningCorrWindowSize;
        this.isBootstrapSampling = isBootstrapSampling;
        this.bootstrapSamples = bootstrapSamples;
    }

    public boolean isStatisticalSignificance() {
        return isStatisticalSignificance;
    }

    public void setStatisticalSignificance(boolean isStatisticalSignificance) {
        this.isStatisticalSignificance = isStatisticalSignificance;
    }

    public boolean isTwoTailedTest() {
        return isTwoTailedTest;
    }

    public void setTwoTailedTest(boolean isTwoTailedTest) {
        this.isTwoTailedTest = isTwoTailedTest;
    }

    public double getSignificanceLevelAlpha() {
        return significanceLevelAlpha;
    }

    public void setSignificanceLevelAlpha(double significanceLevelAlpha) {
        this.significanceLevelAlpha = significanceLevelAlpha;
    }

    public boolean isRunningCorrelation() {
        return isRunningCorrelation;
    }

    public void setRunningCorrelation(boolean isRunningCorrelation) {
        this.isRunningCorrelation = isRunningCorrelation;
    }

    public int getRunningCorrWindowSize() {
        return runningCorrWindowSize;
    }

    public void setRunningCorrWindowSize(int runningCorrWindowSize) {
        this.runningCorrWindowSize = runningCorrWindowSize;
    }

    public boolean isBootstrapSampling() {
        return isBootstrapSampling;
    }

    public void setBootstrapSampling(boolean isBootstrapSampling) {
        this.isBootstrapSampling = isBootstrapSampling;
    }

    public int getBootstrapSamples() {
        return bootstrapSamples;
    }

    public void setBootstrapSamples(int bootstrapSamples) {
        this.bootstrapSamples = bootstrapSamples;
    }
    
}
