/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.hulist.util;

import java.util.prefs.Preferences;

/**
 *
 * @author Aleksander Hulist <aleksander.hulist@gmail.com>
 */
public class UserPreferences {
    protected static UserPreferences instance = null;
    private final Preferences prefs;
    
    protected UserPreferences(){
        prefs = Preferences.userNodeForPackage(this.getClass());
    }
    
    public static UserPreferences getInstance(){
        if( instance==null ){
            instance = new UserPreferences();
        }
        return instance;
    }
    
    public Preferences getPrefs(){
        return prefs;
    }
}
