/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hulist.logic;

/**
 *
 * @author Aleksander Hulist <aleksander.hulist@gmail.com>
 */
public class Column {

    private double[] data;
    private final String name;
    /*public Column(String name, int dataSize) {
     this.data = new double[dataSize];
     this.name = name;
     }*/

    public Column(String name, double[] data) {
        this.name = name;
        this.data = data;
    }
    /*public String getName(){
     return name;
     }*/

    public double[] getData() {
        return this.data;
    }

    public void setData(double[] data) {
        this.data = data;
    }

    public String getName() {
        return name;
    }

}
