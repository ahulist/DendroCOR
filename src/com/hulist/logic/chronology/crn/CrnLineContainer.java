/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hulist.logic.chronology.crn;

/**
 *
 * @author Aleksander Hulist <aleksander.hulist@gmail.com>
 */
public class CrnLineContainer {

    protected String ID;
    protected int year;
    protected int howManyYearsInLine = 10;
    protected double[][] data = new double[10][2];    // data, num
    protected CrnColumnTypes type;  // raw, std, res, ...
    
    public CrnLineContainer() {
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public double[][] getData() {
        return data;
    }

    public void setData(double[][] data) {
        this.data = data;
    }

    public CrnColumnTypes getType() {
        return type;
    }

    public void setType(String type) {
        setType(CrnColumnTypes.fromString(type));
    }
    
    public void setType(CrnColumnTypes type){
        this.type = type;
    }
    
    public int getHowManyYearsInLine() {
        return howManyYearsInLine;
    }

    public void setHowManyYearsInLine(int howManyYearsInLine) {
        this.howManyYearsInLine = howManyYearsInLine;
    }
}
