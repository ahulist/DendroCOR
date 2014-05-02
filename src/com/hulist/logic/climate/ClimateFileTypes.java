/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hulist.logic.climate;

/**
 *
 * @author Aleksander Hulist <aleksander.hulist@gmail.com>
 */
public enum ClimateFileTypes {

    ICRU("icru*.dat.txt"),
    AO("_ao.dat.txt");
    //PRN("*.prn");

    String displayName;

    private ClimateFileTypes(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
    
    /**
     * array order is consistent with FileTypes.values() order
     * @return 
     */
    public static String[] getDisplayNames(){
        String[] names = new String[ClimateFileTypes.values().length];
        int counter = 0;
        for( ClimateFileTypes type : ClimateFileTypes.values() ) {
            names[counter] = type.getDisplayName();
            counter++;
        }
        return names;
    }

}
