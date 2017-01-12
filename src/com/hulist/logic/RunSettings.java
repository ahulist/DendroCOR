/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hulist.logic;

import com.hulist.gui2.PreferencesFXMLController;
import com.hulist.gui2.PreferencesFXMLController.PlotColorType;

/**
 *
 * @author Aleksander
 */
public class RunSettings {
    private boolean isStatisticalSignificance = false;
    private boolean isTwoTailedTest = false;
    private double significanceLevelAlpha = 0.05;
    private boolean isRunningCorrelation = false;
    private int runningCorrWindowSize = 49;
    private boolean isBootstrapSampling = false;
    private int bootstrapSamples = 500;
    private int howManyRowsToSave = 100;
    private boolean isSaveAllRows = false;
    private boolean isPlotColored = true;
    private PlotColorType plotColorType = PreferencesFXMLController.PlotColorType.ALL;

    public RunSettings() {}

    public RunSettings(boolean isStatisticalSignificance, boolean isTwoTailedTest,
            double significanceLevelAlpha, boolean isRunningCorrelation,
            int runningCorrWindowSize, boolean isBootstrapSampling, int bootstrapSamples, 
            int howManyRowsToSave, boolean isSaveAllRows, boolean isPlotColored, 
            PlotColorType plotColorType) {
        this.isStatisticalSignificance = isStatisticalSignificance;
        this.isTwoTailedTest = isTwoTailedTest;
        this.significanceLevelAlpha = significanceLevelAlpha;
        this.isRunningCorrelation = isRunningCorrelation;
        this.runningCorrWindowSize = runningCorrWindowSize;
        this.isBootstrapSampling = isBootstrapSampling;
        this.bootstrapSamples = bootstrapSamples;
        this.howManyRowsToSave = howManyRowsToSave;
        this.isSaveAllRows = isSaveAllRows;
        this.isPlotColored = isPlotColored;
        this.plotColorType = plotColorType;
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

    public boolean isPlotColored() {
        return isPlotColored;
    }

    public PlotColorType getPlotColorType() {
        return plotColorType;
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

    public int getHowManyRowsToSave() {
        return howManyRowsToSave;
    }

    public void setHowManyRowsToSave(int howManyRowsToSave) {
        this.howManyRowsToSave = howManyRowsToSave;
    }

    public boolean isSaveAllRows() {
        return isSaveAllRows;
    }

    public void setSaveAllRows(boolean isSaveAllRows) {
        this.isSaveAllRows = isSaveAllRows;
    }
    
}
