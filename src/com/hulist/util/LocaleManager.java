/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hulist.util;

import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 *
 * @author Aleksander Hulist <aleksander.hulist@gmail.com>
 */
public final class LocaleManager {

    private static final ArrayList<LocaleChangeListener> listeners = new ArrayList<>();

    public static void register(LocaleChangeListener l) {
        if( !listeners.contains(l) ){
            listeners.add(l);
        }
    }

    public static void unregister(LocaleChangeListener l) {
        listeners.remove(l);
    }

    public static void changeDefaultLocale(Locale l) {
        Locale oldLocale = Locale.getDefault();
        if( !l.equals(oldLocale) ){
            Locale.setDefault(l);
            ResourceBundle.clearCache();
            for( LocaleChangeListener localeChangeListener : listeners ) {
                localeChangeListener.onLocaleChange(oldLocale);
            }
        }
    }
}
