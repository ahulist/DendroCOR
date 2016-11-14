package com.hulist.logic.daily;

import com.hulist.util.Misc;

/**
 *
 * @author Aleksander
 * N - Name
 * Y - Year
 * M - Month
 * D - Day
 * V - Single value
 * VS - Many columns with values
 */
public enum DailyFileTypes {
    N_YMD_VS("N_YMD_VS"), Y_M_D_V("Y_M_D_V");
    
    String displayNameId;
    
    private DailyFileTypes(String displayNameId) {
        this.displayNameId = displayNameId;
    }
    
    public String getDisplayName() {
        return Misc.getInternationalized(this.displayNameId);
    }
    
    @Override
    public String toString() {
        return getDisplayName();
    }
    
    public static String[] getDisplayNames(){
        String[] names = new String[DailyFileTypes.values().length];
        int counter = 0;
        for( DailyFileTypes type : DailyFileTypes.values() ) {
            names[counter] = type.getDisplayName();
            counter++;
        }
        return names;
    }
}
