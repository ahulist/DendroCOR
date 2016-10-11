/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hulist.util;

import com.hulist.gui2.GUIMain;
import java.io.IOException;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.scene.control.TextArea;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Aleksander Hulist <aleksander.hulist@gmail.com>
 */
public class TextAreaToMonths {

    private final TextArea area;
    private final Logger log = LoggerFactory.getLogger(TextAreaToMonths.class);

    public TextAreaToMonths(TextArea a) {
        this.area = a;
    }

    public ArrayList<MonthsPair> getList() {
        ArrayList<MonthsPair> list = new ArrayList<>();
        String[] lines = area.getText().split("\\n");
        for( String line : lines ) {
            String[] elems = line.trim().split("\\s");
            try {
//                assert elems.length == 2 || elems.length == 3;
                if (!(elems.length == 2 || elems.length == 3)) {
                    throw new IOException();
                }
                int start = Integer.parseInt(elems[0]);
                int end = Integer.parseInt(elems[1]);
                int yearsShift = 0;
//                assert start <= end;
//                assert start >= 1 && start <= 12;
//                assert end >= 1 && end <= 12;
                if (!(start <= end)) {
                    throw new IOException();
                }
                if (!(start >= 1 && start <= 12)) {
                    throw new IOException();
                }
                if (!(end >= 1 && end  <= 12)) {
                    throw new IOException();
                }
                if( elems.length == 3 ){
                    yearsShift = Integer.parseInt(elems[2]);
                    assert yearsShift >= 0;
                }
                MonthsPair pair = new MonthsPair(Months.getMonth(start), Months.getMonth(end), yearsShift);
                list.add(pair);
            } catch( IOException | NumberFormatException | AssertionError e ) {
                if( !line.equals("")){
                    ResourceBundle bundle = ResourceBundle.getBundle(GUIMain.BUNDLE, GUIMain.getCurrLocale());
                    log.warn(String.format(bundle.getString("ZAKRES MIESIĘCY %S NIE JEST POPRAWNY."), line));
                    log.debug(Misc.stackTraceToString(e));
                }
            }
        }

        return list;
    }

}
