/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hulist.logic.chronology.tabs;

/**
 *
 * @author Aleksander Hulist <aleksander.hulist@gmail.com>
 */
public class TabsLineContainer {

    private int year;
    private int num;
    private double seg;
    private double age;
    private double raw;
    private double std;
    private double res;
    private double ars;

    public TabsLineContainer() {
    }

    public TabsLineContainer(int year, int num, double seg, double age, double raw, double std, double res, double ars) {
        this.year = year;
        this.num = num;
        this.seg = seg;
        this.age = age;
        this.raw = raw;
        this.std = std;
        this.res = res;
        this.ars = ars;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public double getSeg() {
        return seg;
    }

    public void setSeg(double seg) {
        this.seg = seg;
    }

    public double getAge() {
        return age;
    }

    public void setAge(double age) {
        this.age = age;
    }

    public double getRaw() {
        return raw;
    }

    public void setRaw(double raw) {
        this.raw = raw;
    }

    public double getStd() {
        return std;
    }

    public void setStd(double std) {
        this.std = std;
    }

    public double getRes() {
        return res;
    }

    public void setRes(double res) {
        this.res = res;
    }

    public double getArs() {
        return ars;
    }

    public void setArs(double ars) {
        this.ars = ars;
    }
}
