/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hulist.logic.daily;

import static com.hulist.gui.MainWindow.BUNDLE;
import java.util.ResourceBundle;

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
        return ResourceBundle.getBundle(BUNDLE).getString(this.displayNameId);
    }
    
    /**
     * array order is consistent with FileTypes.values() order
     * @return 
     */
    public static String[] getDisplayNames(){
        String[] names = new String[DailyColumnTypes.values().length];
        int counter = 0;
        for( DailyColumnTypes type : DailyColumnTypes.values() ) {
            names[counter] = type.getDisplayName();
            counter++;
        }
        return names;
    }
}
