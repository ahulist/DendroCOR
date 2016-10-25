/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hulist.logic.daily.type1;

import java.util.ArrayList;

/**
 *
 * @author Aleksander
 */
public class Type1SeriesDataContainer{
    
    private ArrayList<Type1DataContainer> data = new ArrayList<>();

    public Type1SeriesDataContainer(ArrayList<Type1DataContainer> data) {
        this.data = data;
    }

    public ArrayList<Type1DataContainer> getData() {
        return data;
    }

    public void add(Type1DataContainer val){
        this.data.add(val);
    }

}
