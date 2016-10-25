/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.hulist.logic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Aleksander Hulist <aleksander.hulist@gmail.com>
 */
public abstract class BaseImporter{
    
    protected int startYear = Integer.MIN_VALUE, endYear = Integer.MAX_VALUE;     // both inclusive
    protected boolean allYears = true;
    protected final Logger log = LoggerFactory.getLogger(BaseImporter.class);
    protected RunParams rp;
    /**
     * if isAllYears == true, then startYear and endYear values do not matter
     * @param rp
     */
    public BaseImporter(RunParams rp) {
        this.rp = rp;
        
        if( rp.isAllYears() ){
            setAllYearsTrue();
        }else{
            selectRange(rp.getStartYear(), rp.getEndYear());
        }
        
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
