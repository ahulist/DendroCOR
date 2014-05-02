/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hulist.logic.chronology;

/**
 *
 * @author Aleksander Hulist <aleksander.hulist@gmail.com>
 */
public enum ChronologyFileTypes {

    DEKADOWY(java.util.ResourceBundle.getBundle("com/hulist/bundle/Bundle").getString("dekadowy")),
    TABS("*_tabs.txt");

    String displayName;

    private ChronologyFileTypes(String displayName) {
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
        String[] names = new String[ChronologyFileTypes.values().length];
        int counter = 0;
        for( ChronologyFileTypes type : ChronologyFileTypes.values() ) {
            names[counter] = type.getDisplayName();
            counter++;
        }
        return names;
    }

}
