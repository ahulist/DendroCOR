package com.hulist.logic.chronology;

import com.hulist.util.Misc;

/**
 *
 * @author Aleksander Hulist <aleksander.hulist@gmail.com>
 */
public enum ChronologyFileTypes {

    DEKADOWY("ID Tucson (*.rwl)"/*java.util.Misc.getInternationalized("dekadowy")*/),
    TABS("ID Arstan (*_tabs.txt)"),
    RCS("Arstan RCS"),
    TABS_MULTICOL("ID Arstan multicol");

    String displayNameId;

    private ChronologyFileTypes(String displayNameId) {
        this.displayNameId = displayNameId;
    }

    public String getDisplayName() {
        return Misc.getInternationalized(this.displayNameId);
    }
    
    @Override
    public String toString() {
        return getDisplayName();
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
