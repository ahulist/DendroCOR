/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.hulist.logic;

import java.io.File;

/**
 *
 * @author Aleksander Hulist <aleksander.hulist@gmail.com>
 */
public abstract class FileDataContainer{

    private File sourceFile;
    private int yearMin = Integer.MAX_VALUE, yearMax = Integer.MIN_VALUE;

    public FileDataContainer(File sourceFile) {
        this.sourceFile = sourceFile;
    }
    
    public int getYearMin() {
        return yearMin;
    }

    public int getYearMax() {
        return yearMax;
    }

    protected final void updateMinMax(int year) {
        if( year < yearMin ){
            yearMin = year;
        }
        if( year > yearMax ){
            yearMax = year;
        }
    }

    public File getSourceFile() {
        return sourceFile;
    }
    
    public abstract boolean isEmpty();
}
