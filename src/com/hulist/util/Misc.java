/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hulist.util;

import com.hulist.gui2.GUIMain;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Stream;

/**
 *
 * @author Aleksander Hulist <aleksander.hulist@gmail.com>
 */
public class Misc {

    public static String stackTraceToString(Throwable e) {
        StringWriter errors = new StringWriter();
        e.printStackTrace(new PrintWriter(errors));
        return errors.toString();
    }

    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
        Map<K, V> result = new LinkedHashMap<>();
        Stream<Map.Entry<K, V>> st = map.entrySet().stream();
        st.sorted(Map.Entry.comparingByValue()).forEachOrdered(e -> result.put(e.getKey(), e.getValue()));
        return result;
    }
    
    public static String getInternationalized(String key){
        return ResourceBundle.getBundle(GUIMain.BUNDLE, GUIMain.getCurrLocale()).getString(key);
    }
}
