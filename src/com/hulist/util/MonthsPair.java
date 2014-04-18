/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hulist.util;

import java.util.Objects;

/**
 *
 * @author Aleksander Hulist <aleksander.hulist@gmail.com>
 */
public class MonthsPair {

    public final Months start, end;
    public final int yearsShift;

    public MonthsPair(Months start, Months end) {
        this(start, end, 0);
    }

    public MonthsPair(Months start, Months end, int yearsShift) {
        this.start = start;
        this.end = end;
        this.yearsShift = yearsShift;
    }

    @Override
    public String toString() {
        String ys = "";
        if( yearsShift > 0 ){
            ys = " ("+yearsShift+")";
        }
        return start + "-" + end + ys;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 37 * hash + Objects.hashCode(this.start);
        hash = 37 * hash + Objects.hashCode(this.end);
        hash = 37 * hash + this.yearsShift;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if( obj == null ){
            return false;
        }
        if( getClass() != obj.getClass() ){
            return false;
        }
        final MonthsPair other = (MonthsPair) obj;
        if( this.start != other.start ){
            return false;
        }
        if( this.end != other.end ){
            return false;
        }
        if( this.yearsShift != other.yearsShift ){
            return false;
        }
        return true;
    }

    
}
