/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hulist.logic.daily;

import com.hulist.util.Misc;
import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author Aleksander
 */
public enum DailyColumnTypes {
    PREC("Precipitation"), TEMP("Temperature");
    
    String displayNameId;

    private DailyColumnTypes(String displayNameId) {
        this.displayNameId = displayNameId;
    }

    public String getDisplayName() {
        return Misc.getInternationalized(this.name());
    }
    
    /**
     * array order is consistent with FileTypes.values() order
     * @return 
     */
    public static ArrayList<String> getDisplayNames(){
        String[] names = new String[DailyColumnTypes.values().length];
        int counter = 0;
        for( DailyColumnTypes type : DailyColumnTypes.values() ) {
            names[counter] = type.getDisplayName();
            counter++;
        }
        return new ArrayList<>(Arrays.asList(names));
    }

    @Override
    public String toString() {
        return getDisplayName();
    }
    
    public static DailyColumnTypes getDailyFromDisplayName(String name){
        for (DailyColumnTypes e : DailyColumnTypes.values()) {
            if (e.getDisplayName().equals(name)) {
                return e;
            }
        }
        return null;
    }
}
