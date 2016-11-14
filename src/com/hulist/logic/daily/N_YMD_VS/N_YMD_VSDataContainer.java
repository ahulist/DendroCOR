/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hulist.logic.daily.N_YMD_VS;

import com.hulist.logic.daily.DailyFileDataContainer;
import com.hulist.util.Progress;
import java.io.File;

/**
 *
 * @author Aleksander
 */
public class N_YMD_VSDataContainer extends DailyFileDataContainer<N_YMD_VSLineContainer> {

    private String station;

    public N_YMD_VSDataContainer(File sourceFile) {
        super(sourceFile);
    }

    public N_YMD_VSDataContainer(File sourceFile, String station) {
        this(sourceFile);
        this.station = station;
    }

    @Override
    public boolean isEmpty() {
        return container.isEmpty();
    }

    public String getStation() {
        return station;
    }

    public void setStation(String station) {
        this.station = station;
    }

    @Override
    public Progress getProgress() {
        return this.progress;
    }

    @Override
    public void setProgress(Progress p) {
        this.progress = p;
    }
    
   @Override
    public String toString() {
        return super.toString() + " " + station;
    }
}
