/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.hulist.logic.chronology.deka;

import java.util.ArrayList;

/**
 *
 * @author Aleksander Hulist <aleksander.hulist@gmail.com>
 */
public class DekaSeriesDataContainer {
    
    private ArrayList<DekaSerie> series;

    public DekaSeriesDataContainer() {
        this.series = new ArrayList<>();
    }

    public DekaSeriesDataContainer(ArrayList<DekaSerie> series) {
        this.series = series;
    }
    
    public void add(DekaSerie serie){
        series.add(serie);
    }
    
    public ArrayList<DekaSerie> getSeries() {
        return series;
    }

    public void setSeries(ArrayList<DekaSerie> series) {
        this.series = series;
    }
    
}
