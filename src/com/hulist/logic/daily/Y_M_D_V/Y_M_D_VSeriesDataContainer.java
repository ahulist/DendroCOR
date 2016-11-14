/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hulist.logic.daily.Y_M_D_V;

import java.util.ArrayList;

/**
 *
 * @author Aleksander
 */
public class Y_M_D_VSeriesDataContainer{
    
    private ArrayList<Y_M_D_VDataContainer> data = new ArrayList<>();

    public Y_M_D_VSeriesDataContainer(ArrayList<Y_M_D_VDataContainer> data) {
        this.data = data;
    }

    public ArrayList<Y_M_D_VDataContainer> getData() {
        return data;
    }

    public void add(Y_M_D_VDataContainer val){
        this.data.add(val);
    }

}
