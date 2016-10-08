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

    public boolean isIsStatisticalSignificance() {
        return isStatisticalSignificance;
    }

    public void setIsStatisticalSignificance(boolean isStatisticalSignificance) {
        this.isStatisticalSignificance = isStatisticalSignificance;
    }

    public boolean isIsTwoTailedTest() {
        return isTwoTailedTest;
    }

    public void setIsTwoTailedTest(boolean isTwoTailedTest) {
        this.isTwoTailedTest = isTwoTailedTest;
    }

    public double getSignificanceLevelAlpha() {
        return significanceLevelAlpha;
    }

    public void setSignificanceLevelAlpha(double significanceLevelAlpha) {
        this.significanceLevelAlpha = significanceLevelAlpha;
    }

    public boolean isIsRunningCorrelation() {
        return isRunningCorrelation;
    }

    public void setIsRunningCorrelation(boolean isRunningCorrelation) {
        this.isRunningCorrelation = isRunningCorrelation;
    }

    public int getRunningCorrWindowSize() {
        return runningCorrWindowSize;
    }

    public void setRunningCorrWindowSize(int runningCorrWindowSize) {
        this.runningCorrWindowSize = runningCorrWindowSize;
    }

    public boolean isIsBootstrapSampling() {
        return isBootstrapSampling;
    }

    public void setIsBootstrapSampling(boolean isBootstrapSampling) {
        this.isBootstrapSampling = isBootstrapSampling;
    }

    public int getBootstrapSamples() {
        return bootstrapSamples;
    }

    public void setBootstrapSamples(int bootstrapSamples) {
        this.bootstrapSamples = bootstrapSamples;
    }
    
}
