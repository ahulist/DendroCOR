/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hulist.logic.daily.N_YMD_VS;

import java.util.ArrayList;

/**
 *
 * @author Aleksander
 */
public class N_YMD_VSSeriesDataContainer{
    
    private ArrayList<N_YMD_VSDataContainer> data = new ArrayList<>();

    public N_YMD_VSSeriesDataContainer(ArrayList<N_YMD_VSDataContainer> data) {
        this.data = data;
    }

    public ArrayList<N_YMD_VSDataContainer> getData() {
        return data;
    }

    public void add(N_YMD_VSDataContainer val){
        this.data.add(val);
    }

}
