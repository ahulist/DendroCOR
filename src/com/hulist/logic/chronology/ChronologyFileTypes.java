/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hulist.logic.chronology;

import static com.hulist.gui.MainWindow.BUNDLE;
import java.util.ResourceBundle;

/**
 *
 * @author Aleksander Hulist <aleksander.hulist@gmail.com>
 */
public enum ChronologyFileTypes {

    DEKADOWY("ID Tucson (*.rwl)"/*java.util.ResourceBundle.getBundle(MainWindow.BUNDLE).getString("dekadowy")*/),
    TABS("ID Arstan (*_tabs.txt)"),
    TABS_MULTICOL("ID Arstan multicol");

    String displayNameId;

    private ChronologyFileTypes(String displayNameId) {
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
        String[] names = new String[ChronologyFileTypes.values().length];
        int counter = 0;
        for( ChronologyFileTypes type : ChronologyFileTypes.values() ) {
            names[counter] = type.getDisplayName();
            counter++;
        }
        return names;
    }

}
