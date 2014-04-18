/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.hulist.logic;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Aleksander Hulist <aleksander.hulist@gmail.com>
 */
public abstract class BaseImporter {
    
    protected int startYear = Integer.MIN_VALUE, endYear = Integer.MAX_VALUE;     // both inclusive
    protected boolean allYears = true;
    protected final Logger log;
    
    /**
     * if isAllYears == true, then startYear and endYear values do not matter
     * @param isAllYears
     * @param startYear
     * @param endYear 
     */
    public BaseImporter(boolean isAllYears, int startYear, int endYear) {
        if( isAllYears ){
            setAllYearsTrue();
        }else{
            selectRange(startYear, endYear);
        }
        
        this.log = Logger.getLogger(this.getClass().getCanonicalName());
        log.setLevel(Level.ALL);
    }
    
    public final void setAllYearsTrue() {
        allYears = true;
    }

    public final void selectRange(int start, int end) {
        allYears = false;
        startYear = start;
        endYear = end;
    }
}
